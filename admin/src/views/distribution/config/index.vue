<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form ref="promoterForm" :model="promoterForm" :rules="rules" label-width="200px" class="demo-promoterForm"  v-loading="loading">
        <el-form-item prop="brokerageFuncStatus">
          <span slot="label">
            <span>分销启用：</span>
            <el-tooltip class="item" effect="dark" content="商城分销功能开启关闭" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-radio-group v-model="promoterForm.brokerageFuncStatus">
            <el-radio label="1">开启</el-radio>
            <el-radio label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="storeBrokerageQuota">
          <span slot="label">
            <span>满额分销最低金额：</span>
            <el-tooltip class="item" effect="dark" content="满额分销满足金额开通分销权限" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-input-number
          v-model="promoterForm.storeBrokerageQuota"
          placeholder="满额分销满足金额开通分销权限" :min="-1"
          :step="1" class="selWidth"
          @keydown.native="channelInputLimit"></el-input-number>
        </el-form-item>
        <!-- <el-form-item prop="storeBrokerageStatus">
          <span slot="label">
            <span>分销模式：</span>
            <el-tooltip class="item" effect="dark" content="人人分销”默认每个人都可以分销，“指定分销”仅可后台手动设置推广员" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-radio-group v-model="promoterForm.storeBrokerageStatus">
            <el-radio label="1">指定分销</el-radio>
            <el-radio label="2">人人分销</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item prop="brokerageBindind">
          <span slot="label">
            <span>分销关系绑定：</span>
            <el-tooltip class="item" effect="dark" content="所有用户”指所有没有上级推广人的用户，“新用户”指新注册的用户" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-radio-group v-model="promoterForm.brokerageBindind">
            <el-radio label="0">所有用户</el-radio>
            <el-radio label="1">新用户</el-radio>
          </el-radio-group>
        </el-form-item>
         <el-form-item prop="storeBrokerageIsBubble">
          <span slot="label">
            <span>分销气泡：</span>
            <el-tooltip class="item" effect="dark" content="基础商品详情页分销气泡功能开启关闭" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-radio-group  v-model="promoterForm.storeBrokerageIsBubble">
            <el-radio label="1">开启</el-radio>
            <el-radio label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="storeBrokerageRatio">
          <span slot="label">
            <span>一级返佣比例：</span>
            <el-tooltip class="item" effect="dark" content="订单交易成功后给上级返佣的比例0 - 100,例:5 = 反订单金额的5%" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-input-number v-model="promoterForm.storeBrokerageRatio" step-strictly :min="0" :max="100" class="selWidth" placeholder="订单交易成功后给上级返佣的比例0 - 100,例:5 = 反订单金额的5%"></el-input-number>
          <span>%</span>
        </el-form-item>
        <el-form-item prop="storeBrokerageTwo">
          <span slot="label">
            <span>二级返佣比例：</span>
            <el-tooltip class="item" effect="dark" content="订单交易成功后给上级返佣的比例0 ~ 100,例:5 = 反订单金额的5%" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-input-number v-model="promoterForm.storeBrokerageTwo" step-strictly :min="0" :max="100" class="selWidth" placeholder="订单交易成功后给上级返佣的比例0 ~ 100,例:5 = 反订单金额的5%"></el-input-number>
          <span>%</span>
        </el-form-item>
        <el-form-item prop="userExtractMinPrice">
          <span slot="label">
            <span>提现最低金额：</span>
            <el-tooltip class="item" effect="dark" content="用户提现最低金额" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-input-number v-model="promoterForm.userExtractMinPrice" :min="0" :step="1" class="selWidth" placeholder="用户提现最低金额"></el-input-number>
        </el-form-item>
        <el-form-item prop="userExtractBank">
          <span slot="label">
            <span>提现银行卡：</span>
            <el-tooltip class="item" effect="dark" content="提现银行卡，每个银行换行" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-input
            type="textarea"
            :rows="4"
            placeholder="提现银行卡，每个银行换行"
            v-model="promoterForm.userExtractBank">
          </el-input>
        </el-form-item>
        <el-form-item prop="extractTime">
          <span slot="label">
            <span>冻结时间：</span>
            <el-tooltip class="item" effect="dark" content="佣金冻结时间(天)" placement="top-start">
              <i class="el-icon-warning-outline" />
            </el-tooltip>
          </span>
          <el-input-number v-model="promoterForm.extractTime" :min="0" class="selWidth" placeholder="佣金冻结时间(天)"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submitForm('promoterForm')" v-hasPermi="['admin:retail:spread:manage:set']">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
  import { configApi, configUpdateApi, productCheckApi } from '@/api/distribution'
  import * as selfUtil from '@/utils/ZBKJIutil.js'
  import { checkPermi } from "@/utils/permission"; // 权限判断函数
  import {Debounce} from '@/utils/validate'
  export default {
    name: 'Index',
    data() {
      return {
        promoterForm: {},
        loading: true,
        rules: {
          brokerageFuncStatus: [
            { required: true, message: '请选择是否启用分销', trigger: 'change' }
          ],
          storeBrokerageRatio: [
            { required: true, message: '请输入一级返佣比例', trigger: 'blur' }
          ],
          storeBrokerageTwo: [
            { required: true, message: '请输入二级返佣比例', trigger: 'blur' }
          ]
        }
      }
    },
    mounted() {
      this.getDetal()
    },
    methods: {
      checkPermi,
      channelInputLimit(e){
        let key = e.key
      // 不允许输入'e'和'.'
      if (key === 'e' || key === '.') {
        e.returnValue = false
        return false
      }
      return true
      },
      getDetal() {
        this.loading = true;
        configApi().then(res => {
          this.loading = false;
          this.promoterForm = res;
          this.promoterForm.storeBrokerageIsBubble = res.storeBrokerageIsBubble.toString();
          this.promoterForm.brokerageFuncStatus = res.brokerageFuncStatus.toString();
          this.promoterForm.brokerageBindind = res.brokerageBindind.toString();
        }).catch((res) => {
          this.$message.error(res.message)
        })
      },
      submitForm:Debounce(function(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
          if(selfUtil.Add(this.promoterForm.storeBrokerageRatio,this.promoterForm.storeBrokerageTwo)>100) return this.$message.warning('返佣比例相加不能超过100%')
            this.loading = true
            configUpdateApi(this.promoterForm).then(res => {
              this.loading = false
              this.$message.success('提交成功')
              // this.$modalSure('提交成功，是否自动下架商户低于此佣金比例的商品').then(() => {
              //   productCheckApi().then(({ message }) => {
              //     this.$message.success(message)
              //   }).catch(({ message }) => {
              //     this.$message.error(message)
              //   })
              // })
            }).catch((err) => {
              this.loading = false
            })
          } else {
            return false
          }
        })
      })
    }
  }
</script>

<style scoped lang="scss">
  .selWidth{
    width: 300px;
  }
</style>
