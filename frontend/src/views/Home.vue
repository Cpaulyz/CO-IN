<template>
  <div class="box">
    <div class="title">
      <el-menu
        :default-active="activeIndex"
        mode="horizontal"
        @select="handleSelect"
      >
        <el-menu-item index="1">广场</el-menu-item>
        <el-menu-item index="2">我的项目</el-menu-item>
        <el-menu-item index="3">用户手册</el-menu-item>
        <el-menu-item index="4">系统设计</el-menu-item>
        <!-- <el-menu-item index="5">智能助手</el-menu-item> -->
      </el-menu>
      <!-- header -->
      <div v-if="activeIndex === '1'" class="square-banner">
        <Logo @click="backToSearch" />
        <Input
          ref="search_input"
          placeholder="想找什么菜?"
          v-model="searchInput"
          :submitSearch="setSearchFilter"
          @blur="checkSearchOnBlur"
        />
      </div>
      <h1 v-else-if="activeIndex === '2'">co$in 我的项目</h1>
      <h1 v-else-if="activeIndex === '3'">co$in 用户手册</h1>
      <h1 v-else-if="activeIndex === '4'">co$in 系统设计</h1>
      <el-button
        icon="el-icon-plus"
        class="add"
        @click="createNewGraph()"
        v-if="activeIndex === '2'"
      >
        新建项目
      </el-button>
      <!-- avatar -->
      <el-popover placement="bottom" trigger="click" class="user">
        <div style="font-size: 20px">{{ userInfo.username }}</div>
        <div class="userinfo-email">{{ userInfo.email }}</div>
        <el-button style="width: 100%; border: none" @click="logout()">
          登出
        </el-button>
        <el-button
          icon="el-icon-user-solid"
          circle
          slot="reference"
        ></el-button>
      </el-popover>
    </div>
    <!-- main page -->
    <router-view></router-view>
    <NewProjectPanel />
  </div>
</template>

<script>
import { mapGetters, mapMutations, mapActions } from 'vuex';

import Input from '../components/Input.vue';
import Logo from '../components/Logo.vue';
import NewProjectPanel from '../modules/home/components/NewProjectPanel';
import { ROUTE_PATH } from '../router/config';

export default {
  name: 'Home',
  components: {
    NewProjectPanel,
    Logo,
    Input,
  },
  data() {
    return {
      ROUTE_PATH,
      // tabs
      activeIndex: '1',
      // 广场搜索
      searchInput: '',
      userId: null,
      childRouteList: [
        ,
        ROUTE_PATH.HomeSquare,
        ROUTE_PATH.Home,
        ROUTE_PATH.Tutorial,
        ROUTE_PATH.SystemDesign,
        ROUTE_PATH.Chat,
      ],
    };
  },
  computed: {
    ...mapGetters(['userInfo', 'allPageNo', 'ownPageNo', 'searchProjects']),
  },
  methods: {
    ...mapMutations(['setGraphProjectId', 'setShowCreatePanel']),
    ...mapActions([
      // 'getListByUserId',
      'userLogout',
      'getUserInfo',
      'getAllListByPageNo',
      'getAllListBySearchInput',
      'getOwnListByPageNo',
      'getAllListAmount',
      'getOwnListAmount',
    ]),
    createNewGraph() {
      this.setShowCreatePanel(true);
    },
    logout() {
      localStorage.removeItem('coin-token');
      this.$router.push('/user');
    },
    handleSelect(key) {
      this.activeIndex = key;
      const path = this.childRouteList[key];
      if (path) {
        this.$router.push(path);
      }
    },
    backToSearch() {
      let path = ROUTE_PATH.Search;
      if (this.activeIndex === '1' && this.searhInput) {
        path += `?q=${this.searhInput}`;
      }
      this.$router.push(path);
    },
    setSearchFilter(input) {
      console.log(`[setSearchFilter] input = ${input}`);
      this.getAllListBySearchInput(input);
    },
    checkSearchOnBlur() {
      if (!this.searchInput && typeof this.searchProjects === 'string') {
        this.setSearchFilter(this.searchInput);
      }
    },
  },
  watch: {
    activeIndex(index) {
      if (index === '1') {
        this.$nextTick(() => {
          console.log(`[watch.activeIndex] searchInput = ${this.searchInput}`);
          this.$refs.search_input.value = this.searchInput;
        });
      }
    },
    // searchInput(input) {
    //   if (!input) {
    //     this.setSearchFilter(input);
    //   }
    // },
  },
  mounted() {
    // console.log(`path: ${this.$route.path}`)
    const activeIndex = (this.activeIndex = this.childRouteList
      .indexOf(this.$route.path)
      .toString());
    this.getUserInfo().then(success => {
      if (success) {
        this.userId = this.userInfo.id;
        this.getOwnListAmount(this.userId);
        this.getAllListAmount();
        this.getOwnListByPageNo({
          userId: this.userId,
          pageNo: this.ownPageNo,
        });
        this.getAllListByPageNo(this.allPageNo);
      }
    });
    // 挂载页面时载入 querySearch
    if (activeIndex === '1') {
      const query = this.$route.query;
      const initSearch = query.q;
      if (initSearch) {
        console.log(`[Home] mounted with search = ${initSearch}`);
        this.searchInput = this.$refs.search_input.value = initSearch;
        this.setSearchFilter(initSearch);
      }
    }
  },
};
</script>

<style scoped>
.box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  text-align: center;
  height: 100vh;
  padding: 0 32px 32px 32px;
  box-sizing: border-box;
  overflow: auto;
}

.box > .title {
  position: sticky;
  width: 100%;
  height: 23%;
  top: 0;
  background-color: #ffffff;
}

.box > .title > .add {
  position: relative;
  top: -60px;
  right: -200px;
}

.box > .title > .user {
  position: fixed;
  top: 15px;
  right: 50px;
}

.userinfo-email {
  font-size: 15px;
  border-bottom: 1px solid black;
  padding-bottom: 5px;
  margin-bottom: 5px;
}

.square-banner {
  display: flex;
  align-items: center;
  margin: 20px 0;
}
.square-banner img {
  cursor: pointer;
  margin-right: 12px;
}
</style>
