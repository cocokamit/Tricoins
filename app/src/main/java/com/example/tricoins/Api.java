package com.example.tricoins;

public class Api {
    private static final String ROOT_URL = "https://meshnetworksinc.com/Tricoinsphp/v1/Api.php?apicall=";

    public static final String URL_CREATE_ACCOUNT = ROOT_URL + "createaccount";
    public static final String URL_READ_ACOUNT = ROOT_URL + "getusers";
    public static final String URL_UPDATE_ACCOUNT = ROOT_URL + "updateaccount";
    public static final String URL_DELETE_ACCOUNT = ROOT_URL + "deleteaccount&id=";
    public static final String URL_READ_ACCOUNT_BY_ID = ROOT_URL + "readaccount&id=";
    public static final String URL_READ_ACCOUNT_BY_LOGIN = ROOT_URL + "getuserbylogin";
    public static final String URL_LIST_ITEMS = ROOT_URL + "getlistofitems";
    public static final String URL_LIST_ITEMS_BY_ID = ROOT_URL + "getlistofitemsbyid&id=";
    public static final String URL_CREATE_ITEM = ROOT_URL + "createItem";
    public static final String URL_LIST_ITEMS_BY_TIMECAP=ROOT_URL+"getlistofitemsbyTimeCap&TimeCap=";
    public static final String URL_LIST_ITEMS_BY_TIMECAP_AND_DIGITS=ROOT_URL+"getlistofitemsbyTimeCapAndDigits";
    public static final String URL_LIST_AGENT_STAT_DETAILS=ROOT_URL+"getAgentStatsDetails";
    public static final String URL_LIST_ALL_AGENT_STAT_DETAILS=ROOT_URL+"getAllAgentStatsDetails";
    public static final String URL_CREATE_WINNINGNUMBER=ROOT_URL+"createwinnumber";
    public static final String URL_UPDATE_LIMITER=ROOT_URL+"updatelimiter";
    public static final String URL_UPDATE_WIN_NUMBERS=ROOT_URL+"updatewinnumbery";
}
