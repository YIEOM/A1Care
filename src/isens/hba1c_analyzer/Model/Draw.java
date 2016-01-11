package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.SerialPort;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Draw extends SurfaceView implements SurfaceHolder.Callback {

	SurfaceHolder mSurfaceHolder;
	
	public Draw(Context context, SurfaceView mSurfaceView) {
		
		super(context); 
		
		mSurfaceHolder = mSurfaceView.getHolder();
		
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	public void drawGraph(float[] aDCVal, int idx, int[] cdn) {

		DrawGraph mDrawGraph = new DrawGraph(mSurfaceHolder, aDCVal, idx, cdn);
		mDrawGraph.start();
	}
	
	private class DrawGraph extends Thread {
		
		SurfaceHolder mSurfaceHolder;
		float[] aDCVal = new float[LampModel.LAMP_ADC_BUF_SIZE];
		int[] cdn = new int[6];
		int idx;
		
		public DrawGraph(SurfaceHolder mSurfaceHolder, float[] aDCVal, int idx, int[] cdn) {
			
			this.mSurfaceHolder = mSurfaceHolder;
			this.aDCVal = aDCVal;
			this.idx = idx;
			this.cdn = cdn;
		}
		
		public void run() {
			
			Canvas canvas = null;
			Paint pnt = new Paint();
			
			int preIdx,
				nextIdx;
			
			pnt.setColor(Color.WHITE);
			pnt.setStrokeWidth(1);
			
			try {
				
				canvas = mSurfaceHolder.lockCanvas(null);
				
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
				
				float range = (float) 250/(cdn[5]*12);
				
				for(int i = 1; i < LampModel.LAMP_ADC_BUF_SIZE; i++) {
					
					preIdx = idx+i+1;
					if(preIdx > LampModel.LAMP_ADC_BUF_SIZE-1) preIdx = preIdx - LampModel.LAMP_ADC_BUF_SIZE;
					
					nextIdx = preIdx-1;
					if(preIdx == 0) nextIdx = nextIdx + LampModel.LAMP_ADC_BUF_SIZE;
					
					canvas.drawLine(2*i, (float) (250-((aDCVal[nextIdx]-cdn[4])*range)), 2*(i+1), (float) (250-((aDCVal[preIdx]-cdn[4])*range)), pnt);
				}
				
			} finally {
				
				mSurfaceHolder.unlockCanvasAndPost(canvas);
			}	
		}
	}
}
