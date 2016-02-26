package com.android.dis.businessqrcard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.WindowManager;

import com.android.dis.businessqrcard.adapter.TabsFragmentAdapter;
import com.android.dis.businessqrcard.fragment.FragmentEditInfo;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    public SharedPreferences sPref;

    final String SAVED_FIO = "FIO";
    final String SAVED_COMPANY = "COMPANY";
    final String SAVED_EMAIL = "EMAIL";
    final String SAVED_PHONE = "PHONE";

    public static int smallerDimension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initTabs();
        loadText();

        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        //display.getSize(point);
        point.x = display.getWidth();
        point.y = display.getHeight();
        int width = point.x;
        int height = point.y;
        smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }

    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsFragmentAdapter adapter = new TabsFragmentAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    void loadText() {
        sPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        String savedText = sPref.getString(SAVED_FIO, "");
        String savedText_2 = sPref.getString(SAVED_COMPANY, "");
        String savedText_3 = sPref.getString(SAVED_EMAIL, "");
        String savedText_4 = sPref.getString(SAVED_PHONE, "");
        /*FragmentEditInfo.editText_fio.setText(savedText);
        FragmentEditInfo.editText_company.setText(savedText_2);
        FragmentEditInfo.editText_email.setText(savedText_3);
        FragmentEditInfo.editText_phone.setText(savedText_4);*/
        FragmentEditInfo.str_fio = savedText.toString();
        FragmentEditInfo.str_company = savedText_2.toString();
        FragmentEditInfo.str_email = savedText_3.toString();
        FragmentEditInfo.str_phone = savedText_4.toString();
    }
}
