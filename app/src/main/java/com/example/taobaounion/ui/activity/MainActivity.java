package com.example.taobaounion.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.taobaounion.R;
import com.example.taobaounion.base.BaseFragment;
import com.example.taobaounion.ui.fragment.HomeFragment;
import com.example.taobaounion.ui.fragment.RedPacketFragment;
import com.example.taobaounion.ui.fragment.SearchFragment;
import com.example.taobaounion.ui.fragment.SelectedFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView mNavigationView;
    private HomeFragment mHomeFragment;
    private SelectedFragment mSelectedFragment;
    private RedPacketFragment mRedPacketFragment;
    private SearchFragment mSearchFragment;
    private FragmentManager mFm;
    private Unbinder mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBind = ButterKnife.bind(this);

        initView();
        initFragment();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();
        }
    }

    private void initFragment() {
        mFm = getSupportFragmentManager();
        mHomeFragment = new HomeFragment();
        mSelectedFragment = new SelectedFragment();
        mRedPacketFragment = new RedPacketFragment();
        mSearchFragment = new SearchFragment();
        switchFragment(mHomeFragment);
    }

    private void initListener() {
        mNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: title ===> " + item.getTitle());
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:
                        switchFragment(mHomeFragment);
                        break;
                    case R.id.selected:
                        switchFragment(mSelectedFragment);
                        break;
                    case R.id.red_packet:
                        switchFragment(mRedPacketFragment);
                        break;
                    case R.id.search:
                        switchFragment(mSearchFragment);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void switchFragment(BaseFragment fragment) {
        FragmentTransaction transaction = mFm.beginTransaction();
        transaction.replace(R.id.main_container, fragment);
        transaction.commit();
    }

    private void initView() {
        mNavigationView = (BottomNavigationView) this.findViewById(R.id.main_navigation_bar);
    }
}