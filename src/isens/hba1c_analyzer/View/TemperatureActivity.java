package isens.hba1c_analyzer.View;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.EngineerActivity;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.MainTimer;
import isens.hba1c_analyzer.Presenter.AboutPresenter;
import isens.hba1c_analyzer.Presenter.TemperaturePresenter;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TemperatureActivity extends Activity implements TemperatureIView {
	
	private TemperaturePresenter mTemperaturePresenter;
	
	private TextView chamTmpText,
					 ambTmpText;
	
	private EditText tmpEText;
	
	private Button backBtn,
				   setBtn;	

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.temperature);
		
		tmpEText = (EditText) findViewById(R.id.tmpEText);
		
		mTemperaturePresenter = new TemperaturePresenter(this, this, this, R.id.temperatureLayout);
		mTemperaturePresenter.init();
	}
	
	public void setTextId() {
		
		chamTmpText = (TextView)findViewById(R.id.chamTmpText);
		ambTmpText = (TextView)findViewById(R.id.ambTmpText);
	}
	
	public void setText(String txt1, String txt2) {
		
		chamTmpText.setText(txt1);
		ambTmpText.setText(txt2);
	}
	
	public void setEditTextId() {
		
		tmpEText = (EditText) findViewById(R.id.tmpEText);
	}
	
	public void setEditText(String txt) {
		
		tmpEText.setText(txt);
	}
	
	public void setButtonId() {
	
		backBtn = (Button)findViewById(R.id.backBtn);
		setBtn = (Button)findViewById(R.id.setBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		setBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}

	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				mTemperaturePresenter.unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.backBtn		:
					mTemperaturePresenter.changeActivity();
					break;
					
				case R.id.setBtn		:
					mTemperaturePresenter.changeChamTmp();
					break;
				
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public String getChamTmp() {
		
		String chamTmp = tmpEText.getText().toString();
		
		return chamTmp;
	}
}