package com.example.kreatora3.mydemoapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.BitSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReferenceArray;


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
    BitSet dots;
    int mWidth;
    int mHeight;
    String mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.setWebViewClient(new WebViewClient() {

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                myWebView.loadUrl("file:///android_asset/static4.html");
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if (url.startsWith("mailto:")){
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse(url));
                    startActivity(emailIntent);
                    return true;
                }

                return false;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                return false;
            }


        }  );


        myWebView.loadUrl("http://formoexpress.ilweb.eu/");
//        myWebView.loadUrl("http://fasto.ilweb.eu/");

    }

    public void Scan() {
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

    public void print_image(final String message) {

        Thread t = new Thread() {
            public void run() {

                byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                convertBitmap(decodedByte);
                int offset = 0;
                byte widthLSB = (byte) (decodedByte.getWidth() & 0xFF);
                byte widthMSB = (byte) ((decodedByte.getWidth() >> 8) & 0xFF);

//                // COMMANDS
//                byte[] selectBitImageModeCommand = buildPOSCommand(SELECT_BIT_IMAGE_MODE, (byte) 33, widthLSB, widthMSB);
                byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, widthLSB, widthMSB};
                try {
                    if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                        OutputStream os = mBluetoothSocket.getOutputStream();
                        while (offset < decodedByte.getHeight()) {
                            os.write(SELECT_BIT_IMAGE_MODE);
                            for (int x = 0; x < decodedByte.getWidth(); ++x) {

                                for (int k = 0; k < 3; ++k) {

                                    byte slice = 0;
                                    for (int b = 0; b < 8; ++b) {
                                        int y = (((offset / 8) + k) * 8) + b;


                                        int i = (y * decodedByte.getWidth()) + x;
                                        boolean v = false;

                                        if (i < dots.length()) {
                                            v = dots.get(i);
                                        }
                                        slice |= (byte) ((v ? 1 : 0) << (7 - b));
                                    }
                                    os.write(slice);
                                }
                            }
                            offset += 24;
                            os.write(PrinterCommands.FEED_LINE);
                        }
                    }else{
                        Scan();
                    }

                } catch (Exception e) {

                }
            }

            ;

        };
        t.start();
    }

    public String convertBitmap(Bitmap inputBitmap) {

        mWidth = inputBitmap.getWidth();
        mHeight = inputBitmap.getHeight();

        convertArgbToGrayscale(inputBitmap, mWidth, mHeight);
        mStatus = "ok";
        return mStatus;

    }

    private void convertArgbToGrayscale(Bitmap bmpOriginal, int width,
                                        int height) {
        int pixel;
        int k = 0;
        int B = 0, G = 0, R = 0;
        dots = new BitSet();
        try {

            for (int x = 0; x < height; x++) {
                for (int y = 0; y < width; y++) {
                    // get one pixel color
                    pixel = bmpOriginal.getPixel(y, x);

                    // retrieve color of all channels
                    R = Color.red(pixel);
                    G = Color.green(pixel);
                    B = Color.blue(pixel);
                    // take conversion up to one single value by calculating
                    // pixel intensity.
                    R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                    // set bit into bitset, by calculating the pixel's luma
                    if (R < 127) {
                        dots.set(k);//this is the bitset that i'm printing
                    }
                    k++;

                }


            }


        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, e.toString());
        }
    }

    public void PrintImage(final String image) {
        Thread t = new Thread() {

            public void run() {
                Bitmap imageBit = BitmapFactory.decodeResource(getResources(), R.drawable.qr);
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
                    if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                        OutputStream os = mBluetoothSocket.getOutputStream();
                        byte[] decodedString = Base64.decode(message, Base64.DEFAULT);
                        os.write(PrinterCommands.SELECT_CYRILLIC_CHARACTER_CODE_TABLE);

                        os.write(decodedString);
                    } else {
                        Scan();
                    }

                } catch (Exception e) {
                    Log.e("Main", "Exe ", e);
                }
            }
        };
        t.start();

    }

    public void PrintWithoutDecoding(final String message) {
        Thread t = new Thread() {
            public void run() {
                try {
                    if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
                        OutputStream os = mBluetoothSocket.getOutputStream();

                        os.write(PrinterCommands.SELECT_CYRILLIC_CHARACTER_CODE_TABLE);

                        os.write(message.getBytes("CP866"));
                    } else {
                        Scan();
                    }

                } catch (Exception e) {
                    Log.e("Main", "Exe ", e);
                }
            }
        };
        t.start();

    }

    @Override
    public void onBackPressed() {
myWebView.loadUrl("http://formoexpress.ilweb.eu/");
    }

    public boolean IsConnected(){
        return mBluetoothSocket != null && mBluetoothSocket.isConnected();
    }
};

