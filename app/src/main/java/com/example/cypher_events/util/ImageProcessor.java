package com.example.cypher_events.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * ImageProcessor
 *
 * This helper class is responsible for all the image handling. It Takes an image Uri from the gallery
 * (content://...), loads it as a Bitmap, shrinks it to a reasonable size, compresses it to JPEG,
 * converts the compressed bytes to a Base64 string (for storing in Firestore)
 * and converts a Base64 string from Firestore back to a Bitmap (for displaying in ImageViews)
 *
 * Why Base64?
 *Firestore documents store text fields easily, Base64 is a way to represent binary data (bytes) as text,
 *so it can be stored in a string field.
 *
 * Code and implementation adapted from CMPUT 301 forum discussion, various discussions
 * on Stackoverflow, ChatGPT 5.1 (OpenAI)  */
public class ImageProcessor{
    /**
     * maxWidth / maxHeight set so that We do not store or display really large images
     *
     * These values control the maximum width and height (in pixels) of the image
     * that we will store as Base64 and anything larger will be scaled down. */
    private static final int maxWidth = 1080;
    private static final int maxHeight = 1080;

    /**
     *compress the bitmap to JPEG, choose a quality between 0 and 100.
     * 100 is best quality, but large file size. 80 is selected as a good middle ground */
    private static final int jpegQuality = 80;

    /**
     * uriToBase64
     * IP:  a Context and a Uri (from the image picker)
     * Loads the image from the Uri into a Bitmap, downscales the Bitmap if it is too large,
     * compresses the Bitmap to JPEG format, coverts JPEG bytes into a Base64 string
     * OP: Base 64 string for Firestore storage
     * If any step fails (for instance, cannot open the Uri), returns null.*/
    public static String uriToBase64(Context context, Uri imageUri){
        if (context==null||imageUri==null) {
            return null;
        }

        try {
            // load Bitmap from Uri
            Bitmap originalBitmap = loadBitmapFromUri(context, imageUri);
            if (originalBitmap == null) {
                // if image couldn't be decoded, then return null
                return null;
            }

            //scale the bitmap so it's not larger than maxWidth and maxHeight.
            //If it is already small enough, just return the original.
            Bitmap scaledBitmap = scaleBitmap(originalBitmap, maxWidth, maxHeight);

            //If a new scaled bitmap was created,free memory of the larger original one
            if (scaledBitmap!= originalBitmap) {
                originalBitmap.recycle();
            }

            //compress scaled bitmap to JPEG and encode as Base64 text.
            return bitmapToBase64(scaledBitmap);

        } catch (IOException error) {
            //if reading from the Uri fails.
            error.printStackTrace();
            return null;
        }
    }
    /**
     * base64ToBitmap
     *This method is used when reading the event document from Firestore
     * and to show the stored poster image.
     * IP: a Base64 string (value stored in Firestore).
     * Decodes the Base64 text back into raw image bytes and decodes those bytes to an Android Bitmap object
     * This Bitmap can be passed to an ImageView
     *
     * Returns null if the Base64 string is empty or invalid*/
    public static Bitmap base64ToBitmap(String base64String){
        if (base64String== null||base64String.isEmpty()){
            // No base64 string to decode
            return null;
        }
        try {
            //convert Base64 text back into raw bytes.
            byte[] imageBytes = Base64.decode(base64String, android.util.Base64.DEFAULT);

            //turn the raw bytes into a Bitmap so it can be displayed by Android
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        } catch (IllegalArgumentException error) {
            //if the Base64 string is not valid
            error.printStackTrace();
            return null;
        }
    }
    /**
     * loadBitmapFromUri
     *
     * This method decodes a Bitmap from a Uri
     *
     * First, decode only the bounds of the image (width and height), not the pixels.
     *  (done using inJustDecodeBounds = true) Then, we calculate an "inSampleSize" which tells B
     *  BitmapFactory how much it can downscale the image while decoding it (half size, quarter size etc.).
     * Then decode the bitmap again with that inSampleSize applied.
     *
     * Done to avoid loading a full res image into memory */
    private static Bitmap loadBitmapFromUri(Context context, Uri uri) throws IOException{
        // This options object will first be used to measure the image.
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;  //no memory allocation for pixels yet.

        // input stream to the image data using the content resolver.
        InputStream boundsStream = context.getContentResolver().openInputStream(uri);
        if (boundsStream == null) {
            return null;
        }
        //Decode the stream just to get width and height information.
        BitmapFactory.decodeStream(boundsStream, null, decodeOptions);
        boundsStream.close();
        int originalWidth = decodeOptions.outWidth;
        int originalHeight = decodeOptions.outHeight;

        //If width or height is 0 or negative, return null since decoding failed
        if (originalWidth <= 0 || originalHeight <= 0) {
            return null;
        }
        //decide how much we can shrink the image during decoding.
        decodeOptions.inSampleSize = calculateSampleSize(
                originalWidth,
                originalHeight,
                maxWidth,
                maxHeight
        );

        //dealing with pixels
        decodeOptions.inJustDecodeBounds = false;

        //new stream to decode actual bitmap.
        InputStream bitmapStream = context.getContentResolver().openInputStream(uri);
        if (bitmapStream == null) {
            return null;
        }

        //Decode the bitmap with inSampleSize applied.
        Bitmap bitmap = BitmapFactory.decodeStream(bitmapStream, null, decodeOptions);
        bitmapStream.close();
        return bitmap;
    }

    /**
     * calculateSampleSize
     * helps decide how much the image should be downscaled while decoding.
     * originalWidth and originalHeight: actual pixel dimensions of the image.
     * wantedWidth and wantedHeight: the max dimensions we want to roughly fit inside.
     * compute a "sampleSize" that is a power of two (1, 2, 4, 8, ...).
     * The bigger the sampleSize, the smaller the decoded bitmap will be and uses less memory
     *
     * keep doubling sampleSize until halving the image that many times still
     * keeps it larger than or equal to the desired width/height.
     */
    private static int calculateSampleSize(
            int width,
            int height,
            int wantedWidth,
            int wantedHeight
    ) {
        int sampleSize = 1;

        //Only calculate reduction if the image is larger than what we want.
        if (height > wantedHeight || width > wantedWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            //Keep increasing sampleSize as long as dividing the dimensions by
            //sampleSize still leaves them larger than the wanted width/height.
            while ((halfHeight / sampleSize) >= wantedHeight
                    && (halfWidth / sampleSize) >= wantedWidth) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }
    /**
     * scaleBitmap
     *
     * This method does a final scaling step using Bitmap.createScaledBitmap().
     * If the bitmap is already smaller than maxWidth and maxHeight return the original
     * If it is larger compute a scale factor to keep the the aspect ratio so the image
     * does not look stretched or squished.
     *  First it checks if scaling is needed at all, compute widthRatio and heightRatio
     *  (how much we could shrink along each dimension).
     *  Then, choose the smaller of these ratios so that both width and height fit.
     *  Use that ratio to compute newWidth and newHeight and create and return the new scaled bitmap*/
    private static Bitmap scaleBitmap(Bitmap originalBitmap, int maxWidth, int maxHeight){
        if (originalBitmap == null) {
            return null;
        }
        int currentWidth = originalBitmap.getWidth();
        int currentHeight = originalBitmap.getHeight();

        //If the bitmap already fits inside target size just return the originalBitmap (no scaling)
        if (currentWidth <= maxWidth && currentHeight <= maxHeight) {
            return originalBitmap;
        }

        //widthRatio is how much to scale if only caring about width
        //heightRatio is how much to scale only considering height
        float widthRatio = (float) maxWidth/currentWidth;
        float heightRatio = (float) maxHeight/currentHeight;

        //choose the smaller ratio so that both dimensions fit within the bounds.
        float scaleAmount = Math.min(widthRatio, heightRatio);

        int newWidth = Math.round(currentWidth*scaleAmount);
        int newHeight = Math.round(currentHeight*scaleAmount);

        //create a new bitmap with the newly computed width and height.
        //true flag asks for a smoother scaling algorithm.
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
    }
    /**
     * bitmapToBase64
     * This is the final step for saving
     * It takes a Bitmap, compress it to JPEG with the chosen jpegQuality and then it converts the
     * compressed byte array to a Base64 string*/
    private static String bitmapToBase64(Bitmap bitmap){
        if (bitmap==null){
            return null;
        }
        //ByteArrayOutputStream is like a growable byte[], to write data to.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //compress the bitmap to JPEG format and write the bytes into the stream.
        bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, stream);
        //extract the raw byte[] of the compressed JPEG.
        byte[] imageBytes = stream.toByteArray();
        //encode the byte[] as a Base64 string that can be stored in Firestore
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}


