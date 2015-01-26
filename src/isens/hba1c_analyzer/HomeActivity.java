package isens.hba1c_analyzer;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;

/*
 * 
 * Object : Development SW
 * 
 */

public class HomeActivity extends Activity {
	
	final static byte NORMAL = 0,
					  DEVEL = 1, // Development
					  DEMO = 2, // Sales department
					  STABLE = 3, // Motion that TUVSUD require ; Operation for 2 hours
					  ANALYZER_SW = NORMAL;

	final static byte PP = 1,
			          ES = 2,
			          ANALYZER_DEVICE = PP;			  
	
	public DatabaseHander mDatabaseHander;
	public OperatorController mOperatorController;
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;
	
	public SoundPool mPool;
	
	public Button runBtn,
				  settingBtn,
				  recordBtn;
	
	public enum TargetIntent {Home, HbA1c, NA, Action, Run, Blank, Record, Result, ResultError, Remove, Image, Date, Setting, SystemSetting, DataSetting, OperatorSetting, Time, Display, HIS, HISSetting, Export, Maintenance, FileSave, ControlFileLoad, PatientFileLoad, NextFile, PreFile, Adjustment, Sound, Calibration, Language, Correlation, Temperature, Lamp, Convert, Absorbance}
	
	public static boolean LoginFlag = true,
						  CheckFlag;
	
	public static byte NumofStable = 0;
	
	public TextView idText,
					demoVerText;
	
	public boolean btnState = false;
	
	public int mWin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.home);
		
		mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mWin = mPool.load(this, R.raw.win, 1);
		
		HomeInit();
		
		/*Test Activity activation*/
		runBtn = (Button)findViewById(R.id.runbtn);
		runBtn.setOnClickListener(new View.OnClickListener() { 
		
			public void onClick(View v) {
			
				if(!btnState) {
		
					btnState = true;
					
					runBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Blank);
				}
			}
		});
		
		/*Setting Activity activation*/
		settingBtn = (Button)findViewById(R.id.settingbtn);
		settingBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(ANALYZER_SW != DEMO) {
				
					if(!btnState) {
						
						btnState = true;
	
						settingBtn.setEnabled(false);
						
						WhichIntent(TargetIntent.Setting);				
					}
				}
			}
		});
		
		/*Memory Activity activation*/
		recordBtn = (Button)findViewById(R.id.recordbtn);
		recordBtn.setOnClickListener(new View.OnClickListener() { 
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					recordBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Record);
				}
			}
		});
	}
	
	public void HomeInit() {
		
		int state;
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.homelayout);
		
		mPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
		      public void onLoadComplete(SoundPool mPool, int sampleId, int status) {

		  		mPool.play(mWin, 1, 1, 0, 0, 1); // playing sound
		      }
		});
			
		Intent itn = getIntent();
		state = itn.getIntExtra("System Check State", 0);
		
		if(state != 0) {
			
			mErrorPopup = new ErrorPopup(this, this, R.id.homelayout);
			mErrorPopup.ErrorBtnDisplay(state);
		
		} else {
			
			Login(this, this, R.id.homelayout);	
			
//			if(!LoginFlag) OperatorDisplay(this, this);
		}
		
		DisplayDemo();
	}
	
	public void Login(Activity activity, Context context, int layoutid) {
		
		Log.w("Login", "LoginFlag : " + LoginFlag);
		
		if(LoginFlag) {
			
			mOperatorController = new OperatorController(activity, context, layoutid);
			mOperatorController.LoginDisplay();
			
		} else OperatorDisplay(activity, context);
		
		btnState = false;
	}
	
	public void OperatorDisplay(Activity activity, Context context) {
		
		mDatabaseHander = new DatabaseHander(context);
		String id = mDatabaseHander.GetLastLoginID();
		
		if(id == null) id = "Guest";
		
		idText = (TextView) activity.findViewById(R.id.idtext);
		
		idText.setText("Operator : " + id);
	}
	
	public void DisplayDemo() {
		
		if(ANALYZER_SW == DEMO) {
			
			String demoVersion = "A1Care_v1.3.02-demo";
			
			demoVerText = (TextView) findViewById(R.id.demovertext);
			demoVerText.setText(demoVersion);	
		
		} else if(ANALYZER_SW == DEVEL) {
			
			String demoVersion = "A1Care_v1.3-devel";
			
			demoVerText = (TextView) findViewById(R.id.demovertext);
			demoVerText.setText(demoVersion);	
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Blank		:			
			nextIntent = new Intent(getApplicationContext(), BlankActivity.class); // Change to BLANK Activity
			break;
			
		case Record		:			
			nextIntent = new Intent(getApplicationContext(), RecordActivity.class); // Change to MEMORY Activity
			break;
			
		case Setting	:
			nextIntent = new Intent(getApplicationContext(), SettingActivity.class); // Change to SETTING Activity
			break;
			
		default			:	
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