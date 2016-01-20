package isens.hba1c_analyzer.Presenter;

import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.SoundModel;
import isens.hba1c_analyzer.View.SoundIView;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class SoundPresenter {

	private SoundIView mSoundIView;
	private SoundModel mSound;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public SoundPresenter(SoundIView view, Activity activity, Context context, int layout) {
		
		mSoundIView = view;
		mSound = new SoundModel(activity, context);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mSoundIView.setImageId();
		mSoundIView.setImage();
		mSoundIView.setTextId();
		mSoundIView.setText();
		mSoundIView.setButtonId();
		
		mTimerDisplay.ActivityParm(activity, layout);
		displayBarGauge(mSound.getSoundVolume());
		
		SerialPort.Sleep(500);
		
		mSoundIView.setButtonClick();
	}
	
	public void upSound() {
		
		int volume;
		
		volume = mSound.upSoundVolume(mSound.getSoundVolume());
		displayBarGauge(volume);
		mSound.setSoundVolume(volume);
		
		SerialPort.Sleep(100);
		
		enabledAllBtn();
	}
	
	public void downSound() {
		
		int volume;
		
		volume = mSound.downSoundVolume(mSound.getSoundVolume());
		displayBarGauge(volume);
		mSound.setSoundVolume(volume);

		SerialPort.Sleep(100);
		
		enabledAllBtn();
	}
	
	public void displayBarGauge(int volume) {
		
		mSoundIView.setBarGaugeImage(mSound.getBarGauageImage(volume));
	}
	
	public void enabledAllBtn() {
		
		mSoundIView.setButtonState(R.id.backBtn, true);
		mSoundIView.setButtonState(R.id.minusBtn, true);
		mSoundIView.setButtonState(R.id.plusBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mSoundIView.setButtonState(R.id.backBtn, false);
		mSoundIView.setButtonState(R.id.minusBtn, false);
		mSoundIView.setButtonState(R.id.plusBtn, false);
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
