package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.ConvertPresenter;
import isens.hba1c_analyzer.Presenter.FunctionalTestPresenter;
import isens.hba1c_analyzer.Presenter.RecordPresenter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordActivity extends Activity implements RecordIView {

	private RecordPresenter mRecordPresenter;

	private TextView titleText,
					 qcText;
	
	private Button backBtn,
				   patientBtn,
				   controlBtn,
				   snapshotBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.record);
		
		mRecordPresenter = new RecordPresenter(this, this, this, R.id.recordLayout);
		mRecordPresenter.init();
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
	}
	
	public void setText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.recordtitle);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		patientBtn = (Button)findViewById(R.id.patientBtn);
		patientBtn.setPaintFlags(patientBtn.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		patientBtn.setText(R.string.patientdata);
		controlBtn = (Button)findViewById(R.id.controlBtn);
		controlBtn.setPaintFlags(controlBtn.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		controlBtn.setText(R.string.controldata);
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		patientBtn.setOnTouchListener(mTouchListener);
		controlBtn.setOnTouchListener(mTouchListener);
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
					mRecordPresenter.changeActivity(v.getId());
					break;
					
				case R.id.patientBtn	:
					mRecordPresenter.changeActivity(v.getId());
					break;
					
				case R.id.controlBtn	:
					mRecordPresenter.changeActivity(v.getId());
					break;
					
				case R.id.snapshotBtn		:
					mRecordPresenter.changeActivity(v.getId());
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
