package br.com.finguard.notificationlistener;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NotificationListenerService - intercepta notificações de apps bancários.
 * Requer permissão no AndroidManifest e ativação pelo usuário em Configurações.
 */
public class FinGuardNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "FinGuardNL";
    private static final String[] BANK_PACKAGES = {
            "com.nubank.android",
            "com.itau",
            "com.bradesco",
            "com.bb.mobile",
            "com.santander.bank",
            "com.inter",
            "br.com.c6bank.app",
            "com.bancooriginal",
            "com.banco.safra",
            "com.banrisul"
    };

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pkg = sbn.getPackageName();
        if (!isBankPackage(pkg)) return;

        try {
            CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
            CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");
            CharSequence bigText = sbn.getNotification().extras.getCharSequence("android.bigText");

            StringBuilder content = new StringBuilder();
            if (title != null) content.append(title).append(" ");
            if (text != null) content.append(text).append(" ");
            if (bigText != null) content.append(bigText);

            String contentStr = content.toString().trim();
            if (contentStr.isEmpty()) return;

            JSONObject data = new JSONObject();
            data.put("title", title != null ? title.toString() : "");
            data.put("text", text != null ? text.toString() : "");
            data.put("content", contentStr);
            data.put("packageName", pkg);

            // Enviar via LocalBroadcast para o plugin processar.
            // O plugin deve registrar um BroadcastReceiver no app principal.
            android.content.Intent intent = new android.content.Intent("br.com.finguard.NOTIFICATION_RECEIVED");
            intent.putExtra("data", data.toString());
            sendBroadcast(intent);

            Log.d(TAG, "Notification intercepted: " + contentStr.substring(0, Math.min(50, contentStr.length())));
        } catch (JSONException e) {
            Log.e(TAG, "Error building notification payload", e);
        }
    }

    private boolean isBankPackage(String pkg) {
        for (String bank : BANK_PACKAGES) {
            if (pkg.startsWith(bank) || pkg.equals(bank)) return true;
        }
        return false;
    }
}
