package isens.hba1c_analyzer.Model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import isens.hba1c_analyzer.R;

public class LocationModel {
	
	private FileSystem mFileSystem;
	
	private Activity activity;
	
	public static char LocationCode;
	private int locationCode;
	
	public LocationModel(Activity activity) {
		
		this.activity = activity;
		locationCode = (int) LocationCode;
	}
	
	public int getBtnID(int code) {
		
		int btnId = R.id.lct_1st1stBtn;
		
		if(code < 91) btnId = R.id.lct_1st1stBtn + (code - 65); // ASCII code : A~Z (65~90)
		else btnId = R.id.lct_4th3rdBtn + (code - 97); // ASCII code : a~z (97~122)
		
		return btnId;
	}
	
	public int getCode(int btnId) {
		
		int code = 'A';
		
		if(btnId < R.id.lct_4th3rdBtn) code = 'A' + btnId - R.id.lct_1st1stBtn; // ASCII code : A~Z (65~90)
		else code = 'a' + btnId - R.id.lct_4th3rdBtn; // ASCII code : a~z (97~122)

		return code;
	}
	
	public boolean isChangeCode(int btnId) {
		
		boolean isChange = false;
		
		if(getBtnID(locationCode) == btnId) isChange = false;
		else isChange = true;
		
		return isChange;
	}
	
	public int getDeselectedBtnID(int btnId) {
	
		int deselectedBtnId = getBtnID(locationCode);
		
		locationCode = getCode(btnId);
		
		return deselectedBtnId;
	}
	
	public void setCode() {
		
		LocationCode = (char) locationCode;
		
		mFileSystem = new FileSystem(activity);
		mFileSystem.setPreferences("Code", Activity.MODE_PRIVATE);
		mFileSystem.putIntPref("Location", locationCode);
		mFileSystem.commitPref();
	}
}
