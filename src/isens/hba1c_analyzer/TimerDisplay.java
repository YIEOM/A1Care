package isens.hba1c_analyzer;

import isens.hba1c_analyzer.Model.ShellCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class TimerDisplay {
	
	final static byte TIMER_PERIOD = 1000/20, // 1000/Hz
					  PERIOD_1sec  = 1000/TIMER_PERIOD, // 1 second
					  PERIOD_250ms = 250/TIMER_PERIOD; // 250 milisecond
	
	final static byte FILE_CLOSE 	= 0,
			  		  FILE_OPEN 	= 1,
			  		  FILE_NOT_OPEN = 2,
			  		  FILE_USB_OPEN = 3;
	
	public Timer timer;

	public GpioPort mGpioPort;
	public SerialPort mSerialPort;
	private ShellCommand mShellCommand;

	public Handler handler = new Handler();
	public static TimerTask FiftymsPeriod;

	public static byte ExternalDeviceBarcode = FILE_CLOSE;
	
	public static String rTime[] = new String[8];
	
	public TextView timeText;
	public ImageView deviceImage;
	
	public static Activity activity;
	public static int layoutid;
	
	public static boolean RXBoardFlag = false;
	private int isCmdCnt = 0;
	
	public void TimerInit() {
		
		mSerialPort = new SerialPort();
		mGpioPort = new GpioPort();
		
		FiftymsPeriod = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if(cnt++ == 1000) cnt = 1;
						
						if(RXBoardFlag) {
							
							mSerialPort.BoardRxData();
						}
						
						if((cnt % PERIOD_1sec) == 0) { // One second period
						
							RealTimeSec();
							
							ExternalDeviceCheck();
							
							mGpioPort.CartridgeSensorScan();
							
							if(Integer.parseInt(rTime[6]) == 0) { // Whenever 00 second
											
								CurrTimeDisplay();
							}
							
						} else if((cnt % PERIOD_250ms) == 0) {
							
							ExternalDeviceCheck();
							
							mGpioPort.CartridgeSensorScan();
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(FiftymsPeriod, 0, TIMER_PERIOD); // Timer period : 50ms
	}
	
	public void ExternalDeviceCheck() {
		
		try {
			
		    int lineNum = 0;		    
			
		    mShellCommand = new ShellCommand();
		    mShellCommand.setCommand("/system/bin/busybox lsusb");
		    BufferedReader br = mShellCommand.getMessage();
		    
			String line = "";
			
			while((line = br.readLine()) != null) {
			
				if(lineNum++ == 3) {
				
					if(getExternalBarcode(line)) openExternalBarcode();
					else openExternalUSB();
				}
			}
			
			br.close();
			
			if(lineNum == 3) {
			
				closeExternalDevice();
			}
			
		} catch (IOException e) {
	
			throw new RuntimeException(e);		
		}		
	}
	
	public void RealTimeSec() {
		
		Calendar c = Calendar.getInstance();
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		rTime[6] = dfm.format(c.get(Calendar.SECOND));
	}
	
	public void RealTime() { // Get current date and time
	
		Calendar c = Calendar.getInstance();
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		rTime[0] = Integer.toString(c.get(Calendar.YEAR));
		rTime[1] = dfm.format(c.get(Calendar.MONTH) + 1);
		rTime[2] = dfm.format(c.get(Calendar.DAY_OF_MONTH));
		
		if(c.get(Calendar.AM_PM) == 0) rTime[3] = "AM";
		else rTime[3] = "PM";			
		
		if(c.get(Calendar.HOUR) != 0) rTime[4] = dfm.format(c.get(Calendar.HOUR));
		else rTime[4] = "12";
		
		rTime[5] = dfm.format(c.get(Calendar.MINUTE));		
		rTime[6] = dfm.format(c.get(Calendar.SECOND));
		rTime[7] = dfm.format(c.get(Calendar.HOUR_OF_DAY));
	}
	
	public void ActivityParm(Activity act, int id) {
		
		activity = act;
		layoutid = id;
		
		CurrTimeDisplay();
		ExternalDeviceDisplay();
	}
	
	public void CurrTimeDisplay() {
		
		if(layoutid != R.id.systemchecklayout) {
			
			RealTime();
			
			timeText = (TextView) activity.findViewById(R.id.timeText);
			
			timeText.setText(rTime[4] + ":" + rTime[5] + " " + rTime[3]);
		}
	}
	
	private boolean getExternalBarcode(String line) {
		
		boolean isConnect = false;
		
		if(line.substring(23).equals("0483:5740")) isConnect = true;
		
		return isConnect;
	}
	
	private void openExternalBarcode() {
		
		if(ExternalDeviceBarcode != FILE_OPEN) {
			
			mSerialPort.HHBarcodeSerialInit();
			mSerialPort.HHBarcodeRxStart();
			
			ExternalDeviceDisplay(); 
		}
	}
	
	private void openExternalUSB() {
		
		if((ExternalDeviceBarcode != FILE_USB_OPEN) && (isCmdCnt++ < 10)) {
			
			mShellCommand = new ShellCommand();
	    
			if(isCmdCnt == 1) mShellCommand.setCommand("ssu -c echo 1 > /sys/bus/usb/devices/2-1.1/bConfigurationValue");
			
			mShellCommand.setCommand("/system/bin/busybox ls /dev/block/sda1");
			BufferedReader br = mShellCommand.getMessage();
			String line = "";
			
			try {
				
				while((line = br.readLine()) != null) {
		
					if(line.equals("/dev/block/sda1")) {
						
						mShellCommand.setCommand("ssu -c mount -t vfat /dev/block/sda1 /mnt/usb");
						ExternalDeviceBarcode = FILE_USB_OPEN;
						
						ExternalDeviceDisplay();
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	private void closeExternalDevice() {
		
		if(ExternalDeviceBarcode == FILE_OPEN || ExternalDeviceBarcode == FILE_USB_OPEN) {
			
			mShellCommand.setCommand("ssu -c umount /mnt/usb");
			
			ExternalDeviceBarcode = FILE_CLOSE;
			
			ExternalDeviceDisplay();
		}

		isCmdCnt = 0;
	}
	
	public void ExternalDeviceDisplay() {
		
		if(layoutid != R.id.systemchecklayout) {
			
			deviceImage = (ImageView) activity.findViewById(R.id.device);
			
			if(ExternalDeviceBarcode == FILE_OPEN || ExternalDeviceBarcode == FILE_USB_OPEN) deviceImage.setBackgroundResource(R.drawable.main_usb_c);
			else deviceImage.setBackgroundResource(R.drawable.main_usb);
		}
	}
}
