package com.example.dcmotorgprs;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Init extends AppCompatActivity implements ConnectivityReceiverListener{
    private static final String TAG =Init.class.getName();
    public static String val1 = "", val2 = "";
    static EditText hardwareIPAddress, hardwarePhoneNumber;
    static String SERVERIP, phonenumber;
    TextView txtMobileIP;
    Button   Start;
    String   hardwareDeviceIP;
    Context  context;
    public static final String INTENT_IP_KEY="ip";
    public static final String INTENT_PHONE_KEY="phone";
    public void setConnectivityListener(ConnectivityReceiverListener listener) {

        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
    public static String getIPAddress(boolean useIPv6) {
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf
                        .getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv6) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port
                                // suffix
                                return delim < 0 ? sAddr : sAddr.substring(0,
                                        delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG,"Error While Getting IP :" + ex.getMessage());
        }
        return "";
    }

    public static void updateMessageBox(String messageBody,
                                        String originatingAddress) {
        SERVERIP = messageBody;
        Log.e(TAG, "Received Hardware Device IP  " +SERVERIP);
        hardwareIPAddress.setText(SERVERIP);
        phonenumber = originatingAddress;
        hardwarePhoneNumber.setText(phonenumber);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setConnectivityListener(this);
        setContentView(R.layout.activity_init);
        txtMobileIP = (TextView) findViewById(R.id.txtMobileIP);
        hardwareIPAddress = (EditText) findViewById(R.id.init_ip);
        hardwarePhoneNumber = (EditText) findViewById(R.id.Phoneno);

        if (!Utils.isNetworkAvailable(context)) {
            Utils.showAlertDialog(context, getString(R.string.app_name), "Please Enable Internet Connection");
        }
        //hardwarePhoneNumber.setText("8220883170");
        Start = (Button) findViewById(R.id.start);
        String mobileIPAddress = getIPAddress(true);
        txtMobileIP.setText(mobileIPAddress);

        Start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkAvailable(context)) {
                    Utils.showAlertDialog(context, getString(R.string.app_name), "Please Enable Internet Connection");
                    return;
                }
                if ( hardwareIPAddress != null && hardwareIPAddress.getText().length() != 0
                        && hardwarePhoneNumber.getText().length() != 0) {
                    hardwareDeviceIP = hardwareIPAddress.getText().toString();
                    String hardwareDevicePhone = hardwarePhoneNumber.getText().toString();
                    Intent intent = new Intent(Init.this, MainActivity.class);
                    intent.putExtra(INTENT_IP_KEY, hardwareDeviceIP);
                    intent.putExtra(INTENT_PHONE_KEY, hardwareDevicePhone);
                    val1 = hardwareDeviceIP;
                    val1 = hardwareDevicePhone;
                    startActivity(intent);
                    finish();
                } else {
                    Utils.showAlertDialog(context, getString(R.string.app_name), "Please Provide Device IP");
                    Toast.makeText(getApplicationContext(),
                            "Please Type Udp ServerIP", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    // gets the ip address of your hardwarePhoneNumber's networ
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        try{
            String ip = getIPAddress(true);
            txtMobileIP.setText(ip);
        }catch(Exception e){
            Log.e(TAG, "Faild To Get hardwareIPAddress address on network Change Error : " + e.toString());
        }
    }
}
