package isens.hba1c_analyzer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DateActivity extends Activity {

	private TimerDisplay mTimerDisplay;
	
	public Handler handler = new Handler();
	public TimerTask oneHundredmsPeriod;
	
	public Timer timer;
	
	private TextView yearText,
					 monthText,
					 dayText;
	
	private Calendar c;
	
	private Button backIcon,
				   yPlusBtn,
				   yMinusBtn,
				   mPlusBtn,
				   mMinusBtn,
				   dPlusBtn,
				   dMinusBtn;
	
	private static TextView TimeText;
	private static ImageView deviceImage;
	
	public static int WhichDate = 0;
	
	private int year,
				month,
				day;
	
	private boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.date);
		
		yearText  = (TextView) findViewById(R.id.yeartext);
		monthText = (TextView) findViewById(R.id.monthtext);
		dayText   = (TextView) findViewById(R.id.daytext);
		
		/*Home Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					backIcon.setEnabled(false);
					
					DateSave();
					
					WhichIntent(TargetIntent.SystemSetting);
				}
			}
		});
		
		yPlusBtn = (Button) findViewById(R.id.yplusbtn);
		yPlusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						DateChange(SystemSettingActivity.YEAR_UP);
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

		yPlusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.YEAR_UP);
				
				return true;
			}
		});
		
		yMinusBtn = (Button) findViewById(R.id.yminusbtn);
		yMinusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						DateChange(SystemSettingActivity.YEAR_DOWN);
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

		yMinusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.YEAR_DOWN);
				
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
						
						DateChange(SystemSettingActivity.MONTH_UP);
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
			
				TimerInit(SystemSettingActivity.MONTH_UP);
				
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
						
						DateChange(SystemSettingActivity.MONTH_DOWN);
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
			
				TimerInit(SystemSettingActivity.MONTH_DOWN);
				
				return true;
			}
		});
		
		dPlusBtn = (Button) findViewById(R.id.dplusbtn);
		dPlusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						DateChange(SystemSettingActivity.DAY_UP);
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

		dPlusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.DAY_UP);
				
				return true;
			}
		});

		dMinusBtn = (Button) findViewById(R.id.dminusbtn);
		dMinusBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						DateChange(SystemSettingActivity.DAY_DOWN);
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

		dMinusBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				TimerInit(SystemSettingActivity.DAY_DOWN);
				
				return true;
			}
		});
		
		DateInit();
	}
	
	public void DateInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.datelayout);
		
		GetCurrDate();
	}
		
	public void TimerInit(final int whichDate) {
		
		oneHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
		
						DateChange(whichDate);
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(oneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public synchronized void DateDisplay() { // displaying date parameter
		
		yearText.setText(Integer.toString(year));
		monthText.setText(Integer.toString(month));
		dayText.setText(Integer.toString(day));
		
		btnState = false;
	}
	
	public void GetCurrDate() { // acquiring date parameter displayed
		
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);
		
		DateDisplay();
	}
	
	public void GetDate() { // getting the calendar data for date
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);
		
		DateDisplay();
	}
	
	private void DateChange(int whichDate) {
		
		switch(whichDate) {
		
		case SystemSettingActivity.YEAR_UP		:
			c.add(Calendar.YEAR, 1);
			break;
			
		case SystemSettingActivity.YEAR_DOWN	:
			c.add(Calendar.YEAR, -1);
			break;
		
		case SystemSettingActivity.MONTH_UP		:
			c.add(Calendar.MONTH, 1);
			break;
			
		case SystemSettingActivity.MONTH_DOWN	:
			c.add(Calendar.MONTH, -1);
			break;
		
		case SystemSettingActivity.DAY_UP		:
			c.add(Calendar.DAY_OF_MONTH, 1);
			break;
			
		case SystemSettingActivity.DAY_DOWN		:
			c.add(Calendar.DAY_OF_MONTH, -1);
			break;
			
		default		:
			break;
		}
		
		GetDate();
	}
	
	private void DateSave() { // saving the date modified
		
		TimerDisplay.FiftymsPeriod.cancel(); // finishing the running timer 
		
		SystemClock.setCurrentTimeMillis(c.getTimeInMillis());

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.TimerInit(); // starting the timer

		SerialPort.Sleep(1000);
	}
	
	private void WhichIntent(TargetIntent Itn) { // Activity conversion
		
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
