<template>
  <el-dialog
    width="540px"
    title="定时任务"
    :visible.sync="dialogVisible"
    :before-close="handleClose"
    :closeOnClickModal="false"
  >
    <el-form ref="dataForm" :model="dataForm" label-width="100px" :rules="rules" v-loading="loadingFrom">
      <el-form-item required label="定时任务类名：" prop="beanName">
        <el-input v-model.trim="dataForm.beanName" placeholder="请输入定时任务名称" />
      </el-form-item>
      <el-form-item required label="cron表达式：" prop="cronExpression">
        <el-input v-model.trim="dataForm.cronExpression" placeholder="请输入cron表达式" />
      </el-form-item>
      <el-form-item required label="方法名：" prop="methodName">
        <el-input v-model.trim="dataForm.methodName" placeholder="请输入定时任务方法名" />
      </el-form-item>
      <el-form-item label="参数：" prop="params">
        <el-input v-model.trim="dataForm.params" placeholder="请输入定时任务方法名" />
      </el-form-item>
      <el-form-item label="备注：" prop="remark">
        <el-input v-model.trim="dataForm.remark" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose('dataForm')">取消</el-button>
      <el-button type="primary" :loading="loading" @click="onsubmit('dataForm')">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import * as schedule from '@/api/schedule';
export default {
  name: 'creatClassify',
  props: {
    editData: {
      type: Object,
      default: () => {
        return {};
      },
    },
    dialogVisible: {
      type: Boolean,
      default: false,
    },
  },
  // computed: {
  //   ...mapGetters(['adminProductClassify']),
  // },
  data() {
    return {
      loading: false,
      loadingFrom: false,
      rules: {},
      dataForm: { ...this.editData },
    };
  },
  watch: {
    editData: {
      handler: function (val) {
        this.dataForm = { ...val };
      },
      deep: true,
    },
  },
  methods: {
    handleClose() {
      this.$nextTick(() => {
        this.$emit('closeModel');
        this.$refs['dataForm'].resetFields();
      });
    },
    onClose() {
      this.$refs['dataForm'].resetFields();
      this.$emit('closeModel');
      this.loading = false;
      this.$emit('getList');
      this.dialogVisible = false;
    },
    onsubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          !this.dataForm.jobId
            ? schedule
                .scheduleJobAdd(this.dataForm)
                .then((res) => {
                  this.$message.success('操作成功');
                  this.onClose();
                })
                .catch(() => {
                  this.loading = false;
                })
            : schedule
                .scheduleJobUpdate(this.dataForm)
                .then((res) => {
                  this.$message.success('操作成功');
                  this.onClose();
                })
                .catch(() => {
                  this.loading = false;
                });
        } else {
          return false;
        }
      });
    },
  },
};
</script>

<style scoped lang="scss">
.lang {
  width: 100%;
  ::v-deep.el-form-item__content {
    width: 79%;
  }
}
</style>
