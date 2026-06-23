<template>
  <common_wrapper :config="configObj">
    <div class="home_product">
      <!-- Header Section -->
      <div class="header-box" :style="headerBoxStyle" v-if="headerText || headerImg">
        <div class="title-text" v-if="headerType === 0" :style="titleTextStyle">
          {{ headerText }}
        </div>
        <div class="title-img" v-else :style="titleImgBoxStyle">
          <img :src="headerImg" :style="titleImgStyle" alt="" v-if="headerImg" />
          <div class="empty-img" v-else>暂无图片</div>
        </div>
      </div>

      <!-- Single Column -->
      <template v-if="styleConfig == 0">
        <div class="list-wrapper itemA">
          <div
            class="item"
            v-for="(item, index) in list"
            :key="index"
            :style="{
              borderRadius: bgRadius,
            }"
          >
            <div class="img-box">
              <img
                class="img"
                v-if="item.image"
                :src="item.image"
                alt=""
                :style="{
                  borderRadius: imgRadius,
                }"
              />
              <div
                v-else
                class="empty-box"
                :style="{
                  borderRadius: imgRadius,
                }"
              >
                <img src="../../assets/images/shan.png" />
              </div>
            </div>
            <div class="info">
              <div class="hd">
                <div
                  class="title line2"
                  v-if="checkboxInfo.indexOf(0) != -1"
                  :style="{
                    fontWeight: goodsName,
                    color: toneConfig ? goodsNameColor : '#333',
                  }"
                >
                  {{ item.store_name || '华为荣耀畅享平板换屏服务 屏幕换外屏主板维修' }}
                </div>
                <img v-if="checkboxInfo.indexOf(1) != -1" src="../../assets/images/goods01.png" />
              </div>
              <div
                class="price acea-row row-middle"
                :class="checkboxInfo.indexOf(3) == -1 && checkboxInfo.indexOf(4) == -1 ? 'on' : ''"
              >
                <div
                  class="num"
                  v-if="checkboxInfo.indexOf(2) != -1"
                  :style="{
                    color: toneConfig ? goodsPriceColor : colorStyle.theme,
                  }"
                >
                  <span>￥</span>{{ item.price ? $HandlePrice(item.price, 0) : 33
                  }}<span>{{ item.price ? $HandlePrice(item.price, 1) : '' }}</span>
                </div>
                <img class="img" v-if="checkboxInfo.indexOf(5) != -1" src="../../assets/images/goods02.png" />
              </div>
              <div class="bottom">
                <span
                  class="mr8"
                  v-if="checkboxInfo.indexOf(3) != -1"
                  :style="{
                    color: toneConfig ? soldNumColor : '#999999',
                  }"
                  >已售{{ item.sales || 0 }}件</span
                >
                <span
                  v-if="checkboxInfo.indexOf(4) != -1"
                  :style="{
                    color: toneConfig ? scoreColor : '#999999',
                  }"
                  >评分 {{ item.star || 0 }}</span
                >
              </div>
            </div>
            <div v-if="!cartConfig">
              <div
                class="bnt"
                v-if="bntStyleConfig == 0"
                :style="{
                  background: toneCartConfig ? bntBgColor : themeColor,
                }"
              >
                购买
              </div>
              <div
                class="jia"
                v-else
                :style="{
                  background: toneCartConfig ? bntBgColor : themeColor,
                }"
              >
                <div class="jiaCon">
                  <span class="iconfont iconjiahao1" v-if="bntStyleConfig == 1"></span>
                  <span class="iconfont icongouwuche1" v-else></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- Two Columns -->
      <template v-else-if="styleConfig == 1">
        <div class="list-wrapper itemC">
          <div class="item" v-for="(item, index) in list" :key="index">
            <div class="img-box">
              <img
                class="img"
                v-if="item.image"
                :src="item.image"
                alt=""
                :style="{
                  borderRadius: imgRadius2,
                }"
              />
              <div
                v-else
                class="empty-box"
                :style="{
                  borderRadius: imgRadius2,
                }"
              >
                <img src="../../assets/images/shan.png" />
              </div>
            </div>
            <div
              class="info"
              :class="
                checkboxInfo.length == 1 && checkboxInfo.indexOf(0) != -1 && !cartConfig
                  ? 'on'
                  : ((checkboxInfo.length == 1 && checkboxInfo.indexOf(4) != -1) || !checkboxInfo.length) && !cartConfig
                  ? 'on2'
                  : ''
              "
              :style="{
                borderRadius: bgRadius2,
              }"
            >
              <div class="hd">
                <div
                  class="title line2"
                  v-if="checkboxInfo.indexOf(0) != -1"
                  :style="{
                    fontWeight: goodsName,
                    color: toneConfig ? goodsNameColor : '#333',
                  }"
                >
                  {{ item.store_name || '蓝牙音乐手表 | Jeep智能表蓝牙通话健康管理 P07' }}
                </div>
                <img v-if="checkboxInfo.indexOf(1) != -1" src="../../assets/images/goods01.png" />
              </div>
              <div class="price acea-row row-middle">
                <div
                  class="num mb-10"
                  v-if="checkboxInfo.indexOf(2) != -1"
                  :style="{
                    color: toneConfig ? goodsPriceColor : colorStyle.theme,
                  }"
                >
                  <span>￥</span>{{ item.price ? $HandlePrice(item.price, 0) : 77
                  }}<span>{{ item.price ? $HandlePrice(item.price, 1) : '' }}</span>
                </div>
                <img class="img" v-if="checkboxInfo.indexOf(5) != -1" src="../../assets/images/goods02.png" />
              </div>
              <div
                class="bottom"
                v-if="checkboxInfo.indexOf(3) != -1"
                :style="{
                  color: toneConfig ? soldNumColor : '#999999',
                }"
              >
                <span>已售{{ item.sales || 0 }}件</span>
              </div>
            </div>
            <div
              class="jia"
              v-if="!cartConfig"
              :style="{
                background: toneCartConfig ? bntBgColor : themeColor,
              }"
            >
              <div class="jiaCon">
                <span class="iconfont iconjiahao1" v-if="bntStyleConfig == 0"></span>
                <span class="iconfont icongouwuche1" v-else></span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- Three Columns / Sliding -->
      <template v-else>
        <div
          class="list-wrapper itemB"
          :class="styleConfig == 3 ? 'itemD' : ''"
          :style="{
            borderRadius: bgRadius,
          }"
        >
          <div class="list">
            <div class="item" v-for="(item, index) in list" :key="index">
              <div class="img-box">
                <img
                  class="img"
                  v-if="item.image"
                  :src="item.image"
                  alt=""
                  :style="{
                    borderRadius: imgRadius,
                  }"
                />
                <div
                  v-else
                  class="empty-box"
                  :style="{
                    borderRadius: imgRadius,
                  }"
                >
                  <img src="../../assets/images/shan.png" />
                </div>
              </div>
              <div
                class="info"
                :class="
                  checkboxInfo.indexOf(2) == -1 && checkboxInfo.indexOf(0) != -1 && !cartConfig
                    ? 'on'
                    : checkboxInfo.indexOf(2) == -1 && checkboxInfo.indexOf(0) == -1 && !cartConfig
                    ? 'on2'
                    : ''
                "
              >
                <div class="hd" v-if="checkboxInfo.indexOf(0) != -1">
                  <div
                    class="title line2"
                    :style="{
                      fontWeight: goodsName,
                      color: toneConfig ? goodsNameColor : '#333',
                    }"
                  >
                    {{ item.store_name || '商品名称商品商名称商品商…' }}
                  </div>
                </div>
                <div class="price" v-if="checkboxInfo.indexOf(2) != -1">
                  <div
                    class="num"
                    :style="{
                      color: toneConfig ? goodsPriceColor : colorStyle.theme,
                    }"
                  >
                    <span>￥</span>{{ item.price ? $HandlePrice(item.price, 0) : 77
                    }}<span>{{ item.price ? $HandlePrice(item.price, 1) : '' }}</span>
                  </div>
                </div>
              </div>
              <div
                class="jia"
                v-if="!cartConfig"
                :style="{
                  background: toneCartConfig ? bntBgColor : themeColor,
                }"
              >
                <div class="jiaCon">
                  <span class="iconfont iconjiahao1" v-if="bntStyleConfig == 0"></span>
                  <span class="iconfont icongouwuche1" v-else></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </common_wrapper>
</template>

<script>
import { mapState } from 'vuex';
export default {
  name: 'home_good_recommend',
  cname: '优品推荐',
  configName: 'c_good_recommend',
  icon: '#iconzujian-youpintuijian', // Placeholder icon
  type: 3,
  defaultName: 'goodRecommend',
  props: {
    index: {
      type: null,
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
    headerBoxStyle() {
      return {
        marginBottom: '10px',
        padding: '0 10px',
        textAlign: this.headerAlign,
      };
    },
    titleTextStyle() {
      return {
        color: this.headerColor,
        fontSize: this.headerFontSize + 'px',
        fontWeight: this.headerFontWeight,
        fontStyle: this.headerFontStyle,
        textAlign: this.headerAlign,
      };
    },
    titleImgBoxStyle() {
      return {
        textAlign: this.headerAlign,
      };
    },
    titleImgStyle() {
      return {
        maxWidth: '100%',
        height: 'auto',
      };
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
        cname: '优品推荐',
        name: 'goodRecommend',
        timestamp: this.num,
        isHide: false,
        setUp: {
          tabVal: 0,
        },
        // Header Config
        headerTitle: '头部设置',
        headerType: {
          title: '标题类型',
          tabVal: 0,
          tabList: [{ name: '文字' }, { name: '图片' }],
        },
        headerText: {
          title: '标题文字',
          value: '优品推荐',
        },
        headerImg: {
          url: '',
          type: 'code',
          delType: 1,
          name: '上传图片',
        },
        // Content Config
        titleGoods: '商品设置',
        goodsList: {
          max: 20,
          list: [],
        },
        productList: {
          list: [],
        },
        typeConfig: {
          title: '选择方式',
          activeValue: 1,
          list: [
            { activeValue: 1, title: '指定商品' },
            { activeValue: 3, title: '指定分类' },
            // { activeValue: 4, title: '商品标签' },
          ],
        },
        goodsSort: {
          title: '商品排序',
          tabVal: 1,
          tabList: [{ name: '综合' }, { name: '销量' }, { name: '价格' }],
        },
        numberConfig: {
          title: '商品数量',
          val: 3,
          min: 1,
        },
        classList: {
          title: '商品分类',
          classVal: [],
        },
        goodsLabel: {
          title: '商品标签',
          activeValue: [],
          list: [],
        },
        checkboxInfo: {
          title: '展示信息',
          name: 'checkboxInfo',
          type: [0, 3, 2, 5], // Name, Price, Member Price (5)
          list: [
            { id: 0, name: '商品名称' },
            { id: 2, name: '商品价格' },
            { id: 5, name: '会员价格' },
            { id: 3, name: '已售数量' },
          ],
        },
        cartConfig: {
          title: '购物车按钮',
          tabVal: 0, // 0: Show, 1: Hide
          tabList: [{ name: '显示' }, { name: '隐藏' }],
        },
        bntStyleConfig: {
          title: '按钮样式',
          tabVal: 0,
          tabList: [
            { name: '样式1', icon: 'icon-circle' },
            { name: '样式2', icon: 'icon-plus' },
            { name: '样式3', icon: 'icon-cart' },
          ],
        },
        bntConfig: {
          title: '按钮效果',
          tabVal: 1,
          tabList: [{ name: '进入商品详情页' }, { name: '加入购物车' }],
        },
        // Style Config
        titleRight: '列表样式', // List Style
        styleConfig: {
          title: '列表样式',
          tabVal: 0,
          tabList: [{ name: '单列展示' }, { name: '两列纵向' }, { name: '三列展示' }, { name: '左右滑动' }],
        },
        headerStyleTitle: '头部样式',
        headerTextConfig: {
          title: '标题文字',
          tabVal: 1, // 0: Bold, 1: Normal, 2: Italic
          tabList: [
            { name: '加粗', style: 'bold' },
            { name: '正常', style: 'normal' },
            { name: '倾斜', style: 'italic' },
          ],
        },
        headerColor: {
          title: '标题颜色',
          default: [{ item: '#333333' }],
          color: [{ item: '#333333' }],
        },
        headerAlign: {
          title: '标题位置',
          tabVal: 1, // 0: Left, 1: Center, 2: Right
          tabList: [
            { name: '左对齐', style: 'left' },
            { name: '居中对齐', style: 'center' },
            { name: '右对齐', style: 'right' },
          ],
        },
        headerFontSize: {
          title: '标题字号',
          val: 16,
          min: 12,
          max: 30,
        },
        cartStyleTitle: '购物车按钮',
        goodsStyleTitle: '商品图样式',
        toneCartConfig: {
          title: '色调',
          tabVal: 0,
          tabList: [{ name: '跟随主题风格' }, { name: '自定义' }],
        },
        bntBgColor: {
          title: '按钮颜色',
          default: [{ item: '#E93323' }, { item: '#FF7931' }],
          color: [{ item: '#E93323' }, { item: '#FF7931' }],
        },
        generalStyleTitle: '通用样式',
        componentBgConfig: {
          title: '背景设置',
          tabVal: 0,
          tabList: [{ name: '颜色' }, { name: '图片' }],
          colorConfig: {
            title: '背景颜色',
            default: [{ item: '#F5F5F5' }],
            color: [{ item: '#F5F5F5' }],
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
        bottomBgColor: {
          title: '底部背景',
          default: [{ item: '#F5F5F5' }],
          color: [{ item: '#F5F5F5' }],
        },
        paddingConfig: {
          title: '内边距',
          isAll: false,
          val: 10,
          min: 0,
          max: 100,
          valList: [{ val: 0 }, { val: 10 }, { val: 0 }, { val: 10 }],
        },
        marginConfig: {
          title: '外边距',
          isAll: false,
          val: 0,
          min: 0,
          max: 100,
          valList: [{ val: 10 }, { val: 0 }, { val: 0 }, { val: 0 }],
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
          title: '内容间距', // Page Spacing in prompt, using Content Spacing name
          val: 10,
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
        filletImg: {
          title: '圆角值', // Image Radius
          type: 0,
          val: 8,
          valList: [{ val: 8 }, { val: 8 }, { val: 8 }, { val: 8 }],
        },
        goodsName: {
          title: '商品名称',
          tabVal: 0,
          tabList: [
            { name: '加粗', style: 'bold' },
            { name: '正常', style: 'normal' },
          ],
        },
        toneConfig: {
          title: '色调',
          tabVal: 0,
          tabList: [{ name: '跟随主题风格' }, { name: '自定义' }],
        },
        goodsNameColor: {
          title: '商品名称',
          default: [{ item: '#333333' }],
          color: [{ item: '#333333' }],
        },
        goodsPriceColor: {
          title: '商品价格',
          default: [{ item: '#E93323' }],
          color: [{ item: '#E93323' }],
        },
        soldNumColor: {
          title: '已售数量',
          default: [{ item: '#999999' }],
          color: [{ item: '#999999' }],
        },
        scoreColor: {
          title: '评分',
          default: [{ item: '#999999' }],
          color: [{ item: '#999999' }],
        },
      },
      list: [],
      pageData: {},
      styleConfig: 0,
      checkboxInfo: [],
      cartConfig: 0,
      bntStyleConfig: 0,
      imgRadius: 0,
      imgRadius2: 0,
      goodsName: '',
      toneConfig: 0,
      goodsNameColor: '',
      goodsPriceColor: '',
      soldNumColor: '',
      scoreColor: '',
      toneCartConfig: 0,
      bntBgColor: '',
      bntBgColorLeft: '',
      bgColor: '',
      bottomBgColor: '',
      paddingConfig: {
        title: '内边距',
        isAll: false,
        val: 10,
        min: 0,
        max: 100,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },
      marginConfig: {
        title: '外边距',
        isAll: false,
        val: 0,
        min: 0,
        max: 100,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      },
      bgRadius: 0,
      bgRadius2: 0,
      themeColor: '',
      // Header Data
      headerType: 0, // 0: Text, 1: Image (In tabList index) -> Wait, tabList[0] is Image, tabList[1] is Text in my config above?
      // In default config above: tabList: [{name: '图片'}, {name: '文字'}]. So 0 is Image, 1 is Text.
      // Let's stick to this.
      headerText: '',
      headerImg: '',
      headerFontWeight: 'normal',
      headerFontStyle: 'normal',
      headerColor: '',
      headerAlign: 'center',
      headerFontSize: 16,
      showHeader: true,
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
      let configObj = JSON.parse(JSON.stringify(data));
      this.configObj = configObj;

      this.paddingConfig = configObj.paddingConfig || {
        title: '内边距',
        val: 10,
        min: 0,
        max: 100,
        isAll: false,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      };
      this.marginConfig = configObj.marginConfig || {
        title: '外边距',
        val: 0,
        min: 0,
        max: 100,
        isAll: false,
        valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
      };

      if (!configObj.paddingConfig) {
        if (configObj.topConfig) this.paddingConfig.valList[0].val = configObj.topConfig.val;
        if (configObj.bottomConfig) this.paddingConfig.valList[2].val = configObj.bottomConfig.val;
        if (configObj.prConfig) {
          this.paddingConfig.valList[1].val = configObj.prConfig.val;
          this.paddingConfig.valList[3].val = configObj.prConfig.val;
        }
        configObj.paddingConfig = this.paddingConfig;
      }
      if (!configObj.marginConfig) {
        if (configObj.mbConfig) this.marginConfig.valList[0].val = configObj.mbConfig.val;
        configObj.marginConfig = this.marginConfig;
      }

      // Ensure new configs exist
      if (!configObj.zIndexConfig) this.$set(configObj, 'zIndexConfig', this.defaultConfig.zIndexConfig);
      if (!configObj.borderConfig) this.$set(configObj, 'borderConfig', this.defaultConfig.borderConfig);
      if (!configObj.shadowConfig) this.$set(configObj, 'shadowConfig', this.defaultConfig.shadowConfig);
      if (!configObj.componentBgConfig) this.$set(configObj, 'componentBgConfig', this.defaultConfig.componentBgConfig);

      // Header Config Defaults
      if (!configObj.headerTitle) this.$set(configObj, 'headerTitle', this.defaultConfig.headerTitle);
      if (!configObj.headerType) this.$set(configObj, 'headerType', this.defaultConfig.headerType);
      if (!configObj.headerText) this.$set(configObj, 'headerText', this.defaultConfig.headerText);
      if (!configObj.headerImg) this.$set(configObj, 'headerImg', this.defaultConfig.headerImg);
      if (!configObj.headerStyleTitle) this.$set(configObj, 'headerStyleTitle', this.defaultConfig.headerStyleTitle);
      if (!configObj.headerTextConfig) this.$set(configObj, 'headerTextConfig', this.defaultConfig.headerTextConfig);
      if (!configObj.headerColor) this.$set(configObj, 'headerColor', this.defaultConfig.headerColor);
      if (!configObj.headerAlign) this.$set(configObj, 'headerAlign', this.defaultConfig.headerAlign);
      if (!configObj.headerFontSize) this.$set(configObj, 'headerFontSize', this.defaultConfig.headerFontSize);

      // Header Config Assignment
      this.headerType = configObj.headerType.tabVal;
      this.headerText = configObj.headerText.value;
      this.headerImg = configObj.headerImg.url;

      let headerTextConfig = configObj.headerTextConfig.tabVal;
      this.headerFontWeight = headerTextConfig == 0 ? 'bold' : 'normal';
      this.headerFontStyle = headerTextConfig == 2 ? 'italic' : 'normal';

      this.headerColor =
        configObj.headerColor.color && configObj.headerColor.color[0] ? configObj.headerColor.color[0].item : '#333';

      const alignList = configObj.headerAlign.tabList;
      const alignVal = configObj.headerAlign.tabVal;
      this.headerAlign = alignList && alignList[alignVal] ? alignList[alignVal].style : 'left';

      this.headerFontSize = configObj.headerFontSize.val;

      if (configObj.mbConfig) {
        this.styleConfig = configObj.styleConfig.tabVal;
        this.checkboxInfo = configObj.checkboxInfo.type;
        this.cartConfig = configObj.cartConfig.tabVal;
        this.bntStyleConfig = configObj.bntStyleConfig.tabVal;

        let filletImg = configObj.filletImg.type;
        let filletValImg = configObj.filletImg.val;
        let valListImg = configObj.filletImg.valList;
        this.imgRadius = filletImg
          ? valListImg[0].val + 'px ' + valListImg[1].val + 'px ' + valListImg[3].val + 'px ' + valListImg[2].val + 'px'
          : filletValImg + 'px';
        this.imgRadius2 = filletImg
          ? valListImg[0].val + 'px ' + valListImg[1].val + 'px 0 0'
          : filletValImg + 'px ' + filletValImg + 'px 0 0';

        let goodsTabVal = configObj.goodsName.tabVal;
        this.goodsName = configObj.goodsName.tabList[goodsTabVal].style;

        this.toneConfig = configObj.toneConfig.tabVal;
        this.goodsNameColor = configObj.goodsNameColor.color[0].item;
        this.goodsPriceColor = configObj.goodsPriceColor.color[0].item;
        this.soldNumColor = configObj.soldNumColor.color[0].item;
        this.scoreColor = configObj.scoreColor.color[0].item;

        this.toneCartConfig = configObj.toneCartConfig.tabVal;
        let bntBgColorLeft = configObj.bntBgColor.color[0].item;
        let bntBgColorRight = configObj.bntBgColor.color[1].item;
        this.bntBgColorLeft = bntBgColorLeft;
        this.bntBgColor = `linear-gradient(90deg,${bntBgColorLeft} 0%,${bntBgColorRight} 100%)`;

        this.bottomBgColor = configObj.bottomBgColor.color[0].item;
        this.themeColor = `linear-gradient(90deg,${this.colorStyle.theme} 0%,${this.colorStyle.gradient} 100%)`;

        let fillet = configObj.fillet.type;
        let filletVal = configObj.fillet.val;
        let valList = configObj.fillet.valList;
        this.bgRadius = fillet
          ? valList[0].val + 'px ' + valList[1].val + 'px ' + valList[3].val + 'px ' + valList[2].val + 'px'
          : filletVal + 'px';
        this.bgRadius2 = fillet
          ? '0 0 ' + valList[3].val + 'px ' + valList[2].val + 'px'
          : '0 0 ' + filletVal + 'px ' + filletVal + 'px';

        this.list = this.getGoodsPreviewList(configObj, 4);
      }
    },
    getGoodsPreviewList(data, placeholderCount) {
      const typeValue = data.typeConfig && data.typeConfig.activeValue;
      const goodsList = data.goodsList && Array.isArray(data.goodsList.list) ? data.goodsList.list : [];
      const productList = data.productList && Array.isArray(data.productList.list) ? data.productList.list : [];
      const list = typeValue == 1 ? goodsList : productList;
      return list.length ? list : placeholderCount;
    },
  },
};
</script>

<style scoped lang="scss">
.mobile-page {
  display: inline-block;
  width: -webkit-fill-available;
}
.itemOn {
  border-radius: 0 !important;
  img,
  .empty-box {
    border-radius: 0 !important;
  }
  .img-box {
    .label {
      border-radius: 0 0 8px 0 !important;
    }
  }
}
.pageOn {
  border-radius: 8px !important;
}
.list-wrapper {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  .item {
    width: 48.5%;
    margin-bottom: 10px;
    background-color: #fff;
    position: relative;
    .bnt {
      width: 48px;
      height: 28px;
      background: linear-gradient(90deg, #e93323 0%, #ff7931 100%);
      border-radius: 25px;
      color: #fff;
      text-align: center;
      line-height: 28px;
      font-size: 12px;
      position: absolute;
      right: 10px;
      bottom: 10px;
    }
    .jia {
      width: 22px;
      height: 22px;
      background-color: #e93323;
      border-radius: 50%;
      position: absolute;
      right: 10px;
      bottom: 10px;
      .jiaCon {
        width: 100%;
        height: 100%;
        text-align: center;
        line-height: 22px;
        .iconfont {
          color: #fff;
          font-size: 13px;
        }
      }
    }
    .img-box {
      position: relative;
      width: 100%;
      height: 173px;
      .img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      img,
      .box {
        width: 65px;
        height: 50px;
      }
      .empty-box {
        background: #f3f9ff;
      }
    }
    .info {
      padding: 7px 10px;
      .title {
        font-size: 14px;
        color: #333;
      }
      img {
        height: 14px;
        display: block;
        margin-top: 4px;
      }
      .bottom {
        color: #999999;
        font-size: 11px;
      }
      .price {
        display: flex;
        align-items: center;
        img {
          width: 70px;
          height: 15px;
          display: block;
        }
        .num {
          font-size: 20px;
          margin-right: 4px;
          font-family: SemiBold;
          span {
            font-size: 12px;
          }
        }
      }
    }
  }
}
.itemA {
  .item {
    display: flex;
    width: 100%;
    padding: 10px;
    position: relative;
    .img-box {
      position: relative;
      width: 112px;
      height: 112px;
    }
    .info {
      display: flex;
      justify-content: space-between;
      flex-direction: column;
      flex: 1;
      margin-left: 10px;
      padding: 0;
      .hd {
        height: 63px;
      }
      .price {
        margin-top: 2px;
        &.on {
          margin-top: 20px;
        }
      }
    }
  }
}
.itemB {
  justify-content: inherit;
  background-color: #fff;
  padding: 16px 10px 0 10px;
  width: 100%;
  box-sizing: border-box;
  .list {
    display: flex;
    flex-wrap: wrap;
    width: 100%;
  }
  .item {
    width: 31.3%;
    margin-right: 10px;
    background: unset;
    .jia {
      right: 2px;
      bottom: 0;
    }
    .info {
      padding: 0;
      &.on {
        height: 70px;
      }
      &.on2 {
        height: 30px;
      }
      .hd {
        margin-top: 7px;
        height: 42px;
      }
      .price {
        margin-top: 7px;
        line-height: 1.2;
      }
    }
    &:nth-child(3n) {
      margin-right: 0;
    }
    .img-box {
      position: relative;
      width: 100%;
      height: 110px;
      img,
      .box,
      .empty-box {
        border-radius: 10px 10px 0 0;
      }
    }
  }
}
.itemD {
  flex-wrap: nowrap;
  display: flex;
  overflow-x: auto;
  padding-bottom: 10px; // Space for scrollbar or visual
  &::-webkit-scrollbar {
    display: none;
  }
  .list {
    flex-wrap: nowrap;
    // justify-content: center; // Don't center, let it flow
    // align-items: center;
  }
  .item {
    width: 110px; // Fixed width for sliding items
    flex-shrink: 0;
    margin-right: 10px;
    background: unset;
    // &:nth-child(3n) {
    //   margin-right: 10px; // Reset 3n margin removal for sliding
    // }
    &:last-child {
      margin-right: 0;
    }
    .img-box {
      height: 110px;
    }
  }
}
.itemC {
  .item {
    background-color: transparent;
    .info {
      background-color: #fff;
    }
  }
  .item .info.on {
    height: 67px;
  }
  .item .info.on2 {
    height: 40px;
  }
  .item .info .price {
    margin-top: 6px;
    margin-bottom: 8px;
  }
  .item .info .bottom {
    margin-top: 3px;
  }
}
.title-img {
  display: inline-block;
}
</style>
