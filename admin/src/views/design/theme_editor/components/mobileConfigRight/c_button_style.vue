<template>
  <div>
    <div class="button-style acea-row row-middle">
      <div class="title-tips" v-if="configData">
        <span>{{ configData.title }}</span>
      </div>
      <div class="style-box acea-row row-middle">
        <div class="bnt" @click="styleTap">修改风格</div>
        <div class="name">当前：样式{{ configData.tabVal + 1 }}</div>
      </div>
    </div>
    <el-dialog
      v-model="modals"
      title="风格选择器"
      height="500"
      :width="configData.type == 'signIn' || configData.type == 'ranking' ? '630px' : '950px'"
    >
      <div class="list acea-row row-middle">
        <div
          class="item"
          :class="current == index ? 'on' : ''"
          v-for="(item, index) in list"
          :key="index"
          @click="tap(index)"
        >
          <div class="pictrue acea-row row-center-wrapper">
            <img
              :src="item.url"
              :style="{
                width: item.width + 'px',
                height: item.height + 'px',
              }"
            />
            <span class="iconfont icona-zu80222" v-if="current == index"></span>
          </div>
          <div class="name">风格{{ index + 1 }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button v-db-click @click="cancel">取 消</el-button>
          <el-button type="primary" v-db-click @click="ok">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import tab02 from '@/assets/images/tab02.png';
import tab03 from '@/assets/images/tab03.png';
import tab01 from '@/assets/images/tab01.png';
import signIn01 from '@/assets/images/signIn01.png';
import signIn02 from '@/assets/images/signIn02.png';
import ranking01 from '@/assets/images/ranking01.png';
import ranking02 from '@/assets/images/ranking02.png';
import coupon01 from '@/assets/images/coupon01.png';
import coupon02 from '@/assets/images/coupon02.png';
import coupon03 from '@/assets/images/coupon03.png';
import coupon04 from '@/assets/images/coupon04.png';
import coupon05 from '@/assets/images/coupon05.png';
import cube2 from '@/assets/images/cube2.png';
import cube3 from '@/assets/images/cube3.png';
import cube4 from '@/assets/images/cube4.png';
import cube5 from '@/assets/images/cube5.png';
import cube6 from '@/assets/images/cube6.png';
import cube7 from '@/assets/images/cube7.png';
import cube8 from '@/assets/images/cube8.png';
import cube9 from '@/assets/images/cube9.png';
import cube10 from '@/assets/images/cube10.png';
import cube11 from '@/assets/images/cube11.png';
import cube12 from '@/assets/images/cube12.png';

defineOptions({ name: 'c_button_style' });

const props = defineProps({
  configObj: {
    type: Object,
  },
  configNme: {
    type: String,
  },
});

const defaults = ref({});
const configData = ref({});
const modals = ref(false);
const current = ref(0);
const navBar = ref([
  {
    url: tab02,
    width: 220,
    height: 24,
  },
  {
    url: tab03,
    width: 220,
    height: 24,
  },
  {
    url: tab01,
    width: 220,
    height: 24,
  },
]);
const signIn = ref([
  {
    url: signIn01,
    width: 220,
    height: 64,
  },
  {
    url: signIn02,
    width: 220,
    height: 59,
  },
]);
const ranking = ref([
  {
    url: ranking01,
    width: 200,
    height: 172,
  },
  {
    url: ranking02,
    width: 200,
    height: 167,
  },
]);
const coupon = ref([
  {
    url: coupon01,
    width: 220,
    height: 69,
  },
  {
    url: coupon02,
    width: 220,
    height: 87,
  },
  {
    url: coupon03,
    width: 220,
    height: 62,
  },
  {
    url: coupon04,
    width: 220,
    height: 69,
  },
  {
    url: coupon05,
    width: 220,
    height: 49,
  },
]);
const pictureCube = ref([
  {
    url: cube2,
    width: 130,
    height: 129,
    count: 2,
  },
  {
    url: cube3,
    width: 130,
    height: 129,
    count: 2,
  },
  {
    url: cube4,
    width: 130,
    height: 129,
    count: 3,
  },
  {
    url: cube5,
    width: 130,
    height: 129,
    count: 3,
  },
  {
    url: cube6,
    width: 130,
    height: 129,
    count: 3,
  },
  {
    url: cube7,
    width: 130,
    height: 129,
    count: 3,
  },
  {
    url: cube8,
    width: 130,
    height: 129,
    count: 3,
  },
  {
    url: cube9,
    width: 130,
    height: 129,
    count: 4,
  },
  {
    url: cube10,
    width: 130,
    height: 129,
    count: 5,
  },
  {
    url: cube11,
    width: 130,
    height: 129,
    count: 4,
  },
  {
    url: cube12,
    width: 130,
    height: 129,
    count: 1,
  },
  // {
  //   url: cube1,
  //   width: 130,
  //   height: 130,
  //   count: 16,
  // },
]);
const list = ref([]);

watch(
  () => props.configObj,
  (nVal) => {
    defaults.value = nVal;
    configData.value = nVal[props.configNme] || {};
  },
  { deep: true },
);

onMounted(() => {
  nextTick(() => {
    defaults.value = props.configObj;
    configData.value = props.configObj[props.configNme] || {};
    nextTick((e) => {
      current.value = configData.value.tabVal;
    });
    switch (configData.value.type) {
      case 'navBar':
        list.value = navBar.value;
        break;
      case 'signIn':
        list.value = signIn.value;
        break;
      case 'ranking':
        list.value = ranking.value;
        break;
      case 'coupon':
        list.value = coupon.value;
        break;
      case 'pictureCube':
        list.value = pictureCube.value;
        break;
    }
  });
});

function tap(index) {
  current.value = index;
}
function styleTap() {
  modals.value = true;
}
function cancel() {
  modals.value = false;
}
function ok() {
  modals.value = false;
  configData.value.tabVal = current.value;
  configData.value.count = list.value[current.value].count;
  if (defaults.value.picStyle) {
    defaults.value.picStyle.tabVal = 0;
  }
}
</script>

<style scoped lang="scss">
:deep(.ivu-modal-body ){
  max-height: 623px;
  overflow: auto;
}
.list {
  padding-left: 8px;
  max-height: 50vh;
  .item {
    margin-right: 11px;
    text-align: center;
    cursor: pointer;

    &.on {
      .pictrue {
        border-color: var(--prev-color-primary);
      }
    }

    .name {
      color: #3d3d3d;
      font-size: 14px;
      margin: 12px 0;
    }

    &:nth-of-type(3n) {
      margin-right: 0;
    }
    .pictrue {
      width: 273px;
      height: 218px;
      background: #f5f5f5;
      border-radius: 4px;
      border: 1px solid #dddddd;
      position: relative;

      img {
        display: block;
      }

      .iconfont {
        position: absolute;
        right: -1px;
        bottom: -4px;
        color: var(--prev-color-primary);
      }
    }
  }
}
.button-style {
  padding: 0 15px;
  margin-bottom: 20px;

  .title-tips {
    margin-right: 14px;
    color: #999;
    font-size: 12px;
    width: 82px;
  }

  .style-box {
    .bnt {
      width: 94px;
      height: 32px;
      background: var(--prev-color-primary);
      border-radius: 4px;
      text-align: center;
      line-height: 32px;
      color: #fff;
      font-size: 12px;
      cursor: pointer;
    }
    .name {
      color: #999999;
      font-size: 12px;
      margin-left: 12px;
    }
  }
}
</style>
