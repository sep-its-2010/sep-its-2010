package sep.conquest.activity;

import sep.conquest.R;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class Map extends Activity{
	
	public static int x;
	public static int y;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        x = metrics.widthPixels;
        y = metrics.heightPixels;
        
        setContentView(R.layout.map_main); 
    }

}
