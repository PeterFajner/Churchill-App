package ibstudent.schoolapp;

import android.widget.LinearLayout;

/**
 * Created by peter on 2016-02-17.
 */
public class PageMeta
{
    public String id;
    public String html;
    public LinearLayout layout;
    public String url;

    public PageMeta(String id, String url, LinearLayout layout)
    {
        this.id = id;
        this.url = url;
        this.layout = layout;
    }
}
