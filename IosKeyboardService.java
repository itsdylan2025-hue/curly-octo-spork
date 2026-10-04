package com.example.iosemoji;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IosKeyboardService extends InputMethodService {

    private final List<String> names = new ArrayList<>();
    private Bitmap[] cache;

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    @Override
    public View onCreateInputView() {
        loadNames();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#1F1F1F"));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(6), dp(4), dp(6), dp(4));

        Button switchBtn = new Button(this);
        switchBtn.setText("Switch keyboard");
        switchBtn.setAllCaps(false);
        switchBtn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showInputMethodPicker();
        });

        View spacer = new View(this);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, 1, 1f);

        Button del = new Button(this);
        del.setText("\u232B");
        del.setOnClickListener(v -> sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL));

        bar.addView(switchBtn);
        bar.addView(spacer, sp);
        bar.addView(del);
        root.addView(bar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        GridView grid = new GridView(this);
        grid.setNumColumns(GridView.AUTO_FIT);
        grid.setColumnWidth(dp(54));
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setAdapter(new EmojiAdapter());
        grid.setOnItemClickListener((parent, view, position, id) -> typeEmoji(names.get(position)));
        root.addView(grid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(240)));

        return root;
    }

    private void loadNames() {
        names.clear();
        try {
            String[] files = getAssets().list("emoji");
            if (files != null) {
                Arrays.sort(files);
                for (String f : files) {
                    if (f.endsWith(".png")) names.add(f.substring(0, f.length() - 4));
                }
            }
        } catch (Exception ignored) { }
        cache = new Bitmap[names.size()];
    }

    private void typeEmoji(String hex) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        try {
            int cp = Integer.parseInt(hex, 16);
            StringBuilder sb = new StringBuilder();
            sb.appendCodePoint(cp);
            if (cp < 0x10000) sb.appendCodePoint(0xFE0F);
            ic.commitText(sb.toString(), 1);
        } catch (NumberFormatException ignored) { }
    }

    private Bitmap bitmapAt(int i) {
        if (cache[i] == null) {
            try (InputStream in = getAssets().open("emoji/" + names.get(i) + ".png")) {
                cache[i] = BitmapFactory.decodeStream(in);
            } catch (Exception ignored) { }
        }
        return cache[i];
    }

    private class EmojiAdapter extends BaseAdapter {
        @Override public int getCount() { return names.size(); }
        @Override public Object getItem(int p) { return names.get(p); }
        @Override public long getItemId(int p) { return p; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            if (convertView instanceof ImageView) {
                iv = (ImageView) convertView;
            } else {
                iv = new ImageView(IosKeyboardService.this);
                iv.setLayoutParams(new GridView.LayoutParams(dp(54), dp(54)));
                iv.setPadding(dp(7), dp(7), dp(7), dp(7));
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            iv.setImageBitmap(bitmapAt(position));
            return iv;
        }
    }
}
