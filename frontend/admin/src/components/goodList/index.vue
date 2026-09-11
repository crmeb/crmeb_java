<template>
  <div class="divBox">
    <div class="header clearfix">
      <div class="container">
        <el-form inline>
          <el-form-item label="商品分类：">
            <el-cascader
              v-model="tableFrom.cateId"
              :options="merCateList"
              :props="cascaderProps"
              clearable
              class="selWidth"
              @change="getList(1)"
            />
          </el-form-item>
          <el-form-item label="商品搜索：" class="searchItem">
            <el-input
              v-model="tableFrom.keywords"
              @input="onInput($event)"
              placeholder="请输入商品名称，关键字，产品编号"
              class="selWidth"
            >
            </el-input>
            <el-button class="ml30" type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-table
      v-loading="listLoading"
      :data="tableData.data"
      style="width: 100%"

      ref="multipleTableRef"
      @selection-change="handleSelectionChange"
    >
      <el-table-column key="2" v-if="handleNum === 'many'" width="55">
        <template #header="scope">
          <el-checkbox
            :value="isChecked && checkedPage.indexOf(tableFrom.page) > -1"
            @change="changeType"
          />
        </template>
        <template #default="scope">
          <el-checkbox :value="checkedIds.indexOf(scope.row.id) > -1" @change="(v) => changeOne(v, scope.row)" />
        </template>
      </el-table-column>
      <el-table-column width="55" key="1" v-if="handleNum !== 'many'">
        <template #default="scope">
          <el-radio v-model="templateRadio" @change="getTemplateRow(scope.row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column prop="id" label="ID" min-width="50" />
      <el-table-column label="商品图" width="80">
        <template #default="scope">
          <div class="demo-image__preview">
            <el-image style="width: 36px; height: 36px" :src="scope.row.image" :preview-src-list="[scope.row.image]" preview-teleported />
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="storeName" label="商品名称" min-width="180" />
      <el-table-column label="商品分类" min-width="150">
        <template #default="scope">
          <span v-for="(item, index) in scope.row.cateValues.split(',')" :key="index" class="mr10">{{ item }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="acea-row row-between" :class="handleNum === 'many' ? '' : 'fr'">
      <div class="block mb20">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout=" sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
      <div v-if="handleNum === 'many'" class="right-align mt30">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="ok">确定</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import {
  productLstApi,
  productDeleteApi,
  categoryApi,
  putOnShellApi,
  offShellApi,
  productHeadersApi,
} from '@/api/store';

defineOptions({ name: 'GoodList' });

const props = defineProps({
  handleNum: {
    type: String,
    default: '',
  },
  checked: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['getStoreItem', 'close']);

const { proxy } = getCurrentInstance();

const multipleTableRef = ref(null);

const templateRadio = ref(0);
const merCateList = ref([]);
const cascaderProps = ref({
  children: 'child',
  label: 'name',
  value: 'id',
  emitPath: false,
  // disabled: 'status'
});
const listLoading = ref(true);
const tableData = ref({
  data: [],
  total: 0,
});
const tableFrom = ref({
  page: 1,
  limit: 10,
  cateId: '',
  keywords: '',
  type: '1',
});
const imgList = ref([]);
const multipleSelection = ref([]);
const checkedPage = ref([]);
const isChecked = ref(false);
const isIndex = ref(0);
const checkBox = ref([]);
const checkedIds = ref([]); // 订单当前页选中的数据

function handleReset() {
  tableFrom.value.cateId = '';
  tableFrom.value.keywords = '';
  getList();
}

function close() {
  emit('close');
}

function onInput(e) {
  proxy.$forceUpdate();
}

function changeType(v) {
  isChecked.value = v;
  const index = checkedPage.value.indexOf(tableFrom.value.page);
  isIndex.value = index;
  checkedPage.value.push(tableFrom.value.page);
  if (index > -1) {
    checkedPage.value.splice(index, 1);
  }
  syncCheckedId(v);
}

function changeOne(v, order) {
  if (v) {
    const index = checkedIds.value.indexOf(order.id);
    if (index === -1) {
      checkedIds.value.push(order.id);
      checkBox.value.push(order);
    }
  } else {
    const index = checkedIds.value.indexOf(order.id);
    if (index > -1) {
      checkedIds.value.splice(index, 1);
      checkBox.value.splice(index, 1);
    }
  }
}

function syncCheckedId(o) {
  if (o) {
    tableData.value.data.forEach((item) => {
      const index = checkedIds.value.indexOf(item.id);
      if (index === -1) {
        checkedIds.value.push(item.id);
        checkBox.value.push(item);
      }
    });
  } else {
    tableData.value.data.forEach((item) => {
      const index = checkedIds.value.indexOf(item.id);
      if (index > -1) {
        checkedIds.value.splice(index, 1);
        checkBox.value.splice(index, 1);
      }
    });
  }
}

function handleSelectionChange(val) {
  const tables = [];
  val.map((item) => {
    tables.push({ src: item.image, id: item.id });
  });
  multipleSelection.value = tables;
}

function ok() {
  emit('getStoreItem', checkBox.value);
}

function getTemplateRow(row) {
  emit('getStoreItem', row);
}

// 商户分类；
function getCategorySelect() {
  categoryApi({ status: -1, type: 1 })
    .then((res) => {
      merCateList.value = addDisabled(res);
    })
    .catch((res) => {
      ElMessage.error(res.message);
    });
}

//限制商品分类只能选择开启的
function addDisabled(dropdownList) {
  const list = [];
  try {
    dropdownList.forEach((e, index) => {
      let e_new = {
        id: e.id,
        name: e.name,
        level: e.level,
        pid: e.pid,
        sort: e.sort,
        status: e.status,
      };
      if (!e.status) {
        e_new = { ...e_new, disabled: true };
      }
      if (e.child) {
        const childList = addDisabled(e.child);
        e_new = { ...e_new, child: childList };
      }
      list.push(e_new);
    });
  } catch (error) {
    console.log(error);
    return [];
  }
  return list;
}

function getList(num) {
  listLoading.value = true;
  tableFrom.value.page = num ? num : tableFrom.value.page;
  productLstApi(tableFrom.value)
    .then((res) => {
      tableData.value.data = res.list;
      tableData.value.total = res.total;
      // res.list.map((item) => {
      //   this.imgList.push(item.image)
      // })
      tableData.value.data.forEach((item) => {
        props.checked.forEach((element) => {
          if (Number(item.id) === Number(element.id)) {
            nextTick(() => {
              multipleTableRef.value.toggleRowSelection(item, true);
            });
          }
        });
      });
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
      ElMessage.error(res.message);
    });
}

function pageChange(page) {
  tableFrom.value.page = page;
  getList();
}

function handleSizeChange(val) {
  tableFrom.value.limit = val;
  getList();
}

onMounted(() => {
  getList();
  if (props.checked.length) {
    let [...arr2] = props.checked;
    checkBox.value = arr2;
    checkedIds.value = arr2.map((item) => {
      return item.id;
    });
  }
  getCategorySelect();
});
</script>

<style scoped lang="scss">
.right-align {
  float: right;
  padding-bottom: 20px !important;
}

.selWidth {
  width: 250px !important;
}

.seachTiele {
  line-height: 35px;
}

.fr {
  float: right;
}
.searchItem {
  margin-right: 0 !important;
}
</style>
