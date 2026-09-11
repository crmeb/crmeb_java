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

defineOptions({ name: 'c_home_title', componentsName: 'home_title' });

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
const rComContent = [
  {
    components: toolCom.c_title,
    configNme: 'titleLeft',
  },
  {
    components: toolCom.c_input_item,
    configNme: 'titleConfig',
  },
  {
    components: toolCom.c_radio,
    configNme: 'buttonConfig',
  },
];
const oneContent = [
  {
    components: toolCom.c_input_item,
    configNme: 'titleConfigRight',
  },
  {
    components: toolCom.c_input_item,
    configNme: 'linkConfig',
  },
];
const oneStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleRight',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'themeColor',
  },
  {
    components: toolCom.c_slider,
    configNme: 'fontSize',
  },
  {
    components: toolCom.c_radio,
    configNme: 'textStyle',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'buttonColor',
  },
  {
    components: toolCom.c_slider,
    configNme: 'buttonText',
  },
];
const twoStyle = [
  {
    components: toolCom.c_title,
    configNme: 'titleRight',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'themeColor',
  },
  {
    components: toolCom.c_slider,
    configNme: 'fontSize',
  },
  {
    components: toolCom.c_radio,
    configNme: 'textPosition',
  },
  {
    components: toolCom.c_radio,
    configNme: 'textStyle',
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

watch(
  () => props.num,
  (nVal) => {
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
      if (type.value == 0) {
        let rComTemp = arr.concat(rComContent);
        rCom.value = rComTemp.concat(oneContent);
      } else {
        rCom.value = arr.concat(rComContent);
      }
    } else {
      if (type.value == 0) {
        let rComTemp = arr.concat(oneStyle);
        rCom.value = rComTemp.concat(currencyStyle);
      } else {
        let rComTemp = arr.concat(twoStyle);
        rCom.value = rComTemp.concat(currencyStyle);
      }
    }
  },
  { deep: true },
);

watch(
  () => configObj.value?.buttonConfig?.tabVal,
  (nVal, oVal) => {
    type.value = nVal;
    var arr = [rCom.value[0]];
    if (setUp.value) {
      if (nVal == 0) {
        let rComTemp = arr.concat(oneStyle);
        rCom.value = rComTemp.concat(currencyStyle);
      } else {
        let rComTemp = arr.concat(twoStyle);
        rCom.value = rComTemp.concat(currencyStyle);
      }
    } else {
      if (nVal == 0) {
        let rComTemp = arr.concat(rComContent);
        rCom.value = rComTemp.concat(oneContent);
      } else {
        rCom.value = arr.concat(rComContent);
      }
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
      max: 100,
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
      isAll: false,
      valList: [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }],
    };
    if (data.mbConfig) data.marginConfig.valList[0].val = data.mbConfig.val;
  }
  return data;
}

// 获取组件参数
function getConfig(data) {}
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
