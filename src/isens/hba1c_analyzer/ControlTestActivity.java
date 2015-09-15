package isens.hba1c_analyzer;

import java.text.DecimalFormat;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.CustomTextView;
import isens.hba1c_analyzer.View.ExportActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ControlTestActivity extends Activity {
	
	public SerialPort mSerialPort;
	public TimerDisplay mTimerDisplay;
	public ErrorPopup mErrorPopup;
	
	private Activity activity;
	private Context context;
	
	public RelativeLayout record2Layout;
	public View detailPopupView;
	public PopupWindow detailPopup;
	
	public TextView TestNumText [] = new TextView[5],
					TypeText    [] = new TextView[5],
					ResultText  [] = new TextView[5],
					UnitText    [] = new TextView[5],
					DateTimeText[] = new TextView[5],
					titleText,
					patientID,
					testDate,
					typeDetailText,
					primary,
					range,
					ref,
					testNo,
					operatorID,
					result,
					pageText;
	
	public CustomTextView detailViewText;
	
	public Button homeIcon,
				  backIcon,
				  detailViewBtn,
				  nextViewBtn,
				  preViewBtn,
				  printBtn,
				  cancleBtn,
				  exportBtn,
				  snapshotBtn,
				  snapshotBtn2;
	
	public ImageButton checkBoxBtn1,
					   checkBoxBtn2,
					   checkBoxBtn3,
				       checkBoxBtn4,
					   checkBoxBtn5;
	
	public String dateTime[] = new String[5],
			  	  testNum [] = new String[5],
			  	  refNum  [] = new String[5],
			  	  typeStr [] = new String[5],
			  	  pID     [] = new String[5],
			  	  oID	  [] = new String[5],
			  	  priStr  [] = new String[5],
			  	  hbA1c   [] = new String[5];
	
	public boolean checkFlag = false;
	
	public ImageButton whichBox = null;
	
	public int boxNum = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.record2);
		
		/* Popup window activation */
		record2Layout = (RelativeLayout)findViewById(R.id.record2Layout);
		detailPopupView = View.inflate(this, R.layout.detailviewpopup, null);
		detailPopup = new PopupWindow(detailPopupView, 800, 480, true);
		
		ControlInit();
	}	
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		detailViewText = new CustomTextView(context);
		detailViewText = (CustomTextView) detailPopupView.findViewById(R.id.detailViewText);
		patientID = (TextView) detailPopupView.findViewById(R.id.patient);
		testDate = (TextView) detailPopupView.findViewById(R.id.testdate);
		typeDetailText = (TextView) detailPopupView.findViewById(R.id.type);
		primary = (TextView) detailPopupView.findViewById(R.id.primary);
		range = (TextView) detailPopupView.findViewById(R.id.range);
		ref = (TextView) detailPopupView.findViewById(R.id.ref);
		testNo = (TextView) detailPopupView.findViewById(R.id.testno);
		operatorID = (TextView) detailPopupView.findViewById(R.id.operator);
		result = (TextView) detailPopupView.findViewById(R.id.result);
	}
	
	private void setText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.controldata);
	}
	
	public void setButtonId(Activity activity, View detailPopupView) {
		
		homeIcon = (Button)activity.findViewById(R.id.homeicon);
		backIcon = (Button)activity.findViewById(R.id.backicon);
		preViewBtn = (Button)activity.findViewById(R.id.previousviewbtn);
		detailViewBtn = (Button)activity.findViewById(R.id.detailviewbtn);
		nextViewBtn = (Button)activity.findViewById(R.id.nextviewbtn);
		printBtn = (Button)detailPopupView.findViewById(R.id.printbtn);
		cancleBtn = (Button)detailPopupView.findViewById(R.id.cancelbtn);
		exportBtn = (Button)activity.findViewById(R.id.exportbtn);
		snapshotBtn = (Button)activity.findViewById(R.id.snapshotBtn);
		snapshotBtn2 = (Button)detailPopupView.findViewById(R.id.snapshotBtn2);
	}
	
	public void setButtonClick() {
		
		homeIcon.setOnTouchListener(mTouchListener);
		backIcon.setOnTouchListener(mTouchListener);
		preViewBtn.setOnTouchListener(mTouchListener);
		detailViewBtn.setOnTouchListener(mTouchListener);
		nextViewBtn.setOnTouchListener(mTouchListener);
		printBtn.setOnTouchListener(mTouchListener);
		cancleBtn.setOnTouchListener(mTouchListener);
		exportBtn.setOnTouchListener(mTouchListener);
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
			
			snapshotBtn.setOnTouchListener(mTouchListener);
			snapshotBtn2.setOnTouchListener(mTouchListener);
		}
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	public void setDetailButtonState(int btnId, boolean state, View detailPopupView) {
		
		detailPopupView.findViewById(btnId).setEnabled(state);
	}

	public void setImageButtonId() {
		
		checkBoxBtn1 = (ImageButton) findViewById(R.id.chdckbox1);
		checkBoxBtn2 = (ImageButton) findViewById(R.id.chdckbox2);
		checkBoxBtn3 = (ImageButton) findViewById(R.id.chdckbox3);
		checkBoxBtn4 = (ImageButton) findViewById(R.id.chdckbox4);
		checkBoxBtn5 = (ImageButton) findViewById(R.id.chdckbox5);
	}
	
	public void setImageButtonClick() {
		
		checkBoxBtn1.setOnTouchListener(mImageTouchListener);
		checkBoxBtn2.setOnTouchListener(mImageTouchListener);
		checkBoxBtn3.setOnTouchListener(mImageTouchListener);
		checkBoxBtn4.setOnTouchListener(mImageTouchListener);
		checkBoxBtn5.setOnTouchListener(mImageTouchListener);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn(activity);
					
				switch(v.getId()) {
				
				case R.id.homeicon	:
					WhichIntent(activity, context, TargetIntent.Home);
					break;
				
				case R.id.backicon	:
					WhichIntent(activity, context, TargetIntent.Record);
					break;
				
				case R.id.previousviewbtn	:
					WhichIntent(activity, context, TargetIntent.PreFile);
					break;
					
				case R.id.detailviewbtn	:
					DisplayDetailView();
					break;
					
				case R.id.nextviewbtn	:
					WhichIntent(activity, context, TargetIntent.NextFile);
					break;
				
				case R.id.printbtn	:
					unenabledAllDetailBtn(detailPopupView);
					PrintRecordData();
					break;
				
				case R.id.cancelbtn	:
					unenabledAllDetailBtn(detailPopupView);
					detailPopup.dismiss();
					enabledAllDetailBtn(detailPopupView);
					enabledAllBtn(activity);
					break;
					
				case R.id.exportbtn	:
					WhichIntent(activity, context, TargetIntent.Export);
					break;
					
				case R.id.snapshotBtn		:
					WhichIntent(activity, context, TargetIntent.SnapShot);
					break;
					
				case R.id.snapshotBtn2		:
					CaptureScreen mCaptureScreen = new CaptureScreen();
					byte[] bitmapBytes = mCaptureScreen.captureScreen(activity, detailPopupView);
					
					WhichIntentforSnapshot(activity, context, bitmapBytes);
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllBtn(Activity activity) {

		setButtonState(R.id.homeicon, true, activity);
		setButtonState(R.id.backicon, true, activity);
		setButtonState(R.id.previousviewbtn, true, activity);
		setButtonState(R.id.detailviewbtn, true, activity);
		setButtonState(R.id.nextviewbtn, true, activity);
		setButtonState(R.id.exportbtn, true, activity);
	}
	
	public void unenabledAllBtn(Activity activity) {

		setButtonState(R.id.homeicon, false, activity);
		setButtonState(R.id.backicon, false, activity);
		setButtonState(R.id.previousviewbtn, false, activity);
		setButtonState(R.id.detailviewbtn, false, activity);
		setButtonState(R.id.nextviewbtn, false, activity);
		setButtonState(R.id.exportbtn, false, activity);
	}
	
	public void enabledAllDetailBtn(View detailPopupView) {

		setDetailButtonState(R.id.printbtn, true, detailPopupView);
		setDetailButtonState(R.id.cancelbtn, true, detailPopupView);
	}
	
	public void unenabledAllDetailBtn(View detailPopupView) {

		setDetailButtonState(R.id.printbtn, false, detailPopupView);
		setDetailButtonState(R.id.cancelbtn, false, detailPopupView);
	}
	
	ImageButton.OnTouchListener mImageTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
				
				case R.id.chdckbox1	:
					boxNum = 1;
					PressedCheckBox(checkBoxBtn1);
					break;
					
				case R.id.chdckbox2	:
					boxNum = 2;
					PressedCheckBox(checkBoxBtn2);
					break;
				
				case R.id.chdckbox3	:
					boxNum = 3;
					PressedCheckBox(checkBoxBtn3);
					break;
				
				case R.id.chdckbox4	:
					boxNum = 4;
					PressedCheckBox(checkBoxBtn4);
					break;
				
				case R.id.chdckbox5	:
					boxNum = 5;
					PressedCheckBox(checkBoxBtn5);
					break;
				}		
				
				break;
			}
			
			return false;
		}
	};
	
	public void ControlInit() {
		
		activity = this;
		context = this;
		
		setTextId();
		setText();
		setButtonId(activity, detailPopupView);
		setImageButtonId();
		
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);
		
		if(state != RunActivity.NORMAL_OPERATION) {
			
			mErrorPopup = new ErrorPopup(this, this, R.id.record2Layout, null, 0);
			mErrorPopup.ErrorBtnDisplay(state);
		}
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.record2Layout);
		
		ControlDisplay();
		
		SerialPort.Sleep(500);
		
		setButtonClick();
		setImageButtonClick();
	}
	
	public void GetItnData() { // getting the intent data
		
		if(HomeActivity.ANALYZER_SW != HomeActivity.DEMO) {
			
			Intent itn = getIntent();
			
			dateTime = itn.getStringArrayExtra("DateTime");
			testNum  = itn.getStringArrayExtra("TestNum");
			refNum   = itn.getStringArrayExtra("RefNumber");
			hbA1c    = itn.getStringArrayExtra("HbA1c");
			pID      = itn.getStringArrayExtra("PatientID");
			oID      = itn.getStringArrayExtra("OperatorID");
			priStr   = itn.getStringArrayExtra("Primary");
			typeStr  = itn.getStringArrayExtra("Type");
		
		} else {
			
			dateTime[0] = "20150305AM0900";
			testNum [0] = "0001";
			refNum  [0] = "DBANA";
			hbA1c   [0] = "5.5";
			pID     [0] = "Patient";
			oID     [0] = "Operator";
			priStr  [0] = "0";
			typeStr	[0] = "W";
		}
	}
		
	public void ControlText() { // textview activation
		
		TestNumText [0] = (TextView) findViewById(R.id.testNum1);
		TypeText    [0] = (TextView) findViewById(R.id.type1);
		ResultText  [0] = (TextView) findViewById(R.id.result1);
		UnitText    [0] = (TextView) findViewById(R.id.unit1);
		DateTimeText[0] = (TextView) findViewById(R.id.dateTime1);
		
		TestNumText [1] = (TextView) findViewById(R.id.testNum2);
		TypeText    [1] = (TextView) findViewById(R.id.type2);
		ResultText  [1] = (TextView) findViewById(R.id.result2);
		UnitText    [1] = (TextView) findViewById(R.id.unit2);
		DateTimeText[1] = (TextView) findViewById(R.id.dateTime2);
		
		TestNumText [2] = (TextView) findViewById(R.id.testNum3);
		TypeText    [2] = (TextView) findViewById(R.id.type3);
		ResultText  [2] = (TextView) findViewById(R.id.result3);
		UnitText    [2] = (TextView) findViewById(R.id.unit3);
		DateTimeText[2] = (TextView) findViewById(R.id.dateTime3);
		
		TestNumText [3] = (TextView) findViewById(R.id.testNum4);
		TypeText    [3] = (TextView) findViewById(R.id.type4);
		ResultText  [3] = (TextView) findViewById(R.id.result4);
		UnitText    [3] = (TextView) findViewById(R.id.unit4);
		DateTimeText[3] = (TextView) findViewById(R.id.dateTime4);
		
		TestNumText [4] = (TextView) findViewById(R.id.testNum5);
		TypeText    [4] = (TextView) findViewById(R.id.type5);
		ResultText  [4] = (TextView) findViewById(R.id.result5);
		UnitText    [4] = (TextView) findViewById(R.id.unit5);
		DateTimeText[4] = (TextView) findViewById(R.id.dateTime5);
		
		pageText = (TextView) findViewById(R.id.pagetext);
	}
	
	public void ControlDisplay() { // displaying the patient data
			
		String type;
		
		GetItnData();
		ControlText();
		
		for(int i = 0; i < 5; i++) {
		
    		if(testNum[i] != null) {
    		
    			if(typeStr[i].equals("W") || typeStr[i].equals("X")) type = "Control HbA1c";
				else type = "Control ACR";
				
    			TestNumText [i].setText(testNum[i]);
    			TypeText    [i].setText(type);
    			ResultText  [i].setText(hbA1c[i]);
				if(priStr[i].equals("0")) UnitText[i].setText("%");
				else UnitText[i].setText("mmol/mol");				
            	DateTimeText[i].setText(dateTime[i].substring(0, 4) + "." + dateTime[i].substring(4, 6) + "." + dateTime[i].substring(6, 8) + "   " + dateTime[i].substring(10, 12) + ":" + dateTime[i].substring(12, 14) + " " + dateTime[i].substring(8, 10));	
    		}	
    	}
	
		PageDisplay();
	}
	
	public void PageDisplay() {
		
		int tPage = (RemoveActivity.ControlDataCnt+3)/5;
		if(tPage == 0) tPage = 1;
		String page = Integer.toString(RecordActivity.DataPage+1) + " / " + Integer.toString(tPage);
		
		pageText.setText(page);
	}
	
	public void PressedCheckBox(ImageButton box) { // displaying the button pressed
		
		if(!checkFlag) { // whether or not box is checked

			checkFlag = true;
			box.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
			
		} else {

			whichBox.setBackgroundResource(R.drawable.checkbox); // changing to not checked box

			if(whichBox == box) {
				
				checkFlag = false;
				
			} else {
				
				box.setBackgroundResource(R.drawable.checkbox_s);
			}
		}
		
		whichBox = box;
	}
	
	public void DisplayDetailView() { // displaying the detail patient data

		String pri, unit, ran, type, tempPid, pid, tempTestDate;
		
		if(checkFlag && testNum[boxNum - 1] != null) {
				
			detailViewText.setPaintFlags(detailViewText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
			detailViewText.setTextScaleX(0.9f);
			detailViewText.setLetterSpacing(3);
			detailViewText.setText(R.string.detailview);
			
			if(typeStr[boxNum - 1].equals("W") || typeStr[boxNum - 1].equals("X")) type = "Control HbA1c";
			else type = "Control ACR";
			
			tempPid = pID[boxNum - 1];
			if(tempPid.length() > 10) pid = tempPid.substring(0, 10) + " \n" + tempPid.substring(10);
			else pid = tempPid;
			
			tempTestDate = dateTime[boxNum - 1];
			
			patientID.setText(pid);
			testDate.setText(tempTestDate.substring(2, 4) + "." + tempTestDate.substring(4, 6) + "." + tempTestDate.substring(6, 8) + "   " + tempTestDate.substring(10, 12) + ":" + tempTestDate.substring(12, 14) + " " + tempTestDate.substring(8, 10));
			typeDetailText.setText(type);
			
			if(priStr[boxNum - 1].equals("0")) {
				
				pri = "NGSP";
				unit = " %";
				ran = "4.0 - 6.0 %";
				
			} else {
				
				pri = "IFCC";
				unit = " mmol/mol";
				ran = "20 - 42 mmol/mol";
			}
			
			primary.setText(pri);
			range.setText(ran);
			ref.setText(refNum[boxNum - 1]);
			testNo.setText(testNum[boxNum - 1]);
			operatorID.setText(oID[boxNum - 1]);
			result.setText(hbA1c[boxNum - 1] + unit);

			detailPopup.showAtLocation(record2Layout, Gravity.CENTER, 0, 0);
			detailPopup.setAnimationStyle(0);
		}
		
		enabledAllBtn(activity);
	}
		            		
	public void PrintRecordData() {
		
		DecimalFormat pIDLenDfm = new DecimalFormat("00");
		
		StringBuffer txData = new StringBuffer();
		
		txData.delete(0, txData.capacity());
		
		txData.append(dateTime[boxNum - 1].substring(0, 4));
		txData.append(dateTime[boxNum - 1].substring(4, 6));
		txData.append(dateTime[boxNum - 1].substring(6, 8));
		txData.append(dateTime[boxNum - 1].substring(8, 10));
		txData.append(dateTime[boxNum - 1].substring(10, 12));
		txData.append(dateTime[boxNum - 1].substring(12, 14));
		txData.append(testNum[boxNum - 1]);
		
		txData.append(typeStr[boxNum - 1]);		
		txData.append(refNum[boxNum - 1]);
		txData.append(pIDLenDfm.format(pID[boxNum - 1].length()));
		txData.append(pID[boxNum - 1]);
		txData.append(pIDLenDfm.format(oID[boxNum - 1].length()));
		txData.append(oID[boxNum - 1]);
		txData.append(priStr[boxNum - 1]);
		txData.append(hbA1c[boxNum - 1]);
		
		mSerialPort = new SerialPort();
		mSerialPort.PrinterTxStart(activity, context, SerialPort.PRINT_RECORD, txData);
		
		SerialPort.Sleep(100);
		
		enabledAllDetailBtn(detailPopupView);
	}
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home		:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			finish();
			break;
						
		case Record		:				
			Intent MemoryIntent = new Intent(getApplicationContext(), RecordActivity.class);
			startActivity(MemoryIntent);
			finish();
			break;
			
		case NextFile	:
			if((RemoveActivity.ControlDataCnt-2)/5 > RecordActivity.DataPage) {
			
				Intent NextFileIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
				NextFileIntent.putExtra("DataCnt", RemoveActivity.ControlDataCnt);
				NextFileIntent.putExtra("DataPage", ++RecordActivity.DataPage);
				NextFileIntent.putExtra("Type", (int) FileLoadActivity.CONTROL);
				NextFileIntent.putExtra("System Check State", RunActivity.NORMAL_OPERATION);
				startActivity(NextFileIntent);
				finish();
				
			} else enabledAllBtn(activity);
			break;
		
		case PreFile	:
			if(RecordActivity.DataPage > 0){
			
				Intent PreFileIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
				PreFileIntent.putExtra("DataCnt", RemoveActivity.ControlDataCnt);
				PreFileIntent.putExtra("DataPage", --RecordActivity.DataPage);
				PreFileIntent.putExtra("Type", (int) FileLoadActivity.CONTROL);
				PreFileIntent.putExtra("System Check State", RunActivity.NORMAL_OPERATION);
				startActivity(PreFileIntent);
				finish();
				
			} else enabledAllBtn(activity);
			break;
			
		case SnapShot	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			Intent nextIntent = new Intent(context, FileSaveActivity.class);
			nextIntent.putExtra("snapshot", true);
			nextIntent.putExtra("datetime", TimerDisplay.rTime);
			nextIntent.putExtra("bitmap", bitmapBytes);
			startActivity(nextIntent);
			finish();
			break;
			
		case Export	:
			if(TimerDisplay.ExternalDeviceBarcode == TimerDisplay.FILE_USB_OPEN) {
				
				Intent exportIntent = new Intent(getApplicationContext(), ExportActivity.class);
				exportIntent.putExtra("HWSN", AboutModel.HWSN);
				exportIntent.putExtra("DataCnt", RemoveActivity.ControlDataCnt);
				exportIntent.putExtra("DataPage", RecordActivity.DataPage);
				exportIntent.putExtra("Type", (int) FileLoadActivity.CONTROL);
				exportIntent.putExtra("System Check State", RunActivity.NORMAL_OPERATION);
				startActivity(exportIntent);
				finish();
				
			} else enabledAllBtn(activity);
			break;
		
		default		:	
			break;			
		}
	}

	public void WhichIntentforSnapshot(Activity activity, Context context, byte[] bitmapBytes) {
		
		Intent nextIntent = null;
		
		nextIntent = new Intent(context, FileSaveActivity.class);
		nextIntent.putExtra("snapshot", true);
		nextIntent.putExtra("datetime", TimerDisplay.rTime);
		nextIntent.putExtra("bitmap", bitmapBytes);
		
		activity.startActivity(nextIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}