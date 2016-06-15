package com.example.kreatora3.mydemoapplication;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kreatora3 on 4/30/2016.
 */
public class WebAppInterface {
    Activity mContext;

    /**
     * Instantiate the interface and set the context
     */
    WebAppInterface(Activity c) {
        mContext = c;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void PrintInvoice(String text) {
//        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        ((MainActivity) mContext).Print(text);
    }

    @JavascriptInterface
    public void ScanForPrinters() {
        ((MainActivity)mContext).Scan();
    }
    @JavascriptInterface
    public  void  print_image(String image){
        ((MainActivity)mContext).print_image(image);
    }

    @JavascriptInterface
    public boolean check_connectionStatus(){
      return  ((MainActivity)mContext).IsConnected();
    }
}
