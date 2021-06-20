export const fakeGraphData = {
  nodes: [
    { id: 8, name: '小智', group: '人物', radius: 1 },
    { id: 9, name: 'cyz', group: '人物', radius: 5 },
    { id: 68, name: '皮卡丘', group: '宠物', radius: 3 },
    { id: 69, name: '小智', group: '人物', radius: 1 },
    { id: 6, name: '皮卡丘', group: '宠物', radius: 3 },
    { id: 7, name: '杰尼龟', group: '宠物', radius: 2 }
  ],
  links: [
    { id: 3, name: '主人', source: 8, target: 6, value: 1 },
    { id: 2, name: '主人', source: 9, target: 6, value: 5 },
    { id: 11, name: '主人', source: 8, target: 6, value: 1 },
    { id: 10, name: '主人', source: 9, target: 6, value: 5 },
    { id: 5, name: '朋友', source: 6, target: 7, value: 1 },
    { id: 4, name: '主人', source: 8, target: 7, value: 1 },
    { id: 13, name: '朋友', source: 6, target: 7, value: 1 },
    { id: 12, name: '主人', source: 8, target: 7, value: 1 }
  ]
}