package com.example.doorunlockingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Control_Server extends AppCompatActivity {

    EditText ip;
    Socket myAppSocket = null;
    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;
    public static String CMD = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_server);

        ip = findViewById(R.id.ipAddess);
    }

    public void train_pi(View view) {
        getIPandPort();
        CMD = " train";
        Socket_AsyncTask cmd_increase_servo = new Socket_AsyncTask();
        cmd_increase_servo.execute();
    }

    public void manual_control(View view) {
        getIPandPort();
        CMD = " terminate";
        Socket_AsyncTask cmd_increase_servo = new Socket_AsyncTask();
        cmd_increase_servo.execute();
    }

    public void getIPandPort()
    {
        String iPandPort = ip.getText().toString();
        Toast.makeText(this, iPandPort, Toast.LENGTH_SHORT).show();
        Log.d("MYTEST","IP String: "+ iPandPort);
        String temp[]= iPandPort.split(":");
        wifiModuleIp = temp[0];
        wifiModulePort = Integer.valueOf(temp[1]);
        Toast.makeText(this, String.valueOf(wifiModulePort), Toast.LENGTH_SHORT).show();
        Log.d("MY TEST","IP:" +wifiModuleIp);
        Log.d("MY TEST","PORT:"+wifiModulePort);
    }
    public class Socket_AsyncTask extends AsyncTask<Void,Void,Void>
    {
        Socket socket;

        @Override
        protected Void doInBackground(Void... params){
            try{
                //Toast.makeText(Control_Server.this, "1", Toast.LENGTH_SHORT).show();
                InetAddress inetAddress = InetAddress.getByName(Control_Server.wifiModuleIp);
                socket = new java.net.Socket(inetAddress,Control_Server.wifiModulePort);

                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeBytes(CMD);

                dataOutputStream.close();
                socket.close();

            }catch (UnknownHostException e){
                e.printStackTrace();
                Toast.makeText(Control_Server.this, e.toString(), Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                e.printStackTrace();
                Toast.makeText(Control_Server.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }
}