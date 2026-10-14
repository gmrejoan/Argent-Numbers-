# Argent Numbers — Android prototype

A starter Android app for Bangladesh emergency numbers. It currently offers emergency hotline shortcuts and nearby-place search using the phone's map app.

## Build in GitHub Actions

The included workflow is `.github/workflows/build-android.yml`. It builds a debug APK and uploads it as a workflow artifact named `Argent-Numbers-debug-APK`.

For GitHub Actions to run, the project files and `.github/workflows/build-android.yml` must be in the root of your own GitHub repository (not nested inside an extra folder). Open **Actions → Build Argent Numbers APK → Run workflow**. When complete, open the successful run and download the artifact.

## Important limitations

- This is source code, not a prebuilt APK; it has not yet been compiled or tested on a real phone.
- The prototype is not an official emergency-service directory. Verify all hotline numbers and local contact information before relying on it or publishing it.
- Nearby search opens a map app and requires internet; it does not yet contain a verified national database of police, fire, hospitals, ambulance or electricity complaint centres.
- The APK from this workflow is a debug APK for testing, not a Play Store release.
