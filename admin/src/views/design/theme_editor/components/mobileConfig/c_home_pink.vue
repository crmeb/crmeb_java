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

defineOptions({ name: 'c_home_pink', cname: '拼团', componentsName: 'home_pink' });

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
    configNme: 'goodStyleConfig',
  },
  {
    components: toolCom.c_set_up,
    configNme: 'setUp',
  },
]);
const oneContent = [
  {
    components: toolCom.c_title,
    configNme: 'bgTitle',
  },
  {
    components: toolCom.c_radio,
    configNme: 'styleConfig',
  },
];
const oneContentImg = [
  {
    components: toolCom.c_upload_img,
    configNme: 'imgBgConfig',
  },
];
const twoContent = [
  {
    components: toolCom.c_radio,
    configNme: 'titleConfig',
  },
];
const twoContentImg = [
  {
    components: toolCom.c_upload_img,
    configNme: 'imgConfig',
  },
];
const twoContentColorImg = [
  {
    components: toolCom.c_upload_img,
    configNme: 'imgColorConfig',
  },
];
const twoContentText = [
  {
    components: toolCom.c_input_item,
    configNme: 'titleTxtConfig',
  },
];
const threeContent = [
  {
    components: toolCom.c_title,
    configNme: 'titleGoods',
  },
  {
    components: toolCom.c_slider,
    configNme: 'numberConfig',
  },
  {
    components: toolCom.c_checkbox,
    configNme: 'checkboxInfo',
  },
];
const fourContent = [
  {
    components: toolCom.c_radio,
    configNme: 'pinkConfig',
  },
];
const oneStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleRight',
  },
];
const twoStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'headerBgColor',
  },
];
const threeStyle = [
  {
    components: toolCom.c_radio,
    configNme: 'titleText',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'titleColor',
  },
  {
    components: toolCom.c_slider,
    configNme: 'titleNumber',
  },
];
const fourStyle = [];
const fourStyle2 = [];
const fourColorStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'tipsColor',
  },
];
const fourColorStyle2 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'tipsColor2',
  },
];
const fourGoodsStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'dividerColor',
  },
  {
    components: toolCom.c_title,
    configNme: 'titleGoodsStyle',
  },
  {
    components: toolCom.c_fillet,
    configNme: 'filletImg',
  },
  {
    components: toolCom.c_radio,
    configNme: 'goodsName',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'goodsNameColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'goodsPriceColor',
  },
  {
    components: toolCom.c_radio,
    configNme: 'toneConfig',
  },
];
const fiveStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'pinkPriceColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'labelColor',
  },
];
const bntStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'goodsBntColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'goodsBntTxtColor',
  },
];
const currencyStyle = [
  {
    components: toolCom.c_common_style,
    configNme: 'commonStyle',
  },
];
const setUp = ref(0);
const type = ref(0);
const type2 = ref(0);
const type3 = ref(0);
const type4 = ref(0);
const type5 = ref(0);

function buildArr() {
  return [
    {
      components: toolCom.c_card_select,
      configNme: 'goodStyleConfig',
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
    // debugger;
    let value = JSON.parse(JSON.stringify(mobildConfigStore.defaultArray[nVal]));
    configObj.value = value;
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
      getRComContent(arr, type.value, type2.value, type3.value);
    } else {
      getRComStyle(arr, type.value, type2.value, type3.value, type5.value, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.styleConfig?.tabVal,
  (nVal, oVal) => {
    type.value = nVal;
    var arr = buildArr();
    if (setUp.value == 0) {
      getRComContent(arr, nVal, type2.value, type3.value);
    } else {
      getRComStyle(arr, nVal, type2.value, type3.value, type5.value, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.titleConfig?.tabVal,
  (nVal, oVal) => {
    type2.value = nVal;
    var arr = buildArr();
    if (setUp.value == 0) {
      getRComContent(arr, type.value, nVal, type3.value);
    } else {
      getRComStyle(arr, type.value, nVal, type3.value, type5.value, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.goodStyleConfig?.tabVal,
  (nVal, oVal) => {
    type3.value = nVal;
    var arr = buildArr();
    if (setUp.value == 0) {
      getRComContent(arr, type.value, type2.value, nVal);
    } else {
      getRComStyle(arr, type.value, type2.value, nVal, type5.value, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.pinkConfig?.tabVal,
  (nVal, oVal) => {
    type5.value = nVal;
    var arr = buildArr();
    if (setUp.value) {
      getRComStyle(arr, type.value, type2.value, type3.value, nVal, type4.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneConfig?.tabVal,
  (nVal, oVal) => {
    type4.value = nVal;
    var arr = buildArr();
    if (setUp.value) {
      getRComStyle(arr, type.value, type2.value, type3.value, type5.value, nVal);
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
  return config;
}

function getRComContent(arr, typeVal, type2Val, type3Val) {
  if (typeVal == 0) {
    if (type2Val == 0) {
      if (type3Val == 2 || type3Val == 3) {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...twoContent,
          ...twoContentColorImg,
          ...threeContent,
        ];
      } else {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...twoContent,
          ...twoContentColorImg,
          ...threeContent,
          ...fourContent,
        ];
      }
    } else {
      if (type3Val == 2 || type3Val == 3) {
        rCom.value = [...arr, ...oneContent, ...twoContent, ...twoContentText, ...threeContent];
      } else {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...twoContent,
          ...twoContentText,
          ...threeContent,
          ...fourContent,
        ];
      }
    }
  } else {
    if (type2Val == 0) {
      if (type3Val == 2 || type3Val == 3) {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContentImg,
          ...twoContent,
          ...twoContentImg,
          ...threeContent,
        ];
      } else {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContentImg,
          ...twoContent,
          ...twoContentImg,
          ...threeContent,
          ...fourContent,
        ];
      }
    } else {
      if (type3Val == 2 || type3Val == 3) {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContentImg,
          ...twoContent,
          ...twoContentText,
          ...threeContent,
        ];
      } else {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContentImg,
          ...twoContent,
          ...twoContentText,
          ...threeContent,
          ...fourContent,
        ];
      }
    }
  }
}

function getRComStyle(arr, typeVal, type2Val, type3Val, type5Val, type4Val) {
  let obj = [
    ...arr,
    ...oneStyle,
    ...twoStyle,
    ...fourStyle2,
    ...fourColorStyle2,
    ...fourGoodsStyle,
    ...currencyStyle,
  ];
  let obj2 = [
    ...arr,
    ...oneStyle,
    ...twoStyle,
    ...threeStyle,
    ...fourStyle2,
    ...fourColorStyle2,
    ...fourGoodsStyle,
    ...currencyStyle,
  ];
  let obj3 = [
    ...arr,
    ...oneStyle,
    ...fourStyle,
    ...fourColorStyle,
    ...fourGoodsStyle,
    ...currencyStyle,
  ];
  let obj4 = [
    ...arr,
    ...oneStyle,
    ...threeStyle,
    ...fourStyle,
    ...fourColorStyle,
    ...fourGoodsStyle,
    ...currencyStyle,
  ];
  if (typeVal == 0) {
    if (type2Val == 0) {
      if (type3Val == 0 || type3Val == 1) {
        if (type5Val == 0) {
          if (type4Val == 0) {
            rCom.value = obj;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...twoStyle,
              ...fourStyle2,
              ...fourColorStyle2,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...bntStyle,
              ...currencyStyle,
            ];
          }
        } else {
          if (type4Val == 0) {
            rCom.value = obj;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...twoStyle,
              ...fourStyle2,
              ...fourColorStyle2,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...currencyStyle,
            ];
          }
        }
      } else {
        if (type4Val == 0) {
          rCom.value = obj;
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...fourStyle2,
            ...fourColorStyle2,
            ...fourGoodsStyle,
            ...fiveStyle,
            ...currencyStyle,
          ];
        }
      }
    } else {
      if (type3Val == 0 || type3Val == 1) {
        if (type5Val == 0) {
          if (type4Val == 0) {
            rCom.value = obj2;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...twoStyle,
              ...threeStyle,
              ...fourStyle2,
              ...fourColorStyle2,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...bntStyle,
              ...currencyStyle,
            ];
          }
        } else {
          if (type4Val == 0) {
            rCom.value = obj2;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...twoStyle,
              ...threeStyle,
              ...fourStyle2,
              ...fourColorStyle2,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...currencyStyle,
            ];
          }
        }
      } else {
        if (type4Val == 0) {
          rCom.value = obj2;
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...threeStyle,
            ...fourStyle2,
            ...fourColorStyle2,
            ...fourGoodsStyle,
            ...fiveStyle,
            ...currencyStyle,
          ];
        }
      }
    }
  } else {
    if (type2Val == 0) {
      if (type3Val == 0 || type3Val == 1) {
        if (type5Val == 0) {
          if (type4Val == 0) {
            rCom.value = obj3;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...fourStyle,
              ...fourColorStyle,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...bntStyle,
              ...currencyStyle,
            ];
          }
        } else {
          if (type4Val == 0) {
            rCom.value = obj3;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...fourStyle,
              ...fourColorStyle,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...currencyStyle,
            ];
          }
        }
      } else {
        if (type4Val == 0) {
          rCom.value = obj3;
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...fourStyle,
            ...fourColorStyle,
            ...fourGoodsStyle,
            ...fiveStyle,
            ...currencyStyle,
          ];
        }
      }
    } else {
      if (type3Val == 0 || type3Val == 1) {
        if (type5Val == 0) {
          if (type4Val == 0) {
            rCom.value = obj4;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...threeStyle,
              ...fourStyle,
              ...fourColorStyle,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...bntStyle,
              ...currencyStyle,
            ];
          }
        } else {
          if (type4Val == 0) {
            rCom.value = obj4;
          } else {
            rCom.value = [
              ...arr,
              ...oneStyle,
              ...threeStyle,
              ...fourStyle,
              ...fourColorStyle,
              ...fourGoodsStyle,
              ...fiveStyle,
              ...currencyStyle,
            ];
          }
        }
      } else {
        if (type4Val == 0) {
          rCom.value = obj4;
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...threeStyle,
            ...fourStyle,
            ...fourColorStyle,
            ...fourGoodsStyle,
            ...fiveStyle,
            ...currencyStyle,
          ];
        }
      }
    }
  }
}

function getConfig(data) {}
</script>

<style scoped></style>
