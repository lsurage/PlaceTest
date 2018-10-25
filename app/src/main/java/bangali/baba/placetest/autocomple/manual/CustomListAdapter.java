package bangali.baba.placetest.autocomple.manual;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import bangali.baba.placetest.R;

public class CustomListAdapter extends ArrayAdapter {

    private List<PlaceItem> mPlaceList;
    private Context mContext;
    private int itemLayout;

    public CustomListAdapter(Context context, int resource, List<PlaceItem> placeItemList) {
        super(context, resource, placeItemList);
        mPlaceList = placeItemList;
        mContext = context;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return mPlaceList.size();
    }

    @Override
    public PlaceItem getItem(int position) {
        return mPlaceList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(itemLayout, parent, false);
        }

        TextView primaryText = (TextView) view.findViewById(R.id.primaryText);
        primaryText.setText(getItem(position).getPrimary());

        TextView secondaryText = (TextView) view.findViewById(R.id.secondaryText);
        secondaryText.setText(getItem(position).getSecondary());
//
//        TextView primaryText = (TextView) view.findViewById(R.id.textView3);
//        primaryText.setText(getItem(position).getPrimary()+ getItem(position).getSecondary() + getItem(position).getID());
        return view;
    }


}