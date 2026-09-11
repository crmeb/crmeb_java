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

defineOptions({ name: 'c_nav_bar', componentsName: 'nav_bar', cname: '导航' });

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
  //     components: toolCom.c_title,
  //     configNme: 'titleLeft'
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
    configNme: 'stickyConfig',
  },
  {
    components: toolCom.c_title,
    configNme: 'titleTab',
  },
  {
    components: toolCom.c_tab_list,
    configNme: 'tabListConfig',
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
    configNme: 'textColor',
  },
];
const fourStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'textColor2',
  },
];
const fiveStyle = [
  {
    components: toolCom.c_bg_color,
    configNme: 'textColor3',
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
    var arr = [
      // {
      //     components: toolCom.c_title,
      //     configNme: 'titleLeft'
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
    if (nVal == 0) {
      rCom.value = arr.concat(oneContent);
    } else {
      if (type.value == 0) {
        if (type2.value == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle, ...threeStyle, ...currencyStyle];
        }
      } else if (type.value == 1) {
        if (type2.value == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle2, ...fourStyle, ...currencyStyle];
        }
      } else {
        if (type2.value == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle, ...fiveStyle, ...currencyStyle];
        }
      }
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.styleConfig?.tabVal,
  (nVal, oVal) => {
    type.value = nVal;
    var arr = [
      // {
      //     components: toolCom.c_title,
      //     configNme: 'titleLeft'
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
    if (setUp.value) {
      if (nVal == 0) {
        if (type2.value == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle, ...threeStyle, ...currencyStyle];
        }
      } else if (nVal == 1) {
        if (type2.value == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle2, ...fourStyle, ...currencyStyle];
        }
      } else {
        if (type2.value == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle, ...fiveStyle, ...currencyStyle];
        }
      }
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.toneConfig?.tabVal,
  (nVal, oVal) => {
    type2.value = nVal;
    var arr = [
      // {
      //     components: toolCom.c_title,
      //     configNme: 'titleLeft'
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
    if (setUp.value) {
      if (type.value == 0) {
        if (nVal == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle, ...threeStyle, ...currencyStyle];
        }
      } else if (type.value == 1) {
        if (nVal == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle2, ...fourStyle, ...currencyStyle];
        }
      } else {
        if (nVal == 0) {
          rCom.value = [...arr, ...oneStyle, ...currencyStyle];
        } else {
          rCom.value = [...arr, ...oneStyle, ...twoStyle, ...fiveStyle, ...currencyStyle];
        }
      }
    }
  },
  { deep: true },
);

onMounted(() => {
  nextTick(() => {
    let value = JSON.parse(JSON.stringify(mobildConfigStore.defaultArray[props.num]));
    configObj.value = value;
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
  return data;
}

// 获取组件参数
function getConfig(data) {}
</script>

<style scoped></style>
