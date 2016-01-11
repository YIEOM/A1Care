package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.SerialPort.CtrTarget;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class LampModel {
	
	private Hardware mHardware;
	
	private Context context;
	
	public final static int LAMP_ADC_BUF_SIZE = 200;
	
	private float[] aDCValArry = new float[LAMP_ADC_BUF_SIZE];
	
	private int lastIdx = 0;
	private AnalyzerState whichFilter;
	
	public LampModel(Context context) {
		
		mHardware = new Hardware();
		mHardware.initBlankValue();
		
		this.context = context;
	}
	
	public AnalyzerState setFMotor(AnalyzerState state, AnalyzerState whichState) {
		
		if(state == AnalyzerState.NormalOperation) {
		
			for(int i = 0; i < 6; i++) {
			
				switch(whichState) {
				
				case Filter750nm	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_SKIP, CtrTarget.NormalSet, AnalyzerState.Filter660nm);
					break;
					
				case Filter660nm	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_SKIP, CtrTarget.NormalSet, AnalyzerState.Filter535nm);
					break;
					
				case Filter535nm	:
					whichState = mHardware.getAMotorState(Hardware.FILTER_SKIP, CtrTarget.NormalSet, AnalyzerState.NormalOperation);
					break;
				
				default	:
					break;						
				}
			}
			
		} else return state;
		
		return whichState;
	}
	
	public float[] measurePhoto() {
		
		mHardware.getPhotoValue(AnalyzerState.NormalOperation);
		writeADCArry(mHardware.getPhotoADC());
		
		return aDCValArry;
	}
	
	public ArrayList<String> getInitforText() {
		
		ArrayList<String> txtList = new ArrayList<String>();
		
		for(int i = 0; i < 6; i++) {
			
			txtList.add(i, "");
		}
		
		return txtList;
	}
	
	public ArrayList<String> toStringADC(float value, int[] valArry) {
		
		DecimalFormat dfm = new DecimalFormat("0.0");
		ArrayList<String> stringList = new ArrayList<String>();
		
		stringList.add(0, dfm.format((double) value));
		stringList.add(1, Integer.toString(valArry[0]));
		stringList.add(2, Integer.toString(valArry[1]));
		stringList.add(3, Integer.toString(valArry[2]));
		stringList.add(4, Integer.toString(valArry[3]));
		stringList.add(5, Integer.toString(valArry[4]));
		
		return stringList;
	}
	
	public ArrayList<Integer> getFilterBtn(AnalyzerState filter) {
		
		ArrayList<Integer> imgList = new ArrayList<Integer>();
		
		whichFilter = filter;
		
		switch(filter) {
		
		case FilterDark	:
			imgList.add(0, R.drawable.btn_s);
			imgList.add(1, R.drawable.btn);
			imgList.add(2, R.drawable.btn);
			imgList.add(3, R.drawable.btn);
			break;
			
		case Filter535nm	:
			imgList.add(0, R.drawable.btn);
			imgList.add(1, R.drawable.btn_s);
			imgList.add(2, R.drawable.btn);
			imgList.add(3, R.drawable.btn);
			break;
			
		case Filter660nm	:
			imgList.add(0, R.drawable.btn);
			imgList.add(1, R.drawable.btn);
			imgList.add(2, R.drawable.btn_s);
			imgList.add(3, R.drawable.btn);
			break;
			
		case Filter750nm	:
			imgList.add(0, R.drawable.btn);
			imgList.add(1, R.drawable.btn);
			imgList.add(2, R.drawable.btn);
			imgList.add(3, R.drawable.btn_s);
			break;
			
		default	:
			break;
		}
		
		return imgList;
	}
	
	public AnalyzerState getTrgFilter() {
		
		return whichFilter;
	}
	
	public int getIdx() {
		
		return lastIdx;
	}
	
	public void writeADCArry(float value) {
			
		aDCValArry[lastIdx++] = value;
		
		if(lastIdx == LAMP_ADC_BUF_SIZE) lastIdx = 0;
	}
	
	public void initADCArry() {
		
		for(int i = 0; i < LAMP_ADC_BUF_SIZE; i++) {
			
			aDCValArry[i] = 0;
		}
	}
	
	public int[] getADCMaxMin(float value[]) {
		
		float currVal;
		int min,
			max;
		int[] maxMin = new int[2];
		
		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;
		
		for(int i = 0; i < value.length; i++) {
			
			currVal = value[i];
			
			if(currVal > max) max = (int) currVal;
			if(currVal > 0 && currVal < min) min = (int) currVal;
		}
		
		maxMin[0] = max;
		maxMin[1] = min;
		
		return maxMin;
	}
	
	public int[] getCoordinate(int maxMin[]) {
		
		int yCdn[] = new int[6];
		int diff;
		
		diff = (maxMin[0] - maxMin[1])/8;
		
		if((maxMin[1] - 2*diff) < 0) {
			
			diff = maxMin[0]/10;		
		}

		yCdn[0] = maxMin[0] + 2*diff;
		yCdn[1] = maxMin[0] - diff;
		yCdn[2] = maxMin[0] - 4*diff;
		yCdn[3] = maxMin[0] - 7*diff;
		yCdn[4] = maxMin[0] - 10*diff;
		yCdn[5] = diff;
		
		return yCdn;
	}
}
