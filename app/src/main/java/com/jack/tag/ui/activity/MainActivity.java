package com.jack.tag.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.jack.tag.R;
import com.jack.tag.utils.ImgUtils;
import com.jack.tag.utils.ImgWatermarkUtil;

import com.jack.tag.utils.OpcvImgUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img, imgTest, imgTest2;
    private List<LocalMedia> selectList;

    private String path = "", filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

        //初始化
        if (OpenCVLoader.initDebug()) {
            LogUtils.e("success");
        }else  {
            LogUtils.e("fail");
        }
    }

    private void initView() {
        img = findViewById(R.id.img);
        imgTest = findViewById(R.id.img_test);
        imgTest2 = findViewById(R.id.img_test2);

        findViewById(R.id.tv_change).setOnClickListener(this);
        findViewById(R.id.tv_add).setOnClickListener(this);
        findViewById(R.id.tv_extract).setOnClickListener(this);

        findViewById(R.id.gary_test).setOnClickListener(this);
    }

    private void initData() {
        selectList = new ArrayList<>();

        Glide.with(this).load(R.mipmap.ope).into(img);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_change:
                openImageSel();
                break;
            case R.id.tv_add:
                addWaterMark();
                break;
            case R.id.tv_extract:
                extractWaterMark();
                break;
            case R.id.gary_test:
                convert2Grey();
                break;
        }
    }

    /**
     * 添加水印
     */
    private void addWaterMark() {
        try {
            Mat imgMat = Utils.loadResource(this, R.mipmap.ope); //Imgcodecs.IMREAD_COLOR /, Imgcodecs.IMREAD_COLOR
            Mat imageMat = OpcvImgUtils.addImageWatermarkWithText(imgMat, "JACK --- 6666666666666");



           // DFTUtil.getInstance().createOptimizedMagnitude(imageMat);

            Bitmap bt3 = null;
            bt3 = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(imageMat, bt3);


            File root = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "WaterMark");
            if ((root.exists() || root.mkdir()) && root.isDirectory()) {
                filePath = root.getAbsolutePath();
            }
            path = filePath + "/" + ImgUtils.getTimeStampFileName(0);
            ImageUtils.save(bt3, path,  Bitmap.CompressFormat.PNG);
            LogUtils.e(path);
            Glide.with(this).load(path).into(imgTest);


//            Mat showMat = OpcvImgUtils.getImageWatermarkWithText(imageMat);
//            Bitmap  bt4 = Bitmap.createBitmap(showMat.cols(), showMat.rows(), Bitmap.Config.RGB_565);
//            Utils.matToBitmap(showMat, bt4);
//
//
//            ImageUtils.save(bt4, path + "/" + ImgUtils.getTimeStampFileName(0),  Bitmap.CompressFormat.PNG);

            //imgTest2.setImageBitmap(bt4);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  提取水印
     */
    private void extractWaterMark() {
        try {
//            Mat imageMat = Utils.loadResource(this, R.mipmap.a);
//            Mat showMat = OpcvImgUtils.getImageWatermarkWithText(imageMat);
//            Bitmap bt3 = null;
//            bt3 = Bitmap.createBitmap(showMat.cols(), showMat.rows(), Bitmap.Config.RGB_565);
//            Utils.matToBitmap(showMat, bt3);
//            imgTest.setImageBitmap(bt3);

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Mat temp = new Mat();
            Utils.bitmapToMat(bitmap, temp);

            Mat showMat = ImgWatermarkUtil.getImageWatermarkWithText(temp);
            Bitmap  bt4 = Bitmap.createBitmap(showMat.cols(), showMat.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(showMat, bt4);

            imgTest2.setImageBitmap(bt4);
            String paths = filePath + "/" + ImgUtils.getTimeStampFileName(0);
            ImageUtils.save(bt4, paths ,  Bitmap.CompressFormat.PNG);

//            Bitmap bitmap = BitmapFactory.decodeFile(path);
//            Mat temp = new Mat();
//            Utils.bitmapToMat(bitmap, temp);
//            Mat showMat = OpcvImgUtils.getImageWatermarkWithText(temp);
//            Bitmap  bt3 = Bitmap.createBitmap(showMat.cols(), showMat.rows(), Bitmap.Config.RGB_565);
//            Utils.matToBitmap(showMat, bt3);
//            imgTest2.setImageBitmap(bt3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /** 灰度测试 代码 */
    private void convert2Grey() {
        Mat src = new Mat();//Mat是OpenCV的一种图像格式
        Mat temp = new Mat();
        Mat dst = new Mat();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ope);
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, temp, Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(dst, bitmap);
        img.setImageBitmap(bitmap);
        src.release();
        temp.release();
        dst.release();
    }


    @SuppressLint("WrongConstant")
    private void openImageSel() {
        PermissionUtils.permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        PictureSelector.create(MainActivity.this)
                                .openGallery(PictureMimeType.ofImage())
                                .selectionMode(PictureConfig.SINGLE)
                                .synOrAsy(true) //是否同步异步
                                .forResult(10086);
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {

                    }
                })
                .request();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10086) {
            selectList = PictureSelector.obtainMultipleResult(data);
            if (null != selectList && selectList.size() > 0) {
                String path = selectList.get(0).getPath();
                LogUtils.e("AXC", path);
                Glide.with(this).load(path).into(img);
            }
        }
    }
}
