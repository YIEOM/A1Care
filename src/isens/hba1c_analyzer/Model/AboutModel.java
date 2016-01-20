package isens.hba1c_analyzer.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import isens.hba1c_analyzer.EngineerActivity;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.SerialPort.CtrTarget;

public class AboutModel {
	
	final static String FW_VERSION =  "QV";
	public static String HWSN,
						 SWVersion,
						 FWVersion,
						 OSVersion;

	private SerialPort mSerialPort;
	private FileSystem mFileSystem;
	
	private Activity activity;
	
	public AboutModel(Activity activity) {
		
		this.activity = activity;
	}
	
	public void setHWVersion(String version) {
		
		HWSN = version;
		
		mFileSystem = new FileSystem(activity);
		mFileSystem.setPreferences("About", Activity.MODE_PRIVATE);
		mFileSystem.putStringPref("HW S/N", version);
		mFileSystem.commitPref();
	}
	
	public String getSWVersion() {
		
		GetSWVersion mGetSWVersion = new GetSWVersion(activity);
		mGetSWVersion.start();
		
		try {
		
			mGetSWVersion.join();
		
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
		
		return mGetSWVersion.getVersion();
	}
	
	public class GetSWVersion extends Thread {
		
		PackageInfo pi = null;
		String version;
		Activity activity;
		
		public GetSWVersion(Activity activity) {
			
			this.activity = activity;
		}
		
		public void run() {
			
			try {

				pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);

				version = pi.versionName;
				
			} catch (NameNotFoundException e) {

				e.printStackTrace();
				
				version = "Nothing";
			}		
		}
		
		public String getVersion() {
			
			return version;
		}
	}
	
	public String getFWVersion() {
		
		GetFWVersion mGetFWVersion = new GetFWVersion();
		mGetFWVersion.start();
		
		try {
			
			mGetFWVersion.join();
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		return mGetFWVersion.getVersion();
	}
	
	public class GetFWVersion extends Thread {
		
		String version;
		
		public void run() {
			
			String temp = "NR";
			int cnt = 0;
			
			while(TimerDisplay.RXBoardFlag) SerialPort.Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			mSerialPort = new SerialPort();
			mSerialPort.BoardTx(FW_VERSION, CtrTarget.NormalSet);
			
			do {	
			
				temp = mSerialPort.BoardMessageOutput();
				
				if(cnt++ == 20) {
					
					temp = "Nothing";
					break;
				}
				
				SerialPort.Sleep(100);
			
			} while(temp.equals("NR"));
			
			TimerDisplay.RXBoardFlag = false;
			
			version = temp;
		}
		
		public String getVersion() {
			
			return version;
		}
	}

	public String getOSVersion() {
		
		GetOSVersion mGetOSVersion = new GetOSVersion();
		mGetOSVersion.start();
		
		try {
			
			mGetOSVersion.join();
		
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		return mGetOSVersion.getVersion();
	}
	
	public class GetOSVersion extends Thread {
		
		String version;
		
		public void run() {
			
			try {
				
				int cnt = 0;
				
				Process shell = Runtime.getRuntime().exec("getprop ro.build.version.os");

				BufferedReader br = new BufferedReader(new InputStreamReader(shell.getInputStream()));
				String line = "", temp = "";
				
				while((line = br.readLine()) != null) {
				
					if(line.substring(0, 3).equals("ICS")) {
					
						temp = line;
					}
				}
				
				br.close();

				version = temp;
				
			} catch (IOException e) {
		
				version	= "Nothing";			
				throw new RuntimeException(e);
			}			
		}
		
		public String getVersion() {
			
			return version;
		}
	}
}
