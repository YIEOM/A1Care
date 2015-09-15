package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.CaptureScreen;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class OperatorSettingActivity extends Activity {
	
	final static byte PRE_VIEW  = 0,
					  NEXT_VIEW = 1,
					  LOGIN = 2,
					  ADD = 3,
					  MODIFY = 4;
	
	public OperatorPopup mOperatorPopup;
	public DatabaseHander mDatabaseHander;
	public TimerDisplay mTimerDisplay;
	
	public Activity activity;
	private Context context;
	
	public TextView titleText,
					pageText,
					modifyText;
	
	private Button homeIcon,
				   backIcon,
				   addOperatorBtn,
				   modOperatorBtn,
				   delOperatorBtn,
				   nextViewBtn,
				   preViewBtn,
				   loginBtn,
				   snapshotBtn;
	
	private ImageButton checkBoxBtn1,
						checkBoxBtn2,
						checkBoxBtn3,
						checkBoxBtn4,
						checkBoxBtn5;
	
	private TextView OperatorText[] = new TextView[5],
			 		 DateTimeText[] = new TextView[5],
			 		 PasswordText[] = new TextView[5],
			 		 CommentText [] = new TextView[5];
	
	private boolean checkFlag = false;
	private ImageButton whichBox = null;

	private int boxNum = 0;
	private static int pageNum = 1;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.operatorsetting);
		
		OperatorInit();
	}
	
	private void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		modifyText = (TextView) findViewById(R.id.modifyText);
	}
	
	private void setText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.operatorsettingtitle);
		modifyText.setPaintFlags(modifyText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		modifyText.setText(R.string.modify);
	}
	
	public void setButtonId() {
		
		homeIcon = (Button)findViewById(R.id.homeicon);
		preViewBtn = (Button)findViewById(R.id.previousviewbtn);
		loginBtn = (Button)findViewById(R.id.loginbtn);
		modOperatorBtn = (Button)findViewById(R.id.modifybtn);
		addOperatorBtn = (Button)findViewById(R.id.addbtn);
		delOperatorBtn = (Button)findViewById(R.id.deletebtn);
		nextViewBtn = (Button)findViewById(R.id.nextviewbtn);
		backIcon = (Button)findViewById(R.id.backicon);
		snapshotBtn = (Button)activity.findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		homeIcon.setOnTouchListener(mTouchListener);
		preViewBtn.setOnTouchListener(mTouchListener);
		loginBtn.setOnTouchListener(mTouchListener);
		modOperatorBtn.setOnTouchListener(mTouchListener);
		addOperatorBtn.setOnTouchListener(mTouchListener);
		delOperatorBtn.setOnTouchListener(mTouchListener);
		nextViewBtn.setOnTouchListener(mTouchListener);
		backIcon.setOnTouchListener(mTouchListener);
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	public void setImageButtonId() {
		
		checkBoxBtn1 = (ImageButton) findViewById(R.id.chdckbox1);
		checkBoxBtn2 = (ImageButton) findViewById(R.id.chdckbox2);
		checkBoxBtn3 = (ImageButton) findViewById(R.id.chdckbox3);
		checkBoxBtn4 = (ImageButton) findViewById(R.id.chdckbox4);
		checkBoxBtn5 = (ImageButton) findViewById(R.id.chdckbox5);
	}
	
	public void setImageButtonClick() {
		
		checkBoxBtn1.setOnTouchListener(mImageTouchListener);
		checkBoxBtn2.setOnTouchListener(mImageTouchListener);
		checkBoxBtn3.setOnTouchListener(mImageTouchListener);
		checkBoxBtn4.setOnTouchListener(mImageTouchListener);
		checkBoxBtn5.setOnTouchListener(mImageTouchListener);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn(activity);

				switch(v.getId()) {
			
				case R.id.homeicon	:
					WhichIntent(activity, context, TargetIntent.Home);
					break;
				
				case R.id.previousviewbtn	:
					TurnPage(PRE_VIEW);
					enabledAllBtn(activity);
					break;
				
				case R.id.loginbtn	:
					Login();
					break;
				
				case R.id.modifybtn	:
					Modify();
					break;
				
				case R.id.addbtn	:
					Add();
					break;
				
				case R.id.deletebtn	:
					Delete();
					break;
				
				case R.id.nextviewbtn	:
					TurnPage(NEXT_VIEW);
					enabledAllBtn(activity); //0624
					break;
				
				case R.id.backicon	:
					WhichIntent(activity, context, TargetIntent.Setting);
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

		setButtonState(R.id.homeicon, true, activity);
		setButtonState(R.id.previousviewbtn, true, activity);
		setButtonState(R.id.loginbtn, true, activity);
		setButtonState(R.id.modifybtn, true, activity);
		setButtonState(R.id.addbtn, true, activity);
		setButtonState(R.id.deletebtn, true, activity);
		setButtonState(R.id.nextviewbtn, true, activity);
		setButtonState(R.id.backicon, true, activity);
	}
	
	public void unenabledAllBtn(Activity activity) {
		
		setButtonState(R.id.homeicon, false, activity);
		setButtonState(R.id.previousviewbtn, false, activity);
		setButtonState(R.id.loginbtn, false, activity);
		setButtonState(R.id.modifybtn, false, activity);
		setButtonState(R.id.addbtn, false, activity);
		setButtonState(R.id.deletebtn, false, activity);
		setButtonState(R.id.nextviewbtn, false, activity);
		setButtonState(R.id.backicon, false, activity);
	}
	
	ImageButton.OnTouchListener mImageTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
				
				case R.id.chdckbox1	:
					boxNum = 1;
					PressedCheckBox(checkBoxBtn1);
					break;
					
				case R.id.chdckbox2	:
					boxNum = 2;
					PressedCheckBox(checkBoxBtn2);
					break;
				
				case R.id.chdckbox3	:
					boxNum = 3;
					PressedCheckBox(checkBoxBtn3);
					break;
				
				case R.id.chdckbox4	:
					boxNum = 4;
					PressedCheckBox(checkBoxBtn4);
					break;
				
				case R.id.chdckbox5	:
					boxNum = 5;
					PressedCheckBox(checkBoxBtn5);
					break;
				}		
				
				break;
			}
			
			return false;
		}
	};
	
	public void OperatorInit() {
		
		int count;
		
		activity = this;
		context = this;
		
		setTextId();
		setText();
		setButtonId();
		setImageButtonId();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.operatorlayout);
		
		mOperatorPopup = new OperatorPopup(this, getApplicationContext(), R.id.operatorlayout);
		count = mOperatorPopup.OperatorCount();
		OperatorDisplay(this, mOperatorPopup.ReadOperator(count), count, count);
		
		SerialPort.Sleep(500);
		
		setButtonClick();
		setImageButtonClick();		
	}

	public void Login() {
		
		String id = null;
		
		if(checkFlag) id = OperatorText[boxNum - 1].getText().toString();
		
		mOperatorPopup = new OperatorPopup(this, this, R.id.operatorlayout);
		mOperatorPopup.OperatorLoginDisplay(id);
	}
	
	public void Add() {
		
		mOperatorPopup = new OperatorPopup(this, this, R.id.operatorlayout);
		mOperatorPopup.AddOperatorDisplay();
	}
	
	public void Modify() {
		
		if(checkFlag && !OperatorText[boxNum - 1].getText().toString().equals("")) {
		
			mOperatorPopup = new OperatorPopup(this, this, R.id.operatorlayout);
			mOperatorPopup.ModOperatorDisplay(OperatorText[boxNum - 1].getText().toString());
		
		} else enabledAllBtn(activity);
	}
	
	public void Delete() {
		
		if(checkFlag && !OperatorText[boxNum - 1].getText().toString().equals("")) {
			
			mOperatorPopup = new OperatorPopup(this, this, R.id.operatorlayout);
			mOperatorPopup.DelOperatorDisplay(OperatorText[boxNum - 1].getText().toString());
		
		} else enabledAllBtn(activity);
	}
	
	public void PressedCheckBox(ImageButton box) { // displaying the button pressed
		
		if(!checkFlag) { // whether or not box is checked
	
			checkFlag = true;
			box.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
			
		} else {

			whichBox.setBackgroundResource(R.drawable.checkbox); // changing to not checked box

			if(whichBox == box) {
				
				checkFlag = false;
				
			} else {
				
				box.setBackgroundResource(R.drawable.checkbox_s);
			}
		}
		
		whichBox = box;
	}
	
	public void OperatorText(Activity activity) {
		
		OperatorText[0] = (TextView) activity.findViewById(R.id.operator1);
		DateTimeText[0] = (TextView) activity.findViewById(R.id.dateTime1);
		PasswordText[0] = (TextView) activity.findViewById(R.id.password1);
		CommentText [0] = (TextView) activity.findViewById(R.id.comment1);
		
		OperatorText[1] = (TextView) activity.findViewById(R.id.operator2);
		DateTimeText[1] = (TextView) activity.findViewById(R.id.dateTime2);
		PasswordText[1] = (TextView) activity.findViewById(R.id.password2);
		CommentText [1] = (TextView) activity.findViewById(R.id.comment2);
		
		OperatorText[2] = (TextView) activity.findViewById(R.id.operator3);
		DateTimeText[2] = (TextView) activity.findViewById(R.id.dateTime3);
		PasswordText[2] = (TextView) activity.findViewById(R.id.password3);
		CommentText [2] = (TextView) activity.findViewById(R.id.comment3);
		
		OperatorText[3] = (TextView) activity.findViewById(R.id.operator4);
		DateTimeText[3] = (TextView) activity.findViewById(R.id.dateTime4);
		PasswordText[3] = (TextView) activity.findViewById(R.id.password4);
		CommentText [3] = (TextView) activity.findViewById(R.id.comment4);
		
		OperatorText[4] = (TextView) activity.findViewById(R.id.operator5);
		DateTimeText[4] = (TextView) activity.findViewById(R.id.dateTime5);
		PasswordText[4] = (TextView) activity.findViewById(R.id.password5);
		CommentText [4] = (TextView) activity.findViewById(R.id.comment5);
		
		pageText = (TextView) activity.findViewById(R.id.pagetext);
	}

	public void OperatorDisplay(Activity activity, String[][] Operator, int last, int numofRow) {
		
		int first,
			dataCnt;
		
		if(last > 5) {
			
			first = last - 5;
			dataCnt = 5;
		
		} else {
			
			first = 0;
			dataCnt = last;
		}
		
		OperatorText(activity);
		
		for(int i = 0; i < 5; i++) {
			
			OperatorText[i].setText("");
			DateTimeText[i].setText("");
			PasswordText[i].setText("");
			CommentText [i].setText("");
		}
		
		for(int i = 0; i < dataCnt; i++) {	
			
			OperatorText[i].setText(Operator[0][i]);
			DateTimeText[i].setText(Operator[1][i]);
			PasswordText[i].setText(Operator[2][i]);
			CommentText [i].setText("N/A");
		}
		
		if(last == numofRow) pageNum = 1;
		
		int tPage = (numofRow+4)/5;
		if(tPage == 0) tPage = 1;
		String page = Integer.toString(pageNum) + " / " + Integer.toString(tPage);
		
		pageText.setText(page);
	}
	
	public void TurnPage(int direction) {
		
		int count,
			last,
			tPage;
		
		mOperatorPopup = new OperatorPopup(this, this, R.id.operatorlayout);
		count = mOperatorPopup.OperatorCount();
		
		switch(direction) {
		
		case PRE_VIEW	:
			if(pageNum > 1) {
				pageNum--;
				last = count-((pageNum-1)*5);
				OperatorDisplay(this, mOperatorPopup.ReadOperator(last), last, count);
			}
			break;
			
		case NEXT_VIEW	:
			tPage = (count+4)/5;
			
			if(tPage > pageNum) {
				pageNum++;
				last = count-((pageNum-1)*5);
				OperatorDisplay(this, mOperatorPopup.ReadOperator(last), last, count);
			}
			break;
			
		default	:
			break;
		}
	}
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home		:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
						
		case Setting	:				
			nextIntent = new Intent(getApplicationContext(), SettingActivity.class);
			break;
			
		case SnapShot	:
			CaptureScreen mCaptureScreen = new CaptureScreen();
			byte[] bitmapBytes = mCaptureScreen.captureScreen(activity);
			
			nextIntent = new Intent(context, FileSaveActivity.class);
			nextIntent.putExtra("snapshot", true);
			nextIntent.putExtra("datetime", TimerDisplay.rTime);
			nextIntent.putExtra("bitmap", bitmapBytes);
			break;
			
		default		:	
			break;			
		}	
		
		startActivity(nextIntent);
		finish(activity);
	}
	
	public void WhichIntentforSnapshot(Activity activity, Context context, byte[] bitmapBytes) {
		
		Intent nextIntent = null;
		
		nextIntent = new Intent(context, FileSaveActivity.class);
		nextIntent.putExtra("snapshot", true);
		nextIntent.putExtra("datetime", TimerDisplay.rTime);
		nextIntent.putExtra("bitmap", bitmapBytes);
		
		activity.startActivity(nextIntent);
		finish(activity);
	}
	
	public void finish(Activity activity) {
		
		super.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}