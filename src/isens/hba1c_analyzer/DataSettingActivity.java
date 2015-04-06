package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
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

public class DataSettingActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	
	private Button backBtn,
		   		   homeBtn,
		   		   exportBtn;
	
	private boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.datasetting);
		
		DataSettingInit();
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backicon);
		homeBtn = (Button)findViewById(R.id.homeicon);
		exportBtn = (Button)findViewById(R.id.exportbtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		homeBtn.setOnTouchListener(mTouchListener);
		exportBtn.setOnTouchListener(mTouchListener);
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
				
					case R.id.backBtn	:
						WhichIntent(TargetIntent.Setting);
						break;
					
					case R.id.homeBtn	:
						WhichIntent(TargetIntent.Home);
						break;
					
					case R.id.exportbtn	:
						WhichIntent(TargetIntent.Export);
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
	
	public void enabledAllBtn() {

		setButtonState(R.id.homeicon, true);
	}
	
	public void unenabledAllBtn() {

		setButtonState(R.id.homeicon, false);
		
		btnState = false;
	}
	
	public void DataSettingInit() {
		
		setButtonId();
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.datalayout);
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home		:	
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
			
		case Setting	:
			nextIntent = new Intent(getApplicationContext(), SettingActivity.class);
			break;
			
		case Export		:
			nextIntent = new Intent(getApplicationContext(), ExportActivity.class);
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