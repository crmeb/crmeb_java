<template>
  <div>
    <div class="acea-row row-middle border_bottom pb-24" v-if="psInfo">
      <div class="avatar mr20"><img :src="psInfo.user.avatar"></div>
      <div class="dashboard-workplace-header-tip">
      <p class="dashboard-workplace-header-tip-title" v-text="psInfo.user.nickname || '-'"></p>
      <div class="dashboard-workplace-header-tip-desc">
      <span class="dashboard-workplace-header-tip-desc-sp pb-1">余额: {{ psInfo.balance }}</span>
      <span class="dashboard-workplace-header-tip-desc-sp pb-1">总计订单: {{ psInfo.allOrderCount }}</span>
      <span class="dashboard-workplace-header-tip-desc-sp pb-1">总消费金额: {{ psInfo.allConsumeCount }}</span>
      <span class="dashboard-workplace-header-tip-desc-sp">积分: {{ psInfo.integralCount }}</span>
      <span class="dashboard-workplace-header-tip-desc-sp">本月订单: {{ psInfo.mothOrderCount }}</span>
      <span class="dashboard-workplace-header-tip-desc-sp">本月消费金额: {{ psInfo.mothConsumeCount }}</span>
      </div>
      </div>
    </div>
    <el-row align="middle" :gutter="10" class="ivu-mt mt20">
      <el-col :span="4">
        <el-menu
          default-active="0"
          class="el-menu-vertical-demo"
          @select="changeType"
        >
          <el-menu-item :name="item.val" v-for="(item, index) in list" :key="index" :index="item.val">
            <span slot="title">{{item.label}}</span>
          </el-menu-item >
        </el-menu>
      </el-col>
      <el-col :span="20">
        <el-table :data="tableData.data" class="tabNumWidth"  v-loading="loading" max-height="400">
          <el-table-column
            :prop="item.key"
            :label="item.title"
            width="item.minWidth"
            :show-overflow-tooltip="true"
            v-for="(item, index) in columns" :key="index"
          />
        </el-table>
        <div class="block">
          <el-pagination
            :page-sizes="[6, 12, 18, 24]"
            :page-size="tableFrom.limit"
            :current-page="tableFrom.page"
            layout="total, sizes, prev, pager, next, jumper"
            :total="tableData.total"
            @size-change="handleSizeChange"
            @current-change="pageChange"
          />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import { infobyconditionApi, topdetailApi } from '@/api/user'
  import { integralListApi } from '@/api/marketing'
  export default {
    name: "UserDetails",
    props:{
      uid: {
        type: Number,
        default: null
      }
    },
    data() {
      return {
        loading: false,
        columns: [],
        Visible: false,
        list: [
          { val: '0', label: '消费记录' },
          { val: '1', label: '积分明细' },
          { val: '2', label: '签到记录' },
          { val: '3', label: '持有优惠券' },
          { val: '4', label: '余额变动' },
          { val: '5', label: '好友关系' }
        ],
        tableData: {
          data: [],
          total: 0
        },
        tableFrom: {
          page: 1,
          limit: 6,
          type : '0',
          userId : ''
        },
        psInfo: null
      }
    },
    mounted(){
      if(this.uid){
        this.getHeader()
        this.getInfo()
      }

    },
    methods: {
      changeType(key) {
        this.tableFrom.type = key
        if(key === '1'){
          this.integral()
        }else{
          this.getInfo()
        }
      },
      integral() {
        this.loading = true
        integralListApi({ limit:this.tableFrom.limit, page: this.tableFrom.page}, {uid:this.uid}).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          this.columns = [
            {
              title: '来源/用途',
              key: 'title',
              minWidth: 120
            },
            {
              title: '积分变化',
              key: 'integral',
              minWidth: 120
            },
            {
              title: '变化后积分',
              key: 'balance',
              minWidth: 120
            },
            {
              title: '日期',
              key: 'updateTime',
              minWidth: 120
            },
            {
              title: '备注',
              key: 'mark',
              minWidth: 120
            }
          ]
          this.loading = false
        }).catch(res => {
          this.loading = false
        })
      },
      getInfo() {
        this.tableFrom.userId = this.uid
        this.loading = true
        infobyconditionApi(this.tableFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          switch (this.tableFrom.type) {
            case '0':
              this.columns = [
                {
                  title: '订单ID',
                  key: 'orderId',
                  minWidth: 250
                },
                {
                  title: '收货人',
                  key: 'realName',
                  minWidth: 90
                },
                {
                  title: '商品数量',
                  key: 'totalNum',
                  minWidth: 80
                },
                {
                  title: '商品总价',
                  key: 'totalPrice',
                  minWidth: 90
                },
                {
                  title: '实付金额',
                  key: 'payPrice',
                  minWidth: 90
                },
                {
                  title: '交易完成时间',
                  key: 'payTime',
                  minWidth: 160
                }
              ]
              break;
            case '2':
              this.columns = [
                {
                  title: '动作',
                  key: 'title',
                  minWidth: 120
                },
                {
                  title: '获得积分',
                  key: 'number',
                  minWidth: 120
                },
                {
                  title: '签到时间',
                  key: 'createTime',
                  minWidth: 120
                },
                {
                  title: '备注',
                  key: 'title',
                  minWidth: 120
                }
              ]
              break;
            case '3':
              this.columns = [
                {
                  title: '优惠券名称',
                  key: 'name',
                  minWidth: 120
                },
                {
                  title: '面值',
                  key: 'money',
                  minWidth: 120
                },
                {
                  title: '有效期',
                  key: 'endTime',
                  minWidth: 120
                },
                {
                  title: '最低消费额',
                  key: 'minPrice',
                  minWidth: 120
                },
                {
                  title: '兑换时间',
                  key: 'updateTime',
                  minWidth: 120
                }
              ]
              break;
            case '4':
              this.columns = [
                {
                  title: '变动金额',
                  key: 'number',
                  minWidth: 120
                },
                {
                  title: '变动后',
                  key: 'balance',
                  minWidth: 120
                },
                {
                  title: '类型',
                  key: 'title',
                  minWidth: 120
                },
                {
                  title: '创建时间',
                  key: 'add_time',
                  minWidth: 120
                },
                {
                  title: '备注',
                  key: 'mark',
                  minWidth: 120
                }
              ]
              break;
            default:
              this.columns = [
                {
                  title: 'ID',
                  key: 'uid',
                  minWidth: 120
                },
                {
                  title: '昵称',
                  key: 'nickname',
                  minWidth: 120
                },
                {
                  title: '等级',
                  key: 'level',
                  minWidth: 120
                },
                {
                  title: '加入时间',
                  key: 'createTime',
                  minWidth: 120
                }
              ]
          }
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      },
      pageChange(page) {
        this.tableFrom.page = page
        if(this.tableFrom.type === '1'){
          this.integral()
        }else{
          this.getInfo()
        }
      },
      handleSizeChange(val) {
        this.tableFrom.limit = val
        if(this.tableFrom.type === '1'){
          this.integral()
        }else{
          this.getInfo()
        }
      },
      getHeader() {
        topdetailApi({userId : this.uid}).then(res => {
          this.psInfo = res
        })
      }
    }
  }
</script>

<style scoped lang="scss">
  .avatar{
    width: 70px;
    height: 70px;
    border-radius: 50%;
    overflow: hidden;
    margin-left: 18px;
    img{
      width: 100%;
      height: 100%;
    }
  }
  .dashboard-workplace {
    &-header {
      &-avatar {
        width: 64px;
        height: 64px;
        border-radius: 50%;
        margin-right: 16px;
        font-weight: 600;
      }

      &-tip {
        width: 82%;
        display: inline-block;
        vertical-align: middle;
        margin-top: -12px;
        &-title {
          font-size: 13px;
          font-weight: 600;
          color: rgba(0, 0, 0, 0.85);
          line-height: 18px;
          padding: 10px 0 10px;
        }

        &-desc {
          &-sp {
            width: 32%;
            color: #17233D;
            font-size: 13px;
            display: inline-block;
          }
        }
      }

      &-extra {
        .ivu-col {
          p {
            text-align: right;
          }

          p:first-child {
            span:first-child {
              margin-right: 4px;
            }

            span:last-child {
              color: #808695;
            }
          }

          p:last-child {
            font-size: 22px;
          }
        }
      }
    }
  }
  .pb-1{
    padding-bottom: 10px;
  }
  .mr20{
    margin: 0 20px;
  }
  .border_bottom{
    border-bottom: 1px solid #E7EAEC;
  }
  .pb-24{
    padding-bottom: 24px;
  }
</style>
