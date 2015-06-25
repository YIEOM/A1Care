package isens.hba1c_analyzer.View;

public interface FunctionalTestIView {

	void setImageId();
	void setImage();
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	int getIntentData();
}
