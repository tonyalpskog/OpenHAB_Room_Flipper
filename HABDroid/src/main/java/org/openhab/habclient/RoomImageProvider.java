package org.openhab.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.openhab.domain.model.Room;
import org.openhab.habclient.dagger.ApplicationContext;

import javax.inject.Inject;

public class RoomImageProvider implements IRoomImageProvider {
    private final Context mContext;

    @Inject
    public RoomImageProvider(@ApplicationContext Context context) {
        mContext = context;
    }

    @Override
    public Bitmap getRoomImage(Room room, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = getBitMapOptions(room);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        
        return getRoomBitmap(room, options);
    }

    private boolean hasFileReference(Room room) {
        return(room.getBackgroundImageFilePath() == null || room.getBackgroundImageFilePath().isEmpty());
    }

    private Bitmap getRoomBitmap(Room room, BitmapFactory.Options options) {
        if(hasFileReference(room))
            return getBitmap(room.getBackgroundImageResourceId(), options);
        return getBitmap(room.getBackgroundImageFilePath(), options);
    }
    
    private Bitmap getBitmap(int bitmapResourceId, BitmapFactory.Options options) {
        return BitmapFactory.decodeResource(mContext.getResources(), bitmapResourceId, options);
    }

    private Bitmap getBitmap(String bitmapFilePath, BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(bitmapFilePath, options);
    }

    private BitmapFactory.Options getBitMapOptions(Room room) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if(hasFileReference(room))
            BitmapFactory.decodeResource(mContext.getResources(), room.getBackgroundImageResourceId(), options);
        BitmapFactory.decodeFile(room.getBackgroundImageFilePath(), options);
        return options;
    }
    
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    || (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    
    public Bitmap setPointAsAlfa(int x, int y, Bitmap source) {
        int pixelColor = source.getPixel(x, y);
        return setColorAsAlfa(pixelColor, source);
    }

    public Bitmap setColorAsAlfa(int color, Bitmap source) {
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());

        int height = source.getHeight();
        int width = source.getWidth();

        for (int yPos = 0; yPos < height; yPos++) {
            for (int xPos = 0; xPos < width; xPos++) {
                if(target.getPixel(xPos, yPos) == color)
                    target.setPixel(xPos, yPos, Color.alpha(color));
            }
        }

        return target;
    }

    private Bitmap invertBitmap(Bitmap source) {
        Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), source.getConfig());
        int A, R, G, B;
        int pixelColor;
        int height = source.getHeight();
        int width = source.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelColor = source.getPixel(x, y);
                A = Color.alpha(pixelColor);

                R = 255 - Color.red(pixelColor);
                G = 255 - Color.green(pixelColor);
                B = 255 - Color.blue(pixelColor);

                target.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        return target;
    }
}
