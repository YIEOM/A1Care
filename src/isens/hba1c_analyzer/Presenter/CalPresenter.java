package isens.hba1c_analyzer.Presenter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import isens.hba1c_analyzer.ActionActivity;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.Calibration;
import isens.hba1c_analyzer.Model.MainTimer;
import isens.hba1c_analyzer.Model.Calibration.TargetMode;
import isens.hba1c_analyzer.Model.Hardware;
import isens.hba1c_analyzer.View.CalIView;

public class CalPresenter {
	
	private CalIView mCalIView;
	private Calibration mCalibration;
	private MainTimer mMainTimer;
	private ActivityChange mActivityChange;
	private Hardware mHardware;
	private ActionActivity mActionActivity;
	private SerialPort mSerialPort;
	
	private Activity activity;
	private Context context;
	private int layout;
	private TargetIntent target;
	
	public Handler handler = new Handler();
	public TimerTask fiftymsPeriod;
	public Timer timer;
	
	private TargetMode targetMode;
	private boolean isRxUART2 = true,
					isRun = true;
	
	public CalPresenter(CalIView view, Activity activity, Context context, int layout, TargetIntent target) {
		
		mCalIView = view;
		mCalibration = new Calibration(activity);
		mMainTimer = new MainTimer(activity, layout);
		mActivityChange = new ActivityChange(activity, context);
		mHardware = new Hardware();
		mActionActivity = new ActionActivity();
		mSerialPort = new SerialPort();
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
		this.target = target;
	}
	
	public void init() {
		
		mCalIView.setButtonId();
		mCalIView.setTextId();
		mCalIView.setText(mCalibration.getInitforText(target));
		mCalIView.setDSText("READY", "#3CB371");
		mCalIView.setButtonState(R.id.quickBtn, false);
		mCalIView.setButtonState(R.id.fullBtn, false);
		
		SerialPort.Sleep(500);
		
		mCalIView.setButtonClick();
		targetMode = TargetMode.StandBy;
		
		mSerialPort.startRxUART2();
		readPCCmd();
		
		startTimer();
	}
	
	public void startBlank(byte msgType) {

		mCalIView.setR1AbsText(mCalibration.getInitforAbsText());
		mCalIView.setR2AbsText(mCalibration.getInitforAbsText());
		mCalIView.setR3AbsText(mCalibration.getInitforAbsText());
		mCalIView.setR4AbsText(mCalibration.getInitforAbsText());
		mCalIView.setR5AbsText(mCalibration.getInitforAbsText());
		mCalIView.setR6AbsText(mCalibration.getInitforAbsText());
		mCalIView.setRstText(mCalibration.getInitforAbsText());
		
		isRxUART2 = false;
		isRun = false;
		targetMode = TargetMode.Blank;
		RunActivity.IsError = false;
		
		StartBlank mStartBlank = new StartBlank(activity, msgType);
		mStartBlank.start();
	}

	private class StartBlank extends Thread {
		
		Activity activity;
		AnalyzerState state;
		byte msgType;
		
		public StartBlank(Activity activity, byte msgType) {
			
			this.activity = activity;
			this.msgType = msgType;
		}
		
		public void run() {
			
			state = AnalyzerState.NormalOperation;
			state = mCalibration.startTest(state, AnalyzerState.InitPosition);
			
			state = mCalibration.measureA1CPhoto(state, AnalyzerState.MeasureDark);
			mHardware.setBlkVal(mCalibration.getADCValList());
			
			state = mCalibration.finishTest(state, AnalyzerState.FilterHome);

        	new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable(){
			            public void run() {
			
			            	targetMode = TargetMode.StandBy;
			            	displayDS(true, state);
			        		
			            	enabledAllBtn();
			    		}
			        });
			    }
			}).start();
        	
        	if(msgType != 0x00) mSerialPort.startUART2toPC(SerialPort.PC_QC_CAL, mCalibration.getBlankforTx());
    		isRxUART2 = true;
			isRun = true;
        	readPCCmd();
		}
	}
	
	public void startQuick(byte msgType) {

		isRxUART2 = false;
		isRun = false;
		if(target == TargetIntent.A1CCal) targetMode = TargetMode.QuickA1C;
		else targetMode = TargetMode.QuickACR;
		RunActivity.IsError = false;
		
		StartRun mStartRun = new StartRun(activity, msgType, (byte) 0);
		mStartRun.start();
	}

	public void startFull(byte msgType, byte num) {

		isRxUART2 = false;
		isRun = false;
		if(target == TargetIntent.A1CCal) targetMode = TargetMode.FullA1C;
		else targetMode = TargetMode.FullACR;
		
		RunActivity.IsError = false;

		StartRun mStartRun = new StartRun(activity, msgType, num);
		mStartRun.start();
	}
	
	private class StartRun extends Thread {
		
		Activity activity;
		AnalyzerState state;
		byte msgType,
			 num;
		
		public StartRun(Activity activity, byte msgType, byte num) {
			
			this.activity = activity;
			this.num = num;
			this.msgType = msgType;
		}
		
		public void run() {
		
			state = AnalyzerState.NormalOperation;
			state = mCalibration.shakeCMotor(state, AnalyzerState.Step1Position, targetMode);
			
			
			state = mCalibration.startTest(state, AnalyzerState.MeasurePosition);
			

			state = mCalibration.measureA1CPhoto(state, AnalyzerState.FilterDark);
			mHardware.setStep1stValue1(mCalibration.getADCValList());
						
			
			state = mCalibration.measureA1CPhoto(state, AnalyzerState.FilterDark);
			mHardware.setStep1stValue2(mCalibration.getADCValList());
			
			
			state = mCalibration.measureA1CPhoto(state, AnalyzerState.FilterDark);
			mHardware.setStep1stValue3(mCalibration.getADCValList());
			
			
			mCalibration.calStep1AbsforCal(target);
			
			new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable(){
			            public void run() {
			
			            	displayStp1Abs();
			    		}
			        });
			    }
			}).start();
			
			
			state = mCalibration.shakeCMotor(state, AnalyzerState.Step2Position, targetMode);
			
			
			state = mCalibration.startTest(state, AnalyzerState.MeasurePosition);
			
			
			state = mCalibration.measureA1CPhoto(state, AnalyzerState.FilterDark);
			mHardware.setStep2ndValue1(mCalibration.getADCValList());
			
			
			state = mCalibration.measureA1CPhoto(state, AnalyzerState.FilterDark);
			mHardware.setStep2ndValue2(mCalibration.getADCValList());
						
			
			state = mCalibration.measureA1CPhoto(state, AnalyzerState.FilterDark);
			mHardware.setStep2ndValue3(mCalibration.getADCValList());
			
			
			state = mCalibration.finishTest(state, AnalyzerState.CartridgeDump);
			
			
			mHardware.calStep2Absorbance();

        	new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable(){
			            public void run() {
			
			            	displayStp2Abs();
			            	
			            	targetMode = TargetMode.StandBy;
			            	displayDS(false, state);
			            	mCalIView.setButtonState(R.id.backBtn, true);
			            	mCalIView.setButtonState(R.id.blankBtn, true);
			    		}
			        });
			    }
			}).start();
			
        	if(msgType != 0x00) {
        	
        		if(targetMode == TargetMode.FullA1C || targetMode == TargetMode.FullACR ) mSerialPort.startUART2toPC(SerialPort.PC_QC_CAL, mCalibration.getFullforTx(msgType, num));
        		else mSerialPort.startUART2toPC(SerialPort.PC_QC_CAL, mCalibration.getQuickforTx());
        	}
        	
        	isRxUART2 = true;
			isRun = true;
        	readPCCmd();
		}
	}
	
	private void readPCCmd() {
		
		ReadPCCmd mReadPCCmd = new ReadPCCmd();
		mReadPCCmd.start();
	}
	
	private class ReadPCCmd extends Thread {
		
		public void run() {
			
			while(isRun) {
				
				ArrayList<Byte> pcCmd = new ArrayList<Byte>();
				final ArrayList<Byte> data = new ArrayList<Byte>();
				pcCmd = mSerialPort.getPCMsg();
				
				if(pcCmd.size() > 1) { // if message is nothing, size of pcCmd is 1
					
					final byte msgType = pcCmd.get(1); // message type
					int dataLen = (int) (pcCmd.get(2) << 8) + (int) pcCmd.get(3); // length of data
					
//					Log.w("ReadPCCmd", "MsGType : " + msgType);
//					Log.w("ReadPCCmd", "dataLen : " + dataLen);
					
					for(int i = 0; i < dataLen; i++) data.add(i, pcCmd.get(i+4)); 
					
					new Thread(new Runnable() {
					    public void run() {
					    	activity.runOnUiThread(new Runnable(){
					            public void run() {
					
					            	switch(msgType) {
					            	
					            	case Hardware.PC_QC_CONNECT	: // 7E0110000035D77F
					            		mSerialPort.startUART2toPC(SerialPort.PC_QC_CAL, mSerialPort.getHWSNforTx());
					            		break;
					            		
					            	case Hardware.PC_QC_BLANK	: // 7E01200000F0727F
					            		startBlank(Hardware.PC_QC_BLANK);
					            		break;
					            	
					            	case Hardware.PC_QC_QUICK	: // 7E012200009E127F
					            		startQuick(Hardware.PC_QC_QUICK);
					            		break;
					            		
					            	case Hardware.PC_QC_Hct	: // 7E013000010924A07F
					            		startFull(Hardware.PC_QC_Hct, data.get(0));
					            		break;
					            	
					            	case Hardware.PC_QC_CS	: // 7E014000010966D57F
					            		startFull(Hardware.PC_QC_CS, data.get(0));
					            		break;
					            	
				            		default	:
				            			mSerialPort.startUART2toPC(SerialPort.PC_QC_CAL, mSerialPort.getMsgTypeErrorforTx());
					            		break;
					            	}
					            }
					        });
					    }
					}).start();
				}
				
				SerialPort.Sleep(100);
			}
 		}
	}
	
	public void scanBarcode() {

		isRxUART2 = false;
		isRun = false;
		targetMode = TargetMode.Scan;
		
		ScanBarcode mScanBarcode = new ScanBarcode();
		mScanBarcode.start();
	}
	
	private class ScanBarcode extends Thread {
		
		public void run() {
			
			int waitCnt = 0;
			
			HomeActivity.MEASURE_MODE = HomeActivity.A1C;
			ActionActivity.BarcodeCheckFlag = false;
			SerialPort.BarcodeReadStart = false;
			ActionActivity.BarcodeQCCheckFlag = true;
			SerialPort.BarcodeBufIndex = 0;
			
			while(!ActionActivity.BarcodeCheckFlag) {
				
				if((waitCnt++ == 599)) break; 
				SerialPort.Sleep(100);
			}
			
			new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable() {
			            public void run() {
			            	
			            	mCalIView.setRstText(mCalibration.toStringRst(target, mHardware.getRstValue(target)));
			            	
			            	targetMode = TargetMode.StandBy;
			            	mCalIView.setButtonState(R.id.backBtn, true);
			            	mCalIView.setButtonState(R.id.blankBtn, true);
			            	mCalIView.setButtonState(R.id.scanBtn, true);
			    		}
			        });
			    }
			}).start();
        	
			isRxUART2 = true;
			isRun = true;
        	readPCCmd();
		}
	}
	
	private void startTimer() {
		
		fiftymsPeriod = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if(cnt++ == 1000) cnt = 1;
						
						if(isRxUART2) mSerialPort.UART2RxData();
						
						if((cnt % 10) == 0) {
						
							if((targetMode == TargetMode.Scan) && !ActionActivity.BarcodeCheckFlag) {
								
								mActionActivity.BarcodeScan();
								
							} else if(targetMode != TargetMode.StandBy) {
							
								if((cnt % 20) == 0) displayDSinRun(true);
								else displayDSinRun(false);
							}
							
							if(!RunActivity.IsError && !MainTimer.RXBoardFlag) displayTmp();
						}
					}
				};
				
				handler.post(updater);
			}
		};
		
		timer = new Timer();
		timer.schedule(fiftymsPeriod, 0, MainTimer.TIMER_PERIOD); // Timer period : 50msec
	}

	private void displayTmp() {
		
		DisplayTmp mDisplayTmp = new DisplayTmp();
		mDisplayTmp.start();
	}
	
	private class DisplayTmp extends Thread {
		
		public void run() {
			
			DecimalFormat tmpdfm = new DecimalFormat("0.0");
			final String rCmbTmp,
				   		 innerTmp;
        	
			rCmbTmp = tmpdfm.format((double) mHardware.getChamTmp());
        	innerTmp = tmpdfm.format((double) mHardware.getInnerTmp());
			
			new Thread(new Runnable() {
			    public void run() {
			    	activity.runOnUiThread(new Runnable(){
			            public void run() {
			            	
			            	mCalIView.setTmpText(rCmbTmp, innerTmp);	
			    		}
			        });
			    }
			}).start();
		}
	}
	
	private void displayDSinRun(boolean data) {

	 	switch(targetMode) {
		
		case Blank	:
			if(data) mCalIView.setDSText("BLANK", "#1E90FF");
			else mCalIView.setDSText("BLANK", "#FFFFFF");
			break;
			
		case QuickA1C	:
		case QuickACR	:
				if(data) mCalIView.setDSText("QUICK", "#04A458");
			else mCalIView.setDSText("QUICK", "#FFFFFF");
			break;
		
		case FullA1C	:
		case FullACR	:
			if(data) mCalIView.setDSText("FULL", "#023894");
			else mCalIView.setDSText("FULL", "#FFFFFF");
			break;
			
		default	:
			break;
		}
	}
	
	private void displayDS(boolean isBlank, AnalyzerState state) {

		switch(state) {
		
		case NormalOperation	:
			if(isBlank) mCalIView.setDSText("BLANK", "#565656");
			else mCalIView.setDSText("READY", "#565656");
			break;
			
		case ShakingMotorError		:
			mCalIView.setDSText("SHAKING ERROR", "#FF0000");
			break;
			
		case FilterMotorError		:
			mCalIView.setDSText("FILTER ERROR", "#FF0000");
			break;
			
		case NoResponse		:
			mCalIView.setDSText("RESPONSE ERROR", "#FF0000");
			break;
			
		case ErrorCover		:
			mCalIView.setDSText("COVER ERROR", "#FF0000");
			break;
			
		default	:
			break;
		}
	}
	
	private void displayStp1Abs() {
		
		mCalIView.setR1AbsText(mCalibration.convertAbstoString(Hardware.Stp1Abs1List));
		mCalIView.setR2AbsText(mCalibration.convertAbstoString(Hardware.Stp1Abs2List));
		mCalIView.setR3AbsText(mCalibration.convertAbstoString(Hardware.Stp1Abs3List));
	}
	
	private void displayStp2Abs() {
		
		mCalIView.setR4AbsText(mCalibration.convertAbstoString(Hardware.Stp2Abs1List));
		mCalIView.setR5AbsText(mCalibration.convertAbstoString(Hardware.Stp2Abs2List));
		mCalIView.setR6AbsText(mCalibration.convertAbstoString(Hardware.Stp2Abs3List));
	}
	
	public void enabledAllBtn() {

		mCalIView.setButtonState(R.id.backBtn, true);
		mCalIView.setButtonState(R.id.blankBtn, true);
		mCalIView.setButtonState(R.id.quickBtn, true);
		mCalIView.setButtonState(R.id.fullBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mCalIView.setButtonState(R.id.backBtn, false);
		mCalIView.setButtonState(R.id.blankBtn, false);
		mCalIView.setButtonState(R.id.quickBtn, false);
		mCalIView.setButtonState(R.id.fullBtn, false);
	}

	public void changeActivity() {
		
		isRun = false;
		timer.cancel();
		mSerialPort.stopRxUART2fromPC();
		
		mActivityChange.whichIntent(TargetIntent.Engineer);
		mActivityChange.finish();
	}
}
