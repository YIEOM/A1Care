package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.Model.SoundModel;
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
					  COVER_ACTION_ESC = 3,
					  SCAN_ACTIVITY    = 4;
	
	private SerialPort mSerialPort;
	private ErrorPopup mErrorPopup;
	private TimerDisplay mTimerDisplay;
	private DatabaseHander mDatabaseHander;
	private RunActivity mRunActivity;
	private SoundModel mSoundModel;
	private LanguageModel mLanguageModel;
	
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
	
	private String getTime[] = new String[6];
	
	public static int ItnData;
	
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
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
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
		
		getSampleType();
		
		setTextId();
		mLanguageModel = new LanguageModel(activity);
		languageIdx = mLanguageModel.getSettingLanguage();
		setHbA1cTextSize();
		setButtonId(activity);
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.resultlayout);
		
		GetCurrTime();
		GetDataCnt();		
		
		Intent itn = getIntent();
		ItnData = itn.getIntExtra("RunState", 0);
		
		if(ItnData == RunActivity.NORMAL_OPERATION || ItnData == RunActivity.DEMO_OPERATION) {
				
			if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
				
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
			
			primaryByte = ConvertModel.Primary;
			
			mRunActivity = new RunActivity();
			UnitConvert(mRunActivity.ConvertHbA1c(ConvertModel.Primary), ConvertModel.Primary);
			HbA1cDisplay();
		
			mSoundModel = new SoundModel(this, this);
			mSoundModel.playSound(R.raw.result_bgm);
			
		} else if(ItnData == R.string.stop) { 
			
			hbA1cText.setText(sampleType + " = " + getString(ItnData));
			
			mSoundModel = new SoundModel(this, this);
			mSoundModel.playSound(R.raw.result_bgm);
			
		} else {
			
			hbA1cText.setText(sampleType + " = " + getString(ItnData));
			mErrorPopup = new ErrorPopup(this, this, R.id.resultlayout, null, 0);
			mErrorPopup.ErrorBtnDisplay(ItnData);
		}
		
		dateText.setText(TimerDisplay.rTime[0] + "." + TimerDisplay.rTime[1] + "." + TimerDisplay.rTime[2] + "   " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
		amPmText.setText(TimerDisplay.rTime[3]);
		refText.setText(Barcode.RefNum);
		
		mDatabaseHander = new DatabaseHander(this);
		operator = mDatabaseHander.GetLastLoginID();
		
		if(operator == null) operator = "Guest";
	}
	
	public void PatientIDDisplay(final StringBuffer str) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	PatientIDText.setText(str.substring(0, str.length() - 1));
		            }
		        });
		    }
		}).start();
	}
	
	public void GetCurrTime() { // getting the current date and time
		
		getTime[0] = TimerDisplay.rTime[0];
		getTime[1] = TimerDisplay.rTime[1];
		getTime[2] = TimerDisplay.rTime[2];
		getTime[3] = TimerDisplay.rTime[3];		
		getTime[4] = TimerDisplay.rTime[4];
		getTime[5] = TimerDisplay.rTime[5];			
	}
	
	public void GetDataCnt() {
		
		if(Barcode.Type.equals("W") || Barcode.Type.equals("X") || Barcode.Type.equals("Y") || Barcode.Type.equals("Z")) dataCnt = RemoveActivity.ControlDataCnt;
		else dataCnt = RemoveActivity.PatientDataCnt;
	}
	
	public void PrintResultData() {
		
		if(ItnData == RunActivity.NORMAL_OPERATION) {
			
			StringBuffer txData = new StringBuffer();
			DecimalFormat dfm = new DecimalFormat("0000"),
						  pIDLenDfm = new DecimalFormat("00");
			
			int tempDataCnt;
			
			tempDataCnt = dataCnt % 9999;
			if(tempDataCnt == 0) tempDataCnt = 9999; 
			
			txData.delete(0, txData.capacity());
			
			txData.append(getTime[0]);
			txData.append(getTime[1]);
			txData.append(getTime[2]);
			txData.append(getTime[3]);
			txData.append(getTime[4]);
			txData.append(getTime[5]);
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
		
		} else if(ItnData == RunActivity.DEMO_OPERATION) {
			
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
			txData.append("DBANA");
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
		
		if(ItnData == RunActivity.NORMAL_OPERATION || ItnData == RunActivity.DEMO_OPERATION) {
			
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
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion after intent data deliver
		
		Intent nextIntent = null;
		
		if(Itn != TargetIntent.SnapShot) {
			
			nextIntent = new Intent(getApplicationContext(), FileSaveActivity.class);
			DecimalFormat photoDfm = new DecimalFormat("0.0"),
						  absorbDfm = new DecimalFormat("0.0000"),
						  pIDLenDfm = new DecimalFormat("00");
			
			if(ItnData == RunActivity.NORMAL_OPERATION) {
				
				UnitConvert(mRunActivity.ConvertHbA1c(ConvertModel.Primary), ConvertModel.Primary);
				primaryByte = ConvertModel.Primary;
			
			} else primaryByte = 2;
			
			nextIntent.putExtra("RunState", ItnData);
			nextIntent.putExtra("Year", getTime[0]);
			nextIntent.putExtra("Month", getTime[1]);
			nextIntent.putExtra("Day", getTime[2]);
			nextIntent.putExtra("AmPm", getTime[3]);
			nextIntent.putExtra("Hour", getTime[4]);
			nextIntent.putExtra("Minute", getTime[5]);
			nextIntent.putExtra("DataCnt", dataCnt);
			nextIntent.putExtra("Type", Barcode.Type);
			nextIntent.putExtra("RefNumber", Barcode.RefNum);
			nextIntent.putExtra("PatientIDLen", pIDLenDfm.format(PatientIDText.getText().toString().length()));
			nextIntent.putExtra("PatientID", PatientIDText.getText().toString());
			nextIntent.putExtra("OperatorLen", pIDLenDfm.format(operator.length()));
			nextIntent.putExtra("Operator", operator);
			nextIntent.putExtra("Primary", Integer.toString((int) primaryByte)); // primary
			nextIntent.putExtra("Hba1cPct", hbA1cCurr);
			
			nextIntent.putExtra("Chamber Tmp", photoDfm.format(BlankActivity.ChamberTmp));
			nextIntent.putExtra("BlankVal0", photoDfm.format(RunActivity.BlankValue[0]));
			nextIntent.putExtra("BlankVal1", photoDfm.format(RunActivity.BlankValue[1]));
			nextIntent.putExtra("BlankVal2", photoDfm.format(RunActivity.BlankValue[2]));
			nextIntent.putExtra("BlankVal3", photoDfm.format(RunActivity.BlankValue[3]));
			nextIntent.putExtra("St1Abs1by0", absorbDfm.format(RunActivity.Step1stAbsorb1[0]));
			nextIntent.putExtra("St1Abs1by1", absorbDfm.format(RunActivity.Step1stAbsorb1[1]));
			nextIntent.putExtra("St1Abs1by2", absorbDfm.format(RunActivity.Step1stAbsorb1[2]));
			nextIntent.putExtra("St1Abs2by0", absorbDfm.format(RunActivity.Step1stAbsorb2[0]));
			nextIntent.putExtra("St1Abs2by1", absorbDfm.format(RunActivity.Step1stAbsorb2[1]));
			nextIntent.putExtra("St1Abs2by2", absorbDfm.format(RunActivity.Step1stAbsorb2[2]));
			nextIntent.putExtra("St1Abs3by0", absorbDfm.format(RunActivity.Step1stAbsorb3[0]));
			nextIntent.putExtra("St1Abs3by1", absorbDfm.format(RunActivity.Step1stAbsorb3[1]));
			nextIntent.putExtra("St1Abs3by2", absorbDfm.format(RunActivity.Step1stAbsorb3[2]));
			nextIntent.putExtra("St2Abs1by0", absorbDfm.format(RunActivity.Step2ndAbsorb1[0]));
			nextIntent.putExtra("St2Abs1by1", absorbDfm.format(RunActivity.Step2ndAbsorb1[1]));
			nextIntent.putExtra("St2Abs1by2", absorbDfm.format(RunActivity.Step2ndAbsorb1[2]));
			nextIntent.putExtra("St2Abs2by0", absorbDfm.format(RunActivity.Step2ndAbsorb2[0]));
			nextIntent.putExtra("St2Abs2by1", absorbDfm.format(RunActivity.Step2ndAbsorb2[1]));
			nextIntent.putExtra("St2Abs2by2", absorbDfm.format(RunActivity.Step2ndAbsorb2[2]));
			nextIntent.putExtra("St2Abs3by0", absorbDfm.format(RunActivity.Step2ndAbsorb3[0]));
			nextIntent.putExtra("St2Abs3by1", absorbDfm.format(RunActivity.Step2ndAbsorb3[1]));
			nextIntent.putExtra("St2Abs3by2", absorbDfm.format(RunActivity.Step2ndAbsorb3[2]));
			nextIntent.putExtra("HWSN", AboutModel.HWSN);
			nextIntent.putExtra("SWVersion", AboutModel.SWVersion);
			nextIntent.putExtra("FWVersion", AboutModel.FWVersion);
			nextIntent.putExtra("OSVersion", AboutModel.OSVersion);
			
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
