package com.kits.ocrkowsar.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class ImageInfo {


    private final Context mContext;
    CallMethod callMethod;


    public ImageInfo(Context mContext) {
        this.mContext = mContext;
        callMethod = new CallMethod(mContext);
    }

    public void SaveLogo(Bitmap finalBitmap) {

        File dir = new File(Environment.getExternalStorageDirectory() + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/");
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
        }
        String fname ="Logo.jpg";
        File file = new File(dir, fname);
        file.setWritable(true);
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }

    }
    public Bitmap LoadLogo() {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File imagefile = new File(root + "/Kowsar/" +
                callMethod.ReadString("EnglishCompanyNameUse") + "/Logo.jpg"
        );
        return BitmapFactory.decodeFile(imagefile.getAbsolutePath());

    }




}
