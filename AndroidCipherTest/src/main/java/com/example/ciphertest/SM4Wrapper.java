package com.example.ciphertest;

import android.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SM4Wrapper {

    static{
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        // the old BC provider does not support the GM suite

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null){
            Security.addProvider(new BouncyCastleProvider()); // add our new BC provider
        }
    }

    private String TextToCode;
    private String password;

    private static final int PswdIterations = 65536;
    private static final int KeySize = 128;
    private static final String SM4Salt = "Salt";
    private static final String SecretKeyInstance = "PBKDF2WithHmacSHA1";
    public static final String CipherInstance = "SM4/ECB/NoPadding";

    /**
     * Constructor : initialize the SM4 instance
     * @param password user input used for deriving the real 128bit key
     *
     */
    public SM4Wrapper(String password, String TextToCode){
        this.password = password;
        this.TextToCode = TextToCode;
    }

    /**
     * KeyDerivation : generate the (deterministic) secret key from the password
     * @param password user input
     * @return the raw key byte array
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */

    private static byte[] KeyDerivation(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SecretKeyInstance);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), SM4Salt.getBytes(), PswdIterations, KeySize);
        SecretKey tmp = factory.generateSecret(spec);
        return tmp.getEncoded();
    }

    /**
     * GenerateCipher : initialize the block cipher instance
     * @param mode encrypt or decrypt under ECB or CBC mode with or without padding
     * @param password user input
     * @return initialized block cipher instance
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    private static Cipher GenerateCipher(int mode, String password)
            throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance(CipherInstance, BouncyCastleProvider.PROVIDER_NAME);
        SecretKeySpec skeySpec = new SecretKeySpec(KeyDerivation(password), "SM4");
        cipher.init(mode, skeySpec);
        return cipher;
    }

    /**
     * SM4 encryption
     * @param password
     * @param textToEncrypt (plain text)
     * @return cipher text (base64 encoded)
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeySpecException
     */

    public static String encrypt(String password, String textToEncrypt)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException, InvalidKeySpecException {
        Cipher cipher = GenerateCipher(Cipher.ENCRYPT_MODE, password);
        byte[] encrypted = cipher.doFinal(textToEncrypt.getBytes());
        return bytesToHex(encrypted);
    }

    /**
     * SM4 decryption
     * @param password
     * @param textToDecrypt (base64 encoded cipher text)
     * @return plain text
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeySpecException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String password, String textToDecrypt)
            throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeySpecException, UnsupportedEncodingException {
        Cipher cipher = GenerateCipher(Cipher.DECRYPT_MODE, password);
        byte[] encryted_bytes = HexToByteArray(textToDecrypt);
        byte[] decrypted = cipher.doFinal(encryted_bytes);
        return new String(decrypted, "UTF-8");
    }

    public String getRandomString(int len) {
        String str = "abcdef0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int number = random.nextInt(15);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static byte[] HexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j++] =(byte)Integer.parseInt(inHex.substring(i,i+2),16);
        }
        return result;
    }

    /**
     *
     * @param mode encryption or decryption
     * @return the corresponding result string
     */
    public String execute(String mode){
        String res = "";
        try{
            if(mode.equals("enc")){
                res = encrypt(password, TextToCode);
            }
            else if(mode.equals("dec")){
                res = decrypt(password, TextToCode);
            }
        }
        catch (Exception e){
            System.out.println("Something wrong.");
            e.printStackTrace();
            return null;
        }
        return res;
    }
}
