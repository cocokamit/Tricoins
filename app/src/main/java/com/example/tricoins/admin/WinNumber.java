package com.example.tricoins.admin;

public class WinNumber {

    private int id,Digits;
    private String Sysddate,TimeCap,WinCount,Amount;

    public WinNumber(int id, int Digits,String TimeCap,String Sysddate,String WinCount,String Amount) {
        this.id = id;
        this.Digits = Digits;
        this.Sysddate=Sysddate;
        this.TimeCap=TimeCap;
        this.WinCount=WinCount;
        this.Amount=Amount;
    }
    public String getWinCount() { return WinCount;}

    public String getAmount() { return Amount;}

    public int getId() {
        return id;
    }

    public int getDigits() {
        return Digits;
    }

    public String getSysddate() {
        return Sysddate;
    }

    public String getTimeCap() {
        return TimeCap;
    }

}
