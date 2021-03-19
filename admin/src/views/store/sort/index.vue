<template>
  <div class="components-container">
    <category-list :biztype="constants.categoryType[0]" />
  </div>
</template>

<script>
  import categoryList from '@/components/Category/list'
  export default {
    components: { categoryList },
    data() {
      return {
        constants: this.$constants
      }
    }
  }
</script>

<style scoped>

</style>
