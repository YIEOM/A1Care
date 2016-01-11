package isens.hba1c_analyzer.Presenter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.ActionActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.Calibration;
import isens.hba1c_analyzer.Model.Draw;
import isens.hba1c_analyzer.Model.Hardware;
import isens.hba1c_analyzer.Model.LampModel;
import isens.hba1c_analyzer.Model.MainTimer;
import isens.hba1c_analyzer.Model.Calibration.TargetMode;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.View.LampIView;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LampPresenter {

	private LampIView mLampIView;
	private LampModel mLampModel;
	private MainTimer mMainTimer;
	private ActivityChange mActivityChange;
	private Calibration mCalibration;
	private Draw mDraw;
	
	private Activity activity;
	private Context context;
	private int layout;
	private SurfaceView mSurfaceView;
	
	public Handler handler = new Handler();
	public TimerTask FiveHundmsPeriod;
	public Timer timer;
	
	private TargetMode targetMode;
	private boolean isMeasure;
	
	public LampPresenter(LampIView view, Activity activity, Context context, int layout, SurfaceView mSurfaceView) {
		
		mLampIView = view;
		mLampModel = new LampModel(context);
		mMainTimer = new MainTimer(activity, layout);
		mActivityChange = new ActivityChange(activity, context);
		mCalibration = new Calibration(activity);
		mDraw = new Draw(context, mSurfaceView);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
		this.mSurfaceView = mSurfaceView;
	}
	
	public void init() {
		
		mLampIView.setImageId();
		mLampIView.setTextId();
		mLampIView.setButtonId();
		mLampIView.setImageBgColor("#00000000");
		mLampIView.setText(mLampModel.getInitforText());
		mLampIView.setButtonBg(mLampModel.getFilterBtn(AnalyzerState.FilterDark));

		unenabledAllTxt();
		
		SerialPort.Sleep(500);
		
		mLampIView.setButtonClick();
		targetMode = TargetMode.StandBy;
		isMeasure = false;
	}
	
	public void startRun() {
		
		targetMode = TargetMode.Run;
		
		StartRun mStartRun = new StartRun();
		mStartRun.start();
	}
	
	private class StartRun extends Thread {
		
		AnalyzerState state;
		
		public void run() {
			
			state = AnalyzerState.NormalOperation;
			mCalibration.startTest(state, AnalyzerState.InitPosition);
			
			mLampModel.setFMotor(state, mLampModel.getTrgFilter());
						
			new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable(){
			            public void run() {
			
			            	mLampIView.setButtonState(R.id.cancelBtn, true);
			       		}
			        });
			    }
			}).start();
			
			startTimer();
		}
	}
	
	private void startMeasure() {
		
		StartMeasure mStartMeasure = new StartMeasure();
		mStartMeasure.start();
	}
	
	private class StartMeasure extends Thread {
		
		float[] aDCVal = new float[LampModel.LAMP_ADC_BUF_SIZE];
		int[] cdn = new int[6];
		int idx;
		
		public void run() {
			
			idx = mLampModel.getIdx();
			
			aDCVal = mLampModel.measurePhoto();
			
			cdn = mLampModel.getCoordinate(mLampModel.getADCMaxMin(aDCVal));
			
			mDraw.drawGraph(aDCVal, idx, cdn);
			
			new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable(){
			            public void run() {
			
			            	displayCoordinate(aDCVal[idx], cdn);
			    		}
			        });
			    }
			}).start();
		}
	}
	
	public void cancelRun() {
		
		timer.cancel();
		
		CancelRun mCancelRun = new CancelRun();
		mCancelRun.start();		
	}
	
	private class CancelRun extends Thread {
		
		AnalyzerState state;
		
		public void run() {
			
			state = AnalyzerState.NormalOperation;
			mCalibration.finishTest(state, AnalyzerState.CartridgeDump);
			
			mLampModel.initADCArry();
			
			new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable(){
			            public void run() {
			
			            	targetMode = TargetMode.StandBy;
			        		mLampIView.setImageBgColor("#00000000");
			        		
			        		mLampModel.initADCArry();
			        		mLampIView.setText(mLampModel.getInitforText());
			        		enabledAllBtn();
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
						
						if(targetMode != TargetMode.StandBy) {
						
							if((cnt++ % 2) == 1) {
								
								displayDSinRun(true);
								startMeasure();
							
							} else displayDSinRun(false);
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(FiveHundmsPeriod, 0, 500); // Timer period : 500msec
	}
	
	private void displayCoordinate(float value, int cdn[]) {
		
		mLampIView.setText(mLampModel.toStringADC(value, cdn));
	}
	
	private void displayDSinRun(boolean data) {

		if(data) mLampIView.setImageBgColor("#04A458");
		else mLampIView.setImageBgColor("#00000000");
	}
	
	public void displayFilterBtn(AnalyzerState filter) {
		
		mLampIView.setButtonBg(mLampModel.getFilterBtn(filter));
	}
	
	public void enabledAllTxt() {
		
		mLampIView.setButtonState(R.id.adcText, true);
		mLampIView.setButtonState(R.id.adc1Text, true);
		mLampIView.setButtonState(R.id.adc2Text, true);
		mLampIView.setButtonState(R.id.adc3Text, true);
		mLampIView.setButtonState(R.id.adc4Text, true);
		mLampIView.setButtonState(R.id.adc5Text, true);
	}

	public void unenabledAllTxt() {
		
		mLampIView.setButtonState(R.id.adcText, false);
		mLampIView.setButtonState(R.id.adc1Text, false);
		mLampIView.setButtonState(R.id.adc2Text, false);
		mLampIView.setButtonState(R.id.adc3Text, false);
		mLampIView.setButtonState(R.id.adc4Text, false);
		mLampIView.setButtonState(R.id.adc5Text, false);
	}
	
	public void enabledAllBtn() {

		mLampIView.setButtonState(R.id.backBtn, true);
		mLampIView.setButtonState(R.id.runBtn, true);
		mLampIView.setButtonState(R.id.cancelBtn, true);
		mLampIView.setButtonState(R.id.darkBtn, true);
		mLampIView.setButtonState(R.id.f535nmBtn, true);
		mLampIView.setButtonState(R.id.f660nmBtn, true);
		mLampIView.setButtonState(R.id.f750nmBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mLampIView.setButtonState(R.id.backBtn, false);
		mLampIView.setButtonState(R.id.runBtn, false);
		mLampIView.setButtonState(R.id.cancelBtn, false);
		mLampIView.setButtonState(R.id.darkBtn, false);
		mLampIView.setButtonState(R.id.f535nmBtn, false);
		mLampIView.setButtonState(R.id.f660nmBtn, false);
		mLampIView.setButtonState(R.id.f750nmBtn, false);
	}
	
	public void changeActivity() {
		
		mActivityChange.whichIntent(TargetIntent.Engineer);
		mActivityChange.finish();
	}
}
