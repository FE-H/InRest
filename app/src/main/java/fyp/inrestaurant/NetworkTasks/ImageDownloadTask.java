package fyp.inrestaurant.NetworkTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by HFE on 13-Jan-17.
 */
public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap>
{
    protected String urlStr = "http://192.168.43.92/fyp/assets/img/restaurant_logo/";

    protected URL url;
    protected HttpURLConnection urlConn;
    protected Bitmap result;

    protected BufferedInputStream inBuff;

    protected InputStream iStream;

    public ImageDownloadTask(String imageURL)
    {
        urlStr += imageURL;

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
    protected Bitmap doInBackground(String... strings)
    {
        try
        {
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);

            iStream = urlConn.getInputStream();
            inBuff = new BufferedInputStream(iStream);
            result = BitmapFactory.decodeStream(inBuff);

            iStream.close();
            inBuff.close();

            return result;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
        //delegate.processFinish(bitmap_TO_STR(result));
    }
}
