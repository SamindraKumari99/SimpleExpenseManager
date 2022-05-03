package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

public class PersistentAccountDAO implements AccountDAO {
    private final DataBaseHelper dbHelper;
    public PersistentAccountDAO(Context context){
        this.dbHelper = DataBaseHelper.getDBHelper(context);
    }
    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String[] projection = {"ACCOUNT_NUM"};
        Cursor cursor = db.query("ACCOUNT",projection,null,null,null,null,null);
        List accNums = new ArrayList<>();
        while(cursor.moveToNext()){
            String accNum = cursor.getString(cursor.getColumnIndexOrThrow("ACCOUNT_NUM"));
            accNums.add(accNum);
        }
        cursor.close();
        db.close();
        return accNums;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query("ACCOUNT",null,null,null,null,null,null);
        List accs = new ArrayList<>();
        while(cursor.moveToNext()){
            String accNum = cursor.getString(0);
            String bank = cursor.getString(1);
            String accHolder = cursor.getString(2);
            double balance = cursor.getDouble(3);
            accs.add(new Account(accNum,bank,accHolder,balance));
        }
        cursor.close();
        db.close();
        return accs;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String[] selectionArgs = {accountNo};
        Cursor cursor = db.query("ACCOUNT",null,"ACCOUNT_NUM = ?", selectionArgs,null,null,null);
        if(!cursor.moveToFirst()){
            String msg = "Account" + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        String accNum = cursor.getString(0);
        String bank = cursor.getString(1);
        String accHolder = cursor.getString(2);
        double balance = cursor.getDouble(3);
        cursor.close();
        db.close();
        return new Account(accNum,bank,accHolder,balance);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ACCOUNT_NUM", account.getAccountNo());
        cv.put("BANK", account.getBankName());
        cv.put("ACC_HOLDER",account.getAccountHolderName());
        cv.put("ACC_BALANCE",account.getBalance());
        db.insert("ACCOUNT",null,cv);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String[] selectionArgs = {accountNo};
        int rows = db.delete("ACCOUNT","ACCOUNT_NUM = ?",selectionArgs);
        if(rows<1){
            String msg = "Account" + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {"ACC_BALANCE"};
        String[] selectionArgs = {accountNo};
        Cursor cursor = db.query("ACCOUNT",projection,"ACCOUNT_NUM = ?", selectionArgs,null,null,null);
        if(!cursor.moveToFirst()){
            String msg = "Account" + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        double accBalance = cursor.getDouble(cursor.getColumnIndexOrThrow("ACC_BALANCE"));

        switch(expenseType){
            case INCOME:
               accBalance = accBalance + amount;
               break;
            case EXPENSE:
               accBalance = accBalance - amount;
               break;
        }
        cursor.close();
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ACC_BALANCE", accBalance);
        String[] selectArgs = {accountNo};
        db.update("ACCOUNT", cv, "ACCOUNT_NUM = ?", selectArgs);
        db.close();
    }
}
