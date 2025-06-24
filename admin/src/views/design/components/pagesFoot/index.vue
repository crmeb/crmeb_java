<template>
  <div class="page-fooot" :style="{ background: bgColor }">
    <div class="foot-item" v-for="(item, index) in menuList" :key="index">
      <img :src="item.imgList[0]" alt="" v-if="index == isSpecial" />
      <img :src="item.imgList[1]" alt="" v-else />
      <p v-if="index == isSpecial" :style="{ color: activeTxtColor }">{{ item.name }}</p>
      <p v-else :style="{ color: txtColor }">{{ item.name }}</p>
    </div>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { mapState } from 'vuex';

export default {
  name: 'index.vue',
  props: {
    configObj: {
      type: Object,
      default: function () {
        return {};
      },
    },
    configNme: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      status: true,
      txtColor: '',
      activeTxtColor: '',
      bgColor: '',
      menuList: [],
      isSpecial: 2,
    };
  },
  computed: {
    ...mapState('mobildConfig', ['pageFooter']),
  },
  watch: {
    pageFooter: {
      handler(nVal, oVal) {
        this.setConfig(nVal);
      },
      deep: true,
    },
  },
  mounted() {
    let data = this.$store.state.mobildConfig.pageFooter;
    this.setConfig(data);
  },
  methods: {
    setConfig(data) {
      this.status = data.status;
      this.txtColor = data.txtColor.color[0].item;
      this.activeTxtColor = data.activeTxtColor.color[0].item;
      this.bgColor = data.bgColor && data.bgColor.color[0].item;
      this.menuList = [];
      this.$set(this, 'menuList', data.menuList);
      if (data.status.title == '是否显示') {
        this.isSpecial = 2;
      } else {
        this.isSpecial = 0;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.page-fooot {
  display: flex;
  background: #fff;
  position: relative;
  // .bg{
  //     position: absolute;
  //     top: 0;
  //     left: 0;
  //     width: 379px;
  //     height: 100%;
  // }
  &.filter {
    filter: blur(1px);
    // opacity: .8;
  }
  .foot-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 50px;
    img {
      width: 25px;
      height: 25px;
    }
    p {
      padding: 0;
      margin: 0;
      font-size: 12px;
      color: #282828;
      &.on {
        color: #00a4f8;
      }
    }
  }
}
</style>
