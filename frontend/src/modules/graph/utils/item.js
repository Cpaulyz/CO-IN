/**
 * 编辑面板属性列
 */

// 编辑面板属性
export const itemOptions = {
  node: [
    { label: '实体名称', attr: 'name', holder: '实体名称', type: 'input' },
    { label: '实体类别', attr: 'group', holder: '实体类别', type: 'input' },
    { label: '实体权重', attr: 'radius', holder: '1~20', type: 'input' },
    { label: '实体颜色', attr: 'color', holder: '实体颜色', type: 'color' },
    { label: '实体形状', attr: 'figure', holder: '实体形状', type: 'input' },
    { label: '字体大小', attr: 'textSize', holder: 'px', type: 'input' }
  ],
  link: [
    { label: '关系名称', attr: 'name', holder: '关系名称', type: 'input' },
    { label: '关系实体1', attr: 'from', holder: '实体名称', type: 'select' },
    { label: '关系实体2', attr: 'to', holder: '实体名称', type: 'select' },
    { label: '关系权重', attr: 'value', holder: '1~20', type: 'input' }
  ]
}

// 对象映射
export const typeMapper = {
  node: '实体',
  link: '关系'
}

/**
 * 图谱图元持久化转换 / 映射
 */

// d3 item 转 后端 item
export const itemTransformer = (type, item, projectId) => {
  return {
    node({ id: nodeId, properties, ...rest }) {
      return {
        projectId,
        nodeId,
        properties: transformProperties(properties),
        ...rest
      }
    },
    link({
      name,
      id: relationId,
      from: source,
      to: target,
      value: width,
      properties
    }) {
      return {
        projectId,
        relationId,
        name,
        source,
        target,
        width,
        properties: transformProperties(properties)
      }
    }
  }[type](item)
}

// 后端 item 转 d3 item
export const responseItemTranformer = (type, item) => {
  return {
    node({ nodeId: id, projectId, ...rest }) {
      return { id, ...rest }
    },
    link({ name, relationId: id, source, target, width: value }) {
      return { name, id, source, target, value, from: source, to: target }
    }
  }[type](item)
}

export const transformProperties = propsList => {
  const props = {}
  for (const { name, value } of propsList) {
    props[name] = value
  }
  return props
}

// 转换格式
export const itemVarify = (type, item) => {
  return {
    node({ radius, ...rest }) {
      radius = +radius
      return { radius, ...rest, properties: {} }
    },
    link({ width, from, to, ...rest }) {
      width = +width
      from = +from
      to = +to
      return { width, from, to, ...rest }
    }
  }[type](item)
}
