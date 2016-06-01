package com.example.kreatora3.mydemoapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity implements Runnable {


    private WebView myWebView;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;
    protected static final String TAG = "TAG";
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private ProgressDialog mBluetoothConnectProgressDialog;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.loadUrl("file:///android_asset/index.html");
    }

   public void Scan(){
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       /* this stands for the context of the activity*/
    if (mBluetoothAdapter == null) {
        Toast.makeText(this, "No bluetooth on this device", Toast.LENGTH_SHORT).show();
    }

    if (!mBluetoothAdapter.isEnabled()) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
        ListPairedDevices();
        Intent connectIntent = new Intent(MainActivity.this,
                DeviceListActivity.class);
        startActivityForResult(connectIntent,
                REQUEST_CONNECT_DEVICE);
    }
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONNECT_DEVICE) {
            if (resultCode == RESULT_OK) {
                Bundle mExtra = data.getExtras();
                String mDeviceAddress = mExtra.getString("DeviceAddress");
                Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                        "Connecting...", mBluetoothDevice.getName() + " : "
                                + mBluetoothDevice.getAddress(), true, false);
                Thread mBlutoothConnectThread = new Thread(this);
                mBlutoothConnectThread.start();
            }
        }
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                ListPairedDevices();
                Intent connectIntent = new Intent(MainActivity.this,
                        DeviceListActivity.class);
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
            } else {
                Toast.makeText(MainActivity.this, "Message", Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();

        } catch (IOException ex) {

        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Device Connected", Toast.LENGTH_LONG).show();
        }
    };

    public void PrintExample() {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    String BILL = "";

                    BILL = "\nInvoice No: ABCDEF28060000005" + "    "
                            + "04-08-2011\n";
                    BILL = BILL
                            + "-----------------------------------------";
                    BILL = BILL + "\n\n";
                    BILL = BILL + "Total Qty:" + "      " + "2.0\n";
                    BILL = BILL + "Total Value:" + "     "
                            + "17625.0\n";
                    BILL = BILL
                            + "-----------------------------------------\n";
                    os.write(BILL.getBytes());

                } catch (Exception e) {
                    Log.e("Main", "Exe ", e);
                }
            }
        };
        t.start();
    }
public void print_image(String message){
    byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    String a = "";
}
    public  void PrintImage(final String image){
    Thread t = new Thread() {

        public void run() {
            Bitmap imageBit = BitmapFactory.decodeResource(getResources(),R.drawable.qr);
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            imageBit.compress(Bitmap.CompressFormat.JPEG, 0, blob);
            byte[] bitmapdata = blob.toByteArray();

            try {
                OutputStream os = mBluetoothSocket.getOutputStream();


                byte[] data = Base64.decode(image, Base64.DEFAULT);

               os.write(data);
            } catch (Exception e) {
                Log.e("Main", "Exe ", e);
            }
        }
    };
        t.start();
}
    public void Print(final String message) {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket.getOutputStream();
                    os.write(message.getBytes());

                } catch (Exception e) {

                    Log.e("Main", "Exe ", e);
                }
            }
        };
        t.start();

    }
};

