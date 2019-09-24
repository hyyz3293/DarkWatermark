# DarkWatermark

Android opcv 添加暗水印



![image](https://github.com/hyyz3293/DarkWatermark/blob/master/images/%E5%8A%A0%E6%B0%B4%E5%8D%B0%E5%90%8E.png)![image](https://github.com/hyyz3293/DarkWatermark/blob/master/images/%E8%A7%A3%E6%9E%90.png) ![image](https://github.com/hyyz3293/DarkWatermark/blob/master/images/cccc.png) 




第一步：android studio 配置 opcv环境  https://opencv.org/opencv-3-4-1/
     
    OPCV 环境配置：
    a:下载安卓opcv：  https://opencv.org/opencv-3-4-1/  （我用的是openCVLibrary341）
     
    b:将下载好后的（sdk\java）    导入 android studio 项目中： File ->  New -> Import Module  
    
    c:删除module中的  <uses-sdk android:minSdkVersion="16" android:targetSdkVersion="21" />  
      更改项目项目配置 与你的一样
    d:将module中libs里面的架包 （OpenCV-android-sdk\sdk\native\libs） 拷贝入自己的项目中 
     
    e：自己项目加入配置 让app包，与新添加的这个OpenCV库关联
       implementation project(':openCVLibrary341')
         sourceSets {
         main {
            //说明so的路径为该libs路径，关联所有地图SDK的so文件
            jniLibs.srcDir 'libs'
        }
      }
      
     f: 加入 //以下很重要
      //将添加的.so文件，打包成jar
      task nativeLibsToJar(type: Jar, description: 'create a jar archive of the native libs') {
          destinationDir file("$buildDir/native-libs")
          baseName 'native-libs'
          from fileTree(dir: 'libs', include: '**/*.so')
          into 'lib/'
      }
      tasks.withType(JavaCompile) {
          compileTask -> compileTask.dependsOn(nativeLibsToJar)
      }

 第二步：判断Opcv是否集成成功
         //初始化
        if (OpenCVLoader.initDebug()) 
        
        可根据灰色测试按钮测试
        
        Mat src = new Mat();//Mat是OpenCV的一种图像格式
        Mat temp = new Mat();
        Mat dst = new Mat();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.a);
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, temp, Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(dst, bitmap);
        img.setImageBitmap(bitmap);
        src.release();
        temp.release();
        dst.release();
        
  
  第三步：  开始写代码了 
  
  
        1、添加水印 并保存图片到本地哟

        Bitmap bt = ImgUtils.drawableToBitmap(getResources().getDrawable(R.mipmap.a));
        Mat src = new Mat(bt.getHeight(), bt.getWidth(), CvType.CV_8U);
        Utils.bitmapToMat(bt, src);

        Mat imageMat = OpcvImgUtils.addImageWatermarkWithText(src, "Jack666");
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
       
       
       
       2、从本地图片中 提取水印哟
       
             Bitmap bitmap = BitmapFactory.decodeFile(path);
            Mat temp = new Mat();
            Utils.bitmapToMat(bitmap, temp);

            Mat showMat = OpcvImgUtils.getImageWatermarkWithText(temp);
            Bitmap  bt4 = Bitmap.createBitmap(showMat.cols(), showMat.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(showMat, bt4);

            imgTest2.setImageBitmap(bt4);
            String paths = filePath + "/" + ImgUtils.getTimeStampFileName(0);
            ImageUtils.save(bt4, paths ,  Bitmap.CompressFormat.PNG);
              Bitmap bitmap = BitmapFactory.decodeFile(path);
            Mat temp = new Mat();
            Utils.bitmapToMat(bitmap, temp);

            Mat showMat = OpcvImgUtils.getImageWatermarkWithText(temp);
            Bitmap  bt4 = Bitmap.createBitmap(showMat.cols(), showMat.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(showMat, bt4);

            imgTest2.setImageBitmap(bt4);
            String paths = filePath + "/" + ImgUtils.getTimeStampFileName(0);
            ImageUtils.save(bt4, paths ,  Bitmap.CompressFormat.PNG);
       
--------------------------------------------------------------------------------------------------------------------------------------    
       
       OpcvImgUtils.java
       
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
        //Imgcodecs.IMREAD_COLOR-->BORDER_CONSTANT
        Core.copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
                Core.BORDER_REPLICATE, Scalar.all(0));

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
    
    
    
    ImgUtils.java
    
    
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
        
        
         
        
        
        
           


