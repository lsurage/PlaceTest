package bangali.baba.placetest.autocomple.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import bangali.baba.placetest.R;

public class GoogleAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private Drawable mCloseIcon;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private AutoCompleteLocationListener mAutoCompleteLocationListener;

    public interface AutoCompleteLocationListener {
        void onItemSelected(Place selectedPlace);
    }

    public GoogleAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaxLines(1);
        mCloseIcon = context.getResources().getDrawable(R.drawable.ic_dialog_close_light);
        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Places.GEO_DATA_API)
                .build();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mGoogleApiClient.connect();
        this.addTextChangedListener(mAutoCompleteTextWatcher);
        this.setOnTouchListener(mOnTouchListener);
        this.setOnItemClickListener(mAutocompleteClickListener);
        this.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter = new AutoCompleteAdapter(getContext(), mGoogleApiClient, null, null);
        this.setAdapter(mAutoCompleteAdapter);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    GoogleAutoCompleteTextView.this.getText().toString().equals("") ? null : mCloseIcon, null);
        }
    }

    public void setAutoCompleteTextListener(
            AutoCompleteLocationListener autoCompleteLocationListener) {
        mAutoCompleteLocationListener = autoCompleteLocationListener;
    }

    private TextWatcher mAutoCompleteTextWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            GoogleAutoCompleteTextView.this.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    GoogleAutoCompleteTextView.this.getText().toString().equals("") ? null : mCloseIcon, null);
        }

        @Override public void afterTextChanged(Editable editable) {
        }
    };

    private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getX()
                    > GoogleAutoCompleteTextView.this.getWidth()
                    - GoogleAutoCompleteTextView.this.getPaddingRight()
                    - mCloseIcon.getIntrinsicWidth()) {
                GoogleAutoCompleteTextView.this.setText("");
                GoogleAutoCompleteTextView.this.setCompoundDrawables(null, null, null, null);
            }
            return false;
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener =
            new AdapterView.OnItemClickListener() {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final AutocompletePrediction item = mAutoCompleteAdapter.getItem(position);
                    if (item != null) {
                        final String placeId = item.getPlaceId();
                        PendingResult<PlaceBuffer> placeResult =
                                Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                    }
                }
            };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback =
            new ResultCallback<PlaceBuffer>() {
                @Override public void onResult(@NonNull PlaceBuffer places) {
                    if (!places.getStatus().isSuccess()) {
                        places.release();
                        return;
                    }
                    final Place place = places.get(0);
                    if (mAutoCompleteLocationListener != null) {
                        mAutoCompleteLocationListener.onItemSelected(place);
                    }
                    places.release();
                }
            };
}