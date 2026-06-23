<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-steps :active="currentTab" align-center>
          <el-step title="安装系统" />
          <el-step title="应用配置" />
          <el-step title="支付配置" />
          <el-step title="站点配置" />
          <!-- <el-step title="一号通" /> -->
        </el-steps>
      </div>
      <div class="install" v-show="currentTab === 0">
        <div class="ins_name flex align-center">
          <span>选择应用配置:</span>
          <el-alert
            type="warning"
            show-icon=""
            title="使用前提，绑定微信平台"
            style="width: 300px; margin-left: 20px"
            v-if="checkArr.includes(installList[0]) || checkArr.includes(installList[1])"
          ></el-alert>
        </div>
        <div class="flex">
          <div
            v-for="item in installList"
            :key="item.id"
            :class="{ active: checkArr.includes(item) }"
            @click="install(item)"
          >
            <div class="ins_item">
              <img :src="item.img" alt="" />
              <i class="el-icon-success icon_abs" v-show="checkArr.includes(item)"></i>
            </div>
            <p class="ins_item_name">{{ item.name }}</p>
          </div>
        </div>
      </div>
      <div class="application" v-show="currentTab === 1">
        <el-tabs v-model="applicationName" @tab-click="handleClick" v-loading="loading">
          <el-tab-pane
            v-for="(tabItem, itemIndex) in checkArr"
            :key="itemIndex"
            :label="tabItem.name"
            :name="tabItem.id.toString()"
          >
            <parser
              v-if="formConf.render"
              :is-edit="formConf.isEdit"
              :form-conf="formConf.content"
              :form-edit-data="currentEditData"
              @submit="handlerSubmit"
            />
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="application" v-show="currentTab === 2">
        <el-tabs v-model="payConfigName" @tab-click="handleClick" v-loading="loading">
          <el-tab-pane
            v-for="(tabItem, itemIndex) in payConfigArr"
            :key="itemIndex"
            :label="tabItem.name"
            :name="tabItem.extra"
          >
            <parser
              v-if="formConf.render"
              :is-edit="formConf.isEdit"
              :form-conf="formConf.content"
              :form-edit-data="currentEditData"
              @submit="handlerSubmit"
            />
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="application" v-show="currentTab === 3">
        <el-tabs v-model="siteConfigName" @tab-click="handleClick" v-loading="loading">
          <el-tab-pane
            v-for="(tabItem, itemIndex) in siteConfigArr"
            :key="itemIndex"
            :label="tabItem.name"
            :name="tabItem.extra"
          >
            <parser
              v-if="formConf.render"
              :is-edit="formConf.isEdit"
              :form-conf="formConf.content"
              :form-edit-data="currentEditData"
              @submit="handlerSubmit"
            />
          </el-tab-pane>
        </el-tabs>
      </div>
      <!-- <div class="application flex-center" v-show="currentTab === 4">
        <div class="sms_reg">
          <el-form ref="formInline" :model="formInline" :rules="ruleInline" label-position="right" label-width="100px">
            <div>
              <p class="title mb20">一号通账户注册</p>
            </div>
            <el-form-item prop="phone" label="手机号码">
              <el-input v-model="formInline.phone" placeholder="请输入您的手机号" style="width: 340px" />
            </el-form-item>
            <el-form-item prop="password" label="登录密码">
              <el-input
                :key="passwordType"
                v-model="formInline.password"
                :type="passwordType"
                placeholder="密码"
                tabindex="2"
                auto-complete="off"
                style="width: 340px"
              />
            </el-form-item>
            <el-form-item prop="domain" label="网址域名">
              <el-input v-model="formInline.domain" placeholder="请输入网址域名" style="width: 340px" />
            </el-form-item>
            <el-form-item prop="code" class="captcha" label="验证码">
              <div class="acea-row" style="flex-wrap: nowrap">
                <el-input
                  v-model="formInline.code"
                  placeholder="验证码"
                  type="text"
                  tabindex="1"
                  style="width: 240px; margin-right: 10px"
                />
                <el-button
                  :disabled="!this.canClick"
                  @click="cutDown"
                  type="primary"
                  plain
                  v-hasPermi="['admin:pass:send:code']"
                  >{{ cutNUm }}</el-button
                >
              </div>
            </el-form-item>
            <div class="flex-center mb20">
              <el-button
                :loading="loading"
                type="primary"
                @click="formSubmit('formInline')"
                v-hasPermi="['admin:pass:register']"
                >注册</el-button
              >
            </div>
            <div class="flex-center go_login">
              <router-link to="/operation/onePass">已有帐号 去登录</router-link>
            </div>
          </el-form>
        </div>
      </div> -->
      <div :class="currentTab != 0 ? 'step_btn_box' : ''">
        <el-button class="step_btn" v-show="currentTab > 0" @click="beforeStep()">上一步</el-button>
        <el-button
          type="primary"
          :class="currentTab == 0 ? 'step_btn' : ''"
          @click="nextStep()"
          v-show="currentTab < 3"
          :disabled="disabled"
          >下一步</el-button
        >
        <el-button v-show="currentTab === 3" type="primary" @click="complate()">完成</el-button>
      </div>
    </el-card>
  </div>
</template>
<script>
import parser from '@/components/FormGenerator/components/parser/Parser';
import * as categoryApi from '@/api/categoryApi.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import * as systemSettingApi from '@/api/systemSetting.js';
import { captchaApi, registerApi } from '@/api/sms';
import { Debounce } from '@/utils/validate';
export default {
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请填写手机号'));
      } else if (!/^1[3456789]\d{9}$/.test(value)) {
        callback(new Error('手机号格式不正确!'));
      } else {
        callback();
      }
    };
    return {
      currentTab: 0,
      cTab: 0,
      installList: [
        { img: require('@/assets/imgs/wechat_h5.png'), name: '公众号', id: 65 },
        { img: require('@/assets/imgs/routine.png'), name: '小程序', id: 66 },
        { img: require('@/assets/imgs/app.png'), name: 'APP', id: 134 },
      ],
      checkArr: [],
      payConfigArr: [],
      siteConfigArr: [],
      applicationName: '',
      siteConfigName: '',
      payConfigName: '',
      checked: false,
      formConf: { content: { fields: [] }, id: null, render: false, isEdit: false },
      currentEditId: null,
      currentEditData: {},
      loading: false,
      disabled: true,
      passwordType: 'password',
      cutNUm: '获取验证码',
      canClick: true,
      formInline: {
        account: '',
        code: '',
        domain: '',
        phone: '',
        password: '',
      },
      ruleInline: {
        password: [{ required: true, message: '请输入短信平台密码/token', trigger: 'blur' }],
        domain: [{ required: true, message: '请输入网址域名', trigger: 'blur' }],
        phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
      },
    };
  },
  components: { parser },
  mounted() {
    this.$cache.local.remove('step');
    this.handlerGetTreeList();
    this.getStep();
  },
  methods: {
    install(i) {
      if (this.checkArr.includes(i)) {
        //includes()方法判断是否包含某一元素,返回true或false表示是否包含元素，对NaN一样有效
        //filter()方法用于把Array的某些元素过滤掉，filter()把传入的函数依次作用于每个元素，然后根据返回值是true还是false决定保留还是丢弃该元素：生成新的数组
        this.checkArr = this.checkArr.filter(function (ele) {
          return ele != i;
        });
      } else {
        this.checkArr.push(i);
        this.applicationName = this.checkArr[0].id.toString();
        if (this.checkArr.length) this.disabled = false;
      }
    },
    nextStep() {
      this.currentTab++;
      switch (this.currentTab) {
        case 1:
          if (this.checkArr.length) {
            this.handlerGetFormConfig(Number(this.applicationName));
          }
          break;
        case 2:
          this.payConfigName = this.payConfigArr[0].extra;
          if (this.payConfigArr.length) {
            this.handlerGetFormConfig(Number(this.payConfigName));
          }
          break;
        case 3:
          this.siteConfigName = this.siteConfigArr[0].extra;
          if (this.siteConfigArr.length) {
            this.handlerGetFormConfig(Number(this.siteConfigName));
          }
        default:
          break;
      }
      let data = {
        currentTab: this.currentTab,
        checkArr: this.checkArr,
        payConfigArr: this.payConfigArr,
        siteConfigArr: this.siteConfigArr,
      };
      this.$cache.local.setJSON('step', data);
    },
    getStep() {
      if (!this.$cache.local.has('step')) return;
      let data = this.$cache.local.getJSON('step');
      this.currentTab = data.currentTab ? data.currentTab : 0;
      this.checkArr = data.checkArr;
      this.payConfigArr = data.payConfigArr;
      this.siteConfigArr = data.siteConfigArr;
      if (this.checkArr.length) this.disabled = false;
      if (this.currentTab == 1) {
        this.applicationName = this.checkArr[0].id.toString();
        this.handlerGetFormConfig(Number(this.checkArr[0].id));
      } else if (this.currentTab == 2) {
        this.payConfigName = this.payConfigArr[0].extra;
        this.handlerGetFormConfig(Number(this.payConfigArr[0].extra));
      } else if (this.currentTab == 3) {
        this.siteConfigName = this.siteConfigArr[0].extra;
        this.handlerGetFormConfig(Number(this.siteConfigArr[0].extra));
      }
    },
    beforeStep() {
      this.currentTab--;
      this.formConf.content = { fields: [] };
      this.formConf.render = false;
      if (this.currentTab == 1) {
        this.applicationName = this.checkArr[0].id.toString();
        this.handlerGetFormConfig(Number(this.checkArr[0].id));
      } else if (this.currentTab == 2) {
        this.payConfigName = this.payConfigArr[0].extra;
        this.handlerGetFormConfig(Number(this.payConfigArr[0].extra));
      } else if (this.currentTab == 3) {
        this.siteConfigName = this.siteConfigArr[0].extra;
        this.handlerGetFormConfig(Number(this.siteConfigArr[0].extra));
      }
    },
    handleClick(tab) {
      let _id = tab.name ? tab.name : '';
      if (!_id) return this.$message.error('表单配置不正确，请关联正确表单后使用');
      this.handlerGetFormConfig(_id);
    },
    handlerGetFormConfig(id) {
      const formPram = { id: id };
      this.currentEditId = id;
      this.formConf.content = { fields: [] };
      this.formConf.render = false;
      this.loading = true;
      systemFormConfigApi
        .getFormConfigInfo(formPram)
        .then((data) => {
          const { id, name, info, content } = data;
          this.formConf.content = JSON.parse(content);
          this.formConf.id = id;
          this.handlerGetSettingInfo(id, 1);
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    handlerGetSettingInfo(id, level) {
      systemSettingApi.systemConfigInfo({ id: id }).then((data) => {
        this.currentEditData = data;
        if (level === 1) {
          this.formConf.isEdit = this.currentEditData !== null;
          this.formConf.render = true;
        }
      });
    },
    handlerSubmit: Debounce(function (formValue) {
      this.handlerSave(formValue);
    }),
    handlerSave(formValue) {
      const _pram = this.buildFormPram(formValue);
      let _formId = 0;
      systemSettingApi.systemConfigSave(_pram).then((data) => {
        this.$message.success('添加数据成功');
      });
    },
    buildFormPram(formValue) {
      const _pram = {
        fields: [],
        id: this.currentEditId,
        sort: 0, // 参数暂时无用
        status: true, // 参数暂时无用
      };
      const _fields = [];
      Object.keys(formValue).forEach((key) => {
        _fields.push({
          name: key,
          title: key,
          value: formValue[key],
        });
      });
      _pram.fields = _fields;
      return _pram;
    },
    handlerGetTreeList() {
      const _pram = { type: 6, status: 1 };
      this.loading = true;
      categoryApi
        .treeCategroy(_pram)
        .then((data) => {
          this.payConfigArr = data[3].child;
          this.siteConfigArr[0] = data[0].child[0];
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    formSubmit: Debounce(function (name) {
      this.formInline.account = this.formInline.phone;
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.loading = true;
          registerApi(this.formInline)
            .then(async (res) => {
              this.$message.success('注册成功');
              this.loading = false;
            })
            .catch(() => {
              this.loading = false;
            });
        } else {
          return false;
        }
      });
    }),
    complate() {
      this.$cache.local.remove('step');
      this.$router.push('/');
    },
    cutDown() {
      if (this.formInline.phone) {
        if (!this.canClick) return;
        this.canClick = false;
        this.cutNUm = 60;
        captchaApi({ phone: this.formInline.phone, types: 0 }).then(async (res) => {
          this.$message.success('发送成功');
        });
        const time = setInterval(() => {
          this.cutNUm--;
          if (this.cutNUm === 0) {
            this.cutNUm = '获取验证码';
            this.canClick = true;
            clearInterval(time);
          }
        }, 1000);
      } else {
        this.$message.warning('请填写手机号!');
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.flex {
  display: flex;
}

.align-center {
  align-items: center;
}

.install {
  padding: 20px 0 0 105px;

  .ins_name {
    font-size: 14px;
    color: rgba(0, 0, 0, 0.85);
    font-weight: 400;
    padding-bottom: 20px;
  }

  .ins_item {
    width: 130px;
    height: 130px;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #dddddd;
    margin-right: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;

    img {
      width: 60px;
      height: 60px;
    }

    .icon_abs {
      position: absolute;
      top: 6px;
      right: 6px;
      color: var(--prev-color-primary);
      font-size: 23px;
    }
  }

  .ins_item_name {
    width: 130px;
    color: #333333;
    font-size: 16px;
    font-weight: 600;
    text-align: center;
    margin-top: 15px;
  }

  .active {
    .ins_item {
      border: 1px solid var(--prev-color-primary);
    }

    .ins_item_name {
      color: var(--prev-color-primary);
    }
  }

  .next_step {
    margin: 54px 0 100px;
  }
}

.step_btn {
  margin: 54px 0 0 105px;
}
.step_btn {
  margin-left: 105px !important;
}
.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.sms_reg {
  width: 550px;
  height: 380px;
}

.title {
  font-size: 16px;
  text-align: center;
  font-weight: 600;
  color: #333333;
}

.go_login {
  font-size: 12px;
  font-weight: 400;
  color: #1890ff;
}
.active {
  border: none !important;
}
::v-deep .el-step__line {
  margin-left: 95px;
  margin-right: 30px !important;
}
::v-deep .el-step__title {
  position: absolute;
  top: -6px;
  right: 50px;
}
::v-deep .is-process .el-step__icon {
  border: none;
  background: var(--prev-color-primary);
  color: #fff;
}
// ::v-deep .el-step__icon-inner {
//   margin-right: 2px;
//   margin-top: 1px;
// }
::v-deep .el-steps {
  margin-left: 3px;
}
::v-deep .el-steps {
  width: 1100px !important;
}
.step_btn_box {
  margin-left: 35%;
}
::v-deep .dialog-footer-inner {
  float: left !important;
}
::v-deep .closeBtn {
  display: none;
}
</style>
