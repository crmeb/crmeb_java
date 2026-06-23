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

export default {
  name: 'c_product_info',
  componentsName: 'home_product_info',
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
      defaultConfig: {
        cname: '商品信息',
        desc: '商品信息组件',
        name: 'productInfo',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
        },
        indicatorConfig: {
          title: '指示器设置',
          tabVal: 1,
          tabList: [{ name: '线段样式' }, { name: '点线样式' }, { name: '数字样式' }],
          positionVal: 1,
          positionList: [{ name: '左对齐' }, { name: '居中对齐' }, { name: '右对齐' }],
          selectColor: {
            title: '选中样式',
            name: 'selectColor',
            default: [{ item: '#E93323' }],
            color: [{ item: '#E93323' }],
          },
          defaultColor: {
            title: '默认样式',
            name: 'defaultColor',
            default: [{ item: '#CCCCCC' }],
            color: [{ item: '#CCCCCC' }],
          },
        },
        titleConfig: {
          title: '标题设置',
          tabVal: 0,
          tabList: [
            { name: '跟随主题风格', val: 0 },
            { name: '自定义', val: 1 },
          ],
          color: {
            title: '标题颜色',
            default: [{ item: '#333333' }],
            color: [{ item: '#333333' }],
          },
          fontSize: {
            title: '字体大小',
            val: 16,
            min: 12,
          },
        },
        specStyle: {
          title: '规格样式',
          tabVal: 0,
          tabList: [{ name: '样式一' }, { name: '样式二' }, { name: '样式三' }, { name: '样式四' }],
        },
        specSettings: {
          title: '规格设置',
          colorTone: {
            title: '色调',
            tabVal: 0,
            tabList: [
              { name: '跟随主题风格', val: 0 },
              { name: '自定义', val: 1 },
            ],
          },
          textColor: {
            title: '按钮颜色',
            name: 'textColor',
            default: [{ item: '#666' }],
            color: [{ item: '#666' }],
          },
          selectedBorderColor: {
            title: '选中边框',
            name: 'selectedBorderColor',
            default: [{ item: '#E93323' }],
            color: [{ item: '#E93323' }],
          },
          selectedTextColor: {
            title: '选中文字',
            name: 'selectedTextColor',
            default: [{ item: '#E93323' }],
            color: [{ item: '#E93323' }],
          },
          selectedBgColor: {
            title: '选中背景',
            name: 'selectedBgColor',
            default: [{ item: '#FDEBEB' }],
            color: [{ item: '#FDEBEB' }],
          },
          unselectedTextColor: {
            title: '未选中文字',
            name: 'unselectedTextColor',
            default: [{ item: '#333333' }],
            color: [{ item: '#333333' }],
          },
        },
        sortList: {
          title: '信息设置',
          tips: '鼠标拖拽板块可调整信息展示顺序',
          list: [
            {
              name: 'price',
              cname: '商品价格',
              type: 'radio',
              show: true,
              checkList: [1, 2],
              checkBoxList: [
                // { name: '售价', value: 0 },
                { name: '划线价', value: 1 },
                { name: '会员价', value: 2 },
              ],
            },
            {
              name: 'name',
              cname: '商品名称',
              type: 'radio',
              show: true,
            },
            {
              name: 'data',
              cname: '商品数据',
              type: 'radio',
              show: true,
              checkList: [0, 1, 2],
              checkBoxList: [
                { name: '原价', value: 0 },
                { name: '库存', value: 1 },
                { name: '销量', value: 2 },
              ],
            },
            {
              name: 'tags',
              cname: '商品标签',
              type: 'radio',
              show: true,
            },
          ],
        },
        priceSettings: {
          title: '价格设置',
          colorTone: {
            title: '色调',
            tabVal: 0,
            tabList: [
              { name: '跟随主题风格', val: 0 },
              { name: '自定义', val: 1 },
            ],
          },
          finalPriceColor: {
            title: '到手价颜色',
            name: 'finalPriceColor',
            default: [{ item: '#E93323' }],
            color: [{ item: '#E93323' }],
          },
          sellingPriceColor: {
            title: '售价颜色',
            name: 'sellingPriceColor',
            default: [{ item: '#333333' }],
            color: [{ item: '#333333' }],
          },
          priceFontSize: {
            title: '价格字号',
            val: 24,
            min: 12,
            max: 50,
          },
        },
        dataSettings: {
          title: '数据设置',
          originalPriceColor: {
            title: '原价颜色',
            name: 'originalPriceColor',
            default: [{ item: '#999999' }],
            color: [{ item: '#999999' }],
          },
          stockColor: {
            title: '库存颜色',
            name: 'stockColor',
            default: [{ item: '#999999' }],
            color: [{ item: '#999999' }],
          },
          salesColor: {
            title: '销量颜色',
            name: 'salesColor',
            default: [{ item: '#999999' }],
            color: [{ item: '#999999' }],
          },
        },
      },
      rCom: [
        {
          components: toolCom.c_card_select,
          configNme: 'specStyle',
        },
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ],
    };
  },
  watch: {
    num(nVal) {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[nVal]));
      this.setConfig(value);
    },
    configObj: {
      handler(nVal, oVal) {
        this.$store.commit('mobildConfig/UPDATEARR', { num: this.num, val: nVal });
      },
      deep: true,
    },
    'configObj.setUp.tabVal': {
      handler(nVal) {
        this.setUpChange(nVal);
      },
    },
  },
  mounted() {
    this.$nextTick(() => {
      let value = JSON.parse(JSON.stringify(this.$store.state.mobildConfig.defaultArray[this.num]));
      this.setConfig(value);
      this.setUpChange(value.setUp.tabVal);
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      this.patchConfig(data);
      for (let key in this.defaultConfig) {
        if (data[key] === undefined) {
          this.$set(data, key, this.defaultConfig[key]);
        }
      }
      this.configObj = data;
    },
    patchConfig(data) {
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
      if (data.specSettings) {
        if (!data.specSettings.selectedBorderColor) {
          this.$set(data.specSettings, 'selectedBorderColor', {
            title: '选中边框',
            name: 'selectedBorderColor',
            default: [{ item: '#E93323' }],
            color: [{ item: '#E93323' }],
          });
        }
        if (!data.specSettings.selectedTextColor) {
          this.$set(data.specSettings, 'selectedTextColor', {
            title: '选中文字',
            name: 'selectedTextColor',
            default: [{ item: '#E93323' }],
            color: [{ item: '#E93323' }],
          });
        }
        if (!data.specSettings.selectedBgColor) {
          this.$set(data.specSettings, 'selectedBgColor', {
            title: '选中背景',
            name: 'selectedBgColor',
            default: [{ item: '#FDEBEB' }],
            color: [{ item: '#FDEBEB' }],
          });
        }
        if (!data.specSettings.unselectedTextColor) {
          this.$set(data.specSettings, 'unselectedTextColor', {
            title: '未选中文字',
            name: 'unselectedTextColor',
            default: [{ item: '#333333' }],
            color: [{ item: '#333333' }],
          });
        }
      }
    },
    setUpChange(val) {
      this.rCom = [
        {
          components: toolCom.c_card_select,
          configNme: 'specStyle',
        },
        {
          components: toolCom.c_set_up,
          configNme: 'setUp',
        },
      ];
      if (val == 0) {
        // Content Settings
        // Spec Style at the top

        // Product Info List (Draggable Sections)
        this.rCom.push({
          components: toolCom.c_product_info_list,
          configNme: 'sortList',
        });
      } else {
        // Style Settings
        this.rCom.push({
          components: toolCom.c_indicator_settings,
          configNme: 'indicatorConfig',
        });
        this.rCom.push({
          components: toolCom.c_title_settings,
          configNme: 'titleConfig',
        });
        this.rCom.push({
          components: toolCom.c_spec_settings,
          configNme: 'specSettings',
        });
        this.rCom.push({
          components: toolCom.c_price_settings,
          configNme: 'priceSettings',
        });
        this.rCom.push({
          components: toolCom.c_data_settings,
          configNme: 'dataSettings',
        });
        this.rCom.push({
          components: toolCom.c_common_style,
          configNme: 'c_common_style',
        });
      }
    },
    getConfig(data) {
      // Handle updates from child components if needed
    },
  },
};
</script>

<style scoped lang="scss"></style>
