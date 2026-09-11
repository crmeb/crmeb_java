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

defineOptions({ name: 'c_product_desc', componentsName: 'home_product_desc' });

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
const contentList = [
  {
    components: toolCom.c_title,
    configNme: 'contentTitle',
  },
  {
    components: toolCom.c_radio,
    configNme: 'isShow',
  },
];
const styleList = [
  {
    components: toolCom.c_title,
    configNme: 'titleStyle',
  },
  {
    components: toolCom.c_align,
    configNme: 'textPosition',
  },
  {
    components: toolCom.c_bg_color,
    configNme: 'textColor',
  },
  {
    components: toolCom.c_slider,
    configNme: 'fontSize',
  },
  {
    components: toolCom.c_common_style,
    configNme: 'currencyStyle',
  },
];
const setUp = ref(0);

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
    updateRCom(nVal);
  },
  { deep: true },
);

onMounted(() => {
  nextTick(() => {
    setConfig(mobildConfigStore.defaultArray[props.num]);
  });
});

function setConfig(data) {
  if (!data) return;
  const value = JSON.parse(JSON.stringify(data));
  configObj.value = value;
  setUp.value = value.setUp ? value.setUp.tabVal : 0;
  updateRCom(setUp.value);
}

function updateRCom(tabVal) {
  var arr = [rCom.value[0]];
  if (tabVal == 0) {
    rCom.value = arr.concat(contentList);
  } else {
    rCom.value = arr.concat(styleList);
  }
}

function getConfig(data) {}
</script>
