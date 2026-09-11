<template>
  <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="75px" class="demo-ruleForm">
    <el-form-item label="用户编号：" prop="id">
      <el-input v-model="ruleForm.id" disabled></el-input>
    </el-form-item>
    <el-form-item label="用户地址：" prop="addres">
      <el-input v-model="ruleForm.addres"></el-input>
    </el-form-item>
    <el-form-item label="用户备注：" prop="mark">
      <el-input v-model="ruleForm.mark" type="textarea"></el-input>
    </el-form-item>
    <el-form-item label="用户分组：" prop="groupId">
      <el-select style="width: 100%" v-model="ruleForm.groupId" placeholder="请选择" clearable filterable>
        <el-option :value="item.id" v-for="(item, index) in groupList" :key="index" :label="item.groupName"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="用户标签：" prop="labelData">
      <el-select style="width: 100%" v-model="labelData" placeholder="请选择" clearable filterable multiple>
        <el-option :value="item.id" v-for="(item, index) in labelLists" :key="index" :label="item.name"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="推广员：" prop="isPromoter">
      <el-radio-group v-model="ruleForm.isPromoter">
        <el-radio :label="true" :value="true">开启</el-radio>
        <el-radio :label="false" :value="false">关闭</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="状态：" prop="status">
      <el-radio-group v-model="ruleForm.status">
        <el-radio :label="true" :value="true">开启</el-radio>
        <el-radio :label="false" :value="false">关闭</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item class="dialog-footer-inner">
      <el-button @click="resetForm('ruleForm')">取消</el-button>
      <el-button type="primary" @click="submitForm('ruleForm')" v-hasPermi="['admin:user:update']">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="jsx">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { groupListApi, tagListApi, userInfoApi, userUpdateApi } from '@/api/user';
import { Debounce } from '@/utils/validate';
const defaultObj = {
  // birthday: '',
  // cardId: '',
  id: null,
  mark: '',
  //  phone: '',
  // realName: '',
  addres: '',
  groupId: '',
  isPromoter: false,
  status: false,
};

defineOptions({ name: 'UserEdit' });

const props = defineProps({
  uid: {
    type: Number,
    default: null,
  },
});

const emit = defineEmits(['resetForm', 'success']);

const ruleForm = ref(Object.assign({}, defaultObj));
const groupData = ref([]);
const labelData = ref([]);
const labelLists = ref([]);
const groupList = ref([]);
const rules = {
  id: [{ required: true, message: '请输入用户编号', trigger: 'change' }],
  addres: [{ required: true, message: '请输入用户地址', trigger: 'change' }],
  mark: [{ required: false, message: '请输入用户备注', trigger: 'blur' }],
  groupId: [{ required: true, message: '请选择用户分组', trigger: 'blur' }],
  isPromoter: [{ required: true, message: '请选择状态', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'blur' }],
};
const ruleFormRef = ref(null);

// 详情
function userInfo() {
  userInfoApi({ id: props.uid }).then(async (res) => {
    ruleForm.value = {
      // birthday: res.birthday,
      // cardId: res.cardId,
      id: res.uid,
      mark: res.mark,
      // phone: res.phone,
      // realName: res.realName,
      status: res.status,
      addres: res.addres,
      groupId: Number(res.groupId) || '',
      isPromoter: res.isPromoter,
      tagId: res.tagId || '',
    };
    labelData.value = res.tagId ? res.tagId.split(',').map(Number) : [];
  });
}
// 分组列表
function groupLists() {
  groupListApi({ page: 1, limit: 9999 }).then(async (res) => {
    groupList.value = res.list;
  });
}
//标签列表
function getTagList() {
  tagListApi({ page: 1, limit: 9999 }).then((res) => {
    labelLists.value = res.list;
  });
}
const submitForm = Debounce(function (formName) {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      ruleForm.value.tagId = labelData.value.join(',');
      userUpdateApi({ id: ruleForm.value.id }, ruleForm.value).then(async (res) => {
        ElMessage.success('编辑成功');
        emit('success');
      });
    } else {
      return false;
    }
  });
});
function resetForm(formName) {
  ruleFormRef.value.resetFields();
  emit('resetForm');
}

onMounted(() => {
  if (props.uid) userInfo();
  groupLists();
  getTagList();
});
</script>

<style scoped>
.selWidth {
  width: 90%;
}
</style>
