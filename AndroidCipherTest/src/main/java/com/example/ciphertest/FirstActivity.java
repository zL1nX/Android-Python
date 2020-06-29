package com.example.ciphertest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;


public class FirstActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_first);
        Button btn = findViewById(R.id.button);
        final EditText ServerAddr = findViewById(R.id.editTextTextPersonName2);
        final EditText ServerPort = findViewById(R.id.editTextTextPersonName3);

        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                final String ip = ServerAddr.getText().toString().trim();
                final int port = Integer.parseInt(ServerPort.getText().toString().trim());
                    new Thread(){
                        @Override
                        public void run() {
                            try{
                                Client client = new Client(ip, port, getBaseContext());
                                client.run();
                                client.stop();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

            }
        });

    }



}



/*public class SecondActivity extends AppCompatActivity {

    private String type;
    private String key;
    private Intent it;
    private int times;
    private int iterations = 100;
    private SymCypher cypherObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_second);
        Button encBtn = findViewById(R.id.button5);
        Button decBtn = findViewById(R.id.button6);
        Button backBtn = findViewById(R.id.button7);
        final CheckBox ck = findViewById(R.id.checkBox);
        final EditText show_plain = findViewById(R.id.editTextTextMultiLine);
        final EditText show_cypher = findViewById(R.id.editTextTextMultiLine2);

        try{
            it = getIntent();
            type = it.getStringExtra("type");
            key = it.getStringExtra("key");

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        encBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Do the encryption!");
                String plaintext = show_plain.getText().toString().trim();
                show_plain.setText("");
                if(plaintext.length() == 0){
                    Toast.makeText(SecondActivity.this, "Your cipher text is empty", Toast.LENGTH_LONG).show();
                }
                else {
                    times = ck.isChecked() ? iterations : 1;
                    cypherObj = new SymCypher(key, type, "enc", plaintext);
                    String res = cypherObj.run(times);
                    System.out.println(res);
                    show_cypher.setText(res);
                }
            }
        });

        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ciphertext = show_cypher.getText().toString().trim();
                show_cypher.setText("");
                if(ciphertext.length() == 0){
                    Toast.makeText(SecondActivity.this, "Your cipher text is empty", Toast.LENGTH_LONG).show();
                }
                else{
                    times = ck.isChecked() ? iterations : 1;
                    cypherObj = new SymCypher(key, type, "dec", ciphertext);
                    // do the decryption and get the plain
                    String res = cypherObj.run(times);
                    System.out.println(res);
                    show_plain.setText(res);
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "You can input your key again", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


}*/
