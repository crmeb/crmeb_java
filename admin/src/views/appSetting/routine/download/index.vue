<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" style="font-size: 16px">
        <div class="page_title">小程序下载</div>
      </div>
      <div>
        <div class="flex">
          <div class="relative">
            <iframe class="iframe-box" :src="iframeUrl" frameborder="0" ref="iframe"></iframe>
            <div class="mask"></div>
          </div>
          <div class="ml-100 flex-1">
            <div class="header_title">小程序设置</div>
            <div class="content-box">
              <div class="left">小程序名称：</div>
              <div class="right">一码秦川</div>
            </div>
            <div class="content-box">
              <div class="left">小程序包：</div>
              <el-button type="primary" @click="downCode()">下载小程序</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
import { wechatCodeDownload } from '@/api/wxApi';
import { getMiniDownloadUrlApi } from '@/api/systemConfig';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  data() {
    return {
      iframeUrl: '',
    };
  },
  mounted() {
    this.getUrl();
  },
  methods: {
    downCode() {
      wechatCodeDownload().then((res) => {
        window.open(res);
      });
    },
    // 获取小程序下载地址
    getUrl() {
      getMiniDownloadUrlApi().then((res) => {
        this.iframeUrl = res.value;
      });
    },
  },
};
</script>
<style lang="scss">
.flex {
  display: flex;
}

.ml-100 {
  margin-left: 100px;
}

.flex-1 {
  flex: 1;
}

.page_title {
  font-size: 18px;
  font-family: PingFangSC-Semibold, PingFang SC;
  font-weight: 600;
  color: #000000;
  padding-left: 10px;
}

.header_title {
  font-size: 16px;
  font-family: PingFangSC-Semibold, PingFang SC;
  font-weight: 600;
  color: #000000;
  padding-left: 8px;
  position: relative;
  margin-left: 20px;

  &::before {
    position: absolute;
    content: '';
    width: 2px;
    height: 18px;
    background: #1890ff;
    top: 0;
    left: 0;
  }
}

.iframe-box {
  width: 312px;
  height: 600px;
  border-radius: 5px;
}

.relative {
  position: relative;
}

.mask {
  position: absolute;
  left: 0;
  top: 0;
  width: 312px;
  height: 600px;
  background-color: rgba(0, 0, 0, 0);
}

.content {
  padding: 0 20px;
}

.content>.title {
  padding-bottom: 26px;
}

.content-box {
  display: flex;
  align-items: center;
  margin: 20px 20px 0 20px;
  color: #333;
}

.content-box.last {
  margin-top: 0;
  color: #999999;
}

.content-box .title {
  font-size: 16px;
  font-weight: bold;
}

.content-box>span {
  color: #f5222d;
  font-size: 20px;
}

.content-box .left {
  font-size: 14px;
  text-align: left;
}

.content-box .right {
  width: 400px;
}

.rad {
  margin-left: 20px;
}
</style>
