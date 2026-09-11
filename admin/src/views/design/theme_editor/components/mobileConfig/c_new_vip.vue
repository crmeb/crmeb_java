<template>
  <div class="mobile-config">
    <div v-for="(item, key) in rCom" :key="key">
      <component
        :is="item.components"
        :configObj="configObj"
        ref="childData"
        :configNme="item.configNme"
        :key="key"
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
import { newcomerList } from '@/api/theme';

defineOptions({ name: 'c_new_vip', componentsName: 'home_new_vip' });

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
const twoStyle01 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'tipsColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'integralBgColor',
  },
];
const twoStyle02 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'bntColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'integralTxtColor',
  },
];
const threeStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleCoupon',
  },
  {
    components: toolCom.c_radio,
    configNme: 'toneCouponConfig',
  },
];
const fourStyle01 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'couponMoneyColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'bntTxtColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'couponTypeColor',
  },
  {
    components: toolCom.c_slider,
    configNme: 'spacingConfig',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'vipBgColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'couponBgColor',
  },
];
const fourStyle02 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'couponMoneyColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'couponBgColor2',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'bntBgColor',
  },
  {
    components: toolCom.c_slider,
    configNme: 'spacingConfig2',
  },
];
const fiveStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleGoods',
  },
  {
    components: toolCom.c_radio,
    configNme: 'toneGoodsConfig',
  },
];
const sixStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'priceColor',
  },
];
const currencyTitleStyle = [];
const moduleColorStyle = [];
const currencyStyle = [
  {
    components: toolCom.c_common_style,
    configNme: 'c_common_style',
  },
];
const setUp = ref(0);
const type = ref(0);
const type2 = ref(0);
const type3 = ref(0);
const type4 = ref(0);

watch(
  () => props.num,
  (nVal) => {
    configObj.value = patchConfig(mobildConfigStore.defaultArray[nVal]);
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
          components: toolCom.c_radio,
          configNme: 'styleConfig',
        },
        {
          components: toolCom.c_title,
          configNme: 'titleContent',
        },
        {
          components: toolCom.c_checkbox,
          configNme: 'checkboxInfo',
        },
      ];
      rCom.value = arr.concat(tempArr);
    } else {
      getRComStyle(arr, type.value, type2.value, type3.value, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.styleConfig?.tabVal,
  (nVal, oVal) => {
    type.value = nVal;
    var arr = [rCom.value[0]];
    if (setUp.value) {
      getRComStyle(arr, nVal, type2.value, type3.value, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneConfig?.tabVal,
  (nVal, oVal) => {
    type2.value = nVal;
    var arr = [rCom.value[0]];
    if (setUp.value) {
      getRComStyle(arr, type.value, nVal, type3.value, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneCouponConfig?.tabVal,
  (nVal, oVal) => {
    type3.value = nVal;
    var arr = [rCom.value[0]];
    if (setUp.value) {
      getRComStyle(arr, type.value, type2.value, nVal, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneGoodsConfig?.tabVal,
  (nVal, oVal) => {
    type4.value = nVal;
    var arr = [rCom.value[0]];
    if (setUp.value) {
      getRComStyle(arr, type.value, type2.value, type3.value, nVal);
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
      isAll: false,
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
      isAll: false,
      title: '外边距',
      val: 0,
      min: 0,
      max: 100,
      valList: [{ val: 0 }, { val: 0 }, { val: config.mbConfig ? config.mbConfig.val : 0 }, { val: 0 }],
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

function getRComStyle(arr, type, type2, type3, type4) {
  if (type == 0) {
    if (type2 == 0) {
      if (type3 == 0) {
        if (type4 == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...threeStyle,
            ...fiveStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...threeStyle,
            ...fiveStyle,
            ...sixStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        }
      } else {
        if (type4 == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...threeStyle,
            ...fourStyle01,
            ...fiveStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...threeStyle,
            ...fourStyle01,
            ...fiveStyle,
            ...sixStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        }
      }
    } else {
      if (type3 == 0) {
        if (type4 == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle01,
            ...threeStyle,
            ...fiveStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle01,
            ...threeStyle,
            ...fiveStyle,
            ...sixStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        }
      } else {
        if (type4 == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle01,
            ...threeStyle,
            ...fourStyle01,
            ...fiveStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle01,
            ...threeStyle,
            ...fourStyle01,
            ...fiveStyle,
            ...sixStyle,
            ...currencyTitleStyle,
            ...currencyStyle,
          ];
        }
      }
    }
  } else {
    if (type2 == 0) {
      if (type3 == 0) {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...threeStyle,
          ...currencyTitleStyle,
          ...moduleColorStyle,
          ...currencyStyle,
        ];
      } else {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...threeStyle,
          ...fourStyle02,
          ...currencyTitleStyle,
          ...moduleColorStyle,
          ...currencyStyle,
        ];
      }
    } else {
      if (type3 == 0) {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...twoStyle02,
          ...threeStyle,
          ...currencyTitleStyle,
          ...moduleColorStyle,
          ...currencyStyle,
        ];
      } else {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...twoStyle02,
          ...threeStyle,
          ...fourStyle02,
          ...currencyTitleStyle,
          ...moduleColorStyle,
          ...currencyStyle,
        ];
      }
    }
  }
}

// 获取组件参数
// getConfig (data) {
//     newcomerList({
//         page: 1,
//         limit: configObj.value.numConfig.val,
//         priceOrder: configObj.value.itemSort.type == 2 ? 'desc' : '',
//         salesOrder: configObj.value.itemSort.type == 1 ? 'desc' : ''
//     }).then(res=>{
//         configObj.value.newVipList.list = res.data;
//     }).catch(err=>{
//        return ElMessage.error(err.msg);
//     })
// },
function handleSubmit(name) {
  let obj = {};
  obj.activeIndex = props.activeIndex;
  obj.data = configObj.value;
  mobildConfigStore.UPDATEARR(obj);
}
</script>

<style scoped></style>
