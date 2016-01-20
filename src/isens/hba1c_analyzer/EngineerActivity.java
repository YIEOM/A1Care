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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	public DataStorage mDataStorage;
	public ErrorPopup mErrorPopup;
	
	public Activity activity;
	public Context context;

	public Button escBtn,
	  			  lampBtn,
	  			  adjustBtn,
	  			  calibrationBtn,
	  			  tempBtn,
	  			  tHbBtn,
	  			  f535Btn,
	  			  f660Btn,
	  			  collelationBtn,
	  			  aboutBtn,
	  			  deleteBtn;
	
	public TextView swVersionText, fwVersionText, osVersionText;
	
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
	
	public void setButtonId(Activity activity) {
		
		escBtn = (Button)activity.findViewById(R.id.escBtn);
		adjustBtn = (Button)activity.findViewById(R.id.adjustBtn);
		calibrationBtn = (Button)activity.findViewById(R.id.calibrationBtn);
		tempBtn = (Button)activity.findViewById(R.id.tempBtn);
		lampBtn = (Button)activity.findViewById(R.id.lampBtn);
		tHbBtn = (Button)activity.findViewById(R.id.tHbBtn);
		f535Btn = (Button)activity.findViewById(R.id.f535Btn);
		f660Btn = (Button)activity.findViewById(R.id.f660Btn);
		collelationBtn = (Button)activity.findViewById(R.id.collelationBtn);
		aboutBtn = (Button)activity.findViewById(R.id.aboutBtn);
		deleteBtn = (Button)activity.findViewById(R.id.deleteBtn);
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
		deleteBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				unenabledAllBtn(activity);
				
				switch(v.getId()) {
			
				case R.id.escBtn	:
					WhichIntent(activity, TargetIntent.Home);
					break;
					
				case R.id.adjustBtn	:
					WhichIntent(activity, TargetIntent.Adjustment);
					break;
				
				case R.id.calibrationBtn	:
					WhichIntent(activity, TargetIntent.Calibration);
					break;
				
				case R.id.tempBtn	:
					WhichIntent(activity, TargetIntent.Temperature);
					break;
					
				case R.id.lampBtn	:
					WhichIntent(activity, TargetIntent.Lamp);
					break;
					
				case R.id.tHbBtn	:
					WhichIntent(activity, TargetIntent.tHb);
					break;
				
				case R.id.f535Btn	:
					WhichIntent(activity, TargetIntent.f535);
					break;
					
				case R.id.f660Btn	:
					WhichIntent(activity, TargetIntent.f660);
					break;
					
				case R.id.collelationBtn	:
					WhichIntent(activity, TargetIntent.Correlation);
					break;
				
				case R.id.aboutBtn	:
					WhichIntent(activity, TargetIntent.About);
					break;
					
				case R.id.deleteBtn	:
					mErrorPopup = new ErrorPopup(activity, context, R.id.engineerlayout, null, 0);
					mErrorPopup.OXBtnDisplay(R.string.delete);
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

		setButtonState(R.id.escBtn, true, activtiy);
		setButtonState(R.id.adjustBtn, true, activtiy);
		setButtonState(R.id.calibrationBtn, true, activtiy);
		setButtonState(R.id.tempBtn, true, activtiy);
		setButtonState(R.id.lampBtn, true, activtiy);
		setButtonState(R.id.tHbBtn, true, activtiy);
		setButtonState(R.id.f535Btn, true, activtiy);
		setButtonState(R.id.f660Btn, true, activtiy);
		setButtonState(R.id.collelationBtn, true, activtiy);
		setButtonState(R.id.aboutBtn, true, activtiy);
		setButtonState(R.id.deleteBtn, true, activtiy);
	}
	
	public void unenabledAllBtn(Activity activtiy) {
		
		setButtonState(R.id.escBtn, false, activtiy);
		setButtonState(R.id.adjustBtn, false, activtiy);
		setButtonState(R.id.calibrationBtn, false, activtiy);
		setButtonState(R.id.tempBtn, false, activtiy);
		setButtonState(R.id.lampBtn, false, activtiy);
		setButtonState(R.id.tHbBtn, false, activtiy);
		setButtonState(R.id.f535Btn, false, activtiy);
		setButtonState(R.id.f660Btn, false, activtiy);
		setButtonState(R.id.collelationBtn, false, activtiy);
		setButtonState(R.id.aboutBtn, false, activtiy);
		setButtonState(R.id.deleteBtn, false, activtiy);
	}	
	
	public void MaintenanceInit() {
		
		activity = this;
		context = this;
		
		setTextId();
		setButtonId(activity);
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.engineerlayout);
	}
	
	public void WhichIntent(Activity activity, TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home			:				
			nextIntent = new Intent(activity.getApplicationContext(), HomeActivity.class);
			break;
						
		case Lamp		:				
			nextIntent = new Intent(activity.getApplicationContext(), LampCopyActivity.class);
			break;
			
		case Adjustment		:				
			nextIntent = new Intent(activity.getApplicationContext(), AdjustmentActivity.class);
			break;

		case Calibration	:				
			nextIntent = new Intent(activity.getApplicationContext(), CalibrationActivity.class);
			break;
			
		case Temperature	:				
			nextIntent = new Intent(activity.getApplicationContext(), TemperatureActivity.class);
			break;
		
		case tHb		:				
			nextIntent = new Intent(activity.getApplicationContext(), tHbActivity.class);
			break;
					
		case f535		:				
			nextIntent = new Intent(activity.getApplicationContext(), f535Activity.class);
			break;
		
		case f660		:				
			nextIntent = new Intent(activity.getApplicationContext(), f660Activity.class);
			break;
		
		case Correlation	:				
			nextIntent = new Intent(activity.getApplicationContext(), CorrelationActivity.class);
			break;
		
		case About	:				
			nextIntent = new Intent(activity.getApplicationContext(), AboutActivity.class);
			break;
			
		case Delete	:
			int patientDataCnt = RemoveActivity.PatientDataCnt;
			int controlDataCnt = RemoveActivity.ControlDataCnt;
			RemoveActivity.PatientDataCnt = 1;
			RemoveActivity.ControlDataCnt = 1;
			
			SharedPreferences DcntPref = activity.getSharedPreferences("Data Counter", MODE_PRIVATE);
			SharedPreferences.Editor edit = DcntPref.edit();
			
			edit.putInt("PatientDataCnt", 1);
			edit.putInt("ControlDataCnt", 1);
			
			edit.commit();
			
			nextIntent = new Intent(activity.getApplicationContext(), FileDeleteActivity.class);
			nextIntent.putExtra("PatientDataCnt", patientDataCnt);
			nextIntent.putExtra("ControlDataCnt", controlDataCnt);
			break;
			
		default		:	
			break;			
		}
		
		activity.startActivity(nextIntent);
		finish(activity);
	}
	
	public void finish(Activity activity) {
		
		super.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}