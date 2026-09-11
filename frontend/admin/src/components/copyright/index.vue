<template>
  <div class="ivu-global-footer i-copyright">
    <div class="footers acea-row">
      <div class="title" v-text="copyrightNew"></div>
    </div>
  </div>
</template>
<script setup>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { ref, onMounted } from 'vue';
import { getCopyrightInfo } from '@/libs/copyPublic';

defineOptions({ name: 'i-copyright' });

const copyright = ref('Copyright © 2026 西安众邦网络科技有限公司');
const copyrightNew = ref('');
const version = ref('');

function getVersion() {
  getCopyrightInfo().then((res) => {
    const data = res || {};
    copyrightNew.value = data.companyName ? data.companyName : copyright.value;
    version.value = data.version || '';
  });
}

onMounted(() => {
  getVersion();
});
</script>
<style lang="scss" scoped>
.ivu-global-footer {
  /* margin: 48px 0 24px 0; */
  /* padding: 0 16px; */
  // margin: 15px 0px;
  text-align: center;
  box-sizing: border-box;
  // margin-left: 210px;
}
.i-copyright {
  flex: 0 0 auto;
}
.footers {
  flex-direction: column;
  align-items: center;
}
.footers .version {
  margin-top: 2px;
  font-size: 13px;
  color: #808695;
}
.ivu-global-footer-links {
  margin-bottom: 8px;
}
.ivu-global-footer-links a:not(:last-child) {
  margin-right: 40px;
}
.ivu-global-footer-links a {
  font-size: 14px;
  color: #808695;
  transition: all 0.2s ease-in-out;
}
.ivu-global-footer-copyright {
  color: #808695;
  font-size: 14px;
}
</style>
