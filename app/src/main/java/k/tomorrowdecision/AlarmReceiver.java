package k.tomorrowdecision;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver {

    private static PowerManager.WakeLock sCpuWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "hi");

        sCpuWakeLock.acquire();

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }

        Intent mServiceIntent = new Intent(context, AlarmService.class);

        mServiceIntent.putExtra("timeText", intent.getStringExtra("timeText"));
        mServiceIntent.putExtra("index", intent.getIntExtra("index", 1));
        mServiceIntent.putExtra("content", intent.getStringExtra("content"));

        context.startService(mServiceIntent);
    }

}
