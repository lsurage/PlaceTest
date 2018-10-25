package bangali.baba.placetest.autocomple.manual;

public class PlaceItem {
    private String mPrimary;
    private String mSecondary;
    private String mID;

    public PlaceItem(String primary, String secondary, String id) {
        mPrimary = primary;
        mSecondary = secondary;
        mID = id;
    }

    public String getPrimary() {
        return mPrimary;
    }

    public String getSecondary() {
        return mSecondary;
    }

    public String getID() {
        return mID;
    }
    

}