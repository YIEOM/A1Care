package isens.hba1c_analyzer.View;

import java.util.ArrayList;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.Calibration.TargetMode;
import isens.hba1c_analyzer.Presenter.AboutPresenter;
import isens.hba1c_analyzer.Presenter.CalPresenter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CalActivity extends Activity implements CalIView {

	private CalPresenter mCalPresenter;
	private ActivityChange mActivityChange;
	
	private TextView absText[][] = new TextView[6][3],
			 		 titleText,
					 dSText,
					 cmbTmpText,
					 inTmpText,
					 rst1TtlText,
					 rst1Text,
					 rst2TtlText,
					 rst2Text,
					 rst3TtlText,
					 rst3Text;
					 
	private Button backBtn,
				   blankBtn,
				   quickBtn,
				   fullBtn,
				   scanBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.calibration);
		
		mActivityChange = new ActivityChange(this, this);
		mActivityChange.setIntent();
		TargetIntent target = mActivityChange.getTrgIntent("TargetIntent");
		
		mCalPresenter = new CalPresenter(this, this, this, R.id.calibLayout, target);
		mCalPresenter.init();
	}
	
	public void setTextId() {
		
		titleText = (TextView) findViewById(R.id.titleText);
		dSText = (TextView) findViewById(R.id.dSText);
		cmbTmpText = (TextView) findViewById(R.id.cmbTmpText);
		inTmpText = (TextView) findViewById(R.id.inTmpText);
		absText[0][0] = (TextView) findViewById(R.id.r1c1Text);
		absText[0][1] = (TextView) findViewById(R.id.r1c2Text);
		absText[0][2] = (TextView) findViewById(R.id.r1c3Text);
		absText[1][0] = (TextView) findViewById(R.id.r2c1Text);
		absText[1][1] = (TextView) findViewById(R.id.r2c2Text);
		absText[1][2] = (TextView) findViewById(R.id.r2c3Text);
		absText[2][0] = (TextView) findViewById(R.id.r3c1Text);
		absText[2][1] = (TextView) findViewById(R.id.r3c2Text);
		absText[2][2] = (TextView) findViewById(R.id.r3c3Text);
		absText[3][0] = (TextView) findViewById(R.id.r4c1Text);
		absText[3][1] = (TextView) findViewById(R.id.r4c2Text);
		absText[3][2] = (TextView) findViewById(R.id.r4c3Text);
		absText[4][0] = (TextView) findViewById(R.id.r5c1Text);
		absText[4][1] = (TextView) findViewById(R.id.r5c2Text);
		absText[4][2] = (TextView) findViewById(R.id.r5c3Text);
		absText[5][0] = (TextView) findViewById(R.id.r6c1Text);
		absText[5][1] = (TextView) findViewById(R.id.r6c2Text);
		absText[5][2] = (TextView) findViewById(R.id.r6c3Text);
		rst1TtlText = (TextView) findViewById(R.id.rst1TtlText);
		rst1Text = (TextView) findViewById(R.id.rst1Text);
		rst2TtlText = (TextView) findViewById(R.id.rst2TtlText);
		rst2Text = (TextView) findViewById(R.id.rst2Text);
		rst3TtlText = (TextView) findViewById(R.id.rst3TtlText);
		rst3Text = (TextView) findViewById(R.id.rst3Text);
	}
	
	public void setText(ArrayList<String> txtList) {
		
		titleText.setPaintFlags(titleText.getPaintFlags()|Paint.FAKE_BOLD_TEXT_FLAG);
		titleText.setText(txtList.get(0));
		rst1TtlText.setText(txtList.get(1));
		rst2TtlText.setText(txtList.get(2));
		rst3TtlText.setText(txtList.get(3));
	}
	
	public void setDSText(String txt, String color) {
		
		dSText.setTextColor(Color.parseColor(color));
		dSText.setText(txt);
	}
	
	public void setTmpText(String txt1, String txt2) {
		
		cmbTmpText.setText(txt1);
		inTmpText.setText(txt2);
	}
	
	public void setR1AbsText(ArrayList<String> txtList) {
		
		absText[0][0].setText(txtList.get(0));
		absText[0][1].setText(txtList.get(1));
		absText[0][2].setText(txtList.get(2));
	}
	
	public void setR2AbsText(ArrayList<String> txtList) {
		
		absText[1][0].setText(txtList.get(0));
		absText[1][1].setText(txtList.get(1));
		absText[1][2].setText(txtList.get(2));
	}
	
	public void setR3AbsText(ArrayList<String> txtList) {
		
		absText[2][0].setText(txtList.get(0));
		absText[2][1].setText(txtList.get(1));
		absText[2][2].setText(txtList.get(2));
	}
	
	public void setR4AbsText(ArrayList<String> txtList) {
		
		absText[3][0].setText(txtList.get(0));
		absText[3][1].setText(txtList.get(1));
		absText[3][2].setText(txtList.get(2));
	}
	
	public void setR5AbsText(ArrayList<String> txtList) {
		
		absText[4][0].setText(txtList.get(0));
		absText[4][1].setText(txtList.get(1));
		absText[4][2].setText(txtList.get(2));
	}
	
	public void setR6AbsText(ArrayList<String> txtList) {
		
		absText[5][0].setText(txtList.get(0));
		absText[5][1].setText(txtList.get(1));
		absText[5][2].setText(txtList.get(2));
	}
	
	public void setRstText(ArrayList<String> txtList) {
		
		rst1Text.setText(txtList.get(0));
		rst2Text.setText(txtList.get(1));
		rst3Text.setText(txtList.get(2));
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setBackgroundResource(R.drawable.back_selector);
		blankBtn = (Button)findViewById(R.id.blankBtn);
		quickBtn = (Button)findViewById(R.id.quickBtn);
		fullBtn = (Button)findViewById(R.id.fullBtn);
		scanBtn = (Button)findViewById(R.id.scanBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		blankBtn.setOnTouchListener(mTouchListener);
		quickBtn.setOnTouchListener(mTouchListener);
		fullBtn.setOnTouchListener(mTouchListener);
		scanBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
			
				mCalPresenter.unenabledAllBtn();
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mCalPresenter.changeActivity();
					break;
				
				case R.id.blankBtn	:
					mCalPresenter.startBlank((byte) 0x00);
					break;
				
				case R.id.quickBtn	:
					mCalPresenter.startQuick((byte) 0x00);
					break;
					
				case R.id.fullBtn	:
					mCalPresenter.startFull((byte) 0x00, (byte) 0x00);
					break;
				
				case R.id.scanBtn	:
					mCalPresenter.scanBarcode();
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
