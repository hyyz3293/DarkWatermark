package com.jack.tag.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImgUtils {

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
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */
    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap( drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
