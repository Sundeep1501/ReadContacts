package com.contacts.sundeep.readcontacts;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sundeep on 3/5/16.
 */
public class Contact {
    long id;
    String name;
    String email;
    String accountType;
    List<String> phoneno;
    long rawId;

    public Contact() {
        phoneno = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getPhoneno() {
        return phoneno;
    }

    public void addPhoneno(String phoneno) {
        this.phoneno.add(phoneno);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public long getRawId() {
        return rawId;
    }

    public void setRawId(long rawId) {
        this.rawId = rawId;
    }

    @Override
    public String toString() {
        return "ID: " + getId() +
                "\nRaw ID: " + getRawId() +
                "\nName: " + getName() +
                "\nAccount: " + getEmail() +
                "\nType: " + getAccountType() +
                "\nPhone No: " + getPhoneNosAsString();
    }

    public String getPhoneNosAsString() {
        StringBuilder stringBuffer = new StringBuilder();
        for (String phno :
                getPhoneno()) {
            stringBuffer.append(",").append(phno);
        }
        String string = stringBuffer.toString();
        if (TextUtils.isEmpty(string)) {
            return string;
        }
        return string.substring(1);
    }
}
