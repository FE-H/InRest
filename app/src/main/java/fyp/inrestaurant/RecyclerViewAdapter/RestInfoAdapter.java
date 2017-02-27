package fyp.inrestaurant.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import fyp.inrestaurant.NetworkTasks.ImageDownloadTask;
import fyp.inrestaurant.R;
import fyp.inrestaurant.RestaurantInfo;

/**
 * Created by HFE on 07-Oct-16.
 */
public class RestInfoAdapter extends RecyclerView.Adapter<RestInfoAdapter.RestInfo_ViewHolder> implements Filterable
{
    public Filter filter;

    private Context context;
    private ArrayList<RestInfo> rInf_LIST;
    private ArrayList<RestInfo> filteredList;
    //Java Array starts at 0
    int selectedItemID = -1;

    public Context getContext()
    {return context;}

    public RestInfoAdapter(Context mContext, ArrayList<RestInfo> rInf)
    {
        this.context = mContext;
        rInf_LIST = rInf;
        filteredList = rInf;
        filter = new rInf_LIST_Filter();
    }

    @Override
    public RestInfo_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemVw = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new RestInfo_ViewHolder(itemVw);
    }

    //Binds 1 RestInfo to the UI elements
    @Override
    public void onBindViewHolder(final RestInfo_ViewHolder holder, int position)
    {
        RestInfo rInf = filteredList.get(position);
        holder.rName.setText(rInf.getRestName());
        holder.rLot.setText(rInf.getRestLot());
        holder.rVacancy_PROGBAR.setProgress(rInf.getResVacant()*10);
        holder.logo.setImageBitmap(rInf.getLogo());
        holder.progBarVal.setText(Integer.toString(rInf.getResVacant()));
    }

    @Override
    public int getItemCount()
    {
        return rInf_LIST.size();
    }

    @Override
    public Filter getFilter()
    {
        return filter;
    }

    private class rInf_LIST_Filter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            FilterResults results = new FilterResults();
            ArrayList<RestInfo> temp = new ArrayList<>();

            if(charSequence.length() == 0)
                temp.addAll(rInf_LIST);
            else
            {
                String filtrate = charSequence.toString().toLowerCase().trim();

                for(int count = 0; count < rInf_LIST.size(); count++)
                {
                    if(rInf_LIST.get(count).getRestName().toLowerCase().contains(filtrate))
                        temp.add(rInf_LIST.get(count));
                }
            }

            results.values = temp;
            results.count = temp.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            filteredList.clear();
            filteredList.addAll((ArrayList<RestInfo>) filterResults.values);
            notifyDataSetChanged();
        }
    }

    public class RestInfo_ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView rName, rLot, progBarVal;
        public ImageView logo;
        public ProgressBar rVacancy_PROGBAR;

        public RestInfo_ViewHolder(final View itemView)
        {
            super(itemView);

            rName = (TextView)itemView.findViewById(R.id.txtVw_RestName);
            rLot = (TextView)itemView.findViewById(R.id.txtVw_RestLot);
            progBarVal = (TextView)itemView.findViewById(R.id.progBarTextValue);
            logo = (ImageView)itemView.findViewById(R.id.logo);

            rVacancy_PROGBAR = (ProgressBar)itemView.findViewById(R.id.progBar);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    selectedItemID = getAdapterPosition();
                    Intent intent = new Intent(context, RestaurantInfo.class);

                    intent.putExtra("selected_Rest", filteredList.get(selectedItemID));
                    context.startActivity(intent);
                }
            });
        }
    }
}
