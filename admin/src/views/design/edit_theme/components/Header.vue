<template>
  <div class="page-header">
    <div class="header-left">
      <span v-if="isMicroPage" class="iconfont iconfanhui" @click="backToMicroPage"></span>
      <span class="label">当前{{ isMicroPage ? '专题' : '主题' }}：</span>
      <span class="theme-name">{{ themeName }}</span>
      <span class="iconfont iconic_edit1 edit-icon" @click="handleEdit"></span>
    </div>

    <div class="header-right">
      <el-button size="small" icon="el-icon-view" @click="$emit('preview')">预览</el-button>
      <el-button v-if="!isMicroPage" size="small" @click="$emit('save-template')">另存主题</el-button>
      <el-button size="small" @click="$emit('save')">保存</el-button>
      <el-button type="primary" size="small" @click="$emit('save-close')">保存并关闭</el-button>
    </div>

    <!-- 修改主题信息弹窗 -->
    <el-dialog :title="`修改${isMicroPage ? '专题' : '主题'}信息`" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" ref="form" label-width="80px">
        <el-form-item :label="`${isMicroPage ? '专题' : '主题'}名称：`">
          <el-input
            v-model="form.title"
            :maxlength="20"
            show-word-limit
            :placeholder="`请输入${isMicroPage ? '专题' : '主题'}名称`"
          ></el-input>
        </el-form-item>
        <el-form-item :label="`${isMicroPage ? '专题' : '主题'}简介：`">
          <el-input
            type="textarea"
            v-model="form.info"
            :maxlength="200"
            show-word-limit
            :placeholder="`请输入${isMicroPage ? '专题' : '主题'}简介`"
            :rows="4"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleConfirm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'PageHeader',
  props: {
    themeName: {
      type: String,
      default: '',
    },
    themeInfo: {
      type: String,
      default: '',
    },
    isMicroPage: {
      type: Boolean,
      default: false,
    },
  },

  data() {
    return {
      dialogVisible: false,
      form: {
        title: '',
        info: '',
      },
    };
  },
  methods: {
    handleEdit() {
      this.form.title = this.themeName;
      this.form.info = this.themeInfo;
      this.dialogVisible = true;
    },
    handleConfirm() {
      let data = {
        title: this.form.title,
        info: this.form.info,
      };
      if (this.$route.query.page_type === 'micro') data.page_type = 'micro';
      if (this.$route.query.tid) data.tid = this.$route.query.tid;
      this.$emit('update-info', data);
      this.dialogVisible = false;
    },
    backToMicroPage() {
      this.$router.push({
        path: '/design/micro_theme',
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.page-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;
    font-size: 16px;
    color: #333;

    .label {
      font-weight: 500;
    }
    .iconfanhui {
      margin-right: 10px;
    }
    .theme-name {
      font-weight: 500;
      margin-right: 10px;
    }

    .edit-icon {
      cursor: pointer;
      color: #999;

      &:hover {
        color: #1890ff;
      }
    }
  }
}
</style>
