package pt.ulisboa.tecnico.mybasaclient.util;

import android.graphics.Color;

import java.security.SecureRandom;

public class NiceColor {

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private static final int LEN = 6;

    public static int randomColor(){

        StringBuilder sb = new StringBuilder( LEN );
        for( int i = 0; i < LEN; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        String token = sb.toString();
        return betterNiceColor(token);

    }

    public static int betterNiceColor(String name)
    {
        double goldenRatioConj = 0.618033988749895;
//        float hue = new Random().nextInt(360);
        float hue = nameToValue(name);
        hue += (goldenRatioConj*360);
        hue = hue % 360;

        int c = Color.HSVToColor(new float[]{hue, 0.5f, 0.95f});
        return c;
    }

    private static int nameToValue(String name){
        name = name.replace(" ", "");
        char[] c = name.toCharArray();
        int value = 0;
        for (Character ss : c)
            value += (ss - 'a' + 1);

        value = value % 360;
        return value;
    }


}