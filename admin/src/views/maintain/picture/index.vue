<template>
  <div class="divBox">
    <el-card class="box-card">
      <upload-index :pictureType="pictureType"></upload-index>
    </el-card>
  </div>
</template>

<script>
  import UploadIndex from '@/components/uploadPicture/index.vue'

  export default {
    name: "index",
    data(){
      return {
        pictureType: 'maintain'
      }
    },
    components: {UploadIndex},
  }
</script>

<style scoped>

</style>
