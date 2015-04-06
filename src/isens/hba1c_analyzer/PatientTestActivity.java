package isens.hba1c_analyzer;

import java.text.DecimalFormat;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
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

public class PatientTestActivity extends Activity {
	
	public SerialPort mSerialPort;
	public TimerDisplay mTimerDisplay;
	
	public RelativeLayout pTestLayout;
	public View detailPopupView;
	public PopupWindow detailPopup;
	
	private TextView TestNumText [] = new TextView[5],
					 TypeText    [] = new TextView[5],
					 ResultText  [] = new TextView[5],
					 UnitText    [] = new TextView[5],
					 DateTimeText[] = new TextView[5],
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
	
	public Button homeIcon,
				  backIcon,
				  detailViewBtn,
				  nextViewBtn,
				  preViewBtn,
				  printBtn,
				  cancleBtn,
				  exportBtn;
	
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
	
	private boolean checkFlag = false,
					btnState = false;
	
	private ImageButton whichBox = null;
	
	private int boxNum = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.patienttest);
		
		/* Popup window activation */
		pTestLayout = (RelativeLayout)findViewById(R.id.ptestlayout);
		detailPopupView = View.inflate(this, R.layout.detailviewpopup, null);
		detailPopup = new PopupWindow(detailPopupView, 526, 264, true);
		
		PatientInit();
	}	
	
	public void setTextId() {
	
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
	
	public void setButtonId() {
		
		homeIcon = (Button)findViewById(R.id.homeicon);
		backIcon = (Button)findViewById(R.id.backicon);
		preViewBtn = (Button)findViewById(R.id.previousviewbtn);
		detailViewBtn = (Button)findViewById(R.id.detailviewbtn);
		nextViewBtn = (Button)findViewById(R.id.nextviewbtn);
		printBtn = (Button)detailPopupView.findViewById(R.id.printbtn);
		cancleBtn = (Button)detailPopupView.findViewById(R.id.canclebtn);
		exportBtn = (Button)findViewById(R.id.exportbtn);
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
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
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
				
				if(!btnState) {

					btnState = true;

					switch(v.getId()) {
				
					case R.id.homeicon	:
						WhichIntent(TargetIntent.Home);
						break;
					
					case R.id.backicon	:
						WhichIntent(TargetIntent.Record);
						break;
					
					case R.id.previousviewbtn	:
						WhichIntent(TargetIntent.PreFile);
						btnState = false;
						break;
						
					case R.id.detailviewbtn	:
						DisplayDetailView();
						cancleBtn.setEnabled(true);
						break;
						
					case R.id.nextviewbtn	:
						WhichIntent(TargetIntent.NextFile);
						btnState = false;
						break;
					
					case R.id.printbtn	:
						PrintRecordData();
						break;
					
					case R.id.canclebtn	:
						cancleBtn.setEnabled(false);
						detailPopup.dismiss();
						detailViewBtn.setEnabled(true);
						btnState = false;
						break;
						
					case R.id.exportbtn	:
						btnState = false;
						break;
						
					default	:
						break;
					}
					
					break;
				}
			}
			
			return false;
		}
	};
	
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
	
	public void enabledAllBtn() {

		setButtonState(R.id.homeicon, true);
	}
	
	public void unenabledAllBtn() {

		setButtonState(R.id.homeicon, false);
		
		btnState = false;
	}
	
	public void PatientInit() {

		setTextId();
		setButtonId();
		setButtonClick();
		setImageButtonId();
		setImageButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.ptestlayout);
		
		PatientDisplay();
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
		
		} else {
			
			dateTime[0] = "20150305AM0900";
			testNum [0] = "0001";
			refNum  [0] = "DBANA";
			hbA1c   [0] = "5.5";
			pID     [0] = "Patient";
			oID     [0] = "Operator";
			priStr  [0] = "0";
		}
//		Log.w("GetItnData", "Cartridge Lot : " + refNum[0] + " HbA1c : " + hbA1c[0]);
	}
	
	public void PatientText() { // textview activation
		
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
	
	public void PatientDisplay() { // displaying the patient data
			
		String hbA1cValue;
		
		GetItnData();
		PatientText();
		
		for(int i = 0; i < 5; i++) {
			
			if(testNum[i] != null) {
			
				TestNumText [i].setText(testNum[i]);
				typeStr     [i] = "HbA1c";
				TypeText    [i].setText(typeStr[i]);
				ResultText  [i].setText(hbA1c[i]);
				if(priStr[i].equals("0")) UnitText[i].setText("%");
				else UnitText[i].setText("mmol/mol");				
	        	DateTimeText[i].setText(dateTime[i].substring(0, 4) + "." + dateTime[i].substring(4, 6) + "." + dateTime[i].substring(6, 8) + " " + dateTime[i].substring(8, 10) + " " + dateTime[i].substring(10, 12) + ":" + dateTime[i].substring(12, 14));	
			}	
		}

		PageDisplay();
	}
	
	public void PageDisplay() {
		
		int tPage = (RemoveActivity.PatientDataCnt+3)/5;
		Log.w("PageDisplay", ""+ RemoveActivity.PatientDataCnt);
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

		String pri,
			   unit,
			   ran;
		
		if(checkFlag && testNum[boxNum - 1] != null) {
			
			patientID.setText(pID[boxNum - 1]);
			testDate.setText(dateTime[boxNum - 1].substring(2, 4) + "." + dateTime[boxNum - 1].substring(4, 6) + "." + dateTime[boxNum - 1].substring(6, 8) + " " + dateTime[boxNum - 1].substring(8, 10) + " " + dateTime[boxNum - 1].substring(10, 12) + ":" + dateTime[boxNum - 1].substring(12, 14));
			typeDetailText.setText(typeStr[boxNum - 1]);
			
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
			
			detailViewBtn.setEnabled(false);
			detailPopup.showAtLocation(pTestLayout, Gravity.CENTER, 0, 0);
			detailPopup.setAnimationStyle(0);
		}
		
		btnState = false;
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
		txData.append(refNum[boxNum - 1]);
		txData.append(pIDLenDfm.format(pID[boxNum - 1].length()));
		txData.append(pID[boxNum - 1]);
		txData.append(pIDLenDfm.format(oID[boxNum - 1].length()));
		txData.append(oID[boxNum - 1]);
		txData.append(priStr[boxNum - 1]);
		txData.append(hbA1c[boxNum - 1]);
		
		mSerialPort = new SerialPort();
		mSerialPort.PrinterTxStart(SerialPort.PRINTRECORD, txData);
		
		SerialPort.Sleep(100);
		
		btnState = false;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
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
			Log.w("Which Intent", "Patient : " + RemoveActivity.PatientDataCnt + "Data Page : " + RecordActivity.DataPage);
			if((RemoveActivity.PatientDataCnt-2)/5 > RecordActivity.DataPage) {
			
				Intent NextFileIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
				NextFileIntent.putExtra("DataCnt", RemoveActivity.PatientDataCnt);
				NextFileIntent.putExtra("DataPage", ++RecordActivity.DataPage);
				NextFileIntent.putExtra("Type", (int) FileLoadActivity.PATIENT);
				startActivity(NextFileIntent);
				finish();
			} else nextViewBtn.setEnabled(true);
			break;
		
		case PreFile	:
			if(RecordActivity.DataPage > 0){
				Log.w("Which Intent", "Patient : " + RemoveActivity.PatientDataCnt + "Data Page : " + RecordActivity.DataPage);
				
				Intent PreFileIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
				PreFileIntent.putExtra("DataCnt", RemoveActivity.PatientDataCnt);
				PreFileIntent.putExtra("DataPage", --RecordActivity.DataPage);
				PreFileIntent.putExtra("Type", (int) FileLoadActivity.PATIENT);
				startActivity(PreFileIntent);
				finish();
			} else preViewBtn.setEnabled(true);
			break;
			
		default		:	
			break;			
		}
		
		btnState = false;
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}