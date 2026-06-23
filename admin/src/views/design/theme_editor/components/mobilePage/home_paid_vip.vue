<template>
  <common_wrapper :config="configObj">
    <div class="vip-container" :style="[containerStyle]">
      <div class="left-content">
        <img :src="imgUrl" class="vip-icon" v-if="imgUrl" />
        <div class="text-content" :style="{ color: tipsTextColor }">
          开通SVIP会员预计省 <span :style="{ color: moneyTextColor }">2.90</span> 元
        </div>
      </div>
      <div class="right-content">
        <div class="open-btn" :style="[btnStyle]">
          {{ rightBntText }}
          <span class="iconfont iconjinru"></span>
        </div>
      </div>
    </div>
  </common_wrapper>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'home_paid_vip',
  cname: '付费会员',
  configName: 'c_paid_vip',
  icon: '#iconzujian-fufeihuiyuan',
  type: 3, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'home_paid_vip', // 外面匹配名称
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
    // Container Style (Page Spacing, Page Background)
    // Component Style (Background, Radius)
    containerStyle() {
      return {};
    },
    // Button Style
    btnStyle() {
      return {
        color: this.buttonColor,
        fontSize: this.buttonFontSize + 'px',
      };
    },
    imgUrl() {
      return this.imgConfig.url;
    },
    rightBntText() {
      return this.rightBntConfig.value;
    },
    // Colors
    tipsTextColor() {
      return this.isCustomTone ? this.tipsColor : '#333333';
    },
    moneyTextColor() {
      return this.isCustomTone ? this.moneyColor : '#F62C2C';
    },
    buttonColor() {
      return this.isCustomTone ? this.btnColor : '#FF6B00';
    },
    buttonFontSize() {
      return this.configObj && this.configObj.btnConfig ? this.configObj.btnConfig.val : 13;
    },
    isCustomTone() {
      return this.configObj && this.configObj.toneConfig && this.configObj.toneConfig.tabVal === 1;
    },
    // Config mappings
    imgConfig() {
      return this.configObj && this.configObj.imgConfig ? this.configObj.imgConfig : {};
    },
    rightBntConfig() {
      return this.configObj && this.configObj.rightBntConfig ? this.configObj.rightBntConfig : {};
    },
    // Common Style Mappings
    bottomBgColor() {
      return this.configObj &&
        this.configObj.bottomBgColor &&
        this.configObj.bottomBgColor.color &&
        this.configObj.bottomBgColor.color[0]
        ? this.configObj.bottomBgColor.color[0].item
        : '#FFF0D1';
    },
    filletVal() {
      if (!this.configObj) return '0px';
      const fillet = this.configObj.fillet;
      if (!fillet) return '0px';
      if (fillet.type === 0) {
        return fillet.val + 'px';
      } else {
        return fillet.valList.map((item) => item.val + 'px').join(' ');
      }
    },
    // Text Style Colors
    tipsColor() {
      return this.configObj &&
        this.configObj.tipsColor &&
        this.configObj.tipsColor.color &&
        this.configObj.tipsColor.color[0]
        ? this.configObj.tipsColor.color[0].item
        : '#333333';
    },
    moneyColor() {
      return this.configObj &&
        this.configObj.moneyColor &&
        this.configObj.moneyColor.color &&
        this.configObj.moneyColor.color[0]
        ? this.configObj.moneyColor.color[0].item
        : '#F62C2C';
    },
    btnColor() {
      return this.configObj &&
        this.configObj.btnColor &&
        this.configObj.btnColor.color &&
        this.configObj.btnColor.color[0]
        ? this.configObj.btnColor.color[0].item
        : '#FF6B00';
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
      paddingConfig: null,
      marginConfig: null,
      borderConfig: null,
      shadowConfig: null,
      componentBgConfig: null,
      // 默认初始化数据禁止修改
      defaultConfig: {
        cname: '付费会员',
        name: 'home_paid_vip',
        timestamp: this.num,
        setUp: {
          tabVal: 0,
        },
        // Content Settings
        titleContent: '内容设置',
        imgConfig: {
          info: '建议：36px * 36px',
          url: require('@/assets/images/goods_vip.png'),
          type: 'code',
          delType: 0,
          name: '会员图片',
        },
        rightBntConfig: {
          title: '右侧按钮',
          value: '立即开通',
          place: '请输入按钮文字',
          max: 6,
        },

        // Style Settings - Text
        titleStyle: '文字设置',
        toneConfig: {
          title: '色调',
          tabVal: 0,
          tabList: [{ name: '默认' }, { name: '自定义' }],
        },
        tipsColor: {
          title: '提示文字',
          default: [{ item: '#333333' }],
          color: [{ item: '#333333' }],
        },
        moneyColor: {
          title: '金额文字',
          default: [{ item: '#F62C2C' }],
          color: [{ item: '#F62C2C' }],
        },
        btnColor: {
          title: '按钮颜色',
          default: [{ item: '#FF6B00' }],
          color: [{ item: '#FF6B00' }],
        },
        btnConfig: {
          title: '按钮字号',
          val: 13,
          min: 12,
        },

        // Style Settings - Common
        titleCurrency: '通用样式',
        componentBgColor: {
          title: '组件背景',
          default: [{ item: '#FFF0D1' }],
          color: [{ item: '#FFF0D1' }],
        },
        paddingConfig: {
          title: '内边距',
          val: 15,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 15 }, { val: 15 }, { val: 15 }, { val: 15 }],
        },
        marginConfig: {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        zIndexConfig: {
          title: '组件上浮',
          val: 0,
          min: 0,
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
        bottomBgColor: {
          title: '底部背景',
          default: [
            {
              item: '#fff',
            },
          ],
          color: [
            {
              item: '#fff',
            },
          ],
        },
        componentBgConfig: {
          title: '背景设置',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [{ item: '#FFF0D1' }, { item: '#FFF0D1' }],
            color: [{ item: '#FFF0D1' }, { item: '#FFF0D1' }],
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
        fillet: {
          title: '背景圆角',
          type: 0,
          list: [
            { val: '全部', icon: 'iconcaozuo-zhengti' },
            { val: '单个', icon: 'iconcaozuo-bianjiao' },
          ],
          valName: '圆角值',
          val: 8,
          min: 0,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
      },
    };
  },
  mounted() {
    this.$nextTick(() => {
      if (this.num) {
        let data = this.$store.state.mobildConfig.defaultArray[this.num];
        this.setConfig(data);
      }
    });
  },
  methods: {
    setConfig(data) {
      if (!data) return;
      let dataClone = JSON.parse(JSON.stringify(data));

      // Backward compatibility for legacy configs
      if (!dataClone.paddingConfig) {
        dataClone.paddingConfig = {
          title: '内边距',
          val: 15,
          min: 0,
          max: 100,
          valList: [{ val: 15 }, { val: 15 }, { val: 15 }, { val: 15 }],
        };
        if (dataClone.topConfig) dataClone.paddingConfig.valList[0].val = dataClone.topConfig.val;
        if (dataClone.bottomConfig) dataClone.paddingConfig.valList[2].val = dataClone.bottomConfig.val;
        if (dataClone.prConfig) {
          dataClone.paddingConfig.valList[1].val = dataClone.prConfig.val;
          dataClone.paddingConfig.valList[3].val = dataClone.prConfig.val;
        }
      }
      if (!dataClone.marginConfig) {
        dataClone.marginConfig = {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        };
        if (dataClone.mbConfig) dataClone.marginConfig.valList[0].val = dataClone.mbConfig.val;
      }

      for (let key in this.defaultConfig) {
        if (dataClone[key] === undefined) {
          this.$set(dataClone, key, JSON.parse(JSON.stringify(this.defaultConfig[key])));
        }
      }

      this.configObj = dataClone;
      this.paddingConfig = dataClone.paddingConfig;
      this.marginConfig = dataClone.marginConfig;
      this.borderConfig = dataClone.borderConfig;
      this.shadowConfig = dataClone.shadowConfig;
      this.componentBgConfig = dataClone.componentBgConfig;
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-page {
  display: inline-block;
  width: -webkit-fill-available;
  overflow: hidden;
}
.vip-container {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .left-content {
    display: flex;
    align-items: center;

    .vip-icon {
      height: 18px;
      margin-right: 8px;
    }

    .text-content {
      font-size: 13px;
      font-weight: 400;
    }
  }

  .right-content {
    .open-btn {
      display: flex;
      align-items: center;
      font-weight: 500;
      cursor: pointer;

      .iconjinru {
        font-size: 12px;
        margin-left: 2px;
      }
    }
  }
}
</style>
