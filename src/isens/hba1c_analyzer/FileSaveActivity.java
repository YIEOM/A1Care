package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.FileSystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FileSaveActivity extends Activity {

	public static final byte NORMAL_RESULT = 0,
					   		 CONTROL_TEST = 1,
					   		 PATIENT_TEST = 2,
					   		 SYSTEMCHECK = 8;
	
	private DataStorage mDataStorage;

	private Intent itn;
	
	public static int DataCnt;

	private int whichState;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		DataInit();
	}

	public void DataInit() {

		mDataStorage = new DataStorage();

		itn = getIntent();
	
		SaveFile mSaveFile = new SaveFile(itn.getStringArrayListExtra("MeasureStrData"), itn.getIntegerArrayListExtra("MeasureIntData"), itn.getStringArrayListExtra("HistoryStrData"));
		mSaveFile.start();
	}
	
	public class SaveFile extends Thread {

		ArrayList<String> dataStrArrayList = new ArrayList<String>();
		ArrayList<Integer> dataIntArrayList = new ArrayList<Integer>();
		ArrayList<String> historyDataStrArrayList = new ArrayList<String>();

		public SaveFile(ArrayList<String> dataStrArrayList, ArrayList<Integer> dataIntArrayList, ArrayList<String> historyDataStrArrayList) {

			this.dataStrArrayList = dataStrArrayList;
			this.dataIntArrayList = dataIntArrayList;
			this.historyDataStrArrayList = historyDataStrArrayList;
		}

		public void run() {

			DataCnt = dataIntArrayList.get(1);
			whichState = itn.getIntExtra("WhichIntent", 0);

			if(!itn.getBooleanExtra("snapshot", false)) {
			
				if(dataIntArrayList.get(0) == (int) NORMAL_RESULT) {

					String dataType = dataStrArrayList.get(6);

					if(dataType.equals("W") || dataType.equals("X") || dataType.equals("Y") || dataType.equals("Z")) mDataStorage.DataSave(CONTROL_TEST, getTempDataCnt(DataCnt), getLastData(dataStrArrayList, dataIntArrayList));
					else mDataStorage.DataSave(PATIENT_TEST, getTempDataCnt(DataCnt), getLastData(dataStrArrayList, dataIntArrayList)); // if HbA1c test is normal, the Result data is saved
				}

				mDataStorage.DataHistorySave(getLastData(dataStrArrayList, dataIntArrayList), getLastHistoryData(historyDataStrArrayList)); // the History data is saved
				
				WhichIntent(false);
				
			} else {
				
				byte[] bytes = itn.getByteArrayExtra("bitmap");
				String[] str = itn.getStringArrayExtra("datetime");
		        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				
		        mDataStorage.saveSnapShot(bmp, str);
		        
		        WhichIntent(true);
			}	
		}
	}

	public StringBuffer getLastData(ArrayList<String> dataStrArrayList, ArrayList<Integer> dataIntArrayList) {

		DecimalFormat dfm = new DecimalFormat("0000");
		int tempDataCnt = getTempDataCnt(dataIntArrayList.get(1));

		StringBuffer overallData = new StringBuffer();
		overallData.delete(0, overallData.capacity());

		for(int i = 0; i < 6; i++) {

			overallData.append(dataStrArrayList.get(i));
		}

		overallData.append(dfm.format(tempDataCnt));

		for(int i = 0; i < 8; i++) {

			overallData.append(dataStrArrayList.get(i+6));
		}

		return overallData;
	}

	public StringBuffer getLastHistoryData(ArrayList<String> dataStrArrayList) {

		StringBuffer historyData = new StringBuffer();
		historyData.delete(0, historyData.capacity());

		for(int i = 0; i < dataStrArrayList.size()-1; i++) {

			historyData.append(dataStrArrayList.get(i) + "\t");
		}

		historyData.append(dataStrArrayList.get(dataStrArrayList.size()-1));

		return historyData;
	}

//	public void DataInit() {
//
//		DataCnt = 1;
//
//		for(int i = 0; i < 9994; i++){
//		DataArray();
//
//		SaveData = new DataStorage();
//
//		itn = getIntent();
//
//		if(dataType.equals("W") || dataType.equals("X") || dataType.equals("Y") || dataType.equals("Z")) SaveData.DataSave(CONTROL_TEST, overallData);
//		else SaveData.DataSave(PATIENT_TEST, overallData); // if HbA1c test is normal, the Result data is saved
//		}
//
//		WhichIntent(false);
//	}

	public int getTempDataCnt(int dataCnt) {

		int tempDataCnt;

		tempDataCnt = dataCnt % 9999;
		if(tempDataCnt == 0) tempDataCnt = 9999;

		return tempDataCnt;
	}

	public void WhichIntent(boolean isSnapshot) { // Activity conversion

		Intent nextIntent = null;

		if(!isSnapshot) {

			nextIntent = new Intent(getApplicationContext(), RemoveActivity.class);
			nextIntent.putExtra("WhichIntent", whichState);

		} else {

			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			nextIntent.putExtra("System Check State", 0);
		}

		startActivity(nextIntent);
		finish();
	}

	public void finish() {

		super.finish();
	}
}
