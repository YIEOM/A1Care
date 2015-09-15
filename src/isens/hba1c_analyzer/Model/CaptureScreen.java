package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

public class CaptureScreen extends Activity {

	public byte[] captureScreen(Activity activity) {
    
		View view = activity.getWindow().getDecorView().getRootView();
		view.setDrawingCacheEnabled(true);
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray(); 

		return bytes;
    }
	
	public byte[] captureScreen(Activity activity, View view2) {
	    
		View view1 = activity.getWindow().getDecorView().getRootView();
		view1.setDrawingCacheEnabled(true);
        Bitmap bmp1 = Bitmap.createBitmap(view1.getDrawingCache());
        view1.setDrawingCacheEnabled(false);
        
        view2.setDrawingCacheEnabled(true);
        Bitmap bmp2 = Bitmap.createBitmap(view2.getDrawingCache());
        view2.setDrawingCacheEnabled(false);
        
        Canvas canvas = new Canvas(bmp1);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp1.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray(); 

		return bytes;
    }
}
