<template>
  <div class="iconBox">
    <div class="icon-toolbar">
      <el-input
        ref="searchRef"
        v-model="iconVal"
        placeholder="输入关键词搜索,注意全是英文"
        clearable
        @input="upIcon(iconVal)"
      />
    </div>
    <div class="icons-container">
      <div class="grid">
        <div v-for="item of list" :key="item">
          <el-tooltip placement="top">
            <template #content>
              <div>{{ item }}</div>
            </template>
            <div class="icon-item" @click.stop="handleClipboard(item)">
              <el-icon>
                <component :is="getIconComponent(item)" v-if="getIconComponent(item)" />
                <i :class="'el-icon-' + item" v-else />
              </el-icon>
              <span>{{ item }}</span>
            </div>
          </el-tooltip>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import elementIcons from './element-icons';

defineOptions({ name: 'Index' });

const emit = defineEmits(['getIcon']);

const elementIconsRef = elementIcons;
const iconVal = ref('');
const list = ref([]);
const searchRef = ref(null);

onMounted(() => {
  list.value = elementIconsRef;
});

function handleClipboard(item) {
  emit('getIcon', item);
}

function getIconComponent(name) {
  const componentName = name
    .split('-')
    .map((item) => item.charAt(0).toUpperCase() + item.slice(1))
    .join('');
  return ElementPlusIconsVue[componentName];
}

// 搜索
function upIcon(n) {
  if (!n) {
    list.value = elementIconsRef;
    return;
  }
  list.value = elementIconsRef.filter((name) => name.indexOf(n) !== -1);
}
</script>

<style lang="scss" scoped>
.iconBox {
  width: 820px;
  max-width: 100%;
}

.icon-toolbar {
  padding: 0 4px 12px;

  .el-input {
    width: 300px;
  }
}

.icons-container {
  max-height: 470px;
  overflow: auto;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: #fff;

  .grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(102px, 1fr));
    gap: 0;
    padding: 8px;
  }

  .icon-item {
    display: flex;
    height: 82px;
    padding: 8px 6px;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    gap: 8px;
    border: 1px solid #edf0f5;
    border-radius: 4px;
    box-sizing: border-box;
    text-align: center;
    color: #24292e;
    cursor: pointer;
    transition:
      border-color 0.15s ease,
      background-color 0.15s ease,
      color 0.15s ease;

    &:hover {
      color: var(--el-color-primary);
      border-color: var(--el-color-primary-light-5);
      background: var(--el-color-primary-light-9);
    }

    .el-icon {
      flex: none;
      font-size: 24px;
      line-height: 1;
    }
  }

  span {
    display: block;
    width: 100%;
    font-size: 12px;
    font-weight: 400;
    line-height: 16px;
    color: #606266;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .disabled {
    pointer-events: none;
  }
}
</style>
