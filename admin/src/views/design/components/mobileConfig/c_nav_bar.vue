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
import { mapState, mapMutations, mapActions } from 'vuex';
import rightBtn from '../rightBtn/index.vue';
export default {
  name: 'c_nav_bar',
  componentsName: 'nav_bar',
  cname: '导航',
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
  components: {
    // formCreate: formCreate.$form()
    ...toolCom,
    rightBtn,
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
              configNme: 'listConfig',
            },
            {
              components: toolCom.c_classify,
              configNme: 'listConfig',
            },
          ];
          this.rCom = arr.concat(tempArr);
        } else {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'bgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'bgColor',
            },
            {
              components: toolCom.c_title,
              configNme: 'fontColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'fontColor',
            },
            {
              components: toolCom.c_radio,
              configNme: 'themeStyleConfig',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'checkColor',
            },
            // {
            //   components: toolCom.c_title,
            //   configNme: 'upConfig',
            // },
            // {
            //   components: toolCom.c_slider,
            //   configNme: 'upConfig',
            // },
            // {
            //   components: toolCom.c_slider,
            //   configNme: 'downConfig',
            // },
            // {
            //   components: toolCom.c_slider,
            //   configNme: 'contentConfig',
            // },
            // {
            //   components: toolCom.c_slider,
            //   configNme: 'lrConfig',
            // },
            // {
            //   components: toolCom.c_slider,
            //   configNme: 'mbConfig',
            // },
            {
              components: toolCom.c_title,
              configNme: 'bgStyle',
            },
            {
              components: toolCom.c_slider,
              configNme: 'bgStyle',
            },
          ];
          this.rCom = arr.concat(tempArr);
        }
      },
      deep: true,
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
      // rCom: [
      //   {
      //     components: toolCom.c_bg_color,
      //     configNme: 'txtColor',
      //   },
      //   {
      //     components: toolCom.c_bg_color,
      //     configNme: 'activeColor',
      //   },
      //   {
      //     components: toolCom.c_bg_color,
      //     configNme: 'bgColor',
      //   },
      //   {
      //     components: toolCom.c_slider,
      //     configNme: 'mbConfig',
      //   },
      //   // toolCom.c_bg_color,
      //   // toolCom.c_slider
      // ],
    };
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = value;
    });
  },
  methods: {
    // 获取组件参数
    getConfig(data) {
      if (data.name !== 'checkbox_tab') this.configObj.listConfig.list = data.values;
    },
  },
};
</script>

<style scoped></style>
