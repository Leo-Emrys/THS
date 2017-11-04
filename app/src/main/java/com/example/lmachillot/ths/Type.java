package com.example.lmachillot.ths;

/**
 * Created by lmachillot on 13/03/17.
 */

public enum Type {

    injection("injection", 'f'), gel("application", 'f'), crème("application", 'f'), comprimé("prise", 'f'), patch("renouvellement", 'm'), implant("renouvellement", 'm');


    private final String denomination;
    private final char accord;

    Type(String denomination, char accord) {
        this.denomination=denomination;
        this.accord=accord;
    }

    public String getDenom() {
        return this.denomination;
    }

    public char getAccord() { return this.accord; }



}
