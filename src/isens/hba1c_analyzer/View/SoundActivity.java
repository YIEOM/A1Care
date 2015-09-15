package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.DisplayPresenter;
import isens.hba1c_analyzer.Presenter.SoundPresenter;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.drawable;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.raw;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
	
public class SoundActivity extends Activity implements SoundIView {
	
	private SoundPresenter mSoundPresenter;
	
	private TextView titleText;
	
	private ImageView iconImage, barGaugeImage;
	
	private Button backBtn, 
				   minusBtn, 
				   plusBtn,
				   snapshotBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting3);
		
		mSoundPresenter = new SoundPresenter(this, this, this, R.id.setting3Layout);
		mSoundPresenter.init();
	}
	
	public void setImageId() {
		
		iconImage = (ImageView) findViewById(R.id.icon);
		barGaugeImage = (ImageView) findViewById(R.id.bargauge);
	}
	
	public void setImage() {
		
		iconImage.setBackgroundResource(R.drawable.sound);
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
	}
	
	public void setText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.soundtitle);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		minusBtn = (Button)findViewById(R.id.minusBtn);
		plusBtn = (Button)findViewById(R.id.plusBtn);
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		minusBtn.setOnTouchListener(mTouchListener);
		plusBtn.setOnTouchListener(mTouchListener);
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
				
				mSoundPresenter.unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mSoundPresenter.changeActivity(v.getId());
					break;
					
				case R.id.minusBtn	:
					mSoundPresenter.downSound();
					break;
					
				case R.id.plusBtn	:
					mSoundPresenter.upSound();
					break;
					
				case R.id.snapshotBtn		:
					mSoundPresenter.changeActivity(v.getId());
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public void setBarGaugeImage(int drawable) {
		
		barGaugeImage.setBackgroundResource(drawable);
	}
}
