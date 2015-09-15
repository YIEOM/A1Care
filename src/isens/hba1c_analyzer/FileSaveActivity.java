package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.CaptureScreen;
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

	public static byte NORMAL_RESULT = 0,
					   CONTROL_TEST = 1,
					   PATIENT_TEST = 2;
	
	private DataStorage SaveData;
	
	private TextView Text;
	private Intent itn;
	
	private StringBuffer overallData = new StringBuffer(),
						 historyData = new StringBuffer();

	public static int DataCnt,
					  TempDataCnt;
	
	private int runState,
				whichState;
	
	private String dataType;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);
		
		Text = (TextView) findViewById(R.id.text);
		
		DataInit();
	}
	
	public void DataInit() {
		
		DataArray();
		
		SaveData = new DataStorage();
		
		itn = getIntent();
	
		if(!itn.getBooleanExtra("snapshot", false)) {
		
			if(itn.getIntExtra("RunState", 0) == (int) NORMAL_RESULT) {
		
				if(dataType.equals("W") || dataType.equals("X") || dataType.equals("Y") || dataType.equals("Z")) SaveData.DataSave(CONTROL_TEST, overallData);
				else SaveData.DataSave(PATIENT_TEST, overallData); // if HbA1c test is normal, the Result data is saved
			}
			
			SaveData.DataHistorySave(overallData, historyData); // the History data is saved
			
			WhichIntent(false);
			
		} else {
			
			byte[] bytes = itn.getByteArrayExtra("bitmap");
			String[] str = itn.getStringArrayExtra("datetime");
	        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			
	        SaveData.saveSnapShot(bmp, str);
	        
	        WhichIntent(true);
		}
	}
	
	public void DataArray() { // Enumerating data
		
		DecimalFormat dfm = new DecimalFormat("0000");

		overallData.delete(0, overallData.capacity());
		historyData.delete(0, historyData.capacity());
		
		itn = getIntent();
		whichState = itn.getIntExtra("WhichIntent", 0);
		DataCnt = itn.getIntExtra("DataCnt", 0);
		TempDataCnt = DataCnt % 9999;
		if(TempDataCnt == 0) TempDataCnt = 9999;
		
		dataType = itn.getStringExtra("Type");
		
		overallData.append(itn.getStringExtra("Year"));
		overallData.append(itn.getStringExtra("Month"));
		overallData.append(itn.getStringExtra("Day"));
		overallData.append(itn.getStringExtra("AmPm"));
		overallData.append(itn.getStringExtra("Hour"));
		overallData.append(itn.getStringExtra("Minute"));
		overallData.append(dfm.format(TempDataCnt));
		overallData.append(itn.getStringExtra("Type"));
		overallData.append(itn.getStringExtra("RefNumber"));
		overallData.append(itn.getStringExtra("PatientIDLen"));
		overallData.append(itn.getStringExtra("PatientID"));
		overallData.append(itn.getStringExtra("OperatorLen"));
		overallData.append(itn.getStringExtra("Operator"));
		overallData.append(itn.getStringExtra("Primary"));
		overallData.append(itn.getStringExtra("Hba1cPct"));
		
		historyData.append(itn.getStringExtra("Chamber Tmp") + "\t");
		historyData.append(itn.getStringExtra("BlankVal0") + "\t");
		historyData.append(itn.getStringExtra("BlankVal1") + "\t");
		historyData.append(itn.getStringExtra("BlankVal2") + "\t");
		historyData.append(itn.getStringExtra("BlankVal3") + "\t");
		historyData.append(itn.getStringExtra("St1Abs1by0") + "\t");
		historyData.append(itn.getStringExtra("St1Abs1by1") + "\t");
		historyData.append(itn.getStringExtra("St1Abs1by2") + "\t");
		historyData.append(itn.getStringExtra("St1Abs2by0") + "\t");
		historyData.append(itn.getStringExtra("St1Abs2by1") + "\t");
		historyData.append(itn.getStringExtra("St1Abs2by2") + "\t");
		historyData.append(itn.getStringExtra("St1Abs3by0") + "\t");
		historyData.append(itn.getStringExtra("St1Abs3by1") + "\t");
		historyData.append(itn.getStringExtra("St1Abs3by2") + "\t");
		historyData.append(itn.getStringExtra("St2Abs1by0") + "\t");
		historyData.append(itn.getStringExtra("St2Abs1by1") + "\t");
		historyData.append(itn.getStringExtra("St2Abs1by2") + "\t");
		historyData.append(itn.getStringExtra("St2Abs2by0") + "\t");
		historyData.append(itn.getStringExtra("St2Abs2by1") + "\t");
		historyData.append(itn.getStringExtra("St2Abs2by2") + "\t");
		historyData.append(itn.getStringExtra("St2Abs3by0") + "\t");
		historyData.append(itn.getStringExtra("St2Abs3by1") + "\t");
		historyData.append(itn.getStringExtra("St2Abs3by2") + "\t");
		historyData.append(itn.getStringExtra("HWSN") + "\t");
		historyData.append(itn.getStringExtra("SWVersion") + "\t");
		historyData.append(itn.getStringExtra("FWVersion") + "\t");
		historyData.append(itn.getStringExtra("OSVersion"));
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
//	
//	public void DataArray() { // Enumerating data
//		
//		DecimalFormat dfm = new DecimalFormat("0000");
//
//		overallData.delete(0, overallData.capacity());
//		historyData.delete(0, historyData.capacity());
//		
//		itn = getIntent();
//		whichState = itn.getIntExtra("WhichIntent", 0);
//		TempDataCnt = DataCnt % 9999;
//		if(TempDataCnt == 0) TempDataCnt = 9999;
//		
//		dataType = itn.getStringExtra("Type");
//		
//		overallData.append("2015");
//		overallData.append("09");
//		overallData.append("09");
//		overallData.append("PM");
//		overallData.append("12");
//		overallData.append("00");
//		overallData.append(dfm.format(TempDataCnt));
//		overallData.append("W");
//		overallData.append("ABCDE");
//		overallData.append("07");
//		overallData.append("patient");
//		overallData.append("08");
//		overallData.append("operator");
//		overallData.append("0");
//		overallData.append("5.0");
//		
//		historyData.append(itn.getStringExtra("Chamber Tmp") + "\t");
//		historyData.append(itn.getStringExtra("BlankVal0") + "\t");
//		historyData.append(itn.getStringExtra("BlankVal1") + "\t");
//		historyData.append(itn.getStringExtra("BlankVal2") + "\t");
//		historyData.append(itn.getStringExtra("BlankVal3") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs1by0") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs1by1") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs1by2") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs2by0") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs2by1") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs2by2") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs3by0") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs3by1") + "\t");
//		historyData.append(itn.getStringExtra("St1Abs3by2") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs1by0") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs1by1") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs1by2") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs2by0") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs2by1") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs2by2") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs3by0") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs3by1") + "\t");
//		historyData.append(itn.getStringExtra("St2Abs3by2") + "\t");
//		historyData.append(itn.getStringExtra("HWSN") + "\t");
//		historyData.append(itn.getStringExtra("SWVersion") + "\t");
//		historyData.append(itn.getStringExtra("FWVersion") + "\t");
//		historyData.append(itn.getStringExtra("OSVersion"));
//	}
	
	public void WhichIntent(boolean isSnapshot) { // Activity conversion
	
		Intent nextIntent = null;
		
		if(!isSnapshot) {
			
			nextIntent = new Intent(getApplicationContext(), RemoveActivity.class);
			nextIntent.putExtra("WhichIntent", whichState);
			nextIntent.putExtra("DataCnt", DataCnt);
		
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
