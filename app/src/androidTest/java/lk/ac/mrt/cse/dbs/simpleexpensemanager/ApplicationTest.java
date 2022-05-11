/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static org.junit.Assert.assertTrue;

public class ApplicationTest {
    private ExpenseManager expenseManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        expenseManager = new PersistentExpenseManager(context);
    }

    @Test
    public void testAddAccount() {

        expenseManager.addAccount("1234ABC", "BOC", "Samindra", 150.0);
        List<String> accNumbers = expenseManager.getAccountNumbersList();
        assertTrue(accNumbers.contains("1234ABC"));
    }
    @Test
    public void testUpdateAccBalance() throws InvalidAccountException {
        expenseManager.addAccount("1234ABC", "BOC", "Samindra", 150.0);
        expenseManager.updateAccountBalance("1234ABC", 11, 4, 2022, ExpenseType.valueOf("EXPENSE"), "50.0");
        List<Transaction> transactionLogs = expenseManager.getTransactionLogs();
        Transaction LastTransaction = transactionLogs.get(transactionLogs.size() - 1);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(LastTransaction.getDate());
        assertTrue(strDate.equals("11-05-2022") && LastTransaction.getAccountNo().equals("1234ABC") && LastTransaction.getExpenseType().toString().equals("EXPENSE") && LastTransaction.getAmount() ==  50.0);
    }
}