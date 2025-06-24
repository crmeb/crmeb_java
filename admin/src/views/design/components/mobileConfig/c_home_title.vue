<template>
  <div class="mobile-config">
    <div v-for="(item, key) in rCom" :key="key">
      <component
        :is="item.components.name"
        :configObj="configObj"
        ref="childData"
        :configNme="item.configNme"
        :key="key"
        @getConfig="getConfig"
        :index="activeIndex"
        :num="item.num"
      ></component>
    </div>
    <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
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
import toolCom from '../mobileConfigRight/index.js';
import rightBtn from '../rightBtn/index.vue';
import { mapState, mapMutations, mapActions } from 'vuex';
export default {
  name: 'c_home_bargain',
  componentsName: 'home_bargain',
  components: {
    ...toolCom,
    rightBtn,
  },
  props: {
    activeIndex: {
      type: null,
    },
    num: {
      type: null,
    },
    index: {
      type: null,
    },
  },
  data() {
    return {
      configObj: {},
      rCom: [
        {
          components: toolCom.c_checked_tab,
          configNme: 'setUp',
        },
      ],
    };
  },
  watch: {
    num(nVal) {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = value;
    },
    configObj: {
      handler(nVal, oVal) {
        this.$store.commit('mobildConfig/UPDATEARR', { num: this.num, val: nVal });
      },
      deep: true,
    },
    'configObj.setUp.tabVal': {
      handler(nVal, oVal) {
        var arr = [this.rCom[0]];
        if (nVal == 0) {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'titleConfig',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'titleConfig',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'titleFuConfig',
            },
            {
              components: toolCom.c_radio,
              configNme: 'selectShow',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'titleRightConfig',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'linkConfig',
            },
            {
              components: toolCom.c_radio,
              configNme: 'selectStyle',
            },
            {
              components: toolCom.c_upload_img,
              configNme: 'bgImg',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'bgColor',
            },
          ];
          this.rCom = arr.concat(tempArr);
        } else {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'fontColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'fontColor',
            },
            {
              components: toolCom.c_slider,
              configNme: 'fontSize',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'textStyle',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'fontFuColor',
            },
            {
              components: toolCom.c_slider,
              configNme: 'fontFuSize',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'fontRightColor',
            },
            {
              components: toolCom.c_slider,
              configNme: 'fontRightSize',
            },
            {
              components: toolCom.c_title,
              configNme: 'upConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'upConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'downConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'lrConfig',
            },
            {
              components: toolCom.c_slider,
              configNme: 'mbConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'bgTopStyle',
            },
            {
              components: toolCom.c_slider,
              configNme: 'bgTopStyle',
            },
            {
              components: toolCom.c_slider,
              configNme: 'bgDownStyle',
            },
          ];
          this.rCom = arr.concat(tempArr);
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = value;
    });
  },
  methods: {
    // 获取组件参数
    getConfig(data) {},
  },
};
</script>

<style scoped lang="scss">
.title-tips {
  padding-bottom: 10px;
  color: #333;
  span {
    margin-right: 14px;
    color: #999;
  }
}
</style>
