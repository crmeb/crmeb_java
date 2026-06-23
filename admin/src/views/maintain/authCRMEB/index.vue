<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="auth acea-row row-between-wrapper">
        <div class="acea-row row-middle">
          <div v-if="status === 1" class="text">
            <div>商业授权</div>
            <div class="code">授权码：{{ authCode }}</div>
          </div>
          <div v-if="status === -1" class="text">请申请授权</div>
        </div>
        <div>
          <el-button v-if="status === 1" @click="toCrmeb()">进入官网</el-button>
          <el-button v-else-if="status === -1" type="primary" @click="applyAuth('java_mer')">申请授权</el-button>
          <el-button v-else-if="status === 2" type="primary" @click="applyAuth('java_mer')">重新申请</el-button>
          <el-button v-else-if="status === 0" class="grey">审核中</el-button>
        </div>
      </div>
    </el-card>
    <el-card v-if="!copyright && status == 1" class="box-card" style="margin-top: 15px">
      <div class="auth acea-row row-between-wrapper">
        <div class="acea-row row-middle">
          <i class="el-icon-s-help iconIos blue" />
          <div class="text">
            <div>去版权服务</div>
            <div class="code">购买之后可以设置</div>
          </div>
        </div>
        <div>
          <el-button type="primary" @click="applyAuth('copyright')">去版权</el-button>
        </div>
      </div>
    </el-card>
    <el-card v-if="copyright" style="margin-top: 15px">
      <el-form :model="copyForm" :rules="rules" ref="copyForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="修改版权信息" prop="name">
          <el-input v-model.trim="copyForm.companyName"></el-input>
        </el-form-item>
        <el-form-item label="上传版权图片" prop="region">
          <div class="authorized">
            <div class="uploadPictrue" v-if="copyForm.companyImage">
              <img v-lazy="copyForm.companyImage" />
              <i class="el-icon-error btndel" @click="handleRemove" />
            </div>
            <div class="upload" v-else @click="modalPicTap()">
              <div class="iconfont">+</div>
            </div>
          </div>
          <div class="prompt">建议尺寸：宽290px*高100px</div>
        </el-form-item>
        <el-form-item v-hasPermi="['admin:copyright:update:company:info']">
          <el-button type="primary" @click="saveCopyRight('copyForm')">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-dialog
      v-if="isTemplate"
      v-model="isTemplate"
      :visible.sync="isTemplate"
      :close-on-click-modal="false"
      :before-close="handleClose"
      width="440px"
      :title="title"
      close-on-click-modal
      class="mapBox"
      custom-class="dialog-scustom"
      style="padding: 0"
    >
      <iframe :src="iframeUrl" style="width: 400px; height: 600px" frameborder="0" />
    </el-dialog>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { copyrightInfoApi, saveCrmebCopyRight } from '@/api/authInformation';
import { Debounce } from '@/utils/validate';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'index',
  data() {
    return {
      baseUrl: 'https://shop.crmeb.net/html/index.html',
      iframeUrl: '',
      captchs: 'http://authorize.crmeb.net/api/captchs/',
      authCode: '',
      status: null,
      dayNum: 0,
      copyright: '',
      isTemplate: false,
      price: '',
      proPrice: '',
      productStatus: false,
      copyForm: {
        companyName: '',
        companyImage: '',
      },
      success: false,
      payType: '',
      disabled: false,
      isShow: false, // 验证码模态框是否出现
      active: 0,
      timer: null,
      version: '',
      label: '',
      productType: '',
      modalPic: false,
      isChoice: '单选',
      gridPic: {
        xl: 6,
        lg: 8,
        md: 12,
        sm: 12,
        xs: 12,
      },
      gridBtn: {
        xl: 4,
        lg: 8,
        md: 8,
        sm: 8,
        xs: 8,
      },
      title: '',
      rules: {
        companyName: [{ required: true, message: '请输入版权信息', trigger: 'blur' }],
        companyImage: [{ required: true, message: '请上传版权图片', trigger: 'change' }],
      },
      loading: false,
      domainUrl: '',
    };
  },
  mounted() {
    if (checkPermi(['admin:copyright:get:info'])) this.getAuth();
    window.addEventListener('message', (e) => {
      if (e.data.type == 'onComplete') {
        this.handleClose();
      }
    });
  },
  methods: {
    checkPermi,
    // 选择图片
    modalPicTap() {
      const _this = this;
      this.$modalUpload(function (img) {
        _this.copyForm.companyImage = img[0].sattDir;
      });
    },
    //删除授权图片
    handleRemove() {
      this.copyForm.companyImage = '';
    },
    toCrmeb() {
      window.open('http://www.crmeb.com');
    },
    handleClose() {
      this.getAuth();
      this.iframeUrl = '';
      this.isTemplate = false;
    },
    // 申请授权
    applyAuth(product) {
      this.productType = product;
      this.title = product === 'copyright' ? '去版权' : '商业授权';
      let host = location.host;
      let hostData = host.split('.');
      if (hostData[0] === 'test' && hostData.length === 4) {
        host = host.replace('test.', '');
      } else if (hostData[0] === 'www' && hostData.length === 3) {
        host = host.replace('www.', '');
      }
      this.iframeUrl =
        this.baseUrl +
        '?url=' +
        this.domainUrl +
        '&product=' +
        product +
        '&version=' +
        this.version +
        '&label=' +
        this.label;
      this.isTemplate = true;
    },
    getAuth() {
      copyrightInfoApi().then((res) => {
        const data = res || {};
        this.label = data.label;
        this.version = data.version;
        this.authCode = data.authCode || '';
        this.status = data.status;
        this.copyright = data.copyright;
        this.copyForm.companyName = data.companyName;
        this.copyForm.companyImage = data.companyImage;
        this.domainUrl =
          data.domainUrl && data.domainUrl.indexOf('https://') !== -1 ? data.domainUrl.slice(8) : data.domainUrl;
      });
    },
    //保存版权信息
    saveCopyRight: Debounce(function (form) {
      this.$refs[form].validate((valid) => {
        if (!valid) return;
        saveCrmebCopyRight(this.copyForm).then((res) => {
          this.$message.success('保存成功');
          this.getAuth();
        });
      });
    }),
  },
};
</script>

<style scoped lang="scss">
.btndel {
  position: absolute;
  z-index: 1;
  width: 20px !important;
  height: 20px !important;
  left: 43px;
  top: 1px;
  cursor: pointer;
}
.auth {
  padding: 9px 16px 9px 10px;
}
.auth .iconIos {
  font-size: 40px;
  margin-right: 10px;
  color: #001529;
}
.auth .text {
  font-weight: 400;
  color: rgba(0, 0, 0, 1);
  font-size: 18px;
}
.auth .price {
  color: red;
  font-size: 18px;
}
.auth .text .code {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.5);
  margin-top: 5px;
}
.auth .blue {
  color: #1890ff !important;
}
.auth .red {
  color: #ed4014 !important;
}
.grey {
  background-color: #999999;
  border-color: #999999;
  color: #fff;
}
.update {
  font-size: 13px;
  color: rgba(0, 0, 0, 0.85);
  padding-right: 12px;
}
.prompt {
  font-size: 12px;
  font-weight: 400;
  color: #999999;
}
.code .input .ivu-input {
  border-radius: 4px 0 0 4px !important;
}
.code .pictrue {
  height: 32px;
  width: 17%;
}
.submit {
  width: 100%;
}
.code .input {
  width: 83%;
}
.authorized {
  display: flex;
  margin-bottom: 14px;
  .upload {
    width: 60px;
    height: 60px;
    background: rgba(0, 0, 0, 0.02);
    border-radius: 4px;
    border: 1px solid #dddddd;
  }
}
.upload .iconfont {
  text-align: center;
  line-height: 60px;
}
.uploadPictrue {
  width: 60px;
  height: 60px;
  border: 1px dotted rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  position: relative;
}
.uploadPictrue img {
  width: 100%;
  height: 100%;
}
.customer {
  border-right: 0;
}
.customer a {
  font-size: 12px;
}
.ivu-input-group-prepend,
.ivu-input-group-append {
  background-color: #fff;
}
.ivu-input-group .ivu-input {
  border-right: 0 !important;
}
</style>
