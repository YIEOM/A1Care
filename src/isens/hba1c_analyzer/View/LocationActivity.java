package isens.hba1c_analyzer.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import isens.hba1c_analyzer.CustomKeyboard;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.AboutPresenter;
import isens.hba1c_analyzer.Presenter.AdjustmentPresenter;
import isens.hba1c_analyzer.Presenter.ConvertPresenter;
import isens.hba1c_analyzer.Presenter.Correction1Presenter;
import isens.hba1c_analyzer.Presenter.CorrelationPresenter;
import isens.hba1c_analyzer.Presenter.LanguagePresenter;
import isens.hba1c_analyzer.Presenter.LocationPresenter;
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

public class LocationActivity extends Activity implements LocationIView {
	
	private LocationPresenter mLocationPresenter;

	private Button backBtn,
				   lct_1st1stBtn, lct_1st2ndBtn, lct_1st3rdBtn, lct_1st4thBtn, lct_1st5thBtn, lct_1st6thBtn, lct_1st7thBtn, lct_1st8thBtn,
				   lct_2nd1stBtn, lct_2nd2ndBtn, lct_2nd3rdBtn, lct_2nd4thBtn, lct_2nd5thBtn, lct_2nd6thBtn, lct_2nd7thBtn, lct_2nd8thBtn,
				   lct_3rd1stBtn, lct_3rd2ndBtn, lct_3rd3rdBtn, lct_3rd4thBtn, lct_3rd5thBtn, lct_3rd6thBtn, lct_3rd7thBtn, lct_3rd8thBtn, 
				   lct_4th1stBtn, lct_4th2ndBtn, lct_4th3rdBtn, lct_4th4thBtn, lct_4th5thBtn, lct_4th6thBtn, lct_4th7thBtn, lct_4th8thBtn, 
				   lct_5th1stBtn, lct_5th2ndBtn, lct_5th3rdBtn, lct_5th4thBtn, lct_5th5thBtn, lct_5th6thBtn, lct_5th7thBtn, lct_5th8thBtn, 
 				   lct_6th1stBtn, lct_6th2ndBtn, lct_6th3rdBtn, lct_6th4thBtn, lct_6th5thBtn, lct_6th6thBtn, lct_6th7thBtn, lct_6th8thBtn, 
				   lct_7th1stBtn, lct_7th2ndBtn, lct_7th3rdBtn, lct_7th4thBtn, lct_7th5thBtn, lct_7th6thBtn, lct_7th7thBtn, lct_7th8thBtn,
				   snapshotBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.location);
		
		mLocationPresenter = new LocationPresenter(this, this, this, R.id.locationLayout);
		mLocationPresenter.init();
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setBackgroundResource(R.drawable.back_selector);
		lct_1st1stBtn = (Button)findViewById(R.id.lct_1st1stBtn);
		lct_1st2ndBtn = (Button)findViewById(R.id.lct_1st2ndBtn);
		lct_1st3rdBtn = (Button)findViewById(R.id.lct_1st3rdBtn);
		lct_1st4thBtn = (Button)findViewById(R.id.lct_1st4thBtn);
		lct_1st5thBtn = (Button)findViewById(R.id.lct_1st5thBtn);
		lct_1st6thBtn = (Button)findViewById(R.id.lct_1st6thBtn);
		lct_1st7thBtn = (Button)findViewById(R.id.lct_1st7thBtn);
		lct_1st8thBtn = (Button)findViewById(R.id.lct_1st8thBtn);
		lct_2nd1stBtn = (Button)findViewById(R.id.lct_2nd1stBtn);
		lct_2nd2ndBtn = (Button)findViewById(R.id.lct_2nd2ndBtn);
		lct_2nd3rdBtn = (Button)findViewById(R.id.lct_2nd3rdBtn);
		lct_2nd4thBtn = (Button)findViewById(R.id.lct_2nd4thBtn);
		lct_2nd5thBtn = (Button)findViewById(R.id.lct_2nd5thBtn);
		lct_2nd6thBtn = (Button)findViewById(R.id.lct_2nd6thBtn);
		lct_2nd7thBtn = (Button)findViewById(R.id.lct_2nd7thBtn);
		lct_2nd8thBtn = (Button)findViewById(R.id.lct_2nd8thBtn);
		lct_3rd1stBtn = (Button)findViewById(R.id.lct_3rd1stBtn); 
		lct_3rd2ndBtn = (Button)findViewById(R.id.lct_3rd2ndBtn);
		lct_3rd3rdBtn = (Button)findViewById(R.id.lct_3rd3rdBtn);
		lct_3rd4thBtn = (Button)findViewById(R.id.lct_3rd4thBtn);
		lct_3rd5thBtn = (Button)findViewById(R.id.lct_3rd5thBtn);
		lct_3rd6thBtn = (Button)findViewById(R.id.lct_3rd6thBtn);
		lct_3rd7thBtn = (Button)findViewById(R.id.lct_3rd7thBtn);
		lct_3rd8thBtn = (Button)findViewById(R.id.lct_3rd8thBtn);
		lct_4th1stBtn = (Button)findViewById(R.id.lct_4th1stBtn);
		lct_4th2ndBtn = (Button)findViewById(R.id.lct_4th2ndBtn);
		lct_4th3rdBtn = (Button)findViewById(R.id.lct_4th3rdBtn);
		lct_4th4thBtn = (Button)findViewById(R.id.lct_4th4thBtn);
		lct_4th5thBtn = (Button)findViewById(R.id.lct_4th5thBtn);
		lct_4th6thBtn = (Button)findViewById(R.id.lct_4th6thBtn);
		lct_4th7thBtn = (Button)findViewById(R.id.lct_4th7thBtn);
		lct_4th8thBtn = (Button)findViewById(R.id.lct_4th8thBtn); 
		lct_5th1stBtn = (Button)findViewById(R.id.lct_5th1stBtn);
		lct_5th2ndBtn = (Button)findViewById(R.id.lct_5th2ndBtn);
		lct_5th3rdBtn = (Button)findViewById(R.id.lct_5th3rdBtn);
		lct_5th4thBtn = (Button)findViewById(R.id.lct_5th4thBtn);
		lct_5th5thBtn = (Button)findViewById(R.id.lct_5th5thBtn);
		lct_5th6thBtn = (Button)findViewById(R.id.lct_5th6thBtn);
		lct_5th7thBtn = (Button)findViewById(R.id.lct_5th7thBtn);
		lct_5th8thBtn = (Button)findViewById(R.id.lct_5th8thBtn);
		lct_6th1stBtn = (Button)findViewById(R.id.lct_6th1stBtn);
		lct_6th2ndBtn = (Button)findViewById(R.id.lct_6th2ndBtn);
		lct_6th3rdBtn = (Button)findViewById(R.id.lct_6th3rdBtn);
		lct_6th4thBtn = (Button)findViewById(R.id.lct_6th4thBtn);
		lct_6th5thBtn = (Button)findViewById(R.id.lct_6th5thBtn);
		lct_6th6thBtn = (Button)findViewById(R.id.lct_6th6thBtn);
		lct_6th7thBtn = (Button)findViewById(R.id.lct_6th7thBtn);
		lct_6th8thBtn = (Button)findViewById(R.id.lct_6th8thBtn); 
		lct_7th1stBtn = (Button)findViewById(R.id.lct_7th1stBtn);
		lct_7th2ndBtn = (Button)findViewById(R.id.lct_7th2ndBtn);
		lct_7th3rdBtn = (Button)findViewById(R.id.lct_7th3rdBtn);
		lct_7th4thBtn = (Button)findViewById(R.id.lct_7th4thBtn);
		lct_7th5thBtn = (Button)findViewById(R.id.lct_7th5thBtn);
		lct_7th6thBtn = (Button)findViewById(R.id.lct_7th6thBtn);
		lct_7th7thBtn = (Button)findViewById(R.id.lct_7th7thBtn);
		lct_7th8thBtn = (Button)findViewById(R.id.lct_7th8thBtn);
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonBg(int btnId, int resId) {
		
		findViewById(btnId).setBackgroundResource(resId);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		lct_1st1stBtn.setOnTouchListener(mTouchListener);
		lct_1st2ndBtn.setOnTouchListener(mTouchListener);
		lct_1st3rdBtn.setOnTouchListener(mTouchListener);
		lct_1st4thBtn.setOnTouchListener(mTouchListener);
		lct_1st5thBtn.setOnTouchListener(mTouchListener);
		lct_1st6thBtn.setOnTouchListener(mTouchListener);
		lct_1st7thBtn.setOnTouchListener(mTouchListener);
		lct_1st8thBtn.setOnTouchListener(mTouchListener);
		lct_2nd1stBtn.setOnTouchListener(mTouchListener);
		lct_2nd2ndBtn.setOnTouchListener(mTouchListener);
		lct_2nd3rdBtn.setOnTouchListener(mTouchListener);
		lct_2nd4thBtn.setOnTouchListener(mTouchListener);
		lct_2nd5thBtn.setOnTouchListener(mTouchListener);
		lct_2nd6thBtn.setOnTouchListener(mTouchListener);
		lct_2nd7thBtn.setOnTouchListener(mTouchListener);
		lct_2nd8thBtn.setOnTouchListener(mTouchListener);
		lct_3rd1stBtn.setOnTouchListener(mTouchListener);
		lct_3rd2ndBtn.setOnTouchListener(mTouchListener);
		lct_3rd3rdBtn.setOnTouchListener(mTouchListener);
		lct_3rd4thBtn.setOnTouchListener(mTouchListener);
		lct_3rd5thBtn.setOnTouchListener(mTouchListener);
		lct_3rd6thBtn.setOnTouchListener(mTouchListener);
		lct_3rd7thBtn.setOnTouchListener(mTouchListener);
		lct_3rd8thBtn.setOnTouchListener(mTouchListener);
		lct_4th1stBtn.setOnTouchListener(mTouchListener);
		lct_4th2ndBtn.setOnTouchListener(mTouchListener);
		lct_4th3rdBtn.setOnTouchListener(mTouchListener);
		lct_4th4thBtn.setOnTouchListener(mTouchListener);
		lct_4th5thBtn.setOnTouchListener(mTouchListener);
		lct_4th6thBtn.setOnTouchListener(mTouchListener);
		lct_4th7thBtn.setOnTouchListener(mTouchListener);
		lct_4th8thBtn.setOnTouchListener(mTouchListener);
		lct_5th1stBtn.setOnTouchListener(mTouchListener);
		lct_5th2ndBtn.setOnTouchListener(mTouchListener);
		lct_5th3rdBtn.setOnTouchListener(mTouchListener);
		lct_5th4thBtn.setOnTouchListener(mTouchListener);
		lct_5th5thBtn.setOnTouchListener(mTouchListener);
		lct_5th6thBtn.setOnTouchListener(mTouchListener);
		lct_5th7thBtn.setOnTouchListener(mTouchListener);
		lct_5th8thBtn.setOnTouchListener(mTouchListener);
		lct_6th1stBtn.setOnTouchListener(mTouchListener);
		lct_6th2ndBtn.setOnTouchListener(mTouchListener);
		lct_6th3rdBtn.setOnTouchListener(mTouchListener);
		lct_6th4thBtn.setOnTouchListener(mTouchListener);
		lct_6th5thBtn.setOnTouchListener(mTouchListener);
		lct_6th6thBtn.setOnTouchListener(mTouchListener);
		lct_6th7thBtn.setOnTouchListener(mTouchListener);
		lct_6th8thBtn.setOnTouchListener(mTouchListener);
		lct_7th1stBtn.setOnTouchListener(mTouchListener);
		lct_7th2ndBtn.setOnTouchListener(mTouchListener);
		lct_7th3rdBtn.setOnTouchListener(mTouchListener);
		lct_7th4thBtn.setOnTouchListener(mTouchListener);
		lct_7th5thBtn.setOnTouchListener(mTouchListener);
		lct_7th6thBtn.setOnTouchListener(mTouchListener);
		lct_7th7thBtn.setOnTouchListener(mTouchListener);
		lct_7th8thBtn.setOnTouchListener(mTouchListener);
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
				
				mLocationPresenter.unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mLocationPresenter.changeActivity(v.getId());
					break;
					
				case R.id.snapshotBtn	:
					mLocationPresenter.changeActivity(v.getId());
					break;
					
				default	:
					mLocationPresenter.changeCode(v.getId());
					break;
				}
				
				mLocationPresenter.enabledAllBtn();
				break;
			}
			
			return false;
		}
	};
}
