<template>
  <div class="upload-container">
    <el-upload class="upload-demo mr10 mb15" action :http-request="handleUploadForm" :headers="myHeaders"
      :show-file-list="false" multiple>
      <div v-if="url" class="upLoadPicBox">
        <div class="upLoad column">
          <i class="el-icon-document-checked cameraIconfont" />
          <span class="file-type">{{ fileType }}</span>
        </div>
      </div>
      <el-button v-else type="primary" v-hasPermi="['admin:upload:file']">点击上传</el-button>
    </el-upload>
  </div>
</template>
<script>
import { fileFileApi } from '@/api/systemSetting';
import { getToken } from '@/utils/auth';
export default {
  name: 'UploadFile',
  props: {
    value: {},
  },
  data() {
    return {
      myHeaders: { 'X-Token': getToken() },
      url: '',
      fileType: '',
    };
  },
  beforeMount() {
    if (this.value) {
      this.url = this.value;
      this.getFileType(this.value);
    }
  },
  methods: {
    // 上传
    handleUploadForm(param) {
      const formData = new FormData();
      const data = {
        model: this.$route.path.split('/')[1],
        pid: 10,
      };
      formData.append('multipart', param.file);
      const loading = this.$loading({
        lock: true,
        text: '上传中，请稍候...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
      });
      fileFileApi(formData, data)
        .then((res) => {
          loading.close();
          this.url = res.url;
          this.getFileType(res.url);

          this.$emit('input', this.url);
          this.$message.success('上传成功');
        })
        .catch((res) => {
          loading.close();
        });
    },
    getFileType(file) {
      var first = file.lastIndexOf('.'); //取到文件名开始到最后一个点的长度
      var namelength = file.length; //取到文件名长度
      var filesuffix = file.substring(first + 1, namelength); //截取获得后缀名
      this.fileType = filesuffix.toUpperCase();
    },
  },
};
</script>

<style scoped>
.flex-column {
  display: flex;
  flex-direction: column;
}

.column {
  display: flex;
  flex-direction: column;
  line-height: 16px;
}

.file-type {
  font-size: 12px;
  padding-top: 2px;
}
</style>
