package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	
	public Button backIcon,
				  minusBtn,
				  plusBtn;
	
	public ImageView titleImage,
					 iconImage, 
					 barGauge;
	
	public int brightnessValue = 0;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.bargauge);
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
		barGauge = (ImageView) findViewById(R.id.bargauge);
		
		/*SystemSetting Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					backIcon.setEnabled(false);
					
					WhichIntent(TargetIntent.SystemSetting);
				}
			}
		});
		
		minusBtn = (Button)findViewById(R.id.minusbtn);
		minusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					BrightnessUp();
				}
			}
		});
		
		plusBtn = (Button)findViewById(R.id.plusbtn);
		plusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					BrightnessDown();
				}
			}
		});
				
		DisplayInit();
	}
	
	public void DisplayInit() {
		
		mTimerDisplay = new TimerDisplay();		
		mTimerDisplay.ActivityParm(this, R.id.bargaugelayout);
		
		titleImage.setBackgroundResource(R.drawable.display_title);
		iconImage.setBackgroundResource(R.drawable.display);
		
		try {
			
			brightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
//			Log.w("GetBrightness", "Brightness : " + brightnessValue);
			
			GaugeDisplay();
			
		} catch(Exception e) {
			
		}
	}

	public synchronized void BrightnessUp() {
		
		switch(brightnessValue) {
		
		case 51		:
			brightnessValue = 102;
			break;

		case 102		:
			brightnessValue = 153;
			break;
			
		case 153		:
			brightnessValue = 204;
			break;
			
		case 204	:
			brightnessValue = 255;
			break;
			
		default		:
			break;
		}
		
		GaugeDisplay();
		
		SetBrightness();
	}
	
	public synchronized void BrightnessDown() {
		
		switch(brightnessValue) {
		
		case 102		:
			brightnessValue = 51;
			break;
			
		case 153		:
			brightnessValue = 102;
			break;
			
		case 204	:
			brightnessValue = 153;
			break;
		
		case 255		:
			brightnessValue = 204;
			break;
			
		default		:
			break;
		}
		
		GaugeDisplay();
		
		SetBrightness();
	}
	
	public void GaugeDisplay() {
		
		switch(brightnessValue) {
		
		case 51		:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_5);
			break;

		case 102	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_4);
			break;
			
		case 153	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_3);
			break;
			
		case 204	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_2);
			break;
		
		case 255	:
			barGauge.setBackgroundResource(R.drawable.display_bar_gauge_green_1);
			break;
			
		default		:
			break;
		}
	}
	
	public void SetBrightness() {
		
		try {
			
//			Log.w("SetBrightness", "Brightness : " + brightnessValue);
			
			WindowManager.LayoutParams params = getWindow().getAttributes();
			params.screenBrightness = (float)brightnessValue/255;
			getWindow().setAttributes(params);
			
			android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
			
			btnState = false;
			
		} catch(Exception e) {
			
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case SystemSetting	:				
			nextIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
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
