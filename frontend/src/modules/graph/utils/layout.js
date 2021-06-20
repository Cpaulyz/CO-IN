import { consoleGroup } from '@/common/utils'

/**
 * 清理节点固定坐标 fx, fy
 * @param {*} nodes
 * @returns
 */
export const clearFixed = nodes => {
  return nodes.map(node => ({ ...node, fx: null, fy: null }))
}

export const layoutsTransformer = layoutsArr => {
  const layouts = {}
  layoutsArr.forEach(l => {
    const layout = {
      ...l,
      nodes: l.nodes.map(node => ({
        id: node.nodeId,
        x: node.xaxis,
        y: node.yaxis
      }))
    }
    layouts[l.type] = layout
  })
  return layouts
}

const ascOrder = (x, y) => x - y

// 计算排版模式布局
export const getGridLayout = (nodes, config) => {
  // 节点分组
  const groupsObj = {}
  nodes.forEach(({ group, id, radius }) => {
    const node = { id, radius }
    if (!Reflect.has(groupsObj, group)) {
      groupsObj[group] = { d: 0, nodes: [] }
    }
    groupsObj[group].nodes.push(node)
  })

  // 按分组名排序
  const groups = Reflect.ownKeys(groupsObj)
  groups.sort()

  // 总宽度、高度
  let [width, height] = [0, 0]
  const [gap, baseRadius] = [40, config.baseRadius]
  groups.forEach(name => {
    const group = groupsObj[name]

    // 节点按 id 排序
    const nodes = group.nodes
    nodes.sort(({ id: x }, { id: y }) => ascOrder(x, y))

    // 计算组内最大直径
    let d = 0
    let sumHeight = 0
    nodes.forEach(({ radius }) => {
      d = Math.max(d, (baseRadius + radius * 10) * 2)
      sumHeight += radius * 2 + gap
    })

    // 高度/宽度更新
    group.d = d
    width += d + gap
    height = Math.max(height, sumHeight - gap)
  })
  width -= gap

  consoleGroup('[getGridLayout]', () => {
    console.log('groups', groups)
    console.log('groupsObj', groupsObj)
    console.log('width', width)
    console.log('height', height)
  })

  // 计算实际坐标
  let [x, y] = [-width / 2, 0]
  const layoutNodes = []
  groups.forEach(name => {
    const { d, nodes } = groupsObj[name]
    x += d / 2
    const len = nodes.length
    const mid = (len - 1) / 2
    for (let i = 0; i < len; i++) {
      const node = nodes[i]
      layoutNodes.push({ id: node.id, x, y: y + (i - mid) * (d + gap) })
    }
    x += gap + d / 2
  })

  return layoutNodes
}

// 计算节点缩放
export const calcScale = (nodes, config) => {
  if (nodes.length === 0) return 1
  const node0 = nodes[0]
  // console.log('nodes', nodes)

  let [x1, y1, x2, y2] = [
    node0.fx || node0.x,
    node0.fy || node0.y,
    node0.fx || node0.x,
    node0.fy || node0.y
  ]
  nodes.forEach(({ fx, fy, x, y, radius }) => {
    x1 = Math.min(x1, (fx || x) - radius)
    y1 = Math.min(y1, (fy || y) - radius)
    x2 = Math.max(x2, (fx || x) + radius)
    y2 = Math.max(y2, (fy || y) + radius)
  })

  const { width, height, zoomAlpha } = config
  const [realWidth, realHeight] = [x2 - x1, y2 - y1]

  const scale = Math.min(
    (width * zoomAlpha) / realWidth,
    (height * zoomAlpha) / realHeight,
    zoomAlpha
  )

  // consoleGroup('getScale', () => {
  //   console.log(`width = ${width}, height = ${height}`)
  //   console.log(`realWidth = ${realWidth}, realHeight = ${realHeight}`)
  //   console.log(`scale = ${scale}`)
  // })

  return scale
}
