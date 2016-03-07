package isens.hba1c_analyzer;

import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.CustomTextView;
import isens.hba1c_analyzer.Model.FileSystem;
import isens.hba1c_analyzer.Model.LocationModel;
import isens.hba1c_analyzer.Model.SoundModel;
import isens.hba1c_analyzer.Model.TemperatureModel;

import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;

/*
 * 
 * Object : SW for A1Care project
 * 
 */

public class HomeActivity extends Activity {
	
	public static final byte ANALYZER_SW = RunActivity.NORMAL_OPERATION;

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
	private FileSystem mFileSystem;
	
	public Activity activity;
	public Context context;
	
	public TextView runText,
					settingText,
					recordText,
					idText,
					demoVerText,
					swVersionText;
	
	public SoundPool mPool;
	
	public Button runBtn,
				  settingBtn,
				  recordBtn,
				  escIcon,
				  snapshotBtn;
	
	public CustomTextView customTextView;
	
	public enum TargetIntent {Home, HbA1c, NA, Action, ActionQC, Run, RunQC, Blank, BlankQC, Record, Result, ResultQC, Remove, Image, Date, Setting, SystemSetting, DataSetting, OperatorSetting, FunctionalTest, Time, Display, HIS, HISSetting, Export, Engineer, FileSave, ControlFileLoad, PatientFileLoad, NextFile, PreFile, Adjustment, Sound, Calibration, Language, Correlation, About, Delete, Temperature, Lamp, Convert, Location, ShutDown, ScanTemp, f535, f660, SystemCheck, SnapShot}
	
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
	
	private void setTextId() {
		
		runText = (TextView) findViewById(R.id.runText);
		settingText = (TextView) findViewById(R.id.settingText);
		recordText = (TextView) findViewById(R.id.recordText);
		swVersionText = (TextView) findViewById(R.id.swVersionText);
	}
	
	private void setText() {
		
		runText.setPaintFlags(runText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		runText.setText(R.string.run);
		settingText.setPaintFlags(settingText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		settingText.setText(R.string.setting);
		recordText.setPaintFlags(recordText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		recordText.setText(R.string.record);
		swVersionText.setText(AboutModel.SWVersion + "(" + Character.valueOf(LocationModel.LocationCode) + (TemperatureModel.TmpSensorCode-48) + ")");
	}
	
	public void setButtonId(Activity activity) {
		
		runBtn = (Button)activity.findViewById(R.id.runbtn);
		settingBtn = (Button)activity.findViewById(R.id.settingbtn);
		recordBtn = (Button)activity.findViewById(R.id.recordbtn);
		escIcon = (Button)activity.findViewById(R.id.escicon);
		snapshotBtn = (Button)activity.findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		runBtn.setOnTouchListener(mTouchListener);
		settingBtn.setOnTouchListener(mTouchListener);
		recordBtn.setOnTouchListener(mTouchListener);
		escIcon.setOnTouchListener(mTouchListener);
		if(ANALYZER_SW == RunActivity.DEVEL_OPERATION) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				unenabledAllBtn(activity);
				
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
					
				case R.id.snapshotBtn	:
					WhichIntent(activity, context, TargetIntent.SnapShot);
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllBtn(Activity activity) {

		setButtonState(R.id.escicon, true, activity);
		setButtonState(R.id.runbtn, true, activity);
		setButtonState(R.id.settingbtn, true, activity);
		setButtonState(R.id.recordbtn, true, activity);
	}
	
	public void unenabledAllBtn(Activity activity) {
		
		setButtonState(R.id.escicon, false, activity);
		setButtonState(R.id.runbtn, false, activity);
		setButtonState(R.id.settingbtn, false, activity);
		setButtonState(R.id.recordbtn, false, activity);
	}

	public void HomeInit() {

		activity = this;
		context = this;

		setTextId();
		setText();
		setButtonId(activity);
		unenabledAllBtn(activity);

		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);
		setDataNumber(itn.getIntExtra("data cnt", 0), itn.getIntExtra("test type", 0));

		if(state != RunActivity.NORMAL_OPERATION) {

			mErrorPopup = new ErrorPopup(activity, context, R.id.homelayout, null, 0);
			mErrorPopup.ErrorBtnDisplay(state);

		} else {

			Login(activity, context, R.id.homelayout);
		}

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.homelayout);

		DisplayDemo();

		SetButton mSetButton = new SetButton();
		mSetButton.start();
	}

	public class SetButton extends Thread {
		
		public void run() {
			
			SerialPort.Sleep(500);
			
			setButtonClick();
		}
	}
	
	public void Login(Activity activity, Context context, int layoutid) {
		
		if(LoginFlag) {
			
			mOperatorController = new OperatorPopup(activity, context, layoutid);
			mOperatorController.LoginDisplay();
			
			mSoundModel = new SoundModel(activity, context);
			mSoundModel.playSound(R.raw.booting_bgm);
			
		} else OperatorDisplay(activity, context);
	}
	
	private void setDataNumber(int dataCnt, int testType) {

		mFileSystem = new FileSystem(activity);
		mFileSystem.setPreferences("Data Counter", MODE_PRIVATE);

		if(LoginFlag) {

			if(testType == RecordActivity.CONTROL) {

				RemoveActivity.PatientDataCnt = mFileSystem.getIntPref("PatientDataCnt", 1);
				RemoveActivity.ControlDataCnt = dataCnt;

			} else if(testType == RecordActivity.PATIENT) {

				RemoveActivity.PatientDataCnt = dataCnt;
				RemoveActivity.ControlDataCnt = mFileSystem.getIntPref("ControlDataCnt", 1);

			} else {

				RemoveActivity.PatientDataCnt = mFileSystem.getIntPref("PatientDataCnt", 1);
				RemoveActivity.ControlDataCnt = mFileSystem.getIntPref("ControlDataCnt", 1);
			}
		}
	}
	
	public void OperatorDisplay(Activity activity, Context context) {
		
		mDatabaseHander = new DatabaseHander(context);
		String id = mDatabaseHander.GetLastLoginID();
		
		if(id == null) id = "Guest";
		
		idText = (TextView) activity.findViewById(R.id.idtext);
		
		idText.setText(context.getResources().getString(R.string.operator) + " : " + id);
		
		SerialPort.Sleep(100);
		
		enabledAllBtn(activity);
	}
	
	public void DisplayDemo() {
		
		String demoVersion;
		
		if(ANALYZER_SW == RunActivity.DEMO_OPERATION) {
			
			demoVersion = "v1.3.31-D";
			DisplayDemoVersion(demoVersion);
		
		} else if(ANALYZER_SW == RunActivity.DEVEL_OPERATION) {
			
			demoVersion = "v1.3-devel";
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
			
		case SnapShot	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			nextIntent = new Intent(context, FileSaveActivity.class);
			nextIntent.putExtra("snapshot", true);
			nextIntent.putExtra("datetime", TimerDisplay.rTime);
			nextIntent.putExtra("bitmap", bitmapBytes);
			break;
			
		default			:	
			break;
		}
		
		activity.startActivity(nextIntent);
		finish(activity, Itn);
	}
	
	public void WhichIntentforSnapshot(Activity activity, Context context, byte[] bitmapBytes) {
		
		Intent nextIntent = null;
		
		nextIntent = new Intent(context, FileSaveActivity.class);
		nextIntent.putExtra("snapshot", true);
		nextIntent.putExtra("datetime", TimerDisplay.rTime);
		nextIntent.putExtra("bitmap", bitmapBytes);
		
		activity.startActivity(nextIntent);
		finish(activity, TargetIntent.SnapShot);
	}
	
	public void finish(Activity activity, TargetIntent Itn) {
		
		super.finish();
		if(Itn != TargetIntent.SystemCheck) activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}