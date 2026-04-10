# Multithread-Downloader
# 🚀 Multithreaded File Downloader (Java)

A high-performance file downloader built using Java that utilizes **multithreading** to download files faster by splitting them into multiple parts and downloading them simultaneously.

---

## 📌 Overview

This project demonstrates how multithreading can significantly improve download speed. Instead of downloading a file sequentially, the file is divided into chunks, and each chunk is handled by a separate thread.

---

## 🛠️ Technologies Used

- Java  
- Multithreading (Thread / Runnable)  
- Networking (HttpURLConnection, URL)  
- File Handling (InputStream, RandomAccessFile)  

---

## 📖 How It Works

1. User provides the file URL  
2. File size is determined  
3. File is divided into multiple parts  
4. Each part is assigned to a separate thread  
5. Threads download simultaneously  
6. Parts are merged into a single file  
