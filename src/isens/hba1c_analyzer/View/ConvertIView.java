package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.R;
import android.widget.ImageView;

public interface ConvertIView {

	void setImageId();
	void setImage();
	void setTextId();
	void setText(int unit);
	void setButtonId();
	void setButtonClick();
	void setButtonState(int btnId, boolean state);
}
