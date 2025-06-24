<template>
  <div>
    <el-drawer ref="userDetailFrom" :visible.sync="dialogUserDetail" size="1100px" @close="handleClose">
      <div slot="title" class="demo-drawer_title">
        <div class="con-head">
          <img :src="userDetailData.avatar" alt="" />
          <span class="nickname">{{ userDetailData.nickname }}</span>
        </div>
        <div class="acea-row info-row">
          <div class="info-row-item">
            <div class="info-row-item-title">余额</div>
            <div>{{ userDetailData.nowMoney }}</div>
          </div>
          <div class="info-row-item">
            <div class="info-row-item-title">积分</div>
            <div>{{ userDetailData.integral }}</div>
          </div>
          <div class="info-row-item">
            <div class="info-row-item-title">经验</div>
            <div>{{ userDetailData.experience }}</div>
          </div>
          <div class="info-row-item">
            <div class="info-row-item-title">佣金</div>
            <div>{{ userDetailData.brokeragePrice }}</div>
          </div>
          <div class="info-row-item">
            <div class="info-row-item-title">消费次数</div>
            <div>{{ userDetailData.payCount }}</div>
          </div>
          <div class="info-row-item">
            <div class="info-row-item-title">连续签到</div>
            <div>{{ userDetailData.signNum }}</div>
          </div>
        </div>
      </div>
      <div class="demo-drawer__content">
        <div class="description" v-if="userDetailData">
          <!-- Tabs -->
          <el-tabs type="border-card" v-model="tabsVal">
            <!-- 用户信息 -->
            <el-tab-pane name="0" label="用户信息">
              <div class="user-info">
                <div class="section">
                  <div class="section-hd">基本信息</div>
                  <div class="section-bd">
                    <div class="item">
                      <div>用户ID：</div>
                      <div class="value">{{ userDetailData.uid }}</div>
                    </div>
                    <div class="item">
                      <div>真实姓名：</div>
                      <div class="value">{{ userDetailData.realName || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>用户账号：</div>
                      <div class="value">{{ userDetailData.account || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>用户电话：</div>
                      <div class="value">{{ userDetailData.phone }}</div>
                    </div>
                    <div class="item">
                      <div>生日：</div>
                      <div class="value">{{ userDetailData.birthday || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>性别：</div>
                      <div class="value">
                        {{
                          userDetailData.sex == 0
                            ? '未知'
                            : userDetailData.sex == 1
                            ? '男'
                            : userDetailData.sex == 2
                            ? '女'
                            : '保密'
                        }}
                      </div>
                    </div>
                    <div class="item">
                      <div>国家：</div>
                      <div class="value">{{ userDetailData.country == 'CN' ? '中国' : '其他' || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>用户地址：</div>
                      <div class="value">
                        {{ userDetailData.addres || '-' }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="user-info">
                <div class="section">
                  <div class="section-hd-other">用户概况</div>
                  <div class="section-bd">
                    <div class="item">
                      <div>创建ip：</div>
                      <div class="value">{{ userDetailData.addIp || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>注册类型：</div>
                      <div class="value">
                        {{
                          userDetailData.userType == 'wechat'
                            ? '公众号'
                            : userDetailData.userType == 'routine'
                            ? '小程序'
                            : userDetailData.userType == 'h5'
                            ? 'H5'
                            : userDetailData.userType == 'iosWx'
                            ? '微信ios'
                            : userDetailData.userType == 'androidWx'
                            ? '微信安卓'
                            : 'ios' || '-'
                        }}
                      </div>
                    </div>
                    <div class="item">
                      <div>添加时间：</div>
                      <div class="value">{{ userDetailData.createTime || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>状态：</div>
                      <div class="value">{{ userDetailData.status == true ? '正常' : '禁止' || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>最后一次登录ip：</div>
                      <div class="value">{{ userDetailData.lastIp || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>最后一次登录时间：</div>
                      <div class="value">{{ userDetailData.lastLoginTime || '-' }}</div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="user-info">
                <div class="section">
                  <div class="section-hd">推广信息</div>
                  <div class="section-bd">
                    <div class="item">
                      <div>是否为推广员：</div>
                      <div class="value">{{ userDetailData.isPromoter == true ? '是' : '否' || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>下级人数：</div>
                      <div class="value">{{ userDetailData.spreadCount || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>成为分销员时间：</div>
                      <div class="value">{{ userDetailData.promoterTime || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>上级推广员ID：</div>
                      <div class="value">{{ userDetailData.spreadUid || '-' }}</div>
                    </div>
                    <div class="item">
                      <div>绑定上级推广员时间：</div>
                      <div class="value">{{ userDetailData.spreadTime || '-' }}</div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="user-info">
                <div class="section">
                  <div class="section-hd">用户备注</div>
                  <div class="section-bd">
                    <div class="item">
                      <div>备注：</div>
                      <div class="value">{{ userDetailData.mark || '-' }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <!-- 消费记录 -->
            <el-tab-pane name="1" label="消费记录">
              <el-table :data="tableData" size="small" class="mt20">
                <el-table-column prop="orderId" label="订单ID" min-width="100" />
                <el-table-column prop="realName" label="收货人" />
                <el-table-column prop="totalNum" label="商品数量" />
                <el-table-column prop="totalPrice" label="商品总价" />
                <el-table-column prop="payPrice" label="实付金额" />
                <el-table-column prop="payTime" label="交易完成时间" />
              </el-table>
            </el-tab-pane>
            <!-- 积分明细 -->
            <el-tab-pane name="2" label="积分明细">
              <el-table :data="tableData" size="small" class="mt20">
                <el-table-column prop="title" label="来源/用途" min-width="100" />
                <el-table-column prop="integral" label="积分变化" />
                <el-table-column prop="balance" label="变化后积分" />
                <el-table-column prop="updateTime" label="日期" />
                <el-table-column prop="mark" label="备注" />
              </el-table>
            </el-tab-pane>
            <!-- 签到记录 -->
            <el-tab-pane name="3" label="签到记录">
              <el-table :data="tableData" size="small" class="mt20">
                <el-table-column prop="title" label="动作" min-width="100" />
                <el-table-column prop="number" label="获得积分" min-width="120" />
                <el-table-column prop="createTime" label="签到时间" />
              </el-table>
            </el-tab-pane>
            <!-- 持有优惠券 -->
            <el-tab-pane name="4" label="持有优惠券">
              <el-table :data="tableData" size="small" class="mt20">
                <el-table-column prop="name" label="优惠券名称" />
                <el-table-column prop="money" label="面值" />
                <el-table-column prop="endTime" label="有效期" />
                <el-table-column prop="minPrice" label="最低消费额" />
                <el-table-column prop="updateTime" label="兑换时间" />
              </el-table>
            </el-tab-pane>
            <!-- 余额变动 -->
            <el-tab-pane name="5" label="余额变动">
              <el-table :data="tableData" size="small" class="mt20">
                <el-table-column prop="number" label="变动金额" />
                <el-table-column prop="balance" label="变动后" />
                <el-table-column prop="title" label="类型" />
                <el-table-column prop="add_time" label="创建时间" />
                <el-table-column prop="mark" label="备注" />
              </el-table>
            </el-tab-pane>
            <!-- 好有关系 -->
            <el-tab-pane name="6" label="好友关系">
              <el-table :data="tableData" size="small" class="mt20">
                <el-table-column prop="uid" label="ID" />
                <el-table-column prop="nickname" label="昵称" />
                <el-table-column prop="level" label="等级" />
                <el-table-column prop="createTime" label="加入时间" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
          <div class="block" v-if="tabsVal != '0'">
            <el-pagination
              :page-size="paginationData.limit"
              :total="paginationData.total"
              :current-page="paginationData.page"
              @size-change="handleSizeChange"
              @current-change="pageChange"
              background
              :page-sizes="[10, 20, 30, 40]"
              layout="total, sizes, prev, pager, next, jumper"
            />
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { infobyconditionApi, topdetailApi } from '@/api/user';
import { integralListApi } from '@/api/marketing';
export default {
  name: 'detailUser',
  props: {
    userNo: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      tableData: [],
      dialogUserDetail: false,
      userDetailData: {},
      tabsVal: '0',
      paginationData: {
        page: 1,
        limit: 10,
        type: 0,
        userId: '',
        total: 10,
      },
    };
  },
  watch: {
    tabsVal(val) {
      this.paginationData.userId = this.userNo;
      this.paginationData.page = 1;
      this.paginationData.type = val - 1;
      if (val != 0) {
        if (this.paginationData.type == '1') {
          this.getIntergralList();
        } else {
          this.getListData();
        }
      }
    },
  },
  methods: {
    getNewList() {
      if (this.paginationData.type == '1') {
        this.getIntergralList();
      } else {
        this.getListData();
      }
    },
    handleSizeChange(val) {
      this.paginationData.limit = val;
      this.getNewList();
    },
    pageChange(val) {
      this.paginationData.page = val;
      this.getNewList();
    },
    handleClose() {
      this.dialogUserDetail = false;
      this.tabsVal = '0';
    },
    getIntergralList() {
      integralListApi(
        { limit: this.paginationData.limit, page: this.paginationData.page },
        { uid: this.paginationData.uid },
      ).then((res) => {
        this.tableData = res.list;
        this.paginationData.total = res.total;
      });
    },
    getListData() {
      infobyconditionApi(this.paginationData).then((res) => {
        this.tableData = res.list;
        this.paginationData.total = res.total;
      });
    },
    getUserDetail(id) {
      topdetailApi({ userId: id }).then((res) => {
        this.userDetailData = res.user;
      });
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .el-drawer__header {
  display: flex !important;
  align-items: flex-start !important;
  margin: 0 !important;
  padding: 30px 15px 0 5px !important;
}
::v-deep .demo-drawer_title {
  width: 90%;
}
::v-deep .el-drawer__body {
  padding: 0 0 30px 0 !important;
}
.InvoiceList {
  ::v-deep.el-collapse-item__header {
    font-size: 12px;
    color: #606266;
  }
}

.wrapper {
  background-color: #fff;
  margin-top: 7px;
  padding: 10px 12px;
  &-num {
    font-size: 10px;
    color: #999999;
  }

  &-title {
    color: #666666;
    font-size: 12px;
  }

  &-img {
    width: 60px;
    height: 60px;
    margin-right: 10px;
    border-radius: 7px;
    overflow: hidden;
    margin-bottom: 10px;

    image {
      width: 100%;
      height: 100%;
    }

    &:nth-child(5n) {
      margin-right: 0;
    }
  }
}

.title {
  font-size: 36px;
}

.demo-drawer__content {
  // padding: 0 30px;
  overflow: hidden;
}

.demo-image__preview {
  display: inline-block;
  .el-image {
    width: 50px;
    height: 50px;
  }
}

.logistics {
  align-items: center;
  padding: 10px 0px;
  .logistics_img {
    width: 45px;
    height: 45px;
    margin-right: 12px;
    img {
      width: 100%;
      height: 100%;
    }
  }
  .logistics_cent {
    span {
      display: block;
      font-size: 12px;
    }
  }
}

.trees-coadd {
  width: 100%;
  height: 400px;
  border-radius: 4px;
  overflow: hidden;
  .scollhide {
    width: 100%;
    height: 100%;
    overflow: auto;
    margin-left: 18px;
    padding: 10px 0 10px 0;
    box-sizing: border-box;
    .content {
      font-size: 12px;
    }

    .time {
      font-size: 12px;
      color: #2d8cf0;
    }
  }
}

.title {
  margin-bottom: 14px;
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.description {
  &-term {
    display: table-cell;
    padding-bottom: 5px;
    line-height: 20px;
    width: 50%;
    font-size: 12px;
    color: #606266;
  }
  ::v-deep .el-divider--horizontal {
    margin: 12px 0 !important;
  }
}
.description-term img {
  width: 60px;
  height: 60px;
}
.description-term {
  display: flex;
  align-items: center;
}
.con-head {
  padding: 0 30px;
  display: flex;
  align-items: center;
  img {
    width: 60px;
    height: 60px;
    margin-right: 15px;
    border-radius: 50%;
  }
  .nickname {
    font-weight: 500;
    font-size: 16px;
    line-height: 16px;
    color: rgba(0, 0, 0, 0.85);
  }
}

.info-row {
  flex-wrap: nowrap;
  padding: 20px 35px 24px 30px;
  border-bottom: 1px dashed #eee;

  &-item {
    flex: none;
    width: 155px;
    font-size: 14px;
    line-height: 14px;
    color: rgba(0, 0, 0, 0.85);

    &-title {
      margin-bottom: 12px;
      font-size: 13px;
      line-height: 13px;
      color: #666666;
    }
  }
}
.user-info {
  .section {
    padding: 25px 0;
    border-bottom: 1px dashed #eee;
    &-hd {
      padding-left: 10px;
      border-left: 3px solid var(--prev-color-primary);
      font-weight: 500;
      font-size: 14px;
      line-height: 16px;
      color: #303133;
    }

    &-bd {
      display: flex;
      flex-wrap: wrap;
    }

    .item {
      width: 30%;
      // flex: 0 0 calc(~"(100% - 60px) / 3");
      display: flex;
      margin: 16px 30px 0 0;
      font-size: 13px;
      color: #606266;

      &:nth-child(3n + 3) {
        margin: 16px 0 0;
      }
    }

    .value {
      flex: 1;
    }
    .avatar {
      width: 60px;
      height: 60px;
      overflow: hidden;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
}
.section-hd-other {
  padding-left: 10px;
  border-left: 3px solid var(--prev-color-primary);
  font-weight: 500;
  font-size: 14px;
  line-height: 16px;
  color: #303133;
}
::v-deep .el-tabs__nav .el-tabs__item:nth-of-type(1) {
  padding-left: 20px !important;
}
.block {
  padding: 0 35px;
}
</style>
