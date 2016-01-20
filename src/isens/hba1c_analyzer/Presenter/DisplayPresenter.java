package isens.hba1c_analyzer.Presenter;

import android.app.Activity;
import android.content.Context;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.DisplayModel;
import isens.hba1c_analyzer.View.DisplayIView;

public class DisplayPresenter {

	private DisplayIView mDisplayIView;
	private DisplayModel mDisplay;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public DisplayPresenter(DisplayIView view, Activity activity, Context context, int layout) {
		
		mDisplayIView = view;
		mDisplay = new DisplayModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mDisplayIView.setImageId();
		mDisplayIView.setImage();
		mDisplayIView.setTextId();
		mDisplayIView.setText();
		mDisplayIView.setButtonId();
		
		mTimerDisplay.ActivityParm(activity, layout);
		displayBarGauge(mDisplay.getBrightnessValue());
		
		SerialPort.Sleep(500);
		
		mDisplayIView.setButtonClick();
	}
	
	public void upBrightness() {
		
		int value;
		
		value = mDisplay.upBrightnessValue(mDisplay.getBrightnessValue());
		displayBarGauge(value);
		mDisplay.setBrightnessValue(value);

		SerialPort.Sleep(100);
		
		enabledAllBtn();
	}
	
	public void downBrightness() {
		
		int value;
		
		value = mDisplay.downBrightnessValue(mDisplay.getBrightnessValue());
		displayBarGauge(value);
		mDisplay.setBrightnessValue(value);
		
		SerialPort.Sleep(100);
		
		enabledAllBtn();
	}
	
	public void displayBarGauge(int Value) {
		
		mDisplayIView.setBarGaugeImage(mDisplay.getBarGauageImage(Value));
	}
	
	public void enabledAllBtn() {
		
		mDisplayIView.setButtonState(R.id.backBtn, true);
		mDisplayIView.setButtonState(R.id.minusBtn, true);
		mDisplayIView.setButtonState(R.id.plusBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mDisplayIView.setButtonState(R.id.backBtn, false);
		mDisplayIView.setButtonState(R.id.minusBtn, false);
		mDisplayIView.setButtonState(R.id.plusBtn, false);
	}
	
	public void changeActivity(int btn) {
		
		switch(btn) {
		
		case R.id.backBtn	:
			mActivityChange.whichIntent(TargetIntent.SystemSetting);
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
