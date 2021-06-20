export default {
  prefix: '/project',
  configs: {
    getProjectInfo(projectId) {
      return { path: `/${projectId}` }
    },
    getListByUserId(userId) {
      return { path: `/listByUserId/${userId}` }
    },
    createProject(projectInfo) {
      return { path: '/create', method: 'POST', data: projectInfo }
    },
    exportProjectXml(projectId) {
      return { path: `/export/${projectId}` }
    },
    updateName(data) {
      return { path: '/updateProjectName', method: 'POST', data }
    },
    updateDescription(data) {
      return { path: '/updateProjectDescription', method: 'POST', data }
    },
    updateStatus(data) {
      return { path: '/updateProjectStatus', method: 'POST', data }
    },
    getAllListByPageNo(pageNo) {
      return { path: `/publicProject/${pageNo}` }
    },
    getOwnListByPageNo(data) {
      return { path: `/listByUserId/${data.userId}/${data.pageNo}` }
    },
    getAllListCount() {
      return { path: '/countPublicProject' }
    },
    getOwnListCount(userId) {
      return { path: `/countByUserId/${userId}` }
    }
  }
}
