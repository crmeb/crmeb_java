<template>
  <div class="divBox">
    <pages-header
      ref="pageHeader"
      :title="$route.params.id ? (isDisabled ? '商品详情' : '编辑商品') : '添加商品'"
      backUrl="/store/index"
    ></pages-header>
    <el-card v-if="isCopy" class="mt14" shadow="never" :bordered="false">
      <div class="line-ht mb15">
        <div v-if="copyConfigError" style="color: #f56c6c; margin-bottom: 8px;">{{ copyConfigError }}</div>
        生成的商品默认是没有上架的，请手动上架商品！
        <span v-if="copyConfig.copyType && copyConfig.copyType == 1"
          >您当前剩余{{ copyConfig.copyNum }}条采集次数。
        </span>
        <div class="tips-bottom">
          商品采集设置：设置 > 系统设置 > 第三方接口设置 >
          采集商品配置（如配置一号通采集，请先登录一号通账号，无一号通，请选择99Api设置）
        </div>
      </div>
      <div :span="24" v-if="copyConfig.copyType">
        <el-input v-model.trim="url" placeholder="请输入链接地址" class="selWidth100">
          <template #append>
            <el-button :icon="Search" @click="addProduct" />
          </template>
        </el-input>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-tabs class="list-tabs" v-model="currentTab" @tab-click="tabsHandleClick">
        <el-tab-pane label="商品信息" name="0"></el-tab-pane>
        <el-tab-pane label="规格库存" name="1"></el-tab-pane>
        <el-tab-pane label="商品详情" name="2"></el-tab-pane>
        <el-tab-pane label="其他设置" name="3"></el-tab-pane>
      </el-tabs>
      <el-form
        ref="formValidateRef"
        v-loading="fullscreenLoading"
        class="formValidate mt20"
        :rules="ruleValidate"
        :model="formValidate"
        label-width="90px"
        @submit.prevent
      >
        <el-row v-show="currentTab == 0" :gutter="24">
          <!-- 商品信息-->
          <el-col v-bind="grid2">
            <el-form-item label="商品名称：" prop="storeName">
              <el-input
                class="from-ipt-width"
                v-model="formValidate.storeName"
                maxlength="30"
                show-word-limit
                placeholder="请输入商品名称"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品分类：" prop="cateIds">
              <el-cascader
                class="from-ipt-width"
                v-model="formValidate.cateIds"
                :options="merCateList"
                :props="props2"
                clearable
                :show-all-levels="false"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品关键字：" prop="keyword">
              <el-input
                class="from-ipt-width"
                v-model="formValidate.keyword"
                placeholder="请输入商品关键字"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="单位：" prop="unitName">
              <el-input
                class="from-ipt-width"
                v-model="formValidate.unitName"
                placeholder="请输入单位"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品封面图：" prop="image">
              <div class="acea-row upLoadPicBox row-middle">
                <div v-if="formValidate.image" class="pictrue" @click="modalPicTap('1')">
                  <el-image
                    class="image"
                    :src="formValidate.image"
                    :preview-src-list="isDisabled ? [formValidate.image] : []" preview-teleported
                  >
                  </el-image>
                </div>
                <div v-else class="upLoad" @click="modalPicTap('1')">
                  <i class="el-icon-camera cameraIconfont" />
                </div>
              </div>
              <div class="from-tips" v-show="!isDisabled">建议尺寸：800*800px，上传小于500kb的图片</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品轮播图：" prop="sliderImages">
              <div class="acea-row">
                <div
                  v-for="(item, index) in formValidate.sliderImages"
                  :key="index"
                  class="pictrue"
                  draggable="true"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnter($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >
                  <el-image class="image" :src="item" :preview-src-list="formValidate.sliderImages" preview-teleported> </el-image>
                  <i v-if="!isDisabled" class="el-icon-error btndel" @click="handleRemove(index)" />
                </div>
                <div
                  v-if="formValidate.sliderImages.length < 10 && !isDisabled"
                  class="upLoadPicBox"
                  @click="modalPicTap('2')"
                >
                  <div class="upLoad">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </div>
            </el-form-item>
            <el-form-item label="保障服务：" prop="guarantee">
              <el-select
                v-model="guaranteeIdsList"
                multiple
                collapse-tags
                placeholder="请选择"
                class="from-ipt-width"
                @change="updateGuaranteeIds"
                :disabled="isDisabled"
              >
                <el-option v-for="item in guaranteeList" :key="item.id" :label="item.name" :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="主图视频：">
              <el-input
                class="from-ipt-width mr15"
                maxlength="250"
                v-model="videoLink"
                :disabled="isDisabled"
                placeholder="请输入视频链接"
              />
              <el-button v-if="videoLink" @click="zh_uploadFile" :disabled="isDisabled">确认添加</el-button>
              <el-button v-if="!videoLink" :disabled="isDisabled" @click="modalPicTap('3', '', '', 'video')"
                >选择视频</el-button
              >
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <div class="iview-video-style" v-if="formValidate.videoLink">
              <video
                style="width: 100%; height: 100% !important; border-radius: 10px"
                :src="formValidate.videoLink"
                controls
                autoplay
                muted
              >
                您的浏览器不支持 video 标签。
              </video>
              <div class="mark"></div>
              <span class="iconv iconfont iconmd-trash" @click="delVideo()"></span>
            </div>
          </el-col>
          <el-col>
            <el-form-item label="运费模板：" prop="tempId">
              <el-select
                class="from-ipt-width mr15"
                v-model="formValidate.tempId"
                placeholder="请选择"
                :disabled="isDisabled"
                style="width: 100%"
              >
                <el-option v-for="item in shippingList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
              <el-button v-show="!isDisabled" class="mr15" @click="addTem">运费模板</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        <creatAttr
          v-if="currentTab == 1"
          v-model="formValidate"
          :oneFormBatch="oneFormBatch"
          :isDisabled="isDisabled"
          :formThead="formThead"
          :manyTabDate="manyTabDate"
          :OneattrValue="OneattrValue"
          :ManyAttrValue="ManyAttrValue"
          :manyTabTit="manyTabTit"
          @changeManyAttrValue="changeManyAttrValue"
          @handleBatchDel="handleBatchDel"
        ></creatAttr>
        <!-- 商品详情-->
        <el-row v-show="currentTab == 2 && !isDisabled">
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <WangEditor v-model.trim="formValidate.content" :key="htmlKey"></WangEditor>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-show="currentTab == 2 && isDisabled">
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <span v-html="formValidate.content || '无'"></span>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 其他设置-->
        <el-row v-show="currentTab == 3">
          <el-col :span="24">
            <el-col v-bind="grid">
              <el-form-item label="排序：">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.sort"
                  :min="0"
                  placeholder="请输入排序"
                  :disabled="isDisabled"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid">
              <el-form-item label="积分：">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.giveIntegral"
                  :min="0"
                  placeholder="请输入排序"
                  :disabled="isDisabled"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid">
              <el-form-item label="虚拟销量：">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.ficti"
                  :min="0"
                  placeholder="请输入排序"
                  :disabled="isDisabled"
                />
              </el-form-item>
            </el-col>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品推荐：">
              <el-checkbox-group v-model="checkboxGroup" @change="onChangeGroup" :disabled="isDisabled">
                <el-checkbox v-for="(item, index) in recommend" :key="index" :label="item.value" :value="item.value">{{
                  item.name
                }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动优先级：">
              <div class="color-list acea-row row-middle">
                <div
                  :disabled="isDisabled"
                  class="color-item"
                  :class="activity[item]"
                  v-for="item in formValidate.activity"
                  :key="item"
                  draggable="true"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnterFont($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >
                  {{ item }}
                </div>
                <div class="tip">可拖动按钮调整活动的优先展示顺序</div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="赠送优惠券：" class="proCoupon">
              <div>
                <el-tag
                  v-for="(tag, index) in formValidate.coupons"
                  :key="index"
                  class="mr10"
                  :closable="!isDisabled"
                  :disable-transitions="false"
                  @close="handleCloseCoupon(tag)"
                >
                  {{ tag.name }}
                </el-tag>
                <span class="mr10" v-if="formValidate.couponIds == null">暂无优惠券</span>
                <el-button v-if="!isDisabled" class="mr15" @click="addCoupon">选择优惠券</el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button v-show="Number(currentTab) > 0" class="submission" @click="handleSubmitUp">上一步</el-button>
          <el-button
            v-show="Number(currentTab) < 3"
            class="submission"
            :class="Number(currentTab) == 0 ? 'onePrimary' : ''"
            @click="handleSubmitNest('formValidate')"
            >下一步</el-button
          >
          <el-button v-show="!isDisabled" type="primary" class="submission" @click="handleSubmit('formValidate')"
            >提交</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
    <CreatTemplates ref="addTemplatesRef" @getList="getShippingList" />
  </div>
</template>

<script setup lang="jsx">
import { reactive, ref, computed, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { useRoute, useRouter } from 'vue-router';
import { useTagsViewStore } from '@/store/modules/tagsView';
import WangEditor from '@/components/wangEditor/index.vue';
import {
  templateListApi,
  productCreateApi,
  categoryApi,
  productDetailApi,
  productUpdateApi,
  guaranteeListApi,
  copyConfigApi,
  copyProductApi,
} from '@/api/store';
import { marketingSendApi } from '@/api/marketing';
import { shippingTemplatesList } from '@/api/logistics';
import { goodDesignList } from '@/api/systemGroup';
import { arraysEqual } from '@/utils';
import { clearTreeData } from '@/utils/ZBKJIutil';
import CreatTemplates from '@/views/systemSetting/deliverGoods/freightSet/creatTemplates';
import creatAttr from '../components/creatAttr';
import Templates from '../../appSetting/wxAccount/wxTemplate/index';
import { Debounce } from '@/utils/validate';
import { Search } from '@element-plus/icons-vue';

defineOptions({ name: 'SortCreat' });

const { proxy } = getCurrentInstance();
const route = useRoute();
const router = useRouter();
const tagsViewStore = useTagsViewStore();
const isCopy = computed(() => route.query.isCopy === '1' || route.params.isCopy === '1');

const defaultObj = {
  image: '',
  sliderImages: [],
  videoLink: '',
  sliderImage: '',
  storeName: '',
  keyword: '',
  cateIds: [], // 商品分类id
  cateId: null, // 商品分类id传值
  unitName: '',
  sort: 0,
  giveIntegral: 0,
  ficti: 0,
  isShow: false,
  isBenefit: false,
  isNew: false,
  isGood: false,
  isHot: false,
  isBest: false,
  tempId: '',
  attrValue: [
    {
      image: '',
      price: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      barCode: '',
      weight: 0,
      volume: 0,
    },
  ],
  attr: [],
  selectRule: '',
  isSub: false,
  content: '',
  specType: false,
  id: 0,
  couponIds: [],
  coupons: [],
  guaranteeIds: '', // 服务保障id字符串
  activity: ['默认', '秒杀', '砍价', '拼团'],
};
const objTitle = {
  price: {
    title: '售价',
  },
  cost: {
    title: '成本价',
  },
  otPrice: {
    title: '原价',
  },
  stock: {
    title: '库存',
  },
  barCode: {
    title: '商品编号',
  },
  weight: {
    title: '重量（KG）',
  },
  volume: {
    title: '体积(m³)',
  },
};

const htmlKey = ref(0);
const isDisabled = ref(route.params.isDisabled === '1' ? true : false);
const activity = { 默认: 'red', 秒杀: 'blue', 砍价: 'green', 拼团: 'yellow' };
const props2 = {
  children: 'child',
  label: 'name',
  value: 'id',
  multiple: true,
  emitPath: false,
};
const checkboxGroup = ref([]);
const recommend = ref([]);
const tabs = ref([]);
const fullscreenLoading = ref(false);
const props = { multiple: true };
const active = ref(0);
const OneattrValue = ref([Object.assign({}, defaultObj.attrValue[0])]); // 单规格
const ManyAttrValue = ref([Object.assign({}, defaultObj.attrValue[0])]); // 多规格
const ruleList = ref([]);
const merCateList = ref([]); // 商品分类筛选
const shippingList = ref([]); // 运费模板
const formThead = ref(Object.assign({}, objTitle));
const formValidate = reactive(Object.assign({}, defaultObj));
const formDynamics = reactive({
  ruleName: '',
  ruleValue: [],
});
const tempData = {
  page: 1,
  limit: 9999,
};
const manyTabTit = ref({});
const manyTabDate = ref({});
const grid2 = {
  xl: 24,
  lg: 24,
  md: 24,
  sm: 24,
  xs: 24,
};
// 规格数据
const formDynamic = reactive({
  attrsName: '',
  attrsVal: '',
});
const isBtn = ref(false);
const manyFormValidate = ref([]);
const currentTab = ref('0');
const isChoice = ref('');
const grid = {
  xl: 24,
  lg: 24,
  md: 24,
  sm: 24,
  xs: 24,
};
const ruleValidate = {
  storeName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { max: 30, message: '商品名称长度不能超过30个字', trigger: 'blur' },
  ],
  cateIds: [{ required: true, message: '请选择商品分类', trigger: 'change', type: 'array', min: '1' }],
  keyword: [{ required: true, message: '请输入商品关键字', trigger: 'blur' }],
  unitName: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  tempId: [{ required: true, message: '请选择运费模板', trigger: 'change' }],
  image: [{ required: true, message: '请上传商品图', trigger: 'change' }],
  sliderImages: [{ required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }],
  specType: [{ required: true, message: '请选择商品规格', trigger: 'change' }],
};
const attrInfo = ref({});
const tableFrom = reactive({
  page: 1,
  limit: 9999,
  keywords: '',
});
const tempRoute = ref({});
const keyNum = ref(0);
const isAttr = ref(false);
const showAll = ref(false);
const videoLink = ref('');
const copyConfig = ref({});
const copyConfigError = ref('');
const url = ref('');
const guaranteeList = ref([]); // 服务保障列表
const guaranteeIdsList = ref([]); // 服务保障选择id列表
// 批量添加数据
const oneFormBatch = ref([Object.assign({}, defaultObj.attrValue[0])]);
// 表单 ref
const formValidateRef = ref(null);
const addTemplatesRef = ref(null);
// 原 methods 中引用但未声明的变量（保留原逻辑）
const loading = ref(false);
const dragging = ref(null);

const attrValue = computed(() => {
  const obj = Object.assign({}, defaultObj.attrValue[0]);
  delete obj.image;
  return obj;
});

function parseCategoryIds(cateId) {
  if (cateId === null || cateId === undefined || cateId === '') return [];
  const ids = Array.isArray(cateId) ? cateId : String(cateId).split(',');
  return ids.map((id) => Number(String(id).trim())).filter((id) => Number.isInteger(id) && id > 0);
}

// created 逻辑
tempRoute.value = Object.assign({}, route);
if (route.params.id && formValidate.specType) {
  // this.$watch('formValidate.attr', this.watCh);
}
// 获取服务保障列表
getGuaranteeList();

function addProduct() {
  if (url.value) {
    loading.value = true;
    copyConfig.value.copyType == 1
      ? copyProductApi({ url: url.value })
          .then((res) => {
            let info = res;
            Object.assign(formValidate, {
              image: proxy.$selfUtil.setDomain(info.image),
              sliderImage: info.sliderImage,
              storeName: info.storeName,
              keyword: info.keyword,
              cateIds: parseCategoryIds(info.cateId), // 商品分类id
              cateId: info.cateId, // 商品分类id传值
              unitName: info.unitName,
              sort: 0,
              isShow: 0,
              isBenefit: false,
              isNew: false,
              isGood: false,
              isHot: false,
              isBest: false,
              tempId: info.tempId,
              attrValue: info.attrValue,
              attr: info.attr || [],
              selectRule: info.selectRule,
              isSub: false,
              content: proxy.$selfUtil.replaceImgSrcHttps(info.content),
              specType: info.attr.length ? true : false,
              id: info.id,
              giveIntegral: info.giveIntegral,
              ficti: info.ficti,
              activity: ['默认', '秒杀', '砍价', '拼团'],
            });
            if (info.specType) {
              // 设置多规格商品属性数据
              generateManyAttr();
            } else {
              OneattrValue.value = info.attrValue;
            }
            if (info.isHot) checkboxGroup.value.push('isHot');
            if (info.isGood) checkboxGroup.value.push('isGood');
            if (info.isBenefit) checkboxGroup.value.push('isBenefit');
            if (info.isBest) checkboxGroup.value.push('isBest');
            if (info.isNew) checkboxGroup.value.push('isNew');
            let imgs = JSON.parse(info.sliderImage);
            let imgss = [];
            Object.keys(imgs).map((i) => {
              imgss.push(proxy.$selfUtil.setDomain(imgs[i]));
            });
            formValidate.sliderImages = imgss;
            if (formValidate.attr.length) {
              oneFormBatch.value[0].image = proxy.$selfUtil.setDomain(info.image);
              oneFormBatch.value[0].brokerage = 0; // 设置采集商品的默认一级佣金
              oneFormBatch.value[0].brokerageTwo = 0; // 设置采集商品的默认二级佣金
              for (var i = 0; i < formValidate.attr.length; i++) {
                formValidate.attr[i].attrValue = JSON.parse(formValidate.attr[i].attrValues);
              }
            }
            loading.value = false;
          })
          .catch(() => {
            loading.value = false;
          })
      : importProductApi({ url: url.value, form: form })
          .then((res) => {
            Object.assign(formValidate, {
              image: proxy.$selfUtil.setDomain(res.image),
              sliderImage: res.sliderImage,
              storeName: res.storeName,
              keyword: res.keyword,
              cateIds: parseCategoryIds(res.cateId), // 商品分类id
              cateId: res.cateId, // 商品分类id传值
              unitName: res.unitName,
              sort: 0,
              isShow: 0,
              isBenefit: false,
              isNew: false,
              isGood: false,
              isHot: false,
              isBest: false,
              tempId: res.tempId,
              attrValue: res.attrValue,
              attr: res.attr || [],
              selectRule: res.selectRule,
              isSub: false,
              content: res.content,
              specType: res.attr.length ? true : false,
              id: res.id,
              giveIntegral: res.giveIntegral,
              ficti: res.ficti,
              activity: ['默认', '秒杀', '砍价', '拼团'],
            });
            if (info.specType) {
              // 设置多规格商品属性数据
              generateManyAttr();
            } else {
              OneattrValue.value = info.attrValue;
              // this.formValidate.attr = [] //单规格商品规格设置为空
            }
            let imgs = JSON.parse(res.sliderImage);
            let imgss = [];
            Object.keys(imgs).map((i) => {
              imgss.push(proxy.$selfUtil.setDomain(imgs[i]));
            });
            formValidate.sliderImages = imgss;
            if (formValidate.attr.length) {
              oneFormBatch.value[0].image = proxy.$selfUtil.setDomain(res.image);
              for (var i = 0; i < formValidate.attr.length; i++) {
                formValidate.attr[i].attrValue = JSON.parse(formValidate.attr[i].attrValues);
              }
            }
            loading.value = false;
          })
          .catch(() => {
            loading.value = false;
          });
  } else {
    ElMessage.warning('请输入链接地址！');
  }
}
function getCopyConfig() {
  copyConfigApi()
    .then((res) => {
      copyConfig.value = res;
      copyConfigError.value = '';
    })
    .catch((err) => {
      copyConfigError.value = (err && err.message) || '获取配置失败';
    });
}
function tabsHandleClick(tab, event) {
  currentTab.value = tab.name;
}
function keyupEvent(key, val, index, num) {
  var re = /([0-9]+.[0-9]{2})[0-9]*/;
  switch (num) {
    case 1:
      oneFormBatch.value[index][key] =
        key === 'stock' ? parseInt(val) : (oneFormBatch.value[index][key] = String(val).replace(re, '$1'));
      break;
    case 2:
      OneattrValue.value[index][key] =
        key === 'stock' ? parseInt(val) : (OneattrValue.value[index][key] = String(val).replace(re, '$1'));
      break;
    default:
      ManyAttrValue.value[index][key] =
        key === 'stock' ? parseInt(val) : (ManyAttrValue.value[index][key] = String(val).replace(re, '$1'));
      break;
  }
}
function handleCloseCoupon(tag) {
  isAttr.value = true;
  formValidate.coupons.splice(formValidate.coupons.indexOf(tag), 1);
  formValidate.couponIds.splice(formValidate.couponIds.indexOf(tag.id), 1);
}
function addCoupon() {
  proxy.$modalCoupon(
    'wu',
    (keyNum.value += 1),
    formValidate.coupons,
    function (row) {
      formValidate.couponIds = [];
      formValidate.coupons = row;
      row.map((item) => {
        formValidate.couponIds.push(item.id);
      });
    },
    '',
  );
}
function setTagsViewTitle() {
  const title = isDisabled.value ? '商品详情' : '编辑商品';
  const routeObj = Object.assign({}, tempRoute.value, { title: `${title}-${route.params.id}` });
  tagsViewStore.updateVisitedView(routeObj);
}
function onChangeGroup() {
  checkboxGroup.value.includes('isGood') ? (formValidate.isGood = true) : (formValidate.isGood = false);
  checkboxGroup.value.includes('isBenefit')
    ? (formValidate.isBenefit = true)
    : (formValidate.isBenefit = false);
  checkboxGroup.value.includes('isBest') ? (formValidate.isBest = true) : (formValidate.isBest = false);
  checkboxGroup.value.includes('isNew') ? (formValidate.isNew = true) : (formValidate.isNew = false);
  checkboxGroup.value.includes('isHot') ? (formValidate.isHot = true) : (formValidate.isHot = false);
}
// 运费模板
function addTem() {
  addTemplatesRef.value.dialogVisible = true;
  addTemplatesRef.value.getCityList();
}
// 商品分类；
function getCategorySelect() {
  categoryApi({ status: -1, type: 1 }).then((res) => {
    merCateList.value = addDisabled(res);
  });
}
//限制商品分类只能选择开启的
function addDisabled(dropdownList) {
  const list = [];
  try {
    dropdownList.forEach((e, index) => {
      let e_new = {
        id: e.id,
        name: e.name,
        level: e.level,
        pid: e.pid,
        sort: e.sort,
        status: e.status,
      };
      if (!e.status) {
        e_new = { ...e_new, disabled: true };
      }
      if (e.child) {
        const childList = addDisabled(e.child);
        e_new = { ...e_new, child: childList };
      }
      list.push(e_new);
    });
  } catch (error) {
    console.log(error);
    return [];
  }
  return list;
}
function filerMerCateList(treeData) {
  return treeData.map((item) => {
    if (!item.child) {
      item.disabled = true;
    }
    item.label = item.name;
    return item;
  });
}
// 获取商品属性模板；
function productGetRule() {
  templateListApi(tableFrom).then((res) => {
    const list = res.list;
    for (var i = 0; i < list.length; i++) {
      list[i].ruleValue = JSON.parse(list[i].ruleValue);
    }
    ruleList.value = list;
  });
}
// 运费模板；
function getShippingList() {
  shippingTemplatesList(tempData).then((res) => {
    shippingList.value = res.list;
  });
}
// 详情
function getInfo() {
  fullscreenLoading.value = true;
  productDetailApi(route.params.id)
    .then(async (res) => {
      // this.isAttr = true;
      let info = res;
      Object.assign(formValidate, {
        image: proxy.$selfUtil.setDomain(info.image),
        sliderImage: info.sliderImage,
        sliderImages: JSON.parse(info.sliderImage),
        storeName: info.storeName,
        keyword: info.keyword,
        cateIds: parseCategoryIds(info.cateId), // 商品分类id
        cateId: info.cateId, // 商品分类id传值
        unitName: info.unitName,
        sort: info.sort,
        isShow: info.isShow,
        isBenefit: info.isBenefit,
        isNew: info.isNew,
        isGood: info.isGood,
        isHot: info.isHot,
        isBest: info.isBest,
        tempId: info.tempId,
        attr: info.attr,
        attrValue: info.attrValue,
        selectRule: info.selectRule,
        isSub: info.isSub,
        content: info.content ? proxy.$selfUtil.replaceImgSrcHttps(info.content) : '',
        specType: info.specType,
        id: info.id,
        giveIntegral: info.giveIntegral,
        ficti: info.ficti,
        coupons: info.coupons,
        couponIds: info.couponIds,
        activity: info.activity ? info.activity : ['默认', '秒杀', '砍价', '拼团'],
      });
      // 获取服务保障被选id列表
      getGuranteeIdsList(info.guaranteeList);
      marketingSendApi({ type: 3 }).then((res) => {
        if (formValidate.couponIds !== null) {
          let ids = formValidate.couponIds.toString();
          let arr = res.list;
          let obj = {};
          for (let i in arr) {
            obj[arr[i].id] = arr[i];
          }
          let strArr = ids.split(',');
          let newArr = [];
          for (let item of strArr) {
            if (obj[item]) {
              newArr.push(obj[item]);
            }
          }
          formValidate.coupons = newArr; //在编辑回显时，让返回数据中的优惠券id，通过接口匹配显示,
        }
      });
      let imgs = JSON.parse(info.sliderImage);
      let imgss = [];
      Object.keys(imgs).map((i) => {
        imgss.push(proxy.$selfUtil.setDomain(imgs[i]));
      });
      formValidate.sliderImages = [...imgss];
      if (getFileType(formValidate.sliderImages[0]) == 'video') {
        //如果返回数据轮播图的第一张是视频，就将其赋值给videoLink做渲染，同时将其在轮播图中删除
        formValidate.videoLink = formValidate.sliderImages[0];
        formValidate.sliderImages.splice(0, 1);
      }
      if (info.isHot) checkboxGroup.value.push('isHot');
      if (info.isGood) checkboxGroup.value.push('isGood');
      if (info.isBenefit) checkboxGroup.value.push('isBenefit');
      if (info.isBest) checkboxGroup.value.push('isBest');
      if (info.isNew) checkboxGroup.value.push('isNew');
      productGetRule();
      if (info.specType) {
        // 设置多规格商品属性数据
        generateManyAttr();
      } else {
        OneattrValue.value = info.attrValue;
        // this.formValidate.attr = [] //单规格商品规格设置为空
      }
      fullscreenLoading.value = false;
    })
    .catch((res) => {
      fullscreenLoading.value = false;
      ElMessage.error(res.message);
    });
}
function handleRemove(i) {
  formValidate.sliderImages.splice(i, 1);
}
// 点击商品图
function modalPicTap(tit, num, i, status) {
  if (isDisabled.value) return;
  proxy.$modalUpload(
    function (img) {
      if (tit === '1' && !num) {
        formValidate.image = img[0].sattDir;
        OneattrValue.value[0].image = img[0].sattDir;
      }
      if (tit === '2' && !num) {
        if (img.length > 10) return ElMessage.warning('最多选择10张图片！');
        if (img.length + formValidate.sliderImages.length > 10)
          return ElMessage.warning('最多选择10张图片！');
        img.map((item) => {
          formValidate.sliderImages.push(item.sattDir);
        });
      }
      if (tit === '3' && status === 'video') {
        let videoInfo = img[0];
        if (videoInfo.attType !== 'video/mp4') {
          ElMessage.warning('请重新选择视频！');
        } else {
          formValidate.videoLink = videoInfo.sattDir;
        }
      }
      if (tit === '1' && num === 'dan') {
        OneattrValue.value[0].image = img[0].sattDir;
      }
      if (tit === '1' && num === 'duo') {
        ManyAttrValue.value[i].image = img[0].sattDir;
      }
      if (tit === '1' && num === 'pi') {
        oneFormBatch.value[0].image = img[0].sattDir;
      }
    },
    tit,
    'content',
  );
}
function handleSubmitUp() {
  if (currentTab.value-- < 0) currentTab.value = 0;
  currentTab.value = currentTab.value.toString();
}
function handleSubmitNest(name) {
  formValidateRef.value.validate((valid) => {
    if (valid) {
      if (currentTab.value++ > 3) currentTab.value = 0;
      currentTab.value = currentTab.value.toString();
    } else {
      if (
        !formValidate.store_name ||
        !formValidate.cate_id ||
        !formValidate.keyword ||
        !formValidate.unit_name ||
        !formValidate.store_info ||
        !formValidate.image ||
        !formValidate.slider_image
      ) {
        ElMessage.warning('请填写完整商品信息！');
      }
    }
  });
}
//提交接口数据更新
function getFromData() {
  if (formValidate.specType && formValidate.attr.length < 1)
    return ElMessage.warning('请填写多规格属性！');
  formValidate.cateId = formValidate.cateIds.join(',');
  if (formValidate.videoLink) {
    //如果有视频主图，将视频链接插入到轮播图第一的位置
    formValidate.sliderImages.unshift(formValidate.videoLink);
  }
  formValidate.sliderImage = JSON.stringify(formValidate.sliderImages);
  if (formValidate.specType) {
    formValidate.attrValue = ManyAttrValue.value.slice(1);
    formValidate.attr = formValidate.attr.map((item) => {
      return {
        attrName: item.attrName,
        id: item.id,
        attrValues: item.optionList.map((val) => val.value).join(','),
        isShowImage: item.isShowImage || false,
        optionList: item.optionList || [{ value: '默认' }],
      };
    });
    if (typeof formValidate.attrValue[0].attrValue == 'object') {
      formValidate.attrValue.forEach((item) => {
        item.attrValue = JSON.stringify(item.attrValue);
      });
    }
    // 如果不是采集商品
    if (!isCopy.value) {
      for (var i = 0; i < formValidate.attrValue.length; i++) {
        formValidate.attrValue[i].id = 0;
        formValidate.attrValue[i].productId = 0;
        let attrValues = formValidate.attrValue[i].attrValue;
        // this.$set(this.formValidate.attrValue[i], 'attrValue', JSON.stringify(attrValues));
        delete formValidate.attrValue[i].value0;
      }
    }
  } else {
    formValidate.attr = [
      {
        attrName: '规格',
        attrValues: '默认',
        id: route.params.id ? formValidate.attr[0].id : 0,
        isShowImage: false,
        optionList: [{ value: '默认' }],
      },
    ];
    OneattrValue.value.map((item) => {
      item.attrValue = JSON.stringify({ 规格: '默认' });
      // 如果佣金设置为默认
      if (!formValidate.isSub) {
        item.brokerage = 0;
        item.brokerageTwo = 0;
      }
      //this.$set(item, 'productId', 0);
    });
    formValidate.attrValue = OneattrValue.value;
  }
}
// 提交
const handleSubmit = Debounce(function (name) {
  onChangeGroup();
  getFromData();
  formValidateRef.value.validate((valid) => {
    if (valid) {
      fullscreenLoading.value = true;
      route.params.id
        ? productUpdateApi(formValidate)
            .then(async (res) => {
              ElMessage.success('编辑成功');
              setTimeout(() => {
                router.push({ path: '/store/index' });
              }, 500);
              fullscreenLoading.value = false;
            })
            .catch((res) => {
              fullscreenLoading.value = false;
              restoreData();
              if (formValidate.specType) ManyAttrValue.value = formValidate.attrValue;
            })
        : productCreateApi(formValidate)
            .then(async (res) => {
              ElMessage.success('新增成功');
              setTimeout(() => {
                router.push({ path: '/store/index' });
              }, 500);
              fullscreenLoading.value = false;
            })
            .catch((res) => {
              fullscreenLoading.value = false;
              restoreData();
            });
    } else {
      if (
        !formValidate.storeName ||
        !formValidate.cateId ||
        !formValidate.keyword ||
        !formValidate.unitName ||
        !formValidate.image ||
        !formValidate.sliderImages
      ) {
        ElMessage.warning('请填写完整商品信息！');
      }
    }
  });
});
// 提交失败之后恢复数据
function restoreData() {
  for (var i = 0; i < formValidate.attrValue.length; i++) {
    let attrValues = formValidate.attrValue[i].attrValue;
    formValidate.attrValue[i].attrValue = JSON.parse(attrValues);
  }
}
// 表单验证
function validate(prop, status, error) {
  if (status === false) {
    ElMessage.warning(error);
  }
}
// 移动
function handleDragStart(e, item) {
  if (!isDisabled.value) dragging.value = item;
}
function handleDragEnd(e, item) {
  if (!isDisabled.value) dragging.value = null;
}
function handleDragOver(e) {
  if (!isDisabled.value) e.dataTransfer.dropEffect = 'move';
}
function handleDragEnter(e, item) {
  if (!isDisabled.value) {
    e.dataTransfer.effectAllowed = 'move';
    if (item === dragging.value) {
      return;
    }
    const newItems = [...formValidate.sliderImages];
    const src = newItems.indexOf(dragging.value);
    const dst = newItems.indexOf(item);
    newItems.splice(dst, 0, ...newItems.splice(src, 1));
    formValidate.sliderImages = newItems;
  }
}
function handleDragEnterFont(e, item) {
  if (!isDisabled.value) {
    e.dataTransfer.effectAllowed = 'move';
    if (item === dragging.value) {
      return;
    }
    const newItems = [...formValidate.activity];
    const src = newItems.indexOf(dragging.value);
    const dst = newItems.indexOf(item);
    newItems.splice(dst, 0, ...newItems.splice(src, 1));
    formValidate.activity = newItems;
  }
}
function getGoodsType() {
  /** 让商品推荐列表的name属性与页面设置tab的name匹配**/
  goodDesignList({ gid: 70 }).then((response) => {
    let list = response.list;
    let arr = [],
      arr1 = [];
    const listArr = [{ name: '是否热卖', value: 'isGood', type: '5' }];
    let typeLists = [
      { name: '', value: 'isHot', type: '2' }, //热门榜单
      { name: '', value: 'isBenefit', type: '4' }, //促销单品
      { name: '', value: 'isBest', type: '1' }, //精品推荐
      { name: '', value: 'isNew', type: '3' },
    ]; //首发新品
    list.forEach((item) => {
      let obj = {};
      obj.value = JSON.parse(item.value);
      obj.id = item.id;
      obj.gid = item.gid;
      obj.status = item.status;
      arr.push(obj);
    });
    arr.forEach((item1) => {
      let obj1 = {};
      obj1.name = item1.value.fields[1].value;
      obj1.status = item1.status;
      obj1.type = item1.value.fields[3].value;
      arr1.push(obj1);
    });
    typeLists.forEach((item) => {
      arr1.forEach((item1) => {
        if (item.type == item1.type) {
          listArr.push({
            name: item1.name,
            value: item.value,
            type: item.type,
          });
        }
      });
    });
    recommend.value = listArr;
  });
}
// 删除视频；
function delVideo() {
  formValidate.videoLink = '';
}
function zh_uploadFile() {
  if (videoLink.value) {
    formValidate.videoLink = videoLink.value;
  }
}
function getFileType(fileName) {
  // 后缀获取
  let suffix = '';
  // 获取类型结果
  let result = '';
  try {
    const flieArr = fileName.split('.');
    suffix = flieArr[flieArr.length - 1];
  } catch (err) {
    suffix = '';
  }
  // fileName无后缀返回 false
  if (!suffix) {
    return false;
  }
  suffix = suffix.toLocaleLowerCase();
  // 图片格式
  const imglist = ['png', 'jpg', 'jpeg', 'bmp', 'gif'];
  // 进行图片匹配
  result = imglist.find((item) => item === suffix);
  if (result) {
    return 'image';
  }
  // 匹配 视频
  const videolist = ['mp4', 'm2v', 'mkv', 'rmvb', 'wmv', 'avi', 'flv', 'mov', 'm4v'];
  result = videolist.find((item) => item === suffix);
  if (result) {
    return 'video';
  }
  // 其他 文件类型
  return 'other';
}
// 获取服务保障列表
function getGuaranteeList() {
  guaranteeListApi({
    isShow: 1,
  })
    .then((res) => {
      guaranteeList.value = res;
    })
    .catch((err) => {
      ElMessage.error(err.message);
    });
}
// 获取被选服务保障id列表
function getGuranteeIdsList(list) {
  if (list) {
    guaranteeIdsList.value = list.map((item) => {
      return item.id;
    });
  }
}
// 修改服务保障
function updateGuaranteeIds(list) {
  formValidate.guaranteeIds = list.join(',');
}
// 回调规格生成表格数据 多规格
function changeManyAttrValue(e) {
  // rows数组第一项 新增默认数据 oneFormBatch
  ManyAttrValue.value = e;
}
//批量清空规格中的批量数据
function handleBatchDel() {
  oneFormBatch.value = [
    {
      image: '',
      price: void 0,
      cost: void 0,
      otPrice: void 0,
      stock: void 0,
      weight: void 0,
      volume: void 0,
      brokerage: void 0,
      brokerageTwo: void 0,
      barCode: '',
    },
  ];
}
// 设置多规格商品的表格数据
function generateManyAttr() {
  // 多规格属性赋值
  ManyAttrValue.value = formValidate.attrValue;
  ManyAttrValue.value.forEach((val) => {
    val.image = proxy.$selfUtil.setDomain(val.image);
    val.attrValue = JSON.parse(val.attrValue);
  });
  ManyAttrValue.value = [...oneFormBatch.value, ...ManyAttrValue.value];
  // 此处手动实现后台原本value0 value1的逻辑
  formValidate.attrValue.forEach((item) => {
    for (let attrValueKey in item.attrValue) {
      item[attrValueKey] = item.attrValue[attrValueKey];
    }
  });
}

onMounted(() => {
  getCopyConfig();
  formValidate.sliderImages = [];
  if (route.params.id) {
    setTagsViewTitle();
    getInfo();
  }
  getCategorySelect();
  getShippingList();
  getGoodsType();
});
</script>
<style scoped lang="scss">
.upLoadPicBox {
  :deep(.el-alert ){
    padding: 0 !important;
  }
}

.disLabel {
  :deep(.el-form-item__label ){
    margin-left: 36px !important;
  }
}

.disLabelmoren {
  :deep(.el-form-item__label ){
    margin-left: 120px !important;
  }
}

.priamry_border {
  border: 1px solid #1890ff;
  color: #1890ff;
}

.color-item {
  height: 30px;
  line-height: 30px;
  padding: 0 10px;
  color: #fff;
  margin-right: 10px;
}

.color-list .color-item.blue {
  background-color: #1e9fff;
}

.color-list .color-item.yellow {
  background-color: rgb(254, 185, 0);
}

.color-list .color-item.green {
  background-color: #009688;
}

.color-list .color-item.red {
  background-color: #ed4014;
}

.proCoupon {
  :deep(.el-form-item__content ){
    margin-top: 5px;
  }
}

.tabPic {
  width: 40px !important;
  height: 40px !important;

  img {
    width: 100%;
    height: 100%;
  }
}

.noLeft {
  :deep(.el-form-item__content ){
    margin-left: 0 !important;
  }
}

.tabNumWidth {
  :deep(.el-input-number--medium ){
    width: 121px !important;
  }

  :deep(.el-input-number__increase ){
    width: 20px !important;
    font-size: 12px !important;
  }

  :deep(.el-input-number__decrease ){
    width: 20px !important;
    font-size: 12px !important;
  }

  :deep(.el-input-number--medium .el-input__inner ){
    padding-left: 25px !important;
    padding-right: 25px !important;
  }

  :deep(.thead) {
    line-height: normal !important;
  }

  :deep(.el-table .cell) {
    line-height: normal !important;
  }
}

.selWidth {
  width: 100%;
}

.selWidthd {
  width: 300px;
}

.button-new-tag {
  padding-top: 0;
  padding-bottom: 0;
}

.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}

.pictrue {
  width: 60px;
  height: 60px;
  border: 1px dotted rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  position: relative;
  cursor: pointer;

  img,
  .image {
    width: 100%;
    height: 100%;
  }

  video {
    width: 100%;
    height: 100%;
  }
}

.btndel {
  position: absolute;
  z-index: 1;
  width: 20px !important;
  height: 20px !important;
  left: 43px;
  top: 1px;
}

.labeltop {
  :deep(.el-form-item__label ){
    float: none !important;
    display: inline-block !important;
    width: auto !important;
  }
}

.iview-video-style {
  width: 300px;
  height: 180px;
  border-radius: 10px;
  background-color: #707070;
  margin: 0 120px 20px;
  position: relative;
  overflow: hidden;
}

.iview-video-style .iconv {
  color: #fff;
  line-height: 180px;
  width: 50px;
  height: 50px;
  display: inherit;
  font-size: 26px;
  position: absolute;
  top: -74px;
  left: 50%;
  margin-left: -25px;
}

.iview-video-style .mark {
  position: absolute;
  width: 100%;
  height: 30px;
  top: 0;
  background-color: rgba(0, 0, 0, 0.5);
  text-align: center;
}

:deep(.el-tabs__nav-scroll) {
  margin-top: -20px;
}
.selWidth100 {
  width: 100%;
}
.tips-bottom {
  margin-top: 10px;
}
.onePrimary {
  margin-left: 0 !important;
}
.formValidate.mt20 {
  padding: 0 30px;
}
.mr16 {
  margin-right: 16px;
}
:deep(.el-radio__label) {
  font-size: 12px !important;
}
:deep(.el-radio__input) {
  font-size: 12px !important;
}
.inputWid {
  width: 500px;
}
.noLeft {
  margin-left: -60px;
}
</style>
