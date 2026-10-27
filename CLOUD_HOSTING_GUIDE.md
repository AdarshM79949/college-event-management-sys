# 🌐 CampusPulse: Complete Cloud Hosting & Deployment Guide

This guide details how to host the **CampusPulse College Event Management System** and its **SQL Database** online on the internet for **100% FREE** with 24/7 availability.

---

## 📑 Table of Contents
1. [Option 1: 1-Click Free Hosting on Render (Recommended)](#option-1-1-click-free-hosting-on-render-recommended)
2. [Option 2: 1-Click Instant Deploy on Railway](#option-2-1-click-instant-deploy-on-railway)
3. [Option 3: Free Cloud Database (Neon / Supabase / Aiven)](#option-3-free-cloud-database-neon--supabase--aiven)
4. [Option 4: Docker & Docker Compose VPS Deployment](#option-4-docker--docker-compose-vps-deployment)
5. [Connecting Desktop Clients Over the Internet](#connecting-desktop-clients-over-the-internet)

---

## Option 1: 1-Click Free Hosting on Render (Recommended)

[Render.com](https://render.com) provides free cloud web services and free PostgreSQL databases with automatic HTTPS.

### Step 1: Push Code to GitHub
1. Create a new GitHub repository (e.g. `college-event-management-system`).
2. Push your project code:
   ```bash
   git init
   git add .
   git commit -m "Initial CampusPulse commit"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
   git push -u origin main
   ```

### Step 2: Create Free Database on Render
1. Go to [Render Dashboard](https://dashboard.render.com/) -> Click **New +** -> **PostgreSQL**.
2. Name: `campuspulse-db`
3. Database: `college_event_db`
4. User: `campus_admin`
5. Region: Pick closest to you (e.g., *Singapore*, *Frankfurt*, *Oregon*).
6. Plan: **Free** -> Click **Create Database**.
7. Copy the **Internal Database URL** (or External URL).

### Step 3: Create Web Service on Render
1. In Render Dashboard, click **New +** -> **Web Service**.
2. Connect your GitHub repository.
3. Configure the service:
   - **Runtime**: `Docker` (Render will automatically detect `Dockerfile`)
   - **Instance Type**: `Free`
4. Under **Environment Variables**, add:
   | Key | Value |
   | :--- | :--- |
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<RENDER_HOST>:5432/college_event_db` |
   | `SPRING_DATASOURCE_USERNAME` | `<RENDER_USER>` |
   | `SPRING_DATASOURCE_PASSWORD` | `<RENDER_PASSWORD>` |
   | `SPRING_SQL_INIT_MODE` | `always` |
5. Click **Create Web Service**.
6. Render will automatically build the Docker image and deploy your app at:  
   `https://campuspulse-xxxx.onrender.com`

---

## Option 2: 1-Click Instant Deploy on Railway

[Railway.app](https://railway.app) offers instant container and database provisioning.

1. Go to [Railway Dashboard](https://railway.app/).
2. Click **New Project** -> **Provision MySQL** (or **PostgreSQL**).
3. Click **New** -> **GitHub Repo** -> select your `college-event-management-system` repo.
4. In Railway variables for the app:
   - Connect `MYSQL_URL` to `SPRING_DATASOURCE_URL` (format: `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true`)
   - `SPRING_DATASOURCE_USERNAME`: `${{MySQL.MYSQLUSER}}`
   - `SPRING_DATASOURCE_PASSWORD`: `${{MySQL.MYSQLPASSWORD}}`
   - `SPRING_PROFILES_ACTIVE`: `prod`
5. Railway generates a live domain under **Settings -> Networking -> Generate Domain**.

---

## Option 3: Free Cloud Database (Neon / Supabase / Aiven)

If you only want to host the **database online** and run clients from anywhere:

### A. Neon Tech (Serverless PostgreSQL - 100% Free Forever)
1. Go to [Neon.tech](https://neon.tech/) and sign up.
2. Create project `campuspulse`.
3. In SQL Editor, paste and run the contents of [`database/schema_and_mock_data.sql`](./database/schema_and_mock_data.sql).
4. Copy the Java JDBC Connection string:
   ```properties
   spring.datasource.url=jdbc:postgresql://ep-xyz.neon.tech/neondb?sslmode=require
   spring.datasource.username=alex
   spring.datasource.password=your_secure_password
   ```

### B. Aiven (Free MySQL / PostgreSQL Cloud DB)
1. Go to [Aiven.io](https://aiven.io/) -> Create free MySQL service.
2. In Aiven console, copy Service URI.

---

## Option 4: Docker & Docker Compose VPS Deployment

If deploying on your own VPS (DigitalOcean, Linode, AWS EC2, Oracle Cloud Free Tier):

1. Install Docker & Docker Compose on your server:
   ```bash
   sudo apt update && sudo apt install docker.io docker-compose -y
   ```
2. Clone your repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/YOUR_REPO.git
   cd "YOUR_REPO"
   ```
3. Start the entire stack with 1 command:
   ```bash
   docker-compose up -d --build
   ```
4. Check running containers:
   ```bash
   docker-compose ps
   ```
   Your app is live on `http://YOUR_SERVER_IP:8080`.

---

## 🖥️ Connecting Desktop Clients Over the Internet

If students or faculty run the **Java Desktop Application** on their laptops while connecting to your online database:

In `src/main/resources/application.properties` (or by passing VM arguments):
```properties
# Point to your live cloud database on the internet
spring.datasource.url=jdbc:mysql://YOUR_CLOUD_DB_HOST:3306/college_event_db?useSSL=true
spring.datasource.username=cloud_user
spring.datasource.password=cloud_password
```

Run the desktop client:
```powershell
.\run-desktop-app.bat
```
All logins, event registrations, QR codes, and seat updates will sync in real-time across the globe!
