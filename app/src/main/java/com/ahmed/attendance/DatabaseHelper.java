package com.ahmed.attendance;


import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;

public class DatabaseHelper {
    private Realm realm;
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context context) {
        //Initialize Realm
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    public void addDayToRealm(Day day) {
        realm.beginTransaction();
        realm.insertOrUpdate(day);
        realm.commitTransaction();
        Log.d(TAG, "Saved Day To Database.");
    }

    public boolean isSaved(String id) {
        Day result = realm.where(Day.class).equalTo("ID", id).findFirst();
        Log.d(TAG, result != null ? "Day is Saved." : "Day is Not Saved.");
        return (result != null);
    }

    public RealmResults<Day> getAllDaysFromRealm() {
        return realm.where(Day.class).findAll();
    }

    public Day getDayFromRealm(String id) {
        Day results = realm.where(Day.class).equalTo("ID", id).findFirst();
        if (results != null)
            return new Day(id, results.getDate_started(),
                    results.getDate_ended(),
                    results.getNum_of_houres(),
                    results.getExtra_houres(),
                    results.getColor_index());
        else
            return null;
    }

    public void removeDayFromRealm(int id) {
        Day result = realm.where(Day.class).equalTo("ID", id).findFirst();
        if (result != null) {
            realm.beginTransaction();
            result.deleteFromRealm();
            realm.commitTransaction();
            Log.d(TAG, "Removed Day From Database.");
        } else {
            Log.d(TAG, "Day Doesn't exist in Database.");
        }
    }
}
