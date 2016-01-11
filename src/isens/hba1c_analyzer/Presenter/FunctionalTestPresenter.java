package isens.hba1c_analyzer.Presenter;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Button;
import isens.hba1c_analyzer.ErrorPopup;
import isens.hba1c_analyzer.FileSaveActivity;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.Model.MainTimer;
import isens.hba1c_analyzer.View.ConvertIView;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.FunctionalTestIView;
import isens.hba1c_analyzer.View.LanguageIView;

public class FunctionalTestPresenter {
	
	private FunctionalTestIView mFunctionalTestIView;
	private MainTimer mMainTimer;
	private ActivityChange mActivityChange;
	private ErrorPopup mErrorPopup;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public FunctionalTestPresenter(FunctionalTestIView view, Activity activity, Context context, int layout) {
		
		mFunctionalTestIView = view;
		mMainTimer = new MainTimer(activity, layout);
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		int state;
		
		mFunctionalTestIView.setTextId();
		mFunctionalTestIView.setText();
		mFunctionalTestIView.setButtonId();
		
		state = mFunctionalTestIView.getIntentData();
		
		if(state != 0) {
			
			mErrorPopup = new ErrorPopup(activity, context, R.id.functionalTestLayout, null, 0);
			mErrorPopup.ErrorBtnDisplay(state);		
		}
		
		SerialPort.Sleep(500);
		
		mFunctionalTestIView.setButtonClick();
	}
	
	public void enabledAllBtn() {

		mFunctionalTestIView.setButtonState(R.id.backBtn, true);
		mFunctionalTestIView.setButtonState(R.id.homeBtn, true);
		mFunctionalTestIView.setButtonState(R.id.qcBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mFunctionalTestIView.setButtonState(R.id.backBtn, false);
		mFunctionalTestIView.setButtonState(R.id.homeBtn, false);
		mFunctionalTestIView.setButtonState(R.id.qcBtn, false);
	}
	
	public void changeActivity(int btn) {
		
		switch(btn) {
		
		case R.id.backBtn	:
			mActivityChange.whichIntent(TargetIntent.Setting);
			break;
		
		case R.id.homeBtn	:
			mActivityChange.whichIntent(TargetIntent.Home);
			break;
		
		case R.id.qcBtn	:
			HomeActivity.MEASURE_MODE = HomeActivity.A1C_QC;
			mActivityChange.whichIntent(TargetIntent.Blank);
			break;
		
		case R.id.snapshotBtn	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			mActivityChange.whichIntent(TargetIntent.SnapShot);
			mActivityChange.putBooleanIntent("snapshot", true);
			mActivityChange.putStringsIntent("datetime", MainTimer.rTime);
			mActivityChange.putBytesIntent("bitmap", bitmapBytes);
			break;
			
		default	:
			break;
		}
		
		mActivityChange.finish();
	}
}
