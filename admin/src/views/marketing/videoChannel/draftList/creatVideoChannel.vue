<template>
  <div class="divBox">
    <pages-header
      ref="pageHeader"
      :title="$route.params.id ? '编辑商品' : '添加商品'"
      backUrl="/marketing/videoChannel/draftList"
    ></pages-header>
    <el-card class="box-card mt14">
      <el-tabs class="list-tabs" v-model="currentTab" @tab-click="tabsHandleClick">
        <el-tab-pane v-if="!$route.params.id" label="选择商品" name="0"></el-tab-pane>
        <el-tab-pane label="基础信息" name="1"></el-tab-pane>
        <el-tab-pane label="商品详情" name="2"></el-tab-pane>
      </el-tabs>
      <el-form
        ref="formValidate"
        v-loading="fullscreenLoading"
        class="formValidate mt20"
        :rules="ruleValidate"
        :model="formValidate"
        label-width="100px"
        @submit.native.prevent
      >
        <!-- 视频号商品-->
        <div v-show="currentTab == 0 && !$route.params.id">
          <el-form-item label="选择商品：" prop="image">
            <div class="upLoadPicBox" @click="changeGood">
              <div v-if="formValidate.image" class="pictrue">
                <img :src="formValidate.image" />
              </div>
              <div v-else class="upLoad">
                <i class="el-icon-camera cameraIconfont" />
              </div>
            </div>
          </el-form-item>
        </div>
        <!-- 商品信息-->
        <div v-show="currentTab == 1">
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item label="商品轮播图：" prop="images">
                <div class="acea-row">
                  <div
                    v-for="(item, index) in formValidate.images"
                    :key="index"
                    class="pictrue"
                    draggable="true"
                    @dragstart="handleDragStart($event, item, 'images')"
                    @dragover.prevent="handleDragOver($event, item, 'images')"
                    @dragenter="handleDragEnter($event, item, 'images')"
                    @dragend="handleDragEnd($event, item, 'images')"
                  >
                    <img v-if="item.split('.')[item.split('.').length - 1] !== 'mp4'" :src="item" />
                    <video v-else :src="item" />
                    <i class="el-icon-error btndel" @click="handleRemove(index, 'images')" />
                  </div>
                  <div v-if="formValidate.images.length < 5" class="upLoadPicBox" @click="modalPicTap('2')">
                    <div class="upLoad">
                      <i class="el-icon-camera cameraIconfont" />
                    </div>
                  </div>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="商品标题：" prop="title">
                <el-input v-model="formValidate.title" class="selWidth" maxlength="249" placeholder="请输入商品名称" />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="单位：" prop="unitName">
                <el-input v-model="formValidate.unitName" placeholder="请输入单位" class="selWidth" />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="运费模板：" prop="tempId">
                <div class="acea-row">
                  <el-select v-model="formValidate.tempId" placeholder="请选择" class="selWidth">
                    <el-option v-for="item in shippingList" :key="item.id" :label="item.name" :value="item.id" />
                  </el-select>
                </div>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="虚拟销量：" prop="ficti">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.ficti"
                  :step="1"
                  step-strictly
                  :min="0"
                  placeholder="请输入虚拟销量"
                  class="selWidth"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="微信商品类目：" prop="thirdCatIdList">
                <el-cascader
                  class="selWidth"
                  filterable
                  v-model="formValidate.thirdCatIdList"
                  :options="options"
                  :props="propsCatId"
                  ref="demoCascader"
                  @focus="handleChanges"
                ></el-cascader>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="获得积分：" prop="giveIntegral">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.giveIntegral"
                  :step="1"
                  step-strictly
                  :min="0"
                  placeholder="请输入获得积分"
                  class="selWidth"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24" v-if="productQualificationType > 0">
              <el-form-item label="商品资质图：">
                <div class="acea-row">
                  <div
                    v-for="(item, index) in formValidate.qualificationPicsList"
                    :key="index"
                    class="pictrue"
                    draggable="true"
                    @dragstart="handleDragStart($event, item, 'qualificationPics')"
                    @dragover.prevent="handleDragOver($event, item, 'qualificationPics')"
                    @dragenter="handleDragEnter($event, item, 'qualificationPics')"
                    @dragend="handleDragEnd($event, item, 'qualificationPics')"
                  >
                    <img :src="item" />
                    <i class="el-icon-error btndel" @click="handleRemove(index, 'qualificationPics')" />
                  </div>
                  <div
                    v-if="formValidate.qualificationPicsList.length < 5"
                    class="upLoadPicBox"
                    @click="modalPicTap('3')"
                  >
                    <div class="upLoad">
                      <i class="el-icon-camera cameraIconfont" />
                    </div>
                  </div>
                </div>
                <div class="fontColor3">
                  资质类型说明：{{ productQualification }}。{{
                    productQualificationType === 1 ? '必填项！' : '选填项！'
                  }}
                </div>
              </el-form-item>
            </el-col>
            <!-- 规格表格-->
            <el-col :span="24">
              <el-form-item label="商品属性：" class="labeltop" required>
                <el-table
                  ref="multipleTable"
                  :data="ManyAttrValue"
                  tooltip-effect="dark"
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                >
                  <el-table-column type="selection" key="1" v-if="formValidate.specType" width="55"> </el-table-column>
                  <template v-if="manyTabDate && formValidate.specType">
                    <el-table-column
                      v-for="(item, iii) in manyTabDate"
                      :key="iii"
                      :label="manyTabTit[iii].title"
                      min-width="80"
                    >
                      <template slot-scope="scope">
                        <span class="priceBox" v-text="scope.row[iii]" />
                      </template>
                    </el-table-column>
                  </template>
                  <el-table-column label="图片" min-width="80">
                    <template slot-scope="scope">
                      <div class="upLoadPicBox" @click="modalPicTap('1', 'duo', scope.$index)">
                        <div v-if="scope.row.image" class="pictrue tabPic">
                          <img :src="scope.row.image" />
                        </div>
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
                    min-width="140"
                  >
                    <template slot-scope="scope">
                      <el-input-number
                        controls-position="right"
                        size="small"
                        v-if="formThead[iii].title === '商品价'"
                        v-model="scope.row[iii]"
                        :min="0"
                        :precision="2"
                        :step="0.1"
                        class="priceBox"
                      />
                      <el-input-number
                        controls-position="right"
                        size="small"
                        v-else-if="formThead[iii].title === '限量'"
                        v-model="scope.row[iii]"
                        type="number"
                        :min="1"
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
            <!-- <ueditor-from v-model="formValidate.descInfo" :content="formValidate.descInfo" /> -->
            <Tinymce v-model="formValidate.descInfo"></Tinymce>
          </el-form-item>
        </div>
        <el-form-item style="margin-top: 30px">
          <el-button
            v-show="(!$route.params.id && currentTab > 0) || ($route.params.id && currentTab == 2)"
            class="submission"
            @click="handleSubmitUp"
            >上一步</el-button
          >
          <el-button v-show="currentTab == 0" class="submission onePrimary" @click="handleSubmitNest1('formValidate')"
            >下一步</el-button
          >
          <el-button v-show="currentTab == 1" class="submission" @click="handleSubmitNest2('formValidate')"
            >下一步</el-button
          >
          <el-button
            :loading="loading"
            type="primary"
            class="submission"
            @click="handleSubmit('formValidate')"
            v-hasPermi="['admin:pay:component:product:update']"
            >提交</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getShippingList" />

    <el-dialog title="提示" :visible.sync="dialogVisible" width="900px" class="modalBox" :before-close="handleClose">
      <el-cascader
        class="modalBox"
        style="width: 400px"
        filterable
        v-model="formValidate.thirdCatIdList"
        :options="options"
        :props="propsCatId"
        ref="demoCascader"
        :popper-append-to-body="false"
        popper-class="eloption"
        @change="handleChangesModel"
      ></el-cascader>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Tinymce from '@/components/Tinymce/index';
import { productDetailApi } from '@/api/store';
import { shippingTemplatesList } from '@/api/logistics';
import { draftUpdateApi, draftInfoApi, catListApi, videoAddApi, shopImgUploadApi } from '@/api/marketing';
import CreatTemplates from '@/views/systemSetting/deliverGoods/freightSet/creatTemplates';
import { Debounce } from '@/utils/validate';
const defaultObj = {
  image: '',
  images: [],
  headImg: '',
  title: '',
  ficti: 0,
  unitName: '',
  sort: 0,
  giveIntegral: 0,
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
  descInfo: '',
  specType: false,
  id: 0,
  thirdCatId: '',
  thirdCatIdList: [],
  brandId: 0,
  qualificationPics: '',
  qualificationPicsList: [],
};
const objTitle = {
  price: {
    title: '商品价',
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
export default {
  name: 'creatSeckill',
  components: { CreatTemplates, Tinymce },
  data() {
    return {
      dialogVisible: false,
      props: {
        children: 'secondCatList',
        label: 'secondCatName',
        value: 'secondCatId',
        multiple: true,
        emitPath: false,
      },
      propsCatId: {
        emitPath: true,
      },
      options: [],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < new Date().setTime(new Date().getTime() - 3600 * 1000 * 24);
        },
      },
      props2: {
        children: 'child',
        label: 'name',
        value: 'id',
        multiple: true,
        emitPath: false,
      },
      grid2: {
        xl: 24,
        lg: 24,
        md: 24,
        sm: 24,
        xs: 24,
      },
      currentTab: 0,
      formThead: Object.assign({}, objTitle),
      formValidate: Object.assign({}, defaultObj),
      loading: false,
      fullscreenLoading: false,
      shippingList: [], // 运费模板
      ruleValidate: {
        image: [{ required: true, message: '请选择商品', trigger: 'change' }],
        productId: [{ required: true, message: '请选择商品', trigger: 'blur' }],
        primaryProductId: [{ required: true, message: '请选择商品', trigger: 'change' }],
        title: [{ required: true, message: '请输入商品标题', trigger: 'blur' }],
        attrValue: [
          {
            required: true,
            message: '请选择商品属相',
            trigger: 'change',
            type: 'array',
            min: '1',
          },
        ],
        ficti: [{ required: true, message: '请输入虚拟销量', trigger: 'blur' }],
        unitName: [{ required: true, message: '请输入单位', trigger: 'blur' }],
        tempId: [{ required: true, message: '请选择运费模板', trigger: 'change' }],
        giveIntegral: [{ required: true, message: '请输入获得积分', trigger: 'blur' }],
        images: [
          {
            required: true,
            message: '请上传商品轮播图',
            type: 'array',
            trigger: 'change',
          },
        ],
        specType: [{ required: true, message: '请选择商品规格', trigger: 'change' }],
        thirdCatIdList: [{ required: true, message: '请选择商品类目', trigger: 'change' }],
      },
      manyTabDate: {},
      manyTabTit: {},
      attrInfo: {},
      tempRoute: {},
      multipleSelection: [],
      primaryProductId: 0,
      productQualificationType: 0, //商品资质类型，0不需要，1必填，2选填
      productQualification: '', // 资质说明
      OneattrValue: [Object.assign({}, defaultObj.attrValue[0])], // 单规格
      ManyAttrValue: [Object.assign({}, defaultObj.attrValue[0])], // 多规格
    };
  },
  computed: {
    attrValue() {
      const obj = Object.assign({}, defaultObj.attrValue[0]);
      delete obj.image;
      return obj;
    },
  },
  created() {
    this.$watch('formValidate.attr', this.watCh);
    this.tempRoute = Object.assign({}, this.$route);
    if (!JSON.parse(sessionStorage.getItem('videoCategory'))) this.getCatList();
  },
  mounted() {
    this.formValidate.images = [];
    if (this.$route.params.id) {
      this.setTagsViewTitle();
      this.getInfo();
      this.currentTab = '1';
    }
    this.getShippingList();
    this.options = JSON.parse(sessionStorage.getItem('videoCategory'));
  },
  methods: {
    tabsHandleClick(tab, event) {
      this.currentTab = tab.name;
      if (!this.$route.params.id && tab.index == 1) this.getProdect(this.primaryProductId);
    },
    handleClose() {
      this.dialogVisible = false;
    },
    handleChangesModel(e) {
      const obj = this.$refs['demoCascader'].getCheckedNodes();
      this.productQualificationType = obj.length > 0 ? obj[0].data.productQualificationType : 0;
      this.productQualification = obj.length > 0 ? obj[0].data.productQualification : '';
      this.dialogVisible = false;
    },
    handleChanges(e) {
      this.dialogVisible = true;
      // const obj = this.$refs['demoCascader'].getCheckedNodes()
      // this.productQualificationType  =  obj.length>0 ? obj[0].data.productQualificationType : 0;
      // this.productQualification =  obj.length>0 ? obj[0].data.productQualification : '';
    },
    // 类目；
    getCatList() {
      catListApi().then((res) => {
        this.options = res;
        sessionStorage.setItem('videoCategory', JSON.stringify(res));
      });
    },
    watCh(val) {
      const tmp = {};
      const tmpTab = {};
      this.formValidate.attr.forEach((o, i) => {
        // tmp['value' + i] = { title: o.attrName }
        // tmpTab['value' + i] = ''
        tmp[o.attrName] = { title: o.attrName };
        tmpTab[o.attrName] = '';
      });
      this.manyTabTit = tmp;
      this.manyTabDate = tmpTab;
      this.formThead = Object.assign({}, this.formThead, tmp);
    },
    handleRemove(i, val) {
      if (val === 'images') {
        this.formValidate.images.splice(i, 1);
      } else {
        this.formValidate.qualificationPicsList.splice(i, 1);
      }
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 点击商品图
    modalPicTap(tit, num, i) {
      const _this = this;
      if (tit === '3') return this.$message.warning('正在建造');
      this.fullscreenLoading = true;
      // productDetailApi(id).then(async res => {
      this.$modalUpload(
        async function (img) {
          if (tit === '1' && !num) {
            _this.formValidate.image = img[0].sattDir;
            _this.ManyAttrValue[0].image = img[0].sattDir;
          }
          if (tit === '2' && !num) {
            if (img.length > 5 || img.length + _this.formValidate.images.length > 5)
              return this.$message.warning('最多选择5张图片！');
            let result = await _this.getShopImgUpload(img, []);
            result.map((item) => {
              _this.formValidate.images.push(item.img);
            });
            _this.fullscreenLoading = false;
          }
          if (tit === '1' && num === 'duo') {
            let result = await _this.getImgData(img[0].sattDir);
            _this.ManyAttrValue[i].image = result;
            _this.fullscreenLoading = false;
          }
          if (tit === '3') {
            if (img.length > 5 || img.length + _this.formValidate.qualificationPicsList.length > 5)
              return this.$message.warning('最多选择5张图片！');
            let result = await _this.getShopImgUpload(img, []);
            result.map((item) => {
              _this.formValidate.qualificationPicsList.push(item.img);
            });
            _this.fullscreenLoading = false;
          }
        },
        tit,
        'content',
      );
    },
    changeGood() {
      const _this = this;
      this.$modalGoodList(function (row) {
        _this.formValidate.image = row.image;
        _this.primaryProductId = row.id;
        if (!_this.$route.params.id) _this.getProdect(row.id);
      });
    },
    handleSubmitNest1() {
      if (!this.formValidate.image) {
        return this.$message.warning('请选择商品！');
      } else {
        this.currentTab++;
        this.currentTab = this.currentTab.toString();
        if (!this.$route.params.id) this.getProdect(this.primaryProductId);
      }
    },
    filerMerCateList(treeData) {
      return treeData.map((item) => {
        if (!item.child) {
          item.disabled = true;
        }
        item.label = item.name;
        return item;
      });
    },
    // 运费模板；
    getShippingList() {
      shippingTemplatesList(this.tempData).then((res) => {
        this.shippingList = res.list;
      });
    },
    // 运费模板
    addTem() {
      this.$refs.addTemplates.dialogVisible = true;
      this.$refs.addTemplates.getCityList();
    },
    // 商品详情
    getInfo() {
      if (!this.$route.params.id) {
        this.getProdect(this.primaryProductId);
      } else {
        this.getDraftProdect(this.$route.params.id);
      }
    },
    getProdect(id) {
      this.fullscreenLoading = true;
      productDetailApi(id)
        .then(async (res) => {
          let info = res;
          this.formValidate = {
            image: this.$selfUtil.setDomain(info.image),
            images: JSON.parse(info.sliderImage),
            title: info.storeName,
            quota: '',
            unitName: info.unitName,
            sort: info.sort,
            tempId: info.tempId,
            attr: info.attr,
            selectRule: info.selectRule,
            descInfo: info.content,
            specType: info.specType,
            primaryProductId: info.id,
            giveIntegral: info.giveIntegral,
            ficti: info.ficti,
            qualificationPicsList: [],
            thirdCatIdList: [],
            thirdCatId: 0,
            brandId: 2100000000,
            id: 0,
          };
          if (info.specType) {
            info.attrValue.forEach((row) => {
              row.quota = row.stock;
              row.attrValue = JSON.parse(row.attrValue);
              for (let attrValueKey in row.attrValue) {
                row[attrValueKey] = row.attrValue[attrValueKey];
              }
            });
            this.$nextTick(() => {
              info.attrValue.forEach((row) => {
                row.image = this.$selfUtil.setDomain(row.image);
                this.$refs.multipleTable.toggleRowSelection(row, true);
                this.$set(row, 'checked', true);
              });
            });
            this.ManyAttrValue = info.attrValue;
            this.multipleSelection = info.attrValue;
          } else {
            info.attrValue.forEach((row) => {
              row.quota = row.stock;
            });
            this.ManyAttrValue = info.attrValue;
            this.formValidate.attr = [];
          }
          this.getShopImg(info.sliderImage, info.attrValue);
        })
        .catch((res) => {
          this.fullscreenLoading = false;
        });
    },
    /**
     * 视频号转图片路径
     * @param sliderImage轮播图, attrValue规格
     */
    async getShopImg(sliderImage, attrValue) {
      let imgList = [...JSON.parse(sliderImage)];
      attrValue.map((item) => {
        imgList.push(item.image);
      });
      let attrs = [...new Set(imgList)];
      let ShopImg = [];
      attrs.map((item) => {
        ShopImg.push({
          key: item,
          img: item,
        });
      });
      let result = await this.getShopImgUpload(ShopImg, []);
      this.formValidate.images = this.formValidate.images.map((item, index) => {
        if (item === result[index].key) {
          return result[index].img;
        }
      });
      this.ManyAttrValue.map((item, index) => {
        result.map((items, index) => {
          if (item.image === items.key) {
            item.image = items.img;
          }
        });
      });
      this.fullscreenLoading = false;
    },
    async getShopImgUpload(attrs, arr) {
      for (const key in attrs) {
        let res = await this.getImgData(attrs[key].img || attrs[key].sattDir);
        arr.push({
          key: attrs[key].img || attrs[key].imgsattDir,
          img: res,
        });
      }
      return arr;
    },
    /**
     * 视频号转图片路径请求接口
     * @param imgUrl
     * @returns {Promise<any>}
     */
    getImgData(imgUrl) {
      return new Promise((resolve, reject) => {
        shopImgUploadApi({
          imgUrl: imgUrl,
          respType: 1,
          uploadType: 1,
        }).then((res) => {
          if (res.errcode > 0) {
            this.fullscreenLoading = false;
            this.$message.error(res.errmsg);
          } else {
            resolve(res.img_info.temp_img_url);
          }
        });
      });
    },
    getDraftProdect(id) {
      this.fullscreenLoading = true;
      draftInfoApi(id)
        .then(async (res) => {
          let info = res;
          this.formValidate = {
            images: JSON.parse(info.headImg),
            title: info.title,
            unitName: info.unitName,
            tempId: info.tempId,
            attr: JSON.parse(info.attr),
            descInfo: info.descInfo,
            specType: info.specType,
            primaryProductId: info.primaryProductId,
            giveIntegral: info.giveIntegral,
            ficti: info.ficti,
            thirdCatIdList: [info.catInfo.firstCatId, info.catInfo.secondCatId, info.catInfo.thirdCatId],
            thirdCatId: info.thirdCatId,
            brandId: info.brandId,
            qualificationPicsList: JSON.parse(info.qualificationPics) || [],
            id: info.productId,
          };
          if (info.specType) {
            this.ManyAttrValue = JSON.parse(info.attrValue);
            this.$nextTick(() => {
              this.ManyAttrValue.forEach((item, index) => {
                item.attrValue = JSON.parse(item.attrValue);
                for (let attrValueKey in item.attrValue) {
                  item[attrValueKey] = item.attrValue[attrValueKey];
                }
                if (item.id) {
                  this.$set(item, 'price', item.price);
                  this.$set(item, 'quota', item.quota);
                  this.$nextTick(() => {
                    this.$refs.multipleTable.toggleRowSelection(item, true);
                  });
                }
              });
            });
          } else {
            this.ManyAttrValue = JSON.parse(info.attrValue);
            this.formValidate.attr = [];
          }
          this.fullscreenLoading = false;
        })
        .catch((res) => {
          this.fullscreenLoading = false;
        });
    },
    handleSubmitNest2(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          if (this.formValidate.specType && this.multipleSelection.length === 0)
            return this.$message.warning('请选择至少一个商品属性！');
          if (this.productQualificationType === 1 && this.formValidate.qualificationPicsList.length === 0)
            return this.$message.warning('该类目必须上传商品资质图片，请上传！');
          this.currentTab++;
          this.currentTab = this.currentTab.toString();
        } else {
          return false;
        }
      });
    },
    // 提交
    handleSubmit: Debounce(function (name) {
      if (!this.formValidate.specType) {
        // this.formValidate.attr = []
        this.formValidate.attrValue = this.ManyAttrValue;
      } else {
        // this.multipleSelection.forEach((row) => {
        //   this.$set(row, 'checked', true)
        // });
        this.formValidate.attrValue = this.multipleSelection;
        this.formValidate.attrValue.forEach((item) => {
          item.attrValue = JSON.stringify(item.attrValue);
        });
      }
      this.formValidate.thirdCatId = this.formValidate.thirdCatIdList[2];
      this.formValidate.headImg = JSON.stringify(this.formValidate.images);
      this.formValidate.qualificationPics = JSON.stringify(this.formValidate.qualificationPicsList);
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.fullscreenLoading = true;
          this.loading = true;
          this.$route.params.id
            ? draftUpdateApi(this.formValidate)
                .then(async () => {
                  this.fullscreenLoading = false;
                  this.$message.success('编辑成功');
                  this.$router.push({
                    path: '/marketing/videoChannel/draftList',
                  });
                  this.$refs[name].resetFields();
                  this.formValidate.images = [];
                  this.formValidate.qualificationPics = [];
                  this.loading = false;
                })
                .catch(() => {
                  this.fullscreenLoading = false;
                  this.loading = false;
                })
            : videoAddApi(this.formValidate)
                .then(async (res) => {
                  this.fullscreenLoading = false;
                  this.$message.success('新增成功');
                  this.$router.push({
                    path: '/marketing/videoChannel/draftList',
                  });
                  this.$refs[name].resetFields();
                  this.formValidate.images = [];
                  this.formValidate.qualificationPics = [];
                  this.loading = false;
                })
                .catch(() => {
                  this.fullscreenLoading = false;
                  this.loading = false;
                });
        } else {
          if (
            !this.formValidate.storeName ||
            !this.formValidate.unitName ||
            !this.formValidate.store_info ||
            !this.formValidate.image ||
            !this.formValidate.images
          ) {
            this.$message.warning('请填写完整商品信息！');
          }
        }
      });
    }),
    handleSubmitUp() {
      this.productQualificationType = 0;
      this.formValidate.thirdCatIdList = [];
      if (this.currentTab-- < 0) this.currentTab = 0;
      this.currentTab = this.currentTab.toString();
    },
    setTagsViewTitle() {
      const title = '编辑视频号商品';
      const route = Object.assign({}, this.tempRoute, {
        title: `${title}-${this.$route.params.id}`,
      });
      this.$store.dispatch('tagsView/updateVisitedView', route);
    },
    // 移动
    handleDragStart(e, item) {
      this.dragging = item;
    },
    handleDragEnd(e, item) {
      this.dragging = null;
    },
    handleDragOver(e) {
      e.dataTransfer.dropEffect = 'move';
    },
    handleDragEnter(e, item, val) {
      e.dataTransfer.effectAllowed = 'move';
      if (item === this.dragging) {
        return;
      }
      let newItems = [];
      if (val === 'images') {
        newItems = [...this.formValidate.images];
      } else {
        newItems = [...this.formValidate.qualificationPicsList];
      }
      const src = newItems.indexOf(this.dragging);
      const dst = newItems.indexOf(item);
      newItems.splice(dst, 0, ...newItems.splice(src, 1));
      if (val === 'images') {
        this.formValidate.images = newItems;
      } else {
        this.formValidate.qualificationPicsList = newItems;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.modalBox {
  ::v-deep.el-dialog__body {
    height: 650px;
  }
}

.labeltop {
  ::v-deep el-input-number--small {
    /*width: 172px !important;*/
    min-width: 132px !important;
  }
}

.proCoupon {
  ::v-deepel-form-item__content {
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
  ::v-deepel-form-item__content {
    margin-left: 0 !important;
  }
}

.tabNumWidth {
  ::v-deepel-input-number--medium {
    width: 121px !important;
  }

  ::v-deepel-input-number__increase {
    width: 20px !important;
    font-size: 12px !important;
  }

  ::v-deepel-input-number__decrease {
    width: 20px !important;
    font-size: 12px !important;
  }

  ::v-deepel-input-number--medium .el-input__inner {
    padding-left: 25px !important;
    padding-right: 25px !important;
  }

  ::v-deep thead {
    line-height: normal !important;
  }

  ::v-deep .el-table .cell {
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

::v-deep .el-tabs__nav-scroll {
  margin-top: -20px;
}
.onePrimary {
  margin-left: 0 !important;
}
</style>
