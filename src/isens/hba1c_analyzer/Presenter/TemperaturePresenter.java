package isens.hba1c_analyzer.Presenter;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import isens.hba1c_analyzer.ActionActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.FileSystem;
import isens.hba1c_analyzer.Model.MainTimer;
import isens.hba1c_analyzer.Model.Calibration.TargetMode;
import isens.hba1c_analyzer.Model.Hardware;
import isens.hba1c_analyzer.Model.TemperatureModel;
import isens.hba1c_analyzer.View.AboutIView;
import isens.hba1c_analyzer.View.TemperatureIView;

public class TemperaturePresenter {
	
	private TemperatureIView mTemperatureIView;
	private TemperatureModel mTemperatureModel;
	private Hardware mHardware;
	private MainTimer mMainTimer;
	private ActivityChange mActivityChange;
	private FileSystem mFileSystem;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	private Handler handler = new Handler();
	private TimerTask FiveHundmsPeriod;
	private Timer timer;
	
	public TemperaturePresenter(TemperatureIView view, Activity activity, Context context, int layout) {
		
		mTemperatureIView = view;
		mTemperatureModel = new TemperatureModel();
		mHardware = new Hardware();
		mMainTimer = new MainTimer(activity, layout);
		mActivityChange = new ActivityChange(activity, context);
		mFileSystem = new FileSystem(activity);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mTemperatureIView.setButtonId();
		mTemperatureIView.setTextId();
		mTemperatureIView.setEditTextId();
		
		mTemperatureIView.setEditText(mTemperatureModel.toStringTmp(Hardware.InitTmp));
		displayTmp();
		
		SerialPort.Sleep(500);
	
		mTemperatureIView.setButtonClick();
		
		startTimer();
	}
	
	private void displayTmp() {
		
		DisplayTmp mDisplayTmp = new DisplayTmp();
		mDisplayTmp.start();
	}
	
	private class DisplayTmp extends Thread {
		
		public void run() {
		
			final float chamTmp,
						innerTmp;
				
			chamTmp = mHardware.getChamTmp();
			innerTmp = mHardware.getInnerTmp(); 
			
			new Thread(new Runnable() {
				public void run() {    
					activity.runOnUiThread(new Runnable(){
						public void run(){
	
							display(chamTmp, innerTmp);
						}
					});
				}
			}).start();
		}
	}
	
	private void startTimer() {
		
		FiveHundmsPeriod = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						displayTmp();
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(FiveHundmsPeriod, 0, 500); // Timer period : 500msec
	}
	
	public void display(float chamTmp, float innerTmp) {
		
		final DecimalFormat dfm = new DecimalFormat("0.0");
		
		mTemperatureIView.setText(dfm.format(chamTmp), dfm.format(innerTmp));						
	}
	
	public void enabledAllBtn() {

		mTemperatureIView.setButtonState(R.id.backBtn, true);
		mTemperatureIView.setButtonState(R.id.setBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mTemperatureIView.setButtonState(R.id.backBtn, false);
		mTemperatureIView.setButtonState(R.id.setBtn, true);
	}
	
	public void changeChamTmp() {
		
		ChangeChamTmp mChangeChamTmp = new ChangeChamTmp();
		mChangeChamTmp.start();
	}
	
	private class ChangeChamTmp extends Thread {
		
		public void run() {
			
			Float tmpFloat;
			String tmpString = mTemperatureIView.getChamTmp();
			
			try {
				
				tmpFloat = Float.parseFloat(tmpString);
					
			} catch(NumberFormatException e) {
				
				tmpFloat = Hardware.InitTmp;
			}
			
			mFileSystem.setPreferences("Temperature", Activity.MODE_PRIVATE);
			mFileSystem.putFloatPref("Cell Block", tmpFloat);
			mFileSystem.commitPref();
			
			Hardware.InitTmp = tmpFloat;
			
			mHardware.setChamTmp();
			
			new Thread(new Runnable() {
				public void run() {    
					activity.runOnUiThread(new Runnable(){
						public void run(){

							enabledAllBtn();
						}
					});
				}
			}).start();
		}
	}
	
	public void changeActivity() {
		
		timer.cancel();
		
		mActivityChange.whichIntent(TargetIntent.Engineer);
		mActivityChange.finish();
	}
}
