package isens.hba1c_analyzer.Presenter;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import isens.hba1c_analyzer.DataStorage;
import isens.hba1c_analyzer.FileLoadActivity;
import isens.hba1c_analyzer.FileSaveActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RemoveActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.ShutDownPopup;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.ExportModel;
import isens.hba1c_analyzer.Model.FactorModel;
import isens.hba1c_analyzer.Model.FileSystem;
import isens.hba1c_analyzer.Model.ShellCommand;
import isens.hba1c_analyzer.View.AboutIView;
import isens.hba1c_analyzer.View.ExportIView;

public class ExportPresenter {

	private ExportIView mExportIView;
	private ActivityChange mActivityChange;
	private DataStorage mDataStorage;
	private ExportModel mExportModel;
	private ShellCommand mShellCommand;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	private int state = 0;
	private TargetIntent target;
	
	private boolean isExporting = true;
	
	public ExportPresenter(ExportIView view, Activity activity, Context context, int layout) {
		
		mExportIView = view;
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		String hwSN;
		int testNum;
		
		mExportIView.setImageId();
		mExportIView.setTextId();
		mExportIView.setText();
		
		mDataStorage = new DataStorage();
		mExportModel = new ExportModel(activity);
		
		mActivityChange.setIntent();
		hwSN = mActivityChange.getStringIntent("HWSN");
		target = mActivityChange.getTrgIntent("TargetIntent");
		testNum = mActivityChange.getIntIntent("DataCnt", 0);
		
		AniExportData mAniExportData = new AniExportData();
		mAniExportData.start();
		
		ExportFile mExportFile = new ExportFile(hwSN, target, testNum);
		mExportFile.start();
	}
	
	public class ExportFile extends Thread {
		
		String data = "",
			   hwSN;
		int	testNum;
		TargetIntent target;
		
		public ExportFile(String hwSN, TargetIntent target, int testNum) {
			
			this.hwSN = hwSN;
			this.target = target;
			this.testNum = testNum;
		}
		
		public void run() {
			
			if(!mExportModel.exportFile(hwSN, target, testNum)) state = R.string.e341;	
			
			try {
				
				Runtime.getRuntime().exec("ssu -c umount /mnt/usb").waitFor();
				Runtime.getRuntime().exec("ssu -c echo 1 > /sys/bus/usb/devices/2-1.1/bConfigurationValue").waitFor();
				DataStorage.Sleep(2000);
				Runtime.getRuntime().exec("ssu -c mount -t vfat /dev/block/sda1 /mnt/usb").waitFor();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			isExporting = false;
			
			changeActivity();
		}
	}
	
	public class AniExportData extends Thread {
		
		public void run() {
			
			int aniNum = 0;	
				
			while(isExporting) {
				
				switch(aniNum) {
					
				case 0 :
					mExportIView.setImage(R.drawable.shutdown_point_1);
					aniNum = 1;
					break;
				
				case 1 :
					mExportIView.setImage(R.drawable.shutdown_point_2);
					aniNum = 2;
					break;
					
				case 2 :
					mExportIView.setImage(R.drawable.shutdown_point_3);
					aniNum = 0;
					break;
					
				default	:	
					break;
				}
				
				DataStorage.Sleep(500);
			}
		}
	}
	
	public void changeActivity() {
		
		int dataPage = mActivityChange.getIntIntent("DataPage", 0);
		
		switch(target) {
		
		case ControlFileLoad	:
			mActivityChange.whichIntent(TargetIntent.ControlFileLoad);
			mActivityChange.putIntIntent("DataCnt", FileSaveActivity.DataCnt);
			mActivityChange.putIntIntent("DataPage", dataPage);
			mActivityChange.putTrgIntent("TargetIntent", target);
			mActivityChange.putIntIntent("System Check State", state);
			break;
			
		case PatientFileLoad	:
			mActivityChange.whichIntent(TargetIntent.PatientFileLoad);
			mActivityChange.putIntIntent("DataCnt", FileSaveActivity.DataCnt);
			mActivityChange.putIntIntent("DataPage", dataPage);
			mActivityChange.putTrgIntent("TargetIntent", target);
			mActivityChange.putIntIntent("System Check State", state);
			break;
		}
		
		mActivityChange.finish();
	}
}
