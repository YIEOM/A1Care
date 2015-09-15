package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.View.ConvertActivity;
import isens.hba1c_analyzer.View.CorrelationActivity;
import isens.hba1c_analyzer.View.DateActivity;
import isens.hba1c_analyzer.View.DisplayActivity;
import isens.hba1c_analyzer.View.LanguageActivity;
import isens.hba1c_analyzer.View.SoundActivity;
import isens.hba1c_analyzer.View.TimeActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
	
	private Activity activity;
	private Context context;
	
	private TextView titleText,
					 displayText,
					 dateText,
					 timeSettingText,
					 soundText,
					 languageText,
					 unitText;
	
	public Button homeIcon,
	 			  backIcon,
				  displayBtn,
				  dateBtn,
				  timeBtn,
				  soundBtn,
				  languageBtn,
				  resultBtn,
				  collelationBtn,
				  convertBtn,
				  snapshotBtn;

	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.systemsetting);
		
		SystemSettingInit();
	}
	
	private void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		displayText = (TextView) findViewById(R.id.displayText);
		dateText = (TextView) findViewById(R.id.dateText);
		timeSettingText = (TextView) findViewById(R.id.timeSettingText);
		soundText = (TextView) findViewById(R.id.soundText);
		languageText = (TextView) findViewById(R.id.languageText);
		unitText = (TextView) findViewById(R.id.unitText);
	}
	
	private void setText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.systemsettingtitle);
		displayText.setPaintFlags(displayText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		displayText.setText(R.string.displaytitle);
		dateText.setPaintFlags(dateText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		dateText.setText(R.string.datetitle);
		timeSettingText.setPaintFlags(timeSettingText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		timeSettingText.setText(R.string.timetitle);
		soundText.setPaintFlags(soundText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		soundText.setText(R.string.soundtitle);
		languageText.setPaintFlags(languageText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		languageText.setText(R.string.languagetitle);
		unitText.setPaintFlags(unitText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		unitText.setText(R.string.unitselection);
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
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
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
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn(); //0624
					
				switch(v.getId()) {
			
				case R.id.homeicon		:
					WhichIntent(activity, context, TargetIntent.Home);
					break;
					
				case R.id.backicon		:
					WhichIntent(activity, context, TargetIntent.Setting);
					break;
				
				case R.id.displaybtn	:
					WhichIntent(activity, context, TargetIntent.Display);
					break;
				
				case R.id.datebtn	:
					WhichIntent(activity, context, TargetIntent.Date);
					break;
					
				case R.id.timebtn		:
					WhichIntent(activity, context, TargetIntent.Time);
					break;
					
				case R.id.soundbtn		:
					WhichIntent(activity, context, TargetIntent.Sound);
					break;
				
				case R.id.languagebtn	:
					WhichIntent(activity, context, TargetIntent.Language);
					break;
				
				case R.id.convertbtn	:
					WhichIntent(activity, context, TargetIntent.Convert);
					break;
					
				case R.id.snapshotBtn		:
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
	
	public void enabledAllBtn() {

		setButtonState(R.id.homeicon, true);
		setButtonState(R.id.backicon, true);
		setButtonState(R.id.displaybtn, true);
		setButtonState(R.id.datebtn, true);
		setButtonState(R.id.timebtn, true);
		setButtonState(R.id.soundbtn, true);
		setButtonState(R.id.languagebtn, true);
		setButtonState(R.id.convertbtn, true);
	}
	
	public void unenabledAllBtn() {
		
		setButtonState(R.id.homeicon, false);
		setButtonState(R.id.backicon, false);
		setButtonState(R.id.displaybtn, false);
		setButtonState(R.id.datebtn, false);
		setButtonState(R.id.timebtn, false);
		setButtonState(R.id.soundbtn, false);
		setButtonState(R.id.languagebtn, false);
		setButtonState(R.id.convertbtn, false);
	}
	
	public void SystemSettingInit() {

		activity = this;
		context = this;
		
		setTextId();
		setText();
		setButtonId();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.systemsettinglayout);
		
		SerialPort.Sleep(500);
		
		setButtonClick();
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
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
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
			
		case SnapShot	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			nextIntent = new Intent(context, FileSaveActivity.class);
			nextIntent.putExtra("snapshot", true);
			nextIntent.putExtra("datetime", TimerDisplay.rTime);
			nextIntent.putExtra("bitmap", bitmapBytes);
			startActivity(nextIntent);
			finish();
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