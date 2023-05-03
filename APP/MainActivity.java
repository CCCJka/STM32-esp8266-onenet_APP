package com.example.example;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String Device_ID = "1076205432";        //设备id
    private String api_key = "PkGbK=02duescpR4ZK6=PLoDpmo=";
    //http://api.heclouds.com/devices/1076205432  API地址


    private String str;
    private int speed = 0;

    private Button send;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button)findViewById(R.id.send);
        tv = (TextView) findViewById(R.id.showrec);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(speed <= 100){
                    speed += 20;
                    if(speed > 100){
                        speed = 0;
                    }
                }
                Log.e("TAG",String.valueOf(speed));
//                postData("status",String.valueOf(speed));
                postCmd("{\"status\":"+speed+"}");
                tv.setText(str);

            }
        });
    }

    //向云服务器上传数据
    public void postData(String dataStream, String dataToPost){
        Toast.makeText(MainActivity.this, "开始上传", Toast.LENGTH_SHORT).show();   //提示
        String dataNew = new String("{\""+dataStream+"\":"+dataToPost+"}");
        String response = null;
        byte[] data = dataNew.getBytes();
        try{//上传数据采用POST方法
            URL url = new URL("http://api.heclouds.com/devices/"+Device_ID+"/datapoints?type=5");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(120);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("api-key", api_key);
            connection.setRequestProperty("Content-Length", String.valueOf(data.length));
            connection.setChunkedStreamingMode(5);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
            if(connection.getResponseCode()==200){
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = inputStream.read(buffer))!=-1){
                    os.write(buffer,0,len);
                }
                inputStream.close();
                os.close();
                //正常则返回{"errno":0,"error":"succ"}，此函数返回void，用不上这个
                response = os.toString();
                Log.i("TAG", response);
            }
            Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //向云服务器上传数据
    public void postCmd(String command){
        Toast.makeText(MainActivity.this, "开始发送命令", Toast.LENGTH_SHORT).show();   //提示
        String dataNew = new String(command);
        str = dataNew;
        String response = null;
        byte[] data = dataNew.getBytes();
        try{//上传数据采用POST方法
            URL url = new URL("http://api.heclouds.com/cmds?timeout=1000&device_id="+Device_ID);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(15*1000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("api-key",api_key);
            connection.setRequestProperty("Content-Length",String.valueOf(data.length));
            connection.setChunkedStreamingMode(5);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
            if(connection.getResponseCode()==200){
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = inputStream.read(buffer))!=-1){
                    os.write(buffer,0,len);
                }
                inputStream.close();
                os.close();
                //正常则返回{"errno":0,"error":"succ"}，此函数为void，用不上这个
                response = os.toString();
                Log.i("TAG", response);
            }
            Toast.makeText(MainActivity.this, "发送命令成功", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "发送命令失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void parseJSON(String response, List<DataBean> list) {
        if (list != null) {
            list.clear();
        }
        try {
            JSONObject object = new JSONObject(response);
            JSONObject jodata = object.getJSONObject("data");
            JSONArray jadatastreams = jodata.getJSONArray("datastreams");
            JSONObject jodatapoints = (JSONObject) jadatastreams.get(0);
            JSONArray jadatapoints = jodatapoints.getJSONArray("datapoints");
            for (int i=0; i<jadatapoints.length(); i++) {
                JSONObject jo = (JSONObject) jadatapoints.get(i);
                String at = jo.getString("at");
                String value = jo.getString("value");
                DataBean db = new DataBean(at, value);
                list.add(db);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}