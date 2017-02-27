package fyp.inrestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fyp.inrestaurant.InputValidation.InputValidation;
import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;

public class ResetCode extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{
    private EditText code;
    private Button verify;
    private Toolbar toolbar;

    private SharedPreferences sharedPref;
    private String prefName, key01;

    InputValidation codeIV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_code);

        prefName = getString(R.string.prefName);
        key01 = getString(R.string.key1);
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);

        toolbar = (Toolbar)findViewById(R.id.resetCodeToolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(getString(R.string.reset_code));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        code = (EditText)findViewById(R.id.resetCode);

        codeIV = new InputValidation(this, code);
        code.addTextChangedListener(codeIV);

        verify = (Button)findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(!codeIV.getHasError())
                    new NetworkDatabaseTask(ResetCode.this, ResetCode.this, "verify"+code.getText().toString()).execute();
                else
                    showDialog("");
            }
        });
    }

    @Override
    public void processFinish(String output)
    {
        if(!output.isEmpty())
        {
            if(output.contains("Invalid") || output.contains("Error"))
            {
                showDialog(output);
            }
            else
            {
                Intent intent = new Intent(this, NewPassword.class);
                intent.putExtra("id", output);

                startActivity(intent);
                finish();
            }
        }
    }

    private void showDialog(String input)
    {
        String title = "Generic Title", msg = "Generic Message";

        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int w)
            {
            }
        };

        if(input.isEmpty())
        {
            title = "Invalid Input!";
            msg =  "Please resolve all input error(s) before submitting the code for verification";
        }
        else
        {
            title = "Server Response";
            msg = input;
        }

        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setPositiveButton("OK", ocl);
    }
}
