package com.ta.slk.sistemlayanankegiatan.Method;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.Preference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.brouding.simpledialog.SimpleDialog;
import com.ta.slk.sistemlayanankegiatan.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class Application extends android.app.Application {
    private static Session session;
    @Override
    public void onCreate() {
        super.onCreate();
        session = new Session(getApplicationContext());
    }

    public static Session getSession(){
        return session;
    }

    public static int getColorRandom(){
        Random rnd = new Random();
        int color = Color.argb(200, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }

    public static SimpleDialog.Builder getProgress(Context context, String msg) {
        return new SimpleDialog.Builder(context)
                .showProgress(true)
                .setContent(msg)
                .setBtnCancelText("Minimize")
                .setCancelable(true);
    }

    public static String getTimeAgo(String date) {
        String dateAgo = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String timeNow = format.format(new Date());
            Date past = format.parse(date);
            Date now = format.parse(timeNow);
            long dateseccond = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long datediff = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long diffHours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());

            if (dateseccond < 60) {
                dateAgo = dateseccond + " second ago";
            } else if (datediff < 60 && datediff >= 1) {
                dateAgo = datediff + " minutes ago";
            } else if (diffHours >= 1 && diffHours <= 24) {
                dateAgo = diffHours + " hours ago";
            } else if (diffHours > 24) {
                dateAgo = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago";
            }

        } catch (Exception e) {

        }

        return dateAgo;
    }
}
