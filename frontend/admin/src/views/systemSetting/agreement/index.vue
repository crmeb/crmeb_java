<template>
  <div class="divBox">
    <el-card class="box-card" :bordered="false" shadow="never">
      <div v-loading="loading">
        <el-tabs
          tab-position="left"
          v-model="agreementValue"
          @tab-change="tabStatus"
          v-hasPermi="[
            'admin:agreement:user:info',
            'admin:agreement:userprivacy:info',
            'admin:agreement:useraccountcancel:info',
            'admin:agreement:useraccountcancelnotice:info',
            'admin:agreement:aboutus:info',
            'admin:agreement:intelligent:info',
            'admin:agreement:coupon:agreement:info',
          ]"
        >
          <el-tab-pane :label="item.title" v-for="(item, index) in tabList" :key="index" :name="item.info">
            <div class="content">
              <div class="phoneBox">
                <div class="fontBox" v-html="formValidate.agreement"></div>
              </div>
              <div class="ueditor">
                <div class="font"><span class="verticalLine"></span> {{ item.title }}</div>
                <WangEditor v-model="formValidate.agreement" :key="keyIndex"></WangEditor>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
        <div class="btn">
          <el-button
            class="button"
            type="primary"
            @click="submenus"
            v-hasPermi="[
              'admin:agreement:user:save',
              'admin:agreement:userprivacy:save',
              'admin:agreement:useraccountcancel:save',
              'admin:agreement:useraccountcancelnotice:save',
              'admin:agreement:aboutus:save',
              'admin:agreement:intelligent:save',
              'admin:agreement:coupon:agreement:save',
            ]"
            >提交</el-button
          >
        </div>
      </div>
    </el-card>
  </div>
</template>
<script setup>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { reactive, ref } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import WangEditor from '@/components/wangEditor/index.vue';
import { agreementInfoApi, agreementSaveApi } from '@/api/agreement';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'agreements' });

const loading = ref(false);
const tabList = [
  { title: '用户协议', info: 'userinfo', save: 'usersave' },
  { title: '隐私协议', info: 'userprivacyinfo', save: 'userprivacysave' },
  { title: '关于我们', info: 'aboutusinfo', save: 'aboutussave' },
  { title: '资质证照', info: 'intelligentinfo', save: 'intelligentsave' },
  { title: '注销协议', info: 'useraccountcancelinfo', save: 'useraccountcancelsave' },
  { title: '注销声明', info: 'useraccountcancelnoticeinfo', save: 'useraccountcancelnoticesave' },
  { title: '优惠券协议', info: 'coupon/agreement/info', save: 'coupon/agreement/save' },
];
const formValidate = reactive({
  agreement: '',
});
const agreementValue = ref('userinfo');
const agreementSave = ref('usersave');
const keyIndex = ref(0);

function getAgreementConfig(info) {
  return tabList.find((item) => item.info === info);
}

function getInfo(data) {
  if (!data) {
    return ElMessage.error('协议配置不正确，请刷新后重试');
  }
  loading.value = true;
  formValidate.agreement = '';
  agreementInfoApi(data)
    .then((res) => {
      formValidate.agreement = res ? JSON.parse(res).agreement : '';
      keyIndex.value += 1;
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
}
function submenus() {
  if (formValidate.agreement === '' || !formValidate.agreement) {
    return ElMessage.warning('请输入协议信息！');
  } else {
    agreementSaveApi(agreementSave.value, formValidate).then(async (res) => {
      ElMessage.success('保存成功');
    });
  }
}
// tab-change 回调直接传入当前 tab 的 name（即 item.info），此时 v-model 已更新
function tabStatus(info) {
  const currentAgreement = getAgreementConfig(info);
  if (!currentAgreement) {
    return ElMessage.error('协议配置不正确，请刷新后重试');
  }
  agreementSave.value = currentAgreement.save;
  getInfo(currentAgreement.info);
}

// created
if (
  checkPermi([
    'admin:agreement:user:info',
    'admin:agreement:userprivacy:info',
    'admin:agreement:useraccountcancel:info',
    'admin:agreement:useraccountcancelnotice:info',
    'admin:agreement:aboutus:info',
    'admin:agreement:intelligent:info',
    'admin:agreement:coupon:agreement:info',
  ])
)
  getInfo(agreementValue.value);
</script>
<style scoped lang="scss">
.box-card :v-deep.el-card__body {
  padding-left: 0px;
}

:v-deep.el-tabs__item.is-active {
  color: var(--prev-color-primary);
  background-color: #ccc;
  background: #437efd1e;
}

:v-deep.el-tabs--left .el-tabs__header.is-left {
  float: left;
  margin-bottom: 0;
  margin-right: 20px;
  height: 700px;
}

.verticalLine {
  border: 2px solid var(--prev-color-primary);
  margin-right: 10px;
}

.btn {
  border-top: 1px solid #ccc;
  margin-top: 20px;

  .button {
    display: block;
    margin: 17px auto 0px;
  }
}

.content {
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;

  img {
    max-width: 100%;
  }

  .phoneBox {
    width: 302px;
    height: 543px;
    background-image: url('../../../assets/imgs/phoneBox.png');
    background-repeat: no-repeat;
    background-position: center;
    background-size: cover;
    overflow: hidden;
    margin-right: 20px;

    .fontBox {
      margin: 0 auto;
      margin-top: 45px;
      width: 255px;
      height: 450px;
      background: #ffffff;
      border: 1px solid #e2e2e2;
      padding: 10px;
      overflow: hidden;
      overflow-y: auto;

      :deep(.img) {
        width: 100% !important;
      }
    }
  }

  .ueditor {
    flex: 1;

    .font {
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 30px;
    }
  }
}
:deep(.el-tabs__item){
    margin-bottom: 0 !important;
    padding-bottom: 5px !important;
}
</style>
