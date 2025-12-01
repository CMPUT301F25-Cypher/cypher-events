# Troubleshooting Large Image Crash

## The Problem
Your app is crashing because some events in Firestore have extremely large base64-encoded images (720MB+). This happens when images aren't properly compressed before being saved.

## Immediate Fix Applied
The code now:
1. Rejects base64 strings over 5MB
2. Rejects decoded images over 10MB  
3. Rejects images with dimensions over 10,000 pixels
4. Uses aggressive downsampling (4x for images over 2000px)
5. Uses RGB_565 color format (uses 50% less memory)
6. Shows placeholder images when decoding fails

## Clean Up Bad Data in Firestore

You need to find and delete/fix the problematic events. Here's how:

### Option 1: Delete Events with Large Images (Recommended)
1. Open Firebase Console
2. Go to Firestore Database
3. Open the "Events" collection
4. Look for events with very large `Event_posterBase64` fields
5. Either:
   - Delete the entire event document, OR
   - Delete just the `Event_posterBase64` field (set it to empty string)

### Option 2: Check Image Sizes Programmatically
Add this temporary code to your app to log image sizes:

```java
db.collection("Events").get()
    .addOnSuccessListener(snapshot -> {
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            String base64 = doc.getString("Event_posterBase64");
            if (base64 != null) {
                int sizeKB = base64.length() / 1024;
                if (sizeKB > 500) { // Over 500KB
                    Log.e("IMAGE_CHECK", "Event " + doc.getId() + 
                          " has large image: " + sizeKB + "KB");
                }
            }
        }
    });
```

## Prevent Future Issues

When creating events, the ImageProcessor already compresses images to:
- Max 800x800 pixels
- JPEG quality 80%
- Base64 encoded

Make sure you're always using `ImageProcessor.uriToBase64()` when saving images, never save raw base64 from other sources.

## Testing
After cleaning up the data:
1. Clear app data/cache
2. Uninstall and reinstall the app
3. The app should now work without crashes
