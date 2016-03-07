package isens.hba1c_analyzer.View;

public interface TemperatureIView {

	void setEditTextId();
	void setEditText(String text);
	void setTextId();
	void setText(String chamTmp, String ambTmp);
	void setButtonId();
	void setButtonBg(int btnId, int resId);
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	String getChambTmp();
}
