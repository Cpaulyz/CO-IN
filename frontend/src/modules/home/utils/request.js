import api from '@/api/dispatcher';

export const getAllProjectsById = async projectIds => {
  return Promise.all(
    projectIds.map(projectId => api.getProjectInfo(projectId)),
  );
};
