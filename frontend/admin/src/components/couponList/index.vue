<template>
  <div class="divBox">
    <div class="header clearfix">
      <div class="container">
        <el-form inline>
          <el-form-item label="优惠券名称：">
            <el-input v-model="tableFrom.keywords" placeholder="请输入优惠券名称" class="selWidth">
            </el-input>
            <el-button class="ml30" type="primary" @click="getList(1)">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-table
      ref="tableRef"
      v-loading="listLoading"
      :data="tableData.data"
      style="width: 100%"

      max-height="400"
      tooltip-effect="dark"
      highlight-current-row
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="handle === 'wu'" type="selection" width="55" />
      <el-table-column prop="id" label="ID" min-width="50" />
      <el-table-column prop="name" label="优惠券名称" min-width="90" />
      <el-table-column prop="money" label="优惠券面值" min-width="90" />
      <el-table-column prop="minPrice" label="最低消费额" min-width="90">
        <template #default="scope">
          <span>{{ scope.row.minPrice === 0 ? '不限制' : scope.row.minPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期限" min-width="190">
        <template #default="scope">
          <span>{{
            scope.row.isFixedTime === 1 ? scope.row.useStartTime + ' 一 ' + scope.row.useEndTime : '不限制'
          }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余数量" min-width="90">
        <template #default="scope">
          <span>{{ !scope.row.isLimited ? '不限量' : scope.row.lastTotal }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="handle === 'send'" label="操作" width="120" fixed="right">
        <template #default="scope">
          <a class="mr10" @click="sendGrant(scope.row.id)" v-hasPermi="['admin:coupon:user:receive']">发送</a>
        </template>
      </el-table-column>
    </el-table>
    <div class="block mb20 acea-row" :class="handle === 'wu' ? 'row-between' : 'fr'">
      <el-pagination
        :page-sizes="[10, 20, 30, 40]"
        :page-size="tableFrom.limit"
        :current-page="tableFrom.page"
        layout=" sizes, prev, pager, next, jumper"
        :total="tableData.total"
        @size-change="handleSizeChange"
        @current-change="pageChange"
      />
      <div class="mt30" v-if="handle === 'wu'">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="ok">确定</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { marketingListApi, couponUserApi, marketingSendApi } from '@/api/marketing';

defineOptions({ name: 'CouponList' });

const props = defineProps({
  handle: {
    type: String,
    default: '',
  },
  couponData: {
    type: Array,
    default: () => [],
  },
  keyNum: {
    type: Number,
    default: 0,
  },
  userIds: {
    type: String,
    default: '',
  },
  userType: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['getCouponId', 'closeDialog']);

const { proxy } = getCurrentInstance();

const tableRef = ref(null);
const listLoading = ref(true);
const tableData = ref({
  data: [],
  total: 0,
});
const tableFrom = ref({
  page: 1,
  limit: 10,
  keywords: '',
  type: '',
});
const multipleSelection = ref([]);
const multipleSelectionAll = ref([]);
const idKey = ref('id');
const nextPageFlag = ref(false);
const attr = ref([]);

watch(
  () => props.keyNum,
  (val) => {
    getList();
  },
  { deep: true },
);

onMounted(() => {
  tableFrom.value.page = 1;
  getList();
  if (!props.couponData) return;
  props.couponData.forEach((row) => {
    tableRef.value.toggleRowSelection(row);
  });
});

function closeDialog() {
  emit('closeDialog');
}

function close() {
  multipleSelection.value = [];
}

function handleSelectionChange(val) {
  multipleSelection.value = val;
  setTimeout(() => {
    changePageCoreRecordData();
  }, 50);
}

// 设置选中的方法
function setSelectRow() {
  if (!multipleSelectionAll.value || multipleSelectionAll.value.length <= 0) {
    return;
  }
  // 标识当前行的唯一键的名称
  const idKeyValue = idKey.value;
  const selectAllIds = [];
  multipleSelectionAll.value.forEach((row) => {
    selectAllIds.push(row[idKeyValue]);
  });
  tableRef.value.clearSelection();
  for (var i = 0; i < tableData.value.data.length; i++) {
    if (selectAllIds.indexOf(tableData.value.data[i][idKeyValue]) >= 0) {
      // 设置选中，记住table组件需要使用ref="table"
      tableRef.value.toggleRowSelection(tableData.value.data[i], true);
    }
  }
}

// 记忆选择核心方法
function changePageCoreRecordData() {
  // 标识当前行的唯一键的名称
  const idKeyValue = idKey.value;
  // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
  if (multipleSelectionAll.value.length <= 0) {
    multipleSelectionAll.value = multipleSelection.value;
    return;
  }
  // 总选择里面的key集合
  const selectAllIds = [];
  multipleSelectionAll.value.forEach((row) => {
    selectAllIds.push(row[idKeyValue]);
  });
  const selectIds = [];
  // 获取当前页选中的id
  multipleSelection.value.forEach((row) => {
    selectIds.push(row[idKeyValue]);
    // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
    if (selectAllIds.indexOf(row[idKeyValue]) < 0) {
      multipleSelectionAll.value.push(row);
    }
  });
  const noSelectIds = [];
  // 得到当前页没有选中的id
  tableData.value.data.forEach((row) => {
    if (selectIds.indexOf(row[idKeyValue]) < 0) {
      noSelectIds.push(row[idKeyValue]);
    }
  });
  noSelectIds.forEach((id) => {
    if (selectAllIds.indexOf(id) >= 0) {
      for (let i = 0; i < multipleSelectionAll.value.length; i++) {
        if (multipleSelectionAll.value[i][idKeyValue] == id) {
          // 如果总选择中有未被选中的，那么就删除这条
          multipleSelectionAll.value.splice(i, 1);
          break;
        }
      }
    }
  });
}

function ok() {
  if (multipleSelection.value.length > 0) {
    emit('getCouponId', multipleSelectionAll.value);
    close();
  } else {
    ElMessage.warning('请先选择优惠劵');
  }
}

// 列表
function getList(num) {
  listLoading.value = true;
  tableFrom.value.page = num ? num : tableFrom.value.page;
  props.userType ? (tableFrom.value.type = 1) : (tableFrom.value.type = 3);
  marketingSendApi(tableFrom.value)
    .then((res) => {
      tableData.value.data = res.list;
      tableData.value.total = res.total;
      listLoading.value = false;
      nextTick(function () {
        setSelectRow(); // 调用跨页选中方法
      });
    })
    .catch((res) => {
      listLoading.value = false;
    });
}

function pageChange(page) {
  changePageCoreRecordData();
  tableFrom.value.page = page;
  getList();
}

function handleSizeChange(val) {
  changePageCoreRecordData();
  tableFrom.value.limit = val;
  getList();
}

// 发送
function sendGrant(id) {
  proxy.$modalSure('发送优惠劵吗').then(() => {
    couponUserApi({ couponId: id, uid: props.userIds }).then(() => {
      ElMessage.success('发送成功');
      getList();
    });
  });
}
</script>

<style scoped lang="scss">
.selWidth {
  width: 219px !important;
}
.seachTiele {
  line-height: 35px;
}
.fr {
  float: right;
}
</style>
