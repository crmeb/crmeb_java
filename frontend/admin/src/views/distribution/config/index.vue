<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-form
        ref="promoterFormRef"
        :model="promoterForm"
        :rules="rules"
        label-width="200px"
        class="demo-promoterForm"
        v-loading="loading"
      >
        <el-form-item prop="brokerageFuncStatus">
          <template #label>
            <span>
              <span>分销启用：</span>
              <el-tooltip class="item" effect="dark" content="商城分销功能开启关闭" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-radio-group v-model="promoterForm.brokerageFuncStatus">
            <el-radio label="1" value="1">开启</el-radio>
            <el-radio label="0" value="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="storeBrokerageQuota">
          <template #label>
            <span>
              <span>满额分销最低金额：</span>
              <el-tooltip class="item" effect="dark" content="满额分销满足金额开通分销权限" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-input-number
            controls-position="right"
            v-model="promoterForm.storeBrokerageQuota"
            placeholder="满额分销满足金额开通分销权限"
            :min="-1"
            :step="1"
            class="selWidth"
            @keydown="channelInputLimit"
          ></el-input-number>
        </el-form-item>
        <!-- <el-form-item prop="storeBrokerageStatus">
          <template #label>
            <span>
              <span>分销模式：</span>
              <el-tooltip class="item" effect="dark" content="人人分销：默认每个人都可以分销，指定分销：仅可后台手动设置推广员" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-radio-group v-model="promoterForm.storeBrokerageStatus">
            <el-radio label="1" value="1">指定分销</el-radio>
            <el-radio label="2" value="2">人人分销</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-form-item prop="brokerageBindind">
          <template #label>
            <span>
              <span>分销关系绑定：</span>
              <el-tooltip
                class="item"
                effect="dark"
                content="所有用户：指所有没有上级推广人的用户，新用户：指新注册的用户"
                placement="top-start"
              >
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-radio-group v-model="promoterForm.brokerageBindind">
            <el-radio label="0" value="0">所有用户</el-radio>
            <el-radio label="1" value="1">新用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="storeBrokerageIsBubble">
          <template #label>
            <span>
              <span>分销气泡：</span>
              <el-tooltip class="item" effect="dark" content="基础商品详情页分销气泡功能开启关闭" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-radio-group v-model="promoterForm.storeBrokerageIsBubble">
            <el-radio label="1" value="1">开启</el-radio>
            <el-radio label="0" value="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="storeBrokerageRatio">
          <template #label>
            <span>
              <span>一级返佣比例：</span>
              <el-tooltip
                class="item"
                effect="dark"
                content="订单交易成功后给上级返佣的比例0 - 100,例:5 = 反订单金额的5%"
                placement="top-start"
              >
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-input-number
            controls-position="right"
            v-model="promoterForm.storeBrokerageRatio"
            step-strictly
            :min="0"
            :max="100"
            class="selWidth"
            placeholder="订单交易成功后给上级返佣的比例0 - 100,例:5 = 反订单金额的5%"
          ></el-input-number>
          <span>%</span>
        </el-form-item>
        <el-form-item prop="storeBrokerageTwo">
          <template #label>
            <span>
              <span>二级返佣比例：</span>
              <el-tooltip
                class="item"
                effect="dark"
                content="订单交易成功后给上级返佣的比例0 ~ 100,例:5 = 反订单金额的5%"
                placement="top-start"
              >
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-input-number
            controls-position="right"
            v-model="promoterForm.storeBrokerageTwo"
            step-strictly
            :min="0"
            :max="100"
            class="selWidth"
            placeholder="订单交易成功后给上级返佣的比例0 ~ 100,例:5 = 反订单金额的5%"
          ></el-input-number>
          <span>%</span>
        </el-form-item>
        <el-form-item prop="userExtractMinPrice">
          <template #label>
            <span>
              <span>提现最低金额：</span>
              <el-tooltip class="item" effect="dark" content="用户提现最低金额" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-input-number
            controls-position="right"
            v-model="promoterForm.userExtractMinPrice"
            :min="0"
            :step="1"
            class="selWidth"
            placeholder="用户提现最低金额"
          ></el-input-number>
        </el-form-item>
        <el-form-item prop="userExtractBank">
          <template #label>
            <span>
              <span>提现银行卡：</span>
              <el-tooltip class="item" effect="dark" content="提现银行卡，每个银行换行" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-input
            type="textarea"
            :rows="4"
            placeholder="提现银行卡，每个银行换行"
            v-model="promoterForm.userExtractBank"
          >
          </el-input>
        </el-form-item>
        <el-form-item prop="storeBrokerageShareNode">
          <template #label>
            <span>
              <span>开始冻结规则：</span>
              <el-tooltip class="item" effect="dark" content="从哪个状态开始计算冻结时间" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-select v-model="promoterForm.storeBrokerageShareNode">
            <el-option
              v-for="item in storeBrokerageShareNodeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="extractTime">
          <template #label>
            <span>
              <span>冻结时间：</span>
              <el-tooltip class="item" effect="dark" content="佣金冻结时间(天)" placement="top-start">
                <i class="el-icon-warning-outline" />
              </el-tooltip>
            </span>
          </template>
          <el-input-number
            controls-position="right"
            v-model="promoterForm.extractTime"
            step-strictly
            :step="1"
            :min="0"
            :max="30"
            class="selWidth"
            placeholder="佣金冻结时间(天)"
          ></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="submitForm('promoterForm')"
            v-hasPermi="['admin:retail:spread:manage:set']"
            >提交</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { configApi, configUpdateApi } from '@/api/distribution';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'Index' });

const { proxy } = getCurrentInstance();

const promoterFormRef = ref(null);
const promoterForm = reactive({});
const loading = ref(true);
const storeBrokerageShareNodeList = [
  {
    value: 'pay',
    label: '订单支付后',
  },
  {
    value: 'receipt',
    label: '订单收货后',
  },
  {
    value: 'complete',
    label: '订单完成后',
  },
];
const rules = {
  brokerageFuncStatus: [{ required: true, message: '请选择是否启用分销', trigger: 'change' }],
  storeBrokerageRatio: [{ required: true, message: '请输入一级返佣比例', trigger: 'blur' }],
  storeBrokerageTwo: [{ required: true, message: '请输入二级返佣比例', trigger: 'blur' }],
  storeBrokerageShareNode: [{ required: true, message: '请选择开始冻结规则', trigger: 'none' }],
};

function channelInputLimit(e) {
  let key = e.key;
  // 不允许输入'e'和'.'
  if (key === 'e' || key === '.') {
    e.returnValue = false;
    return false;
  }
  return true;
}
function getDetal() {
  loading.value = true;
  configApi()
    .then((res) => {
      loading.value = false;
      Object.assign(promoterForm, res);
      promoterForm.storeBrokerageIsBubble = res.storeBrokerageIsBubble.toString();
      promoterForm.brokerageFuncStatus = res.brokerageFuncStatus.toString();
      promoterForm.brokerageBindind = res.brokerageBindind.toString();
      promoterForm.storeBrokerageShareNode = res.storeBrokerageShareNode || 'complete';
      promoterForm.extractTime = Number.isInteger(res.extractTime) && res.extractTime > 0 ? res.extractTime : 0;
    })
    .catch((res) => {
      ElMessage.error(res.message);
    });
}
const submitForm = Debounce(function (formName) {
  if (!promoterForm.storeBrokerageShareNode) {
    ElMessage.error('请选择开始冻结规则');
  }
  promoterFormRef.value.validate((valid) => {
    if (valid) {
      if (selfUtil.Add(promoterForm.storeBrokerageRatio, promoterForm.storeBrokerageTwo) > 100)
        return ElMessage.warning('返佣比例相加不能超过100%');
      loading.value = true;
      configUpdateApi(promoterForm)
        .then((res) => {
          loading.value = false;
          ElMessage.success('提交成功');
          // proxy.$modalSure('提交成功，是否自动下架商户低于此佣金比例的商品').then(() => {
          //   productCheckApi().then(({ message }) => {
          //     ElMessage.success(message)
          //   }).catch(({ message }) => {
          //     ElMessage.error(message)
          //   })
          // })
        })
        .catch((err) => {
          loading.value = false;
        });
    } else {
      return false;
    }
  });
});

onMounted(() => {
  getDetal();
});
</script>

<style scoped lang="scss">
.selWidth {
  width: 300px;
}
</style>
