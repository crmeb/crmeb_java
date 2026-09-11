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
        :number="num"
        :num="item.num"
      ></component>
    </div>
    <rightBtn :activeIndex="activeIndex" :configObj="configObj"></rightBtn>
  </div>
</template>

<script setup>
import { ref, shallowRef, watch, nextTick, onMounted } from 'vue';
import { getCategory as getCategoryApi, getProProduct } from '@/api/theme';
import toolCom from '@/views/design/theme_editor/components/mobileConfigRight/index.js';
import rightBtn from '@/views/design/theme_editor/components/rightBtn/index.vue';
import { useMobildConfigStore } from '@/store/modules/mobildConfig';
import { PRODUCT_SELECTION_TYPES } from '@/views/design/theme_editor/utils/productSelection';

defineOptions({ name: 'c_home_goods_list', componentsName: 'home_goods_list', cname: '产品列表' });

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
    configNme: 'titleGoods',
  },
  {
    components: toolCom.c_select,
    configNme: 'typeConfig',
  },
];
const oneContent1 = [
  {
    components: toolCom.c_goods,
    configNme: 'goodsList',
  },
];
const oneContent2 = [
  {
    components: toolCom.c_brand,
    configNme: 'brandList',
  },
  {
    components: toolCom.c_slider,
    configNme: 'numberConfig',
  },
  {
    components: toolCom.c_radio,
    configNme: 'goodsSort',
  },
];
const oneContent3 = [
  {
    components: toolCom.c_classify,
    configNme: 'classList',
  },
  {
    components: toolCom.c_slider,
    configNme: 'numberConfig',
  },
  {
    components: toolCom.c_radio,
    configNme: 'goodsSort',
  },
];
const oneContent4 = [
  {
    components: toolCom.c_slider,
    configNme: 'numberConfig',
  },
  {
    components: toolCom.c_radio,
    configNme: 'goodsSort',
  },
];
const twoContent = [
  {
    components: toolCom.c_title,
    configNme: 'titleContents',
  },
  {
    components: toolCom.c_checkbox,
    configNme: 'checkboxInfo',
  },
];
const threeContent = [
  {
    components: toolCom.c_title,
    configNme: 'titleCart',
  },
  {
    components: toolCom.c_radio,
    configNme: 'cartConfig',
  },
];
const threeContent1 = [
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
    components: toolCom.c_fillet,
    configNme: 'filletImg',
  },
  {
    components: toolCom.c_radio,
    configNme: 'goodsName',
  },
  {
    components: toolCom.c_radio,
    configNme: 'toneConfig',
  },
];
const oneStyle1 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'goodsNameColor',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'goodsPriceColor',
  },
];
const oneStyle2 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'soldNumColor',
  },
];
const oneStyle3 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'scoreColor',
  },
];
const twoStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleCart',
  },
  {
    components: toolCom.c_radio,
    configNme: 'toneCartConfig',
  },
];
const twoStyle1 = [
  {
    components: toolCom.c_bg_color,
    configNme: 'bntBgColor',
  },
];
const currencyTitleStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleCurrency',
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
const lockStatus = ref(false);

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
    let value = JSON.parse(JSON.stringify(mobildConfigStore.defaultArray[nVal]));
    configObj.value = patchConfig(value);
    if (!value.selectConfig.list || !value.selectConfig.list[0].value) {
      getCategory();
    }
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
      getRComStyle(arr, type.value, type3.value, type4.value, type5.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.styleConfig?.tabVal,
  (nVal, oVal) => {
    type.value = nVal;
    var arr = buildArr();
    if (setUp.value === 0) {
      getRComContent(arr, nVal, type2.value, type3.value);
    } else {
      getRComStyle(arr, nVal, type3.value, type4.value, type5.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.typeConfig?.activeValue,
  (nVal, oVal) => {
    type2.value = nVal;
    var arr = buildArr();
    if (setUp.value === 0) {
      getRComContent(arr, type.value, nVal, type3.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.cartConfig?.tabVal,
  (nVal, oVal) => {
    type3.value = nVal;
    var arr = buildArr();
    if (setUp.value === 0) {
      getRComContent(arr, type.value, type2.value, nVal);
    } else {
      getRComStyle(arr, type.value, nVal, type4.value, type5.value);
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
      getRComStyle(arr, type.value, type3.value, nVal, type5.value);
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneCartConfig?.tabVal,
  (nVal, oVal) => {
    type5.value = nVal;
    var arr = [
      {
        components: toolCom.c_title,
        configNme: 'titleLeft',
      },
      {
        components: toolCom.c_card_select,
        configNme: 'styleConfig',
      },
      {
        components: toolCom.c_set_up,
        configNme: 'setUp',
      },
    ];
    if (setUp.value) {
      getRComStyle(arr, type.value, type3.value, type4.value, nVal);
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
      max: 100,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
    if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
  }
  return data;
}

function getCategory() {
  getCategoryApi().then((res) => {
    configObj.value.selectConfig.list = res.data;
  });
}

function getRComContent(arr, typeVal, type2Val, type3Val) {
  if (typeVal == 3) {
    if (type2Val == 1) {
      rCom.value = [...arr, ...oneContent, ...oneContent1, ...twoContent];
    } else if (type2Val == 2) {
      rCom.value = [...arr, ...oneContent, ...oneContent2, ...twoContent];
    } else if (type2Val == 3) {
      rCom.value = [...arr, ...oneContent, ...oneContent3, ...twoContent];
    } else {
      rCom.value = [...arr, ...oneContent, ...oneContent4, ...twoContent];
    }
  } else {
    if (type2Val == 1) {
      if (type3Val == 0) {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContent1,
          ...twoContent,
          ...threeContent,
          ...threeContent1,
        ];
      } else {
        rCom.value = [...arr, ...oneContent, ...oneContent1, ...twoContent, ...threeContent];
      }
    } else if (type2Val == 2) {
      if (type3Val == 0) {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContent2,
          ...twoContent,
          ...threeContent,
          ...threeContent1,
        ];
      } else {
        rCom.value = [...arr, ...oneContent, ...oneContent2, ...twoContent, ...threeContent];
      }
    } else if (type2Val == 3) {
      if (type3Val == 0) {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContent3,
          ...twoContent,
          ...threeContent,
          ...threeContent1,
        ];
      } else {
        rCom.value = [...arr, ...oneContent, ...oneContent3, ...twoContent, ...threeContent];
      }
    } else {
      if (type3Val == 0) {
        rCom.value = [
          ...arr,
          ...oneContent,
          ...oneContent4,
          ...twoContent,
          ...threeContent,
          ...threeContent1,
        ];
      } else {
        rCom.value = [...arr, ...oneContent, ...oneContent4, ...twoContent, ...threeContent];
      }
    }
  }
}

function getRComStyle(arr, typeVal, type3Val, type4Val, type5Val) {
  let obj4 = [],
    currencyStyleLocal = [];
  if (type4Val) {
    if (typeVal == 1 || typeVal == 4) {
      obj4 = [...oneStyle1, ...oneStyle2];
      currencyStyleLocal = [...currencyStyle];
    } else if (typeVal == 0) {
      obj4 = [...oneStyle1, ...oneStyle2, ...oneStyle3];
      currencyStyleLocal = [...currencyStyle];
    } else if (typeVal == 2 || typeVal == 3) {
      obj4 = [...oneStyle1];
      currencyStyleLocal = [...currencyStyle];
    } else {
      obj4 = [...oneStyle1, ...oneStyle2];
      currencyStyleLocal = [...currencyStyle];
    }
  } else {
    if (typeVal == 0 || typeVal == 1 || typeVal == 4) {
      currencyStyleLocal = [...currencyStyle];
    } else {
      currencyStyleLocal = [...currencyStyle];
    }
  }
  let obj5 = [];
  if (typeVal != 3) {
    if (type5Val) {
      obj5 = [...twoStyle, ...twoStyle1];
    } else {
      obj5 = [...twoStyle];
    }
  }
  if (type3Val == 0) {
    rCom.value = [...arr, ...oneStyle, ...obj4, ...obj5, ...currencyStyleLocal];
  } else {
    rCom.value = [...arr, ...oneStyle, ...obj4, ...currencyStyleLocal];
  }
}

function getConfig(data, name) {
  if (name != 'radio' && (typeof data !== 'object' || data === null)) {
    return;
  }
  let typeVal = configObj.value.typeConfig.activeValue;
  let dataObj = {
    page: 1,
    limit: configObj.value.numberConfig.val,
    priceOrder: configObj.value.goodsSort.tabVal == 2 ? 'desc' : '',
    salesOrder: configObj.value.goodsSort.tabVal == 1 ? 'desc' : '',
  };
  if (typeVal == 1) {
    configObj.value.productList.list = [];
    return;
  } else if (typeVal == 3) {
    dataObj.cate_id = configObj.value.classList.classVal;
  } else {
    configObj.value.typeConfig.activeValue = PRODUCT_SELECTION_TYPES[0].activeValue;
    configObj.value.productList.list = [];
    return;
  }
  getProProduct(dataObj).then((res) => {
    configObj.value.productList.list = res.data;
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
