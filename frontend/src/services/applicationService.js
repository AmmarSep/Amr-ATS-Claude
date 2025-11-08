import api from './api';

const applicationService = {
  submitApplication: async (jobId, resume, notes) => {
    const formData = new FormData();
    formData.append('jobId', jobId);
    formData.append('resume', resume);
    if (notes) formData.append('notes', notes);

    const response = await api.post('/applications', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  getAllApplications: async (params = {}) => {
    const response = await api.get('/applications', { params });
    return response.data;
  },

  getApplicationById: async (id) => {
    const response = await api.get(`/applications/${id}`);
    return response.data;
  },

  updateApplicationStatus: async (id, status) => {
    const response = await api.patch(`/applications/${id}/status`, null, {
      params: { status },
    });
    return response.data;
  },

  scheduleInterview: async (id, interviewData) => {
    const response = await api.post(`/applications/${id}/interview`, interviewData);
    return response.data;
  },

  updateInterview: async (id, interviewData) => {
    const response = await api.put(`/applications/${id}/interview`, interviewData);
    return response.data;
  },

  cancelInterview: async (id) => {
    const response = await api.delete(`/applications/${id}/interview`);
    return response.data;
  },
};

export default applicationService;
