package ibstudent.schoolapp;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by peter on 2016-02-15.
 * Fetches data from the school website
 */
public class DataFetcher
{
    Context context = null;
    String frontPageURL = null;
    String staffPageURL = null;
    PageMeta[] pages;

    public DataFetcher(Context context)
    {
        this.context = context;
    }

    boolean refresh(PageMeta[] pages)
    {
        this.frontPageURL = context.getResources().getString(R.string.front_page_URL);
        this.staffPageURL = context.getResources().getString(R.string.staff_page_URL);
        this.pages = pages;
        Log.d("dbg", "URL: "+ frontPageURL);
        Log.d("dbg", "URL: "+ staffPageURL);
        new FrontPageHTMLRetriever().execute();
        return true;
    }

    private class FrontPageHTMLRetriever extends AsyncTask<Void, Void, Void>
    {
        String frontPageHTML;
        String staffPageHTML;

        protected Void doInBackground(Void... v)
        {
            try {
                URL frontUrlObj = new URL(frontPageURL);
                URLConnection cnx = frontUrlObj.openConnection();
                cnx.setConnectTimeout(15000);
                cnx.setReadTimeout(15000);
                cnx.connect();

                InputStream in = cnx.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder html = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    html.append(line);
                }
                in.close();
                frontPageHTML = html.toString();
            }
            catch (MalformedURLException E) {
                Log.e("err", "Malformed URL: " + frontPageURL);
                E.printStackTrace();
                return null;
            }
            catch (IOException E) {
                Log.e("err", "IOException when fetching front page!");
                E.printStackTrace();
                return null;
            }

            try {
                URL staffUrlObj = new URL(staffPageURL);
                URLConnection cnx = staffUrlObj.openConnection();
                cnx.setConnectTimeout(15000);
                cnx.setReadTimeout(15000);
                cnx.connect();

                InputStream in = cnx.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder html = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    html.append(line);
                }
                in.close();
                staffPageHTML = html.toString();
            }
            catch (MalformedURLException E) {
                Log.e("err", "Malformed URL: " + staffPageURL);
                E.printStackTrace();
                return null;
            }
            catch (IOException E) {
                Log.e("err", "IOException when fetching staff page!");
                E.printStackTrace();
                return null;
            }

            return null;
        }

        protected void onPostExecute(Void v)
        {
            for (PageMeta page : pages) {
                if (page.id.contains("information") || page.id.contains("events") || page.id.contains("spotlight")) {
                    page.html = frontPageHTML;
                }
                else if (page.id.contains("TabbedPanels1")) {
                    page.html = staffPageHTML;
                } else {
                    Log.e("err", "Page ID not recognized, don't know what URL to assign");
                }
            }
            HtmlToViews converter = new HtmlToViews(context, frontPageHTML, staffPageHTML, pages);
            converter.convert();
        }
    }
}