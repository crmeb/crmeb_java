<template>
  <div class="goodClass">
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
        <div class="pictrue" :style="{ backgroundColor: themeColor }"><img :src="item.image" /></div>
        <div class="name">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRoute, useRouter } from 'vue-router';
import { themeInfo, themeSave } from '@/api/theme';
import setting from '@/utils/settingMer';
import morenImg from '@/assets/imgs/moren.png';
import youxuanImg from '@/assets/imgs/youxuan.png';
import haowuImg from '@/assets/imgs/haowu.png';
import shengxianImg from '@/assets/imgs/shengxian.png';

defineOptions({ name: 'goodClass' });

const emit = defineEmits(['parentFun']);

const route = useRoute();
const router = useRouter();

const classList = [
  { image: morenImg, name: '默认模板' },
  { image: youxuanImg, name: '模板1' },
  { image: haowuImg, name: '模板2' },
  { image: shengxianImg, name: '模板3' },
];
const activeStyle = ref('-1');
const themeColor = ref('');

function getTheme() {
  themeInfo(route.query.id, 'theme').then((res) => {
    themeColor.value = res.data ? res.data.theme_color : '#E93323';
  });
}
function getInfo() {
  themeInfo(route.query.id, 'category').then((res) => {
    activeStyle.value = res.data.status ? res.data.status - 1 : 0;
  });
}
function selectTap(index) {
  activeStyle.value = index;
}
function saveOnly(num) {
  emit('parentFun', true);
  activeStyle.value = num == 1 ? 0 : activeStyle.value;
  themeSave(route.query.id, {
    type: 'category',
    value: num == 1 ? 1 : activeStyle.value + 1,
  }).then((res) => {
    if (route.query.id == 0) {
      router.replace({ query: { ...route.query, id: res.data.id } });
    }
    ElMessage.success(res.msg);
  });
}
function saveAndClose() {
  // 先触发父组件事件
  emit('parentFun', true);

  // 保存数据
  themeSave(route.query.id, {
    type: 'category',
    value: activeStyle.value + 1,
  })
    .then((res) => {
      // 如果是新建（id为0），更新路由参数
      if (route.query.id == 0) {
        router.replace({ query: { ...route.query, id: res.data.id } });
      }

      // 显示成功消息
      ElMessage.success(res.msg);

      // 保存成功后跳转回主题列表页面
      router.push('/design/my_theme');
    })
    .catch((err) => {
      // 保存失败时的处理
      ElMessage.error(err.msg || '保存失败');
    });
}

// created 等价逻辑
getInfo();
getTheme();

defineExpose({ saveOnly, saveAndClose });
</script>
<style lang="scss" scoped>
.goodClass {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
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
