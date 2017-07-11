package com.aviv_pos.olgats.avivinventory;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aviv_pos.olgats.avivinventory.async.CloseSessionTask;
import com.aviv_pos.olgats.avivinventory.async.ItemSearchTask;
import com.aviv_pos.olgats.avivinventory.beans.ItemSet;
import com.aviv_pos.olgats.avivinventory.services.ZFetchService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SearchFragment.OnFragmentInteractionListener, ScannerFragment.OnFragmentInteractionListener, ItemListFragment.OnFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String ACTION_FOR_INTENT_CALLBACK = "THIS_IS_A_UNIQUE_KEY_WE_USE_TO_COMMUNICATE";
    private final static int SCAN_BAR = 1;
    private final static int SCAN_QR = 2;
    private static final int TAB_RESULTS = 2;
    private static final int TAB_SEARCH = 1;
    private static final int TAB_SCAN = 0;
    private DrawerLayout mDrawerLayout;
    private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private ProgressDialog progress;
    public final static String ZFETCH_PARAM_PINTENT = "pendingIntent";
    public final static String ZFETCH_RESULT = "zfetchresult";
    public final static int ZFETCH_STATUS_START = 100;
    public final static int ZFETCH_STATUS_FINISH = 300;
    public final static int ZFETCH_TASK_CODE = 999;

    private ViewPager viewPager;
    private ItemListFragment itemListFragment;
    private boolean zFetchRunning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);


        //final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        //ab.setDisplayHomeAsUpEnabled(true);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            final CloseSessionTask task = new CloseSessionTask(this);
            task.execute();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_status) {
            Intent intent = new Intent(MainActivity.this, StatusActivity.class);
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
            i.putExtra(Intent.EXTRA_TEXT, "body of email");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(ScannerFragment.newInstance(SCAN_BAR, SCAN_QR), "Scan");
        adapter.addFragment(SearchFragment.newInstance(), "Search by criteria");
        adapter.addFragment(itemListFragment = ItemListFragment.newInstance(), "Results");
        viewPager.setAdapter(adapter);
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

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // Ловим сообщения об окончании задач
        if (requestCode==ZFETCH_TASK_CODE){
            switch(resultCode) {
                case ZFETCH_STATUS_START:
                    zFetchRunning = true;
                    break;
            }

        }else {

            IntentResult scan = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scan != null) {
                String contents = scan.getContents();
                if (contents != null) {
                    String format = scan.getFormatName();
                    Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG).show();
                    ItemSearchCriteria c = new ItemSearchCriteria();
                    c.setCode(contents);
                    getContent(c);
                }
            }
        }



    }

    @Override
    public void onSearchFragmentInteraction(ItemSearchCriteria c) {
        getContent(c);
    }

    private void showItem(String code) {
        Intent intentItem = new Intent(this, ItemActivity.class);
        intentItem.putExtra("itemCode", code);
        startActivity(intentItem);
    }


    public void embeddedScanBar(View v) {
        try {
            // Check that the device will let you use the camera
            PackageManager pm = getPackageManager();

            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
                integrator.setOrientationLocked(false);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }else{
                showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
            }
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void embeddedScanQR(View v) {
        try {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
            integrator.setOrientationLocked(false);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan a QRcode");
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    @Override
    public void onScanerFragmentInteraction(View view, int type) {
        switch (type) {
            case SCAN_BAR:
                embeddedScanBar(view);
                break;
            case SCAN_QR:
                embeddedScanQR(view);
                break;
        }
    }

    private void getContent(ItemSearchCriteria c) {
        // the request
        if (c != null) {
            try {
                final ItemSearchTask task = new ItemSearchTask(this, ACTION_FOR_INTENT_CALLBACK);
                task.execute(c);
                progress = new ProgressDialog(this);
                progress.setTitle(getString(R.string.loading));
                progress.setIndeterminate(true);
                progress.setMessage(getString(R.string.waiting_for_result));
                progress.setCancelable(false);
                progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        task.cancel(true);
                    }
                });
                progress.show();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void setProgressMessage(String msg){
        if(progress!=null){
            progress.setMessage(msg);
        }
    }
    /**
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }
            int response = intent.getIntExtra(ItemSearchTask.RESPONSE, -1);
            switch (response) {
                case WSConstants.SUCCESS:
                    int size = intent.getIntExtra(ItemSearchTask.RESPONSE_SIZE, 0);
                    switch (size) {
                        case 0:
                            android.support.v7.app.AlertDialog ad = new AlertDialog.Builder(MainActivity.this).setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).setMessage(getString(R.string.alertItemNotFind)).show();
                            break;
                        case 1:
                            String code = intent.getStringExtra(ItemSearchTask.RESPONSE_FIRST);
                            if (code != null) {
                                itemListFragment.updateList();
                                showItem(code);
                            }
                            break;
                        default:

                            itemListFragment.updateList();
                            viewPager.setCurrentItem(TAB_RESULTS, true);
                            Toast.makeText(MainActivity.this, "Item list:" + size, Toast.LENGTH_SHORT).show();

                    }
                    break;
                case WSConstants.CLIENT_CONNECTION_ERROR:
                    android.support.v7.app.AlertDialog ad = new AlertDialog.Builder(MainActivity.this).setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setMessage(getString(R.string.alertClientConnectionError)).show();
                    break;
                case WSConstants.PRIVATE_WS_NOT_REACHABLE:
                    ad = new AlertDialog.Builder(MainActivity.this).setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setMessage(getString(R.string.alertPrivateWSConnectionError)).show();
                    break;
                case WSConstants.SESSION_CLOSED:
                    onSessionClose();
                    break;

            }
        }
    };

    private void onSessionClose() {
        new AlertDialog.Builder(MainActivity.this).setPositiveButton(getString(R.string.login),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton(getString(R.string.exit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                }).setMessage(getString(R.string.alertSessionClosed)).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));
        if (viewPager.getCurrentItem() == TAB_RESULTS) {
            itemListFragment.updateList();
        }
        if(!zFetchRunning) {
            PendingIntent pi;
            Intent intent;

            // Создаем PendingIntent для ZFetchService
            pi = createPendingResult(ZFETCH_TASK_CODE, new Intent(), 0);
            // Создаем Intent для вызова сервиса, кладем туда созданный PendingIntent
            intent = new Intent(this, ZFetchService.class).putExtra(ZFETCH_PARAM_PINTENT, pi);
            // стартуем сервис
            startService(intent);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onItemListFragmentInteraction(String code) {
        showItem(code);
    }


}
