package com.example.lmachillot.ths;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by lmachillot on 13/03/17.
 */

public class Prise {

    private long id;
    private Date dateprise;
    private Traitement traitement;
    private Zone zone;

    public Prise(long id, Date dateprise, Traitement traitement, Zone zone) {
        this.id=id;
        this.dateprise = dateprise;
        this.traitement = traitement;
        this.zone=zone;

        // changer la date de renouvellement du traitement en fonction de la date de la prise et de la fréquence du traitement
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(this.dateprise);
        cal.add(Calendar.DATE, traitement.getFrequence());
        traitement.setDate_renouvellement(cal.getTime());


    }

    public long enregistrerBD(Context context) {
        PriseDAO pdao = new PriseDAO(context);
        pdao.open();
        long res = pdao.ajouterPrise(this);
        pdao.close();
        return res;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Date getDateprise() {
        return dateprise;
    }

    public void setDateprise(Date dateprise) {
        this.dateprise = dateprise;
    }

    public Traitement getTraitement() {
        return traitement;
    }

    public void setTraitement(Traitement traitement) {
        this.traitement = traitement;
    }

    @Override
    public String toString() {
        return "Prise{" +
                "id=" + id +
                ", dateprise=" + dateprise +
                ", traitement=" + traitement +
                ", zone=" + zone +
                '}';
    }
}
