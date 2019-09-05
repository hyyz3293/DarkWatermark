# DarkWatermark

Android opcv 添加暗水印



![image](https://github.com/hyyz3293/DarkWatermark/blob/master/images/%E5%8A%A0%E6%B0%B4%E5%8D%B0%E5%90%8E.png)![image](https://github.com/hyyz3293/DarkWatermark/blob/master/images/%E8%A7%A3%E6%9E%90.png)



https://opencv.org/opencv-3-4-1/

第一步：android studio 配置 opcv环境
     
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

 第二步：


