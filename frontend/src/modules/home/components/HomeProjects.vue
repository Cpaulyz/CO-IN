<template>
  <div class="list">
    <div class="cards">
      <HomeProjectCard
        v-for="project in ownProjects"
        :key="project.projectId"
        :project="project"
        @click="gotoProject(project.projectId)"
      />
    </div>
    <!-- 换页选择器 -->
    <el-pagination
      layout="prev, pager, next"
      :total="ownListCount"
      @current-change="switchPageOwn"
      :current-page="ownPageNo"
    >
    </el-pagination>
    <!-- invisible snapshot builder -->
    <SnapshotLoader ref="projects_snapshot_loader" />
  </div>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex';
import { SnapshotCache } from '../utils/snapshot';
import HomeProjectCard from './HomeProjectCard.vue';
import SnapshotLoader from './SnapshotLoader.vue';

let loadingLock = false;
let hasDestroyed = false;

export default {
  components: { HomeProjectCard, SnapshotLoader },
  name: 'HomeProjects',
  computed: {
    ...mapGetters(['ownProjects', 'ownListCount', 'ownPageNo']),
  },
  methods: {
    ...mapMutations(['setOwnPageNo', 'setOwnProjectsSnapshot']),
    ...mapActions(['getOwnListByPageNo']),
    gotoProject(id) {
      this.$router.push(`/graph/${id}`);
    },
    switchPageOwn(currPageNo) {
      this.setOwnPageNo(currPageNo);
      this.getOwnListByPageNo({
        userId: this.userId,
        pageNo: this.ownPageNo,
      });
    },
    async loadSnapshots(projects) {
      // no need to loadSnapshots
      return;

      // TODO clear old codes & redundant dependencies
      if (loadingLock) {
        console.warn('[HomeProjects] loadSnapshots duplicate');
        return;
      }
      loadingLock = true;

      console.log('watch projects', projects);
      const loader = this.$refs.projects_snapshot_loader;

      for (const project of projects) {
        if (hasDestroyed) {
          // 组件卸载
          console.warn('[HomeProjects] stop loadSnapshots for early unmounted');
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
          this.setOwnProjectsSnapshot({
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
            this.setOwnProjectsSnapshot({
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
    ownProjects(projects) {
      this.loadSnapshots(projects);
    },
  },
  mounted() {
    hasDestroyed = false;
    console.log('[HomeProjects] mounted', this.ownProjects);
    this.loadSnapshots(this.ownProjects);
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
.cards {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 40px;
}
</style>
