<template>
  <div class="divBox relative">
    <el-card class="box-card">
      <el-tabs v-model="tableFrom.type" @tab-change="onChangeType" class="mb20">
        <el-tab-pane label="短信" name="sms"></el-tab-pane>
        <el-tab-pane label="商品采集" name="copy"></el-tab-pane>
        <el-tab-pane label="物流查询" name="expr_query"></el-tab-pane>
        <el-tab-pane label="电子面单打印" name="expr_dump"></el-tab-pane>
      </el-tabs>
      <router-link :to="{ path: '/operation/onePassConfig' }">
        <el-button class="link_abs"  :icon="ArrowLeft">返回</el-button>
      </router-link>
      <el-row v-loading="fullscreenLoading" :gutter="16">
        <el-col :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">短信账户名称：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span>{{ account }}</span>
          </el-col>
        </el-col>
        <el-col :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">当前剩余条数：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span>{{ numbers }}</span>
          </el-col>
        </el-col>
        <el-col :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">选择套餐：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <el-row :gutter="20">
              <el-col v-for="(item, index) in list" :key="index" :xl="6" :lg="6" :md="12" :sm="24" :xs="24">
                <div
                  class="list-goods-list-item mb15"
                  :class="{ active: index === current }"
                  @click="check(item, index)"
                >
                  <div class="list-goods-list-item-title" :class="{ active: index === current }">
                    ¥ <i>{{ item.price }}</i>
                  </div>
                  <div class="list-goods-list-item-price" :class="{ active: index === current }">
                    <span>{{ $filters.onePassTypeFilter(tableFrom.type) }}条数: {{ item.num }}</span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-col>
        </el-col>
        <el-col v-if="checkList" :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">充值条数：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span>{{ checkList.num }}</span>
          </el-col>
        </el-col>
        <el-col v-if="checkList" :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">支付金额：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span class="list-goods-list-item-number">￥{{ checkList.price }}</span>
          </el-col>
        </el-col>
        <el-col :span="24" class="ivu-text-left mb20" v-if="code">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">付款方式：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span class="list-goods-list-item-pay"
              >微信支付<i v-if="code.invalid">{{ '  （ 支付码过期时间：' + code.invalid + ' ）' }}</i></span
            >
          </el-col>
        </el-col>
        <el-col :span="24">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">&nbsp;</el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <div class="list-goods-list-item-code mr20">
              <!--              <img :src="code.code_url">-->
              <div id="payQrcode"></div>
            </div>
          </el-col>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import { ArrowLeft } from '@element-plus/icons-vue';
import { smsNumberApi, smsPriceApi, payCodeApi, smsInfoApi } from '@/api/sms';
import QRcode from 'qrcodejs2';

defineOptions({ name: 'SmsPay' });

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isLogin = computed(() => userStore.isLogin);

const numbers = ref('');
const account = ref('');
const list = ref([]);
const current = ref(0);
const checkList = ref({});
const fullscreenLoading = ref(false);
const code = ref({});
const tableFrom = reactive({
  type: 'sms',
});

tableFrom.type = route.query.type;
onIsLogin();

function onChangeType(val) {
  current.value = 0;
  getPrice();
  getNumber();
}
// 查看是否登录
function onIsLogin() {
  fullscreenLoading.value = true;
  userStore
    .checkIsLogin()
    .then(async (res) => {
      const data = res;
      if (!data.status) {
        ElMessage.warning('请先登录');
        router.push('/operation/onePassConfig?url=' + route.path);
      } else {
        getNumber();
        getPrice();
      }
      fullscreenLoading.value = false;
    })
    .catch((res) => {
      router.push('/operation/onePassConfig?url=' + route.path);
      fullscreenLoading.value = false;
    });
}
// 剩余条数
function getNumber() {
  smsInfoApi().then(async (res) => {
    let data = res;
    account.value = data.account;
    switch (tableFrom.type) {
      case 'sms':
        numbers.value = data.sms.num;
        break;
      case 'copy':
        numbers.value = data.copy.num;
        break;
      case 'expr_dump':
        numbers.value = data.dump.num;
        break;
      default:
        numbers.value = data.query.num;
        break;
    }
  });
}
// 支付套餐
function getPrice() {
  fullscreenLoading.value = true;
  smsPriceApi(tableFrom)
    .then(async (res) => {
      setTimeout(() => {
        fullscreenLoading.value = false;
      }, 800);
      const data = res;
      list.value = data.data;
      checkList.value = list.value[0];
      getCode(checkList.value);
    })
    .catch(() => {
      fullscreenLoading.value = false;
    });
}
// 选中
function check(item, index) {
  fullscreenLoading.value = true;
  current.value = index;
  setTimeout(() => {
    getCode(item);
    checkList.value = item;
    fullscreenLoading.value = false;
  }, 800);
}
// 支付码
function getCode(item) {
  const data = {
    payType: 'weixin',
    mealId: item.id,
    price: item.price,
    num: item.num,
    type: tableFrom.type,
  };
  payCodeApi(data)
    .then(async (res) => {
      code.value = res;
      document.getElementById('payQrcode').innerHTML = '';
      new QRcode('payQrcode', { width: 135, height: 135, text: res.qr_code });
    })
    .catch((err) => {
      router.push({ path: '/operation/onePassConfig', query: { type: tableFrom.type } });
      code.value = {};
      document.getElementById('payQrcode').innerHTML = '';
    });
}

onMounted(() => {
  if (!isLogin.value) {
    // router.push('/operation/onePassConfig?url=' + route.path)
  } else {
    getNumber();
    getPrice();
  }
});
</script>

<style lang="scss" scoped>
.active {
  background: #0091ff;
  box-shadow: 0px 6px 20px 0px rgba(0, 145, 255, 0.3);
  color: #fff !important;
}
.list-goods-list-item {
  border: 1px solid #dadfe6;
  height: 118px;
  box-sizing: border-box;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}
.list-goods-list {
  &-item {
    text-align: center;
    position: relative;
    cursor: pointer;
    img {
      width: 60%;
    }
    .ivu-tag {
      position: absolute;
      top: 10px;
      right: 10px;
    }
    &-title {
      font-size: 16px;
      font-weight: bold;
      color: #0091ff;
      margin-bottom: 15px;
      i {
        font-size: 30px;
        font-style: normal;
      }
    }
    &-desc {
      font-size: 14px;
      color: #303133;
    }
    &-price {
      font-size: 14px;
      color: #000000;
      s {
        color: #c5c8ce;
      }
    }
    &-number {
      font-size: 14px;
      color: #ed4014;
    }
    &-pay {
      font-size: 14px;
      color: #00c050;
      i {
        font-size: 12px;
        font-style: normal;
        color: #6d7278;
      }
    }
    &-code {
      width: 130px;
      height: 130px;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
}
.relative {
  position: relative;
}
.link_abs {
  position: absolute;
  top: 36px;
  right: 40px;
}
</style>
