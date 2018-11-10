package com.ahmed.attendance;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper databaseHelper;
    public static CaldroidFragment caldroidFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_view, caldroidFragment);
        t.commit();

        updateCalender();

        final CaldroidListener caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("date", sdf.format(date));
                startActivity(intent);
            }
        };

        caldroidFragment.setCaldroidListener(caldroidListener);
    }

    void updateCalender() {
        RealmResults<Day> days = databaseHelper.getAllDaysFromRealm();
        for (Day day : days) {
            if (day.getColor_index() != -1) {
                String[] colors = getResources().getStringArray(R.array.colors);
                int color_index = day.getColor_index();
                ColorDrawable color = new ColorDrawable(Color.parseColor(colors[color_index]));

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                try {
                    caldroidFragment.setBackgroundDrawableForDate(color, sdf.parse(day.getID()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        caldroidFragment.refreshView();
    }
}
