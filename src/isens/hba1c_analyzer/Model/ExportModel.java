package isens.hba1c_analyzer.Model;

import android.app.Activity;
import android.util.Log;
import isens.hba1c_analyzer.DataStorage;
import isens.hba1c_analyzer.FileLoadActivity;
import isens.hba1c_analyzer.FileSaveActivity;
import isens.hba1c_analyzer.SerialPort;

public class ExportModel {

	private FileSystem mFileSystem;
	private DataStorage mDataStorage;
	
	private Activity activity;
	
	public ExportModel(Activity activity) {
		
		this.activity = activity;
		
		mDataStorage = new DataStorage();
		mFileSystem = new FileSystem(activity);
	}
	
	public StringBuffer getItemName() {
		
		StringBuffer data = new StringBuffer();
		
		data.append("Test Number\t");
		data.append("Test Date/Time\t");
		data.append("Type\t");
		data.append("Result\t");
		data.append("Primary\t");
		data.append("Reference Range\t");
		data.append("Lot\t");
		data.append("Patient ID\t");
		data.append("Operator ID");
		
		return data;
	}
	
	public StringBuffer getHandlingData(String data) {
		
		StringBuffer handlingData = new StringBuffer();
		
		int	pIdx,
			pLen,
			oIdx,
			oLen;
	
		String primary = "", unit = "", refRange = "", type ="", tmp="";
		
		pIdx = 24 + 2;
		pLen = Integer.parseInt(data.substring(24, pIdx));
		oIdx = pIdx + pLen + 2;
		oLen = Integer.parseInt(data.substring(pIdx + pLen, oIdx));

		tmp = data.substring(18, 19);
		
		if(tmp.equals("D")) type = "HbA1c";
		else if(tmp.equals("E")) type = "ACR";
		else if(tmp.equals("W") ||tmp.equals("X")) type = "Control HbA1c";
		else if(tmp.equals("Y") || tmp.equals("Z")) type = "Control ACR";
		
		if(Integer.parseInt(data.substring(oIdx + oLen, oIdx + oLen + 1)) == ConvertModel.NGSP) {
			
			primary = "NGSP";
			unit = " %";
			refRange = "4.0 - 6.0 %";
			
		} else {
			
			primary = "IFCC";
			unit = " mmol/mol";
			refRange = "20 - 42 mmol/mol";
		}
		
		handlingData.append(data.substring(14, 18) + "\t"); // Test Number
		handlingData.append(data.substring(4, 6) + "/" + data.substring(6, 8) + "/"  + data.substring(0, 4) + " " + data.substring(10, 12) + ":" + data.substring(12, 14) + " " + data.substring(8, 10)+ "\t"); // Date
		handlingData.append(type + "\t"); // Type
		handlingData.append(data.substring(oIdx + oLen + 1) + unit + "\t"); // HbA1c
		handlingData.append(primary + "\t"); // Primary
		handlingData.append(refRange + "\t"); // Reference Range
		handlingData.append(data.substring(19, 24) + "\t"); // Reference Number
		handlingData.append(data.substring(pIdx, pIdx + pLen) + "\t"); // Patient ID
		handlingData.append(data.substring(oIdx, oIdx + oLen)); // Operator ID
		
		return handlingData;
	}
	
	public boolean exportFile(String hwSN, int type, int testNum) {
		
		String path = "",
			   data = "";
			
		path = mDataStorage.createUSBFile(hwSN, (byte) type);
		mDataStorage.writeUSBData(path, getItemName());
		
		if(testNum > 10000) testNum = 10000; 
			
		if(testNum > 1) {
		
			for(int i = 1; i < testNum; i++) {
				
				data = getuSDCardData(i, type);
				mDataStorage.writeUSBData(path, getHandlingData(data));
			}
			
			mDataStorage.closeUSBFile(path);
			return checkFile(hwSN, type, testNum-1);
		}
		
		return true;
	}
	
	public boolean checkFile(String hwSN, int type, int testNum) {
		
		boolean isEquals = false;
		String data = "";
		
		while(!isEquals) {
		
			DataStorage.Sleep(100);
			
			data = mDataStorage.checkUSBFile(hwSN, (byte) type);
			
			if(data.length() > 3) {
		
				if(Integer.parseInt(data.substring(0, 4)) == testNum) isEquals = true;
			
			} else if(data.equals("")) break;
		}
		
		return isEquals;
	}
	
	public String getuSDCardData(int testNum, int type) {
		
		String filePath = "",
			   loadData = "";
			
		filePath = mDataStorage.FileCheck(FileSaveActivity.DataCnt - testNum, type);
		
		if(filePath != null) {
			
			loadData = mDataStorage.DataLoad(filePath);
		}
	
		return loadData;
	}
}
