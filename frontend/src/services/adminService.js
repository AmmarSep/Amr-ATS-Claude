import api from './api';

const adminService = {
  getAllUsers: async (role = null) => {
    const params = role ? { role } : {};
    const response = await api.get('/admin/users', { params });
    return response.data;
  },

  getUserById: async (id) => {
    const response = await api.get(`/admin/users/${id}`);
    return response.data;
  },

  createUser: async (userData) => {
    const response = await api.post('/admin/users', userData);
    return response.data;
  },

  createRecruiter: async (recruiterData) => {
    const response = await api.post('/admin/recruiters', recruiterData);
    return response.data;
  },

  toggleUserStatus: async (id) => {
    const response = await api.patch(`/admin/users/${id}/toggle`);
    return response.data;
  },

  resetPassword: async (id) => {
    const response = await api.post(`/admin/users/${id}/reset-password`);
    return response.data;
  },

  getDashboardStats: async () => {
    const response = await api.get('/admin/dashboard/stats');
    return response.data;
  },
};

export default adminService;
