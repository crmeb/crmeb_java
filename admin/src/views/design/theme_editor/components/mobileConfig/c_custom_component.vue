<template>
  <div class="mobile-config">
    <div v-for="(item, key) in rCom" :key="key">
      <component
        :is="item.components"
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
      v-model="modals"
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

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue';
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import CustomDesign from '@/components/CustomDesign';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';
import { mergeConfigDefaults } from '@/views/design/theme_editor/utils/mergeConfigDefaults';

defineOptions({ name: 'c_custom_component', componentsName: 'home_custom_component' });

const mobildConfigStore = useMobildConfigStore();

const props = defineProps({
  activeIndex: {
    type: null,
  },
  num: {
    type: null,
  },
  index: {
    type: null,
  },
});

const configObj = ref({});
const rCom = shallowRef([]);
const setUp = ref(0);
const type = ref('user');
const modals = ref(false);

const defaultConfig = {
  setUp: { tabVal: 0 },
  messageTitle: '信息设置',
  dataTitle: '数据设置',
  designTitle: '组件设计',
  commonTitle: '通用样式',
  dataStyleTitle: '数据样式',
  selectType: {
    title: '选择信息',
    activeValue: 'user',
    list: [
      { activeValue: 'user', title: '用户' },
      { activeValue: 'article', title: '文章' },
      { activeValue: 'coupon', title: '优惠券' },
      { activeValue: 'goods', title: '商品' },
    ],
  },
  articleDisplayMode: {
    title: '展示方式',
    tabVal: 0,
    tabList: [{ name: '纵向平铺' }, { name: '横向滑动' }],
  },
  articleColumnStyle: {
    title: '排列方式',
    tabVal: 0,
    tabList: [{ name: '1列' }, { name: '2列' }, { name: '3列' }, { name: '4列' }],
  },
  articleDataSource: {
    title: '数据选择',
    tabVal: 0,
    tabList: [{ name: '指定数据' }, { name: '筛选数据' }],
  },
  articleList: { list: [] },
  articleClass: {
    title: '文章分类',
    activeValue: '',
    list: [],
  },
  articleNum: {
    title: '显示数量',
    val: 1,
    min: 1,
  },
  articleSort: {
    title: '排序类型',
    tabVal: 0,
    tabList: [{ name: '浏览量' }, { name: '发布时间' }],
  },
  articleSortRule: {
    title: '排序规则',
    tabVal: 0,
    tabList: [{ name: '升序' }, { name: '降序' }],
  },
  couponDisplayMode: {
    title: '展示方式',
    tabVal: 0,
    tabList: [{ name: '纵向平铺' }, { name: '横向滑动' }],
  },
  couponColumnStyle: {
    title: '排列方式',
    tabVal: 0,
    tabList: [{ name: '1列' }, { name: '2列' }, { name: '3列' }, { name: '4列' }],
  },
  couponDataSource: {
    title: '数据选择',
    tabVal: 0,
    tabList: [{ name: '指定数据' }, { name: '筛选数据' }],
  },
  couponList: { list: [] },
  couponType: {
    title: '优惠券类型',
    activeValue: '',
    list: [
      { activeValue: '', title: '全部' },
      { activeValue: '0', title: '通用券' },
      { activeValue: '1', title: '品类券' },
      { activeValue: '2', title: '商品券' },
    ],
  },
  couponUserType: {
    title: '用户类型',
    activeValue: '',
    list: [
      { activeValue: '', title: '全部' },
      { activeValue: '1', title: '普通用户' },
      { activeValue: '2', title: '会员用户' },
    ],
  },
  couponSendType: {
    title: '发送方式',
    activeValue: '',
    list: [
      { activeValue: '', title: '全部' },
      { activeValue: '1', title: '手动领取' },
      { activeValue: '3', title: '赠送券' },
    ],
  },
  couponThreshold: {
    title: '使用门槛',
    tabVal: 0,
    tabList: [{ name: '无门槛' }, { name: '有门槛' }],
  },
  couponThresholdValue: {
    title: '门槛金额',
    val: 0,
    min: 0,
    max: 10000,
  },
  couponTime: {
    title: '领取时间',
    val: [],
  },
  couponSort: {
    title: '排序类型',
    tabVal: 0,
    tabList: [{ name: '面值大小' }, { name: '发布时间' }],
  },
  couponSortRule: {
    title: '排序规则',
    tabVal: 0,
    tabList: [{ name: '升序' }, { name: '降序' }],
  },
  couponNum: {
    title: '显示数量',
    val: 1,
    min: 1,
  },
  goodsDisplayMode: {
    title: '展示方式',
    tabVal: 0,
    tabList: [{ name: '纵向平铺' }, { name: '横向滑动' }],
  },
  goodsColumnStyle: {
    title: '排列方式',
    tabVal: 0,
    tabList: [{ name: '1列' }, { name: '2列' }, { name: '3列' }, { name: '4列' }],
  },
  goodsDataSource: {
    title: '数据选择',
    tabVal: 0,
    tabList: [{ name: '指定数据' }, { name: '指定分类' }],
  },
  goodsList: {
    title: '商品列表',
    max: 20,
    list: [],
  },
  goodsClass: {
    title: '商品分类',
    activeValue: '',
    list: [],
  },
  goodsNum: {
    title: '显示数量',
    val: 6,
    min: 1,
  },
  goodsSort: {
    title: '商品排序',
    tabVal: 0,
    tabList: [{ name: '销量' }, { name: '价格' }],
  },
  goodsSortRule: {
    title: '排序规则',
    tabVal: 0,
    tabList: [{ name: '降序' }, { name: '升序' }],
  },
  customBtnConfig: {
    title: '设计组件',
  },
  moduleColor: {
    title: '组件背景',
    default: [{ item: '#fff' }, { item: '#fff' }],
    color: [{ item: '#fff' }, { item: '#fff' }],
  },
  bottomBgColor: {
    title: '底部背景',
    color: [{ item: '#f5f5f5' }],
    default: [{ item: '#f5f5f5' }],
  },
  componentBgConfig: {
    title: '背景样式',
    tabVal: 0,
    tabList: [{ name: '颜色' }, { name: '图片' }],
    colorConfig: {
      title: '背景颜色',
      color: [{ item: '#fff' }],
      default: [{ item: '#fff' }],
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
  },
  componentBgDataConfig: {
    title: '背景样式',
    tabVal: 0,
    tabList: [{ name: '颜色' }, { name: '图片' }],
    colorConfig: {
      title: '背景颜色',
      color: [{ item: '#fff' }],
      default: [{ item: '#fff' }],
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
  },
  fillet: {
    title: '背景圆角',
    type: 0,
    list: [
      { val: '全部', icon: 'iconcaozuo-zhengti' },
      { val: '单个', icon: 'iconcaozuo-bianjiao' },
    ],
    valName: '圆角值',
    val: 0,
    min: 0,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  filletDataConfig: {
    title: '背景圆角',
    type: 0,
    list: [
      { val: '全部', icon: 'iconcaozuo-zhengti' },
      { val: '单个', icon: 'iconcaozuo-bianjiao' },
    ],
    valName: '圆角值',
    val: 0,
    min: 0,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  marginConfig: {
    title: '边距设置',
    val: 0,
    min: 0,
    max: 100,
    isAll: false,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  marginDataConfig: {
    title: '外边距',
    val: 0,
    min: 0,
    max: 100,
    isAll: false,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  paddingConfig: {
    title: '内边距',
    val: 0,
    min: 0,
    max: 100,
    isAll: false,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  paddingDataConfig: {
    title: '内边距',
    val: 0,
    min: 0,
    max: 100,
    isAll: false,
    valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  },
  borderConfig: {
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
  },
  borderDataConfig: {
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
  shadowDataConfig: {
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
};

const columnNum = computed(() => {
  let styleConfig = null;
  if (type.value === 'article') styleConfig = configObj.value.articleColumnStyle;
  else if (type.value === 'goods') styleConfig = configObj.value.goodsColumnStyle;
  else if (type.value === 'coupon') styleConfig = configObj.value.couponColumnStyle;

  if (styleConfig) {
    return (styleConfig.tabVal || 0) + 1;
  }
  return 1;
});

watch(
  () => props.num,
  (nVal) => {
    setConfig(mobildConfigStore.defaultArray[nVal]);
  },
);

watch(
  configObj,
  (nVal, oVal) => {
    mobildConfigStore.UPDATEARR({ num: props.num, val: nVal });
  },
  { deep: true },
);

watch(
  () => configObj.value?.setUp?.tabVal,
  (nVal, oVal) => {
    setUp.value = nVal;
    updateRCom();
  },
  { deep: true },
);

watch(
  () => configObj.value?.selectType?.activeValue,
  (nVal, oVal) => {
    type.value = nVal;
    updateRCom();
  },
  { deep: true },
);

watch(
  () => configObj.value?.couponUserType?.activeValue,
  (nVal, oVal) => {
    updateRCom();
  },
  { deep: true },
);

watch(
  () => configObj.value?.goodsDataSource?.tabVal,
  (nVal, oVal) => {
    updateRCom();
  },
  { deep: true },
);

watch(
  () => configObj.value?.couponDataSource?.tabVal,
  (nVal, oVal) => {
    updateRCom();
  },
  { deep: true },
);

watch(
  () => configObj.value?.couponThreshold?.tabVal,
  (nVal, oVal) => {
    updateRCom();
  },
  { deep: true },
);

watch(
  () => configObj.value?.articleDataSource?.tabVal,
  (nVal, oVal) => {
    updateRCom();
  },
  { deep: true },
);

onMounted(() => {
  nextTick(() => {
    setConfig(mobildConfigStore.defaultArray[props.num]);
  });
});

function patchConfig(data) {
  if (!data) return data;
  data = mergeConfigDefaults(data, defaultConfig);
  // Ensure structure exists if missing
  if (!data.setUp) {
    data.setUp = { tabVal: 0 };
  }
  if (!data.customBtnConfig) {
    data.customBtnConfig = {
      title: '设计组件',
    };
  }
  if (!data.fillet) {
    data.fillet = {
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
    };
  }
  if (!data.filletDataConfig) {
    data.filletDataConfig = {
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
    };
  }
  if (!data.componentBgConfig) {
    data.componentBgConfig = {
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
    };
  }
  if (!data.componentBgDataConfig) {
    data.componentBgDataConfig = {
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
    };
  }
  if (!data.marginConfig) {
    data.marginConfig = {
      title: '边距设置',
      val: 0,
      min: 0,
      max: 100,
      isAll: false,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
  }
  if (!data.marginDataConfig) {
    data.marginDataConfig = {
      title: '外边距',
      val: 0,
      min: 0,
      max: 100,
      isAll: false,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
  }
  if (!data.paddingConfig) {
    data.paddingConfig = {
      title: '内边距',
      val: 0,
      min: 0,
      max: 100,
      isAll: false,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
  }
  if (!data.paddingDataConfig) {
    data.paddingDataConfig = {
      title: '内边距',
      val: 0,
      min: 0,
      max: 100,
      isAll: false,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
  }
  if (!data.borderConfig) {
    data.borderConfig = {
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
    };
  }
  if (!data.borderDataConfig) {
    data.borderDataConfig = {
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
    };
  }
  if (!data.shadowConfig) {
    data.shadowConfig = {
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
    };
  }
  if (!data.shadowDataConfig) {
    data.shadowDataConfig = {
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
    };
  }
  if (!data.bottomBgColor) {
    data.bottomBgColor = {
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
    };
  }
  if (!data.selectType) {
    data.selectType = {
      title: '选择信息',
      activeValue: 'user',
      list: [
        { activeValue: 'user', title: '用户' },
        { activeValue: 'article', title: '文章' },
        { activeValue: 'coupon', title: '优惠券' },
        { activeValue: 'goods', title: '商品' },
      ],
    };
  }
  if (!data.couponUserType) {
    data.couponUserType = {
      title: '用户类型',
      activeValue: '',
      list: [
        { activeValue: '', title: '全部' },
        { activeValue: '1', title: '普通用户' },
        { activeValue: '2', title: '会员用户' },
      ],
    };
  }
  return data;
}

function setConfig(data) {
  if (!data) return;
  const value = patchConfig(data);
  configObj.value = value;
  setUp.value = value.setUp.tabVal;
  type.value = value.selectType.activeValue;
  updateRCom();
}

function updateRCom() {
  if (setUp.value === 0) {
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

    if (type.value === 'article') {
      arr = arr.concat([
        { components: toolCom.c_radio, configNme: 'articleDisplayMode' },
        { components: toolCom.c_radio, configNme: 'articleColumnStyle' },
        {
          components: toolCom.c_title,
          configNme: 'dataTitle',
        },
        { components: toolCom.c_radio, configNme: 'articleDataSource' },
      ]);

      if (configObj.value.articleDataSource && configObj.value.articleDataSource.tabVal === 0) {
        // Specific Data
        arr.push({ components: toolCom.c_article, configNme: 'articleList' });
      } else if (configObj.value.articleDataSource && configObj.value.articleDataSource.tabVal === 1) {
        // Filtered Data
        arr.push(
          { components: toolCom.c_classify, configNme: 'articleClass' },
          { components: toolCom.c_input_number, configNme: 'articleNum' },
          { components: toolCom.c_radio, configNme: 'articleSort' },
          { components: toolCom.c_radio, configNme: 'articleSortRule' },
        );
        arr.push({ components: toolCom.c_input_number, configNme: 'articleNum' });
      }
    } else if (type.value === 'coupon') {
      arr = arr.concat([
        { components: toolCom.c_radio, configNme: 'couponDisplayMode' },
        { components: toolCom.c_radio, configNme: 'couponColumnStyle' },
        {
          components: toolCom.c_title,
          configNme: 'dataTitle',
        },
        { components: toolCom.c_radio, configNme: 'couponDataSource' },
      ]);

      if (configObj.value.couponDataSource && configObj.value.couponDataSource.tabVal === 0) {
        // Manual Selection
        arr.push({ components: toolCom.c_coupon_select, configNme: 'couponList' });
      } else if (configObj.value.couponDataSource && configObj.value.couponDataSource.tabVal === 1) {
        // Filtered Data
        arr.push(
          { components: toolCom.c_select, configNme: 'couponType' },
          { components: toolCom.c_select, configNme: 'couponUserType' },
        );
        if (configObj.value.couponUserType && configObj.value.couponUserType.activeValue != 2) {
          arr.push({ components: toolCom.c_select, configNme: 'couponSendType' });
        }
        arr.push({ components: toolCom.c_radio, configNme: 'couponThreshold' });
        if (configObj.value.couponThreshold && configObj.value.couponThreshold.tabVal === 1) {
          arr.push({ components: toolCom.c_input_number, configNme: 'couponThresholdValue' });
        }
        arr.push(
          { components: toolCom.c_datetime_picker, configNme: 'couponTime' },
          { components: toolCom.c_radio, configNme: 'couponSort' },
          { components: toolCom.c_radio, configNme: 'couponSortRule' },
        );
      }

      // arr.push({ components: toolCom.c_input_number, configNme: 'couponNum' });
    } else if (type.value === 'goods') {
      arr = arr.concat([
        { components: toolCom.c_radio, configNme: 'goodsDisplayMode' },
        { components: toolCom.c_radio, configNme: 'goodsColumnStyle' },
        {
          components: toolCom.c_title,
          configNme: 'dataTitle',
        },
        { components: toolCom.c_radio, configNme: 'goodsDataSource' },
      ]);

      if (configObj.value.goodsDataSource && configObj.value.goodsDataSource.tabVal === 0) {
        // Specific Data
        arr.push({ components: toolCom.c_goods, configNme: 'goodsList' });
      } else if (configObj.value.goodsDataSource && configObj.value.goodsDataSource.tabVal === 1) {
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

    rCom.value = arr;
  } else {
    // Style Config
    rCom.value = [
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
}

function getConfig(data) {
  if ((data && data.name) == 'custom_btn_click') {
    modals.value = true;
  }
}

function handleSave(data) {
  configObj.value.customComponents = data;
}
</script>

<style scoped lang="scss">
.mobile-config {
  padding-bottom: 20px;
}
:deep(.custom-design-dialog) {
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
