<template>
  <div>
    <el-dialog
      title="上传图片"
      v-model="uploadModal"
      :append-to-body="true"
      :width="isIframe ? '100%' : '1024px'"
      :fullscreen="isIframe"
      @close="closed"
    >
      <div class="main" v-loading="loading">
        <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="100px" class="demo-ruleForm">
          <el-form-item label="上传方式：" prop="type">
            <el-radio-group v-model="ruleForm.type" @input="radioChange(ruleForm.type)">
              <el-radio :label="0" :value="0">本地上传</el-radio>
              <el-radio :label="1" :value="1">网络上传</el-radio>
              <el-radio :label="2" :value="2">扫码上传</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="上传至分组：" prop="region" v-show="ruleForm.type == 0 || ruleForm.type == 1">
            <el-cascader
              class="form-width"
              v-model="ruleForm.region"
              :props="propsData"
              :options="categoryList"
              @change="handleChange"
            ></el-cascader>
          </el-form-item>
          <el-form-item label="网络图片：" prop="region" v-if="ruleForm.type == 1">
            <el-input class="form-width" v-model="webImgUrl" placeholder="请网络图片地址"></el-input>
            <span class="tq-text" v-db-click @click="getImg">提取照片</span>
          </el-form-item>
          <el-form-item label="上传图片：" prop="region" v-if="ruleForm.type == 0">
            <div class="acea-row">
              <div class="uploadCont">
                <el-upload
                  ref="uploadRef"
                  :action="fileUrl"
                  list-type="picture-card"
                  :on-change="fileChange"
                  :file-list="ruleForm.imgList"
                  :auto-upload="false"
                  :data="uploadData"
                  :headers="header"
                  :multiple="true"
                  :limit="limit"
                >
                  <template #default>
                    <i class="el-icon-plus"></i>
                  </template>
                  <template #file="{ file }">
                    <div
                      draggable="false"
                      @dragstart="handleDragStart($event, file)"
                      @dragover="handleDragOver($event, file)"
                      @dragenter="handleDragEnter($event, file)"
                      @dragend="handleDragEnd($event, file)"
                    >
                      <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                      <i class="el-icon-error btndel" v-db-click @click="handleWebRemove(file)" />
                    </div>
                  </template>
                </el-upload>
                <div class="tips">
                  建议上传图片最大宽度750px，不超过3MB；仅支持jpeg、jpg、png格式，可拖拽调整上传顺序
                </div>
              </div>
            </div>
          </el-form-item>
          <template v-if="ruleForm.type == 1">
            <div class="img-box pl100">
              <div
                v-for="(item, index) in ruleForm.imgList"
                :key="index"
                class="pictrue"
                draggable="false"
                @dragstart="handleDragStart($event, item)"
                @dragover.prevent="handleDragOver($event, item)"
                @dragenter="handleDragEnter($event, item)"
                @dragend="handleDragEnd($event, item)"
              >
                <img :src="item.url" />
                <i class="el-icon-error btndel" v-db-click @click="handleRemove(index)" />
              </div>
            </div>
          </template>
          <div class="code-image" v-if="ruleForm.type == 2">
            <div class="left">
              <el-form-item label="上传至分组：" prop="region">
                <el-cascader
                  class="form-width"
                  v-model="ruleForm.region"
                  :props="propsData"
                  :options="categoryList"
                  @change="handleChange"
                ></el-cascader>
              </el-form-item>
              <el-form-item label="二维码：" prop="region">
                <div class="code" ref="qrCodeUrlRef"></div>
                <div class="trip">扫描二维码，快速上传手机图片</div>
                <div class="trip-small">建议使用手机浏览器</div>
              </el-form-item>
            </div>
            <div class="right">
              <el-button v-db-click @click="scanUploadGet">刷新图库</el-button>
              <div class="tip">刷新图库按钮，可显示移动端上传成功的图片</div>
              <div class="img-box">
                <div
                  v-for="(item, index) in ruleForm.imgList"
                  :key="index"
                  class="pictrue"
                  draggable="false"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnter($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >
                  <img :src="item.att_dir" />
                  <i class="el-icon-error btndel" v-db-click @click="handleWebRemove(item)" />
                </div>
              </div>
            </div>
          </div>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button v-db-click @click="clear">取 消</el-button>
          <el-button type="primary" :disabled="!ruleForm.imgList.length" v-db-click @click="submitUpload"
            >确 定</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onBeforeUnmount, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { getCategoryListApi, moveApi, onlineUpload, scanUploadCode } from '@/api/uploadPictures';
import Setting from '@/setting';
import { getCookies } from '@/libs/util';
import { fileUpload, scanUploadQrcode, scanUploadGet } from '@/api/setting';
import QRCode from 'qrcodejs2';
import compressImg from '@/utils/compressImg.js';
import { isPicUpload } from '@/utils/index';

defineOptions({ name: '' });

const props = defineProps({
  categoryList: {
    default: () => {
      return [];
    },
  },
  categoryId: {
    default: '',
  },
  isPage: {
    default: false,
  },
  isIframe: {
    default: false,
  },
});

const emit = defineEmits(['uploadSuccess']);

const ruleFormRef = ref(null);
const uploadRef = ref(null);
const qrCodeUrlRef = ref(null);

const webImgUrl = ref('');
const uploadModal = ref(false);
const fileUrl = ref(Setting.apiBaseURL + '/file/upload');
const header = ref({
  Authorization: 'Bearer ' + getCookies('token'),
});
const uploadData = ref({});
const propsData = ref({ checkStrictly: true, emitPath: false, label: 'title', value: 'id' });
const disabled = ref(false);
const ruleForm = ref({
  type: 0,
  region: '',
  imgList: [],
});
const rules = ref({ type: [{ required: true, message: '请选择活动资源', trigger: 'change' }] });
const qrcode = ref('');
const scanToken = ref('');
const limit = ref(20);
const loading = ref(false);
const time = ref(undefined);
const dragging = ref(null);
const dialogImageUrl = ref('');
const dialogVisible = ref(false);

watch(
  uploadModal,
  (newVal) => {
    if (newVal) ruleForm.value.region = props.categoryId;
  },
  { immediate: true },
);

onBeforeUnmount(() => {
  clearInterval(time.value);
  time.value = undefined;
});

function radioChange(type) {
  ruleForm.value.type = type;
  ruleForm.value.imgList = [];
  clearInterval(time.value);
  time.value = undefined;
  if (type == 2) {
    scanUploadQrcodeFn();
    time.value = setInterval((e) => {
      scanUploadGetFn();
    }, 2000);
  }
}

function scanUploadQrcodeFn() {
  scanUploadQrcode(ruleForm.value.region).then((res) => {
    creatQrCode(res.data.url);
    scanToken.value = res.data.url;
  });
}

function scanUploadGetFn() {
  let token = scanToken.value.split('token=')[1];
  scanUploadGet(token).then((res) => {
    ruleForm.value.imgList = res.data;
  });
}

function getImg() {
  if (!webImgUrl.value) {
    ElMessage.error('请先输入图片地址');
    return;
  }
  if (webImgUrl.value.indexOf('.php') != -1) {
    ElMessage.error('请先输入其他图片地址');
    return;
  }
  ruleForm.value.imgList.push({
    url: webImgUrl.value,
  });
}

async function submitUpload() {
  if (!ruleForm.value.imgList.length) return ElMessage.warning('请先选择图片');
  if (ruleForm.value.type == 0) {
    uploadData.value = {
      pid: ruleForm.value.region,
    };
    if (ruleForm.value.imgList.length) {
      if (loading.value) return;
      loading.value = true;
      for (let i = 0; i < ruleForm.value.imgList.length; i++) {
        const file = ruleForm.value.imgList[i].raw;
        await uploadItem(file);
        if (i == ruleForm.value.imgList.length - 1) {
          ElMessage.success('上传成功');
          emit('uploadSuccess');
          uploadModal.value = false;
          loading.value = false;
          initData();
        }
      }
    }
  } else if (ruleForm.value.type == 1) {
    let urls = ruleForm.value.imgList.map((e) => {
      return e.url;
    });
    if (urls.length) {
      if (loading.value) return;
      loading.value = true;
      onlineUpload({ pid: ruleForm.value.region, images: urls })
        .then((res) => {
          ElMessage.success('上传成功');
          emit('uploadSuccess');
          uploadModal.value = false;
          loading.value = false;
          initData();
        })
        .catch((err) => {
          loading.value = false;
          ElMessage.error(err.msg);
        });
    }
  } else if (ruleForm.value.type == 2) {
    let attId = ruleForm.value.imgList.map((e) => {
      return e.att_id;
    });
    moveApi({ pid: ruleForm.value.region, images: attId }).then((res) => {
      ElMessage.success('上传成功');
      emit('uploadSuccess');
      uploadModal.value = false;
      initData();
    });
  }
}

function uploadItem(file) {
  return new Promise((resolve, reject) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('pid', ruleForm.value.region);
    fileUpload(formData)
      .then((res) => {
        if (res.status == 200) {
          resolve();
          // emit('uploadImgSuccess', res.data);
        } else {
          loading.value = false;
          ElMessage({
            message: '上传失败',
            type: 'error',
            duration: 1000,
          });
        }
      })
      .catch((err) => {
        loading.value = false;
        ElMessage.error(err.msg);
      });
  });
}

function beforeUpload(file) {
  console.log(file);
}

function creatQrCode(url) {
  qrCodeUrlRef.value.innerHTML = '';
  var qrcode = new QRCode(qrCodeUrlRef.value, {
    text: url, // 需要转换为二维码的内容
    width: 160,
    height: 160,
    colorDark: '#000000',
    colorLight: '#ffffff',
    correctLevel: QRCode.CorrectLevel.H,
  });
}

function handleWebRemove(file) {
  let index = ruleForm.value.imgList.findIndex((e) => {
    return e.url == file.url;
  });
  ruleForm.value.imgList.splice(index, 1);
}

function handleRemove(index) {
  ruleForm.value.imgList.splice(index, 1);
}

function handlePictureCardPreview(file) {
  dialogImageUrl.value = file.url;
  dialogVisible.value = true;
}

function handleDownload(file) {
  console.log(file);
}

async function fileChange(file, fileList) {
  if (isPicUpload(file)) {
    if (file.size >= 2097152) {
      await comImg(file.raw).then((res) => {
        fileList.map((e) => {
          if (e.uid === file.uid) {
            e.raw = res;
          }
        });
        ruleForm.value.imgList = fileList;
      });
    } else {
      ruleForm.value.imgList = fileList;
    }
  } else {
    // 从ruleForm对象的imgList数组中删除最后一个元素
    ruleForm.value.imgList.splice(ruleForm.value.imgList.length, 1);
  }
}

function comImg(file) {
  return new Promise((resolve, reject) => {
    compressImg(file).then((res) => {
      resolve(res);
    });
  });
}

function loadData(item, callback) {
  getCategoryListApi({
    pid: item.value,
  })
    .then(async (res) => {
      const data = res.data.list;
      callback(data);
    })
    .catch((res) => {});
}

function handleChange(e) {
  if (ruleForm.value.type == 2) scanUploadQrcodeFn();
}

// 移动
function handleDragStart(e, item) {
  dragging.value = item;
}

function handleDragEnd(e, item) {
  dragging.value = null;
}

function handleDragOver(e) {
  e.dataTransfer.dropEffect = 'move';
}

function handleDragEnter(e, item) {
  e.dataTransfer.effectAllowed = 'move';
  if (item === dragging.value) {
    return;
  }
  const newItems = [...ruleForm.value.imgList];
  const src = newItems.indexOf(dragging.value);
  const dst = newItems.indexOf(item);
  newItems.splice(dst, 0, ...newItems.splice(src, 1));
  ruleForm.value.imgList = newItems;
}

function closed() {
  initData();
  scanUploadCode().then((res) => {});
}

function clear() {
  uploadModal.value = false;
  initData();
}

function initData() {
  ruleForm.value.type = 0;
  ruleForm.value.region = 0;
  scanToken.value = '';
  webImgUrl.value = '';
  ruleForm.value.imgList = [];
  clearInterval(time.value);
  time.value = undefined;
}

defineExpose({
  uploadModal,
});
</script>
<style lang="scss" scoped>
:deep(.el-dialog__title) {
  font-size: 16px;
}
.main {
  min-height: 410px;
}
.pictrue {
  width: 60px !important;
  height: 60px !important;
  border: 1px dotted rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  position: relative;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
  }
}
.btndel {
  position: absolute;
  z-index: 1;
  font-size: 18px;
  right: -5px;
  top: -5px;
  color: #999;
}
.form-width {
  width: 280px;
}
.tq-text {
  margin-left: 14px;
  font-size: 12px;
  font-weight: 400;
  color: var(--prev-color-primary);
  cursor: pointer;
}
.uploadCont :deep(.el-upload--picture-card),
:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 64px;
  height: 64px;
  line-height: 72px;
  overflow: inherit;
}
.uploadCont :deep(.el-upload--picture-card),
:deep(.el-upload-list--picture-card .el-upload-list__item img) {
  width: 64px !important;
  height: 64px !important;
  border-radius: 6px;
  object-fit: cover;
}
.pl100 {
  padding-left: 100px;
}
.img-box {
  display: flex;
  flex-wrap: wrap;
}
.tips {
  font-size: 12px;
  color: #bbbbbb;
}
.code-image {
  display: flex;
  margin-top: 12px;
  .left {
    display: flex;
    flex-direction: column;
    margin-right: 20px;
    align-items: center;
    .code {
      border: 1px solid #dddddd;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 200px;
      height: 200px;
      border-radius: 4px;
      .code-img {
        width: 160px;
        height: 160px;
      }
    }
    .form-width {
      width: 200px;
    }
    .code {
      margin-bottom: 14px;
    }
    .trip {
      color: #333333;
      text-align: center;
      line-height: 18px;
    }
    .trip-small {
      font-size: 12px;
      font-weight: 400;
      color: #bbbbbb;
      text-align: center;
      line-height: 16px;
    }
  }
  .right {
    margin-top: 62px;
    .tip {
      font-size: 12px;
      font-weight: 400;
      color: #bbbbbb;
      margin: 10px 0;
    }
  }
}
</style>
