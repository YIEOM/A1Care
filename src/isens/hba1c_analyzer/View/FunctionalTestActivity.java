package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.Presenter.ConvertPresenter;
import isens.hba1c_analyzer.Presenter.FunctionalTestPresenter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FunctionalTestActivity extends Activity implements FunctionalTestIView {

	private FunctionalTestPresenter mFunctionalTestPresenter;
	
	private ImageView titleImage;
	
	private Button backBtn, homeBtn, qcBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.functionaltest);
		
		mFunctionalTestPresenter = new FunctionalTestPresenter(this, this, this, R.id.functionalTestLayout);
		mFunctionalTestPresenter.init();
	}
	
	public void setImageId() {
		
		titleImage = (ImageView) findViewById(R.id.title);
	}
	
	public void setImage() {
		
		titleImage.setBackgroundResource(R.drawable.ft_title);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		homeBtn = (Button)findViewById(R.id.homeBtn);
		qcBtn = (Button)findViewById(R.id.qcBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		homeBtn.setOnTouchListener(mTouchListener);
		qcBtn.setOnTouchListener(mTouchListener);
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
					mFunctionalTestPresenter.changeActivity(v.getId());
					break;
					
				case R.id.homeBtn	:
					mFunctionalTestPresenter.changeActivity(v.getId());
					break;
					
				case R.id.qcBtn	:
					mFunctionalTestPresenter.changeActivity(v.getId());
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public int getIntentData() {
		
		Intent itn = getIntent();
		int state = itn.getIntExtra("System Check State", 0);
		
		return state;
	}
}
