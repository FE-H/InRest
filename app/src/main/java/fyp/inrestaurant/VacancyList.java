package fyp.inrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import fyp.inrestaurant.NetworkTasks.ImageDownloadTask;
import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;
import fyp.inrestaurant.RecyclerViewAdapter.RestInfo;
import fyp.inrestaurant.RecyclerViewAdapter.RestInfoAdapter;

public class VacancyList extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{
    private final static ArrayList<String> ELEMENTS = new ArrayList<>(Arrays.asList("restaurantID", "restaurantName", "restaurantType", "restaurantLot", "restaurantVacancy", "restaurantOwner", "restaurantContact", "restaurantEmail", "restaurantLogoURL"));

    private DrawerLayout dLayout;
    private SearchView srchVw;
    private NavigationView navView;
    private Toolbar toolBar;
    private RecyclerView recyclVw;
    private RestInfoAdapter adapter;
    private SwipeRefreshLayout refresh;
    private Spinner sortSpin;

    private ArrayList<RestInfo> rInf_LIST;
    private JSONArray jsArr;
    private ArrayList<String> imageDLURL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_list);

        toolBar = (Toolbar) findViewById(R.id.vacList_toolbar);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        dLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dLayout, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView = (NavigationView) findViewById(R.id.vacNav);
        navView.getMenu().findItem(R.id.prevActv).setVisible(false);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.aboutPage:
                        startActivity(new Intent(VacancyList.this, About.class));
                        return true;
                    case R.id.viewPoints:
                        startActivity(new Intent(VacancyList.this, ViewProfile.class));
                        return true;
                    case R.id.logOut:
                        String prefName = getString(R.string.prefName), key0 = getString(R.string.key0), key1 = getString(R.string.key1), key2 = getString(R.string.key2);

                        SharedPreferences sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor edtr = sharedPref.edit();

                        edtr.putString(key0, "loggedOut");
                        edtr.remove(key1);
                        edtr.remove(key2);

                        edtr.apply();
                        startActivity(new Intent(VacancyList.this, MainActivity.class));

                        return true;
                    default:
                        return true;
                }
            }
        });

        toolBar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (dLayout.isDrawerOpen(GravityCompat.START))
                    dLayout.closeDrawer(GravityCompat.START);
                else
                    dLayout.openDrawer(GravityCompat.START);
            }
        });

        rInf_LIST = new ArrayList<>();
        new NetworkDatabaseTask(this, "listfirstTime").execute();

        //Setup Recycler View with the appropriate adapter and layout.
        recyclVw = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RestInfoAdapter(this, rInf_LIST);
        GridLayoutManager glm = new GridLayoutManager(this, 1);

        recyclVw.setLayoutManager(glm);
        recyclVw.setAdapter(adapter);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new Handler().post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        new NetworkDatabaseTask(VacancyList.this, "list").execute();
                        Toast.makeText(VacancyList.this, "Refreshed", Toast.LENGTH_SHORT).show();
                        refresh.setRefreshing(false);
                    }
                });
            }
        });

        srchVw = (SearchView) findViewById(R.id.search);
        srchVw.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter.getFilter().filter(query);;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        sortSpin = (Spinner )findViewById(R.id.sortItem);

        String[] strArr = getResources().getStringArray(R.array.sorting_type);
        ArrayList<String> sortItems = new ArrayList<>(Arrays.asList(strArr));
        ArrayAdapter<String> strAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner, sortItems);

        sortSpin.setAdapter(strAdapter);
        sortSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                String selectedItem = sortSpin.getSelectedItem().toString();

                switch (selectedItem)
                {
                    case "By Alphabetical Order (A-Z)":
                        Collections.sort(rInf_LIST, RestInfo.ASC_NAME_COMPARATOR);
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount()-1);
                        break;
                    case "By Vacancies Available (Ascending)":
                        Collections.sort(rInf_LIST, RestInfo.ASC_VACANCY_COMPARATOR);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }

    //NetworkDatabaseTask
    @Override
    public void processFinish(String output)
    {
        String boolEAN = output.substring(0, output.indexOf('e') + 1);
        boolean firstTime = Boolean.parseBoolean(boolEAN);

        imageDLURL = new ArrayList<>();

        output = output.substring(output.indexOf('e') + 1);

        if (!output.isEmpty())
        {
            try
            {
                jsArr = new JSONArray(output);

                for (int ctr = 0; ctr < jsArr.length(); ctr++)
                {
                    RestInfo temp = JSONArray_RestInf(ctr);
                    RestInfo_ArrLst(temp, firstTime);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();

            }
        }
        else
            Toast.makeText(VacancyList.this, "Failed to retrieve list", Toast.LENGTH_SHORT).show();
    }

    //Converts the JSONArray to RestInfo
    private RestInfo JSONArray_RestInf(final int index)
    {
        RestInfo tempInf;

        try
        {
            ArrayList<String> tempArrLst = new ArrayList<>();
            Bitmap temp = null;

            //Converts JSONArray to ArrayList of strings
            for (String ctr : ELEMENTS)
            {
                tempArrLst.add(jsArr.getJSONObject(index).get(ctr).toString());

                if (ctr.equals(ELEMENTS.get(ELEMENTS.size() - 1)))
                    temp = (!jsArr.getJSONObject(index).get(ctr).toString().equals(getString(R.string.defaultURL))) ? new ImageDownloadTask(jsArr.getJSONObject(index).get(ctr).toString()).execute().get() : null;
            }

            tempInf = new RestInfo(this, Integer.parseInt(tempArrLst.get(0)), tempArrLst.get(1), tempArrLst.get(2), tempArrLst.get(3), Integer.parseInt(tempArrLst.get(4)), tempArrLst.get(5), tempArrLst.get(6), tempArrLst.get(7), tempArrLst.get(8), temp);

            return tempInf;
        }
        catch (JSONException e)
        {
            e.printStackTrace();

            return new RestInfo(0, "", "", "", 0, "", "", "", "");
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();

            return new RestInfo(0, "", "", "", 0, "", "", "", "");
        }
    }

    //Adds the temporary restaurant info to the array.
    private void RestInfo_ArrLst(RestInfo tempInf, boolean firstT)
    {
        if (!tempInf.getEMPTY_Status())
        {
            if (firstT)
            {
                rInf_LIST.add(tempInf);
                adapter.notifyItemInserted(rInf_LIST.indexOf(tempInf));
            }
            else
            {
                boolean hasSameField;
                ArrayList<Boolean> hasChanged = new ArrayList<>(), isSimilar = new ArrayList<>();
                boolean hasRestInf = false;
                ArrayList<Integer> changedPos = new ArrayList<>();

                //Loop to check for duplicates
                for (int ctr = 0; ctr < rInf_LIST.size(); ctr++)
                {
                    hasSameField = (tempInf.checkFields(rInf_LIST.get(ctr)));
                    if (tempInf.getRestID() == rInf_LIST.get(ctr).getRestID() && !hasSameField)
                    {
                        rInf_LIST.set(ctr, tempInf);
                        changedPos.add(ctr);

                        hasRestInf = true;
                        hasChanged.add(true);
                        isSimilar.add(false);
                    }
                    else if (tempInf.getRestID() == rInf_LIST.get(ctr).getRestID() && hasSameField)
                    {
                        hasRestInf = true;
                        isSimilar.add(true);
                        hasChanged.add(false);
                    }
                }

                for(int ctr = 0; ctr < hasChanged.size(); ctr++)
                {
                    if (!hasChanged.get(ctr)&& !isSimilar.get(ctr))
                    {
                        rInf_LIST.add(tempInf);
                        adapter.notifyItemInserted(adapter.getItemCount() - 1);
                    }
                    else if (hasChanged.get(ctr))
                    {
                        adapter.notifyItemChanged(changedPos.get(ctr));
                    }
                }

                if(!hasRestInf)
                {
                    rInf_LIST.add(tempInf);
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new NetworkDatabaseTask(this, "list").execute();
    }
}
