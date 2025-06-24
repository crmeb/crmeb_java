<template>
  <div class="divBox">
    <el-card class="box-card">
      <zb-parser
        :form-id="formId"
        :is-create="isCreate"
        :edit-data="editData"
        @submit="handlerSubmit"
        @resetForm="resetForm"
        v-if="isShow && checkPermi(['admin:pass:appget'])"
      />
    </el-card>
  </div>
</template>

<script>
import zbParser from '@/components/FormGenerator/components/parser/ZBParser';
import { passAppSaveApi, configInfo, passAppInfoApi } from '@/api/systemConfig.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'onePassConfig',
  components: { zbParser },
  data() {
    return {
      isShow: true,
      isCreate: 0,
      editData: {},
      formId: 144, //一号通配置
    };
  },
  mounted() {
    if (checkPermi(['admin:pass:appget'])) this.getPassAppInfo();
  },
  methods: {
    checkPermi,
    resetForm(formValue) {
      this.isShow = false;
    },
    handlerSubmit(data) {
      passAppSaveApi(data).then((res) => {
        this.getPassAppInfo();
        this.$message.success('操作成功');
      });
    },
    //获取配置详情
    getPassAppInfo() {
      passAppInfoApi().then((res) => {
        this.isShow = false;
        this.editData = res;
        this.isCreate = 1;
        setTimeout(() => {
          // 让表单重复渲染待编辑数据
          this.isShow = true;
        }, 80);
      });
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .closeBtn {
  display: none;
}
::v-deep .dialog-footer-inner {
  float: left !important;
}
</style>
