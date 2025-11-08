# ATS Frontend - React Application

Modern React-based frontend for the Applicant Tracking System (ATS).

## Tech Stack

- **React 18** - UI library
- **Vite** - Build tool
- **React Router v6** - Routing
- **Axios** - HTTP client
- **Tailwind CSS** - Styling
- **React Hook Form** - Form handling
- **React Dropzone** - File uploads

## Getting Started

### Prerequisites

- Node.js 16+ and npm
- Running backend server (Spring Boot on port 8080)

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

The app will be available at `http://localhost:5173`

## Environment Variables

Create a `.env` file in the frontend directory:

```env
VITE_API_BASE_URL=http://localhost:8080/ats/api
```

## Features

### For Candidates
- Browse active job postings
- View job details with AI-matched skills
- Apply to jobs with resume upload (drag & drop)
- Track application status with AI screening scores
- View interview schedules

### For Recruiters/Admins
- Dashboard with statistics
- Manage job postings (create, update, toggle status)
- Review applications sorted by AI score
- Schedule/manage interviews
- User management (admin only)

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── Navbar.jsx
│   │   └── ProtectedRoute.jsx
│   ├── context/
│   │   └── AuthContext.jsx
│   ├── pages/
│   │   ├── Login.jsx
│   │   ├── JobList.jsx
│   │   ├── JobDetail.jsx
│   │   ├── MyApplications.jsx
│   │   └── admin/
│   │       └── Dashboard.jsx
│   ├── services/
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── jobService.js
│   │   ├── applicationService.js
│   │   ├── adminService.js
│   │   └── fileService.js
│   ├── App.jsx
│   └── main.jsx
└── package.json
```

## Authentication

The app uses JWT token-based authentication:
- Tokens are stored in localStorage
- Axios interceptor adds token to all API requests
- Automatic redirect to login on 401 errors
- Role-based route protection

## API Integration

All API calls go through the centralized `api.js` service with:
- Automatic token injection
- Error handling
- Base URL configuration

## Build & Deploy

```bash
# Build for production
npm run build

# Output will be in ./dist directory
# Deploy dist/ to any static hosting service
```

## Development Notes

- Hot reload enabled in development
- Tailwind CSS configured for JIT compilation
- All API responses follow the ApiResponse DTO format
- File uploads use multipart/form-data
