<template>
  <el-card class="project-card" shadow="hover">
    <div slot="header" class="project-card-header" @click="clickCard">
      <span>{{ project.name }}</span>
    </div>
    <div class="project-card-body" @click="clickCard">
      <span v-if="error">
        项目封面加载失败
        <el-link type="primary" @click="reloadImage">点击重试</el-link>
      </span>
      <img v-else-if="src" :src="src" @error="loadImageError" />
      <!-- <span v-else-if="project.snapshot === undefined">
        缩略图加载中
        <span class="loading"></span>
      </span> -->
      <span v-else>
        尚未添加项目封面
        <el-link type="primary" @click="trySetImage">点击添加</el-link>
      </span>
      <!-- <img v-if="project.snapshot != null" :src="project.snapshot" alt="" />
      <span v-else-if="project.snapshot === undefined">
        缩略图加载中
        <span class="loading"></span>
      </span>
      <span v-else-if="project.snapshot === false">
        预览加载失败
      </span> -->
    </div>
  </el-card>
</template>

<script>
import api from '@/api/dispatcher';
import { chooseFile } from '../utils/file';

export default {
  name: 'HomeProjectCard',
  props: ['project'],
  data() {
    return {
      src: '',
      error: false,
    };
  },
  methods: {
    clickCard(e) {
      this.$emit('click', e);
    },
    loadImageError(e) {
      const { projectId, image } = this.project;
      if (image) {
        this.error = e;
        console.log(
          `[HomeProjectCard] loadImageError(projectId = ${projectId}), imageUrl='${image}'`,
          e,
        );
      }
    },
    reloadImage(e) {
      e.stopPropagation();
      this.error = false;
      this.loadImageSrc();
    },
    loadImageSrc(url) {
      this.src = url || this.project.image;
    },
    trySetImage(e) {
      e.stopPropagation();
      chooseFile().then(async file => {
        const projectId = this.project.projectId;

        console.log(`[HomeProjectCard] projectId=${projectId} , file`, file);
        api
          .updateProjectImage(file, projectId)
          .then(res => {
            if (res.status === 200) {
              this.$message.success('封面更新成功');
              console.log(`[HomeProjectCard] updateProjectImage res`, res);

              // TODO add real image url
              const url = res.data.msg;
              this.loadImageSrc(url);
            } else {
              this.$message.error('封面更新失败');
              console.log(`[HomeProjectCard] updateProjectImage fail`, res);
            }
          })
          .catch(e => {
            this.$message.error('封面更新失败');
            console.log(`[HomeProjectCard] updateProjectImage error`, e);
          });
      });
    },
  },
  mounted() {
    this.loadImageSrc();
  },
};
</script>

<style scoped>
.project-card {
  width: 400px;
  cursor: pointer;
}

.project-card-header {
  padding: 18px 20px;
}

.project-card >>> .el-card__header,
.project-card >>> .el-card__body {
  padding: 0;
}

.project-card >>> .el-card__body {
  height: 400px;
}

.project-card-body {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  overflow: hidden;
}
.loading::after {
  content: '';
  display: inline-block;
  width: 25px;
  text-align: left;
  animation: loading infinite 2s;
}

@keyframes loading {
  0% {
    content: '.';
  }
  20% {
    content: '..';
  }
  40% {
    content: '...';
  }
  60% {
    content: '....';
  }
  80% {
    content: '.....';
  }
}
</style>
