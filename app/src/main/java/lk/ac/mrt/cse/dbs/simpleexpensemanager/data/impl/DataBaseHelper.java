package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static DataBaseHelper dbHelper;
    private DataBaseHelper(@Nullable Context context) {
        super(context, "Acc&Transactions.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createACCTable = "CREATE TABLE ACCOUNT (ACCOUNT_NUM TEXT PRIMARY KEY, BANK TEXT, ACC_HOLDER TEXT, ACC_BALANCE REAL)";
        sqLiteDatabase.execSQL(createACCTable);
        String createTransactionTable = "CREATE TABLE TRANSACTIONS (ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, TYPE TEXT, AMOUNT REAL, ACCOUNT_NUM TEXT, FOREIGN KEY (ACCOUNT_NUM) REFERENCES ACCOUNT (ACCOUNT_NUM))";
        sqLiteDatabase.execSQL(createTransactionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNT");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TRANSACTIONS");
        onCreate(sqLiteDatabase);
    }
    public static DataBaseHelper getDBHelper(Context context){
        if (DataBaseHelper.dbHelper ==null){
            DataBaseHelper.dbHelper = new DataBaseHelper(context);
        }
        return DataBaseHelper.dbHelper;
    }
}
