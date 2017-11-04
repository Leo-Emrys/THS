package com.example.lmachillot.ths;

import android.content.Context;

import java.util.Date;

/**
 * Created by lmachillot on 13/03/17.
 */

public class Ordonnance {

    private long id;
    private String nom;
    private Date dateordo;
    private int duree;


    public Ordonnance(long id, String nom, Date dateordo, int duree) {
        this.id = id;
        this.nom = nom;
        this.dateordo = dateordo;
        this.duree = duree;
    }

    public long enregistrerBD(Context context) {
        OrdonnanceDAO odao = new OrdonnanceDAO(context);
        odao.open();
        long res = odao.ajouterOrdonnance(this);
        odao.close();
        return res;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateordo() {
        return dateordo;
    }

    public void setDateordo(Date dateordo) {
        this.dateordo = dateordo;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    @Override
    public String toString() {
        return "Ordonnance{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", dateordo=" + dateordo +
                ", duree=" + duree +
                '}';
    }
}
