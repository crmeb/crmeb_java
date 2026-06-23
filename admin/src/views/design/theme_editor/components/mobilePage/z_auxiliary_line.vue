<template>
  <common_wrapper :config="configObj">
    <div
      class="box"
      :style="{
        borderBottomColor: lineColor,
        borderBottomStyle: style,
        borderBottomWidth: `${configObj && configObj.heightConfig.val}px`,
      }"
    ></div>
  </common_wrapper>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
export default {
  name: 'z_auxiliary_line',
  cname: '辅助线',
  configName: 'c_auxiliary_line',
  icon: '#iconzujian-fuzhuxian',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'guide', // 外面匹配名称
  props: {
    index: {
      type: null,
      default: -1,
    },
    num: {
      type: null,
    },
  },
  computed: {
    ...mapState('mobildConfig', ['defaultArray']),
  },
  watch: {
    pageData: {
      handler(nVal, oVal) {
        this.setConfig(nVal);
      },
      deep: true,
    },
    num: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[nVal];
        this.setConfig(data);
      },
      deep: true,
    },
    defaultArray: {
      handler(nVal, oVal) {
        let data = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(data);
      },
      deep: true,
    },
  },
  data() {
    return {
      // 默认初始化数据禁止修改
      defaultConfig: {
        cname: '辅助线',
        name: 'guide',
        timestamp: this.num,
        isHide: false,
        setUp: {
          tabVal: 0,
        },
        titleLeft: '展示设置',
        titleRight: '线条样式',
        titleCurrent: '通用样式',
        zIndexConfig: {
          title: '组件上浮',
          val: 0,
          min: 0,
        },
        lineColor: {
          title: '线条颜色',
          default: [
            {
              item: '#f5f5f5',
            },
          ],
          color: [
            {
              item: '#f5f5f5',
            },
          ],
        },
        lineBgColor: {
          title: '底部背景',
          default: [
            {
              item: '#E93323',
            },
          ],
          color: [
            {
              item: '#E93323',
            },
          ],
        },
        lineStyle: {
          title: '选择样式',
          tabVal: 1,
          tabList: [
            {
              name: '虚线',
              style: 'dashed',
            },
            {
              name: '实线',
              style: 'solid',
            },
            {
              name: '点状线',
              style: 'dotted',
            },
          ],
        },
        paddingConfig: {
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        borderConfig: {
          title: '边框设置',
          tabVal: 0,
          tabList: [{ name: '隐藏' }, { name: '显示' }],
          val: 0, // 0: Hide, 1: Show
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
        },
        heightConfig: {
          title: '线条高度',
          val: 10,
          min: 1,
        },
        shadowConfig: {
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
        },
        componentBgConfig: {
          title: '背景设置',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [{ item: '#F5F5F5' }, { item: '#F5F5F5' }],
            color: [{ item: '#F5F5F5' }, { item: '#F5F5F5' }],
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
        },
        marginConfig: {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
      },
      configObj: null,
      bgColor: '',
      confObj: {},
      pageData: {},
      edge: '',
      udEdge: '',
      topConfig: '',
      bottomConfig: '',
      style: '',
      lineColor: '',
      paddingConfig: {
        title: '内边距',
        val: 0,
        min: 0,
        max: 100,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },
      marginConfig: {
        title: '外边距',
        val: 0,
        min: 0,
        max: 100,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.pageData = this.$store.state.mobildConfig.defaultArray[this.num];
      this.setConfig(this.pageData);
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      this.configObj = data;
      this.$set(this.configObj, 'bottomBgColor', data.lineBgColor);
      let styleType = data.lineStyle.tabVal;
      this.bgColor = data.lineBgColor.color[0].item;
      this.lineColor = data.lineColor.color[0].item;
      this.style = data.lineStyle.tabList[styleType].style;

      for (let key in this.defaultConfig) {
        if (this.configObj[key] === undefined) {
          this.$set(this.configObj, key, this.defaultConfig[key]);
        }
      }

      if (!data.paddingConfig) {
        data.paddingConfig = {
          isAll: false,
          valList: [
            { val: data.topConfig ? data.topConfig.val : 0 },
            { val: data.lrEdge ? data.lrEdge.val : 0 },
            { val: data.bottomConfig ? data.bottomConfig.val : 0 },
            { val: data.lrEdge ? data.lrEdge.val : 0 },
          ],
        };
      }

      if (!data.marginConfig) {
        data.marginConfig = {
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: data.mbConfig ? data.mbConfig.val : 0 }, { val: 0 }],
        };
      }
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-page {
  padding: 7px 0;
  display: inline-block;
  width: -webkit-fill-available;
}
</style>
