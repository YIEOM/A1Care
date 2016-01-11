package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.EngineerActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.MainTimer;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;

public interface TemperatureIView {

	void setTextId();
	void setText(String txt1, String txt2);
	void setEditTextId();
	void setEditText(String txt);
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	String getChamTmp();
}