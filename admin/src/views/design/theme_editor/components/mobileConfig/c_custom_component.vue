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

    <!-- <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn> -->
    <el-dialog
      custom-class="custom-design-dialog"
      :visible.sync="modals"
      width="100%"
      fullscreen
      :close-on-click-modal="false"
      :show-close="false"
    >
      <CustomDesign
        v-if="modals"
        :initialData="configObj.customComponents"
        :columnNum="columnNum"
        :type="type"
        @save="handleSave"
        @close="modals = false"
      />
    </el-dialog>
  </div>
</template>

<script>
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import CustomDesign from '@/components/CustomDesign';
import { mapState, mapMutations } from 'vuex';

export default {
  name: 'c_custom_component',
  componentsName: 'home_custom_component',
  components: {
    ...toolCom,
    rightBtn,
    CustomDesign,
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
      rCom: [],
      setUp: 0,
      type: 'user',
      modals: false,
    };
  },
  computed: {
    columnNum() {
      let styleConfig = null;
      if (this.type === 'article') styleConfig = this.configObj.articleColumnStyle;
      else if (this.type === 'goods') styleConfig = this.configObj.goodsColumnStyle;
      else if (this.type === 'coupon') styleConfig = this.configObj.couponColumnStyle;

      if (styleConfig) {
        return (styleConfig.tabVal || 0) + 1;
      }
      return 1;
    },
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
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.selectType.activeValue': {
      handler(nVal, oVal) {
        this.type = nVal;
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.couponUserType.activeValue': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.goodsDataSource.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.couponDataSource.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.couponThreshold.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
      },
      deep: true,
    },
    'configObj.articleDataSource.tabVal': {
      handler(nVal, oVal) {
        this.updateRCom();
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
      // Ensure structure exists if missing
      if (!data.setUp) {
        this.$set(data, 'setUp', { tabVal: 0 });
      }
      if (!data.customBtnConfig) {
        this.$set(data, 'customBtnConfig', {
          title: '设计组件',
        });
      }
      if (!data.fillet) {
        this.$set(data, 'fillet', {
          title: '背景圆角',
          type: 0,
          list: [
            {
              val: '全部',
              icon: 'iconcaozuo-zhengti',
            },
            {
              val: '单个',
              icon: 'iconcaozuo-bianjiao',
            },
          ],
          valName: '圆角值',
          val: 0,
          min: 0,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
      }
      if (!data.filletDataConfig) {
        this.$set(data, 'filletDataConfig', {
          title: '背景圆角',
          type: 0,
          list: [
            {
              val: '全部',
              icon: 'iconcaozuo-zhengti',
            },
            {
              val: '单个',
              icon: 'iconcaozuo-bianjiao',
            },
          ],
          valName: '圆角值',
          val: 0,
          min: 0,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
      }
      if (!data.componentBgConfig) {
        this.$set(data, 'componentBgConfig', {
          title: '背景样式',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            color: [
              {
                item: '#fff',
              },
            ],
            default: [
              {
                item: '#fff',
              },
            ],
          },
          imageConfig: {
            title: '背景图片',
            url: '',
          },
          colorDirection: {
            title: '渐变方向',
            tabVal: 0,
            tabList: [{ name: '横向' }, { name: '纵向' }, { name: '左斜' }, { name: '右斜' }],
          },
        });
      }
      if (!data.componentBgDataConfig) {
        this.$set(data, 'componentBgDataConfig', {
          title: '背景样式',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            color: [
              {
                item: '#fff',
              },
            ],
            default: [
              {
                item: '#fff',
              },
            ],
          },
          imageConfig: {
            title: '背景图片',
            url: '',
          },
          colorDirection: {
            title: '渐变方向',
            tabVal: 0,
            tabList: [{ name: '横向' }, { name: '纵向' }, { name: '左斜' }, { name: '右斜' }],
          },
        });
      }
      if (!data.marginConfig) {
        this.$set(data, 'marginConfig', {
          title: '边距设置',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
      }
      if (!data.marginDataConfig) {
        this.$set(data, 'marginDataConfig', {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
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
      if (!data.paddingDataConfig) {
        this.$set(data, 'paddingDataConfig', {
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        });
      }
      if (!data.borderConfig) {
        this.$set(data, 'borderConfig', {
          title: '边框设置',
          tabVal: 0,
          tabList: [
            {
              name: '隐藏',
            },
            {
              name: '显示',
            },
          ],
          val: 0,
          styleConfig: {
            title: '边框样式',
            tabVal: 0,
            tabList: [
              {
                name: '实线',
                style: 'solid',
              },
              {
                name: '虚线',
                style: 'dashed',
              },
              {
                name: '点状',
                style: 'dotted',
              },
            ],
          },
          widthConfig: {
            title: '边框粗细',
            val: 1,
            min: 1,
          },
          colorConfig: {
            title: '边框颜色',
            default: [
              {
                item: '#e5e5e5',
              },
            ],
            color: [
              {
                item: '#e5e5e5',
              },
            ],
          },
        });
      }
      if (!data.borderDataConfig) {
        this.$set(data, 'borderDataConfig', {
          title: '边框设置',
          tabVal: 0,
          tabList: [
            {
              name: '隐藏',
            },
            {
              name: '显示',
            },
          ],
          val: 0,
          styleConfig: {
            title: '边框样式',
            tabVal: 0,
            tabList: [
              {
                name: '实线',
                style: 'solid',
              },
              {
                name: '虚线',
                style: 'dashed',
              },
              {
                name: '点状',
                style: 'dotted',
              },
            ],
          },
          widthConfig: {
            title: '边框粗细',
            val: 1,
            min: 1,
          },
          colorConfig: {
            title: '边框颜色',
            default: [
              {
                item: '#e5e5e5',
              },
            ],
            color: [
              {
                item: '#e5e5e5',
              },
            ],
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
      if (!data.shadowDataConfig) {
        this.$set(data, 'shadowDataConfig', {
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
      if (!data.bottomBgColor) {
        this.$set(data, 'bottomBgColor', {
          title: '底部背景',
          color: [
            {
              item: '#f5f5f5',
            },
          ],
          default: [
            {
              item: '#f5f5f5',
            },
          ],
        });
      }
      if (!data.selectType) {
        this.$set(data, 'selectType', {
          title: '选择信息',
          activeValue: 'user',
          list: [
            { activeValue: 'user', title: '用户' },
            { activeValue: 'article', title: '文章' },
            { activeValue: 'coupon', title: '优惠券' },
            { activeValue: 'goods', title: '商品' },
          ],
        });
      }
      if (!data.couponUserType) {
        this.$set(data, 'couponUserType', {
          title: '用户类型',
          activeValue: '',
          list: [
            { activeValue: '', title: '全部' },
            { activeValue: '1', title: '普通用户' },
            { activeValue: '2', title: '会员用户' },
          ],
        });
      }
      return data;
    },
    updateRCom() {
      if (this.setUp === 0) {
        // Content Config
        let arr = [
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
          {
            components: toolCom.c_title,
            configNme: 'messageTitle',
          },
          {
            components: toolCom.c_select,
            configNme: 'selectType',
          },
        ];

        if (this.type === 'article') {
          arr = arr.concat([
            { components: toolCom.c_radio, configNme: 'articleDisplayMode' },
            { components: toolCom.c_radio, configNme: 'articleColumnStyle' },
            {
              components: toolCom.c_title,
              configNme: 'dataTitle',
            },
            { components: toolCom.c_radio, configNme: 'articleDataSource' },
          ]);

          if (this.configObj.articleDataSource && this.configObj.articleDataSource.tabVal === 0) {
            // Specific Data
            arr.push({ components: toolCom.c_article, configNme: 'articleList' });
          } else if (this.configObj.articleDataSource && this.configObj.articleDataSource.tabVal === 1) {
            // Filtered Data
            arr.push(
              { components: toolCom.c_classify, configNme: 'articleClass' },
              { components: toolCom.c_input_number, configNme: 'articleNum' },
              { components: toolCom.c_radio, configNme: 'articleSort' },
              { components: toolCom.c_radio, configNme: 'articleSortRule' },
            );
            arr.push({ components: toolCom.c_input_number, configNme: 'articleNum' });
          }
        } else if (this.type === 'coupon') {
          arr = arr.concat([
            { components: toolCom.c_radio, configNme: 'couponDisplayMode' },
            { components: toolCom.c_radio, configNme: 'couponColumnStyle' },
            {
              components: toolCom.c_title,
              configNme: 'dataTitle',
            },
            { components: toolCom.c_radio, configNme: 'couponDataSource' },
          ]);

          if (this.configObj.couponDataSource && this.configObj.couponDataSource.tabVal === 0) {
            // Manual Selection
            arr.push({ components: toolCom.c_coupon_select, configNme: 'couponList' });
          } else if (this.configObj.couponDataSource && this.configObj.couponDataSource.tabVal === 1) {
            // Filtered Data
            arr.push(
              { components: toolCom.c_select, configNme: 'couponType' },
              { components: toolCom.c_select, configNme: 'couponUserType' },
            );
            if (this.configObj.couponUserType && this.configObj.couponUserType.activeValue != 2) {
              arr.push({ components: toolCom.c_select, configNme: 'couponSendType' });
            }
            arr.push({ components: toolCom.c_radio, configNme: 'couponThreshold' });
            if (this.configObj.couponThreshold && this.configObj.couponThreshold.tabVal === 1) {
              arr.push({ components: toolCom.c_input_number, configNme: 'couponThresholdValue' });
            }
            arr.push(
              { components: toolCom.c_datetime_picker, configNme: 'couponTime' },
              { components: toolCom.c_radio, configNme: 'couponSort' },
              { components: toolCom.c_radio, configNme: 'couponSortRule' },
            );
          }

          // arr.push({ components: toolCom.c_input_number, configNme: 'couponNum' });
        } else if (this.type === 'goods') {
          arr = arr.concat([
            { components: toolCom.c_radio, configNme: 'goodsDisplayMode' },
            { components: toolCom.c_radio, configNme: 'goodsColumnStyle' },
            {
              components: toolCom.c_title,
              configNme: 'dataTitle',
            },
            { components: toolCom.c_radio, configNme: 'goodsDataSource' },
          ]);

          if (this.configObj.goodsDataSource && this.configObj.goodsDataSource.tabVal === 0) {
            // Specific Data
            arr.push({ components: toolCom.c_goods, configNme: 'goodsList' });
          } else if (this.configObj.goodsDataSource && this.configObj.goodsDataSource.tabVal === 1) {
            // Category Data
            arr.push(
              { components: toolCom.c_classify, configNme: 'goodsClass' },
              { components: toolCom.c_radio, configNme: 'goodsSort' },
              { components: toolCom.c_radio, configNme: 'goodsSortRule' },
            );
            arr.push({ components: toolCom.c_input_number, configNme: 'goodsNum' });
          }
        }

        arr.push(
          {
            components: toolCom.c_title,
            configNme: 'designTitle',
          },
          { components: toolCom.c_custom_btn, configNme: 'customBtnConfig' },
        );

        this.rCom = arr;
      } else {
        // Style Config
        this.rCom = [
          {
            components: toolCom.c_set_up,
            configNme: 'setUp',
          },
          {
            components: toolCom.c_data_style,
            configNme: 'c_data_style',
          },
          {
            components: toolCom.c_title,
            configNme: 'commonTitle',
          },
          {
            components: toolCom.c_common_style,
            configNme: 'c_common_style',
          },
          // Can add padding/margin config here if c_slider/c_margin_style is available and configured
        ];
      }
    },
    getConfig(data) {
      if ((data && data.name) == 'custom_btn_click') {
        this.modals = true;
      }
    },
    handleSave(data) {
      this.$set(this.configObj, 'customComponents', data);
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-config {
  padding-bottom: 20px;
}
::v-deep .custom-design-dialog {
  border-radius: 0px !important;
  .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    padding: 0 !important;
    height: 100%;
    overflow: hidden;
    max-height: 100vh !important;
  }
}
</style>
