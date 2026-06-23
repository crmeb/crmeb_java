<template>
  <common_wrapper :config="configObj">
    <div
      class="pointsMall"
      :style="{
        borderRadius: bgRadius,
        overflow: 'hidden',
      }"
    >
      <div
        class="title acea-row row-between-wrapper"
        :style="
          styleConfig
            ? 'backgroundImage:url(' + imgBgUrl + ')'
            : `background:linear-gradient(90deg,${headerBgColorLeft} 0%,${headerBgColorRight} 100%)`
        "
      >
        <div
          v-if="titleConfig"
          :style="
            (titleTabVal == 2 ? 'fontStyle:' : 'fontWeight:') +
            titleText +
            ';color:' +
            titleColor +
            ';fontSize:' +
            titleNumber +
            'px;'
          "
        >
          {{ titleTxtConfig }}
        </div>
        <img v-else :src="styleConfig ? imgUrl : imgColorUrl" alt="" />
        <div
          class="more"
          :style="{
            color: styleConfig ? headerBntColor : headerBntColor2,
            fontSize: bntNumber + 'px',
          }"
        >
          {{ rightBntTxt
          }}<span
            class="iconfont iconjinru"
            :style="{
              fontSize: bntNumber + 'px',
            }"
          ></span>
        </div>
      </div>
      <div
        class="conter"
        v-if="goodStyleConfig == 0"
        :style="{
          background: styleConfig ? bgColor : bgColor2,
          borderRadius: bgRadius2,
        }"
      >
        <div class="list">
          <div class="item" v-for="(item, index) in numberConfig" :key="index">
            <div
              class="pictrue acea-row row-center-wrapper"
              :style="{
                borderRadius: imgRadius,
              }"
            >
              <img src="../../assets/images/shan.png" />
            </div>
            <div
              class="bottom"
              :style="{
                color: toneConfig ? goodsPriceColor : '#fff',
                background: toneConfig
                  ? `linear-gradient(90deg,${priceBgColorRight} 0%,${priceBgColorLeft} 100%)`
                  : themeColor,
              }"
            >
              68880积分
            </div>
          </div>
        </div>
      </div>
      <div
        class="list on"
        v-else-if="goodStyleConfig == 1"
        :style="{
          background: styleConfig ? bgColor : bgColor2,
          borderRadius: bgRadius2,
        }"
      >
        <div class="item" v-for="(item, index) in numberConfig" :key="index">
          <div
            class="pictrue acea-row row-center-wrapper"
            :style="{
              borderRadius: imgRadius,
            }"
          >
            <img src="../../assets/images/shan.png" />
          </div>
          <div class="money acea-row row-middle">
            <img src="../../assets/images/points.png" /><span
              class="num"
              :style="{
                color: !toneConfig
                  ? styleConfig
                    ? '#fff'
                    : colorStyle.theme
                  : styleConfig
                  ? goodsPriceColor
                  : goodsPriceColor2,
              }"
              >6888</span
            >
          </div>
          <div
            class="name"
            :style="{
              color: styleConfig ? goodsNameColor2 : goodsNameColor,
            }"
          >
            小米蓝牙耳机...
          </div>
        </div>
      </div>
      <div
        class="list on2"
        v-else
        :style="{
          background: styleConfig ? bgColor : bgColor2,
          borderRadius: bgRadius2,
        }"
      >
        <div class="item" v-for="(item, index) in numberConfig" :key="index">
          <div
            class="pictrue acea-row row-center-wrapper"
            :style="{
              borderRadius: imgRadius,
            }"
          >
            <img src="../../assets/images/shan.png" />
          </div>
          <div
            class="name"
            :style="{
              color: styleConfig ? goodsNameColor2 : goodsNameColor,
            }"
          >
            小米蓝牙耳机你值得拥有
          </div>
          <div class="money acea-row row-middle">
            <img src="../../assets/images/points.png" /><span
              class="num on"
              :style="{
                color: !toneConfig
                  ? styleConfig
                    ? '#fff'
                    : colorStyle.theme
                  : styleConfig
                  ? goodsPriceColor
                  : goodsPriceColor2,
              }"
              >6888</span
            >
          </div>
        </div>
      </div>
    </div>
  </common_wrapper>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
// import theme from "@/views/design/theme_editor/mixins/theme";
import Setting from '@/utils/settingMer';
export default {
  name: 'points_mall',
  cname: '积分商城',
  configName: 'c_points_mall',
  icon: '#iconzujian-jifenshangcheng',
  type: 1, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'pointsMall', // 外面匹配名称
  props: {
    index: {
      type: null,
      default: -1,
    },
    num: {
      type: null,
    },
    colorStyle: {
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
  // mixins: [theme],
  data() {
    return {
      // 默认初始化数据禁止修改
      configObj: null,
      defaultConfig: {
        cname: '积分商城',
        name: 'pointsMall',
        timestamp: this.num,
        isHide: false,
        setUp: {
          tabVal: 0,
        },
        titleLeft: '头部设置',
        titleGoodsList: '商品列表',
        titleGoods: '商品设置',
        titleRight: '头部样式',
        titleGoodsStyle: '商品样式',
        titleCurrency: '通用样式',
        styleConfig: {
          title: '选择风格',
          tabVal: 1,
          tabList: [
            {
              name: '背景色',
            },
            {
              name: '背景图片',
            },
          ],
        },
        titleConfig: {
          title: '标题类型',
          tabVal: 0,
          tabList: [
            {
              name: '图片',
            },
            {
              name: '文字',
            },
          ],
        },
        imgBgConfig: {
          info: '建议：710px * 96px',
          url: Setting.httpUrl + '/' + 'statics/images/pointsBg.png',
          type: 'code',
          delType: 0,
          name: '背景图片',
        },
        imgConfig: {
          info: '建议：154px * 32px',
          url: require('@/assets/images/points01.png'),
          type: 'code',
          delType: 0,
          name: '标题图片',
        },
        imgConfig2: {
          info: '建议：154px * 32px',
          url: require('@/assets/images/points02.png'),
          type: 'code',
          delType: 0,
          name: '标题图片',
        },
        titleTxtConfig: {
          title: '标题文字',
          value: '积分兑好礼',
          place: '请输入标题文字',
          max: 10,
        },
        rightBntConfig: {
          title: '右侧按钮',
          value: '更多',
          place: '请输入右侧按钮',
          max: 6,
        },
        numberConfig: {
          title: '商品数量',
          val: 3,
          min: 1,
        },
        goodStyleConfig: {
          title: '选择风格',
          tabVal: 0,
          tabList: [
            {
              name: '样式1',
            },
            {
              name: '样式2',
            },
            {
              name: '样式3',
            },
          ],
        },
        headerBgColor: {
          title: '背景颜色',
          name: 'headerBgColor',
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
        titleText: {
          title: '标题文字',
          tabVal: 0,
          tabList: [
            {
              name: '加粗',
              style: 'bold',
            },
            {
              name: '正常',
              style: 'normal',
            },
            {
              name: '倾斜',
              style: 'italic',
            },
          ],
        },
        titleColor: {
          title: '标题颜色',
          name: 'titleColor',
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
        titleNumber: {
          title: '标题字号',
          val: 16,
          min: 0,
        },
        headerBntColor: {
          title: '按钮颜色',
          name: 'headerBntColor',
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
        headerBntColor2: {
          title: '按钮颜色',
          name: 'headerBntColor2',
          default: [
            {
              item: '#999',
            },
          ],
          color: [
            {
              item: '#999',
            },
          ],
        },
        bntNumber: {
          title: '按钮字号',
          val: 12,
          min: 0,
        },
        filletImg: {
          title: '商品圆角',
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
          val: 5,
          min: 0,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        goodsNameColor: {
          title: '商品名称',
          name: 'goodsNameColor',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        goodsNameColor2: {
          title: '商品名称',
          name: 'goodsNameColor2',
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
        goodsUnitPriceColor2: {
          title: '价格单位',
          name: 'goodsUnitPriceColor2',
          default: [
            {
              item: '#282828',
            },
          ],
          color: [
            {
              item: '#282828',
            },
          ],
        },
        goodsUnitPriceColor: {
          title: '价格单位',
          name: 'goodsUnitPriceColor',
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
        toneConfig: {
          title: '色调',
          tabVal: 0,
          tabList: [
            {
              name: '跟随主题风格',
            },
            {
              name: '自定义',
            },
          ],
        },
        goodsPriceColor: {
          title: '商品价格',
          name: 'goodsPriceColor',
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
        goodsPriceColor2: {
          title: '商品价格',
          name: 'goodsPriceColor2',
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
        priceBgColor: {
          title: '价格背景',
          name: 'priceBgColor',
          default: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
          color: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
        },
        moduleColor: {
          title: '组件背景',
          name: 'moduleColor',
          default: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
          color: [
            {
              item: '#FF7931',
            },
            {
              item: '#E93323',
            },
          ],
        },
        moduleColor2: {
          title: '组件背景',
          name: 'moduleColor',
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
        bottomBgColor: {
          title: '底部背景',
          name: 'bgColor',
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
        componentBgConfig: {
          title: '组件背景',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [{ item: '#f5f5f5' }, { item: '#f5f5f5' }],
            color: [{ item: '#f5f5f5' }, { item: '#f5f5f5' }],
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
          val: 0, // 0: Off, 1: On
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
        paddingConfig: {
          title: '内边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        marginConfig: {
          title: '外边距',
          val: 0,
          min: 0,
          max: 100,
          isAll: false,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
        topConfig: {
          title: '上边距',
          val: 0,
          min: 0,
        },
        bottomConfig: {
          title: '下边距',
          val: 0,
          min: 0,
        },
        prConfig: {
          title: '左右边距',
          val: 10,
          min: 0,
        },
        mbConfig: {
          title: '页面上间距',
          val: 0,
          min: 0,
        },
        zIndexConfig: {
          title: '组件上浮',
          val: 0,
          min: 0,
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
          val: 8,
          min: 0,
          valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
        },
      },
      pageData: {},
      bottomBgColor: '',
      paddingConfig: {
        title: '内边距',
        val: 0,
        min: 0,
        max: 100,
        isAll: false,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },
      marginConfig: {
        title: '外边距',
        val: 0,
        min: 0,
        max: 100,
        isAll: false,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },
      topConfig: 0,
      bottomConfig: 0,
      prConfig: 0,
      styleConfig: 0,
      imgBgUrl: 0,
      headerBgColorLeft: '',
      headerBgColorRight: '',
      titleConfig: 0,
      imgUrl: '',
      imgColorUrl: '',
      headerBntColor: '',
      headerBntColor2: '',
      titleTabVal: 0,
      titleText: '',
      titleColor: '',
      titleNumber: 0,
      titleTxtConfig: '',
      rightBntTxt: '',
      numberConfig: 0,
      goodStyleConfig: 0,
      bntNumber: 0,
      imgRadius: 0,
      toneConfig: 0,
      goodsPriceColor: '',
      goodsPriceColor2: '',
      priceBgColorLeft: '',
      priceBgColorRight: '',
      bgColor: '',
      bgColor2: '',
      mTop: 0,
      bgRadius: 0,
      bgRadius2: 0,
      goodsNameColor: '',
      goodsNameColor2: '',
      goodsUnitPriceColor: '',
      goodsUnitPriceColor2: '',
      themeColor: '',
      zIndexConfig: 0,
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
      let dataClone = JSON.parse(JSON.stringify(data));
      for (let key in this.defaultConfig) {
        if (dataClone[key] == undefined) {
          this.$set(dataClone, key, JSON.parse(JSON.stringify(this.defaultConfig[key])));
        }
      }

      if (!data.componentBgConfig && data.bottomBgColor) {
        dataClone.componentBgConfig.colorConfig.color[0].item = data.bottomBgColor.color[0].item;
        dataClone.componentBgConfig.colorConfig.color[1].item = data.bottomBgColor.color[0].item;
      }

      if (!data.paddingConfig) {
        if (dataClone.topConfig) dataClone.paddingConfig.valList[0].val = dataClone.topConfig.val;
        if (dataClone.bottomConfig) dataClone.paddingConfig.valList[2].val = dataClone.bottomConfig.val;
        if (dataClone.prConfig) {
          dataClone.paddingConfig.valList[1].val = dataClone.prConfig.val;
          dataClone.paddingConfig.valList[3].val = dataClone.prConfig.val;
        }
      }
      if (!data.marginConfig) {
        if (dataClone.mbConfig) dataClone.marginConfig.valList[0].val = dataClone.mbConfig.val;
      }
      this.paddingConfig = dataClone.paddingConfig;
      this.marginConfig = dataClone.marginConfig;
      this.zIndexConfig = dataClone.zIndexConfig.val;
      this.configObj = dataClone;

      this.styleConfig = dataClone.styleConfig.tabVal;
      this.imgBgUrl = dataClone.imgBgConfig.url;
      this.headerBgColorLeft = dataClone.headerBgColor.color[0].item;
      this.headerBgColorRight = dataClone.headerBgColor.color[1].item;
      this.titleConfig = dataClone.titleConfig.tabVal;
      this.imgUrl = dataClone.imgConfig.url;
      this.imgColorUrl = dataClone.imgConfig2.url;
      this.headerBntColor = dataClone.headerBntColor.color[0].item;
      this.headerBntColor2 = dataClone.headerBntColor2.color[0].item;
      this.bntNumber = dataClone.bntNumber.val;
      let tabVal = dataClone.titleText.tabVal;
      this.titleTabVal = tabVal;
      this.titleText = dataClone.titleText.tabList[tabVal].style;
      this.titleColor = dataClone.titleColor.color[0].item;
      this.titleNumber = dataClone.titleNumber.val;
      this.titleTxtConfig = dataClone.titleTxtConfig.value;
      this.rightBntTxt = dataClone.rightBntConfig.value;
      this.numberConfig = dataClone.numberConfig.val;
      this.goodStyleConfig = dataClone.goodStyleConfig.tabVal;
      let filletImg = dataClone.filletImg.type;
      let filletValImg = dataClone.filletImg.val;
      let valListImg = dataClone.filletImg.valList;
      this.imgRadius = filletImg
        ? valListImg[0].val + 'px ' + valListImg[1].val + 'px ' + valListImg[3].val + 'px ' + valListImg[2].val + 'px'
        : filletValImg + 'px';
      this.toneConfig = dataClone.toneConfig.tabVal;
      this.goodsPriceColor = dataClone.goodsPriceColor.color[0].item;
      this.goodsPriceColor2 = dataClone.goodsPriceColor2.color[0].item;
      this.priceBgColorLeft = dataClone.priceBgColor.color[0].item;
      this.priceBgColorRight = dataClone.priceBgColor.color[1].item;
      let bgColorLeft = dataClone.moduleColor.color[0].item;
      let bgColorRight = dataClone.moduleColor.color[1].item;
      this.bgColor = `linear-gradient(90deg,${bgColorRight} 0%,${bgColorLeft} 100%)`;
      let bgColorLeft2 = dataClone.moduleColor2.color[0].item;
      let bgColorRight2 = dataClone.moduleColor2.color[1].item;
      this.bgColor2 = `linear-gradient(90deg,${bgColorRight2} 0%,${bgColorLeft2} 100%)`;
      this.bottomBgColor = dataClone.bottomBgColor.color[0].item;
      let fillet = dataClone.fillet.type;
      let filletVal = dataClone.fillet.val;
      let valList = dataClone.fillet.valList;
      this.bgRadius = fillet
        ? valList[0].val + 'px ' + valList[1].val + 'px 0 0'
        : filletVal + 'px ' + filletVal + 'px 0 0';
      this.bgRadius2 = fillet
        ? '0 0 ' + valList[3].val + 'px ' + valList[2].val + 'px'
        : '0 0 ' + filletVal + 'px ' + filletVal + 'px';
      this.goodsNameColor = dataClone.goodsNameColor.color[0].item;
      this.goodsNameColor2 = dataClone.goodsNameColor2.color[0].item;
      this.goodsUnitPriceColor = dataClone.goodsUnitPriceColor.color[0].item;
      this.goodsUnitPriceColor2 = dataClone.goodsUnitPriceColor2.color[0].item;
      this.themeColor = `linear-gradient(90deg,${this.colorStyle.theme} 0%,${this.colorStyle.gradient} 100%)`;
    },
  },
};
</script>

<style scoped lang="scss">
.pointsMall {
  .title {
    font-size: 16px;
    color: #333;
    background-repeat: no-repeat;
    background-size: 100% 100%;
    width: 100%;
    height: 48px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: 500;
    padding: 0 12px;
    img {
      width: 88px;
      height: 16px;
      display: block;
    }
    .more {
      font-size: 12px;
      color: #999;
      .iconfont {
        font-size: 12px;
      }
    }
  }
  .conter {
    padding: 0 0 16px 10px;
    background: linear-gradient(90deg, #e93323 0%, #ff7931 100%);
  }
  .list {
    background-color: #fff;
    padding: 10px 0 10px 10px;
    border-radius: 8px 0 0 8px;
    display: flex;
    overflow: hidden;
    &.on2 {
      flex-wrap: wrap;
      padding-top: 0;
      .item {
        width: 47.4%;
        margin-right: 11px;
        margin-bottom: 10px;
        &:nth-of-type(2n) {
          margin-right: 0;
        }
        &:nth-last-child(1),
        &:nth-last-child(2) {
          margin-bottom: 0;
        }
        .pictrue {
          width: 100%;
          height: 162px;
        }
        .name {
          font-size: 14px;
          margin-top: 5px;
        }
        .money {
          margin-top: 0;
          .num {
            &.on {
              font-size: 16px;
              font-family: D-DIN-PRO, D-DIN-PRO;
              font-weight: 600;
            }
          }
        }
      }
    }
    &.on {
      flex-wrap: wrap;
      padding-top: 0;
      .item {
        width: 30.7%;
        margin-right: 9px;
        margin-bottom: 10px;
        &:nth-last-child(1),
        &:nth-last-child(2),
        &:nth-last-child(3) {
          margin-bottom: 0;
        }
        .pictrue {
          width: 100%;
          height: 106px;
        }
      }
    }
    .item {
      width: 112px;
      margin-right: 10px;
      .pictrue {
        width: 112px;
        height: 112px;
        background-color: #f3f9ff;
        img {
          width: 65px;
          height: 50px;
        }
      }
      .bottom {
        width: 98px;
        height: 18px;
        background: linear-gradient(90deg, #e93323 0%, #ff7931 100%);
        border-radius: 1px 10px 10px 10px;
        text-align: center;
        line-height: 18px;
        color: #fff;
        font-size: 11px;
        margin-top: 8px;
      }
      .money {
        font-size: 12px;
        color: #666;
        margin-top: 8px;
        img {
          width: 16px;
          height: 16px;
          display: block;
          margin-right: 4px;
        }
        .num {
          color: #e93323;
        }
      }
      .name {
        color: #282828;
        font-size: 13px;
        margin-top: 3px;
      }
    }
  }
}
</style>
