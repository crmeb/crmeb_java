<template>
  <div class="mobile-config pro">
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
export default {
  name: 'c_home_menu',
  cname: '导航组',
  componentsName: 'home_menu',
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
    ...toolCom,
    rightBtn,
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
      oneStyle: [
        {
          components: toolCom.c_title,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_txt_tab,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_txt_tab,
          configNme: 'rowsNum',
        },
        {
          components: toolCom.c_txt_tab,
          configNme: 'number',
        },
        {
          components: toolCom.c_title,
          configNme: 'menuConfig',
        },
        {
          components: toolCom.c_menu_list,
          configNme: 'menuConfig',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_title,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_txt_tab,
          configNme: 'tabConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'menuConfig',
        },
        {
          components: toolCom.c_menu_list,
          configNme: 'menuConfig',
        },
      ],
      configStyle: [
        {
          components: toolCom.c_title,
          configNme: 'bgColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'bgColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'titleColor',
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
          configNme: 'contentConfig',
        },
        {
          components: toolCom.c_slider,
          configNme: 'mbConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'bgStyle',
        },
        {
          components: toolCom.c_slider,
          configNme: 'bgStyle',
        },
        {
          components: toolCom.c_slider,
          configNme: 'contentStyle',
        },
      ],
      type: 0,
      setUp: 0,
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
        this.setUp = nVal;
        var arr = [this.rCom[0]];
        if (nVal == 0) {
          if (this.type == 0) {
            this.rCom = arr.concat(this.twoStyle);
          } else {
            this.rCom = arr.concat(this.oneStyle);
          }
        } else {
          this.rCom = arr.concat(this.configStyle);
        }
      },
      deep: true,
    },
    'configObj.tabConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp === 0) {
          if (nVal == 0) {
            this.rCom = arr.concat(this.twoStyle);
          } else {
            this.rCom = arr.concat(this.oneStyle);
          }
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
    getConfig(data) {},
  },
};
</script>

<style scoped></style>
