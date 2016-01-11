package isens.hba1c_analyzer.Presenter;

import java.sql.Date;
import java.util.ArrayList;
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
import isens.hba1c_analyzer.Model.AboutModel;
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
import isens.hba1c_analyzer.View.ExportActivity;
import isens.hba1c_analyzer.View.FunctionalTestIView;
import isens.hba1c_analyzer.View.LanguageIView;
import isens.hba1c_analyzer.View.RecordDataIView;
import isens.hba1c_analyzer.View.RecordIView;

public class RecordDataPresenter {
	
	private RecordDataIView mRecordDataIView;
	private RecordDataModel mRecordDataModel;
	private MainTimer mMainTimer;
	private ActivityChange mActivityChange;
	private ErrorPopup mErrorPopup;
	private SerialPort mSerialPort;
	
	private Activity activity;
	private Context context;
	private int layout;
	private TargetIntent target;
	
	private boolean isPressed = false;
	private int btnId;
	
	public RecordDataPresenter(RecordDataIView view, Activity activity, Context context, int layout, TargetIntent target) {
		
		mRecordDataIView = view;
		mRecordDataModel = new RecordDataModel(activity, context);
		mMainTimer = new MainTimer(activity, layout);
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
		this.target = target;
	}
	
	public void init() {
		
		mRecordDataIView.setTextId();
		if(target == TargetIntent.PatientFileLoad) mRecordDataIView.setText(context.getResources().getString(R.string.patientdata));
		else mRecordDataIView.setText(context.getResources().getString(R.string.controldata));
		mRecordDataIView.setPopupWindowId();
		mRecordDataIView.setDetailTextId();
		mRecordDataIView.setButtonId();
		mRecordDataIView.setDetailButtonId();
		mRecordDataIView.setIButtonId();
		
		SerialPort.Sleep(500);
		
		displayError();
		
		mRecordDataIView.setButtonClick();
		mRecordDataIView.setDetailButtonClick();
		mRecordDataIView.setIButtonClick();
		unenabledAllDetailBtn();
		
		display();
	}
	
	public void display() {
		
		mRecordDataIView.setRecord1Text(mRecordDataModel.getRecordData(0, target));
		mRecordDataIView.setRecord2Text(mRecordDataModel.getRecordData(1, target));
		mRecordDataIView.setRecord3Text(mRecordDataModel.getRecordData(2, target));
		mRecordDataIView.setRecord4Text(mRecordDataModel.getRecordData(3, target));
		mRecordDataIView.setRecord5Text(mRecordDataModel.getRecordData(4, target));
		mRecordDataIView.setPageText(mRecordDataModel.getPage(target));
	}
	
	public void displayError() {
		
		mActivityChange.setIntent();
		int state = mActivityChange.getIntIntent("System Check State", 0);
		
		if(state != RunActivity.NORMAL_OPERATION) {
			
			mErrorPopup = new ErrorPopup(activity, context, R.id.record2Layout, null, 0);
			mErrorPopup.ErrorBtnDisplay(state);
		}
	}
	
	public void enabledAllBtn() {

		mRecordDataIView.setButtonState(R.id.backBtn, true);
		mRecordDataIView.setButtonState(R.id.homeBtn, true);
		mRecordDataIView.setButtonState(R.id.exportBtn, true);
		mRecordDataIView.setButtonState(R.id.preViewBtn, true);
		mRecordDataIView.setButtonState(R.id.detailViewBtn, true);
		mRecordDataIView.setButtonState(R.id.nextViewBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mRecordDataIView.setButtonState(R.id.backBtn, false);
		mRecordDataIView.setButtonState(R.id.homeBtn, false);
		mRecordDataIView.setButtonState(R.id.exportBtn, false);
		mRecordDataIView.setButtonState(R.id.preViewBtn, false);
		mRecordDataIView.setButtonState(R.id.detailViewBtn, false);
		mRecordDataIView.setButtonState(R.id.nextViewBtn, false);
	}
	
	public void enabledAllDetailBtn() {

		mRecordDataIView.setDetailButtonState(R.id.dPrintBtn, true);
		mRecordDataIView.setDetailButtonState(R.id.dCancelBtn, true);
	}
	
	public void unenabledAllDetailBtn() {

		mRecordDataIView.setDetailButtonState(R.id.dPrintBtn, false);
		mRecordDataIView.setDetailButtonState(R.id.dCancelBtn, false);
	}
	
	public void displayCheckBox(int id) {
		
		if(!isPressed) {
			
			isPressed = true;
			mRecordDataIView.setIButton(id, isPressed);
			
		} else {

			mRecordDataIView.setIButton(btnId, false);

			if(btnId == id) isPressed = false;
			else mRecordDataIView.setIButton(id, isPressed);
		}
		
		btnId = id;
	}
	
	public void displayDetailView() {
		
		if(isPressed && !(mRecordDataModel.getRecordData(mRecordDataModel.getPressedCheckBox(btnId), target).get(0).equals(""))) {
		
			mRecordDataIView.setPopupWindow();
			mRecordDataIView.setDetailText(mRecordDataModel.getDetRecordData(mRecordDataModel.getPressedCheckBox(btnId), target));			
			enabledAllDetailBtn();
		
		} else enabledAllBtn();	
	}
	
	public void print() {
		
		mSerialPort = new SerialPort();
		mSerialPort.PrinterTxStart(activity, context, SerialPort.PRINT_RECORD, mRecordDataModel.getRawData(mRecordDataModel.getPressedCheckBox(btnId), target));
	}
	
	public void cancelDetailView() {
		
		unenabledAllDetailBtn();
		mRecordDataIView.dismissPopupWindow();
		enabledAllBtn();
	}
	
	public void changePrePage() {
		
		if(RecordDataModel.DataPage > 0) {
		
			mActivityChange.whichIntent(target);
			mActivityChange.putIntIntent("DataCnt", mRecordDataModel.getDataCnt(target));
			mActivityChange.putIntIntent("DataPage", --RecordDataModel.DataPage);
			mActivityChange.putTrgIntent("TargetIntent", target);
			mActivityChange.putIntIntent("System Check State", RunActivity.NORMAL_OPERATION);
			mActivityChange.finish();
		}
		
		enabledAllBtn();
	}
	
	public void changeNextPage() {
		
		int dataCnt;
		
		if(target == TargetIntent.PatientFileLoad) dataCnt = RemoveActivity.PatientDataCnt;
		else dataCnt = RemoveActivity.ControlDataCnt;
		
		if((dataCnt-2)/5 > RecordDataModel.DataPage) {
		
			mActivityChange.whichIntent(target);
			mActivityChange.putIntIntent("DataCnt", dataCnt);
			mActivityChange.putIntIntent("DataPage", ++RecordDataModel.DataPage);
			mActivityChange.putTrgIntent("TargetIntent", target);
			mActivityChange.putIntIntent("System Check State", RunActivity.NORMAL_OPERATION);
			mActivityChange.finish();
		}
		
		enabledAllBtn();
	}
	
	public void export() {
		
		if(MainTimer.ExtDeviceBarcode == MainTimer.FILE_USB_OPEN) {
			
			mActivityChange.whichIntent(TargetIntent.Export);
			mActivityChange.putStringIntent("HWSN", AboutModel.HWSN);
			mActivityChange.putIntIntent("DataCnt", mRecordDataModel.getDataCnt(target));
			mActivityChange.putIntIntent("DataPage", RecordDataModel.DataPage);
			mActivityChange.putTrgIntent("TargetIntent", target);
			mActivityChange.putIntIntent("System Check State", RunActivity.NORMAL_OPERATION);
			mActivityChange.finish();

		} else enabledAllBtn();
	}
	
	public void changeActivity(int btn) {
		
		switch(btn) {
		
		case R.id.backBtn	:
			mActivityChange.whichIntent(TargetIntent.Record);
			break;
		
		case R.id.homeBtn	:
			mActivityChange.whichIntent(TargetIntent.Home);
			break;
		
		case R.id.snapshotBtn	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			mActivityChange.whichIntent(TargetIntent.FileSave);
			mActivityChange.putBooleanIntent("snapshot", true);
			mActivityChange.putStringsIntent("datetime", MainTimer.rTime);
			mActivityChange.putBytesIntent("bitmap", bitmapBytes);
			break;
			
		default	:
			break;
		}
		
		mActivityChange.finish();
	}
	
	public void snapshotDetailView(byte[] bitmapBytes) {
		
		mActivityChange.whichIntent(TargetIntent.FileSave);
		mActivityChange.putBooleanIntent("snapshot", true);
		mActivityChange.putStringsIntent("datetime", MainTimer.rTime);
		mActivityChange.putBytesIntent("bitmap", bitmapBytes);
		
		mActivityChange.finish();
	}
}
