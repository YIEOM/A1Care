package isens.hba1c_analyzer;

import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.CalibrationActivity.TargetMode;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.View.FunctionalTestActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	public OperatorPopup mOperatorController;
	
	public Activity activity;
	
	public Button systemBtn,
				  dataBtn,
				  operatorBtn,
				  functionalBtn,
				  backIcon;
	
	public Handler handler = new Handler();
	public TimerTask OneHundredmsPeriod;
	public Timer timer;
	
	public Button cheat1Btn,
				  cheat2Btn;

	public Button btn;
	
	public static boolean isC1Pressed = false,
						  isC2Pressed = false;
	
	public boolean isC1Running = true;
	public boolean isCheat = false;
	public int cnt;
	public int longClickTime;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting);
		
		SettingInit();
	}
	
	public void setButtonId(Activity activity) {
		
		systemBtn = (Button)activity.findViewById(R.id.systembtn);
		operatorBtn = (Button)activity.findViewById(R.id.operatorbtn);
		functionalBtn = (Button)activity.findViewById(R.id.functionalbtn);
		backIcon = (Button)activity.findViewById(R.id.backicon);
		cheat1Btn = (Button)activity.findViewById(R.id.cheat1btn);
		cheat2Btn = (Button)activity.findViewById(R.id.cheat2btn);
	}
	
	public void setButtonClick() {
		
		systemBtn.setOnTouchListener(mTouchListener);
		operatorBtn.setOnTouchListener(mTouchListener);
		functionalBtn.setOnTouchListener(mTouchListener);
		backIcon.setOnTouchListener(mTouchListener);
		cheat1Btn.setOnTouchListener(mTouchListener);
		cheat2Btn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn(); //0624
				
				switch(v.getId()) {
				
				case R.id.systembtn		:
					TakeOffBtn();
					WhichIntent(TargetIntent.SystemSetting);
					break;
					
				case R.id.operatorbtn	:
					TakeOffBtn();
					WhichIntent(TargetIntent.OperatorSetting);
					break;
				
				case R.id.functionalbtn	:
					TakeOffBtn();
					WhichIntent(TargetIntent.FunctionalTest);
					break;
				
				case R.id.backicon	:
					WhichIntent(TargetIntent.Home);
					break;
					
				case R.id.cheat1btn	:
					Cheat1stModeStart();
					enabledAllBtn(activity); //0624
					break;
				
				case R.id.cheat2btn	:
					TakeOffBtn();
					enabledAllBtn(activity); //0624
					break;
					
				default	:
					break;
				}
			
				break;
				
			case MotionEvent.ACTION_DOWN	:
									
				switch(v.getId()) {
			
				case R.id.cheat2btn	:
					PressedBtn();
					break;
					
				default	:
					break;
				}
		
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllBtn(Activity activity) {

		setButtonState(R.id.systembtn, true, activity);
		setButtonState(R.id.operatorbtn, true, activity);
		setButtonState(R.id.functionalbtn, true, activity);
		setButtonState(R.id.backicon, true, activity);
		setButtonState(R.id.cheat1btn, true, activity);
		setButtonState(R.id.cheat2btn, true, activity);
	}
	
	public void unenabledAllBtn() {
		
		setButtonState(R.id.systembtn, false, activity);
		setButtonState(R.id.operatorbtn, false, activity);
		setButtonState(R.id.functionalbtn, false, activity);
		setButtonState(R.id.backicon, false, activity);
		setButtonState(R.id.cheat1btn, false, activity);
		setButtonState(R.id.cheat2btn, false, activity);
	}
	
	public void SettingInit() {
		
		activity = this;
		
		setButtonId(activity);
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.settinglayout);
	}
	
	public void PressedBtn() {
		
		if(!isC2Pressed && isC1Pressed) {
			
			isC2Pressed = true;
			isCheat = false;
			longClickTime = cnt;
		}
	}
	
	public void TakeOffBtn() {
		
		CheatModeStop(this, isCheat);
	}
	
	public void TimerInit() {
		
		OneHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						cnt++;
						
						if(!isC2Pressed) CheatMode();
						else Cheat2ndModeStart();
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public void Cheat1stModeStart() {
		
		if(!isC1Pressed) {
			
			BtnColor(this, R.id.cheat1btn, "#3004A458");
		
			isC1Pressed = true;
			
			cnt = 0;
			
			TimerInit();
		}
	}
	
	public void Cheat2ndModeStart() {
		
		int hundredmsCnt = cnt - longClickTime;
		
		BtnColor(this, R.id.cheat2btn, "#30023894");
		
		if(hundredmsCnt > 30) {
			
			isCheat = true;
			
			timer.cancel();
			
			mOperatorController = new OperatorPopup(this, this, R.id.settinglayout);
			mOperatorController.EngineerLoginDisplay();
		}
	}
	
	public void CheatModeStop(Activity activity, boolean isCheat) {
		
		if(isC1Pressed) {
			
			BtnColor(activity, R.id.cheat1btn, "#00000000");
		
			isC1Pressed = false;
			
			if(!isCheat) timer.cancel();
		}
		
		if(isC2Pressed) {
			
			BtnColor(activity, R.id.cheat2btn, "#00000000");
			
			isC2Pressed = false;
		}
	}
	
	public void CheatMode() {
		
		if(cnt == 50) {
			
			CheatModeStop(this, isCheat);
		}
	}
	
	public void BtnColor(final Activity activity, final int id, final String color) {
		
		new Thread(new Runnable() {			
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {

						btn = (Button) activity.findViewById(id);
 	
						btn.setBackgroundColor(Color.parseColor(color));
					}
				});
			}
		}).start();
	}

	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home				:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
						
		case SystemSetting		:
			nextIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
			break;
			
		case DataSetting		:
			nextIntent = new Intent(getApplicationContext(), DataSettingActivity.class);
			break;			
			
		case OperatorSetting	:		
			nextIntent = new Intent(getApplicationContext(), OperatorSettingActivity.class);
			break;
			
		case FunctionalTest	:		
			nextIntent = new Intent(getApplicationContext(), FunctionalTestActivity.class);
			break;
			
		default		:	
			break;			
		}
		
		startActivity(nextIntent);
		finish(this);		
	}
	
	public void MaintenanceIntent(Activity activity, Context context) { // Activity conversion
		
		isC1Pressed = false;
		isC2Pressed = false;
		
		Intent MaintenanceIntent = new Intent(context, EngineerActivity.class);
		activity.startActivity(MaintenanceIntent);
		
		finish(activity);		
	}
	
	public void finish(Activity activity) {
		
		super.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
