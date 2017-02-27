package fyp.inrestaurant.NetworkTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by HFE on 30-Dec-16.
 */
public class NetworkDatabaseTask extends AsyncTask<String, Void, String>
{
    public AsyncResponse delegate = null;
    ArrayList< AsyncTask<String, Void, Bitmap> > taskList;

    private String netTask, query, line, urlStr = "http://192.168.43.92/fyp/android-server-interaction/";
    private Boolean mode;
    private Context context;

    private URL url;
    private HttpURLConnection urlConn;
    private String uriStr, result;
    private Uri.Builder builder;

    private BufferedReader buffRead;
    private BufferedWriter buffWrit;
    private StringBuffer buffStr;

    private InputStream iStream;
    private OutputStream oStream;

    private ArrayList<String> imgDL;
    private String temp;

    private ProgressDialog progDlg;

    public interface AsyncResponse
    {
        void processFinish(String output);
    }

    public NetworkDatabaseTask(AsyncResponse arg, String task)
    {
        uriStr = "";
        mode = false;
        result = "";
        imgDL = new ArrayList<>();

        if (arg != null)
            this.delegate = arg;

        if (task.contains("list"))
        {
            if (task.contains("firstTime"))
            {
                mode = true;
                netTask = "firstTime";
            }
            else
                netTask = "list";

            urlStr += "populateList.php";
        }
        else if(task.contains("use"))
        {
            setUri(task, "use");
            netTask = "use";

            builder = new Uri.Builder().appendQueryParameter(netTask, uriStr);
            urlStr += "userClaim.php";
        }
        else if (task.contains("view"))
        {
            setUri(task, "view");
            netTask = "view";

            builder = new Uri.Builder().appendQueryParameter("dataArray", netTask + "||" + uriStr);
            urlStr += "userProfile.php";
        }
        else if (task.contains("random"))
        {
            setUri(task, "random");
            netTask = "random";

            builder = new Uri.Builder().appendQueryParameter("data", uriStr);
            urlStr += "userRedeem.php";
        }
        else if (task.contains("edit"))
        {
            setUri(task, (task.contains("profile"))? "editprofile" : "editpassword");
            netTask = (task.contains("profile"))? "edit||profile" : "edit||password";

            builder = new Uri.Builder().appendQueryParameter("dataArray", netTask + "||" + uriStr);
            urlStr += "userProfile.php";
        }
        else if (task.contains("registrationData"))
        {
            setUri(task, "registrationData");
            netTask = "registrationData";

            urlStr += ("userRegister.php");
            builder = new Uri.Builder().appendQueryParameter(netTask, uriStr);
        }
        else if (task.contains("info"))
        {
            setUri(task, "info");
            netTask = "info";

            urlStr += "updateCheck.php";
            builder = new Uri.Builder().appendQueryParameter("dataArray", uriStr);
        }
        else if (task.contains("login"))
        {
            setUri(task, "login");
            netTask = "loginData";

            urlStr += "userLogin.php";
            builder = new Uri.Builder().appendQueryParameter(netTask, uriStr);
        }

        if (!uriStr.isEmpty())
            query = builder.build().getEncodedQuery();

        try
        {
            url = new URL(urlStr);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public NetworkDatabaseTask(Context ctxt, AsyncResponse arg, String task)
    {
        uriStr = "";
        mode = false;
        result = "";
        imgDL = new ArrayList<>();

        context = ctxt;

        if (arg != null)
            this.delegate = arg;

        if (task.contains("reset"))
        {
            setUri(task, "reset");
            netTask = "reset";

            urlStr += "userForgotPassword.php";
            builder = new Uri.Builder().appendQueryParameter("mailInf", uriStr);
        }
        else if(task.contains("verify"))
        {
            setUri(task, "verify");
            netTask = "verify";

            urlStr += "userVerifyReset.php";
            builder = new Uri.Builder().appendQueryParameter("verifyCode", uriStr);
        }

        if (!uriStr.isEmpty())
            query = builder.build().getEncodedQuery();

        try
        {
            url = new URL(urlStr);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute()
    {
        if(netTask.equals("reset"))
        {
            if(context != null)
            {
                progDlg = new ProgressDialog(context);

                progDlg.setMessage("Waiting for server response...");
                progDlg.setIndeterminate(false);
                progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDlg.setCancelable(true);

                progDlg.show();
            }
        }
    }

    @Override
    protected String doInBackground(String... strings)
    {
        try
        {
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);

            if (!netTask.equals("list") && !netTask.equals("firstTime"))
            {
                oStream = urlConn.getOutputStream();
                buffWrit = new BufferedWriter(new OutputStreamWriter(oStream, "UTF-8"), 8);

                buffWrit.write(query);

                buffWrit.flush();
                buffWrit.close();
                oStream.close();
            }

            iStream = urlConn.getInputStream();
            buffRead = new BufferedReader(new InputStreamReader(iStream, "UTF-8"), 8);
            buffStr = new StringBuffer();

            line = "";

            while ((line = buffRead.readLine()) != null)
            {
                buffStr.append(line);
            }

            result = buffStr.toString();

            iStream.close();
            buffRead.close();
            urlConn.disconnect();

            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (!result.isEmpty())
        {
            if (netTask.equals("firstTime") || netTask.equals("list"))
            {
                delegate.processFinish(mode+result);
            }
            else if (netTask.equals("reset"))
            {
                if(context != null)
                    progDlg.dismiss();
                delegate.processFinish(result);
            }
            else
                delegate.processFinish(result);
        }
    }

    private void setUri(String task, String keyword)
    {
        uriStr = task.substring(keyword.length());
    }
}
