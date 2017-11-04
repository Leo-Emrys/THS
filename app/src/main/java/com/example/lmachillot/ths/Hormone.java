package com.example.lmachillot.ths;

/**
 * Created by lmachillot on 13/03/17.
 */

public enum Hormone {
    testostérone, œstrogènes, anti_androgènes, progestérone;

    public static boolean contains(String test) {
        for(Hormone h : Hormone.values()) {
            if(h.name().equals(test))
                return true;
        }
        return false;
    }
}
