package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.CustomTextView;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.preference.PreferenceManager;

public class OperatorPopup {
	
	final static int LOGIN = 0,
					 OPERATOR = 1,
					 OPERATOR_ADD = 2,
					 OPERATOR_MOD = 3,
					 ENGINEER = 4;
	
	public DatabaseHander mDatabaseHander;
	public OperatorSettingActivity mOperatorSettingActivity;
	public ErrorPopup mErrorPopup;
	public HomeActivity mHomeActivity;
	public SettingActivity mSettingActivity;
	
	public Activity activity;
	public Context context;
	public int layoutId;
	
	public RelativeLayout hostLayout;
	public View popupView;
	public PopupWindow popupWindow;
	
	public EditText oIDEText,
	 				passEText;

	public CustomTextView loginText;
	
	public Button loginBtn,
				  guestBtn,
		   		  checkBtn,
		   		  snapshotBtn2;
	
	public CustomTextView popupText;
	
	public EditText fct1stEText,
					fct2ndEText,
					fct3rdEText;

	public Button doneBtn,
				  cancelBtn;

	public TextView modOperatorText,
					fct1stText,
					fct2ndText,
					fct3rdText;
	
	public Button delOkBtn,
				  delCancelBtn;

	public TextView delOperatorText;

	public String id;
	public boolean btnState = false;
	
	public OperatorPopup(Activity activity, Context context, int layoutId) {
		
		this.activity = activity;
		this.context = context;
		this.layoutId = layoutId;
	}
	
	public void setLoginTextId(View popupView) {
		
		loginText = new CustomTextView(context);
		loginText = (CustomTextView) popupView.findViewById(R.id.loginText);
	}
	
	public void setLoginText(int mode) {
		
		switch(mode) {
		
		case LOGIN	:
		case OPERATOR	:
			loginText.setPaintFlags(loginText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
			loginText.setTextScaleX(0.9f);
			loginText.setLetterSpacing(3);
			
			loginText.setText(R.string.login);
			break;
				
		case ENGINEER	:
			loginText.setText("Engineer");
			break;
		}
	}
	
	public void setLoginButtonId(View popupView) {
		
		loginBtn = (Button)popupView.findViewById(R.id.loginbtn);
		guestBtn = (Button)popupView.findViewById(R.id.guestbtn);
		checkBtn = (Button)popupView.findViewById(R.id.checkbtn);
		snapshotBtn2 = (Button)popupView.findViewById(R.id.snapshotBtn2);
	}
	
	public void setLoginButtonText(int mode) {
		
		loginBtn.setTextScaleX(0.9f);
		
		switch(mode) {
		
		case LOGIN	:
			guestBtn.setText(R.string.guest);
			break;
			
		case OPERATOR	:				
		case ENGINEER	:
			guestBtn.setText(R.string.cancel);
			break;
		}
	}
	
	public void setLoginButtonClick() {
		
		loginBtn.setOnTouchListener(mLoginTouchListener);
		guestBtn.setOnTouchListener(mLoginTouchListener);
		checkBtn.setOnTouchListener(mLoginTouchListener);
		snapshotBtn2.setOnTouchListener(mLoginTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, View popupView) {
		
		popupView.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mLoginTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllLoginBtn(popupView);
					
				switch(v.getId()) {
			
				case R.id.loginbtn	:
					LoginCheck();
					break;
					
				case R.id.guestbtn	:
					LoginGuest();
					break;
				
				case R.id.checkbtn	:
					CheckBoxDisplay(checkBtn);
					enabledAllLoginBtn(popupView);
					break;
				
				case R.id.snapshotBtn2		:
					closePopupSnapshot();
					break;
					
				default	:
					break;
				}
					
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllLoginBtn(View popupView) {

		setButtonState(R.id.loginbtn, true, popupView);
		setButtonState(R.id.guestbtn, true, popupView);
		setButtonState(R.id.checkbtn, true, popupView);
	}
	
	public void unenabledAllLoginBtn(View popupView) {
		
		setButtonState(R.id.loginbtn, false, popupView);
		setButtonState(R.id.guestbtn, false, popupView);
		setButtonState(R.id.checkbtn, false, popupView);
	}
	
	public void LoginDisplay() {
		
		if(popupWindow == null) {
			
			hostLayout = (RelativeLayout)activity.findViewById(layoutId);
			popupView = View.inflate(context, R.layout.loginpopup, null);
			popupWindow = new PopupWindow(popupView, 800, 480, true);
	
			setLoginTextId(popupView);
			setLoginText(LOGIN);
			
			oIDEText = (EditText) popupView.findViewById(R.id.loginoid);
			passEText = (EditText) popupView.findViewById(R.id.loginpass);
			
			setLoginButtonId(popupView);
			setLoginButtonText(LOGIN);
			setLoginButtonClick();
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
					AutoWriteLogin();
				}
			});
		}
	}
	
	public void setOperatorButtonClick() {
		
		loginBtn.setOnTouchListener(mOperatorTouchListener);
		guestBtn.setOnTouchListener(mOperatorTouchListener);
		checkBtn.setOnTouchListener(mOperatorTouchListener);
		snapshotBtn2.setOnTouchListener(mOperatorTouchListener);
	}

	Button.OnTouchListener mOperatorTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllLoginBtn(popupView);
				
				switch(v.getId()) {
			
				case R.id.loginbtn	:
					LoginCheck();
					break;
					
				case R.id.guestbtn	:
					PopupClose();
					break;
					
				case R.id.checkbtn	:
					CheckBoxDisplay(checkBtn);
					enabledAllLoginBtn(popupView);
					break;
				
				case R.id.snapshotBtn2		:
					closePopupSnapshot();
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public void OperatorLoginDisplay(String id) {
		
		if(popupWindow == null) {
			
			hostLayout = (RelativeLayout)activity.findViewById(layoutId);
			popupView = View.inflate(context, R.layout.loginpopup, null);
			popupWindow = new PopupWindow(popupView, 800, 480, true);
	
			setLoginTextId(popupView);
			setLoginText(OPERATOR);
			
			oIDEText = (EditText) popupView.findViewById(R.id.loginoid);
			passEText = (EditText) popupView.findViewById(R.id.loginpass);
			
			oIDEText.setText(id);
			
			setLoginButtonId(popupView);
			setLoginButtonText(OPERATOR);
			setOperatorButtonClick();
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
					AutoWriteLogin();
				}
			});
		}
	}
	
	public void LoginCheck() {
		
		int Count;
		
		String id = oIDEText.getText().toString(), 
			   password = passEText.getText().toString();
		
		if(!id.equals("")) {
			
			mDatabaseHander = new DatabaseHander(context);
			mDatabaseHander.getWritableDatabase();
			
			if(mDatabaseHander.CheckIDDuplication(id)) {
				password = mDatabaseHander.GetPassword(id);
				
				if(passEText.getText().toString().equals(password)) {
					
					popupWindow.dismiss();
					
					CheckFlagSave();
					
					mDatabaseHander.UpdateLastLogIn(id);
					
					switch(layoutId) {
					
					case R.id.homelayout		:
						HomeActivity.LoginFlag = false;
						
						mHomeActivity = new HomeActivity();
						mHomeActivity.OperatorDisplay(activity, context);
						enabledAllLoginBtn(popupView);
						break;
						
					case R.id.operatorlayout	:
						mOperatorSettingActivity = new OperatorSettingActivity();
						Count = OperatorCount();
						mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);
						enabledAllLoginBtn(popupView);
						mOperatorSettingActivity.enabledAllBtn(activity);
						break;
						
					default	:
						break;
					}
					
				} else {
							
					mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.LOGIN);
					mErrorPopup.ErrorBtnDisplay(R.string.w018);
				}
			} else {
				
				mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.LOGIN);
				mErrorPopup.ErrorBtnDisplay(R.string.w005);
			}
			
		} else {
		
			mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.LOGIN);
			mErrorPopup.ErrorBtnDisplay(R.string.w011);
		}
	}
	
	public void LoginGuest() {
		
		HomeActivity.LoginFlag = false;
		
		mDatabaseHander = new DatabaseHander(context);
		mDatabaseHander.UpdateGuestLogIn();
		
		mHomeActivity = new HomeActivity();
		mHomeActivity.OperatorDisplay(activity, context);
		
		popupWindow.dismiss();
		
		enabledAllLoginBtn(popupView);
	}
	
	public void AutoWriteLogin() {
		
		String id,
			   password;
		
		mDatabaseHander = new DatabaseHander(context);
			
		if(HomeActivity.CheckFlag) {
		
			checkBtn.setBackgroundResource(R.drawable.login_checkbox_check);
		
			if(layoutId == R.id.homelayout) {
			
				id = mDatabaseHander.GetLastLoginID();
				password = mDatabaseHander.GetPassword(id);
				
				oIDEText.setText(id);
				passEText.setText(password);
			}
			
		} else {
			
			checkBtn.setBackgroundResource(R.drawable.login_checkbox);
		}
	}
	
	public void CheckBoxDisplay(Button btn) {
		
		if(HomeActivity.CheckFlag) {
			
			HomeActivity.CheckFlag = false;
			btn.setBackgroundResource(R.drawable.login_checkbox);
			
		} else {
			
			HomeActivity.CheckFlag = true;
			btn.setBackgroundResource(R.drawable.login_checkbox_check);
		}
	}
		
	public void setEngineerButtonClick() {
		
		loginBtn.setOnTouchListener(mEngineerTouchListener);
		guestBtn.setOnTouchListener(mEngineerTouchListener);
	}
	
	Button.OnTouchListener mEngineerTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				unenabledAllLoginBtn(popupView);
					
				switch(v.getId()) {
			
				case R.id.loginbtn	:
					EngineerLoginCheck();
					break;
					
				case R.id.guestbtn	:
					EngineerPopupClose();
					mSettingActivity = new SettingActivity();
					mSettingActivity.enabledAllBtn(activity);
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void EngineerLoginDisplay() {
		
		if(popupWindow == null) {
			
			hostLayout = (RelativeLayout)activity.findViewById(layoutId);
			popupView = View.inflate(context, R.layout.loginpopup, null);
			popupWindow = new PopupWindow(popupView, 800, 480, true);
	
			setLoginTextId(popupView);
			setLoginText(ENGINEER);
			
			oIDEText = (EditText) popupView.findViewById(R.id.loginoid);
			passEText = (EditText) popupView.findViewById(R.id.loginpass);
			passEText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
			passEText.setFilters(new InputFilter [] {new InputFilter.LengthFilter(10)});
			
			oIDEText.setText("ENGINEER");
			
			setLoginButtonId(popupView);
			setLoginButtonText(ENGINEER);
			setEngineerButtonClick();
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
				}
			});
		}
	}
	
	public void EngineerLoginCheck() {
		
		String id = oIDEText.getText().toString(), 
			   password = passEText.getText().toString();
		
		if(id.equals("ENGINEER")) {
	
			if(HomeActivity.ANALYZER_SW != HomeActivity.DEVEL) {
			
				if(password.equals("entry")) {
					
					popupWindow.dismiss();
					
					mSettingActivity = new SettingActivity();
					mSettingActivity.MaintenanceIntent(activity, context);
				}
				
			} else {
				
				popupWindow.dismiss();
				
				mSettingActivity = new SettingActivity();
				mSettingActivity.MaintenanceIntent(activity, context);
			}
		}

		enabledAllLoginBtn(popupView);
	}

	public void EngineerPopupClose() {
		
		mSettingActivity = new SettingActivity();
		mSettingActivity.CheatModeStop(activity, true);
		
		popupWindow.dismiss();
	}
	
	private void setAddModTextId(View popupView) {
		
		popupText = new CustomTextView(context);
		popupText = (CustomTextView) popupView.findViewById(R.id.popupText);
		
		modOperatorText = (TextView) popupView.findViewById(R.id.idText);
		fct1stText = (TextView) popupView.findViewById(R.id.fct1stText);
		fct2ndText = (TextView) popupView.findViewById(R.id.fct2ndText);
		fct3rdText = (TextView) popupView.findViewById(R.id.fct3rdText);
		
		fct1stEText = (EditText) popupView.findViewById(R.id.fct1stEText);
		fct2ndEText = (EditText) popupView.findViewById(R.id.fct2ndEText);
		fct3rdEText = (EditText) popupView.findViewById(R.id.fct3rdEText);
	}
	
	public void setAddModText(int mode, String id) {
		
		popupText.setPaintFlags(popupText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		popupText.setTextScaleX(0.9f);
		popupText.setLetterSpacing(3);
		
		fct1stText.setPaintFlags(fct1stText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		fct2ndText.setPaintFlags(fct2ndText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		fct3rdText.setPaintFlags(fct3rdText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		
		switch(mode) {
			
		case OPERATOR_ADD	:
			popupText.setText(R.string.newaccount);
			fct1stText.setText("ID");
			fct2ndText.setText(R.string.password);
			fct3rdText.setText(R.string.confirmpw);
			break;
			
		case OPERATOR_MOD	:
			modOperatorText.setPaintFlags(modOperatorText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
			modOperatorText.setText(id);
			popupText.setText("ID:");
			fct1stEText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			fct1stEText.setFilters(new InputFilter [] {new InputFilter.LengthFilter(4)});
			fct1stText.setText(R.string.currentpw);
			fct2ndText.setText(R.string.newpw);
			fct3rdText.setText(R.string.confirmpw);
			break;
		}
	}
	
	public void setAddModButtonId(View popupView) {
		
		doneBtn = (Button)popupView.findViewById(R.id.donebtn);
		cancelBtn = (Button)popupView.findViewById(R.id.cancelbtn);
		snapshotBtn2 = (Button) popupView.findViewById(R.id.snapshotBtn2);
	}
	
	public void setAddButtonClick() {
		
		doneBtn.setOnTouchListener(mAddTouchListener);
		cancelBtn.setOnTouchListener(mAddTouchListener);
		snapshotBtn2.setOnTouchListener(mAddTouchListener);
	}
	
	Button.OnTouchListener mAddTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllAddModBtn(popupView);
					
				switch(v.getId()) {
			
				case R.id.donebtn	:
					AddOperator();
					break;
					
				case R.id.cancelbtn	:
					PopupClose();
					enabledAllAddModBtn(popupView);
					break;
				
				case R.id.snapshotBtn2		:
					closePopupSnapshot();
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllAddModBtn(View popupView) {

		setButtonState(R.id.donebtn, true, popupView);
		setButtonState(R.id.cancelbtn, true, popupView);
	}
	
	public void unenabledAllAddModBtn(View popupView) {
		
		setButtonState(R.id.donebtn, false, popupView);
		setButtonState(R.id.cancelbtn, false, popupView);
	}
	
	public void AddOperatorDisplay() {

		if(popupWindow == null) {
			
			hostLayout = (RelativeLayout)activity.findViewById(layoutId);
			popupView = View.inflate(context, R.layout.operatorpopup, null);
			popupWindow = new PopupWindow(popupView, 800, 480, true);
	
			setAddModTextId(popupView);
			setAddModText(OPERATOR_ADD, "");
			setAddModButtonId(popupView);
			setAddButtonClick();
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
				}
			});
		}
	}
	
	public void AddOperator() {
		
		int Count;
		
		String id = fct1stEText.getText().toString(),
			   aPW = fct2ndEText.getText().toString(),
			   cPW = fct3rdEText.getText().toString();
		
		mDatabaseHander = new DatabaseHander(context);
		
		if(!id.equals("")) {
			
			if(!mDatabaseHander.CheckIDDuplication(id) & !id.equals("Guest")) {
				
				if(!aPW.equals("")) {
					
					if(!cPW.equals("")) {
						
						if(aPW.equals(cPW)) {
							 
							mDatabaseHander.AddOperator(id, TimerDisplay.rTime[0] + TimerDisplay.rTime[1] + TimerDisplay.rTime[2] + TimerDisplay.rTime[7] + TimerDisplay.rTime[5], aPW);
							
							PopupClose();
							
							mOperatorSettingActivity = new OperatorSettingActivity();
							Count = OperatorCount();
							mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);

							enabledAllAddModBtn(popupView);
							mOperatorSettingActivity.enabledAllBtn(activity);
						
						} else {
							
							mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.ADD);
							mErrorPopup.ErrorBtnDisplay(R.string.w018);
						}
						
					} else {
						
						mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.ADD);
						mErrorPopup.ErrorBtnDisplay(R.string.w014);
					}
					
				} else {
					
					mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.ADD);
					mErrorPopup.ErrorBtnDisplay(R.string.w013);
				}
				
			} else {
				
				mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.ADD);
				mErrorPopup.ErrorBtnDisplay(R.string.w012);
			}
	
		} else {
			
			mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.ADD);
			mErrorPopup.ErrorBtnDisplay(R.string.w011);
		}
	}
	
	public void setModButtonClick() {
		
		doneBtn.setOnTouchListener(mModTouchListener);
		cancelBtn.setOnTouchListener(mModTouchListener);
		snapshotBtn2.setOnTouchListener(mModTouchListener);
	}
	
	Button.OnTouchListener mModTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllAddModBtn(popupView);
					
				switch(v.getId()) {
			
				case R.id.donebtn	:
					ModOperator();
					break;
					
				case R.id.cancelbtn	:
					PopupClose();						
					enabledAllAddModBtn(popupView);
					break;
				
				case R.id.snapshotBtn2		:
					closePopupSnapshot();
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void ModOperatorDisplay(String id) {
		
		if(popupWindow == null) {
			
			hostLayout = (RelativeLayout) activity.findViewById(layoutId);
			popupView = View.inflate(context, R.layout.operatorpopup, null);
			popupWindow = new PopupWindow(popupView, 800, 480, true);
			
			setAddModTextId(popupView);
			setAddModText(OPERATOR_MOD, id);
			setAddModButtonId(popupView);
			setModButtonClick();
					
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
				}
			});
		}
	}
	
	public void ModOperator() {
	
		int Count;
		
		String id = modOperatorText.getText().toString(), 
			   iPW = fct1stEText.getText().toString(), 
			   nPW = fct2ndEText.getText().toString(), 
			   cPW = fct3rdEText.getText().toString();
		
		mDatabaseHander = new DatabaseHander(context);
		
		if(!iPW.equals("")) {
			
			if(iPW.equals(mDatabaseHander.GetPassword(id))) {
				
				if(!nPW.equals("")) {
					
					if(!cPW.equals("")) {
						
						if(nPW.equals(cPW)) {
							 
							mDatabaseHander.UpdateOperator(id, nPW);
							
							PopupClose();
							
							mOperatorSettingActivity = new OperatorSettingActivity();
							Count = OperatorCount();
							mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);
							mOperatorSettingActivity.enabledAllBtn(activity);
						
						} else {
							
							mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.MODIFY);
							mErrorPopup.ErrorBtnDisplay(R.string.w018);
						}
						
					} else {
						
						mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.MODIFY);
						mErrorPopup.ErrorBtnDisplay(R.string.w014);
					}
					
				} else {
					
					mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.MODIFY);
					mErrorPopup.ErrorBtnDisplay(R.string.w017);
				}
				
			} else {
				
				mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.MODIFY);
				mErrorPopup.ErrorBtnDisplay(R.string.w016);
			}
	
		} else {
			
			mErrorPopup = new ErrorPopup(activity, context, layoutId, popupView, (int)OperatorSettingActivity.MODIFY);
			mErrorPopup.ErrorBtnDisplay(R.string.w013);
		}
	}
	
	public void setDelButtonId(View popupView) {
		
		delOkBtn = (Button)popupView.findViewById(R.id.yesbtn);
		delCancelBtn = (Button)popupView.findViewById(R.id.nobtn);
		snapshotBtn2 = (Button) popupView.findViewById(R.id.snapshotBtn2);
	}
	
	public void setDelButtonClick() {
		
		delOkBtn.setOnTouchListener(mDelTouchListener);
		delCancelBtn.setOnTouchListener(mDelTouchListener);
		snapshotBtn2.setOnTouchListener(mDelTouchListener);
	}
	
	Button.OnTouchListener mDelTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllDelBtn(popupView);
					
				switch(v.getId()) {
			
				case R.id.yesbtn	:
					DelOperator(id);
					break;
					
				case R.id.nobtn	:
					PopupClose();
					enabledAllDelBtn(popupView);
					break;
				
				case R.id.snapshotBtn2		:
					closePopupSnapshot();
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllDelBtn(View popupView) {

		setButtonState(R.id.yesbtn, true, popupView);
		setButtonState(R.id.nobtn, true, popupView);
	}
	
	public void unenabledAllDelBtn(View popupView) {
		
		setButtonState(R.id.yesbtn, false, popupView);
		setButtonState(R.id.nobtn, false, popupView);
	}
	
	public void DelOperatorDisplay(String id) {
		
		if(popupWindow == null) {
			
			hostLayout = (RelativeLayout)activity.findViewById(layoutId);
			popupView = View.inflate(context, R.layout.oxpopup, null);
			popupWindow = new PopupWindow(popupView, 800, 480, true);
			
			delOperatorText = (TextView)popupView.findViewById(R.id.oxtext);
			
			delOperatorText.setText("ID : " + id + " " + context.getResources().getText((R.string.delete)));
			
			this.id = id;
			
			setDelButtonId(popupView);
			setDelButtonClick();
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
				}
			});
		}
	}
	
	public void DelOperator(String id) {
		
		int Count;
		
		mDatabaseHander = new DatabaseHander(context);
		mDatabaseHander.DeleteOperator(id);
		
		PopupClose();
		
		mOperatorSettingActivity = new OperatorSettingActivity();
		Count = OperatorCount();
		mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);
	
		enabledAllDelBtn(popupView);
		mOperatorSettingActivity.enabledAllBtn(activity);
	}
	
	public void PopupClose() {
		
		popupWindow.dismiss();
		mOperatorSettingActivity = new OperatorSettingActivity();
		mOperatorSettingActivity.enabledAllBtn(activity);
	}
	
	public String[][] ReadOperator(int last) {
		
		int first,
			dataCnt;
		
		String tempDate,
			   tempPassword,
			   formDate,
			   formPassword;
		
		String tempHour[] = new String[2],
			   rowData[] = new String[4];
		
		String Operator[][] = new String[4][5];
		
		mDatabaseHander = new DatabaseHander(context);
		
		if(last > 5) {
			
			first = last - 5;
			dataCnt = 5;
		
		} else {
			
			first = 0;
			dataCnt = last;
		}
		
		for(int i = 0; i < dataCnt; i++) {
			
			rowData = mDatabaseHander.GetRowWithNumber(last - (i + 1));
				
			tempDate = rowData[1];
			tempPassword = rowData[2];
				
			tempHour = TimeHandling(tempDate.substring(8, 10));
			
			formDate = tempDate.substring(0, 4) + "." + tempDate.substring(4, 6) + "." + tempDate.substring(6, 8) + "   " + tempHour[1] + ":" + tempDate.substring(10, 12) + " " + tempHour[0];
			formPassword = PasswordHandling(tempPassword);	
			
			Operator[0][i] = rowData[0];
			Operator[1][i] = formDate;
			Operator[2][i] = formPassword;
			Operator[3][i] = rowData[3];
		}
		
		return Operator;
	}
	
	public int OperatorCount() {
		
		int count;
		
		mDatabaseHander = new DatabaseHander(context);
		count = mDatabaseHander.GetRowCount();
		
		return count;
	}
	
	public String[] TimeHandling(String time) {
		
		DecimalFormat dfm = new DecimalFormat("00");
		String pTime[] = new String[2];
		int tempTime = Integer.parseInt(time);
		
		if(tempTime > 12) {
			
			tempTime -= 12;
			pTime[0] = "PM";
			
		} else if((0 < tempTime) && (tempTime < 12)) {
			
			pTime[0] = "AM";
		
		} else if(tempTime == 12) {
			
			pTime[0] = "PM";
		
		} else {
			
			tempTime = 12;
			pTime[0] = "AM";
		}
		
		pTime[1] = dfm.format(tempTime);
		
		return pTime;
	}
	
	public String PasswordHandling(String password) {
		
		String pPassword = password.substring(0, 1);
		
		for(int i = 0; i < (password.length() - 1); i++) {
			
			pPassword = pPassword + "*";
		}
		
		return pPassword;
	}

	public void CheckFlagSave() { // Saving number of user define parameter
		
		SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor loginedit = loginPref.edit();
		
		loginedit.putBoolean("Check Box", HomeActivity.CheckFlag);
		loginedit.commit();
	}
	
	public void closePopupSnapshot() {
		
		CaptureScreen mCaptureScreen = new CaptureScreen();
		byte[] bitmapBytes = mCaptureScreen.captureScreen(activity, popupView);
		
		popupWindow.dismiss();		
		
		switch(layoutId) {
		
		case R.id.homelayout	:
			mHomeActivity = new HomeActivity();
			mHomeActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mHomeActivity.enabledAllBtn(activity);
			break;
			
		case R.id.operatorlayout	:
			mOperatorSettingActivity = new OperatorSettingActivity();
			mOperatorSettingActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mOperatorSettingActivity.enabledAllBtn(activity);
			break;
			
		default	:
			break;
		}
	}
}
