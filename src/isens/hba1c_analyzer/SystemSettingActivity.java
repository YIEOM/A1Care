package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.View.ConvertActivity;
import isens.hba1c_analyzer.View.CorrelationActivity;
import isens.hba1c_analyzer.View.DateActivity;
import isens.hba1c_analyzer.View.DisplayActivity;
import isens.hba1c_analyzer.View.LanguageActivity;
import isens.hba1c_analyzer.View.SoundActivity;
import isens.hba1c_analyzer.View.TimeActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SystemSettingActivity extends Activity {
	
	final static byte NONE = 0;					  
	
	public TimerDisplay mTimerDisplay;
	public ErrorPopup mErrorPopup;
	
	public Button homeIcon,
	 			  backIcon,
				  displayBtn,
				  dateBtn,
				  timeBtn,
				  soundBtn,
				  languageBtn,
				  resultBtn,
				  collelationBtn,
				  convertBtn;

	public TextView resetText;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.systemsetting);
		
		SystemSettingInit();
	}
	
	public void setButtonId() {
		
		homeIcon = (Button)findViewById(R.id.homeicon);
		backIcon = (Button)findViewById(R.id.backicon);
		displayBtn = (Button)findViewById(R.id.displaybtn);
		dateBtn = (Button)findViewById(R.id.datebtn);
		timeBtn = (Button)findViewById(R.id.timebtn);
		soundBtn = (Button)findViewById(R.id.soundbtn);
		languageBtn = (Button)findViewById(R.id.languagebtn);
		convertBtn = (Button)findViewById(R.id.convertbtn);
	}
	
	public void setButtonClick() {
		
		homeIcon.setOnTouchListener(mTouchListener);
		backIcon.setOnTouchListener(mTouchListener);
		displayBtn.setOnTouchListener(mTouchListener);
		dateBtn.setOnTouchListener(mTouchListener);
		timeBtn.setOnTouchListener(mTouchListener);
		soundBtn.setOnTouchListener(mTouchListener);
		languageBtn.setOnTouchListener(mTouchListener);
		convertBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				if(!btnState) {

					btnState = true;
					
					switch(v.getId()) {
				
					case R.id.homeicon		:
						WhichIntent(TargetIntent.Home);
						break;
						
					case R.id.backicon		:
						WhichIntent(TargetIntent.Setting);
						break;
					
					case R.id.displaybtn	:
						WhichIntent(TargetIntent.Display);
						break;
					
					case R.id.datebtn	:
						WhichIntent(TargetIntent.Date);
						break;
						
					case R.id.timebtn		:
						WhichIntent(TargetIntent.Time);
						break;
						
					case R.id.soundbtn		:
						WhichIntent(TargetIntent.Sound);
						break;
					
					case R.id.languagebtn	:
						WhichIntent(TargetIntent.Language);
						break;
					
					case R.id.convertbtn	:
						WhichIntent(TargetIntent.Convert);
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
	
	public void SystemSettingInit() {

		setButtonId();
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.systemsettinglayout);
	}
	
	public void Reset() {
		
		mErrorPopup = new ErrorPopup(this, this, R.id.systemsettinglayout);
		mErrorPopup.OXBtnDisplay(R.string.reset);
	}

	public void SettingParameterInit() {
		
		/* Adjustment factor parameter Initialization */
		SharedPreferences AdjustmentPref = getSharedPreferences("User Define", MODE_PRIVATE);
		SharedPreferences.Editor adjustmentedit = AdjustmentPref.edit();
		
		adjustmentedit.putFloat("AF SlopeVal", 1.0f);
		adjustmentedit.putFloat("AF OffsetVal", 0.0f);
		adjustmentedit.putFloat("CF SlopeVal", 1.0f);
		adjustmentedit.putFloat("CF OffsetVal", 0.0f);
		adjustmentedit.commit();
		
		RunActivity.AF_Slope = 1.0f;
		RunActivity.AF_Offset = 0.0f;
		RunActivity.CF_Slope = 1.0f;
		RunActivity.CF_Offset = 0.0f;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home			:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
					
		case Setting		:				
			nextIntent = new Intent(getApplicationContext(), SettingActivity.class);
			break;
			
		case Display		:				
			nextIntent = new Intent(getApplicationContext(), DisplayActivity.class);
			break;
			
		case Date			:				
			nextIntent = new Intent(getApplicationContext(), DateActivity.class);
			break;
			
		case Time			:				
			nextIntent = new Intent(getApplicationContext(), TimeActivity.class);
			break;
			
		case Sound			:				
			nextIntent = new Intent(getApplicationContext(), SoundActivity.class);
			break;
			
		case Language		:				
			nextIntent = new Intent(getApplicationContext(), LanguageActivity.class);
			break;

		case Convert		:
			nextIntent = new Intent(getApplicationContext(), ConvertActivity.class);
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