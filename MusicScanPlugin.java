package com.miniature.player;

import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(name = "MusicScan", permissions = {
    @Permission(alias = "audio", strings = { "android.permission.READ_MEDIA_AUDIO" }),
    @Permission(alias = "storage", strings = { "android.permission.READ_EXTERNAL_STORAGE" })
})
public class MusicScanPlugin extends Plugin {

    private String alias() {
        return Build.VERSION.SDK_INT >= 33 ? "audio" : "storage";
    }

    @PluginMethod
    public void scan(PluginCall call) {
        if (getPermissionState(alias()) != PermissionState.GRANTED) {
            requestPermissionForAlias(alias(), call, "permDone");
            return;
        }
        doScan(call);
    }

    @PermissionCallback
    private void permDone(PluginCall call) {
        if (getPermissionState(alias()) == PermissionState.GRANTED) {
            doScan(call);
        } else {
            call.reject("Permission denied. Allow Music and audio access in the app settings.");
        }
    }

    private void doScan(PluginCall call) {
        JSArray songs = new JSArray();
        String[] proj = {
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA
        };
        String sel = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.DURATION + " >= 30000";
        try (Cursor c = getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, sel, null,
                MediaStore.Audio.Media.DATA + " ASC")) {
            if (c != null) {
                while (c.moveToNext()) {
                    String path = c.getString(3);
                    if (path == null) continue;
                    JSObject o = new JSObject();
                    o.put("name", c.getString(0));
                    o.put("size", c.getLong(1));
                    o.put("modified", c.getLong(2));
                    o.put("path", path);
                    songs.put(o);
                }
            }
        } catch (Exception e) {
            call.reject("Scan failed: " + e.getMessage());
            return;
        }
        JSObject r = new JSObject();
        r.put("songs", songs);
        call.resolve(r);
    }
}
