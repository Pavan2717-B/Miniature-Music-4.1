package com.miniaturemusic.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(MusicScanPlugin.class);
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        int bg = Color.parseColor("#080c16");
        w.getDecorView().setBackgroundColor(bg);
        try { w.setStatusBarColor(bg); w.setNavigationBarColor(bg); } catch (Exception e) { }
        WindowCompat.getInsetsController(w, w.getDecorView()).setAppearanceLightStatusBars(false);
        WindowCompat.getInsetsController(w, w.getDecorView()).setAppearanceLightNavigationBars(false);

        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                | WindowInsetsCompat.Type.displayCutout()
                | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
