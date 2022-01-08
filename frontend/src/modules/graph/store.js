import api from '@/api/dispatcher';
import { $notify } from '@/common/utils';

import { svgToPng, download, xmlDownload } from './utils/saving';
import { transformProperties, typeMapper, itemTransformer } from './utils/item';
import { deepCopy } from '../../common/utils';
import { _graphData } from './utils/data';

const graph = {
  state: {
    projectId: -1,
    showModifyNameModal: false,
    showModifyDescModal: false,
    showModifyStatusModal: false,
    showModifyProjectInfoModal: false,
  },
  mutations: {
    setGraphProjectId(state, id) {
      state.projectId = id;
    },
    setShowModifyNameModal(state, data) {
      state.showModifyNameModal = data;
    },
    setShowModifyDescModal(state, data) {
      state.showModifyDescModal = data;
    },
    setShowModifyStatusModal(state, data) {
      state.showModifyStatusModal = data;
    },
    setShowModifyProjectInfoModal(state, data) {
      state.showModifyProjectInfoModal = data;
    },
  },
  actions: {
    // async getProjectInfo(_, projectId) {
    //   const res = await api.getProjectInfo(projectId)
    //   return res.status === 200 ? res.data : null
    // },
    async getProjectGraphData(_, projectId) {
      const res = await api.getGraphByProjectId(projectId);
      // const res = { data: _graphData, status: 200 }
      return res.status === 200 ? res.data : null;
    },
    /********** new **********/
    async createItem(_, { projectId, type, item }) {
      const data = itemTransformer(type, item, projectId);
      console.log(`[action] createItem: type=${type}, item=`, data);
      if (type === 'node' || type === 'link') {
        const res =
          type === 'node'
            ? await api.graphInsertNode(data)
            : await api.graphInsertRel(data);
        // console.log('res', res)
        if (res.status === 200) {
          $notify({ title: `新增${typeMapper[type]}成功`, type: 'success' });
          return type === 'node'
            ? {
                ...item,
                radius: Number(item.radius),
                id: res.data.nodeId,
                properties: transformProperties(item.properties),
              }
            : {
                ...item,
                id: res.data.relationId,
                source: data.source,
                target: data.target,
                properties: transformProperties(item.properties),
              };
        } else {
          $notify({ title: `新增${typeMapper[type]}失败`, type: 'error' });
          return false;
        }
      }
      $notify({ title: `类型错误`, type: 'error' });
      return false;
    },
    async updateItem(_, { projectId, type, item }) {
      const data = itemTransformer(type, item, projectId);
      console.log(`[action] updateItem: type=${type}, item=`, data);
      if (type === 'node' || type === 'link') {
        const res =
          type === 'node'
            ? await api.graphUpdateNode(data)
            : await api.graphUpdateRel(data);
        // console.log('res', res)
        if (res.status === 200) {
          $notify({ title: `修改${typeMapper[type]}成功`, type: 'success' });
          return type === 'node'
            ? {
                ...item,
                radius: Number(item.radius),
                properties: transformProperties(item.properties),
              }
            : {
                ...item,
                source: data.source,
                target: data.target,
                properties: transformProperties(item.properties),
              };
        } else {
          $notify({ title: `修改${typeMapper[type]}失败`, type: 'error' });
          return false;
        }
      }
      $notify({ title: `类型错误`, type: 'error' });
      return false;
    },
    async deleteItem(_, { projectId, type, id }) {
      console.log(`[action] deleteItem: type=${type}, id=${id}`);
      if (type === 'node' || type === 'link') {
        const res =
          type === 'node'
            ? await api.graphDeleteNodeCascade(id, projectId)
            : await api.graphDeleteRel(id, projectId);
        // console.log('res', res)
        if (res.status === 200) {
          $notify({ title: `删除${typeMapper[type]}成功`, type: 'success' });
          return true;
        } else {
          $notify({ title: `删除${typeMapper[type]}失败`, type: 'error' });
          return false;
        }
      }
      $notify({ title: `类型错误`, type: 'error' });
      return false;
    },
    /********** other **********/
    async updateLayout(_, layout) {
      console.log(`[action] save layout: layout=`, layout);
      const beLayout = {
        ...layout,
        nodes: layout.nodes.map(({ id, x, y }) => ({
          nodeId: id,
          xaxis: x,
          yaxis: y,
        })),
      };
      const res = await api.updateLayout(beLayout);
      if (res.status === 200) {
        $notify({ title: '保存布局成功', type: 'success' });
        return true;
      } else {
        $notify({ title: '保存布局失败，请重新尝试', type: 'error' });
        return false;
      }
    },
    // 持久化相关
    saveAsPng(_, { projectName, svg }) {
      console.log('[action] saveAsPng');

      const group = svg._groups[0][0];
      const width = group.width.baseVal.value;
      const height = group.height.baseVal.value;

      svgToPng(svg, width, height).then(dataUrl => {
        download(`知识图谱-${projectName}.png`, dataUrl);
      });
    },
    async saveAsXml(_, { projectId, name }) {
      const res = await api.exportProjectXml(projectId);
      xmlDownload(res.data.xml, `知识图谱-${name}.xml`);
    },
    /********** project info **********/
    updateProjectName: async (_, data) => {
      const res = await api.updateName(data);
      return res.data;
    },
    updateProjectDesc: async (_, data) => {
      const res = await api.updateDescription(data);
      return res.data;
    },
    updateProjectStatus: async (_, data) => {
      const res = await api.updateStatus(data);
      return res.data;
    },
    updateProjectInfo: async ({ dispatch, commit }, data) => {
      const { nameData, descData, statusData } = data;
      const res1 = await dispatch('updateProjectName', nameData);
      const res2 = await dispatch('updateProjectDesc', descData);
      const res3 = await dispatch('updateProjectStatus', statusData);
      return res1 && res2 && res3;
    },
  },
  getters: {
    projectId: state => state.projectId,
    showModifyNameModal: state => state.showModifyNameModal,
    showModifyDescModal: state => state.showModifyDescModal,
    showModifyStatusModal: state => state.showModifyStatusModal,
    showModifyProjectInfoModal: state => state.showModifyProjectInfoModal,
  },
};

export default graph;
