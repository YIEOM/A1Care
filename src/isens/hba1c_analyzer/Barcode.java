package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;

import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Barcode {

	final static double a1ref  = 0.01, 
						b1ref  = -0.07, 
						a21ref = 0.05, 
						b21ref = 0.03, 
						a22ref = 0.035, 
						b22ref = 0.04;
	
	public static String RefNum, Type;
	
	public static double a1 = 0.009793532,
						 b1 = -0.028,
						 a21 = 0.060055,
						 b21 = -0.003032,
						 a22 = 0.05014,
						 b22 = -0.004829,
						 a23 = 0.039032,
						 b23 = -0.005064,
						 L   = 5.1,
						 M 	= 7.1,
						 H   = 11.7,
						 NorMean = 0,
						 AbnorMean = 0;
	
	public static double Sm, Im, Ss, Is, Asm, Aim, Ass, Ais;

	public void BarcodeCheck(StringBuffer buffer) { // Check a barcode data
		
		int len; 
		
		len = buffer.length();
		
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
			
			if(type.equals("D") && (Type.equals("D") || Type.equals("W") || Type.equals("X"))) {
			
				Sm = 0.0237 * (((int) buffer.charAt(6) - 42) - 1) + 0.1;
				Im = 0.158 * (((int) buffer.charAt(7) - 42) - 1) - 6;
				Ss = 0.0003 * (((int) buffer.charAt(8) - 42) - 1);
				Is = 0.002 * (((int) buffer.charAt(9) - 42) - 1);
				
				Asm = 0.000316 * (((int) buffer.charAt(10) - 42) - 1) - 0.01;
				Aim = 0.00237 * (((int) buffer.charAt(11) - 42) - 1) - 0.1;
				Ass = 0.000004 * (((int) buffer.charAt(12) - 42) - 1);
				Ais = 0.00003 * (((int) buffer.charAt(13) - 42) - 1);
				
				CheckSum(buffer);
			
			} else if(type.equals("E") && (Type.equals("E") || Type.equals("Y") || Type.equals("Z"))) CheckSum(buffer); 
			else BarcodeStop(false);

		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}	
	}
	
	private void BarcodeHbA1CQC(StringBuffer buffer) {
		
		try {
			
			Type = buffer.substring(0, 1);
			
			if(Type.equals("W") || Type.equals("X") || Type.equals("Y") || Type.equals("Z")) {
				
				NorMean = 0.1 * ((int) buffer.charAt(6) - 42) + 2.9;
				AbnorMean = 0.1 * ((int) buffer.charAt(7) - 42) + 7.9;
				
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
			
			sum = (test + year + month + day + line + locate) % 10; // Checksum bit
			
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
		
		sYear = Integer.parseInt(TimerDisplay.rTime[0]) % 100;
		sMonth = Integer.parseInt(TimerDisplay.rTime[1]);
		sDay = Integer.parseInt(TimerDisplay.rTime[2]);
		
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
