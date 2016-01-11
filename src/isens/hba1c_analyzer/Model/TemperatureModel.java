package isens.hba1c_analyzer.Model;

import java.text.DecimalFormat;

public class TemperatureModel {

	public String toStringTmp(float value) {

		DecimalFormat dfm = new DecimalFormat("0.0");
		String string;
		
		string = dfm.format((double) value);
		
		return string;
	}
}
