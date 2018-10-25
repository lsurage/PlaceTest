package bangali.baba.placetest.autocomple.manual;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import bangali.baba.placetest.R;

public class CustomAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private Drawable mCloseIcon;

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaxLines(1);
        mCloseIcon = context.getResources().getDrawable(R.drawable.ic_dialog_close_light);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.addTextChangedListener(mAutoCompleteTextWatcher);
        this.setOnTouchListener(mOnTouchListener);
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    CustomAutoCompleteTextView.this.getText().toString().equals("") ? null : mCloseIcon, null);
        }
    }


    private TextWatcher mAutoCompleteTextWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            CustomAutoCompleteTextView.this.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    CustomAutoCompleteTextView.this.getText().toString().equals("") ? null : mCloseIcon, null);

        }

        @Override public void afterTextChanged(Editable editable) {
        }
    };

    private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getX()
                    > CustomAutoCompleteTextView.this.getWidth()
                    - CustomAutoCompleteTextView.this.getPaddingRight()
                    - mCloseIcon.getIntrinsicWidth()) {
                CustomAutoCompleteTextView.this.setText("");
                CustomAutoCompleteTextView.this.setCompoundDrawables(null, null, null, null);
            }
            return false;
        }
    };


}