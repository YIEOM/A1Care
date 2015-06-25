package isens.hba1c_analyzer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.SoundModel;
import isens.hba1c_analyzer.View.FunctionalTestActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActionActivity extends Activity {
	
	public GpioPort mGpioPort;
	public SerialPort mSerialPort;
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;
	public ActivityChange mActivityChange;
	public SoundModel mSoundModel;
	
	private Activity activity;
	private Context context;
	private int layoutId;
	
	public Handler handler = new Handler();
	public Timer timer;
	
	public AnimationDrawable scanAni;
	public ImageView scanImage;
		
	public RelativeLayout actionLinear;
	
	public Button escBtn;
	
	public static boolean IsCorrectBarcode = false,
						  IsExpirationDate = false,
			  			  BarcodeCheckFlag = false, 
						  BarcodeQCCheckFlag = false;
	public static boolean IsEnablePopup = false,
						  ESCButtonFlag = false;
	
	public static byte CartridgeCheckFlag, 
					   DoorCheckFlag;
	
	public AudioManager audioManager;
	public SoundPool mPool;
	public int mWin;
	
	private int checkError = RunActivity.NORMAL_OPERATION;
	
	public int waitCnt = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.action);
		
		ActionInit();
	}
	
	public void setButtonId(Activity activity) {
		
		escBtn = (Button)activity.findViewById(R.id.escicon);
	}
	
	public void setButtonClick() {
		
		escBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state, Activity activity) {
		
		activity.findViewById(btnId).setEnabled(state);
	}
	
	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_UP	:
				unenabledAllBtn(activity); //0624
				
				switch(v.getId()) {
			
				case R.id.escicon		:
					ESC();
					break;
					
				default	:
					break;
				}
			
				break;
			}
			
			return false;
		}
	};
	
	public void enabledAllBtn(Activity activity) {

		setButtonState(R.id.escicon, true, activity);
	}
	
	public void unenabledAllBtn(Activity activity) {
		
		setButtonState(R.id.escicon, false, activity);
	}
	
	public void ActionInit() {
		
		activity = this;
		context = this;
		
		setButtonId(activity);
		setButtonClick();
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.actionlayout);
		
		BarcodeQCCheckFlag = false;
		
		startScan(this, this, R.id.actionlayout);
    }
	
	public void startScan(Activity activity, Context context, int layoutId) {
		
		this.activity = activity;
		this.context = context;
		this.layoutId = layoutId;
		
		if(HomeActivity.MEASURE_MODE == HomeActivity.A1C) {
			
			startBarcodeScan(activity);
		
		} else {
			
			if(!BarcodeQCCheckFlag)	startBarcodeQCScan(activity);
			else startBarcodeScan(activity);
		}
	}
	
	private void initScan() {
		
		BarcodeCheckFlag = false;
		IsCorrectBarcode = false;
		IsExpirationDate = false;
		SerialPort.BarcodeReadStart = false;
		ESCButtonFlag = false;
		waitCnt = 0;
	}
	
	private void startBarcodeScan(final Activity activity) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	actionLinear = (RelativeLayout) activity.findViewById(R.id.actionlayout);
		        		actionLinear.setBackgroundResource(R.drawable.ani_scan_bg);
		            }
		        });
		    }
		}).start();
		
		BarcodeQCCheckFlag = true;
		BarcodeScan mBarcodeScan = new BarcodeScan();
		mBarcodeScan.start();
	}
	
	private void startBarcodeQCScan(Activity activity) {
		
		actionLinear = (RelativeLayout) activity.findViewById(R.id.actionlayout);
		actionLinear.setBackgroundResource(R.drawable.ani_qc_scan_bg);
		BarcodeQCScan mBarcodeQCScan = new BarcodeQCScan();
    	mBarcodeQCScan.start();
	}
	
	public class BarcodeQCScan extends Thread {
		
		public void run () {
			
			initScan();
			
			/* Barcode scan action */
    		BarcodeAniStart(activity);
			
			SerialPort.BarcodeBufIndex = 0;
			
			Trigger();
			
			while(!BarcodeCheckFlag) {
				
				if((waitCnt++ == 5999) || ESCButtonFlag) break; 
				SerialPort.Sleep(100);
			}

			timer.cancel();
			
			if(waitCnt != 6000) {
			
				if(!ESCButtonFlag) { 
					
					if(IsCorrectBarcode) {
						
						startBarcodeScan(activity);
						
					} else {
					
						mErrorPopup = new ErrorPopup(activity, context, layoutId, null, 0);
						mErrorPopup.ErrorBtnDisplay(R.string.e313);
					}	
				}
				
			} else {
				
				checkError = R.string.e311;
				changeActivity(activity, context);
			}
		}
	}
	
	public class BarcodeScan extends Thread {
		
		public void run () {
			
			initScan();
			
			/* Barcode scan action */
    		BarcodeAniStart(activity);
			
			SerialPort.BarcodeBufIndex = 0;
			
			if(HomeActivity.MEASURE_MODE != HomeActivity.A1C) SerialPort.Sleep(2000);
			
			Trigger();
			
			while(!BarcodeCheckFlag) {
				
				if((waitCnt++ == 5999) || ESCButtonFlag) break; 
				SerialPort.Sleep(100);
			}

			timer.cancel();
			
			if(waitCnt != 6000) {
			
				if(!ESCButtonFlag) { 
					
					if(IsCorrectBarcode) {
						
						if(!IsExpirationDate) {
							
							mErrorPopup = new ErrorPopup(activity, context, layoutId, null, 0);
							mErrorPopup.ErrorBtnDisplay(R.string.e131);						
						
						} else {
							
							CartridgeInsert CartridgeInsertObj = new CartridgeInsert();
							CartridgeInsertObj.start();
						}
						
					} else {
					
						mErrorPopup = new ErrorPopup(activity, context, layoutId, null, 0);
						mErrorPopup.ErrorBtnDisplay(R.string.e313);
					}	
				}
				
			} else {
				
				checkError = R.string.e311;
				changeActivity(activity, context);
			}
		}
	}
		
	public class CartridgeInsert extends Thread {
		
		public void run () {

			/* Cartridge insertion action */
			CartridgeAniStart(activity);
				
			GpioPort.CartridgeActState = true;
			waitCnt = 0;
			
			while(ActionActivity.CartridgeCheckFlag != 1 || IsEnablePopup) { // to test
			
				if((waitCnt++ == 5999) || ESCButtonFlag) break;
				startWarningSound(waitCnt);
				SerialPort.Sleep(100);
			}
			
			if(waitCnt != 6000) {
				
				if(!ESCButtonFlag) {  // to test
				
					ActionActivity.DoorCheckFlag = 0;
					
					mSoundModel = new SoundModel(activity, context);
					mSoundModel.playSound(R.raw.insert_bgm);
					
					SerialPort.Sleep(100);
					
					CollectorCover CollectorCoverObj = new CollectorCover();
					CollectorCoverObj.start();
				}
				
			} else {
				
				checkError = R.string.e312;
				changeActivity(activity, context);
			}
		}
	}
	
	public class CollectorCover extends Thread {
		
		public void run() {
			
			/* Cover close action */
			CoverAniStart(activity);
			
			GpioPort.DoorActState = true;
			waitCnt = 0;
			
			while((ActionActivity.DoorCheckFlag != 1) || (ActionActivity.CartridgeCheckFlag != 1) || IsEnablePopup) {
				
				if((waitCnt++ == 599) || ESCButtonFlag) break;
				SerialPort.Sleep(100);
			}
			
			CoverAniStop(activity);
			
			if(waitCnt != 600) {
				
				if(!ESCButtonFlag) {
				
					WhichIntent(activity, context, TargetIntent.Run);
				}
				
			} else {
				
				checkError = R.string.e321;
				changeActivity(activity, context);
			}
		}
	}
	
	public void BarcodeAniStart(Activity activity) { // Barcode scan animation start
		
		scanImage = (ImageView)activity.findViewById(R.id.userAct1);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
				
		            	if(BarcodeQCCheckFlag) scanImage.setBackgroundResource(R.drawable.useract1);
		            	else scanImage.setBackgroundResource(R.drawable.useract2);
		            	
		            	scanAni = (AnimationDrawable)scanImage.getBackground();
		        		scanAni.start();
		            }
		        });
		    }
		}).start();
	}
	
	public void CartridgeAniStart(Activity activity) { // Cartridge insertion animation start
		
		actionLinear = (RelativeLayout)activity.findViewById(R.id.actionlayout);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
		  
						actionLinear.setBackgroundResource(R.drawable.ani_3steps_bg);
		        		scanImage.setBackgroundResource(0);
						scanAni.stop();
		            }
		        });
		    }
		}).start();
    }
	
	public void CoverAniStart(Activity activity) { // Cover close animation start

		scanImage = (ImageView)activity.findViewById(R.id.userAct4);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
		  
						scanImage.setBackgroundResource(R.drawable.useract4);
						scanAni = (AnimationDrawable)scanImage.getBackground();
						
						actionLinear.setBackgroundResource(R.drawable.ani_close_bg);
						
						scanAni.start();
					}
		        });
		    }
		}).start();
	}
	
	public void CoverAniStop(Activity activity) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
		  
						scanAni.stop();
					}
		        });
		    }
		}).start();
	}
	
	public void Trigger() {
		
		BarcodeScan();
		
		TimerTask triggerTimer = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if (cnt++ == 5) {
							
							cnt = 0;
							BarcodeScan();
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(triggerTimer, 0, 1000); // Timer period : 1sec
	}

	public void BarcodeScan() {
		
		mGpioPort = new GpioPort();
		mGpioPort.TriggerLow();
		SerialPort.Sleep(100);
		mGpioPort.TriggerHigh();
	}
	
	public void startWarningSound(int cnt) {
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL) {
		
			switch(cnt) {

			case 5899 :
			case 5909 :
			case 5919 :
			case 5929 :
			case 5939 :
			case 5949 :
			case 5959 :
			case 5969 :
			case 5979 :
			case 5989 :
			case 5999 :
				mSoundModel = new SoundModel(activity, context);
				mSoundModel.playSound(R.raw.beep);
				break;
			}
		
		} else if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
		
			switch(cnt) {
			
			case 199 :
			case 209 :
			case 219 :
			case 229 :
			case 239 :
			case 249 :
			case 259 :
			case 269 :
			case 279 :
			case 289 :
			case 299 :
				mSoundModel = new SoundModel(activity, context);
				mSoundModel.playSound(R.raw.beep);
				break;
			}
		}
	}
	
	public void ESC() {
		
		IsEnablePopup = true;
		
		mErrorPopup = new ErrorPopup(activity, context, R.id.actionlayout, null, 0); // to test
		mErrorPopup.OXBtnDisplay(R.string.esc);
	}

	private void changeActivity(Activity activity, Context context) {
		
		if(HomeActivity.MEASURE_MODE == HomeActivity.A1C) {
			
			WhichIntent(activity, context, TargetIntent.Home);
		
		} else {
			
			WhichIntent(activity, context, TargetIntent.FunctionalTest);
		}
	}
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		GpioPort.CartridgeActState = false;
		GpioPort.DoorActState = false;
		IsEnablePopup = false;
		
		mGpioPort = new GpioPort();
		mGpioPort.TriggerHigh();
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Run	:	
			nextIntent = new Intent(context, RunActivity.class);
			break;
						
		case Home	:	
			ESCButtonFlag = true;
			
			nextIntent = new Intent(context, HomeActivity.class);
			nextIntent.putExtra("System Check State", (int) checkError);
			break;
			
		case FunctionalTest	:
			ESCButtonFlag = true;
			
			nextIntent = new Intent(context, FunctionalTestActivity.class);
			nextIntent.putExtra("System Check State", (int) checkError);
			break;
				
		case Remove	:	
			ESCButtonFlag = true;
			
			nextIntent = new Intent(context, RemoveActivity.class);
			nextIntent.putExtra("System Check State", R.string.stop);
			nextIntent.putExtra("WhichIntent", (int) ResultActivity.COVER_ACTION_ESC);
			break;
			
		default		:	
			break;			
		}
		
		activity.startActivity(nextIntent);
		finish(activity);
	}
	
	public void finish(Activity activity) {
		
		super.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
