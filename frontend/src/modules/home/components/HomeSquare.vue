<template>
  <div class="list">
    <div
      v-if="searchProjects && typeof searchProjects === 'string'"
      class="no-search-result"
    >
      搜索 "{{ searchProjects }}" 无结果，请重新输入
    </div>
    <div class="cards">
      <HomeProjectCard
        v-for="project in filteredAllProjects"
        :key="project.projectId"
        :project="project"
        @click="gotoSmarthelper(project.projectId)"
      />
    </div>
    <!-- 换页选择器 -->
    <el-pagination
      layout="prev, pager, next"
      :total="allListCount"
      @current-change="switchPageAll"
      :current-page="allPageNo"
    >
    </el-pagination>
    <!-- invisible snapshot builder -->
    <SnapshotLoader ref="square_snapshot_loader" />
  </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex';
import { ROUTE_PATH } from '../../../router/config';
import HomeProjectCard from './HomeProjectCard.vue';
import SnapshotLoader from './SnapshotLoader.vue';
import { SnapshotCache } from '../utils/snapshot';

let loadingLock = false;
let hasDestroyed = false;

export default {
  components: { HomeProjectCard, SnapshotLoader },
  name: 'HomeSquare',
  computed: {
    ...mapGetters([
      'allProjects',
      'searchProjects',
      'allPageNo',
      'allListCount',
    ]),
    filteredAllProjects() {
      let projects = this.searchProjects;
      // 1. 优先返回搜索结果
      //      input 为 '' 时  => null
      //      结果为空时       => input
      if (projects && Array.isArray(projects)) {
        return projects;
      }

      // 2. 否则返回全部
      return this.allProjects;
    },
  },
  methods: {
    ...mapMutations(['setAllPageNo', 'setAllProjectsSnapshot']),
    ...mapActions(['getAllListByPageNo']),
    gotoSmarthelper(id) {
      console.log('go smart helper');
      this.$router.push(ROUTE_PATH.Smarthelper(id));
    },
    switchPageAll(currPageNo) {
      // console.log('switchPage', currPageNo)
      this.setAllPageNo(currPageNo);
      this.getAllListByPageNo(currPageNo);
    },
    async loadSnapshots(projects) {
      // no need to loadSnapshots
      return;

      // TODO clear old codes & redundant dependencies
      if (loadingLock) {
        console.warn('[HomeSquare] loadSnapshots duplicate');
        return;
      }
      loadingLock = true;

      console.log('watch projects', projects);
      const loader = this.$refs.square_snapshot_loader;

      for (const project of projects) {
        if (hasDestroyed) {
          // 组件卸载
          console.warn('[HomeSquare] stop loadSnapshots for early unmounted');
          loadingLock = false;
          return;
        }
        if (project.snapshot) {
          continue;
        }
        const projectId = project.projectId;
        let snapshot;
        // 加载缓存快照
        snapshot = SnapshotCache.get(projectId);
        if (snapshot) {
          this.setAllProjectsSnapshot({
            ...project,
            snapshot,
          });
          continue;
        }

        // 生成快照
        try {
          snapshot = await loader.load(projectId);
          if (snapshot) {
            SnapshotCache.set(projectId, snapshot);
            this.setAllProjectsSnapshot({
              ...project,
              snapshot,
            });
          } else {
            console.log('[watch.allProjects] empty snapshot');
          }
        } catch (e) {
          console.log('[watch.allProjects] load snapshot fail', e);
        }
      }
      loadingLock = false;
    },
  },
  watch: {
    allProjects(projects) {
      this.loadSnapshots(projects);
    },
  },
  mounted() {
    hasDestroyed = false;
    console.log('[HomeSquare] mounted', this.allProjects);
    this.loadSnapshots(this.allProjects);
  },
  destroyed() {
    hasDestroyed = true;
  },
};
</script>

<style scoped>
.list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}
.no-search-result {
  margin-bottom: 24px;
}
.cards {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 40px;
}
</style>
