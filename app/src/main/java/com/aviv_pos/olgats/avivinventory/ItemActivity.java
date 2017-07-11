package com.aviv_pos.olgats.avivinventory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviv_pos.olgats.avivinventory.beans.Item;
import com.aviv_pos.olgats.avivinventory.beans.Promo;
import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;
import com.aviv_pos.olgats.avivinventory.model.item.BooleanExtraModel;
import com.aviv_pos.olgats.avivinventory.model.item.FloatExtraModel;
import com.aviv_pos.olgats.avivinventory.model.item.AbstractExtraModel;
import com.aviv_pos.olgats.avivinventory.model.item.ItemPromoModel;
import com.aviv_pos.olgats.avivinventory.model.item.MapExtraModel;
import com.aviv_pos.olgats.avivinventory.model.item.PercentFloatExtraModel;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ItemActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "ItemActivityPrefsFile";

    private static final String TAG = "ItemActivity";
    private Item currentItem;
    private static String code;
    private TextView tvCode, tvName, tvPrice;
    private TextView tvTitlePromo;
    private CheckBox cbWeight;

    private RecyclerView rvExtra;
    private ItemExtraAdapter extraAdapter;
    private TreeMap<String, String> strings = new TreeMap<>();

    private Handler mUiHandler = new Handler();
    private FloatingActionMenu menuItem;
    private ImageView imageView;
    private RecyclerView rvPromo;
    private ItemPromoAdapter promoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if (currentItem != null) {
                    showItem(currentItem.getCode());
                }
            }
        });*/
        imageView = (ImageView) findViewById(R.id.backdrop);
        menuItem = (FloatingActionMenu) findViewById(R.id.menu_item);
        menuItem.hideMenuButton(false);
        int delay = 400;
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menuItem.showMenuButton(true);
            }
        }, delay);
        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                if (currentItem != null) {
                    showItem(currentItem.getCode());
                }
            }
        });

        com.github.clans.fab.FloatingActionButton fabSales = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabSales);
        fabSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intentItem = new Intent(ItemActivity.this, ItemChartActivity.class);
                intentItem.putExtra("itemCode", code);
                startActivity(intentItem);
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvCode = (TextView) findViewById(R.id.textBarcode);
        tvName = (TextView) findViewById(R.id.textName);
        tvPrice = (TextView) findViewById(R.id.textPrice);
        tvTitlePromo = (TextView) findViewById(R.id.titlePromo);
        cbWeight = (CheckBox) findViewById(R.id.cbWeight);

        rvExtra = (RecyclerView) findViewById(R.id.rvExtra);
        rvPromo = (RecyclerView) findViewById(R.id.rvPromo);

        // Create adapter passing in the sample user data
        extraAdapter = new ItemExtraAdapter();
        // Attach the adapter to the recyclerview to populate items
        rvExtra.setAdapter(extraAdapter);

        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
// Control orientation of the items
// also supports LinearLayoutManager.HORIZONTAL
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvExtra.setLayoutManager(layoutManager);
        //rvExtra.setItemAnimator(new SlideInUpAnimator());


        // Create adapter passing in the sample user data
        promoAdapter = new ItemPromoAdapter();
        // Attach the adapter to the recyclerview to populate items
        rvPromo.setAdapter(promoAdapter);

        layoutManager = new LinearLayoutManager(this);
// Control orientation of the items
// also supports LinearLayoutManager.HORIZONTAL
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvPromo.setLayoutManager(layoutManager);
        //Decorations
      /*  RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvExtra.addItemDecoration(itemDecoration);*/
        initStrings();
        if (savedInstanceState != null) {
            code = savedInstanceState.getString("code");
            loadData();
        }
    }

    private void initStrings() {
        strings.put("group", getString(R.string.grp));
        strings.put("department", getString(R.string.department));
        strings.put("profit", getString(R.string.profit));
        strings.put("discountSupplier", getString(R.string.discountSupplier));
        strings.put("discountable", getString(R.string.discountable));
        strings.put("bonus_discount", getString(R.string.bonus_discout));
        strings.put("bonus_count", getString(R.string.bonus_count));
        strings.put("supplier", getString(R.string.supplier));
        strings.put("cost_brutto", getString(R.string.cost_brutto));
        strings.put("cost_netto", getString(R.string.cost_netto));
        strings.put("discountItem", getString(R.string.discountItem));
        strings.put("vat_sum", getString(R.string.vat_sum));
        strings.put("unit", getString(R.string.unit));
        strings.put("bonus_base", getString(R.string.bonus_base));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String str = intent.getStringExtra("itemCode");
        if (str != null) {
            code = str;
            //loadData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (code != null) {
            DatabaseHandler.Items items = new DatabaseHandler.Items(this);
            currentItem = items.retrieveItem(code, true);
            getContent(currentItem);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (code != null) {
            outState.putString("code", code);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (code == null) {
            code = savedInstanceState.getString("code");
        }
        loadData();
    }


    private void getContent(Item item) {
        if (item != null) {
            tvCode.setText(item.getCode());
            tvName.setText(item.getName());
            tvPrice.setText(String.format("%.2f", item.getPrice()));
            cbWeight.setChecked(item.isWeightable());
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            collapsingToolbar.setTitle(item.getName());
            extraAdapter.setModel(getModel(item.getExtra()));
            rvExtra.setAdapter(extraAdapter);
            promoAdapter.setModel(getPromoModels(item.getPromotions()));
            rvPromo.setAdapter(promoAdapter);
            extraAdapter.notifyDataSetChanged();
            promoAdapter.notifyDataSetChanged();
            loadBackdrop(item.getCode());
        }

    }

    private List<ItemPromoModel> getPromoModels(Collection<Promo> promotions) {
        ArrayList<ItemPromoModel> arr=new ArrayList<>();
        for(Promo promo:promotions){
            ItemPromoModel m=new ItemPromoModel();
            m.setBarcode(promo.getBarcode());
            m.setId(promo.getId());
            m.setName(promo.getName());
            m.setFromDate(promo.getFromDate());
            m.setToDate(promo.getToDate());
            arr.add(m);
        }
        return arr;
    }

    private void showItem(String code) {
        Intent intentItem = new Intent(this, ItemUpdateActivity.class);
        intentItem.putExtra("itemCode", code);
        startActivity(intentItem);
    }


    private List<AbstractExtraModel> getModel(Hashtable<String, Object> data) {
        ArrayList<AbstractExtraModel> arr = new ArrayList<>();
        for (Map.Entry<String, Object> e : data.entrySet()) {
            String name = e.getKey();
            switch (name) {
                case "group":
                    arr.add(new MapExtraModel(getString(name), Integer.valueOf(e.getValue().toString()), new DatabaseHandler.Groups(this)));
                    break;
                case "department":
                    arr.add(new MapExtraModel(getString(name), Integer.valueOf(e.getValue().toString()), new DatabaseHandler.Departments(this)));
                    break;
                case "unit":
                    arr.add(new MapExtraModel(getString(name), Integer.valueOf(e.getValue().toString()), new DatabaseHandler.Units(this)));
                    break;
                case "supplier":
                    arr.add(new MapExtraModel(getString(name), Integer.valueOf(e.getValue().toString()), new DatabaseHandler.Suppliers(this)));
                    break;
                case "discountSupplier":
                case "bonus_discount":
                case "profit":
                case "discountItem":
                    arr.add(new PercentFloatExtraModel(getString(name), Float.valueOf(e.getValue().toString())));
                    break;

                case "cost_brutto":
                case "vat_sum":
                case "bonus_count":
                case "bonus_base":
                case "cost_netto":
                    arr.add(new FloatExtraModel(getString(name), Float.valueOf(e.getValue().toString())));
                    break;

                case "discountable":
                    arr.add(new BooleanExtraModel(getString(name), "true".equalsIgnoreCase(e.getValue().toString())));
                    break;

            }
        }
        Collections.sort(arr, new Comparator<AbstractExtraModel>() {
            @Override
            public int compare(AbstractExtraModel lhs, AbstractExtraModel rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return arr;
    }

    private String getString(String key) {
        String s = strings.get(key);
        return s != null ? s : key;
    }

    private void loadBackdrop(String code) {
        File photo = Utilities.getImageFile(this,code);
        if (photo!=null) {
            /*Bitmap b=BitmapFactory.decodeFile(photo.getAbsolutePath());
            imageView.setImageBitmap(b);
            // Now change ImageView's dimensions to match the scaled image
            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) imageView.getLayoutParams();
            params.width = b.getWidth();
            params.height = b.getHeight();
            imageView.setLayoutParams(params);*/

            Glide.with(this).load(photo).centerCrop().into(imageView);
        }else
            Glide.with(this).load(R.drawable.catalogs).centerCrop().into(imageView);
    }
}
