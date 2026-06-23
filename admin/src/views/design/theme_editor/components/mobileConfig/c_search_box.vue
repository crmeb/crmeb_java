<template>
  <div class="mobile-config">
    <Form ref="formInline">
      <div v-for="(item, key) in rCom" :key="key">
        <component
          :is="item.components.name"
          :configObj="configObj"
          ref="childData"
          :configNme="item.configNme"
          :key="key"
          :index="activeIndex"
          :num="item.num"
        ></component>
      </div>
      <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
    </Form>
  </div>
</template>

<script>
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { mapMutations } from 'vuex';
export default {
  name: 'c_search_box',
  componentsName: 'search_box',
  cname: '搜索框',
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
      hotIndex: 1,
      configObj: {}, // 配置对象
      rCom: [
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ], // 当前页面组件
      oneContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleLeft',
        },
        {
          components: toolCom.c_radio,
          configNme: 'styleConfig',
        },
      ],
      oneContentType: [
        {
          components: toolCom.c_radio,
          configNme: 'styleTypeConfig',
        },
      ],
      oneContentFix: [
        {
          components: toolCom.c_radio,
          configNme: 'fixConfig',
        },
      ],
      twoContent: [
        {
          components: toolCom.c_upload_img,
          configNme: 'logoConfig',
        },
      ],
      threeContent: [
        {
          components: toolCom.c_input_item,
          configNme: 'titleConfig',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'linkConfig',
        },
      ],
      rComContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleSearch',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'tipConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleHotWords',
        },
        {
          components: toolCom.c_hot_word,
          configNme: 'hotWords',
        },
        {
          components: toolCom.c_input_number,
          configNme: 'numConfig',
        },
      ],
      oneStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRight',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'tipColor',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'hotWordsColor',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleTxt',
        },
        {
          components: toolCom.c_radio,
          configNme: 'txtFixConfig',
        },
      ],
      twoStyle1: [
        {
          components: toolCom.c_radio,
          configNme: 'txtStyleConfig',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'txtColor',
        },
        {
          components: toolCom.c_slider,
          configNme: 'txtSize',
        },
      ],
      currencyStyle: [
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
          this.getRComContent(arr);
        } else {
          this.getRComStyle(arr);
        }
      },
      deep: true,
    },
    'configObj.styleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp == 0) {
          this.getRComContent(arr);
        } else {
          this.getRComStyle(arr);
        }
      },
      deep: true,
    },
    'configObj.styleTypeConfig.tabVal': {
      handler(nVal, oVal) {
        this.type2 = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp == 0) {
          this.getRComContent(arr);
        } else {
          this.getRComStyle(arr);
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
          isAll: false,
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
          isAll: false,
          valList: [{ val: config.mbConfig ? config.mbConfig.val : 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        };
      }
      if (!config.componentBgConfig) {
        config.componentBgConfig = {
          title: '背景设置',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [{ item: '#fff' }, { item: '#fff' }],
            color: [{ item: '#fff' }, { item: '#fff' }],
          },
          colorDirection: {
            title: '渐变方向',
            tabVal: 0,
            tabList: [{ name: '横向' }, { name: '纵向' }, { name: '左斜' }, { name: '右斜' }],
          },
          imageConfig: {
            header: '背景图片',
            title: '',
            name: '上传图片',
            type: 'code',
            url: '',
            info: '建议尺寸：750px * 400px',
          },
        };
      }
      if (!config.borderConfig) {
        config.borderConfig = {
          title: '边框设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0,
          styleConfig: {
            title: '边框样式',
            tabVal: 0,
            tabList: [
              { name: '实线', style: 'solid' },
              { name: '虚线', style: 'dashed' },
              { name: '点状', style: 'dotted' },
            ],
          },
          widthConfig: {
            title: '边框粗细',
            val: 1,
            min: 1,
            max: 20,
          },
          colorConfig: {
            title: '边框颜色',
            default: [{ item: '#e5e5e5' }],
            color: [{ item: '#e5e5e5' }],
          },
        };
      }
      if (!config.shadowConfig) {
        config.shadowConfig = {
          title: '阴影设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0,
          colorConfig: {
            title: '阴影颜色',
            default: [{ item: 'rgba(0,0,0,0.1)' }],
            color: [{ item: 'rgba(0,0,0,0.1)' }],
          },
          xConfig: {
            title: 'X轴偏移',
            val: 0,
            min: -50,
            max: 50,
          },
          yConfig: {
            title: 'Y轴偏移',
            val: 0,
            min: -50,
            max: 50,
          },
          blurConfig: {
            title: '模糊半径',
            val: 10,
            min: 0,
            max: 50,
          },
          spreadConfig: {
            title: '扩展半径',
            val: 0,
            min: -50,
            max: 50,
          },
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
    getRComContent(arr) {
      if (this.type == 1) {
        // this.rCom = [...arr, ...this.oneContent, ...this.threeContent];
        this.rCom = [...arr, ...this.oneContent, ...this.threeContent, ...this.oneContentFix];
      } else if (this.type == 2) {
      } else {
        if (this.type2 == 0) {
          this.rCom = [...arr, ...this.oneContent, ...this.oneContentType, ...this.threeContent, ...this.rComContent];
        } else if (this.type2 == 1) {
          this.rCom = [...arr, ...this.oneContent, ...this.oneContentType, ...this.twoContent, ...this.rComContent];
        } else {
          this.rCom = [...arr, ...this.oneContent, ...this.oneContentType, ...this.oneContentFix, ...this.rComContent];
        }
      }
    },
    getRComStyle(arr) {
      if (this.type == 0) {
        if (this.type2 == 2) {
          this.rCom = [...arr, ...this.oneStyle, ...this.twoStyle, ...this.currencyStyle];
        } else {
          this.rCom = [...arr, ...this.oneStyle, ...this.twoStyle, ...this.twoStyle1, ...this.currencyStyle];
        }
      } else {
        this.rCom = [...arr, ...this.twoStyle, ...this.twoStyle1, ...this.currencyStyle];
      }
    },
  },
};
</script>

<style scoped lang="scss">
.title-tips {
  padding-bottom: 10px;
  font-size: 14px;
  color: #333;
  span {
    margin-right: 14px;
    color: #999;
  }
}
</style>
