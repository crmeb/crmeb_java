<template>
  <div class="divBox">
    <el-card class="box-card" shadow="never" :bordered="false" :body-style="{ padding: '40px 50px' }">
      <div class="detail-section">
        <h3 class="detail-title">使用说明</h3>
        <div class="detail-centent">
          根据《商家自营类小程序运营规范》,特定类型的小程序需要在平台完成发货信息录入及确认收货流程后方可进行资金结算。符合以下情形的小程序，需接入微信开放平台订单发货管理功能：<br />
          1. 小程序内提供珠宝玉石、3C数码、盲盒等商品在线销售及配送服务；<br />

          2.小程序的账号主体为近一年内新成立的企业或个体户主体；<br />

          3. 小程序的账号管理员、运营者等角色，与其它高风险小程序存在关联；<br />

          4. 小程序内经营预售商品。<br />
          <br />
          在小程序mp后台菜单出现一个发货信息管理菜单
          <el-link :underline="false" type="primary"
            >（图例）<el-image
              class="images"
              :src="require('@/assets/imgs/wxtu.png')"
              :preview-src-list="[require('@/assets/imgs/wxtu.png')]"
            ></el-image>
          </el-link>
          <span class="textE93323"
            >，只有该类小程序需要开启发货配置开关；需要若没有这个菜单，则无需开启，以免出错；</span
          >之前已产生的订单不会去主动触发，需要微信开放平台手动发货才可提现。<br />
          快递物流：资金将于订单发货后的第10天，系统自动确认收货后结算；<br />
          自提/同城配送/虚拟发货：资金将于订单发货后的第2天，系统自动确认收货后结算；<br />
          <div class="acea-row color">
            详情见<el-link
              :underline="false"
              type="primary"
              target="_blank"
              href="https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/order-shipping/order-shipping.html#一、发货信息录入接口"
              >《小程序发货信息管理服务》</el-link
            >
            <el-link
              :underline="false"
              type="primary"
              target="_blank"
              href="https://developers.weixin.qq.com/miniprogram/product/jiaoyilei/yunyingguifan.html#_1-2-商品规范"
              >《交易类小程序运营规范》</el-link
            >
          </div>
          <div
            v-hasPermi="['admin:wechat:mini:shipping:switch:update', 'admin:wechat:mini:shipping:switch:get']"
            class="mt20"
          >
            发货配置<el-switch
              v-model="shippingSwitch"
              active-value="1"
              inactive-value="0"
              class="ml10"
              active-text="开启"
              inactive-text="关闭"
              @change="handleStatusChange"
            ></el-switch>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
import { checkPermi } from '@/utils/permission';
import { Debounce } from '@/utils/validate';
import { wechatGetShippingSwitchApi, wechatUpdateShippingSwitchApi } from '@/api/wxApi'; // 权限判断函数
export default {
  name: 'deliveryManagement',
  data() {
    return {
      delivery: false,
      shippingSwitch: '0',
    };
  },
  mounted() {
    if (checkPermi(['admin:wechat:mini:shipping:switch:get'])) this.getWechatShippingSwitch();
  },
  methods: {
    checkPermi,
    getWechatShippingSwitch() {
      wechatGetShippingSwitchApi().then(async (res) => {
        this.shippingSwitch = res.value;
      });
    },
    //修改状态
    handleStatusChange: Debounce(function () {
      wechatUpdateShippingSwitchApi({ value: this.shippingSwitch }).then((res) => {
        this.$message.success('更新状态成功');
        this.getWechatShippingSwitch();
      });
    }),
  },
};
</script>

<style scoped lang="scss">
.images {
  position: absolute;
  width: 54px;
  height: 27px;
  left: 3px;
}
::v-deep .el-image__inner {
  opacity: 0;
}
.detail-centent {
  line-height: 30px;
  font-size: 14px;
}

.textE93323 {
  color: #e93323;
}

.detail-title {
  font-size: 16px;
}
::v-deep .el-icon-circle-close {
  color: #999;
}
</style>
