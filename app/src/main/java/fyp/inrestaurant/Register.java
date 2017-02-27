package fyp.inrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;
import fyp.inrestaurant.InputValidation.InputValidation;

public class Register extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{
    private int left = 0, top = left, right, bottom;
    private boolean[] hasError;

    private Button buttonReg;
    private EditText uName, uEmail, uPword, uPword2;
    private InputValidation nameIV, emailIV, pwordIV, pword2IV;

    private TimerTask task = new TimerTask()
    {
        @Override
        public void run()
        {
            startActivity(new Intent(Register.this, MainActivity.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        hasError = new boolean[4];
        Arrays.fill(hasError, Boolean.TRUE);

        buttonReg = (Button) findViewById(R.id.submitReg);
        uName = (EditText) findViewById(R.id.uNameReg);
        uEmail = (EditText) findViewById(R.id.uEmailReg);
        uPword = (EditText) findViewById(R.id.uPwordReg);
        uPword2 = (EditText) findViewById(R.id.uPword2Reg);

        nameIV = new InputValidation(this, uName);
        emailIV = new InputValidation(this, uEmail);
        pwordIV = new InputValidation(this, uPword);
        pword2IV = new InputValidation(this, uPword2, uPword);

        uName.addTextChangedListener(nameIV);
        uEmail.addTextChangedListener(emailIV);
        uPword.addTextChangedListener(pwordIV);
        uPword2.addTextChangedListener(pword2IV);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (nameIV.getHasError() || emailIV.getHasError() || pwordIV.getHasError() || pword2IV.getHasError())
                {
                    showDialog();
                }
                else
                {
                    String input = uName.getText().toString() + "||" + uPword.getText().toString() + "||" + uEmail.getText().toString();

                    new NetworkDatabaseTask(Register.this, "registrationData" + input).execute();
                }
            }
        });
    }

    private void showDialog()
    {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int w)
            {
            }
        };
        dlg.setTitle(getString(R.string.dialog_title));
        dlg.setMessage(getString(R.string.resolveError));
        dlg.setPositiveButton("OK", ocl);
        dlg.show();
    }

    @Override
    public void processFinish(String output)
    {
        if (!output.isEmpty())
        {
            if(output.equals("Successful Insertion"))
            {
                Toast.makeText(Register.this, R.string.succRegis, Toast.LENGTH_SHORT).show();

                Timer timertask = new Timer();
                long delay = 1000;
                timertask.schedule(task, delay);
            }
            else
                Toast.makeText(Register.this, R.string.failRegis, Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(Register.this, R.string.failRegis, Toast.LENGTH_SHORT).show();
        }
    }
}
