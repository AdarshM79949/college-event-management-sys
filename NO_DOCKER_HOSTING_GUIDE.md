# 🌐 No-Docker Hosting Guide for CampusPulse

You do **NOT** need Docker to host your application and database on the internet. Here are the 3 simplest, 100% free ways:

---

## ⚡ Method 1: Instant Public HTTPS URL (From Your PC in 10 Seconds)

You can make your local application accessible to anyone in the world immediately without deploying to any cloud server or installing Docker.

1. Start your Spring Boot application locally:
   ```powershell
   .\run.bat
   ```
2. In a second terminal window, run either of these free tunnel tools:
   
   **Using NPX (No installation needed if Node.js is installed):**
   ```bash
   npx localtunnel --port 8080
   ```
   *Output: `your url is: https://cold-campus-events.loca.lt`*

   **Using Cloudflare Tunnel (100% Free, unlimited bandwidth):**
   ```bash
   cloudflared tunnel --url http://localhost:8080
   ```
   *Output: `https://xyz-random-subdomain.trycloudflare.com`*

   **Using Ngrok:**
   ```bash
   ngrok http 8080
   ```

Share that URL with anyone — they can access your live system directly from their phone or laptop!

---

## ☁️ Method 2: 100% Free Native Cloud Hosting on Render (No Docker)

Render can build and run Java Maven projects natively without any Docker containers.

### Step 1: Push Code to GitHub
```bash
git init
git add .
git commit -m "Deploy CampusPulse"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/college-event-management.git
git push -u origin main
```

### Step 2: Create a Free PostgreSQL / MySQL Cloud Database
1. Go to [Neon.tech](https://neon.tech/) (Free PostgreSQL) or [Aiven.io](https://aiven.io/) (Free MySQL).
2. Create a free project.
3. Open their **SQL Query Editor** on the web.
4. Copy & paste the entire contents of [`database/schema_and_mock_data.sql`](./database/schema_and_mock_data.sql) and click **Run**.
5. Copy your database connection URL (e.g. `jdbc:postgresql://ep-xyz.neon.tech/neondb?sslmode=require`).

### Step 3: Deploy on Render (Native Environment)
1. Go to [dashboard.render.com](https://dashboard.render.com/) $\rightarrow$ **New +** $\rightarrow$ **Web Service**.
2. Connect your GitHub repository.
3. Choose settings:
   - **Environment**: `Java` (or `Native`) *(Do NOT choose Docker)*
   - **Build Command**:
     ```bash
     mvn clean package -DskipTests
     ```
   - **Start Command**:
     ```bash
     java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/event-management-system-1.0.0-SNAPSHOT.jar
     ```
   - **Plan**: `Free`
4. In **Environment Variables**, add:
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://ep-xyz.neon.tech/neondb?sslmode=require`
   - `SPRING_DATASOURCE_USERNAME`: `your_db_username`
   - `SPRING_DATASOURCE_PASSWORD`: `your_db_password`
   - `HEADLESS`: `true`
5. Click **Create Web Service**. Your app is live at `https://campuspulse-xxxx.onrender.com`!

---

## 💻 Method 3: Cloud Database + Local/Remote Java Desktop GUI

If you want the database hosted online 24/7 so multiple people can use the Java Desktop app from different computers:

1. Create a free database on [Aiven.io](https://aiven.io/) (MySQL) or [Neon.tech](https://neon.tech/) (PostgreSQL).
2. Load [`database/schema_and_mock_data.sql`](./database/schema_and_mock_data.sql).
3. In [`src/main/resources/application.properties`](./src/main/resources/application.properties), replace the local datasource URL with your cloud URL:
   ```properties
   spring.datasource.url=jdbc:mysql://mysql-xxxx.aivencloud.com:15320/college_event_db?ssl-mode=REQUIRED
   spring.datasource.username=avnadmin
   spring.datasource.password=your_aiven_password
   ```
4. Run:
   ```powershell
   .\run-desktop-app.bat
   ```
   Now your Java desktop app connects directly to the live internet database!
