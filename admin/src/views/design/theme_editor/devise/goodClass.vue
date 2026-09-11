<template>
  <div class="goodClass">
    <el-card class="h100" :bordered="false" shadow="never">
      <!-- <div class="title">页面设置</div> -->
      <div class="list acea-row row-top">
        <div
          class="item"
          :class="activeStyle == index ? 'on' : ''"
          v-for="(item, index) in classList"
          :key="index"
          v-db-click
          @click="selectTap(index)"
        >
          <div class="pictrue"><img :src="item.image" /></div>
          <div class="name">{{ item.name }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { colorChange, getColorChange } from '@/api/theme';
import morenImg from '@/assets/imgs/moren.png';
import youxuanImg from '@/assets/imgs/youxuan.png';
import haowuImg from '@/assets/imgs/haowu.png';
import shengxianImg from '@/assets/imgs/shengxian.png';

defineOptions({ name: 'goodClass' });

const emit = defineEmits(['parentFun']);

const classList = ref([
  { image: morenImg, name: '默认模板' },
  { image: youxuanImg, name: '模板1' },
  { image: haowuImg, name: '模板2' },
  { image: shengxianImg, name: '模板3' },
]);
const activeStyle = ref('-1');

function getInfo() {
  getColorChange('category').then((res) => {
    activeStyle.value = res.data.status ? res.data.status - 1 : 0;
  });
}
function selectTap(index) {
  activeStyle.value = index;
}
function onSubmit(num) {
  emit('parentFun', true);
  activeStyle.value = num == 1 ? 0 : activeStyle.value;
  colorChange(num == 1 ? 1 : activeStyle.value + 1, 'category')
    .then((res) => {
      emit('parentFun', false);
      ElMessage.success(res.msg);
    })
    .catch((err) => {
      ElMessage.error(err.msg);
      emit('parentFun', false);
    });
}

getInfo();

defineExpose({ onSubmit, getInfo, selectTap });
</script>
<style lang="scss" scoped>
.goodClass {
  .title {
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    position: relative;
    padding-left: 11px;
    font-weight: bold;
    &:after {
      position: absolute;
      content: ' ';
      width: 2px;
      height: 14px;
      background-color: var(--prev-color-primary);
      left: 0;
      top: 3px;
    }
  }
  .list {
    .item {
      width: 264px;
      margin: 0px 30px 0 0;
      cursor: pointer;
      .pictrue {
        width: 100%;
        height: 496px;
        border: 1px solid #eeeeee;
        border-radius: 10px;
        img {
          width: 100%;
          height: 100%;
          border-radius: 10px;
        }
      }
      .name {
        font-size: 13px;
        color: rgba(0, 0, 0, 0.85);
        margin-top: 16px;
        text-align: center;
      }
      &.on {
        .pictrue {
          border: 2px solid var(--prev-color-primary);
        }
        .name {
          color: var(--prev-color-primary);
        }
      }
    }
  }
}
</style>
