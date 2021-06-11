<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="acea-row row-center-wrapper">
        <el-button type="primary" @click="onClick">申请授权</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
    export default {
        name: "index",
      methods:{
        onClick() {
          window.open('http://crmeb.com/web/auth/apply')
        }
      }
    }
</script>

<style scoped>

</style>
