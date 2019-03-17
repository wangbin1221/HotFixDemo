package com.example.hotfixdemo;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2019/3/16.
 */

public class BugClass {
    public void divide (Context context){
        int a = 1;
        int b = 1;
        Toast.makeText(context,String.valueOf(a/b),Toast.LENGTH_LONG).show();
    }
}
