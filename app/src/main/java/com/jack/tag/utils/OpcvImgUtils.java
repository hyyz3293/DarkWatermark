package com.jack.tag.utils;

import android.graphics.Bitmap;

import com.jack.tag.R;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OpcvImgUtils {

    private static List<Mat> planes = new ArrayList<Mat>();
    private static List<Mat> allPlanes = new ArrayList<Mat>();

    public static Mat addImgWaterMarkTxt(Mat image, String watermarkText, Point point, Double fontSize, Scalar scalar) {
        Mat complexImage = new Mat();
        //优化图像的尺寸
        //Mat padded = optimizeImageDim(image);
        Mat padded = splitSrc(image);
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Core.merge(planes, complexImage);
        // dft
        Core.dft(complexImage, complexImage);
        Imgproc.putText(complexImage, watermarkText, point, Core.FONT_HERSHEY_DUPLEX, fontSize, scalar);
        Core.flip(complexImage, complexImage, -1);
        Imgproc.putText(complexImage, watermarkText, point, Core.FONT_HERSHEY_DUPLEX, fontSize, scalar);
        Core.flip(complexImage, complexImage, -1);
        return antitransformImage(complexImage, allPlanes);
    }

    public static Mat addImageWatermarkWithText(Mat image, String watermarkText){
        Mat complexImage = new Mat();
        //优化图像的尺寸
        //Mat padded = optimizeImageDim(image);
        Mat padded = splitSrc(image);
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Core.merge(planes, complexImage);
        // dft
        Core.dft(complexImage, complexImage);
        // 添加文本水印
        Scalar scalar = new Scalar(0, 0, 0);
        Point point = new Point(50, 50);
        Imgproc.putText(complexImage, watermarkText, point, Core.FONT_HERSHEY_DUPLEX, 1D, scalar);
        Core.flip(complexImage, complexImage, -1);
        Imgproc.putText(complexImage, watermarkText, point, Core.FONT_HERSHEY_DUPLEX, 1D, scalar);
        Core.flip(complexImage, complexImage, -1);
        return antitransformImage(complexImage, allPlanes);
    }

   
     /**
      *  
      * @ProjectName:    获取图片水印
      * @Package:        
      * @ClassName:      
      * @Description:    java类作用描述
      * @Author:         jack
      * @CreateDate:      
      * @UpdateUser:     更新者
      * @UpdateDate:      
      * @UpdateRemark:   更新内容
      * @Version:        1.0
      */
    public static Mat getImageWatermarkWithText(Mat image){
        List<Mat> planes = new ArrayList<Mat>();
        Mat complexImage = new Mat();
        Mat padded = splitSrc(image);
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Core.merge(planes, complexImage);
        // dft
        Core.dft(complexImage, complexImage);
        Mat magnitude = createOptimizedMagnitude(complexImage);
        planes.clear();
        return magnitude;
    }
    
    private static Mat splitSrc(Mat mat) {
        mat = optimizeImageDim(mat);
        Core.split(mat, allPlanes);
        Mat padded = new Mat();
        if (allPlanes.size() > 1) {
            for (int i = 0; i < allPlanes.size(); i++) {
                if (i == 0) {
                    padded = allPlanes.get(i);
                    break;
                }
            }
        } else {
            padded = mat;
        }
        return padded;
    }

    private static Mat antitransformImage(Mat complexImage, List<Mat> allPlanes) {
        Mat invDFT = new Mat();
        Core.idft(complexImage, invDFT, Core.DFT_SCALE | Core.DFT_REAL_OUTPUT, 0);
        Mat restoredImage = new Mat();
        invDFT.convertTo(restoredImage, CvType.CV_8U);
        if (allPlanes.size() == 0) {
            allPlanes.add(restoredImage);
        } else {
            allPlanes.set(0, restoredImage);
        }
        Mat lastImage = new Mat();
        Core.merge(allPlanes, lastImage);
        return lastImage;
    }

     /**
      *
      * @ProjectName:    为加快傅里叶变换的速度，对要处理的图片尺寸进行优化
      * @Package:
      * @ClassName:
      * @Description:    java类作用描述
      * @Author:         jack
      * @CreateDate:
      * @UpdateUser:     更新者
      * @UpdateDate:
      * @UpdateRemark:   更新内容
      * @Version:        1.0
      */
    private static Mat optimizeImageDim(Mat image) {
        Mat padded = new Mat();
        int addPixelRows = Core.getOptimalDFTSize(image.rows());
        int addPixelCols = Core.getOptimalDFTSize(image.cols());
        Core.copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
                Core.BORDER_CONSTANT, Scalar.all(0));

        return padded;
    }

    private static Mat createOptimizedMagnitude(Mat complexImage) {
        List<Mat> newPlanes = new ArrayList<Mat>();
        Mat mag = new Mat();
        Core.split(complexImage, newPlanes);
        Core.magnitude(newPlanes.get(0), newPlanes.get(1), mag);
        Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
        Core.log(mag, mag);
        shiftDFT(mag);
        mag.convertTo(mag, CvType.CV_8UC1);
        Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        return mag;
    }

    private static void shiftDFT(Mat image) {
        image = image.submat(new Rect(0, 0, image.cols() & -2, image.rows() & -2));
        int cx = image.cols() / 2;
        int cy = image.rows() / 2;

        Mat q0 = new Mat(image, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(image, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(image, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(image, new Rect(cx, cy, cx, cy));

        Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }



    public void test() {
        try {
//            Mat imageMat = Utils.loadResource(this, R.mipmap.ddd);
//            Mat showMat = OpcvImgUtils.getImageWatermarkWithText(imageMat);
//            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.a);
//            Utils.matToBitmap(showMat, bm);
//            imgTest.setImageBitmap(bm);

            // 初始化数据
            //Mat mat1 = new Mat();
//            Mat mat2 = new Mat();
//            Mat mat1Sub = new Mat();
//
//            // 加载图片
//            Bitmap bt1 = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.im_beauty);
//            Bitmap bt2 = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.im_flower3);
            //Bitmap bt3 = null;

//            // 转换数据
//            Utils.bitmapToMat(bt1, mat1);
//            Utils.bitmapToMat(bt2, mat2);
//
//            /** 方法一加权 高级方式 可实现水印效果*********/
//
//            // mat1Sub=mat1.submat(0, mat2.rows(), 0, mat2.cols());
//            // Core.addWeighted(mat1Sub, 1, mat2, 0.3, 0., mat1Sub);
//
//            /** 方法二 求差 ********/
//
//            // submat(y坐标, 图片2的高, x坐标，图片2的宽);
//            // mat1Sub=mat1.submat(0, mat2.rows(), 0, mat2.cols());
//            // mat2.copyTo(mat1Sub);
//
//            /*** 方法三兴趣区域裁剪 **/
//            // 定义感兴趣区域Rect(x坐标，y坐标,图片2的宽，图片2的高)
//            Rect rec = new Rect(0, 0, mat2.cols(), mat2.rows());
//            // submat(y坐标, 图片2的高, x坐标，图片2的宽);
//            mat1Sub = mat1.submat(rec);
//            mat2.copyTo(mat1Sub);
            //转化为android识别的图像数据注意bt3的宽高要和mat1一至

//            Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(src, processedImage);

//            bt3 = Bitmap.createBitmap(showMat.cols(), showMat.rows(), Bitmap.Config.RGB_565);
//            Utils.matToBitmap(showMat, bt3);
            //imgTest.setImageBitmap(bt3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
