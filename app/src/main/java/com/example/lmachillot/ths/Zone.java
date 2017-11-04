package com.example.lmachillot.ths;

/**
 * Created by lmachillot on 13/03/17.
 */

public class Zone {
    private long id;
    private String intitule;
    private String cote;

    public Zone(long id, String intitule, String cote) {
        this.id = id;
        this.intitule = intitule;
        this.cote = cote;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id=id; }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getCote() {
        return cote;
    }

    public void setCote(String cote) {
        this.cote = cote;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id=" + id +
                ", intitule='" + intitule + '\'' +
                ", cote='" + cote + '\'' +
                '}';
    }
}
