package com.example.lmachillot.ths;

/**
 * Created by lmachillot on 23/03/17.
 */

public class RappelPref {

    private long id;
    private int delai;
    private int heure;
    private Traitement traitement;
    private Ordonnance ordonnance;
    private Stock stock;

    public RappelPref(long id, int delai, int heure, Traitement traitement, Ordonnance ordonnance, Stock stock) {
        this.id = id;
        this.delai = delai;
        this.traitement = traitement;
        this.ordonnance=ordonnance;
        this.stock=stock;
        this.heure=heure;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDelai() {
        return delai;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }

    public int getHeure() {
        return heure;
    }

    public void setHeure(int heure) {
        this.heure = heure;
    }

    public Traitement getTraitement() {
        return traitement;
    }

    public void setTraitement(Traitement traitement) {
        this.traitement = traitement;
    }

    public Ordonnance getOrdonnance() {
        return ordonnance;
    }

    public void setOrdonnance(Ordonnance ordonnance) {
        this.ordonnance = ordonnance;
    }

    public Stock getStock() { return stock; }

    public void setStock(Stock stock) { this.stock = stock; }

    @Override
    public String toString() {
        if(traitement!=null) {
            return "RappelPref{" +
                    "id=" + id +
                    ", delai=" + delai +
                    ", traitement=" + traitement.getId() + " ("+traitement.getNom()+")"+
                    '}';
        } else if(ordonnance!=null) {
            return "RappelPref{" +
                    "id=" + id +
                    ", delai=" + delai +
                    ", ordonnance=" + ordonnance.getId() + " ("+ordonnance.getNom()+")"+
                    '}';

        } else {
            return "RappelPref{" +
                    "id=" + id +
                    ", delai=" + delai +
                    ", stock=" + stock.getId()+
                    '}';
        }

    }
}
