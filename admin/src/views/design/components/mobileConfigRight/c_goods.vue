<template>
  <!--商品列表指定商品组件-->
  <div class="goods-box borderPadding" v-if="defaults.tabConfig.tabVal === 0">
    <div class="wrapper">
      <template v-if="defaults.goodsList.list">
        <div class="item" v-for="(good, index) in defaults.goodsList.list" :key="index">
          <img :src="good.image" alt="" />
          <i class="el-icon-error" @click.stop="bindDelete(index)" />
        </div>
        <div v-if="defaults.goodsList.list.length < 100" class="add-item item" @click="addGoods">
          <span class="iconfont icon-tianjia1"></span>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import vuedraggable from 'vuedraggable';
export default {
  name: 'c_goods',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
  },
  components: {
    draggable: vuedraggable,
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
      },
      immediate: true,
      deep: true,
    },
  },
  data() {
    return {
      goodsList: [],
      defaults: this.configObj,
    };
  },
  created() {
    this.defaults = this.configObj;
  },
  methods: {
    //选择商品
    addGoods() {
      const _this = this;
      this.$modalGoodList(
        function (row) {
          _this.listLoading = false;
          _this.getAttrValue(row);
        },
        'many',
        _this.defaults.goodsList.list,
      );
    },
    // 选中商品
    getAttrValue(row) {
      const _this = this;
      _this.defaults.goodsList.list = row;
      _this.$emit('getConfig', { name: 'goods', values: row });
    },
    bindDelete(index) {
      this.defaults.goodsList.list.splice(index, 1);
    },
  },
};
</script>

<style scoped lang="scss">
.goods-box {
  margin-bottom: 16px;
  .wrapper,
  .list-group {
    display: flex;
    flex-wrap: wrap;
  }
  .add-item {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 80px;
    height: 80px;
    margin-bottom: 10px;
    background: #f7f7f7;
    .iconfont-diy,
    .iconfont {
      font-size: 20px;
      color: #d8d8d8;
    }
  }
  .item {
    position: relative;
    width: 80px;
    height: 80px;
    margin-bottom: 20px;
    margin-right: 12px;
    img {
      width: 100%;
      height: 100%;
    }
    .el-icon-error {
      position: absolute;
      right: -9px;
      top: -13px;
      font-size: 19px;
      cursor: pointer;
    }
  }
}
</style>
