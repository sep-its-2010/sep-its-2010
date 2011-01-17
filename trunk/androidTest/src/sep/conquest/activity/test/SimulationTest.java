package sep.conquest.activity.test;

import sep.conquest.activity.MapSurfaceView;
import sep.conquest.activity.Simulation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class SimulationTest extends
		ActivityInstrumentationTestCase2<Simulation> {
	
	public static final int ADAPTER_COUNT = 1;
	public static final int INITIAL_POSITION = 6;
	public static final int TEST_POSITION = 5;
	public static final String EXTRA_FILE_PATH = "Path";
	public static final String SELECTED_MAP = "bone.sim";
	public static final int REQUEST_SIM_CONFIG = 10;

	private Simulation mActivity;
	private Spinner mSpinner;
	private SpinnerAdapter mData;
	private String mSelection;
	private int mPos;
	private MapSurfaceView msv;

	public SimulationTest() {
		super("sep.conquest.activity", Simulation.class);
	} // end of SimulationTest constructor definition

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		
		final Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName("sep.conquest.activity",
            "sep.conquest.activity.Import");
        intent.putExtra(EXTRA_FILE_PATH, SELECTED_MAP);
		setActivityIntent(intent);
		
		mActivity = getActivity();
		
		
		
		
		msv = (MapSurfaceView) mActivity.findViewById(sep.conquest.R.id.im_preview);
		mSpinner = (Spinner) mActivity.findViewById(sep.conquest.R.id.spNumber);
		mData = mSpinner.getAdapter();

	} // end of setUp() method definition
	
	
	public void testPreConditions() {
	    assertTrue(mSpinner.getOnItemSelectedListener() != null);
	    assertTrue(mData != null);
	    assertEquals(mData.getCount(),ADAPTER_COUNT);
	  } // end of testPreConditions() method definition
	
	
	public void testSpinnerUI() {

	    mActivity.runOnUiThread(
	      new Runnable() {
	        public void run() {
	          mSpinner.requestFocus();
	          mSpinner.setSelection(INITIAL_POSITION);
	        } // end of run() method definition
	      } // end of anonymous Runnable object instantiation
	    ); // end of invocation of runOnUiThread
	    
	    
	    this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	    for (int i = 1; i <= TEST_POSITION; i++) {
	      this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
	    } // end of for loop

	    this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	    
	    mPos = mSpinner.getSelectedItemPosition();
	    mSelection = (String)mSpinner.getItemAtPosition(mPos);
	    TextView resultView =
	      (TextView) mActivity.findViewById(
	        sep.conquest.R.id.filename
	      );

	    String resultText = (String) resultView.getText();

	    assertEquals(resultText,mSelection);

	}
	
}
