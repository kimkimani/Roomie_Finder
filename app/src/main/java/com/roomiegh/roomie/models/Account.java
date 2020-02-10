package com.roomiegh.roomie.models;

import java.io.Serializable;

/**
 * Created by KayO on 21/05/2017.
 */

public class Account implements Serializable {
    String accountNum,accountName,bank;
    int id;

    public Account() {
    }

    public Account(String accountNum, String accountName, String bank, int id) {
        this.accountNum = accountNum;
        this.accountName = accountName;
        this.bank = bank;
        this.id = id;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != account.id) return false;
        if (accountNum != null ? !accountNum.equals(account.accountNum) : account.accountNum != null)
            return false;
        if (accountName != null ? !accountName.equals(account.accountName) : account.accountName != null)
            return false;
        return bank != null ? bank.equals(account.bank) : account.bank == null;

    }

    @Override
    public int hashCode() {
        int result = accountNum != null ? accountNum.hashCode() : 0;
        result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
        result = 31 * result + (bank != null ? bank.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountNum='" + accountNum + '\'' +
                ", accountName='" + accountName + '\'' +
                ", bank='" + bank + '\'' +
                ", id=" + id +
                '}';
    }
}
