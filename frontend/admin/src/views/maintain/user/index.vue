<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form ref="pramRef" :model="pram" :rules="rules" label-width="100px">
        <el-form-item label="管理员账号" prop="account">
          <el-input v-model="pram.account" placeholder="管理员账号" :disabled="true" />
        </el-form-item>
        <el-form-item label="管理员姓名" prop="realName">
          <el-input v-model.trim="pram.realName" placeholder="管理员姓名" :required="true" />
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
import { getInfo } from '@/api/user.js';
import Cookies from 'js-cookie';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'index' });

const { proxy } = getCurrentInstance();
const router = useRouter();

const JavaInfo = JSON.parse(Cookies.get('JavaInfo'));
const pram = reactive({
  account: JavaInfo.account,
  realName: JavaInfo.realName,
  id: JavaInfo.id,
});
const roleList = ref([]);
const rules = reactive({
  account: [{ required: true, message: '请填管理员账号', trigger: ['blur', 'change'] }],
  realName: [{ required: true, message: '请填管理员姓名', trigger: ['blur', 'change'] }],
});

const pramRef = ref(null);

function close(formName) {
  proxy.$refs[formName].resetFields();
}
const handlerSubmit = Debounce(function (formName) {
  proxy.$refs[formName].validate((valid) => {
    if (valid) {
      systemAdminApi.adminNameUpdateApi({ realName: pram.realName }).then((data) => {
        ElMessage.success('提交成功');
        router.go(-1);
      });
    } else {
      return false;
    }
  });
});
</script>

<style scoped></style>
