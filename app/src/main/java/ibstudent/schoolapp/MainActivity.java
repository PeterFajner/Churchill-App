package ibstudent.schoolapp;

/*
import android.app.Fragment;
//import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
*/

import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "ibstudent.schoolapp";
    public DataFetcher dataFetcher;
    public ViewPager viewPager;
    public TabLayout tabLayout;
    public PageMeta[] pages;

    ArrayList<Refreshable> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("dbg", "starting app...");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.dataFetcher = new DataFetcher(this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        viewPager.setOffscreenPageLimit(50);

        ArrayList<PageMeta> pages = new ArrayList<>();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        InformationFragment infoFrag = new InformationFragment();
        adapter.addFragment(infoFrag, "Info");
        fragments.add(infoFrag);

        EventsFragment eventsFrag = new EventsFragment();
        adapter.addFragment(eventsFrag, "Events");
        fragments.add(eventsFrag);

        SpotlightFragment spotFrag = new SpotlightFragment();
        adapter.addFragment(spotFrag, "Spot");
        fragments.add(spotFrag);

        StaffFragment staffFrag = new StaffFragment();
        adapter.addFragment(staffFrag, "Staff");
        fragments.add(staffFrag);

        //adapter.addFragment(new EventsFragment(), "Events");
        //adapter.addFragment(new SpotlightFragment(), "Spotlight");
        //adapter.addFragment(new StaffFragment(), "Staff");
        viewPager.setAdapter(adapter);
        pages.add(new PageMeta("information", null, infoFrag.layout));
        this.pages = pages.toArray(new PageMeta[0]);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == R.id.action_refresh) {
            Log.d("dbg", "Refreshing");
            /*PageMeta[] pages = new PageMeta[]{
                    new PageMeta("events", null, (LinearLayout)findViewById(R.id.container_events)),
                    new PageMeta("information", null, (LinearLayout)findViewById(R.id.container_information)),
                    new PageMeta("spotlight", null, (LinearLayout)findViewById(R.id.container_spotlight)),
                    new PageMeta("TabbedPanels1", null, (LinearLayout)findViewById(R.id.container_staff))};*/
            //dataFetcher.refresh(pages);
            for (Refreshable fragment : fragments) {
                fragment.refresh();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}