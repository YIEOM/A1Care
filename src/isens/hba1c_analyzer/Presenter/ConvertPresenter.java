package isens.hba1c_analyzer.Presenter;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.View.ConvertIView;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.LanguageIView;

public class ConvertPresenter {
	
	private ConvertIView mConvertIView;
	private ConvertModel mConvertModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public ConvertPresenter(ConvertIView view, Activity activity, Context context, int layout) {
		
		mConvertIView = view;
		mConvertModel = new ConvertModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mConvertIView.setImageId();
		mConvertIView.setImage();
		mConvertIView.setTextId();
		mConvertIView.setButtonId();
		
		mConvertModel.initPrimary();
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
		
		SerialPort.Sleep(500);
		
		mConvertIView.setButtonClick();
	}
	
	public void changePrimaryUp() {
		
		mConvertModel.upPrimaryIdx();
		
		display();
		
		enabledAllBtn();
	}

	public void changePrimaryDown() {
		
		mConvertModel.downPrimaryIdx();
		
		display();
		
		enabledAllBtn();
	}
	
	public void display() {
		
		int primary;
		
		primary = mConvertModel.getPrimary();
		mConvertIView.setText(primary);
	}
	
	public void enabledAllBtn() {

		mConvertIView.setButtonState(R.id.backBtn, true);
		mConvertIView.setButtonState(R.id.leftBtn, true);
		mConvertIView.setButtonState(R.id.rightBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mConvertIView.setButtonState(R.id.backBtn, false);
		mConvertIView.setButtonState(R.id.leftBtn, false);
		mConvertIView.setButtonState(R.id.rightBtn, false);
	}
	
	public void changeActivity(int btn) {
		
		switch(btn) {
		
		case R.id.backBtn	:
			mConvertModel.setPrimary();
			
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
