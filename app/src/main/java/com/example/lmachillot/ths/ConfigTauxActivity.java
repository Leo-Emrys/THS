package com.example.lmachillot.ths;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ConfigTauxActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_taux);

        TauxHormonalDAO tdao = new TauxHormonalDAO(this);
        tdao.open();
        List<TauxHormonal> taux = tdao.getTauxHormonaux();
        tdao.close();

        //si liste non vide ?

        //légende
        TableLayout table = (TableLayout) findViewById(R.id.tableedittaux);

        TableRow tr = new TableRow(this);
        tr.setBackgroundColor(Color.WHITE);
        TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 2, 0, 2);//2px right-margin
        tr.setLayoutParams(llp);


        //New Cell
        LinearLayout cell = new LinearLayout(this);
        cell.setBackgroundColor(Color.WHITE);
        TextView tv = new TextView(this);
        tv.setText("Hormone |");
        tv.setPadding(0, 0, 4, 3);
        cell.addView(tv);
        tr.addView(cell);

        cell = new LinearLayout(this);
        cell.setBackgroundColor(Color.WHITE);
        tv = new TextView(this);
        tv.setText(" Dosage |");
        tv.setPadding(0, 0, 4, 3);
        cell.addView(tv);
        tr.addView(cell);

        cell = new LinearLayout(this);
        cell.setBackgroundColor(Color.WHITE);
        tv = new TextView(this);
        tv.setText(" Date");
        tv.setPadding(0, 0, 4, 3);
        cell.addView(tv);
        tr.addView(cell);

        table.addView(tr);

        GregorianCalendar cal = new GregorianCalendar();
        EditText ed;

        for(TauxHormonal t : taux) {
            Log.e("taux +++++++++", t.toString());

            tr = new TableRow(this);
            tr.setBackgroundColor(Color.WHITE);
            llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 2, 0, 2);//2px right-margin
            tr.setLayoutParams(llp);

            cell = new LinearLayout(this);
            cell.setBackgroundColor(Color.WHITE);
            ed = new EditText(this);
            ed.setText(t.getHormone()+" |");
            ed.setPadding(0, 0, 4, 3);
            cell.addView(ed);
            tr.addView(cell);

            cell = new LinearLayout(this);
            cell.setBackgroundColor(Color.WHITE);
            ed = new EditText(this);
            ed.setText(t.getTaux()+" |");
            ed.setPadding(0, 0, 4, 3);
            cell.addView(ed);
            tr.addView(cell);

            Date date = t.getDate();


            cal.setTime(date);
            int dd = cal.get(Calendar.DAY_OF_WEEK);
            //int numjour = cal.get(Calendar.DAY_OF_MONTH);
            int nummois = cal.get(Calendar.MONTH);
            int annee = cal.get(Calendar.YEAR);

            String datestr = dd + "/" + (nummois+1) + "/" + annee;

            cell = new LinearLayout(this);
            cell.setBackgroundColor(Color.WHITE);
            ed = new EditText(this);
            ed.setText(datestr+" |");
            ed.setPadding(0, 0, 4, 3);
            cell.addView(ed);
            tr.addView(cell);

            table.addView(tr);
        }

        //TableLayout tl = (TableLayout) findViewById(R.id.tableedittaux);
/*
        TableRow tr_head = new TableRow(this);
        tr_head.setBackgroundColor(Color.GRAY);        // part1
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView label_hello = new TextView(this);
        label_hello.setText("HELLO");
        label_hello.setTextColor(Color.WHITE);          // part2
        label_hello.setPadding(5, 5, 5, 5);
        tr_head.addView(label_hello);// add the column to the table row here

        TextView label_android = new TextView(this);    // part3
        label_android.setText("ANDROID..!!"); // set the text for the header
        label_android.setTextColor(Color.WHITE); // set the color
        label_android.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_android); // add the column to the table row here

        tl.addView(tr_head);
*/


    }
}
