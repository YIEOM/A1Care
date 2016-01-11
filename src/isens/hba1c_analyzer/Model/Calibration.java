package isens.hba1c_analyzer.Model;

import java.text.DecimalFormat;
import java.util.ArrayList;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class Calibration {

	public enum TargetMode {StandBy, Blank, QuickA1C, QuickACR, FullA1C, FullACR, Scan, Run}
	
	private Hardware mHardware;
	private FileSystem mFileSystem;	
	
	private Activity activity;

	private ArrayList<Float> aDCValList;
	
	public Calibration(Activity activity) {
		
		this.activity = activity;
		
		mHardware = new Hardware();
	}	
		
	public AnalyzerState startTest(AnalyzerState state, AnalyzerState whichState)  {
		
		if(state == AnalyzerState.NormalOperation) {
			
			for(int i = 0; i < 3; i++) {
				
				switch(whichState) {
				
				case InitPosition	:
					whichState = mHardware.getAMotorState(Hardware.HOME_POSITION, CtrTarget.NormalSet, AnalyzerState.MeasurePosition);
					break;
					
				case MeasurePosition	:
					whichState = mHardware.getAMotorState(Hardware.MEASURE_POSITION, CtrTarget.NormalSet, AnalyzerState.NormalOperation);
					break;
				
				default	:
					break;						
				}
			}
		
		} else return state;
		
		return whichState;
	}
	
	public AnalyzerState shakeCMotor(AnalyzerState state, AnalyzerState whichState, TargetMode targetMode) {
		
		if(state == AnalyzerState.NormalOperation) {
		
			for(int i = 0; i < 3; i++) {
						
				switch(whichState) {
				
				case Step1Position	:
					whichState = mHardware.getAMotorState(Hardware.Step1st_POSITION, CtrTarget.NormalSet, AnalyzerState.Step1Shaking);
					break;
					
				case Step2Position	:
					whichState = mHardware.getAMotorState(Hardware.Step2nd_POSITION, CtrTarget.NormalSet, AnalyzerState.Step2Shaking);
					break;
					
				case Step1Shaking	:
					if(targetMode == TargetMode.FullA1C) whichState = mHardware.getShkCMotorState(Hardware.A1C_STP1_SHK_TIME, AnalyzerState.NormalOperation);
					else if(targetMode == TargetMode.FullACR) whichState = mHardware.getShkCMotorState(Hardware.ACR_STP1_SHK_TIME, AnalyzerState.NormalOperation);
					else whichState = mHardware.getShkCMotorState(5, AnalyzerState.NormalOperation);
					break;
				
				case Step2Shaking	:
					if(targetMode == TargetMode.FullA1C) whichState = mHardware.getShkCMotorState(Hardware.A1C_STP2_SHK_TIME, AnalyzerState.NormalOperation);
					else if(targetMode == TargetMode.FullACR) whichState = mHardware.getShkCMotorState(Hardware.ACR_STP2_SHK_TIME, AnalyzerState.NormalOperation);
					else whichState = mHardware.getShkCMotorState(5, AnalyzerState.NormalOperation);
					break;
					
				default	:
					break;		
				}
			}
		
		} else return state;
		
		return whichState;
	}
	
	public AnalyzerState measureA1CPhoto(AnalyzerState state, AnalyzerState whichState) {
		
		if(state == AnalyzerState.NormalOperation) {
			
			aDCValList = new ArrayList<Float>();
				
			for(int i = 0; i < 6; i++) {
			
				switch(whichState) {
				
				case MeasureDark	:
					mHardware.initBlankValue();
					whichState = mHardware.getAMotorState(Hardware.FILTER_DARK, CtrTarget.NormalSet, AnalyzerState.Measure535nm);
					if(whichState == AnalyzerState.Measure535nm) whichState = mHardware.getPhotoValue(AnalyzerState.Measure535nm);
					Hardware.BlkValList.add(0, mHardware.getPhotoADC());
					aDCValList.add(0, Hardware.BlkValList.get(0));
					break;
				
				case FilterDark	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_DARK, CtrTarget.NormalSet, AnalyzerState.Measure535nm);
					aDCValList.add(0, Hardware.BlkValList.get(0));
					break;
				
				case Measure535nm	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_SKIP, CtrTarget.NormalSet, AnalyzerState.Measure660nm);
					if(whichState == AnalyzerState.Measure660nm) whichState = mHardware.getPhotoValue(AnalyzerState.Measure660nm);
					aDCValList.add(1, mHardware.getPhotoADC());
					break;
				
				case Measure660nm	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_SKIP, CtrTarget.NormalSet, AnalyzerState.Measure750nm);
					if(whichState == AnalyzerState.Measure750nm) whichState = mHardware.getPhotoValue(AnalyzerState.Measure750nm);
					aDCValList.add(2, mHardware.getPhotoADC());
					break;
				
				case Measure750nm	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_SKIP, CtrTarget.NormalSet, AnalyzerState.FilterHome);
					if(whichState == AnalyzerState.FilterHome) whichState = mHardware.getPhotoValue(AnalyzerState.NormalOperation);
					aDCValList.add(3, mHardware.getPhotoADC());
					break;
				
				default	:
					break;						
				}
			}
			
		} else return state;
		
		return whichState;
	}
	
	public AnalyzerState measureACRPhoto(AnalyzerState state, AnalyzerState whichState) {
		
		if(state == AnalyzerState.NormalOperation) {
		
			aDCValList = new ArrayList<Float>();
				
			for(int i = 0; i < 6; i++) {
			
				switch(whichState) {
				
				case MeasureDark	:
					mHardware.initBlankValue();
					whichState = mHardware.getAMotorState(Hardware.FILTER_DARK, CtrTarget.NormalSet, AnalyzerState.Measure535nm);
					if(whichState == AnalyzerState.Measure535nm) whichState = mHardware.getPhotoValue(AnalyzerState.Filter535nm);
					Hardware.BlkValList.add(0, mHardware.getPhotoADC());
					aDCValList.add(0, Hardware.BlkValList.get(0));
					break;
				
				case FilterDark	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_DARK, CtrTarget.NormalSet, AnalyzerState.Filter535nm);
					aDCValList.add(0, Hardware.BlkValList.get(0));
					break;
					
				case Filter535nm	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_SKIP, CtrTarget.NormalSet, AnalyzerState.Measure535nm);
					if(whichState == AnalyzerState.Measure535nm) whichState = mHardware.getPhotoValue(AnalyzerState.Measure660nm);
					aDCValList.add(1, mHardware.getPhotoADC());
					break;
				
				case Measure535nm	:
					whichState = mHardware.getPhotoValue(AnalyzerState.Measure660nm);
					aDCValList.add(0, Hardware.BlkValList.get(0));
					aDCValList.add(1, mHardware.getPhotoADC());
					break;
					
				case Measure660nm	:
					whichState = AnalyzerState.Measure750nm;
					aDCValList.add(2, 1.0f);
					break;
				
				case Measure750nm	:
					whichState = AnalyzerState.NormalOperation;
					aDCValList.add(3, 1.0f);
					break;
				
				default	:
					break;						
				}
			}
			
		} else return state;
		
		return whichState;
	}
	
	public AnalyzerState finishTest(AnalyzerState state, AnalyzerState whichState) {
		
		if(state == AnalyzerState.NormalOperation) {
		
			for(int i = 0; i < 4; i++) {
			
				switch(whichState) {
					
				case CartridgeDump	:
					whichState = mHardware.getAMotorState(Hardware.CARTRIDGE_DUMP, CtrTarget.NormalSet, AnalyzerState.FilterHome);
					break;
				
				case FilterHome	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_DARK, CtrTarget.NormalSet, AnalyzerState.CartridgeHome);
					break;
					
				case CartridgeHome	:
					whichState = mHardware.getAMotorState(Hardware.HOME_POSITION, CtrTarget.NormalSet, AnalyzerState.NormalOperation);
					break;
					
				default	:
					break;						
				}
			}
			
		} else return state;
		
		return whichState;
	}
		
	public ArrayList<Float> getADCValList() {
		
		return aDCValList;
	}
	
	public ArrayList<Float> convertFloat(ArrayList<String> strList) {
		
		ArrayList<Float> valueList = new ArrayList<Float>();
		
		for(int i = 0; i < strList.size(); i++) {
		
			valueList.add(i, Float.parseFloat(strList.get(i)));
		}
		
		return valueList;
	}
	
	public ArrayList<String> convertAbstoString(ArrayList<Float> valList) {
		
		DecimalFormat dfm = new DecimalFormat("0.0000");
		ArrayList<String> stringList = new ArrayList<String>();
		
		for(int i = 0; i < valList.size(); i++) {
			
			stringList.add(i, dfm.format((double) valList.get(i)));
		}
		
		return stringList;
	}
	
	public void calStep1AbsforCal(TargetIntent target) {
		
		if(target == TargetIntent.A1CCal) mHardware.calStep1Absorbance();
		else mHardware.calStep1AbsforACR();
	}
	
	public ArrayList<String> toStringRst(TargetIntent target, ArrayList<Float> valList) {
		
		DecimalFormat dfm = new DecimalFormat("0.0000");
		DecimalFormat dfm2 = new DecimalFormat("0.0");
		ArrayList<String> stringList = new ArrayList<String>();
		
		if(target == TargetIntent.A1CCal) {
		
			stringList.add(0, dfm.format((double) valList.get(0)));
			stringList.add(1, "-1.0");
			stringList.add(2, dfm2.format((double) valList.get(1)));
			
		} else {
			
			stringList.add(0, dfm2.format((double) valList.get(0)));
			stringList.add(1, dfm2.format((double) valList.get(1)));
			stringList.add(2, dfm2.format((double) valList.get(2)));
		}
		
		return stringList;
	}
		
	public ArrayList<String> getInitforText(TargetIntent target) {
		
		ArrayList<String> txtList = new ArrayList<String>();
		
		if(target == TargetIntent.A1CCal) {
			
			txtList.add(0, "HbA1C CAL.");
			txtList.add(1, "t");
			txtList.add(2, "");
			txtList.add(3, "H");
		
		} else {
			
			txtList.add(0, "ACR CAL.");
			txtList.add(1, "m");
			txtList.add(2, "C");
			txtList.add(3, "A");
		}
		
		return txtList;
	}
	
	public ArrayList<String> getInitforAbsText() {
		
		ArrayList<String> valueList = new ArrayList<String>();
		
		for(int i = 0; i < 3; i++) {
			
			valueList.add(i, "");
		}
		
		return valueList;
	}
	
	public ArrayList<Byte> getBlankforTx() {
		
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		
		txDataList.add(0, (byte) 0x01); // device code
		txDataList.add(1, (byte) (Hardware.PC_QC_BLANK+0x01)); // message type
		txDataList.add(2, (byte) 0x00); // high bit of length of data
		txDataList.add(3, (byte) 0x01); // low bit of length of data
		txDataList.add(4, (byte) 0x00);
		
		return txDataList;
	}
	
	public ArrayList<Byte> getQuickforTx() {
		
		ArrayList<Byte> dateTimeList = new ArrayList<Byte>();
		ArrayList<Byte> absList = new ArrayList<Byte>();
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		
		int idx = 0,
			len,
			hwSNLen,
			dateTimeLen,
			absLen;
		
		dateTimeList = getDateTimeforTx();
		absList = getAbsforTx();
		
		hwSNLen = AboutModel.HWSN.length();
		dateTimeLen = dateTimeList.size();
		absLen = absList.size();
		
		len = 1 +  + dateTimeLen + absLen;
		
		txDataList.add(idx++, (byte) 0x01); // device code
		txDataList.add(idx++, (byte) (Hardware.PC_QC_QUICK+0x01)); // message type
		txDataList.add(idx++, (byte) ((len >> 8) & 0xff)); // high bit of length of data
		txDataList.add(idx++, (byte) (len & 0xff)); // low bit of length of data
		txDataList.add(idx++, (byte) 0x00);
		
		for(int i = 0; i < hwSNLen; i++) txDataList.add(idx++, AboutModel.HWSN.getBytes()[i]);
		for(int i = 0; i < dateTimeLen; i++) txDataList.add(idx++, dateTimeList.get(i));
		for(int i = 0; i < absLen; i++) txDataList.add(idx++, absList.get(i));
		
		return txDataList;
	}
	
	public ArrayList<Byte> getFullforTx(byte msgType, byte num) {
		
		ArrayList<Byte> dateTimeList = new ArrayList<Byte>();
		ArrayList<Byte> absList = new ArrayList<Byte>();
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		
		int idx = 0,
			len,
			hwSNLen,
			dateTimeLen,
			absLen;
		
		dateTimeList = getDateTimeforTx();
		absList = getAbsforTx();
		
		hwSNLen = AboutModel.HWSN.length();
		dateTimeLen = dateTimeList.size();
		absLen = absList.size();
		
		len = 1 +  + dateTimeLen + absLen;
		
		txDataList.add(idx++, (byte) 0x01); // device code
		txDataList.add(idx++, (byte) (msgType+0x01)); // message type
		txDataList.add(idx++, (byte) ((len >> 8) & 0xff)); // high bit of length of data
		txDataList.add(idx++, (byte) (len & 0xff)); // low bit of length of data
		
		
		for(int i = 0; i < hwSNLen; i++) txDataList.add(idx++, AboutModel.HWSN.getBytes()[i]);
		txDataList.add(idx++, num);
		for(int i = 0; i < dateTimeLen; i++) txDataList.add(idx++, dateTimeList.get(i));
		for(int i = 0; i < absLen; i++) txDataList.add(idx++, absList.get(i));
		
		return txDataList;
	}
	
	private ArrayList<Byte> getDateTimeforTx() {
		
		int idx = 0;
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		
		for(int i = 0; i < 4; i++) txDataList.add(idx++, MainTimer.rTime[0].getBytes()[i]); // year
		for(int i = 0; i < 2; i++) txDataList.add(idx++, MainTimer.rTime[1].getBytes()[i]); // month
		for(int i = 0; i < 2; i++) txDataList.add(idx++, MainTimer.rTime[2].getBytes()[i]); // day
		for(int i = 0; i < 2; i++) txDataList.add(idx++, MainTimer.rTime[7].getBytes()[i]); // hour
		for(int i = 0; i < 2; i++) txDataList.add(idx++, MainTimer.rTime[5].getBytes()[i]); // minute
		
		return txDataList;
	}
	
	private ArrayList<Byte> getAbsforTx() {
		
		int idx = 0;
		ArrayList<Byte> txDataList = new ArrayList<Byte>();
		ArrayList<String> absList = new ArrayList<String>();
		
		absList = convertAbstoString(Hardware.Stp1Abs1List);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(0).getBytes()[i]); // absList.get(0) : ex) 0.0000
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(1).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(2).getBytes()[i]);
		
		absList = convertAbstoString(Hardware.Stp1Abs2List);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(0).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(1).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(2).getBytes()[i]);
		
		absList = convertAbstoString(Hardware.Stp1Abs3List);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(0).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(1).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(2).getBytes()[i]);
		
		absList = convertAbstoString(Hardware.Stp2Abs1List);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(0).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(1).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(2).getBytes()[i]);
		
		absList = convertAbstoString(Hardware.Stp2Abs2List);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(0).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(1).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(2).getBytes()[i]);
		
		absList = convertAbstoString(Hardware.Stp2Abs3List);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(0).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(1).getBytes()[i]);
		for(int i = 0; i < 6; i++) txDataList.add(idx++, absList.get(2).getBytes()[i]);
		
		return txDataList;
	}
}
