package za.co.rsadevelopers.android.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "CashlessPayments";
    public static final String TRANSACTION_TABLE_NAME = "Transactions";
    public static final String TRANSACTION_COLUMN_ID = "TransactionId";
    public static final String TRANSACTION_COLUMN_CLIENT_ID = "ClientId";
    public static final String TRANSACTION_COLUMN_VALUE = "TransactionValue";
    public static final String TRANSACTION_COLUMN_DATETIME = "TransactionDateTime";
    public static final String TRANSACTION_COLUMN_UPLOADED = "Uploaded";

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TRANSACTION_TABLE_NAME + " (" +
                TRANSACTION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TRANSACTION_COLUMN_CLIENT_ID + " TEXT, " +
                TRANSACTION_COLUMN_VALUE + " INTEGER, " +
                TRANSACTION_COLUMN_DATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TRANSACTION_COLUMN_UPLOADED + " INTEGER DEFAULT 0" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
