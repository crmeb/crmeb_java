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
  name: 'c_ueditor_box',
  componentsName: 'z_ueditor',
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
        var arr = [this.rCom[0]];
        if (nVal == 0) {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'titleLeft',
            },
            {
              components: toolCom.c_page_ueditor,
              configNme: 'richText',
            },
          ];
          this.rCom = arr.concat(tempArr);
        } else {
          let tempArr = [
            {
              components: toolCom.c_title,
              configNme: 'titleRight',
            },
            {
              components: toolCom.c_common_style,
              configNme: 'c_common_style',
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
            { val: config.lrConfig ? config.lrConfig.val : 0 },
            { val: config.bottomConfig ? config.bottomConfig.val : 0 },
            { val: config.lrConfig ? config.lrConfig.val : 0 },
          ],
        };
      }
      if (!config.marginConfig) {
        config.marginConfig = {
          title: '外边距',
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: config.udConfig ? config.udConfig.val : 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
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
    // 获取组件参数
    getConfig(data) {},
  },
};
</script>

<style scoped></style>
