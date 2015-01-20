package isens.hba1c_analyzer;

import isens.hba1c_analyzer.SerialPort.CtrTarget;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

public class Temperature extends SerialPort {

	TemperatureActivity mTemperatureActivity;

	final static String TEMPERATURE_CELLBLOCK = "VT",
						TEMPERATURE_AMBIENT   = "IA";
	
	public TextView TmpText;
	
	public static float InitTmp;

	static final int MaxAmbTmp = 39,
					 MinAmbTmp = 20;
	
	public double cellTmp,
				  ambTmp;
	
	public void TmpInit() {
		
		TmpInit mTmpInit = new TmpInit();
		mTmpInit.start();
	}
	
	public class TmpInit extends Thread {
		
		public void run() {
			
			double tmpDouble;
			String tmpString;
			DecimalFormat tmpFormat;
			
			tmpDouble = (double) InitTmp * (double) 1670.17 + (double) 25891.34;
			tmpFormat = new DecimalFormat("#####0");
			
			if(tmpFormat.format(tmpDouble).length() == 5) tmpString = "0" + tmpFormat.format(tmpDouble);
			else tmpString = tmpFormat.format(tmpDouble);
			
			TimerDisplay.RXBoardFlag = true;
			BoardTx("R" + tmpString, CtrTarget.TmpSet);
			while(!BoardMessageOutput().equals(tmpFormat.format(tmpDouble))) Sleep(100);
			
			if(TimerDisplay.layoutid != R.id.systemchecklayout) TimerDisplay.RXBoardFlag = false;
		}
	}
	
	public void CellTmpRead() { // Read current temperature of cell block
		
		CellTmpRead mCellTmpRead = new CellTmpRead();
		mCellTmpRead.start();
	}
	
	public class CellTmpRead extends Thread {
		
		public void run() {
			
			double tmpRaw;
			String temp;
			
			TimerDisplay.RXBoardFlag = true;
			
			BoardTx(TEMPERATURE_CELLBLOCK, CtrTarget.TmpCall);
			
			do {	
			
				temp = BoardMessageOutput();
				Sleep(10);
			
			} while(temp.equals("NR"));
			
			TimerDisplay.RXBoardFlag = false;
			
			tmpRaw = Double.parseDouble(temp);
			cellTmp = (tmpRaw / (double) 1670.17) - (double) 15.5;
			
	//		Log.w("CellTmpRead", "tmpDouble : " + tmpDouble);
		}
	}

	public double CellTmpValue() {
		
		return cellTmp;
	}

	public void AmbTmpRead() { // Read current temperature of cell block
		
		AmbTmpRead mAmbTmpRead = new AmbTmpRead();
		mAmbTmpRead.start();
	}
	
	public class AmbTmpRead extends Thread {
	
		public void run() {
			
			int tmpADC;
			double tmpV;
			String tmpData;
			
			TimerDisplay.RXBoardFlag = true;
			BoardTx(TEMPERATURE_AMBIENT, CtrTarget.AmbientTmpCall);
					
			do {
				
				tmpData = SensorMessageOutput();
				Sleep(10);
			
			} while(!tmpData.substring(1, 2).equals("T"));
			
			TimerDisplay.RXBoardFlag = false;
			
			tmpADC = Integer.parseInt(tmpData.substring(2));
			
			tmpV = ((double) 5/1024 * (tmpADC + 1));
			ambTmp = (double) tmpV / 0.01;
		}
	}

	public double AmbTmpValue() {
		
		return ambTmp;
	}
}
