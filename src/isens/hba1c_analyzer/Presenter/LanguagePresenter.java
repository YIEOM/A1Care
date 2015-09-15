package isens.hba1c_analyzer.Presenter;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.LanguageIView;

public class LanguagePresenter {
	
	private LanguageIView mLanguageIView;
	private LanguageModel mLanguageModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public LanguagePresenter(LanguageIView view, Activity activity, Context context, int layout) {
		
		mLanguageIView = view;
		mLanguageModel = new LanguageModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mLanguageIView.setImageId();
		mLanguageIView.setImage();
		mLanguageIView.setTextId();
		mLanguageIView.setButtonId();
		
		mLanguageModel.getSettingLanguage();
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
		
		SerialPort.Sleep(500);
		
		mLanguageIView.setButtonClick();
	}
	
	public void upLanguage() {
		
		mLanguageModel.upLanguageIdx();
		
		display();
		
		SerialPort.Sleep(100);
		
		enabledAllBtn();
	}

	public void downLanguage() {
		
		mLanguageModel.downLanguageIdx();
		
		display();
		
		SerialPort.Sleep(100);
		
		enabledAllBtn();
	}
	
	public void display() {
		
		int language;
		
		language = mLanguageModel.getLanguage();
		mLanguageIView.setText(language);
	}
	
	public void enabledAllBtn() {

		mLanguageIView.setButtonState(R.id.backBtn, true);
		mLanguageIView.setButtonState(R.id.leftBtn, true);
		mLanguageIView.setButtonState(R.id.rightBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mLanguageIView.setButtonState(R.id.backBtn, false);
		mLanguageIView.setButtonState(R.id.leftBtn, false);
		mLanguageIView.setButtonState(R.id.rightBtn, false);
	}
	
	public void changeActivity(int btn) {
				
		switch(btn) {
		
		case R.id.backBtn	:
			mLanguageModel.setLocale();
			
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
