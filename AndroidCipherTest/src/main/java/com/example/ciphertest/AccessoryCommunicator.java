package com.example.ciphertest;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AccessoryCommunicator {

    private static final String TAG = AccessoryCommunicator.class.getSimpleName();
    private UsbManager usbManager;
    private Context context;
    private Handler sendHandler;
    private ParcelFileDescriptor fileDescriptor;
    private FileInputStream inStream;
    private FileOutputStream outStream;
    private boolean running;
    private PendingIntent mPermissionIntent;
    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    public AccessoryCommunicator(final Context context) {
        this.context = context;

        usbManager = (UsbManager) this.context.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        registerReceiver(mUsbReceiver, filter);
        final UsbAccessory[] accessoryList = usbManager.getAccessoryList();

        if (accessoryList == null || accessoryList.length == 0) {
            onError("no accessory found");
        } else {
            openAccessory(accessoryList[0]);
        }
    }

    public void send(byte[] payload) throws IOException {
        outStream.write(payload);
        System.out.println("write already");
    }

    public void send(int payload) throws IOException {
        outStream.write(payload);
        System.out.println("write already");

    }

    public abstract void onReceive(final byte[] payload, final int length);

    public abstract void onError(String msg);

    public abstract void onConnected();

    public abstract void onDisconnected();



    private void openAccessory(UsbAccessory accessory) {
        usbManager.requestPermission(accessory, mPermissionIntent); // permission required, this is necessary

        fileDescriptor = usbManager.openAccessory(accessory);
        if (fileDescriptor != null) {

            FileDescriptor fd = fileDescriptor.getFileDescriptor();
            inStream = new FileInputStream(fd);
            outStream = new FileOutputStream(fd);

            onConnected();
        } else {
            onError("could not connect");
        }
    }

    public void closeAccessory() {
        running = false;

        try {
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileDescriptor = null;
        }

        onDisconnected();
    }

}
