package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FileDeleteActivity extends Activity {

	public DataStorage mDataStorage;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);
		
		DataDelete();
	}
	
	public void DataDelete() {
		
		int patientDataCnt,	controlDataCnt;
		
		String filePath = null;
		
		Intent itn = getIntent();
		
		patientDataCnt = itn.getIntExtra("PatientDataCnt", 1);
		controlDataCnt = itn.getIntExtra("ControlDataCnt", 1);
		
		mDataStorage = new DataStorage();
		
		for(int i = 1; i < patientDataCnt; i++) {
			
			filePath = mDataStorage.FileCheck(i, FileLoadActivity.PATIENT);
			if(filePath != null) mDataStorage.FileDelete(filePath);
		}
		
		for(int i = 1; i < controlDataCnt; i++) {
			
			filePath = mDataStorage.FileCheck(i, FileLoadActivity.CONTROL);
			if(filePath != null) mDataStorage.FileDelete(filePath);
		}
		
		WhichIntent();
	}
	
	public void WhichIntent() {
	
		Intent nextIntent = null;
		
		nextIntent = new Intent(getApplicationContext(), EngineerActivity.class);
		startActivity(nextIntent);
				
		finish();
	}
	
	public void finish() {
		
		super.finish();
	}
}