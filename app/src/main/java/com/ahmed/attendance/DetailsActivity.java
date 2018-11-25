package com.ahmed.attendance;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.ahmed.attendance.MainActivity.caldroidFragment;
import static com.ahmed.attendance.MainActivity.databaseHelper;

public class DetailsActivity extends AppCompatActivity {

    public static final int WORKING_HOURS = 8;

    EditText startedTimeEdit, endedTimeEdit;
    TextView hoursText, extraText;
    GridLayout gridLayout;
    Date startedTime, endedTime;
    int hours, extra;
    int color_index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        startedTimeEdit = findViewById(R.id.started_time);
        endedTimeEdit = findViewById(R.id.ended_time);
        hoursText = findViewById(R.id.hours);
        extraText = findViewById(R.id.extra);
        gridLayout = findViewById(R.id.grid_layout);

        if (databaseHelper.isSaved(getIntent().getExtras().getString("date")))
            getDataFromRealm(getIntent().getExtras().getString("date"));

        final String[] colorsArray = getResources().getStringArray(R.array.colors);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            FrameLayout frameLayout = (FrameLayout) gridLayout.getChildAt(i);
            ImageView imageView = (ImageView) frameLayout.getChildAt(0);
            imageView.setColorFilter(Color.parseColor(colorsArray[i]));

            final int index = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = ((FrameLayout) v.getParent()).getChildAt(1);
                    if (view.getBackground() == null) {
                        if (color_index != -1)
                            ((FrameLayout) gridLayout.getChildAt(color_index)).getChildAt(1).setBackground(null);
                        color_index = index;
                        view.setBackgroundResource(R.drawable.ic_check);
                    } else {
                        color_index = -1;
                        view.setBackground(null);
                    }
                }
            });
        }
    }

    public void selectTime(final View view) {
        final Calendar myCalendar = Calendar.getInstance();
        if (!((TextView) view).getText().toString().equals("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
            try {
                myCalendar.setTime(sdf.parse(((TextView) view).getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(DetailsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
                myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                myCalendar.set(Calendar.MINUTE, selectedMinute);
                ((EditText) view).setText(sdf.format(myCalendar.getTime()));
                if (!startedTimeEdit.getText().toString().equals("") &&
                        !endedTimeEdit.getText().toString().equals("")) {
                    SimpleDateFormat stf = new SimpleDateFormat("h:mm a", Locale.US);
                    try {
                        startedTime = stf.parse(startedTimeEdit.getText().toString());
                        endedTime = stf.parse(endedTimeEdit.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    hours = calculateHours(startedTime, endedTime);
                    hoursText.setText(String.valueOf(hours));
                    extra = hours - WORKING_HOURS;
                    extraText.setText(String.valueOf(extra));
                }
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void save(View view) {
        String date = getIntent().getExtras().getString("date");
        SimpleDateFormat stf = new SimpleDateFormat("h:mm a", Locale.US);
        try {
            startedTime = stf.parse(startedTimeEdit.getText().toString());
        } catch (ParseException ignored) {
        }

        try {
            endedTime = stf.parse(endedTimeEdit.getText().toString());
        } catch (ParseException ignored) {
        }

        hours = Integer.parseInt(hoursText.getText().toString());
        extra = Integer.parseInt(extraText.getText().toString());

        Day day = new Day(
                date,
                startedTime,
                endedTime,
                hours,
                extra,
                color_index);

        databaseHelper.addDayToRealm(day);

        ColorDrawable color;
        if (color_index == -1) {
            int c = getResources().getColor(R.color.caldroid_white);
            color = new ColorDrawable(c);
        } else {
            String[] colors = view.getResources().getStringArray(R.array.colors);
            color = new ColorDrawable(Color.parseColor(colors[color_index]));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            caldroidFragment.setBackgroundDrawableForDate(color, sdf.parse(day.getID()));
            caldroidFragment.refreshView();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        finish();
    }

    void getDataFromRealm(String ID) {
        Day day = databaseHelper.getDayFromRealm(ID);
        SimpleDateFormat stf = new SimpleDateFormat("h:mm a", Locale.US);
        if (day.getDate_started() != null)
            startedTimeEdit.setText(stf.format(day.getDate_started()));
        if (day.getDate_ended() != null)
            endedTimeEdit.setText(stf.format(day.getDate_ended()));

        hoursText.setText(String.valueOf(day.getNum_of_houres()));
        extraText.setText(String.valueOf(day.getExtra_houres()));

        color_index = day.getColor_index();
        if (color_index != -1)
            ((FrameLayout) gridLayout.getChildAt(color_index)).getChildAt(1).setBackgroundResource(R.drawable.ic_check);
    }

    int calculateHours(Date start, Date end) {
        long l = end.getTime() - start.getTime();
        return (int) ((l / (1000 * 60 * 60)) % 24);
    }
}
