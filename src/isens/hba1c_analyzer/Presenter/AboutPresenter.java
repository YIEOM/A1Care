package isens.hba1c_analyzer.Presenter;

import android.app.Activity;
import android.content.Context;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.AboutModel;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.View.AboutIView;

public class AboutPresenter {
	
	private AboutIView mAboutIView;
	private AboutModel mAboutModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public AboutPresenter(AboutIView view, Activity activity, Context context, int layout) {
		
		mAboutIView = view;
		mAboutModel = new AboutModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mAboutIView.setImageId();
		mAboutIView.setImage();
		mAboutIView.setButtonId();
		mAboutIView.setTextId();
		mAboutIView.setEditTextId();
		
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);

		SerialPort.Sleep(500);
	
		mAboutIView.setButtonClick();
	}
	
	public void display() {
		
		mAboutIView.setText(AboutModel.SWVersion, AboutModel.FWVersion, AboutModel.OSVersion);
		mAboutIView.setEditText(AboutModel.HWSN);						
	}
	
	public void enabledAllBtn() {

		mAboutIView.setButtonState(R.id.backBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mAboutIView.setButtonState(R.id.backBtn, false);
	}
	
	public void changeActivity() {
		
		String version;
		
		version = mAboutIView.getHWVersion();
		mAboutModel.setHWVersion(version);
		
		mActivityChange.whichIntent(TargetIntent.Engineer);
		mActivityChange.finish();
	}
}
