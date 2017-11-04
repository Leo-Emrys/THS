package com.example.lmachillot.ths;


import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by lmachillot on 13/03/17.
 */

public class Traitement {


    private long id;
    private String nom;
    private Hormone hormone;
    private int frequence; // nombre de jours d'intervalle
    private Date premiereprise;
    private Date date_renouvellement; // prochaine date prévue, en fonction de fréquence et dernière date
    private Stock stock;
    private Type type;
    private String dosage;


    public Traitement(long id, String nom, Hormone hormone, int frequence,
                      Date premiereprise, Date date_renouvellement, Stock stock, String dosage, Type type) {
        this.nom = nom;
        this.hormone = hormone;
        this.premiereprise = premiereprise;
        this.date_renouvellement=date_renouvellement;
        this.frequence = frequence;
        this.id=id;
        this.stock = stock;
        this.type = type;
        this.dosage=dosage;
    }

    public void calculeDateRenouv() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(this.premiereprise);
        cal.add(Calendar.DATE, frequence);
        this.date_renouvellement = cal.getTime();

        Log.d("date calculée =========", this.date_renouvellement+" ");

    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
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

    public Hormone getHormone() {
        return hormone;
    }

    public void setHormone(Hormone hormone) {
        this.hormone = hormone;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    public Date getPremiereprise() {
        return premiereprise;
    }

    public void setPremiereprise(Date premiereprise) {
        this.premiereprise = premiereprise;
    }

    public Date getDate_renouvellement() {
        return date_renouvellement;
    }

    public void setDate_renouvellement(Date date_renouvellement) {
        this.date_renouvellement = date_renouvellement;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Traitement{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", hormone=" + hormone +
                ", frequence=" + frequence +
                ", premiereprise=" + premiereprise +
                ", date_renouvellement=" + date_renouvellement +
                ", stock=" + stock +
                ", dosage=" + dosage +
                ", type=" + type +
                '}';
    }
}
