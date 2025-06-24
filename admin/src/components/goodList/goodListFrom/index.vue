<template>
  <div>
    <el-dialog title="商品列表" :visible.sync="visible" width="896px" :before-close="handleClose">
      <good-list
        v-if="visible"
        @getStoreItem="getStoreItem"
        :handleNum="handleNum"
        :checked="checked"
        @close="close"
      ></good-list>
    </el-dialog>
  </div>
</template>

<script>
import goodList from '@/components/goodList/index.vue';
export default {
  name: 'GoodListFrom',
  components: { goodList },
  data() {
    return {
      handleNum: '',
      visible: false,
      callback: function () {},
      checked: [],
    };
  },
  methods: {
    close() {
      this.visible = false;
    },
    handleClose() {
      this.visible = false;
    },
    getStoreItem(img) {
      this.callback(img);
      this.visible = false;
    },
  },
};
</script>

<style scoped>
::v-deep .el-dialog__body {
  padding: 20px 24px 0 24px !important;
}
</style>
