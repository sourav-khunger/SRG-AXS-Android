package com.unipos.axslite.Activity;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.unipos.axslite.Adapter.TabLayoutPagerAdapter;
import com.unipos.axslite.BackgroudService.Workers.SyncRemoteServerService;
import com.unipos.axslite.CoverterPOJOToEntity.DriverInfoToDriverInfoEntity;
import com.unipos.axslite.Database.Entities.DriverInfoEntity;
import com.unipos.axslite.Database.Repository.TaskInfoRepository;
import com.unipos.axslite.Database.ViewModel.DriverInfoViewModel;
import com.unipos.axslite.MainActivity;
import com.unipos.axslite.POJO.DriverInfo;
import com.unipos.axslite.POJO.LoginResponse;
import com.unipos.axslite.R;
import com.unipos.axslite.UpdateDebug.MenuActivity;
import com.unipos.axslite.Utils.Constants;
import com.unipos.axslite.ui.NavigationViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ScreenSlidePagerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationViewModel navigationViewModel;
    private DrawerLayout drawerLayout;
    TabLayoutPagerAdapter pagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    private TextView driverNameNavText;
    private TextView driverCompanyNavText;
    TaskInfoRepository mTaskInfoRepository;
    private DatePicker mDatePicker;
    private Calendar mCalender;
    private int year, month, date;
    private DatePickerDialog.OnDateSetListener mDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide_pager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewModel = new ViewModelProvider(this).get(NavigationViewModel.class);

        View headerView = navigationView.getHeaderView(0);
        driverNameNavText = headerView.findViewById(R.id.driver_name_nav_text);
        driverCompanyNavText = headerView.findViewById(R.id.driver_company_nav_text);

        pagerAdapter = new TabLayoutPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tab_layout_main);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);

        mTaskInfoRepository = new TaskInfoRepository((Application) getApplicationContext());
//        mTaskInfoRepository.deleteAll();

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        addDetailToNavDrawer();
    }

    private void addDetailToNavDrawer() {
        navigationViewModel.getDriver().observe(this, new Observer<DriverInfo>() {
            @Override
            public void onChanged(DriverInfo driver) {
                driverNameNavText.setText(driver.getFirstName() + " " + driver.getLastName());
                driverCompanyNavText.setText(driver.getCompanyName());
            }
        });

        String jsonLoginResponse = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.PREF_KEY_LOGIN_RESPONSE, "");
        LoginResponse loginResponse = new Gson().fromJson(jsonLoginResponse, LoginResponse.class);
        navigationViewModel.setDriver(loginResponse.getDriverInfo());
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_login:
                // Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ScreenSlidePagerActivity.this, ChooseCompanyActivity.class));
                break;
            case R.id.nav_Menu:
                // Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ScreenSlidePagerActivity.this, MenuActivity.class));
                break;
            case R.id.nav_show_calendar:
                //
                if (mTaskInfoRepository.getTaskInfoByRecordStatus(Constants.MODIFIED).size() > 0) {
                    Toast.makeText(getApplicationContext(), "Local Data is not synced with server yet! Please try after some time.", Toast.LENGTH_LONG).show();
                } else {

                    mTaskInfoRepository.deleteAll();
                    showDialog(999);
                }

                break;
            case R.id.nav_refresh:
                if (mTaskInfoRepository.getTaskInfoByRecordStatus(Constants.MODIFIED).size() > 0) {
                    Toast.makeText(getApplicationContext(), "Local Data is not synced with server yet! Please try after some time.", Toast.LENGTH_LONG).show();
                } else {
                    mTaskInfoRepository.deleteAll();

                    stopService(new Intent(getApplicationContext(), SyncRemoteServerService.class));
                    startService(new Intent(getApplicationContext(), SyncRemoteServerService.class));
                }
                break;
            case R.id.route_menu:
                Intent intent = new Intent(ScreenSlidePagerActivity.this, MenuActivity.class);
                intent.putExtra("map", "map");
                startActivity(intent);

                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }


    private void init() {

        mCalender = Calendar.getInstance();
        year = mCalender.get(Calendar.YEAR);
        month = mCalender.get(Calendar.MONTH);
        date = mCalender.get(Calendar.DATE);
        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                stopService(new Intent(ScreenSlidePagerActivity.this, SyncRemoteServerService.class));
                String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(getDateFromDatePicker(datePicker));
                PreferenceManager.getDefaultSharedPreferences(ScreenSlidePagerActivity.this).edit()
                        .putString(Constants.PREF_KEY_SELECTED_DATE, selectedDate)
                        .apply();
                // Log.d(TAG, selectedDate);
                startService(new Intent(ScreenSlidePagerActivity.this, SyncRemoteServerService.class));
            }
        };
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, mDateListener, year, month, date);
        }
        return super.onCreateDialog(id);
    }
}