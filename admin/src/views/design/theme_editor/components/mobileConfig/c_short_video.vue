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
// import { videoList } from '@/api/marketing'
import { mapState, mapMutations, mapActions } from 'vuex';
export default {
  name: 'c_short_video',
  componentsName: 'home_short_video',
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
      oneContent: [
        {
          components: toolCom.c_title,
          configNme: 'titleLeft',
        },
        {
          components: toolCom.c_radio,
          configNme: 'styleConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleHead',
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
          configNme: 'titleContent',
        },
        {
          components: toolCom.c_slider,
          configNme: 'numberConfig',
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
        {
          components: toolCom.c_title,
          configNme: 'titleVideoStyle',
        },
      ],
      videoSpaceStyle: [
        {
          components: toolCom.c_slider,
          configNme: 'videoSpace',
        },
      ],
      videoSpaceStyle2: [
        {
          components: toolCom.c_slider,
          configNme: 'videoSpace2',
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
      this.configObj = this.$store.state.mobildConfig.defaultArray[nVal];
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
          this.getRComContent(arr, this.type2);
        } else {
          this.getRComStyle(arr, this.type, this.type2);
        }
      },
      deep: true,
    },
    'configObj.styleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp == 0) {
          this.getRComContent(arr, this.type2);
        } else {
          this.getRComStyle(arr, nVal, this.type2);
        }
      },
    },
    'configObj.titleConfig.tabVal': {
      handler(nVal, oVal) {
        this.type2 = nVal;
        var arr = [this.rCom[0]];
        if (this.setUp == 0) {
          this.getRComContent(arr, nVal);
        } else {
          this.getRComStyle(arr, this.type, nVal);
        }
      },
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
    getRComContent(arr, type2) {
      if (type2 == 0) {
        this.rCom = [...arr, ...this.oneContent, ...this.oneContentImg, ...this.twoContent];
      } else {
        this.rCom = [...arr, ...this.oneContent, ...this.oneContentText, ...this.twoContent];
      }
    },
    getRComStyle(arr, type, type2) {
      if (type == 0) {
        if (type2 == 0) {
          this.rCom = [...arr, ...this.oneStyle, ...this.threeStyle, ...this.videoSpaceStyle, ...this.currencyStyle];
        } else {
          this.rCom = [
            ...arr,
            ...this.oneStyle,
            ...this.twoStyle,
            ...this.threeStyle,
            ...this.videoSpaceStyle,
            ...this.currencyStyle,
          ];
        }
      } else {
        if (type2 == 0) {
          this.rCom = [...arr, ...this.oneStyle, ...this.threeStyle, ...this.videoSpaceStyle2, ...this.currencyStyle];
        } else {
          this.rCom = [
            ...arr,
            ...this.oneStyle,
            ...this.twoStyle,
            ...this.threeStyle,
            ...this.videoSpaceStyle2,
            ...this.currencyStyle,
          ];
        }
      }
    },
    getVideoList(limit) {
      videoList({
        page: 1,
        limit: limit,
      })
        .then((res) => {
          this.configObj.videoList = res.data.list;
        })
        .catch((err) => {
          this.$message.error(err.msg);
        });
    },
    // 获取组件参数
    getConfig(data) {
      if (data.name == 'radio') {
        return;
      }
      // this.getVideoList(data.numVal);
    },
    handleSubmit(name) {
      let obj = {};
      obj.activeIndex = this.activeIndex;
      obj.data = this.configObj;
      this.add(obj);
    },
    ...mapMutations({
      add: 'mobildConfig/UPDATEARR',
    }),
  },
};
</script>

<style scoped></style>
