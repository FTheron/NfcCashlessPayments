package za.co.rsadevelopers.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "CashlessPayments";
    static final String TRANSACTION_TABLE_NAME = "Transactions";
    static final String TRANSACTION_COLUMN_ID = "TransactionId";
    static final String TRANSACTION_COLUMN_CLIENT_ID = "ClientId";
    static final String TRANSACTION_COLUMN_VALUE = "TransactionValue";
    static final String TRANSACTION_COLUMN_DATETIME = "TransactionDateTime";
    static final String TRANSACTION_COLUMN_UPLOADED = "Uploaded";

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
