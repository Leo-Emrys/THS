package com.example.lmachillot.ths;

import java.util.Date;

/**
 * Created by lmachillot on 29/08/17.
 */

public class TauxHormonal {
    private long id;
    private String hormone;
    private double taux;
    private Date date;


    public TauxHormonal(long id, String hormone, double taux, Date date) {
        this.id = id;
        this.hormone = hormone;
        this.taux = taux;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getHormone() {
        return hormone;
    }

    public double getTaux() {
        return taux;
    }

    public Date getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHormone(String hormone) {
        this.hormone = hormone;
    }

    public void setTaux(double taux) {
        this.taux = taux;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TauxHormonal{" +
                "id=" + id +
                ", hormone=" + hormone +
                ", taux=" + taux +
                ", date=" + date.toString() +
                '}';
    }
}
