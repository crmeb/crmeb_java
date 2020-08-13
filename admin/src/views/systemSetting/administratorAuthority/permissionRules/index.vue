<template>
  <div class="divBox">
    <!--<el-card shadow style="max-width: 500px;" class="mr20">-->
      <!--<div slot="header" class="clearfix">-->
        <!--<span>前端路由</span>-->
      <!--</div>-->
      <!--<div v-for="item,index in asyncRoutes">-->
        <!--<template v-if="!item.hidden">-->
          <!--<span v-if="item.meta">{{ item.meta.title }}{{ item.path }}</span>-->
          <!--&lt;!&ndash;            <span>name:{{ item.name }}</span>&ndash;&gt;-->
          <!--&lt;!&ndash;            <span>path:{{ item.path }}</span>&ndash;&gt;-->
          <!--<template v-if="item.children">-->
            <!--<div v-for="item1,iindex in item.children" class="ml20">-->
              <!--<span v-if="item1.meta">{{ item1.meta.title }}</span>-->
              <!--&lt;!&ndash;                <span>name:{{ item1.name }}</span>&ndash;&gt;-->
              <!--&lt;!&ndash;                <span>path:{{ item1.path }}</span>&ndash;&gt;-->
              <!--<span>allPath:{{ item.path }}/{{ item1.path }}</span>-->
              <!--<template v-if="item1.children">-->
                <!--<div v-for="item2,index2 in item1.children" class="ml20">-->
                  <!--<span v-if="item2.meta">{{ item2.meta.title }}</span>-->
                  <!--&lt;!&ndash;                    <span>name:{{ item2.name }}</span>&ndash;&gt;-->
                  <!--&lt;!&ndash;                    <span>path:{{ item2.path }}</span>&ndash;&gt;-->
                  <!--<span>allPath:{{ item.path }}/{{ item1.path }}/{{ item2.path }}</span>-->
                <!--</div>-->
              <!--</template>-->
            <!--</div>-->
          <!--</template>-->
        <!--</template>-->
      <!--</div>-->
    <!--</el-card>-->
    <category-list :biztype="constants.categoryType[4]" />
  </div>
</template>

<script>
import { asyncRoutes, constantRoutes } from '@/router'
import * as categoryApi from '@/api/categoryApi.js'
import categoryList from '@/components/Category/list'
import * as constants from '@/utils/constants.js'
export default {
  // name: "index"
  components: { categoryList },
  data() {
    return {
      asyncRoutes,
      constants
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
