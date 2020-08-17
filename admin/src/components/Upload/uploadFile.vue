<template>
  <div class="upload-container">
    <el-upload
      class="upload-demo mr10 mb15"
      action
      :http-request="handleUploadForm"
      :headers="myHeaders"
      :show-file-list="false"
      multiple
    >
      <div class="upLoadPicBox" v-if="url">
        <div class="upLoad">
          <i class="el-icon-document-checked cameraIconfont" />
        </div>
      </div>
      <el-button size="mini" type="primary" v-else>点击上传</el-button>
    </el-upload>
  </div>
</template>
<script>
  import { fileFileApi } from '@/api/systemSetting'
  import { getToken } from '@/utils/auth'
  export default {
    name: "uploadFile",
    data() {
      return {
        myHeaders: {'X-Token': getToken()},
        url: ''
      }
    },
    props:{
      value: {}
    },
    beforeMount(){
      if(this.value){
        this.url = this.value
      }
    },
    methods: {
      // 上传
      handleUploadForm(param){
        const formData = new FormData()
        const data = {
          model: this.$route.path.split("/")[1],
          pid: 10
        }
        formData.append('multipart', param.file)
        let loading = this.$loading({
          lock: true,
          text: '上传中，请稍候...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        fileFileApi(formData, data).then(res => {
          loading.close()
          this.url = res.url
          this.$emit('input', this.url)
          this.$message.success('上传成功')
        }).catch(res => {
          loading.close()
        })
      }
    }
  }
</script>

<style scoped>

</style>
