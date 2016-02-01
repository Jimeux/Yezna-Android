package com.moobasoft.yezna.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.moobasoft.yezna.R;

public class ImageUtil {

    private ImageUtil() {
    }

    public static final int REQUEST_SELECT_IMAGE = 531;
    public static final int REQUEST_CAPTURE_IMAGE = 532;

    public static class ImageResult {
        public String imageUrl;
        public String imagePath;
    }

    /*public static void scaleImage(String imagePath) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }*/

    @Nullable
    public static ImageResult onImageSelected(Context context, int requestCode, int resultCode, Intent data, View anchor) {
        try {
            if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
                String picturePath = ImageUtil.getImagePath(data, context);

                if (!TextUtils.isEmpty(picturePath)) {
                    ImageResult result = new ImageResult();

                    if (picturePath.contains("http"))
                        result.imageUrl = picturePath;
                    else
                        result.imagePath = picturePath;

                    return result;
                }
            } else {
                Snackbar.make(anchor, context.getString(R.string.no_image_selected), Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Snackbar.make(anchor, e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
        return null;
    }

    @Nullable
    public static String getImagePath(Intent data, Context context) {
        Uri uri = data.getData();
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver()
                .query(uri, projection, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();   //TODO: Null checks
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }
}