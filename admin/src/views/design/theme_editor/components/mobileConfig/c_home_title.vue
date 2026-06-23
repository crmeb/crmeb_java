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
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { mapState, mapMutations, mapActions } from 'vuex';
export default {
  name: 'c_home_title',
  componentsName: 'home_title',
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
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ],
      rComContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleLeft',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'titleConfig',
        },
        {
          components: toolCom.c_radio,
          configNme: 'buttonConfig',
        },
      ],
      oneContent: [
        {
          components: toolCom.c_input_item,
          configNme: 'titleConfigRight',
        },
        {
          components: toolCom.c_input_item,
          configNme: 'linkConfig',
        },
      ],
      oneStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRight',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'themeColor',
        },
        {
          components: toolCom.c_slider,
          configNme: 'fontSize',
        },
        {
          components: toolCom.c_radio,
          configNme: 'textStyle',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'buttonColor',
        },
        {
          components: toolCom.c_slider,
          configNme: 'buttonText',
        },
      ],
      twoStyle: [
        {
          components: toolCom.c_title,
          configNme: 'titleRight',
        },
        {
          components: toolCom.c_bg_color,
          configNme: 'themeColor',
        },
        {
          components: toolCom.c_slider,
          configNme: 'fontSize',
        },
        {
          components: toolCom.c_radio,
          configNme: 'textPosition',
        },
        {
          components: toolCom.c_radio,
          configNme: 'textStyle',
        },
      ],
      currencyStyle: [
        {
          components: toolCom.c_common_style,
          configNme: 'commonStyle',
        },
      ],
      setUp: 0,
      type: 0,
    };
  },
  watch: {
    num(nVal) {
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
          if (this.type == 0) {
            let rCom = arr.concat(this.rComContent);
            this.rCom = rCom.concat(this.oneContent);
          } else {
            this.rCom = arr.concat(this.rComContent);
          }
        } else {
          if (this.type == 0) {
            let rCom = arr.concat(this.oneStyle);
            this.rCom = rCom.concat(this.currencyStyle);
          } else {
            let rCom = arr.concat(this.twoStyle);
            this.rCom = rCom.concat(this.currencyStyle);
          }
        }
      },
      deep: true,
    },
    'configObj.buttonConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp) {
          if (nVal == 0) {
            let rCom = arr.concat(this.oneStyle);
            this.rCom = rCom.concat(this.currencyStyle);
          } else {
            let rCom = arr.concat(this.twoStyle);
            this.rCom = rCom.concat(this.currencyStyle);
          }
        } else {
          if (nVal == 0) {
            let rCom = arr.concat(this.rComContent);
            this.rCom = rCom.concat(this.oneContent);
          } else {
            this.rCom = arr.concat(this.rComContent);
          }
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
    patchConfig(data) {
      if (!data) return data;
      if (!data.paddingConfig) {
        this.$set(data, 'paddingConfig', {
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
        if (data.topConfig) data.paddingConfig.valList[0].val = data.topConfig.val;
        if (data.prConfig) {
          data.paddingConfig.valList[1].val = data.prConfig.val;
          data.paddingConfig.valList[3].val = data.prConfig.val;
        }
        if (data.bottomConfig) data.paddingConfig.valList[2].val = data.bottomConfig.val;
      }
      if (!data.marginConfig) {
        this.$set(data, 'marginConfig', {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
        if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
      }
      return data;
    },
    // 获取组件参数
    getConfig(data) {},
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
