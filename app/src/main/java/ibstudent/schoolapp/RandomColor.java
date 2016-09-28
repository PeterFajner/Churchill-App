package ibstudent.schoolapp;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by peter on 2016-02-19.
 * Used for debugging layout issues.
 */
public class RandomColor
{
    static int random()
    {
        Random r = new Random();
        return Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }
}
