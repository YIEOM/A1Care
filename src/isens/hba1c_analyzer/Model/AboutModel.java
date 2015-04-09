package isens.hba1c_analyzer.Model;

import android.app.Activity;
import isens.hba1c_analyzer.RunActivity;

public class AboutModel {
	
	private Activity activity;
	
	public AboutModel(Activity activity) {
		
		this.activity = activity;
	}
	
	public void setFactor(String version) {
		
		FileSystem mFileSystem = new FileSystem(activity);
		mFileSystem.setPreferences("Analyzer", Activity.MODE_PRIVATE);
		mFileSystem.putStringPref("HW version", version);
		mFileSystem.commitPref();
	}
}
