## Version 1.0 (Initial Release) - June 03, 2026

This is a test version of Witube.

### What does this version do?
* **Android App (Kotlin):** You can open the app, enter a YouTube link, view the video's thumbnail, and request the audio download.
* **Server (Backend):** Receives the link, gets metadata, and converts the video to MP3 format using `yt-dlp`.
* **Security:** Does not save passwords and protects your YouTube cookies so they are not uploaded publicly to GitHub.

### Problems fixed before release:
* **Blocking Error (429):** Added support to read YouTube cookies so we are not detected as a bot.
* **Read-Only Server Error:** Configured the backend to copy cookies to the `/tmp/` temporary folder so the app doesn't crash when running on platforms like Render.
