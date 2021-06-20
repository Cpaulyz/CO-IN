<template>
  <div>
    <div v-if="!selectedItem.type">
      <slot />
      <h4>或</h4>
      <h3>点击实体/关系查看细节</h3>
    </div>
    <div v-else>
      <!-- 编辑器头部 -->
      <div class="header">
        <div class="left">
          <el-button
            type="text"
            size="small"
            icon="el-icon-arrow-left"
            @click="selectNone"
            >返回</el-button
          >
          <h3>{{ editorTitle }}</h3>
        </div>
        <el-button
          v-if="!createNew"
          size="small"
          type="danger"
          icon="el-icon-delete"
          @click="deleteItem"
          >删除{{ typeStr }}</el-button
        >
      </div>
      <!-- 实体编辑 -->
      <el-form
        v-if="selectedItem.type === 'node'"
        ref="editorItem"
        label-position="top"
        :rules="activeRules"
        :model="selectedItem.item"
        label-width="80px"
      >
        <el-form-item required label="实体名称" prop="name">
          <el-input
            clearable
            :disabled="!editable"
            placeholder="实体名称"
            v-model="selectedItem.item.name"
          ></el-input>
        </el-form-item>
        <el-form-item required label="实体类别" prop="group">
          <!-- <el-select
            clearable
            :disabled="!editable"
            placeholder="实体类别"
            v-model="selectedItem.item.group"
          >
            <el-option
              v-for="group in options.itemGroups"
              :key="group"
              :value="group"
            ></el-option>
          </el-select> -->
          <el-autocomplete
            clearable
            :disabled="!editable"
            :fetch-suggestions="queryGroup"
            placeholder="实体类别"
            v-model="selectedItem.item.group"
          ></el-autocomplete>
        </el-form-item>
        <el-form-item required label="实体权重" prop="radius">
          <el-input
            clearable
            :disabled="!editable"
            placeholder="1~20"
            v-model.number="selectedItem.item.radius"
          ></el-input>
        </el-form-item>
        <el-form-item label="实体颜色">
          <div>
            <el-radio-group :disabled="!editable" v-model="useGroupColor">
              <el-radio :label="true" style="margin-bottom: 10px"
                >按默认分组颜色</el-radio
              >
              <el-radio :label="false">自定义颜色</el-radio>
            </el-radio-group>
            <el-input
              style="padding-top: 10px"
              v-if="!useGroupColor"
              type="color"
              :disabled="!editable"
              v-model="selectedItem.item.color"
            ></el-input>
          </div>
        </el-form-item>
        <!-- <el-form-item label="实体形状">
          <el-input
            clearable
            :disabled="!editable"
            placeholder="实体形状"
            v-model="selectedItem.item.figure"
          ></el-input>
        </el-form-item> -->
        <el-form-item required label="字体大小" prop="textSize">
          <el-input
            clearable
            :disabled="!editable"
            placeholder="单位 px"
            v-model.number="selectedItem.item.textSize"
          ></el-input>
        </el-form-item>
        <el-form-item label="其他属性">
          <div
            class="properties"
            v-for="(prop, idx) in selectedItem.item.properties"
            :key="idx"
          >
            <!-- <el-select
              clearable
              :disabled="!editable"
              placeholder="属性名"
              v-model="prop.name"
            >
              <el-option
                v-for="prop in restProperties"
                :key="prop"
                :value="prop"
              ></el-option>
            </el-select> -->
            <div class="name">
              <el-autocomplete
                clearable
                :disabled="!editable"
                :fetch-suggestions="queryAttributes"
                placeholder="属性名"
                v-model="prop.name"
              ></el-autocomplete>
              <el-button
                v-if="editable"
                type="danger"
                icon="el-icon-close"
                @click="removeProp(idx)"
                >移除</el-button
              >
            </div>
            <el-input
              clearable
              :disabled="!editable"
              type="textarea"
              :autosize="{ maxRows: '5' }"
              placeholder="属性值"
              v-model="prop.value"
            ></el-input>
          </div>
          <el-button v-if="editable" icon="el-icon-plus" @click="addProp"
            >添加属性</el-button
          >
        </el-form-item>
        <el-form-item class="options">
          <el-button type="primary" @click="primaryButton.handler">{{
            primaryButton.label
          }}</el-button>
          <el-button v-if="editable" @click="alterButton.handler">{{
            alterButton.label
          }}</el-button>
        </el-form-item>
      </el-form>
      <!-- 关系编辑 -->
      <el-form
        v-if="selectedItem.type === 'link'"
        ref="editorItem"
        label-position="top"
        :rules="activeRules"
        :model="selectedItem.item"
        label-width="80px"
      >
        <el-form-item required label="关系名称" prop="name">
          <!-- <el-select
            clearable
            :disabled="!editable"
            placeholder="关系名称"
            v-model="selectedItem.item.name"
          >
            <el-option
              v-for="name in options.linkNames"
              :key="name"
              :value="name"
            ></el-option>
          </el-select> -->
          <el-autocomplete
            clearable
            :disabled="!editable"
            :fetch-suggestions="queryLinkName"
            placeholder="关系名称"
            v-model="selectedItem.item.name"
          ></el-autocomplete>
        </el-form-item>
        <el-form-item required label="关系实体1" prop="from">
          <div>
            <el-select
              clearable
              :disabled="!editable"
              placeholder="实体名称"
              v-model="selectedItem.item.from"
              style="width: 170px"
            >
              <el-option
                v-for="node in options.nodeOptions"
                :key="node.id"
                :label="node.name"
                :value="node.id"
              ></el-option>
            </el-select>
            <el-button
              title="点击选择目标节点"
              :disabled="!editable"
              :icon="
                selectingNode === 'from' ? 'el-icon-close' : 'el-icon-thumb'
              "
              @click="toggleSelectingNode('from')"
              @blur="toggleSelectingNode('from')"
            ></el-button>
          </div>
        </el-form-item>
        <el-form-item required label="关系实体2" prop="to">
          <div>
            <el-select
              clearable
              :disabled="!editable"
              placeholder="实体名称"
              v-model="selectedItem.item.to"
              style="width: 170px"
            >
              <el-option
                v-for="node in options.nodeOptions"
                :key="node.id"
                :label="node.name"
                :value="node.id"
              ></el-option>
            </el-select>
            <el-button
              title="点击选择目标节点"
              :disabled="!editable"
              :icon="selectingNode === 'to' ? 'el-icon-close' : 'el-icon-thumb'"
              @click="toggleSelectingNode('to')"
              @blur="toggleSelectingNode('to')"
            ></el-button>
          </div>
        </el-form-item>
        <el-form-item required label="关系权重" prop="value">
          <el-input
            clearable
            :disabled="!editable"
            placeholder="1~20"
            v-model="selectedItem.item.value"
          ></el-input>
        </el-form-item>
        <el-form-item label="其他属性">
          <div
            class="properties"
            v-for="(prop, idx) in selectedItem.item.properties"
            :key="idx"
          >
            <!-- <el-select
              clearable
              :disabled="!editable"
              placeholder="属性名"
              v-model="prop.name"
            >
              <el-option
                v-for="prop in restProperties"
                :key="prop"
                :value="prop"
              ></el-option>
            </el-select> -->
            <div class="name">
              <el-autocomplete
                clearable
                :disabled="!editable"
                :fetch-suggestions="queryAttributes"
                placeholder="属性名"
                v-model="prop.name"
              ></el-autocomplete>
              <el-button
                v-if="editable"
                type="danger"
                icon="el-icon-close"
                @click="removeProp(idx)"
                >移除</el-button
              >
            </div>
            <el-input
              clearable
              :disabled="!editable"
              type="textarea"
              :autosize="{ maxRows: '5' }"
              placeholder="属性值"
              v-model="prop.value"
            ></el-input>
          </div>
          <el-button v-if="editable" icon="el-icon-plus" @click="addProp"
            >添加属性</el-button
          >
        </el-form-item>
        <el-form-item class="options">
          <el-button type="primary" @click="primaryButton.handler">{{
            primaryButton.label
          }}</el-button>
          <el-button v-if="editable" @click="alterButton.handler">{{
            alterButton.label
          }}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import { deepCopy } from '../../../common/utils/object'
export default {
  name: 'GraphEditorItem',
  props: {
    projectId: {
      type: Number
    },
    nodeOptions: {
      type: Array
    },
    nodeGroups: {
      type: Array
    }
  },
  data() {
    return {
      useGroupColor: false,
      editable: false,
      projectInfo: {
        id: null
      },
      selectedItem: {
        type: null,
        item: {}
      },
      originItem: null,
      options: {
        nodeOptions: [],
        nodeGroups: [],
        itemGroups: ['菜谱', '菜系', '食材'],
        linkNames: ['属于', '主食材', '辅料'],
        nodeProperties: {
          菜谱: ['口味', '工艺', '耗时', '做法'],
          食材: ['简介', '功效', '营养价值']
        },
        linkProperties: {
          主食材: ['用量'],
          辅料: ['用量']
        }
      },
      selectingNode: '',
      selectingMode: false,
      rules: {
        node: {
          name: [{ required: true, message: '实体名称为必填项' }],
          group: [{ required: true, message: '实体类别为必填项' }],
          radius: [
            { required: true, message: '实体权重为必填项' },
            { type: 'number', min: 1, message: '实体权重必须为正整数' }
          ],
          textSize: [
            { required: true, message: '字体大小为必填项' },
            { type: 'number', min: 1, message: '实体权重必须为正整数' }
          ]
        },
        link: {
          name: [{ required: true, message: '关系名称为必填项' }],
          from: [{ required: true, message: '关系实体1为必填项' }],
          to: [{ required: true, message: '关系实体2为必填项' }],
          value: [{ required: true, message: '关系权重为必填项' }]
        }
      }
    }
  },
  computed: {
    typeStr() {
      return this.selectedItem.type === 'node' ? '实体' : '关系'
    },
    editorTitle() {
      return this.createNew
        ? `新增${this.typeStr}`
        : `${this.typeStr} ID: ${this.selectedItem.item.id}`
    },
    createNew() {
      return !this.selectedItem.item.id
    },
    primaryButton() {
      return !this.createNew
        ? this.editable
          ? { label: `确认修改`, handler: this.updateItem }
          : {
              label: '点击修改',
              handler: () => {
                this.editable = true
              }
            }
        : { label: `新增${this.typeStr}`, handler: this.createItem }
    },
    alterButton() {
      return this.createNew
        ? { label: '重置选项', handler: this.reset }
        : { label: '取消修改', handler: this.cancel }
    },
    // restProperties() {
    //   const {
    //     selectedItem: {
    //       type,
    //       item: { name, group, properties }
    //     },
    //     options: { nodeProperties, linkProperties }
    //   } = this
    //   const prop = type === 'node' ? group : name
    //   const props = type === 'node' ? nodeProperties : linkProperties
    //   const options = props[prop] || []
    //   const usedProperties = properties.map(prop => prop.name)
    //   const rest = options.filter(option => !usedProperties.includes(option))
    //   // console.log('restProperties', rest)
    //   return rest
    // },
    activeRules() {
      return this.rules[this.selectedItem.type]
    }
  },
  watch: {
    projectId(id) {
      this.projectInfo.id = id
    },
    useGroupColor(bool) {
      if (bool) {
        this.selectedItem.item.color = ''
      }
    },
    nodeOptions(options) {
      this.options.nodeOptions = options
    },
    nodeGroups(groups) {
      this.options.nodeGroups = groups
    }
  },
  methods: {
    ...mapActions({
      createItemAct: 'createItem',
      updateItemAct: 'updateItem',
      deleteItemAct: 'deleteItem'
    }),
    /********** 外部操作事件 **********/
    createNode() {
      const newNode = this.getEditNode()
      // console.log('createNode')

      this.selectedItem.type = 'node'
      this.selectedItem.item = newNode
      this.originItem = null
      this.editable = true
      this.useGroupColor = true
      this.$emit('graph-action', 'clearFocus')
    },
    createLink() {
      const newLink = this.getEditLink()
      // console.log('createLink')

      this.selectedItem.type = 'link'
      this.selectedItem.item = newLink
      this.originItem = null
      this.editable = true
      this.useGroupColor = true
      this.$emit('graph-action', 'clearFocus')
    },
    selectNode(node) {
      const newNode = this.getEditNode(node)
      // console.log('selectNode', newNode)

      this.selectedItem.type = 'node'
      this.selectedItem.item = newNode
      this.originItem = deepCopy(newNode)
      this.editable = false
      this.useGroupColor = !node.color
    },
    selectLink(link) {
      const newLink = this.getEditLink(link)
      // console.log('selectLink', link)

      this.selectedItem.type = 'link'
      this.selectedItem.item = newLink
      this.originItem = deepCopy(newLink)
      this.editable = false
      this.useGroupColor = true
    },
    selectNone() {
      // console.log('selectNone')
      this.selectedItem.type = ''
      this.selectedItem.item = {}
      this.$emit('graph-action', 'clearFocus')
    },
    /********** 内部方法 **********/
    getEditNode(origin) {
      if (origin) {
        const {
          id,
          name,
          group,
          radius,
          color,
          figure,
          textSize,
          properties
        } = origin
        const props = []
        for (const name in properties) {
          props.push({ name, value: properties[name] })
        }
        const node = {
          id,
          name,
          group,
          radius,
          color,
          figure,
          textSize,
          properties: props
        }
        return node
      } else {
        return {
          name: '',
          group: '',
          radius: '',
          color: '',
          figure: '',
          textSize: '',
          properties: []
        }
      }
    },
    getEditLink(origin) {
      if (origin) {
        const { id, name, from, to, value, properties } = origin
        const props = []
        for (const name in properties) {
          props.push({ name, value: properties[name] })
        }
        const newLink = { id, name, from, to, value, properties: props }
        return newLink
      } else {
        return {
          name: '',
          from: '',
          to: '',
          value: '',
          properties: []
        }
      }
    },
    // 添加实体属性
    addProp() {
      this.selectedItem.item.properties.push({ name: '', value: '' })
    },
    // 删除实体属性
    removeProp(idx) {
      this.selectedItem.item.properties.splice(idx, 1)
    },
    toggleSelectingNode(attr) {
      const mode = this.selectingNode !== attr
      const node = mode ? attr : ''
      this.selectingMode = mode
      this.selectingNode = node
      this.$emit('graph-action', 'setSelectingMode', mode)
    },
    catchNode(node) {
      const { id, name } = node
      // console.log(`catchNode: id=${id}, name=${name}`)
      this.selectedItem.item[this.selectingNode] = node.id
      this.selectingMode = false
      this.selectingNode = ''
    },
    // 重置选项
    reset() {
      const item = this.selectedItem.item
      for (const prop in item) {
        item[prop] = ''
      }
    },
    // 重置属性
    cancel() {
      this.editable = false
      // reset attribute
      this.selectedItem.item = deepCopy(this.originItem)
    },
    validate() {
      return new Promise(resolve => {
        this.$refs.editorItem.validate(res => {
          resolve(res)
        })
      })
    },
    async createItem() {
      const {
        projectInfo: { id: projectId },
        selectedItem: { type, item },
        validate
      } = this
      const res = await validate()
      console.log('validate', res)
      if (!res) return
      this.createItemAct({ projectId, type, item }).then(item => {
        if (item) {
          if (type === 'node') {
            this.$emit('graph-action', 'createNode', item)
            this.selectNode(item)
          } else if (type === 'link') {
            this.$emit('graph-action', 'createLink', item)
            this.selectLink(item)
          }
        }
      })
    },
    async updateItem() {
      const {
        projectInfo: { id: projectId },
        selectedItem: { type, item },
        validate
      } = this
      const res = await validate()
      console.log('validate', res)
      if (!res) return
      this.updateItemAct({ projectId, type, item }).then(item => {
        if (item) {
          if (type === 'node') {
            this.$emit('graph-action', 'updateNode', item)
            this.selectNode(item)
          } else if (type === 'link') {
            this.$emit('graph-action', 'updateLink', item)
            this.selectLink(item)
          }
        }
      })
    },
    deleteItem() {
      const {
        projectInfo: { id: projectId },
        selectedItem: { type, item }
      } = this
      this.deleteItemAct({ projectId, type, id: item.id }).then(res => {
        if (res) {
          const action =
            type === 'node' ? 'deleteNode' : type === 'link' ? 'deleteLink' : ''
          if (action) {
            this.$emit('graph-action', action, item.id)
            this.selectNone()
          }
        }
      })
    },
    queryGroup(_, cb) {
      return cb(this.options.itemGroups.map(group => ({ value: group })))
    },
    queryLinkName(_, cb) {
      return cb(this.options.linkNames.map(name => ({ value: name })))
    },
    queryAttributes(_, cb) {
      const {
        selectedItem: {
          type,
          item: { name, group }
        },
        options: { nodeProperties, linkProperties }
      } = this
      const prop = type === 'node' ? group : name
      const props = type === 'node' ? nodeProperties : linkProperties
      const suggestions = props[prop] || []
      return cb(suggestions.map(option => ({ value: option })))
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header > .left {
  display: flex;
  gap: 16px;
}

.properties {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

.properties > .name {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.properties + .properties {
  margin-top: 10px;
}
</style>
