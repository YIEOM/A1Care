package isens.hba1c_analyzer;

import java.lang.annotation.Target;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.View.FunctionalTestActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RemoveActivity extends Activity {

	public SerialPort mSerialPort;
	public TimerDisplay mTimerDisplay;
	private LanguageModel mLanguageModel;
	
	private Activity activity;
	private Context context;
	
	public AnimationDrawable removeAni;
	private ImageView removeImage,
					  explainTextImage;
	
	public Button snapshotBtn;
	
	public static int PatientDataCnt,
					  ControlDataCnt;
	
	private boolean isSnapshot = false;

	byte[] bitmapBytes;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.remove);

		mSerialPort = new SerialPort();
		
		RemoveInit();
	}
	
	private void setImageId() {
		
		explainTextImage = (ImageView) findViewById(R.id.explainTextImage);
	}
	
	private void setImage(int languageIdx) {
		
		switch(languageIdx) {
		
		case LanguageModel.KO	:
			explainTextImage.setBackgroundResource(R.drawable.remove_text_ko);
			break;
			
		case LanguageModel.EN	:
			explainTextImage.setBackgroundResource(R.drawable.remove_text_en);
			break;
			
		case LanguageModel.ZH:
			explainTextImage.setBackgroundResource(R.drawable.remove_text_zh);
			break;
			
		case LanguageModel.JA	:
			explainTextImage.setBackgroundResource(R.drawable.remove_text_ja);
			break;
			
		default	:
			explainTextImage.setBackgroundResource(R.drawable.remove_text_en);
			break;
		}
	}
	
	public void setButtonId(Activity activity) {
		
		snapshotBtn = (Button)activity.findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
			
				case R.id.snapshotBtn		:
					CaptureScreen mCaptureScreen = new CaptureScreen();
					bitmapBytes = mCaptureScreen.captureScreen(activity);
					
					GpioPort.DoorActState = false;
					GpioPort.CartridgeActState = false;
					
					ActionActivity.CartridgeCheckFlag = 0;
					ActionActivity.DoorCheckFlag = 1;
							
					isSnapshot = true;
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void RemoveInit() {

		this.activity = this;
		this.context = this;
		
		setImageId();
		mLanguageModel = new LanguageModel(activity);
		setImage(mLanguageModel.getSettingLanguage());
		setButtonId(activity);
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.removelayout);
		
		UserAction UserActionObj = new UserAction();
		UserActionObj.start();
	}

	public class UserAction extends Thread {
		
		public void run() {
			
			int whichIntent;
			
			User1stAction();
			
			GpioPort.DoorActState = true;
			GpioPort.CartridgeActState = true;
	
			SerialPort.Sleep(1500);
			
			while(ActionActivity.CartridgeCheckFlag != 0) SerialPort.Sleep(100);
			
			while((ActionActivity.DoorCheckFlag != 1) | (ActionActivity.CartridgeCheckFlag != 0)) SerialPort.Sleep(100);
			
			GpioPort.DoorActState = false;
			GpioPort.CartridgeActState = false;
			
			Intent itn = getIntent();
			whichIntent = itn.getIntExtra("WhichIntent", 0);
			
			if(whichIntent != ResultActivity.COVER_ACTION_ESC) {
					
				if(Barcode.Type.equals("W") || Barcode.Type.equals("X") || Barcode.Type.equals("Y") || Barcode.Type.equals("Z")) ControlDataCnt = itn.getIntExtra("DataCnt", 0);
				else PatientDataCnt = itn.getIntExtra("DataCnt", 0);
					
				DataCntSave();			
			}
						
			removeAni.stop();
			
			switch(whichIntent) {
			
			case ResultActivity.ACTION_ACTIVITY	:
				WhichIntent(activity, context, TargetIntent.Blank);
				break;
			
			case ResultActivity.HOME_ACTIVITY		:	
				changeActivity(activity, context);
				break;
				
			case ResultActivity.COVER_ACTION_ESC	:
				changeActivity(activity, context);
				break;
				
			default	:
				break;
			}
		}
	}
	
	public void User1stAction() { // Cartridge remove animation start
		
		removeImage = (ImageView)findViewById(R.id.removeAct1);
		removeAni = (AnimationDrawable)removeImage.getBackground();
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	removeAni.start();
		            }
		        });
		    }
		}).start();	
	}
	
	public void DataCntSave() { // Saving data number
		
		SharedPreferences DcntPref = getSharedPreferences("Data Counter", MODE_PRIVATE);
		SharedPreferences.Editor edit = DcntPref.edit();
		
		edit.putInt("PatientDataCnt", PatientDataCnt);
		edit.putInt("ControlDataCnt", ControlDataCnt);
		
		edit.commit();
	}
	
	private void changeActivity(Activity activity, Context context) {
		
		if(HomeActivity.MEASURE_MODE == HomeActivity.A1C) {
			
			WhichIntent(activity, context, TargetIntent.Home);
		
		} else {
			
			WhichIntent(activity, context, TargetIntent.FunctionalTest);
		}
	}
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home		:				
			if(!isSnapshot) {
			
				nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
				nextIntent.putExtra("System Check State", state);
			
			} else {
				
				nextIntent = new Intent(context, FileSaveActivity.class);
				nextIntent.putExtra("snapshot", true);
				nextIntent.putExtra("datetime", TimerDisplay.rTime);
				nextIntent.putExtra("bitmap", bitmapBytes);
			}
			break;
			
		case FunctionalTest		:				
			nextIntent = new Intent(getApplicationContext(), FunctionalTestActivity.class);
			nextIntent.putExtra("System Check State", state);
			break;
			
		case Blank		:				
			nextIntent = new Intent(getApplicationContext(), BlankActivity.class);
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
