# Quick Start Guide - React ATS Application

## 🚀 Setup & Run

### Prerequisites
- Java 11+
- Node.js 16+
- PostgreSQL database
- Maven

### 1. Backend Setup

```bash
# From project root
cd /home/user/Amr-ATS-Claude

# Build backend
mvn clean install -DskipTests

# Run Spring Boot server
mvn spring-boot:run
```

Backend will be available at: **http://localhost:8080/ats**

### 2. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (if not already done)
npm install

# Start development server
npm run dev
```

Frontend will be available at: **http://localhost:5173**

### 3. Access the Application

Open your browser and go to: **http://localhost:5173**

## 🔐 Demo Credentials

### Admin Account
- **Email**: admin@ats.com
- **Password**: Admin@ABC
- **Access**: Full admin dashboard, user management, all features

### Candidate Account
- **Email**: candidate@ats.com
- **Password**: Ats@ABC
- **Access**: Job browsing, apply for jobs, track applications

### Recruiter Account
- **Email**: recruiter@ats.com (create if doesn't exist)
- **Password**: Ats@ABC
- **Access**: Job management, application review

## ✨ Key Features to Test

### As a Candidate
1. **Browse Jobs** - View all active job postings
2. **Apply to Job** - Upload resume with drag-and-drop
3. **AI Screening** - See your AI match score (0-100%)
4. **Track Applications** - Monitor status and interview schedules

### As Admin/Recruiter
1. **Dashboard** - View statistics and recent activity
2. **Job Management** - Create, edit, toggle job postings
3. **Application Review** - See all applications sorted by AI score
4. **Interview Scheduling** - Schedule/update/cancel interviews
5. **User Management** (Admin only) - Create recruiters, manage users

## 📡 API Endpoints

Base URL: `http://localhost:8080/ats/api`

### Authentication
```bash
# Login
POST /api/auth/login
{
  "email": "admin@ats.com",
  "password": "Admin@ABC"
}

# Response includes JWT token
{
  "success": true,
  "data": {
    "token": "eyJhbGc...",
    "userId": 1,
    "username": "admin",
    "email": "admin@ats.com",
    "role": "ADM"
  }
}
```

### Jobs
```bash
# Get all active jobs
GET /api/jobs

# Get job details
GET /api/jobs/{id}

# Create job (requires Admin/Recruiter role)
POST /api/jobs
```

### Applications
```bash
# Submit application with resume
POST /api/applications
FormData: jobId, resume (file), notes

# Get my applications
GET /api/applications

# Update application status
PATCH /api/applications/{id}/status?status=Interview
```

## 🔧 Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

**Database connection error:**
- Check PostgreSQL is running
- Verify credentials in `src/main/resources/application.properties`
- Ensure database `spring-ats` exists

**Lombok errors:**
- Enable annotation processing in your IDE
- Install Lombok plugin

### Frontend Issues

**Port 5173 already in use:**
```bash
# The dev server will auto-increment to 5174
# Or kill the process
lsof -ti:5173 | xargs kill -9
```

**Cannot connect to backend:**
- Check `.env` file has correct API URL
- Verify backend is running on port 8080
- Check browser console for CORS errors

**White screen/React errors:**
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### CORS Errors
- Check `CorsConfig.java` includes your frontend URL
- Default allowed: localhost:3000, localhost:5173, localhost:8080

### 401 Unauthorized
- Token expired (24 hour expiry)
- Clear localStorage in browser DevTools
- Log in again to get new token

## 📦 Production Build

### Frontend Build
```bash
cd frontend
npm run build

# Output in frontend/dist/
```

### Deploy Both Together
```bash
# Option 1: Copy React build to Spring Boot static
cp -r frontend/dist/* src/main/resources/static/

# Build Spring Boot with embedded React
mvn clean package -DskipTests

# Run the jar
java -jar target/getready-0.0.1-SNAPSHOT.jar
```

### Deploy Separately
- **Backend**: Railway, Heroku, AWS
- **Frontend**: Netlify, Vercel, Cloudflare Pages

## 🧪 Testing the Transformation

### Test Flow
1. **Login** as admin → Should see Dashboard
2. **Create Job** → Fill form with required skills
3. **Logout** and login as candidate
4. **Browse Jobs** → See the job you created
5. **Apply** → Upload a resume (use test-resumes/)
6. **Check AI Score** → Should see match percentage
7. **Back to Admin** → Review application with AI score
8. **Schedule Interview** → Set date/time/location
9. **Back to Candidate** → See interview details

### API Testing with cURL
```bash
# Get token
TOKEN=$(curl -s -X POST http://localhost:8080/ats/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ats.com","password":"Admin@ABC"}' \
  | jq -r '.data.token')

# Get jobs with token
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/ats/api/jobs | jq
```

## 📂 Project Structure

```
Amr-ATS-Claude/
├── frontend/                    # React application
│   ├── src/
│   │   ├── components/         # Reusable components
│   │   ├── pages/              # Page components
│   │   ├── services/           # API services
│   │   ├── context/            # React Context
│   │   └── App.jsx             # Main app
│   ├── package.json
│   └── vite.config.js
│
├── src/main/java/
│   └── com/spring/getready/
│       ├── controller/api/     # REST controllers (NEW)
│       ├── dto/                # API DTOs (NEW)
│       ├── security/           # JWT security (NEW)
│       ├── config/             # Configuration
│       ├── services/           # Business logic
│       └── model/              # Entities
│
├── pom.xml                     # Maven dependencies (updated)
└── REACT_TRANSFORMATION.md     # Full documentation
```

## 🎯 Next Steps

After successful setup, you can:
1. ✅ Customize the UI (Tailwind classes in components)
2. ✅ Add more admin pages (job creation form, etc.)
3. ✅ Enhance AI algorithm in `AIResumeScreeningService`
4. ✅ Add email notifications
5. ✅ Implement real-time updates with WebSockets
6. ✅ Add TypeScript for type safety
7. ✅ Create mobile app with React Native (same API)

## 📚 Documentation

- **Full Transformation Guide**: `REACT_TRANSFORMATION.md`
- **Frontend README**: `frontend/README.md`
- **Original Docs**: `CLAUDE.md`

## 🆘 Need Help?

Check these files:
- `REACT_TRANSFORMATION.md` - Complete migration documentation
- `frontend/README.md` - Frontend-specific docs
- Browser DevTools Console - Check for errors
- Network tab - Check API calls

## ✅ What's Working

✅ JWT authentication
✅ Job listing and details
✅ Application submission with file upload
✅ AI resume screening (automatic on upload)
✅ Application tracking for candidates
✅ Admin dashboard with statistics
✅ Protected routes by role
✅ Responsive design (mobile-friendly)
✅ File download/view
✅ Interview scheduling

**The transformation is complete and ready for testing!** 🎉
