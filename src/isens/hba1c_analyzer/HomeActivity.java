package isens.hba1c_analyzer;

import isens.hba1c_analyzer.Model.SoundModel;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
 * Object : SW for A1Care project
 * 
 */

public class HomeActivity extends Activity {
	
	final static byte NORMAL = 0,
					  DEVEL = 1, // Development
					  DEMO = 2, // Sales department
					  ANALYZER_SW = NORMAL;

	final static byte PP = 1,
			          ES = 2,
			          ANALYZER_DEVICE = PP;

	public static final byte A1C_QC = 1,
							 A1C    = 0;
	
	public static byte MEASURE_MODE;
	
	public DatabaseHander mDatabaseHander;
	public OperatorPopup mOperatorController;
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;
	public ShutDownPopup mShutDownPopup;
	public SoundModel mSoundModel;
	
	public Activity activity;
	public Context context;
	
	public TextView idText,
					demoVerText;
	
	public SoundPool mPool;
	
	public Button runBtn,
				  settingBtn,
				  recordBtn,
				  escIcon;
	
	public enum TargetIntent {Home, HbA1c, NA, Action, ActionQC, Run, RunQC, Blank, BlankQC, Record, Result, ResultQC, Remove, Image, Date, Setting, SystemSetting, DataSetting, OperatorSetting, FunctionalTest, Time, Display, HIS, HISSetting, Export, Engineer, FileSave, ControlFileLoad, PatientFileLoad, NextFile, PreFile, Adjustment, Sound, Calibration, Language, Correlation, About, Delete, Temperature, Lamp, Convert, tHb, ShutDown, ScanTemp, f535, f660, SystemCheck}
	
	public static boolean LoginFlag = true,
						  CheckFlag;
	
	public static byte NumofStable = 0;
	
	public boolean isShutDown = false;
	
	public int mWin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.home);
		
		HomeInit();
	}
	
	public void setButtonId(Activity activity) {
		
		runBtn = (Button)activity.findViewById(R.id.runbtn);
		settingBtn = (Button)activity.findViewById(R.id.settingbtn);
		recordBtn = (Button)activity.findViewById(R.id.recordbtn);
		escIcon = (Button)activity.findViewById(R.id.escicon);
	}
	
	public void setButtonClick() {
		
		runBtn.setOnTouchListener(mTouchListener);
		settingBtn.setOnTouchListener(mTouchListener);
		recordBtn.setOnTouchListener(mTouchListener);
		escIcon.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn(activity); //0624
				
				switch(v.getId()) {
			
				case R.id.escicon		:
					ESC();
					break;
					
				case R.id.runbtn		:
					MEASURE_MODE = A1C;
					WhichIntent(activity, context, TargetIntent.Blank);
					break;
				
				case R.id.settingbtn	:
					WhichIntent(activity, context, TargetIntent.Setting);
					break;
				
				case R.id.recordbtn		:
					WhichIntent(activity, context, TargetIntent.Record);
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllBtn(Activity activtiy) {

		setButtonState(R.id.escicon, true, activtiy);
		setButtonState(R.id.runbtn, true, activtiy);
		setButtonState(R.id.settingbtn, true, activtiy);
		setButtonState(R.id.recordbtn, true, activtiy);
	}
	
	public void unenabledAllBtn(Activity activtiy) {
		
		setButtonState(R.id.escicon, false, activtiy);
		setButtonState(R.id.runbtn, false, activtiy);
		setButtonState(R.id.settingbtn, false, activtiy);
		setButtonState(R.id.recordbtn, false, activtiy);
	}
	
	public void HomeInit() {
		
		activity = this;
		context = this;
		
		setButtonId(activity);
		unenabledAllBtn(activity);
		setButtonClick();
				
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);
		
		if(state != RunActivity.NORMAL_OPERATION) {
			
			mErrorPopup = new ErrorPopup(this, this, R.id.homelayout, null, 0);
			mErrorPopup.ErrorBtnDisplay(state);
		
		} else {
			
			Login(this, this, R.id.homelayout);
		}
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.homelayout);
		
		DisplayDemo();
	}
	
	public void Login(Activity activity, Context context, int layoutid) {
		
		if(LoginFlag) {
			
			mOperatorController = new OperatorPopup(activity, context, layoutid);
			mOperatorController.LoginDisplay();
			
			mSoundModel = new SoundModel(activity, context);
			mSoundModel.playSound(R.raw.booting_bgm);
			
		} else OperatorDisplay(activity, context);
	}
	
	public void OperatorDisplay(Activity activity, Context context) {
		
		mDatabaseHander = new DatabaseHander(context);
		String id = mDatabaseHander.GetLastLoginID();
		
		if(id == null) id = "Guest";
		
		idText = (TextView) activity.findViewById(R.id.idtext);
		
		idText.setText("Operator : " + id);
		
		enabledAllBtn(activity);
	}
	
	public void DisplayDemo() {
		
		String demoVersion;
		
		if(ANALYZER_SW == DEMO) {
			
			demoVersion = "A1Care_v1.3.27-D";
			DisplayDemoVersion(demoVersion);	
		
		} else if(ANALYZER_SW == DEVEL) {
			
			demoVersion = "A1Care_v1.3-devel";
			DisplayDemoVersion(demoVersion);
		}
	}
	
	public void DisplayDemoVersion(String version) {
		
		demoVerText = (TextView) findViewById(R.id.demovertext);
		demoVerText.setText(version);
	}
	
	public void ESC() {
		
		mErrorPopup = new ErrorPopup(this, this, R.id.homelayout, null, 0);
		mErrorPopup.OXBtnDisplay(R.string.shutdown);
	}
	
	public void shutDown(Activity activity, Context context, int layoutid) {
		
		AniShutDown mAniShutDown = new AniShutDown(activity, context, layoutid);
		mAniShutDown.start();
		
		TimerDisplay.ExternalDeviceBarcode = TimerDisplay.FILE_CLOSE;
		
		TimerDisplay.FiftymsPeriod.cancel();
		
		isShutDown = true;
	}
	
	public class AniShutDown extends Thread {
		
		private Activity activity;
		private Context context;
		private int layoutid;
		
		public AniShutDown(Activity activity, Context context, int layoutid) {
			
			this.activity = activity;
			this.context = context;
			this.layoutid = layoutid;
		}
		
		public void run() {
			
			mShutDownPopup = new ShutDownPopup(activity, context, layoutid);
			mShutDownPopup.ShutDownDisplay();
			
			do {

				mShutDownPopup.setText(R.string.shuttingdown);
				mShutDownPopup.setImage(R.drawable.shutdown_point_1);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_2);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_3);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_1);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_2);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_3);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_1);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_2);
				SerialPort.Sleep(500);
				mShutDownPopup.setImage(R.drawable.shutdown_point_3);
					
			} while(!isShutDown);
			
			mShutDownPopup.setText(R.string.turnoff);
		}
	}
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Blank		:			
			nextIntent = new Intent(context, BlankActivity.class); // Change to BLANK Activity
			nextIntent.putExtra("Mode", (int) HomeActivity.A1C);
			break;
			
		case Record		:			
			nextIntent = new Intent(context, RecordActivity.class); // Change to MEMORY Activity
			break;
			
		case Setting	:
			nextIntent = new Intent(context, SettingActivity.class); // Change to SETTING Activity
			break;
				
		case SystemCheck	:
			nextIntent = new Intent(context, SystemCheckActivity.class);
			nextIntent.putExtra("System Check State", R.string.e221);
			break;
			
		default			:	
			break;
		}
		
		activity.startActivity(nextIntent);
		finish(Itn);
	}
	
	public void finish(TargetIntent Itn) {
		
		super.finish();
		if(Itn != TargetIntent.SystemCheck) overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}