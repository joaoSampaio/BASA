package pt.ulisboa.tecnico.basa.util;

import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.reflect.TypeToken;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;

import pt.ulisboa.tecnico.basa.Global;

/**
 * Created by Sampaio on 26/07/2016.
 */
public class Encryptor {

    private static final int KEY_LENGTH = 256;
    private static final String DEFAULT_PASSWORD_SALT = "g7n8@m!";

    private static String bytes2Hex(byte[] bytes) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < bytes.length; n++) {
            stmp = (Integer.toHexString(bytes[n] & 0XFF));
            if (stmp.length() == 1) {
                hs += "0" + stmp;
            } else {
                hs += stmp;
            }
        }
        return hs.toLowerCase(Locale.ENGLISH);
    }


    public static String getSHA(String text) {
        String sha = "";
        if (TextUtils.isEmpty(text)) {
            return sha;
        }
        MessageDigest shaDigest = null;
        try {
            shaDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (shaDigest != null) {
            String salt = getSalt();
            text = salt + text + salt;
            byte[] textBytes = text.getBytes();
            shaDigest.update(textBytes, 0, text.length());
            byte[] shahash = shaDigest.digest();
            return bytes2Hex(shahash);
        }

        return null;
    }

    private static String getSalt(){
        String salt = new ModelCache<String>().loadModel(new TypeToken<String>(){}.getType(), Global.KEY_SALT);
        if(salt == null){
            salt = generateSalt();
            new ModelCache<String>().saveModel(salt, Global.KEY_SALT);
        }
        return salt;
    }

    private static String generateSalt() {
        byte[] salt = new byte[KEY_LENGTH];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(System.currentTimeMillis());
            sr.nextBytes(salt);
            return Arrays.toString(salt);
        } catch (Exception e) {
            salt = DEFAULT_PASSWORD_SALT.getBytes();
        }
        return Base64.encodeToString(salt, Base64.DEFAULT);
    }


}
