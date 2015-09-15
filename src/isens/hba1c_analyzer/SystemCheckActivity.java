package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import isens.hba1c_analyzer.CalibrationActivity.Cart1stShaking;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.RunActivity.CheckCoverError;
import isens.hba1c_analyzer.Temperature.CellTmpRead;
import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.Hardware;
import isens.hba1c_analyzer.View.ConvertActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SystemCheckActivity extends Activity {
	
	final static double MaxDark = 10000,
						MinDark = 2000,
						Max535  = 300000,
						Min535  = 70000,
						Max660  = 600000,
						Min660  = 150000,
						Max750  = 990000,
						Min750  = 400000;

	final static byte ERROR_DARK  = 1,
					  ERROR_535nm = 2,
					  ERROR_660nm = 4,
					  ERROR_750nm = 8;	

	public int numberChaberTmpCheck = 5*60; // 5 minute
	final static byte NUMBER_AMBIENT_TMP_CHECK = 120/10; // 120 seconds
	final static String SHAKING_CHECK_TIME = "0030";
	
	private enum TmpState {FirstTmp, SecondTmp, ThirdTmp, ForthTmp, FifthTmp}
	
	public GpioPort mGpioPort;
	public SerialPort mSerialPort;
	public Temperature mTemperature;
	public TimerDisplay mTimerDisplay;
	public ErrorPopup mErrorPopup;
	public AboutModel mAboutModel;
	
	public AudioManager audioManager;
	
	public RelativeLayout systemCheckLinear;
	
	private AnimationDrawable systemCheckAni;
	private ImageView systemCheckImage;
	
	private AnalyzerState systemState;
	
	public TmpState tmpNumber;
	
	private byte photoCheck;
	public int checkError;
	
	private Button snapshotBtn;
	
	private Activity activity;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.systemcheck);
		
		SystemCheckInit();
	}
	
	public void setButtonId(Activity activity) {
		
		snapshotBtn = (Button)activity.findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
					
				case R.id.snapshotBtn	:
					WhichIntent(TargetIntent.SnapShot);
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void SystemCheckInit() {
		
		activity = this;
		context = this;
		
		setButtonId(activity);
		setButtonClick();
		
		SystemAniStart();
	
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", RunActivity.NORMAL_OPERATION);
		
		if(state != R.string.e221) {
		
			/* Serial communication start */
			mSerialPort = new SerialPort();
			mSerialPort.BoardSerialInit();
			mSerialPort.BoardRxStart();
			mSerialPort.PrinterSerialInit();
			mSerialPort.BarcodeSerialInit();
			mSerialPort.BarcodeRxStart();
		
			/* Timer start */
			mTimerDisplay = new TimerDisplay();
			mTimerDisplay.ActivityParm(this, R.id.systemchecklayout);
			mTimerDisplay.TimerInit();
			
			/* Barcode reader off */
			mGpioPort = new GpioPort();
			mGpioPort.TriggerHigh();
			
			ParameterInit();
			GetVersion mGetVersion = new GetVersion(this);
			mGetVersion.start();
			
			BrightnessInit();
			VolumeInit();
			
			/* Temperature setting */
			mTemperature = new Temperature(); // to test
			mTemperature.TmpInit(); // to test
			
			/* TEST Mode */
			if((HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) || (HomeActivity.ANALYZER_SW == HomeActivity.DEMO)) {

				ChangeHome mChangeHome = new ChangeHome();
				mChangeHome.start();
			}
			else {
			
				mErrorPopup = new ErrorPopup(this, this, R.id.systemchecklayout, null, 0);

				SensorCheck SensorCheckObj = new SensorCheck();
				SensorCheckObj.start();			
			}
			
		} else {
			
			mTemperature = new Temperature();
			
			InsideTmpCheck mInsideTmpCheck = new InsideTmpCheck();
			mInsideTmpCheck.start();
		}
	}
	
	public class SensorCheck extends Thread {
		
		Activity activity;
		Context context;
		int layoutid;
		
		public void run() {
			
			GpioPort.DoorActState = true;
			GpioPort.CartridgeActState = true;
			
			SerialPort.Sleep(5000);
			
			while((ActionActivity.DoorCheckFlag != 1) || (ActionActivity.CartridgeCheckFlag != 0)) {
				
				if(ActionActivity.CartridgeCheckFlag != 0) mErrorPopup.ErrorDisplay(R.string.w002);
				else if(ActionActivity.DoorCheckFlag != 1) mErrorPopup.ErrorDisplay(R.string.w001);
				
				SerialPort.Sleep(100);
			}
			mErrorPopup.ErrorPopupClose();
			
			GpioPort.DoorActState = false;			
			GpioPort.CartridgeActState = false;

			MotorCheck MotorCheckObj = new MotorCheck();
			MotorCheckObj.start();
		}
	}
	
	public class MotorCheck extends Thread {
		
		public void run() {
			
			photoCheck = 0;
			systemState = AnalyzerState.InitPosition;
			checkError = RunActivity.NORMAL_OPERATION;
			RunActivity.IsError = false;
			
			for(int i = 0; i < 18; i++) {
				
				checkMode();
				
				switch(systemState) {
				
				case InitPosition		:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					MotorCheck(RunActivity.HOME_POSITION, AnalyzerState.Step1Position, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
				
				case Step1Position		:
					MotionInstruct(RunActivity.Step1st_POSITION, SerialPort.CtrTarget.NormalSet);			
					MotorCheck(RunActivity.Step1st_POSITION, AnalyzerState.Step1Shaking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case Step1Shaking		:
					MotionInstruct(SHAKING_CHECK_TIME, SerialPort.CtrTarget.MotorSet);
					MotorCheck(RunActivity.MOTOR_COMPLETE, AnalyzerState.Step2Position, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case Step2Position		:
					MotionInstruct(RunActivity.Step2nd_POSITION, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.Step2nd_POSITION, AnalyzerState.Step2Shaking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case Step2Shaking		:
					MotionInstruct(SHAKING_CHECK_TIME, SerialPort.CtrTarget.MotorSet);
					MotorCheck(RunActivity.MOTOR_COMPLETE, AnalyzerState.MeasurePosition, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case MeasurePosition	:
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.MEASURE_POSITION, AnalyzerState.FilterDark, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case FilterDark			:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.FILTER_DARK, AnalyzerState.MeasureDark, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case MeasureDark		:
					PhotoCheck(AnalyzerState.Filter535nm, MaxDark, MinDark, ERROR_DARK, 1);
					if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL) PhotoErrorCheck();
					break;
					
				case Filter535nm		:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.NEXT_FILTER, AnalyzerState.Measure535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case Measure535nm		:
					PhotoCheck(AnalyzerState.Filter660nm, Max535, Min535, ERROR_535nm, 1);
					break;
					
				case Filter660nm		:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.NEXT_FILTER, AnalyzerState.Measure660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case Measure660nm		:
					PhotoCheck(AnalyzerState.Filter750nm, Max660, Min660, ERROR_660nm, 1);
					break;
					
				case Filter750nm		:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.NEXT_FILTER, AnalyzerState.Measure750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case Measure750nm		:
					PhotoCheck(AnalyzerState.FilterHome, Max750, Min750, ERROR_750nm, 1);
					if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL) PhotoErrorCheck();
					break;
					
				case FilterHome			:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.FILTER_DARK, AnalyzerState.CartridgeDump, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case CartridgeDump		:
					MotionInstruct(RunActivity.CARTRIDGE_DUMP, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.CARTRIDGE_DUMP, AnalyzerState.CartridgeHome, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case CartridgeHome		:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case NormalOperation	:
					TimerDisplay.RXBoardFlag = false;
					
					if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL) {
					
						ChamberTmpCheck mChamberTmpCheck = new ChamberTmpCheck();
						mChamberTmpCheck.start();
					
					} else WhichIntent(TargetIntent.Home);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					systemState = AnalyzerState.NoWorking;
					WhichIntent(HomeActivity.TargetIntent.Home);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					MotorCheck(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					WhichIntent(HomeActivity.TargetIntent.Home);
					break;
				
				case PhotoSensorError	:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.FILTER_DARK, AnalyzerState.NoWorking, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					MotorCheck(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					WhichIntent(HomeActivity.TargetIntent.Home);
					break;
					
				case LampError			:
					checkError = R.string.e232;
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					MotorCheck(RunActivity.FILTER_DARK, AnalyzerState.NoWorking, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					MotorCheck(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					WhichIntent(HomeActivity.TargetIntent.Home);
					break;
					
				case NoResponse			:
					checkError = R.string.e241;
					systemState = AnalyzerState.NoWorking;
					WhichIntent(HomeActivity.TargetIntent.Home);
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					systemState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}

			switch(checkError) {
				
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w001);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
			
			default	:
				break;
			}
		}
	}
	
	public class ChamberTmpCheck extends Thread {
		
		public void run() {
			
			int i;
			double tmp = 0;
			tmpNumber = TmpState.FirstTmp;
			
			for(i = 0; i < numberChaberTmpCheck; i++) {
				
				tmp = mTemperature.CellTmpRead();
				
				switch(tmpNumber) {
				
				case FirstTmp	:
					if(((Temperature.InitTmp - 1) < tmp) & (tmp < (Temperature.InitTmp + 1))) tmpNumber = TmpState.SecondTmp;
					break;
					
				case SecondTmp	:
					if(((Temperature.InitTmp - 1) < tmp) & (tmp < (Temperature.InitTmp + 1))) tmpNumber = TmpState.ThirdTmp;
					else tmpNumber = TmpState.FirstTmp;
					break;
					
				case ThirdTmp	:
					if(((Temperature.InitTmp - 1) < tmp) & (tmp < (Temperature.InitTmp + 1))) tmpNumber = TmpState.ForthTmp;
					else tmpNumber = TmpState.FirstTmp;
					break;
					
				case ForthTmp	:
					if(((Temperature.InitTmp - 1) < tmp) & (tmp < (Temperature.InitTmp + 1))) tmpNumber = TmpState.FifthTmp;
					else tmpNumber = TmpState.FirstTmp;
					break;
					
				case FifthTmp	:
					if(((Temperature.InitTmp - 1) < tmp) & (tmp < (Temperature.InitTmp + 1))) numberChaberTmpCheck = 0;
					else tmpNumber = TmpState.FirstTmp;
					break;
				
				default	:
					break;
				}
				
				 SerialPort.Sleep(1000);
			}
			
			if(i != numberChaberTmpCheck) {
				
				SerialPort.Sleep(387000);
				
				InsideTmpCheck mInsideTmpCheck = new InsideTmpCheck();
				mInsideTmpCheck.start();
				
			} else {
				
				checkError = R.string.e222;
				WhichIntent(TargetIntent.Home);
			}
		}
	}
	
	public class InsideTmpCheck extends Thread {
		
		public void run() {
			
			int i;
			double tmp = 0;
			
			mTemperature.AmbTmpRead();
			
			for(i = 0; i < NUMBER_AMBIENT_TMP_CHECK; i++) {
				
				tmp += mTemperature.AmbTmpRead();
				
				SerialPort.Sleep(10000);
			}
			
			if((Temperature.MinAmbTmp < tmp/NUMBER_AMBIENT_TMP_CHECK) && (tmp/NUMBER_AMBIENT_TMP_CHECK < Temperature.MaxAmbTmp)) {
				
				checkError = RunActivity.NORMAL_OPERATION;
				WhichIntent(TargetIntent.Home);
			
			} else {
				
				checkError = R.string.e221;
				WhichIntent(TargetIntent.Home);
			}
		}
	}
	
	public void MotorCheck(String colRsp, AnalyzerState nextState, String errRsp, AnalyzerState errState, int rspTime) {
		
		int time = 0;
		String temp = "";
		
		rspTime = rspTime * 10;
		
		while(true) {
			
			temp = mSerialPort.BoardMessageOutput();
			
			if(colRsp.equals(temp)) {
				
				systemState = nextState;
				break;
			
			} else if(errRsp.equals(temp)) {
				
				systemState = errState;
				break;
			}
			
			if(time++ > rspTime) {
				
				systemState = AnalyzerState.NoResponse;
				checkError = R.string.e241;
				break;
			}
			
			if(RunActivity.IsError) break;
			
			SerialPort.Sleep(100);
		}
		
		TimerDisplay.RXBoardFlag = false;
	}
	
	public void PhotoCheck(AnalyzerState nextState, double max, double min, byte errBits, int rspTime) {
		
		int time = 0;
		String rawValue;
		double douValue = 0;
		
		while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
		
		TimerDisplay.RXBoardFlag = true;
		
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.NormalSet);
		
		do {
		
			rawValue = mSerialPort.BoardMessageOutput();
			
			if(time++ > 50)	break;
			
			if(RunActivity.IsError) break;
			
			SerialPort.Sleep(100);
		
		} while(rawValue.length() != 8);	
		
		if(!RunActivity.IsError) {
		
			try {
				
				douValue = Double.parseDouble(rawValue);
				
				if((min > douValue) || (douValue > max)) photoCheck += errBits;
				
				systemState = nextState;
				
			} catch(NumberFormatException e) {
				
				douValue = 0.0;
				
				systemState = AnalyzerState.NoResponse;
				checkError = R.string.e241;
			}
		}
		
		TimerDisplay.RXBoardFlag = false;
	}
	
	public void PhotoErrorCheck() {
		
		switch(photoCheck) {
		
		case 1	:
			systemState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e231;
			break;
			
		case 2	:
			systemState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e233;
			break;
			
		case 4	:
			systemState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e234;
			break;
			
		case 8	:
			systemState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e235;
			break;
			
		case 14	:
			systemState = AnalyzerState.LampError;
			checkError = R.string.e232;
			break;
			
		default	:
			break;
		}
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
		
		TimerDisplay.RXBoardFlag = true;
		mSerialPort.BoardTx(str, target);
	}
	
	public void SystemAniStart() { // System Check animation start
		
		systemCheckLinear = (RelativeLayout)findViewById(R.id.systemchecklayout);
		systemCheckImage = (ImageView)findViewById(R.id.systemcheckani);
		systemCheckAni = (AnimationDrawable)systemCheckImage.getBackground();
		
      	systemCheckLinear.post(new Runnable() {
	        public void run() {

	        	systemCheckAni.start();
	        }
		});
	}
	
	public void VolumeInit() { 
		
		int volume;
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		if((volume % 2) != 0 ) {
			
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 2, AudioManager.FLAG_PLAY_SOUND);
		}
	}
	
	public void BrightnessInit() {
		
		int brightness;
		
		try {
		
			brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
			
			if((brightness % 51) != 0) brightness = 51;
		
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.screenBrightness = (float)brightness/255;
			getWindow().setAttributes(params);
			
			android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
		
		} catch (Exception e) {
			
		}
	}
	
	public void ParameterInit() { // Load to saved various parameter
		
		SharedPreferences DcntPref = getSharedPreferences("Data Counter", MODE_PRIVATE);
		RemoveActivity.PatientDataCnt = DcntPref.getInt("PatientDataCnt", 1);
		RemoveActivity.ControlDataCnt = DcntPref.getInt("ControlDataCnt", 1);
		
		SharedPreferences factorPref = getSharedPreferences("User Define", MODE_PRIVATE);
		RunActivity.AF_Slope = factorPref.getFloat("AF SlopeVal", 1f);
		RunActivity.AF_Offset = factorPref.getFloat("AF OffsetVal", 0f);
		RunActivity.CF_Slope = factorPref.getFloat("CF SlopeVal", 1f);
		RunActivity.CF_Offset = factorPref.getFloat("CF OffsetVal", 0f);
		RunActivity.RF1_Slope = factorPref.getFloat("RF1 SlopeVal", 1f);
		RunActivity.RF1_Offset = factorPref.getFloat("RF1 OffsetVal", 0f);
		RunActivity.RF2_Slope = factorPref.getFloat("RF2 SlopeVal", 1f);
		RunActivity.RF2_Offset = factorPref.getFloat("RF2 OffsetVal", 0f);
		RunActivity.SF_F1 = factorPref.getFloat("SF Fct1stVal", 0f);
		RunActivity.SF_F2 = factorPref.getFloat("SF Fct2ndVal", 0f);
		
		SharedPreferences convertPref = getSharedPreferences("Primary", MODE_PRIVATE);
		ConvertModel.Primary = (byte) convertPref.getInt("Convert", 0);
		
		SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(this);
		HomeActivity.CheckFlag = loginPref.getBoolean("Check Box", false);
		
		SharedPreferences temperaturePref = getSharedPreferences("Temperature", MODE_PRIVATE);
		Temperature.InitTmp = temperaturePref.getFloat("Cell Block", 30f);
		
		SharedPreferences aboutPref = getSharedPreferences("About", MODE_PRIVATE);
		AboutModel.HWSN = aboutPref.getString("HW S/N", "Nothing");
	}
	
	public class GetVersion extends Thread {
		
		Activity activity;
		String swVersion, fwVersion, osVersion;
		
		public GetVersion(Activity activity) {
			
			this.activity = activity;
		}
		
		public void run() {
			
			mAboutModel = new AboutModel(activity);
			AboutModel.SWVersion = mAboutModel.getSWVersion();
			AboutModel.FWVersion = mAboutModel.getFWVersion();
			AboutModel.OSVersion = mAboutModel.getOSVersion();
		}
	}
	
	public void checkMode() {
		
		if(RunActivity.IsError) {
			
			systemState = AnalyzerState.ErrorCover;		
		}
	}
	
	public class CheckCoverError extends Thread {
		
		public void run() {
			
			GpioPort.DoorActState = true;			
			GpioPort.CartridgeActState = true;
			
			SerialPort.Sleep(2000);
			
			while(ActionActivity.DoorCheckFlag != 1) SerialPort.Sleep(100);
			mErrorPopup.ErrorPopupClose();
			
			GpioPort.DoorActState = false;			
			GpioPort.CartridgeActState = false;

			MotorCheck MotorCheckObj = new MotorCheck();
			MotorCheckObj.start();
		}
	}
	
	public class ChangeHome extends Thread {
		
		public void run() {
			
			SerialPort.Sleep(5000);
			
			WhichIntent(TargetIntent.Home);
		}
	}	
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home		:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			nextIntent.putExtra("System Check State", checkError);
			break;
		
		case SnapShot	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			nextIntent = new Intent(context, FileSaveActivity.class);
			nextIntent.putExtra("snapshot", true);
			nextIntent.putExtra("datetime", TimerDisplay.rTime);
			nextIntent.putExtra("bitmap", bitmapBytes);
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
