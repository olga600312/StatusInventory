package com.aviv_pos.olgats.avivinventory.dao;

/**
 * Created by olgats on 15/12/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;

import com.aviv_pos.olgats.avivinventory.beans.Promo;
import com.aviv_pos.olgats.avivinventory.beans.ZReport;
import com.aviv_pos.olgats.avivinventory.beans.Item;
import com.aviv_pos.olgats.avivinventory.beans.KeyValue;
import com.aviv_pos.olgats.avivinventory.beans.KeyValueSet;
import com.aviv_pos.olgats.avivinventory.beans.Store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DatabaseHandler {
    private final static String TAG = "DatabaseHelper";


    /**
     * CRUD
     * Create
     * Retrieve
     * Update
     * Delete
     */


    public abstract static class Master<T extends DAOHelper.MASTER> {
        private DAOHelper helper;
        protected T t;

        public Master(Context context, T t) {
            helper = new DAOHelper(context);
            this.t = t;
        }

        public T getHelper() {
            return t;
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(t.TABLE_NAME, null, null, null, null, null, null);
        }

        public Cursor getRawCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(t.TABLE_NAME, new String[]{
                    "rowid _id",
                    t.ID, t.NAME
            }, null, null, null, null, null);
        }

        public String getValue(int key) {
            String result = null;
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(t.TABLE_NAME);
            queryBuilder.appendWhere(t.ID + " = " + key);
            SQLiteDatabase db = helper.getReadableDatabase();
            String str = queryBuilder.buildQuery(new String[]{t.NAME}, null, null, null, null, null);
            Cursor cursor = queryBuilder.query(db, new String[]{t.NAME}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int valueColumn = cursor.getColumnIndex(t.NAME);
                do {
                    result = cursor.getString(valueColumn);
                }
                while (result == null && cursor.moveToNext());

            }
            cursor.close();
            return result;
        }

        public boolean setValue(int key, String value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl = false;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(t.ID, key);
                values.put(t.NAME, value);
                fl = database.insert(t.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(t.TABLE_NAME, t.ID + "=" + key, null) > 0;
            }
            return fl;
        }

        public boolean setValue(KeyValueSet set) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(t.TABLE_NAME, null, null);
            ContentValues values = new ContentValues();
            boolean fl = true;
            for (KeyValue k : set.getSet()) {
                values.put(t.ID, k.getKey());
                values.put(t.NAME, k.getValue());
                fl = fl && database.insert(t.TABLE_NAME, null, values) > 0;
                values.clear();
            }
            return fl;
        }


        public boolean replaceValue(int key, String value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(t.ID, key);
                values.put(t.NAME, value);
                fl = database.replace(t.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(t.TABLE_NAME, t.ID + "=" + key, null) > 0;
            }
            return fl;
        }

        public abstract String getName();
    }

    public static class Groups extends Master<DAOHelper.GRP> {

        public Groups(Context context) {
            super(context, new DAOHelper.GRP());
        }

        @Override
        public String getName() {
            return "groups";
        }
    }

    public static class Departments extends Master<DAOHelper.DEPARTMENT> {

        public Departments(Context context) {
            super(context, new DAOHelper.DEPARTMENT());
        }

        @Override
        public String getName() {
            return "departments";
        }
    }

    public static class Suppliers extends Master<DAOHelper.SUPPLIER> {

        public Suppliers(Context context) {
            super(context, new DAOHelper.SUPPLIER());
        }

        @Override
        public String getName() {
            return "suppliers";
        }
    }

    public static class Units extends Master<DAOHelper.UNIT> {

        public Units(Context context) {
            super(context, new DAOHelper.UNIT());
        }

        @Override
        public String getName() {
            return "units";
        }
    }


    public static class Settings {
        private DAOHelper helper;

        public Settings(Context context) {
            helper = new DAOHelper(context);
        }

        public String getValue(String key) {
            String result = null;
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DAOHelper.SETTINGS.TABLE_NAME);
            queryBuilder.appendWhere(DAOHelper.SETTINGS.ID + " = '" + key + "'");
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, new String[]{DAOHelper.SETTINGS.NAME}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int valueColumn = cursor.getColumnIndex(DAOHelper.SETTINGS.NAME);
                do {
                    result = cursor.getString(valueColumn);
                }
                while (result == null && cursor.moveToNext());

            }
            cursor.close();
            return result;
        }

        public <T> boolean setValue(String key, T value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl = false;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(DAOHelper.SETTINGS.ID, key);
                values.put(DAOHelper.SETTINGS.NAME, String.valueOf(value));
                fl = database.insert(DAOHelper.SETTINGS.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(DAOHelper.SETTINGS.TABLE_NAME, DAOHelper.SETTINGS.ID + "='" + key + "'", null) > 0;
            }
            return fl;
        }


        public <T> boolean replaceValue(String key, T value) {
            SQLiteDatabase database = helper.getWritableDatabase();
            boolean fl;
            if (value != null) {
                ContentValues values = new ContentValues();
                values.put(DAOHelper.SETTINGS.ID, key);
                values.put(DAOHelper.SETTINGS.NAME, String.valueOf(value));
                fl = database.replace(DAOHelper.SETTINGS.TABLE_NAME, null, values) > 0;
            } else {
                fl = database.delete(DAOHelper.SETTINGS.TABLE_NAME, DAOHelper.SETTINGS.ID + "='" + key + "'", null) > 0;
            }
            return fl;
        }

    }

    public static class Items {
        private DAOHelper helper;
        private ItemPromos promos;

        public Items(Context context) {
            helper = new DAOHelper(context);
            promos=new ItemPromos(context);
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DAOHelper.ITEMS.TABLE_NAME, null, null, null, null, null, null);
        }

        public List<Item> retrieve(boolean extra) {
            List<Item> items = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(DAOHelper.ITEMS.TABLE_NAME, null, null, null, null, null, DAOHelper.ITEMS.NAME);
            if (cursor.moveToFirst()) {
                final int barcodeColumn = cursor.getColumnIndex(DAOHelper.ITEMS.BARCODE);
                final int nameColumn = cursor.getColumnIndex(DAOHelper.ITEMS.NAME);
                final int priceColumn = cursor.getColumnIndex(DAOHelper.ITEMS.PRICE);
                final int weightColumn = cursor.getColumnIndex(DAOHelper.ITEMS.WEIGHT);
                do {
                    Item item = new Item();
                    item.setCode(cursor.getString(barcodeColumn));
                    item.setName(cursor.getString(nameColumn));
                    item.setPrice(cursor.getFloat(priceColumn));
                    int w = cursor.getInt(weightColumn);
                    item.setWeightable(w == 1);
                    if (extra) {
                        item.setExtra(getExtra(item.getCode()));
                        item.setPromotions(promos.retrieve(item.getCode()));
                    }
                    items.add(item);
                }
                while (cursor.moveToNext());

            }
            cursor.close();
            return items;
        }

        public Item retrieveItem(String code, boolean extra) {
            Item item = null;
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DAOHelper.ITEMS.TABLE_NAME);
            queryBuilder.appendWhere(DAOHelper.ITEMS.BARCODE + " = '" + code + "'");
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int barcodeColumn = cursor.getColumnIndex(DAOHelper.ITEMS.BARCODE);
                final int nameColumn = cursor.getColumnIndex(DAOHelper.ITEMS.NAME);
                final int priceColumn = cursor.getColumnIndex(DAOHelper.ITEMS.PRICE);
                final int weightColumn = cursor.getColumnIndex(DAOHelper.ITEMS.WEIGHT);
                do {
                    item = new Item();
                    item.setCode(cursor.getString(barcodeColumn));
                    item.setName(cursor.getString(nameColumn));
                    item.setPrice(cursor.getFloat(priceColumn));
                    int w = cursor.getInt(weightColumn);
                    item.setWeightable(w == 1);
                    if (extra) {
                        item.setExtra(getExtra(item.getCode()));
                        item.setPromotions(promos.retrieve(item.getCode()));
                    }
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return item;
        }

        public Hashtable<String, Object> getExtra(String code) {
            Hashtable<String, Object> ht = new Hashtable<>();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DAOHelper.EXTRA.TABLE_NAME);
            queryBuilder.appendWhere(DAOHelper.EXTRA.BARCODE + " = '" + code + "'");
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = queryBuilder.query(db, new String[]{DAOHelper.EXTRA.NAME, DAOHelper.EXTRA.VALUE}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int nameColumn = cursor.getColumnIndex(DAOHelper.EXTRA.NAME);
                final int valueColumn = cursor.getColumnIndex(DAOHelper.EXTRA.VALUE);
                do {
                    ht.put(cursor.getString(nameColumn), cursor.getString(valueColumn));
                }
                while (cursor.moveToNext());

            }
            cursor.close();
            return ht;
        }





        private int version;

        /**
         * Must be called from Thread or AsyncTask . do in the background!!
         *
         * @param arr the items array
         * @return
         */
        public boolean init(Item arr[]) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(DAOHelper.ITEMS.TABLE_NAME, null, null);
            database.delete(DAOHelper.EXTRA.TABLE_NAME, null, null);
            for (Item item : arr) {
                create(item);
            }
            return true;
        }

        public Item retrive(Cursor cursor, boolean extra) {
            final int barcodeColumn = cursor.getColumnIndex(DAOHelper.ITEMS.BARCODE);
            final int nameColumn = cursor.getColumnIndex(DAOHelper.ITEMS.NAME);
            final int priceColumn = cursor.getColumnIndex(DAOHelper.ITEMS.PRICE);
            final int weightColumn = cursor.getColumnIndex(DAOHelper.ITEMS.WEIGHT);

            Item item = new Item();
            item.setCode(cursor.getString(barcodeColumn));
            item.setName(cursor.getString(nameColumn));
            item.setPrice(cursor.getFloat(priceColumn));
            int w = cursor.getInt(weightColumn);
            item.setWeightable(w == 1);
            if (extra) {
                item.setExtra(getExtra(item.getCode()));
            }
            return item;
        }

        public boolean replace(Item item) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            String code = item.getCode();
            values.put(DAOHelper.ITEMS.BARCODE, code);
            values.put(DAOHelper.ITEMS.NAME, item.getName());
            values.put(DAOHelper.ITEMS.PRICE, item.getPrice());
            values.put(DAOHelper.ITEMS.WEIGHT, item.isWeightable() ? 1 : 0);

            boolean fl = database.replaceOrThrow(DAOHelper.ITEMS.TABLE_NAME, null, values) > 0;
            database.delete(DAOHelper.EXTRA.TABLE_NAME, DAOHelper.EXTRA.BARCODE + "='" + code + "'", null);
            if (fl) {
                insertExtra(item, database, values, code);
            }
            return fl;

        }

        private long insertExtra(Item item, SQLiteDatabase database, ContentValues values, String code) {
            long res = 0;
            for (Iterator<Map.Entry<String, Object>> it = item.getExtra().entrySet().iterator(); it.hasNext() && res >= 0; ) {
                Map.Entry<String, Object> entry = it.next();
                values.clear();
                values.put(DAOHelper.EXTRA.BARCODE, code);
                values.put(DAOHelper.EXTRA.NAME, entry.getKey());
                values.put(DAOHelper.EXTRA.VALUE, entry.getValue() != null ? entry.getValue().toString() : "");
                res = database.insert(DAOHelper.EXTRA.TABLE_NAME, null, values);
            }
            return res;
        }

        public Item create(Item item) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            String code = item.getCode();
            values.put(DAOHelper.ITEMS.BARCODE, code);
            values.put(DAOHelper.ITEMS.NAME, item.getName());
            values.put(DAOHelper.ITEMS.PRICE, item.getPrice());
            values.put(DAOHelper.ITEMS.WEIGHT, item.isWeightable() ? 1 : 0);
            database.insert(DAOHelper.ITEMS.TABLE_NAME, null, values);
            insertExtra(item, database, values, code);
            return item;
        }
    }


    public static class ItemPromos {
        private DAOHelper helper;

        public ItemPromos(Context context) {
            helper = new DAOHelper(context);
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DAOHelper.ITEM_PROMO.TABLE_NAME, null, null, null, null, null, null);
        }

        public List<Promo> retrieve(String barcode) {
            List<Promo> promos = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(DAOHelper.ITEM_PROMO.TABLE_NAME, null, DAOHelper.ITEM_PROMO.BARCODE+"=?", new String[]{barcode}, null, null, DAOHelper.ITEM_PROMO.ID);
            if (cursor.moveToFirst()) {
                final int barcodeColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.BARCODE);
                final int idColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.ID);
                final int nameColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.NAME);
                final int fromDateColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.FROM_DATE);
                final int toDateColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.TO_DATE);
                do {
                    Promo promo = new Promo();
                    promo.setId(cursor.getInt(idColumn));
                    promo.setBarcode(cursor.getString(barcodeColumn));
                    promo.setName(cursor.getString(nameColumn));
                    promo.setToDate(cursor.getLong(toDateColumn));
                    promo.setFromDate(cursor.getLong(fromDateColumn));
                    promos.add(promo);
                }
                while (cursor.moveToNext());

            }
            cursor.close();
            return promos;
        }


        private int version;

        /**
         * Must be called from Thread or AsyncTask . do in the background!!
         *
         * @param arr the items array
         * @return
         */
        public boolean init(Promo arr[]) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(DAOHelper.ITEM_PROMO.TABLE_NAME, null, null);
            for (Promo promo : arr) {
                create(promo);
            }
            return true;
        }

        public Promo retrive(Cursor cursor) {
            final int barcodeColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.BARCODE);
            final int idColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.ID);
            final int nameColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.NAME);
            final int fromDateColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.FROM_DATE);
            final int toDateColumn = cursor.getColumnIndex(DAOHelper.ITEM_PROMO.TO_DATE);

            Promo promo = new Promo();
            promo.setId(cursor.getInt(idColumn));
            promo.setBarcode(cursor.getString(barcodeColumn));
            promo.setName(cursor.getString(nameColumn));
            promo.setToDate(cursor.getLong(toDateColumn));
            promo.setFromDate(cursor.getLong(fromDateColumn));
            return promo;
        }

        public boolean replace(Promo promo) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DAOHelper.ITEM_PROMO.BARCODE, promo.getBarcode());
            values.put(DAOHelper.ITEM_PROMO.NAME, promo.getName());
            values.put(DAOHelper.ITEM_PROMO.ID, promo.getId());
            values.put(DAOHelper.ITEM_PROMO.FROM_DATE, promo.getFromDate());
            values.put(DAOHelper.ITEM_PROMO.TO_DATE, promo.getToDate());

            return database.replaceOrThrow(DAOHelper.ITEM_PROMO.TABLE_NAME, null, values) > 0;

        }

        public boolean clear(String code) {
            SQLiteDatabase database = helper.getWritableDatabase();
            long row=database.delete(DAOHelper.ITEM_PROMO.TABLE_NAME, DAOHelper.ITEM_PROMO.BARCODE+"=?", new String[]{code});
            return row>0;
        }

        public Promo create(Promo promo) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DAOHelper.ITEM_PROMO.BARCODE, promo.getBarcode());
            values.put(DAOHelper.ITEM_PROMO.NAME, promo.getName());
            values.put(DAOHelper.ITEM_PROMO.ID, promo.getId());
            values.put(DAOHelper.ITEM_PROMO.FROM_DATE, promo.getFromDate());
            values.put(DAOHelper.ITEM_PROMO.TO_DATE, promo.getToDate());
            long row=database.insert(DAOHelper.ITEM_PROMO.TABLE_NAME, null, values);
            return promo;
        }
    }

    public static class Stores {
        private DAOHelper helper;

        public Stores(Context context) {
            helper = new DAOHelper(context);
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DAOHelper.STORES.TABLE_NAME, null, null, null, null, null, null);
        }

        public Cursor getCursor(String orderBy) {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DAOHelper.STORES.TABLE_NAME, null, null, null, null, null, orderBy);
        }

        public List<Store> retrieve() {
            return retrieveFromCursor(getCursor(DAOHelper.STORES.NAME));
        }

        private ArrayList<Store> retrieveFromCursor(Cursor cursor) {
            ArrayList<Store> stores = new ArrayList<>();
            if (cursor.moveToFirst()) {
                int avivIdColumn = cursor.getColumnIndex(DAOHelper.STORES.AVIV_ID);
                int terminalIdColumn = cursor.getColumnIndex(DAOHelper.STORES.TERMINAL_ID);
                int nameColumn = cursor.getColumnIndex(DAOHelper.STORES.NAME);
                int addressColumn = cursor.getColumnIndex(DAOHelper.STORES.ADDRESS);
                int cityColumn = cursor.getColumnIndex(DAOHelper.STORES.CITY);
                int typeColumn = cursor.getColumnIndex(DAOHelper.STORES.TYPE);
                int chainIdColumn = cursor.getColumnIndex(DAOHelper.STORES.CHAIN_ID);
                do {
                    Store store = new Store();
                    store.setAvivId(cursor.getString(avivIdColumn));
                    store.setAddress(cursor.getString(addressColumn));
                    store.setChainId(cursor.getInt(chainIdColumn));
                    store.setCity(cursor.getString(cityColumn));
                    store.setName(cursor.getString(nameColumn));
                    store.setTerminalId(cursor.getString(terminalIdColumn));
                    store.setType(cursor.getInt(typeColumn));
                    stores.add(store);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return stores;
        }

        public Store retrieveStore(String avivId) {
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DAOHelper.STORES.TABLE_NAME);
            queryBuilder.appendWhere(DAOHelper.STORES.AVIV_ID + " = '" + avivId + "'");
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            ArrayList<Store> stores = retrieveFromCursor(cursor);
            return stores.isEmpty() ? null : stores.get(0);
        }


        private int version;

        /**
         * Must be called from Thread or AsyncTask . do in the background!!
         *
         * @param arr the items array
         * @return
         */
        public boolean init(Store arr[]) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(DAOHelper.STORES.TABLE_NAME, null, null);
            for (Store item : arr) {
                create(item);
            }
            return true;
        }

        public boolean replace(Store store) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = getContentValues(store);
            return database.replaceOrThrow(DAOHelper.STORES.TABLE_NAME, null, values) > 0;
        }

        @NonNull
        private ContentValues getContentValues(Store store) {
            ContentValues values = new ContentValues();
            values.put(DAOHelper.STORES.AVIV_ID, store.getAvivId());
            values.put(DAOHelper.STORES.ADDRESS, store.getAddress());
            values.put(DAOHelper.STORES.CHAIN_ID, store.getChainId());
            values.put(DAOHelper.STORES.CITY, store.getCity());
            values.put(DAOHelper.STORES.NAME, store.getName());
            values.put(DAOHelper.STORES.TERMINAL_ID, store.getTerminalId());
            values.put(DAOHelper.STORES.TYPE, store.getType());
            return values;
        }


        public Store create(Store store) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = getContentValues(store);
            database.insert(DAOHelper.STORES.TABLE_NAME, null, values);
            return store;
        }
    }


    public static class ZReports {
        private DAOHelper helper;
        private Context context;

        public ZReports(Context context) {
            helper = new DAOHelper(context);
            this.context = context;
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DAOHelper.Z_REPORT.TABLE_NAME, null, null, null, null, null, null);
        }

        public Cursor getCursor(String orderBy) {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DAOHelper.Z_REPORT.TABLE_NAME, null, null, null, null, null, orderBy);
        }

        public ArrayList<ZReport> getLatestZ(int account) {
            SQLiteDatabase database = helper.getReadableDatabase();
            int res = 0;
            ArrayList<ZReport> arr = new ArrayList<>();
            List<Store> storeList = new Stores(context).retrieve();
            ZReport report = null;
            for (Store store : storeList) {
                String avivId = store.getAvivId();
                Cursor c = database.query(DAOHelper.Z_REPORT.TABLE_NAME, new String[]{"MAX(" + DAOHelper.Z_REPORT.ID_Z + ")"}, DAOHelper.Z_REPORT.AVIV_ID + "='" + avivId + "'", null, null, null, null);
                if (c != null) {
                    report = null;
                    if (c.moveToFirst()) {
                        res = c.getInt(0);
                        report = res > 0 ? retrieveZReport(account, avivId, res) : null;
                    }
                    if (report == null) {
                        report = new ZReport();
                        report.setAccountId(account);
                        report.setAvivId(avivId);
                        report.setTerminalId(store.getTerminalId());
                    }
                    arr.add(report);
                    c.close();
                }
            }
            return arr;
        }


        public List<ZReport> retrieve() {
            return retrieveFromCursor(getCursor(DAOHelper.Z_REPORT.ID_Z));
        }

        public ZReport retrieveThisMonthTotal(String avivId) {
            SQLiteDatabase database = helper.getReadableDatabase();
            ZReport report = new ZReport();
            report.setAvivId(avivId);
            Cursor c = database.query(DAOHelper.Z_REPORT.TABLE_NAME, null, DAOHelper.Z_REPORT.AVIV_ID + "='" + avivId +
                    "'" + " AND strftime('%Y-%m', 'now') = strftime('%Y-%m', " + DAOHelper.Z_REPORT.DATE_CREATE + "/1000, 'unixepoch') ", null, null, null, null);

            if (c != null) {
                ArrayList<ZReport> data = retrieveFromCursor(c);
                for (ZReport r : data) {
                    report.setSum(report.getSum() + r.getSum());
                }
                c.close();
            }
            return report;
        }

        private ArrayList<ZReport> retrieveFromCursor(Cursor cursor) {
            ArrayList<ZReport> data = new ArrayList<>();
            if (cursor.moveToFirst()) {
                int t1 = cursor.getColumnIndex("t1");
                int t2 = cursor.getColumnIndex("t2");
                int accountIdColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.ACCOUNT_ID);
                int avivIdColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.AVIV_ID);
                int terminalIdColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.TERMINAL_ID);
                int idZColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.ID_Z);
                int sumColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.SUM);
                int tmCreateColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.DATE_CREATE);
                int tmMostColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.DATE_MOST_DEALS);
                int tmStartColumn = cursor.getColumnIndex(DAOHelper.Z_REPORT.DATE_START);
                do {
                    ZReport report = new ZReport();
                    if (t1 >= 0) {
                        String st1 = cursor.getString(t1);
                        String st2 = cursor.getString(t2);
                        int a = 0;
                        a = a + 1;
                    }


                    report.setAccountId(cursor.getInt(accountIdColumn));
                    report.setAvivId(cursor.getString(avivIdColumn));
                    report.setTerminalId(cursor.getString(terminalIdColumn));
                    report.setIdZ(cursor.getInt(idZColumn));
                    report.setSum(cursor.getFloat(sumColumn));
                    report.setDate(cursor.getLong(tmCreateColumn));
                    report.setDateMostDeals(cursor.getLong(tmMostColumn));
                    report.setDateStart(cursor.getLong(tmStartColumn));
                    data.add(report);
                }
                while (cursor.moveToNext());

            }
            cursor.close();
            return data;
        }

        public ZReport retrieveZReport(int accountId, String avivId, int id) {
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DAOHelper.Z_REPORT.TABLE_NAME);
            queryBuilder.appendWhere(DAOHelper.Z_REPORT.ACCOUNT_ID + " = '" + accountId + "' AND ");
            queryBuilder.appendWhere(DAOHelper.Z_REPORT.AVIV_ID + " = '" + avivId + "' AND ");
            queryBuilder.appendWhere(DAOHelper.Z_REPORT.ID_Z + " = '" + id + "'");
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            ArrayList<ZReport> data = retrieveFromCursor(cursor);
            return data.isEmpty() ? null : data.get(0);
        }


        private int version;

        /**
         * Must be called from Thread or AsyncTask . do in the background!!
         *
         * @param arr the items array
         * @return
         */
        public boolean init(Iterable<ZReport> arr, boolean add) {
            SQLiteDatabase database = helper.getWritableDatabase();
            if (add) {
                for (ZReport item : arr) {
                    create(item, SQLiteDatabase.CONFLICT_IGNORE);
                }
            } else {
                database.delete(DAOHelper.Z_REPORT.TABLE_NAME, null, null);
                for (ZReport item : arr) {
                    create(item);
                }
            }
            return true;
        }

        public boolean replace(ZReport report) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = getContentValues(report);
            return database.replaceOrThrow(DAOHelper.Z_REPORT.TABLE_NAME, null, values) > 0;
        }

        @NonNull
        private ContentValues getContentValues(ZReport report) {
            ContentValues values = new ContentValues();
            values.put(DAOHelper.Z_REPORT.ACCOUNT_ID, report.getAccountId());
            values.put(DAOHelper.Z_REPORT.AVIV_ID, report.getAvivId());
            values.put(DAOHelper.Z_REPORT.TERMINAL_ID, report.getTerminalId());
            values.put(DAOHelper.Z_REPORT.ID_Z, report.getIdZ());
            values.put(DAOHelper.Z_REPORT.SUM, report.getSum());
            values.put(DAOHelper.Z_REPORT.DATE_CREATE, report.getDate());
            values.put(DAOHelper.Z_REPORT.DATE_MOST_DEALS, report.getDateMostDeals());
            values.put(DAOHelper.Z_REPORT.DATE_START, report.getDateStart());
            return values;
        }


        public ZReport create(ZReport report) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = getContentValues(report);
            database.insert(DAOHelper.Z_REPORT.TABLE_NAME, null, values);
            return report;
        }

        public ZReport create(ZReport report, int onConflict) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = getContentValues(report);
            database.insertWithOnConflict(DAOHelper.Z_REPORT.TABLE_NAME, null, values, onConflict);
            return report;
        }
    }
    /////////////////////////////////////////


}




