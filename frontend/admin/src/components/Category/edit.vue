<template>
  <div>
    <el-form ref="editPramRef" :model="editPram" label-width="80px">
      <el-form-item
        label="分类名称："
        prop="name"
        :rules="[{ required: true, message: '请输入分类名称', trigger: ['blur', 'change'] }]"
      >
        <el-input v-model="editPram.name" :maxlength="biztype.value === 1 ? 8 : 20" placeholder="分类名称" />
      </el-form-item>
      <el-form-item label="URL：" v-if="biztype.value !== 1 && biztype.value !== 3">
        <el-input v-model="editPram.url" placeholder="URL" />
      </el-form-item>
      <el-form-item label="父级：" v-if="biztype.value !== 3">
        <el-cascader
          v-model="editPram.pid"
          :disabled="isCreate === 1 && editPram.pid === 0"
          :options="biztype.value === 5 ? allTreeList : parentOptions"
          :props="categoryProps"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="菜单图标：" v-if="biztype.value === 5">
        <el-input placeholder="请选择菜单图标" v-model="editPram.extra">
          <template #append>
            <el-button :icon="CirclePlus" @click="addIcon"></el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="分类图标(180*180)：" v-if="biztype.value === 1 || biztype.value === 3">
        <div class="upLoadPicBox" @click="modalPicTap('1')">
          <div v-if="editPram.extra" class="pictrue">
            <img :src="editPram.extra" />
          </div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
      </el-form-item>
      <el-form-item label="排序：">
        <el-input-number controls-position="right" v-model="editPram.sort" :min="0" />
      </el-form-item>
      <el-form-item label="状态：">
        <el-switch
          v-model="editPram.status"
          active-text="显示"
          inactive-text="隐藏"
          :active-value="true"
          :inactive-value="false"
        />
      </el-form-item>
      <el-form-item label="扩展字段：" v-if="biztype.value !== 1 && biztype.value !== 3 && biztype.value !== 5">
        <el-input v-model="editPram.extra" type="textarea" placeholder="扩展字段" />
      </el-form-item>
      <el-form-item class="dialog-footer-inner">
        <el-button @click="close">取消</el-button>
        <el-button
          type="primary"
          :loading="loadingBtn"
          v-hasPermi="['admin:category:update']"
          v-debounceClick="
            () => {
              handlerSubmit('editPram');
            }
          "
          >确定</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>
<!--创建和编辑公用一个组件-->
<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { CirclePlus } from '@element-plus/icons-vue';
import * as categoryApi from '@/api/categoryApi.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';

const props = defineProps({
  prent: {
    type: Object,
    required: true,
  },
  isCreate: {
    type: Number,
    default: 0,
  },
  editData: {
    type: Object,
  },
  biztype: {
    type: Object,
    required: true,
  },
  allTreeList: {
    type: Array,
  },
});

const emit = defineEmits(['hideEditDialog']);

const { proxy } = getCurrentInstance();

const editPramRef = ref(null);
const loadingBtn = ref(false);
const constants = proxy.$constants;
const editPram = ref({
  extra: null,
  name: null,
  pid: null,
  sort: 0,
  status: true,
  type: props.biztype.value,
  url: null,
  id: 0,
});
const categoryProps = ref({
  value: 'id',
  label: 'name',
  children: 'child',
  expandTrigger: 'hover',
  checkStrictly: true,
  emitPath: false,
});
const parentOptions = ref([]);

onMounted(() => {
  initEditData();
});

// 点击图标
function addIcon() {
  proxy.$modalIcon(function (icon) {
    editPram.value.extra = icon;
  });
}

// 点击商品图
function modalPicTap(tit, num, i) {
  const attr = [];
  proxy.$modalUpload(
    function (img) {
      if (tit === '1' && !num) {
        editPram.value.extra = img[0].sattDir;
      }
      if (tit === '2' && !num) {
        img.map((item) => {
          attr.push(item.attachment_src);
          formValidate.value.slider_image.push(item);
        });
      }
    },
    tit,
    'store',
  );
}

function close() {
  emit('hideEditDialog');
}

function initEditData() {
  parentOptions.value = [...props.allTreeList];
  addTreeListLabelForCasCard(parentOptions.value, 'child');
  const { extra, name, pid, sort, status, type, id, url } = props.editData;
  if (props.isCreate === 1) {
    editPram.value.extra = extra;
    editPram.value.name = name;
    editPram.value.pid = pid;
    editPram.value.sort = sort;
    editPram.value.status = status;
    editPram.value.type = type;
    editPram.value.url = url;
    editPram.value.id = id;
  } else {
    editPram.value.pid = props.prent.id;
    editPram.value.type = props.biztype.value;
  }
}

function addTreeListLabelForCasCard(arr, child) {
  arr.forEach((o, i) => {
    if (o.child && o.child.length) {
      // o.disabled = true
      o.child.forEach((j) => {
        j.disabled = true;
      });
    }
  });
}

function handlerSubmit(formName) {
  editPramRef.value.validate((valid) => {
    if (!valid) return;
    handlerSaveOrUpdate(props.isCreate === 0);
  });
}

function handlerSaveOrUpdate(isSave) {
  if (isSave) {
    // this.editPram.pid = this.prent.id
    loadingBtn.value = true;
    categoryApi
      .addCategroy(editPram.value)
      .then((data) => {
        emit('hideEditDialog');
        ElMessage.success('创建目录成功');
        loadingBtn.value = false;
      })
      .catch(() => {
        loadingBtn.value = false;
      });
  } else {
    editPram.value.pid = Array.isArray(editPram.value.pid) ? editPram.value.pid[0] : editPram.value.pid;
    loadingBtn.value = true;
    categoryApi
      .updateCategroy(editPram.value)
      .then((data) => {
        emit('hideEditDialog');
        ElMessage.success('更新目录成功');
        loadingBtn.value = false;
      })
      .catch(() => {
        loadingBtn.value = false;
      });
  }
}
</script>

<style scoped></style>
