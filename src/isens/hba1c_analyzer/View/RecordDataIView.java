package isens.hba1c_analyzer.View;

import java.util.ArrayList;

public interface RecordDataIView {

	void setTextId();
	void setText(String txt);
	void setRecord1Text(ArrayList<String> txtList);
	void setRecord2Text(ArrayList<String> txtList);
	void setRecord3Text(ArrayList<String> txtList);
	void setRecord4Text(ArrayList<String> txtList);
	void setRecord5Text(ArrayList<String> txtList);
	void setPageText(String txt);
	void setPopupWindowId();
	void setPopupWindow();
	void dismissPopupWindow();
	void setDetailTextId();
	void setDetailText(ArrayList<String> txtList);
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
	void setDetailButtonId();
	void setDetailButtonClick();
	void setDetailButtonState(int btnId, boolean state);
	void setIButtonId();
	void setIButton(int id, boolean isPressed);
	void setIButtonClick();
}
