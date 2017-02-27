package fyp.inrestaurant.RecyclerViewAdapter;

import android.content.Context;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;
import fyp.inrestaurant.R;

/**
 * Created by HFE on 07-Oct-16.
 */
public class ProfileInfoAdapter extends RecyclerView.Adapter<ProfileInfoAdapter.ProfileInfo_ViewHolder>
{
    private Context context;
    ArrayList<ArrayList<String>> voucherInfo;
    int userID;
    //Java Array starts at 0
    int selectedItemID = -1;

    public Context getContext()
    {return context;}

    public ProfileInfoAdapter(Context mContext, ArrayList<ArrayList<String>> voucher, int id)
    {
        this.context = mContext;
        voucherInfo = voucher;
        userID = id;
    }

    @Override
    public ProfileInfo_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemVw = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pseudotable, parent, false);
        return new ProfileInfo_ViewHolder(itemVw);
    }

    @Override
    public void onBindViewHolder(ProfileInfo_ViewHolder holder, int position)
    {
        ArrayList<String> voucher = voucherInfo.get(position);
        holder.vouchText.setText(voucher.get(0));
        holder.vouchValue.setText(voucher.get(1));
        holder.vouchExpr.setText(voucher.get(2));
    }


    @Override
    public int getItemCount()
    {
        return voucherInfo.size();
    }

    public class ProfileInfo_ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView vouchText, vouchValue, vouchExpr;

        public ProfileInfo_ViewHolder(final View itemView)
        {
            super(itemView);

            vouchText = (TextView)itemView.findViewById(R.id.redeemString);
            vouchValue = (TextView)itemView.findViewById(R.id.valueRedeem);
            vouchExpr = (TextView)itemView.findViewById(R.id.expirDate);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    selectedItemID = getAdapterPosition();
                    showDialog(voucherInfo.get(selectedItemID).get(0));
                }
            });
        }
    }

    private void showDialog(final String vouchcode)
    {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int w)
            {
                switch (w)
                {
                    case AlertDialog.BUTTON_POSITIVE:
                        new NetworkDatabaseTask( (NetworkDatabaseTask.AsyncResponse) context , "use"+vouchcode).execute();
                        new NetworkDatabaseTask( (NetworkDatabaseTask.AsyncResponse) context , "view"+userID).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        break;
                    case AlertDialog.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }

            }
        };

        dlg.setMessage("Do you wish to use this coupon?");
        dlg.setPositiveButton("Yes", ocl);
        dlg.setNegativeButton("No", ocl);
        dlg.show();
    }
}
