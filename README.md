# Witube 🎧

An Android mobile application (Kotlin) that communicates with a Node.js backend (Express) to download YouTube audio as MP3 files using `yt-dlp`.

---

## 📂 Project Structure

* **`app/`**: Android client application (Kotlin, ViewBinding, OkHttp, Coil).
* **`backend/`**: Node.js backend server that runs `yt-dlp`.
* **`CHANGELOG.md`**: Simple log of changes and releases.

---

## 🚀 Getting Started

### 1. Run the Backend (Local)
1. Go to the `backend` folder and install dependencies:
   ```bash
   cd backend
   npm install
   ```
2. Export your YouTube cookies as a Netscape-formatted file from your browser (to bypass blocking/429 errors) and save it as `backend/youtube-cookies.txt`.
3. Start the server:
   ```bash
   npm run dev
   ```

### 2. Deploy to Production (Render)
1. In your **Render** dashboard, go to **Environment** > **Secret Files**.
2. Add a new file named `youtube-cookies.txt` and paste your cookie contents there.
3. Our backend automatically copies the mounted file to a writable location (`/tmp`) so `yt-dlp` can update it at runtime without permission issues.

### 3. Generate the APK (Android)
* **Testing APK:** In Android Studio, go to **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
* **Production/Signed APK:** Go to **Build** > **Generate Signed Bundle / APK...** and follow the prompts.

---

## 💳 Acknowledgments

This project relies on the amazing [yt-dlp](https://github.com/yt-dlp/yt-dlp) open-source tool for media extraction and download. Special thanks to its creators and maintainers.
