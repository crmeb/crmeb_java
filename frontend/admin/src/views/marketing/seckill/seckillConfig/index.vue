<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="秒杀状态：">
            <el-select
              v-model="tableFrom.status"
              placeholder="请选择"
              class="filter-item selWidth"
              @change="getList(1)"
              clearable
            >
              <el-option label="关闭" :value="false" />
              <el-option label="开启" :value="true" />
            </el-select>
          </el-form-item>
          <el-form-item label="秒杀名称：">
            <el-input v-model="tableFrom.name" placeholder="请输入秒杀名称" class="selWidth" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <el-button type="primary" @click="add" v-hasPermi="['admin:seckill:manger:save']">添加秒杀配置</el-button>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" ref="multipleTableRef">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column
          label="秒杀名称"
          min-width="100"
          align="left"
          header-align="left"
          class-name="seckill-name-column"
        >
          <template #default="scope">
            <router-link class="seckill-name-link" :to="{ path: '/marketing/seckill/list/' + scope.row.id }">
              {{ scope.row.name }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="秒杀时段" min-width="100">
          <template #default="scope">
            {{ scope.row.time.split(',').join(' - ') }}
          </template>
        </el-table-column>
        <el-table-column label="轮播图" min-width="200">
          <template #default="scope">
            <div class="acea-row" v-if="scope.row.silderImgs">
              <div class="demo-image__preview mr5" v-for="item in JSON.parse(scope.row.silderImgs)" :key="item.attId">
                <el-image style="width: 36px; height: 36px" :src="item.sattDir" :preview-src-list="[item.sattDir]" preview-teleported />
              </div>
            </div>
            <span v-else>无</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" min-width="150">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:seckill:manger:update:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.status ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="130" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="scope">
            <a
              @click="handleEdit(scope.row.id)"
              v-hasPermi="['admin:seckill:manger:info', 'admin:seckill:manger:update']"
              >编辑</a
            >
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:seckill:manger:delete']">删除</a>
            <el-divider direction="vertical"></el-divider>
            <router-link :to="{ path: '/marketing/seckill/creatSeckill/creat/' + scope.row.id }">
              <a v-hasPermi="['admin:seckill:save']">添加商品</a>
            </router-link>
          </template>
        </el-table-column>
      </el-table>
      <div class="block mb20">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
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

    <el-dialog
      :title="isCreate === 0 ? '添加数据' : '编辑数据'"
      v-model="dialogVisible"
      width="700px"
      :before-close="handleClose"
    >
      <div v-loading="loading">
        <zb-parser
          :form-id="formId"
          :is-create="isCreate"
          :edit-data="editData"
          :format-form-conf="formatSeckillFormConf"
          @submit="handlerSubmit"
          @resetForm="resetForm"
          @closeDialog="dialogVisible = false"
          v-if="dialogVisible"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { configSaveForm, configInfo } from '@/api/systemConfig.js';
import {
  seckillListApi,
  seckillUpdateApi,
  seckillInfoApi,
  seckillSaveApi,
  seckillDeleteApi,
  seckillConfigStatusApi,
} from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'SeckillConfig' });

const { proxy } = getCurrentInstance();

const dialogVisible = ref(false);
const isShow = ref(true);
const isCreate = ref(0);
const editData = ref({});
const formId = ref(123);
const listLoading = ref(true);
const tableData = reactive({
  data: [],
  total: 0,
});
const tableFrom = reactive({
  page: 1,
  limit: 20,
  name: '',
  isDel: false,
  status: '',
});
const seckillId = ref('');
const loading = ref(false);
const multipleTableRef = ref(null);

//重置
const handleReset = () => {
  tableFrom.status = '';
  tableFrom.name = '';
  getList();
};
const resetForm = (formValue) => {
  dialogVisible.value = false;
};
const formatSeckillFormConf = (formConf) => {
  if (!Array.isArray(formConf.fields)) return formConf;
  formConf.fields.forEach((field) => {
    if (field.__vModel__ === 'name') {
      field.placeholder = '请输入秒杀名称';
    }
    if (field.__vModel__ === 'status') {
      field['active-text'] = '开启';
      field['inactive-text'] = '关闭';
    }
  });
  return formConf;
};
// 删除
const handleDelete = (id, idx) => {
  proxy.$modalSure('永久删除该配置').then(() => {
    seckillDeleteApi({ id: id }).then(() => {
      ElMessage.success('删除成功');
      if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1;
      getList();
    });
  });
};
const onchangeIsShow = (row) => {
  seckillConfigStatusApi(row.id, { status: row.status })
    .then(async () => {
      ElMessage.success('修改成功');
      getList();
    })
    .catch(() => {
      row.status = !row.status;
    });
};
const onEditSort = (row) => {
  row.isEdit = true;
};
const onBlur = (row) => {
  row.isEdit = false;
  onEdit(row.id, row);
};
// 获取表单详情
const getFormInfo = (id) => {
  loading.value = true;
  seckillInfoApi({ id: id })
    .then((res) => {
      editData.value = res;
      dialogVisible.value = true;
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
};
// 编辑
const handleEdit = (id) => {
  seckillId.value = id;
  getFormInfo(id);
  isCreate.value = 1;
};
// 编辑
const onEdit = (id, obj) => {
  const data = obj ? obj : editData.value;
  seckillUpdateApi({ id }, data)
    .then((res) => {
      isSuccess();
    })
    .catch((res) => {
      listLoading.value = false;
    });
};
// 提交
const handlerSubmit = Debounce(function (formValue) {
  if (formValue.time.split(',')[0].split(':')[0] > formValue.time.split(',')[1].split(':')[0])
    return ElMessage.error('请填写正确的时间范围');
  isCreate.value === 0
    ? seckillSaveApi(formValue).then((res) => {
        isSuccess();
      })
    : seckillUpdateApi({ id: seckillId.value }, formValue).then((res) => {
        isSuccess();
      });
});
const isSuccess = () => {
  ElMessage.success('操作成功');
  dialogVisible.value = false;
  getList();
};
// 列表
const getList = (num) => {
  listLoading.value = true;
  tableFrom.page = num ? num : tableFrom.page;
  seckillListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      tableData.data.map((item) => (item.isEdit = false));
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
    });
};
const pageChange = (page) => {
  tableFrom.page = page;
  getList();
};
const handleSizeChange = (val) => {
  tableFrom.limit = val;
  getList();
};
const add = () => {
  isCreate.value = 0;
  dialogVisible.value = true;
};
const handleClose = () => {
  dialogVisible.value = false;
  editData.value = {};
};

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
:deep(.el-form-item__content .el-date-editor:nth-of-type(2)) {
  margin-left: 8px;
}

:deep(.seckill-name-link) {
  display: block;
  text-align: left;
}

:deep(.seckill-name-column .cell) {
  text-align: left;
}
</style>
