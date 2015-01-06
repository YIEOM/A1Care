package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TimeActivity extends Activity {

	private TimerDisplay mTimerDisplay;
	
	public Handler handler = new Handler();
	public TimerTask oneHundredmsPeriod;

	public Timer timer;
	
	private TextView hourText,
					 minText,
					 ampmText;
	
	private Calendar c;
	
	private Button backIcon,
				   hPlusBtn,
				   hMinusBtn,
				   mPlusBtn,
				   mMinusBtn,
				   ampmUpBtn,
				   ampmDownBtn;
	
	private int currHour,
				hour,
				currMin,
				min,
				ampm;
	
	private String minStr,
				   ampmStr;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.time);
		
		hourText  = (TextView) findViewById(R.id.hourtext);
		minText   = (TextView) findViewById(R.id.mintext);
		ampmText   = (TextView) findViewById(R.id.ampmtext);
		
		/*System Setting Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					backIcon.setEnabled(false);
				
					TimeSave();
					
					WhichIntent(TargetIntent.SystemSetting);
				}
			}
		});
		
		hPlusBtn = (Button) findViewById(R.id.hplusbtn);
		hPlusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						TimeChange(SystemSettingActivity.HOUR_UP);
					}
					break;

				case MotionEvent.ACTION_UP		:
					if(timer != null) timer.cancel();
					break;
				}
				// TODO Auto-generated method stub
				return false;
			}
		});

		hPlusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.HOUR_UP);
				
				return true;
			}
		});
		
		hMinusBtn = (Button) findViewById(R.id.hminusbtn);
		hMinusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						TimeChange(SystemSettingActivity.HOUR_DOWN);
					}
					break;

				case MotionEvent.ACTION_UP		:
					if(timer != null) timer.cancel();
					break;
				}
				// TODO Auto-generated method stub
				return false;
			}
		});

		hMinusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.HOUR_DOWN);
				
				return true;
			}
		});
		
		mPlusBtn = (Button) findViewById(R.id.mplusbtn);
		mPlusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						TimeChange(SystemSettingActivity.MINUTE_UP);
					}
					break;

				case MotionEvent.ACTION_UP		:
					if(timer != null) timer.cancel();
					break;
				}
				// TODO Auto-generated method stub
				return false;
			}
		});

		mPlusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.MINUTE_UP);
				
				return true;
			}
		});
		
		mMinusBtn = (Button) findViewById(R.id.mminusbtn);
		mMinusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						TimeChange(SystemSettingActivity.MINUTE_DOWN);
					}
					break;

				case MotionEvent.ACTION_UP		:
					if(timer != null) timer.cancel();
					break;
				}
				// TODO Auto-generated method stub
				return false;
			}
		});

		mMinusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.MINUTE_DOWN);
				
				return true;
			}
		});

		ampmUpBtn = (Button) findViewById(R.id.ampmupbtn);
		ampmUpBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					AmPmChange();
				}
			}
		});

		ampmDownBtn = (Button) findViewById(R.id.ampmdownbtn);
		ampmDownBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					AmPmChange();
				}
			}
		});
		
		DateInit();
	}
	
	public void DateInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.timelayout);
		GetCurrTime();
	}
	
	public void TimerInit(final int whichTime) {
		
		oneHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
		
						TimeChange(whichTime);
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(oneHundredmsPeriod, 0, 100); // Timer period : 100msec
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
	
	public synchronized void TimeDisplay() { // displaying the modifying time value
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {

		        		hourText.setText(Integer.toString(hour));
		        		minText.setText(minStr);
		        		ampmText.setText(ampmStr);
		        		
		        		btnState = false;
		            }
		        });
		    }
		}).start();
	}
	
	public void GetCurrTime() { // getting the current time data
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR);
		
		if(c.get(Calendar.AM_PM) != 0) {
			
			ampm = 1;
			ampmStr = "PM";
			currHour = hour + 12;
		} else {
			
			ampm = 0;
			ampmStr = "AM";
			currHour = hour;
		}
		
		if(hour == 0) hour = 12;
		
		min  = c.get(Calendar.MINUTE);
		currMin = min;
		
		minStr  = dfm.format(min);
		
//		Log.w("getcurrdate", "" + c.getTimeInMillis());
		
		TimeDisplay();
	}
	
	public void TimeChange(int whichTime) {
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		switch(whichTime) {
		
		case SystemSettingActivity.HOUR_UP		:
			if(hour < 12) {
				hour += 1;
			} else {
				hour = 1;
			}
			break;
			
		case SystemSettingActivity.HOUR_DOWN	:
			if(hour > 1) {
				hour -= 1;
			} else {
				hour = 12;
			}
			break;
			
		case SystemSettingActivity.MINUTE_UP	:
			if(min < 59) {
				min += 1;
			} else {
				min = 0;
			}
			minStr = dfm.format(min);
			break;
			
		case SystemSettingActivity.MINUTE_DOWN	:
			if(min > 0) {
				min -= 1;
			} else {
				min = 59;
			}
			minStr = dfm.format(min);
			break;
			
		default		:
			break;
		}
		
		TimeDisplay();
	}
	
	public void AmPmChange() { // changing the am/pm
		
		if(ampm == 0) {
			ampmStr = "PM";
			ampm = 1;
		} else {
			ampmStr = "AM";
			ampm = 0;
		}
		
		TimeDisplay();
	}
	
	public void TimeSave() { // saving the time modified

		int setHour = 0;
		int setMin;
		
		if(ampm == 0) {
			
			if(hour != 12) setHour = hour - currHour;
			else setHour -= currHour;
					
		} else {
			
			if(hour != 12) setHour = (hour + 12) - currHour;
			else setHour = hour - currHour;
		}
		setMin = min - currMin;
		
		c.add(Calendar.MINUTE, setMin);
		c.add(Calendar.HOUR_OF_DAY, setHour);;
				
		TimerDisplay.OneHundredmsPeriod.cancel();
		
		SystemClock.setCurrentTimeMillis(c.getTimeInMillis());
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.TimerInit();
		
		SerialPort.Sleep(1500);
		
		if(hour == 0) hour = 12;
		
//		mTimerDisplay.ActivityParm(this, R.id.timelayout);
//
//		SerialPort.Sleep(500);
//		GetCurrTime();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}