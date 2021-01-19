<template>
  <div class="divBox">
    <category-list :biztype="constants.categoryType[4]" />
  </div>
</template>

<script>
import { asyncRoutes, constantRoutes } from '@/router'
import * as categoryApi from '@/api/categoryApi.js'
import categoryList from '@/components/Category/list'
export default {
  // name: "index"
  components: { categoryList },
  data() {
    return {
      asyncRoutes,
      constants: this.$constants
    }
  },
  mounted() {
  },
  methods: {
    handleTreeCheckChange(data, checked, indateminate) {
      console.log(data, checked, indateminate)
    },
    categoryAdd(pram) {
      const _pram = {
        extra: pram.extra,
        name: pram.name,
        pid: pram.pid,
        sort: pram.sort,
        status: pram.status,
        type: pram.type,
        url: pram.url
      }
      categoryApi.addCategroy(_pram).then(data => {
        this.$message.success('添加菜单成功')
      })
    }
  }
}
</script>

<style scoped>
  .routerView{
    display: flex;
    flex-direction: row;
    flex: 1;
  }
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
  }
</style>
