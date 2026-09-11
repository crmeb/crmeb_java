<template>
  <div class="divBox">
    <pages-header
      ref="pageHeader"
      :title="$route.params.id ? ($route.params.type && formValidate.id > 0 ? '商品详情' : '编辑商品') : '添加商品'"
      backUrl="/marketing/seckill/list"
    ></pages-header>
    <el-card class="box-card mt14">
      <el-tabs class="list-tabs" v-model="currentTab" @tab-click="tabsHandleClick">
        <el-tab-pane label="选择商品" name="0" v-if="!$route.params.id"></el-tab-pane>
        <el-tab-pane label="基础信息" name="1"></el-tab-pane>
        <el-tab-pane label="商品详情" name="2"></el-tab-pane>
      </el-tabs>
      <el-form
        ref="formValidateRef"
        v-loading="fullscreenLoading"
        class="formValidate mt20"
        :rules="ruleValidate"
        :model="formValidate"
        label-width="130px"
        @submit.prevent
      >
        <!-- 秒杀商品-->
        <div v-show="currentTab == 0 && !$route.params.id">
          <el-form-item label="选择商品：" prop="image">
            <div class="upLoadPicBox" @click="changeGood">
              <div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image" /></div>
              <div v-else class="upLoad">
                <i class="el-icon-camera cameraIconfont" />
              </div>
            </div>
          </el-form-item>
        </div>
        <!-- 商品信息-->
        <div v-show="currentTab == 1">
          <el-row :gutter="24">
            <el-col v-bind="grid2">
              <el-form-item label="商品主图：" prop="image">
                <div class="upLoadPicBox" @click="modalPicTap('1')">
                  <div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image" /></div>
                  <div v-else class="upLoad">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="商品轮播图：" prop="imagess">
                <div class="acea-row">
                  <div
                    v-for="(item, index) in formValidate.imagess"
                    :key="index"
                    class="pictrue"
                    draggable="true"
                    @dragstart="handleDragStart($event, item)"
                    @dragover.prevent="handleDragOver($event, item)"
                    @dragenter="handleDragEnter($event, item)"
                    @dragend="handleDragEnd($event, item)"
                  >
                    <img v-if="item.split('.')[item.split('.').length - 1] !== 'mp4'" :src="item" />
                    <video v-else :src="item" />
                    <i v-show="!$route.params.type" class="el-icon-error btndel" @click="handleRemove(index)" />
                  </div>
                  <div
                    v-if="formValidate.imagess.length < 10 && !$route.params.type"
                    class="upLoadPicBox"
                    @click="modalPicTap('2')"
                  >
                    <div class="upLoad">
                      <i class="el-icon-camera cameraIconfont" />
                    </div>
                  </div>
                </div>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="商品标题：" prop="title">
                <el-input
                  :disabled="Boolean($route.params.type)"
                  v-model="formValidate.title"
                  class="selWidth"
                  maxlength="249"
                  placeholder="请输入商品名称"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="单位：" prop="unitName">
                <el-input
                  :disabled="Boolean($route.params.type)"
                  v-model="formValidate.unitName"
                  placeholder="请输入单位"
                  class="selWidth"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="运费模板：" prop="tempId">
                <div class="acea-row">
                  <el-select
                    :disabled="Boolean($route.params.type)"
                    v-model="formValidate.tempId"
                    placeholder="请选择"
                    class="selWidth"
                  >
                    <el-option v-for="item in shippingList" :key="item.id" :label="item.name" :value="item.id" />
                  </el-select>
                  <!--<el-button class="mr15" @click="addTem">添加运费模板</el-button>-->
                </div>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="当天参与活动次数：" prop="num">
                <el-input-number
                  :disabled="Boolean($route.params.type)"
                  controls-position="right"
                  v-model="formValidate.num"
                  :step="1"
                  step-strictly
                  :min="1"
                  placeholder="请输入活动次数"
                  class="selWidth"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="活动日期：" prop="timeVal">
                <el-date-picker
                  :disabled="Boolean($route.params.type)"
                  class="selWidth"
                  v-model="formValidate.timeVal"
                  value-format="YYYY-MM-DD"
                  format="YYYY-MM-DD"

                  type="daterange"
                  placement="bottom-end"
                  placeholder="请选择活动日期"
                  @change="onchangeTime"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="活动时间：" prop="timeId">
                <el-select
                  :disabled="Boolean($route.params.type)"
                  v-model="formValidate.timeId"
                  placeholder="请选择"
                  class="selWidth"
                >
                  <el-option
                    v-for="item in seckillTime"
                    :key="item.id"
                    :label="item.name + ' | ' + item.time"
                    :value="item.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="活动状态：" required>
                <el-radio-group :disabled="Boolean($route.params.type)" v-model="formValidate.status">
                  <el-radio :label="0" :value="0" class="radio">关闭</el-radio>
                  <el-radio :label="1" :value="1">开启</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <!-- 规格表格-->
            <el-col :span="24">
              <el-form-item label="商品属性：" class="labeltop" required>
                <el-table
                  ref="multipleTableRef"
                  :data="ManyAttrValue"
                  tooltip-effect="dark"
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                >
                  <el-table-column
                    type="selection"
                    :selectable="checkSelectable"
                    key="1"
                    v-if="formValidate.specType"
                    width="55"
                  >
                  </el-table-column>
                  <template v-if="manyTabDate && formValidate.specType">
                    <el-table-column
                      v-for="(item, iii) in manyTabDate"
                      :key="iii"
                      :label="manyTabTit[iii].title"
                      min-width="100"
                    >
                      <template #default="scope">
                        <span class="priceBox" v-text="scope.row[iii]" />
                      </template>
                    </el-table-column>
                  </template>
                  <el-table-column label="图片" min-width="80">
                    <template #default="scope">
                      <div class="upLoadPicBox" @click="modalPicTap('1', 'duo', scope.$index)">
                        <div v-if="scope.row.image" class="pictrue tabPic"><img :src="scope.row.image" /></div>
                        <div v-else class="upLoad tabPic">
                          <i class="el-icon-camera cameraIconfont" />
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column
                    v-for="(item, iii) in attrValue"
                    :key="iii"
                    :label="formThead[iii].title"
                    min-width="145"
                  >
                    <template #default="scope">
                      <el-input-number
                        :disabled="Boolean($route.params.type)"
                        :controls="false"
                        controls-position="right"

                        v-if="formThead[iii].title === '秒杀价'"
                        v-model="scope.row[iii]"
                        :min="0"
                        :precision="2"
                        :step="0.1"
                        class="priceBox"
                      />
                      <el-input-number
                        :disabled="Boolean($route.params.type)"
                        :controls="false"
                        controls-position="right"

                        v-else-if="formThead[iii].title === '限量'"
                        v-model="scope.row[iii]"
                        type="number"
                        :min="minQuota(scope.row)"
                        :max="scope.row.stock"
                        :step="1"
                        step-strictly
                        class="priceBox"
                      />
                      <span v-else v-text="scope.row[iii]" class="priceBox" />
                      <!--<el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'" class="priceBox" />-->
                    </template>
                  </el-table-column>
                </el-table>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <!-- 商品详情-->
        <div v-show="currentTab == 2">
          <el-form-item label="商品详情：">
            <WangEditor v-if="!$route.params.type" v-model="formValidate.content"></WangEditor>
            <div v-else v-html="formValidate.content"></div>
          </el-form-item>
        </div>
        <el-form-item style="margin-top: 30px">
          <el-button
            v-show="(!$route.params.id && currentTab > 0) || ($route.params.id && currentTab == 2)"
            class="submission"

            @click="handleSubmitUp"
            >上一步</el-button
          >
          <el-button
            v-show="currentTab == 0"
            class="submission onePrimary"

            @click="handleSubmitNest1('formValidateRef')"
            >下一步</el-button
          >
          <el-button v-show="currentTab == 1" class="submission" @click="handleSubmitNest2('formValidateRef')"
            >下一步</el-button
          >
          <el-button
            v-if="!$route.params.type && currentTab != 0"
            :loading="loading"
            type="primary"
            class="submission"

            @click="handleSubmit('formValidateRef')"
            v-hasPermi="['admin:seckill:update']"
            >提交</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getShippingList" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from '@/utils/elementPlusFeedback';
import WangEditor from '@/components/wangEditor/index.vue';
import { productDetailApi, categoryApi } from '@/api/store';
import { shippingTemplatesList } from '@/api/logistics';
import { getSeckillList } from '@/libs/public';
import { seckillStoreSaveApi, seckillStoreUpdateApi, seckillStoreInfoApi } from '@/api/marketing';
import CreatTemplates from '@/views/systemSetting/deliverGoods/freightSet/creatTemplates';
import { Debounce } from '@/utils/validate';
import { useTagsViewStore } from '@/store/modules/tagsView';

defineOptions({ name: 'creatSeckill' });

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();
const tagsViewStore = useTagsViewStore();

const defaultObj = {
  image: '',
  images: '',
  imagess: [],
  title: '',
  num: 1,
  unitName: '',
  sort: 0,
  giveIntegral: 0,
  ficti: 0,
  isShow: false,
  tempId: '',
  attrValue: [
    {
      image: '',
      price: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      quota: 1,
      barCode: '',
      weight: 0,
      volume: 0,
    },
  ],
  attr: [],
  selectRule: '',
  content: '',
  specType: false,
  id: 0,
  timeId: 1,
  startTime: '',
  stopTime: '',
  timeVal: [],
  status: 0,
};
const objTitle = {
  price: {
    title: '秒杀价',
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
  quota: {
    title: '限量',
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

const props2 = reactive({
  children: 'child',
  label: 'name',
  value: 'id',
  multiple: true,
  emitPath: false,
});
const grid2 = reactive({
  xl: 24,
  lg: 24,
  md: 24,
  sm: 24,
  xs: 24,
});
const currentTab = ref(0);
const formThead = ref(Object.assign({}, objTitle));
const formValidate = ref(Object.assign({}, defaultObj));
const loading = ref(false);
const fullscreenLoading = ref(false);
const merCateList = ref([]); // 商户分类筛选
const shippingList = ref([]); // 运费模板
const seckillTime = ref([]);
const ruleValidate = {
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
  attrValue: [{ required: true, message: '请选择商品属相', trigger: 'change', type: 'array', min: '1' }],
  num: [{ required: true, message: '请输入活动次数', trigger: 'blur' }],
  unitName: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  tempId: [{ required: true, message: '请选择运费模板', trigger: 'change' }],
  timeId: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
  image: [{ required: true, message: '请选择商品', trigger: 'change' }],
  imagess: [{ required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }],
  specType: [{ required: true, message: '请选择商品规格', trigger: 'change' }],
  timeVal: [{ required: true, message: '请选择活动日期', trigger: 'change', type: 'array' }],
};
const manyTabDate = ref({});
const manyTabTit = ref({});
const attrInfo = ref({});
const tempRoute = ref({});
const multipleSelection = ref([]);
const productId = ref(0);
const OneattrValue = [Object.assign({}, defaultObj.attrValue[0])]; // 单规格
const ManyAttrValue = ref([Object.assign({}, defaultObj.attrValue[0])]); // 多规格
const dragging = ref(null);
const tempData = ref({});
const formValidateRef = ref(null);
const multipleTableRef = ref(null);
const addTemplatesRef = ref(null);
const pageHeaderRef = ref(null);

const attrValue = computed(() => {
  const obj = Object.assign({}, defaultObj.attrValue[0]);
  delete obj.image;
  return obj;
});
// 限量最小值
const minQuota = computed(() => {
  return (data) => {
    if (data.stock) {
      return 1;
    } else {
      return 0;
    }
  };
});

// 规格表格不可选择项
const checkSelectable = (row, index) => {
  if (route.params.type && formValidate.value.id > 0) {
    return false;
  } else {
    return true;
  }
};
const tabsHandleClick = (tab, event) => {
  currentTab.value = tab.name;
  // if (!this.$route.params.id && tab.index == 1) this.getProdect(this.productId);
};
const watCh = (val) => {
  const tmp = {};
  const tmpTab = {};
  formValidate.value.attr.forEach((o, i) => {
    // tmp['value' + i] = { title: o.attrName }
    // tmpTab['value' + i] = ''
    tmp[o.attrName] = { title: o.attrName };
    tmpTab[o.attrName] = '';
  });
  manyTabTit.value = tmp;
  manyTabDate.value = tmpTab;
  formThead.value = Object.assign({}, formThead.value, tmp);
};
const handleRemove = (i) => {
  formValidate.value.imagess.splice(i, 1);
};
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};
// 点击商品图
const modalPicTap = (tit, num, i) => {
  if (route.params.type) return;
  proxy.$modalUpload(
    function (img) {
      if (tit === '1' && !num) {
        formValidate.value.image = img[0].sattDir;
        ManyAttrValue.value[0].image = img[0].sattDir;
      }
      if (tit === '2' && !num) {
        if (img.length > 10) return ElMessage.warning('最多选择10张图片！');
        if (img.length + formValidate.value.imagess.length > 10) return ElMessage.warning('最多选择10张图片！');
        img.map((item) => {
          formValidate.value.imagess.push(item.sattDir);
        });
      }
      if (tit === '1' && num === 'duo') {
        ManyAttrValue.value[i].image = img[0].sattDir;
      }
    },
    tit,
    'content',
  );
};
// 具体日期
const onchangeTime = (e) => {
  formValidate.value.timeVal = e;
  formValidate.value.startTime = e ? e[0] : '';
  formValidate.value.stopTime = e ? e[1] : '';
};
const changeGood = () => {
  proxy.$modalGoodList(function (row) {
    formValidate.value.image = row.image;
    productId.value = row.id;
    // _this.formValidate.productId = row.id
    if (!route.params.id) getProdect(row.id);
  });
};
const handleSubmitNest1 = () => {
  if (!formValidate.value.image) {
    return ElMessage.warning('请选择商品！');
  } else {
    currentTab.value++;
    currentTab.value = currentTab.value.toString();
  }
};
// 商品分类；
const getCategorySelect = () => {
  categoryApi({ status: -1, type: 1 }).then((res) => {
    merCateList.value = filerMerCateList(res);
  });
};
const filerMerCateList = (treeData) => {
  return treeData.map((item) => {
    if (!item.child) {
      item.disabled = true;
    }
    item.label = item.name;
    return item;
  });
};
// 运费模板；
const getShippingList = () => {
  shippingTemplatesList(tempData.value).then((res) => {
    shippingList.value = res.list;
  });
};
// 运费模板
const addTem = () => {
  addTemplatesRef.value.dialogVisible = true;
  addTemplatesRef.value.getCityList();
};
// 商品详情
const getInfo = () => {
  if (!route.params.id) {
    getProdect(productId.value);
  } else {
    getSekllProdect(route.params.id);
  }
};
const getProdect = (id) => {
  fullscreenLoading.value = true;
  productDetailApi(id)
    .then(async (res) => {
      let info = res;
      formValidate.value = {
        image: proxy.$selfUtil.setDomain(info.image),
        imagess: JSON.parse(info.sliderImage),
        title: info.storeName,
        quota: '',
        unitName: info.unitName,
        sort: info.sort,
        isShow: info.isShow,
        tempId: info.tempId,
        attr: info.attr,
        attrValue: info.attrValue,
        selectRule: info.selectRule,
        content: info.content,
        specType: info.specType,
        productId: info.id,
        giveIntegral: info.giveIntegral,
        ficti: info.ficti,
        timeId: route.params.id
          ? Number(info.timeId)
          : route.params.timeId
          ? Number(route.params.timeId)
          : '',
        startTime: info.startTime || '',
        stopTime: info.stopTime || '',
        timeVal: [],
        status: 0,
        num: 1,
      };
      if (info.specType) {
        nextTick(() => {
          info.attrValue.forEach((row) => {
            row.quota = row.stock;
            row.attrValue = JSON.parse(row.attrValue);
            for (let attrValueKey in row.attrValue) {
              row[attrValueKey] = row.attrValue[attrValueKey];
            }
            row.image = proxy.$selfUtil.setDomain(row.image);
            multipleTableRef.value.toggleRowSelection(row, true);
            // this.$set(row, 'checked', true)
          });
        });

        ManyAttrValue.value = info.attrValue;
        multipleSelection.value = info.attrValue;
      } else {
        info.attrValue.forEach((row) => {
          row.quota = row.stock;
        });
        ManyAttrValue.value = info.attrValue;
        // this.formValidate.attr = []
      }
      fullscreenLoading.value = false;
    })
    .catch((res) => {
      fullscreenLoading.value = false;
    });
};
const getSekllProdect = (id) => {
  fullscreenLoading.value = true;
  seckillStoreInfoApi({ id: id })
    .then(async (res) => {
      let info = res;
      formValidate.value = {
        image: proxy.$selfUtil.setDomain(info.image),
        imagess: JSON.parse(info.sliderImage),
        title: info.storeName,
        quota: info.quota,
        unitName: info.unitName,
        sort: info.sort,
        isShow: info.isShow,
        tempId: info.tempId,
        attr: info.attr,
        attrValue: info.attrValue,
        selectRule: info.selectRule,
        content: info.content,
        specType: info.specType,
        productId: info.productId,
        giveIntegral: info.giveIntegral,
        ficti: info.ficti,
        timeId: Number(info.timeId),
        // startTime: info.startTime || '',
        startTime: info.startTimeStr || '',
        // stopTime: info.stopTime || '',
        stopTime: info.stopTimeStr || '',
        status: info.status,
        num: info.num,
        timeVal: info.startTimeStr && info.stopTimeStr ? [info.startTimeStr, info.stopTimeStr] : [],
        id: info.id,
      };
      if (info.specType) {
        ManyAttrValue.value = info.attrValue;
        nextTick(() => {
          ManyAttrValue.value.forEach((item, index) => {
            item.attrValue = JSON.parse(item.attrValue);
            for (let attrValueKey in item.attrValue) {
              item[attrValueKey] = item.attrValue[attrValueKey];
            }
            item.image = proxy.$selfUtil.setDomain(item.image);
            if (item.id) {
              // 设置自动选中逻辑，只要id存在
              item.price = item.price;
              item.quota = item.quota;
              nextTick(() => {
                multipleTableRef.value.toggleRowSelection(item, true);
              });
            }
          });
        });
      } else {
        ManyAttrValue.value = info.attrValue;
        // this.formValidate.attr = []
      }
      fullscreenLoading.value = false;
    })
    .catch((res) => {
      fullscreenLoading.value = false;
    });
};
const handleSubmitNest2 = (name) => {
  formValidateRef.value.validate((valid) => {
    if (valid) {
      if (formValidate.value.specType && multipleSelection.value.length === 0)
        return ElMessage.warning('请选择至少一个商品属性！');
      currentTab.value++;
      currentTab.value = currentTab.value.toString();
    } else {
      return false;
    }
  });
};
// 提交
const handleSubmit = Debounce(function (name) {
  if (!formValidate.value.specType) {
    // this.formValidate.attr = []
    formValidate.value.attrValue = ManyAttrValue.value;
  } else {
    // this.multipleSelection.forEach((row) => {
    //   this.$set(row, 'checked', true)
    // });
    formValidate.value.attrValue = multipleSelection.value;
  }
  formValidate.value.images = JSON.stringify(formValidate.value.imagess);
  if (typeof formValidate.value.attrValue[0].attrValue == 'object') {
    formValidate.value.attrValue.forEach((item) => {
      item.attrValue = JSON.stringify(item.attrValue);
    });
  }
  formValidateRef.value.validate((valid) => {
    if (valid) {
      fullscreenLoading.value = true;
      loading.value = true;
      route.params.id
        ? seckillStoreUpdateApi({ id: route.params.id }, formValidate.value)
            .then(async () => {
              fullscreenLoading.value = false;
              ElMessage.success('编辑成功');
              router.push({
                path: '/marketing/seckill/list',
              });
              formValidateRef.value.resetFields();
              formValidate.value.images = [];
              loading.value = false;
            })
            .catch(() => {
              fullscreenLoading.value = false;
              loading.value = false;
            })
        : seckillStoreSaveApi(formValidate.value)
            .then(async (res) => {
              fullscreenLoading.value = false;
              ElMessage.success('新增成功');
              router.push({
                path: '/marketing/seckill/list',
              });
              formValidateRef.value.resetFields();
              formValidate.value.images = [];
              loading.value = false;
            })
            .catch(() => {
              fullscreenLoading.value = false;
              loading.value = false;
            });
    } else {
      if (
        !formValidate.value.storeName ||
        !formValidate.value.unitName ||
        !formValidate.value.store_info ||
        !formValidate.value.image ||
        !formValidate.value.images
      ) {
        ElMessage.warning('请填写完整商品信息！');
      }
    }
  });
});
const handleSubmitUp = () => {
  if (currentTab.value-- < 0) currentTab.value = 0;
  currentTab.value = currentTab.value.toString();
};
const setTagsViewTitle = () => {
  const title = '编辑秒杀商品';
  const r = Object.assign({}, tempRoute.value, { title: `${title}-${route.params.id}` });
  tagsViewStore.updateVisitedView(r);
};
// 移动
const handleDragStart = (e, item) => {
  dragging.value = item;
};
const handleDragEnd = (e, item) => {
  dragging.value = null;
};
const handleDragOver = (e) => {
  e.dataTransfer.dropEffect = 'move';
};
const handleDragEnter = (e, item) => {
  e.dataTransfer.effectAllowed = 'move';
  if (item === dragging.value) {
    return;
  }
  const newItems = [...formValidate.value.imagess];
  const src = newItems.indexOf(dragging.value);
  const dst = newItems.indexOf(item);
  newItems.splice(dst, 0, ...newItems.splice(src, 1));
  formValidate.value.imagess = newItems;
};

// created
watch(() => formValidate.value.attr, watCh);
tempRoute.value = Object.assign({}, route);

onMounted(() => {
  getSeckillList(1).then((res) => {
    seckillTime.value = res.list;
  });
  formValidate.value.imagess = [];
  if (route.params.id) {
    setTagsViewTitle();
    getInfo();
    currentTab.value = '1';
  }
  getShippingList();
  getCategorySelect();
});
</script>

<style scoped lang="scss">
.divBox {
  overflow-y: scroll;
  height: 100%;
}

.labeltop {
  :deep(.el-input-number) {
    width: 100% !important;
    min-width: 0 !important;
  }

  :deep(.el-input-number.is-without-controls .el-input__wrapper) {
    padding-left: 8px !important;
    padding-right: 8px !important;
  }
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
  width: 460px !important;
}

.selWidthd {
  width: 300px;
}

.button-new-tag {
  height: 28px;
  line-height: 26px;
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

  img {
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

:deep(.el-tabs__nav-scroll) {
  margin-top: -20px;
}
.onePrimary {
  margin-left: 0 !important;
}
:deep(.el-table .cell) {
  padding-right: 0 !important;
}
</style>
