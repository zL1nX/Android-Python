package com.example.ciphertest;

import android.content.Context;

public class SymCypher {
    private String strKey;
    private String type;
    private String input;
    private String[] output;
    private String mode;
    private String text;

    private AESWrapper aesWrapperObj;
    private SM4Wrapper sm4WrapperObj;
    private Context context;


    public SymCypher(String strKey, String type, String mode, String text, final Context context){
        this.strKey = strKey;
        this.type = type;
        this.mode = mode;
        this.text = text;
        this.context = context;

    }

    public String run(){
        String result = null;
        if(type.equals("aes")){
            aesWrapperObj = new AESWrapper(strKey, text);
            result = aesWrapperObj.execute(mode);

        }
        else if(type.equals("sm4")){
            sm4WrapperObj = new SM4Wrapper(strKey, text);
            result = sm4WrapperObj.execute(mode);

        }
        System.out.println(result);
        return result;
    }
}
