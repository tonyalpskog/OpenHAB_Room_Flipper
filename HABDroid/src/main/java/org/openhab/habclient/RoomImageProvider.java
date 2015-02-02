package org.openhab.habclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.openhab.domain.model.Room;

import javax.inject.Inject;

public class RoomImageProvider implements IRoomImageProvider {
    private final Context mContext;

    @Inject
    public RoomImageProvider(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap getRoomImage(Room room) {
        if(room.getBackgroundImageFilePath() == null || room.getBackgroundImageFilePath().isEmpty())
            return getBitmap(room.getBackgroundImageResourceId());
        return BitmapFactory.decodeFile(room.getBackgroundImageFilePath());
    }

    private Bitmap getBitmap(int bitmapResourceId) {
        return BitmapFactory.decodeResource(mContext.getResources(), bitmapResourceId);
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
