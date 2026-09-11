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

defineOptions({ name: 'c_home_coupon', componentsName: 'home_coupon' });

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
const rCom = shallowRef([
  {
    components: toolCom.c_card_select,
    configNme: 'styleConfig',
  },
  {
    components: toolCom.c_set_up,
    configNme: 'setUp',
  },
]);
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
    configNme: 'couponMoneyColor',
  },
];
const bntBgStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'bntBgColor',
  },
];
const couponBgStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'couponBgColor',
  },
];
const currencyTitleStyle = [
  {
    components: toolCom.c_slider,
    configNme: 'spacingConfig',
  },
];
const moduleColorStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'moduleColor',
  },
];
const moduleColorStyle2 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'moduleColor2',
  },
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

function buildArr() {
  return [
    {
      components: toolCom.c_card_select,
      configNme: 'styleConfig',
    },
    {
      components: toolCom.c_set_up,
      configNme: 'setUp',
    },
  ];
}

watch(
  () => props.num,
  (nVal) => {
    configObj.value = mobildConfigStore.defaultArray[nVal];
    configObj.value = patchConfig(configObj.value);
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
    var arr = buildArr();
    if (nVal == 0) {
      let tempArr = [
        {
          components: toolCom.c_title,
          configNme: 'titleData',
        },
        {
          components: toolCom.c_slider,
          configNme: 'numberConfig',
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
    type.value = nVal;
    var arr = buildArr();
    if (setUp.value) {
      getRComStyle(arr, nVal, type2.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneConfig?.tabVal,
  (nVal, oVal) => {
    type2.value = nVal;
    var arr = buildArr();
    if (setUp.value) {
      getRComStyle(arr, type.value, nVal);
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

function patchConfig(data) {
  if (!data) return data;
  if (!data.paddingConfig) {
    data.paddingConfig = {
      isAll: false,
      title: '内边距',
      val: 0,
      min: 0,
      max: 100,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
    if (data.topConfig) data.paddingConfig.valList[0].val = data.topConfig.val;
    if (data.prConfig) {
      data.paddingConfig.valList[1].val = data.prConfig.val;
      data.paddingConfig.valList[3].val = data.prConfig.val;
    }
    if (data.bottomConfig) data.paddingConfig.valList[2].val = data.bottomConfig.val;
  }
  if (!data.marginConfig) {
    data.marginConfig = {
      isAll: false,
      title: '外边距',
      val: 0,
      min: 0,
      max: 100,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
    if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
  }
  if (!data.c_common_style) {
    data.c_common_style = {
      color: 'rgba(255,255,255,1)',
      color2: 'rgba(255,255,255,1)',
      lr: 0,
      type: 0,
    };
  }
  return data;
}

function getRComStyle(arr, typeVal, type2Val) {
  if (typeVal == 0 || typeVal == 3) {
    if (type2Val == 0) {
      rCom.value = [...arr, ...oneStyle, ...currencyStyle];
    } else {
      rCom.value = [
        ...arr,
        ...oneStyle,
        ...twoStyle,
        ...bntBgStyle,
        ...couponBgStyle,
        ...currencyStyle,
      ];
    }
  } else if (typeVal == 1) {
    if (type2Val == 0) {
      rCom.value = [...arr, ...oneStyle, ...currencyStyle];
    } else {
      rCom.value = [...arr, ...oneStyle, ...twoStyle, ...bntBgStyle, ...currencyStyle];
    }
  } else if (typeVal == 2) {
    if (type2Val == 0) {
      rCom.value = [...arr, ...oneStyle, ...currencyStyle];
    } else {
      rCom.value = [...arr, ...oneStyle, ...twoStyle, ...currencyStyle];
    }
  } else {
    if (type2Val == 0) {
      rCom.value = [...arr, ...oneStyle, ...currencyStyle];
    } else {
      rCom.value = [...arr, ...oneStyle, ...twoStyle, ...bntBgStyle, ...currencyStyle];
    }
  }
}

// 获取组件参数
function getConfig(data) {}

function handleSubmit(name) {
  let obj = {};
  obj.activeIndex = props.activeIndex;
  obj.data = configObj.value;
  mobildConfigStore.UPDATEARR(obj);
}
</script>

<style scoped lang="scss">
.title-tips {
  padding-bottom: 10px;
  font-size: 14px;
  color: #333;
  span {
    margin-right: 14px;
    color: #999;
  }
}
</style>
