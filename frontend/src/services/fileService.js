const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/ats/api';

const fileService = {
  getDownloadUrl: (fileId) => {
    const token = localStorage.getItem('token');
    return `${API_BASE_URL}/files/${fileId}/download?token=${token}`;
  },

  getViewUrl: (fileId) => {
    const token = localStorage.getItem('token');
    return `${API_BASE_URL}/files/${fileId}/view?token=${token}`;
  },

  downloadFile: (fileId) => {
    const url = fileService.getDownloadUrl(fileId);
    window.open(url, '_blank');
  },

  viewFile: (fileId) => {
    const url = fileService.getViewUrl(fileId);
    window.open(url, '_blank');
  },
};

export default fileService;
