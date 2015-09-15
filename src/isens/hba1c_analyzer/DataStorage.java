package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class DataStorage extends Activity {
	
	final static String SAVE_DIRECTORY 		   = "/isens/save", // Save directory path name
						SAVE_USB_DIRECTORY 	   = "/mnt/usb/A1Care", // Save USB directory path name
						SAVE_CONTROL_FILENAME  = "/ControlData", // Save file name
						SAVE_PATIENT_FILENAME  = "/PatientData", // Save file name
						SAVE_HIS_FILENAME      = "/HistoryData", // Save file name
						SAVE_SNAPSHOT_FILENAME = "/SnapShot"; // Save file name
	
	public String SDCardState() { // Check insertion state of uSD card and research for mounted path
		
		String sdState = Environment.getExternalStorageState(),
			   sdPath;
		
		if(sdState.equals(Environment.MEDIA_MOUNTED)) { // Whether or not the uSD card is mounted 
			
			return sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			
		} else {
			
			return sdPath = Environment.MEDIA_UNMOUNTED;
		}
	}
	
	public synchronized void DataSave(byte type, StringBuffer sData) { // Save data to uSD card
				
		String sdPath = SDCardState();
		
		File dir = new File(sdPath + SAVE_DIRECTORY), // File directory
			 file = null;
		
		if(type == FileSaveActivity.CONTROL_TEST) {
		
			file = new File(sdPath + SAVE_DIRECTORY + SAVE_CONTROL_FILENAME + FileSaveActivity.TempDataCnt + ".txt"); // File
		
		} else if(type == FileSaveActivity.PATIENT_TEST) {
			
			file = new File(sdPath + SAVE_DIRECTORY + SAVE_PATIENT_FILENAME + FileSaveActivity.TempDataCnt + ".txt");
		}
		
		try {
			
			if(!dir.isDirectory()) { // if directory doesn't exist 

				dir.mkdirs();
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file, true);
			
			fos.write(sData.toString().getBytes());
			fos.write("\r\n".getBytes());
			fos.flush();
			fos.close();
			
			while(!file.exists()); // Wait until file is created
			
			FileSaveActivity.DataCnt++; // increasing the count of the data stored
			
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();					
			return;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return;
		}		
	}
	
	public synchronized void DataHistorySave(StringBuffer sData1, StringBuffer sData2) { // Save data to uSD card
		
		String sdPath = SDCardState();
		
		File dir = new File(sdPath + SAVE_DIRECTORY);
				
		File file = new File(sdPath + SAVE_DIRECTORY + SAVE_HIS_FILENAME + ".txt"); // File
				
		try {

			if(!dir.isDirectory()) { // if directory doesn't exist 

				dir.mkdirs();
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file, false);
			
			fos.write(sData1.toString().getBytes());
			fos.write("\t".getBytes());
			fos.write(sData2.toString().getBytes());
			fos.write("\r\n".getBytes());
			fos.flush();
			fos.close();
			
			while(!file.exists()); // Wait until file is created
			
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();					
			return;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return;
		}		
	}
	
	public String createUSBFile(String hwSN, byte type) {
		
		File dir = new File(SAVE_USB_DIRECTORY),
			 file = null;
		
		if(type == FileSaveActivity.CONTROL_TEST) {
			
			file = new File(SAVE_USB_DIRECTORY + SAVE_CONTROL_FILENAME + "_" + hwSN + ".txt");
		
		} else if(type == FileSaveActivity.PATIENT_TEST) {
			
			file = new File(SAVE_USB_DIRECTORY + SAVE_PATIENT_FILENAME + "_" + hwSN + ".txt");
		}
		
		try {
			
			if(file.exists()) file.delete();
			if(!checkUSBDirs()) dir.mkdirs();
				
			file.createNewFile();
			
			return file.getPath();
			
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();					
			file = null;
			return "";
			
		} catch (IOException e) {
			
			e.printStackTrace();
			file = null;
			return "";
		}
	}
		
	public synchronized void writeUSBData(String path, StringBuffer sData) {
		
		try {
			
			if(checkUSBDirs()) {
				
				FileOutputStream fos = new FileOutputStream(path, true);
					
				fos.write(sData.toString().getBytes());
				fos.write("\r\n".getBytes());
				fos.flush();
				fos.close();
			}
			
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();					
			return;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return;		
		}		
	}
	
	public synchronized void closeUSBFile(String path) {
		
		try {
			
			FileOutputStream fos = new FileOutputStream(path, true);
					
			fos.flush();
			fos.close();
		
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();					
			return;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return;
		}		
	}
	
	public String checkUSBFile(String hwSN, byte type) {
		
		File file = null;
		
		if(type == FileSaveActivity.CONTROL_TEST) {
			
			file = new File(SAVE_USB_DIRECTORY + SAVE_CONTROL_FILENAME + "_" + hwSN + ".txt");
		
		} else if(type == FileSaveActivity.PATIENT_TEST) {
			
			file = new File(SAVE_USB_DIRECTORY + SAVE_PATIENT_FILENAME + "_" + hwSN + ".txt");
		}
		
		if(checkUSBDirs()) return DataLoad(file.getPath());
		else return "";
	}
	
	public boolean checkUSBDirs() {
		
		File file = new File(SAVE_USB_DIRECTORY);
		
		if(file.exists()) return true;
		else return false;
	}
	
	public synchronized void saveSnapShot(Bitmap bmp, String[] str) { // Save data to uSD card
		
		String sdPath = SDCardState();
		
		File dir = new File(sdPath + SAVE_DIRECTORY), // File directory
			 file = null;
		
		file = new File(sdPath + SAVE_DIRECTORY + SAVE_SNAPSHOT_FILENAME + str[0] + str[1] + str[2] + str[3] + str[4] + str[5] + str[6] + ".png");
		
		try {
			
			if(!dir.isDirectory()) { // if directory doesn't exist 

				dir.mkdirs();
				file.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(file, true);
			
			bmp.compress(CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
			
			while(!file.exists()); // Wait until file is created
			
		} catch(FileNotFoundException e) {
			
			e.printStackTrace();					
			return;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return;
		}		
	}
	
	public String FileCheck(int num, int type) { // Checking specific file
		
		String sdPath = SDCardState(),
			   filePath = null;
		
		int dataCnt;
		
		dataCnt = (FileSaveActivity.DataCnt - num) % 9999;
		if(dataCnt == 0) dataCnt = 9999;
		
		if(type == (int) FileSaveActivity.CONTROL_TEST) {
			
			filePath = sdPath + SAVE_DIRECTORY + SAVE_CONTROL_FILENAME + dataCnt +".txt"; // File number : the latest data - number
		
		} else if(type == (int) FileSaveActivity.PATIENT_TEST) {
			
			filePath = sdPath + SAVE_DIRECTORY + SAVE_PATIENT_FILENAME + dataCnt +".txt"; // File number : the latest data - number
		}
		
		File file = new File(filePath);

		if(file.exists()) {
			
			return filePath;
			
		} else {
			
			return null;			
		}
	}
	
	public synchronized String DataLoad(String filePath) { // Loading to specific file
		
		String curline = "",
			   line = "";
		
		try {
			
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			while((curline = br.readLine()) != null) {
				
				line = curline;
			}
			
			fr.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();					
			return line;
		
		} catch (IOException e) {
			
			e.printStackTrace();					
			return line;
		}
		
		return line;
	}
	
	public synchronized void FileDelete(String filePath) {
		
		File file = new File(filePath);
		file.delete();
	}
	
	public static void Sleep(int t) {
		
		try {
			
			Thread.sleep(t);
			
		} catch(InterruptedException e) {
			
			e.printStackTrace();			
			return;
		}
	}
}
