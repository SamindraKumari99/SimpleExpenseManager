package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final DataBaseHelper dbHelper;
    public PersistentTransactionDAO(Context context){
        this.dbHelper = DataBaseHelper.getDBHelper(context);
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);
        cv.put("DATE", strDate);
        cv.put("TYPE", String.valueOf(expenseType));
        cv.put("AMOUNT",amount);
        cv.put("ACCOUNT_NUM",accountNo);
        db.insert("TRANSACTIONS",null,cv);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query("TRANSACTIONS",null,null,null,null,null,null);
        List transactions = new ArrayList<>();
        while(cursor.moveToNext()){
            String date = cursor.getString(1);
            String type = cursor.getString(2);
            double amount = cursor.getDouble(3);
            String accNum = cursor.getString(4);
            SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
            Date d = null;
            try {
                d = format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            transactions.add(new Transaction(d,accNum,ExpenseType.valueOf(type),amount));
        }
        cursor.close();
        db.close();
        return transactions;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List transactions = this.getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        return transactions.subList(size - limit, size);
    }
}
