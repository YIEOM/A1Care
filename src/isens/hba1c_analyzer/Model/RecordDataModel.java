package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RemoveActivity;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class RecordDataModel {
	
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;

	public static int DataPage;
	
	public RecordDataModel(Activity activity, Context context) {
		
		mActivityChange = new ActivityChange(activity, context);
		mActivityChange.setIntent();
	}
	
	public int getDataCnt(TargetIntent target) {
		
		int dataCnt;
		
		if(target == TargetIntent.PatientFileLoad) dataCnt = RemoveActivity.PatientDataCnt;
		else dataCnt = RemoveActivity.ControlDataCnt;
		
		return dataCnt;
	}
	
	public ArrayList<String> getRawData(int num, TargetIntent target) {
		
		ArrayList<String> txtList = new ArrayList<String>();
		
		if(HomeActivity.ANALYZER_SW != HomeActivity.DEMO && mActivityChange.getStringArrayIntent("TestNum")[num] != null) {
		
			txtList.add(0, mActivityChange.getStringArrayIntent("TestNum")[num]);
			txtList.add(1, mActivityChange.getStringArrayIntent("DateTime")[num]);
			txtList.add(2, mActivityChange.getStringArrayIntent("Type")[num]);
			txtList.add(3, mActivityChange.getStringArrayIntent("HbA1c")[num]);
			txtList.add(4, mActivityChange.getStringArrayIntent("Primary")[num]);
			txtList.add(5, mActivityChange.getStringArrayIntent("RefNumber")[num]);
			txtList.add(6, mActivityChange.getStringArrayIntent("PatientID")[num]);
			txtList.add(7, mActivityChange.getStringArrayIntent("OperatorID")[num]);
		
		} else if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO && num == 0) {
			
			txtList.add(0, "0001");
			txtList.add(1, "20150305AM0900");
			if(target == TargetIntent.PatientFileLoad) txtList.add(2, "D");
			else txtList.add(2, "W");
			txtList.add(3, "5.5");
			txtList.add(4, "0");
			txtList.add(5, "DBANA");
			txtList.add(6, "Patient");
			txtList.add(7, "Operator");
		
		} else {
			
			txtList.add(0, "");
			txtList.add(1, "");
			txtList.add(2, "");
			txtList.add(3, "");
			txtList.add(4, "");
			txtList.add(5, "");
			txtList.add(6, "");
			txtList.add(7, "");
		}
		
		return txtList;
	}
	
	public ArrayList<String> getRecordData(int num, TargetIntent target) {
		
		ArrayList<String> txtList = new ArrayList<String>(),
						  dataList = new ArrayList<String>();
		String type,
			   unit,
			   dateTime;
	
		dataList = getRawData(num, target);
		
		if(!dataList.get(0).equals("")) {
			
			type = dataList.get(2);
			unit = dataList.get(4);
			dateTime = dataList.get(1);
			
			txtList.add(0, dataList.get(0));
			txtList.add(1, getHandledDateTime(dateTime));
			txtList.add(2, getHandledType(type));
			txtList.add(3, dataList.get(3));
			txtList.add(4, getHandledUnit(unit));
		
		} else return dataList;
		
		return txtList;
	}
	
	public ArrayList<String> getDetRecordData(int num, TargetIntent target) {
		
		ArrayList<String> txtList = new ArrayList<String>(),
				  		  dataList = new ArrayList<String>();
		String type,
			   dateTime,
			   pID,
			   primary;
	
		dataList = getRawData(num, target);
		
		type = dataList.get(2);
		primary = dataList.get(4);
		dateTime = dataList.get(1);
		pID = dataList.get(6); 
		
		txtList.add(0, dataList.get(0));
		txtList.add(1, getHandledDetDateTime(dateTime));
		txtList.add(2, getHandledType(type));
		txtList.add(3, dataList.get(3) + " " + getHandledUnit(primary));
		txtList.add(4, getHandledPrimary(primary));
		txtList.add(5, getHandledRange(primary));
		txtList.add(6, dataList.get(5));
		txtList.add(7, getHandledPID(pID));
		txtList.add(8, dataList.get(7));
		
		return txtList;
	}
	
	public String getHandledType(String data) {
		
		String type;
		
		if(data.equals("D")) type = "HbA1c";
		else if(data.equals("E")) type = "ACR";
		else if(data.equals("W") || data.equals("X")) type = "Control HbA1c";
		else type = "Control ACR";
		
		return type;
	}
	
	public String getHandledUnit(String data) {
		
		String unit;
		
		if(data.equals("0")) unit = "%";
		else unit = "mmol/mol";
		
		return unit;
	}
	
	public String getHandledDateTime(String data) {
		
		String dateTime;
		
		dateTime = data.substring(0, 4) + "." + data.substring(4, 6) + "." + data.substring(6, 8) + "   " + data.substring(10, 12) + ":" + data.substring(12, 14) + " " + data.substring(8, 10);
		
		return dateTime;
	}
	
	public String getHandledDetDateTime(String data) {
		
		String dateTime;
		
		dateTime = data.substring(2, 4) + "." + data.substring(4, 6) + "." + data.substring(6, 8) + "   " + data.substring(10, 12) + ":" + data.substring(12, 14) + " " + data.substring(8, 10);
		
		return dateTime;
	}
	
	public String getHandledPID(String data) {
		
		String pID;
		
		if(data.length() > 10) pID = data.substring(0, 10) + " \n" + data.substring(10);
		else pID = data;
		
		return pID;
	}
	
	public String getHandledPrimary(String data) {
		
		String primary;
		
		if(data.equals("0")) primary = "NGSP";
		else primary = "IFCC";
		
		return primary;
	}
	
	public String getHandledRange(String data) {
		
		String range;
		
		if(data.equals("0")) range = "4.0 - 6.0 %";
		else range = "20 - 42 mmol/mol";
		
		return range;
	}
	
	public String getPage(TargetIntent target) {
		
		int tPage;
		String page;
		
		if(HomeActivity.ANALYZER_SW != HomeActivity.DEMO) {
		
			if(target == TargetIntent.PatientFileLoad) tPage = (RemoveActivity.PatientDataCnt+3)/5; 
			else tPage = (RemoveActivity.ControlDataCnt+3)/5;
			if(tPage == 0) tPage = 1;
			
			page = Integer.toString(DataPage+1) + " / " + Integer.toString(tPage);
		
		} else page = "1 / 1";
		
		return page;
	}
	
	public int getPressedCheckBox(int id) {
		
		int idx;
		
		switch(id) {
		
		case R.id.checkBox1IBtn	:
			idx = 0;
			break;
			
		case R.id.checkBox2IBtn	:
			idx = 1;
			break;
			
		case R.id.checkBox3IBtn	:
			idx = 2;
			break;
			
		case R.id.checkBox4IBtn	:
			idx = 3;
			break;
			
		case R.id.checkBox5IBtn	:
			idx = 4;
			break;
			
		default	:
			idx = 0;
			break;
		}
		Log.w("check", "" + idx);
		return idx;
	}
}
