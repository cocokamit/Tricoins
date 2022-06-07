package com.example.tricoins.admin;

public class AgentStatDetails {

    private String Amount,Entries, Sysddate;

    public AgentStatDetails( String Amount,String Entries,String Sysddate) {

        this.Amount = Amount;
        this.Sysddate=Sysddate;
        this.Entries=Entries;
    }

    public String getAmount() {
        return Amount;
    }

    public String getSysddate() {
        return Sysddate;
    }

    public String getEntries(){return Entries;}
}