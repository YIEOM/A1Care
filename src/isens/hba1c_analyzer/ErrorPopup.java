package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.SoundModel;
import isens.hba1c_analyzer.Presenter.FunctionalTestPresenter;
import isens.hba1c_analyzer.View.FunctionalTestActivity;
import isens.hba1c_analyzer.View.LampActivity;
import isens.hba1c_analyzer.View.SoundActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ErrorPopup {
	
	public HomeActivity mHomeActivity;
	public BlankActivity mBlankActivity;
	public ActionActivity mActionActivity;
	public RunActivity mRunActivity;
	public ResultActivity mResultActivity;
	public OperatorPopup mOperatorPopup;
	public SystemSettingActivity mSystemSettingActivity;
	public LampCopyActivity mLampCopyActivity;
	public EngineerActivity mEngineerActivity;
	public SoundModel mSoundModel;
	public OperatorSettingActivity mOperatorSettingActivity;
	public ActivityChange mActivityChange;
	
	public Activity activity;
	public Context context;
	public int layoutId, error, mode;
	
	public View popupView, hostPopView;
	public PopupWindow popupWindow = null;
	public RelativeLayout hostLayout;
	
	public TextView errorText;
	public Button errorBtn,
	  			  snapshotBtn;
	
	public TextView oxText;
	public Button yesBtn, 
	   			  noBtn;
	
	public ErrorPopup(Activity activity, Context context, int layoutId, View popupView, int mode) {
		
		this.activity = activity;
		this.context = context;
		this.layoutId = layoutId;
		this.hostPopView = popupView;
		this.mode = mode;
	}
	
	public void setDisplayId() {
		
		hostLayout = (RelativeLayout) activity.findViewById(layoutId);
		popupView = View.inflate(context, R.layout.errorpopup, null);
		popupWindow = new PopupWindow(popupView, 800 , 480, true);
	
		errorText = (TextView) popupView.findViewById(R.id.errortext);
		errorBtn = (Button) popupView.findViewById(R.id.errorbtn);
		yesBtn = (Button) popupView.findViewById(R.id.yesbtn);
		noBtn = (Button) popupView.findViewById(R.id.nobtn);
		snapshotBtn = (Button) popupView.findViewById(R.id.snapshotBtn2);
	}
		
	public void setErrorButtonClick() {
		
		errorBtn.setBackgroundResource(R.drawable.popup_button_selector);
		errorBtn.setOnTouchListener(mErrorTouchListener);
		snapshotBtn.setOnTouchListener(mErrorTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		popupView.findViewById(btnId).setEnabled(state);
	}
	
	public void enabledAllBtn() {

		setButtonState(R.id.errorbtn, true);
		setButtonState(R.id.yesbtn, true);
		setButtonState(R.id.nobtn, true);
	}
	
	public void unenabledAllBtn() {
		
		setButtonState(R.id.errorbtn, false);
		setButtonState(R.id.yesbtn, false);
		setButtonState(R.id.nobtn, false);
	}
	
	Button.OnTouchListener mErrorTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.errorbtn		:
					ErrorBtnPopupClose();
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
	
		
	public void setOXButtonClick() {
		
		yesBtn.setBackgroundResource(R.drawable.popup_button_selector);
		yesBtn.setOnTouchListener(mOXTouchListener);
		noBtn.setBackgroundResource(R.drawable.popup_button_selector);
		noBtn.setOnTouchListener(mOXTouchListener);
		snapshotBtn.setOnTouchListener(mErrorTouchListener);
	}
	
	Button.OnTouchListener mOXTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn();
				
				switch(v.getId()) {
						
				case R.id.yesbtn	:
					OPopupClose();
					break;
				
				case R.id.nobtn		:
					XPopupClose();
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
	
	public void ErrorBtnDisplay(final int error) {
		
		this.error = error;

		if(popupWindow == null) {
					
			setDisplayId();
			
			setErrorBtnDisplay(error);
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
				}
			});
			
			mSoundModel = new SoundModel(activity, context);
			mSoundModel.playSound(R.raw.beep);
		
		} else {
			
			hostLayout.post(new Runnable() {
				public void run() {
				
					setErrorBtnDisplay(error);
				}
			});
		}
	}
	
	public void setErrorBtnDisplay(int error) {
		
		errorText.setText(error);
		setErrorButtonClick();
		
		yesBtn.setVisibility(View.GONE);
		noBtn.setVisibility(View.GONE);
	}
	
	public void ErrorBtnPopupClose() {
		
		switch(layoutId) {
		
		case R.id.homelayout	:
			ErrorPopupClose();
			
			if(error == R.string.e221) {
				
				mHomeActivity = new HomeActivity();
				mHomeActivity.WhichIntent(activity, context, TargetIntent.SystemCheck);
				
			} else if(error != R.string.w005 && error != R.string.w011 && error != R.string.w018) {
				
				mHomeActivity = new HomeActivity();
				mHomeActivity.Login(activity, context, layoutId);
			
			} else {
				
				mOperatorPopup = new OperatorPopup(activity, context, layoutId);
				mOperatorPopup.enabledAllLoginBtn(hostPopView);
			}
			
			break;
		
		case R.id.functionalTestLayout	:
			ErrorPopupClose();
			break;
			
		case R.id.actionlayout	:
			ErrorPopupClose();
			mActionActivity = new ActionActivity();
			mActionActivity.startScan(activity, context, layoutId);
			break;
			
		case R.id.lampLayout	:
			ErrorPopupClose();
			mLampCopyActivity = new LampCopyActivity();
			mLampCopyActivity.cancelTest();
			mLampCopyActivity.enabledAllBtn();
			
		case R.id.resultlayout	:
			ErrorPopupClose();
			mResultActivity = new ResultActivity();
			mResultActivity.enabledAllBtn(activity);
			break;
			
		case R.id.operatorlayout	:
			ErrorPopupClose();
			
			mOperatorPopup = new OperatorPopup(activity, context, layoutId);
			
			switch(mode) {
			
			case OperatorSettingActivity.LOGIN	:
				mOperatorPopup.enabledAllLoginBtn(hostPopView);
				break;
				
			case OperatorSettingActivity.ADD	:
			case OperatorSettingActivity.MODIFY	:
				mOperatorPopup.enabledAllAddModBtn(hostPopView);
				break;
			}
			break;
			
		case R.id.record2Layout	:
			ErrorPopupClose();
			break;
			
		default	:
			break;
		}
	}

	public void ErrorDisplay(final int error) {
		
		this.error = error;
		
		if(popupWindow == null) {
		
			setDisplayId();
			
			setErrorDisplay(error);
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
				}
			});
		
			mSoundModel = new SoundModel(activity, context);
			mSoundModel.playSound(R.raw.beep);
			
		} else {
			
			hostLayout.post(new Runnable() {
				public void run() {
				
					setErrorDisplay(error);
				}
			});
		}		
	}
	
	public void setErrorDisplay(int error) {
		
		errorText.setText(error);
		
		errorBtn.setVisibility(View.GONE);
		yesBtn.setVisibility(View.GONE);
		noBtn.setVisibility(View.GONE);
	}
	
	public void ErrorPopupClose() {
		
		if(popupWindow != null) {
			
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	public void OXBtnDisplay(final int error) {
		
		this.error = error;
		
		if(popupWindow == null) {
		
			setDisplayId();
			
			setOXBtnDisplay(error);
			
			hostLayout.post(new Runnable() {
				public void run() {
			
					popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
					popupWindow.setAnimationStyle(0);
				}
			});
			
		} else {
			
			hostLayout.post(new Runnable() {
				public void run() {
				
					setOXBtnDisplay(error);
				}
			});
		}
	}
	
	public void setOXBtnDisplay(int error) {
		
		errorText.setText(error);
		
		errorBtn.setVisibility(View.GONE);
		
		setOXButtonClick();
	}
	
	public void OPopupClose() {
		
		switch(layoutId) {
		
		case R.id.homelayout	:
			ErrorPopupClose();			
			mHomeActivity = new HomeActivity();
			mHomeActivity.shutDown(activity, context, layoutId);
			mHomeActivity.setButtonState(R.id.escicon, true, activity);	//0624		
			break;
		
		case R.id.blanklayout	:
			ErrorDisplay(R.string.wait);
			mBlankActivity = new BlankActivity();
			mBlankActivity.BlankStop();
			mBlankActivity.enabledAllBtn(activity);
			break;
			
		case R.id.actionlayout	:
			ErrorPopupClose();			
			ActionActivity.ESCButtonFlag = true;
			mActionActivity = new ActionActivity();
			mActionActivity.enabledAllBtn(activity);
			break;
			
		case R.id.runlayout		:
			ErrorDisplay(R.string.wait);
			mRunActivity = new RunActivity();
			mRunActivity.RunStop();
			mRunActivity.enabledAllBtn(activity);
			break;
			
		case R.id.engineerlayout	:
			ErrorPopupClose();
			mEngineerActivity = new EngineerActivity();
			mEngineerActivity.WhichIntent(activity, TargetIntent.Delete);
			mEngineerActivity.enabledAllBtn(activity);
			break;
			
		default	:
			break;
		}
	}

	public void XPopupClose() {
		
		ErrorPopupClose();
		
		switch(layoutId) {
		
		case R.id.homelayout	:
			mHomeActivity = new HomeActivity();
			mHomeActivity.enabledAllBtn(activity);	//0624		
			break;
			
		case R.id.blanklayout	:
			mBlankActivity = new BlankActivity();
			mBlankActivity.enabledAllBtn(activity);	//0624
			break;
			
		case R.id.actionlayout	:
			ActionActivity.IsEnablePopup = false;
			mBlankActivity = new BlankActivity();
			mBlankActivity.enabledAllBtn(activity);	//0624
			break;
				
		case R.id.runlayout		:
			mRunActivity = new RunActivity();
			mRunActivity.enabledAllBtn(activity);	//0624
			break;
			
		case R.id.engineerlayout	:
			mEngineerActivity = new EngineerActivity();
			mEngineerActivity.enabledAllBtn(activity);
			break;
			
		default	:
			break;
		}
	}
	
	public void closePopupSnapshot() {
		
		CaptureScreen mCaptureScreen = new CaptureScreen();
		byte[] bitmapBytes = mCaptureScreen.captureScreen(activity, popupView);
		
		ErrorPopupClose();
		
		switch(layoutId) {
		
		case R.id.homelayout	:
			mHomeActivity = new HomeActivity();
			mHomeActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mHomeActivity.enabledAllBtn(activity);
			break;
			
		case R.id.blanklayout	:
			mBlankActivity = new BlankActivity();
			mBlankActivity.BlankStop();
			mBlankActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mBlankActivity.enabledAllBtn(activity);
			break;
			
		case R.id.actionlayout	:
			ActionActivity.IsEnablePopup = false;
			mActionActivity = new ActionActivity();
			mActionActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mActionActivity.enabledAllBtn(activity);
			break;
				
		case R.id.runlayout		:
			mRunActivity = new RunActivity();
			mRunActivity.RunStop();
			mRunActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mRunActivity.enabledAllBtn(activity);
			break;
			
		case R.id.resultlayout	:
			mResultActivity = new ResultActivity();
			mResultActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mResultActivity.enabledAllBtn(activity);
			break;
			
		case R.id.operatorlayout	:
			mOperatorSettingActivity = new OperatorSettingActivity();
			mOperatorSettingActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
			mOperatorSettingActivity.enabledAllBtn(activity);
			break;
			
		case R.id.functionalTestLayout	:
			mActivityChange = new ActivityChange(activity, context);
			mActivityChange.whichIntent(TargetIntent.SnapShot);
			mActivityChange.putBooleanIntent("snapshot", true);
			mActivityChange.putStringsIntent("datetime", TimerDisplay.rTime);
			mActivityChange.putBytesIntent("bitmap", bitmapBytes);
			mActivityChange.finish();
			break;
			
		default	:
			break;
		}
	}
}
