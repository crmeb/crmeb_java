<template>
  <div class="mobile-config">
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
  </div>
</template>

<script>
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { mapState, mapMutations, mapActions } from 'vuex';

export default {
  name: 'c_product_service',
  cname: '商品服务',
  componentsName: 'home_product_service',
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
      setUp: 0,
      rCom: [
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ],
    };
  },
  watch: {
    num: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[nVal];
        this.setConfig(data);
      },
      deep: true,
    },
    'configObj.setUp.tabVal': {
      handler(nVal, oVal) {
        this.setUp = nVal;
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.toneConfig.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
  },
  mounted() {
    this.$nextTick(() => {
      let data = this.$store.state.mobildConfig.defaultArray[this.num];
      this.setConfig(data);
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      data = this.patchConfig(data);
      this.configObj = data;
      this.setUp = data.setUp.tabVal;
      this.updateRCom();
    },
    patchConfig(data) {
      if (!data) return data;
      if (data.checkBoxConfig) {
        const list = (data.checkBoxConfig.list || []).filter((item) => item.id !== 2);
        const type = (data.checkBoxConfig.type || []).filter((id) => id !== 2);
        this.$set(data.checkBoxConfig, 'list', list);
        this.$set(data.checkBoxConfig, 'type', type);
      }
      if (!data.paddingConfig) {
        this.$set(data, 'paddingConfig', {
          title: '内边距',
          val: 10,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
      }
      if (!data.marginConfig) {
        this.$set(data, 'marginConfig', {
          title: '外边距',
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
        if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
      }
      return data;
    },
    updateRCom() {
      const arr = [
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ];
      if (this.setUp == 0) {
        // Content Settings
        this.rCom = arr.concat([
          {
            components: toolCom.c_title,
            configNme: 'openService',
          },
          {
            components: toolCom.c_checkbox,
            configNme: 'checkBoxConfig',
          },
        ]);
      } else {
        // Style Settings
        let styleArr = [
          {
            components: toolCom.c_title,
            configNme: 'serviceStyleTitle',
          },
          {
            components: toolCom.c_bg_color,
            configNme: 'titleColor',
          },
          {
            components: toolCom.c_bg_color,
            configNme: 'contentColor',
          },
          {
            components: toolCom.c_radio,
            configNme: 'toneConfig',
          },
        ];

        if (this.configObj.toneConfig && this.configObj.toneConfig.tabVal == 1) {
          styleArr = styleArr.concat([
            {
              components: toolCom.c_bg_color,
              configNme: 'activityColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'activityBgColor',
            },
          ]);
        }

        styleArr = styleArr.concat([
          {
            components: toolCom.c_title,
            configNme: 'generalStyleTitle',
          },
          {
            components: toolCom.c_bg_color,
            configNme: 'componentBgColor',
          },
          {
            components: toolCom.c_common_style,
            configNme: 'c_common_style',
          },
        ]);

        this.rCom = arr.concat(styleArr);
      }
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-config {
  padding-bottom: 20px;
}
</style>
