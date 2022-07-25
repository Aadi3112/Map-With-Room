package com.example.mapwithtab.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog {
    static ProgressDialog progressDialog;


    public static void showLoadingDialog(Context context) {

        if (!(progressDialog != null && progressDialog.isShowing())) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");

            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();
        }
    }

    public static void cancelLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.cancel();
    }
}
