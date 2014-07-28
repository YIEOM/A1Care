package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.TimerDisplay.whichClock;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BlankActivity extends Activity {

	private SerialPort BlankSerial;
	private RunActivity BlankRun;
	
	private static TextView TimeText;
	private ImageView barani;
	
	private RunActivity.AnalyzerState blankState;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.blank);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		
		barani = (ImageView) findViewById(R.id.progressBar);
		
		BlankInit();
	}                     
	
	public void BlankInit() {
		
		TimerDisplay.timerState = whichClock.BlankClock;		
		CurrTimeDisplay();
				
		BlankSerial = new SerialPort();
		BlankRun = new RunActivity();
		
		blankState = RunActivity.AnalyzerState.MeasurePosition;
		
		BlankStep BlankBlank = new BlankStep();
		BlankBlank.start();
	}
	
	public void CurrTimeDisplay() {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	TimeText.setText(TimerDisplay.rTime[3] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
		            }
		        });
		    }
		}).start();	
	}
	
	public class BlankStep extends Thread { // Blank run
		
		public void run() {
			
			for(int i = 0; i < 7; i++) {
				
				switch(blankState) {
				
				case MeasurePosition :
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.PhotoSet);			
					BoardMessage(RunActivity.OPERATE_COMPLETE, 5, RunActivity.AnalyzerState.FilterDark);
					BarAnimation(178);
					break;
					
				case FilterDark :
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.OPERATE_COMPLETE, 5, RunActivity.AnalyzerState.Filter535nm);
					BarAnimation(206);
					RunActivity.BlankValue[0] = 0;
					RunActivity.BlankValue[0] = AbsorbanceMeasure(); // Dark Absorbance
					break;
					
				case Filter535nm :
					/* 535nm filter Measurement */
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.OPERATE_COMPLETE, 5, RunActivity.AnalyzerState.Filter660nm);
					BarAnimation(290);
					RunActivity.BlankValue[1] = AbsorbanceMeasure(); // Dark Absorbance
					break;
				
				case Filter660nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.OPERATE_COMPLETE, 5, RunActivity.AnalyzerState.Filter750nm);
					BarAnimation(374);
					RunActivity.BlankValue[2] = AbsorbanceMeasure(); // Dark Absorbance
					break;
				
				case Filter750nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.OPERATE_COMPLETE, 5, RunActivity.AnalyzerState.FilterHome);
					BarAnimation(458);
					RunActivity.BlankValue[3] = AbsorbanceMeasure(); // Dark Absorbance
					break;
				
				case FilterHome :
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.OPERATE_COMPLETE, 5, RunActivity.AnalyzerState.CartridgeHome);
					BarAnimation(542);
					break;
				
				case CartridgeHome :
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.PhotoSet);
					BoardMessage(RunActivity.OPERATE_COMPLETE, 5, RunActivity.AnalyzerState.NoResponse);
					BarAnimation(579);
					SerialPort.Sleep(1000);
					WhichIntent(TargetIntent.Action);
					break;
				
				case NoResponse :
					Log.w("BlankStep", "NR");
					blankState = AnalyzerState.NoWorking;
					WhichIntent(TargetIntent.Home);
					break;
					
				default	:
					break;
				}
			}
			
		}
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		BlankSerial.BoardTx(str, target);
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
	
		int time = 0;
		String rawValue;
		double douValue = 0;
		
		BlankSerial.BoardTx("VH", SerialPort.CtrTarget.PhotoSet);
		
		rawValue = BlankSerial.BoardMessageOutput();			
		
		while(rawValue.length() != 8) {
		
			time++;
			rawValue = BlankSerial.BoardMessageOutput();			
				
			if(time > 50) blankState = AnalyzerState.NoResponse;
		
			SerialPort.Sleep(100);
		}	
		
		douValue = Double.parseDouble(rawValue);
		
		return (douValue - RunActivity.BlankValue[0]);
	}
	
	public void BoardMessage(String rspStr, int rspTime, AnalyzerState nextState) {
		
		int time = 0;
		boolean isNormalTime = true;
		
		rspTime = rspTime * 10;
		
		while(!rspStr.equals(BlankSerial.BoardMessageOutput())) {
			
			time++;
			
			if(time > rspTime) isNormalTime = false;
			
			SerialPort.Sleep(100);
		}
		
		if(isNormalTime) blankState = nextState;
		else blankState = AnalyzerState.NoResponse;
	}
	
	public void BarAnimation(final int x) {

		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(barani.getLayoutParams());
		            	margin.setMargins(x, 273, 0, 0);
		            	barani.setLayoutParams(new RelativeLayout.LayoutParams(margin));
		            }
		        });
		    }
		}).start();	
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Home	:				
			Intent HomeIntent = new Intent(getApplicationContext(), HomeActivity.class);
			HomeIntent.putExtra("System Check State", (int) HomeActivity.COMMUNICATION_ERROR);
			startActivity(HomeIntent);
			break;
			
		case Action	:				
			Intent ActionIntent = new Intent(getApplicationContext(), ActionActivity.class);
			startActivity(ActionIntent);
			break;
			
		default			:	
			break;
		}		
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
