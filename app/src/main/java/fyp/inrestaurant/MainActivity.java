package fyp.inrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fyp.inrestaurant.InputValidation.InputValidation;
import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;

public class MainActivity extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{

    private SharedPreferences sharedPref;
    private String key0, key1, key2;

    private Button loginBttn, rgstrBttn;
    private EditText uName, pWord;
    private TextView forgotTxtVw;

    private InputValidation uIV, pIV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String prefName = getString(R.string.prefName);
        key0 = getString(R.string.key0);
        key1 = getString(R.string.key1);
        key2 = getString(R.string.key2);

        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);

        if (sharedPref.contains(key0))
        {
            if (sharedPref.getString(key0, "").equals("loggedIn"))
            {
                startActivity(new Intent(MainActivity.this, VacancyList.class));
                finish();
            }
        }

        loginBttn = (Button) findViewById(R.id.loginBttn);
        rgstrBttn = (Button) findViewById(R.id.rgstrBttn);
        uName = (EditText) findViewById(R.id.uName);
        pWord = (EditText) findViewById(R.id.pWord);

        uIV = new InputValidation(this, uName);
        pIV = new InputValidation(this, pWord);

        uName.addTextChangedListener(uIV);
        pWord.addTextChangedListener(pIV);

        loginBttn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new NetworkDatabaseTask(MainActivity.this, "login" + uName.getText().toString() + "||" + pWord.getText().toString()).execute();
            }
        });

        rgstrBttn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });

        forgotTxtVw = (TextView) findViewById(R.id.forgotPass);
        forgotTxtVw.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });
    }

    @Override
    public void processFinish(String output)
    {
        if (output.equals(""))
        {
            Toast.makeText(MainActivity.this, R.string.failLogin, Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(output.contains("Error") || output.contains("Invalid"))
                Toast.makeText(MainActivity.this, output, Toast.LENGTH_SHORT).show();
            else
            {
                SharedPreferences.Editor editr = sharedPref.edit();
                editr.putString(key0, "loggedIn");
                editr.putString(key1, output);
                editr.putString(key2, uName.getText().toString());
                editr.apply();

                startActivity(new Intent(MainActivity.this, VacancyList.class));
                finish();
            }
        }
    }
}

