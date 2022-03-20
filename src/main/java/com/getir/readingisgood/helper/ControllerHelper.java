package com.getir.readingisgood.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ControllerHelper {

    public static Map<String, Object> getQueryMap(Object... tuples) {
        Map<String, Object> map = new HashMap<>();
        int length = tuples.length;
        for (int i = 0; i < length; i++) {
            map.put(String.valueOf(tuples[i]), i == length - 1 ? null : tuples[++i]);
        }
        return map;
    }

    // hash the password with a mixer string
    public static String getHashedPassword(String password) {
        password += "reigo";
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(password.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
