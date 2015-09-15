package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.Presenter.DatePresenter;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DateActivity extends Activity implements DateIView {

	private DatePresenter mDatePresenter;
	
	private TextView titleText, yearText, monthText, dayText;

	private Button backBtn,
				   yearPBtn, 
				   yearMBtn, 
				   monthPBtn, 
				   monthMBtn, 
				   dayPBtn, 
				   dayMBtn,
				   snapshotBtn;

	private ImageView iconImage;
	
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting5);
		
		mDatePresenter = new DatePresenter(this, this, this, R.id.dateTimeLayout);
		mDatePresenter.init();
	}
	
	public void setImageId() {
		
		iconImage = (ImageView) findViewById(R.id.icon);
	}
	
	public void setImage() {
		
		iconImage.setBackgroundResource(R.drawable.date);
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		yearText = (TextView) findViewById(R.id.val1stText);
		monthText = (TextView) findViewById(R.id.val2ndText);
		dayText = (TextView) findViewById(R.id.val3rdText);
	}
	
	public void setTitleText() {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(R.string.datetitle);
	}
	
	public void setText(String year, String month, String day) {
		
		yearText.setText(year);
		monthText.setText(month);
		dayText.setText(day);
	}

	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		yearPBtn = (Button) findViewById(R.id.val1stpBtn);
		yearMBtn = (Button) findViewById(R.id.val1stmBtn);
		monthPBtn = (Button) findViewById(R.id.val2ndpBtn);
		monthMBtn = (Button) findViewById(R.id.val2ndmBtn);
		dayPBtn = (Button) findViewById(R.id.val3rdpBtn);
		dayMBtn = (Button) findViewById(R.id.val3rdmBtn);
		snapshotBtn = (Button)findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		yearPBtn.setOnTouchListener(mTouchListener);
		yearMBtn.setOnTouchListener(mTouchListener);
		monthPBtn.setOnTouchListener(mTouchListener);
		monthMBtn.setOnTouchListener(mTouchListener);
		dayPBtn.setOnTouchListener(mTouchListener);
		dayMBtn.setOnTouchListener(mTouchListener);
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonLongClick() {
		
		yearPBtn.setOnLongClickListener(mLongClickListener);
		yearMBtn.setOnLongClickListener(mLongClickListener);
		monthPBtn.setOnLongClickListener(mLongClickListener);
		monthMBtn.setOnLongClickListener(mLongClickListener);
		dayPBtn.setOnLongClickListener(mLongClickListener);
		dayMBtn.setOnLongClickListener(mLongClickListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					
					switch(v.getId()) {
					
					case R.id.val1stpBtn	:
						mDatePresenter.changeYearUp();
						break;
						
					case R.id.val1stmBtn	:
						mDatePresenter.changeYearDown();
						break;
						
					case R.id.val2ndpBtn	:
						mDatePresenter.changeMonthUp();
						break;
						
					case R.id.val2ndmBtn	:
						mDatePresenter.changeMonthDown();
						break;
						
					case R.id.val3rdpBtn	:
						mDatePresenter.changeDayUp();
						break;
						
					case R.id.val3rdmBtn	:
						mDatePresenter.changeDayDown();
						break;
						
					default	:
						break;
					}	
					
					break;

				case MotionEvent.ACTION_UP		:
					
					switch(v.getId()) {
					
					case R.id.backBtn		:
						mDatePresenter.changeActivity(v.getId());
						break;

					case R.id.snapshotBtn		:
						mDatePresenter.changeActivity(v.getId());
						break;
						
					default	:
						mDatePresenter.cancelTimer();
						break;
					}	
					
					break;
			}
			
			return false;
		}
	};
	
	Button.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		
		public boolean onLongClick(View v) {
			
			switch(v.getId()) {
			
			case R.id.val1stpBtn	:
				mDatePresenter.changeYearAutoUp();
				break;
				
			case R.id.val1stmBtn	:
				mDatePresenter.changeYearAutoDown();
				break;
				
			case R.id.val2ndpBtn	:
				mDatePresenter.changeMonthAutoUp();
				break;
				
			case R.id.val2ndmBtn	:
				mDatePresenter.changeMonthAutoDown();
				break;
				
			case R.id.val3rdpBtn	:
				mDatePresenter.changeDayAutoUp();
				break;
				
			case R.id.val3rdmBtn	:
				mDatePresenter.changeDayAutoDown();
				break;
				
			default	:
				break;
			}
			
			return false;
		}
	};
}
