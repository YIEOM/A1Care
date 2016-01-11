package isens.hba1c_analyzer.View;

import java.util.ArrayList;

public interface CalIView {

	void setTextId();
	void setText(ArrayList<String> txtList);
	void setDSText(String txt, String color);
	void setTmpText(String txt1, String txt2);
	void setR1AbsText(ArrayList<String> txtList);
	void setR2AbsText(ArrayList<String> txtList);
	void setR3AbsText(ArrayList<String> txtList);
	void setR4AbsText(ArrayList<String> txtList);
	void setR5AbsText(ArrayList<String> txtList);
	void setR6AbsText(ArrayList<String> txtList);
	void setRstText(ArrayList<String> txtList);
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
}
