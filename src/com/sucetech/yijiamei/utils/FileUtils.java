package com.sucetech.yijiamei.utils;

import android.content.res.AssetManager;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lihh on 2018/10/4.
 */

public class FileUtils {
    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
    public static byte[] getFile(String fileName) {
        File file=new File(fileName);
        if (file.exists()){
            try {
                InputStream stream =  new FileInputStream(file);
                byte[] bytes = IOUtils.toByteArray(stream);
                return bytes;
            } catch (IOException e) {
                Log.e("LLL","getFile--IOException->"+e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

}
