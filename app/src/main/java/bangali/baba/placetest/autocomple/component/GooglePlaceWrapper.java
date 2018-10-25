package bangali.baba.placetest.autocomple.component;

import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GooglePlaceWrapper {

    public static ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint,
                                                                    GoogleApiClient googleApiClient,
                                                                    LatLngBounds latLngBounds,
                                                                    AutocompleteFilter autocompleteFilter) {
        if (googleApiClient.isConnected()) {
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi.getAutocompletePredictions(googleApiClient, constraint.toString(),
                            latLngBounds, autocompleteFilter);

            AutocompletePredictionBuffer autocompletePredictions = results.await(60, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                autocompletePredictions.release();
                return null;
            }
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }
        return null;
    }
}
