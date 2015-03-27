package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.View.LampActivity;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
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
	public OperatorController mOperatorController;
	public SystemSettingActivity mSystemSettingActivity;
	public LampCopyActivity mLampCopyActivity;
	
	public Activity activity;
	public Context context;
	public int layoutid, error;
	
	public View popupView;
	public PopupWindow popupWindow = null;
	public RelativeLayout hostLayout;
	
	public TextView errorText;
	public Button errorBtn;
	
	public TextView oxText;
	public Button yesBtn, 
	   			  noBtn;
	
	public ErrorPopup(Activity activity, Context context, int layoutid) {
		
		this.activity = activity;
		this.context = context;
		this.layoutid = layoutid;
	}
	
	public void setDisplayId() {
		
		hostLayout = (RelativeLayout) activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.errorpopup, null);
		popupWindow = new PopupWindow(popupView, 800 , 480, true);
	
		errorText = (TextView) popupView.findViewById(R.id.errortext);
		errorBtn = (Button) popupView.findViewById(R.id.errorbtn);
		yesBtn = (Button) popupView.findViewById(R.id.yesbtn);
		noBtn = (Button) popupView.findViewById(R.id.nobtn);
	}
	
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
		
		errorBtn.setBackgroundResource(R.drawable.popup_button_selector);
		errorBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				ErrorBtnPopupClose();
			}
		});
		
		yesBtn.setBackgroundResource(0);
		noBtn.setBackgroundResource(0);
	}
	
	public void ErrorBtnPopupClose() {
		
		switch(layoutid) {
		
		case R.id.homelayout	:
			ErrorPopupClose();
			
			if(error != R.string.w005 && error != R.string.w011 && error != R.string.w018) {
				
				mHomeActivity = new HomeActivity();
				mHomeActivity.Login(activity, context, layoutid);	
			}
			break;
			
		case R.id.actionlayout	:
			ErrorPopupClose();
			mActionActivity = new ActionActivity();
			mActionActivity.ActionInit(activity, context);
			break;
			
		case R.id.lampLayout	:
			ErrorPopupClose();
			mLampCopyActivity = new LampCopyActivity();
			mLampCopyActivity.cancelTest();
			
		case R.id.resultlayout	:
			ErrorPopupClose();
			break;
			
		case R.id.operatorlayout	:
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
		
		errorBtn.setBackgroundResource(0);
		yesBtn.setBackgroundResource(0);
		noBtn.setBackgroundResource(0);
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
		
		errorBtn.setBackgroundResource(0);
		
		yesBtn.setBackgroundResource(R.drawable.popup_yes_selector);
		yesBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				OPopupClose();
			}
		});

		noBtn.setBackgroundResource(R.drawable.popup_no_selector);
		noBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				XPopupClose();
			}
		});
	}
	
	public void OPopupClose() {
		
		switch(layoutid) {
		
		case R.id.homelayout	:
			ErrorPopupClose();			
			mHomeActivity = new HomeActivity();
			mHomeActivity.shutDown(activity, context, layoutid);
			break;
		
		case R.id.blanklayout	:
			ErrorDisplay(R.string.wait);
			mBlankActivity = new BlankActivity();
			mBlankActivity.BlankStop();
			break;
			
		case R.id.actionlayout	:
			ErrorPopupClose();			
			mActionActivity = new ActionActivity();
			mActionActivity.WhichIntent(activity, context, TargetIntent.Remove);
			break;
			
		case R.id.runlayout		:
			ErrorDisplay(R.string.wait);
			mRunActivity = new RunActivity();
			mRunActivity.RunStop();
			break;
			
		case R.id.systemsettinglayout	:
			ErrorPopupClose();			
			mSystemSettingActivity = new SystemSettingActivity();
			mSystemSettingActivity.SettingParameterInit();
			
		default	:
			break;
		}
	}

	public void XPopupClose() {
		
		ErrorPopupClose();
		
		switch(layoutid) {
		
		case R.id.actionlayout	:
			ActionActivity.IsEnablePopup = false;
			break;
			
		default	:
			break;
		}
	}
}
