package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
	
	public Temperature mTemperature;
	public SerialPort mSerialPort;
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;
	public DatabaseHander mDatabaseHander;
	
	public static EditText PatientIDText;
	
	private TextView HbA1cText,
					 DateText,
					 AMPMText,
					 Ref;
	
//	public RelativeLayout resultLinear;
	
	private Button homeIcon,
				   printBtn,
				   nextSampleBtn;
	
	private String getTime[] = new String[6];
	
	public static int ItnData;
	public int dataCnt;
	public double cellBlockEndTmp;
	
	public String operator;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.result);
		
		PatientIDText = (EditText) findViewById(R.id.patientidtext);
		
		ResultInit();
		
		/*Home Activity activation*/
		homeIcon = (Button)findViewById(R.id.homeicon);
		homeIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					homeIcon.setEnabled(false);
					
					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		printBtn = (Button)findViewById(R.id.printbtn);
		printBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					PrintResultData();
				}
			}
		});
		
		/*Test Activity activation*/
		nextSampleBtn = (Button)findViewById(R.id.nextsamplebtn);
		nextSampleBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				if(!btnState) {
					
					btnState = true;
					
					nextSampleBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Run);
				}
			}
		});
	}
	
	public void ResultInit() {

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.resultlayout);
		
		GetCurrTime();
		GetDataCnt();
		
		mTemperature = new Temperature(R.id.resultlayout);
		cellBlockEndTmp = mTemperature.CellTmpRead();
		
		HbA1cText = (TextView) findViewById(R.id.hba1cPct);
		DateText = (TextView) findViewById(R.id.r_testdate1);
		AMPMText = (TextView) findViewById(R.id.r_testdate2);
		Ref = (TextView) findViewById(R.id.ref);
				
		Intent itn = getIntent();
		ItnData = itn.getIntExtra("RunState", 0);
		
		if(ItnData == RunActivity.NORMAL_OPERATION) {
			
			if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
				
				RunActivity.HbA1cPctDbl = 0;
			} else if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
				
				int rem;
				
				rem = (int) ((Math.random() * 10) + 1) % 3;
				
				switch(rem) {
				
				case 0	:
					RunActivity.HbA1cPctDbl = 5.5;
					break;
					
				case 1	:
					RunActivity.HbA1cPctDbl = 6.7;
					break;
				
				case 2	:
					RunActivity.HbA1cPctDbl = 8.3;
					break;
					
				default	:
					break;
				}
			}
			
			DecimalFormat hbA1cFormat = new DecimalFormat("0.0");
			HbA1cText.setText(hbA1cFormat.format(RunActivity.HbA1cPctDbl) + "%");
		
		} else if(ItnData == R.string.stop) { 
			
			HbA1cText.setText(ItnData);
			
		} else {
			
			HbA1cText.setText(ItnData);
			mErrorPopup = new ErrorPopup(this, this, R.id.resultlayout);
			mErrorPopup.ErrorBtnDisplay(ItnData);
		}
		
		DateText.setText(TimerDisplay.rTime[0] + "." + TimerDisplay.rTime[1] + "." + TimerDisplay.rTime[2] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
		AMPMText.setText(TimerDisplay.rTime[3]);
		Ref.setText(Barcode.RefNum);
		
		mDatabaseHander = new DatabaseHander(this);
		operator = mDatabaseHander.GetLastLoginID();
		
		if(operator == null) operator = "Guest";
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.STABLE) {
			
			if(ItnData != R.string.stop) WhichIntent(TargetIntent.Run); // Stable
			else HomeActivity.NumofStable = 0;
		}
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
		if(TimerDisplay.rTime[4].length() != 2) getTime[4] = "0" + TimerDisplay.rTime[4];
		else getTime[4] = TimerDisplay.rTime[4];
		getTime[5] = TimerDisplay.rTime[5];			
	}
	
	public void GetDataCnt() {
		
		if(HomeActivity.ANALYZER_SW != HomeActivity.STABLE) {
		
		
		if(Barcode.RefNum.substring(0, 1).equals("B")) dataCnt = RemoveActivity.ControlDataCnt;
		else dataCnt = RemoveActivity.PatientDataCnt;
		
		} else dataCnt = RemoveActivity.ControlDataCnt;
	}
	
	public void PrintResultData() {
		
		StringBuffer txData = new StringBuffer();
		DecimalFormat dfm = new DecimalFormat("0000"),
					  hbA1cFormat = new DecimalFormat("0.0"),
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
		txData.append(Barcode.RefNum);
		txData.append(pIDLenDfm.format(PatientIDText.getText().toString().length()));
		txData.append(PatientIDText.getText().toString());
		txData.append(pIDLenDfm.format(operator.length()));
		txData.append(operator);
		txData.append(hbA1cFormat.format(RunActivity.HbA1cPctDbl));
		
		mSerialPort = new SerialPort(R.id.resultlayout);
		mSerialPort.PrinterTxStart(SerialPort.PRINTRESULT, txData);
		
		SerialPort.Sleep(100);
		
		btnState = false;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion after intent data deliver
		
		Intent DataSaveIntent = new Intent(getApplicationContext(), FileSaveActivity.class);
		DecimalFormat photoDfm = new DecimalFormat("0.0"),
					  absorbDfm = new DecimalFormat("0.0000"),
					  pIDLenDfm = new DecimalFormat("00");
		
		DataSaveIntent.putExtra("RunState", ItnData);
		DataSaveIntent.putExtra("Year", getTime[0]);
		DataSaveIntent.putExtra("Month", getTime[1]);
		DataSaveIntent.putExtra("Day", getTime[2]);
		DataSaveIntent.putExtra("AmPm", getTime[3]);
		DataSaveIntent.putExtra("Hour", getTime[4]);
		DataSaveIntent.putExtra("Minute", getTime[5]);
		DataSaveIntent.putExtra("DataCnt", dataCnt);
		DataSaveIntent.putExtra("RefNumber", Barcode.RefNum);
		DataSaveIntent.putExtra("PatientIDLen", pIDLenDfm.format(PatientIDText.getText().toString().length()));
		DataSaveIntent.putExtra("PatientID", PatientIDText.getText().toString());
		DataSaveIntent.putExtra("OperatorLen", pIDLenDfm.format(operator.length()));
		DataSaveIntent.putExtra("Operator", operator);
		DataSaveIntent.putExtra("Hba1cPct", photoDfm.format(RunActivity.HbA1cPctDbl));
		
		DataSaveIntent.putExtra("RunMin", (int) RunActivity.runMin);
		DataSaveIntent.putExtra("RunSec", (int) RunActivity.runSec);
		DataSaveIntent.putExtra("BlankVal0", photoDfm.format(RunActivity.BlankValue[0]));
		DataSaveIntent.putExtra("BlankVal1", photoDfm.format(RunActivity.BlankValue[1]));
		DataSaveIntent.putExtra("BlankVal2", photoDfm.format(RunActivity.BlankValue[2]));
		DataSaveIntent.putExtra("BlankVal3", photoDfm.format(RunActivity.BlankValue[3]));
		DataSaveIntent.putExtra("St1Abs1by0", absorbDfm.format(RunActivity.Step1stAbsorb1[0]));
		DataSaveIntent.putExtra("St1Abs1by1", absorbDfm.format(RunActivity.Step1stAbsorb1[1]));
		DataSaveIntent.putExtra("St1Abs1by2", absorbDfm.format(RunActivity.Step1stAbsorb1[2]));
		DataSaveIntent.putExtra("St1Abs2by0", absorbDfm.format(RunActivity.Step1stAbsorb2[0]));
		DataSaveIntent.putExtra("St1Abs2by1", absorbDfm.format(RunActivity.Step1stAbsorb2[1]));
		DataSaveIntent.putExtra("St1Abs2by2", absorbDfm.format(RunActivity.Step1stAbsorb2[2]));
		DataSaveIntent.putExtra("St1Abs3by0", absorbDfm.format(RunActivity.Step1stAbsorb3[0]));
		DataSaveIntent.putExtra("St1Abs3by1", absorbDfm.format(RunActivity.Step1stAbsorb3[1]));
		DataSaveIntent.putExtra("St1Abs3by2", absorbDfm.format(RunActivity.Step1stAbsorb3[2]));
		DataSaveIntent.putExtra("St2Abs1by0", absorbDfm.format(RunActivity.Step2ndAbsorb1[0]));
		DataSaveIntent.putExtra("St2Abs1by1", absorbDfm.format(RunActivity.Step2ndAbsorb1[1]));
		DataSaveIntent.putExtra("St2Abs1by2", absorbDfm.format(RunActivity.Step2ndAbsorb1[2]));
		DataSaveIntent.putExtra("St2Abs2by0", absorbDfm.format(RunActivity.Step2ndAbsorb2[0]));
		DataSaveIntent.putExtra("St2Abs2by1", absorbDfm.format(RunActivity.Step2ndAbsorb2[1]));
		DataSaveIntent.putExtra("St2Abs2by2", absorbDfm.format(RunActivity.Step2ndAbsorb2[2]));
		DataSaveIntent.putExtra("St2Abs3by0", absorbDfm.format(RunActivity.Step2ndAbsorb3[0]));
		DataSaveIntent.putExtra("St2Abs3by1", absorbDfm.format(RunActivity.Step2ndAbsorb3[1]));
		DataSaveIntent.putExtra("St2Abs3by2", absorbDfm.format(RunActivity.Step2ndAbsorb3[2]));
		
		switch(Itn) {
		
		case Home		:							
			DataSaveIntent.putExtra("WhichIntent", (int) HOME_ACTIVITY);
			startActivity(DataSaveIntent);
			break;

		case Run	:			
			DataSaveIntent.putExtra("WhichIntent", (int) ACTION_ACTIVITY);
			startActivity(DataSaveIntent);
			break;

		default			:	
			break;			
		}	
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
	}
}
