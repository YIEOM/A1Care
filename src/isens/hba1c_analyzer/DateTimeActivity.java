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
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DateTimeActivity extends Activity {

	final static byte DATE_SETTING = 0,
			  		  TIME_SETTING = 1;
	
	final static byte YEAR_UP     = 1,
			  		  YEAR_DOWN   = 2,
			  		  MONTH_UP    = 3,
			  		  MONTH_DOWN  = 4,
			  		  DAY_UP      = 5,
			  		  DAY_DOWN    = 6,
			  		  HOUR_UP     = 7,
					  HOUR_DOWN   = 8,
					  MINUTE_UP   = 9,
					  MINUTE_DOWN = 10;	
	
	final static int MAX_YEAR = 2035,
					 MIN_YEAR = 2000;
	
	private TimerDisplay mTimerDisplay;
	
	public Handler handler = new Handler();
	public TimerTask oneHundredmsPeriod;
	
	public Timer timer;
	
	private TextView val1stText,
					 val2ndText,
					 val3rdText;
	
	private Calendar c;
	
	private Button backIcon,
				   val1stPBtn,
				   val1stMBtn,
				   val2ndPBtn,
				   val2ndMBtn,
				   val3rdPBtn,
				   val3rdMBtn;
	
	public ImageView titleImage,
					 iconImage;

	public static int WhichDate = 0;
	
	private int year,
				month,
				day,
				currHour,
				hour,
				currMin,
				min,
				ampm;
	
	private String minStr,
				   ampmStr;

	private boolean btnState = false;

	public int itnData;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.datetime);
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
		val1stText = (TextView) findViewById(R.id.val1sttext);
		val2ndText = (TextView) findViewById(R.id.val2ndtext);
		val3rdText = (TextView) findViewById(R.id.val3rdtext);
		
		val1stPBtn = (Button) findViewById(R.id.val1stpbtn);
		val1stMBtn = (Button) findViewById(R.id.val1stmbtn);
		val2ndPBtn = (Button) findViewById(R.id.val2ndpbtn);
		val2ndMBtn = (Button) findViewById(R.id.val2ndmbtn);
		val3rdPBtn = (Button) findViewById(R.id.val3rdpbtn);
		val3rdMBtn = (Button) findViewById(R.id.val3rdmbtn);
		
		/*System Setting Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					backIcon.setEnabled(false);
					
					saveDateTime();
				}
			}
		});
		
		initDate();
	}
	
	public void initDate() {
		
		Intent itn = getIntent();
		itnData = itn.getIntExtra("Date/Time Mode", 0);
		
		switch(itnData) {
			
		case DATE_SETTING :
			displayImage(R.drawable.date_title, R.drawable.date);
			displayDateButton();
			c = Calendar.getInstance();
			getCurrDate();		
			displayDate();
			break;
		
		case TIME_SETTING :
			displayImage(R.drawable.time_title, R.drawable.time);
			displayTimeButton();
			getCurrTime();
			displayTime();
			break;
			
		default :
			break;
		}
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.datetimelayout);
	}
		
	public void displayImage(int title, int icon) {
		
		titleImage.setBackgroundResource(title);
		iconImage.setBackgroundResource(icon);
	}
	
	public void displayDateButton() {
		
		val1stPBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeDate(YEAR_UP);
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

		val1stPBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(YEAR_UP);
				
				return true;
			}
		});
		
		val1stMBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeDate(YEAR_DOWN);
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

		val1stMBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(YEAR_DOWN);
				
				return true;
			}
		});
		
		val2ndPBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeDate(MONTH_UP);
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

		val2ndPBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(MONTH_UP);
				
				return true;
			}
		});
		
		val2ndMBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeDate(MONTH_DOWN);
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

		val2ndMBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(MONTH_DOWN);
				
				return true;
			}
		});
		
		val3rdPBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeDate(DAY_UP);
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

		val3rdPBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(DAY_UP);
				
				return true;
			}
		});

		val3rdMBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeDate(DAY_DOWN);
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

		val3rdMBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(DAY_DOWN);
				
				return true;
			}
		});
	}
	
	public void displayTimeButton() {
		
		val1stPBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeTime(HOUR_UP);
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

		val1stPBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(HOUR_UP);
				
				return true;
			}
		});
		
		val1stMBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeTime(HOUR_DOWN);
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

		val1stMBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(HOUR_DOWN);
				
				return true;
			}
		});
		
		val2ndPBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeTime(MINUTE_UP);
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

		val2ndPBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(MINUTE_UP);
				
				return true;
			}
		});
		
		val2ndMBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					if(!btnState) {
						
						btnState = true;
						
						changeTime(MINUTE_DOWN);
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

		val2ndMBtn.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
			
				initTimer(MINUTE_DOWN);
				
				return true;
			}
		});

		val3rdPBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					changeAmPm();
				}
			}
		});

		val3rdMBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					changeAmPm();
				}
			}
		});
	}
	
	public void initTimer(final int whichDate) {
		
		oneHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
		
						changeDate(whichDate);
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(oneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public synchronized void displayDate() { // displaying date parameter
		
		val1stText.setText(Integer.toString(year));
		val2ndText.setText(Integer.toString(month));
		val3rdText.setText(Integer.toString(day));
		
		btnState = false;
	}
	
	public synchronized void displayTime() { // displaying the modifying time value
		
		val1stText.setText(Integer.toString(hour));
		val2ndText.setText(minStr);
		val3rdText.setText(ampmStr);
		        		
		btnState = false;
	}
	
	public void getCurrDate() { // acquiring date parameter displayed
		
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH) + 1;
		day = c.get(Calendar.DAY_OF_MONTH);
	}
	
	private void changeDate(int whichDate) {
		
		getCurrDate();
		
		switch(whichDate) {
		
		case YEAR_UP	:
			if(year < MAX_YEAR) c.add(Calendar.YEAR, 1);
			break;
			
		case YEAR_DOWN	:
			if(year > MIN_YEAR) c.add(Calendar.YEAR, -1);
			break;
		
		case MONTH_UP	:
			if((year != MAX_YEAR) || (month != 12)) c.add(Calendar.MONTH, 1);
			break;
			
		case MONTH_DOWN	:
			if((year != MIN_YEAR) || (month != 1)) c.add(Calendar.MONTH, -1);
			break;
		
		case DAY_UP		:
			if((year != MAX_YEAR) || (month != 12) || (day != 31)) c.add(Calendar.DAY_OF_MONTH, 1);
			break;
			
		case DAY_DOWN	:
			if((year != MIN_YEAR) || (month != 1) || (day != 1)) c.add(Calendar.DAY_OF_MONTH, -1);
			break;
			
		default		:
			break;
		}
		
		getCurrDate();		
		displayDate();
	}
	
	public void getCurrTime() { // getting the current time data
		
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
		
//		Log.w("getcurrdate", "" + c.getTimeInMillis())
	}

	public void changeTime(int whichTime) {
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		switch(whichTime) {
		
		case HOUR_UP		:
			if(hour < 12) {
				hour += 1;
			} else {
				hour = 1;
			}
			break;
			
		case HOUR_DOWN	:
			if(hour > 1) {
				hour -= 1;
			} else {
				hour = 12;
			}
			break;
			
		case MINUTE_UP	:
			if(min < 59) {
				min += 1;
			} else {
				min = 0;
			}
			minStr = dfm.format(min);
			break;
			
		case MINUTE_DOWN	:
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
		
		displayTime();
	}
	
	public void changeAmPm() { // changing the am/pm
		
		if(ampm == 0) {
			ampmStr = "PM";
			ampm = 1;
		} else {
			ampmStr = "AM";
			ampm = 0;
		}
		
		displayTime();
	}
	
	private void saveDateTime() { // saving the date modified
				
		if(itnData == TIME_SETTING) {
			
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
		}
		
		TimerDisplay.FiftymsPeriod.cancel(); // finishing the running timer 
		
		SystemClock.setCurrentTimeMillis(c.getTimeInMillis());

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.TimerInit(); // starting the timer

		SaveDateTime mSaveDateTime = new SaveDateTime();
		mSaveDateTime.start();
	}
	
	public class SaveDateTime extends Thread {
		
		public void run() {
			
			SerialPort.Sleep(1000);
			
			whichIntent(TargetIntent.SystemSetting);
		}
	}
	
	private void whichIntent(TargetIntent Itn) { // Activity conversion
		
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
