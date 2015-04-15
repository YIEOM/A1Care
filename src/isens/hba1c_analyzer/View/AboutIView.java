package isens.hba1c_analyzer.View;

public interface AboutIView {

	void setImageId();
	void setImage();
	void setTextId();
	void setText(String swVersion, String fwVersion, String osVersion);
	void setEditTextId();
	void setEditText(String text);
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	String getHWVersion();
}
