<template>
  <div>
    <el-tabs v-model="typeDate" @tab-click="radioChange" class="list-tabs">
      <el-tab-pane label="图片" name="pic"></el-tab-pane>
      <el-tab-pane label="视频" name="video"></el-tab-pane>
    </el-tabs>
    <el-row :gutter="20" v-loading="loadingPic">
      <el-col
        v-bind="grid"
        v-hasPermi="['admin:category:list:tree']"
        :style="{ width: !pictureType ? '23%' : '17%',maxWidth: '300px' }"
      >
        <div class="Nav">
          <div class="trees-coadd">
            <div class="scollhide">
              <div class="trees" :style="{ maxHeight: !pictureType ? '500px' : '' }">
                <el-tree
                  ref="treeRef"
                  :data="treeData2"
                  :filter-node-method="filterNode"
                  :props="defaultProps"
                  node-key="id"
                  highlight-current
                  icon-class="el-icon-arrow-down"
                >
                  <template #default="{ node, data }">
                    <div
                      :class="tableData.pid == 0 && data.id == 0 ? 'special' : ''"
                      class="custom-tree-node"
                      @click.stop="handleNodeClick(data)"
                    >
                      <div>
                        <span class="custom-tree-node-label" :title="node.label">{{ node.label }}</span>
                        <span v-if="data.space_property_name" style="font-size: 11px; color: #3889b1"
                          >（{{ data.name }}）</span
                        >
                      </div>
                      <span class="el-ic">
                        <el-dropdown>
                          <span class="el-dropdown-link">
                            <i
                              :class="data.id !== 0 ? 'el-icon-more' : 'el-icon-plus'"
                              @click.stop="allAdd(data)"
                            ></i>
                          </span>
                          <template #dropdown>
                            <el-dropdown-menu>
                              <el-dropdown-item
                                @click="onAdd(data.id)"
                                v-if="data.id !== 0"
                                >添加分类</el-dropdown-item
                              >
                              <el-dropdown-item
                                v-if="data.id !== 0"
                                @click="onEdit(data.id)"
                                >编辑分类</el-dropdown-item
                              >
                              <el-dropdown-item
                                v-if="data.id !== 0"
                                @click="handleOpenDelete(data.id)"
                                >删除分类</el-dropdown-item
                              >
                            </el-dropdown-menu>
                          </template>
                        </el-dropdown>
                      </span>
                    </div>
                  </template>
                </el-tree>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col  class="colLeft" :style="{ flex: 1 }">
        <div class="conter mb15 relative">
          <div class="bnt" :style="{ paddingLeft: listType == 1 ? '17px' : '0' }">
            <el-button type="primary" class="mr10 mb20" @click="checkPics" v-if="!pictureType">
              {{ typeDate === 'pic' ? '使用选中图片' : '使用选中视频' }}
            </el-button>
            <!--弹窗组件-->

            <!--素材管理-->
            <div class="acea-row">
              <div v-if="typeDate === 'pic'">
                <el-upload
                  class="upload-demo"
                  action
                  :http-request="handleUploadForm"
                  :headers="myHeaders"
                  :show-file-list="false"
                  accept=".jpg,.jpeg,.gif,.png,.bmp"
                  multiple
                >
                  <el-button class="mr10 mb20" type="primary">上传图片</el-button>
                </el-upload>
              </div>

              <el-upload
                v-if="typeDate !== 'pic'"
                class="upload-demo"
                action
                :http-request="handleUploadForm"
                :on-change="imgSaveToUrl"
                :headers="myHeaders"
                :show-file-list="false"
                multiple
              >
                <el-button class="mr10 mb20" type="primary">上传视频</el-button>
              </el-upload>
              <div v-hasPermi="['admin:system:attachment:delete']">
                <el-button class="mr10" @click.stop="editPicList(typeDate)"
                  >{{ typeDate === 'pic' ? '删除图片' : '删除视频' }}
                </el-button>
              </div>
            </div>
            <el-select
              v-hasPermi="['admin:system:attachment:move']"
              v-model="sleOptions.attachment_category_name"
              v-if="pictureType"
              :placeholder="typeDate === 'pic' ? '图片移动至' : '视频移动至'"
              class="mb20"
              :size="pictureType ? '' : 'small'"
            >
              <el-option
                class="demo"
                :label="sleOptions.attachment_category_name"
                :value="sleOptions.attachment_category_id"
                style="max-width: 560px; height: 200px; overflow: auto; background-color: #fff"
              >
                <el-tree
                  ref="tree2Ref"
                  :data="treeData2"
                  :filter-node-method="filterNode"
                  :props="defaultProps"
                  highlight-current
                  @node-click="handleSelClick"
                />
              </el-option>
            </el-select>
            <div class="input">
              <div>
                <el-input
                  v-model.trim="filterName"
                  class="searchInput"
                  v-if="false"
                  :placeholder="typeDate == 'pic' ? '搜索图片名称' : '搜索视频名称'"
                  :suffix-icon="Search"
                  style="width: 100%"
                  clearable
                />
              </div>
              <div class="checkoutBtn" v-if="pictureType">
                <div class="checkoutBtn-gg" :class="listType === 1 ? 'checkoutActive' : ''" @click="checkoutBtn(1)">
                  <i class="el-icon-menu"></i>
                </div>
                <div class="checkoutBtn-list" :class="listType === 0 ? 'checkoutActive' : ''" @click="checkoutBtn(0)">
                  <i class="el-icon-s-operation"></i>
                </div>
              </div>
            </div>
            <div class="abs_video"></div>
          </div>
          <div class="pictrueList acea-row">
            <div v-show="isShowPic" class="imagesNo">
              <i class="el-icon-picture" style="font-size: 60px; color: rgb(219, 219, 219)" />
              <span class="imagesNo_sp">素材为空</span>
            </div>
            <!-- 宫格布局 -->
            <div
              class="conters scrollbarAll"
              ref="myDivRef"
              :style="{ maxHeight: !pictureType ? '500px' : '', display: 'flex' }"
              v-show="listType === 1"
            >
              <div
                v-for="(item, index) in pictrueList.list"
                :key="item.attId || index"
                class="gridPic"
                :style="gridPicStyle"
                @mouseenter="handleMouse(item, true)"
                @mouseleave="handleMouse(item, false)"
              >
                <span class="num_badge" v-if="item.num > 0" :style="{ right: 0 }">{{ item.num }}</span>
                <img
                  :src="item.sattDir || item.attDir || localImg"
                  :class="item.isSelect ? 'on' : ''"
                  @click="changImage(item, index, pictrueList.list)"
                  v-if="item.attType !== 'video/mp4'"
                  :style="{
                    width: pictureType ? '100%' : '90px',
                    height: pictureType ? '146px' : '90px',
                    'object-fit': 'contain',
                  }"
                />
                <video
                  :src="item.sattDir"
                  :class="item.isSelect ? 'on' : ''"
                  @click="changImage(item, index, pictrueList.list)"
                  v-if="item.attType == 'video/mp4'"
                  :style="{
                    width: pictureType ? '100%' : '90px',
                    height: pictureType ? '146px' : '90px',
                    'object-fit': 'contain',
                  }"
                ></video>
                <div class="imgTitle" :style="{ width: pictureType ? `${(divWidth - 104) / 6}px` : '90px' }">
                  {{ item.name }}
                </div>
                <!-- 删除/重命名/查看 -->
                <div class="operate" v-if="pictureType && item.isCk">
                  <span @click="deleteOperate(item)">删除</span>
                  <span v-if="false">重命名</span>
                  <span v-if="false">查看</span>
                </div>
              </div>
              <div class="block">
                <el-pagination
                  :page-size="tableData.limit"
                  :current-page="tableData.page"
                  :pager-count="5"
                  layout="prev, pager, next, jumper"
                  :total="pictrueList.total"
                  @size-change="handleSizeChange"
                  @current-change="pageChange"
                  background
                />
              </div>
            </div>
            <!-- 列表布局 -->
            <div
              class="conters scrollbarAll"
              :style="{ maxHeight: !pictureType ? '500px' : '700px' }"
              v-show="listType === 0"
            >
              <el-table
                ref="multipleTableRef"
                tooltip-effect="dark"
                style="width: 100%"
                :data="pictrueList.list"
                @selection-change="listSelectionChange"
                v-show="!isShowPic"
              >
                <el-table-column type="selection"> </el-table-column>
                <el-table-column :label="typeDate === 'pic' ? '图片名称' : '视频名称'">
                  <template #default="scope">
                    <div class="imgDiv">
                      <img :src="scope.row.sattDir" alt="" class="listImg" v-if="typeDate == 'pic'" />
                      <video :src="scope.row.sattDir" v-if="typeDate !== 'pic'" class="listImg"></video>
                      <div class="imgName">
                        {{ scope.row.name }}
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="name" label="大小">
                  <template #default="scope">{{ scope.row.attSize }}</template>
                </el-table-column>
                <el-table-column prop="address" label="上传时间">
                  <template #default="scope">{{ scope.row.createTime }}</template>
                </el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-link type="primary" @click="deleteListFun(scope)">删除</el-link>
                    <span class="decollator" v-if="false">|</span>
                    <el-link type="primary" v-if="false">查看</el-link>
                    <span class="decollator" v-if="false">|</span>
                    <el-link type="primary" v-if="false">重命名</el-link>
                  </template>
                </el-table-column>
              </el-table>
              <div class="block">
                <el-pagination
                  background
                  :page-size="tableData.limit"
                  :current-page="tableData.page"
                  :pager-count="5"
                  layout="prev, pager, next, jumper"
                  :total="pictrueList.total"
                  @size-change="handleSizeChange"
                  @current-change="pageChange"
                />
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog
      :title="bizTitle"
      v-model="visible"
      destroy-on-close
      :close-on-click-modal="false"
      :modal="true"
      @close="closeModel"
      width="540px"
      :append-to-body="!pictureType ? true : false"
    >
      <el-form ref="editPramRef" :model="editPram" label-width="75px" v-loading="loading">
        <el-form-item
          label="上级分类"
          prop="pid"
          :rules="[
            {
              type: 'number',
              required: true,
              message: '请选择上级分类',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-cascader v-model="editPram.pid" :options="treeData2" :props="categoryProps" style="width: 100%" />
        </el-form-item>
        <el-form-item
          label="分类名称"
          prop="name"
          :rules="[
            {
              required: true,
              message: '请输入分类名称',
              trigger: ['blur', 'change'],
            },
          ]"
        >
          <el-input v-model.trim="editPram.name" placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number controls-position="right" v-model.trim="editPram.sort" />
        </el-form-item>
        <el-form-item class="dialog-footer-inner">
          <el-button @click="visible = false">取 消</el-button>
          <el-button type="primary" @click="handlerSubmit('editPram')">确定 </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-dialog
      title="上传图片"
      v-model="visiblePic"
      @close="onCallback(false)"
      class="handleDialog"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="uploadDialog"
    >
      <div v-if="visiblePic" class="wrap">
        <div class="main">
          <div class="cropperContent">
            <div class="mb35">
              <div class="title">原始图片</div>
              <div class="cropper mr20">
                <vue-cropper
                  ref="cropperRef"
                  :img="option.img"
                  :output-size="option.size"
                  :output-type="option.outputType"
                  :info="true"
                  :full="option.full"
                  :fixed="fixed"
                  :fixed-number="fixedNumber"
                  :can-move="option.canMove"
                  :can-move-box="option.canMoveBox"
                  :fixed-box="option.fixedBox"
                  :original="option.original"
                  :auto-crop="option.autoCrop"
                  :auto-crop-width="option.autoCropWidth"
                  :auto-crop-height="option.autoCropHeight"
                  :center-box="option.centerBox"
                  @real-time="realTime"
                  :high="option.high"
                  @img-load="imgLoad"
                  mode="contain"
                  :max-img-size="option.max"
                ></vue-cropper>
              </div>
              <div class="scopeButton mt20 mb20">
                <el-button-group>
                  <el-button type="primary">
                    <label for="uploads">选择本地图片</label>
                    <input
                      type="file"
                      id="uploads"
                      class="inputFile"
                      accept="image/png, image/jpeg, image/gif, image/jpg"
                      @change="uploadImg($event)"
                    />
                  </el-button>
                  <el-button type="primary" v-if="!crap" @click="startCrop">图片裁剪 </el-button>
                  <el-button type="primary" @click="stopCrop" v-else>关闭裁剪 </el-button>
                  <el-button type="primary" @click="clearCrop">清除裁剪 </el-button>
                  <el-button type="primary" @click="changeScale(1)" :icon="Plus"></el-button>
                  <el-button type="primary" @click="changeScale(-1)" :icon="Minus"></el-button>
                  <el-button type="primary" @click="rotateLeft" :icon="RefreshLeft"></el-button>
                  <el-button type="primary" @click="rotateRight" :icon="RefreshRight"></el-button>
                </el-button-group>
              </div>
              <div class="uploadButton acea-row">
                <el-button-group>
                  <el-button @click="down('blob')" type="primary" :icon="Download">下载 </el-button>
                  <el-button @click="uploadNewPic" type="primary" :icon="Upload2">上传至图片库 </el-button>
                  <el-upload
                    action
                    :http-request="handleUploadForm"
                    :on-change="imgSaveToUrl"
                    :headers="myHeaders"
                    :show-file-list="false"
                    multiple
                  >
                    <el-button :icon="Upload2" type="primary" style="margin-left: 15px">多图上传 </el-button>
                  </el-upload>
                </el-button-group>
              </div>
            </div>
            <div class="previewBox">
              <div class="title">实时预览</div>
              <div
                class="show-preview"
                :style="{ width: previews.w + 'px', height: previews.h + 'px', overflow: 'hidden', margin: '5px' }"
              >
                <div :style="previews.div">
                  <img :src="previews.url" :style="previews.img" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import { ref, reactive, computed, watch, onMounted, onUnmounted, getCurrentInstance, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage, ElMessageBox, ElLoading } from '@/utils/elementPlusFeedback';
import { Plus, Minus, RefreshLeft, RefreshRight, Download, Upload as Upload2, Search } from '@element-plus/icons-vue';
import { addCategroy, treeCategroy, infoCategroy, updateCategroy, deleteCategroy } from '@/api/categoryApi';
import { fileImageApi, fileListApi, fileDeleteApi, attachmentMoveApi } from '@/api/systemSetting';
import { getToken } from '@/utils/auth';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { VueCropper } from 'vue-cropper';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'uploadPicture' });

const props = defineProps({
  pictureType: {
    type: String,
    default: '',
  },
  isMore: {
    type: String,
    default: '1',
  },
  modelName: {
    type: String,
    default: '',
  },
  checkedMore: {
    type: Array,
    default: () => [],
  },
  labelName: {
    type: String,
    default: '',
  },
  // 是否展示上传视频
  isShowVideo: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(['getImage']);

const { proxy } = getCurrentInstance();
const route = useRoute();

//grid宽度
const divWidth = ref(null);
const checkDialogData = ref({});
//列表类型 1 宫格 0 列表
const listType = ref(1);
const uploadName = ref('上传');
const fixed = ref(false);
const fixedNumber = ref([16, 9]);
const option = reactive({
  img: '',
  size: 1,
  full: false,
  outputType: 'png',
  canMove: true,
  fixedBox: false,
  original: false,
  canMoveBox: true,
  autoCrop: false,
  // 只有自动截图开启 宽度高度才生效
  // autoCropWidth: 160,
  // autoCropHeight: 150,
  centerBox: false,
  high: true,
  max: 99999,
});
const picId = ref('');
const newPicId = ref('');
const crap = ref(false);
const previews = ref({});
const visiblePic = ref(false);
const loadingPic = ref(false);
const loading = ref(false);
const modals = ref(true);
const allTreeList = ref([]);
const categoryProps = {
  value: 'id',
  label: 'name',
  children: 'child',
  expandTrigger: 'hover',
  checkStrictly: true,
  emitPath: false,
};
const editPram = reactive({
  pid: 0,
  name: '',
  type: 2,
  sort: 1,
  status: 0,
  url: 'url',
  id: 0,
});
const visible = ref(false);
const bizTitle = ref('');
const sleOptions = reactive({
  attrId: '',
  pid: 0,
});
const list = ref([]);
const grid = {
  xl: 7,
  lg: 7,
  md: 7,
  sm: 7,
  xs: 24,
};
const grid2 = {
  xl: 17,
  lg: 17,
  md: 17,
  sm: 17,
  xs: 24,
};
const filterText = ref(''); //筛选分类
const filterName = ref(''); //筛选图片/视频名称
const treeData = ref([]);
const treeData2 = ref([]);
const defaultProps = {
  children: 'child',
  label: 'name',
};
const tableData = reactive({
  page: 1,
  limit: 20,
  pid: 0,
  attType: 'jpg,jpeg,gif,png,bmp,PNG,JPG',
});
const listTableData = reactive({
  page: 1,
  limit: 8,
  pid: 0,
  attType: 'jpg,jpeg,gif,png,bmp,PNG,JPG',
});
const classifyId = ref(0);
const myHeaders = { 'X-Token': getToken() };
const treeFrom = reactive({
  status: -1,
  type: 2,
});
const pictrueList = reactive({
  list: [],
  total: 0,
});
const isShowPic = ref(false);
const checkPicList = ref([]);
const ids = ref([]);
const listPram = reactive({
  pid: 0,
  type: 2,
  status: 0,
  name: '',
  page: 1,
  limit: 9999,
});
const localImg = ref('');
const videoStatus = ref(false);
const typeDate = ref('pic');
const picDeleteConfig = reactive({
  // 此处删除完全可以用confirm来替代，但是因为在多层dialog后再次 confirm 后就会出现index的层级问题，算是规避问题的处理方式
  dialogVisible: false,
  forDeleteId: 0,
});

const treeRef = ref(null);
const tree2Ref = ref(null);
const myDivRef = ref(null);
const multipleTableRef = ref(null);
const editPramRef = ref(null);
const cropperRef = ref(null);

watch(filterText, (val) => {
  treeRef.value.filter(val);
});

const gridPicStyle = computed(() => {
  return {
    width: props.pictureType ? `${(divWidth.value - 104) / 6}px` : '90px',
    marginLeft: props.pictureType ? '17px' : '20px',
    marginBottom: props.pictureType ? '42px' : '19px',
  };
});

const allAttachmentName = computed(() => (typeDate.value === 'pic' ? '全部图片' : '全部视频'));

// created
if (props.labelName) {
  option.fixedBox = true;
  option.autoCropWidth = 200;
  option.autoCropHeight = 100;
  let str = props.labelName;
  let regex = /\((.+?)\)/g; // 中括号
  let regexs = /（(.+?)）/g; // 中文小括号
}
props.pictureType ? (tableData.limit = 18) : (tableData.limit = 18);
if (checkPermi(['admin:category:list:tree'])) getList();
getFileList();

onMounted(() => {
  if (props.pictureType) {
    // 在mounted钩子函数中获取div的初始宽度
    divWidth.value = myDivRef.value.offsetWidth;
    window.addEventListener('resize', getWindowInfo);
  }
});
onUnmounted(() => {
  //移除监听事件
  window.removeEventListener('resize', getWindowInfo);
});

//视口大小变化更改盒子宽度
function getWindowInfo() {
  divWidth.value = myDivRef.value.offsetWidth;
}
function allAdd(data) {
  if (data.id === 0) {
    onAdd();
  }
}
//宫格删除操作
function deleteOperate(item) {
  proxy.$modalSure().then(() => {
    fileDeleteApi(item.attId).then(() => {
      ElMessage.success('删除成功');
      tableData.page = 1;
      getFileList();
      uploadName.value = '上传';
    });
  });
}
//移入移出显示操作
function handleMouse(item, type) {
  item.isCk = type;
}
//查看模态框控制
function checkHandleClose(done) {
  ElMessageBox.confirm('确认关闭？')
    .then((_) => {
      done();
    })
    .catch((_) => {});
}
//列表选中
function listSelectionChange(val) {
  checkPicList.value = val;
  val.forEach((element) => {
    ids.value.push(element.attId);
  });
}

function deleteListFun(scope) {
  proxy.$modalSure().then(() => {
    fileDeleteApi(scope.row.attId).then(() => {
      ElMessage.success('删除成功');
      tableData.page = 1;
      getFileList();
      checkPicList.value = [];
      uploadName.value = '上传';
    });
  });
}
//promise 获取列表，用于同步切换分类
function getFileListAsync() {
  return new Promise((resolve, reject) => {
    loadingPic.value = true;
    fileListApi(tableData)
      .then(async (res) => {
        pictrueList.list = res.list;
        if (tableData.page === 1 && pictrueList.list.length > 0)
          pictrueList.list[0].localImg = localImg.value;
        if (pictrueList.list.length) {
          isShowPic.value = false;
        } else {
          isShowPic.value = true;
        }
        pictrueList.total = res.total;
        checkPicList.value = [];
        loadingPic.value = false;
        resolve();
      })
      .catch(() => {
        loadingPic.value = false;
      });
  });
}
//切换宫格或列表
function checkoutBtn(type) {
  if (type === 0) {
    // this.pictureType ? (this.tableData.limit = 10) : (this.tableData.limit = 7);
    tableData.limit = 10;
    tableData.page = 1;
    checkPicList.value = [];
    ids.value = [];
    //请求成功后切换列表
    getFileListAsync().then(() => {
      listType.value = type;
    });
  } else {
    tableData.limit = 18;
    tableData.page = 1;
    checkPicList.value = [];
    ids.value = [];
    getFileListAsync().then(() => {
      listType.value = type;
    });
  }
}
// 选取图片后自动回调，里面可以获取到文件
function imgSaveToUrl(event) {
  // 也可以用file
  proxy.localFile = event.raw; // 或者 this.localFile=file.raw

  // 转换操作可以不放到这个函数里面，
  // 因为这个函数会被多次触发，上传时触发，上传成功也触发
  let reader = new FileReader();
  reader.readAsDataURL(proxy.localFile); // 这里也可以直接写参数event.raw

  // 转换成功后的操作，reader.result即为转换后的DataURL ，
  // 它不需要自己定义，你可以console.integralLog(reader.result)看一下
  reader.onload = () => {
    // console.integralLog(reader.result)
  };

  /* 另外一种本地预览方法 */
  let URL = window.URL || window.webkitURL;
  localImg.value = URL.createObjectURL(event.raw);
  // 转换后的地址为 blob:http://xxx/7bf54338-74bb-47b9-9a7f-7a7093c716b5
}
function closeModel() {
  editPramRef.value.resetFields();
}
function handlerSubmit(formName) {
  editPramRef.value.validate((valid) => {
    if (valid) {
      if (editPram.pid > 0) {
        if (editPram.pid === editPram.id) return ElMessage.warning('上级分类不能选当前分类');
      }
      bizTitle.value === '添加分类'
        ? addCategroy(editPram).then((data) => {
            ElMessage.success('创建成功');
            visible.value = false;
            getList();
          })
        : updateCategroy(editPram).then((data) => {
            ElMessage.success('编辑成功');
            visible.value = false;
            getList();
          });
    } else {
      return false;
    }
  });
}
// 表单分类
function handlerGetList() {
  let datas = {
    name: allAttachmentName.value,
    id: 0,
  };
  treeCategroy(treeFrom).then((data) => {
    allTreeList.value = data;
    allTreeList.value.unshift(datas);
  });
}
// 搜索分类
function filterNode(value, data) {
  if (!value) return true;
  return data.name.indexOf(value) !== -1;
}
// 所有分类
function getList() {
  const data = {
    name: allAttachmentName.value,
    id: 0,
  };
  return treeCategroy(treeFrom).then((res) => {
    treeData.value = res;
    treeData.value.unshift(data);
    treeData2.value = [...treeData.value];
  });
}
// 添加分类
function onAdd(id) {
  tableData.pid = id;
  bizTitle.value = '添加分类';
  visible.value = true;
  Object.assign(editPram, {
    pid: id || 0,
    name: '',
    type: 2,
    sort: 1,
    status: 0,
    url: 'url',
    id: 0,
  });
}
// 编辑
function onEdit(id) {
  bizTitle.value = '编辑分类';
  loading.value = true;
  infoCategroy({ id }).then((res) => {
    Object.assign(editPram, res);
    loading.value = false;
  });
  visible.value = true;
}
// 删除
function handleOpenDelete(id) {
  picDeleteConfig.forDeleteId = id;
  picDeleteConfig.dialogVisible = true;
  proxy.$modalSure('删除当前分类吗').then(() => {
    deleteCategroy({ id: picDeleteConfig.forDeleteId }).then(() => {
      ElMessage.success('删除成功');
      picDeleteConfig.forDeleteId = 0;
      getList();
    });
  });
}
function handleNodeClick(data) {
  checkPicList.value = [];
  pictrueList.list = [];
  tableData.pid = data.id;
  tableData.page = 1;
  getFileList();
}
// 上传
function handleUploadForm(param) {
  const formData = new FormData();
  const data = {
    model: props.modelName ? props.modelName : 'product',
    pid: tableData.pid ? tableData.pid : 0,
  };
  formData.append('multipart', param.file);
  uploadPic(formData, data);
}
function uploadPic(formData, data) {
  let loadingInstance = ElLoading.service({
    lock: true,
    text: '上传中，请稍候...',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)',
  });
  fileImageApi(formData, data)
    .then((res) => {
      loadingInstance.close();
      ElMessage.success('上传成功');
      tableData.page = 1;
      getFileList();
      onCallback();
    })
    .catch((res) => {
      onCallback();
      loadingInstance.close();
    });
}
// 文件列表
function getFileList() {
  loadingPic.value = true;
  fileListApi(tableData)
    .then(async (res) => {
      pictrueList.list = res.list;
      if (tableData.page === 1 && pictrueList.list.length > 0)
        pictrueList.list[0].localImg = localImg.value;
      if (pictrueList.list.length) {
        isShowPic.value = false;
      } else {
        isShowPic.value = true;
      }
      pictrueList.total = res.total;
      checkPicList.value = [];
      loadingPic.value = false;
    })
    .catch(() => {
      loadingPic.value = false;
    });
}
function pageChange(page) {
  tableData.page = page;
  checkPicList.value = [];
  getFileList();
}
function handleSizeChange(val) {
  tableData.limit = val;
  getFileList();
}
// 选中图片
function changImage(item, index, row) {
  let activeIndex = 0;
  if (!item.isSelect) {
    item.isSelect = true;
    checkPicList.value.push(item);
  } else {
    item.isSelect = !item.isSelect;
    checkPicList.value.map((el, index) => {
      if (el.attId == item.attId) {
        activeIndex = index;
      }
    });
    checkPicList.value.splice(activeIndex, 1);
  }
  ids.value = [];
  checkPicList.value.map((item, i) => {
    ids.value.push(item.attId);
  });
  if (checkPicList.value.length > 0) {
    uploadName.value = '裁剪图片';
  } else {
    uploadName.value = '上传';
  }

  pictrueList.list.map((el, i) => {
    if (el.isSelect) {
      checkPicList.value.filter((el2, j) => {
        if (el.attId == el2.attId) {
          el.num = j + 1;
          nextTick(() => {
            pictrueList.list;
          });
        }
      });
    } else {
      el.num = 0;
    }
  });
}
// 点击使用选中图片
function checkPics() {
  if (!checkPicList.value.length) return ElMessage.warning('请先选择图片');
  if (route && route.query.field === 'dialog') {
    let str = '';
    for (let i = 0; i < checkPicList.value.length; i++) {
      str += '<img src="' + checkPicList.value[i].sattDir + '">';
    }
    /* eslint-disable */
    nowEditor.dialog.close(true);
    nowEditor.editor.setContent(str, true);
  } else {
    if (props.isMore === '1' && checkPicList.value.length > 1) {
      return ElMessage.warning('最多只能选一张图片');
    }

    emit('getImage', [...props.checkedMore, ...checkPicList.value]);
  }
}
// 删除图片
function editPicList(typeDate) {
  if (!checkPicList.value.length) return ElMessage.warning(`请先选择${typeDate === 'pic' ? '图片' : '视频'}`);
  proxy.$modalSure().then(() => {
    fileDeleteApi(ids.value.join(',')).then(() => {
      ElMessage.success('删除成功');
      tableData.page = 1;
      getFileList();
      checkPicList.value = [];
      uploadName.value = '上传';
    });
  });
}
// 移动分类点击
function handleSelClick(node) {
  if (ids.value.length) {
    Object.assign(sleOptions, {
      attrId: ids.value.join(','),
      pid: node.id,
    });
    getMove();
  } else {
    ElMessage.warning('请先选择图片');
  }
}
function getMove() {
  attachmentMoveApi(sleOptions)
    .then(async (res) => {
      ElMessage.success('操作成功');
      clearBoth();
      tableData.page = 1;
      getFileList();
    })
    .catch((res) => {
      clearBoth();
    });
}
function clearBoth() {
  Object.assign(sleOptions, {
    attrId: '',
    pid: 0,
  });
  checkPicList.value = [];
  ids.value = [];
}
function videoChange(val) {
  tableData.attType = val ? 'video/mp4' : 'jpg,jpeg,gif,png,bmp,PNG,JPG';
  tableData.pid = 0;
  tableData.page = 1;
  checkPicList.value = [];
  ids.value = [];
  pictrueList.list = [];
  getList().then(() => {
    nextTick(() => {
      treeRef.value && treeRef.value.setCurrentKey(0);
    });
  });
  getFileList();
}
function radioChange(pane) {
  const activeName = pane?.props?.name || pane?.name || typeDate.value;
  typeDate.value = activeName;
  videoChange(activeName === 'video');
}
/**
 * 裁剪图片
 * @returns {ElMessageComponent}
 */
function startCrop() {
  crap.value = true;
  cropperRef.value.startCrop();
}
function stopCrop() {
  //  stop
  crap.value = false;
  cropperRef.value.stopCrop();
}
function clearCrop() {
  // clear
  cropperRef.value.clearCrop();
}
function handleChangeImage() {
  if (checkPicList.value.length > 1) {
    return ElMessage.warning('最多只能选一张图片');
  }
  visiblePic.value = true;
  if (checkPicList.value.length > 0) imageToBase64(checkPicList.value[0].sattDir);
}
function saveNewPic() {
  // if (!this.newPicId) {
  //   return ElMessage.warning("请上传裁剪后的图片");
  // }
  cropperRef.value.getCropBlob((data) => {
    proxy.downImg = window.URL.createObjectURL(data);
    // aLink.download = this.downImg;
    emit('getImage', [
      {
        sattDir: proxy.downImg,
      },
    ]);
  });
}
function onCallback() {
  visiblePic.value = false;
  option.img = '';
  if (cropperRef.value) {
    stopCrop();
    clearCrop();
  }
}
function changeScale(num) {
  num = num || 1;
  cropperRef.value.changeScale(num);
}
function rotateLeft() {
  cropperRef.value.rotateLeft();
}
function rotateRight() {
  cropperRef.value.rotateRight();
}
// 实时预览函数
function realTime(data) {
  previews.value = data;
}
//image转Base64
function imageToBase64(file) {
  //一定要设置为let，不然图片不显示
  let image = new Image();
  //解决跨域问题
  image.setAttribute('crossOrigin', 'anonymous');
  let imageUrl = file;
  image.src = imageUrl;
  //image.onload为异步加载
  image.onload = () => {
    var canvas = document.createElement('canvas');
    canvas.width = image.width;
    canvas.height = image.height;
    var context = canvas.getContext('2d');
    context.drawImage(image, 0, 0, image.width, image.height);

    var quality = 0.8;
    //这里的dataurl就是base64类型
    var dataURL = canvas.toDataURL('image/jpeg', quality); //使用toDataUrl将图片转换成jpeg的格式,不要把图片压缩成png，因为压缩成png后base64的字符串可能比不转换前的长！
    option.img = dataURL;
  };
}
// 将base64转换为文件 百度随便找的 看需求使用
function dataURLtoFile(dataurl, filename) {
  var arr = dataurl.split(','),
    mime = arr[0].match(/:(.*?);/)[1],
    bstr = atob(arr[1]),
    n = bstr.length,
    u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new File([u8arr], filename, { type: mime });
}
const uploadNewPic = Debounce(function () {
  cropperRef.value.getCropData((data) => {
    let name = new Date().getTime();
    let file = dataURLtoFile(data, `${name}.png`);
    const datas = {
      model: props.modelName ? props.modelName : 'product',
      pid: tableData.pid ? tableData.pid : 0,
    };
    let formData = new FormData();
    formData.append('multipart', file);
    uploadPic(formData, datas);
  });
});
function down(type) {
  // event.preventDefault()
  var aLink = document.createElement('a');
  aLink.download = 'author-img';
  // 输出
  if (type === 'blob') {
    cropperRef.value.getCropBlob((data) => {
      proxy.downImg = window.URL.createObjectURL(data);
      // aLink.download = this.downImg;
      aLink.href = window.URL.createObjectURL(data);
      aLink.click();
    });
  } else {
    cropperRef.value.getCropData((data) => {
      proxy.downImg = data;
      aLink.href = data;
      aLink.click();
    });
  }
}
function uploadImg(e) {
  //上传图片
  var file = e.target.files[0];
  if (!/\.(gif|jpg|jpeg|png|bmp|GIF|JPG|PNG)$/.test(e.target.value)) {
    ElMessage.warning('图片类型必须是.gif,jpeg,jpg,png,bmp中的一种');
    return false;
  }
  var reader = new FileReader();
  reader.onload = (e) => {
    let data;
    if (typeof e.target.result === 'object') {
      // 把Array Buffer转化为blob 如果是base64不需要
      data = window.URL.createObjectURL(new Blob([e.target.result]));
    } else {
      data = e.target.result;
    }
    option.img = data;
  };
  // 转化为base64
  // reader.readAsDataURL(file)
  // 转化为blob
  reader.readAsArrayBuffer(file);
}
function imgLoad(msg) {}

defineExpose({ getList, getFileList });
</script>

<style scoped lang="scss">
.cut {
  width: 500px;
  height: 500px;
  margin: 30px auto;
}

.handleDialog {
  z-index: 999999 !important;

  .cropperContent {
    display: flex;
    padding-left: 20px;
    flex-wrap: wrap;

    .cropper {
      width: 1200px;
      height: 500px;
      border: 1px solid #ddd;
    }

    .title {
      font-size: 18px;
      height: 36px;
      margin-bottom: 20px;
    }

    .previewBox {
      flex: 1;
      display: flex;
      flex-direction: column;

      .showPreview {
        flex: 1;
        display: flex;
        justify-content: center;

        .preview {
          overflow: hidden;
          background: #eeeeee;
          border: 1px solid #eeeeee;
        }

        img {
          width: 100%;
          height: 100%;
        }
      }
    }
  }

  .scopeButton {
    width: 600px;
    display: flex;
    justify-content: space-between;

    :deep(.el-button--primary:first-child ){
      padding: 0;
    }
  }

  .uploadButton {
    :deep(.el-button-group ){
      display: flex;
    }

    :deep(.el-button--primary:last-child ){
      padding: 0;
      border-right-color: none;
    }
  }

  .inputFile {
    width: 180px;
    position: absolute;
    clip: rect(0 0 0 0);
  }
}

.demo::-webkit-scrollbar {
  display: none;
  /* Chrome Safari */
}

.demo {
  scrollbar-width: none;
  /* firefox */
  -ms-overflow-style: none;
  /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
}

.temp {
  height: 0;
  margin-bottom: 0;
  border: none;
  padding: 0;
}

.selectTreeClass {
  background: #d5e8fc;
}

.treeBox {
  width: 100%;
  height: 100%;
}

.upload-demo {
  display: inline-block !important;
  float: left;
}

.tree_w {
  padding: 20px 30px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
  padding-right: 8px;
}

.custom-tree-node-label {
  display: block;
  width: 125px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-weight: 400;
  font-size: 13px;
  color: #303133;
}

.el-ic {
  i,
  span {
    /*padding: 0 14px;*/
    font-size: 18px;
    font-weight: 600;
  }

  .svg-icon {
    color: #4386c6;
  }

  .el-icon-more {
    color: #909399;
    font-size: 12px;
    // font-weight: 300;
  }

  .el-icon-plus {
    font-size: 12px;
    color: var(--prev-color-primary);
  }
}

.el-dialog__body {
  .upload-container .image-preview .image-preview-wrapper img {
    height: 100px;
  }

  .el-dialog .el-collapse-item__wrap {
    padding-top: 0px;
  }

  .spatial_img {
    .el-collapse-item__wrap {
      margin-bottom: 0;
      padding-top: 0px;
    }
  }

  .upload-container .image-preview .image-preview-wrapper {
    width: 120px;
  }

  .upload-container .image-preview .image-preview-action {
    line-height: 100px;
    height: 100px;
  }
}

.trees-coadd {
  width: 100%;
  border-radius: 4px;
  overflow: hidden;
  position: relative;

  .scollhide {
    overflow-x: hidden;
    overflow-y: scroll;
    padding: 0 0 10px 0;
    box-sizing: border-box;

    // 树状图穿透样式
    .trees {
      width: 100%;
    }
  }

  .scollhide::-webkit-scrollbar {
    display: none;
  }
}

.conters {
  width: 100%;
  // display: flex;
  flex-wrap: wrap;
  /*max-height: 296px;*/
  overflow: auto;

  position: relative;
  padding-bottom: 10px;
}

.gridPic {
  margin-left: 17px;
  position: relative;
  cursor: pointer;

  img {
    display: block;
    background: #eee;
  }

  video {
    width: 100%;
    height: 95%;
    display: block;
  }

  .num_badge {
    position: absolute;
    bottom: 25px;
    display: inline-block;
    min-width: 20px;
    height: 20px;
    border-radius: 10px;
    background: var(--prev-color-primary);
    font-size: 14px;
    text-align: center;
    color: #fff;
    line-height: 20px;
  }

  .operate {
    position: absolute;
    // bottom: -15px;
    left: 0;
    font-size: 12px;

    span {
      font-weight: 400;
      color: var(--prev-color-primary) !important;
    }
  }
}

.conter {
  width: 99%;
  height: 100%;

  .bnt {
    width: 100%;
    padding: 0 13px 0 0;
    box-sizing: border-box;
    display: flex;
    position: relative;

    :deep(.el-button + .el-button ){
      margin-left: 0;
    }

    .input {
      :deep(.el-input__inner ){
        width: 220px;
        height: 32px;
      }
    }

    :deep(.el-select) {
      
        width: 160px;
        height: 32px;
      
    }
  }

  .pictrueList {
    /*padding-left: 15px;*/
    width: 100%;

    el-image {
      width: 100%;
      border: 2px solid #fff;
    }

    .on {
      border: 2px solid var(--prev-color-primary);
    }
  }

  .el-image {
    width: 110px;
    height: 110px;
    cursor: pointer;
  }

  .imagesNo {
    width: 100%;
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    margin: 65px 0;

    .imagesNo_sp {
      font-size: 13px;
      color: #dbdbdb;
      line-height: 3;
    }
  }
}

.relative {
  position: relative;
}

.abs_video {
  position: absolute;
  right: 20px;
  top: 0;
}

.imgTitle {
  margin-top: 6px;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.input {
  display: flex;
  position: absolute;
  right: 0;
}

.el-col.el-col-24.el-col-xs-24.el-col-sm-7 {
  border-right: 1px solid #eee;
}

.checkoutBtn {
  display: flex;
  width: 80px;
  height: 32px;
  border-radius: 4px !important;
  border: 1px solid #ddd;
  cursor: pointer;
  margin-left: 14px;

  .checkoutBtn-gg {
    width: 50%;
    height: 100%;
    text-align: center;
    line-height: 32px;
    border-right: 1px solid #ddd;
  }

  .checkoutBtn-list {
    width: 50%;
    height: 100%;
    text-align: center;
    line-height: 32px;
  }

  .checkoutActive {
    background: var(--prev-color-primary);
    color: #fff;
  }
}

.block {
  width: 100%;

  .el-pagination {
    float: right;
  }
}

.listImg {
  width: 30px;
  height: 30px;
  vertical-align: middle;
}

.imgDiv {
  display: flex;

  .imgName {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    margin-left: 20px;
    line-height: 40px;
  }
}

.decollator {
  color: #dddddd;
  margin: 0 8px;
}

.checkDialog-box {
  display: flex;
  justify-content: space-between;
}

.checkDialog-box > div div {
  margin-top: 50px;
}

.checkDialog-box span {
  font-weight: 600;
}

.el-divider__text,
.el-link {
  font-size: 12px;
}

:deep(.rightBtn) {
  text-align: right;
}

:deep(.el-dropdown-menu__item:not(.is-disabled):hover) {
  background-color: var(--prev-color-primary-light-9) !important;
  color: var(--prev-color-primary);
}

:deep(.el-tree-node__content:hover) {
  background-color: var(--prev-color-primary-light-9) !important;
  color: var(--prev-color-primary);
}
:deep(.el-tabs__nav-scroll) {
  margin-top: -20px;
}

:deep(.el-tree-node__content:hover) {
  background-color: var(--prev-color-primary-light-9) !important;
  color: var(--prev-color-primary);
}

:deep(.el-tree-node .custom-tree-node) {
  height: 40px;
  line-height: 40px;
  color: #303133;
}

:deep(.el-tree-node__content) {
  height: 40px;
  line-height: 40px;
  color: #303133;
}

:deep(.el-tree-node:focus > .el-tree-node__content) {
  background-color: var(--prev-color-primary-light-9) !important;

  .custom-tree-node-label {
    color: var(--prev-color-primary);
  }

  .el-icon-more {
    color: var(--prev-color-primary);
  }
}
</style>
