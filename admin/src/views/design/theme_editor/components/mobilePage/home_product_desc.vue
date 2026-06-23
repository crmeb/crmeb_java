<template>
  <common_wrapper :config="configObj">
    <div class="product-desc-box">
      <div
        class="title"
        v-if="titleShow"
        :style="{
          color: titleColor,
          fontSize: titleSize + 'px',
          textAlign: titleAlign,
        }"
      >
        产品介绍
      </div>
      <div class="desc">此模块暂无内容设置，仅支持通用样式设置</div>
    </div>
  </common_wrapper>
</template>

<script>
import { mapState } from 'vuex';
export default {
  name: 'home_product_desc',
  cname: '产品介绍',
  configName: 'c_product_desc',
  icon: '#iconzujian-wenzhangliebiao',
  type: 3,
  defaultName: 'productDesc',
  props: {
    index: {
      type: null,
    },
    num: {
      type: null,
    },
  },
  computed: {
    ...mapState('mobildConfig', ['defaultArray']),
    titleShow() {
      return (this.configObj && this.configObj.isShow && this.configObj.isShow.tabVal) == 0;
    },
    titleColor() {
      return (this.configObj && this.configObj.textColor && this.configObj.textColor.color && this.configObj.textColor.color[0] && this.configObj.textColor.color[0].item) || '#333';
    },
    titleSize() {
      return (this.configObj && this.configObj.fontSize && this.configObj.fontSize.val) || 16;
    },
    titleAlign() {
      return (this.configObj && this.configObj.textPosition && this.configObj.textPosition.val) || 'left';
    },
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
      configObj: null,
      defaultConfig: {
        cname: '产品介绍',
        name: 'productDesc',
        timestamp: this.num,
        contentTitle: '内容设置',
        titleStyle: '标题样式',
        setUp: {
          tabVal: 0,
        },
        isShow: {
          title: '显示标题',
          tabVal: 0,
          tabList: [{ name: '显示' }, { name: '隐藏' }],
        },

        textPosition: {
          title: '对齐方式',
          val: 'center',
        },
        textColor: {
          title: '文字颜色',
          default: [{ item: '#333' }],
          color: [{ item: '#333' }],
        },
        fontSize: {
          title: '字体大小',
          val: 16,
          min: 12,
          max: 40,
        },
        titleCurrency: '通用样式',
        moduleColor: {
          title: '背景颜色',
          name: 'moduleColor',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        },
        bottomBgColor: {
          title: '底部背景',
          name: 'bottomBgColor',
          default: [{ item: '#fff' }],
          color: [{ item: '#fff' }],
        },
        marginConfig: {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        paddingConfig: {
          title: '内边距',
          val: 10,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 10 }, { val: 10 }, { val: 10 }, { val: 10 }],
        },
        componentBgConfig: {
          title: '组件背景',
          tabVal: 0,
          colorConfig: {
            title: '颜色设置',
            default: [{ item: '#fff' }],
            color: [{ item: '#fff' }],
          },
          imageConfig: {
            title: '图片设置',
            url: '',
          },
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
        shadowConfig: {
          title: '阴影设置',
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
          colorConfig: {
            title: '阴影颜色',
            default: [
              {
                item: 'rgba(0,0,0,0.1)',
              },
            ],
            color: [
              {
                item: 'rgba(0,0,0,0.1)',
              },
            ],
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
        fillet: {
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
        },
        borderRadius: '0',
        zIndexConfig: {
          title: '层级',
          val: 0,
          min: 0,
        },
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
      if (data) {
        this.configObj = data;
      }
    },
  },
};
</script>

<style scoped>
.product-desc-box {
  text-align: center;
}
.title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}
.desc {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}
</style>
