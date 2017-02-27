package fyp.inrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;
import fyp.inrestaurant.InputValidation.InputValidation;

public class ForgotPassword extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{
    private Toolbar toolbar;
    private EditText uName, userEmail;
    private TextView hasCode;
    private InputValidation nameIV, emailIV;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = (Toolbar) findViewById(R.id.ForgotPassToolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reset Password");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        uName = (EditText)findViewById(R.id.forgotUName);
        userEmail = (EditText) findViewById(R.id.forgotUEmail);
        hasCode = (TextView)findViewById(R.id.hasCode);
        hasCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(ForgotPassword.this, ResetCode.class));
            }
        });

        nameIV = new InputValidation(this, uName);
        emailIV = new InputValidation(this, userEmail);

        uName.addTextChangedListener(nameIV);
        userEmail.addTextChangedListener(emailIV);

        submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (nameIV.getHasError() || emailIV.getHasError())
                {
                    showDialog("");
                }
                else
                {
                    String input = uName.getText().toString() + "||" + userEmail.getText().toString();
                    new NetworkDatabaseTask(ForgotPassword.this, ForgotPassword.this, "reset" + input).execute();
                }
            }
        });

    }

    @Override
    public void processFinish(String output)
    {
        if (output.length() != 0)
        {
            if (output.contains("Invalid") || output.contains("Failed"))
            {
                showDialog(output);
            }
            else
            {
                Toast.makeText(this, output, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent i=new Intent(ForgotPassword.this, ResetCode.class);
                        startActivity(i);
                    }
                }, 2000);
            }
        }
        else
            Toast.makeText(this, "Server failed to respond", Toast.LENGTH_SHORT).show();

    }

    private void showDialog(String error)
    {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int w)
            {
            }
        };

        if(error.length() == 0)
        {
            dlg.setTitle(getString(R.string.dialog_title));
            dlg.setMessage(getString(R.string.resolveError));
        }
        else
        {
            dlg.setTitle(((error.contains("Invalid"))? "Invalid combination" : "Server Error"));
            dlg.setMessage(error);
        }

        dlg.setPositiveButton("OK", ocl);
        dlg.show();
    }
}
