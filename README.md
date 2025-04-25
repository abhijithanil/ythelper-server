# 🎥 YT Upload Service

## 📌 Overview
**YT Upload Service** is a backend service designed to handle **video uploads, conversions, and storage** for a YouTube-like platform. It supports **secure file handling**, automatic **MP4 conversion**, and cloud storage integration.

---

## 🚀 Features
- **Upload Videos**: Supports multiple formats including `.mp4`, `.mov`, `.avi`, etc.
- **Automatic Video Conversion**: Converts non-MP4 files to **MP4 (H.264/AAC)**.
- **Cloud Storage**: Supports uploading to **Google Cloud Storage (GCS)**.
- **Secure Access**: Implements JWT-based authentication for secured endpoints.
- **Database Integration**: Tracks video statuses and metadata using **PostgreSQL**.
- **RESTful API**: Provides endpoints for uploading, streaming, and managing videos.

---

## 📦 Tech Stack
- **Spring Boot** (Java 17)
- **Spring Security** (JWT authentication)
- **Google Cloud Storage API**
- **FFmpeg** (for video conversion)
- **PostgreSQL** (database)
- **Maven** (dependency management)

---

## 📜 API Endpoints

### 🔹 Authentication
| Method | Endpoint | Description |
|--------|---------|-------------|
| `POST` | `/api/auth/login` | Logs in a user and returns a JWT token |
| `POST` | `/api/auth/register` | Registers a new user |

### 🔹 Video Upload & Management
| Method | Endpoint | Description |
|--------|---------|-------------|
| `POST` | `/api/media/video/upload` | Uploads a video file |
| `GET`  | `/api/media/video/view?id={videoId}` | Streams a video |
| `DELETE` | `/api/media/video/delete/{videoId}` | Deletes a video |

### 🔹 Video Conversion Status
| Method | Endpoint | Description |
|--------|---------|-------------|
| `GET`  | `/api/media/video/status/{videoId}` | Checks conversion status |
| `PUT`  | `/api/media/video/status/update` | Updates conversion status |

---

## ⚡ Installation & Setup

### **1️⃣ Prerequisites**
Ensure you have the following installed:
- **Java 17+**
- **Maven**
- **PostgreSQL**
- **Google Cloud SDK** (if using GCS for storage)
- **FFmpeg** (for video conversion)

### **2️⃣ Clone the Repository**
```sh
 git clone https://github.com/your-repo/ytupload-service.git
 cd ytupload-service
3️⃣ Configure Environment Variables
Create an .env file or set environment variables:

```

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/ytservice
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
JWT_SECRET=your_secret_key
GCP_BUCKET_NAME=your-gcp-bucket
FFMPEG_PATH=/usr/bin/ffmpeg  # Adjust based on your OS
4️⃣ Build and Run the Service
```

```text
ytupload-service/
│-- src/main/java/com/inspire17/ytservice
│   ├── controllers      # API Controllers
│   ├── service          # Business Logic
│   ├── repository       # Database Interaction
│   ├── entity           # JPA Entities
│   ├── sec              # Security Configuration
│   ├── config           # General Configuration Files
│   ├── helper           # Video Processing Utilities
│-- src/main/resources
│   ├── application.properties  # Spring Boot Configurations
│-- pom.xml             # Maven Dependencies
│-- README.md           # Documentation
```

# 📌 License
This project is licensed under the MIT License.

📩 Contributions are welcome! Feel free to open issues or submit PRs.

``` text

You can **copy and paste** this content into your `README.md` file. 🚀 Let me know if you need any modificat