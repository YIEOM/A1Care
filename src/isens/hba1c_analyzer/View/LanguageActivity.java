package isens.hba1c_analyzer.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.string;
import isens.hba1c_analyzer.Presenter.LanguagePresenter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
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
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageActivity extends Activity implements LanguageIView {
		
	private LanguagePresenter mLanguagePresenter;
	
	private TextView titleText, languageText;

	private ImageView iconImage;
	
	private Button backBtn, 
				   leftBtn, 
				   rightBtn,
				   snapshotBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting4);
		
		mLanguagePresenter = new LanguagePresenter(this, this, this, R.id.setting4Layout);
		mLanguagePresenter.init();
	}
	
	public void setImageId() {
		
		iconImage = (ImageView) findViewById(R.id.icon);
	}
	
	public void setImage() {
		
		iconImage.setBackgroundResource(R.drawable.language);
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		languageText = (TextView) findViewById(R.id.mainText);
	}
	
	public void setText(int language) {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.languagetitle);
		languageText.setText(language);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		leftBtn = (Button)findViewById(R.id.leftBtn);
		rightBtn = (Button)findViewById(R.id.rightBtn);
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		leftBtn.setOnTouchListener(mTouchListener);
		rightBtn.setOnTouchListener(mTouchListener);
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}

	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				mLanguagePresenter.unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mLanguagePresenter.changeActivity(v.getId());
					break;
					
				case R.id.leftBtn	:
					mLanguagePresenter.downLanguage();
					break;
					
				case R.id.rightBtn	:
					mLanguagePresenter.upLanguage();
					break;
					
				case R.id.snapshotBtn		:
					mLanguagePresenter.changeActivity(v.getId());
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
}
