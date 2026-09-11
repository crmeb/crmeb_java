<template>
  <div class="components-container">
    <el-form ref="editPramRef" :model="editPram" label-width="75px">
      <el-form-item label="父级：">
        <!--          <span>{{ prent.name}}</span>-->
        <el-cascader
          v-model="editPram.pid"
          :options="allTreeList"
          :props="categoryProps"
          disabled
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item
        label="分类名称："
        prop="name"
        :rules="[{ required: true, message: '请输入分类名称', trigger: ['blur', 'change'] }]"
      >
        <el-input v-model="editPram.name" placeholder="分类名称" />
      </el-form-item>
      <el-form-item
        label="英文名称："
        prop="url"
        :rules="[{ required: true, message: '英文名称不能为空', trigger: ['blur', 'change'] }]"
      >
        <el-input v-model="editPram.url" placeholder="URL" />
      </el-form-item>
      <el-form-item label="排序：">
        <el-input-number controls-position="right" v-model="editPram.sort" :min="1" :max="10" />
      </el-form-item>
      <el-form-item label="状态：">
        <el-switch v-model="editPram.status" :active-value="true" :inactive-value="false" />
      </el-form-item>
      <!--        这里的类型是无限极分类的类型不能当做配置分类的类型，使用下面的扩展字段实现原有业务中的类型-->
      <!--        <el-form-item label="类型" prop="type" :rules="[{required:true,message:'请选择类型',trigger:['blur']}]">-->
      <!--          <el-select v-model="editPram.type" disabled>-->
      <!--            <el-option v-for="item in constants.categoryType" :key="item.value"-->
      <!--                       :label="item.name" :value="item.value"></el-option>-->
      <!--          </el-select>-->
      <!--        </el-form-item>-->
      <!--      <el-form-item label="类型">-->
      <!--        <el-radio-group v-model="editPram.extra">-->
      <!--          <el-radio v-for="item,index in constants.configCategory" :label="item.value" :value="item.value">-->
      <!--            {{ item.label }}-->
      <!--          </el-radio>-->
      <!--        </el-radio-group>-->
      <!--        &lt;!&ndash;          <el-input type="textarea" v-model="editPram.extra" placeholder="扩展字段"/>&ndash;&gt;-->
      <!--      </el-form-item>-->
      <el-form-item class="dialog-footer-inner">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="handlerSubmit('editPramRef')">确定</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import * as categoryApi from '@/api/categoryApi.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import { Debounce } from '@/utils/validate';

defineOptions({});

const props = defineProps({
  prent: {
    type: Object,
    default: () => 0,
  },
  isCreate: {
    type: Number,
    default: 0,
  },
  editData: {
    type: Object,
  },
  allTreeList: {
    type: Array,
  },
});

const emit = defineEmits(['hideEditDialog']);

const { proxy } = getCurrentInstance();
const constants = proxy.$constants;

const editPram = reactive({
  extra: null, // 关联表单id
  name: null,
  pid: null,
  sort: 0,
  status: true,
  type: constants.categoryType[5].value,
  url: null,
  id: 0,
});
const categoryProps = reactive({
  value: 'id',
  label: 'name',
  children: 'child',
  expandTrigger: 'hover',
  checkStrictly: true,
  emitPath: false,
});
const parentOptions = ref([]);

const editPramRef = ref(null);

function close() {
  emit('hideEditDialog');
}
function initEditData() {
  parentOptions.value = selfUtil.addTreeListLabelForCasCard(props.allTreeList);
  if (props.isCreate !== 1) {
    const { id } = props.prent;
    editPram.pid = id;
  } else {
    const { extra, name, pid, sort, status, type, url, id } = props.editData;
    // editPram.extra = extra
    editPram.name = name;
    editPram.pid = pid;
    editPram.sort = sort;
    editPram.status = status;
    editPram.type = type;
    editPram.url = url;
    editPram.id = id;
    editPram.extra = extra;
  }
}
const handlerSubmit = Debounce(function (formName) {
  proxy.$refs[formName].validate((valid) => {
    if (!valid) return;
    handlerSaveOrUpdate(props.isCreate === 0);
  });
});
function handlerSaveOrUpdate(isSave) {
  if (isSave) {
    editPram.pid = props.prent.id;
    categoryApi.addCategroy(editPram).then((data) => {
      emit('hideEditDialog');
      ElMessage.success('创建分类成功');
    });
  } else {
    editPram.pid = Array.isArray(editPram.pid) ? editPram.pid[0] : editPram.pid;
    categoryApi.updateCategroy(editPram).then((data) => {
      emit('hideEditDialog');
      ElMessage.success('更新分类成功');
    });
  }
}

onMounted(() => {
  initEditData();
});
</script>

<style scoped></style>
