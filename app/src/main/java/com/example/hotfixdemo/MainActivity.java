package com.example.hotfixdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //String[] permissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            /*for (String per : permissionList){
                if (checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED){
                    //requestPermissions(permissionList,110);
                    permissionList.ad
                }
            }*/
            List<String> permissionList = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                //checkSelfPermission(permissionList.toArray(),110);
                requestPermissions(permissionList.toArray(new String[permissionList.size()]),110);
            }

            //if (checkSelfPermission(permissionList) == )
        }
    }

    public void jump(View view) {
        startActivity(new Intent(MainActivity.this,Main2Activity.class));
    }

    public void fix(View view) {
        //源文件
        File source = new File(Environment.getExternalStorageDirectory(),Constant.DEX_NAME);
        //
        File target = new File(getDir(Constant.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath()+"" +
                File.separator+Constant.DEX_NAME);
        if (target.exists()){
            target.delete();
        }
        try {
            copyFile(source,target);
            Toast.makeText(getApplicationContext(),"copy done",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FixUtils.loadFixFile(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void copyFile(File sourceFile,File targetFile) throws IOException{
        FileInputStream inputStream = new FileInputStream(sourceFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
        byte[] b = new byte[1024*5];
        int len;
        while((len = bufferedInputStream.read(b))!= -1){
            bufferedOutputStream.write(b,0,len);
        }
        bufferedOutputStream.flush();
        inputStream.close();
        bufferedInputStream.close();
        bufferedOutputStream.close();
    }
}
