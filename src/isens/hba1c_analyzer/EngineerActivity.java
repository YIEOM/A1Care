package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.Temperature.CellTmpRead;
import isens.hba1c_analyzer.View.AboutActivity;
import isens.hba1c_analyzer.View.tHbActivity;
import isens.hba1c_analyzer.View.AdjustmentActivity;
import isens.hba1c_analyzer.View.f535Activity;
import isens.hba1c_analyzer.View.f660Activity;
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

public class EngineerActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	public SerialPort mSerialPort;
	
	public Button escBtn,
	  			  lampBtn,
	  			  adjustBtn,
	  			  calibrationBtn,
	  			  tempBtn,
	  			  tHbBtn,
	  			  f535Btn,
	  			  f660Btn,
	  			  collelationBtn,
	  			  
	  			  aboutBtn;
	
	public TextView swVersionText, fwVersionText, osVersionText;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.engineer);
		
		MaintenanceInit();
	}
	
	public void setTextId() {

		swVersionText = (TextView)findViewById(R.id.swVersionText);
		fwVersionText = (TextView)findViewById(R.id.fwVersionText);
		osVersionText = (TextView)findViewById(R.id.osVersionText);		
	}
	
	public void setButtonId() {
		
		escBtn = (Button)findViewById(R.id.escBtn);
		adjustBtn = (Button)findViewById(R.id.adjustBtn);
		calibrationBtn = (Button)findViewById(R.id.calibrationBtn);
		tempBtn = (Button)findViewById(R.id.tempBtn);
		lampBtn = (Button)findViewById(R.id.lampBtn);
		tHbBtn = (Button)findViewById(R.id.tHbBtn);
		f535Btn = (Button)findViewById(R.id.f535Btn);
		f660Btn = (Button)findViewById(R.id.f660Btn);
		collelationBtn = (Button)findViewById(R.id.collelationBtn);
		aboutBtn = (Button)findViewById(R.id.aboutBtn);
	}
	
	public void setButtonClick() {
		
		escBtn.setOnTouchListener(mTouchListener);
		adjustBtn.setOnTouchListener(mTouchListener);
		calibrationBtn.setOnTouchListener(mTouchListener);
		tempBtn.setOnTouchListener(mTouchListener);
		lampBtn.setOnTouchListener(mTouchListener);
		tHbBtn.setOnTouchListener(mTouchListener);
		f535Btn.setOnTouchListener(mTouchListener);
		f660Btn.setOnTouchListener(mTouchListener);
		collelationBtn.setOnTouchListener(mTouchListener);
		aboutBtn.setOnTouchListener(mTouchListener);
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
				
					case R.id.escBtn	:
						WhichIntent(TargetIntent.Home);
						break;
						
					case R.id.adjustBtn	:
						WhichIntent(TargetIntent.Adjustment);
						break;
					
					case R.id.calibrationBtn	:
						WhichIntent(TargetIntent.Calibration);
						break;
					
					case R.id.tempBtn	:
						WhichIntent(TargetIntent.Temperature);
						break;
						
					case R.id.lampBtn	:
						WhichIntent(TargetIntent.Lamp);
						break;
						
					case R.id.tHbBtn	:
						WhichIntent(TargetIntent.tHb);
						break;
					
					case R.id.f535Btn	:
						WhichIntent(TargetIntent.f535);
						break;
						
					case R.id.f660Btn	:
						WhichIntent(TargetIntent.f660);
						break;
						
					case R.id.collelationBtn	:
						WhichIntent(TargetIntent.Correlation);
						break;
					
					case R.id.aboutBtn	:
						WhichIntent(TargetIntent.About);
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
		
		case tHb		:				
			nextIntent = new Intent(getApplicationContext(), tHbActivity.class);
			break;
					
		case f535		:				
			nextIntent = new Intent(getApplicationContext(), f535Activity.class);
			break;
		
		case f660		:				
			nextIntent = new Intent(getApplicationContext(), f660Activity.class);
			break;
		
		case Correlation	:				
			nextIntent = new Intent(getApplicationContext(), CorrelationActivity.class);
			break;
		
		case About	:				
			nextIntent = new Intent(getApplicationContext(), AboutActivity.class);
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