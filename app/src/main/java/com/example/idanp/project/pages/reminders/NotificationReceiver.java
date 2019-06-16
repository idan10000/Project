package com.example.idanp.project.pages.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.idanp.project.R;

import java.util.Calendar;

class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    private static boolean studiedToday = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.idanp.project.pages.reminders.MY_NOTIFICATION_MESSAGE")) {
            Log.d(TAG, "onReceive: Alarm hit");
            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month =  calendar.get(Calendar.MONTH);

            //TODO undo display code: uncomment the following code and delete the if(false) statement

//            if(dayOfMonth == intent.getIntExtra("dayOfTest",0) && month == intent.getIntExtra("monthOfTest",0))
//            {
//                Intent stop = new Intent(context, NotificationReceiver.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, stop, 0);
//                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                alarmManager.cancel(pendingIntent);
//            }

            if(false);

            else {
                if (currentHour == 8) {
                    studiedToday = false;
                }

                if (!studiedToday && currentHour > 7 && currentHour < 21) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent studiedIntent = new Intent(context, NotificationReceiver.class);
                    studiedIntent.setAction("com.example.idanp.project.pages.reminders.Studied");
                    PendingIntent studiedPendingIntent = PendingIntent.getBroadcast(context, 100, studiedIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ProjectNotifications")
                            .setSmallIcon(R.drawable.ic_work_black_24dp)
                            .setContentTitle("Study Time Reminder")
                            .setContentText("You have yet study " + intent.getIntExtra("numOfHours", 0) + " hours today for the test in "
                                    + intent.getStringExtra("subjectName"))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_check_black_24dp, "Studied", studiedPendingIntent);

                    notificationManager.notify(100, builder.build());
                }
            }
        }
        if(intent.getAction().equals("com.example.idanp.project.pages.reminders.Studied")){
            studiedToday = true;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }

}
