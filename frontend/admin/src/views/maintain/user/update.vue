<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form ref="pramRef" :model="pram" :rules="rules" label-width="100px">
        <el-form-item label="管理员账号" prop="account">
          <el-input v-model="pram.account" placeholder="管理员账号" :disabled="true" />
        </el-form-item>
        <el-form-item label="管理员姓名" prop="realName">
          <el-input v-model="pram.realName" placeholder="管理员姓名" :disabled="true" />
        </el-form-item>
        <el-form-item label="原始密码" prop="oldPassword">
          <el-input v-model="pram.oldPassword" placeholder="原始密码" type="password" />
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="pram.password" placeholder="新密码" @input="handlerPwdInput" type="password" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pram.confirmPassword" placeholder="确认新密码" type="password" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlerSubmit('pramRef')">提交</el-button>
          <el-button @click="close('pramRef')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRouter } from 'vue-router';
import * as systemAdminApi from '@/api/systemadmin.js';
import Cookies from 'js-cookie';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'index' });

const { proxy } = getCurrentInstance();
const router = useRouter();

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'));
  } else if (value !== pram.password) {
    callback(new Error('两次输入密码不一致!'));
  } else {
    callback();
  }
};
const JavaInfo = JSON.parse(Cookies.get('JavaInfo'));
const pram = reactive({
  oldPassword: '',
  account: JavaInfo.account,
  password: null,
  confirmPassword: null,
  realName: JavaInfo.realName,
  id: JavaInfo.id,
});
const roleList = ref([]);
const rules = reactive({
  account: [{ required: true, message: '请填管理员账号', trigger: ['blur', 'change'] }],
  password: [{ required: true, message: '请填管理员密码', trigger: ['blur', 'change'] }],
  oldPassword: [{ required: true, message: '请填原始密码', trigger: ['blur', 'change'] }],
  confirmPassword: [
    { required: true, message: '确认密码', validator: validatePass, trigger: ['blur', 'change'] },
  ],
  realName: [{ required: true, message: '管理员姓名', trigger: ['blur', 'change'] }],
});

const pramRef = ref(null);

function close(formName) {
  proxy.$refs[formName].resetFields();
}
const handlerSubmit = Debounce(function (formName) {
  proxy.$refs[formName].validate((valid) => {
    if (valid) {
      systemAdminApi.adminPwdUpdateApi(pram).then((data) => {
        ElMessage.success('提交成功');
        router.go(-1);
      });
    } else {
      return false;
    }
  });
});
function handlerPwdInput(val) {
  if (!val) {
    return;
  }
  rules.password = [
    { required: true, message: '请填管理员密码', trigger: ['blur', 'change'] },
    { min: 6, max: 30, message: '长度6-30个字符', trigger: ['blur', 'change'] },
  ];
  rules.confirmPassword = [
    {
      required: true,
      message: '两次输入密码不一致',
      validator: (rule, value, callback) => {
        if (value === '') {
          callback(new Error('两次输入密码不一致!'));
        } else if (value !== pram.password) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      },
      trigger: ['blur', 'change'],
    },
  ];
}
</script>

<style scoped></style>
