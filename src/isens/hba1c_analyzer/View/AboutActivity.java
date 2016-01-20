package isens.hba1c_analyzer.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import isens.hba1c_analyzer.CustomKeyboard;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.AboutPresenter;
import isens.hba1c_analyzer.Presenter.AdjustmentPresenter;
import isens.hba1c_analyzer.Presenter.ConvertPresenter;
import isens.hba1c_analyzer.Presenter.Correction1Presenter;
import isens.hba1c_analyzer.Presenter.CorrelationPresenter;
import isens.hba1c_analyzer.Presenter.LanguagePresenter;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity implements AboutIView {
	
	private AboutPresenter mAboutPresenter;
	
	private ImageView iconImage;
	
	private TextView swVersionText,
					 fwVersionText,
					 osVersionText;
	
	private EditText hwSNEText;

	private Button backBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.about);
		
		mAboutPresenter = new AboutPresenter(this, this, this, R.id.aboutLayout);
		mAboutPresenter.init();
	}
	
	public void setImageId() {
		
		iconImage = (ImageView) findViewById(R.id.icon);
	}
	
	public void setImage() {
		
		iconImage.setBackgroundResource(R.drawable.about_icon);
	}
	
	public void setTextId() {
		
		swVersionText = (TextView) findViewById(R.id.swVersionText);
		fwVersionText = (TextView) findViewById(R.id.fwVersionText);
		osVersionText = (TextView) findViewById(R.id.osVersionText);
	}
	
	public void setText(String swVersion, String fwVersion, String osVersion) {
		
		swVersionText.setText(swVersion);
		fwVersionText.setText(fwVersion);
		osVersionText.setText(osVersion);
	}
	
	public void setEditTextId() {
		
		hwSNEText = (EditText) findViewById(R.id.hwSNEText);
	}
	
	public void setEditText(String text) {
		
		hwSNEText.setText(text);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setBackgroundResource(R.drawable.back_selector);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mAboutPresenter.changeActivity();
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public String getHWVersion() {
		
		String version = hwSNEText.getText().toString();
		
		return version;
	}
}
