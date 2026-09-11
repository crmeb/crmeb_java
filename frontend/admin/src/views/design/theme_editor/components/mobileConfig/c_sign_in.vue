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
    <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue';
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';

defineOptions({ name: 'c_sign_in', componentsName: 'sign_in', cname: '签到' });

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

// 组件参数配置
const option = {
  submitBtn: false,
};
const configObj = ref({}); // 配置对象
const rCom = shallowRef([
  {
    components: toolCom.c_set_up,
    configNme: 'setUp',
  },
]); // 当前页面组件
const oneStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleRight',
  },
  {
    components: toolCom.c_radio,
    configNme: 'toneConfig',
  },
];
const twoStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'bntBgColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'bntTxtColor',
  },
];
const twoStyle2 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'labelBgColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'labelTxtColor',
  },
];
const titleStyle = [];
const moduleStyle = [];
const moduleStyle2 = [
  // {
  //   components: toolCom.c_bg_color,
  //   configNme: 'moduleColor2',
  // },
];
const currencyStyle = [
  {
    components: toolCom.c_common_style,
    configNme: 'c_common_style',
  },
];
const setUp = ref(0);
const type = ref(0);
const type2 = ref(0);

watch(
  () => props.num,
  (nVal) => {
    // debugger;
    let value = JSON.parse(JSON.stringify(mobildConfigStore.defaultArray[nVal]));
    configObj.value = patchConfig(value);
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
    var arr = [rCom.value[0]];
    if (nVal == 0) {
      let tempArr = [
        {
          components: toolCom.c_title,
          configNme: 'titleLeft',
        },
        {
          components: toolCom.c_button_style,
          configNme: 'styleConfig',
        },
      ];
      rCom.value = arr.concat(tempArr);
    } else {
      getRComStyle(arr, type.value, type2.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.styleConfig?.tabVal,
  (nVal, oVal) => {
    type2.value = nVal;
    var arr = [rCom.value[0]];
    if (setUp.value) {
      getRComStyle(arr, type.value, nVal);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneConfig?.tabVal,
  (nVal, oVal) => {
    type.value = nVal;
    var arr = [rCom.value[0]];
    if (setUp.value) {
      getRComStyle(arr, nVal, type2.value);
    }
  },
  { deep: true },
);

onMounted(() => {
  nextTick(() => {
    let value = JSON.parse(JSON.stringify(mobildConfigStore.defaultArray[props.num]));
    configObj.value = patchConfig(value);
  });
});

function patchConfig(config) {
  if (!config.paddingConfig) {
    config.paddingConfig = {
      title: '内边距',
      val: 0,
      min: 0,
      max: 100,
      valList: [
        { val: config.topConfig ? config.topConfig.val : 0 },
        { val: config.prConfig ? config.prConfig.val : 0 },
        { val: config.bottomConfig ? config.bottomConfig.val : 0 },
        { val: config.prConfig ? config.prConfig.val : 0 },
      ],
    };
  }
  if (!config.marginConfig) {
    config.marginConfig = {
      title: '外边距',
      val: 0,
      min: 0,
      max: 100,
      valList: [{ val: config.mbConfig ? config.mbConfig.val : 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
  }
  if (!config.c_common_style) {
    config.c_common_style = {
      color: 'rgba(255,255,255,1)',
      color2: 'rgba(255,255,255,1)',
      lr: 0,
      type: 0,
    };
  }
  return config;
}

function getRComStyle(arr, type, type2) {
  if (type2 == 0) {
    if (type == 0) {
      rCom.value = [...arr, ...oneStyle, ...titleStyle, ...moduleStyle2, ...currencyStyle];
    } else {
      rCom.value = [
        ...arr,
        ...oneStyle,
        ...twoStyle,
        ...titleStyle,
        ...moduleStyle2,
        ...currencyStyle,
      ];
    }
  } else {
    if (type == 0) {
      rCom.value = [...arr, ...oneStyle, ...titleStyle, ...moduleStyle, ...currencyStyle];
    } else {
      rCom.value = [
        ...arr,
        ...oneStyle,
        ...twoStyle,
        ...twoStyle2,
        ...titleStyle,
        ...moduleStyle,
        ...currencyStyle,
      ];
    }
  }
}

function getConfig(data) {}
</script>

<style scoped></style>
