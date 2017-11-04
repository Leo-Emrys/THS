package com.example.lmachillot.ths;

/**
 * Created by lmachillot on 15/03/17.
 */

public class TcTraitementZone {
    private long idtraitement;
    private long idzone;
    private int frequence;

    public TcTraitementZone(long idtraitement, long idzone, int frequence) {
        this.idtraitement = idtraitement;
        this.idzone = idzone;
        this.frequence = frequence;
    }

    public long getIdtraitement() {
        return idtraitement;
    }

    public void setIdtraitement(long idtraitement) {
        this.idtraitement = idtraitement;
    }

    public long getIdzone() {
        return idzone;
    }

    public void setIdzone(long idzone) {
        this.idzone = idzone;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    @Override
    public String toString() {
        return "TcTraitementZone{" +
                "idtraitement=" + idtraitement +
                ", idzone=" + idzone +
                ", frequence=" + frequence +
                '}';
    }
}
