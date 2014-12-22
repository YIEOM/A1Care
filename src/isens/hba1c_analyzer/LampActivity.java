package isens.hba1c_analyzer;

import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.CalibrationActivity.TargetMode;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.Cart1stFilter2;
import isens.hba1c_analyzer.RunActivity.CartDump;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LampActivity extends Activity {
	
	public SerialPort mSerialPort;
	public TimerDisplay mTimerDisplay;
	public ErrorPopup mErrorPopup;
	public Graph mGraph;
	
	public Button escIcon,
				  runBtn,
				  cancelBtn;
	
	public TextView adcText;
	
	public ImageView stateFlag1,
					 stateFlag2;
	
	public boolean btnState = false;
	
	public double f535nmValue[] = new double[100];
	
	public RunActivity.AnalyzerState photoState;

	public int checkError = RunActivity.NORMAL_OPERATION;
	
	public boolean isNormal = true;
	
	public SurfaceView mSurfaceView;
	
	public TextView adc1Text,
					adc2Text,
					adc3Text,
					adc4Text,
					adc5Text;
	
	public int adcMax,
			   adcMin;
	
	public boolean isMeasured = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.lamp);
		
		LampInit();
		
		/*Home Activity activation*/
		escIcon = (Button)findViewById(R.id.escicon);
		escIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					escIcon.setEnabled(false);
				
					WhichIntent(TargetIntent.Maintenance);
				}
			}
		});
		
		runBtn = (Button)findViewById(R.id.runbtn);
		runBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				runBtn.setEnabled(false);
				
				TestStart();
			}
		});
		
		cancelBtn = (Button)findViewById(R.id.cancelbtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				cancelBtn.setEnabled(false);
				
				isNormal = false;
			}
		});
	}
	
	public void LampInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.lamplayout);
		
		adcText = (TextView) findViewById(R.id.adctext);
		stateFlag1 = (ImageView) findViewById(R.id.stateflag1);
		stateFlag2 = (ImageView) findViewById(R.id.stateflag2);
		
		adc1Text = (TextView) findViewById(R.id.adc1text);
		adc2Text = (TextView) findViewById(R.id.adc2text);
		adc3Text = (TextView) findViewById(R.id.adc3text);
		adc4Text = (TextView) findViewById(R.id.adc4text);
		adc5Text = (TextView) findViewById(R.id.adc5text);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.graphbg);
		
		mGraph = new Graph(this, mSurfaceView);
		
		ADCAcquire(0);
		MeasureDisplay(false, CoordinateAcquire());
		
		mSerialPort = new SerialPort(R.id.lamplayout);
	}
	
//	public void TestStart() {
//
//		isNormal = true;
//		isMeasured = false;
//		
//		escIcon.setEnabled(false);
//		
//		for(int i = 0; i < 3; i++) {
//			
//			switch(photoState) {
//			
//			case MeasurePosition :
//				MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
//				BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.FilterDark);
//				break;
//				
//			case FilterDark :
//				MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
//				BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm);
//				break;
//				
//			case Filter535nm :
//				MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
//				BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter535nm);
//				break;
//				
//			default	:
//				break;
//			}
//		}
//		
//		if(photoState == AnalyzerState.Filter535nm) {
//		
//			DrawThread mDrawThread = new DrawThread(mGraph.GetHolder());
//			mDrawThread.start();
//
//			PhotoMeasure mPhotoMeasure = new PhotoMeasure();
//			mPhotoMeasure.start();
//		
//			cancelBtn.setEnabled(true);
//			
//		} else {
//			
//			TestCancel();
//		}
//	}
	
	public void TestStart() {

		isNormal = true;
		isMeasured = false;
		
		escIcon.setEnabled(false);
		
		TimerDisplay.RXBoardFlag = true;
		
		photoState = AnalyzerState.MeasurePosition;
		
		TestStart mTestStart = new TestStart(this, this, R.id.lamplayout);
		mTestStart.start();
	}
	
	public class TestStart extends Thread {
		
		Activity activity;
		Context context;
		int layoutid;
		
		TestStart(Activity activity, Context context, int layoutid) {
			
			this.activity = activity;
			this.context = context;
			this.layoutid = layoutid;
		}
		
		public void run() {
			
			for(int i = 0; i < 4; i++) {
				
				switch(photoState) {
				
				case MeasurePosition :
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
					BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.FilterDark, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case Filter535nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.NormalOperation, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					photoState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse			:
					photoState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			switch(checkError) {
			
			case RunActivity.NORMAL_OPERATION	:
				DrawThread mDrawThread = new DrawThread(mGraph.GetHolder());
				mDrawThread.start();

				PhotoMeasure mPhotoMeasure = new PhotoMeasure(activity, context, layoutid);
				mPhotoMeasure.start();
			
				cancelBtn.setEnabled(true);
				break;
				
			default	:
				mErrorPopup = new ErrorPopup(activity, context, layoutid);
				mErrorPopup.ErrorBtnDisplay(checkError);
				break;
			}
		}
	}
	
	public class PhotoMeasure extends Thread {
		
		boolean isOn = false;
		double adc;
		
		Activity activity;
		Context context;
		int layoutid;
		
		PhotoMeasure(Activity activity, Context context, int layoutid) {
			
			this.activity = activity;
			this.context = context;
			this.layoutid = layoutid;
		}
		
		public void run() {
			
			while(isNormal) {
				
				if(isOn) isOn = false;
				else isOn = true;
				
				while(isMeasured) SerialPort.Sleep(100);
				
				adc = AbsorbanceMeasure();
						
				if(adc != -1.0) {
					
					ADCAcquire(adc);
					MeasureDisplay(isOn, CoordinateAcquire());
					
					Log.w("Photo measure", "run");
					isMeasured = true;
				
				} else {
					
					isNormal = false;
				}
			}
			
			switch(checkError) {
			
			case RunActivity.NORMAL_OPERATION	:
				TestCancel();
				break;
				
			default	:
				mErrorPopup = new ErrorPopup(activity, context, layoutid);
				mErrorPopup.ErrorBtnDisplay(checkError);
				break;
			}
		}
	}
	
	public class Graph extends SurfaceView implements SurfaceHolder.Callback {

		SurfaceHolder holder;
		
		public Graph(Context context, SurfaceView surfaceView) {
			
			super(context); 
			
			holder = surfaceView.getHolder();
			
			holder.addCallback(this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
		
		public SurfaceHolder GetHolder() {
			
			return this.holder;
		}
	}
	
	public class DrawThread extends Thread {
		
		SurfaceHolder surfaceHolder;
		
		public DrawThread(SurfaceHolder surfaceHolder) {
			
			this.surfaceHolder = surfaceHolder;
		}
		
		public void run() {
			
			Canvas canvas = null;
			Paint pnt;
			
			int[] cdn = new int[5];
			float range;
			
			pnt = new Paint();
			pnt.setColor(Color.WHITE);
			pnt.setStrokeWidth(1);
			
			while(isNormal) {
			
				try {
				
					while(!isMeasured) SerialPort.Sleep(100);
					
					canvas = surfaceHolder.lockCanvas(null);
					
					canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
					cdn = CoordinateAcquire();
					
					if(cdn[5] != 0) {
						
						range = (float) 250/(cdn[5]*12);
						
						Log.w("DrawThread", "alpha : " + cdn[5]);
						
						for(int i = 0; i < (f535nmValue.length-1); i++) {
							
//							Log.w("DrawThread", "value : " + f535nmValue[i]);
							canvas.drawLine(4*i, (float) (250-((f535nmValue[i]-cdn[4])*range)), 4*(i+1), (float) (250-((f535nmValue[i+1]-cdn[4])*range)), pnt);
						}
					}
					
					isMeasured = false;
										
				} finally {
					
					surfaceHolder.unlockCanvasAndPost(canvas);
				}	
			}
		}
	}
	
	public void ADCAcquire(double adc) {
		
		if(adc != 0) {
		
			for(int i = 0; i < (f535nmValue.length-1); i++) {
				
				f535nmValue[i] = f535nmValue[i + 1];
			}
			
			f535nmValue[f535nmValue.length-1] = adc;
		
		} else {			
			
			for(int i = 0; i < f535nmValue.length; i++) {
				
				f535nmValue[i] = 0;
			}
		}
		
		ADCMaxMinAcquire(f535nmValue);
	}
	
	public void ADCMaxMinAcquire(double value[]) {
		
		adcMax = Integer.MIN_VALUE;
		adcMin = Integer.MAX_VALUE;
		
		for(int i = 0; i < value.length; i++) {
			
			if(value[i] > adcMax) adcMax = (int) value[i];
			if(value[i] < adcMin) adcMin = (int) value[i];
		}
		
		Log.w("ADCMaxMinAcquire", "Max : " + adcMax + " Min : " + adcMin);
	}
	
	public int ADCDiffrence() {
		
		Log.w("ADCDiffrence", "difference : " + (adcMax - adcMin));
		
		return (adcMax - adcMin)/8;
	}
	
	public int[] CoordinateAcquire() {
		
		int yCdn[] = new int[6];
		int diff;
		
		diff = ADCDiffrence();
		
		if((adcMin - 2*diff) < 0) {
			
			diff = adcMax/10;		
		}

		yCdn[0] = adcMax + 2*diff;
		yCdn[1] = adcMax - diff;
		yCdn[2] = adcMax - 4*diff;
		yCdn[3] = adcMax - 7*diff;
		yCdn[4] = adcMax - 10*diff;
		yCdn[5] = diff;
				
		return yCdn;
	}

	public void MeasureDisplay(final boolean flag, final int xCdn[]) {
		
		final String color;
		
		if(flag) color = "#04A458";
		else color = "#00000000";
		
		new Thread(new Runnable() {			
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {

						adcText.setText(Double.toString(f535nmValue[f535nmValue.length-1]));
						stateFlag1.setBackgroundColor(Color.parseColor(color));
						stateFlag2.setBackgroundColor(Color.parseColor(color));
						
						adc1Text.setText(Integer.toString(xCdn[0]) + " -");
						adc2Text.setText(Integer.toString(xCdn[1]) + " -");
						adc3Text.setText(Integer.toString(xCdn[2]) + " -");
						adc4Text.setText(Integer.toString(xCdn[3]) + " -");
						adc5Text.setText(Integer.toString(xCdn[4]) + " -");
					}
				});
			}
		}).start();
	}
	
	public void TestCancel() {
	
		ADCAcquire(0);
		isMeasured = true;
		photoState = AnalyzerState.FilterHome;
		
		for(int i = 0; i < 2; i++) {
			
			switch(photoState) {
			
			case FilterHome :
				MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
				BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.CartridgeHome, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
				break;
			
			case CartridgeHome :
				MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
				BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
				break;
				
			default	:
				break;
			}
		}
		
		TimerDisplay.RXBoardFlag = false;
	
		SerialPort.Sleep(1000);
		
		ADCAcquire(0);
		MeasureDisplay(false, CoordinateAcquire());
		
		photoState = AnalyzerState.MeasurePosition;
		
		new Thread(new Runnable() {			
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {

						runBtn.setEnabled(true);
						escIcon.setEnabled(true);
					}
				});
			}
		}).start();
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		mSerialPort.BoardTx(str, target);
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
		
		int time = 0;
		String rawValue;
		double douValue = 0;
		
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.PhotoSet);
		
		rawValue = mSerialPort.BoardMessageOutput();			
		
		while(rawValue.length() != 8) {
			
			rawValue = mSerialPort.BoardMessageOutput();			
				
			if(time++ > 50) {
				
				photoState = AnalyzerState.NoResponse;
			
				break;
			}
		
			SerialPort.Sleep(100);
		}	
	
		if(photoState != AnalyzerState.NoResponse) {

			douValue = Double.parseDouble(rawValue);
			
			return douValue;
		}
		
		return -1;
	}
	
	public void BoardMessage(String colRsp, AnalyzerState nextState, String errRsp, AnalyzerState errState, int rspTime) {
		
		int time = 0;
		String temp = "";
		
		rspTime = rspTime * 10;
		
		while(true) {
			
			temp = mSerialPort.BoardMessageOutput();
	
			if(temp.equals(colRsp)) {
				
				photoState = nextState;
				break;
			
			} else if(temp.equals(errRsp)) {
				
				photoState = errState;
				break;
			}
					
			if(time++ > rspTime) {
				
				photoState = AnalyzerState.NoResponse;
				checkError = R.string.e241;
				break;
			}
			
			SerialPort.Sleep(100);
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home				:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(HomeIntent);
			break;
			
		case Maintenance		:				
			Intent MaintenanceIntent = new Intent(getApplicationContext(), MaintenanceActivity.class);
			startActivity(MaintenanceIntent);
			break;
			
		default		:	
			break;			
		}
		
		finish();		
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
