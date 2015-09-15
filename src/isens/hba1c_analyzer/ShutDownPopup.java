package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShutDownPopup {

	private HomeActivity mHomeActivity;
	
	public Activity activity;
	public Context context;
	public int layoutid;
	
	public View popupView;
	public PopupWindow popupWindow = null;
	public RelativeLayout hostLayout;
	
	public ImageView shutDownIcon;
	public TextView shutDownText;
	
	public Button snapshotBtn;
	
	public ShutDownPopup(Activity activity, Context context, int layoutid) {
		
		this.activity = activity;
		this.context = context;
		this.layoutid = layoutid;
	}
	
	public void setButtonId() {
		
		snapshotBtn = (Button) popupView.findViewById(R.id.snapshotBtn2);
	}
		
	public void setButtonClick() {
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		popupView.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
			
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
	
	public void ShutDownDisplay() {
		
		hostLayout = (RelativeLayout) activity.findViewById(R.id.homelayout);
		popupView = View.inflate(context, R.layout.shutdownpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);
	
		setButtonId();
		setButtonClick();
		
		shutDownIcon = (ImageView) popupView.findViewById(R.id.icon);
		shutDownText = (TextView) popupView.findViewById(R.id.text);
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void setText(final int text) {
		
		hostLayout.post(new Runnable() {
			public void run() {
			
				shutDownText.setText(text);
			}
		});
	}
	
	public void setImage(final int image) {
		
		hostLayout.post(new Runnable() {
			public void run() {
			
				shutDownIcon.setBackgroundResource(image);
			}
		});
	}
	
	public void ErrorPopupClose() {
		
		if(popupWindow != null) {
			
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	public void closePopupSnapshot() {
		
		CaptureScreen mCaptureScreen = new CaptureScreen();
		byte[] bitmapBytes = mCaptureScreen.captureScreen(activity, popupView);
		
		ErrorPopupClose();
		
		mHomeActivity = new HomeActivity();
		mHomeActivity.WhichIntentforSnapshot(activity, context, bitmapBytes);
		mHomeActivity.enabledAllBtn(activity);
	}
}
