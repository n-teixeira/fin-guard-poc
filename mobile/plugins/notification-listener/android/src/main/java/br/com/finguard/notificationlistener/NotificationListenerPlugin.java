package br.com.finguard.notificationlistener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONException;

@CapacitorPlugin(name = "NotificationListener")
public class NotificationListenerPlugin extends Plugin {

    private static final String TAG = "NotificationListener";

    @PluginMethod
    public void requestPermission(PluginCall call) {
        Context ctx = getContext();
        if (ctx == null) {
            call.reject("Context not available");
            return;
        }

        if (!isNotificationServiceEnabled(ctx)) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            call.resolve(new JSObject().put("granted", false));
        } else {
            call.resolve(new JSObject().put("granted", true));
        }
    }

    @PluginMethod
    public void hasPermission(PluginCall call) {
        Context ctx = getContext();
        if (ctx == null) {
            call.reject("Context not available");
            return;
        }
        call.resolve(new JSObject().put("granted", isNotificationServiceEnabled(ctx)));
    }

    private boolean isNotificationServiceEnabled(Context ctx) {
        String pkgName = ctx.getPackageName();
        final String flat = Settings.Secure.getString(ctx.getContentResolver(), "enabled_notification_listeners");
        if (flat == null) return false;
        final String[] names = flat.split(":");
        for (String name : names) {
            final ComponentName cn = ComponentName.unflattenFromString(name);
            if (cn != null && pkgName.equals(cn.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
