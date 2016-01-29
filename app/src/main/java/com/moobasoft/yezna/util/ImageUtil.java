package com.moobasoft.yezna.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.moobasoft.yezna.R;

public class ImageUtil {

    private ImageUtil() {
    }

    public static final int SELECT_PICTURE = 531;

    public static class ImageResult {
        public String imageUrl;
        public String imagePath;
    }

    public static ImageResult onImageSelected(Context context, int requestCode, int resultCode, Intent data, View anchor) {
        try {
            if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK && null != data) {
                String picturePath = ImageUtil.getImagePath(data, context);

                if (picturePath != null) {
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