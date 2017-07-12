package k.tomorrowdecision;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {
    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationManager notificationmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);

        System.out.println(intent.getStringExtra("content"));
        System.out.println(intent.getStringExtra("timeText"));
        System.out.println(intent.getIntExtra("index", 1));

        builder.setSmallIcon(R.drawable.i).setTicker("지금은...")
                .setContentTitle(intent.getStringExtra("content")).setContentText(intent.getStringExtra("timeText"))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setAutoCancel(true);

        notificationmanager.notify(intent.getIntExtra("index", 1), builder.build());

        return START_NOT_STICKY;
    }
}
