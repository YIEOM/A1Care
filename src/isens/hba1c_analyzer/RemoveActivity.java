package isens.hba1c_analyzer;

import java.lang.annotation.Target;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.View.FunctionalTestActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class RemoveActivity extends Activity {

	public SerialPort mSerialPort;
	public TimerDisplay mTimerDisplay;
	
	private Activity activity;
	private Context context;
	
	public AnimationDrawable removeAni;
	public ImageView removeImage;
	
	public static int PatientDataCnt,
					  ControlDataCnt;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.remove);

		mSerialPort = new SerialPort();
		
		RemoveInit();
	}
	
	public void RemoveInit() {

		this.activity = this;
		this.context = this;
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.removelayout);
		
		UserAction UserActionObj = new UserAction();
		UserActionObj.start();
	}

	public class UserAction extends Thread {
		
		public void run() {
			
			int whichIntent;
			
			User1stAction();
			
			GpioPort.DoorActState = true;
			GpioPort.CartridgeActState = true;
	
			SerialPort.Sleep(1500);
			
			while(ActionActivity.CartridgeCheckFlag != 0) SerialPort.Sleep(100);
			
			while((ActionActivity.DoorCheckFlag != 1) | (ActionActivity.CartridgeCheckFlag != 0)) SerialPort.Sleep(100);
			
			GpioPort.DoorActState = false;
			GpioPort.CartridgeActState = false;
			
			Intent itn = getIntent();
			whichIntent = itn.getIntExtra("WhichIntent", 0);
			
			if(whichIntent != ResultActivity.COVER_ACTION_ESC) {
					
				if(Barcode.Type.equals("W") || Barcode.Type.equals("X") || Barcode.Type.equals("Y") || Barcode.Type.equals("Z")) ControlDataCnt = itn.getIntExtra("DataCnt", 0);
				else PatientDataCnt = itn.getIntExtra("DataCnt", 0);
					
				DataCntSave();			
			}
						
			removeAni.stop();
			
			switch(whichIntent) {
			
			case ResultActivity.ACTION_ACTIVITY	:
				WhichIntent(activity, context, TargetIntent.Blank);
				break;
			
			case ResultActivity.HOME_ACTIVITY		:	
				changeActivity(activity, context);
				break;
				
			case ResultActivity.COVER_ACTION_ESC	:
				changeActivity(activity, context);
				break;
				
			case ResultActivity.SCAN_ACTIVITY	:
				WhichIntent(activity, context, TargetIntent.ScanTemp);
				break;
				
			default	:
				break;
			}
		}
	}
	
	public void User1stAction() { // Cartridge remove animation start
		
		removeImage = (ImageView)findViewById(R.id.removeAct1);
		removeAni = (AnimationDrawable)removeImage.getBackground();
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	removeAni.start();
		            }
		        });
		    }
		}).start();	
	}
	
	public void DataCntSave() { // Saving data number
		
		SharedPreferences DcntPref = getSharedPreferences("Data Counter", MODE_PRIVATE);
		SharedPreferences.Editor edit = DcntPref.edit();
		
		edit.putInt("PatientDataCnt", PatientDataCnt);
		edit.putInt("ControlDataCnt", ControlDataCnt);
		
		edit.commit();
	}
	
	private void changeActivity(Activity activity, Context context) {
		
		if(HomeActivity.MEASURE_MODE == HomeActivity.A1C) {
			
			WhichIntent(activity, context, TargetIntent.Home);
		
		} else {
			
			WhichIntent(activity, context, TargetIntent.FunctionalTest);
		}
	}
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home		:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			nextIntent.putExtra("System Check State", state);
			break;
			
		case FunctionalTest		:				
			nextIntent = new Intent(getApplicationContext(), FunctionalTestActivity.class);
			nextIntent.putExtra("System Check State", state);
			break;
			
		case Blank		:				
			nextIntent = new Intent(getApplicationContext(), BlankActivity.class);
			break;
			
		default		:	
			break;			
		}
		
		startActivity(nextIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
