<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch">
          <el-form-item label="菜单名称：" prop="menuName">
            <el-input class="selWidth" v-model="queryParams.name" placeholder="请输入菜单名称" clearable />
          </el-form-item>
          <el-form-item label="状态：" prop="menuType">
            <el-select class="selWidth" v-model="queryParams.menuType" placeholder="菜单状态" clearable>
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">搜索</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-row :gutter="10" class="mb20">
        <el-col :span="1.5">
          <el-button type="primary" plain @click="handleAdd">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="info" plain @click="toggleExpandAll">展开/折叠</el-button>
        </el-col>
      </el-row>

      <el-table
        v-if="refreshTable"
        v-loading="listLoading"
        :data="menuList"
        row-key="id"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="name" label="菜单名称" :show-overflow-tooltip="true" width="160"></el-table-column>
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="scope">
            <MenuIcon v-if="scope.row.icon" :name="scope.row.icon" :size="20" />
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="60"></el-table-column>
        <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="isShow" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isShow ? 'success' : 'danger'">{{ scope.row.isShow ? '显示' : '隐藏' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="80">
          <template #default="scope">
            <span class="type_tag one" v-if="scope.row.menuType == 'M'">目录</span>
            <span class="type_tag two" v-else-if="scope.row.menuType == 'C'">菜单</span>
            <span class="type_tag three" v-else type="info">按钮</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <a @click="handleUpdate(scope.row)" v-hasPermi="['admin:system:menu:info']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleAdd(scope.row)" v-hasPermi="['admin:system:menu:add']">新增</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row)" v-hasPermi="['admin:system:menu:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加或修改菜单对话框 -->
      <el-dialog :title="title" v-model="open" width="680px" append-to-body :close-on-click-modal="false">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
          <el-row>
            <el-col :span="24">
              <el-form-item label="上级菜单：">
                <el-tree-select
                  v-model="form.pid"
                  :data="menuOptions"
                  :props="{ label: 'name', children: 'children' }"
                  node-key="id"
                  :show-count="true"
                  check-strictly
                  placeholder="选择上级菜单"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="菜单类型：" prop="menuType">
                <el-radio-group v-model="form.menuType">
                  <el-radio label="M" value="M">目录</el-radio>
                  <el-radio label="C" value="C">菜单</el-radio>
                  <el-radio label="A" value="A">按钮</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item v-if="form.menuType != 'A'" label="菜单图标：">
                <el-input placeholder="请选择菜单图标" v-model="form.icon">
                  <template #prefix>
                    <MenuIcon v-if="form.icon" :name="form.icon" :size="16" />
                  </template>
                  <template #append>
                    <el-button :icon="CirclePlus" @click="addIcon"></el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="菜单名称：" prop="menuName">
                <el-input v-model="form.name" placeholder="请输入菜单名称" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="显示排序：" prop="sort">
                <el-input-number v-model="form.sort" controls-position="right" :min="0" />
              </el-form-item>
            </el-col>
            <el-col :span="24" v-if="form.menuType !== 'A'">
              <!-- v-if="form.menuType == 'C'" -->
              <el-form-item prop="component">
                <template #label>
                  <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                    <i class="el-icon-question"></i>
                  </el-tooltip>
                  组件路径：
                </template>
                <el-input v-model="form.component" placeholder="请输入组件路径" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item v-if="form.menuType != 'M'">
                <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
                <template #label>
                  <el-tooltip
                    content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)"
                    placement="top"
                  >
                    <i class="el-icon-question"></i>
                  </el-tooltip>
                  权限字符：
                </template>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <!-- v-if="form.menuType != 'A'" -->
                <template #label>
                  <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                    <i class="el-icon-question"></i>
                  </el-tooltip>
                  显示状态：
                </template>
                <el-radio-group v-model="form.isShow">
                  <el-radio v-for="item in showStatus" :key="item.value" :label="item.value" :value="item.value">{{ item.label }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template #footer>
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm" v-hasPermi="['admin:system:menu:update']">确定</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, nextTick, getCurrentInstance } from 'vue';
import { ElLoading } from 'element-plus';
import { CirclePlus } from '@element-plus/icons-vue';
import { menuListApi, menuInfo, menuUpdate, menuAdd, menuDelete } from '@/api/systemadmin';
import MenuIcon from '@/components/MenuIcon/index.vue';
// 原 @riophae/vue-treeselect 无 Vue3 版，改用 element-plus 的 el-tree-select
import { Debounce } from '@/utils/validate';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'Menu' });

const { proxy } = getCurrentInstance();
const parseTime = proxy.parseTime;
const handleTree = proxy.handleTree;
const resetFormGlobal = proxy.resetForm;

const queryFormRef = ref(null);
const formRef = ref(null);

// 遮罩层
const listLoading = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 菜单表格树数据
const menuList = ref([]);
// 菜单树选项
const menuOptions = ref([]);
// 弹出层标题
const title = ref('');
// 是否显示弹出层
const open = ref(false);
// 是否展开，默认全部折叠
const isExpandAll = ref(false);
// 重新渲染表格状态
const refreshTable = ref(true);
// 查询参数
const queryParams = reactive({
  name: '',
  menuType: '',
});
// 表单参数
const form = ref({});
//请求到的menu数据
const menuDataList = ref([]);
// 表单校验
const rules = reactive({
  name: [{ required: true, message: '菜单名称不能为空', trigger: 'blur' }],
  sort: [{ required: true, message: '菜单顺序不能为空', trigger: 'blur' }],
});
const statusOptions = [
  { value: 'M', label: '目录' },
  { value: 'C', label: '菜单' },
  { value: 'A', label: '按钮' },
];
const showStatus = [
  { label: '显示', value: true },
  { label: '隐藏', value: false },
];

// 点击图标
function addIcon() {
  proxy.$modalIcon(function (icon) {
    form.value.icon = icon;
  });
}
/** 查询菜单列表 */
function getList(num) {
  listLoading.value = true;
  menuListApi(queryParams).then((res) => {
    let obj = {},
      _menuList = [];
    res.forEach((item) => {
      obj = item;
      obj.parentId = item.pid;
      obj.children = [];
      _menuList.push(obj);
    });
    menuDataList.value = _menuList;
    menuList.value = handleTree(_menuList, 'menuId');
    getTreeselect();
    listLoading.value = false;
  });
}
/** 转换菜单数据结构 */
function normalizer(node) {
  if (node.children && !node.children.length) {
    delete node.children;
  }
  return {
    id: node.id ? node.id : 0,
    label: node.name ? node.name : '主目录',
    children: node.children,
  };
}
/** 查询菜单下拉树结构 */
function getTreeselect() {
  menuOptions.value = [];
  const menu = { menuId: 0, menuName: '主类目', children: [] };
  menu.children = handleTree(menuDataList.value, 'menuId');
  menuOptions.value.push(menu);
}
// 取消按钮
function cancel() {
  open.value = false;
  reset();
}
// 表单重置
function reset() {
  form.value = {
    menuId: '',
    parentId: 0,
    name: '',
    icon: '',
    menuType: 'M',
    sort: 0,
    isShow: true,
    component: '',
    perms: '',
  };
  resetFormGlobal('formRef');
}
/** 搜索按钮操作 */
function handleQuery() {
  getList(1);
}
/** 重置按钮操作 */
function resetQuery() {
  queryParams.name = '';
  queryParams.menuType = '';
  handleQuery();
}
/** 新增按钮操作 */
function handleAdd(row) {
  reset();
  if (row != null && row.id) {
    form.value.pid = row.id;
  } else {
    form.value.pid = 0;
  }
  open.value = true;
  title.value = '添加菜单';
}
/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}
/** 修改按钮操作 */
function handleUpdate(row) {
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
  });
  reset();
  getTreeselect();
  menuInfo(row.id).then((response) => {
    form.value = response;
    open.value = true;
    title.value = '修改菜单';
    loading.close();
  });
}
/** 提交按钮 */
const submitForm = Debounce(function () {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.value.id != undefined) {
        menuUpdate(form.value).then((response) => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList(1);
        });
      } else {
        menuAdd(form.value).then((response) => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList(1);
        });
      }
    }
  });
});
/** 删除按钮操作 */
function handleDelete(row) {
  proxy
    .$modalSure('删除名称为"' + row.name + '"的数据项？')
    .then(function () {
      return menuDelete(row.id);
    })
    .then(() => {
      getList(1);
      proxy.$modal.msgSuccess('删除成功');
    })
    .catch(() => {});
}

// created
getList();
</script>
<style lang="scss">
.mb8 {
  margin-bottom: 8px;
}

.type_tag {
  display: inline-block;
  height: 32px;
  padding: 0 10px;
  line-height: 30px;
  font-size: 12px;
  border-radius: 4px;
  box-sizing: border-box;
  white-space: nowrap;
}

.two {
  background: rgba(239, 156, 32, 0.1);
  color: rgba(239, 156, 32, 1);
}

.one {
  background: rgba(75, 202, 213, 0.1);
  color: rgba(75, 202, 213, 1);
}

.three {
  color: rgba(120, 128, 160, 1);
  background: rgba(120, 128, 160, 0.1);
}
</style>
