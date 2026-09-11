<template>
  <common_wrapper :config="configObj">
    <div
      class="box"
      :style="{
        background: bgColor,
        borderRadius: fillet
          ? valList[0].val + 'px ' + valList[1].val + 'px ' + valList[3].val + 'px ' + valList[2].val + 'px'
          : filletVal + 'px',
      }"
      v-html="richText"
    ></div>
  </common_wrapper>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';

defineOptions({
  name: 'z_ueditor',
  cname: '富文本',
  configName: 'c_ueditor_box',
  icon: '#iconzujian-fuwenben',
  type: 2, // 0 基础组件 1 营销组件 2工具组件
  defaultName: 'richText', // 外面匹配名称
});

const props = defineProps({
  index: {
    type: null,
    default: -1,
  },
  num: {
    type: null,
  },
});

const mobildConfigStore = useMobildConfigStore();

// 默认初始化数据禁止修改
const defaultConfig = {
  cname: '富文本',
  name: 'richText',
  timestamp: props.num,
  isHide: false,
  setUp: {
    tabVal: 0,
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
      default: [{ item: '#e5e5e5' }],
      color: [{ item: '#e5e5e5' }],
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
  titleLeft: '富文本内容',
  titleRight: '通用样式',
  bgColor: {
    title: '组件背景',
    name: 'bgColor',
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
  bottomBgColor: {
    title: '底部背景',
    name: 'bgColor',
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
  richText: {
    val: '',
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
};

const cSlider = ref('');
const configObj = ref(null);
const bgColor = ref('');
const confObj = ref({});
const pageData = ref({});
const richText = ref('');
const bottomBgColor = ref('');
const fillet = ref(0);
const filletVal = ref(0);
const valList = ref([]);
const paddingConfig = ref({
  valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
});
const marginConfig = ref({
  valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
});

function setConfig(data) {
  if (!data) return;
  configObj.value = data;
  for (let key in defaultConfig) {
    if (data[key] == undefined) {
      data[key] = defaultConfig[key];
    }
  }
  bgColor.value = data.bgColor.color[0].item;
  bottomBgColor.value = data.bottomBgColor.color[0].item;
  richText.value = data.richText.val;
  fillet.value = data.fillet.type;
  filletVal.value = data.fillet.val;
  valList.value = data.fillet.valList;
  paddingConfig.value = data.paddingConfig || {
    valList: [
      { val: data.topConfig ? data.topConfig.val : 0 },
      { val: data.lrConfig ? data.lrConfig.val : 0 },
      { val: data.bottomConfig ? data.bottomConfig.val : 0 },
      { val: data.lrConfig ? data.lrConfig.val : 0 },
    ],
  };
  marginConfig.value = data.marginConfig || {
    valList: [{ val: data.udConfig ? data.udConfig.val : 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
  };
}

watch(
  pageData,
  (nVal, oVal) => {
    setConfig(nVal);
  },
  { deep: true },
);
watch(
  () => props.num,
  (nVal, oVal) => {
    let data = mobildConfigStore.defaultArray[nVal];
    setConfig(data);
  },
  { deep: true },
);
watch(
  () => mobildConfigStore.defaultArray,
  (nVal, oVal) => {
    let data = mobildConfigStore.defaultArray[props.num];
    setConfig(data);
  },
  { deep: true },
);

onMounted(() => {
  nextTick(() => {
    pageData.value = mobildConfigStore.defaultArray[props.num];
    setConfig(pageData.value);
  });
});
</script>

<style scoped lang="scss">
.mobile-page {
  display: inline-block;
  width: -webkit-fill-available;
}
.mobile-page :deep(.video) {
  max-width: 100% !important;
}
.box {
  min-height: 42px;
  padding: 10px;
  background-color: #f5f5f5;
  word-break: break-all;
  border-radius: 12px;
  img {
    max-width: 100%;
    height: auto;
  }
  :deep(video) {
    max-width: 100%;
  }
}
</style>
