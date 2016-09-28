package ibstudent.schoolapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Peter on 2016-02-17.
 */
// http://www.androidhive.info/2015/09/android-material-design-working-with-tabs/
public class EventsFragment extends Fragment implements Refreshable
{
    public LinearLayout layout;
    DataFetcherIndiv fetcher;
    PageMeta page;
    boolean hasRefreshedOnce = false;
    PageToViews converter;
    AtomicBoolean finishedDownloading;

    public EventsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.finishedDownloading = new AtomicBoolean();
        this.fetcher = new DataFetcherIndiv(getContext(), finishedDownloading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate layout
        View inflated = inflater.inflate(R.layout.fragment_page, container, false);
        layout = (LinearLayout) ((ViewGroup)((ViewGroup) inflated).getChildAt(0)).getChildAt(0);
        this.page = new PageMeta("events", getString(R.string.front_page_URL), layout);
        this.converter = new PageToViews(getContext(), page);
        if (!hasRefreshedOnce) {
            refresh();
            hasRefreshedOnce = true;
        }
        else {
            converter.convert();
        }
        return inflated;
    }

    @Override
    public void refresh()
    {
        finishedDownloading.set(false);
        fetcher.refresh(page);
        new Drawer().execute();
    }

    private class Drawer extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... v)
        {
            while (!finishedDownloading.get()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException E) {}
            } return null;

        }

        protected void onPostExecute(Void v)
        {
            converter.convert();
        }
    }
}
