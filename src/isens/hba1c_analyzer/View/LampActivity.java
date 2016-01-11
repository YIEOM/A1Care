package isens.hba1c_analyzer.View;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.ErrorPopup;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.EngineerActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.drawable;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.string;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.Cart1stFilter2;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.Model.LampModel;
import isens.hba1c_analyzer.Model.MainTimer;
import isens.hba1c_analyzer.Presenter.LampPresenter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LampActivity extends Activity implements LampIView {
	
	private LampPresenter mLampPresenter;
	
	public TextView adcText,
					adc1Text,
					adc2Text,
					adc3Text,
					adc4Text,
					adc5Text;

	public ImageView stateFlag1,
					 stateFlag2;
	
	public Button backBtn,
				  runBtn,
				  cancelBtn,
				  darkBtn,
				  f535nmBtn,
				  f660nmBtn,
				  f750nmBtn;
	
	public SurfaceView mSurfaceView;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.lamp);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.graphBg);
		
		mLampPresenter = new LampPresenter(this, this, this, R.id.lampLayout, mSurfaceView);
		mLampPresenter.init();
	}
	
	public void setImageId() {
		
		stateFlag1 = (ImageView) findViewById(R.id.stateFlag1);
		stateFlag2 = (ImageView) findViewById(R.id.stateFlag2);
	}
	
	public void setImageBgColor(String color) {
		
		stateFlag1.setBackgroundColor(Color.parseColor(color));
		stateFlag2.setBackgroundColor(Color.parseColor(color));
	}
	
	public void setTextId() {
		
		adcText = (TextView) findViewById(R.id.adcText);
		adc1Text = (TextView) findViewById(R.id.adc1Text);
		adc2Text = (TextView) findViewById(R.id.adc2Text);
		adc3Text = (TextView) findViewById(R.id.adc3Text);
		adc4Text = (TextView) findViewById(R.id.adc4Text);
		adc5Text = (TextView) findViewById(R.id.adc5Text);
	}
	
	public void setText(ArrayList<String> txtList) {
		
		adcText.setText(txtList.get(0));
		adc1Text.setText(txtList.get(1));
		adc2Text.setText(txtList.get(2));
		adc3Text.setText(txtList.get(3));
		adc4Text.setText(txtList.get(4));
		adc5Text.setText(txtList.get(5));
	}

	public void setTextState(int txtId, boolean state) {
		
		findViewById(txtId).setEnabled(state);
	}

	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		runBtn = (Button)findViewById(R.id.runBtn);
		cancelBtn = (Button)findViewById(R.id.cancelBtn);
		darkBtn = (Button)findViewById(R.id.darkBtn);
		f535nmBtn = (Button)findViewById(R.id.f535nmBtn);
		f660nmBtn = (Button)findViewById(R.id.f660nmBtn);
		f750nmBtn = (Button)findViewById(R.id.f750nmBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		runBtn.setOnTouchListener(mTouchListener);
		cancelBtn.setOnTouchListener(mTouchListener);
		darkBtn.setOnTouchListener(mTouchListener);
		f535nmBtn.setOnTouchListener(mTouchListener);
		f660nmBtn.setOnTouchListener(mTouchListener);
		f750nmBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonBg(ArrayList<Integer> valList) {
		
		darkBtn.setBackgroundResource(valList.get(0));
		f535nmBtn.setBackgroundResource(valList.get(1));
		f660nmBtn.setBackgroundResource(valList.get(2));
		f750nmBtn.setBackgroundResource(valList.get(3));
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
			
				mLampPresenter.unenabledAllBtn();
						
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mLampPresenter.changeActivity();
					break;
					
				case R.id.runBtn	:
					mLampPresenter.startRun();
					break;
					
				case R.id.cancelBtn	:
					mLampPresenter.cancelRun();
					break;
					
				default	:
					mLampPresenter.enabledAllBtn();
					break;
				}
			
				break;
			
			case MotionEvent.ACTION_DOWN	:
			
				switch(v.getId()) {
				
				case R.id.darkBtn	:
					mLampPresenter.displayFilterBtn(AnalyzerState.FilterDark);
					break;
					
				case R.id.f535nmBtn	:
					mLampPresenter.displayFilterBtn(AnalyzerState.Filter535nm);
					break;
					
				case R.id.f660nmBtn	:
					mLampPresenter.displayFilterBtn(AnalyzerState.Filter660nm);
					break;
				
				case R.id.f750nmBtn	:
					mLampPresenter.displayFilterBtn(AnalyzerState.Filter750nm);
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
