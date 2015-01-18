package com.devrookie.databasebeta;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0; //What column the fields are in


    /*
     * CHANGE 1:
     */
//    // TODO: Setup your fields here: Original Code
//    public static final String KEY_NAME = "name";
//    public static final String KEY_STUDENTNUM = "studentnum";
//    public static final String KEY_FAVCOLOUR = "favcolour";
//
//    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
//    public static final int COL_NAME = 1;
//    public static final int COL_STUDENTNUM = 2;
//    public static final int COL_FAVCOLOUR = 3;


    // TODO: Setup your fields here: My Edit
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";


    // TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...) My Edit
    public static final int COL_TITLE = 1;
    public static final int COL_DESCRIPTION = 2;


    //    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_NAME, KEY_STUDENTNUM, KEY_FAVCOLOUR};
    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_TITLE, KEY_DESCRIPTION};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "MyDb";
    public static final String DATABASE_TABLE = "mainTable";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 3; //Default was 2


    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "

//			/*
//			 * CHANGE 2:
//			 */
//                    // TODO: Place your fields here! Original
//                    // + KEY_{...} + " {type} not null"
//                    //	- Key is the column name you created above.
//                    //	- {type} is one of: text, integer, real, blob
//                    //		(http://www.sqlite.org/datatype3.html)
//                    //  - "not null" means it is a required field (must be given a value).
//                    // NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
//                    + KEY_NAME + " text not null, "
//                    + KEY_STUDENTNUM + " integer not null, "
//                    + KEY_FAVCOLOUR + " string not null"
//
//                    // Rest  of creation:
//                    + ");";


                    + KEY_TITLE + " text not null, "
                    + KEY_DESCRIPTION + " text not null "
                    // Rest  of creation:
                    + ");";

    /*SQL Query Variable
    private static final String DATABASE_CREATE_SQL =
    CREATE TABLE mainTable,
    (_id, INTEGER PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL,
    "description" TEXT NOT NULL);
     */


    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////



    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database. ORIGINAL
//    public long insertRow(String name, int studentNum, String favColour) {
//		/*
//		 * CHANGE 3:
//		 */
//        // TODO: Update data in the row with new fields. ORIGINAL
//        // TODO: Also change the function's arguments to be what you need!
//        // Create row's data:
//        ContentValues initialValues = new ContentValues();
//        initialValues.put(KEY_NAME, name);
//        initialValues.put(KEY_STUDENTNUM, studentNum);
//        initialValues.put(KEY_FAVCOLOUR, favColour);
//
//        // Insert it into the database.
//        return db.insert(DATABASE_TABLE, null, initialValues);
//    }

    public long insertRow(String title, String description) {

        // Create row's data:
        ContentValues initialValues = new ContentValues();
        //Pass in the parameters
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DESCRIPTION, description);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    //Get all that matches search
    public Cursor getMatchRows(String search){
        //String where = KEY_TITLE + " like " + "'%'" + search.toLowerCase() + "'%'" ;
        String query = "Select * FROM " + DATABASE_TABLE + " WHERE "
                + KEY_TITLE + " LIKE " + "'%" + search + "%';";


        Cursor c = db.rawQuery(query, null);

        //Select * from Table where name like '%' + str + '%'


        if (c != null){
            c.moveToFirst();
        }
        return c;

        /*
        SELECT * FROM Customers
        WHERE Country LIKE '%land%';
         */

    }

//    // Change an existing row to be equal to new data.
//    public boolean updateRow(long rowId, String name, int studentNum, String favColour) {
//        String where = KEY_ROWID + "=" + rowId;
//
//		/*
//		 * CHANGE 4:
//		 */
//        // TODO: Update data in the row with new fields. ORIGINAL
//        // TODO: Also change the function's arguments to be what you need!
//        // Create row's data:
//        ContentValues newValues = new ContentValues();
//        newValues.put(KEY_NAME, name);
//        newValues.put(KEY_STUDENTNUM, studentNum);
//        newValues.put(KEY_FAVCOLOUR, favColour);
//
//        // Insert it into the database.
//        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
//    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String title, String description) {
        String where = KEY_ROWID + "=" + rowId;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TITLE, title);
        newValues.put(KEY_DESCRIPTION, description);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
