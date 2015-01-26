package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SoundActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	
	public AudioManager audioManager;
	public SoundPool mPool;
	
	public Button backIcon,
				  minusBtn,
				  plusBtn;
	
	public ImageView titleImage,
	 				 iconImage, 
	 				 barGauge;
	
	public int volume = 0;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.bargauge);

		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
		barGauge = (ImageView) findViewById(R.id.bargauge);
		
		/*System Setting Activity activation*/
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
					
					SoundVolumeDown();
				}
			}
		});
		
		plusBtn = (Button)findViewById(R.id.plusbtn);
		plusBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					SoundVolumeUp();
				}
			}
		});
		
		SoundInit();
	}
	
	public void SoundInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.bargaugelayout);
		
		titleImage.setBackgroundResource(R.drawable.sound_title);
		iconImage.setBackgroundResource(R.drawable.sound);
	
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		GaugeDisplay();
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
	
	public synchronized void SoundVolumeUp() {
		
		switch(volume) {
		
		case 0	:
			volume = 3;
			break;

		case 3	:
			volume = 6;
			break;
			
		case 6	:
			volume = 9;
			break;
			
		case 9	:
			volume = 12;
			break;
		
		case 12	:
			volume = 15;
			break;
			
		default		:
			break;
		}
		
		GaugeDisplay();
		
		SetSoundVolume();
	}
	
	public synchronized void SoundVolumeDown() {
		
		switch(volume) {
		
		case 3	:
			volume = 0;
			break;

		case 6	:
			volume = 3;
			break;
			
		case 9	:
			volume = 6;
			break;
			
		case 12	:
			volume = 9;
			break;
		
		case 15	:
			volume = 12;
			break;
			
		default		:
			break;
		}
		
		GaugeDisplay();
		
		SetSoundVolume();
	}
	
	public synchronized void SetSoundVolume() { // setting the sound volume modified
		
		try {
			
			final int mWin;
			
			audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
			
			mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
			mWin = mPool.load(this, R.raw.win, 1);
			
			mPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			      public void onLoadComplete(SoundPool mPool, int sampleId, int status) {

			  		mPool.play(mWin, 1, 1, 0, 0, 1); // playing sound
			      }
			});

			btnState = false;
			
		} catch(Exception e) {
			
		}
	}
	
	public void GaugeDisplay() {
		
		switch(volume) {
		
		case 0	:
			barGauge.setBackgroundResource(0);
			break;
		
		case 3	:
			barGauge.setBackgroundResource(R.drawable.sound_bar_gauge_blue_1);
			break;

		case 6	:
			barGauge.setBackgroundResource(R.drawable.sound_bar_gauge_blue_2);
			break;
			
		case 9	:
			barGauge.setBackgroundResource(R.drawable.sound_bar_gauge_blue_3);
			break;
			
		case 12	:
			barGauge.setBackgroundResource(R.drawable.sound_bar_gauge_blue_4);
			break;
		
		case 15	:
			barGauge.setBackgroundResource(R.drawable.sound_bar_gauge_blue_5);
			break;
			
		default	:
			break;
		}
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
