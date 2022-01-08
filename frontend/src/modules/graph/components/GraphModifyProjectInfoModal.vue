<template>
  <el-dialog
    title="设定 - 项目信息"
    :visible="showModifyProjectInfoModal"
    width="40%"
    @close="disablePanel()"
  >
    <el-form
      label-width="500px"
      label-position="top"
      :model="ruleForm"
      :rules="rules"
      ref="ruleForm"
    >
      <el-form-item label="修改项目名称" prop="name">
        <el-input
          v-model="ruleForm.name"
          :placeholder="projectInfo.name"
        ></el-input>
      </el-form-item>
      <el-form-item label="修改项目描述" prop="desc">
        <el-input
          v-model="ruleForm.desc"
          :placeholder="projectInfo.description"
        ></el-input>
      </el-form-item>
      <el-form-item label="修改项目权限" prop="status">
        <el-radio-group size="mini" v-model="status">
          <el-radio label="公开"></el-radio>
          <el-radio label="私有"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          style="width: 100%"
          round
          @click="commitUpdateProjectInfo('ruleForm')"
          >提交</el-button
        >
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
import { mapActions, mapGetters, mapMutations } from 'vuex';

export default {
  name: 'GraphModifyProjectInfoModal',
  data() {
    const checkName = (rule, value, callback) => {
      if (value === '') {
        return callback(new Error('项目名称不能为空'));
      } else {
        callback();
      }
    };
    const checkDesc = (rule, value, callback) => {
      if (value === '') {
        return callback(new Error('项目描述不能为空'));
      } else {
        callback();
      }
    };
    return {
      status: '',
      _projectInfo: {},
      ruleForm: {
        name: '',
        desc: '',
      },
      rules: {
        name: [
          {
            validator: checkName,
            trigger: 'blur',
          },
        ],
        desc: [
          {
            validator: checkDesc,
            trigger: 'blur',
          },
        ],
      },
    };
  },
  async mounted() {
    const projectId = Number(this.$route.params.projectId);
    this._projectInfo = await this.getProjectInfo(projectId);
    const { status } = this._projectInfo;
    this.status = status === 'PUBLIC' ? '公开' : '私有';
  },
  computed: {
    ...mapGetters(['showModifyProjectInfoModal', 'projectInfo']),
  },
  methods: {
    ...mapMutations(['setShowModifyProjectInfoModal']),
    ...mapActions([
      'updateProjectDesc',
      'updateProjectName',
      'updateProjectStatus',
      'getProjectInfo',
      'updateProjectInfo',
    ]),
    disablePanel() {
      console.log('qwertyuiop');
      this.setShowModifyProjectInfoModal(false);
    },
    commitUpdateProjectInfo(formName) {
      const projectId = Number(this.$route.params.projectId);
      const nameData = {
        name: this.ruleForm.name,
        projectId,
      };
      const descData = {
        description: this.ruleForm.desc,
        projectId,
      };
      const statusData = {
        status: this.status === '私有' ? 'PRIVATE' : 'PUBLIC',
        projectId,
      };
      const data = {
        nameData,
        descData,
        statusData,
      };
      this.$refs[formName].validate((valid) => {
        if (valid) {
          console.log(
            'commitUpdateProjectInfo',
            nameData,
            descData,
            statusData,
          );
          this.updateProjectInfo(data)
            .then((res) => {
              const { success, message } = res;
              if (success) {
                this.disablePanel();
                this.$message({
                  message: '更新成功',
                  type: 'success',
                });
                const projectId = Number(this.$route.params.projectId);
                this.$router.go(0)
              } else {
                this.$message({
                  message: '更新失败',
                  type: 'error',
                });
              }
            })
            .catch((err) => {
              console.log('updateProjectInfo', err);
            });
        } else {
          console.log('invalid');
          return false;
        }
      });
    },
  },
};
</script>

<style></style>
