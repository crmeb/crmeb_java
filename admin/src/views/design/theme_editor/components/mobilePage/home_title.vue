<template>
  <common_wrapper :config="configObj">
    <div class="mobile-page">
      <div
        class="title"
        :style="{
          background: `linear-gradient(90deg,${titleColorLeft} 0%,${titleColorRight} 100%)`,
          borderRadius: fillet
            ? valList[0].val + 'px ' + valList[1].val + 'px ' + valList[3].val + 'px ' + valList[2].val + 'px'
            : filletVal + 'px',
        }"
      >
        <div
          class="title-box"
          :class="buttonConfig ? 'on' : ''"
          :style="{
            color: themeColor,
            fontSize: fontSize + 'px',
            fontStyle: txtStyle != 'bold' ? txtStyle : '',
            fontWeight: txtStyle == 'bold' ? txtStyle : '',
            textAlign: txtPosition,
          }"
        >
          {{ titleTxt }}
        </div>
        <div
          v-if="!buttonConfig"
          :style="{
            color: buttonColor,
            fontSize: buttonSize + 'px',
          }"
        >
          {{ buttonTitle }}<span class="iconfont iconjinru"></span>
        </div>
      </div>
    </div>
  </common_wrapper>
</template>

<script>
import { mapState } from 'vuex';
export default {
  name: 'home_title',
  cname: '文本标题',
  icon: '#iconzujian-biaoti',
  configName: 'c_home_title',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'titles', // 外面匹配名称
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
        cname: '文本标题',
        name: 'titles',
        timestamp: this.num,
        isHide: false,
        setUp: {
          tabVal: 0,
        },
        titleLeft: '标题设置',
        titleRight: '文字设置',
        titleCurrency: '通用样式',
        zIndexConfig: {
          title: '组件上浮',
          val: 0,
          min: 0,
        },
        titleConfig: {
          title: '标题名称',
          value: '标题',
          place: '请输入标题',
          max: 10,
        },
        titleConfigRight: {
          title: '右侧文字',
          value: '更多',
          place: '请输入右侧文字',
          max: 5,
        },
        buttonConfig: {
          title: '右侧按钮',
          tabVal: 0,
          tabList: [
            {
              name: '显示',
            },
            {
              name: '隐藏',
            },
          ],
        },
        linkConfig: {
          title: '链接',
          value: '',
          place: '请输入链接地址',
          max: 100,
          type: 'link',
        },
        themeColor: {
          title: '标题颜色',
          name: 'themeColor',
          default: [
            {
              item: '#333333',
            },
          ],
          color: [
            {
              item: '#333333',
            },
          ],
        },
        buttonColor: {
          title: '按钮颜色',
          default: [
            {
              item: '#999999',
            },
          ],
          color: [
            {
              item: '#999999',
            },
          ],
        },
        moduleColor: {
          title: '组件背景',
          default: [
            {
              item: '#fff',
            },
            {
              item: '#fff',
            },
          ],
          color: [
            {
              item: '#fff',
            },
            {
              item: '#fff',
            },
          ],
        },
        buttonText: {
          title: '按钮文字',
          val: 12,
          min: 6,
        },
        textPosition: {
          title: '标题位置',
          tabVal: 0,
          tabList: [
            {
              name: '左对齐',
              style: 'left',
              icon: 'icondoc_left',
            },
            {
              name: '居中对齐',
              style: 'center',
              icon: 'icondoc_center',
            },
            {
              name: '右对齐',
              style: 'right',
              icon: 'icondoc_right',
            },
          ],
        },
        textStyle: {
          title: '标题样式',
          tabVal: 0,
          tabList: [
            {
              name: '正常',
              style: 'normal',
              icon: 'icondoc_general',
            },
            {
              name: '倾斜',
              style: 'italic',
              icon: 'icondoc_skew',
            },
            {
              name: '加粗',
              style: 'bold',
              icon: 'icondoc_bold',
            },
          ],
        },
        fontSize: {
          title: '标题文字',
          val: 16,
          min: 8,
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
        paddingConfig: {
          title: '内边距',
          val: 0,
          min: 0,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        marginConfig: {
          title: '外边距',
          val: 0,
          min: 0,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
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
        bottomBgColor: {
          title: '底部背景',
          default: [
            {
              item: '#F5F5F5',
            },
          ],
          color: [
            {
              item: '#F5F5F5',
            },
          ],
        },
      },
      titleTxt: '',
      link: '',
      txtPosition: '',
      txtStyle: '',
      fontSize: 0,
      titleColorLeft: '',
      titleColorRight: '',
      themeColor: '',
      paddingConfig: {
        title: '内边距',
        val: 0,
        max: 100,
        isAll: false,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },
      marginConfig: {
        title: '外边距',
        val: 0,
        max: 100,
        isAll: false,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },

      pageData: {},
      bottomBgColor: '',
      buttonConfig: 0,
      buttonTitle: '',
      buttonColor: '',
      buttonSize: 0,
      fillet: 0,
      filletVal: 0,
      valList: [],
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
      for (let key in this.defaultConfig) {
        if (data[key] == undefined) {
          this.$set(data, key, this.defaultConfig[key]);
        }
      }
      if (data.id) {
        this.titleTxt = data.titleConfig.value;
        this.link = data.linkConfig.value;
        this.txtPosition = data.textPosition.tabList[data.textPosition.tabVal].style;
        this.txtStyle = data.textStyle.tabList[data.textStyle.tabVal].style;
        this.themeColor = data.themeColor.color[0].item;
        this.fontSize = data.fontSize.val;
        this.titleColorLeft = data.moduleColor.color[0].item;
        this.titleColorRight = data.moduleColor.color[1].item;
        this.bottomBgColor = data.bottomBgColor.color[0].item;
        this.buttonConfig = data.buttonConfig.tabVal;
        this.buttonTitle = data.titleConfigRight.value;
        this.buttonColor = data.buttonColor.color[0].item;
        this.buttonSize = data.buttonText.val;
        this.fillet = data.fillet.type;
        this.filletVal = data.fillet.val;
        this.valList = data.fillet.valList;
        this.marginConfig = data.marginConfig || {
          val: 0,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        };

        if (!data.marginConfig) {
          if (data.mbConfig) this.marginConfig.valList[0].val = data.mbConfig.val;
        }
      }
    },
  },
};
</script>

<style scoped lang="scss">
.title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.titleOn {
  border-radius: 10px !important;
}
.title {
  padding: 13px 12px;
  .title-box {
    &.on {
      width: 100%;
    }
  }
}
.iconfont {
  font-size: 14px;
}
</style>
