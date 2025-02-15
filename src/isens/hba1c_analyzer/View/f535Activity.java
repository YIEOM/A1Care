package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.Presenter.Correction1Presenter;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class f535Activity extends Activity implements FactorIView {
	
	private Correction1Presenter mCorrectionPresenter;
	
	private EditText fct1stEText, fct2ndEText;

	private ImageView titleImage, iconImage, fct1stImage, fct2ndImage;
	
	private Button backBtn;
	
//	public CustomKeyboard mCustomKeyboard;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting2);
		
		mCorrectionPresenter = new Correction1Presenter(this, this, this, R.id.setting2Layout);
		mCorrectionPresenter.init();
		
//		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
//			
//			/* */
//			
//			mCustomKeyboard = new CustomKeyboard(this, R.id.customkeyboard, R.layout.numkeyboard);
//			mCustomKeyboard.RegisterEditText(R.id.fct2ndtext);
//		
//			/* */
//		}
	}
	
	public void setImageId() {
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
		fct1stImage = (ImageView) findViewById(R.id.fct1st);
		fct2ndImage = (ImageView) findViewById(R.id.fct2nd);
	}
	
	public void setImage() {
		
		titleImage.setBackgroundResource(R.drawable.f535_title);
		iconImage.setBackgroundResource(R.drawable.abs57_icon);
		fct1stImage.setBackgroundResource(R.drawable.af_slope);
		fct2ndImage.setBackgroundResource(R.drawable.af_intercept);
	}
	
	public void setEditTextId() {
		
		fct1stEText = (EditText) findViewById(R.id.fct1stText);
		fct2ndEText = (EditText) findViewById(R.id.fct2ndText);
	}
	
	public void setEditText(String fct1stVal, String fct2ndVal) {
		
		fct1stEText.setText(fct1stVal);
		fct2ndEText.setText(fct2ndVal);
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
					mCorrectionPresenter.changeActivity();
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public float getFactor1st() {
		
		float value;
		
		try {
			
			value = Float.valueOf(fct1stEText.getText().toString()).floatValue();
			
		} catch (NumberFormatException e) {
			
			value = 1.0f;
		}
		
		return value;
	}
	
	public float getFactor2nd() {
		
		float value;
		
		try {
			
			value = Float.valueOf(fct2ndEText.getText().toString()).floatValue();
			
		} catch (NumberFormatException e) {
			
			value = 0f;
		}
		
		return value;
	}
}
