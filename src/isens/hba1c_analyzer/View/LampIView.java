package isens.hba1c_analyzer.View;

import java.util.ArrayList;

public interface LampIView {

	void setImageId();
	void setImageBgColor(String color);
	void setTextId();
	void setText(ArrayList<String> txtList);
	void setTextState(int txtId, boolean state);
	void setButtonId();
	void setButtonClick();
	void setButtonBg(ArrayList<Integer> valList);
	void setButtonState(int btnId, boolean state);
}
