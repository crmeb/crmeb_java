<template>
  <div class="divBox">
    <el-card v-if="isShowList" v-loading="fullscreenLoading" class="box-card">
      <div class="content acea-row row-middle">
        <div class="demo-basic--circle acea-row row-middle">
          <div class="circleUrl mr20"><img :src="circleUrl"></div>
          <!--<el-avatar :size="50" :src="circleUrl" class="mr20" />-->
          <div class="dashboard-workplace-header-tip">
            <div class="dashboard-workplace-header-tip-title">{{ smsAccount }}，祝您每一天开心！</div>
            <div class="dashboard-workplace-header-tip-desc">
              <!--<span class="mr10">修改密码</span>-->
              <span @click="signOut">退出登录</span>
            </div>
          </div>
        </div>
        <div class="dashboard-workplace-header-extra">
          <el-row type="flex" justify="center" align="middle" :gutter="12">
            <el-col :span="8">
                <span class="pfont acea-row row-middle">
                  <el-avatar icon="el-icon-user-solid" size="small" class="mr10" />
                  <span>剩余条数</span>
                </span>
              <span class="rR" v-text="numbers" />
            </el-col>
            <el-col :span="8">
                <span class="pfont acea-row row-middle">
                  <el-avatar icon="el-icon-user-solid" size="small" class="mr10" />
                  <span>已发送</span>
                </span>
              <span class="rR" v-text="sendTotal" />
            </el-col>
            <el-col :span="8">
                <span class="pfont acea-row row-middle">
                  <el-avatar icon="el-icon-user-solid" size="small" class="mr10" />
                  <span>总条数</span>
                </span>
              <span class="rR" v-text="amount" />
            </el-col>
          </el-row>
        </div>
      </div>
    </el-card>
    <table-list v-if="isShowList" />
    <login-from v-if="isShowLogn" @on-changes="onChangeReg" @on-Login="onLogin" />
    <register-from v-if="isShowReg" @on-change="logoup" />
  </div>
</template>

<script>
import tableList from './components/tableList'
import loginFrom from './components/loginFrom'
import registerFrom from './components/register'
import { logoutApi, smsNumberApi, smsInfoApi } from '@/api/sms'
import { mapGetters } from 'vuex'
export default {
  name: 'SmsConfig',
  components: { tableList, loginFrom, registerFrom },
  data() {
    return {
      fullscreenLoading: false,
      smsAccount: '',
      circleUrl: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      spinShow: false,
      isShowLogn: false,
      isShow: false,
      isShowReg: false,
      isShowList: false,
      amount: 0,
      numbers: 0,
      sendTotal: 0
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
    // 剩余条数
    getNumber() {
      smsInfoApi().then(async res => {
        const data = res
        this.numbers = data.num
        this.sendTotal = data.surp
        this.amount = data.num + data.surp
        this.smsAccount = data.account
      })
    },
    // 登录跳转
    onLogin() {
      const url = this.$route.query.url
      if (url) {
        this.$router.replace(url)
      } else {
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
        }
        this.getNumber()
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
      }).catch(res => {
        this.$message.error(res.message)
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
