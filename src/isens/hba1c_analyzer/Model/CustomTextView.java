package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class CustomTextView extends TextView {

	private float letterSpacing = 0.0f;
    private CharSequence originalText = "";

	private boolean stroke = false;   
    private float strokeWidth = 0.0f;   
    private int strokeColor;
	
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {   
        
    	super(context, attrs, defStyle);   
           
        initView(context, attrs);   
    }   
  
    public CustomTextView(Context context, AttributeSet attrs) {   
        
    	super(context, attrs);   

        originalText = super.getText();
        
        applyLetterSpacing();
        
        this.invalidate();
   
        initView(context, attrs);   
    }   
  
    public CustomTextView(Context context) {
    	
        super(context);
    }   
       
    private void initView(Context context, AttributeSet attrs) {   
        
    	TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);   
        stroke = a.getBoolean(R.styleable.CustomTextView_textStroke, false);   
        strokeWidth = a.getFloat(R.styleable.CustomTextView_textStrokeWidth, 0.0f);
        strokeColor = a.getColor(R.styleable.CustomTextView_textStrokeColor, 0xffffffff);
    }   
  
    public float getLetterSpacing() {
    	
        return letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
    	
        this.letterSpacing = letterSpacing;
        
        applyLetterSpacing();
    }

    @Override
    public CharSequence getText() {
    	
        return originalText;
    }

    private void applyLetterSpacing() {
        
    	StringBuilder builder = new StringBuilder();
        
    	for(int i = 0; i < originalText.length(); i++) {
           
    		builder.append(originalText.charAt(i));
           
    		if(i+1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        
    	SpannableString finalText = new SpannableString(builder.toString());
        
    	if(builder.toString().length() > 1) {
            
    		for(int i = 1; i < builder.toString().length(); i+=2) {
                
    			finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        
    	super.setText(finalText, BufferType.SPANNABLE);
    }
    
    public void setText(CharSequence text, BufferType type) {
        
    	originalText = text;
        
    	applyLetterSpacing();
    }
    
    public void setStrokeEnabled(boolean state) {
    	
    	stroke = state;
    }
    
    public void setStrokeColor(int color) {
    	
    	strokeColor = color;
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {
    	
    	if(stroke) {
        	
    		ColorStateList states = getTextColors();
        	getPaint().setStyle(Style.STROKE);
            getPaint().setStrokeWidth(strokeWidth);
            setTextColor(strokeColor);
            super.onDraw(canvas);
               
            getPaint().setStyle(Style.FILL);
            setTextColor(states);
    	}
    	
    	super.onDraw(canvas);
    }
}
