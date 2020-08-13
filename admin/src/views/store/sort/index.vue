<template>
  <div class="components-container">
    <category-list :biztype="constants.categoryType[0]" />
  </div>
</template>

<script>
  import categoryList from '@/components/Category/list'
  import * as constants from '@/utils/constants.js'
  export default {
    // name: "list",
    components: { categoryList },
    data() {
      return {
        constants
      }
    }
  }
</script>

<style scoped>

</style>
