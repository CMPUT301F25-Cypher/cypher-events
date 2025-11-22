package com.example.cypher_events.util;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import android.graphics.Color;

/**
 * Helper class for creating QR code images using the ZXing library.

 * It takes a piece of text (like an event ID) and uses ZXing's QRCodeWriter
 * to encode that text into a BitMatrix (a grid of black and white cells), and then
 * converts that grid into a Bitmap, which can then be shown in an ImageView
 * Other parts of the app can call these methods to generate QR codes for any text,
 * such as QR codes for specific events.
 */

public class QRCodeHelper {

    /** default width ahd height in pixels for a generated QR code */

    public static final int defaultQrSizePx = 512;

    /** constructor */

    private QRCodeHelper() {
    }
    /**
     * Generates a QR code bitmap from the given text using the specified size.
     *
     * @param data text to encode in the QR code (for example, an event ID like "EVT001").
     * @param size width and height of the bitmap in pixels (square).
     * @return a  Bitmap containing the QR code, or null if the generation fails.
     * @throws IllegalArgumentException if data is null or empty, or if the code size <= 0.
     *
     * Code adapted from ChatGPT 5.1(OpenAI)
     */
    @Nullable
    public static Bitmap generateQr(String data, int size) {
        if (data==null||data.trim().isEmpty()){
            throw new IllegalArgumentException("qr data must not be null or empty");
        }
        if (size<=0){
            throw new IllegalArgumentException("qr size must be positive");
        }
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // Fill bitmap in based on BitMatrix content, black and white
            int black = Color.argb(255, 0, 0, 0);
            int white = Color.argb(255, 255, 255, 255);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? black : white);

                }
            }
            return bitmap;


        } catch (WriterException e) {
            //catches QR-generation errors to avoid crashing app, and returns null to signal failure.
            return null;
        }
    }

/**
 * Generates a QR code bitmap for an event using the default size.
 * Pass in the event's Firestore document ID (for instance, "EVT001") as the eventId.
 *@param eventId the Firestore document ID for the event
 *@return a Bitmap containing the QR code or null if generation fails.*/
@Nullable
public static Bitmap generateEventQr(String eventId) {
    return generateQr(eventId, defaultQrSizePx);
}

}
