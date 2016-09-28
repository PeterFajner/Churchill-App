package ibstudent.schoolapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Created by peter on 2016-02-15.
 */
public class HtmlToViews extends Activity
{
    Context context;
    LinearLayout layout;
    String frontPageHTML;
    String staffPageHTML;
    PageMeta[] pages;

    public HtmlToViews(Context context, String frontPageHTML, String staffPageHTML, PageMeta[] pages)
    {
        this.context = context;
        this.frontPageHTML = frontPageHTML;
        this.staffPageHTML = staffPageHTML;
        this.pages = pages;
    }

    public void convert()
    {
        for (PageMeta page : pages) {
            if (page.html != null) {
                convertPage(page);
            }
            else {
                Log.e("dbg", "Could not display page "+page.id);
                // TODO: display error to user
            }
        }
    }

    public void convertPage(PageMeta page)
    {
        if (page.html == frontPageHTML) convertFrontSection(page);
        else if (page.html == staffPageHTML) convertStaffSection(page);
    }

    public void convertStaffSection(PageMeta page)
    {

    }

    public void convertFrontSection(PageMeta page)
    {
        String divHtml = isolateId(page.id, page.html);

        ArrayList<Tag> paragraphs = isolateParagraphTags(divHtml);

        for (int i = 0; i < paragraphs.size(); i++) {
            Tag tag = paragraphs.get(i);
            String text = tag.contents;
            switch (tag.tag){
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
            Log.d("version", "Version >= 21");
            tview.setElevation(10);
            //tview.setShadowLayer(1, 0, 0, Color.BLACK);
        }
        //Random rnd = new Random();
        //tview.setBackgroundColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        return tview;
    }

    // isolates a div
    String isolateId(String id, String html)
    {
        // There cannot be any other divs inside this one!
        String a = "<div id=\"";
        String b = id;
        String c = "\">";
        String isolated = html.split(a+b+c)[1]; // cut off stuff before opening tag
        isolated = isolated.split("</div>")[0]; // cut off stuff after div
        Log.d("dbg", isolated);
        return isolated;
    }

    // creates an array containing <h1-4> and <p> elements
    // elements of this type cannot be nested in other elements
    ArrayList<Tag> isolateParagraphTags(String html)
    {
        ArrayList<Tag> data = new ArrayList<>();

        String[] tagArray = {"h1", "h2", "h3", "h4", "p"};
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
                Log.d("tagplus", String.valueOf(temp.charAt(openStart+1)));
                if (String.valueOf(temp.charAt(openStart+1)).equals("/")) {
                    String postTagStart = temp.substring(openStart);
                    int closingBracket = postTagStart.indexOf(">");
                    String tagRemoved = temp.substring(0, openStart) + postTagStart.substring(closingBracket+1);
                    temp = tagRemoved;
                    Log.d("Remaining", temp);
                    continue;
                }
                else {
                    Log.e("unknown tag", temp.substring(openStart));
                    return data;
                }
            }
            int openEnd = openStart + 2 + correctTag.length(); // end of the open tag
            int closeStart = temp.indexOf("</" + correctTag); // start of the close tag
            int closeEnd = closeStart + 3 + correctTag.length(); // end of the close tag
            Tag tag = new Tag(correctTag, temp.substring(openEnd, closeStart));
            data.add(tag);
            temp = temp.substring(closeEnd);
            Log.d("New tag", tag.contents);
            Log.d("Remaining", temp);
        }
        Log.d("paragraph num", ""+data.size());
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
            Log.d("hrefstart", text.substring(href));
            int linkStart = text.substring(href).indexOf("\"") + href + 1;
            Log.d("linkstart", text.substring(linkStart));
            int linkEnd = text.substring(linkStart+1).indexOf("\"") + linkStart + 1;
            Log.d("linkend", text.substring(linkEnd));
            String url = text.substring(linkStart, linkEnd);
            if (url.indexOf("http") < 0 && url.indexOf("@") < 0) {
                url = context.getResources().getString(R.string.front_page_URL) + url;
            }
            Log.d("url", url);
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
            Log.d("Remaining", text);
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
}
