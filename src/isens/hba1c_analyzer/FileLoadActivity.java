package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
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

public class FileLoadActivity extends Activity {

	private DataStorage mDataStorage;
	private FileSaveActivity mFileSaveActivity;

	private String fileDateTime  [] = new String[5],
			   	   fileTestNum   [] = new String[5],
				   fileRefNum    [] = new String[5],
				   filePatientID [] = new String[5],
				   fileOperatorID[] = new String[5],
				   filePrimary   [] = new String[5],
				   fileHbA1c     [] = new String[5],
				   fileType      [] = new String[5];
			   
	private Intent itn;
	
	private String filePath = "",
				   loadData;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		init();
	}

	private void init() {
		
		mDataStorage = new DataStorage();
		
		itn = getIntent();
		int mode = itn.getIntExtra("Mode", 0);
		FileSaveActivity.DataCnt = itn.getIntExtra("DataCnt", 0);
		
		if(mode != FileSaveActivity.SYSTEMCHECK) {
			
			LoadData mLoadData = new LoadData();
			mLoadData.start();
			
		} else {
			
			CheckData mCheckData = new CheckData(itn.getStringArrayListExtra("MeasureStrData"), itn.getIntegerArrayListExtra("MeasureIntData"), itn.getStringArrayListExtra("HistoryStrData"));
			mCheckData.start();
		}
	}
	
	public class LoadData extends Thread {
		
		public void run() {
			
			StringInit();
			
			FileLoad(itn.getIntExtra("Mode", 0), itn.getIntExtra("DataPage", 0));
		}
	}
	
	public void StringInit(){
		
		for(int i = 0; i < 5; i++) {
			
			fileDateTime  [i] = null;
			fileTestNum   [i] = null;
			fileType	  [i] = null;
			fileRefNum    [i] = null;
			filePatientID [i] = null;
			fileOperatorID[i] = null;
			filePrimary   [i] = null;
			fileHbA1c     [i] = null;
		}
	}

	public void FileLoad(int type, int dataPage) { // loading 10 recently saved data

		int	pIdx,
			pLen,
			oIdx,
			oLen;

		for (int i = 0; i < 5 ; i++) {

			filePath = mDataStorage.FileCheck(dataPage*5 + i + 1, type);

			if(filePath != null) { // If file exist

				loadData = mDataStorage.DataLoad(filePath);

				try {

					pIdx = 25 + 2;
					pLen = Integer.parseInt(loadData.substring(25, pIdx));
					oIdx = pIdx + pLen + 2;
					oLen = Integer.parseInt(loadData.substring(pIdx + pLen, oIdx));

					fileDateTime  [i] = loadData.substring(0, 4) + loadData.substring(4, 6) + loadData.substring(6, 8) + loadData.substring(8, 10) +
									loadData.substring(10, 12) + loadData.substring(12, 14);
					fileTestNum   [i] = loadData.substring(14, 18);
					fileType	  [i] = loadData.substring(18, 19);
					fileRefNum    [i] = loadData.substring(19, 25);
					filePatientID [i] = loadData.substring(pIdx, pIdx + pLen);
					fileOperatorID[i] = loadData.substring(oIdx, oIdx + oLen);
					filePrimary   [i] = loadData.substring(oIdx + oLen, oIdx + oLen + 1);
					fileHbA1c     [i] = loadData.substring(oIdx + oLen + 1);

				} catch(StringIndexOutOfBoundsException e) {

					fileDateTime  [i] = null;
					fileTestNum   [i] = null;
					fileType	  [i] = null;
					fileRefNum    [i] = null;
					filePatientID [i] = null;
					fileOperatorID[i] = null;
					filePrimary   [i] = null;
					fileHbA1c     [i] = null;
				}
			}
		}

		WhichIntent(type, 0, 0);
	}

	public class CheckData extends Thread {

		ArrayList<String> dataStrArrayList = new ArrayList<String>();
		ArrayList<Integer> dataIntArrayList = new ArrayList<Integer>();
		ArrayList<String> historyDataStrArrayList = new ArrayList<String>();

		public CheckData(ArrayList<String> dataStrArrayList, ArrayList<Integer> dataIntArrayList, ArrayList<String> historyDataStrArrayList) {

			mFileSaveActivity = new FileSaveActivity();
			this.dataStrArrayList = dataStrArrayList;
			this.dataIntArrayList = dataIntArrayList;
			this.historyDataStrArrayList = historyDataStrArrayList;
		}

		public void run() {

			int testType = 0,
				dataCnt = 0;

			if(dataIntArrayList.get(0) == FileSaveActivity.NORMAL_RESULT) {

				String type = dataStrArrayList.get(6);
				FileSaveActivity.DataCnt = dataIntArrayList.get(1);

				if(type.equals("W") || type.equals("X") || type.equals("Y") || type.equals("Z")) {

					mDataStorage.DataSave(FileSaveActivity.CONTROL_TEST, mFileSaveActivity.getTempDataCnt(dataIntArrayList.get(1)), mFileSaveActivity.getLastData(dataStrArrayList, dataIntArrayList));

					dataCnt = dataIntArrayList.get(1) + 1;
					testType = FileSaveActivity.CONTROL_TEST;

				} else {

					mDataStorage.DataSave(FileSaveActivity.PATIENT_TEST, mFileSaveActivity.getTempDataCnt(dataIntArrayList.get(1)), mFileSaveActivity.getLastData(dataStrArrayList, dataIntArrayList));

					dataCnt = dataIntArrayList.get(1) + 1;
					testType = FileSaveActivity.PATIENT_TEST;
				}
			}

			String savedData = mDataStorage.DataLoad(mDataStorage.SDCardState() + DataStorage.SAVE_DIRECTORY + DataStorage.SAVE_HIS_FILENAME + ".txt");
			String dateTime = dataStrArrayList.get(0) + dataStrArrayList.get(1) + dataStrArrayList.get(2) + dataStrArrayList.get(3) + dataStrArrayList.get(4) + dataStrArrayList.get(5);

			if(!dateTime.equals("")) {

				try {

					if(!savedData.substring(0, 14).equals(dateTime)) mDataStorage.DataHistorySave(mFileSaveActivity.getLastData(dataStrArrayList, dataIntArrayList), mFileSaveActivity.getLastHistoryData(historyDataStrArrayList));

				} catch(StringIndexOutOfBoundsException e) {

					mDataStorage.DataHistorySave(mFileSaveActivity.getLastData(dataStrArrayList, dataIntArrayList), mFileSaveActivity.getLastHistoryData(historyDataStrArrayList));
				}
			}

			WhichIntent(FileSaveActivity.SYSTEMCHECK, testType, dataCnt);
		}
	}

	public void WhichIntent(int type, int testType, int dataCnt) { // Activity conversion

		Intent nextIntent = null;
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);

		switch(type) {

			case (int) FileSaveActivity.CONTROL_TEST	:
				nextIntent = new Intent(getApplicationContext(), ControlTestActivity.class);
				nextIntent.putExtra("DateTime", fileDateTime);
				nextIntent.putExtra("TestNum", fileTestNum);
				nextIntent.putExtra("RefNumber", fileRefNum);
				nextIntent.putExtra("PatientID", filePatientID);
				nextIntent.putExtra("OperatorID", fileOperatorID);
				nextIntent.putExtra("Primary", filePrimary);
				nextIntent.putExtra("HbA1c", fileHbA1c);
				nextIntent.putExtra("Mode", fileType);
				break;

			case (int) FileSaveActivity.PATIENT_TEST	:
				nextIntent = new Intent(getApplicationContext(), PatientTestActivity.class);
				nextIntent.putExtra("DateTime", fileDateTime);
				nextIntent.putExtra("TestNum", fileTestNum);
				nextIntent.putExtra("RefNumber", fileRefNum);
				nextIntent.putExtra("PatientID", filePatientID);
				nextIntent.putExtra("OperatorID", fileOperatorID);
				nextIntent.putExtra("Primary", filePrimary);
				nextIntent.putExtra("HbA1c", fileHbA1c);
				nextIntent.putExtra("Mode", fileType);
				break;

			case (int) FileSaveActivity.SYSTEMCHECK	:
				nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
				nextIntent.putExtra("test type", testType);
				nextIntent.putExtra("data cnt", dataCnt);
				break;

			default	:
				break;
		}

		nextIntent.putExtra("System Check State", state);
		startActivity(nextIntent);

		finish();
	}
	
	public void finish() {
		
		super.finish();
	}
}