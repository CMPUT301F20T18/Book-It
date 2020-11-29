package com.example.cmput301f20t18;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;

/**
 * A class used to reformat variable representing photos into different types
 * Referenced https://www.baeldung.com/java-base64-image-string
 * Referenced https://stackoverflow.com/questions/7620401/how-to-convert-byte-array-to-bitmap
 * @author Sean Butler
 */

public class photoAdapter {
    
    /**
     * Converts a byte array representing a photo to a String
     * @param photo The byte[] object representation of a photo
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String byteToString(byte[] photo){
        return Base64
                .getEncoder()
                .encodeToString(photo);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap stringToBitmap(String photo){
        return byteToBitmap(stringToByte(photo));
    }

    public static Bitmap byteToBitmap(byte[] photo){
        return BitmapFactory.decodeByteArray(photo, 0, photo.length);
    }

    /**
     * Converts a String representing a photo to a byte array
     * @param photo The String object representation of a photo
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] stringToByte(String photo){
        return Base64.getDecoder().decode(photo);
    }

    /**
     * Converts a Bitmap representing a photo to a byte array
     * @param bitmap The Bitmap object representation of a photo
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteToString(byteArray);

    }

    /**
     * Takes in a Bitmap and rescales it with a new height and width. Also makes it mutable.
     * @param bitmap The Bitmap object representation of a photo
     * @param newWidth The new width of the Bitmap
     * @param newHeight The new height of the Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float newWidth, float newHeight){
        if(bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            float scaleWidth = newWidth / width;
            float scaleHeight = newHeight / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            Bitmap finalMap = Bitmap
                    .createBitmap(bitmap, 0, 0, width, height, matrix, false)
                    .copy(Bitmap.Config.ARGB_8888, true);

            return finalMap;
        }
        else{
            return null;
        }

    }

    /**
     * https://stackoverflow.com/questions/14050813/how-to-make-an-image-fit-into-a-circular-frame-in-android
     */

    public static Bitmap makeCircularImage(Bitmap bitmap, int r) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, r, r, true);
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, r, r);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle((r/2), (r/2), (r/2), paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(resized, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }
}
