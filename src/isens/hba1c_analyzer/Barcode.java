package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.MainTimer;

import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Barcode {

	final static float a1ref  = 0.01f, 
					   b1ref  = -0.07f, 
					   a21ref = 0.05f, 
					   b21ref = 0.03f, 
					   a22ref = 0.035f, 
					   b22ref = 0.04f;
	
	public static String RefNum, Type;
	
	public static float a1 = 0.009793532f,
						b1 = -0.028f,
						a21 = 0.060055f,
						b21 = -0.003032f,
						a22 = 0.05014f,
						b22 = -0.004829f,
						a23 = 0.039032f,
						b23 = -0.005064f,
						L   = 5.1f,
						M 	= 7.1f,
						H   = 11.7f,
						NorMean = 0.0f,
						AbnorMean = 0.0f;
	
	public static float Sm, Im, Ss, Is, Asm, Aim, Ass, Ais;

	public void BarcodeCheck(StringBuffer buffer) { // Check a barcode data
		
		int len; 
		
		len = buffer.length();
//		Log.w("BarcodeCheck", "buffer : " + buffer.toString());
		if(HomeActivity.MEASURE_MODE == HomeActivity.A1C) {
			
			if(len == SerialPort.A1C_MAX_BUFFER_INDEX) BarcodeHbA1C(buffer);
			else BarcodeStop(false);
		
		} else {
			
			if(!ActionActivity.BarcodeQCCheckFlag) {
			
				if(len == SerialPort.A1C_QC_MAX_BUFFER_INDEX) BarcodeHbA1CQC(buffer);
				else BarcodeStop(false);
			
			} else {
				
				if(len == SerialPort.A1C_MAX_BUFFER_INDEX) BarcodeHbA1C(buffer);
				else BarcodeStop(false);
			}
		}
	}
	
	private void BarcodeHbA1C(StringBuffer buffer) {
		
		try {
			
			String type; 
			
			if(HomeActivity.MEASURE_MODE == HomeActivity.A1C) Type = buffer.substring(0, 1);
			
			RefNum = buffer.substring(0, 5);
			type = RefNum.substring(0, 1);
//			Log.w("BarcodeHbA1C", "RefNum : " + RefNum);
			
			if(type.equals("D") && (Type.equals("D") || Type.equals("W") || Type.equals("X"))) {
			
				Sm = 0.0237f * (((int) buffer.charAt(6) - 42) - 1) + 0.1f;
				Im = 0.158f * (((int) buffer.charAt(7) - 42) - 1) - 6.0f;
				Ss = 0.0003f * (((int) buffer.charAt(8) - 42) - 1);
				Is = 0.002f * (((int) buffer.charAt(9) - 42) - 1);
				
				Asm = 0.000316f * (((int) buffer.charAt(10) - 42) - 1) - 0.01f;
				Aim = 0.00237f * (((int) buffer.charAt(11) - 42) - 1) - 0.1f;
				Ass = 0.000004f * (((int) buffer.charAt(12) - 42) - 1);
				Ais = 0.00003f * (((int) buffer.charAt(13) - 42) - 1);
				
				CheckSum(buffer);
			
			} else if(type.equals("E") && (Type.equals("E") || Type.equals("Y") || Type.equals("Z"))) {
				
				Sm = 0.00002f * (((int) buffer.charAt(6) - 42) - 1) + 0.0001f; // mALB slope
				Im = 0.001f * (((int) buffer.charAt(7) - 42) - 1) - 0.001f; // mALB intercept
				Ss = 0.000013f * (((int) buffer.charAt(8) - 42) - 1) + 0.0005f; // CRE slope
				Is = 0.001f * (((int) buffer.charAt(9) - 42) - 1) - 0.001f; // CRE interept
				
//				Log.w("BarcodeHbA1C", "buffer.charAt(6) : " + buffer.charAt(6) + " index : " + (((int) buffer.charAt(6) - 42) - 1));
//				Log.w("BarcodeHbA1C", "buffer.charAt(7) : " + buffer.charAt(7) + " index : " + (((int) buffer.charAt(7) - 42) - 1));
//				Log.w("BarcodeHbA1C", "buffer.charAt(8) : " + buffer.charAt(8) + " index : " + (((int) buffer.charAt(8) - 42) - 1));
//				Log.w("BarcodeHbA1C", "buffer.charAt(9) : " + buffer.charAt(9) + " index : " + (((int) buffer.charAt(9) - 42) - 1));
				CheckSum(buffer); 
			
			} else BarcodeStop(false);

		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}	
	}
	
	private void BarcodeHbA1CQC(StringBuffer buffer) {
		
		try {
			
			Type = buffer.substring(0, 1);
			
			if(Type.equals("W") || Type.equals("X") || Type.equals("Y") || Type.equals("Z")) {
				
				NorMean = 0.1f * ((int) buffer.charAt(6) - 42) + 2.9f;
				AbnorMean = 0.1f * ((int) buffer.charAt(7) - 42) + 7.9f;
				
				CheckSum(buffer);
				
			} else BarcodeStop(false);
			
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}	
	}
	
	private void CheckSum(StringBuffer buffer) {
		
		int test, year, month, day, line, locate, check, sum, index;
		
		try {
		
			if(ActionActivity.BarcodeQCCheckFlag) index = SerialPort.A1C_MAX_BUFFER_INDEX - 3;
			else index = SerialPort.A1C_QC_MAX_BUFFER_INDEX - 3;
			
			/* Classification for each digit barcode */
			test   = (int) buffer.charAt(0) - 64;
			year   = (int) buffer.charAt(1) - 64;
			if(year > 26) year -= 6;
			month  = (int) buffer.charAt(2) - 64;
			day    = (int) buffer.charAt(3) - 64;
			if(day > 26) day -= 6;
			line   = (int) buffer.charAt(4) - 64;
			locate = (int) buffer.charAt(5) - 42;
			check  = (int) buffer.charAt(index) - 48;
			
//			Log.w("CheckSum", "buffer.charAt(0) : " + buffer.charAt(0) + " index : " + test);
//			Log.w("CheckSum", "buffer.charAt(1) : " + buffer.charAt(1) + " index : " + year);
//			Log.w("CheckSum", "buffer.charAt(2) : " + buffer.charAt(2) + " index : " + month);
//			Log.w("CheckSum", "buffer.charAt(3) : " + buffer.charAt(3) + " index : " + day);
//			Log.w("CheckSum", "buffer.charAt(4) : " + buffer.charAt(4) + " index : " + line);
//			Log.w("CheckSum", "buffer.charAt(5) : " + buffer.charAt(5) + " index : " + locate);
//			Log.w("CheckSum", "buffer.charAt(index) : " + buffer.charAt(index) + " index : " + ((int) buffer.charAt(index) - 48 + 1));
			
			sum = (test + year + month + day + line + locate) % 10; // Checksum bit
//			Log.w("CheckSum", "sum : " + sum);
			checkExpirationDate((year + 13), month, day);
			
			if( sum == check ) { // Whether or not the correct barcode code
				
				BarcodeStop(true);
					
			} else {
				
				BarcodeStop(false);	
			}
			
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}
	
	private void checkExpirationDate(int year, int month, int day) {
		
		int sYear, sMonth, sDay, diffYear, diffMonth, diffDay;
		
		ActionActivity.IsExpirationDate = false;
		
		sYear = Integer.parseInt(MainTimer.rTime[0]) % 100;
		sMonth = Integer.parseInt(MainTimer.rTime[1]);
		sDay = Integer.parseInt(MainTimer.rTime[2]);
		
		diffYear = sYear - year;	
		diffMonth = sMonth - month;
		diffDay = sDay - day;
		
		if(diffYear == 0) {
			
			if(diffMonth > 0) ActionActivity.IsExpirationDate = true;
			else if(diffMonth == 0) {
				
				if(diffDay >= 0) ActionActivity.IsExpirationDate = true;
			}
			
		} else if(diffYear == 1) {
			
			if(diffMonth < 0) ActionActivity.IsExpirationDate = true;
			else if(diffMonth == 0) {
				
				if(diffDay < 0) ActionActivity.IsExpirationDate = true;
			}
		}		
	}
	
	private void BarcodeStop(boolean state) { // Turn off barcode module
		
		if(state) ActionActivity.IsCorrectBarcode = true;
		else ActionActivity.IsCorrectBarcode = false;
		
		ActionActivity.BarcodeCheckFlag = true;
	}
}
