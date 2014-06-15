package com.ladybug.iptracker_android.iptracker;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private static final String IPTRACKER_ADRESS_DEV = "http://localhost:5000";
    private static final String IPTRACKER_ADRESS_PROD = "http://iptracker.herokuapp.com";
    private static final String IPTRACKER_ADRESS = IPTRACKER_ADRESS_PROD;

    private static final String FILENAME = "file_device_name";

    String m_deviceName;

    private EditText m_editDeviceName;
    private Button m_btnSave;
    private Button m_btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_editDeviceName = (EditText) findViewById(R.id.edit_device_name);

        m_deviceName = getDeviceName();
        if (m_deviceName != null) {
            m_editDeviceName.setText(m_deviceName);
        }

        m_btnSave = (Button) findViewById(R.id.btn_save);
        m_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_deviceName = m_editDeviceName.getText().toString();
                Log.d(TAG, "Save: " + m_deviceName);
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(m_deviceName.getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        m_btnPost = (Button) findViewById(R.id.btn_post);
        m_btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Post: " + m_editDeviceName.getText());
                new DownloadFilesTask().execute();
            }
        });
    }

    protected void postData(String _deviceName) {
        if (_deviceName != null) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(IPTRACKER_ADRESS + "/device/" + _deviceName);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("id", "12345"));
                nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                Log.d(TAG, response.toString());
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.toString());
            }
        }
    }

    protected String getDeviceName() {
        String deviceName = null;

        FileInputStream is = null;
        try {
            deviceName = "";

            is = openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            deviceName = sb.toString();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return deviceName;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            postData(m_deviceName);

            return new Long(0);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }


}
