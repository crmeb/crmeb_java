<template>
  <div class="divBox">
    <el-card class="box-card">
      <template #header>
        <div class="clearfix">
          <el-steps :active="currentTab" align-center>
            <el-step title="安装系统" />
            <el-step title="应用配置" />
            <el-step title="支付配置" />
            <el-step title="站点配置" />
            <!-- <el-step title="一号通" /> -->
          </el-steps>
        </div>
      </template>
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
            :name="toTabName(tabItem.extra)"
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
            :name="toTabName(tabItem.extra)"
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
                  :disabled="!canClick"
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
              <router-link to="/operation/onePassConfig">已有帐号 去登录</router-link>
            </div>
          </el-form>
        </div>
      </div> -->
      <div :class="currentTab != 0 ? 'step_btn_box' : ''">
        <el-button class="step_btn" v-show="currentTab > 0" @click="beforeStep()">上一步</el-button>
        <el-button
          type="primary"
          :class="currentTab == 0 ? 'step_btn' : 'step_btn_next'"
          @click="nextStep()"
          v-show="currentTab < 3"
          :disabled="disabled"
          >下一步</el-button
        >
        <el-button class="step_btn_next" v-show="currentTab === 3" type="primary" @click="complate()">完成</el-button>
      </div>
    </el-card>
  </div>
</template>
<script setup>
import { reactive, ref, onMounted, getCurrentInstance } from 'vue'
import { ElMessage } from '@/utils/elementPlusFeedback'
import { useRouter } from 'vue-router'
import parser from '@/components/FormGenerator/components/parser/Parser'
import * as categoryApi from '@/api/categoryApi.js'
import * as systemFormConfigApi from '@/api/systemFormConfig.js'
import * as systemSettingApi from '@/api/systemSetting.js'
import { captchaApi, registerApi } from '@/api/sms'
import { Debounce } from '@/utils/validate'
// 将 require('@/assets/imgs/*.png') 转为静态 import
import wechatH5Img from '@/assets/imgs/wechat_h5.png'
import routineImg from '@/assets/imgs/routine.png'
import appImg from '@/assets/imgs/app.png'

defineOptions({ name: 'guide' })

const router = useRouter()
const { proxy } = getCurrentInstance()

const validatePhone = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('请填写手机号'))
  } else if (!/^1[3456789]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确!'))
  } else {
    callback()
  }
}

const currentTab = ref(0)
const cTab = ref(0)
const installList = [
  { img: wechatH5Img, name: '公众号', id: 159 },
  { img: routineImg, name: '小程序', id: 160 },
  { img: appImg, name: 'APP', id: 161 }
]
const checkArr = ref([])
const payConfigArr = ref([])
const siteConfigArr = ref([])
const applicationName = ref('')
const siteConfigName = ref('')
const payConfigName = ref('')
const checked = ref(false)
const formConf = reactive({ content: { fields: [] }, id: null, render: false, isEdit: false })
const currentEditId = ref(null)
const currentEditData = ref({})
const loading = ref(false)
const disabled = ref(true)
const passwordType = ref('password')
const cutNUm = ref('获取验证码')
const canClick = ref(true)
const formInline = reactive({
  account: '',
  code: '',
  domain: '',
  phone: '',
  password: ''
})
const ruleInline = reactive({
  password: [{ required: true, message: '请输入短信平台密码/token', trigger: 'blur' }],
  domain: [{ required: true, message: '请输入网址域名', trigger: 'blur' }],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})

const formInlineRef = ref(null)

function install(i) {
  if (checkArr.value.includes(i)) {
    //includes()方法判断是否包含某一元素,返回true或false表示是否包含元素，对NaN一样有效
    //filter()方法用于把Array的某些元素过滤掉，filter()把传入的函数依次作用于每个元素，然后根据返回值是true还是false决定保留还是丢弃该元素：生成新的数组
    checkArr.value = checkArr.value.filter(function (ele) {
      return ele != i
    })
  } else {
    checkArr.value.push(i)
    applicationName.value = checkArr.value[0].id.toString()
    if (checkArr.value.length) disabled.value = false
  }
}
function nextStep() {
  currentTab.value++
  switch (currentTab.value) {
    case 1:
      if (checkArr.value.length) {
        handlerGetFormConfig(Number(applicationName.value))
      }
      break
    case 2:
      payConfigName.value = toTabName(payConfigArr.value[0]?.extra)
      if (payConfigName.value) {
        handlerGetFormConfig(Number(payConfigName.value))
      }
      break
    case 3:
      siteConfigName.value = toTabName(siteConfigArr.value[0]?.extra)
      if (siteConfigName.value) {
        handlerGetFormConfig(Number(siteConfigName.value))
      }
    default:
      break
  }
  let data = {
    currentTab: currentTab.value,
    checkArr: checkArr.value,
    payConfigArr: payConfigArr.value,
    siteConfigArr: siteConfigArr.value
  }
  proxy.$cache.local.setJSON('step', data)
}
function getStep() {
  if (!proxy.$cache.local.has('step')) return
  let data = proxy.$cache.local.getJSON('step')
  currentTab.value = data.currentTab ? data.currentTab : 0
  checkArr.value = data.checkArr
  payConfigArr.value = data.payConfigArr
  siteConfigArr.value = data.siteConfigArr
  if (checkArr.value.length) disabled.value = false
  if (currentTab.value == 1) {
    applicationName.value = checkArr.value[0].id.toString()
    handlerGetFormConfig(Number(checkArr.value[0].id))
  } else if (currentTab.value == 2) {
    payConfigName.value = toTabName(payConfigArr.value[0]?.extra)
    if (payConfigName.value) handlerGetFormConfig(Number(payConfigName.value))
  } else if (currentTab.value == 3) {
    siteConfigName.value = toTabName(siteConfigArr.value[0]?.extra)
    if (siteConfigName.value) handlerGetFormConfig(Number(siteConfigName.value))
  }
}
function beforeStep() {
  currentTab.value--
  formConf.content = { fields: [] }
  formConf.render = false
  if (currentTab.value == 1) {
    applicationName.value = checkArr.value[0].id.toString()
    handlerGetFormConfig(Number(checkArr.value[0].id))
  } else if (currentTab.value == 2) {
    payConfigName.value = toTabName(payConfigArr.value[0]?.extra)
    if (payConfigName.value) handlerGetFormConfig(Number(payConfigName.value))
  } else if (currentTab.value == 3) {
    siteConfigName.value = toTabName(siteConfigArr.value[0]?.extra)
    if (siteConfigName.value) handlerGetFormConfig(Number(siteConfigName.value))
  }
}
function getTabName(tab) {
  return tab?.props?.name ?? tab?.paneName ?? tab?.name ?? tab
}
function toTabName(value) {
  return value != null ? value.toString() : ''
}
function findCategoryByName(list, name) {
  return (list || []).find((item) => item?.name === name)
}
function handleClick(tab) {
  let _id = getTabName(tab)
  if (!_id) return ElMessage.error('表单配置不正确，请关联正确表单后使用')
  handlerGetFormConfig(_id)
}
function handlerGetFormConfig(id) {
  const formPram = { id: id }
  currentEditId.value = id
  formConf.content = { fields: [] }
  formConf.render = false
  loading.value = true
  systemFormConfigApi
    .getFormConfigInfo(formPram)
    .then((data) => {
      const { id, name, info, content } = data
      formConf.content = JSON.parse(content)
      formConf.id = id
      handlerGetSettingInfo(id, 1)
      loading.value = false
    })
    .catch(() => {
      loading.value = false
    })
}
function handlerGetSettingInfo(id, level) {
  systemSettingApi.systemConfigInfo({ id: id }).then((data) => {
    currentEditData.value = data
    if (level === 1) {
      formConf.isEdit = currentEditData.value !== null
      formConf.render = true
    }
  })
}
const handlerSubmit = Debounce(function (formValue) {
  handlerSave(formValue)
})
function handlerSave(formValue) {
  const _pram = buildFormPram(formValue)
  let _formId = 0
  systemSettingApi.systemConfigSave(_pram).then((data) => {
    ElMessage.success('添加数据成功')
  })
}
function buildFormPram(formValue) {
  const _pram = {
    fields: [],
    id: currentEditId.value,
    sort: 0, // 参数暂时无用
    status: true // 参数暂时无用
  }
  const _fields = []
  Object.keys(formValue).forEach((key) => {
    _fields.push({
      name: key,
      title: key,
      value: formValue[key]
    })
  })
  _pram.fields = _fields
  return _pram
}
function handlerGetTreeList() {
  const _pram = { type: 6, status: 1 }
  loading.value = true
  categoryApi
    .treeCategroy(_pram)
    .then((data) => {
      payConfigArr.value = findCategoryByName(data, '支付配置')?.child || []
      siteConfigArr.value = findCategoryByName(data, '系统配置')?.child || []
      loading.value = false
    })
    .catch(() => {
      loading.value = false
    })
}
const formSubmit = Debounce(function (name) {
  formInline.account = formInline.phone
  formInlineRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      registerApi(formInline)
        .then(async (res) => {
          ElMessage.success('注册成功')
          loading.value = false
        })
        .catch(() => {
          loading.value = false
        })
    } else {
      return false
    }
  })
})
function complate() {
  proxy.$cache.local.remove('step')
  router.push('/')
}
function cutDown() {
  if (formInline.phone) {
    if (!canClick.value) return
    canClick.value = false
    cutNUm.value = 60
    captchaApi({ phone: formInline.phone, types: 0 }).then(async (res) => {
      ElMessage.success('发送成功')
    })
    const time = setInterval(() => {
      cutNUm.value--
      if (cutNUm.value === 0) {
        cutNUm.value = '获取验证码'
        canClick.value = true
        clearInterval(time)
      }
    }, 1000)
  } else {
    ElMessage.warning('请填写手机号!')
  }
}

onMounted(() => {
  proxy.$cache.local.remove('step')
  handlerGetTreeList()
  getStep()
})
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
.step_btn_next {
  margin: 54px 0 0 25px;
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

:deep(.el-step__line) {
  margin-left: 95px;
  margin-right: 30px !important;
}
:deep(.el-step__title) {
  position: absolute;
  top: -6px;
  right: 50px;
}
:deep(.is-process .el-step__icon) {
  border: none;
  background: var(--prev-color-primary);
  color: #fff;
}
// :deep(.el-step__icon-inner) {
//   margin-right: 2px;
//   margin-top: 1px;
// }
:deep(.el-steps) {
  margin-left: 3px;
}
:deep(.el-steps) {
  width: 1100px !important;
}
.step_btn_box {
  margin-left: 35%;
}
:deep(.dialog-footer-inner) {
  float: left !important;
}
:deep(.closeBtn) {
  display: none;
}
</style>
