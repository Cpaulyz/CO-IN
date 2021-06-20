export const fakeProjectInfo = {
  projectId: 1,
  name: '知识图谱1',
  description: '这是第一个知识图谱',
  userId: 1,
  xml: null
}

// base on: /project/listByUserId/{userId}  根据用户搜索知识图谱项目列表
export const fakeProjects = [
  {
    description: 'string',
    name: 'string',
    projectId: 1,
    userId: 0,
    xml: 'string'
  },
  {
    description: 'string',
    name: 'string',
    projectId: 2,
    userId: 0,
    xml: 'string'
  },
  {
    description: 'string',
    name: 'string',
    projectId: 3,
    userId: 0,
    xml: 'string'
  }
]


export const fakeXml = `
<project>
  <nodes>
    <node>
      <name>小智</name>
      <group>1</group>
      <val>6</val>
    </node>
    <node>
      <name>杰尼龟</name>
      <group>2</group>
      <val>2</val>
    </node>
    <node>
      <name>皮卡丘</name>
      <group>2</group>
      <val>4</val>
    </node>
    <node>
      <name>陈彦泽</name>
      <group>1</group>
      <val>4</val>
    </node>
  </nodes>
  <relations>
    <relation>
      <name>主人</name>
      <source>1</source>
      <target>2</target>
      <val>2</val>
    </relation>
    <relation>
      <name>主人</name>
      <source>1</source>
      <target>3</target>
      <val>2</val>
    </relation>
    <relation>
      <name>主人</name>
      <source>2</source>
      <target>4</target>
      <val>4</val>
    </relation>
    <relation>
      <name>朋友</name>
      <source>2</source>
      <target>3</target>
      <val>5</val>
    </relation>
  </relations>
</project>
`

export const fakeAllProjects = [
  {
    description: '这是第一个知识图谱',
    layoutStatus: 'FORCE',
    name: '知识图谱1 plaza',
    projectId: 1,
    userId: 1
  },
  {
    description: '这是第二个知识图谱',
    layoutStatus: 'FORCE',
    name: '知识图谱2 plaza',
    projectId: 2,
    userId: 1
  },
  {
    description: '这是第三个知识图谱',
    layoutStatus: 'FORCE',
    name: '知识图谱3 plaza',
    projectId: 3,
    userId: 1
  }
]
