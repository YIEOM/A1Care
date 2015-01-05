package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
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
	
	final static byte NONE        = 0,
					  YEAR_UP     = 1,
					  YEAR_DOWN   = 2,
					  MONTH_UP    = 3,
					  MONTH_DOWN  = 4,
					  DAY_UP      = 5,
					  DAY_DOWN    = 6,
					  HOUR_UP     = 7,
					  HOUR_DOWN   = 8,
					  MINUTE_UP   = 9,
					  MINUTE_DOWN = 10;
	
	public TimerDisplay mTimerDisplay;
	public AdjustmentFactorActivity mAdjustmentFactorActivity;
	public ErrorPopup mErrorPopup;
	
	public Button escBtn,
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
		
		/*Home Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					escBtn.setEnabled(false);
					
					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		/*Display Activity activation*/
		displayBtn = (Button)findViewById(R.id.displaybtn);
		displayBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					displayBtn.setEnabled(false);

					WhichIntent(TargetIntent.Display);
				}
			}
		});
		
		/*Date Activity activation*/
		dateBtn = (Button)findViewById(R.id.datebtn);
		dateBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					dateBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Date);
				}
			}
		});
		
		/*Time Activity activation*/
		timeBtn = (Button)findViewById(R.id.timebtn);
		timeBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					timeBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Time);
				}
			}
		});
		
		/*Correlation Factor Activity activation*/
		collelationBtn = (Button)findViewById(R.id.collelationbtn);
		collelationBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					collelationBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Correlation);
				}
			}
		});
		
		/*Sound Activity activation*/
		soundBtn = (Button)findViewById(R.id.soundbtn);
		soundBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					soundBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Sound);
				}
			}
		});
		
		/*Language Activity activation*/
		languageBtn = (Button)findViewById(R.id.languagebtn);
		languageBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					languageBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Language);
				}
			}
		});
		
		convertBtn = (Button)findViewById(R.id.convertbtn);
		convertBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					convertBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Convert);
				}
			}
		});
	}
	
	public void SystemSettingInit() {

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
		
		switch(Itn) {
		
		case Home			:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
						
		case Display		:				
			Intent DisplayIntent = new Intent(getApplicationContext(), DisplayActivity.class);
			startActivity(DisplayIntent);
			break;
			
		case Date			:				
			Intent DateIntent = new Intent(getApplicationContext(), DateActivity.class);
			startActivity(DateIntent);
			break;
			
		case Time			:				
			Intent TimeIntent = new Intent(getApplicationContext(), TimeActivity.class);
			startActivity(TimeIntent);
			break;
			
		case Sound			:				
			Intent SoundIntent = new Intent(getApplicationContext(), SoundActivity.class);
			startActivity(SoundIntent);
			break;
			
		case Language		:				
			Intent LanguageIntent = new Intent(getApplicationContext(), LanguageActivity.class);
			startActivity(LanguageIntent);
			break;

		case Correlation	:				
			Intent CorrelationIntent = new Intent(getApplicationContext(), CorrelationFactorActivity.class);
			startActivity(CorrelationIntent);
			break;
		
		case Convert		:
			Intent ConvertIntent = new Intent(getApplicationContext(), ConvertActivity.class);
			startActivity(ConvertIntent);
			break;
			
		default		:	
			break;			
		}
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}