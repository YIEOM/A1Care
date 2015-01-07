package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class MaintenanceActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	
	public Button escBtn,
	  			  lampBtn,
	  			  adjustBtn,
	  			  calibrationBtn,
	  			  tempBtn;
	public TextView versionText;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.maintenance);
		
		versionText = (TextView)findViewById(R.id.versiontext);
		
		MaintenanceInit();
					
		/*Home Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					escBtn.setEnabled(false);
					
					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		/*Adjustment Factor Activity activation*/
		adjustBtn = (Button)findViewById(R.id.adjustbtn);
		adjustBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					adjustBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Adjustment);
				}
			}
		});
		
		/*Calibration Activity activation*/
		calibrationBtn = (Button)findViewById(R.id.calibrationbtn);
		calibrationBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					calibrationBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Calibration);
				}
			}
		});
		
		/*Temperature Activity activation*/
		tempBtn = (Button)findViewById(R.id.tempbtn);
		tempBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					tempBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Temperature);
				}
			}
		});
		
		/*Lamp Intensity Activity activation*/
		lampBtn = (Button)findViewById(R.id.lampbtn);
		lampBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					lampBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Lamp);
				}
			}
		});
	}
	
	public void MaintenanceInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.maintenancelayout);
		
		versionText.setText(HomeActivity.VERSION);
	}

	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home			:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
						
		case Lamp		:				
			nextIntent = new Intent(getApplicationContext(), LampActivity.class);
			break;
			
		case Adjustment		:				
			nextIntent = new Intent(getApplicationContext(), AdjustmentFactorActivity.class);
			break;

		case Calibration		:				
			nextIntent = new Intent(getApplicationContext(), CalibrationActivity.class);
			break;
			
		case Temperature		:				
			nextIntent = new Intent(getApplicationContext(), TemperatureActivity.class);
			break;
					
		default		:	
			break;			
		}
		
		startActivity(nextIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}