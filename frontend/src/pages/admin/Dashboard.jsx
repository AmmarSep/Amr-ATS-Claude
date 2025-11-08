import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import adminService from '../../services/adminService';
import jobService from '../../services/jobService';
import applicationService from '../../services/applicationService';
import Navbar from '../../components/Navbar';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalUsers: 0,
    recruiters: 0,
    candidates: 0,
  });
  const [recentJobs, setRecentJobs] = useState([]);
  const [recentApplications, setRecentApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [statsRes, jobsRes, appsRes] = await Promise.all([
        adminService.getDashboardStats(),
        jobService.getAllActiveJobs(),
        applicationService.getAllApplications(),
      ]);

      if (statsRes.success) setStats(statsRes.data);
      if (jobsRes.success) setRecentJobs(jobsRes.data.slice(0, 5));
      if (appsRes.success) setRecentApplications(appsRes.data.slice(0, 5));
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <>
        <Navbar />
        <div className="flex items-center justify-center min-h-screen">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-8">Dashboard</h1>

          {/* Stats Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-blue-500 rounded-md p-3">
                  <svg className="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                </div>
                <div className="ml-5">
                  <p className="text-sm font-medium text-gray-500">Total Users</p>
                  <p className="text-2xl font-semibold text-gray-900">{stats.totalUsers}</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-green-500 rounded-md p-3">
                  <svg className="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                  </svg>
                </div>
                <div className="ml-5">
                  <p className="text-sm font-medium text-gray-500">Active Jobs</p>
                  <p className="text-2xl font-semibold text-gray-900">{recentJobs.length}</p>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow p-6">
              <div className="flex items-center">
                <div className="flex-shrink-0 bg-purple-500 rounded-md p-3">
                  <svg className="h-6 w-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                <div className="ml-5">
                  <p className="text-sm font-medium text-gray-500">Applications</p>
                  <p className="text-2xl font-semibold text-gray-900">{recentApplications.length}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Recent Jobs and Applications */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Recent Jobs */}
            <div className="bg-white rounded-lg shadow">
              <div className="p-6 border-b border-gray-200">
                <div className="flex justify-between items-center">
                  <h2 className="text-lg font-semibold text-gray-900">Recent Job Postings</h2>
                  <Link to="/admin/jobs" className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                    View All
                  </Link>
                </div>
              </div>
              <div className="divide-y divide-gray-200">
                {recentJobs.map((job) => (
                  <div key={job.jobId} className="p-6 hover:bg-gray-50">
                    <h3 className="text-sm font-medium text-gray-900">{job.jobTitle}</h3>
                    <p className="mt-1 text-sm text-gray-500">{job.location} • {job.jobType}</p>
                    <p className="mt-1 text-xs text-gray-400">
                      Posted: {new Date(job.postedOn).toLocaleDateString()}
                    </p>
                  </div>
                ))}
                {recentJobs.length === 0 && (
                  <div className="p-6 text-center text-gray-500">No jobs posted yet</div>
                )}
              </div>
            </div>

            {/* Recent Applications */}
            <div className="bg-white rounded-lg shadow">
              <div className="p-6 border-b border-gray-200">
                <div className="flex justify-between items-center">
                  <h2 className="text-lg font-semibold text-gray-900">Recent Applications</h2>
                  <Link to="/admin/applications" className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                    View All
                  </Link>
                </div>
              </div>
              <div className="divide-y divide-gray-200">
                {recentApplications.map((app) => (
                  <div key={app.applicationId} className="p-6 hover:bg-gray-50">
                    <div className="flex justify-between">
                      <div>
                        <h3 className="text-sm font-medium text-gray-900">{app.candidateName}</h3>
                        <p className="mt-1 text-sm text-gray-500">{app.jobTitle}</p>
                      </div>
                      <div className="text-right">
                        <span className="text-sm font-medium text-blue-600">{app.aiScore?.toFixed(0)}%</span>
                        <p className="mt-1 text-xs text-gray-400">{app.status}</p>
                      </div>
                    </div>
                  </div>
                ))}
                {recentApplications.length === 0 && (
                  <div className="p-6 text-center text-gray-500">No applications yet</div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Dashboard;
