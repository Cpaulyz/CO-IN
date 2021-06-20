<template>
  <svg id="graph"></svg>
</template>

<script>
import { mapActions } from 'vuex'
import { deepCopy } from '../../../common/utils/object'
import { consoleGroup } from '../../../common/utils/message'
import config from '../utils/config'
import { getGridLayout, calcScale } from '../utils/layout'

export default {
  name: 'GraphBoard',
  props: {
    data: {
      type: Object
    }
  },
  data() {
    return {
      origin: null,
      config: { ...config },
      projectInfo: null,
      layouts: null,
      layoutMode: 'FORCE',
      nodes: [],
      links: [],
      focusNodes: [],
      selectedGroups: [],
      svgElements: {
        simulation: null,
        svg: null,
        defs: null,
        marker: null,
        view: null,
        root: null,
        focusGroup: null,
        svgLinks: null,
        svgLinksText: null,
        svgNodes: null,
        svgNodesText: null,
        boundDrag: null,
        boundZoom: null,
        scale: null
      },
      flags: {
        loaded: false,
        singleFocus: true,
        enableFocus: true,
        pinned: false,
        locked: false,
        selectingMode: false
      }
    }
  },
  methods: {
    ...mapActions(['updateLayout', 'saveAsPng', 'saveAsXml']),
    /***** 重新挂载图谱 *****/
    mountGraphData(data, projectInfo) {
      this.origin = deepCopy(data)
      this.projectInfo = deepCopy(projectInfo)

      const { nodes, links, layouts } = data
      this.nodes = [...nodes]
      this.links = [...links]
      this.layouts = deepCopy(layouts)

      // consoleGroup('mountGraphData', () => {
      //   console.log('nodes:', this.nodes)
      //   console.log('links:', this.links)
      //   console.log('layouts:', this.layouts)
      // })

      this.init()
      this.flags.loaded = true
    },
    /***** 图谱绘制 *****/
    // 初始化图谱
    init() {
      const {
        // 全局变量 & 配置
        $d3,
        $el,
        config: { width, height },
        // 子例程
        reset,
        setSimulation,
        setDefs,
        setView,
        setRoot,
        setFocusGroup,
        setLinks,
        setLinksText,
        setNodes,
        setNodesText,
        // 力导图绑定事件
        setDrag,
        setZoom,
        tick,
        // 图谱操作
        resetZoom,
        setEnableFocus,
        restoreLayout
      } = this
      // 清除图谱
      reset()
      setEnableFocus(false)

      const simulation = setSimulation()

      const svg = $d3
        .select($el)
        .attr('viewBox', [-width / 2, -height / 2, width, height])
      this.svgElements.svg = svg

      // 设置 defs 定义组
      // setDefs(svg)

      // 设置透明操作板
      const view = setView(svg)
      const root = setRoot(svg)

      const boundZoom = setZoom(root)
      view.call(boundZoom)

      // 设置高亮组
      setFocusGroup(root)

      // 设置关系、关系文字
      setLinks(root)
      setLinksText(root)

      const boundDrag = setDrag(simulation)
      const scale = $d3.scaleOrdinal($d3.schemeCategory10)
      this.svgElements.scale = scale
      this.$emit('init-property', 'nodeScale', scale)

      // 设置节点、节点文字
      setNodes(root, boundDrag, scale)
      setNodesText(root, boundDrag)

      simulation.on('tick', tick)

      // setTimeout(() => {
      //   resetZoom()
      // }, 300)
      setTimeout(() => {
        resetZoom()
      }, 600)
      setEnableFocus(true)
      restoreLayout()
    },
    // 重置图谱节点
    reset() {
      const { view, root } = this.svgElements
      if (view) view.remove()
      if (root) root.remove()
    },
    // 设置力导图初始化
    setSimulation() {
      const {
        $d3,
        nodes,
        links,
        config: { baseRadius }
      } = this
      const simulation = $d3
        .forceSimulation(nodes)
        .force(
          'link',
          $d3
            .forceLink(links)
            .id(d => d.id)
            .distance(
              d => (d.source.radius + d.target.radius) * 30 + baseRadius
            )
        )
        .force(
          'charge',
          $d3.forceManyBody().strength(d => -1000 - d.radius * 300)
        )
        .force('x', $d3.forceX())
        .force('y', $d3.forceY())
      this.svgElements.simulation = simulation
      return simulation
    },
    setDefs(svg) {
      const defs = this.svgElements.defs || svg.append('defs')
      this.svgElements.defs = defs
      return defs
    },
    // 设置拖曳背景图
    setView(svg) {
      const { width, height } = this.config
      const view = svg
        .append('rect')
        .attr('class', 'view')
        .style('fill', 'transparent')
        .attr('x', -width / 2)
        .attr('y', -height / 2)
        .attr('width', width)
        .attr('height', height)
        .on('click', this.clickView)
      this.svgElements.view = view
      return view
    },
    // 设置图谱根节点
    setRoot(svg) {
      const root = svg.append('g').attr('class', 'root')
      this.svgElements.root = root
      return root
    },
    // 设置图谱高亮组
    setFocusGroup(root) {
      const focusGroup = root
        .append('g')
        .attr('class', 'focus')
        .attr('stroke', 'skyblue')
        .attr('stroke-width', 10)
      this.svgElements.focusGroup = focusGroup
    },
    setLinks(root) {
      const { links, clickLink } = this

      // 设置关系线段
      const svgLinks = root
        .append('g')
        .attr('class', 'links')
        .attr('stroke', '#999')
        .attr('stroke-opacity', 0.6)
        .selectAll('line')
        .data(links)
        .join('line')
        .attr('stroke-width', d => d.value * 5)
        .attr('id', d => `link-${d.id}`)
        .attr('data-id', d => d.id)
        .attr('marker-end', d => `url(#arrow-${d.id})`)
        .on('click', clickLink)
      svgLinks.append('title').text(d => d.name)

      this.svgElements.svgLinks = svgLinks
      return svgLinks
    },
    setLinksText(root) {
      const {
        config: { font, fontSize },
        setLinksPositionRaio,
        links,
        clickLink
      } = this
      const svgLinksText = root
        .append('g')
        .attr('class', 'links_text')
        .selectAll('text')
        .data(links)
        .join('text')
        .style('fill', '#000000')
        .style('font', `${fontSize}px ${font}`)
        .style('user-select', 'none')
        .attr('dominant-baseline', 'middle')
        .attr('text-anchor', 'middle')
        .attr('data-id', d => d.id)
        .text(d => d.name)
        .on('click', clickLink)
      this.svgElements.svgLinksText = svgLinksText

      setLinksPositionRaio()

      return svgLinksText
    },
    setNodes(root, boundDrag, scale) {
      const {
        config: { baseRadius },
        nodes,
        focus,
        unfocus,
        clickNode
      } = this
      const svgNodes = root
        .append('g')
        .attr('class', 'nodes')
        .attr('stroke', '#ffffff')
        .attr('stroke-width', 1.5)
        .selectAll('circle')
        .data(nodes)
        .join('circle')
        .attr('r', d => baseRadius + d.radius * 10)
        .attr('fill', d => (d.color ? d.color : scale(d.group)))
        .attr('data-id', d => d.id)
        .call(boundDrag)
        .on('click', clickNode)
        .on('mouseover', focus)
        .on('mouseout', unfocus)
      svgNodes.append('title').text(d => d.name)
      this.svgElements.svgNodes = svgNodes
      return svgNodes
    },
    setNodesText(root, boundDrag) {
      const {
        config: { font },
        nodes,
        clickNode,
        focus,
        unfocus
      } = this
      const svgNodesText = root
        .append('g')
        .attr('class', 'nodes_text')
        .selectAll('text')
        .data(nodes)
        .join('text')
        .style('fill', '#ffffff')
        .style('font', d => `${d.textSize}px ${font}`)
        .style('user-select', 'none')
        .attr('dominant-baseline', 'middle')
        .attr('text-anchor', 'middle')
        .attr('data-id', d => d.id)
        .text(d => d.name)
        .call(boundDrag)
        .on('click', clickNode)
        .on('mouseover', focus)
        .on('mouseout', unfocus)
      this.svgElements.svgNodesText = svgNodesText
      return svgNodesText
    },
    // 设置 focus 节点
    setFocus(node) {
      const {
        svgElements: { focusGroup: fg },
        flags: { enableFocus },
        setNodeHighLight,
        updateHighLightNodes
      } = this
      if (!enableFocus) return

      if (fg.select(`#focus-node-${node.id}`).empty()) {
        setNodeHighLight(node, true)
        updateHighLightNodes()
      }
    },
    updateHighLightNodes() {
      const {
        config: { baseRadius },
        nodes,
        svgElements: { focusGroup: fg }
      } = this
      const originFocus = fg.selectAll('circle')
      fg.selectAll('circle')
        .data(nodes.filter(node => node.highLight || node.focus))
        .join('circle')
        .attr('r', d => baseRadius + d.radius * 10 + 5)
        .attr('fill', 'none')
        .attr('id', d => `focus-node-${d.id}`)
        .attr('cx', d => d.x)
        .attr('cy', d => d.y)
        .merge(originFocus)
    },
    // 力导图更新
    tick() {
      const {
        focusGroup,
        svgLinks,
        svgLinksText,
        svgNodes,
        svgNodesText
      } = this.svgElements

      svgLinks
        .attr('x1', d => d.source.x)
        .attr('y1', d => d.source.y)
        .attr('x2', d => d.target.x)
        .attr('y2', d => d.target.y)

      svgLinksText
        .attr('x', d => {
          const {
            source: { x: x1 },
            target: { x: x2 },
            positionRatio
          } = d
          return x1 + (x2 - x1) * positionRatio
        })
        .attr('y', d => {
          const {
            source: { y: y1 },
            target: { y: y2 },
            positionRatio
          } = d
          return y1 + (y2 - y1) * positionRatio
        })

      svgNodes.attr('cx', d => d.x).attr('cy', d => d.y)
      svgNodesText.attr('x', d => d.x).attr('y', d => d.y)

      focusGroup
        .selectAll('circle')
        .attr('cx', d => d.x)
        .attr('cy', d => d.y)
    },
    updateNodesWithText() {
      if (!this.flags.loaded) return
      const {
        config: { baseRadius, font, opacity },
        nodes,
        links,
        svgElements: {
          simulation,
          root,
          svgNodes,
          svgNodesText,
          boundDrag,
          scale
        },
        clickNode,
        focus,
        unfocus,
        updateAllOpacity
      } = this
      // console.log('updateNodesWithText', nodes)
      updateAllOpacity()

      // update nodes
      let _svgNodes = root
        .select('g.nodes')
        .selectAll('circle')
        .data(nodes)
        .join('circle')
        .attr('r', d => baseRadius + d.radius * 10)
        .attr('fill', d => (d.color ? d.color : scale(d.group)))
        .attr('opacity', d => (d.opacity ? opacity : 1))
        .attr('data-id', d => d.id)
        .attr('cx', d => d.x)
        .attr('cy', d => d.y)
        .call(boundDrag)
        .on('click', clickNode)
        .on('mouseover', focus)
        .on('mouseout', unfocus)
      _svgNodes.append('title').text(d => d.name)
      _svgNodes = _svgNodes.merge(svgNodes)
      this.svgElements.svgNodes = _svgNodes

      // update nodes text
      const _svgNodesText = root
        .select('g.nodes_text')
        .selectAll('text')
        .data(nodes)
        .join('text')
        .style('fill', '#ffffff')
        .style('font', d => `${d.textSize}px ${font}`)
        .style('user-select', 'none')
        .attr('dominant-baseline', 'middle')
        .attr('text-anchor', 'middle')
        .attr('data-id', d => d.id)
        .text(d => d.name)
        .call(boundDrag)
        .on('click', clickNode)
        .on('mouseover', focus)
        .on('mouseout', unfocus)
        .merge(svgNodesText)
      this.svgElements.svgNodesText = _svgNodesText

      simulation.nodes(nodes)
      simulation.force('link').links(links)
      simulation.alpha(1).restart()
    },
    updateLinksWithText() {
      if (!this.flags.loaded) return
      const {
        config: { fontSize, font, opacity },
        nodes,
        links,
        svgElements: { simulation, root, svgLinks, svgLinksText },
        setLinksPositionRaio,
        clickLink,
        updateAllOpacity
      } = this
      // console.log('updateLinksWithText', links)
      updateAllOpacity()

      // update links
      let _svgLinks = root
        .select('g.links')
        .selectAll('line')
        .data(links)
        .join('line')
        .attr('stroke-width', d => d.value * 5)
        .attr('opacity', d => (d.opacity ? opacity : 1))
        .attr('id', d => `link-${d.id}`)
        .attr('data-id', d => d.id)
        .attr('marker-end', d => `url(#arrow-${d.id})`)
        .on('click', clickLink)
      _svgLinks.append('title').text(d => d.name)
      _svgLinks = _svgLinks.merge(svgLinks)
      this.svgElements.svgLinks = _svgLinks

      // update links text
      const _svgLinksText = root
        .select('g.links_text')
        .selectAll('text')
        .data(links)
        .join('text')
        .style('fill', '#000000')
        .style('font', `${fontSize}px ${font}`)
        .attr('opacity', d => (d.opacity ? opacity : 1))
        .style('user-select', 'none')
        .attr('dominant-baseline', 'middle')
        .attr('text-anchor', 'middle')
        .attr('data-id', d => d.id)
        .text(d => d.name)
        .on('click', clickLink)
        .merge(svgLinksText)
      this.svgElements.svgLinksText = _svgLinksText

      simulation.nodes(nodes)
      simulation.force('link').links(links)
      simulation.alpha(1).restart()

      setLinksPositionRaio()
    },
    setLinksPositionRaio() {
      const {
        config: { baseRadius },
        links
      } = this
      links.forEach(link => {
        const {
          source: { radius: r1 },
          target: { radius: r2 }
        } = link
        link.positionRatio =
          (baseRadius + r1 * 20) / ((r1 + r2) * 30 + baseRadius)
      })
    },
    updateFocus() {
      const {
        config: { baseRadius },
        svgElements: { focusGroup: fg }
      } = this
      const originFocus = fg.selectAll('circle')
      fg.selectAll('circle')
        .data(this.nodes.filter(node => node.highLight || node.focus))
        .join('circle')
        .attr('r', d => baseRadius + d.radius * 10 + 5)
        .attr('fill', 'none')
        .attr('id', d => `focus-node-${d.id}`)
        .attr('cx', d => d.x)
        .attr('cy', d => d.y)
        .merge(originFocus)
    },
    /********** 外部节点操作 **********/
    createNode(node) {
      console.log('[GraphBoard] createNode', node)
      const {
        nodes,
        updateNodesWithText,
        setNodeFocus,
        setFocus,
        layoutMode,
        restoreLayout
      } = this
      nodes.push(node)
      updateNodesWithText()
      setNodeFocus(node)
      setFocus(node)
      layoutMode === 'GRID' && restoreLayout(true)
    },
    createLink(link) {
      console.log('[GraphBoard] createLink', link)
      const { links, updateLinksWithText } = this
      links.push(link)
      updateLinksWithText()
    },
    updateNode(node) {
      console.log('[GraphBoard]', node)
      const {
        nodes,
        updateNodesWithText,
        setNodeFocus,
        updateFocus,
        layoutMode,
        restoreLayout
      } = this
      const _node = nodes.filter(_node => _node.id === node.id)[0]
      for (const prop in _node) {
        _node[prop] = node[prop]
      }
      updateNodesWithText()
      setNodeFocus(_node)
      updateFocus()
      layoutMode === 'GRID' && restoreLayout(true)
    },
    updateLink(link) {
      // console.log('[GraphBoard]', link)
      const _link = this.links.filter(_link => _link.id === link.id)[0]
      for (const prop in _link) {
        link[prop] = link[prop]
      }
      this.updateLinksWithText()
    },
    deleteNode(nodeId) {
      // console.log('[GraphBoard]', nodeId)
      const {
        nodes,
        links,
        updateNodesWithText,
        updateLinksWithText,
        clearFocus,
        layoutMode,
        restoreLayout
      } = this
      // delete node
      const node = nodes.filter(node => node.id === nodeId)[0]
      nodes.splice(nodes.indexOf(node), 1)

      // delete links
      this.links = links.filter(
        ({ from, to }) => from !== node.id && to !== node.id
      )
      updateNodesWithText()
      updateLinksWithText()
      clearFocus()
      layoutMode === 'GRID' && restoreLayout(true)
    },
    deleteLink(linkId) {
      // console.log('[GraphBoard]', linkId)
      const { links } = this
      const link = links.filter(link => link.id === linkId)[0]
      links.splice(links.indexOf(link), 1)
      this.updateLinksWithText()
    },
    /********** 外部图谱操作 **********/
    // 重置缩放
    resetZoom() {
      const {
        $d3,
        config,
        nodes,
        svgElements: { view, boundZoom }
      } = this
      const scale = calcScale(nodes, config)
      view
        .transition()
        .duration(750)
        .call(boundZoom.transform, $d3.zoomIdentity.scale(scale))
    },
    // 随机分布
    randomDisorder() {
      const {
        svgElements: { simulation }
      } = this
      if (this.layoutMode === 'FORCE') {
        this.nodes.forEach(node => {
          node.vx = node.vy = null
          node.x = node.y = null
          node.fx = node.fy = null
        })
        simulation.alphaTarget(0.3).restart()
      }
    },
    // 切换布局
    switchLayout(mode) {
      if (this.layoutMode !== mode) {
        this.layoutMode = mode
        this.restoreLayout()
      }
    },
    // 保存布局
    saveLayout() {
      const { layoutMode, nodes, layouts, updateLayout, projectInfo } = this
      const layoutNodes = nodes.map(({ id, x, y }) => ({ id, x, y }))
      console.log(`saveLayout layout mode: ${layoutMode}`, layoutNodes)
      updateLayout({
        type: layoutMode,
        projectId: projectInfo.projectId,
        nodes: layoutNodes
      }).then(res => {
        if (res) {
          layouts[layoutMode].nodes = layoutNodes
        }
      })
    },
    // 恢复布局
    restoreLayout(bool) {
      const {
        config,
        layoutMode,
        layouts,
        nodes,
        pin,
        unPin,
        flags: { pinned },
        setLocked
      } = this
      if (
        bool ||
        (layoutMode === 'GRID' && layouts.GRID.nodes.length !== nodes.length)
      ) {
        layouts.GRID.nodes = getGridLayout(nodes, config)
      }
      const layoutNodesMapper = {}
      console.log('layouts:', layouts)
      console.log('layoutMode:', layoutMode)
      layouts[layoutMode].nodes.forEach(({ id, x, y }) => {
        layoutNodesMapper[id] = { x, y }
      })
      console.log(`restoreLayout ${layoutMode}:`, layoutNodesMapper)

      unPin()
      nodes.forEach(node => {
        if (Reflect.has(layoutNodesMapper, node.id)) {
          const { x, y } = layoutNodesMapper[node.id]
          node.x = x
          node.y = y
          if (pinned) {
            node.fx = x
            node.fy = y
          }
        }
      })
      setLocked(layoutMode === 'GRID')
      layoutMode === 'FORCE' ? unPin() : pin()
    },
    // 选取类别
    selectGroups(groups) {
      // console.log('selectGroups', groups)
      this.setSelectedGroups(groups)
      this.updateNodesWithText()
      this.updateLinksWithText()
    },
    // 直接选中单个节点
    selectNode(nodeId) {
      // console.log('graph selectNode', nodeId)
      const node = this.getNodeById(nodeId)
      this.setFocus(node)
      this.setNodeFocus(node)
    },
    // 多个节点一次高亮
    highLightMultiple(nodeIds) {
      this.clearFocus()
      // console.log('highLightMultiple', nodeIds)
      const nodes = this.getNodesByIds(nodeIds)
      nodes.forEach(node => this.setNodeHighLight(node, true))
      this.updateHighLightNodes()
    },
    // 切换选中模式
    setSelectingMode(mode) {
      this.flags.selectingMode = mode
    },
    selectInSelectingMode() {},
    /********** 图谱操作 **********/
    // 设置高亮组
    setFocusNodes(nodeIds) {
      const {
        config: { baseRadius },
        focusNodes,
        svgElements: { focusGroup: fg },
        getNodesByIds,
        setNodeFocus
      } = this
      const targetNodes = getNodesByIds(nodeIds)
      targetNodes.forEach(node => setNodeFocus(node))

      fg.selectAll('circle')
        .data(focusNodes)
        .enter()
        .insert('circle')
        .attr('r', d => baseRadius + d.radius * 10 + 5)
        .attr('fill', 'none')
        .attr('id', d => `focus-node-${d.id}`)
        .attr('cx', d => d.x)
        .attr('cy', d => d.y)
    },
    // 清除高亮
    clearFocus() {
      this.svgElements.focusGroup.selectAll('circle').remove()
      this.nodes.forEach(node => {
        this.setNodeHighLight(node, false)
        this.clearNodeFocus(node)
      })
    },
    // 固定节点
    pin() {
      const {
        nodes,
        flags: { pinned }
      } = this
      if (!pinned) {
        this.flags.pinned = true
        nodes.forEach(node => {
          node.fx = node.x
          node.fy = node.y
        })
      }
    },
    // 取消固定
    unPin() {
      const {
        svgElements: { simulation },
        nodes
      } = this
      this.flags.pinned = false
      nodes.forEach(node => {
        node.fx = null
        node.fy = null
      })
      simulation.alpha(1).restart()
    },
    /********** 节点/关系操作事件 **********/
    // 点击节点
    clickNode(e) {
      const id = Number(e.target.attributes['data-id'].value)
      const node = this.getNodeById(id)
      // console.log(`click node: id=${id}, `, node)

      if (this.flags.selectingMode) {
        this.flags.selectingMode = false
        this.$emit('editor-action', 'catchNode', node)
      } else {
        this.setNodeFocus(node)
        this.$emit('editor-action', 'selectNode', { ...node })
      }
    },
    // 点击关系
    clickLink(e) {
      if (this.flags.selectingMode) return
      const id = Number(e.target.attributes['data-id'].value)
      const link = this.getLinkById(id)
      // console.log(`click link: id=${id}, `, link)

      this.clearFocus()
      this.$emit('editor-action', 'selectLink', { ...link })
    },
    clickView() {
      if (this.flags.selectingMode) return
      this.clearFocus()
      this.$emit('editor-action', 'selectNone')
    },
    // 聚焦(高亮显示)
    focus(e) {
      const {
        getNodeById,
        setFocus,
        flags: { enableFocus }
      } = this
      if (!enableFocus) return

      const id = Number(e.target.attributes['data-id'].value)
      const targetNode = getNodeById(id)
      if (!targetNode) return

      setFocus(targetNode)
    },
    // 取消聚焦
    unfocus(e) {
      const {
        flags: { enableFocus },
        _unfocus
      } = this
      if (!enableFocus) return

      const id = Number(e.target.attributes['data-id'].value)
      _unfocus(id)
    },
    _unfocus(id) {
      const {
        svgElements: { focusGroup: fg },
        getNodeById,
        setNodeHighLight
      } = this
      const targetNode = getNodeById(id)
      setNodeHighLight(targetNode, false)
      const focusNode = fg.select(`#focus-node-${id}`)
      if (!focusNode.empty()) {
        if (targetNode && targetNode.focus) return

        focusNode.remove()
      }
    },
    /********** 力导图绑定事件 **********/
    // 拖曳设置
    setDrag(simulation) {
      const { setEnableFocus, flags, nodes } = this

      const start = (e, d) => {
        if (!e.active) simulation.alphaTarget(0.3).restart()
        setEnableFocus(false)
        if (flags.locked) {
          const group = d.group
          nodes
            .filter(node => node.group === group)
            .forEach(node => {
              node.fx = d.x
            })
          console.log(nodes.filter(node => node.group === group))
        } else {
          d.fx = d.x
          d.fy = d.y
        }
      }

      const drag = (e, d) => {
        if (flags.locked) {
          const group = d.group
          nodes
            .filter(node => node.group === group)
            .forEach(node => {
              node.fx = e.x
            })
        } else {
          d.fx = e.x
          d.fy = e.y
        }
      }

      const end = (e, d) => {
        setEnableFocus(true)
        if (flags.locked) return
        if (!e.active) simulation.alphaTarget(0)
        d.x = d.fx
        d.y = d.fy
        if (!flags.pinned) {
          d.fx = null
          d.fy = null
        }
      }

      const boundDrag = this.$d3
        .drag()
        .on('start', start)
        .on('drag', drag)
        .on('end', end)
      this.svgElements.boundDrag = boundDrag
      return boundDrag
    },
    // 缩放&平移设置
    setZoom(root) {
      const boundZoom = this.$d3.zoom().on('zoom', e => {
        root.attr('transform', e.transform)
      })
      this.svgElements.boundZoom = boundZoom
      return boundZoom
    },
    /********** 数据属性操作 **********/
    // 根据 id 查找节点
    getNodeById(id) {
      return this.nodes.filter(node => node.id === id)[0]
    },
    getNode(nodeOrId) {
      return typeof nodeOrId === 'object'
        ? nodeOrId
        : this.getNodeById(nodeOrId)
    },
    // 根据 id 查找关系
    getLinkById(id) {
      return this.links.filter(link => link.id === id)[0]
    },
    // 设置选中组别
    setSelectedGroups(groups) {
      this.selectedGroups = groups
      this.updateAllOpacity()
    },
    updateAllOpacity() {
      const { selectedGroups: groups, nodes, links } = this
      nodes.forEach(node => {
        node.opacity = !groups.includes(node.group)
      })
      links.forEach(link => {
        link.opacity = link.source.opacity || link.target.opacity
      })
    },
    // 设置节点高亮标志
    setNodeHighLight(nodeOrId, bool = false) {
      const node = this.getNode(nodeOrId)
      node.highLight = bool
    },
    setNodeFocus(nodeOrId) {
      const {
        nodes,
        flags: { singleFocus },
        _unfocus,
        getNode
      } = this
      const node = getNode(nodeOrId)

      if (singleFocus) {
        nodes
          .filter(n => n !== node)
          .forEach(node => {
            if (node.focus) {
              node.focus = false
              _unfocus(node.id)
            }
          })
      }
      node.focus = true
    },
    clearNodeFocus(nodeOrId) {
      const node = this.getNode(nodeOrId)
      node.focus = false
    },
    getNodesByIds(ids) {
      return this.nodes.filter(node => ids.includes(node.id))
    },
    /********** 图谱标志 **********/
    setEnableFocus(bool = true) {
      this.flags.enableFocus = bool
    },
    setLocked(bool = false) {
      this.flags.locked = bool
    },
    /********** 图谱标志 **********/
    exportPng() {
      const projectName = this.projectInfo.name
      const svg = this.svgElements.svg
      this.saveAsPng({ projectName, svg })
    },
    exportXml() {
      const { projectId, name } = this.projectInfo
      this.saveAsXml({ projectId, name })
    }
  }
}
</script>

<style scoped>
#graph {
  width: 100%;
  height: 100%;
}
</style>
