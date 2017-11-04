package com.example.lmachillot.ths;

import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * Created by Leonard on 26/03/2017.
 */

public class Stock {
    private long id;
    private Date datestock;
    private int duree;

    public Stock(long id, Date datestock, int duree) {
        this.id = id;
        this.datestock = datestock;
        this.duree = duree;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDatestock() {
        return datestock;
    }

    public void setDatestock(Date datestock) {
        this.datestock = datestock;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", datestock=" + datestock +
                ", duree=" + duree +
                '}';
    }

    public long enregistrerBD(Context context) {
        StockDAO sdao = new StockDAO(context);
        sdao.open();
        long res = sdao.ajouterStock(this);
        sdao.close();
        return res;
    }

    public String getTraitementsString(Stock stock, Context context) {
        String noms = "";
        TC_StockTraitementDAO stdao = new TC_StockTraitementDAO(context);
        stdao.open();
        List<Long> idts = stdao.getIdTraitementsDeStock(stock.getId());
        stdao.close();

        TraitementDAO tdao = new TraitementDAO(context);
        tdao.open();
        if(idts.size()>0) {
            int cmpt = 0;
            for(Long id : idts) {
                if(cmpt>0)
                    noms+=" / ";
                noms+=tdao.getNomTraitement(id);
                cmpt++;
            }
        }
        tdao.close();
        return noms;
    }
}
