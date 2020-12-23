<template>
  <div class="divBox">
    <el-card v-if="isShowList" v-loading="fullscreenLoading" class="box-card mb20">
      <div class="content acea-row row-middle">
        <div class="demo-basic--circle acea-row row-middle">
          <div class="circleUrl mr20"><img :src="circleUrl"></div>
          <!--<el-avatar :size="50" :src="circleUrl" class="mr20" />-->
          <div class="dashboard-workplace-header-tip">
            <div class="dashboard-workplace-header-tip-title">{{ smsAccount }}，祝您每一天开心！</div>
            <div class="dashboard-workplace-header-tip-desc">
              <span class="mr10" @click="onChangePassswordIndex">修改密码</span>
              <span class="mr10" @click="onChangePhone">修改手机号</span>
              <span @click="signOut" class="mr10">退出登录</span>
            </div>
          </div>
        </div>
        <div class="dashboard">
          <div class="dashboard-workplace-header-extra">
            <div class="acea-row">
              <div class="header-extra">
                <p class="mb5"><span>短信条数</span></p>
                <p class="mb5">{{sms.num || 0}}</p>
                <el-button size="mini" type="primary" @click="sms.open ===0?onOpen('sms'):mealPay('sms')" v-text="sms.open ===0?'开通服务':'套餐购买'"></el-button>
              </div>
              <div class="header-extra">
                <p class="mb5"><span>采集次数</span></p>
                <p class="mb5">{{copy.num || 0}}</p>
                <el-button size="mini" type="primary" @click="copy.open ===0?onOpen('copy'):mealPay('copy')" v-text="copy.open ===0?'开通服务':'套餐购买'"></el-button>
              </div>
              <div class="header-extra">
                <p class="mb5"><span>物流查询次数</span></p>
                <p class="mb5">{{query.num || 0}}</p>
                <el-button size="mini" type="primary" @click="query.open ===0?onOpen('expr_query'):mealPay('expr_query')" v-text="query.open ===0?'开通服务':'套餐购买'"></el-button>
              </div>
              <div class="header-extra" style="border: none;">
                <p class="mb5"><span>面单打印次数</span> </p>
                <p class="mb5">{{dump.num || 0}}</p>
                <el-button size="mini" type="primary" @click="dump.open ===0?onOpen('expr_dump'):mealPay('expr_dump')" v-text="dump.open ===0?'开通服务':'套餐购买'"></el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    <el-card class="box-card" v-loading="loading">
      <table-list v-if="isShowList"  ref="tableLists"  :sms="sms" :copy="copy" :dump="dump" :query="query" :accountInfo="accountInfo" @openService="openService"/>
      <login-from v-if="isShowLogn" @on-changes="onChangeReg" @on-Login="onLogin" />
      <forget-password v-if="isShow" @goback="goback" @on-Login="onLogin" :isIndex="isIndex"></forget-password>
      <forget-phone v-if="isForgetPhone" @gobackPhone="gobackPhone" @on-Login="onLogin"></forget-phone>
      <register-from v-if="isShowReg" @on-change="logoup" />
    </el-card>

  </div>
</template>

<script>
import tableList from './components/tableList'
import loginFrom from './components/loginFrom'
import registerFrom from './components/register'
import forgetPassword from './components/forgetPassword';
import forgetPhone from './components/forgetPhone';
import { logoutApi, smsNumberApi, smsInfoApi } from '@/api/sms'
import { mapGetters } from 'vuex'
export default {
  name: 'SmsConfig',
  components: { tableList, loginFrom, registerFrom, forgetPassword, forgetPhone },
  data() {
    return {
      fullscreenLoading: false,
      loading: false,
      smsAccount: '',
      circleUrl: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      accountInfo:{},
      spinShow: false,
      isForgetPhone: false, // 修改手机号
      isIndex: false, // 判断忘记密码返回的路径
      isShowLogn: false, // 登录
      isShow: false, // 修改密码
      isShowReg: false, // 注册
      isShowList: false, // 登录之后列表
      sms: { open: 0 }, // 短信信息
      query: { open: 0 }, // 物流查询
      dump: { open: 0 }, // 电子面单打印
      copy: { open: 0 } // 商品采集
    }
  },
  computed: {
    ...mapGetters([
      'isLogin'
    ])
  },
  mounted() {
    this.onIsLogin()
    // if (!this.isLogin) {
    //   this.onIsLogin()
    // } else {
    //   this.isShowList = true
    // }
  },
  methods: {
    // 开通服务
    openService (val) {
      this.getNumber();
    },
    onOpen (val) {
      this.$refs.tableLists.onOpenIndex(val);
    },
    // 手机号返回
    gobackPhone () {
      this.isShowList = true;
      this.isForgetPhone = false;
    },
    onChangePhone () {
      this.isForgetPhone = true
      this.isShowLogn = false;
      this.isShowList = false;
    },
    // 密码返回
    goback () {
      if (this.isIndex) {
        this.isShowList = true;
        this.isShow = false;
      } else {
        this.isShowLogn = true;
        this.isShow = false;
      }
    },
    // 修改密码
    onChangePassswordIndex () {
      this.isIndex = true;
      this.passsword();
    },
    passsword () {
      this.isShowLogn = false;
      this.isShow = true;
      this.isShowList = false;
    },
    mealPay (val) {
      this.$router.push({ path:'/operation/systemSms/pay',query:{type:val}});
    },
    // 剩余条数
    getNumber() {
      this.loading = true;
      smsInfoApi().then(async res => {
        let data = res;
        this.sms = {
          num: data.sms.num,
          open: data.sms.open,
          surp: data.sms.open
        };
        this.query = {
          num: data.query.num,
          open: data.query.open,
          surp: data.query.open
        };
        this.dump = {
          num: data.dump.num,
          open: data.dump.open,
          surp: data.dump.open
        };
        this.copy = {
          num: data.copy.num,
          open: data.copy.open,
          surp: data.copy.open
        };
        this.loading = false;
        this.smsAccount = data.account;
        this.accountInfo = data;
      }).catch(res => {
        this.isShowLogn = true;
        this.isShowList = false;
        this.loading = false;
      })
    },
    // 登录跳转
    onLogin() {
      const url = this.$route.query.url
      if (url) {
        this.$router.replace(url)
      } else {
        this.getNumber()
        this.isShowLogn = false
        this.isShow = false
        this.isShowReg = false
        this.isShowList = true
      }
    },
    // 查看是否登录
    onIsLogin() {
      this.fullscreenLoading = true
      this.$store.dispatch('user/isLogin').then(async res => {
        const data = res
        this.isShowLogn = !data.status
        this.isShowList = data.status
        if (data.status) {
          this.smsAccount = data.info
          this.getNumber()
        }
        this.fullscreenLoading = false
      }).catch(res => {
        this.fullscreenLoading = false
        this.isShowLogn = true
      })
    },
    // 退出登录
    signOut() {
      logoutApi().then(async res => {
        this.isShowLogn = true
        this.isShowList = false
        this.$store.dispatch('user/isLogin')
      })
    },
    // 立即注册
    onChangeReg() {
      this.isShowLogn = false
      this.isShow = false
      this.isShowReg = true
    },
    // 立即登录
    logoup() {
      this.isShowLogn = true
      this.isShow = false
      this.isShowReg = false
    }
  }
}
</script>

<style scoped lang="scss">
  .dashboard{
    width: auto !important;
    min-width: 300px;
  }
  .header-extra{
    /*width: 25%;*/
    border-right: 1px solid #E9E9E9;
    text-align: center;
    padding: 0 18px;
  }
  $cursor: #1890ff;
  .content{
    justify-content: space-between;
  }
  .circleUrl{
    width: 50px;
    height: 50px;
  }
  .circleUrl img{
    width: 100%;
    height: 100%;
    border-radius: 50%;
    overflow: hidden;
  }
  .rR{
    text-align: center;
    font-size: 22px;
    display: block;
  }
  .dashboard-workplace-header-tip {
    display: inline-block;
    vertical-align: middle;
  }
  .dashboard-workplace-header-tip-title {
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 12px;
  }
  .dashboard-workplace-header-tip-desc{
    /*line-height: 0 !important;*/
    display: block;
    span{
      font-size: 12px;
      color: $cursor;
      cursor: pointer;
      display: inline-block;
    }
  }
  .dashboard-workplace-header-extra{
    width: auto!important;
    min-width: 400px;
  }
  .pfont{
    font-size: 12px;
    color: #808695;
  }
</style>
