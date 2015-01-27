package isens.hba1c_analyzer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnCreateContextMenuListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class ShutDownActivity extends Activity {

	public DatabaseHander mDatabaseHander;
	
	public ImageView shutDownIcon;
	public TextView shutDownText;
	
	public AniShutDown mAniShutDown;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.shutdown);
		
		shutDownIcon = (ImageView) findViewById(R.id.icon);
		shutDownText = (TextView) findViewById(R.id.shutDownText);
		
		initShutDown();
	}
	
	public void initShutDown() {
		
		mAniShutDown = new AniShutDown();
		mAniShutDown.start();
		
		shutDown();
	}
	
	public void shutDown() {
		
		TimerDisplay.ExternalDeviceBarcode = TimerDisplay.FILE_CLOSE;
		
		TimerDisplay.FiftymsPeriod.cancel();
		
		mDatabaseHander = new DatabaseHander(this);
		mDatabaseHander.UpdateGuestLogIn();
	}
	
	public class AniShutDown extends Thread {
		
		public void run() {
			
			Animation ani = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		
			shutDownText.setText(R.string.shuttingdown);
			shutDownIcon.setBackgroundResource(R.drawable.blank_bar);
			
			ani.setDuration(1000);
			ani.setRepeatCount(1);
			
			shutDownIcon.startAnimation(ani);
			
			SerialPort.Sleep(2000);
			
			new Thread(new Runnable() {
			    public void run() {    
			         runOnUiThread(new Runnable(){
			             public void run() {
			            	 
			     			shutDownText.setText(R.string.turnoff);
			             }
			         });
			     }
			 }).start();

		}
	}
}
