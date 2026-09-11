<template>
  <div>
    <template v-if="selectModel">
      <el-tree
        ref="treeRef"
        :data="treeList"
        show-checkbox
        node-key="id"
        @check="getCurrentNode"
        :default-checked-keys="selectModelKeysNew"
        :props="treeProps"
      >
      </el-tree>
    </template>
    <template v-else>
      <div class="divBox">
        <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
          <div class="padding-add">
            <el-form inline>
              <el-form-item label="分类状态：">
                <el-select v-model="listPram.status" placeholder="状态" class="selWidth" @change="handlerGetList">
                  <el-option label="全部" :value="-1"></el-option>
                  <el-option label="显示" :value="1"></el-option>
                  <el-option label="不显示" :value="0"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="分类名称：">
                <el-input v-model="listPram.name" placeholder="请输入名称" class="selWidth" clearable>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handlerGetList">搜索</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
        <el-card class="box-card mt14">
          <template #header>
            <div class="clearfix">
              <el-button
                type="primary"
                @click="handleAddMenu({ id: 0, name: '顶层目录' })"
                v-hasPermi="['admin:category:save']"
                >添加{{ biztype.name }}</el-button
              >
            </div>
          </template>
          <el-table
            ref="treeListRef"
            :data="treeList"
            class="table"
            highlight-current-row
            row-key="id"
            :tree-props="{ children: 'child', hasChildren: 'hasChildren' }"
          >
            <el-table-column prop="name" label="名称" min-width="240">
              <template #default="scope"> {{ scope.row.name }} | {{ scope.row.id }} </template>
            </el-table-column>
            <template v-if="!selectModel">
              <el-table-column v-if="biztype.value !== 3" label="类型" min-width="150">
                <template #default="scope">
                  <span>{{ $filters.filterEmpty($filters.filterCategroyType(scope.row.type)) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="分类图标" min-width="80">
                <template #default="scope">
                  <div class="listPic" v-if="biztype.value === 5">
                    <i :class="'el-icon-' + scope.row.extra" style="font-size: 20px" />
                  </div>
                  <div class="demo-image__preview" v-else>
                    <el-image
                      style="width: 36px; height: 36px"
                      :src="scope.row.extra"
                      :preview-src-list="[scope.row.extra]" preview-teleported
                      v-if="scope.row.extra"
                    />
                    <img style="width: 36px; height: 36px" v-else :src="defaultImg" alt="" />
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="Url" min-width="250" v-if="biztype.value === 5" key="2">
                <template #default="scope">
                  <span>{{ scope.row.url }}</span>
                </template>
              </el-table-column>
              <el-table-column label="排序" prop="sort" min-width="150" />
              <el-table-column label="状态" min-width="150">
                <!--  -->
                <template #default="scope" v-if="checkPermi(['admin:category:update:status'])">
                  <el-switch
                    v-model="scope.row.status"
                    :active-value="true"
                    :inactive-value="false"
                    active-text="显示"
                    inactive-text="隐藏"
                    @change="onchangeIsShow(scope.row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="操作" width="190" fixed="right">
                <template #default="scope">
                  <template v-if="(biztype.value === 1 && scope.row.pid === 0) || biztype.value === 5">
                    <a @click="handleAddMenu(scope.row)">添加子目录</a>
                    <el-divider direction="vertical"></el-divider>
                  </template>
                  <a @click="handleEditMenu(scope.row)" v-hasPermi="['admin:category:info']">编辑</a>
                  <el-divider direction="vertical"></el-divider>
                  <a
                    v-debounceClick="
                      () => {
                        handleDelMenu(scope.row);
                      }
                    "
                    v-hasPermi="['admin:category:delete']"
                    >删除</a
                  >
                </template>
              </el-table-column>
            </template>
          </el-table>
        </el-card>
      </div>
    </template>
    <el-dialog
      :title="editDialogConfig.isCreate === 0 ? `添加${biztype.name}` : `编辑${biztype.name}`"
      v-model="editDialogConfig.visible"
      destroy-on-close
      :close-on-click-modal="false"
      width="540px"
    >
      <edit
        v-if="editDialogConfig.visible"
        :prent="editDialogConfig.prent"
        :is-create="editDialogConfig.isCreate"
        :edit-data="editDialogConfig.data"
        :biztype="editDialogConfig.biztype"
        :all-tree-list="treeList"
        @hideEditDialog="hideEditDialog"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import * as categoryApi from '@/api/categoryApi.js';
import info from './info';
import edit from './edit';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import { checkPermi, checkRole } from '@/utils/permission';
import defaultImg from '@/assets/imgs/moren.jpg';

const props = defineProps({
  biztype: {
    // 类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类
    type: Object,
    default: { value: -1 },
    validator: (obj) => {
      return obj.value > 0;
    },
  },
  pid: {
    type: Number,
    default: 0,
    validator: (value) => {
      return value >= 0;
    },
  },
  selectModel: {
    // 是否选择模式
    type: Boolean,
    default: false,
  },
  selectModelKeys: {
    type: Array,
  },
  rowSelect: {},
});

const emit = defineEmits(['rulesSelect']);

const { proxy } = getCurrentInstance();

const treeRef = ref(null);
const treeListRef = ref(null);
const selectModelKeysNew = ref(props.selectModelKeys);
const loading = ref(false);
const constants = proxy.$constants;
const treeProps = ref({
  label: 'name',
  children: 'child',
  // expandTrigger: 'hover',
  // checkStrictly: false,
  // emitPath: false
});
// treeCheckedKeys:[],// 选择模式下的属性结构默认选中
const multipleSelection = ref([]);
const editDialogConfig = ref({
  visible: false,
  isCreate: 0, // 0=创建，1=编辑
  prent: {}, // 父级对象
  data: {},
  biztype: props.biztype, // 统一主业务中的目录类型
});
const dataList = ref([]);
const treeList = ref([]);
const listPram = ref({
  pid: props.pid,
  type: props.biztype.value,
  status: -1,
  name: '',
  page: constants.page.page,
  limit: constants.page.limit[0],
});
const viewInfoConfig = ref({
  data: null,
  visible: false,
});

onMounted(() => {
  /* if(this.biztype.value === 3){
    this.listPram.pageSize = constants.page.pageSize[4]
    this.handlerGetList()
  }else{*/
  handlerGetTreeList();
  // }
});

//权限控制
//重置
function handleReset() {
  listPram.value.status = -1;
  listPram.value.name = '';
  handlerGetTreeList();
}

function onchangeIsShow(row) {
  categoryApi
    .categroyUpdateStatus(row.id)
    .then((res) => {
      ElMessage.success('修改成功');
      handlerGetTreeList();
    })
    .catch(() => {
      row.status = !row.status;
    });
}

function handleEditMenu(rowData) {
  editDialogConfig.value.isCreate = 1;
  editDialogConfig.value.data = rowData;
  editDialogConfig.value.prent = rowData;
  editDialogConfig.value.visible = true;
}

function handleAddMenu(rowData) {
  editDialogConfig.value.isCreate = 0;
  editDialogConfig.value.prent = rowData;
  editDialogConfig.value.data = {};
  editDialogConfig.value.biztype = props.biztype;
  editDialogConfig.value.visible = true;
}

function getCurrentNode(data) {
  let node = treeRef.value.getNode(data);
  childNodes(node);
  // this.parentNodes(node);
  //是否编辑的表示
  // this.ruleForm.isEditorFlag = true;
  //编辑时候使用
  emit('rulesSelect', treeRef.value.getCheckedKeys());
  // this.selectModelKeys = this.$refs.tree.getCheckedKeys();
  //无论编辑和新增点击了就传到后台这个值
  // this.$emit('rulesSelect', this.$refs.tree.getCheckedKeys().concat(this.$refs.tree.getHalfCheckedKeys()));
  // this.ruleForm.menuIdsisEditor = this.$refs.tree.getCheckedKeys().concat(this.$refs.tree.getHalfCheckedKeys());
}

//具体方法可以看element官网api
function childNodes(node) {
  let len = node.childNodes.length;
  for (let i = 0; i < len; i++) {
    node.childNodes[i].checked = node.checked;
    childNodes(node.childNodes[i]);
  }
}

function parentNodes(node) {
  if (node.parent) {
    for (let key in node) {
      if (key == 'parent') {
        node[key].checked = true;
        parentNodes(node[key]);
      }
    }
  }
}

function handleDelMenu(rowData) {
  proxy.$modalSure('删除当前数据?').then(() => {
    categoryApi.deleteCategroy(rowData).then((res) => {
      handlerGetTreeList();
      ElMessage.success('删除成功');
    });
  });
}

function handlerGetList() {
  handlerGetTreeList();
  // categoryApi.listCategroy({status:this.listPram.status, type: 1 }).then(data => {
  //   this.treeList = data.list
  // })
}

function handlerGetTreeList() {
  // this.biztype.value === 5 && !this.selectModel) ?  -1 : 1
  // const _pram = { type: this.biztype.value, status: !this.selectModel ? -1 : (this.biztype.value === 5 ? -1 : 1) }
  const _pram = { type: props.biztype.value, status: listPram.value.status, name: listPram.value.name };
  loading.value = true;
  props.biztype.value !== 3
    ? categoryApi
        .treeCategroy(_pram)
        .then((data) => {
          treeList.value = handleAddArrt(data);
          localStorage.setItem('adminProductClassify', JSON.stringify(treeList.value));
          loading.value = false;
        })
        .catch(() => {
          loading.value = false;
        })
    : categoryApi
        .listCategroy({ type: 3, status: listPram.value.status, pid: listPram.value.pid, name: listPram.value.name })
        .then((data) => {
          treeList.value = data;
          localStorage.setItem('adminProductClassify', JSON.stringify(treeList.value));
        });
}

function handlerGetInfo(id) {
  viewInfoConfig.value.data = id;
  viewInfoConfig.value.visible = true;
}

function handleNodeClick(data) {
  console.log('data:', data);
}

function handleAddArrt(treeData) {
  const _result = selfUtil.addTreeListLabel(treeData);
  return _result;
}

function hideEditDialog() {
  setTimeout(() => {
    editDialogConfig.value.prent = {};
    editDialogConfig.value.type = 0;
    editDialogConfig.value.visible = false;
    handlerGetTreeList();
  }, 200);
}

function handleSelectionChange(d1, { checkedNodes, checkedKeys, halfCheckedNodes, halfCheckedKeys }) {
  // this.multipleSelection =  checkedKeys.concat(halfCheckedKeys)
  multipleSelection.value = checkedKeys;
  emit('rulesSelect', multipleSelection.value);
}
</script>

<style lang="scss" scoped>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
