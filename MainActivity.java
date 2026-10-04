package com.example.iosemoji;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pad = Math.round(20 * getResources().getDisplayMetrics().density);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(pad, pad, pad, pad);

        TextView info = new TextView(this);
        info.setTextSize(16);
        info.setText("iOS Emoji Keyboard\n\n"
                + "1. Tap \"Enable keyboard\" and switch on \"iOS Emoji Keyboard\".\n"
                + "2. Open any chat, tap a text box, then tap \"Choose keyboard\" here "
                + "(or the keyboard icon) and pick iOS Emoji Keyboard.\n"
                + "3. Tap an emoji to type it. Use \"Switch keyboard\" to go back to your normal keyboard.\n\n"
                + "Note: this types normal emoji characters. The iOS look only shows in this keyboard.");

        Button enable = new Button(this);
        enable.setText("1. Enable keyboard");
        enable.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));

        Button choose = new Button(this);
        choose.setText("2. Choose keyboard");
        choose.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showInputMethodPicker();
        });

        layout.addView(info);
        layout.addView(enable);
        layout.addView(choose);
        setContentView(layout);
    }
}
