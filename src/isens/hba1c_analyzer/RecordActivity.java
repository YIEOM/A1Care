package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.CaptureScreen;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordActivity extends Activity {

	final static byte CONTROL = 1,
					  PATIENT = 2;
	
	public TimerDisplay mTimerDisplay;
	
	private Activity activity;
	private Context context;
	
	private TextView titleText;
	
	private Button patientBtn,
				   controlBtn,
				   backIcon,
				   snapshotBtn;
	
	public static int DataPage;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.record);			
		
		MemoryInit();
	}
	
	private void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
	}
	
	private void setText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.recordtitle);
	}
	
	public void setButtonId(Activity activity) {
		
		patientBtn = (Button)activity.findViewById(R.id.patientbtn);
		controlBtn = (Button)activity.findViewById(R.id.controlbtn);
		backIcon = (Button)activity.findViewById(R.id.backicon);
	}
	
	public void setButton(Activity activity) {
		
		patientBtn.setPaintFlags(patientBtn.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		patientBtn.setText(R.string.patientdata);
		controlBtn.setPaintFlags(controlBtn.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		controlBtn.setText(R.string.controldata);
		snapshotBtn = (Button)activity.findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		patientBtn.setOnTouchListener(mTouchListener);
		controlBtn.setOnTouchListener(mTouchListener);
		backIcon.setOnTouchListener(mTouchListener);
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
				unenabledAllBtn(activity);
				
				switch(v.getId()) {
				
				case R.id.patientbtn	:
					WhichIntent(activity, context, TargetIntent.PatientFileLoad);
					break;
					
				case R.id.controlbtn	:
					WhichIntent(activity, context, TargetIntent.ControlFileLoad);
					break;
				
				case R.id.backicon		:
					WhichIntent(activity, context, TargetIntent.Home);
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
	
	public void enabledAllBtn(Activity activity) {

		setButtonState(R.id.patientbtn, true, activity);
		setButtonState(R.id.controlbtn, true, activity);
		setButtonState(R.id.backicon, true, activity);
	}
	
	public void unenabledAllBtn(Activity activity) {
		
		setButtonState(R.id.patientbtn, false, activity);
		setButtonState(R.id.controlbtn, false, activity);
		setButtonState(R.id.backicon, false, activity);
	}
	
	public void MemoryInit() {

		activity = this;
		context = this;
		
		setTextId();
		setText();
		setButtonId(activity);
		setButton(activity);
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.memorylayout);
		
		DataPage = 0;
		
		SerialPort.Sleep(500);
		
		setButtonClick();
	}

	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home				:
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
			
		case ControlFileLoad	:
			nextIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
			nextIntent.putExtra("DataCnt", RemoveActivity.ControlDataCnt); // delivering recent data number
			nextIntent.putExtra("DataPage", DataPage);
			nextIntent.putExtra("Type", (int) CONTROL);
			nextIntent.putExtra("System Check State", (int) RunActivity.NORMAL_OPERATION);
			break;
			
		case PatientFileLoad	:
			nextIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
			nextIntent.putExtra("DataCnt", RemoveActivity.PatientDataCnt); // delivering recent data number
			nextIntent.putExtra("DataPage", DataPage);
			nextIntent.putExtra("Type", (int) PATIENT);
			nextIntent.putExtra("System Check State", (int) RunActivity.NORMAL_OPERATION);
			break;
			
		case SnapShot	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			nextIntent = new Intent(context, FileSaveActivity.class);
			nextIntent.putExtra("snapshot", true);
			nextIntent.putExtra("datetime", TimerDisplay.rTime);
			nextIntent.putExtra("bitmap", bitmapBytes);
			break;
			
		default					:
			break;			
		}
		
		startActivity(nextIntent);
		finish();		
	}
	
	public void finish() {
		
		super.finish();
	}
}
