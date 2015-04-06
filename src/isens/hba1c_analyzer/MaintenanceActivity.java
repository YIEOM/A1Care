package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.Temperature.CellTmpRead;
import isens.hba1c_analyzer.View.AbsorbanceActivity;
import isens.hba1c_analyzer.View.AdjustmentActivity;
import isens.hba1c_analyzer.View.Correction1Activity;
import isens.hba1c_analyzer.View.Correction2Activity;
import isens.hba1c_analyzer.View.CorrelationActivity;
import isens.hba1c_analyzer.View.LampActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class MaintenanceActivity extends Activity {
	
	final static String FW_VERSION =  "QV";
	
	public TimerDisplay mTimerDisplay;
	public SerialPort mSerialPort;
	
	public Button escBtn,
	  			  lampBtn,
	  			  adjustBtn,
	  			  calibrationBtn,
	  			  tempBtn,
	  			  absorbanceBtn,
	  			  correct1Btn,
	  			  correct2Btn,
	  			  collelationBtn;
	
	public TextView swVersionText, fwVersionText, osVersionText;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.maintenance);
		
		MaintenanceInit();
	}
	
	public void setTextId() {

		swVersionText = (TextView)findViewById(R.id.swVersionText);
		fwVersionText = (TextView)findViewById(R.id.fwVersionText);
		osVersionText = (TextView)findViewById(R.id.osVersionText);
		
	}
	
	public void setButtonId() {
		
		escBtn = (Button)findViewById(R.id.escicon);
		adjustBtn = (Button)findViewById(R.id.adjustbtn);
		calibrationBtn = (Button)findViewById(R.id.calibrationbtn);
		tempBtn = (Button)findViewById(R.id.tempbtn);
		lampBtn = (Button)findViewById(R.id.lampbtn);
		absorbanceBtn = (Button)findViewById(R.id.absorbancebtn);
		correct1Btn = (Button)findViewById(R.id.correct1Btn);
		correct2Btn = (Button)findViewById(R.id.correct2Btn);
		collelationBtn = (Button)findViewById(R.id.collelationbtn);
	}
	
	public void setButtonClick() {
		
		escBtn.setOnTouchListener(mTouchListener);
		adjustBtn.setOnTouchListener(mTouchListener);
		calibrationBtn.setOnTouchListener(mTouchListener);
		tempBtn.setOnTouchListener(mTouchListener);
		lampBtn.setOnTouchListener(mTouchListener);
		absorbanceBtn.setOnTouchListener(mTouchListener);
		correct1Btn.setOnTouchListener(mTouchListener);
		correct2Btn.setOnTouchListener(mTouchListener);
		collelationBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				if(!btnState) {

					btnState = true;
					
					switch(v.getId()) {
				
					case R.id.escicon	:
						WhichIntent(TargetIntent.Home);
						break;
						
					case R.id.adjustbtn	:
						WhichIntent(TargetIntent.Adjustment);
						break;
					
					case R.id.calibrationbtn	:
						WhichIntent(TargetIntent.Calibration);
						break;
					
					case R.id.tempbtn	:
						WhichIntent(TargetIntent.Temperature);
						break;
						
					case R.id.lampbtn	:
						WhichIntent(TargetIntent.Lamp);
						break;
						
					case R.id.absorbancebtn	:
						WhichIntent(TargetIntent.Absorbance);
						break;
					
					case R.id.correct1Btn	:
						WhichIntent(TargetIntent.Correction1);
						break;
						
					case R.id.correct2Btn	:
						WhichIntent(TargetIntent.Correction2);
						break;
						
					case R.id.collelationbtn	:
						WhichIntent(TargetIntent.Correlation);
						break;
						
					default	:
						break;
					}
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void MaintenanceInit() {
		
		setTextId();
		setButtonId();
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.maintenancelayout);
		
		DisplayVersion mDisplayVersion = new DisplayVersion();
		mDisplayVersion.start();
	}
	
	public class DisplayVersion extends Thread {
		
		String swVersion, fwVersion, osVersion;
		
		PackageInfo pi = null;
		
		public void run() {
			
			try {

				pi = getPackageManager().getPackageInfo(getPackageName(), 0);

			} catch (NameNotFoundException e) {

				// TODO Auto-generated catch block

				e.printStackTrace();
			}

			swVersion = pi.versionName;
			
			fwVersion = getFwVersion();
			
			osVersion = getOSVersion();
			
			new Thread(new Runnable() {
				public void run() {
					runOnUiThread(new Runnable(){
						public void run() {
	
							swVersionText.setText("SW version : " + swVersion);
							fwVersionText.setText("FW version : " + fwVersion);
							osVersionText.setText("OS version : " + osVersion);
						}
					});
				}
			}).start();
		}
	}
	
	public String getFwVersion() {
		
		GetFwVersion mGetFwVersion = new GetFwVersion();
		mGetFwVersion.start();
		
		try {
			mGetFwVersion.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mGetFwVersion.getVersion();
	}
	
	public class GetFwVersion extends Thread {
		
		String version;
		
		public void run() {
			
			String temp = "NR";
			int cnt = 0;
			
			while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			mSerialPort = new SerialPort();
			mSerialPort.BoardTx(FW_VERSION, CtrTarget.NormalSet);
			
			do {	
			
				temp = mSerialPort.BoardMessageOutput();

				Log.w("GetFwVersion", "temp : " + temp);
				
				if(cnt++ == 15) {
					
					temp = "Nothing";
					break;
				}
				
				SerialPort.Sleep(100);
			
			} while(temp.equals("NR"));
			
			TimerDisplay.RXBoardFlag = false;
			
			version = temp;
		}
		
		public String getVersion() {
			
			return version;
		}
	}

	public String getOSVersion() {
		
		GetOSVersion mGetOSVersion = new GetOSVersion();
		mGetOSVersion.start();
		
		try {
			mGetOSVersion.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mGetOSVersion.getVersion();
	}
	
	public class GetOSVersion extends Thread {
		
		String version;
		
		public void run() {
			
			try {
				
				int cnt = 0;
				
				Process shell = Runtime.getRuntime().exec("getprop ro.build.version.os");

				BufferedReader br = new BufferedReader(new InputStreamReader(shell.getInputStream()));
				String line = "", temp = "";
				
				while((line = br.readLine()) != null) {
				
					if(line.substring(0, 3).equals("ICS")) {
					
						Log.w("GetOSVersion", "temp : " + line);
						
						temp = line;
					}
				}
				
				br.close();

				version = temp;
				
			} catch (IOException e) {
		
				version	= "Nothing";			
				throw new RuntimeException(e);
			}
			
		}
		
		public String getVersion() {
			
			return version;
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home			:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
						
		case Lamp		:				
			nextIntent = new Intent(getApplicationContext(), LampCopyActivity.class);
			break;
			
		case Adjustment		:				
			nextIntent = new Intent(getApplicationContext(), AdjustmentActivity.class);
			break;

		case Calibration	:				
			nextIntent = new Intent(getApplicationContext(), CalibrationActivity.class);
			break;
			
		case Temperature	:				
			nextIntent = new Intent(getApplicationContext(), TemperatureActivity.class);
			break;
		
		case Absorbance		:				
			nextIntent = new Intent(getApplicationContext(), AbsorbanceActivity.class);
			break;
					
		case Correction1		:				
			nextIntent = new Intent(getApplicationContext(), Correction1Activity.class);
			break;
		
		case Correction2		:				
			nextIntent = new Intent(getApplicationContext(), Correction2Activity.class);
			break;
		
		case Correlation	:				
			nextIntent = new Intent(getApplicationContext(), CorrelationActivity.class);
			break;
		
		default		:	
			break;			
		}
		
		startActivity(nextIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}