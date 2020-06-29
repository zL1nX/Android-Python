package com.example.ciphertest;

import android.content.Context;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;


public class Client {

    private final Socket mSocket;
    private String password;
    public int pwdLen = 6;
    private Context context;
    private AccessoryCommunicator communicator;


    public Client(String host, int port, final Context context) throws IOException {
        // 创建 socket 并连接服务器
        mSocket = new Socket(host, port);
        password = getRandomString(pwdLen);
        this.context = context;
        this.communicator = new AccessoryCommunicator(context) {
            @Override
            public void onReceive(byte[] payload, int length) {
                System.out.println("On receive");
            }

            @Override
            public void onError(String msg) {
                System.out.println("On error： " + msg);

            }

            @Override
            public void onConnected() {
                System.out.println("On connected");

            }

            @Override
            public void onDisconnected() {
                System.out.println("On disconnected");

            }
        };
    }


    public String getRandomString(int len) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public void run() throws IOException {
        // 和服务端进行通信
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
        BufferedReader msgget = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        boolean stop = false;
        System.out.println("client here");
        out.write("200");
        out.flush();

        while(true){
            System.out.println("in main loop");
            String msg = "";
            String temp = msgget.readLine();
            System.out.println("temp " + temp);
            String[] server_msg = temp.split(";");
            System.out.println("server msg: " + Arrays.toString(server_msg));

            if(server_msg[0].equals("110")){
                msg = GenerateClientMsg("210", null);
                stop = true;
            }
            else if(server_msg[0].equals("100")){
                msg = GenerateClientMsg("201", server_msg);
                System.out.println("client sending: " + msg);
            }
            SystemClock.sleep(500);
            out.write(msg);
            out.flush(); // required

            if(stop){
                System.out.println("client good bye");
                out.close();
                msgget.close();
                break;
            }
        }
    }

    private String GenerateClientMsg(String mode, String[] params) throws IOException {
        StringBuilder msg = new StringBuilder();
        if(mode.equals("210")){
            return "210";
        }
        else if(mode.equals("201")){
            msg.append(mode);
            SymCypher cypherObj = new SymCypher(password, params[1], params[2], params[3], context);
            msg.append(';');
            communicator.send(1);  // this required the pc server sets up the accessory mode

            SystemClock.sleep(100); // manually scheduled
            String temp = cypherObj.run();
            SystemClock.sleep(100);

            communicator.send(0);
            msg.append(temp);
        }
        return msg.toString();
    }

    public void stop() throws IOException {
        communicator.closeAccessory();
        mSocket.close();
    }

    protected void finalize() throws IOException {
        if(!mSocket.isClosed())
            mSocket.close();
    }

}
