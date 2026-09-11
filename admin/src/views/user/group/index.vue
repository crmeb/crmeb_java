<template>
  <div class="divBox">
    <el-card class="box-card">
      <template #header>
        <div class="clearfix">
          <el-button type="primary" @click="onAdd(null)" v-hasPermi="['admin:user:group:save', 'admin:user:tag:save']">{{
            route.path.indexOf('group') !== -1 ? '添加用户分组' : '添加用户标签'
          }}</el-button>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%">
        <el-table-column label="ID" min-width="80" prop="id" />
        <el-table-column :label="route.path.indexOf('group') !== -1 ? '分组名称' : '标签名称'" min-width="180">
          <template #default="{ row }">
            <span v-text="route.path.indexOf('group') !== -1 ? row.groupName : row.name"></span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <a @click="onAdd(scope.row)" v-hasPermi="['admin:user:group:update', 'admin:user:tag:update']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a
              @click="handleDelete(scope.row.id, scope.$index)"
              disable
              v-hasPermi="['admin:user:group:delete', 'admin:user:tag:delete']"
              >删除</a
            >
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
    <el-dialog
      :title="
        route.path.indexOf('group') !== -1
          ? `${labelPram.id ? '编辑' : '添加'}分组名称`
          : `${labelPram.id ? '编辑' : '添加'}标签名称`
      "
      v-model="visible"
      width="540px"
      :close-on-click-modal="false"
    >
      <el-form :rules="rules" :model="labelPram" ref="ruleFormRef" label-width="75px" class="demo-dynamic">
        <el-form-item :label="route.path.indexOf('group') !== -1 ? '分组名称：' : '标签名称：'" prop="value">
          <el-input
            v-model="labelPram.value"
            :placeholder="route.path.indexOf('group') !== -1 ? '请填写分组名称' : '请填写标签名称'"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div>
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" @click="confirm()">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="jsx">
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from '@/utils/elementPlusFeedback';
import {
  tagListApi,
  tagDeleteApi,
  tagSaveApi,
  tagInfoApi,
  tagUpdateApi,
  groupListApi,
  groupDeleteApi,
  groupSaveApi,
  groupUpdateApi,
} from '@/api/user';

defineOptions({ name: 'UserGroup' });

const { proxy } = getCurrentInstance();
const route = useRoute();

const visible = ref(false);
const tableFrom = reactive({
  page: 1,
  limit: 20,
});
const tableData = reactive({
  data: [],
  total: 0,
});
const listLoading = ref(true);
const labelPram = reactive({
  value: '',
  id: '',
});
const rules = {
  value: [{ required: true, message: '请输入用户标签', trigger: 'blur' }],
};
const ruleFormRef = ref(null);

function info() {}
function onAdd(row) {
  if (row) {
    labelPram.id = row.id;
    labelPram.value = route.path.indexOf('group') !== -1 ? row.groupName : row.name;
  } else {
    labelPram.id = '';
    labelPram.value = '';
  }
  visible.value = true;
}
function confirm() {
  if (route.path.indexOf('group') !== -1) {
    labelPram.id
      ? groupUpdateApi({ id: labelPram.id }, { groupName: labelPram.value }).then(() => {
          ElMessage.success('编辑成功');
          getList();
          visible.value = false;
        })
      : groupSaveApi({ groupName: labelPram.value }).then(() => {
          ElMessage.success('新增成功');
          getList();
          visible.value = false;
        });
  } else {
    labelPram.id
      ? tagUpdateApi({ id: labelPram.id }, { name: labelPram.value }).then(() => {
          ElMessage.success('编辑成功');
          getList();
          visible.value = false;
        })
      : tagSaveApi({ name: labelPram.value }).then(() => {
          ElMessage.success('新增成功');
          getList();
          visible.value = false;
        });
  }
}
// 列表
function getList() {
  listLoading.value = true;
  route.path.indexOf('group') !== -1
    ? groupListApi(tableFrom)
        .then((res) => {
          tableData.data = res.list;
          tableData.total = res.total;
          listLoading.value = false;
        })
        .catch((res) => {
          listLoading.value = false;
        })
    : tagListApi(tableFrom)
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
// 删除
function handleDelete(id, idx) {
  proxy.$modalSure('删除吗？所有用户已经关联的数据都会清除').then(() => {
    route.path.indexOf('group') !== -1
      ? groupDeleteApi({ id: id }).then(() => {
          ElMessage.success('删除成功');
          if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1;
          getList();
        })
      : tagDeleteApi({ id: id }).then(() => {
          ElMessage.success('删除成功');
          if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1;
          getList();
        });
  });
}

onMounted(() => {
  getList();
});
</script>

<style scoped lang="scss"></style>
