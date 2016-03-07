package isens.hba1c_analyzer.Presenter;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.SetButton;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.LocationModel;
import isens.hba1c_analyzer.Model.TemperatureModel;
import isens.hba1c_analyzer.View.AboutIView;
import isens.hba1c_analyzer.View.LocationIView;
import isens.hba1c_analyzer.View.TemperatureIView;

public class TemperaturePresenter {
	
	private TemperatureIView mTemperatureIView;
	private TemperatureModel mTemperatureModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	private Handler runHandler = new Handler();
	private Timer runningTimer;
	
	public TemperaturePresenter(TemperatureIView view, Activity activity, Context context, int layout) {
		
		mTemperatureIView = view;
		mTemperatureModel = new TemperatureModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
	
		InitTemperature mInitTemperature = new InitTemperature();
		mInitTemperature.start();
	}
	
	public class InitTemperature extends Thread {
		
		public void run() {
			
			mTemperatureIView.setEditTextId();
			mTemperatureIView.setTextId();
			mTemperatureIView.setButtonId();
			
			mTemperatureIView.setButtonBg(mTemperatureModel.getBtnID((int) TemperatureModel.TmpSensorCode), R.drawable.btn9_s);
			mTemperatureIView.setEditText(Float.toString(TemperatureModel.InitChambTmp));
			
			mTimerDisplay.ActivityParm(activity, layout);

			SerialPort.Sleep(500);
		
			mTemperatureIView.setButtonClick();
			
			initRunTimer();
		}
	}
	
	private void initRunTimer() {

		TimerTask FiveHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						displayTmp();
					}
				};
				
				runHandler.post(updater);		
			}
		};
		
		runningTimer = new Timer();
		runningTimer.schedule(FiveHundredmsPeriod, 0, 500); // Timer period : 500msec
	}
	
	private void displayTmp() {
		
		DisplayTmp mDisplayTmp = new DisplayTmp(activity);
		mDisplayTmp.start();
	}
	
	public class DisplayTmp extends Thread {
		
		Activity activity;
		
		DisplayTmp(Activity activity) {
			
			this.activity = activity;
		}
		
		public void run() {
			
			final DecimalFormat tmpdfm = new DecimalFormat("0.0");
			final double chambTmp,
						 innerTmp;
			
			chambTmp = mTemperatureModel.getChambTmp();
			innerTmp = mTemperatureModel.getInnerTmp(); 
			
			new Thread(new Runnable() {
				public void run() {    
					activity.runOnUiThread(new Runnable(){
						public void run(){

							display(tmpdfm.format(chambTmp), tmpdfm.format(innerTmp));
						}
					});
				}
			}).start();
		}
	}
	
	public void setTmp() {
		
		String tmp;
		
		try {
		
			tmp = mTemperatureIView.getChambTmp();
			mTemperatureModel.setChambTmp(Float.valueOf(tmp).floatValue());
			
		} catch(NumberFormatException e) {
			
			mTemperatureModel.setChambTmp(TemperatureModel.InitChambTmp);
		}
	}
	
	public void changeCode(int btnId) {
		
		if(mTemperatureModel.isChangeCode(btnId)) {
			
			mTemperatureIView.setButtonBg(mTemperatureModel.getDeselectedBtnID(btnId), R.drawable.btn9);
			mTemperatureIView.setButtonBg(btnId, R.drawable.btn9_s);
			
			mTemperatureModel.setCode();
		}
	}
	
	public void display(String chamTmp, String ambTmp) {
		
		mTemperatureIView.setText(chamTmp, ambTmp);			
	}
	
	public void enabledAllBtn() {

		mTemperatureIView.setButtonState(R.id.backBtn, true);
		mTemperatureIView.setButtonState(R.id.setbtn, true);
		mTemperatureIView.setButtonState(R.id.tmp_1Btn, true);
		mTemperatureIView.setButtonState(R.id.tmp_2Btn, true);
	}
	
	public void unenabledAllBtn() {
		
		mTemperatureIView.setButtonState(R.id.backBtn, false);
		mTemperatureIView.setButtonState(R.id.setbtn, false);
		mTemperatureIView.setButtonState(R.id.tmp_1Btn, false);
		mTemperatureIView.setButtonState(R.id.tmp_2Btn, false);
	}
	
	public void changeActivity(int btn) {
		
		runningTimer.cancel();
		
		switch(btn) {
		
		case R.id.backBtn	:
			mActivityChange.whichIntent(TargetIntent.Engineer);
			mActivityChange.finish();
			break;
		
		case R.id.snapshotBtn	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			mActivityChange.whichIntent(TargetIntent.SnapShot);
			mActivityChange.putBooleanIntent("snapshot", true);
			mActivityChange.putStringsIntent("datetime", TimerDisplay.rTime);
			mActivityChange.putBytesIntent("bitmap", bitmapBytes);
			mActivityChange.finish();
			break;
			
		default	:
			break;
		}
	}
}
