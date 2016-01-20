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

public class FileLoadActivity extends Activity {

	public DataStorage mDataStorage;

	final static byte CONTROL = 1,
					  PATIENT = 2,
					  SYSTEMCHECK = 8;
	
	private String fileDateTime  [] = new String[5],
			   	   fileTestNum   [] = new String[5],
				   fileRefNum    [] = new String[5],
				   filePatientID [] = new String[5],
				   fileOperatorID[] = new String[5],
				   filePrimary   [] = new String[5],
				   fileHbA1c     [] = new String[5],
				   fileType      [] = new String[5];
			   
	String filePath = "",
		   loadData;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		init();
	}
	
	private void init() {
	
		mDataStorage = new DataStorage();
		
		Intent itn = getIntent();
		int mode = itn.getIntExtra("Mode", 0);
		FileSaveActivity.DataCnt = itn.getIntExtra("DataCnt", 0);
		
		if(mode != SYSTEMCHECK) {
			
			StringInit();
			
			FileLoad(itn.getIntExtra("Mode", 0), itn.getIntExtra("DataPage", 0));
			
		} else {
			
			CheckData mCheckData = new CheckData(itn.getIntExtra("pDataCnt", 1), itn.getIntExtra("cDataCnt", 1));
			mCheckData.start();
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
					
					pIdx = 24 + 2;
					pLen = Integer.parseInt(loadData.substring(24, pIdx));
					oIdx = pIdx + pLen + 2;
					oLen = Integer.parseInt(loadData.substring(pIdx + pLen, oIdx));
					
					fileDateTime  [i] = loadData.substring(0, 4) + loadData.substring(4, 6) + loadData.substring(6, 8) + loadData.substring(8, 10) + 
									loadData.substring(10, 12) + loadData.substring(12, 14);
					fileTestNum   [i] = loadData.substring(14, 18);
					fileType	  [i] = loadData.substring(18, 19);
					fileRefNum    [i] = loadData.substring(19, 24);
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
		
		int pDataCnt,
			cDataCnt;
		
		public CheckData(int pDataCnt, int cDataCnt) {
			
			this.pDataCnt = pDataCnt;
			this.cDataCnt = cDataCnt;
		}
		
		public void run() {
			
			int savepDataCnt = 1,
				savecDataCnt = 1;
			
			FileSaveActivity.DataCnt = pDataCnt;
			
			for(int i = 0; i < pDataCnt; i++) {
				
				filePath = mDataStorage.FileCheck(i+1, PATIENT);
				
				if(filePath != null) { // If file exist
					
					try {
					
						Integer.parseInt(mDataStorage.DataLoad(filePath).substring(14, 18));
						savepDataCnt = pDataCnt - i;
						break;
						
					} catch(StringIndexOutOfBoundsException e) {
						
						mDataStorage.FileDelete(filePath);
					}
				}
			}
			
			FileSaveActivity.DataCnt = cDataCnt;
			
			for(int i = 0; i < cDataCnt; i++) {
				
				filePath = mDataStorage.FileCheck(i+1, CONTROL);
				
				if(filePath != null) { // If file exist
					
					try {
					
						Integer.parseInt(mDataStorage.DataLoad(filePath).substring(14, 18));
						savecDataCnt = cDataCnt - i; 
						break;
						
					} catch(StringIndexOutOfBoundsException e) {
						
						mDataStorage.FileDelete(filePath);
					}
				}
			}
			
			WhichIntent(SYSTEMCHECK, savepDataCnt, savecDataCnt);
		}
	}
	
	public void WhichIntent(int type, int pDataCnt, int cDataCnt) { // Activity conversion
		
		Intent nextIntent = null;
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);
		
		switch(type) {
		
		case CONTROL	:
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
			
		case PATIENT	:
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
			
		case SYSTEMCHECK	:
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			nextIntent.putExtra("pDataCnt", pDataCnt);
			nextIntent.putExtra("cDataCnt", cDataCnt);
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