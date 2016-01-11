package isens.hba1c_analyzer.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import isens.hba1c_analyzer.BlankActivity;
import isens.hba1c_analyzer.FileSaveActivity;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.EngineerActivity;
import isens.hba1c_analyzer.FileLoadActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RemoveActivity;
import isens.hba1c_analyzer.SettingActivity;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.View.ExportActivity;
import isens.hba1c_analyzer.View.RecordActivity;

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
					
		case FileSave	:
			nextIntent = new Intent(context, FileSaveActivity.class);
			break;
			
		case PatientFileLoad	:
		case ControlFileLoad	:
			nextIntent = new Intent(context, FileLoadActivity.class);
			break;
			
		case Record	:
			nextIntent = new Intent(context, RecordActivity.class);
			break;
			
		case Export	:
			nextIntent = new Intent(context, ExportActivity.class);
			break;
			
		default		:	
			break;			
		}
	}
	
	public void putBooleanIntent(String name, boolean data) {
			
		nextIntent.putExtra(name, data);
	}
		
	public void putBytesIntent(String name, byte[] data) {
		
		nextIntent.putExtra(name, data);
	}

	public void putIntIntent(String name, int data) {
		
		nextIntent.putExtra(name, data);
	}
	
	public void putStringIntent(String name, String data) {

		nextIntent.putExtra(name, data);		
	}
	
	public void putStringsIntent(String name, String[] data) {

		nextIntent.putExtra(name, data);		
	}
	
	public void putTrgIntent(String name, TargetIntent target) {

		nextIntent.putExtra(name, target);		
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
	
	public String[] getStringArrayIntent(String name) {
		
		return currIntent.getStringArrayExtra(name);
	}
	
	public TargetIntent getTrgIntent(String name) {
		
		return (TargetIntent) currIntent.getSerializableExtra(name);
	}
	
	public void finish() {
		
		activity.startActivity(nextIntent);
		activity.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
