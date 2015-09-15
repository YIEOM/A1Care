package isens.hba1c_analyzer.View;

public interface DisplayIView {

	void setImageId();
	void setImage();
	void setTextId();
	void setText();
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	void setBarGaugeImage(int drawable);
}
