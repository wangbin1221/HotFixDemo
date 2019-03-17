package com.example.hotfixdemo;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by Administrator on 2019/3/17.
 */

public class FixUtils {
    private static HashSet<File> sFiles;

    static {
        sFiles.clear();
    }

    public static void loadFixFile(Context context) {
        if (context == null) return;
        File dir = context.getDir(Constant.DEX_DIR, Context.MODE_PRIVATE);

        File[] file = dir.listFiles();
        for (File file1 : file) {
            if (file1.getName().endsWith(Constant.DEX_SUFFIX) && !"classes.dex".equals(file1.getName())){
                sFiles.add(file1);
            }
        }
        createDexLoader(context,dir);
    }

    private static void createDexLoader(Context context, File dir) {
        String optimizedDir = dir.getAbsolutePath()+File.separator+"opt_dex";
        File fopt = new File(optimizedDir);
        if (fopt.exists()) {
            fopt.mkdirs();
        }
        for (File file: sFiles) {
            DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath()
            ,optimizedDir,null,context.getClassLoader());
            hotfix(dexClassLoader,context);
        }
    }

    private static void hotfix(DexClassLoader dexClassLoader, Context context) {
        //获取系统的classLoader
        PathClassLoader syClassLoader =(PathClassLoader) context.getClassLoader();
        try{
            //获取系统的dexElement
            Object syElement = getElements(getPathList(syClassLoader));
            //获取我的dexElement
            Object myElement = getElements(getPathList(dexClassLoader));
            //合并成新的element
            Object resultElement = arrayCopy(myElement,syElement);
            //获取系统的pathList
            Object systPathList = getPathList(syClassLoader);
            //重新赋值pathList
            setField(systPathList,syClassLoader.getClass(),resultElement);
        }catch (Exception E){

        }

    }
    private static void setField(Object object,Class<?> clazz,Object value) throws Exception{
        Field field = clazz.getDeclaredField("dexElements");
        field.setAccessible(true);
        field.set(object,value);
    }

    private static Object getPathList(Object loader) throws Exception{
            return getField(loader,Class.forName("dalvik.system.BaseDexClassLoader"),"pathList");
    }

    private static Object getField(Object obj, Class<?> aClass, String field) throws Exception{
        Field localField = aClass.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    private static Object getElements(Object param) throws Exception{
        return getField(param,param.getClass(),"dexElements");
    }

    private static Object arrayCopy(Object source,Object target){
        Class<?> localClass = source.getClass().getComponentType();
        int i = Array.getLength(source);
        int j = i+ Array.getLength(target);
        Object result = Array.newInstance(localClass,j);
        for (int k = 0;k<j;k++){
            if (k<i){
                Array.set(result,k,Array.get(source,k));
            }else {
                Array.set(result,k,Array.get(target,k-i));
            }
        }
        return result;
    }
}
