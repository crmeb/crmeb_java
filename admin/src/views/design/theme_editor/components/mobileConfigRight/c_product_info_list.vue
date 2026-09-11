<template>
  <div class="mobile-config-component">
    <div class="title" v-if="configData.title">{{ configData.title }}</div>
    <div class="tips" v-if="configData.tips">{{ configData.tips }}</div>
    <div class="list-box">
      <draggable
        class="dragArea list-group"
        :list="configData.list"
        :item-key="getDraggableItemKey"
        group="productInfoSections"
        handle=".move-icon"
      >
        <template #item="{ element: item }">
          <div class="item" v-if="item.name !== 'tags'">
            <div class="move-icon">
              <span class="iconfont iconxingzhuangjiehe"></span>
            </div>
            <div class="content">
              <div class="info">
                <div class="info-item">
                  <span class="span">{{ item.cname }}</span>
                  <div class="input-box" v-if="item.type === 'radio'">
                    <el-switch v-model="item.show" active-text="" inactive-text=""></el-switch>
                  </div>
                </div>
                <div class="info-item" v-if="item.show && item.checkBoxList">
                  <div class="">
                    <el-checkbox-group v-model="item.checkList">
                      <template v-for="(check, cIndex) in item.checkBoxList" :key="cIndex">
                        <el-checkbox
                          v-if="
                            !(
                              (item.name === 'price' && check.value == 2) ||
                              (item.name === 'data' && check.value == 0)
                            )
                          "
                          :label="check.value"
                          :value="check.value"
                        >
                          {{ check.name }}
                        </el-checkbox>
                      </template>
                    </el-checkbox-group>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </draggable>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import draggable from 'vuedraggable';
import { getDraggableItemKey } from '@/utils/draggableKey';

defineOptions({ name: 'c_product_info_list' });

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

onMounted(() => {
  nextTick(() => {
    syncConfigData(props.configObj);
  });
});

watch(
  () => props.configObj,
  (nVal, oVal) => {
    syncConfigData(nVal);
  },
  { deep: true },
);

function syncConfigData(data) {
  defaults.value = data || {};
  configData.value = defaults.value[props.configNme] || {};
}
</script>

<style scoped lang="scss">
.mobile-config-component {
  margin: 15px;

  .title {
    padding-bottom: 21px;
    color: #333;
    font-size: 14px;
  }

  .tips {
    font-size: 12px;
    color: #999;
    margin-bottom: 15px;
  }

  .list-box {
    .item {
      position: relative;
      display: flex;
      background: #f9f9f9;
      align-items: center; // 垂直居中，类似 c_menu_list
      padding: 16px 20px 16px 0;
      margin-bottom: 16px;
      border-radius: 3px;

      .move-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 30px;
        cursor: move;
        .iconfont {
          color: #dddddd;
          font-size: 16px;
        }
      }

      .content {
        flex: 1;

        .info {
          flex: 1;
          margin-left: 10px; // 调整间距

          .info-item {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            :deep(.el-checkbox) {
              margin-bottom: 0;
            }
            &:nth-last-child(1) {
              margin-bottom: 0;
            }

            .span {
              width: 70px; // 调整宽度以适应"商品价格"等文字
              font-size: 12px;
              color: #999;
            }

            .input-box {
              flex: 1;
              display: flex;
              justify-content: flex-end; // 开关和复选框靠右

              // 针对复选框组的特殊处理
              :deep(.el-checkbox-group) {
                display: flex;
                flex-wrap: wrap;
                justify-content: flex-end;
              }
              :deep(.el-checkbox) {
                margin-right: 10px;
                &:last-child {
                  margin-right: 0;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
