<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form label-width="75px" :inline="true">
          <el-form-item label="变动类型：">
            <el-select @change="getList(1)" class="selWidth" v-model="tableFrom.type" clearable placeholder="请选择">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="用户搜索：">
            <UserSearchInput ref="userSearchInputRef" v-model="tableFrom" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        class="table"
        highlight-current-row
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="佣金变动" min-width="100">
          <template #default="scope">
            <span :class="scope.row.type == 1 ? 'color_red' : 'color_green'"
              >{{ scope.row.type == 1 ? '+' : '-' }}{{ scope.row.price }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="mark" label="变动信息" min-width="150" />
        <el-table-column label="变动类型" min-width="130" prop="title" />
        <el-table-column prop="userName" label="用户信息" min-width="150" />
        <el-table-column label="时间" width="170" prop="updateTime"> </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { brokerageListApi } from '@/api/financial';

defineOptions({ name: 'AccountsCapital' });

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const userSearchInputRef = ref(null);
const timeVal = ref([]);
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
const tableFrom = reactive({
  type: '',
  content: '',
  searchType: 'all',
  page: 1,
  limit: 20,
});
const userTableFrom = reactive({
  page: 1,
  limit: 10,
  dateLimit: '',
});
const fromList = constants.fromList;
const options = ref([]);
const typeOptions = [
  { value: 1, label: '订单返佣' },
  { value: 2, label: '申请提现' },
  { value: 3, label: '提现失败' },
  { value: 4, label: '提现成功' },
  { value: 5, label: '佣金转余额' },
];

// 列表
function getList(num) {
  listLoading.value = true;
  tableFrom.page = num ? num : tableFrom.page;
  brokerageListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
    });
}
function pageChange(page) {
  tableFrom.page = page;
  getList();
}
function handleSizeChange(val) {
  tableFrom.limit = val;
  getList();
}
function getTypes() {
  billTypeApi()
    .then((res) => {
      options.value = res.data;
      localStorage.setItem('CashKey', JSON.stringify(res.data));
    })
    .catch((res) => {
      ElMessage.error(res.message);
    });
}
//重置
function handleReset() {
  tableFrom.content = '';
  tableFrom.searchType = 'all';
  tableFrom.type = '';
  getList();
}
// 搜索
function seachList() {
  tableFrom.page = 1;
  getList();
}

onMounted(() => {
  // getTypes()
  getList();
});
</script>

<style scoped lang="scss">
.dashboard-workplace {
  &-header {
    &-avatar {
      width: 64px;
      height: 64px;
      border-radius: 50%;
      margin-right: 16px;
      font-weight: 600;
    }

    &-tip {
      width: 82%;
      display: inline-block;
      vertical-align: middle;
      margin-top: -12px;
      &-title {
        font-size: 13px;
        color: #000000;
        margin-bottom: 12px;
      }

      &-desc {
        width: 100%;
        &-sp {
          width: 32%;
          color: #17233d;
          font-size: 13px;
          display: inline-block;
          line-height: 25px;
        }
      }
    }

    &-extra {
      .ivu-col {
        p {
          text-align: right;
        }

        p:first-child {
          span:first-child {
            margin-right: 4px;
          }

          span:last-child {
            color: #808695;
          }
        }

        p:last-child {
          font-size: 22px;
        }
      }
    }
  }
}
.selWidth {
  width: 300px;
}
.color_red {
  color: #f5222d;
}
.color_green {
  color: #7abe5c;
}
</style>
