package com.chowen.apackage.snatchredenvelope;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.chowen.apackage.snatchredenvelope.service.RedPackageService;
import com.chowen.apackage.snatchredenvelope.utils.L;
import com.chowen.apackage.snatchredenvelope.utils.StatusBarHelper;

// TODO: 2017/1/11 红包声音
// TODO: 2017/1/12 http://www.jianshu.com/p/4cd8c109cdfb 测试 
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TextView mEtDelayTime;

//    private FloatViewService mFloatViewService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawerLayout();

        initViews();

//        Intent intent = new Intent(this, FloatViewService.class);
//        startService(intent);
//        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        StatusBarHelper.setColor(this, getResources().getColor(R.color.colorPrimaryDark));
    }

    /**
     * 显示悬浮图标
     */
//    private void showFloatView() {
//        if (mFloatViewService != null) {
//            mFloatViewService.showFloat();
//        }
//    }

    /**
     * 隐藏悬浮图标
     */
//    public void hideFloatingView() {
//        if (mFloatViewService != null) {
//            mFloatViewService.hideFloat();
//        }
//    }

    private void initViews() {
        mEtDelayTime = (TextView) findViewById(R.id.et_delay_time);
        findViewById(R.id.btn_access).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        ((ToggleButton) findViewById(R.id.tb)).setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        RedPackageService.setReturnChatPage(isChecked);
                    }
                });

        findViewById(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEtDelayTime.getText()))
                    RedPackageService.setDelayTime(Integer.valueOf(mEtDelayTime.getText().toString()));
                else
                    Toast.makeText(MainActivity.this, "不能为空", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.id_nv_menu);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_launcher);
        ab.openOptionsMenu();
        ab.setDisplayHomeAsUpEnabled(true);

        setupDrawerContent(mNavigationView);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        L.e("Title>>" + item.getTitle() + ">" + item.getItemId());
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
//        stopService(new Intent(this, FloatViewService.class));
//        unbindService(mServiceConnection);
    }

    /**
     * 连接到Service
     */
//    private final ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            mFloatViewService = ((FloatViewService.FloatViewServiceBinder) iBinder).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            mFloatViewService = null;
//        }
//    };
}
