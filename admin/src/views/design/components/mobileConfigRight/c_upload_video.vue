<template>
  <!--视频上传-->
  <div class="upload_img acea-row row-between borderPadding" v-if="configData && configData.isShow === 1">
    <div class="acea-row row-column row-middle align-left">
      <div class="header">{{ configData.title }}</div>
      <div class="title">{{ configData.tips }}</div>
    </div>
    <div class="box">
      <el-upload
        class="upload-demo"
        action
        :http-request="handleUploadForm"
        :headers="myHeaders"
        :show-file-list="false"
        multiple
      >
        <el-button icon="el-icon-upload2" size="small" class="mr15"></el-button>
      </el-upload>
    </div>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { mapState } from 'vuex';
import { fileFileApi } from '@/api/systemSetting';
import { getToken } from '@/utils/auth';
export default {
  name: 'c_upload_video',
  computed: {
    ...mapState({
      tabVal: (state) => state.admin.mobildConfig.searchConfig.data.tabVal,
    }),
  },
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  data() {
    return {
      defaults: {},
      configData: {},
      myHeaders: { 'X-Token': getToken() },
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      immediate: true,
      deep: true,
    },
  },
  created() {
    this.defaults = this.configObj;
    this.configData = this.configObj[this.configNme];
  },
  methods: {
    // 上传
    handleUploadForm(param) {
      const formData = new FormData();
      const data = {
        model: 'product',
        pid: 0,
      };
      formData.append('multipart', param.file);
      this.uploadPic(formData, data);
    },
    uploadPic(formData, data) {
      let loading = this.$loading({
        lock: true,
        text: '上传中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });
      fileFileApi(formData, data)
        .then((res) => {
          loading.close();
          this.$message.success('上传成功');
          this.$emit('getConfig', { name: 'video', values: res.url });
          this.configData.uploadVideo.url = res.url;
        })
        .catch((res) => {
          loading.close();
        });
    },
  },
};
</script>

<style scoped lang="scss">
.upload-demo {
  position: absolute;
  ::v-deep .el-button {
    width: 64px;
    height: 64px;
  }
}
.header {
  font-size: 12px;
  color: #999;
}

.title {
  margin: 10px 0 5px;
  padding-bottom: 3px;
  /*border-bottom:1px solid rgba(0,0,0,0.05);*/
  font-size: 12px;
  color: #bbbbbb;
}
.box {
  width: 64px;
  height: 64px;
  border-radius: 4px 4px 4px 4px;
  margin-bottom: 20px;
  position: relative;
  cursor: pointer;
  .iconfont-diy,
  .iconfont {
    position: absolute;
    color: #999;
  }

  img {
    width: 100%;
    height: 100%;
  }
}
.upload-box {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 64px;
  height: 64px;
  border-radius: 4px 4px 4px 4px;
  opacity: 1;
  border: 1px solid #eeeeee;
  font-size: 12px;
  color: #cccccc;
  .iconfont {
    font-size: 16px;
  }
}
.align-left {
  align-items: flex-start;
  justify-content: center;
}
</style>
