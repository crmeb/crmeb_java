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
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import { mapState, mapMutations, mapActions } from 'vuex';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
export default {
  name: 'c_ranking',
  componentsName: 'home_ranking',
  cname: '排行榜',
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
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ],
      oneContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleLeft',
        },
        {
          components: toolCom.c_radio,
          configNme: 'titleConfig',
        },
      ],
      oneContentImg: [
        {
          components: toolCom.c_upload_img,
          configNme: 'imgConfig',
        },
      ],
      oneContentText: [
        {
          components: toolCom.c_input_item,
          configNme: 'titleTxtConfig',
        },
      ],
      twoContent: [
        {
          components: toolCom.c_input_item,
          configNme: 'rightBntConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleGoods',
        },
        {
          components: toolCom.c_button_style,
          configNme: 'styleConfig',
        },
      ],
      oneStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRight',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_radio,
          configNme: 'titleText',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'titleColor',
        },
        {
          components: toolCom.c_slider,
          configNme: 'titleNumber',
        },
      ],
      threeStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'headerBntColor',
        },
        {
          components: toolCom.c_slider,
          configNme: 'bntNumber',
        },
      ],
      fourStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRanking',
        },
        {
          components: toolCom.c_radio,
          configNme: 'toneConfig',
        },
      ],
      fiveStyle: [
        {
          components: toolCom.c_bg_color,
          configNme: 'classColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'goodsPriceColor',
        },
      ],
      currencyStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleCurrency',
        },
        {
          components: toolCom.c_common_style,
          configNme: 'c_common_style',
        },
      ],
      setUp: 0,
      type: 0,
      type2: 0,
    };
  },
  watch: {
    num(nVal) {
      // debugger;
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.configObj = this.patchConfig(value);
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
          this.getRComContent(arr, this.type);
        } else {
          this.getRComStyle(arr, this.type, this.type2);
        }
      },
      deep: true,
    },
    'configObj.titleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp == 0) {
          this.getRComContent(arr, nVal);
        } else {
          this.getRComStyle(arr, nVal, this.type2);
        }
      },
      deep: true,
    },
    'configObj.toneConfig.tabVal': {
      handler(nVal, oVal) {
        this.type2 = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp) {
          this.getRComStyle(arr, this.type, nVal);
        }
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.configObj = this.patchConfig(value);
    });
  },
  methods: {
    patchConfig(config) {
      if (!config.paddingConfig) {
        config.paddingConfig = {
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [
            { val: config.topConfig ? config.topConfig.val : 0 },
            { val: config.prConfig ? config.prConfig.val : 0 },
            { val: config.bottomConfig ? config.bottomConfig.val : 0 },
            { val: config.prConfig ? config.prConfig.val : 0 },
          ],
        };
      }
      if (!config.marginConfig) {
        config.marginConfig = {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: config.mbConfig ? config.mbConfig.val : 0 }, { val: 0 }],
        };
      }
      if (!config.c_common_style) {
        config.c_common_style = {
          color: 'rgba(255,255,255,1)',
          color2: 'rgba(255,255,255,1)',
          lr: 0,
          type: 0,
        };
      }
      return config;
    },
    getRComContent(arr, type) {
      if (type == 0) {
        this.rCom = [...arr, ...this.oneContent, ...this.oneContentImg, ...this.twoContent];
      } else {
        this.rCom = [...arr, ...this.oneContent, ...this.oneContentText, ...this.twoContent];
      }
    },
    getRComStyle(arr, type, type2) {
      if (type == 0) {
        if (type2 == 0) {
          this.rCom = [...arr, ...this.oneStyle, ...this.threeStyle, ...this.fourStyle, ...this.currencyStyle];
        } else {
          this.rCom = [
            ...arr,
            ...this.oneStyle,
            ...this.threeStyle,
            ...this.fourStyle,
            ...this.fiveStyle,
            ...this.currencyStyle,
          ];
        }
      } else {
        if (type2 == 0) {
          this.rCom = [
            ...arr,
            ...this.oneStyle,
            ...this.twoStyle,
            ...this.threeStyle,
            ...this.fourStyle,
            ...this.currencyStyle,
          ];
        } else {
          this.rCom = [
            ...arr,
            ...this.oneStyle,
            ...this.twoStyle,
            ...this.threeStyle,
            ...this.fourStyle,
            ...this.fiveStyle,
            ...this.currencyStyle,
          ];
        }
      }
    },
    // 获取组件参数
    getConfig(data) {},
  },
};
</script>

<style scoped></style>
