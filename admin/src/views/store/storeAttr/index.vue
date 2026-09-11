<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="规格搜索：">
            <el-input v-model="tableFrom.keywords" placeholder="请输入商品规格" class="selWidth" clearable> </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="seachList">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <div class="acea-row">
            <el-button type="primary" @click="handleAdd" v-hasPermi="['admin:product:rule:save']">添加商品规格</el-button>
            <el-button
              :disabled="!multipleSelectionAll.length"
              @click="handleDeleteAll"
              v-hasPermi="['admin:product:rule:delete']"
              >批量删除</el-button
            >
          </div>
        </div>
      </template>
      <el-table
        ref="tableRef"
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="ruleName" label="规格名称" min-width="150" />
        <el-table-column label="商品规格" min-width="150">
          <template #default="scope">
            <span v-for="(item, index) in scope.row.ruleValue" :key="index" class="mr10" v-text="item.value" />
          </template>
        </el-table-column>
        <el-table-column label="商品属性" min-width="300">
          <template #default="scope">
            <div v-for="(item, index) in scope.row.ruleValue" :key="index" v-text="item.detail.join(',')" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <a @click="handleEdit(scope.row)" v-hasPermi="['admin:product:rule:update', 'admin:product:rule:info']"
              >编辑</a
            >
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:product:rule:delete']">删除</a>
          </template>
        </el-table-column>
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
<script setup lang="jsx">
import { reactive, ref, nextTick, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { templateListApi, attrDeleteApi } from '@/api/store';

defineOptions({ name: 'StoreAttr' });

const { proxy } = getCurrentInstance();

const formDynamic = ref({
  ruleName: '',
  ruleValue: [],
});
const tableFrom = reactive({
  page: 1,
  limit: 20,
  keywords: '',
});
const tableData = reactive({
  data: [],
  loading: false,
  total: 0,
});
const listLoading = ref(true);
const selectionList = ref([]);
const multipleSelectionAll = ref([]);
const idKey = 'id';
const nextPageFlag = ref(false);
const keyNum = ref(0);
const ids = ref(''); // handleDeleteAll 中引用 this.ids（未声明），保留
const tableRef = ref(null);

function seachList() {
  tableFrom.page = 1;
  getList();
}
function handleSelectionChange(val) {
  selectionList.value = val;
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
  const idKeyLocal = idKey;
  const selectAllIds = [];
  multipleSelectionAll.value.forEach((row) => {
    selectAllIds.push(row[idKeyLocal]);
  });
  tableRef.value.clearSelection();
  for (var i = 0; i < tableData.data.length; i++) {
    if (selectAllIds.indexOf(tableData.data[i][idKeyLocal]) >= 0) {
      // 设置选中，记住table组件需要使用ref="table"
      tableRef.value.toggleRowSelection(tableData.data[i], true);
    }
  }
}
// 记忆选择核心方法
function changePageCoreRecordData() {
  // 标识当前行的唯一键的名称
  const idKeyLocal = idKey;
  // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
  if (multipleSelectionAll.value.length <= 0) {
    multipleSelectionAll.value = selectionList.value;
    return;
  }
  // 总选择里面的key集合
  const selectAllIds = [];
  multipleSelectionAll.value.forEach((row) => {
    selectAllIds.push(row[idKeyLocal]);
  });
  const selectIds = [];
  // 获取当前页选中的id
  selectionList.value.forEach((row) => {
    selectIds.push(row[idKeyLocal]);
    // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
    if (selectAllIds.indexOf(row[idKeyLocal]) < 0) {
      multipleSelectionAll.value.push(row);
    }
  });
  const noSelectIds = [];
  // 得到当前页没有选中的id
  tableData.data.forEach((row) => {
    if (selectIds.indexOf(row[idKeyLocal]) < 0) {
      noSelectIds.push(row[idKeyLocal]);
    }
  });
  noSelectIds.forEach((id) => {
    if (selectAllIds.indexOf(id) >= 0) {
      for (let i = 0; i < multipleSelectionAll.value.length; i++) {
        if (multipleSelectionAll.value[i][idKeyLocal] == id) {
          // 如果总选择中有未被选中的，那么就删除这条
          multipleSelectionAll.value.splice(i, 1);
          break;
        }
      }
    }
  });
}
function handleAdd() {
  formDynamic.value = {
    ruleName: '',
    ruleValue: [],
  };
  proxy.$modalAttr(
    Object.assign({}, formDynamic.value),
    function () {
      getList();
    },
    (keyNum.value += 1),
  );
}
// 列表
function getList() {
  listLoading.value = true;
  templateListApi(tableFrom)
    .then((res) => {
      const list = res.list;
      tableData.data = list;
      tableData.total = res.total;
      for (var i = 0; i < list.length; i++) {
        list[i].ruleValue = JSON.parse(list[i].ruleValue);
      }
      nextTick(function () {
        setSelectRow(); // 调用跨页选中方法
      });
      listLoading.value = false;
    })
    .catch(() => {
      listLoading.value = false;
    });
}
function pageChange(page) {
  changePageCoreRecordData();
  tableFrom.page = page;
  getList();
}
function handleSizeChange(val) {
  changePageCoreRecordData();
  tableFrom.limit = val;
  getList();
}
// 删除
function handleDelete(id, idx) {
  proxy.$modalSure()
    .then(() => {
      attrDeleteApi(id).then(() => {
        ElMessage.success('删除成功');
        if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1;
        getList();
      });
    })
    .catch(() => {});
}
function handleDeleteAll() {
  if (!multipleSelectionAll.value.length) return ElMessage.warning('请选择商品规格');
  const data = [];
  multipleSelectionAll.value.map((item) => {
    data.push(item.id);
  });
  ids.value = data.join(',');
  proxy.$modalSure()
    .then(() => {
      attrDeleteApi(ids.value).then(() => {
        ElMessage.success('删除成功');
        getList();
        multipleSelectionAll.value.length = 0;
        data.length = 0;
      });
    })
    .catch(() => {});
}
function handleEdit(val) {
  proxy.$modalAttr(JSON.parse(JSON.stringify(val)), function () {
    getList();
  });
}

onMounted(() => {
  getList();
});
</script>

<style scoped lang="scss">
.seachTiele {
  line-height: 35px;
}

.fr {
  float: right;
}
</style>
