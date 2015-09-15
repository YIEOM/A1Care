package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.ConvertPresenter;
import isens.hba1c_analyzer.Presenter.FunctionalTestPresenter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FunctionalTestActivity extends Activity implements FunctionalTestIView {

	private FunctionalTestPresenter mFunctionalTestPresenter;

	private TextView titleText,
					 qcText;
	
	private Button backBtn,
				   homeBtn,
				   qcBtn,
				   snapshotBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.functionaltest);
		
		mFunctionalTestPresenter = new FunctionalTestPresenter(this, this, this, R.id.functionalTestLayout);
		mFunctionalTestPresenter.init();
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		qcText = (TextView) findViewById(R.id.qcText);
	}
	
	public void setText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.functionaltesttitle);
		qcText.setPaintFlags(qcText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		qcText.setText(R.string.qc);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		homeBtn = (Button)findViewById(R.id.homeBtn);
		qcBtn = (Button)findViewById(R.id.qcBtn);
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		homeBtn.setOnTouchListener(mTouchListener);
		qcBtn.setOnTouchListener(mTouchListener);
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
					
				case R.id.snapshotBtn		:
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
