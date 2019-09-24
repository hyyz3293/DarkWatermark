package com.jack.tag.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.jack.tag.R;
import com.jack.tag.utils.ImgUtils;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class TxActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tx);

        mImgView = findViewById(R.id.image_view);

//        Mat img = new Mat();
//        Bitmap bm = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/10,bitmap.getHeight()/10,true);
//        Utils.bitmapToMat(bm, img);
//        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
//        Mat dst = new Mat(Utils.grabCutFromJNI(img.nativeObj,64,10,230,300));
//        //Imgproc.grabCut(img.nativeObj,64,10,230,300)
//        Bitmap b = Bitmap.createBitmap(bitmap.getWidth()/10,bitmap.getHeight()/10, Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(dst,b);
//        mImgView.setImageBitmap(b);

        //初始化
        if (OpenCVLoader.initDebug()) {
            LogUtils.e("success");
        }else  {
            LogUtils.e("fail");
        }

        bitmap = ImgUtils.drawableToBitmap(getResources().getDrawable(R.drawable.icon_t_a));

        Mat img = new Mat();
        //缩小图片尺寸
        Bitmap bm = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/10,bitmap.getHeight()/10,true);
        //bitmap->mat
        Utils.bitmapToMat(bm, img);
        //转成CV_8UC3格式
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2RGB);
        //设置抠图范围的左上角和右下角
        Point tl=new Point(64, 10);
        Point br=new Point(230, 300);
        Rect rect = new Rect(tl, br);

        //生成遮板
        Mat firstMask = new Mat();
        Mat bgModel = new Mat();
        Mat fgModel = new Mat();
        Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
        Imgproc.grabCut(img, firstMask, rect, bgModel, fgModel,5, Imgproc.GC_INIT_WITH_RECT);
        Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

        //抠图
        Mat foreground = new Mat(img.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        img.copyTo(foreground, firstMask);

        //mat->bitmap
        Bitmap b = Bitmap.createBitmap(bitmap.getWidth()/10,bitmap.getHeight()/10, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(foreground,b);

        mImgView.setImageBitmap(b);




    }




}
