import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex items-center">
            <Link to="/" className="flex items-center">
              <span className="text-2xl font-bold text-blue-600">ATS Portal</span>
            </Link>

            <div className="hidden md:ml-10 md:flex md:space-x-8">
              {user?.role === 'CAN' && (
                <>
                  <Link
                    to="/jobs"
                    className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                  >
                    Browse Jobs
                  </Link>
                  <Link
                    to="/my-applications"
                    className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                  >
                    My Applications
                  </Link>
                </>
              )}

              {(user?.role === 'ADM' || user?.role === 'REC') && (
                <>
                  <Link
                    to="/admin/dashboard"
                    className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                  >
                    Dashboard
                  </Link>
                  <Link
                    to="/admin/jobs"
                    className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                  >
                    Jobs
                  </Link>
                  <Link
                    to="/admin/applications"
                    className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                  >
                    Applications
                  </Link>
                  {user?.role === 'ADM' && (
                    <Link
                      to="/admin/users"
                      className="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium"
                    >
                      Users
                    </Link>
                  )}
                </>
              )}
            </div>
          </div>

          <div className="flex items-center">
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">
                {user?.username} ({user?.role})
              </span>
              <button
                onClick={handleLogout}
                className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 text-sm font-medium"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
