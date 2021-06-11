<template>
  <el-dialog :visible.sync="modals" title="发送货" class="order_box" :before-close="handleClose" width="600px">
    <el-form ref="formItem" :model="formItem" label-width="110px" @submit.native.prevent :rules="rules">
      <el-form-item label="快递公司：" prop="expressCode">
        <el-select v-model="formItem.deliveryId" filterable style="width:80%;">
          <el-option v-for="(item,i) in express" :value="item.deliveryId" :key="i" :label="item.deliveryName"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="快递单号：" prop="waybillId">
        <el-input v-model="formItem.waybillId" placeholder="请输入快递单号" style="width:80%;"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button size="mini" type="primary" @click="putSend('formItem')">提交</el-button>
      <el-button size="mini" @click="cancel('formItem')">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import {videoSendApi, sheetInfoApi, companyGetListApi} from '@/api/order'
  const validatePhone = (rule, value, callback) => {
    if (!value) {
      return callback(new Error('请填写手机号'));
    } else if (!/^1[3456789]\d{9}$/.test(value)) {
      callback(new Error('手机号格式不正确!'));
    } else {
      callback();
    }
  };
  export default {
    name: 'orderSend',
    props: {
      orderId: String
    },
    data() {
      return {
        formItem: {
          deliveryId: '',
          orderNo: '',
          waybillId: ''
        },
        modals: false,
        express: [],
        exportTempList: [],
        tempImg: '',
        rules: {
          deliveryId: [
            {required: true, message: '请选择快递公司', trigger: 'change'}
          ],
          waybillId: [
            {required: true, message: '请输入快递单号', trigger: 'blur'}
          ]
        },
        expressType: 'normal'
      }
    },
    mounted() {
      this.express = JSON.parse(sessionStorage.getItem('videoExpress'));
    },
    methods: {
      // 视频号快递公司
      companyGetList() {
        companyGetListApi().then(async res => {
          this.express = res;
          sessionStorage.setItem('videoExpress', JSON.stringify(res))
        })
      },
      // 提交
      putSend(name) {
        this.formItem.orderNo = this.orderId;
        this.$refs[name].validate((valid) => {
          if (valid) {
            videoSendApi(this.formItem).then(async => {
              this.$message.success('发送货成功');
              this.modals = false;
              this.$refs[name].resetFields();
              this.$emit('submitFail')
            })
          } else {
            this.$message.error('请填写信息');
          }
        })
      },
      handleClose() {
        this.cancel('formItem');
      },
      cancel(name) {
        this.modals = false;
        this.$refs[name].resetFields();
        this.formItem.type = '1';
        this.formItem.expressRecordType = '1';
      }
    }
  }
</script>

<style scoped lang="scss">
  .width8 {
    width: 80%;
  }

  .width9 {
    width: 70%;
  }

  .tempImgList {
    // opacity: 1;
    width: 38px !important;
    height: 30px !important;
    // margin-top: -30px;
    cursor: pointer;
    position: absolute;
    z-index: 11;
    img {
      width: 38px !important;
      height: 30px !important;
    }
  }
</style>
