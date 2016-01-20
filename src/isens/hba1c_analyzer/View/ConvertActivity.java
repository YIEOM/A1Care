package isens.hba1c_analyzer.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.ConvertPresenter;
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

public class ConvertActivity extends Activity implements ConvertIView{
	
	private ConvertPresenter mConvertPresenter;
	
	private TextView titleText, convertText;

	private ImageView iconImage;
	
	private Button backBtn,
				   leftBtn,
				   rightBtn,
				   snapshotBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting4);
		
		mConvertPresenter = new ConvertPresenter(this, this, this, R.id.setting4Layout);
		mConvertPresenter.init();
	}
	
	public void setImageId() {
		
		iconImage = (ImageView) findViewById(R.id.icon);
	}
	
	public void setImage() {
		
		iconImage.setBackgroundResource(R.drawable.convert_icon);
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		convertText = (TextView) findViewById(R.id.mainText);
	}
	
	public void setText(int unit) {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.unitselectiontitle);
		convertText.setText(unit);
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
				
				mConvertPresenter.unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mConvertPresenter.changeActivity(v.getId());
					break;
					
				case R.id.leftBtn	:
					mConvertPresenter.changePrimaryDown();
					break;
					
				case R.id.rightBtn	:
					mConvertPresenter.changePrimaryUp();
					break;
					
				case R.id.snapshotBtn		:
					mConvertPresenter.changeActivity(v.getId());
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
