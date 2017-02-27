package fyp.inrestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fyp.inrestaurant.NetworkTasks.NetworkDatabaseTask;
import fyp.inrestaurant.InputValidation.InputValidation;

public class EditProfile extends AppCompatActivity implements NetworkDatabaseTask.AsyncResponse
{
    private String prefName;
    private SharedPreferences sharedPref;
    private String key0, key1, key2;
    private InputValidation iv1, iv2, pwordIV;

    private Toolbar toolbar;
    private EditText txtVw1, txtVw2, uPword;
    private Button submitButt;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        final String[] input = getIntent().getStringArrayExtra("input");
        msg = (input[0].equals("Profile"))? "profile details" : "password";

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle("Edit " + input[0]);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        prefName = getString(R.string.prefName);
        key0 = getString(R.string.key0);
        key1 = getString(R.string.key1);
        key2 = getString(R.string.key2);
        sharedPref = getSharedPreferences(prefName, Context.MODE_PRIVATE);

        uPword = (EditText) findViewById(R.id.confPass);
        submitButt = (Button) findViewById(R.id.submitEdit);

        if(input[0].equals("Profile"))
        {
            txtVw1 = (EditText) findViewById(R.id.changeUname);
            txtVw2 = (EditText) findViewById(R.id.changeUEmail);



            iv1 = new InputValidation(this, txtVw1);
            iv2 = new InputValidation(this, txtVw2);
        }
        else if(input[0].equals("Password"))
        {
            txtVw1 = (EditText) findViewById(R.id.changePword);
            txtVw1.setVisibility(View.VISIBLE);
            txtVw2 = (EditText) findViewById(R.id.changeConfirm);
            txtVw2.setVisibility(View.VISIBLE);

            TextView invisible1 = (EditText) findViewById(R.id.changeUname);
            invisible1.setVisibility(View.GONE);
            TextView invisible2 = (EditText) findViewById(R.id.changeUEmail);
            invisible2.setVisibility(View.GONE);

            TextView label1 = (TextView) findViewById(R.id.labelVw1);
            TextView label2 = (TextView) findViewById(R.id.labelVw2);

            label1.setText(R.string.newPassword);
            label2.setText(R.string.confNewPassword);

            iv1 = new InputValidation(this, txtVw1, uPword);
            iv2 = new InputValidation(this, txtVw2, txtVw1);
        }

        pwordIV = new InputValidation(this, uPword);

        txtVw1.addTextChangedListener(iv1);
        txtVw2.addTextChangedListener(iv2);
        uPword.addTextChangedListener(pwordIV);

        if(input[0].contains("Profile"))
        {
            txtVw1.setText(input[1]);
            txtVw2.setText(input[2]);
        }

        submitButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(iv1.getHasError() || iv2.getHasError() || pwordIV.getHasError())
                {
                    showDialog(true);
                }
                else
                {
                    String part1 = (input[0].equals("Profile")) ? "editprofile" : "editpassword";
                    String part2 = (input[0].equals("Profile")) ? sharedPref.getString(key1, "") + "||" + txtVw1.getText().toString() + "||" + txtVw2.getText().toString() + "||" + uPword.getText().toString() : sharedPref.getString(key1, "") + "||" + txtVw1.getText().toString() + "||" + uPword.getText().toString();
                    String input = part1 + part2;
                    new NetworkDatabaseTask(EditProfile.this, input).execute();
                }
            }
        });

    }

    @Override
    public void processFinish(String result)
    {
        if (!result.isEmpty())
        {
            if(result.equals("Invalid Password"))
                showDialog(false);
            else if(result.equals("success"))
            {
                Toast.makeText(this, "You have changed your "+msg+"  successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if(result.contains("No new"))
            {
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDialog(boolean hasInputError)
    {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int w)
            {
            }
        };

        if(!hasInputError)
        {
            dlg.setTitle("Failed to change " + msg);
            dlg.setMessage("Invalid Password!");

        }
        else
        {
            dlg.setTitle(getString(R.string.dialog_title));
            dlg.setMessage(getString(R.string.resolveError));
        }

        dlg.setPositiveButton("OK", ocl);
        dlg.show();
    }
}