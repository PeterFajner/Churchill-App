package ibstudent.schoolapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by peter on 2016-02-17.
 */
public class PageToViews
{
    Context context;
    LinearLayout layout;
    String url;
    PageMeta page;
    String frontPageURL = null;
    String staffPageURL = null;

    public PageToViews(Context context, PageMeta page)
    {
        this.context = context;
        this.frontPageURL = context.getResources().getString(R.string.front_page_URL);
        this.staffPageURL = context.getResources().getString(R.string.staff_page_URL);
        this.url = page.url;
        this.page = page;
        this.layout = page.layout;
    }

    public void convert()
    {
        // clear previous layout
        page.layout.removeAllViews();

        if (page.html != null && page.url != null) {
            if (page.url == frontPageURL) convertFrontSection();
            else if (page.url == staffPageURL) convertStaffSection();
        }
        else {
            Log.e("dbg", "Could not display page " + page.id);
            // TODO: display error to user
        }
    }

    void convertStaffSection()
    {
        //String divHtml = isolateId(page.id, page.html, true);
        String divHtml = page.html.split("<div class=\"TabbedPanelsContentGroup\">")[1];

        ArrayList<Tag> paragraphs = isolateParagraphTags(divHtml, new String[]{"div"}); // each "paragraph" is a div representing a letter

        Log.d("pars", ""+paragraphs.size());

        ArrayList<String> rawTeacherData = new ArrayList<>();
        ArrayList<TeacherData> teacherData = new ArrayList<>();

        for (Tag paragraph : paragraphs) {
            String text = paragraph.contents; // this is one page (all the teachers with the same letter name)
            //Log.d("contents", text);
            text = narrowToTag("table", text);
            //text = narrowToTag("tbody", text);
            text = narrowToTag("tr", text);
            text = narrowToTag("td", text);
            //text = narrowToTag("table", text);
            //text = narrowToTag("tbody", text);
            //text = narrowToTag("tr", text);
            //Log.d("narrowed", text);

            // create individual teacher data
            ArrayList<String> perTeacher = new ArrayList<String>(Arrays.asList(text.split("</tr>")));
            //perTeacher.remove(0);
            perTeacher.remove(0);
            Log.d("num teachers", ""+perTeacher.size());
            rawTeacherData.addAll(perTeacher);

            /*for (String teacherRawData : perTeacher) {
                ArrayList<String> splitText = new ArrayList<String>(Arrays.asList(teacherRawData.split("nowrap=\"nowrap\"")));
                splitText.remove(0);
                rawTeacherData.addAll(splitText);
            }*/
        }

        for (String data : rawTeacherData) {

            if (data.contains("nbsp") || data.contains("<strong>")) {
                continue;
            }

            /*
            Log.d("raw", data);
            data = data.substring(data.indexOf("<p>")+3);
            String name = data.substring(0, data.indexOf("</p>"));
            name.replace("*", "");
            data = data.substring(data.indexOf("<p>")+3);
            String position = data.substring(0, data.indexOf("</p>"));
            data = data.substring(data.indexOf("<p>") + 3);
            String email = data.substring(0, data.indexOf("</p>"));
            try {
                email = email.substring(email.indexOf(">")+1);
                email = email.substring(0, email.indexOf("<"));
            } catch (StringIndexOutOfBoundsException E) {}
            email = email.toLowerCase();
            if (!email.contains("@")) email += "@cbe.ab.ca";
            email.replace("@", System.getProperty("line.separator") + "@"); // a nice linebreak
            data = data.substring(data.indexOf("<p>")+3);
            String extension = data.substring(0, data.indexOf("</p>"));*/

            Log.d("raw", data);
            Whittler w = new Whittler(data);
            String name = w.whittle();
            name = name.replace("*", "");
            String position = w.whittle();
            String email = w.whittle();
            try {
                email = email.substring(email.indexOf(">")+1);
                email = email.substring(0, email.indexOf("<"));
            } catch (StringIndexOutOfBoundsException E) {}
            email = email.toLowerCase();
            if (!email.contains("@") && !email.contains("on leave")) email += "@cbe.ab.ca";
            String dispEmail = email.replace("@", System.getProperty("line.separator") + "@"); // a nice linebreak
            String extension = w.whittle();

            //TeacherData teacher = new TeacherData(name, position, email, extension);
            //teacherData.add(teacher);

            //Log.d("teacher info", name + position + email + voicemail);

            // create info box and add to layout
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(25, 25, 25, 25);
            final LinearLayout holder = new LinearLayout(context);
            holder.setLayoutParams(params);
            holder.setPadding(25, 25, 25, 25);
            holder.setBackgroundColor(ContextCompat.getColor(context, R.color.p_bg));
            if (Build.VERSION.SDK_INT >= 21) {
                holder.setElevation(10);
            }

            {
                // left side: name
                TextView nameView = new TextView(context);
                nameView.setText(name);
                nameView.setTextSize(20);
                nameView.setTextColor(Color.BLACK);
                nameView.setBackgroundColor(ContextCompat.getColor(context, R.color.p_bg));
                LinearLayout.LayoutParams nparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                nparams.weight = 1;
                nparams.setMargins(25, 25, 25, 25);
                nameView.setLayoutParams(nparams);
                nameView.setTextIsSelectable(true);
                holder.addView(nameView);
            }

            // right side: contact info
            /*
            LinearLayout infoHolder = new LinearLayout(context);
            LinearLayout.LayoutParams infoHolderParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            infoHolderParams.setLayoutDirection(LinearLayout.VERTICAL);
            infoHolderParams.weight = 1;
            infoHolder.setLayoutParams(infoHolderParams);*/


            {
            /*TextView emailView = makeTextView(email, 14, 0, false, false);
            infoHolder.addView(emailView);*/
                final TextView emailView = new TextView(context);
                emailView.setText(dispEmail);
                emailView.setTextSize(20);
                emailView.setTextColor(Color.BLACK);
                emailView.setBackgroundColor(ContextCompat.getColor(context, R.color.p_bg));
                LinearLayout.LayoutParams eparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                eparams.setMargins(25, 25, 25, 25);
                eparams.weight = 1;
                //eparams.setMargins(25, 25, 25, 25);
                //emailView.setPadding(25, 25, 25, 25);
                emailView.setLayoutParams(eparams);
                emailView.setTextIsSelectable(true);
                final Uri mailto = Uri.parse("mailto:"+email);
                emailView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(mailto);
                        emailView.getContext().startActivity(i);
                    }
                });
                holder.addView(emailView);
            }

            /*TextView extensionView = new TextView(context);
            extensionView.setText(extension);
            extensionView.setTextSize(15);
            extensionView.setTextColor(Color.BLACK);
            extensionView.setBackgroundColor(ContextCompat.getColor(context, R.color.p_bg));
            LinearLayout.LayoutParams exparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            exparams.weight = 1;
            emailView.setLayoutParams(exparams);
            infoHolder.addView(extensionView);*/


            /*infoHolder.invalidate();
            holder.addView(infoHolder);*/

            holder.invalidate();
            layout.addView(holder);
            Log.d("staff member added", "" + holder);
        }
        Log.e("staff layout 2", "" + layout);
        layout.invalidate();
    }

    // narrows a chunk of HTML down to the contents of a unique tag
    String narrowToTag(String tag, String html)
    {
        html = html.substring(html.indexOf(tag)); // removes first section of opening <tag
        html = html.substring(html.indexOf(">")+1); // removes second section of opening ...>
        html = html.substring(0, html.lastIndexOf("</" + tag + ">")); // removes closing tag
        return html;
    }

    void convertFrontSection()
    {
        String divHtml = isolateId(page.id, page.html, false);

        ArrayList<Tag> paragraphs = isolateParagraphTags(divHtml, new String[]{"h1", "h2", "h3", "h4", "p"});

        for (int i = 0; i < paragraphs.size(); i++) {
            Tag tag = paragraphs.get(i);
            String text = tag.contents;
            switch (tag.tag) {
                case "h1":
                case "h2":
                case "h3": // The headers are ugly on the website. Might be easier to roll my own.
                    //layout.addView(processSubTags(tag, 25, ContextCompat.getColor(context, R.color.p_bg), true));
                    break;
                case "h4":
                    //layout.addView(processSubTags(tag, 23, ContextCompat.getColor(context, R.color.p_bg), true));
                    break;
                case "p":
                    page.layout.addView(processSubTags(tag, 19, ContextCompat.getColor(context, R.color.p_bg), false));
                    break;
            }
        }
        page.layout.invalidate();
        Log.d("dbg", "Updated layout");
    }

    TextView makeTextView(String text, float size, int background, boolean isHeader, boolean clickable)
    {
        TextView tview = new TextView(context);
        tview.setText(text);
        tview.setTextSize(size);
        tview.setTextColor(Color.BLACK);
        tview.setBackgroundColor(ContextCompat.getColor(context, R.color.p_bg));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 25, 25, 25);
        tview.setPadding(25, 25, 25, 25);
        if (clickable) {
            tview.setTextColor(ContextCompat.getColor(context, R.color.p_link));
        }
        if (isHeader) {
            tview.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            tview.setTextColor(Color.WHITE);
            params.setMargins(0, 0, 0, 0);
        }
        /*if (isHeader && clickable) {
            tview.setTextColor(ContextCompat.getColor(context, R.color.clickable_dark));
        }*/
        tview.setLayoutParams(params);
        if (Build.VERSION.SDK_INT >= 21) {
            //Log.d("version", "Version >= 21");
            tview.setElevation(10);
            //tview.setShadowLayer(1, 0, 0, Color.BLACK);
        }
        //Random rnd = new Random();
        //tview.setBackgroundColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        return tview;
    }

    // isolates a div
    String isolateId(String id, String html, boolean isClass)
    {
        // There cannot be any other divs inside this one!
        String a = "<div id=\"";
        if (isClass) {
            a = "<div class=\"";
        }
        String b = id;
        String c = "\">";
        String isolated = html.split(a+b+c)[1]; // cut off stuff before opening tag
        isolated = isolated.split("</div>")[0]; // cut off stuff after div
        Log.d("isolated", isolated);
        return isolated;
    }

    // creates an array containing <h1-4> and <p> elements
    // elements of this type cannot be nested in other elements
    ArrayList<Tag> isolateParagraphTags(String html, String[] tagArray)
    {
        ArrayList<Tag> data = new ArrayList<>();

        String temp = html;

        // remove iframes
        temp = removeTag("iframe", temp);

        // process tags
        while (temp.length() > 0) {
            int openStart = temp.indexOf("<"); // found a tag start

            if (openStart < 0) {
                return data;
            }

            String correctTag = null; // the tag that was found
            // determine which tag was found
            for (String tag : tagArray) {
                if (temp.substring(openStart + 1, openStart + 1 + tag.length()).equals(tag)) { // check if the tag found is the same as the tag in the list
                    correctTag = tag;
                    break;
                }
            }
            if (correctTag == null) {
                // check if it's a closing tag without a matching opening tag
                // this is the case in one of the front page columns: there's a </p> hanging around without a <p>
                //Log.d("tagplus", String.valueOf(temp.charAt(openStart+1)));
                if (String.valueOf(temp.charAt(openStart+1)).equals("/")) {
                    String postTagStart = temp.substring(openStart);
                    int closingBracket = postTagStart.indexOf(">");
                    String tagRemoved = temp.substring(0, openStart) + postTagStart.substring(closingBracket+1);
                    temp = tagRemoved;
                    //Log.d("Remaining", temp);
                    continue;
                }
                else {
                    //Log.e("unknown tag", temp.substring(openStart));
                    return data;
                }
            }
            int openEnd = openStart + 2 + correctTag.length(); // end of the open tag
            int closeStart = temp.indexOf("</" + correctTag); // start of the close tag
            int closeEnd = closeStart + 3 + correctTag.length(); // end of the close tag
            Tag tag = new Tag(correctTag, temp.substring(openEnd, closeStart));
            data.add(tag);
            temp = temp.substring(closeEnd);
            //Log.d("New tag", tag.contents);
            //Log.d("Remaining", temp);
        }
        //Log.d("paragraph num", ""+data.size());
        return data;
    }

    String removeTag(String tag, String text)
    {
        String cleanText = "";
        while (text.length() > 0) {
            int openStart = text.indexOf("<"+tag);
            if (openStart > 0) {
                cleanText += text.substring(0, openStart);
                text = text.substring(openStart);
            }
            else if (openStart < 0) {
                cleanText += text;
                break;
            }
            int closeStart = text.indexOf("</"+tag+">");
            text = text.substring(closeStart+3+tag.length());
        }
        return cleanText;
    }

    // process <a>, <br>, <span> tags
    TextView processSubTags(Tag parentTag, float textSize, int background, boolean isHeader)
    {
        String text = parentTag.contents;
        Boolean clickable = false;
        final UriHolder uri = new UriHolder();
        if (text.indexOf("<a") > 0) {
            clickable = true;
            int href = text.indexOf("href");
            //Log.d("hrefstart", text.substring(href));
            int linkStart = text.substring(href).indexOf("\"") + href + 1;
            //Log.d("linkstart", text.substring(linkStart));
            int linkEnd = text.substring(linkStart+1).indexOf("\"") + linkStart + 1;
            //Log.d("linkend", text.substring(linkEnd));
            String url = text.substring(linkStart, linkEnd);
            if (url.indexOf("http") < 0 && url.indexOf("@") < 0) {
                url = context.getResources().getString(R.string.front_page_URL) + url;
            }
            //Log.d("url", url);
            uri.uri = Uri.parse(url);
        }

        // remove <br> tags
        //text = concatenateStringArray(text.split("<br>"));
        //text = concatenateStringArray(text.split("<br />"));
        text = concatenateStringArray(text.split("  "));
        text = text.replace("<br>", "\n");
        text = text.replace("</ br>", "\n");
        text = text.replace("&amp;", "&");
        text = text.replace("&nbsp;", " ");

        // process <a> and <span> tags
        String cleanText = "";
        while (text.length() > 0) {
            int openStart = text.indexOf("<");
            if (openStart > 0) {
                cleanText += text.substring(0, openStart);
                text = text.substring(openStart);
            }
            else if (openStart < 0) {
                cleanText += text;
                break;
            }
            int openEnd = text.indexOf(">");
            text = text.substring(openEnd+1);
            //Log.d("Remaining", text);
        }

        final TextView view = makeTextView(cleanText, textSize, background, isHeader, clickable);
        if (clickable) {
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(uri.uri);
                    view.getContext().startActivity(i);
                }
            });
        }
        view.setTextIsSelectable(true);

        return view;
    }

    // turns a String[] into a String
    String concatenateStringArray(String[] input) {
        String output = "";
        for (String str : input) {
            output += str;
        }
        return output;
    }
}

// a tag and its contents
class Tag
{
    public String tag;
    public String contents;
    public Tag(String tag, String contents)
    {
        this.tag = tag;
        this.contents = contents;
    }
}

class UriHolder
{
    public Uri uri;
    public UriHolder(){}
}

class TeacherData
{
    public String name;
    public String position;
    public String email;
    public String extension;

    public TeacherData(String name, String position, String email, String extension)
    {
        this.name = name;
        this.position = position;
        email = email.toLowerCase();
        if (!email.contains("@")) email += "@cbe.ab.ca";
        email.replace("@", "\n@"); // a nice linebreak
        this.email = email;
        this.extension = extension;
    }
}

class Whittler
{
    String data;

    public Whittler(String data)
    {
        this.data = data;
    }

    String whittle()
    {
        try {
            if (data.contains("<p>")) {
                data = data.substring(data.indexOf("<p>")+3);
                return data.substring(0, data.indexOf("</p>"));
            }
            else {
                data = data.substring(data.indexOf("\">")+2);
                return data.substring(0, data.indexOf("<"));
            }
        }
        catch (StringIndexOutOfBoundsException E) {
            return "ERROR";
        }
    }
}