package com.example.lmachillot.ths;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.round;

public class SuiviHormonalActivity extends MenuActivity {

    private ArrayList<String> mColors = new ArrayList<String>(Arrays.asList(new String[] {"#A970B6","#e15258","#FA7141","#53bbb4", "#FCC93A", "#838CC7", "#888888", "#7d669e", "#996536"}));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suivi_hormonal);

        GregorianCalendar cal = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        //récupérer taux
        TauxHormonalDAO thdao = new TauxHormonalDAO(this);
        thdao.open();
        /// DEBUG

        //thdao.supprimerTousLesTaux();
      /*  thdao.ajouterTaux(new TauxHormonal(1, Hormone.œstrogènes.name(), 0.5, new Date()));
        thdao.ajouterTaux(new TauxHormonal(1, Hormone.progestérone.name(), 0.45, new Date()));
        thdao.ajouterTaux(new TauxHormonal(1, Hormone.testostérone.name(), 2, new Date()));

        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 3);
        Date d2 = cal.getTime();
//        thdao.ajouterTaux(new TauxHormonal(1, Hormone.œstrogènes, 0.2, d2));
       thdao.ajouterTaux(new TauxHormonal(1, Hormone.progestérone.name(), 0.42, d2));
        thdao.ajouterTaux(new TauxHormonal(1, Hormone.testostérone.name(), 0.4, d2));
        cal.add(Calendar.MONTH, 6);
        d2 = cal.getTime();
  //     thdao.ajouterTaux(new TauxHormonal(1, Hormone.œstrogènes, 0.04, d2));
        thdao.ajouterTaux(new TauxHormonal(1, Hormone.progestérone.name(), 0.35, d2));
        thdao.ajouterTaux(new TauxHormonal(1, Hormone.testostérone.name(), 7.5, d2));

        cal.add(Calendar.MONTH, 6);
        d2 = cal.getTime();
        thdao.ajouterTaux(new TauxHormonal(1, Hormone.testostérone.name(), 8, d2));*/

        ///

        List<TauxHormonal> liste = thdao.getTauxHormonaux();
        thdao.close();


        // DEBUG /////////////////////////////////////////
        for(TauxHormonal th : liste) {
            Log.e("++++++++++++++++++++++", th.toString());
            Log.e("date format", th.getDate().getTime()+"");
        }


        ///////////////////////////////////////////

        if(liste.size()>0) {
            // ordonner en fonction de l'hormone
            Map<String, ArrayList<TauxHormonal>> map = new HashMap<>();
            for(TauxHormonal th : liste) {
                if (map.get(th.getHormone()) == null) {
                    map.put(th.getHormone(), new ArrayList<TauxHormonal>());
                }
                map.get(th.getHormone()).add(th);
            }

            Log.e("???????????????????", map.size()+"");
             //DEBUG
            for(Map.Entry<String, ArrayList<TauxHormonal>> entry : map.entrySet()) {
                Log.e("clé ++++++++++", entry.getKey());
                for(TauxHormonal th : entry.getValue()) {
                    Log.d("liste 8888888888888888", th.toString());
                }
            }
            /////////////////

            //Afficher graph
            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.setVisibility(View.VISIBLE);

            for(Map.Entry<String, ArrayList<TauxHormonal>> entry : map.entrySet()) {

                List<DataPoint> points = new ArrayList<>();
                for(TauxHormonal th : entry.getValue()) {
                    points.add(new DataPoint(th.getDate(), th.getTaux()));
                }

                DataPoint[] dataPoints = new DataPoint[points.size()];
                for (int i = 0; i < points.size(); i++) {
                    dataPoints[i] = new DataPoint(points.get(i).getX(),points.get(i).getY());
                }

                if(entry.getValue().size()>1) { // ligne
                  //  affichergraph=true;
                    // CONSTRUCTION GRAPH

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                    series.setTitle(entry.getKey());
                    series.setColor(assignColor(entry.getKey()));
                    //dessiner les points
                    series.setDrawDataPoints(true);
                    series.setDataPointsRadius(10);
                    //afficher
                    graph.addSeries(series);

                } else { // point

                    PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dataPoints);
                    series.setTitle(entry.getKey());
                    series.setColor(assignColor(entry.getKey()));

                    //forme des points
                    series.setShape(PointsGraphSeries.Shape.POINT);
                    series.setSize(12);
                    //afficher
                    graph.addSeries(series);

                }
            }

            /*graph.getViewport().setYAxisBoundsManual(true);
               graph.getViewport().setMinY(0.0);*/

            //récupérer première et dernière date
            Date premdate = liste.get(0).getDate();
            Date derndate = liste.get(liste.size()-1).getDate();
            // afficher dates repères sur l'axe des X
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(SuiviHormonalActivity.this));
            if(premdate.equals(derndate))
                graph.getGridLabelRenderer().setNumHorizontalLabels(1);
            else
                graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setMinX(premdate.getTime());
            graph.getViewport().setMaxX(derndate.getTime());
            graph.getViewport().setXAxisBoundsManual(true);


            //légende
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);


            // as we use dates as labels, the human rounding to nice readable numbers
            // is not necessary
            graph.getGridLabelRenderer().setHumanRounding(false);

        } else { // Pas de données
                TextView texte = (TextView) findViewById(R.id.pasdegraphique);
                texte.setVisibility(View.VISIBLE);
        }
    }

    private int assignColor(String key) {
        switch(key) {
            case "testostérone" :
                return Color.BLUE;
            case "œstrogènes" :
               return Color.MAGENTA;
            case "progestérone" :
                return Color.GREEN;
        }
        if(mColors.size()>0) {
            int random = (int) (Math.random()*mColors.size());
            int color = Color.parseColor(mColors.get(random));
            mColors.remove(random);
            return color;
        } else {
            return Color.CYAN;
        }

    }

    public void ajouterDosage(View view) {
        Intent intent = new Intent(SuiviHormonalActivity.this, NewTauxHormonalActivity.class);
        finish();
        startActivity(intent);
    }


    public void editerDosages(View view) {
        Intent intent = new Intent(SuiviHormonalActivity.this, ConfigTauxActivity.class);
        finish();
        startActivity(intent);
    }
}
