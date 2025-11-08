import api from './api';

const jobService = {
  getAllActiveJobs: async () => {
    const response = await api.get('/jobs');
    return response.data;
  },

  getAllJobs: async () => {
    const response = await api.get('/jobs/all');
    return response.data;
  },

  getJobById: async (id) => {
    const response = await api.get(`/jobs/${id}`);
    return response.data;
  },

  createJob: async (jobData) => {
    const response = await api.post('/jobs', jobData);
    return response.data;
  },

  updateJob: async (id, jobData) => {
    const response = await api.put(`/jobs/${id}`, jobData);
    return response.data;
  },

  toggleJobStatus: async (id) => {
    const response = await api.patch(`/jobs/${id}/toggle`);
    return response.data;
  },
};

export default jobService;
