# Google Maps Setup Instructions

## Get Your Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the following APIs:
   - Maps SDK for Android
   - Geocoding API
4. Go to "Credentials" and create an API key
5. Restrict the API key to Android apps (recommended)
6. Copy your API key

## Add API Key to Your App

Open `app/src/main/AndroidManifest.xml` and replace:
```xml
android:value="YOUR_GOOGLE_MAPS_API_KEY_HERE"
```

With your actual API key:
```xml
android:value="AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
```

## How It Works

### When Creating an Event:
1. Enter the event location (e.g., "University of Alberta, Edmonton")
2. The app will automatically geocode this location to get coordinates
3. Coordinates are saved with the event in Firestore

### When Viewing Event Details:
1. The map will automatically load and show the event location
2. A marker will be placed at the event location
3. The map will zoom to show the location clearly

## Testing Without API Key

If you don't add an API key, the map will show a gray box with "For development purposes only" watermark. The app will still work, but the map won't display properly.
