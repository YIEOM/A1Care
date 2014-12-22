package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TemperatureActivity extends Activity {
	
	public Temperature mTemperature;
	public TimerDisplay mTimerDisplay;
	
	public Button escBtn,
				  setBtn,
				  readBtn;	
	
//	public TextView tmptext;
	
	public EditText tmpEText;
		
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temperature);
		
		tmpEText = (EditText) findViewById(R.id.tmpetext);
		
		/*System setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Maintenance);
			}
		});
		
		setBtn = (Button)findViewById(R.id.setbtn);
		setBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				setBtn.setEnabled(false);
				
				TmpSave(Float.valueOf(tmpEText.getText().toString()).floatValue());
			}
		});
		
		readBtn = (Button)findViewById(R.id.readbtn);
		readBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				readBtn.setEnabled(false);
				
				TmpDisplay();
			}
		});
		
		TemperatureInit();
	}
	
	public void TemperatureInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.temperaturelayout);
		
		tmpEText.setText(Float.toString(Temperature.InitTmp));
	}

	public void TmpSave(float tmp) {
		
		SharedPreferences temperaturePref = getSharedPreferences("Temperature", MODE_PRIVATE);
		SharedPreferences.Editor temperatureedit = temperaturePref.edit();
		
		temperatureedit.putFloat("Cell Block", tmp);
		temperatureedit.commit();
		
		Temperature.InitTmp = tmp;
		
		mTemperature = new Temperature(R.id.temperaturelayout);
		mTemperature.TmpInit();
		
		setBtn.setEnabled(true);
	}
	
	public void TmpDisplay() {
		
		TmpDisplay mTmpDisplay = new TmpDisplay(this);
		mTmpDisplay.start();
	}
	
	public class TmpDisplay extends Thread {
		
		Activity activity;
		
		TmpDisplay(Activity activity) {
			
			this.activity = activity;
		}
		
		public void run() {
			
			final DecimalFormat tmpdfm = new DecimalFormat("0.0");
			final double tmpDouble;
			
			final TextView tmptext = (TextView)activity.findViewById(R.id.tmptext);
			
			mTemperature = new Temperature(R.id.temperaturelayout);
			mTemperature.CellTmpRead();
			
			SerialPort.Sleep(500);
			
			tmpDouble = mTemperature.CellTmpValue();
			Log.w("TmpDisplay", "tmpDouble : " + tmpDouble);
			
			new Thread(new Runnable() {
			    public void run() {    
			        runOnUiThread(new Runnable(){
			            public void run(){
			            	
			            	tmptext.setText(tmpdfm.format(tmpDouble));
			    			
			    			readBtn.setEnabled(true);
			            }
			        });
			    }
			}).start();
			
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case Maintenance	:
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