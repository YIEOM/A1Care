package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.FileSystem;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.Model.LocationModel;
import isens.hba1c_analyzer.Model.SoundModel;
import isens.hba1c_analyzer.Model.TemperatureModel;
import isens.hba1c_analyzer.View.ConvertActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {
		
	final static byte ACTION_ACTIVITY  = 1,
					  HOME_ACTIVITY    = 2,
					  COVER_ACTION_ESC = 3;
	
	private SerialPort mSerialPort;
	private ErrorPopup mErrorPopup;
	private TimerDisplay mTimerDisplay;
	private DatabaseHander mDatabaseHander;
	private RunActivity mRunActivity;
	private SoundModel mSoundModel;
	private LanguageModel mLanguageModel;
	private FileSystem mFileSystem;
	
	private Activity activity;
	private Context context;
	
	public static EditText PatientIDText;
	
	private TextView hbA1cText,
					 hbA1cUnitText,
					 dateText,
					 amPmText,
					 refText,
					 primaryText,
					 rangeText,
					 unitText;
	
	private Button homeBtn,
				   homeBtn2,
				   printBtn,
				   nextSampleBtn,
				   convertBtn,
				   snapshotBtn;

	private ArrayList<String> dateTimeStrList = new ArrayList<String>();

	public static int CheckError;
	
	private int dataCnt;
	
	private String hbA1cCurr,
				   unitCurr,
				   rangeCurr,
				   primaryCurr;
	
	private byte primaryByte;
	
	private String operator,
				   sampleType;
	
	private int languageIdx;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.result);
		
		PatientIDText = (EditText) findViewById(R.id.patientidtext);
		
		ResultInit();
	}
	
	private void setTextId() {
		
		hbA1cText = (TextView) findViewById(R.id.hba1cPct);
		hbA1cUnitText = (TextView) findViewById(R.id.hba1cUnit);
		dateText = (TextView) findViewById(R.id.testdate1);
		amPmText = (TextView) findViewById(R.id.testdate2);
		refText = (TextView) findViewById(R.id.ref);
		primaryText = (TextView) findViewById(R.id.primary);
		rangeText = (TextView) findViewById(R.id.range);
		unitText = (TextView) findViewById(R.id.unit);
	}
	
	private void setHbA1cTextSize() {
		
		switch(languageIdx) {
		
		case LanguageModel.KO	:
			hbA1cText.setTextSize(85.0f);
			break;
			
		case LanguageModel.EN	:
			hbA1cText.setTextSize(85.0f);
			break;
			
		case LanguageModel.ZH:
			hbA1cText.setTextSize(65.0f);
			break;
			
		case LanguageModel.JA	:
			hbA1cText.setTextSize(85.0f);
			break;
			
		default	:
			hbA1cText.setTextSize(85.0f);
			break;
		}		
	}
	
	private void setHbA1cUnitTextSize(byte primary) {
		
		if(primary == ConvertModel.NGSP) {
		
			switch(languageIdx) {
			
			case LanguageModel.KO	:
				hbA1cUnitText.setTextSize(85.0f);
				break;
				
			case LanguageModel.EN	:
				hbA1cUnitText.setTextSize(85.0f);
				break;
				
			case LanguageModel.ZH:
				hbA1cUnitText.setTextSize(65.0f);
				break;
				
			case LanguageModel.JA	:
				hbA1cUnitText.setTextSize(85.0f);
				break;
				
			default	:
				hbA1cUnitText.setTextSize(85.0f);
				break;
			}		
		
		} else {
			
			switch(languageIdx) {
			
			case LanguageModel.KO	:
				hbA1cUnitText.setTextSize(24.0f);
				break;
				
			case LanguageModel.EN	:
				hbA1cUnitText.setTextSize(24.0f);
				break;
				
			case LanguageModel.ZH:
				hbA1cUnitText.setTextSize(24.0f);
				break;
				
			case LanguageModel.JA	:
				hbA1cUnitText.setTextSize(24.0f);
				break;
				
			default	:
				hbA1cUnitText.setTextSize(24.0f);
				break;
			}
		}
	}
	
	public void setButtonId(Activity activity) {
		
		homeBtn = (Button)activity.findViewById(R.id.homeBtn);
		homeBtn2 = (Button)activity.findViewById(R.id.homeBtn2);
		printBtn = (Button)activity.findViewById(R.id.printbtn);
		nextSampleBtn = (Button)activity.findViewById(R.id.nextsamplebtn);
		convertBtn = (Button)activity.findViewById(R.id.convertbtn);
		snapshotBtn = (Button)activity.findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		homeBtn.setOnTouchListener(mTouchListener);
		homeBtn2.setOnTouchListener(mTouchListener);
		printBtn.setOnTouchListener(mTouchListener);
		nextSampleBtn.setOnTouchListener(mTouchListener);
		convertBtn.setOnTouchListener(mTouchListener);
		if(HomeActivity.ANALYZER_SW == RunActivity.DEVEL_OPERATION) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn(activity); //0624
				
				switch(v.getId()) {
			
				case R.id.homeBtn		:
				case R.id.homeBtn2		:
					WhichIntent(activity, context, TargetIntent.Home);
					break;
					
				case R.id.printbtn		:
					PrintResultData();
					break;
				
				case R.id.nextsamplebtn	:
					WhichIntent(activity, context, TargetIntent.Run);
					break;
				
				case R.id.convertbtn	:
					SerialPort.Sleep(200);
					PrimaryConvert();
					break;
					
				case R.id.snapshotBtn	:
					WhichIntent(activity, context, TargetIntent.SnapShot);
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllBtn(Activity activtiy) {

		setButtonState(R.id.homeBtn, true, activtiy);
		setButtonState(R.id.homeBtn2, true, activtiy);
		setButtonState(R.id.printbtn, true, activtiy);
		setButtonState(R.id.nextsamplebtn, true, activtiy);
		setButtonState(R.id.convertbtn, true, activtiy);
	}
	
	public void unenabledAllBtn(Activity activtiy) {
		
		setButtonState(R.id.homeBtn, false, activtiy);
		setButtonState(R.id.homeBtn2, false, activtiy);
		setButtonState(R.id.printbtn, false, activtiy);
		setButtonState(R.id.nextsamplebtn, false, activtiy);
		setButtonState(R.id.convertbtn, false, activtiy);
	}

	public void ResultInit() {

		activity = this;
		context = this;

		Intent itn = getIntent();
		mRunActivity = new RunActivity();
		mDatabaseHander = new DatabaseHander(activity);
		mLanguageModel = new LanguageModel(activity);
		mSoundModel = new SoundModel(activity, context);
		mErrorPopup = new ErrorPopup(this, this, R.id.resultlayout, null, 0);

		setTextId();
		languageIdx = mLanguageModel.getSettingLanguage();
		setHbA1cTextSize();
		setButtonId(activity);
		setButtonClick();

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.resultlayout);

		CheckError = itn.getIntExtra("RunState", 0);
		dateTimeStrList = itn.getStringArrayListExtra("DateTime");
		dataCnt = itn.getIntExtra("DataCnt", 1);

		getSampleType();

		operator = mDatabaseHander.GetLastLoginID();

		if(CheckError == RunActivity.NORMAL_OPERATION || CheckError == RunActivity.DEMO_OPERATION) {

			if(HomeActivity.ANALYZER_SW == RunActivity.DEMO_OPERATION) setDemoResult();

			primaryByte = ConvertModel.Primary;

			UnitConvert(mRunActivity.ConvertHbA1c(ConvertModel.Primary), ConvertModel.Primary);
			HbA1cDisplay();

			mSoundModel.playSound(R.raw.result_bgm);

		} else if(CheckError == R.string.stop) {

			hbA1cText.setText(sampleType + " = " + getString(CheckError));

			mSoundModel.playSound(R.raw.result_bgm);

		} else {

			hbA1cText.setText(sampleType + " = " + getString(CheckError));

			mErrorPopup.ErrorBtnDisplay(CheckError);
		}

		dateText.setText(dateTimeStrList.get(0) + "." + dateTimeStrList.get(1) + "." + dateTimeStrList.get(2) + "   " + dateTimeStrList.get(4) + ":" + dateTimeStrList.get(5));
		amPmText.setText(dateTimeStrList.get(3));
		refText.setText(Barcode.RefNum);
	}

	private void setDemoResult() {

		int rem;

		rem = (int) ((Math.random() * 10) + 1) % 3;

		switch(rem) {

			case 0	:
				RunActivity.HbA1cValue = 5.5;
				break;

			case 1	:
				RunActivity.HbA1cValue = 6.7;
				break;

			case 2	:
				RunActivity.HbA1cValue = 8.3;
				break;

			default	:
				break;
		}

		ConvertModel.Primary = ConvertModel.NGSP;
	}

	public void PatientIDDisplay(final StringBuffer str) {
		
		new Thread(new Runnable() {
		    public void run() {
				runOnUiThread(new Runnable() {
		            public void run() {

		        	   	PatientIDText.setText(str.substring(0, str.length() - 1));
		    		}
				});
		    }
		}).start();
	}
	
	public void PrintResultData() {
		
		if(CheckError == RunActivity.NORMAL_OPERATION) {

			StringBuffer txData = new StringBuffer();
			DecimalFormat dfm = new DecimalFormat("0000"),
						  pIDLenDfm = new DecimalFormat("00");

			int tempDataCnt = dataCnt % 9999;
			if(tempDataCnt == 0) tempDataCnt = 9999; 
			
			txData.delete(0, txData.capacity());
			
			txData.append(dateTimeStrList.get(0));
			txData.append(dateTimeStrList.get(1));
			txData.append(dateTimeStrList.get(2));
			txData.append(dateTimeStrList.get(3));
			txData.append(dateTimeStrList.get(4));
			txData.append(dateTimeStrList.get(5));
			txData.append(dfm.format(tempDataCnt));
			txData.append(Barcode.Type);
			txData.append(Barcode.RefNum);
			txData.append(pIDLenDfm.format(PatientIDText.getText().toString().length()));
			txData.append(PatientIDText.getText().toString());
			txData.append(pIDLenDfm.format(operator.length()));
			txData.append(operator);
			txData.append(Integer.toString((int) primaryByte)); // primary
			txData.append(hbA1cCurr);
			
			mSerialPort = new SerialPort();
			mSerialPort.PrinterTxStart(activity, context, SerialPort.PRINT_RESULT, txData);
			
			SerialPort.Sleep(100);
		
		} else if(CheckError == RunActivity.DEMO_OPERATION) {
			
			StringBuffer txData = new StringBuffer();
			
			txData.delete(0, txData.capacity());
			
			txData.append("2015");
			txData.append("03");
			txData.append("05");
			txData.append("AM");
			txData.append("09");
			txData.append("30");
			txData.append("0003");
			txData.append("D");
			txData.append("DBANAA");
			txData.append("07");
			txData.append("Patient");
			txData.append("08");
			txData.append("Operator");
			txData.append("0"); // primary
			txData.append("8.3");
			
			mSerialPort = new SerialPort();
			mSerialPort.PrinterTxStart(activity, context, SerialPort.PRINT_RESULT, txData);
			
			SerialPort.Sleep(100);
		}

		enabledAllBtn(activity); //0624	
	}
	
	public void UnitConvert(double hbA1cValue, byte primary) {
		
		DecimalFormat hbA1cFormat = new DecimalFormat("0.0");
		
		hbA1cCurr = hbA1cFormat.format(hbA1cValue);
		
		if(primary == ConvertModel.NGSP) {
			
		 	unitCurr = "%";
			if(sampleType.equals(context.getResources().getString(R.string.hba1c))) rangeCurr = "4.0 - 6.0";
			else rangeCurr = "-";
			primaryCurr = "NGSP";
			
		} else {
			
			unitCurr = "mmol/mol";
			if(sampleType.equals(context.getResources().getString(R.string.hba1c))) rangeCurr = "20 - 42";
			else rangeCurr = "-";
			primaryCurr = "IFCC";
		}
		
		setHbA1cUnitTextSize(primary);
	}
	
	public void HbA1cDisplay() {
		
		int color;
		
		if(HomeActivity.MEASURE_MODE == HomeActivity.A1C) color = Color.parseColor("#1F3E6F"); 
		else {
			
			if(getQCResult()) color = Color.parseColor("#04A458");
			else color = Color.parseColor("#E92A2E");
		}
		
		hbA1cText.setTextColor(color);
		hbA1cText.setText(sampleType + " = " + hbA1cCurr);
		hbA1cUnitText.setTextColor(color);
		hbA1cUnitText.setText(unitCurr);
		primaryText.setText(primaryCurr);
		rangeText.setText(rangeCurr);
		unitText.setText(unitCurr);
		
		enabledAllBtn(activity);
	}
	
	private void getSampleType() {
		
		if(Barcode.Type.equals("D") || Barcode.Type.equals("W") || Barcode.Type.equals("X")) sampleType = context.getResources().getString(R.string.hba1c);
		else sampleType = context.getResources().getString(R.string.acr);
	}
	
	private boolean getQCResult() {
		
		double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
		
		if(sampleType.equals("HbA1c")) {
			
			if(Barcode.Type.equals("W")) {
				
				max = Barcode.NorMean + 0.7;
				min = Barcode.NorMean - 0.7;
				
			} else {
				
				max = Barcode.AbnorMean + 1.3;
				min = Barcode.AbnorMean - 1.3;
			}
			
		} else {
			
			if(Barcode.Type.equals("Y")) {
				
				max = Barcode.NorMean + 0;
				min = Barcode.NorMean - 0;
				
			} else {
				
				max = Barcode.AbnorMean + 0;
				min = Barcode.AbnorMean - 0;
			}
		}
		
		if((min <= RunActivity.HbA1cValue) && (RunActivity.HbA1cValue <= max)) return true;
		
		return false;
	}
	
	public void PrimaryConvert() {
		
		if(CheckError == RunActivity.NORMAL_OPERATION || CheckError == RunActivity.DEMO_OPERATION) {
			
			double hbA1cValue;
			
			if(primaryByte == ConvertModel.NGSP) { // to IFCC
					
				primaryByte	= ConvertModel.IFCC;
				hbA1cValue = mRunActivity.ConvertHbA1c(ConvertModel.IFCC);
				UnitConvert(hbA1cValue, primaryByte);
			
			} else {
				
				primaryByte	= ConvertModel.NGSP;
				hbA1cValue = mRunActivity.ConvertHbA1c(ConvertModel.NGSP); 
				UnitConvert(hbA1cValue, primaryByte);
			}
		
			HbA1cDisplay();
		
		} else enabledAllBtn(activity); //0624
	}

	private void saveData(String patientIDLen, String patientID) {

		mFileSystem = new FileSystem(activity);
		mFileSystem.setPreferences("Measurement Data", MODE_PRIVATE);
		mFileSystem.putStringPref("PatientIDLen", patientIDLen);
		mFileSystem.putStringPref("PatientID", patientID);
		mFileSystem.commitPref();
	}

	private ArrayList<String> getStrData(String patientIDLen, String patientID) {

		DecimalFormat iDLenDfm = new DecimalFormat("00");

		ArrayList<String> dataStrArrayList = new ArrayList<String>();

		dataStrArrayList.add(0, dateTimeStrList.get(0));
		dataStrArrayList.add(1, dateTimeStrList.get(1));
		dataStrArrayList.add(2, dateTimeStrList.get(2));
		dataStrArrayList.add(3, dateTimeStrList.get(3));
		dataStrArrayList.add(4, dateTimeStrList.get(4));
		dataStrArrayList.add(5, dateTimeStrList.get(5));
		dataStrArrayList.add(6, Barcode.Type);
		dataStrArrayList.add(7, Barcode.RefNum);
		dataStrArrayList.add(8, patientIDLen);
		dataStrArrayList.add(9, patientID);
		dataStrArrayList.add(10, iDLenDfm.format(operator.length()));
		dataStrArrayList.add(11, operator);
		dataStrArrayList.add(12, mRunActivity.getPrimaryCode(CheckError));
		dataStrArrayList.add(13, hbA1cCurr);

		return dataStrArrayList;
	}

	private ArrayList<Integer> getIntData() {

		ArrayList<Integer> dataIntArrayList = new ArrayList<Integer>();

		dataIntArrayList.add(0, CheckError);
		dataIntArrayList.add(1, dataCnt);

		return dataIntArrayList;
	}

	private ArrayList<String> getStrHistoryData() {

		DecimalFormat photoDfm = new DecimalFormat("0.0"),
				      absorbDfm = new DecimalFormat("0.0000");

		ArrayList<String> dataStrArrayList = new ArrayList<String>();

		dataStrArrayList.add(0, photoDfm.format(BlankActivity.ChamberTmp));
		dataStrArrayList.add(1, photoDfm.format(RunActivity.BlankValue[0]));
		dataStrArrayList.add(2, photoDfm.format(RunActivity.BlankValue[1]));
		dataStrArrayList.add(3, photoDfm.format(RunActivity.BlankValue[2]));
		dataStrArrayList.add(4, photoDfm.format(RunActivity.BlankValue[3]));
		dataStrArrayList.add(5, absorbDfm.format(RunActivity.Step1stAbsorb1[0]));
		dataStrArrayList.add(6, absorbDfm.format(RunActivity.Step1stAbsorb1[1]));
		dataStrArrayList.add(7, absorbDfm.format(RunActivity.Step1stAbsorb1[2]));
		dataStrArrayList.add(8, absorbDfm.format(RunActivity.Step1stAbsorb2[0]));
		dataStrArrayList.add(9, absorbDfm.format(RunActivity.Step1stAbsorb2[1]));
		dataStrArrayList.add(10, absorbDfm.format(RunActivity.Step1stAbsorb2[2]));
		dataStrArrayList.add(11, absorbDfm.format(RunActivity.Step1stAbsorb3[0]));
		dataStrArrayList.add(12, absorbDfm.format(RunActivity.Step1stAbsorb3[1]));
		dataStrArrayList.add(13, absorbDfm.format(RunActivity.Step1stAbsorb3[2]));
		dataStrArrayList.add(14, absorbDfm.format(RunActivity.Step2ndAbsorb1[0]));
		dataStrArrayList.add(15, absorbDfm.format(RunActivity.Step2ndAbsorb1[1]));
		dataStrArrayList.add(16, absorbDfm.format(RunActivity.Step2ndAbsorb1[2]));
		dataStrArrayList.add(17, absorbDfm.format(RunActivity.Step2ndAbsorb2[0]));
		dataStrArrayList.add(18, absorbDfm.format(RunActivity.Step2ndAbsorb2[1]));
		dataStrArrayList.add(19, absorbDfm.format(RunActivity.Step2ndAbsorb2[2]));
		dataStrArrayList.add(20, absorbDfm.format(RunActivity.Step2ndAbsorb3[0]));
		dataStrArrayList.add(21, absorbDfm.format(RunActivity.Step2ndAbsorb3[1]));
		dataStrArrayList.add(22, absorbDfm.format(RunActivity.Step2ndAbsorb3[2]));
		dataStrArrayList.add(23, AboutModel.HWSN);
		dataStrArrayList.add(24, AboutModel.SWVersion + "(" + Character.valueOf(LocationModel.LocationCode) + (TemperatureModel.TmpSensorCode-64) + ")");
		dataStrArrayList.add(25, AboutModel.FWVersion);
		dataStrArrayList.add(26, AboutModel.OSVersion);

		return dataStrArrayList;
	}

	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion after intent data deliver

		Intent nextIntent = null;

		if(Itn != TargetIntent.SnapShot) {

			nextIntent = new Intent(getApplicationContext(), FileSaveActivity.class);
			DecimalFormat pIDLenDfm = new DecimalFormat("00");

			String patientIDLen = pIDLenDfm.format(PatientIDText.getText().toString().length()),
				   patientID = PatientIDText.getText().toString();

			saveData(patientIDLen, patientID);

			if(CheckError == RunActivity.NORMAL_OPERATION) UnitConvert(mRunActivity.ConvertHbA1c(ConvertModel.Primary), ConvertModel.Primary);

			nextIntent.putExtra("MeasureStrData", getStrData(patientIDLen, patientID));
			nextIntent.putExtra("MeasureIntData", getIntData());
			nextIntent.putExtra("HistoryStrData", getStrHistoryData());

			switch(Itn) {

				case Home		:
					nextIntent.putExtra("WhichIntent", (int) HOME_ACTIVITY);
					break;

				case Run	:
					nextIntent.putExtra("WhichIntent", (int) ACTION_ACTIVITY);
					break;

				default			:
					break;
			}

			nextIntent.putExtra("snapshot", false);

		} else {

			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);

			nextIntent = new Intent(context, FileSaveActivity.class);
			nextIntent.putExtra("snapshot", true);
			nextIntent.putExtra("datetime", TimerDisplay.rTime);
			nextIntent.putExtra("bitmap", bitmapBytes);
		}

		startActivity(nextIntent);
		finish();
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
	}
}
