package com.aviv_pos.olgats.avivinventory.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by olgats on 15/12/2015.
 */
public class DAOHelper extends SQLiteOpenHelper {
    private final static String TAG = "DAOHelper";

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "aviv_inventory.db";


    DAOHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ITEMS.TABLE_CREATE);
        Log.d(TAG, ITEMS.TABLE_CREATE);

        db.execSQL(ITEM_PROMO.TABLE_CREATE);
        Log.d(TAG, ITEM_PROMO.TABLE_CREATE);

        db.execSQL(EXTRA.TABLE_CREATE);
        Log.d(TAG, EXTRA.TABLE_CREATE);

        db.execSQL(STORES.TABLE_CREATE);
        Log.d(TAG, STORES.TABLE_CREATE);

        db.execSQL(Z_REPORT.TABLE_CREATE);
        Log.d(TAG, Z_REPORT.TABLE_CREATE);

        GRP g = new GRP();
        db.execSQL(g.TABLE_CREATE);
        Log.d(TAG, g.TABLE_CREATE);

        DEPARTMENT d = new DEPARTMENT();
        db.execSQL(d.TABLE_CREATE);
        Log.d(TAG, d.TABLE_CREATE);

        EMPLOYEE e = new EMPLOYEE();
        db.execSQL(e.TABLE_CREATE);
        Log.d(TAG, e.TABLE_CREATE);

        SUPPLIER s = new SUPPLIER();
        db.execSQL(s.TABLE_CREATE);
        Log.d(TAG, s.TABLE_CREATE);

        UNIT u = new UNIT();
        db.execSQL(u.TABLE_CREATE);
        Log.d(TAG, u.TABLE_CREATE);

        db.execSQL(SETTINGS.TABLE_CREATE);
        Log.d(TAG, SETTINGS.TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    ///////////////// Table interfaces //////////////////////////////
    public interface ITEMS {
        String TABLE_NAME = "items";

        //public final static String ID = BaseColumns._ID;
        String BARCODE = "barcode";
        String NAME = "name";
        String PRICE = "price";
        String WEIGHT = "weight";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        BARCODE + " TEXT NOT NULL PRIMARY KEY, " +
                        NAME + " TEXT NOT NULL, " +
                        PRICE + " REAL NOT NULL, " +
                        WEIGHT + " INTEGER NOT NULL DEFAULT 0" +
                        ");";
        public static final String[] COLUMNS = {
                BARCODE, NAME, PRICE, WEIGHT
        };
    }

    public interface ITEM_PROMO {
        String TABLE_NAME = "item_promo";

        //public final static String ID = BaseColumns._ID;
        String BARCODE = "barcode";
        String ID = "id";
        String NAME = "name";
        String FROM_DATE = "from_date";
        String TO_DATE = "to_date";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        BARCODE + " TEXT NOT NULL , " +
                        ID + " INTEGER NOT NULL DEFAULT 0, " +
                        NAME + " TEXT NOT NULL, " +
                        FROM_DATE + "  INTEGER NOT NULL, " +
                        TO_DATE + "  INTEGER NOT NULL" +
                        ");"+
                        " CREATE INDEX idx_barcode ON " + TABLE_NAME +
                        "(" + BARCODE + ");";
        public static final String[] COLUMNS = {
                BARCODE, ID,NAME, FROM_DATE, TO_DATE
        };
    }

    public interface STORES {
        String TABLE_NAME = "stores";

        //public final static String ID = BaseColumns._ID;
        String AVIV_ID = "aviv_id";
        String TERMINAL_ID = "terminal_id";
        String NAME = "name";
        String ADDRESS = "address";
        String CITY = "city";
        String TYPE = "type";
        String CHAIN_ID = "chain_id";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        AVIV_ID + " TEXT NOT NULL PRIMARY KEY, " +
                        TERMINAL_ID + " TEXT NOT NULL, " +
                        NAME + " TEXT NOT NULL, " +
                        ADDRESS + " TEXT NOT NULL, " +
                        CITY + " TEXT NOT NULL, " +
                        TYPE + " INTEGER NOT NULL DEFAULT 0," +
                        CHAIN_ID + " INTEGER NOT NULL DEFAULT 0" +
                        ");";
        public static final String[] COLUMNS = {
                AVIV_ID, TERMINAL_ID, NAME, ADDRESS, CITY, TYPE, CHAIN_ID
        };
    }

    public interface EXTRA {
        String TABLE_NAME = "extra";

        String BARCODE = "barcode";
        String NAME = "name";
        String VALUE = "value";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        BARCODE + " TEXT," +
                        NAME + " TEXT NOT NULL, " +
                        VALUE + " TEXT NOT NULL);" +
                        " CREATE INDEX idx_barcode ON " + TABLE_NAME +
                        "(" + BARCODE + ");";
    }

    public static abstract class MASTER {
        public String TABLE_NAME;
        public String ID = "id";
        public String NAME = "name";


        public String TABLE_CREATE;

        public MASTER(String TABLE_NAME) {
            this.TABLE_NAME = TABLE_NAME;
            TABLE_CREATE =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            ID + "  INTEGER PRIMARY KEY, " +
                            NAME + " TEXT NOT NULL);";
        }
    }

    public static class GRP extends MASTER {
        public GRP() {
            super("groups");
        }
    }


    public static class DEPARTMENT extends MASTER {
        public DEPARTMENT() {
            super("departments");
        }
    }

    public static class SUPPLIER extends MASTER {
        public SUPPLIER() {
            super("suppliers");
        }
    }

    public static class EMPLOYEE extends MASTER {
        public EMPLOYEE() {
            super("employees");
        }
    }

    public static class UNIT extends MASTER {
        public UNIT() {
            super("units");
        }
    }


    public interface SETTINGS {
        String TABLE_NAME = "settings";

        String ID = "id";
        String NAME = "name";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + "  TEXT PRIMARY KEY, " +
                        NAME + " TEXT NOT NULL);";
    }


    public interface Z_REPORT {
        String TABLE_NAME = "z_report";

        //public final static String ID = BaseColumns._ID;
        String ACCOUNT_ID="account_id";
        String AVIV_ID = "aviv_id";
        String TERMINAL_ID = "terminal_id";
        String ID_Z = "id_z";
        String SUM = "sum";
        String DATE_CREATE = "date_create";
        String DATE_MOST_DEALS = "date_most_deals";
        String DATE_START = "date_start";


        String TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ACCOUNT_ID+" INTEGER NOT NULL, "+
                        AVIV_ID + " TEXT NOT NULL , " +
                        TERMINAL_ID + " TEXT NOT NULL, " +
                        ID_Z + " INTEGER NOT NULL, " +
                        SUM + " REAL NOT NULL DEFAULT 0, " +
                        DATE_CREATE + " INTEGER NOT NULL, " +
                        DATE_MOST_DEALS + " INTEGER NOT NULL, " +
                        DATE_START + " INTEGER NOT NULL, " +
                        " PRIMARY KEY (" + ACCOUNT_ID+","+AVIV_ID + "," + ID_Z + ")" +
                        ");";
        public static final String[] COLUMNS = {
                ACCOUNT_ID,AVIV_ID, TERMINAL_ID, ID_Z, SUM, DATE_CREATE, DATE_MOST_DEALS, DATE_START
        };
    }
}
