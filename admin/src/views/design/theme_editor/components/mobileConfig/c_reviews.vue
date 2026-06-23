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
  name: 'c_reviews',
  cname: '商品评价',
  componentsName: 'home_reviews',
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
          components: toolCom.c_card_select,
          configNme: 'layoutConfig',
        },
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
    updateRCom() {
      const arr = [
        {
          components: toolCom.c_card_select,
          configNme: 'layoutConfig',
        },
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
            configNme: 'headTitle',
          },
          {
            components: toolCom.c_checkbox,
            configNme: 'checkBoxConfig',
          },
          {
            components: toolCom.c_title,
            configNme: 'listTitle',
          },
          // {
          //   components: toolCom.c_radio,
          //   configNme: 'layoutConfig',
          // },
          {
            components: toolCom.c_slider,
            configNme: 'numConfig',
          },
        ]);
      } else {
        // Style Settings
        let styleArr = [
          {
            components: toolCom.c_title,
            configNme: 'reviewStyleTitle',
          },
          {
            components: toolCom.c_bg_color,
            configNme: 'titleColor',
          },
          {
            components: toolCom.c_bg_color,
            configNme: 'countColor',
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
              configNme: 'rateColor',
            },
            {
              components: toolCom.c_bg_color,
              configNme: 'starColor',
            },
          ]);
        }

        styleArr = styleArr.concat([
          {
            components: toolCom.c_title,
            configNme: 'generalStyleTitle',
          },
          {
            components: toolCom.c_common_style,
            configNme: 'c_common_style',
          },
        ]);

        this.rCom = arr.concat(styleArr);
      }
    },
    patchConfig(data) {
      if (!data) return data;
      if (!data.componentBgConfig) {
        this.$set(data, 'componentBgConfig', {
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
        });
      }
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
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
        if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
      }
      if (!data.zIndexConfig) {
        this.$set(data, 'zIndexConfig', {
          title: '组件上浮',
          val: 0,
          min: 0,
        });
      }
      if (!data.borderConfig) {
        this.$set(data, 'borderConfig', {
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
          },
          colorConfig: {
            title: '边框颜色',
            default: [{ item: '#e5e5e5' }],
            color: [{ item: '#e5e5e5' }],
          },
        });
      }
      if (!data.shadowConfig) {
        this.$set(data, 'shadowConfig', {
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
          },
          yConfig: {
            title: 'Y轴偏移',
            val: 0,
            min: -50,
          },
          blurConfig: {
            title: '模糊半径',
            val: 10,
            min: 0,
          },
          spreadConfig: {
            title: '扩展半径',
            val: 0,
            min: -50,
          },
        });
      }
      return data;
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-config {
  padding-bottom: 20px;
}
</style>
