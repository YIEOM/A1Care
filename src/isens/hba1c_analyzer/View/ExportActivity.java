package isens.hba1c_analyzer.View;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.Presenter.ExportPresenter;

public class ExportActivity extends Activity implements ExportIView {
	
	private ExportPresenter mExportPresenter;
	
	private ImageView iconImage;
	
	private TextView exportText;
	
	protected void onCreate(Bundle saveInstanceState) {
		
		super.onCreate(saveInstanceState);
		setContentView(R.layout.export);
		
		mExportPresenter = new ExportPresenter(this, this, this, R.id.exportLayout);
		mExportPresenter.init();
	}
	
	public void setImageId() {
		
		iconImage = (ImageView) findViewById(R.id.iconImage);
	}
	
	public void setImage(final int id) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
				
		            	iconImage.setBackgroundResource(id);	            	
		           }
		        });
		    }
		}).start();
	}
	
	public void setTextId() {
		
		exportText = (TextView) findViewById(R.id.exportText);
	}
	
	public void setText() {
		
		exportText.setText(R.string.export);
	}
}
