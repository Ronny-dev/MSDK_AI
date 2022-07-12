package com.geoai.msdk_ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {

    /**
     * Save Bitmap
     *
     * @param name file name
     * @param bm   picture to save
     */

    public static void saveBitmap(String name, Bitmap bm, Context mContext) {

        Log.d("Save Bitmap", "Ready to save picture");

        //指定我们想要存储文件的地址

        String TargetPath = "/sdcard/EACC" + "/images/";

        Log.d("Save Bitmap", "Save Path=" + TargetPath);

        //判断指定文件夹的路径是否存在

        if (!fileIsExist(TargetPath)) {

            Log.d("Save Bitmap", "TargetPath isn't exist");

        } else {

            //如果指定文件夹创建成功，那么我们则需要进行图片存储操作

            File saveFile = new File(TargetPath, name);

            try {

                FileOutputStream saveImgOut = new FileOutputStream(saveFile);

                // compress - 压缩的意思

                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);

                //存储完成后需要清除相关的进程

                saveImgOut.flush();

                saveImgOut.close();

                Log.d("Save Bitmap", "The picture is save to your phone!");

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    public static boolean fileIsExist(String fileName) {

        //传入指定的路径，然后判断路径是否存在

        File file = new File(fileName);

        if (file.exists())

            return true;

        else {

            //file.mkdirs() 创建文件夹的意思

            return file.mkdirs();

        }

    }

    public static Bitmap resizeBitmap(Bitmap image, int size) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 85, out);
        float zoom = (float)Math.sqrt(size * 1024 / (float)out.toByteArray().length);

        Matrix matrix = new Matrix();
        matrix.setScale(zoom, zoom);

        Bitmap result = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        out.reset();
        result.compress(Bitmap.CompressFormat.JPEG, 85, out);
        while(out.toByteArray().length > size * 1024){
            System.out.println(out.toByteArray().length);
            matrix.setScale(0.9f, 0.9f);
            result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
            out.reset();
            result.compress(Bitmap.CompressFormat.JPEG, 85, out);
        }
        return result;
    }

    public static Bitmap centerCrop(Bitmap srcBitmap, int desWidth, int desHeight) {
//        int srcWidth = srcBitmap.getWidth();
//        int srcHeight = srcBitmap.getHeight();
//        int newWidth = srcWidth;
//        int newHeight = srcHeight;
//        float srcRate = (float) srcWidth / srcHeight;
//        float desRate = (float) desWidth / desHeight;
//        int dx = 0, dy = 0;
//        if (srcRate == desRate) {
//            return srcBitmap;
//        } else if (srcRate > desRate) {
//            newWidth = (int) (srcHeight * desRate);
//            dx = (srcWidth - newWidth) / 2;
//        } else {
//            newHeight = (int) (srcWidth / desRate);
//            dy = (srcHeight - newHeight) / 2;
//        }
        //创建目标Bitmap，并用选取的区域来绘制
        Bitmap desBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, 300, 300);
        return desBitmap;
    }

}
