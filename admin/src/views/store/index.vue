<template>
  <div class="divBox relative">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="商品分类：">
            <el-cascader
              v-model="tableFrom.cateId"
              :options="merCateList"
              :props="props"
              clearable
              class="selWidth"
              @change="seachList"

            />
          </el-form-item>
          <el-form-item label="商品搜索：">
            <el-input
              v-model="tableFrom.keywords"
              placeholder="请输入商品名称，关键字，商品ID"
              class="selWidth"

              clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="seachList" v-hasPermi="['admin:product:list']"
              >搜索</el-button
            >
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <el-tabs v-model="tableFrom.type" @tab-change="seachList" v-if="checkPermi(['admin:product:tabs:headers'])">
            <el-tab-pane
              :label="item.name + '(' + item.count + ')'"
              :name="item.type.toString()"
              v-for="(item, index) in headeNum"
              :key="index"
            />
          </el-tabs>
          <router-link :to="{ path: '/store/list/creatProduct' }">
            <el-button type="primary" class="mr14" v-hasPermi="['admin:product:save']">添加商品</el-button>
          </router-link>
          <el-button type="success" @click="onCopy" v-hasPermi="['admin:product:save']">商品采集</el-button>
          <el-button @click="exports" v-hasPermi="['admin:export:excel:product']">导出</el-button>
        </div>
      </template>
      <el-table
        class="table"
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        :highlight-current-row="true"
      >
        <el-table-column type="expand">
          <template #default="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="商品分类：" label-width="66px">
                <span v-for="(item, index) in props.row.cateValues.split(',')" :key="index" class="mr10">{{
                  item
                }}</span>
              </el-form-item>
              <el-form-item label="市场价:" label-width="46px">
                <span>{{ props.row.otPrice }}</span>
              </el-form-item>
              <el-form-item label="成本价:" label-width="46px">
                <span>{{ props.row.cost }}</span>
              </el-form-item>
              <el-form-item label="收藏:" label-width="34px">
                <span>{{ props.row.collectCount }}</span>
              </el-form-item>
              <el-form-item label="虚拟销量:" label-width="66px">
                <span>{{ props.row.ficti }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" min-width="50" v-if="checkedCities.includes('ID')" />
        <el-table-column label="商品图" min-width="80" v-if="checkedCities.includes('商品图')">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]" preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="商品名称"
          prop="storeName"
          min-width="300"
          v-if="checkedCities.includes('商品名称')"
          :show-overflow-tooltip="true"
        >
        </el-table-column>
        <el-table-column prop="price" label="商品售价" min-width="90" v-if="checkedCities.includes('商品售价')" />
        <el-table-column prop="sales" label="销量" min-width="90" v-if="checkedCities.includes('销量')" />
        <el-table-column prop="stock" label="库存" min-width="90" v-if="checkedCities.includes('库存')" />
        <el-table-column prop="sort" label="排序" min-width="70" v-if="checkedCities.includes('排序')" />

        <el-table-column label="添加时间" min-width="120" v-if="checkedCities.includes('操作时间')">
          <template #default="scope">
            <span>{{ $filters.formatDate(scope.row.addTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80" fixed="right" v-if="checkedCities.includes('状态')">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:product:up', 'admin:product:down'])"
              :disabled="Number(tableFrom.type) > 2"
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="上架"
              inactive-text="下架"
              @change="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.isShow ? '上架' : '下架' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #header>
            <div class="col-setting-header">
              <span style="padding-right: 5px">操作</span>
              <el-popover
                placement="bottom-end"
                :width="200"
                trigger="click"
                v-model:visible="card_select_show"
                popper-class="col-setting-popover"
              >
              <template #reference>
                <i class="el-icon-setting" style="cursor: pointer"></i>
              </template>
              <div class="cell_ht">
                <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
                <el-button link @click="checkSave()">保存</el-button>
              </div>
              <el-checkbox-group
                v-model="checkedCities"
                class="column-checkbox-group"
                @change="handleCheckedCitiesChange"
              >
                <el-checkbox v-for="item in columnData" :label="item" :value="item" :key="item" class="check_cell">{{ item }}</el-checkbox>
              </el-checkbox-group>
            </el-popover>
            </div>
          </template>
          <template #default="scope">
            <router-link :to="{ path: '/store/list/creatProduct/' + scope.row.id + '/1' }">
              <a v-hasPermi="['admin:product:info']">详情</a>
            </router-link>
            <el-divider direction="vertical"></el-divider>
            <template v-if="tableFrom.type !== '5'">
              <router-link :to="{ path: '/store/list/creatProduct/' + scope.row.id }">
                <a v-hasPermi="['admin:product:update']">编辑</a>
              </router-link>
              <el-divider direction="vertical"></el-divider>
              <a
                @click="handleStock(scope.row)"
                v-if="checkPermi(['admin:product:quick:stock:add'])"
                >库存</a
              >
              <el-divider direction="vertical"></el-divider>
            </template>
            <template v-if="tableFrom.type === '5'">
              <a @click="handleRestore(scope.row.id, scope.$index)" v-hasPermi="['admin:product:restore']">恢复商品</a>
              <el-divider direction="vertical"></el-divider>
            </template>
            <a @click="handleDelete(scope.row.id, tableFrom.type)" v-hasPermi="['admin:product:delete']">{{
              tableFrom.type === '5' ? '删除' : '加入回收站'
            }}</a>
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
    <!--库存-->
    <el-drawer
      title="库存"
      v-model="drawer"
      :direction="direction"
      :size="1500"
      class="showHeader"
      :before-close="handleCloseEdit"
    >
      <store-edit @sucess="sucess" :productId="productId" v-if="drawer"></store-edit>
    </el-drawer>
  </div>
</template>

<script setup lang="jsx">
import { reactive, ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRouter } from 'vue-router';
import {
  productLstApi,
  productDeleteApi,
  categoryApi,
  putOnShellApi,
  offShellApi,
  productHeadersApi,
  productExportApi,
  restoreApi,
  productExcelApi,
} from '@/api/store';
import { getToken } from '@/utils/auth';
import storeEdit from './components/storeEdit';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate.js';

defineOptions({ name: 'ProductList' });

const { proxy } = getCurrentInstance();
const router = useRouter();

const direction = 'rtl';
const props = {
  children: 'child',
  label: 'name',
  value: 'id',
  emitPath: false,
};
// roterPre: roterPre,
const headeNum = ref([]);
const listLoading = ref(true);
const tableData = reactive({
  data: [],
  total: 0,
});
const tableFrom = reactive({
  page: 1,
  limit: 20,
  cateId: '',
  keywords: '',
  type: '1',
});
const categoryList = ref([]);
const merCateList = ref([]);
const objectUrl = process.env.VUE_APP_BASE_API;
const card_select_show = ref(false);
const checkAll = ref(false);
const checkedCities = ref(['ID', '商品图', '商品名称', '商品售价', '销量', '库存', '排序', '状态', '操作时间']);
const columnData = ['ID', '商品图', '商品名称', '商品售价', '销量', '库存', '排序', '状态', '操作时间'];
const isIndeterminate = ref(true);
const drawer = ref(false);
const productId = ref(0);
// 原 methods 中引用但未声明的变量（保留原逻辑）
const dragging = ref(null);

function sucess() {
  ElMessage.success('保存成功');
  drawer.value = false;
  getList();
  goodHeade();
}
function handleCloseEdit() {
  drawer.value = false;
}
function handleStock(row) {
  productId.value = row.id;
  drawer.value = true;
}
//重置
function handleReset() {
  tableFrom.cateId = '';
  tableFrom.keywords = '';
  goodHeade();
  getList();
}
//恢复商品
const handleRestore = Debounce(function (id) {
  proxy.$modalSure('恢复商品').then(() => {
    restoreApi(id).then((res) => {
      ElMessage.success('操作成功');
      goodHeade();
      getList();
    });
  });
});
function seachList() {
  tableFrom.page = 1;
  getList();
  goodHeade();
}
// 复制
function onCopy() {
  router.push({
    name: 'SortCreat',
    query: {
      isCopy: 1,
    },
  });
}
// 导出
function exports() {
  productExcelApi({
    cateId: tableFrom.cateId,
    keywords: tableFrom.keywords,
    type: tableFrom.type,
  }).then((res) => {
    window.location.href = res.fileName;
  });
}
// 获取商品表单头数量
function goodHeade() {
  productHeadersApi(tableFrom)
    .then((res) => {
      headeNum.value = res;
    })
    .catch((res) => {
      ElMessage.error(res.message);
    });
}
// 商户分类；
function getCategorySelect() {
  categoryApi({ status: -1, type: 1 })
    .then((res) => {
      merCateList.value = res;
    })
    .catch((res) => {
      ElMessage.error(res.message);
    });
}
// 列表
function getList() {
  listLoading.value = true;
  productLstApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
      ElMessage.error(res.message);
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
// 删除
function handleDelete(id, type) {
  proxy.$modalSure(
    tableFrom.type === '5' ? `删除 id 为 ${id} 的商品吗？` : `将 id 为 ${id} 的商品加入回收站吗？`,
  ).then(() => {
    const deleteFlag = type == 5 ? 'delete' : 'recycle';
    productDeleteApi(id, deleteFlag).then(() => {
      ElMessage.success('操作成功');
      if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1;
      getList();
      goodHeade();
    });
  });
}
function onchangeIsShow(row) {
  row.isShow
    ? putOnShellApi(row.id)
        .then(() => {
          ElMessage.success('上架成功');
          getList();
          goodHeade();
        })
        .catch(() => {
          row.isShow = !row.isShow;
        })
    : offShellApi(row.id)
        .then(() => {
          ElMessage.success('下架成功');
          getList();
          goodHeade();
        })
        .catch(() => {
          row.isShow = !row.isShow;
        });
}
function handleCheckAllChange(val) {
  checkedCities.value = val ? columnData : [];
  isIndeterminate.value = false;
}
function handleCheckedCitiesChange(value) {
  let checkedCount = value.length;
  checkAll.value = checkedCount === columnData.length;
  isIndeterminate.value = checkedCount > 0 && checkedCount < columnData.length;
}
function checkSave() {
  card_select_show.value = false;
  proxy.$modal.loading('正在保存到本地，请稍候...');
  proxy.$cache.local.setJSON('goods_stroge', checkedCities.value);
  setTimeout(proxy.$modal.closeLoading(), 1000);
}

onMounted(() => {
  goodHeade();
  getList();
  getCategorySelect();
  checkedCities.value = proxy.$cache.local.has('goods_stroge')
    ? proxy.$cache.local.getJSON('goods_stroge')
    : checkedCities.value;
});
</script>

<style scoped lang="scss">
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}

.taoBaoModal {
  //  z-index: 3333 !important;
}

.demo-table-expand {
  :deep(.label) {
    width: 82px;
  }
}

.demo-table-expand {
  :deep(.el-form-item__content) {
    width: 77%;
  }
}

.seachTiele {
  line-height: 30px;
}

.relative {
  position: relative;
}

/* 操作列表头：让“操作”文字与设置图标垂直居中对齐 */
.col-setting-header {
  display: inline-flex;
  align-items: center;
}

.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}

.column-checkbox-group {
  padding-bottom: 15px;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #606266;
}
:deep(.el-drawer__header) {
  padding-bottom: 20px !important;
  border-bottom: 1px solid #eee !important;
  font-size: 16px;
}
</style>

<style lang="scss">
/* el-popover 渲染在 body 下，需用全局样式覆盖内边距 */
.col-setting-popover.el-popover.el-popper {
  padding: 0 !important;
}
</style>
