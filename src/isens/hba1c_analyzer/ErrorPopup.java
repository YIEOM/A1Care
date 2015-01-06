package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
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
	public ActionActivity mActionActivity;
	public RunActivity mRunActivity;
	public OperatorController mOperatorController;
	public SystemSettingActivity mSystemSettingActivity;
	public LampActivity mLampActivity;
	
	public Activity activity;
	public Context context;
	public int layoutid, error;
	
	public View popupView;
	public PopupWindow popupWindow;
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
	
	public void ErrorBtnDisplay(int error) {
		
		hostLayout = (RelativeLayout) activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.errorbtnpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);
	
		this.error = error;
		
		errorText = (TextView) popupView.findViewById(R.id.errortext);
		errorText.setText(error);
		
		errorBtn = (Button) popupView.findViewById(R.id.errorbtn);
		errorBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				ErrorBtnPopupClose();
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void ErrorBtnPopupClose() {
		
		popupWindow.dismiss();
	
		switch(layoutid) {
		
		case R.id.homelayout	:
			if(error != R.string.w005 && error != R.string.w011 && error != R.string.w018) {
				
				mHomeActivity = new HomeActivity();
				mHomeActivity.Login(activity, context, layoutid);	
			}
			break;
			
		case R.id.actionlayout	:
			mActionActivity = new ActionActivity();
			mActionActivity.ActionInit(activity, context);
			break;
			
		case R.id.lamplayout	:
			mLampActivity = new LampActivity();
			mLampActivity.TestCancel();
			
		default	:
			break;
		}
	}

	public void ErrorDisplay(int error) {
		
		hostLayout = (RelativeLayout) activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.errorpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);
	
		this.error = error;
		
		errorText = (TextView) popupView.findViewById(R.id.errortext);
		errorText.setText(error);
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void ErrorPopupClose() {
		
		if(popupWindow != null) popupWindow.dismiss();
	}
	
	public void OXBtnDisplay(int error) {
		
		hostLayout = (RelativeLayout) activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.oxpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);
	
		this.error = error;
		
		oxText = (TextView) popupView.findViewById(R.id.oxtext);
		oxText.setText(error);
		
		yesBtn = (Button) popupView.findViewById(R.id.yesbtn);
		yesBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				OPopupClose();
			}
		});
	
		noBtn = (Button) popupView.findViewById(R.id.nobtn);
		noBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				XPopupClose();
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void OPopupClose() {
		
		popupWindow.dismiss();
		
		switch(layoutid) {
		
		case R.id.actionlayout	:
			mActionActivity = new ActionActivity();
			mActionActivity.WhichIntent(activity, context, TargetIntent.Remove);
			break;
			
		case R.id.runlayout		:
			ErrorDisplay(R.string.wait);
			mRunActivity = new RunActivity();
			mRunActivity.RunStop();
			break;
			
		case R.id.systemsettinglayout	:
			mSystemSettingActivity = new SystemSettingActivity();
			mSystemSettingActivity.SettingParameterInit();
			
		default	:
			break;
		}
	}

	public void XPopupClose() {
		
		popupWindow.dismiss();
	}
}
