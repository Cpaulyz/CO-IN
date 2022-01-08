<template>
  <el-input
    class="search-input"
    ref="search_input"
    :placeholder="placeholder"
    :style="{ width }"
    @focus="onInputFocus"
    @blur="onInputBlur"
    v-model="value"
    @input="$emit('input', $event)"
    @change="$emit('change', $event)"
  >
    <el-button
      slot="append"
      icon="el-icon-search"
      @click="submitSearch(value)"
    ></el-button>
  </el-input>
</template>

<script>
export default {
  name: 'Input',
  props: ['width', 'submitSearch', 'placeholder'],
  data() {
    return {
      value: '',
    };
  },
  methods: {
    focusInput() {
      this.$refs.search_input.focus();
    },
    onInputFocus(e) {
      document.addEventListener('keydown', this.keydownSubmit);
      this.$emit('focus', e);
    },
    onInputBlur(e) {
      document.removeEventListener('keydown', this.keydownSubmit);
      // 向外抛出 blur 事件
      this.$emit('blur', e);
    },
    keydownSubmit(e) {
      if (e.key === 'Enter' && !e.isComposing) {
        this.submitSearch(this.value);
      }
    },
  },
  mounted() {
    this.focusInput();
  },
};
</script>

<style scoped>
.search-input {
  width: 500px;
  flex: 1;
}
</style>
