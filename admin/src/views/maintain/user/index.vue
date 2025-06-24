<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form ref="pram" :model="pram" :rules="rules" label-width="100px">
        <el-form-item label="管理员账号" prop="account">
          <el-input v-model="pram.account" placeholder="管理员账号" :disabled="true" />
        </el-form-item>
        <el-form-item label="管理员姓名" prop="realName">
          <el-input v-model.trim="pram.realName" placeholder="管理员姓名" :required="true" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlerSubmit('pram')">提交</el-button>
          <el-button @click="close('pram')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import * as systemAdminApi from '@/api/systemadmin.js';
import { getInfo } from '@/api/user.js';
import Cookies from 'js-cookie';
import { Debounce } from '@/utils/validate';
export default {
  name: 'index',
  data() {
    const JavaInfo = JSON.parse(Cookies.get('JavaInfo'));
    return {
      JavaInfo: JSON.parse(Cookies.get('JavaInfo')),
      pram: {
        account: JavaInfo.account,
        realName: JavaInfo.realName,
        id: JavaInfo.id,
      },
      roleList: [],
      rules: {
        account: [{ required: true, message: '请填管理员账号', trigger: ['blur', 'change'] }],
        realName: [{ required: true, message: '请填管理员姓名', trigger: ['blur', 'change'] }],
      },
    };
  },
  methods: {
    close(formName) {
      this.$refs[formName].resetFields();
    },
    handlerSubmit: Debounce(function (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          systemAdminApi.adminNameUpdateApi({ realName: this.pram.realName }).then((data) => {
            this.$message.success('提交成功');
            this.$router.go(-1);
          });
        } else {
          return false;
        }
      });
    }),
  },
};
</script>

<style scoped></style>
