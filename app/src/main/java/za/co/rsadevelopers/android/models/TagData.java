package za.co.rsadevelopers.android.models;

import java.math.BigDecimal;

import za.co.rsadevelopers.android.helpers.Helper;

public class TagData {
    public TagData(){
    }

    public TagData(String clientId, BigDecimal balance){
        ClientId = clientId;
        Balance = balance;
    }

    public String ToMessage(){
        return ClientId + Helper.getPaddedStringAmount(Balance);
    }

    public String ClientId;
    public BigDecimal Balance;
}
