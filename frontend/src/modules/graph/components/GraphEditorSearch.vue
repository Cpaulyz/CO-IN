<template>
  <div>
    <div class="title">
      <h3>图谱搜索</h3>
      <el-button type="danger" size="medium" @click="clearHistory">
        清除搜索记录
        <i class="el-icon-delete"></i>
      </el-button>
    </div>
    <el-autocomplete
      style="width: 100%"
      ref="searchInput"
      clearable
      placeholder="搜索关键字(实体id/名称/关系)"
      :fetch-suggestions="queryNodes"
      v-model="searchNodeName"
      @select="dSearch(true)"
      @input="dtSearch(false)"
    >
      <el-button
        slot="append"
        icon="el-icon-search"
        @click="dSearch(true)"
      ></el-button>
    </el-autocomplete>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {
  buildSearchHistoryKey,
  getSearchHistory,
  setSearchHistory
} from '../utils/search'
import { typeMapper } from '../utils/item'
import { debounceAndThrottle } from '../utils/function'

function optionStr(option) {
  return `${typeMapper[option.type]} ${option.id}：${option.name}`
}

export default {
  name: 'GraphEditorSearch',
  props: {
    projectId: {
      type: Number
    },
    nodes: {
      type: Array
    },
    links: {
      type: Array
    }
  },
  data() {
    return {
      searchNodeName: '',
      dtSearch: () => {},
      dSearch: () => {},
      history: {
        key: '',
        list: []
      }
    }
  },
  computed: {
    ...mapGetters(['userId']),
    suggestOptions() {
      const { nodes, links } = this
      // console.log('nodes', nodes)
      // console.log('links', links)

      const options = []
      nodes.forEach(node => {
        const { id, name, group, properties: props } = node
        const propNames = Reflect.ownKeys(props).filter(
          prop => prop !== '__ob__'
        )
        const option = {
          type: 'node',
          id,
          name,
          keys: [
            String(id),
            name,
            group,
            ...propNames.map(prop => prop.toLowerCase()),
            ...propNames
              .map(prop => props[prop])
              .filter(s => !!s)
              .map(val => val.toLowerCase())
          ]
        }
        option.keys.push(optionStr(option).toLowerCase())

        options.push(option)
      })
      return options
    },
    historyInputs() {
      return this.history.list.map(value => ({ value }))
    }
  },
  methods: {
    queryNodes(input, cb) {
      const suggestions = this.getSuggestions(input)
        .map(option => ({
          value: optionStr(option)
        }))
        .filter(({ value }) => !this.history.list.includes(value))
      return cb(this.historyInputs.concat(suggestions))
    },
    getSuggestions(input) {
      const options = this.suggestOptions
      // console.log('options', options)

      const target = input.toLowerCase()

      const suggestion = options.filter(({ keys }) => {
        // console.log('keys', keys)
        return keys.some(key => key.includes(target))
      })
      return suggestion
    },
    search(jump = false) {
      const { searchNodeName, getSuggestions, updateHistory } = this
      const suggestions = getSuggestions(searchNodeName)
      // console.log('searchNodeName', searchNodeName)
      // console.log('suggestions', suggestions)

      if (suggestions.length > 0) {
        updateHistory(searchNodeName)
      }

      const highLightNodeIds = searchNodeName
        ? suggestions.filter(i => i.type === 'node').map(i => i.id)
        : []
      // console.log('highLightNodeIds', highLightNodeIds)
      if (highLightNodeIds.length === 1 && jump) {
        // console.log('single selection')
        const nodeId = highLightNodeIds[0]
        this.$emit('graph-action', 'selectNode', nodeId)

        const node = this.nodes.filter(node => node.id === nodeId)[0]
        this.$emit('editor-action', 'selectNode', node)
      } else {
        // console.log('multiple selection')
        this.$emit('graph-action', 'highLightMultiple', highLightNodeIds)
      }
    },
    updateHistory(input) {
      const { list, key } = this.history
      if (!list.includes(input)) {
        list.push(input)
        setSearchHistory(key, list)
      }
    },
    clearHistory() {
      const list = (this.history.list = [])
      setSearchHistory(this.history.key, list)
    }
  },
  mounted() {
    const { search, userId, projectId, history } = this
    let key
    this.dtSearch = debounceAndThrottle(search, 500, 1000)
    this.dSearch = debounceAndThrottle(search, 1000, 0)
    history.key = key = buildSearchHistoryKey(userId, projectId)
    history.list = getSearchHistory(key)
    // console.log('history', history)
  }
}
</script>

<style scoped>
.title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
