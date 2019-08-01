package com.adhityakuncoro.wordpress.squidscan.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adhityakuncoro.wordpress.squidscan.Model.Code;
import com.adhityakuncoro.wordpress.squidscan.Adapter.Listitem;
import com.adhityakuncoro.wordpress.squidscan.Adapter.AdapterViewHolder;
import com.adhityakuncoro.wordpress.squidscan.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    IntentIntegrator scan;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Listitem> listItems;


    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean beep = sharedPref.getBoolean("beep", true);
        Boolean frontCamera = sharedPref.getBoolean("frontCamera", false);

        int camId;
            if (frontCamera == false)
                camId = 0;
        else{
            camId = 1;
        }

        sharedPref.registerOnSharedPreferenceChangeListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();
        adapter = new AdapterViewHolder(listItems, this);
        recyclerView.setAdapter(adapter);
        CardView cardView = (CardView) findViewById(R.id.cardView);

        Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
        try {
            // Iterate Bunnys
            QueryResultIterable<Code> itr = cupboard().withCursor(codes).iterate(Code.class);
            for (Code bunny : itr) {
                // do something with bunny
                Listitem listItem = new Listitem(bunny._id, bunny.name, bunny.type);
                listItems.add(listItem);
                adapter = new AdapterViewHolder(listItems, this);
                recyclerView.setAdapter(adapter);
            }
        } finally {
            // close the cursor
            codes.close();
        }
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                Listitem item = listItems.get(position);

                cupboard().withDatabase(db).delete(Code.class, item.get_Id());
                listItems.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, listItems.size());
            }}).attachToRecyclerView(recyclerView);

        scan = new IntentIntegrator(this);
        scan.setBeepEnabled(beep);
        scan.setCameraId(camId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan.initiateScan();
            }
        });

        ImageView imgAbout = findViewById(R.id.action_about);
        imgAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });

        ImageView imgSettings = findViewById(R.id.action_settings);
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_clear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clearAll) {
            PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
            try {
                if (codes.getCount() > 0) {
                    cupboard().withDatabase(db).delete(Code.class, null);
                    listItems.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    return true;
                }
            } finally {
                codes.close();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.main), "Result Not Found", Snackbar.LENGTH_LONG);

                snackbar.show();
            } else {

                Code codeObj = new Code(result.getContents(), result.getFormatName());
                PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long id = cupboard().withDatabase(db).put(codeObj);
                listItems.clear();
                adapter.notifyDataSetChanged();
                Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
                try {
                    // Iterate Bunnys
                    QueryResultIterable<Code> itr = cupboard().withCursor(codes).iterate(Code.class);
                    for (Code bunny : itr) {
                        // do something with bunny
                        Listitem listItem = new Listitem(bunny._id, bunny.name, bunny.type);
                        listItems.add(listItem);
                        adapter = new AdapterViewHolder(listItems, this);
                        recyclerView.setAdapter(adapter);
                    }
                } finally {
                    // close the cursor
                    codes.close();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void cardClick(View card) {
        TextView textView = (TextView) findViewById(R.id.textViewCode);
        String code = textView.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, code);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount(); // if stack count is 0, it means no fragment left to pop back stack
        if (doubleBackToExitPressedOnce && count == 0) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;

            }
        }, 2000);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("beep")) {
            scan.setBeepEnabled(sharedPreferences.getBoolean(s, true));
        }
        if (s.equals("frontCamera")) {
            int camId;
            if (sharedPreferences.getBoolean(s, false) == false)
                camId = 0;
            else
                camId = 1;
            scan.setCameraId(camId);
        }
    }
}
