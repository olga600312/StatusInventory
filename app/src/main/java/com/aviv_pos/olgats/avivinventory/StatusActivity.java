package com.aviv_pos.olgats.avivinventory;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.aviv_pos.olgats.avivinventory.async.StatusTask;
import com.aviv_pos.olgats.avivinventory.beans.Result;
import com.aviv_pos.olgats.avivinventory.beans.StatusInfo;
import com.aviv_pos.olgats.avivinventory.beans.Store;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.aviv_pos.olgats.avivinventory.model.item.status.StatusHRFragment;
import com.aviv_pos.olgats.avivinventory.model.item.status.StatusSalesFragment;
import com.aviv_pos.olgats.avivinventory.model.item.status.StatusTerminalsFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.SubcolumnValue;

public class StatusActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private StatusSalesFragment statusSalesFragment;
    private StatusHRFragment statusHRFragment;
    private StatusTerminalsFragment statusTerminalFragment;
    private ProgressDialog progress;
    private static final String ACTION_FOR_INTENT_CALLBACK = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE_WITH_STATUSTASK";
    private List<StatusInfo> statusInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        statusInfoList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.statusPager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.statusTabs);
        tabLayout.setupWithViewPager(viewPager);
       /* tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch(tab.getPosition()){
                      case 2:
                        viewPager.setCurrentItem(tab.getPosition(),true);
                        statusTerminalFragment.updateChart(statusInfoList);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(statusSalesFragment = StatusSalesFragment.newInstance(), getString(R.string.statusSales));
        adapter.addFragment(statusHRFragment = StatusHRFragment.newInstance(), getString(R.string.statusHRActivity));
        adapter.addFragment(statusTerminalFragment = StatusTerminalsFragment.newInstance(), getString(R.string.statusTerminalActivity));
        viewPager.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        showChart();
    }

    private void showChart() {
        final StatusTask task = new StatusTask(this, ACTION_FOR_INTENT_CALLBACK);
        task.execute();
        progress = new ProgressDialog(StatusActivity.this);
        progress.setIndeterminate(true);
        progress.setMessage(getString(R.string.loading));
        progress.setCancelable(false);
        progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                task.cancel(true);

            }
        });
        progress.show();
    }


    public void setProgressMessage(String msg) {
        if (progress != null) {
            progress.setMessage(msg);
        }
    }

    public void onChartData(Result<StatusInfo> r) {
        if (progress != null) {
            progress.dismiss();
        }
        Toast.makeText(this, "Status " + (r != null ? r.getCode() : "null"), Toast.LENGTH_SHORT).show();
        generateData(r.getData());
    }

    private void generateData(List<StatusInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
            Toast.makeText(this, getString(R.string.get_info_error), Toast.LENGTH_SHORT).show();
        }
        statusInfoList = list;
        statusSalesFragment.updateChart(list);
        statusHRFragment.updateChart(list);
// statusTerminalFragment.updateChart(statusInfoList);

    }

    public List<StatusInfo> getStatusInfoList() {
        return statusInfoList;
    }

    public void setStatusInfoList(List<StatusInfo> statusInfoList) {
        this.statusInfoList = statusInfoList;
    }
}
