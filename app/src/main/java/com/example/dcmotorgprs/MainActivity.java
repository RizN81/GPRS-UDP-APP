package com.example.dcmotorgprs;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {
    // Button send;
    private static final int UDP_SERVER_PORT = 6001;
    private static final String TAG = MainActivity.class.getName();
    static TextView UDP_Ser_IP;
    static TextView IP;
    static String SERVERIP;
    static Context context;
    private static String UDP_SERVER_IP;
    private static String number;
    // ToggleButton toggle_human;
    EditText te, mm;
    TextView Server_IP;

    // Button forward,reverse,left,right,pick,place,stop1;
    String phone;
    String b = "";
    TextView display, mo, temp;
    DatagramSocket datagramSocket = null;
    String udpMsg;
    boolean stop = false;
    TextView view, txtECG, txtTemperature, txtPulseOx, temp4, txtECGStatus, txtHeartBeatStatus, txtTempStatus;
    GraphView ecgGraph;
    private double graph2LastXValue = 5d;
    private LineGraphSeries<DataPoint> series;
    private static final int MAX_ECG_VALUE=80; // <--80 BPM beats per minute
    private static int counter=1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle b = msg.getData();
            String key = b.getString("data");
            // String data = key.trim();
            String data = key;

            view.setText(data);
            Toast.makeText(context,"Received Data [" +counter + "] Data= " + data,Toast.LENGTH_SHORT).show();
            counter++;
            try {
                if (data.startsWith("*")) {

                    String str1, str2, str3, str4, str5, str6, str7;
                    str1 = data.substring(1, 4);
                    str2 = data.substring(4, 7);
                    str3 = data.substring(7, 10);
                    str4 = data.substring(10, 11);
                    str5 = data.substring(11, 12);
                    str6 = data.substring(12, 13);

                    if (str4.equals("1")) {
                        txtECGStatus.setText("");
                    } else {
                        txtECGStatus.setText("");
                    }

                    txtECG.setText("ECG VALUE : " + str1);
                    graph2LastXValue += 1d;
                    series.appendData(new DataPoint(graph2LastXValue, Double.parseDouble(str1)), true, MAX_ECG_VALUE);
                    txtTemperature.setText("TEMPERATURE VALUE :" + str3);
                    txtPulseOx.setText("PULSE OXIMETER VALUE :" + str2);
                    display.setText("Current[R]:" + str4);
                    display.setText("");

                    if (str4.equals("1")) {
                        txtECGStatus.setText("ECG ABNORMAL");
                    } else {
                        txtECGStatus.setText("ECG NORMAL");
                    }

                    if (str5.equals("1")) {
                        txtHeartBeatStatus.setText("HEART BEAT ABNORMAL");
                    } else {
                        txtHeartBeatStatus.setText("HEART BEAT NORMAL");
                    }

                    if (str6.equals("1")) {
                        txtTempStatus.setText("TEMPERATURE ABNORMAL");
                    } else {
                        txtTempStatus.setText("TEMPERATURE NORMAL");
                    }


                } else if (data.startsWith("+")) {
                    String m = data.substring(1, 4);
                    mo.setText(m);
                }


            } catch (Exception e) {
                Log.e(TAG, "Error Message Handler " + e);
            }


        }
    };
    private Button i1of, i1on, i2of, i2on, i3of, i3on, i4of, i4on;

    public static void updateMessageBox(String ip, final String simNumber) {
        try {
            // String ip1=ip.substring(1);
            UDP_Ser_IP.setText(ip);
            UDP_SERVER_IP = ip.substring(ip.indexOf("*") + 1);

            String v2 = UDP_SERVER_IP.replace("@", "");
            UDP_SERVER_IP = v2;
            Toast.makeText(context, UDP_SERVER_IP, Toast.LENGTH_SHORT).show();
            UDP_Ser_IP.setText("Device IP: " + v2);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(simNumber, null, "#" + SERVERIP + "$", null, null);
        } catch (NullPointerException e) {
            Log.e(TAG, "Error updating text box " + e);
        } catch (Exception e) {
            Log.e(TAG, "Error updating text box " + e);
        }

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
                            if (isIPv4) {
                                return sAddr;
                            }
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
        } catch (NullPointerException e) {
            Log.e(TAG, "Error getting ip address " + e);
        } catch (Exception e) {
            Log.e(TAG, "Error getting ip address " + e);
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        context = this;
        try {
            display = (TextView) findViewById(R.id.textView3);
            ecgGraph = (GraphView) findViewById(R.id.ecgGraph);
            txtECG = (TextView) findViewById(R.id.txtECG);
            txtTemperature = (TextView) findViewById(R.id.txtTemperature);
            txtPulseOx = (TextView) findViewById(R.id.txtPulseOx);
            txtECGStatus = (TextView) findViewById(R.id.txtECGStatus);
            txtHeartBeatStatus = (TextView) findViewById(R.id.txtHeartBeatStatus);
            txtTempStatus = (TextView) findViewById(R.id.txtTempStatus);
            display.setText("");
            txtECG.setText("ECG VALUE : " + "0.0");
            txtTemperature.setText("TEMPERATURE VALUE :" + "0.0");
            txtPulseOx.setText("PULSE OXIMETER VALUE :" + "0.0");


            txtECGStatus.setText("");
            txtHeartBeatStatus.setText("");
            txtTempStatus.setText("");
            UDP_Ser_IP = (TextView) findViewById(R.id.udpserver_ip);
            IP = (TextView) findViewById(R.id.ip);
            view = (TextView) findViewById(R.id.view);
            SERVERIP = getIPAddress(true);
            IP.setText("Mobile IP: " + SERVERIP);

            Intent intename = getIntent();
            if (intename.getSerializableExtra(Init.INTENT_IP_KEY) != null && intename.getSerializableExtra(Init.INTENT_PHONE_KEY) != null) {
                number = (String) intename.getSerializableExtra(Init.INTENT_IP_KEY);
                phone = (String) intename.getSerializableExtra(Init.INTENT_PHONE_KEY);
                Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_SHORT)
                        .show();

                String v2 = number.replace("@", "");
                UDP_Ser_IP.setText("Robo IP: " + number);
                updateMessageBox(number, phone);
                try {
                    datagramSocket = new DatagramSocket();
                } catch (SocketException e) {

                }
            }

            ecgGraph.setTitle("ECG");
            ecgGraph.getViewport().setScrollable(true); // enables horizontal scrolling
            ecgGraph.getViewport().setScrollableY(true); // enables vertical scrolling
            ecgGraph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
            ecgGraph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
            series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(0, 0)});
            ecgGraph.addSeries(series);
            Thread thread = new Thread(new Read_data());
            thread.start();
        } catch (NullPointerException e) {
            Log.e(TAG, "Error While initializing " + e);
        } catch (Exception e) {
            Log.e(TAG, "Error While initializing " + e);
        }
    }

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
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    @Override
    protected void onDestroy() {

        stop = true;
        super.onDestroy();
    }

    public void sendSMS(String phoneNo, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);

    }




    class Read_data implements Runnable {

        public void run() {

            try {
                datagramSocket = new DatagramSocket(6000);
                while (!stop) {
                    try {
                        String lText;
                        byte[] lMsg = new byte[1500];
                        DatagramPacket dp = new DatagramPacket(lMsg,
                                lMsg.length);
                        datagramSocket.receive(dp);
                        lText = new String(lMsg, 0, dp.getLength());

                        // view msg
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        b.putString("data", lText);
                        msg.setData(b);
                        handler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG,"Data gram is closed");
                datagramSocket.close();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error While Read_data " + e);
            } catch (Exception e) {
                Log.e(TAG, "Error While Read_data " + e);
            }
        }

    }

    /**
     * Dispatch onLowMemory() to all fragments.
     */
    @Override
    public void onLowMemory() {
        Log.e(TAG, "onLowMemory ");
        super.onLowMemory();
    }
}
