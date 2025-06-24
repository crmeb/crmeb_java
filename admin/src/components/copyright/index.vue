<template>
  <div class="ivu-global-footer i-copyright">
    <div class="footers acea-row">
      <div class="title" v-text="copyrightNew"></div>
    </div>
  </div>
</template>
<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { getCopyrightInfo } from '@/libs/copyPublic';

export default {
  name: 'i-copyright',
  data() {
    return {
      copyright: 'Copyright © 2025 西安众邦网络科技有限公司',
      copyrightNew: '',
    };
  },
  mounted() {
    this.getVersion();
  },
  methods: {
    getVersion() {
      getCopyrightInfo().then((res) => {
        const data = res || {};
        this.copyrightNew = data.companyName ? data.companyName : this.copyright;
      });
    },
  },
};
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
