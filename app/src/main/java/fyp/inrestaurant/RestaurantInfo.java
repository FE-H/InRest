package fyp.inrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;
import fyp.inrestaurant.RecyclerViewAdapter.RestInfo;

public class RestaurantInfo extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{
    private Button isCheckedIn;
    private DrawerLayout dLayout;
    private NavigationView navView;
    private RestInfo rInf_LISTSELECTED;
    String checkedInrest;

    private TextView restLot, restEmail, restNo, restType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        checkedInrest = "";
        final String prefName = getString(R.string.prefName), key0 = getString(R.string.key0), key1 = getString(R.string.key1), key2 = getString(R.string.key2), key3 = getString(R.string.key3), key4 = getString(R.string.key4);
        final SharedPreferences sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);

        rInf_LISTSELECTED = getIntent().getExtras().getParcelable("selected_Rest");

        restLot = (TextView)findViewById(R.id.restLot);
        restType = (TextView)findViewById(R.id.restType);
        restEmail = (TextView)findViewById(R.id.restEmail);
        restNo = (TextView)findViewById(R.id.restTelNo);

        isCheckedIn = (Button) findViewById(R.id.checkButton);

        if(!sharedPref.getString(key4, "").equals(""))
        {
            Log.d(">>>", rInf_LISTSELECTED.getRestName()+", "+sharedPref.getString(key4, ""));
            if(rInf_LISTSELECTED.getRestName().equals(sharedPref.getString(key4, "")))
                isCheckedIn.setEnabled(true);
            else
            {
                isCheckedIn.setEnabled(false);
                Toast.makeText(this, "Your current checked in restaurant is "+sharedPref.getString(key4, ""), Toast.LENGTH_LONG).show();
            }
        }
        else
            Log.d(">>>", rInf_LISTSELECTED.getRestName()+", "+sharedPref.getString(key4, ""));

        isCheckedIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checkedInrest = rInf_LISTSELECTED.getRestName();
                String input = (sharedPref.contains(getString(R.string.key3))) ? sharedPref.getString(getString(R.string.key2), "") + "||" + sharedPref.getString(getString(R.string.key1), "") + "||" + sharedPref.getString(getString(R.string.key3), "") + "||" + Integer.toString(rInf_LISTSELECTED.getRestID()) : sharedPref.getString(getString(R.string.key2), "") + "||" + sharedPref.getString(getString(R.string.key1), "") + "||" + "No" + "||" + Integer.toString(rInf_LISTSELECTED.getRestID());
                new NetworkDatabaseTask(RestaurantInfo.this, "info" + input).execute();
            }
        });

        if(sharedPref.contains(key3))
            checkIN_OUT((sharedPref.getString(key3, "").equals("No"))?"1":"2");
        else
            checkIN_OUT("1");

        Toolbar toolbar = (Toolbar) findViewById(R.id.restInf_toolbar);
        setSupportActionBar(toolbar);
        //Your toolbar is now an action bar and you can use it like you always do, for example:
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(rInf_LISTSELECTED.getRestName());
        }

        restLot.setText(rInf_LISTSELECTED.getRestLot());
        restType.setText(rInf_LISTSELECTED.getRestType());
        restEmail.setText(rInf_LISTSELECTED.getRestEmail());
        restNo.setText(rInf_LISTSELECTED.getRestNo());

        dLayout = (DrawerLayout) findViewById(R.id.drawer2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView = (NavigationView) findViewById(R.id.infNav);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.prevActv:
                        finish();
                        return true;
                    case R.id.aboutPage:
                        startActivity(new Intent(RestaurantInfo.this, About.class));
                        return true;
                    case R.id.viewPoints:
                        startActivity(new Intent(RestaurantInfo.this, ViewProfile.class));
                        return true;
                    case R.id.logOut:
                        SharedPreferences.Editor edtr = sharedPref.edit();

                        edtr.putString(key0, "loggedOut");
                        edtr.remove(key1);
                        edtr.remove(key2);

                        edtr.apply();
                        startActivity(new Intent(RestaurantInfo.this, MainActivity.class));

                        return true;
                    default:
                        return true;
                }
            }
        });

        //change back button to previous activity
        //modified! no longer serves the above purpose
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dLayout.isDrawerOpen(GravityCompat.START))
                    dLayout.closeDrawer(GravityCompat.START);
                else
                    dLayout.openDrawer(GravityCompat.START);
            }
        });
    }


    @Override
    public void processFinish(String result)
    {
        if (result.isEmpty())
        {
            Toast.makeText(RestaurantInfo.this, R.string.failedCheck + " " + result, Toast.LENGTH_SHORT).show();
        }
        else
        {
            checkIN_OUT(result);
            SharedPreferences shardPref = getSharedPreferences(getString(R.string.prefName), Context.MODE_PRIVATE);
            String msg = (shardPref.getString(getString(R.string.key3), "").equals("Yes"))? "You are now checked into "+rInf_LISTSELECTED.getRestName() : "You have checked out from "+rInf_LISTSELECTED.getRestName()+" and gained 5 points";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private void checkIN_OUT(String result)
    {
        SharedPreferences sharPref = getSharedPreferences(getString(R.string.prefName), Context.MODE_PRIVATE);

        SharedPreferences.Editor edtr = sharPref.edit();

        switch (result)
        {
            case "1":
                edtr.putString(getString(R.string.key3), "No");
                edtr.putString(getString(R.string.key4), "");
                edtr.apply();

                isCheckedIn.setText(R.string.check_in_text);
                break;
            case "2":
                edtr.putString(getString(R.string.key3), "Yes");
                if(!checkedInrest.isEmpty())
                    edtr.putString(getString(R.string.key4), checkedInrest);
                edtr.apply();

                isCheckedIn.setText(R.string.check_out_text);
                break;
            default:
                break;
        }
    }
}
