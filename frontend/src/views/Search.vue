<template>
  <div class="search-container">
    <div class="search-top">
      <div>
        <Logo :width="350" />
      </div>
      <Input ref="search_input" :submitSearch="submitSearch" placeholder="想找什么菜?" />
      <div>
        <el-button @click="go(ROUTE_PATH.HomeSquare)">随便逛逛</el-button>
        <el-button type="primary" @click="go(ROUTE_PATH.HomeProjects)">
          我的项目</el-button
        >
      </div>
    </div>
  </div>
</template>

<script>
import Input from '../components/Input.vue';
import Logo from '../components/Logo.vue';
import { ROUTE_PATH } from '../router/config';

export default {
  components: { Logo, Input },
  name: 'Search',
  data() {
    return { ROUTE_PATH };
  },
  methods: {
    submitSearch(input) {
      console.log(`submit search = '${input}'`);
      this.$router.push(`${ROUTE_PATH.HomeSquare}?q=${input}`);
    },
    go(path) {
      this.$router.push(path);
    },
  },
  mounted() {
    const query = this.$route.query;
    const initSearch = query.q;
    if (initSearch) {
      this.$refs.search_input.value = initSearch;
    }
  },
};
</script>

<style scoped>
.search-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  padding-bottom: 10%;
}
.search-top {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.search-input {
  width: 500px;
}
</style>
