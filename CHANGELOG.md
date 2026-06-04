# Version History (My Releases) 🚀

Welcome to your first release! This file will help you keep a simple, easy-to-read record of the changes and improvements you make to **Witube** every time you build a new APK.

---

## Version 1.0.0 (Initial Release) - June 03, 2026

This is the first stable version of Witube.

### What does this version do?
* **Android App (Kotlin):** You can open the app, enter a YouTube link, view the video's thumbnail, and request the audio download.
* **Server (Backend):** Receives the link, gets metadata, and converts the video to MP3 format using `yt-dlp`.
* **Security:** Does not save passwords and protects your YouTube cookies so they are not uploaded publicly to GitHub.

### Problems fixed before release:
* **Blocking Error (429):** Added support to read YouTube cookies so we are not detected as a bot.
* **Read-Only Server Error:** Configured the backend to copy cookies to the `/tmp/` temporary folder so the app doesn't crash when running on platforms like Render.

---

## 📝 How to add a new version in the future?

When you make an improvement (for example, adding a new button, changing the layout, or fixing a bug) and generate a new APK, simply come to this file and write above the previous entry:

```markdown
## Version X.X.X (Name of the improvement) - Today's Date

* **What's new:** Write the features you added here.
* **Fixed bugs:** Write the issues you solved here.
```
