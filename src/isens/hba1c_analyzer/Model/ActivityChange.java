package isens.hba1c_analyzer.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import isens.hba1c_analyzer.BlankActivity;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.EngineerActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SettingActivity;
import isens.hba1c_analyzer.SystemSettingActivity;

public class ActivityChange {

	Activity activity;
	Context context;
	Intent nextIntent = null, currIntent = null;
	
	public ActivityChange(Activity activity, Context context) {
		
		this.activity = activity;
		this.context = context;
	}
	
	public void whichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case SystemSetting	:				
			nextIntent = new Intent(context, SystemSettingActivity.class);
			break;
						
		case Engineer	:
			nextIntent = new Intent(context, EngineerActivity.class);
			break;
			
		case Setting	:				
			nextIntent = new Intent(context, SettingActivity.class);
			break;
						
		case Home	:
			nextIntent = new Intent(context, HomeActivity.class);
			break;
			
		case Blank	:				
			nextIntent = new Intent(context, BlankActivity.class);
			break;
					
		default		:	
			break;			
		}
	}
	
	public void putIntIntent(String name, int data) {
		
		nextIntent.putExtra(name, data);
	}
	
	public void putStringIntent(String name, String data) {

		nextIntent.putExtra(name, data);		
	}
	
	public void setIntent() {
		
		currIntent = activity.getIntent();
	}
	
	public int getIntIntent(String name, int defaultValue) {
		
		return currIntent.getIntExtra(name, defaultValue);
	}
	
	public String getStringIntent(String name) {
		
		return currIntent.getStringExtra(name);
	}
	
	public void finish() {
		
		activity.startActivity(nextIntent);
		activity.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
