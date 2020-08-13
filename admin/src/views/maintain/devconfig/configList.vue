<template>
  <div class="components-container">
    <config-list :prent-data="prentData" />
  </div>
</template>

<script>
import configList from '@/components/FormGenerator/index/Home.vue'
export default {
  components: { configList },
  // name: "configList",
  props: {
    prentData: {
      type: Object,
      default: {}
    }
  }
}
</script>

<style scoped>

</style>
