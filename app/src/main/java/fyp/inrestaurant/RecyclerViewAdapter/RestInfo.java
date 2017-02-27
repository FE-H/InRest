package fyp.inrestaurant.RecyclerViewAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.util.Collections;
import java.util.Comparator;

import fyp.inrestaurant.R;

/**
 * Created by HFE on 01-Oct-16.
 */

//For adapter, no direct relationship with restaurant info
public class RestInfo implements Parcelable, Comparator<RestInfo>
{
    private Context context;

    //Displayed in CardView (and RestaurantInfo)
    protected Bitmap imgVw;
    protected String restName;
    protected String restLot;
    protected int restVacant;

    //Displayed in RestaurantInfo
    protected String restType;
    protected String restNo;
    protected String restEmail;

    //Displayed for Admin
    protected String restOwn;
    protected int restID;
    protected String restURL;

    //Status
    private boolean objIS_EMPTY = true;

    public RestInfo(int id, String name, String type, String lot, int vacant, String own, String no, String email, String url)
    {
        init(id, name, type, lot, vacant, own, email, no, url, null);
    }

    public RestInfo(Context ctxt, int id, String name, String type, String lot, int vacant, String own, String no, String email, String url, Bitmap img) {
        context = ctxt;
        init(id, name, type, lot, vacant, own, email, no, url, img);
    }

    public static Comparator<RestInfo> ASC_VACANCY_COMPARATOR = new Comparator<RestInfo>()
    {
        @Override
        public int compare(RestInfo restInfo, RestInfo t1)
        {
            return restInfo.getResVacant()- t1.getResVacant();
        }
    };

    public static Comparator<RestInfo> ASC_NAME_COMPARATOR = new Comparator<RestInfo>()
    {
        @Override
        public int compare(RestInfo restInfo, RestInfo t1)
        {
            return restInfo.getRestName().compareTo(t1.getRestName());
        }
    };

    public static Comparator<RestInfo> DESC_VACANCY_COMPARATOR = Collections.reverseOrder(ASC_VACANCY_COMPARATOR);
    public static Comparator<RestInfo> DESC_NAME_COMPARATOR = Collections.reverseOrder(ASC_NAME_COMPARATOR);

    public boolean getEMPTY_Status(){
        return objIS_EMPTY;
    }

    private boolean isEmpty(){
        return restURL.equals("") && restName.equals("") && restLot.equals("") && restVacant == 0 && restType.equals("") && restNo.equals("") && restEmail.equals("") && restOwn.equals("");
    }

    private void init(int id, String name, String type, String lot, int vacant, String own, String email, String no, String url, Bitmap img)
    {
        restID = id;
        restName = name;
        restLot = lot;
        restVacant = vacant;
        restType = type;
        restNo = no;
        restEmail = email;
        restOwn = own;
        restURL = url;

        if(img != null)
            imgVw = img;
        else
            imgVw = BitmapFactory.decodeResource(context.getResources(), R.drawable.sample);

        objIS_EMPTY = isEmpty();


    }

    public String getRestName(){
        return restName;
    }

    public String getRestLot(){
        return restLot;
    }

    public int getResVacant(){
        return restVacant;
    }

    public String getRestType(){
        return restType;
    }

    public String getRestNo(){
        return restNo;
    }

    public String getRestEmail(){
        return restEmail;
    }

    public String getRestOwn(){
        return restOwn;
    }

    public int getRestID(){
        return restID;
    }

    public Bitmap getLogo(){return imgVw;}

    public void setLogo(Bitmap input){imgVw = input;}

    public String getRestURL(){return restURL;}

    public boolean checkFields(RestInfo arg){
        return arg.getRestName().equals(this.getRestName()) && arg.getRestEmail().equals(this.getRestEmail())
                && arg.getRestLot().equals(this.getRestLot())
                && arg.getRestNo().equals(this.getRestNo()) && arg.getRestType().equals(this.getRestType())
                && arg.getRestOwn().equals(this.getRestOwn()) && arg.getResVacant() == this.getResVacant()
                && arg.getRestURL().equals(this.getRestURL());
    }


    protected RestInfo(Parcel in) {
        //imgVw = (Bitmap) in.readValue(ImageView.class.getClassLoader());
        restName = in.readString();
        restLot = in.readString();
        restVacant = in.readInt();
        restType = in.readString();
        restNo = in.readString();
        restEmail = in.readString();
        restOwn = in.readString();
        restID = in.readInt();
        objIS_EMPTY = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeValue(imgVw);
        dest.writeString(restName);
        dest.writeString(restLot);
        dest.writeInt(restVacant);
        dest.writeString(restType);
        dest.writeString(restNo);
        dest.writeString(restEmail);
        dest.writeString(restOwn);
        dest.writeInt(restID);
        dest.writeByte((byte) (objIS_EMPTY ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RestInfo> CREATOR = new Parcelable.Creator<RestInfo>() {
        @Override
        public RestInfo createFromParcel(Parcel in) {
            return new RestInfo(in);
        }

        @Override
        public RestInfo[] newArray(int size) {
            return new RestInfo[size];
        }
    };

    @Override
    public int compare(RestInfo restInfo, RestInfo t1)
    {
        return 0;
    }
}
