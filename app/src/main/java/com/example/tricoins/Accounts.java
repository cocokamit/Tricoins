package com.example.tricoins;

public class Accounts {
    private int id;
    private String Fullname, Username,Password,Status,Type,Collectedentry,Collectedamount;

    public Accounts(int id, String Fullname, String Username, String Password,String Status,String Type,String Collectedentry,String Collectedamount) {
        this.id = id;
        this.Fullname = Fullname;
        this.Username = Username;
        this.Password = Password;
        this.Status=Status;
        this.Type=Type;
        this.Collectedentry=Collectedentry;
        this.Collectedamount=Collectedamount;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return Fullname;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getStatus() {
        return Status;
    }

    public String getType() {
        return Type;
    }

    public String getCollectedentry() {return Collectedentry;}

    public String getCollectedamount() {return Collectedamount;}
}
