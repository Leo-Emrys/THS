package com.example.lmachillot.ths;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.menu_about:
                intent = new Intent(MenuActivity.this, RessourcesActivity.class);
                finish();
                startActivity(intent);
                return true;

            case R.id.menu_prochainedate:
                intent = new Intent(MenuActivity.this, ProchaineDateActivity.class);
                finish();
                startActivity(intent);
                return true;

            case R.id.menu_ordonnance:
                intent = new Intent(MenuActivity.this, ConfigOrdoActivity.class);
                finish();
                startActivity(intent);
                return true;

            case R.id.menu_newtraitement:
                intent = new Intent(MenuActivity.this, NewTraitementActivity.class);
                finish();
                startActivity(intent);
                return true;

            case R.id.menu_stocks:
                intent = new Intent(MenuActivity.this, ConfigStockActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_dosages:
                intent = new Intent(MenuActivity.this, SuiviHormonalActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
