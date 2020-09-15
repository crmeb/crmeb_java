<template>
  <div class="lottie-bg">
    <div>222进来了22222</div>
    <div id="lottie">
      <img
        src="@/assets/imgs/live-logo.gif"
        rel="preload"
        style="width: 100%;"
      />
    </div>
  </div>
</template>
<script>
  import { auth, oAuth } from "@/libs/wechat";
  import Cookies from 'js-cookie'
  const WX_AUTH = "wx_auth";
  export default {
    name: 'AuthSend',
    created() {
      import('@/assets/js/media_750')
      // const hash = window.location.search.slice(1)
      // if (window.localStorage) {
      //   window.localStorage.setItem('x-admin-oauth-code', hash)
      //   window.close()
      // }
      Cookies.set(WX_AUTH, this.$route.query.code);
    },
    render: function(h) {
      return h() // avoid warning message
    },
    mounted() {
     if(this.$route.query.code) location.replace("/login");
    // oAuth('/auth-send')
    //  if( Cookies.get(WX_AUTH)!==undefined) location.replace("/login");
    }
  }
</script>
<style scoped>
  .lottie-bg {
    position: fixed;
    left: 0;
    top: 0;
    background-color: #fff;
    width: 100%;
    height: 100%;
    z-index: 999;
    display: -webkit-flex;
    display: flex;
    -webkit-align-items: center;
    align-items: center;
    -webkit-justify-content: center;
    justify-content: center;
  }

  #lottie {
    width: 35%;
    display: block;
    overflow: hidden;
    transform: translate3d(0, 0, 0);
    margin: auto;
  }
</style>
