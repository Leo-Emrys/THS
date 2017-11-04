package com.example.lmachillot.ths;

import android.content.Context;

import java.util.Date;

/**
 * Created by lmachillot on 13/03/17.
 */

public class Rappel {

    private long id;
    private String objet;
    private Date daterappel;
    private int heure;
    private int delai;
    private long idtraitement;
    private long idordonnance;
    private long idstock;

    public Rappel(long id, String objet, Date daterappel, int heure, int delai, long idtraitement, long idordonnance, long idstock) {
        this.id = id;
        this.daterappel = daterappel;
        this.objet=objet;
        this.heure=heure;
        this.delai=delai;
        this.idtraitement=idtraitement;
        this.idordonnance=idordonnance;
        this.idstock=idstock;
    }

    public long enregistrerBD(Context context) {
        RappelDAO rdao = new RappelDAO(context);
        rdao.open();
        long result = rdao.ajouterRappel(this);
        rdao.close();
        return result;
    }

    public int getDelai() {
        return delai;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDaterappel() {
        return daterappel;
    }

    public void setDaterappel(Date daterappel) {
        this.daterappel = daterappel;
    }

    public int getHeure() {
        return heure;
    }

    public void setHeure(int heure) {
        this.heure = heure;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public long getIdtraitement() {
        return idtraitement;
    }

    public void setIdtraitement(long idtraitement) {
        this.idtraitement = idtraitement;
    }

    public long getIdordonnance() {
        return idordonnance;
    }

    public void setIdordonnance(long idordonnance) {
        this.idordonnance = idordonnance;
    }

    public long getIdstock() {
        return idstock;
    }

    public void setIdstock(long idstock) {
        this.idstock = idstock;
    }

    @Override
    public String toString() {
        return "Rappel{" +
                "id=" + id +
                ", objet='" + objet + '\'' +
                ", daterappel=" + daterappel +
                ", heure=" + heure +
                ", delai=" + delai +
                ", idtraitement=" + idtraitement +
                ", idordonnance=" + idordonnance +
                ", idstock=" + idstock +
                '}';
    }
}
