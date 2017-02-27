package fyp.inrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import fyp.inrestaurant.InputValidation.InputValidation;
import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;

public class NewPassword extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{
    private EditText txtVw1, txtVw2;
    private InputValidation iv1, iv2;
    private Button submit;
    private Toolbar toolbar;

    private TimerTask task = new TimerTask()
    {
        @Override
        public void run()
        {
            startActivity(new Intent(NewPassword.this, MainActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        final String id = getIntent().getStringExtra("id");

        toolbar = (Toolbar)findViewById(R.id.new_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Reset Password");
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        txtVw1 = (EditText) findViewById(R.id.newP);
        txtVw2 = (EditText) findViewById(R.id.confNew);

        iv1 = new InputValidation(this, txtVw1);
        iv2 = new InputValidation(this, txtVw2, txtVw1);

        txtVw1.addTextChangedListener(iv1);
        txtVw2.addTextChangedListener(iv2);

        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(iv1.getHasError() || iv2.getHasError())
                    showDialog(true, "");
                else
                    new NetworkDatabaseTask(NewPassword.this, "editpassword"+id+"||"+txtVw1.getText().toString()+"||"+"NaN").execute();
            }
        });
    }

    private void showDialog(final boolean hasInputError, final String input)
    {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int w)
            {
                if(!hasInputError && input.contains("Success"))
                {
                    Timer timertask = new Timer();
                    long delay = 1000;
                    timertask.schedule(task, delay);
                }
            }
        };

        if(!hasInputError)
        {
            dlg.setTitle("Server Response");
            dlg.setMessage(input);

        }
        else
        {
            dlg.setTitle(getString(R.string.dialog_title));
            dlg.setMessage(getString(R.string.resolveError));
        }

        dlg.setPositiveButton("OK", ocl);
        dlg.show();
    }

    @Override
    public void processFinish(String output)
    {
        if(!output.isEmpty())
        {
            if(output.contains("Error"))
                showDialog(false, output);
            else
            {
                showDialog(false, output);
            }
        }
        else
            showDialog(false, "Server failed to respond");
    }
}
