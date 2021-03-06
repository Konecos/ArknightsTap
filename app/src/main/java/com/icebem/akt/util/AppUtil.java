package com.icebem.akt.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.icebem.akt.BuildConfig;
import com.icebem.akt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AppUtil {
    public static final String THREAD_UPDATE = "update";
    public static final String MARKET_COOLAPK = "com.coolapk.market";
    //    public static final String MARKET_PLAY = "com.android.vending";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String URL_ALIPAY_API = "intent://platformapi/startapp?saId=10000007&qrcode=https://qr.alipay.com/tsx02922ajwj6xekqyd1rbf#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
    public static final String URL_PAYPAL = "https://www.paypal.me/icebem";
    public static final String URL_QQ_API = "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3DN_OjFuCOkERq58jO2KoJEDD2a48vzB53";
    public static final String URL_MARKET = "market://details?id=" + BuildConfig.APPLICATION_ID;
    public static final String URL_PACKAGE = "package:" + BuildConfig.APPLICATION_ID;
    public static final String URL_PROJECT = "https://github.com/IcebemAst/ArknightsTap";
    public static final String URL_COOLAPK = "https://www.coolapk.com/apk/" + BuildConfig.APPLICATION_ID;
    public static final String URL_GREEN_ANDROID = "https://green-android.org/";
    public static final String URL_RELEASE_LATEST = "https://github.com/IcebemAst/ArknightsTap/releases/latest";
    public static final String URL_RELEASE_LATEST_API = "https://api.github.com/repos/IcebemAst/ArknightsTap/releases/latest";
    private static final String URL_RELEASE_DATA = "https://raw.githubusercontent.com/IcebemAst/ArknightsTap/master/app/release/output-metadata.json";

    public static boolean isLatestVersion() throws IOException, JSONException {
        int version = new JSONObject(IOUtil.stream2String(IOUtil.fromWeb(URL_RELEASE_DATA))).getJSONArray("elements").getJSONObject(0).getInt("versionCode");
        return BuildConfig.VERSION_CODE >= version;
    }

    public static String getChangelog(JSONObject json) throws JSONException {
        return json.getString("name") + System.lineSeparator() + json.getString("body");
    }

    public static String getDownloadUrl(JSONObject json) throws JSONException {
        return json.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
    }

    public static void showLogDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error_occurred);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }
}