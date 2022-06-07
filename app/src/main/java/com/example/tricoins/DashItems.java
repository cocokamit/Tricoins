package com.example.tricoins;

public class DashItems {
    private int id,AgentId,Limiters;
    private String Agent,Digits,Amount, qrcode,Description,Sysddate,TimeCap,MatchType,Typecount;

    public DashItems(int id, int AgentId ,String Agent, String Digits, String Amount,String Typecount, String qrcode,String Description,String MatchType,String Sysddate,String TimeCap,int Limiters) {
        this.id = id;
        this.AgentId = AgentId;
        this.Agent = Agent;
        this.Digits = Digits;
        this.Amount = Amount;
        this.qrcode = qrcode;
        this.Description=Description;
        this.MatchType=MatchType;
        this.Sysddate=Sysddate;
        this.TimeCap=TimeCap;
        this.Typecount=Typecount;
        this.Limiters=Limiters;
    }

    public int getId() {
        return id;
    }

    public String getAgent() {
        return Agent;
    }

    public String getDigits() {
        return Digits;
    }

    public String getAmount() {
        return Amount;
    }

    public String getqrcode() {
        return qrcode;
    }

    public int getAgentId() {
        return AgentId;
    }

    public String getDescription() {
        return Description;
    }

    public String getSysddate() {
        return Sysddate;
    }

    public String getTimeCap() {
        return TimeCap;
    }

    public String getMatchType() {
        return MatchType;
    }

    public String getTypecount(){return Typecount;}

    public int getLimiters() {
        return Limiters;
    }

}
