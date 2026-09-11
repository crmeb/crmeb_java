<template>
  <div class="c_product" v-if="configData">
    <div class="title">{{ configData.title }}</div>
    <div class="list-box">
      <draggable
        class="dragArea list-group"
        :list="configData.list"
        :item-key="getDraggableItemKey"
        group="peoples"
        handle=".move-icon"
      >
        <template #item="{ element: item, index }">
          <div class="item">
            <div class="move-icon">
              <span class="iconfont iconxingzhuangjiehe"></span>
            </div>
            <div>
              <div class="con-item">
                <span>{{ item.text.title }}</span>
                <div>
                  <el-input
                    v-model="item.text.val"
                    :placeholder="item.text.pla"
                    :maxlength="item.text.max"
                    show-word-limit
                  />
                </div>
              </div>
              <div class="con-item">
                <span>{{ item.dataType.title }}</span>
                <div>
                  <el-radio-group v-model="item.dataType.tabVal">
                    <el-radio :label="key" :value="key" v-for="(radio, key) in item.dataType.tabList" :key="key">
                      <span>{{ radio.name }}</span>
                    </el-radio>
                  </el-radio-group>
                </div>
              </div>
              <div class="con-item">
                <span>{{ item.dataType.tabList[item.dataType.tabVal].name }}</span>
                <div>
                  <el-input v-if="item.dataType.tabVal == 0" v-model="item.microPage.name" placeholder="选择页面">
                    <template #suffix>
                      <i class="el-icon-link" @click="getLink(index)" />
                    </template>
                  </el-input>
                  <el-cascader
                    v-else-if="item.dataType.tabVal == 1"
                    @change="(e) => sliderChange(e, index)"
                    placeholder="请选择分类"

                    v-model="item.classPage.id"
                    :options="treeSelect"
                    :props="cascaderProps"
                    filterable
                    clearable
                  >
                  </el-cascader>
                </div>
              </div>
            </div>
            <div class="delete" @click.stop="bindDelete(index)">
              <i class="el-icon-circle-close" style="font-size: 20px" />
            </div>
          </div>
        </template>
      </draggable>
    </div>
    <div v-if="configData.list">
      <div class="add-btn" @click="addHotTxt" v-if="configData.list && configData.list.length < configData.max">
        <el-button class="btn" type="primary" ghost> <span class="iconfont iconjiahao"></span>添加 </el-button>
      </div>
    </div>
    <!-- <linkaddress
      ref="linkaddres"
      :linkType="1"
      :fromType="'diyPage'"
      :isCateTree="!['homeComb', 'tabNav'].includes(defaults.name)"
      @linkUrl="linkUrl"
    ></linkaddress> -->
    <linkaddress
      ref="linkaddres"
      :linkType="1"
      :fromType="'diyPage'"
      :isCateTree="!['homeComb', 'tabNav'].includes(defaults.name)"
      @linkUrl="linkUrl"
    ></linkaddress>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import draggable from 'vuedraggable';
import linkaddress from '@/components/linkaddress';
import { cascaderListApi } from '@/api/product';
import { getDraggableItemKey } from '@/utils/draggableKey';

defineOptions({ name: 'c_tab_list' });

const props = defineProps({
  configObj: {
    type: Object,
  },
  configNme: {
    type: String,
  },
  index: {
    type: null,
  },
});

const defaults = ref({});
const configData = ref({});
const itemObj = ref({});
const activeIndex = ref(0);
const treeSelect = ref([]);
// note: `props` (defineProps) shadows the component's `props` data field; use cascaderProps
const cascaderProps = ref({ multiple: false, checkStrictly: true, emitPath: false });
const linkaddres = ref(null);

onMounted(() => {
  goodsCategory();
  nextTick(() => {
    defaults.value = props.configObj;
    configData.value = props.configObj[props.configNme] || {};
  });
});

watch(
  () => props.configObj,
  (nVal, oVal) => {
    defaults.value = nVal;
    configData.value = nVal[props.configNme] || {};
  },
  { deep: true },
);

function goodsCategory() {
  cascaderListApi(1)
    .then((res) => {
      treeSelect.value = formatCategory(res.data || []);
    })
    .catch((res) => {
      ElMessage.error((res && (res.msg || res.message)) || '商品分类获取失败');
    });
}

function formatCategory(list) {
  return (Array.isArray(list) ? list : []).map((item) => {
    return {
      ...item,
      value: item.value !== undefined && item.value !== null ? item.value : item.id,
      label: item.label || item.title || item.name || '',
      children: item.children && item.children.length ? formatCategory(item.children) : undefined,
    };
  });
}

//商品分类
function sliderChange(e, index) {
  // configData.value.list[configData.value.tabCur].selectConfig.activeValue = e;
  // emit('getConfig', { name: 'cascader', values: e });
  activeIndex.value = index;
  configData.value.list[index].classPage.id = e;
  const category = findCategory(treeSelect.value, e);
  configData.value.list[index].classPage.name = category ? category.label : '';
}

function findCategory(list = [], id) {
  for (const item of list) {
    if (item.id == id || item.value == id) return item;
    const child = findCategory(item.children || item.child || [], id);
    if (child) return child;
  }
  return null;
}

function linkUrl(e) {
  if (configData.value.list[activeIndex.value].dataType.tabVal) {
    let obj = e.split('?')[1];
    let obj2 = obj.split('&');
    configData.value.list[activeIndex.value].classPage.name = obj2[1].split('=')[1];
    configData.value.list[activeIndex.value].classPage.id = obj2[0].split('=')[1];
  } else {
    let obj = e.split('?')[1];
    let obj2 = obj.split('&');
    configData.value.list[activeIndex.value].microPage.name = obj2[1].split('=')[1];
    configData.value.list[activeIndex.value].microPage.id = obj2[0].split('=')[1];
  }
}

function getLink(index) {
  activeIndex.value = index;
  let obj = {};
  if (configData.value.list[activeIndex.value].dataType.tabVal) {
    obj = {
      id: 8,
      pid: 2,
      type: 'product_category',
    };
  } else {
    obj = {
      id: 9,
      pid: 1,
      type: 'special',
    };
  }
  linkaddres.value.handleCheckChange(obj);
  linkaddres.value.modals = true;
}

function addHotTxt() {
  if (configData.value.list.length == 0) {
    let storage = window.localStorage;
    itemObj.value = JSON.parse(storage.getItem('itemObj'));
    itemObj.value.dataType.tabVal = 0;
    itemObj.value.microPage.name = '';
    itemObj.value.classPage.name = '';
    configData.value.list.push(itemObj.value);
  } else {
    let obj = JSON.parse(JSON.stringify(configData.value.list[configData.value.list.length - 1]));
    obj.dataType.tabVal = 0;
    obj.microPage.name = '';
    obj.classPage.name = '';
    configData.value.list.push(obj);
  }
}

// 删除数组
function bindDelete(index) {
  if (configData.value.list.length == 1) {
    let itemObjLocal = configData.value.list[0];
    itemObj.value = itemObjLocal;
    let storage = window.localStorage;
    storage.setItem('itemObj', JSON.stringify(itemObjLocal));
  }
  configData.value.list.splice(index, 1);
}
</script>

<style scoped lang="scss">
:deep(.ivu-input) {
  font-size: 12px !important;
}

:deep(.ivu-input-wrapper) {
  width: 240px;
}

:deep(.ivu-input-word-count) {
  color: #bbbbbb;
}

:deep(.ivu-input-icon) {
  color: #bbbbbb;
}
:deep(.el-radio) {
  margin-bottom: 1px !important;
}
.c_product {
  padding: 0 15px 20px 15px;

  .list-box {
    .item {
      display: flex;
      align-items: center;
      position: relative;
      margin-top: 23px;
      padding: 18px 20px 18px 0;
      background-color: #f9f9f9;
      border-radius: 3px;

      .delete {
        position: absolute;
        right: -10px;
        top: -10px;
        color: #ccc;
        cursor: pointer;
      }
    }

    .move-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 50px;
      cursor: move;
    }

    .con-item {
      display: flex;
      align-items: center;
      margin-bottom: 15px;

      &:last-child {
        margin-bottom: 0;
      }

      span {
        width: 75px;
        font-size: 12px;
        color: #999999;
      }
    }
  }

  .add-btn {
    margin-top: 21px;

    .btn {
      width: 100%;
      height: 36px;

      .iconfont {
        font-size: 11px;
        margin-right: 5px;
      }
    }
  }
}

.title {
  font-size: 12px;
  color: #999;
}

.iconfont {
  color: #dddddd;
  font-size: 16px;
}
</style>
