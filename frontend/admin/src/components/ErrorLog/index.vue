<template>
  <div v-if="errorLogs.length > 0">
    <el-badge :is-dot="true" style="line-height: 25px; margin-top: -5px" @click="dialogTableVisible = true">
      <el-button style="padding: 8px 10px" type="danger">
        <svg-icon icon-class="bug" />
      </el-button>
    </el-badge>

    <el-dialog v-model="dialogTableVisible" width="80%" append-to-body>
      <template #header>
        <div>
          <span style="padding-right: 10px">Error Log</span>
          <el-button type="primary" :icon="Delete" @click="clearAll">Clear All</el-button>
        </div>
      </template>
      <el-table :data="errorLogs" border>
        <el-table-column label="Message">
          <template #default="{ row }">
            <div>
              <span class="message-title">Msg:</span>
              <el-tag type="danger">
                {{ row.err.message }}
              </el-tag>
            </div>
            <br />
            <div>
              <span class="message-title" style="padding-right: 10px">Info: </span>
              <el-tag type="warning"> {{ row.vm.$vnode.tag }} error in {{ row.info }} </el-tag>
            </div>
            <br />
            <div>
              <span class="message-title" style="padding-right: 16px">Url: </span>
              <el-tag type="success">
                {{ row.url }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Stack">
          <template #default="scope">
            {{ scope.row.err.stack }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Delete } from '@element-plus/icons-vue';
import { useErrorLogStore } from '@/store/modules/errorLog';

defineOptions({ name: 'ErrorLog' });

const errorLogStore = useErrorLogStore();

const dialogTableVisible = ref(false);

const errorLogs = computed(() => errorLogStore.logs);

function clearAll() {
  dialogTableVisible.value = false;
  errorLogStore.clearErrorLog();
}
</script>

<style scoped>
.message-title {
  font-size: 16px;
  color: #333;
  font-weight: bold;
  padding-right: 8px;
}
</style>
