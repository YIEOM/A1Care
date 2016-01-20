package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
		
		int pDataCnt, cDataCnt;
		
		Intent itn = getIntent();
		
		pDataCnt = itn.getIntExtra("PatientDataCnt", 1);
		cDataCnt = itn.getIntExtra("ControlDataCnt", 1);
		
		mDataStorage = new DataStorage();
		
		DeleteData mDeleteData = new DeleteData(pDataCnt, cDataCnt);
		mDeleteData.start();
	}
	
	public class DeleteData extends Thread {
		
		int pDataCnt, cDataCnt;
		
		public DeleteData(int pDataCnt, int cDataCnt) {
			
			this.pDataCnt = pDataCnt;
			this.cDataCnt = cDataCnt;
		}
		
		public void run() {
			
			String filePath = null;
			
			for(int i = 1; i < pDataCnt; i++) {
				
				filePath = mDataStorage.FileCheck(i, FileLoadActivity.PATIENT);
				if(filePath != null) mDataStorage.FileDelete(filePath);
			}
			
			for(int i = 1; i < cDataCnt; i++) {
				
				filePath = mDataStorage.FileCheck(i, FileLoadActivity.CONTROL);
				if(filePath != null) mDataStorage.FileDelete(filePath);
			}
			
			SerialPort.Sleep(5000);
			
			WhichIntent();
		}
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