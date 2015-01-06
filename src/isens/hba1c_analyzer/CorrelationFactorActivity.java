package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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

public class CorrelationFactorActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	
	public Button backIcon;
	
	public EditText slopeEText, 
					offsetEText;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.correlation);
				
		slopeEText = (EditText) findViewById(R.id.slopeetext);
		offsetEText = (EditText) findViewById(R.id.offsetetext);
		
		/*System setting Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				if(!btnState) {
					
					btnState = true;
				
					backIcon.setEnabled(false);
					
					GetCorrelationFactor();
					
					WhichIntent(TargetIntent.SystemSetting);
				}
			}
		});
		
		CorrelationInit();
	}
	
	public void CorrelationInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.corrlayout);
		
		slopeEText.setText(Float.toString(RunActivity.CF_Slope));
		offsetEText.setText(Float.toString(RunActivity.CF_Offset));
	}
	
	public void GetCorrelationFactor() {
		
		float slope,
			  offset;
		
		try {
		
			slope = Float.valueOf(slopeEText.getText().toString()).floatValue();
			offset = Float.valueOf(offsetEText.getText().toString()).floatValue();
			
		} catch (NumberFormatException e) {
			
			slope = RunActivity.CF_Slope;
			offset = RunActivity.CF_Offset;
		}
		
		CorrelationSave(slope, offset);
	}
	
	public void CorrelationSave(float slope, float offset) { // Saving number of user define parameter
		
		SharedPreferences correlationPref = getSharedPreferences("User Define", MODE_PRIVATE);
		SharedPreferences.Editor correlationedit = correlationPref.edit();
		
		correlationedit.putFloat("CF SlopeVal", slope);
		correlationedit.putFloat("CF OffsetVal", offset);
		correlationedit.commit();
		
		RunActivity.CF_Slope = slope;
		RunActivity.CF_Offset = offset;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case SystemSetting	:
			nextIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
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