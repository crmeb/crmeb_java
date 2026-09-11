<template>
  <div class="divBox">
    <el-card class="box-card">
      <router-link
        v-show="$route.path.indexOf('keyword') !== -1"
        :to="{ path: '/appSetting/publicAccount/wxReply/keyword' }"
      >
        <el-button  class="mr20 mb20" :icon="Back">返回</el-button>
      </router-link>
      <el-row :gutter="30" v-loading="loading">
        <el-col v-bind="grid" class="acea-row">
          <div class="left mb15 ml40">
            <img class="top" src="@/assets/imgs/mobilehead.png" />
            <img class="bottom" src="@/assets/imgs/mobilefoot.png" />
            <div class="centent">
              <div class="time-wrapper"><span class="time">9:36</span></div>
              <div v-if="formValidate.type !== 'news'" class="view-item text-box clearfix">
                <div class="avatar fl"><img src="@/assets/imgs/head.gif" /></div>
                <div class="box-content fl">
                  <span v-if="formValidate.type === 'text'" v-text="formValidate.contents.content" />
                  <div v-if="formValidate.contents.mediaId" class="box-content_pic">
                    <img v-if="formValidate.type === 'image'" :src="formValidate.contents.srcUrl" />
                    <i class="el-icon-service" v-else></i>
                  </div>
                </div>
              </div>
              <div v-if="formValidate.type === 'news'">
                <div class="newsBox">
                  <!--<div class="news_pic mb15" style="backgroundImage: url('@/assets/imgs/mobilefoot.png');backgroundSize:'100% 100%'}" />-->
                  <div
                    class="news_pic mb15"
                    :style="{
                      backgroundImage:
                        'url(' +
                        (formValidate.contents.articleData.imageInput
                          ? formValidate.contents.articleData.imageInput
                          : '') +
                        ')',
                      backgroundSize: '100% 100%',
                    }"
                  />
                  <span class="news_sp">{{ formValidate.contents.articleData.title }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xl="11" :lg="12" :md="14" :sm="22" :xs="22">
          <div class="box-card right ml50">
            <el-form
              ref="formValidateRef"
              :model="formValidate"
              :rules="ruleValidate"
              label-width="100px"
              class="mt20"
              @submit.prevent
            >
              <el-form-item v-if="$route.path.indexOf('keyword') !== -1" label="关键字：" prop="val">
                <div class="arrbox">
                  <el-tag
                    v-for="(item, index) in labelarr"
                    :key="index"
                    type="success"
                    closable
                    class="mr5"
                    :disable-transitions="false"
                    @close="handleClose(item)"
                    >{{ item }}
                  </el-tag>
                  <el-input
                    v-model="val"

                    class="arrbox_ip"
                    placeholder="输入后回车"
                    style="width: 90%"
                    @change="addlabel"
                  />
                </div>
              </el-form-item>
              <el-form-item label="规则状态：">
                <el-radio-group v-model="formValidate.status">
                  <el-radio :label="true" :value="true">启用</el-radio>
                  <el-radio :label="false" :value="false">禁用</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="消息类型：" prop="type">
                <el-select
                  v-model="formValidate.type"
                  placeholder="请选择规则状态"
                  style="width: 90%"
                  @change="RuleFactor(formValidate.type)"
                >
                  <el-option label="文字消息" value="text">文字消息</el-option>
                  <el-option label="图片消息" value="image">图片消息</el-option>
                  <el-option label="图文消息" value="news">图文消息</el-option>
                  <el-option label="声音消息" value="voice">声音消息</el-option>
                </el-select>
              </el-form-item>
              <el-form-item v-if="formValidate.type === 'text'" label="规则内容：" prop="content">
                <el-input
                  v-model="formValidate.contents.content"
                  placeholder="请填写规则内容"
                  style="width: 90%"
                  @input="change($event)"
                />
              </el-form-item>
              <el-form-item v-if="formValidate.type === 'news'" label="选取图文：">
                <el-button  type="primary" @click="changePic">选择图文消息</el-button>
              </el-form-item>
              <el-form-item
                v-if="formValidate.type === 'image' || formValidate.type === 'voice'"
                :label="formValidate.type === 'image' ? '图片地址：' : '语音地址：'"
                prop="mediaId"
              >
                <div class="acea-row row-middle">
                  <el-input
                    v-model="formValidate.contents.mediaId"
                    readonly="readonly"
                    placeholder="default size"
                    style="width: 75%"
                    class="mr10"
                  />
                  <el-upload
                    class="upload-demo mr10"
                    action
                    :http-request="handleUploadForm"
                    :headers="myHeaders"
                    :show-file-list="false"
                    multiple
                  >
                    <el-button  type="primary">点击上传</el-button>
                  </el-upload>
                </div>
                <span v-show="formValidate.type === 'image'">文件最大5Mb，支持bmp/png/jpeg/jpg/gif格式</span>
                <span v-show="formValidate.type === 'voice'"
                  >文件最大5Mb，支持mp3/wma/wav/amr格式,播放长度不超过60s</span
                >
              </el-form-item>
            </el-form>
          </div>
          <el-col :span="24">
            <div class="acea-row row-center">
              <el-button
                type="primary"
                class="ml50"
                @click="submenus('formValidateRef')"
                v-hasPermi="['admin:wechat:keywords:reply:update']"
                >保存并发布
              </el-button>
            </div>
          </el-col>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, getCurrentInstance } from 'vue';
import { Back } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElLoading } from '@/utils/elementPlusFeedback';
import { getToken } from '@/utils/auth';
import { replySaveApi, replyInfoApi, keywordsInfoApi, replyUpdateApi } from '@/api/wxApi';
import { wechatUploadApi } from '@/api/systemSetting';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'Index' });

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const validateContent = (rule, value, callback) => {
  if (formValidate.type === 'text') {
    if (formValidate.contents.content === '') {
      callback(new Error('请填写规则内容'));
    } else {
      callback();
    }
  }
};
const validateSrc = (rule, value, callback) => {
  if (formValidate.type === 'image' && formValidate.contents.mediaId === '') {
    callback(new Error('请上传'));
  } else {
    callback();
  }
};
const validateVal = (rule, value, callback) => {
  if (labelarr.value.length === 0) {
    callback(new Error('请输入后回车'));
  } else {
    callback();
  }
};

const loading = ref(false);
const visible = ref(false);
const grid = reactive({
  xl: 7,
  lg: 12,
  md: 10,
  sm: 24,
  xs: 24,
});
const delfromData = ref({});
const isShow = ref(false);
const maxCols = ref(3);
const scrollerHeight = ref('600');
const contentTop = ref('130');
const contentWidth = ref('98%');
const modals = ref(false);
const val = ref('');
const formatImg = ref(['jpg', 'jpeg', 'png', 'bmp', 'gif']);
const formatVoice = ref(['mp3', 'wma', 'wav', 'amr']);
const header = ref({});
const formValidate = reactive({
  status: true,
  type: '',
  keywords: '',
  contents: {
    content: '',
    articleData: {},
    mediaId: '',
    srcUrl: '',
    articleId: null,
  },
  id: null,
});
const ruleValidate = {
  val: [{ required: true, validator: validateVal, trigger: 'blur' }],
  type: [{ required: true, message: '请选择消息类型', trigger: 'change' }],
  content: [{ required: true, validator: validateContent, trigger: 'blur' }],
  mediaId: [{ required: true, validator: validateSrc, trigger: 'change' }],
};
const labelarr = ref([]);
const myHeaders = { 'X-Token': getToken() };
const formValidateRef = ref(null);

const fileUrl = computed(() => {
  return https + `/wechat/reply/upload/image`;
});
const voiceUrl = computed(() => {
  return https + `/wechat/reply/upload/voice`;
});
const httpsURL = computed(() => {
  return import.meta.env.VITE_APP_BASE_API.replace('api/', '');
});

const change = (e) => {
  proxy.$forceUpdate();
};
// 上传
const handleUploadForm = (param) => {
  const formData = new FormData();
  formData.append('media', param.file);
  let loading = ElLoading.service({
    lock: true,
    text: '上传中，请稍候...',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)',
  });
  wechatUploadApi(formData, { type: formValidate.type === 'image' ? 'image' : 'voice' })
    .then((res) => {
      loading.close();
      formValidate.contents.mediaId = res.mediaId;
      formValidate.contents.srcUrl = res.url;
      ElMessage.success('上传成功');
    })
    .catch(() => {
      loading.close();
    });
};
const changePic = () => {
  proxy.$modalArticle(function (row) {
    formValidate.contents.articleData = {
      title: row.title,
      imageInput: row.imageInput,
    };
    formValidate.contents.articleId = row.id;
  });
};
const handleClosePic = () => {
  visible.value = false;
};
// 详情
const details = () => {
  loading.value = true;
  replyInfoApi({ id: route.params.id })
    .then(async (res) => {
      const info = res || null;
      Object.assign(formValidate, {
        status: info.status,
        type: info.type,
        keywords: info.keywords,
        id: info.id,
        contents: {
          content: JSON.parse(info.data).content,
          mediaId: JSON.parse(info.data).mediaId,
          srcUrl: JSON.parse(info.data).srcUrl,
          articleData: JSON.parse(info.data).articleData,
        },
      });
      labelarr.value = info.keywords.split(',') || [];
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
    });
};
// 关注回复，无效关键词详情
const followDetails = () => {
  loading.value = true;
  keywordsInfoApi({ keywords: route.path.indexOf('follow') !== -1 ? 'subscribe' : 'default' })
    .then(async (res) => {
      const info = res || null;
      Object.assign(formValidate, {
        status: info.status,
        type: info.type,
        keywords: info.keywords,
        data: '',
        id: info.id,
        contents: {
          content: JSON.parse(info.data).content || '',
          mediaId: JSON.parse(info.data).mediaId || '',
          srcUrl: JSON.parse(info.data).srcUrl || '',
          articleData: JSON.parse(info.data).articleData || {},
        },
      });
      loading.value = false;
    })
    .catch(() => {
      loading.value = false;
      // if (res.message === '数据不存在') return
      // this.$message.error(res.message)
    });
};
// 下拉选择
const RuleFactor = (type) => {
  switch (type) {
    case 'text':
      formValidate.contents.mediaId = '';
      formValidate.contents.srcUrl = '';
      formValidate.contents.articleData = {};
      break;
    case 'news':
      formValidate.contents.mediaId = '';
      formValidate.contents.content = '';
      formValidate.contents.srcUrl = '';
      formValidate.contents.articleData = {};
      break;
    default:
      formValidate.contents.content = '';
      formValidate.contents.mediaId = '';
      formValidate.contents.articleData = {};
  }
  // this.$refs['formValidate'].resetFields();
};
const handleClose = (tag) => {
  const index = labelarr.value.indexOf(tag);
  labelarr.value.splice(index, 1);
};
const addlabel = () => {
  const count = labelarr.value.indexOf(val.value);
  if (count === -1) {
    labelarr.value.push(val.value);
  }
  val.value = '';
};
// 保存
const submenus = Debounce(function (name) {
  formValidateRef.value.validate((valid) => {
    if (valid) {
      formValidate.keywords = labelarr.value.join(',');
      formValidate.data = JSON.stringify(formValidate.contents);
      if (route.path.indexOf('keyword') !== -1) {
        route.params.id
          ? replyUpdateApi({ id: route.params.id }, formValidate)
              .then(async (res) => {
                operation();
              })
              .catch((res) => {
                ElMessage.error(res.message);
              })
          : replySaveApi(formValidate)
              .then(async (res) => {
                operation();
              })
              .catch((res) => {
                ElMessage.error(res.message);
              });
      } else {
        route.path.indexOf('follow') !== -1
          ? (formValidate.keywords = 'subscribe')
          : (formValidate.keywords = 'default');
        formValidate.id !== null
          ? replyUpdateApi({ id: formValidate.id }, formValidate).then(async (res) => {
              ElMessage.success('操作成功');
            })
          : replySaveApi(formValidate)
              .then(async (res) => {
                operation();
              })
              .catch((res) => {
                ElMessage.error(res.message);
              });
      }
    } else {
      return false;
    }
  });
});
// 保存成功操作
const operation = () => {
  proxy.$modalSure('继续添加')
    .then(() => {
      setTimeout(() => {
        labelarr.value = [];
        val.value = '';
        formValidateRef.value.resetFields();
        formValidate.contents.mediaId = '';
      }, 1000);
    })
    .catch(() => {
      setTimeout(() => {
        router.push({ path: `/appSetting/publicAccount/wxReply/keyword` });
      }, 500);
    });
};

watch(route, (to, from) => {
  if (route.params.id) {
    // this.formValidate.keywords = this.$route.params.key
    details();
  } else {
    // this.labelarr = []
    // this.$refs['formValidate'].resetFields()
  }
});

onMounted(() => {
  if (route.params.id) {
    details();
  }
  if (route.path.indexOf('keyword') === -1) {
    followDetails();
  }
});
</script>

<style scoped lang="scss">
.newsBox {
  background: #fff;
}
.arrbox {
  background-color: white;
  font-size: 12px;
  border: 1px solid #dcdee2;
  border-radius: 6px;
  margin-bottom: 0px;
  padding: 0 5px;
  text-align: left;
  box-sizing: border-box;
  width: 90%;
}
.news_sp {
  font-size: 12px;
  color: #000000;
  background: #fff;
  width: 100%;
  height: 38px;
  line-height: 38px;
  padding: 0 12px;
  box-sizing: border-box;
  display: block;
}
.arrbox_ip {
  font-size: 12px;
  border: none;
  box-shadow: none;
  outline: none;
  background-color: transparent;
  padding: 0;
  margin: 0;
  width: auto !important;
  max-width: inherit;
  min-width: 80px;
  vertical-align: top;
  color: #34495e;
  margin: 2px;
}

.left {
  min-width: 390px;
  min-height: 550px;
  position: relative;
  padding-left: 40px;
}

.top {
  position: absolute;
  top: 0px;
}

.bottom {
  position: absolute;
  bottom: 0px;
}

.centent {
  background: #f4f5f9;
  min-height: 545px;
  width: 320px;
  padding: 15px;
  box-sizing: border-box;
}

.right {
  background: #fff;
  min-height: 300px;
}

.box-content {
  position: relative;
  max-width: 60%;
  min-height: 40px;
  margin-left: 15px;
  padding: 10px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  word-break: break-all;
  word-wrap: break-word;
  line-height: 1.5;
  border-radius: 5px;
}

.box-content_pic {
  width: 100%;
}

.box-content_pic img {
  width: 100%;
  height: auto;
}

.box-content:before {
  content: '';
  position: absolute;
  left: -13px;
  top: 11px;
  display: block;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 10px solid #ccc;
  -webkit-transform: rotate(90deg);
  transform: rotate(90deg);
}

.box-content:after {
  content: '';
  content: '';
  position: absolute;
  left: -12px;
  top: 11px;
  display: block;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 10px solid #f5f5f5;
  -webkit-transform: rotate(90deg);
  transform: rotate(90deg);
}

.time-wrapper {
  margin-bottom: 10px;
  text-align: center;
  margin-top: 62px;
}

.time {
  display: inline-block;
  color: #f5f5f5;
  background: rgba(0, 0, 0, 0.3);
  padding: 3px 8px;
  border-radius: 3px;
  font-size: 12px;
}

.text-box {
  display: flex;
}

.avatar {
  width: 40px;
  height: 40px;
}

.avatar img {
  width: 100%;
  height: 100%;
}
.modelBox {
  .ivu-modal-body {
    padding: 0 16px 16px 16px !important;
  }
}
.news_pic {
  width: 100%;
  height: 150px;
  overflow: hidden;
  position: relative;
  background-size: 100%;
  background-position: center center;
  border-radius: 5px 5px 0 0;
  padding: 10px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}
.news_cent {
  width: 100%;
  height: auto;
  background: #fff;
  border-top: 1px dashed #eee;
  display: flex;
  padding: 10px;
  box-sizing: border-box;
  justify-content: space-between;
  .news_sp1 {
    font-size: 12px;
    color: #000000;
    width: 71%;
  }
  .news_cent_img {
    width: 81px;
    height: 46px;
    border-radius: 6px;
    overflow: hidden;
    img {
      width: 100%;
      height: 100%;
    }
  }
}
</style>
