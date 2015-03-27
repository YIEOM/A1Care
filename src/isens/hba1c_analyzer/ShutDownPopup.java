package isens.hba1c_analyzer;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShutDownPopup {

	public Activity activity;
	public Context context;
	public int layoutid;
	
	public View popupView;
	public PopupWindow popupWindow = null;
	public RelativeLayout hostLayout;
	
	public ImageView shutDownIcon;
	public TextView shutDownText;
	
	public ShutDownPopup(Activity activity, Context context, int layoutid) {
		
		this.activity = activity;
		this.context = context;
		this.layoutid = layoutid;
	}
	
	public void ShutDownDisplay() {
		
		hostLayout = (RelativeLayout) activity.findViewById(R.id.homelayout);
		popupView = View.inflate(context, R.layout.shutdownpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);
	
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
}
