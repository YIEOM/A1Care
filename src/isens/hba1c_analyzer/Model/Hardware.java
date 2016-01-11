package isens.hba1c_analyzer.Model;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.util.Log;

import isens.hba1c_analyzer.Barcode;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.SerialPort.CtrTarget;

public class Hardware {

	public final static String HOME_POSITION    = "CH",
							   MEASURE_POSITION = "CM",
							   Step1st_POSITION = "C1",
							   Step2nd_POSITION = "C2",
							   CARTRIDGE_DUMP   = "CD",
							   FILTER_DARK      = "FD",
							   FILTER_SKIP		= "FS",
							   MOTOR_COMPLETE   = "AR",
							   PHOTO_MEASURE	= "VH",
							   MOTOR_STOP		= "MS",
							   CARTRIDGE_ERROR	= "CE1",
							   FILTER_ERROR 	= "FE1",
							   ERROR_DOOR       = "ED",
							   TMP_CELLBLOCK 	= "VT",
							   TMP_INNER	   	= "IA",
							   NO_RESPONSE		= "NR",
							   TEMP_CHAMBER 	= "VT",
							   TEMP_INNER	    = "IA";
	
	public final static byte PC_QC_CONNECT = 0x10,
							 PC_QC_BLANK   = 0x20,
							 PC_QC_QUICK   = 0x22,
							 PC_QC_Hct	   = 0x30,
							 PC_QC_CS	   = 0x40;

	public final static int A1C_STP1_SHK_TIME = 105, // Motor shaking time, default : 6 * 105(sec) = 0630
							A1C_STP2_SHK_TIME = 90, // Motor shaking time, default : 6 * 90(sec) = 0540
							ACR_STP1_SHK_TIME = 300,
							ACR_STP2_SHK_TIME = 60;
		
	public static float InitTmp;
	
	public static ArrayList<Float> BlkValList,
						 		   Stp1Val1List,
				 				   Stp1Val2List,
		 						   Stp1Val3List,
 								   Stp2Val1List,
								   Stp2Val2List,
								   Stp2Val3List,
								   Stp1Abs1List,
								   Stp1Abs2List,
								   Stp1Abs3List,
								   Stp2Abs1List,
								   Stp2Abs2List,
								   Stp2Abs3List;
	
	private SerialPort mSerialPort;
	
	private float photoADC = 0.0f;
	
	public Hardware() {
		
		mSerialPort = new SerialPort();
	}
	
	public void setPhotoMeasure() {
		
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.NormalSet);
	}
	
	public String getBoardMessage(int rspTime) {
		
		String message;
		int time = 0;
		
		rspTime = rspTime * 100; // second unit
		
		do {
			
			message = mSerialPort.BoardMessageOutput();
			
			if(time++ > rspTime) {
				
				message = NO_RESPONSE;
				break;
			}
			
			if(RunActivity.IsError || RunActivity.IsStop) break;
			
			SerialPort.Sleep(10);
			
		} while(message.equals(NO_RESPONSE));
//		Log.w("getBoardMessage", "" + message);
		return message;
	}
	
	public String getSensorMessage(int rspTime) {
		
		String message;
		int time = 0;
		
		rspTime = rspTime * 100; // second unit
		
		do {
			
			message = mSerialPort.SensorMessageOutput();
			
			if(time++ > rspTime) {
				
				message = NO_RESPONSE;
				break;
			}
							
			SerialPort.Sleep(10);
			
		} while(message.equals(NO_RESPONSE));
//		Log.w("getSensorMessage", "" + message);
		return message;
	}
	
	public AnalyzerState getAMotorState(String cmd, CtrTarget trg, AnalyzerState nextState) {
		
		AnalyzerState state;
		String message;
		
		while(MainTimer.RXBoardFlag) SerialPort.Sleep(10);
		
		MainTimer.RXBoardFlag = true;
		mSerialPort.BoardTx(cmd, trg);
		
		message = getBoardMessage(10);
//		Log.w("getAMotorState", "cmd : " + cmd + ", message : " + message);
		if(message.equals(cmd)) state = nextState;
		else state = checkBoardError(message);
				
		MainTimer.RXBoardFlag = false;
		
		return state;
	}
	
	public AnalyzerState getShkCMotorState(int sec, AnalyzerState nextState) {
		
		AnalyzerState state;
		String message;
		DecimalFormat ShkDf = new DecimalFormat("0000");
		
		while(MainTimer.RXBoardFlag) SerialPort.Sleep(10);
		
		MainTimer.RXBoardFlag = true;
		
		mSerialPort.BoardTx(ShkDf.format(sec * 6), CtrTarget.MotorSet);
		
		message = getBoardMessage(sec + 5);
//		Log.w("getShkCMotorState", "message : " + message);
		if(message.equals(MOTOR_COMPLETE)) state = nextState;
		else state = checkBoardError(message);
				
		MainTimer.RXBoardFlag = false;
		
		return state;
	}
	
	public AnalyzerState getPhotoValue(AnalyzerState nextState) {
		
		AnalyzerState state;
		String message;
		
		while(MainTimer.RXBoardFlag) SerialPort.Sleep(10);
		
		MainTimer.RXBoardFlag = true;
		mSerialPort.BoardTx(PHOTO_MEASURE, CtrTarget.NormalSet);
		
		message = getBoardMessage(10);
//		Log.w("getPhotoValue", "message : " + message);
		if(message.length() == 8) {
			
			try {
				
				photoADC = Float.parseFloat(message);	
				state = nextState;
				
			} catch(NumberFormatException e) {
				
				state = AnalyzerState.NoResponse;
			}			
			
		} else state = checkBoardError(message);
				
		MainTimer.RXBoardFlag = false;
		
		return state;
	}
	
	public AnalyzerState checkBoardError(String message) {
		
		AnalyzerState state = AnalyzerState.NormalOperation;
//		Log.w("checkBoardError", "message : " + message + ", IsError : " + RunActivity.IsError);
		if(!message.equals(NO_RESPONSE)) {
			
			if(message.equals(CARTRIDGE_ERROR)) state= AnalyzerState.ShakingMotorError;
			else if(message.equals(FILTER_ERROR)) state = AnalyzerState.FilterMotorError;
			else state = AnalyzerState.NoResponse;
			
		} else if(RunActivity.IsError) state = AnalyzerState.ErrorCover;
		
		else state = AnalyzerState.NoResponse;
		
		return state;
	}
	
	public float getPhotoADC() {
		
		return photoADC - BlkValList.get(0);
	}
	
	public void initBlankValue() {
		
		BlkValList = new ArrayList<Float>();
		BlkValList.add(0, 0.0f);
	}
	
	public void setBlkVal(ArrayList<Float> valueList) {
		
		for(int i = 0; i < valueList.size(); i++) {
			
			BlkValList.add(i, valueList.get(i));
		}
	}
	
	public void setStep1stValue1(ArrayList<Float> valueList) {
		
		Stp1Val1List = new ArrayList<Float>();
		
		for(int i = 0; i < valueList.size()-1; i++) {
			
			Stp1Val1List.add(i, valueList.get(i+1));
		}
	}

	public void setStep1stValue2(ArrayList<Float> valueList) {
	
		Stp1Val2List = new ArrayList<Float>();
		
		for(int i = 0; i < valueList.size()-1; i++) {
			
			Stp1Val2List.add(i, valueList.get(i+1));
		}
	}

	public void setStep1stValue3(ArrayList<Float> valueList) {
	
		Stp1Val3List = new ArrayList<Float>();
		
		for(int i = 0; i < valueList.size()-1; i++) {
			
			Stp1Val3List.add(i, valueList.get(i+1));
		}
	}
	
	public void setStep2ndValue1(ArrayList<Float> valueList) {
		
		Stp2Val1List = new ArrayList<Float>();
		
		for(int i = 0; i < valueList.size()-1; i++) {
			
			Stp2Val1List.add(i, valueList.get(i+1));
		}
	}

	public void setStep2ndValue2(ArrayList<Float> valueList) {
	
		Stp2Val2List = new ArrayList<Float>();
		
		for(int i = 0; i < valueList.size()-1; i++) {
			
			Stp2Val2List.add(i, valueList.get(i+1));
		}
	}

	public void setStep2ndValue3(ArrayList<Float> valueList) {
	
		Stp2Val3List = new ArrayList<Float>();
		
		for(int i = 0; i < valueList.size()-1; i++) {
			
			Stp2Val3List.add(i, valueList.get(i+1));
		}
	}
	
	public void calStep1Absorbance() {
		
		float temp;
		int sum = Stp1Val1List.size() + Stp1Val2List.size() + Stp1Val3List.size();
		
		if(sum == 9) {
		
			Stp1Abs1List = new ArrayList<Float>();
			Stp1Abs2List = new ArrayList<Float>();
			Stp1Abs3List = new ArrayList<Float>();
			
			for(int i = 0; i < 3; i++) {
				
				temp = BlkValList.get(i+1);
				Stp1Abs1List.add(i, (float) -Math.log10(Stp1Val1List.get(i)/temp));
				Stp1Abs2List.add(i, (float) -Math.log10(Stp1Val2List.get(i)/temp));
				Stp1Abs3List.add(i, (float) -Math.log10(Stp1Val3List.get(i)/temp));
			}
		}
	}
	
	public void calStep2Absorbance() {
		
		float temp;
		int sum = Stp2Val1List.size() + Stp2Val2List.size() + Stp2Val3List.size();
		
		if(sum == 9) {
		
			Stp2Abs1List = new ArrayList<Float>();
			Stp2Abs2List = new ArrayList<Float>();
			Stp2Abs3List = new ArrayList<Float>();
			
			for(int i = 0; i < 3; i++) {
				
				temp = BlkValList.get(i+1);
				Stp2Abs1List.add(i, (float) -Math.log10(Stp2Val1List.get(i)/temp));
				Stp2Abs2List.add(i, (float) -Math.log10(Stp2Val2List.get(i)/temp));
				Stp2Abs3List.add(i, (float) -Math.log10(Stp2Val3List.get(i)/temp));
			}
		}
	}
	
	public void calStep1AbsforACR() {
		
		float temp;
		int sum = Stp1Val1List.size() + Stp1Val2List.size() + Stp1Val3List.size();
		
		if(sum == 9) {
		
			Stp1Abs1List = new ArrayList<Float>();
			Stp1Abs2List = new ArrayList<Float>();
			Stp1Abs3List = new ArrayList<Float>();
			
			temp = BlkValList.get(1);
			Stp1Abs1List.add(0, (float) (-Math.log10(Stp1Val1List.get(0)/temp)*RunActivity.RF1_Slope)+RunActivity.RF1_Offset);
			Stp1Abs2List.add(0, (float) (-Math.log10(Stp1Val2List.get(0)/temp)*RunActivity.RF1_Slope)+RunActivity.RF1_Offset);
			Stp1Abs3List.add(0, (float) (-Math.log10(Stp1Val3List.get(0)/temp)*RunActivity.RF1_Slope)+RunActivity.RF1_Offset);
			
			for(int i = 1; i < 3; i++) {
				
				temp = BlkValList.get(i+1);
				Stp1Abs1List.add(i, (float) -Math.log10(Stp1Val1List.get(i)/temp));
				Stp1Abs2List.add(i, (float) -Math.log10(Stp1Val2List.get(i)/temp));
				Stp1Abs3List.add(i, (float) -Math.log10(Stp1Val3List.get(i)/temp));
			}
		}
	}
	
	public float getrAt() {
		
		/* rAt : Data processed Abs of 1st 535nm */
		int sum = Stp1Abs1List.size() + Stp1Abs2List.size() + Stp1Abs3List.size();
		
		if(sum == 9) {
				
		float tempAbs[] = new float[3];
						
			tempAbs[0] = Stp1Abs1List.get(0) - Stp1Abs1List.get(1);
			tempAbs[1] = Stp1Abs2List.get(0) - Stp1Abs2List.get(1);
			tempAbs[2] = Stp1Abs3List.get(0) - Stp1Abs3List.get(1);
			
//			Log.w("getrAt", "tempAbs[0] : " + tempAbs[0]);
//			Log.w("getrAt", "tempAbs[1] : " + tempAbs[1]);
//			Log.w("getrAt", "tempAbs[2] : " + tempAbs[2]);
//			Log.w("getrAt", "rAt : " + ((tempAbs[0]+tempAbs[1]+tempAbs[2])/3));
			return (tempAbs[0]+tempAbs[1]+tempAbs[2])/3;
			
		} else return -1.0f;
	}
	
	public float getrBt() {
		
		/* rBt : Data processed Abs of 2nd 535nm */
		int sum = Stp2Abs1List.size() + Stp2Abs2List.size() + Stp2Abs3List.size();
		
		if(sum == 9) {
				
			float tempAbs[] = new float[3];
						
			tempAbs[0] = Stp2Abs1List.get(0) - Stp2Abs1List.get(1);
			tempAbs[1] = Stp2Abs2List.get(0) - Stp2Abs2List.get(1);
			tempAbs[2] = Stp2Abs3List.get(0) - Stp2Abs3List.get(1);
			
//			Log.w("getrBt", "tempAbs[0] : " + tempAbs[0]);
//			Log.w("getrBt", "tempAbs[1] : " + tempAbs[1]);
//			Log.w("getrBt", "tempAbs[2] : " + tempAbs[2]);
//			Log.w("getrBt", "rBt : " + ((tempAbs[0]+tempAbs[1]+tempAbs[2])/3));
			return (tempAbs[0]+tempAbs[1]+tempAbs[2])/3;
		
		} else return -1.0f;
	}
	
	public float getrCt() {
		
		/* rCt : Data processed Abs of 1st 750nm */
		int sum = Stp1Abs1List.size() + Stp1Abs2List.size() + Stp1Abs3List.size();
		
		if(sum == 9) {
		
			ArrayList<Float> absList = new ArrayList<Float>();
				
			absList.add(0, Stp1Abs1List.get(2));
			absList.add(1, Stp1Abs2List.get(2));
			absList.add(2, Stp1Abs3List.get(2));
	
			return calAdvAvgAbs(absList);
			
		} else return -1.0f;
	}

	public float getrDt() {
		
		/* rDt : Data processed Abs of 2nd 535nm - Abs of 2nd 660nm */
		int sum = Stp2Abs1List.size() + Stp2Abs2List.size() + Stp2Abs3List.size();
		
		if(sum == 9) {
		
			ArrayList<Float> absList = new ArrayList<Float>();
				
			absList.add(0, Stp2Abs1List.get(0) - Stp2Abs1List.get(1));
			absList.add(1, Stp2Abs2List.get(0) - Stp2Abs2List.get(1));
			absList.add(2, Stp2Abs3List.get(0) - Stp2Abs3List.get(1));

			return calAdvAvgAbs(absList);
		
		} else return -1.0f;
	}

	public float getAinME() {
		
		/* A(tHb) : Data processed Abs of 1st 535nm - Abs of 1st 750nm) */
		int sum = Stp1Abs1List.size() + Stp1Abs2List.size() + Stp1Abs3List.size();
		
		if(sum == 9) {
		
			ArrayList<Float> absList = new ArrayList<Float>();
				
			absList.add(0, Stp1Abs1List.get(0) - Stp1Abs1List.get(2));
			absList.add(1, Stp1Abs2List.get(0) - Stp1Abs2List.get(2));
			absList.add(2, Stp1Abs3List.get(0) - Stp1Abs3List.get(2));

			return calAdvAvgAbs(absList)*RunActivity.RF1_Slope + RunActivity.RF1_Offset;
		
		} else return -1.0f;
	}
	
	public float getBinME() {
		
		/* B : Data processed Abs of 2nd 660nm - Abs of 2nd 750nm */
		int sum = Stp1Abs1List.size() + Stp1Abs2List.size() + Stp1Abs3List.size();
		
		if(sum == 9) {
		
			ArrayList<Float> absList = new ArrayList<Float>();
				
			absList.add(0, Stp2Abs1List.get(1) - Stp2Abs1List.get(2));
			absList.add(1, Stp2Abs2List.get(1) - Stp2Abs2List.get(2));
			absList.add(2, Stp2Abs3List.get(1) - Stp2Abs3List.get(2));

			return calAdvAvgAbs(absList)*RunActivity.RF2_Slope + RunActivity.RF2_Offset;
		
		} else return -1.0f;
	}

	private float calAdvAvgAbs(ArrayList<Float> valueList) {
		
		ArrayList<Float> devList = new ArrayList<Float>();
		float std, 
			  sum, 
			  avg;
		int idx = 0;
			
		std = (valueList.get(0) + valueList.get(1) + valueList.get(2)) / 3;
			
		for(int i = 0; i < 3; i++) {
			
			if(std > valueList.get(i)) devList.add(i, std - valueList.get(i));
			else devList.add(i, valueList.get(i) - std);
		}
		
		if(devList.get(0) > devList.get(1)) idx = 0; 
		else idx = 1;
		
		if(devList.get(2) > devList.get(idx)) idx = 2;
		
		sum = valueList.get(0) + valueList.get(1) + valueList.get(2);
		
		avg = (sum - valueList.get(idx)) / 2;

		return avg;
	}
	
	private float SlopeCalculate(float x_a, float y_a, float x1, float y1, float x2, float y2,float x3, float y3) {
		
		float slope, numerator, denominator;
		
		numerator = (y1 - y_a)*(x1 - x_a) + (y2 - y_a)*(x2 - x_a) + (y3 - y_a)*(x3 - x_a);
		denominator = (x1 - x_a)*(x1 - x_a) + (x2 - x_a)*(x2 - x_a) + (x3 - x_a)*(x3 - x_a);
		slope = numerator/denominator;
		
		return slope;
	}
	
	public ArrayList<Float> getRstValue(TargetIntent target) {
		
		ArrayList<Float> valList = new ArrayList<Float>();
		
		if(target == TargetIntent.A1CCal) {
			
			valList.add(0, getAinME());
        	valList.add(1, getHbA1c());
		
		} else {
			
			valList.add(0, getmALB());
        	valList.add(1, getcRE());
        	valList.add(2, getACR());
		}
		
		return valList;
	}
	
	public float getChamTmp() {
		
		String message;
		float tmp;
		
		while(MainTimer.RXBoardFlag) SerialPort.Sleep(10);
		
		MainTimer.RXBoardFlag = true;
		mSerialPort.BoardTx(TMP_CELLBLOCK, CtrTarget.NormalSet);
		
		message = getBoardMessage(1);
		
		MainTimer.RXBoardFlag = false;
		
		if(message.length() == 8) {
			
			try {
				
				tmp = Float.parseFloat(message);	
				tmp = (tmp / 1670.17f) - 15.5f;
				
			} catch(NumberFormatException e) {
				
				tmp = -1.0f;
			}			
			
		} else tmp = -1.0f;
			
		return tmp;
	}
	
	public float getInnerTmp() {
		
		String message;
		float tmp;
		
		while(MainTimer.RXBoardFlag) SerialPort.Sleep(10);
		
		MainTimer.RXBoardFlag = true;
		mSerialPort.BoardTx(TMP_INNER, CtrTarget.NormalSet);
		
		message = getSensorMessage(1);
		
		MainTimer.RXBoardFlag = false;
		
		if(message.substring(1,2).equals("T")) {
			
			try {
				
				tmp = Float.parseFloat(message.substring(2));	
				tmp = ((float) 5/1024 * (tmp + 1));
				tmp = tmp / 0.01f;
				
			} catch(NumberFormatException e) {
				
				tmp = -1.0f;
			}			
			
		} else tmp = -1.0f;
		
		return tmp;
	}
	
	public void setChamTmp() {
		
		Float tmpFloat;
		String tmpString,
			   message;
		DecimalFormat tmpFormat = new DecimalFormat("#####0");
		
		while(MainTimer.RXBoardFlag) SerialPort.Sleep(10);
		
		MainTimer.RXBoardFlag = true;
		
		tmpFloat = (Float) InitTmp * 1670.17f + 25891.34f;
		
		if(tmpFormat.format(tmpFloat).length() == 5) tmpString = "0" + tmpFormat.format(tmpFloat);
		else tmpString = tmpFormat.format(tmpFloat);

		mSerialPort.BoardTx("R" + tmpString, CtrTarget.NormalSet);
		
		message = getBoardMessage(1);
		
		MainTimer.RXBoardFlag = false;
		
//		if(message.equals(tmpFormat.format(tmpDouble))) ;
	}
	
	public float getHbA1c() { // Calculation for HbA1c percentage
		
		float A, B, St, Bt, C1, C2, SLA, SMA, SHA, BLA, BMA, BHA, SLV, SMV, SHV, BLV, BMV, BHV, SV, SA, BV, BA, a3, b3, a32, b32, a4, b4, hbA1c;
	
		A = getAinME();
		B = getBinME();
			
		St = (A - Barcode.b1)/Barcode.a1;
		Bt = (A - Barcode.b1)/Barcode.a1 + 1;
		
		C1 = St * (Barcode.Asm + Barcode.Ass) + Barcode.Aim + Barcode.Ais;
		C2 = B - C1;
		
		SLA = St * Barcode.L / 100;
		SMA = St * Barcode.M / 100;
		SHA = St * Barcode.H / 100;
		BLA = Bt * Barcode.L / 100;
		BMA = Bt * Barcode.M / 100;
		BHA = Bt * Barcode.H / 100;
		
		SLV = SLA * Barcode.a21 + Barcode.b21;
		SMV = SMA * Barcode.a22 + Barcode.b22;
		SHV = SHA * Barcode.a23 + Barcode.b23;
		BLV = BLA * Barcode.a21 + Barcode.b21;
		BMV = BMA * Barcode.a22 + Barcode.b22;
		BHV = BHA * Barcode.a23 + Barcode.b23;
		
		SV = (SLV + SMV + SHV) / 3;
		SA = (SLA + SMA + SHA) / 3;
		
		a3 = SlopeCalculate(SA, SV, SLA, SLV, SMA, SMV, SHA, SHV);
		b3 = SV - a3*SA;
		
		BV = (BLV + BMV + BHV) / 3;
		BA = (BLA + BMA + BHA) / 3;
		
		a32 = SlopeCalculate(BA, BV, BLA, BLV, BMA, BMV, BHA, BHV);
		b32 = BV - a32*BA;
		
		a4 = (b32 - b3) / (Bt - St);
		b4 = b3 - (a4 * St);
		
		hbA1c = (C2 - (St * a4 + b4)) / a3 / St * 100; // %-HbA1c(%)
		
		hbA1c = (Barcode.Sm + Barcode.Ss) * hbA1c + (Barcode.Im + Barcode.Is);
		
		hbA1c = RunActivity.CF_Slope * (RunActivity.AF_Slope * hbA1c + RunActivity.AF_Offset) + RunActivity.CF_Offset;
		
		return hbA1c;
	}
	
	public float getmALB() {
		
		float mALB;
		
		mALB = (getrAt() - Barcode.Im)/Barcode.Sm;
//		Log.w("getmALB", "Im : " + Barcode.Im + " Sm : " + Barcode.Sm);
//		Log.w("getmALB", "mALB : " + mALB);
		return mALB;
	}
	
	public float getcRE() {
		
		float cRE;
		
		cRE = (getrBt() - Barcode.Is)/Barcode.Ss;
//		Log.w("getcRE", "Is : " + Barcode.Is + " Ss : " + Barcode.Ss);
//		Log.w("getcRE", "CRE : " + cRE);
		return cRE;
	}
	
	public float getACR() {
		
		float aCR;
			
		aCR = getmALB()/getcRE()*100;
//		Log.w("getACR", "ACR : " + aCR);
		return aCR;
	}
}
