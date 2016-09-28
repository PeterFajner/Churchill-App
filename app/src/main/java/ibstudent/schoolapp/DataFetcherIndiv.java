package ibstudent.schoolapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by peter on 2016-02-17.
 */
public class DataFetcherIndiv
{
    Context context = null;
    String url = null;
    PageMeta page = null;
    AtomicBoolean finishedDownloading = null;

    public DataFetcherIndiv(Context context, AtomicBoolean finishedDownloading)
    {
        this.context = context;
        this.finishedDownloading = finishedDownloading;
    }

    boolean refresh(PageMeta page)
    {
        finishedDownloading.set(false);
        this.url = page.url;
        this.page = page;
        Log.d("dbg", "URL: " + url);
        new HtmlRetriever().execute();
        return true;
    }

    private class HtmlRetriever extends AsyncTask<Void, Void, Void>
    {
        String html;

        protected Void doInBackground(Void... v)
        {
            try {
                URL urlObj = new URL(url);
                URLConnection cnx = urlObj.openConnection();
                cnx.setConnectTimeout(15000);
                cnx.setReadTimeout(15000);
                cnx.connect();

                InputStream in = cnx.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder htmlBuilder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    htmlBuilder.append(line);
                }
                in.close();
                html = htmlBuilder.toString();
            }
            catch (MalformedURLException E) {
                Log.e("err", "Malformed URL: " + url);
                E.printStackTrace();
                return null;
            }
            catch (IOException E) {
                Log.e("err", "IOException when fetching page!");
                E.printStackTrace();
                return null;
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            page.html = this.html;
            finishedDownloading.set(true);
        }
    }

}
