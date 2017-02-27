package fyp.inrestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;
import fyp.inrestaurant.RecyclerViewAdapter.ProfileInfoAdapter;

public class ViewProfile extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{

    private String prefName;
    private SharedPreferences sharedPref;
    private String key0, key1, key2, points, disc;

    private RecyclerView recyclVw;
    private ProfileInfoAdapter adapter;

    private EditText uName, uEmail;
    private Button redeemCoupon;
    private TextView uPoints;
    private DrawerLayout dLayout;
    private NavigationView navView;
    private JSONObject jsObj;
    private ArrayList< ArrayList<String>> profileInf;
    private Spinner pointSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        prefName = getString(R.string.prefName);
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        key0 = getString(R.string.key0);
        key1 = getString(R.string.key1);
        key2 = getString(R.string.key2);
        points = "";
        disc = "";

        uName = (EditText) findViewById(R.id.profileUName);
        uEmail = (EditText) findViewById(R.id.profileUEmail);
        uPoints = (TextView) findViewById(R.id.profileUPoints);

        redeemCoupon = (Button) findViewById(R.id.pointRedeem);
        redeemCoupon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean pointsNOTNULL = !points.isEmpty();
                boolean grtr_O_eql = Integer.parseInt(uPoints.getText().toString()) - Integer.parseInt(points) >= 0;
                boolean eql = Integer.parseInt(uPoints.getText().toString()) - Integer.parseInt(points) == 0;
                boolean lss = Integer.parseInt(uPoints.getText().toString()) - Integer.parseInt(points) < 0;

                if(pointsNOTNULL)
                {
                    if (grtr_O_eql)
                    {
                        if(eql)
                            showDialog("ptsmin10is0");
                        new NetworkDatabaseTask(ViewProfile.this, "random" + sharedPref.getString(key1, "") + "||" + disc).execute();
                    }
                    else if(lss)
                        showDialog("notenoughpts");
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.ViewProfileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        profileInf = new ArrayList<>();
        new NetworkDatabaseTask(this, "view" + sharedPref.getString(key1, "")).execute();

        recyclVw = (RecyclerView) findViewById(R.id.voucherRecycl);
        adapter = new ProfileInfoAdapter(this, profileInf, Integer.parseInt(sharedPref.getString(key1, "")));
        GridLayoutManager glm = new GridLayoutManager(this, 1);

        recyclVw.setLayoutManager(glm);
        recyclVw.setAdapter(adapter);

        dLayout = (DrawerLayout)findViewById(R.id.drawer3);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView = (NavigationView)findViewById(R.id.infNav);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.prevActv:
                        finish();
                        return true;
                    case R.id.aboutPage:
                        startActivity(new Intent(ViewProfile.this, About.class));
                        return true;
                    case R.id.viewPoints:
                        return true;
                    case R.id.logOut:
                        SharedPreferences.Editor edtr = sharedPref.edit();

                        edtr.putString(key0, "loggedOut");
                        edtr.remove(key1);
                        edtr.remove(key2);

                        edtr.apply();
                        startActivity(new Intent(ViewProfile.this, MainActivity.class));

                        return true;
                    default:
                        return true;
                }
            }
        });

        //change back button to previous activity
        //workaround with back button implemented on drawer layout
        //modified! no longer serves the above purpose
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dLayout.isDrawerOpen(GravityCompat.START))
                    dLayout.closeDrawer(GravityCompat.START);
                else
                    dLayout.openDrawer(GravityCompat.START);
            }
        });


        pointSpin = (Spinner)findViewById(R.id.points);

        String[] strArr = getResources().getStringArray(R.array.points_redeem);
        ArrayList<String> sortItems = new ArrayList<>(Arrays.asList(strArr));
        ArrayAdapter<String> strAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner, sortItems);

        pointSpin.setAdapter(strAdapter);
        pointSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                String selectedItem = pointSpin.getSelectedItem().toString();

                switch (selectedItem)
                {
                    case "10 pts for 5% discount":
                        points = "10";
                        disc = "5%";
                        break;
                    case "30 pts for 10% discount":
                        points = "30";
                        disc = "10%";
                        break;
                    case "50 pts for 15% discount":
                        points = "50";
                        disc = "15%";
                        break;
                    default:
                        points = "";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                points = "";
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.edit_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = new Intent(ViewProfile.this, EditProfile.class);
        String[] input;

        switch(item.getItemId())
        {
            case R.id.editProfile:
                input = new String[]{"Profile", uName.getText().toString(), uEmail.getText().toString()};
                intent.putExtra("input", input);

                startActivity(intent);
                return true;
            case R.id.editPassword:
                input = new String[]{"Password", uName.getText().toString(), uEmail.getText().toString()};
                intent.putExtra("input", input);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void processFinish(String result)
    {
        if (!result.isEmpty())
        {
            if(result.contains("Success"))
            {
                showDialog(result);
            }
            else if(result.contains("Error") || result.contains("Failed"))
            {
                showDialog(result);
            }
            else
            {
                try
                {
                    jsObj = new JSONObject(result);

                    if (jsObj.has("uName"))
                    {
                        uName.setText(jsObj.get("uName").toString());
                        uEmail.setText(jsObj.get("userEmail").toString());
                    }


                    uPoints.setText(jsObj.get("userPoint").toString());

                    if(jsObj.has("CouponData"))
                    {
                        ArrayList<ArrayList<String>> tempMainLst = new ArrayList<>();
                        ArrayList<String> tempCoupon;

                        for (int ctr = 0; ctr < jsObj.getJSONArray("CouponData").length(); ctr++)
                        {
                            tempCoupon = new ArrayList<>();

                            tempCoupon.add(jsObj.getJSONArray("CouponData").getJSONObject(ctr).get("CouponCode").toString());
                            tempCoupon.add(jsObj.getJSONArray("CouponData").getJSONObject(ctr).get("CouponValue").toString());
                            tempCoupon.add(jsObj.getJSONArray("CouponData").getJSONObject(ctr).get("CouponExpiry").toString());

                            tempMainLst.add(tempCoupon);
                        }

                        if (profileInf.isEmpty())
                        {
                            profileInf.addAll(tempMainLst);
                            adapter.notifyDataSetChanged();
                        }
                        else
                        {
                            ArrayList<Integer> sameCoupon = new ArrayList<>(), mainIndexList = new ArrayList<>();

                            for (int ctr = 0; ctr < tempMainLst.size(); ctr++)
                            {
                                if (!tempMainLst.get(ctr).isEmpty())
                                {
                                    mainIndexList.add(ctr);

                                    for (int ctr2 = 0; ctr2 < profileInf.size(); ctr2++)
                                    {
                                        if (tempMainLst.get(ctr).get(0).equals(profileInf.get(ctr2).get(0)))
                                            sameCoupon.add(ctr);
                                    }
                                }
                            }

                            for (int ctr = 0; ctr < mainIndexList.size(); ctr++)
                            {
                                for (int ctr2 = 0; ctr2 < sameCoupon.size(); ctr2++)
                                {
                                    if (mainIndexList.get(ctr).equals(sameCoupon.get(ctr2)))
                                    {
                                        mainIndexList.remove(ctr);
                                        sameCoupon.remove(ctr2);

                                        ctr -= (ctr - 1 < -1) ? 0 : 1;
                                        break;
                                    }
                                }
                            }


                            for (int ctr = 0; ctr < mainIndexList.size(); ctr++)
                            {
                                if(tempMainLst.size() > profileInf.size())
                                {
                                    profileInf.add(tempMainLst.get(ctr));
                                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                                }
                            }
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        prefName = getString(R.string.prefName);
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
        new NetworkDatabaseTask(this, "view" + sharedPref.getString(key1, "")).execute();
    }

    private void showDialog(final String input)
    {
        String title = "Generic title", msg = "Generic message", okay = "OK";

        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int w)
            {
                if(input.contains("Success"))
                    ViewProfile.super.recreate();
            }
        };

        switch(input)
        {
            case "notenoughpts":
                title = "Insufficient Points";
                msg = "You do not have sufficient points to redeem any voucher. The minimum is 10 pts.";
                break;
            case "ptsmin10is0":
                title = "Warning!";
                msg = "Your point balance will be 0 after this redemption";
                break;
            default:
                title = "Server Response";
                msg = input;
                break;
        }

        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setPositiveButton(okay,ocl);
        dlg.show();
    }
}

