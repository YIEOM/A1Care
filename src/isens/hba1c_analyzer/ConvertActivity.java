package isens.hba1c_analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConvertActivity extends Activity {
	
	final static byte NGSP = 0,
					  IFCC = 1;
	
	public TimerDisplay mTimerDisplay;
	
	public Button backIcon,
			      leftBtn,
				  rightBtn;
	
	public TextView convertText;
	
	public static byte Primary;
	
	public byte[] convertTable = new byte[] {NGSP, IFCC};
	
	public int idx = 0;
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.convert);
		
		convertText = (TextView) findViewById(R.id.converttext);
		
		/*SystemSetting Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;

					backIcon.setEnabled(false);
					
					ConvertSave();
					
					WhichIntent(TargetIntent.SystemSetting);
				}
			}
		});
		
		leftBtn = (Button)findViewById(R.id.leftbtn);
		leftBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;

					ConvertFront();
				}
			}
		});
			
		rightBtn = (Button)findViewById(R.id.rightbtn);
		rightBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;

					ConvertBack();
				}
			}
		});
		
		ConvertInit();
	}
	
	public void ConvertInit() {

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.convertlayout);
		
		GetConvert();
	}

	public void GetConvert() {
		
		int i;
		
		for(i = 0; i < convertTable.length; i++) {
			
			idx = i;
			
			if(convertTable[i] == Primary) break;
		}
		
		ConvertDisplay();
	}
	
	public void ConvertDisplay() {
		
		switch(idx) {
		
		case NGSP	:
			convertText.setText(R.string.ngsp);
			break;
			
		case IFCC	:
			convertText.setText(R.string.ifcc);
			break;
			
		default	:
			break;
		}
		
		btnState = false;
	}
	
	public void ConvertFront() {
		
		if(idx-- == 0) idx = convertTable.length - 1;
		
		ConvertDisplay();
	}
	
	private void ConvertBack() {
		
		if(idx++ == (convertTable.length - 1)) idx = 0;
		
		ConvertDisplay();
	}
	
	public void ConvertSave() {
		
		SharedPreferences convertPref = getSharedPreferences("Primary", MODE_PRIVATE);
		SharedPreferences.Editor convertedit = convertPref.edit();
		
		convertedit.putInt("Convert", idx);
		convertedit.commit();
		
		Primary = (byte) idx;
		
		Log.w("ConvertSave", "idx : " + idx);
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
