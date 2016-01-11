package isens.hba1c_analyzer.View;

import java.util.ArrayList;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.CaptureScreen;
import isens.hba1c_analyzer.Model.CustomTextView;
import isens.hba1c_analyzer.Presenter.AboutPresenter;
import isens.hba1c_analyzer.Presenter.RecordDataPresenter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecordDataActivity extends Activity implements RecordDataIView {

	private RecordDataPresenter mRecordDataPresenter;
	private ActivityChange mActivityChange;
	
	private RelativeLayout record2Layout;
	private View dPopupView;
	private PopupWindow detailPopup;
	
	private Activity activity;
	
	private TextView testNumText [] = new TextView[5],
					 typeText    [] = new TextView[5],
					 resultText  [] = new TextView[5],
					 unitText    [] = new TextView[5],
					 dateTimeText[] = new TextView[5],
					 titleText,
					 pageText,
					 dTestNumText,
					 dTestDateText,
					 dTypeText,
					 dPrimaryText,
					 dRangeText,
					 dLotText,
					 dPIDText,
					 dOIDText,
					 dRstText;
					
	private CustomTextView dTitleText;
	
	private Button homeBtn,
				   backBtn,
				   exportBtn,
				   preViewBtn,
				   detailViewBtn,
				   nextViewBtn,
				   snapshotBtn,
				   dPrintBtn,
				   dCancelBtn,
				   dSnapshotBtn;
	
	private ImageButton checkBox1IBtn,
						checkBox2IBtn,
						checkBox3IBtn,
						checkBox4IBtn,
						checkBox5IBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.record2);
		
		activity = this;
		
		dTitleText = new CustomTextView(this);
		mActivityChange = new ActivityChange(this, this);
		mActivityChange.setIntent();
		TargetIntent target = mActivityChange.getTrgIntent("TargetIntent");
		
		mRecordDataPresenter = new RecordDataPresenter(this, this, this, R.id.record2Layout, target);
		mRecordDataPresenter.init();
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		pageText = (TextView) findViewById(R.id.pageText);
		
		testNumText [0] = (TextView) findViewById(R.id.testNum1Text);
		typeText    [0] = (TextView) findViewById(R.id.type1Text);
		resultText  [0] = (TextView) findViewById(R.id.result1Text);
		unitText    [0] = (TextView) findViewById(R.id.unit1Text);
		dateTimeText[0] = (TextView) findViewById(R.id.dateTime1Text);
		
		testNumText [1] = (TextView) findViewById(R.id.testNum2Text);
		typeText    [1] = (TextView) findViewById(R.id.type2Text);
		resultText  [1] = (TextView) findViewById(R.id.result2Text);
		unitText    [1] = (TextView) findViewById(R.id.unit2Text);
		dateTimeText[1] = (TextView) findViewById(R.id.dateTime2Text);
		
		testNumText [2] = (TextView) findViewById(R.id.testNum3Text);
		typeText    [2] = (TextView) findViewById(R.id.type3Text);
		resultText  [2] = (TextView) findViewById(R.id.result3Text);
		unitText    [2] = (TextView) findViewById(R.id.unit3Text);
		dateTimeText[2] = (TextView) findViewById(R.id.dateTime3Text);
		
		testNumText [3] = (TextView) findViewById(R.id.testNum4Text);
		typeText    [3] = (TextView) findViewById(R.id.type4Text);
		resultText  [3] = (TextView) findViewById(R.id.result4Text);
		unitText    [3] = (TextView) findViewById(R.id.unit4Text);
		dateTimeText[3] = (TextView) findViewById(R.id.dateTime4Text);
		
		testNumText [4] = (TextView) findViewById(R.id.testNum5Text);
		typeText    [4] = (TextView) findViewById(R.id.type5Text);
		resultText  [4] = (TextView) findViewById(R.id.result5Text);
		unitText    [4] = (TextView) findViewById(R.id.unit5Text);
		dateTimeText[4] = (TextView) findViewById(R.id.dateTime5Text);
	}
	
	public void setText(String txt) {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(txt);
	}
	
	public void setRecord1Text(ArrayList<String> txtList) {
		
		testNumText [0].setText(txtList.get(0));
		dateTimeText[0].setText(txtList.get(1));
		typeText    [0].setText(txtList.get(2));
		resultText  [0].setText(txtList.get(3));
		unitText    [0].setText(txtList.get(4));
	}
	
	public void setRecord2Text(ArrayList<String> txtList) {
		
		testNumText [1].setText(txtList.get(0));
		dateTimeText[1].setText(txtList.get(1));
		typeText    [1].setText(txtList.get(2));
		resultText  [1].setText(txtList.get(3));
		unitText    [1].setText(txtList.get(4));
	}
	
	public void setRecord3Text(ArrayList<String> txtList) {
		
		testNumText [2].setText(txtList.get(0));
		dateTimeText[2].setText(txtList.get(1));
		typeText    [2].setText(txtList.get(2));
		resultText  [2].setText(txtList.get(3));
		unitText    [2].setText(txtList.get(4));
	}
	
	public void setRecord4Text(ArrayList<String> txtList) {
		
		testNumText [3].setText(txtList.get(0));
		dateTimeText[3].setText(txtList.get(1));
		typeText    [3].setText(txtList.get(2));
		resultText  [3].setText(txtList.get(3));
		unitText    [3].setText(txtList.get(4));
	}
	
	public void setRecord5Text(ArrayList<String> txtList) {
		
		testNumText [4].setText(txtList.get(0));
		dateTimeText[4].setText(txtList.get(1));
		typeText    [4].setText(txtList.get(2));
		resultText  [4].setText(txtList.get(3));
		unitText    [4].setText(txtList.get(4));
	}
	
	public void setPageText(String txt) {
		
		pageText.setText(txt);
	}
	
	public void setPopupWindowId() {
		
		record2Layout = (RelativeLayout)findViewById(R.id.record2Layout);
		dPopupView = View.inflate(this, R.layout.detailviewpopup, null);
		detailPopup = new PopupWindow(dPopupView, 800, 480, true);
	}
	
	public void setPopupWindow() {
		
		detailPopup.showAtLocation(record2Layout, Gravity.CENTER, 0, 0);
		detailPopup.setAnimationStyle(0);
	}
	
	public void dismissPopupWindow() {
		
		detailPopup.dismiss();
	}
	
	public void setDetailTextId() {
		
		dTitleText = (CustomTextView) dPopupView.findViewById(R.id.dTitleText);
		dTestNumText = (TextView) dPopupView.findViewById(R.id.dTestNumText);
		dTestDateText = (TextView) dPopupView.findViewById(R.id.dTestDateText);
		dTypeText = (TextView) dPopupView.findViewById(R.id.dTypeText);
		dRstText = (TextView) dPopupView.findViewById(R.id.dRstText);
		dPrimaryText = (TextView) dPopupView.findViewById(R.id.dPrimaryText);
		dRangeText = (TextView) dPopupView.findViewById(R.id.dRangeText);
		dLotText = (TextView) dPopupView.findViewById(R.id.dLotText);
		dPIDText = (TextView) dPopupView.findViewById(R.id.dPIDText);
		dOIDText = (TextView) dPopupView.findViewById(R.id.dOIDText);
	}
	
	public void setDetailText(ArrayList<String> txtList) {
		
		dTitleText.setPaintFlags(dTitleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		dTitleText.setTextScaleX(0.9f);
		dTitleText.setLetterSpacing(3);
		dTitleText.setText(R.string.detailview);
		
		dTestNumText.setText(txtList.get(0));
		dTestDateText.setText(txtList.get(1));
		dTypeText.setText(txtList.get(2));
		dRstText.setText(txtList.get(3));
		dPrimaryText.setText(txtList.get(4));
		dRangeText.setText(txtList.get(5));
		dLotText.setText(txtList.get(6));
		dPIDText.setText(txtList.get(7));
		dOIDText.setText(txtList.get(8));
	}
	
	public void setButtonId() {
		
		backBtn = (Button) findViewById(R.id.backBtn);
		homeBtn = (Button) findViewById(R.id.homeBtn);
		exportBtn = (Button) findViewById(R.id.exportBtn);
		preViewBtn = (Button) findViewById(R.id.preViewBtn);
		detailViewBtn = (Button) findViewById(R.id.detailViewBtn);
		nextViewBtn = (Button) findViewById(R.id.nextViewBtn);
		snapshotBtn = (Button) findViewById(R.id.snapshotBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		homeBtn.setOnTouchListener(mTouchListener);
		exportBtn.setOnTouchListener(mTouchListener);
		preViewBtn.setOnTouchListener(mTouchListener);
		detailViewBtn.setOnTouchListener(mTouchListener);
		nextViewBtn.setOnTouchListener(mTouchListener);
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) snapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	public void setDetailButtonId() {
		
		dPrintBtn = (Button) dPopupView.findViewById(R.id.dPrintBtn);
		dCancelBtn = (Button) dPopupView.findViewById(R.id.dCancelBtn);
		dSnapshotBtn = (Button) dPopupView.findViewById(R.id.dSnapshotBtn);
	}
	
	public void setDetailButtonClick() {
		
		dPrintBtn.setOnTouchListener(mTouchListener);
		dCancelBtn.setOnTouchListener(mTouchListener);
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) dSnapshotBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setDetailButtonState(int btnId, boolean state) {
		
		dPopupView.findViewById(btnId).setEnabled(state);
	}
	
	public void setIButtonId() {
		
		checkBox1IBtn = (ImageButton) findViewById(R.id.checkBox1IBtn);
		checkBox2IBtn = (ImageButton) findViewById(R.id.checkBox2IBtn);
		checkBox3IBtn = (ImageButton) findViewById(R.id.checkBox3IBtn);
		checkBox4IBtn = (ImageButton) findViewById(R.id.checkBox4IBtn);
		checkBox5IBtn = (ImageButton) findViewById(R.id.checkBox5IBtn);
	}
	
	public void setIButton(int id, boolean isPressed) {
		
		if(isPressed) findViewById(id).setBackgroundResource(R.drawable.checkbox_s);
		else findViewById(id).setBackgroundResource(R.drawable.checkbox);
	}
	
	public void setIButtonClick() {
		
		checkBox1IBtn.setOnTouchListener(mImageTouchListener);
		checkBox2IBtn.setOnTouchListener(mImageTouchListener);
		checkBox3IBtn.setOnTouchListener(mImageTouchListener);
		checkBox4IBtn.setOnTouchListener(mImageTouchListener);
		checkBox5IBtn.setOnTouchListener(mImageTouchListener);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
			
				mRecordDataPresenter.unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mRecordDataPresenter.changeActivity(v.getId());
					break;
				
				case R.id.homeBtn	:
					mRecordDataPresenter.changeActivity(v.getId());
					break;
					
				case R.id.exportBtn	:
					mRecordDataPresenter.export();
					break;
				
				case R.id.preViewBtn	:
					mRecordDataPresenter.changePrePage();
					break;
					
				case R.id.detailViewBtn	:
					mRecordDataPresenter.displayDetailView();
					break;
				
				case R.id.nextViewBtn	:
					mRecordDataPresenter.changeNextPage();
					break;
					
				case R.id.snapshotBtn	:
					mRecordDataPresenter.changeActivity(v.getId());
					break;
					
				case R.id.dPrintBtn	:
					mRecordDataPresenter.print();
					break;
					
				case R.id.dCancelBtn	:
					mRecordDataPresenter.cancelDetailView();
					break;
					
				case R.id.dSnapshotBtn	:
					CaptureScreen mCaptureScreen = new CaptureScreen();
					byte[] bitmapBytes = mCaptureScreen.captureScreen(activity, dPopupView);
					mRecordDataPresenter.snapshotDetailView(bitmapBytes);
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	ImageButton.OnTouchListener mImageTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				
				switch(v.getId()) {
				
				case R.id.checkBox1IBtn	:
				case R.id.checkBox2IBtn	:
				case R.id.checkBox3IBtn	:
				case R.id.checkBox4IBtn	:
				case R.id.checkBox5IBtn	:
					mRecordDataPresenter.displayCheckBox(v.getId());
					break;
				}		
				
				break;
			}
			
			return false;
		}
	};
}
