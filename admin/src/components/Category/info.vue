<template>
  <div>
    <el-tree :data="ddd" :props="defaultProps" @node-click="handleNodeClick" />
  </div>
</template>

<script>
import * as categoryApi from '@/api/categoryApi.js'
export default {
  // name: "info"
  props: {
    id: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      ddd: [{
        label: '一级 1',
        children: [{
          label: '二级 1-1',
          children: [{
            label: '三级 1-1-1'
          }]
        }]
      }, {
        label: '一级 2',
        children: [{
          label: '二级 2-1',
          children: [{
            label: '三级 2-1-1'
          }]
        }, {
          label: '二级 2-2',
          children: [{
            label: '三级 2-2-1'
          }]
        }]
      }, {
        label: '一级 3',
        children: [{
          label: '二级 3-1',
          children: [{
            label: '三级 3-1-1'
          }]
        }, {
          label: '二级 3-2',
          children: [{
            label: '三级 3-2-1'
          }]
        }]
      }],
      dataList: {// 数据结果
        page: 0,
        limit: 0,
        totalPage: 0,
        total: 0,
        list: []
      }
    }
  },
  mounted() {
    this.handlerGetTreeList(this.id)
  },
  methods: {
    handlerGetTreeList(id) {
      if (!id) {
        this.$message.error('当前数据id不正确')
        return
      }
      categoryApi.treeCategroy({ pid: id }).then(data => {
        this.dataList = data
      })
    },
    handleNodeClick(data) {
      console.log('data:', data)
    }
  }
}
</script>

<style scoped>

</style>
