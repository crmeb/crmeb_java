<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="acea-row roomBox">
        <div class="room-left">
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
          <div class="acea-row room-left-list">
            <div class="userHead mr10"><img src="../../../../assets/imgs/mobilehead.png"></div>
            <div class="userName">
              <span class="sp1" title="小红帽的外婆家">小红帽的外婆家</span>
              <span class="sp2">是的你好</span>
            </div>
          </div>
        </div>
        <div class="room-med"></div>
        <div class="room-right"></div>
      </div>
    </el-card>
  </div>
</template>

<script>
    export default {
        name: "index"
    }
</script>

<style scoped lang="scss">
.room{
  &-left::-webkit-scrollbar {
    display: none; /* Chrome Safari */
  }
  &-left{
    width: 230px;
    height: 600px;
    border: 1px solid #e6ebf5;
    padding: 0 0 15px 15px;
    box-sizing: border-box;
    scrollbar-width: none; /* firefox */
    -ms-overflow-style: none; /* IE 10+ */
    overflow-x: hidden;
    overflow-y: auto;
    &-list{
      border-bottom: 1px solid #e6ebf5;
      padding-bottom: 15px;
      padding: 15px 0;
      cursor: pointer;
    }
  }
  &-med{
    width: 400px;
    height: 600px;
    border-top: 1px solid #e6ebf5;
    border-bottom: 1px solid #e6ebf5;
  }
  &-right{
    width: 250px;
    height: 600px;
    border: 1px solid #e6ebf5;
  }
}
  .userHead{
    width: 55px;
    height: 55px;
    img{
      width: 100%;
      height: 100%;
      border-radius: 4px;
    }
  }
  .userName{
    .sp1{
      font-size: 15px;
      display: block;
      width: 118px;
      overflow: hidden;
      text-overflow:ellipsis;
      white-space: nowrap;
    }
    .sp2{
      font-size: 14px;
      display: block;
      color: #C0C4CC;
      margin-top: 21px;
      width: 118px;
      overflow: hidden;
      text-overflow:ellipsis;
      white-space: nowrap;
    }

  }
</style>
