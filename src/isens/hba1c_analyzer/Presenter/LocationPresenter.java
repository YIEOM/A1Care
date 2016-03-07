package isens.hba1c_analyzer.Presenter;

import android.app.Activity;
import android.content.Context;
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
import isens.hba1c_analyzer.View.AboutIView;
import isens.hba1c_analyzer.View.LocationIView;

public class LocationPresenter {
	
	private LocationIView mLocationIView;
	private LocationModel mLocationModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public LocationPresenter(LocationIView view, Activity activity, Context context, int layout) {
		
		mLocationIView = view;
		mLocationModel = new LocationModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
	
		InitLocation mInitLocation = new InitLocation();
		mInitLocation.start();
	}
	
	public class InitLocation extends Thread {
		
		public void run() {
						
			mLocationIView.setButtonId();
			
			mLocationIView.setButtonBg(mLocationModel.getBtnID((int) LocationModel.LocationCode), R.drawable.btn9_s);
			
			mTimerDisplay.ActivityParm(activity, layout);

			SerialPort.Sleep(500);
		
			mLocationIView.setButtonClick();
		}
	}
	
	public void changeCode(int btnId) {
		
		Log.w("changeCode", "btnId : " + btnId);
		
		if(mLocationModel.isChangeCode(btnId)) {
			
			mLocationIView.setButtonBg(mLocationModel.getDeselectedBtnID(btnId), R.drawable.btn9);
			mLocationIView.setButtonBg(btnId, R.drawable.btn9_s);
		}
	}
	
	public void enabledAllBtn() {

		mLocationIView.setButtonState(R.id.backBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st1stBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st2ndBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st3rdBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st4thBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st5thBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st6thBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st7thBtn, true);
		mLocationIView.setButtonState(R.id.lct_1st8thBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd1stBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd2ndBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd3rdBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd4thBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd5thBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd6thBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd7thBtn, true);
		mLocationIView.setButtonState(R.id.lct_2nd8thBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd1stBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd2ndBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd3rdBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd4thBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd5thBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd6thBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd7thBtn, true);
		mLocationIView.setButtonState(R.id.lct_3rd8thBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th1stBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th2ndBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th3rdBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th4thBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th5thBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th6thBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th7thBtn, true);
		mLocationIView.setButtonState(R.id.lct_4th8thBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th1stBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th2ndBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th3rdBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th4thBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th5thBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th6thBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th7thBtn, true);
		mLocationIView.setButtonState(R.id.lct_5th8thBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th1stBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th2ndBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th3rdBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th4thBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th5thBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th6thBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th7thBtn, true);
		mLocationIView.setButtonState(R.id.lct_6th8thBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th1stBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th2ndBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th3rdBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th4thBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th5thBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th6thBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th7thBtn, true);
		mLocationIView.setButtonState(R.id.lct_7th8thBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mLocationIView.setButtonState(R.id.backBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st1stBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st2ndBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st3rdBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st4thBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st5thBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st6thBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st7thBtn, false);
		mLocationIView.setButtonState(R.id.lct_1st8thBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd1stBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd2ndBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd3rdBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd4thBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd5thBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd6thBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd7thBtn, false);
		mLocationIView.setButtonState(R.id.lct_2nd8thBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd1stBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd2ndBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd3rdBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd4thBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd5thBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd6thBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd7thBtn, false);
		mLocationIView.setButtonState(R.id.lct_3rd8thBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th1stBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th2ndBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th3rdBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th4thBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th5thBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th6thBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th7thBtn, false);
		mLocationIView.setButtonState(R.id.lct_4th8thBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th1stBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th2ndBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th3rdBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th4thBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th5thBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th6thBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th7thBtn, false);
		mLocationIView.setButtonState(R.id.lct_5th8thBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th1stBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th2ndBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th3rdBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th4thBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th5thBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th6thBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th7thBtn, false);
		mLocationIView.setButtonState(R.id.lct_6th8thBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th1stBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th2ndBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th3rdBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th4thBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th5thBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th6thBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th7thBtn, false);
		mLocationIView.setButtonState(R.id.lct_7th8thBtn, false);
	}
	
	public void changeActivity(int btn) {
		
		switch(btn) {
		
		case R.id.backBtn	:
			mLocationModel.setCode();
			
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
