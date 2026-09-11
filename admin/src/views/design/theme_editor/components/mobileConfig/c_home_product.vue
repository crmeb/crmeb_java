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
import { ref, watch, nextTick, onMounted, shallowRef } from 'vue';
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { getProProduct } from '@/api/theme';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';
import { PRODUCT_SELECTION_TYPES } from '@/views/design/theme_editor/utils/productSelection';

defineOptions({ name: 'c_home_product', componentsName: 'home_product', cname: '商品选项卡' });

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
  // {
  //   components: toolCom.c_title,
  //   configNme: 'titleLeft',
  // },
  {
    components: toolCom.c_card_select,
    configNme: 'styleConfig',
  },
  {
    components: toolCom.c_set_up,
    configNme: 'setUp',
  },
]);
const oneContent = [
  {
    components: toolCom.c_title,
    configNme: 'titleLeft',
  },
  {
    components: toolCom.c_radio,
    configNme: 'slideConfig',
  },
  {
    components: toolCom.c_title,
    configNme: 'titleTab',
  },
  {
    components: toolCom.c_promotion,
    configNme: 'tabConfig',
  },
  {
    components: toolCom.c_title,
    configNme: 'titleCart',
  },
  {
    components: toolCom.c_radio,
    configNme: 'cartConfig',
  },
];
const twoContent = [
  {
    components: toolCom.c_button_img,
    configNme: 'bntStyleConfig',
  },
];
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
    configNme: 'decorateColor',
  },
];
const twoStyle2 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'decorateColor2',
  },
];
const threeStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'textColor2',
  },
];
const threeStyle2 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'textColor',
  },
];
const threeStyle3 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'textColor3',
  },
];
const fourStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleCart',
  },
  {
    components: toolCom.c_radio,
    configNme: 'toneCartConfig',
  },
];
const fourStyle2 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'goodsPriceColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'bntBgColor',
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

function buildArr() {
  return [
    // {
    //   components: toolCom.c_title,
    //   configNme: 'titleLeft',
    // },
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
    var arr = buildArr();
    if (nVal == 0) {
      getRComContent(arr, type3.value);
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
    var arr = buildArr();
    if (setUp.value) {
      getRComStyle(arr, nVal, type2.value, type3.value, type4.value);
    }
  },
);

watch(
  () => configObj.value?.cartConfig?.tabVal,
  (nVal, oVal) => {
    type3.value = nVal;
    var arr = buildArr();
    if (setUp.value == 0) {
      getRComContent(arr, nVal);
    } else {
      getRComStyle(arr, type.value, type2.value, nVal, type4.value);
    }
  },
);

watch(
  () => configObj.value?.toneConfig?.tabVal,
  (nVal, oVal) => {
    type2.value = nVal;
    var arr = buildArr();
    if (setUp.value) {
      getRComStyle(arr, type.value, nVal, type3.value, type4.value);
    }
  },
);

watch(
  () => configObj.value?.toneCartConfig?.tabVal,
  (nVal, oVal) => {
    type4.value = nVal;
    var arr = buildArr();
    if (setUp.value) {
      getRComStyle(arr, type.value, type2.value, type3.value, nVal);
    }
  },
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
      title: '内边距',
      val: 0,
      min: 0,
      isAll: false,
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
      title: '外边距',
      val: 0,
      min: 0,
      isAll: false,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
    if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
  }
  return data;
}

function getRComContent(arr, type3Val) {
  if (type3Val == 0) {
    rCom.value = [...arr, ...oneContent, ...twoContent];
  } else {
    rCom.value = [...arr, ...oneContent];
  }
}

function getRComStyle(arr, typeVal, type2Val, type3Val, type4Val) {
  let obj = [...arr, ...oneStyle, ...currencyStyle];
  let obj2 = [...arr, ...oneStyle, ...fourStyle, ...currencyStyle];
  if (typeVal == 0) {
    if (type2Val == 0) {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = obj2;
        } else {
          rCom.value = [...arr, ...oneStyle, ...fourStyle, ...fourStyle2, ...currencyStyle];
        }
      } else {
        rCom.value = obj;
      }
    } else {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...threeStyle,
            ...fourStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...threeStyle,
            ...fourStyle,
            ...fourStyle2,
            ...currencyStyle,
          ];
        }
      } else {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...twoStyle,
          ...threeStyle,
          ...fourStyle,
          ...currencyStyle,
        ];
      }
    }
  } else if (typeVal == 1) {
    if (type2Val == 0) {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = obj2;
        } else {
          rCom.value = [...arr, ...oneStyle, ...fourStyle, ...fourStyle2, ...currencyStyle];
        }
      } else {
        rCom.value = obj;
      }
    } else {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...threeStyle2,
            ...fourStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...threeStyle2,
            ...fourStyle,
            ...fourStyle2,
            ...currencyStyle,
          ];
        }
      } else {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...twoStyle,
          ...threeStyle2,
          ...fourStyle,
          ...currencyStyle,
        ];
      }
    }
  } else if (typeVal == 2) {
    if (type2Val == 0) {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = obj2;
        } else {
          rCom.value = [...arr, ...oneStyle, ...fourStyle, ...fourStyle2, ...currencyStyle];
        }
      } else {
        rCom.value = obj;
      }
    } else {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle2,
            ...threeStyle,
            ...fourStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle2,
            ...threeStyle,
            ...fourStyle,
            ...fourStyle2,
            ...currencyStyle,
          ];
        }
      } else {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...twoStyle2,
          ...threeStyle,
          ...fourStyle,
          ...currencyStyle,
        ];
      }
    }
  } else {
    if (type2Val == 0) {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = obj2;
        } else {
          rCom.value = [...arr, ...oneStyle, ...fourStyle, ...fourStyle2, ...currencyStyle];
        }
      } else {
        rCom.value = obj;
      }
    } else {
      if (type3Val == 0) {
        if (type4Val == 0) {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...threeStyle3,
            ...fourStyle,
            ...currencyStyle,
          ];
        } else {
          rCom.value = [
            ...arr,
            ...oneStyle,
            ...twoStyle,
            ...threeStyle3,
            ...fourStyle,
            ...fourStyle2,
            ...currencyStyle,
          ];
        }
      } else {
        rCom.value = [
          ...arr,
          ...oneStyle,
          ...twoStyle,
          ...threeStyle3,
          ...fourStyle,
          ...currencyStyle,
        ];
      }
    }
  }
}

function getConfig(data) {
  let configObjLocal = configObj.value.tabConfig.list[configObj.value.tabConfig.tabCur];
  let activeValue = configObjLocal.selectConfig.activeValue;
  if (!data.name) {
    return;
  }
  if (data.name == 'selectType' && data.values == 1) {
    return;
  }
  let typeVal = configObjLocal.tabVal;
  let dataObj = {
    page: 1,
    limit: configObjLocal.numConfig.val,
    priceOrder: configObjLocal.goodsSort == 2 ? 'desc' : '',
    salesOrder: configObjLocal.goodsSort == 1 ? 'desc' : '',
  };
  if (typeVal == 1) {
    configObj.value.productList.list = [];
    return;
  } else if (typeVal == 3) {
    // configObj.value.productList.list = [];
    dataObj.cate_id = activeValue;
  } else {
    configObjLocal.tabVal = PRODUCT_SELECTION_TYPES[0].activeValue;
    configObjLocal.productList.list = [];
    return;
  }
  getProProduct(dataObj).then((res) => {
    configObjLocal.productList.list = res.data;
  });
}
</script>

<style scoped lang="scss">
.pro {
  padding: 15px 15px 0;
  .tips {
    height: 50px;
    line-height: 50px;
    color: #999;
    font-size: 12px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  }
}
.btn-box {
  padding-bottom: 20px;
}
</style>
