package isens.hba1c_analyzer.Model;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.SerialPort.CtrTarget;

public class TemperatureModel {
	
	private SerialPort mSerialPort;
	private FileSystem mFileSystem;
	
	private Activity activity;

	final static String TMP_CHAMBER = "VT",
						TMP_INNER = "IA";

	public static final int MaxInnerTmp1 = 33,
							MinInnerTmp1 = 8,
							MaxInnerTmp2 = 36,
							MinInnerTmp2 = 14;

	public static float InitChambTmp;
	public static char TmpSensorCode;
	private int tmpSensorCode;
	
	public TemperatureModel(Activity activity) {
		
		this.activity = activity;
		mSerialPort = new SerialPort();
		mFileSystem = new FileSystem(activity);
		tmpSensorCode = (int) TmpSensorCode;
	}
	
	public int getBtnID(int code) {
		
		return (R.id.tmp_1Btn + (code - 49)); // ASCII code : 1~9 (49~57)
	}
	
	public int getCode(int btnId) {
		
		return ('1' + btnId - R.id.tmp_1Btn); // ASCII code : 1~9 (49~57)
	}
	
	public boolean isChangeCode(int btnId) {
		
		boolean isChange = false;
		
		if(getBtnID(tmpSensorCode) == btnId) isChange = false;
		else isChange = true;

		
		return isChange;
	}
	
	public int getDeselectedBtnID(int btnId) {
		
		int deselectedBtnId = getBtnID(tmpSensorCode);
		
		tmpSensorCode = getCode(btnId);
		
		return deselectedBtnId;
	}
	
	public void setCode() {
		
		TmpSensorCode = (char) tmpSensorCode;
				
		mFileSystem.setPreferences("Code", Activity.MODE_PRIVATE);
		mFileSystem.putIntPref("Tmp Sensor", tmpSensorCode);
		mFileSystem.commitPref();
	}	
	
	public void setChambTmp(float tmp) {
		
		InitChambTmp = tmp;
		
		mFileSystem.setPreferences("Temperature", Activity.MODE_PRIVATE);
		mFileSystem.putFloatPref("Chamber Tmp", tmp);
		mFileSystem.commitPref();
		
		SetChambTmp mSetChambTmp = new SetChambTmp();
		mSetChambTmp.start();
	}
	
	public class SetChambTmp extends Thread {
		
		public void run() {
			
			double tmpDouble;
			String tmpString;
			DecimalFormat tmpFormat;
			
			while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			tmpDouble = (double) InitChambTmp * (double) 1670.17 + (double) 25891.34;
			tmpFormat = new DecimalFormat("#####0");

			if(tmpFormat.format(tmpDouble).length() == 5) tmpString = "0" + tmpFormat.format(tmpDouble);
			else tmpString = tmpFormat.format(tmpDouble);

			mSerialPort.BoardTx("R" + tmpString, CtrTarget.NormalSet);
			while(!mSerialPort.BoardMessageOutput().equals(tmpFormat.format(tmpDouble))) SerialPort.Sleep(100);
			
			TimerDisplay.RXBoardFlag = false;
		}
	}
	
	public double getChambTmp() { // Read current temperature of cell block
		
		GetChambTmp mGetChambTmp = new GetChambTmp();
		mGetChambTmp.start();
		
		try {
			
			mGetChambTmp.join();
		
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
		
		return mGetChambTmp.getTmp();
	}
	
	public class GetChambTmp extends Thread {
		
		double chambTmp;
		
		public void run() {
			
			double tmpRaw;
			String temp;
			
			while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			mSerialPort.BoardTx(TMP_CHAMBER, CtrTarget.NormalSet);
			
			do {	
			
				temp = mSerialPort.BoardMessageOutput();
				SerialPort.Sleep(10);

			} while(temp.equals(Hardware.NO_RESPONSE));
			
			TimerDisplay.RXBoardFlag = false;
			
			try {
				
				tmpRaw = Double.parseDouble(temp);

			} catch(NumberFormatException e) {
				
				tmpRaw = 0;
			}

			chambTmp = (tmpRaw / (double) 1670.17) - (double) 15.5;
		}
		
		public double getTmp() {
			
			return chambTmp;
		}
	}

	public double getInnerTmp() { // Read current temperature of cell block
		
		GetInnerTmp mGetInnerTmp = new GetInnerTmp();
		mGetInnerTmp.start();
		
		try {
			
			mGetInnerTmp.join();
		
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
		
		return mGetInnerTmp.getTmp();
	}
	
	public class GetInnerTmp extends Thread {
	
		double innerTmp;
		
		public void run() {
			
			int tmpADC;
			double tmpV;
			String tmpData;
			
			while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			mSerialPort.BoardTx(TMP_INNER, CtrTarget.NormalSet);
					
			do {
				
				tmpData = mSerialPort.SensorMessageOutput();
				SerialPort.Sleep(10);
				
			} while(!tmpData.substring(1, 2).equals("T"));
			
			TimerDisplay.RXBoardFlag = false;
			
			try {
				
				tmpADC = Integer.parseInt(tmpData.substring(2));

			} catch(NumberFormatException e) {
				
				tmpADC = 0;
			}
			
			if(TmpSensorCode == '1') {

				innerTmp = ((double) 0.1127 * tmpADC) - (double) 31.9410;
			
			} else {

				tmpV = ((double) 5/1024 * (tmpADC + 1));
				innerTmp = (double) tmpV / 0.01;
			}
		}
		
		public double getTmp() {
			
			return innerTmp;
		}
	}
}
