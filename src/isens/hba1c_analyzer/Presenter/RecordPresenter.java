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
import isens.hba1c_analyzer.RemoveActivity;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.Model.MainTimer;
import isens.hba1c_analyzer.Model.RecordDataModel;
import isens.hba1c_analyzer.View.ConvertIView;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.FunctionalTestIView;
import isens.hba1c_analyzer.View.LanguageIView;
import isens.hba1c_analyzer.View.RecordIView;

public class RecordPresenter {
	
	private RecordIView mRecordIView;
	private MainTimer mMainTimer;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public RecordPresenter(RecordIView view, Activity activity, Context context, int layout) {
		
		mRecordIView = view;
		mMainTimer = new MainTimer(activity, layout);
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mRecordIView.setTextId();
		mRecordIView.setText();
		mRecordIView.setButtonId();
		
		SerialPort.Sleep(500);
		
		RecordDataModel.DataPage = 0;
		
		mRecordIView.setButtonClick();
	}
	
	public void enabledAllBtn() {

		mRecordIView.setButtonState(R.id.backBtn, true);
		mRecordIView.setButtonState(R.id.patientBtn, true);
		mRecordIView.setButtonState(R.id.controlBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mRecordIView.setButtonState(R.id.backBtn, false);
		mRecordIView.setButtonState(R.id.patientBtn, false);
		mRecordIView.setButtonState(R.id.controlBtn, false);
	}
	
	public void changeActivity(int btn) {
		
		switch(btn) {
		
		case R.id.backBtn	:
			mActivityChange.whichIntent(TargetIntent.Home);
			break;
		
		case R.id.patientBtn	:
			mActivityChange.whichIntent(TargetIntent.PatientFileLoad);
			mActivityChange.putIntIntent("DataCnt", RemoveActivity.PatientDataCnt);
			mActivityChange.putIntIntent("DataPage", 0);
			mActivityChange.putTrgIntent("TargetIntent", TargetIntent.PatientFileLoad);
			mActivityChange.putIntIntent("System Check State", RunActivity.NORMAL_OPERATION);
			break;
		
		case R.id.controlBtn	:
			mActivityChange.whichIntent(TargetIntent.ControlFileLoad);
			mActivityChange.putIntIntent("DataCnt", RemoveActivity.ControlDataCnt);
			mActivityChange.putIntIntent("DataPage", 0);
			mActivityChange.putTrgIntent("TargetIntent", TargetIntent.ControlFileLoad);
			mActivityChange.putIntIntent("System Check State", RunActivity.NORMAL_OPERATION);
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
