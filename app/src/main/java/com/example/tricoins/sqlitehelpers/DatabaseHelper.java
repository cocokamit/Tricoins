package com.example.tricoins.sqlitehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.os.AsyncTask;

import com.example.tricoins.Api;
import com.example.tricoins.DashItems;
import com.example.tricoins.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static java.lang.Math.pow;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    public static final String DATABASE_NAME="tricoinsdb.db";
    public static final String TABLE_NAME="user_tickets";
    public static final String COL_1="Id";
    public static final String COL_2="AgentId";
    public static final String COL_3="Agent";
    public static final String COL_4="Digits";
    public static final String COL_5="Amount";
    public static final String COL_6="qrcode";
    public static final String COL_7="Description";
    public static final String COL_8="Type";
    public static final String COL_9="TimeCap";
    public static final String COL_10="Sysddate";
    public static final String COL_11="Status";
    public static String cons="0";
    public static String status="0";
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="create table "+TABLE_NAME+" (Id INTEGER PRIMARY KEY AUTOINCREMENT , AgentId TEXT, Agent TEXT, Digits TEXT, Amount TEXT, qrcode TEXT, Description TEXT,Type TEXT,TimeCap TEXT,Sysddate TEXT,Status TEXT)";
       db.execSQL(query);
       db.execSQL("create table user_accounts (Id INTEGER PRIMARY KEY AUTOINCREMENT,AccId TEXT, Fullname TEXT, Username TEXT, Password TEXT, Type TEXT, Status TEXT)");
       db.execSQL("create table winning_numbers (Id INTEGER PRIMARY KEY AUTOINCREMENT,Digits TEXT, TimeCap TEXT, Sysddate TEXT)");
        db.execSQL("create table tsettings (Id INTEGER PRIMARY KEY AUTOINCREMENT,Limiter TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query="DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(query);
        db.execSQL("DROP TABLE IF EXISTS user_accounts");
        db.execSQL("DROP TABLE IF EXISTS winning_numbers");
        db.execSQL("DROP TABLE IF EXISTS tsettings");
    }

    public boolean insertData(String AgentId,String Agent,String Digits,String Amount,String qrcode,String Description,String Type, String TimeCap)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contentValues.put(COL_2,AgentId);
        contentValues.put(COL_3,Agent);
        contentValues.put(COL_4,Digits);
        contentValues.put(COL_5,Amount);
        contentValues.put(COL_6,qrcode);
        contentValues.put(COL_7,Description);
        contentValues.put(COL_8,Type);
        contentValues.put(COL_9,TimeCap);
        contentValues.put(COL_10,dateFormat.format(currentTime));
        contentValues.put(COL_11,"1");

        long result=db.insert(TABLE_NAME,null,contentValues);

        if(result== -1)
            return false;
        else
            return true;
    }
    public boolean insertlimiter(String Limiter) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Limiter",Limiter);

        deleteAllData("tsettings");
        long result=db.insert("tsettings",null,contentValues);

        if(result== -1)
            return false;
        else
            return true;
    }
    public boolean insertwinnumber(String Digits,String TimeCap)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contentValues.put("Digits",Digits);
        contentValues.put("TimeCap",TimeCap);
        contentValues.put("Sysddate",dateFormat.format(currentTime));

        long result=db.insert("winning_numbers",null,contentValues);

        if(result== -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public Cursor getAllDataByDate(String Timecap)
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Id,AgentId, Agent, Digits,SUM(Amount),COUNT(Type) as Typecount,qrcode,Description,Type,Sysddate,TimeCap FROM user_tickets WHERE TimeCap='"+Timecap+"' AND Date(Sysddate)=Date('"+dateFormat.format(currentTime)+"') group by Digits order by Amount desc",null);
        return res;
    }
    public Cursor getAllDataByDateAndType(String Timecap,String Type)
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Id,AgentId, Agent, Digits,SUM(Amount) as Amount,COUNT(Type) as Typecount,qrcode,Description,Type,Sysddate,TimeCap,(Select Limiter from tsettings LIMIT 1) as Limits FROM user_tickets WHERE TimeCap='"+Timecap+"' AND Type lIKE '%"+Type+"%' AND Date(Sysddate)=Date('"+dateFormat.format(currentTime)+"') group by Digits order by Amount desc",null);
        return res;
    }

    public Cursor getAllDataByDateAndTypeRam(String Timecap,String Type)
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Id,AgentId, Agent, Digits,SUM(Amount) as Amount,COUNT(Type) as Typecount,qrcode,Description,Type,Sysddate,TimeCap,(Select Limiter from tsettings Limit 1) as Limits FROM user_tickets WHERE TimeCap='"+Timecap+"' AND Type lIKE '%"+Type+"%' AND Date(Sysddate)=Date('"+dateFormat.format(currentTime)+"') group by ((1<<CAST(SUBSTR (Digits, 1, 1) AS UNSIGNED)) +(1<<CAST(SUBSTR (Digits, 2, 1) AS UNSIGNED))+(1<<CAST(SUBSTR (Digits, 3, 1) AS UNSIGNED))) order by Amount desc",null);
        return res;
    }

    public Cursor getAllDataByTimeCapAndDigits(String Timecap,String Digits)
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Id,AgentId, Agent, Digits,Amount,qrcode,Description,Type,Sysddate,TimeCap FROM user_tickets WHERE TimeCap='"+Timecap+"' AND Digits='"+Digits+"' AND Date(Sysddate)=Date('"+dateFormat.format(currentTime)+"') AND Type='Straight' order by Amount desc",null);
        return res;
    }

    public Cursor getAllDataByTimeCapAndDigitsRam(String Timecap,String Digits)
    {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Id,AgentId, Agent, Digits,Amount,qrcode,Description,Type,Sysddate,TimeCap FROM user_tickets WHERE TimeCap='"+Timecap+"' AND (Digits like '%"+Digits.charAt(0)+"%' AND Digits like '%"+Digits.charAt(1)+"%' AND Digits like '%"+Digits.charAt(2)+"%') AND Date(Sysddate)=Date('"+dateFormat.format(currentTime)+"') AND Type='Rambolito' order by Amount desc",null);
        return res;
    }

    public Cursor getAllAccData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT a.AccId, a.Fullname, a.Username,a.Password,a.Status,a.Type,(SELECT COUNT(*) FROM user_tickets WHERE AgentId=a.AccId) as Collectedentry,(SELECT SUM(Amount) FROM user_tickets WHERE AgentId=a.AccId) as Collectedamount FROM user_accounts a",null);
        return res;
    }
    public Cursor getAgentStatsDetails(String Id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Sum(b.Amount) as Amount,Count(b.Id) as Entries,Date(b.Sysddate) as Sysddate FROM user_accounts a LEFT join user_tickets b on a.AccId=b.AgentId where a.AccId='"+Id+"' GROUP BY DATE(b.Sysddate) order by DATE(b.Sysddate) asc",null);
        return res;
    }
    public Cursor getAgentStatsDetailsInMonths(String Id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Sum(b.Amount) as Amount,Count(b.Id) as Entries,Date(b.Sysddate) as Sysddate FROM user_accounts a LEFT join user_tickets b on a.AccId=b.AgentId where a.AccId='"+Id+"' GROUP BY strftime('%m', b.Sysddate) order by strftime('%m', b.Sysddate) asc",null);
        return res;
    }
    public Cursor getAllAgentStatsDetailsInMonths()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Sum(b.Amount) as Amount,Count(b.Id) as Entries,Date(b.Sysddate) as Sysddate FROM user_accounts a LEFT join user_tickets b on a.AccId=b.AgentId GROUP BY strftime('%m', b.Sysddate) order by strftime('%m', b.Sysddate) asc",null);
        return res;
    }

    public Cursor getAgentStatsDetailsRecent(String Id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT b.Amount as Amount,b.Type,b.Sysddate as Sysddate FROM user_accounts a LEFT join user_tickets b on a.AccId=b.AgentId where a.AccId='"+Id+"' order by DATE(b.Sysddate) asc",null);
        return res;
    }

    public Cursor getAllAgentStatsDetails()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT a.*,Count(b.Id) as Entries,Sum(b.Amount) as Amount FROM user_accounts a LEFT join user_tickets b on a.AccId=b.AgentId GROUP BY a.AccId",null);
        return res;
    }

    public Cursor getWinningNumbers()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT a.*,"+
                "(Select Count(*) from user_tickets b where b.TimeCap=a.TimeCap and Date(b.Sysddate)=Date(a.Sysddate) AND ((b.Digits=a.Digits AND b.Type='Straight') or ((((1<<CAST(SUBSTR (b.Digits, 1, 1) AS UNSIGNED)) +(1<<CAST(SUBSTR (b.Digits, 2, 1) AS UNSIGNED))+(1<<CAST(SUBSTR (b.Digits, 3, 1) AS UNSIGNED)))=((1<<CAST(SUBSTR (a.Digits, 1, 1) AS UNSIGNED)) +(1<<CAST(SUBSTR (a.Digits, 2, 1) AS UNSIGNED))+(1<<CAST(SUBSTR (a.Digits, 3, 1) AS UNSIGNED)))) AND b.Type='Rambolito')) ) as Entries"+
                " FROM winning_numbers a  Order by datetime(a.Sysddate) desc",null);
        return res;
    }

    public Cursor getWinningAmountList(String Digits,String Timecap,String Sysddate)
    {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = null;
        try {
            currentTime = dateFormat1.parse(Sysddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String lastnani=new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Amount,Type FROM user_tickets WHERE TimeCap='"+Timecap+"' AND ((((Digits like '%"+Digits.charAt(0)+"%' AND Digits like '%"+Digits.charAt(1)+"%' AND Digits like '%"+Digits.charAt(2)+"%')) AND Type='Rambolito') or (Digits='"+Digits+"' AND Type='Straight')) AND Date(Sysddate)=Date('"+lastnani+"') order by Date(Sysddate) asc, TimeCap asc",null);
        return res;
    }


    public Cursor getsettinglimiter()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Limiter from tsettings Limit 1",null);
        return res;
    }


    public Cursor getWinningNumbersList(String Timecap,String Digits,String Sysddate)
    {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = null;
        try {
            currentTime = dateFormat1.parse(Sysddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String lastnani=new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT Id,AgentId, Agent, Digits,Amount,qrcode,Description,Type,Sysddate,TimeCap FROM user_tickets WHERE TimeCap='"+Timecap+"' AND (((Digits like '%"+Digits.charAt(0)+"%' AND Digits like '%"+Digits.charAt(1)+"%' AND Digits like '%"+Digits.charAt(2)+"%')  AND Type='Rambolito') or (Digits='"+Digits+"' AND Type='Straight')) AND Date(Sysddate)=Date('"+lastnani+"') order by Amount desc",null);
        return res;
    }


    public Cursor gettimecapwinnumbereexist(String TimeCap)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Cursor res=db.rawQuery("SELECT * from winning_numbers where TimeCap='"+TimeCap+"' and Date(Sysddate)='"+dateFormat.format(currentTime)+"'",null);
        return res;
    }
    public void updatewinnumber(String Digits,String TimeCap)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        db.execSQL("Update winning_numbers set Digits='"+Digits+"' where TimeCap='"+TimeCap+"' and Date(Sysddate)='"+dateFormat.format(currentTime)+"'");

    }


    public void deleteAllData(String Tablenamee)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("Delete from "+Tablenamee);
    }

    public String ImportData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        cons="1";
        status="0";
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_LIST_ITEMS, null, CODE_GET_REQUEST,db);
        request.execute();
        return status;
    }

    public Cursor ExportData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME+" where Status!='1'",null);
        return res;
    }


    public void Statuschanger(String Id)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_11, "1");
        db.update(TABLE_NAME,contentValues,"Id="+Id,null);
    }


    public Cursor Loginer(String Username,String Password)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT AccId,Fullname,Username,Password,Type,Status FROM user_accounts WHERE Username='"+Username+"' and Password='"+Password+"'",null);

        return res;
    }

    public Cursor Signuper(String Fullname,String Username,String Password,String Type,String Status)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM user_accounts WHERE Username='"+Username+"' and Password='"+Password+"'",null);

      /*  ContentValues contentValues=new ContentValues();
        contentValues.put("Fullname", Fullname);
        contentValues.put("Username", Username);
        contentValues.put("Password", Password);
        contentValues.put("Type", Type);
        contentValues.put("Status", Status);
        if(res.getCount()>0)
        {
            db.update("user_accounts",contentValues,"AccId="+obj.getString("Id"),null);
        }
        else
        {

        }*/

        return res;
    }




    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        SQLiteDatabase db;
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode,SQLiteDatabase db) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.db=db;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    ContentValues contentValues;
                    if(cons.equals("1"))
                    {
                        JSONArray jsonArray=object.getJSONArray("items");

                        contentValues=new ContentValues();

                     /*   for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            Cursor res=db.rawQuery("select * from "+TABLE_NAME+" where qrcode='"+obj.getString("qrcode")+"'",null);

                            int asd=res.getCount();

                            if(res.getCount()==0) {
                                contentValues.put(COL_2, obj.getString("AgentId"));
                                contentValues.put(COL_3, obj.getString("Agent"));
                                contentValues.put(COL_4, obj.getString("Digits"));
                                contentValues.put(COL_5, obj.getString("Amount"));
                                contentValues.put(COL_6, obj.getString("qrcode"));
                                contentValues.put(COL_7, obj.getString("Description"));
                                contentValues.put(COL_8, obj.getString("Type"));
                                contentValues.put(COL_9, obj.getString("TimeCap"));
                                contentValues.put(COL_10, obj.getString("Sysddate"));
                                contentValues.put(COL_11, "1");

                                long result = db.insert(TABLE_NAME, null, contentValues);
                            }
                        }*/

                        jsonArray=object.getJSONArray("users");

                        contentValues=new ContentValues();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            contentValues.put("AccId", obj.getString("Id"));
                            contentValues.put("Fullname", obj.getString("Fullname"));
                            contentValues.put("Username", obj.getString("Username"));
                            contentValues.put("Password", obj.getString("Password"));
                            contentValues.put("Type", obj.getString("Type"));
                            contentValues.put("Status", obj.getString("Status"));

                            Cursor res=db.rawQuery("select * from user_accounts where AccId='"+obj.getString("Id")+"'",null);

                            if(res.getCount()==0)
                            {
                                long result = db.insert("user_accounts", null, contentValues);
                            }
                            else
                            {
                                db.update("user_accounts",contentValues,"AccId="+obj.getString("Id"),null);
                            }
                        }


                        jsonArray=object.getJSONArray("winningno");
                        contentValues=new ContentValues();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            contentValues.put("Id", obj.getString("Id"));
                            contentValues.put("Digits", obj.getString("Digits"));
                            contentValues.put("TimeCap", obj.getString("TimeCap"));
                            contentValues.put("Sysddate", obj.getString("Sysddate"));

                            Cursor res=db.rawQuery("select * from winning_numbers where Id='"+obj.getString("Id")+"'",null);

                            if(res.getCount()==0)
                            {
                                long result = db.insert("winning_numbers", null, contentValues);
                            }
                            else
                            {
                                db.update("winning_numbers",contentValues,"Id="+obj.getString("Id"),null);
                            }
                        }


                        jsonArray=object.getJSONArray("settings");
                        contentValues=new ContentValues();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);


                            contentValues.put("Id", obj.getString("Id"));
                            contentValues.put("Limiter", obj.getString("Limiter"));

                            Cursor res=db.rawQuery("select * from tsettings where Id='"+obj.getString("Id")+"'",null);

                            if(res.getCount()==0)
                            {
                                long result = db.insert("tsettings", null, contentValues);
                            }
                            else
                            {
                                deleteAllData("tsettings");
                                long result = db.insert("tsettings", null, contentValues);
                            }
                        }


                    }
                    else if(cons.equals("2"))
                    {

                    }

                }

                cons="0";
            } catch (JSONException e) {
                e.printStackTrace();
                status="1";
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

}
