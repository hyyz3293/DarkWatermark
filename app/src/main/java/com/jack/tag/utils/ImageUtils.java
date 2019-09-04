package com.jack.tag.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {

    /**
     * 获取文件名
     * @param wsa
     * @return
     */
    public static String getTimeStampFileName(int wsa){
        SimpleDateFormat timesdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = timesdf.format(new Date()).toString();
        String fileName = "";
        if (wsa == 0){
            fileName = timeStamp + ".png";
        }else if (wsa == 1){
            fileName = timeStamp + ".xls";
        } else if (wsa == 2) {
            fileName = timeStamp + ".jpg";
        } else if (wsa == 3) {
            fileName = timeStamp + ".txt";
        }
        return fileName;
    }


    /**
     *  保存图片
     * @param image
     * @param path
     */
    public static void saveImage(Bitmap image, String path) {
        File photoFile = new File(path);

        // 判断文件夹是否存在
        if (!photoFile.getParentFile().exists()) {
            photoFile.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (image != null) {
                if (compressImage(image).compress(Bitmap.CompressFormat.PNG,
                        100, fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e) {
            photoFile.delete();
            e.printStackTrace();
        } catch (IOException e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，0压缩到最小,把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 1000) {
            // 重置baos即清空baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        try {
            baos.close();
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
