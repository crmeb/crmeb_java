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
        :number="num"
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
  name: 'c_home_group',
  componentsName: 'home_group',
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
              configNme: 'tabConfig',
            },
            {
              components: toolCom.c_txt_tab,
              configNme: 'tabConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'logoConfig',
            },
            {
              components: toolCom.c_radio,
              configNme: 'selectBgImg',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'bgColor',
            },
            {
              components: toolCom.c_upload_img,
              configNme: 'bgImg',
            },
            {
              components: toolCom.c_radio,
              configNme: 'selectStyle',
            },
            {
              components: toolCom.c_upload_img,
              configNme: 'logoConfig',
            },
            {
              components: toolCom.c_input_item,
              configNme: 'titleConfig',
            },
            {
              components: toolCom.c_title,
              configNme: 'typeConfig',
            },
            {
              components: toolCom.c_checkbox_group,
              configNme: 'typeConfig',
            },
            {
              components: toolCom.c_radio,
              configNme: 'groupBtnConfig',
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
              components: toolCom.c_radio,
              configNme: 'headerTitleStyle',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'headerTitleColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'lineColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'titleColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'headerBtnColor',
            },
            {
              components: toolCom.c_title,
              configNme: 'nameColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'contentBgColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'nameColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'originalColor',
            },
            {
              components: toolCom.c_radio,
              configNme: 'themeStyleConfig',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'priceColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'groupTitleColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'btnColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'btnFontColor',
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
  created() {},
  methods: {
    getConfig(data) {
      if (data.name && data.name === 'c_input_item') this.configObj.linkConfig.val = data.values;
    },
  },
};
</script>

<style scoped></style>
