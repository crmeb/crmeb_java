<template>
  <div>
    <el-form ref="pramRef" :model="pram" label-width="100px" @submit.prevent>
      <el-form-item
        label="角色名称："
        prop="roleName"
        :rules="[{ required: true, message: '请填写角色名称', trigger: ['blur', 'change'] }]"
      >
        <el-input v-model="pram.roleName" placeholder="身份名称" />
      </el-form-item>
      <el-form-item label="状态：">
        <el-switch
          v-model="pram.status"
          :active-value="true"
          :inactive-value="false"
          active-text="开启"
          inactive-text="关闭"
        />
      </el-form-item>
      <el-form-item label="菜单权限：">
        <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>
        <!-- <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选</el-checkbox> -->
        <el-checkbox v-model="menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')"
          >父子联动</el-checkbox
        >
        <el-tree
          class="tree-border"
          :data="menuOptions"
          show-checkbox
          ref="menuRef"
          node-key="id"
          :check-strictly="!menuCheckStrictly"
          empty-text="加载中，请稍候"
          :props="defaultProps"
        ></el-tree>
      </el-form-item>
      <div class="dialog-footer-inner pd-bt-20">
        <el-button @click="close">取消</el-button>
        <el-button
          type="primary"
          @click="handlerSubmit('pram')"
          v-hasPermi="['admin:system:role:update', 'admin:system:role:save']"
          >{{ isCreate === 0 ? '确定' : '更新' }}</el-button
        >
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick, getCurrentInstance } from 'vue';
import { ElMessage, ElLoading } from '@/utils/elementPlusFeedback';
import * as roleApi from '@/api/role.js';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'roleEdit' });

const props = defineProps({
  isCreate: {
    type: Number,
    required: true,
  },
  editData: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(['hideEditDialog']);

const pram = reactive({
  roleName: null,
  rules: '',
  status: null,
  id: null,
});
const menuExpand = ref(false);
const menuNodeAll = ref(false);
const menuOptions = ref([]);
const menuCheckStrictly = ref(true);
const currentNodeId = ref([]);
const defaultProps = reactive({
  children: 'childList',
  label: 'name',
});
const menuIds = ref([]);

const pramRef = ref(null);
const menuRef = ref(null);

function close() {
  emit('hideEditDialog');
}
function initEditData() {
  if (props.isCreate !== 1) return;
  const { roleName, status, id } = props.editData;
  pram.roleName = roleName;
  pram.status = status;
  pram.id = id;
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
  });
  roleApi.getInfo(id).then((res) => {
    menuOptions.value = res.menuList;
    checkDisabled(menuOptions.value);
    loading.close();
    getTreeId(res.menuList);
    nextTick(() => {
      menuIds.value.forEach((i, n) => {
        var node = menuRef.value.getNode(i);
        if (node.isLeaf) {
          menuRef.value.setChecked(node, true);
        }
      });
    });
  });
}
const handlerSubmit = Debounce(function (form) {
  pramRef.value.validate((valid) => {
    if (!valid) return;
    let roles = getMenuAllCheckedKeys().toString();
    pram.rules = roles;
    if (props.isCreate === 0) {
      handlerSave();
    } else {
      handlerEdit();
    }
  });
});
function handlerSave() {
  roleApi.addRole(pram).then((data) => {
    ElMessage.success('创建身份成功');
    emit('hideEditDialog');
  });
}
function handlerEdit() {
  roleApi.updateRole(pram).then((data) => {
    ElMessage.success('更新身份成功');
    emit('hideEditDialog');
  });
}
function rulesSelect(selectKeys) {
  pram.rules = selectKeys;
}
// 树权限（展开/折叠）
function handleCheckedTreeExpand(value, type) {
  if (type == 'menu') {
    let treeList = menuOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      menuRef.value.store.nodesMap[treeList[i].id].expanded = value;
    }
  }
}
// 树权限（全选/全不选）
function handleCheckedTreeNodeAll(value, type) {
  if (type == 'menu') {
    menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
  }
}
// 树权限（父子联动）
function handleCheckedTreeConnect(value, type) {
  if (type == 'menu') {
    menuCheckStrictly.value = value ? true : false;
  }
}
// 所有菜单节点数据
function getMenuAllCheckedKeys() {
  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}
function getCacheMenu() {
  if (props.isCreate !== 0) return;
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
  });
  roleApi.menuCacheList().then((res) => {
    menuOptions.value = res;
    checkDisabled(menuOptions.value);
    loading.close();
  });
}
function getTreeId(datas) {
  for (var i in datas) {
    if (datas[i].checked) menuIds.value.push(datas[i].id);
    if (datas[i].childList) {
      getTreeId(datas[i].childList);
    }
  }
}
function checkDisabled(data) {
  //设置公共权限默认勾选且不可操作
  data.forEach((item) => {
    if (item.id === 280 || item.id === 294 || item.id === 344 || item.id === 1) {
      item.disabled = true;
      item.childList.forEach((item1) => {
        item1.disabled = true;
        nextTick(() => {
          var node = menuRef.value.getNode(item1.id);
          if (node.isLeaf) {
            menuRef.value.setChecked(node, true);
          }
        });
        //控制台
        if (item.id === 1) {
          item1.childList.forEach((item2) => {
            item2.disabled = true;
            nextTick(() => {
              var node = menuRef.value.getNode(item2.id);
              if (node.isLeaf) {
                menuRef.value.setChecked(node, true);
              }
            });
          });
        }
      });
    }
  });
}

onMounted(() => {
  initEditData();
  getCacheMenu();
});
</script>

<style scoped>
.pd-bt-20 {
  padding-bottom: 20px;
}
</style>
