package isens.hba1c_analyzer.View;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.FileSystem;
import isens.hba1c_analyzer.Model.LocationModel;
import isens.hba1c_analyzer.Presenter.LocationPresenter;
import isens.hba1c_analyzer.Presenter.TemperaturePresenter;
import isens.hba1c_analyzer.RunActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
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

	private TextView chambTmpText,
					 innerTmpText;

	private EditText tmpEText;

	private Button backBtn,
				   setBtn,
				   tmp_1Btn,
				   tmp_2Btn,
				   snapshotBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.temperature);
		
		mTemperaturePresenter = new TemperaturePresenter(this, this, this, R.id.temperatureLayout);
		mTemperaturePresenter.init();
	}
	
	public void setEditTextId() {
		
		tmpEText = (EditText) findViewById(R.id.tmpEText);
	}
	
	public void setEditText(String text) {
		
		tmpEText.setText(text);
	}
	
	public void setTextId() {
		
		chambTmpText = (TextView)findViewById(R.id.chambTmpText);
		innerTmpText = (TextView)findViewById(R.id.innerTmpText);
	}
	
	public void setText(String chamTmp, String ambTmp) {
		
		chambTmpText.setText(chamTmp);
		innerTmpText.setText(ambTmp);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		setBtn = (Button)findViewById(R.id.setbtn);
		tmp_1Btn = (Button)findViewById(R.id.tmp_1Btn);
		tmp_2Btn = (Button)findViewById(R.id.tmp_2Btn);
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonBg(int btnId, int resId) {
		
		findViewById(btnId).setBackgroundResource(resId);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		setBtn.setOnTouchListener(mTouchListener);
		tmp_1Btn.setOnTouchListener(mTouchListener);
		tmp_2Btn.setOnTouchListener(mTouchListener);
		if(HomeActivity.ANALYZER_SW == RunActivity.DEVEL_OPERATION) snapshotBtn.setOnTouchListener(mTouchListener);
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
					mTemperaturePresenter.changeActivity(v.getId());
					break;
					
				case R.id.snapshotBtn	:
					mTemperaturePresenter.changeActivity(v.getId());
					break;
					
				case R.id.setbtn		:
					mTemperaturePresenter.setTmp();
					break;
				
				case R.id.tmp_1Btn		:
					mTemperaturePresenter.changeCode(v.getId());
					break;
					
				case R.id.tmp_2Btn		:
					mTemperaturePresenter.changeCode(v.getId());
					break;
					
				default	:
					break;
				}
			
				mTemperaturePresenter.enabledAllBtn();
				break;
			}
			
			return false;
		}
	};
	
	public String getChambTmp() {
		
		String tmp = tmpEText.getText().toString();
		
		return tmp;
	}
}