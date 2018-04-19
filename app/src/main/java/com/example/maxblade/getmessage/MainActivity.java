package com.example.maxblade.getmessage;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String macAdd="";
    EditText regnum;
    TextView macTag,res;
    Button accept;
    ProgressDialog pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pro=new ProgressDialog(MainActivity.this);
        pro.setMessage("Getting Server Response");

        regnum=findViewById(R.id.regnum);
        accept=findViewById(R.id.accept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accept.getText().toString().equals("Submit")){
                    pro.show();
                    getResults(regnum.getText().toString().substring(regnum.length() - 4));}
                else{accept.setText("Submit");regnum.setVisibility(View.VISIBLE);res.setText("");}
            }
        });

        res=findViewById(R.id.res);

        macAdd=getMacAddr();
        macTag=findViewById(R.id.macTag);
        macTag.setText("Mac Address \n"+macAdd);
    }
    public void getResults(final String data)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    URL u = new URL("https://android-club-project.herokuapp.com/upload_details?reg_no="+data+"&mac="+macAdd);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();
                    InputStream in = c.getInputStream();
                    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    in.read(buffer);
                    bo.write(buffer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                res.setText(bo.toString().trim());
                                regnum.setVisibility(View.INVISIBLE);
                                accept.setText("Clear");
                                bo.close();pro.dismiss();
                            } catch (IOException e) {
                                res.setText("Something Went\nWrong");pro.dismiss();
                            }
                        }
                    });
                }
                catch (Exception e){res.setText("Something Went\nWrong");pro.dismiss();}
            }
        }).start();
    }
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {return "";}
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {res1.append(Integer.toHexString(b & 0xFF) + ":");}
                if (res1.length() > 0) {res1.deleteCharAt(res1.length() - 1);}
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }
}
