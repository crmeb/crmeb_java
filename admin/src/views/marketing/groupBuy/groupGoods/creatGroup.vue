<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-steps :active="currentTab" align-center finish-status="success">
          <el-step title="选择拼团商品" />
          <el-step title="填写基础信息" />
          <el-step title="修改商品详情" />
        </el-steps>
      </div>
      <el-form
        ref="formValidate"
        v-loading="fullscreenLoading"
        class="formValidate mt20"
        :rules="ruleValidate"
        :model="formValidate"
        label-width="180px"
        @submit.native.prevent
      >
        <!-- 拼团商品-->
        <div v-show="currentTab === 0">
          <el-form-item label="选择商品：" prop="image">
            <div class="upLoadPicBox" @click="changeGood">
              <div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image"></div>
              <div v-else class="upLoad">
                <i class="el-icon-camera cameraIconfont"/>
              </div>
            </div>
          </el-form-item>
        </div>
        <!-- 商品信息-->
        <div v-show="currentTab === 1">
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item label="商品主图：" prop="image">
                <div class="upLoadPicBox" @click="modalPicTap('1')">
                  <div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image"></div>
                  <div v-else class="upLoad">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="商品轮播图：" prop="imagelist">
                <div class="acea-row">
                  <div
                    v-for="(item,index) in formValidate.imagelist"
                    :key="index"
                    class="pictrue"
                    draggable="true"
                    @dragstart="handleDragStart($event, item)"
                    @dragover.prevent="handleDragOver($event, item)"
                    @dragenter="handleDragEnter($event, item)"
                    @dragend="handleDragEnd($event, item)"
                  >
                    <img :src="item">
                    <i class="el-icon-error btndel" @click="handleRemove(index)" />
                  </div>
                  <div v-if="formValidate.imagelist.length<10" class="upLoadPicBox" @click="modalPicTap('2')">
                    <div class="upLoad">
                      <i class="el-icon-camera cameraIconfont" />
                    </div>
                  </div>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="拼团名称：" prop="title">
                <el-input v-model="formValidate.title" maxlength="249" placeholder="请输入拼团名称" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="拼团简介：" prop="info">
                <el-input v-model="formValidate.info"  maxlength="250" type="textarea" :rows="3" placeholder="请输入拼团简介" />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="单位：" prop="unitName">
                <el-input v-model="formValidate.unitName" placeholder="请输入单位" class="selWidthd"/>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="活动日期：" prop="timeVal">
                <el-date-picker
                  class="mr20"
                  v-model="formValidate.timeVal"
                  type="daterange"
                  value-format="yyyy-MM-dd"
                  format="yyyy-MM-dd"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  :picker-options="pickerOptions"
                  @change="onchangeTime">
                </el-date-picker>
                <span>设置活动开启结束时间，用户可以在设置时间内发起参与拼团</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="拼团时效(单位 小时)：" prop="effectiveTime">
                <el-input-number v-model="formValidate.effectiveTime" :min="1"  :step="1" step-strictly placeholder="请输入拼团人数" class="selWidthd mr20"/>
                <span>用户发起拼团后开始计时，需在设置时间内邀请到规定好友人数参团，超过时效时间，则系统判定拼团失败，自动发起退款</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="拼团人数：" prop="people">
                <el-input-number v-model="formValidate.people" :min="2"  :step="1" step-strictly placeholder="请输入拼团人数" class="selWidthd mr20"/>
                <span>单次拼团需要参与的用户数</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="购买数量限制：" prop="num">
                <el-input-number v-model="formValidate.num" :min="1"  :step="1" step-strictly placeholder="请输入帮砍次数" class="selWidthd mr20"/>
                <span>活动时间内每个用户参与拼团的次数限制。例如设置为4，表示本次活动有效期内，每个用户最多可参与购买4次</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="单次购买数量限制：" prop="onceNum">
                <el-input-number v-model="formValidate.onceNum" :min="1" :max="formValidate.num"  :step="1" step-strictly placeholder="请输入购买数量限制" class="selWidthd mr20"/>
                <span>用户参与拼团时，一次购买最大数量限制。例如设置为2，表示参与拼团时，用户一次购买数量最大可选择2个</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="补齐人数：" prop="virtualRation">
                <el-input-number v-model="formValidate.virtualRation" :min="0" :max="formValidate.people - 1"   :step="1" step-strictly placeholder="请输入补齐人数" class="selWidthd mr20"/>
                <span>当用户参与拼团后，成团时效内未成团情况下，设置补齐人数可虚拟成团。例如：成团人数为10人，补齐人数为4人，真实用户需要参与6人成团才可以在最后未成团时自动补齐虚拟人员</span>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="排序：" prop="sort">
                <el-input-number v-model="formValidate.sort" :max="9999" placeholder="请输入排序" class="selWidthd"/>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="运费模板：" prop="tempId">
                <div class="acea-row">
                  <el-select v-model="formValidate.tempId" placeholder="请选择"  class="selWidthd">
                    <el-option
                      v-for="item in shippingList"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                  <!--<el-button class="mr15" @click="addTem">添加运费模板</el-button>-->
                </div>
              </el-form-item>
            </el-col>
            <!--<el-col v-bind="grid2">-->
              <!--<el-form-item label="热门推荐：" required>-->
                <!--<el-radio-group v-model="formValidate.isHost">-->
                  <!--<el-radio :label="false" class="radio">关闭</el-radio>-->
                  <!--<el-radio :label="true">开启</el-radio>-->
                <!--</el-radio-group>-->
              <!--</el-form-item>-->
            <!--</el-col>-->
            <el-col v-bind="grid2">
              <el-form-item label="活动状态：" required>
                <el-radio-group v-model="formValidate.isShow">
                  <el-radio :label="false" class="radio">关闭</el-radio>
                  <el-radio :label="true">开启</el-radio>
                </el-radio-group>
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
                  @selection-change="handleSelectionChange">
                  <el-table-column
                    type="selection"
                    key="1"
                    v-if="formValidate.specType"
                    width="55">
                  </el-table-column>
                  <template v-if="manyTabDate && formValidate.specType">
                    <el-table-column v-for="(item,iii) in manyTabDate" :key="iii" align="center" :label="manyTabTit[iii].title" min-width="80">
                      <template slot-scope="scope">
                        <span class="priceBox" v-text="scope.row[iii]" />
                      </template>
                    </el-table-column>
                  </template>
                  <el-table-column align="center" label="图片" min-width="80">
                    <template slot-scope="scope">
                      <div class="upLoadPicBox" @click="modalPicTap('1','duo',scope.$index)">
                        <div v-if="scope.row.image" class="pictrue tabPic"><img :src="scope.row.image"></div>
                        <div v-else class="upLoad tabPic">
                          <i class="el-icon-camera cameraIconfont" />
                        </div>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column v-for="(item,iii) in attrValue" :key="iii" :label="formThead[iii].title" align="center" min-width="140">
                    <template slot-scope="scope">
                      <el-input-number
                        size="small"
                        v-if="formThead[iii].title === '拼团价'"
                        v-model="scope.row[iii]"
                        :min="0"
                        :precision="2" :step="0.1"
                        class="priceBox"
                      />
                      <el-input-number
                        size="small"
                        v-else-if="formThead[iii].title === '限量'"
                        v-model="scope.row[iii]"
                        type="number"
                        :min="1"
                        :max="scope.row.stock"
                        :step="1" step-strictly
                        class="priceBox"
                      />
                      <span v-else v-text="scope.row[iii]" class="priceBox" />
                    </template>
                  </el-table-column>
                </el-table>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <!-- 商品详情-->
        <div v-show="currentTab === 2">
          <el-form-item label="商品详情：">
            <Tinymce v-model="formValidate.content"></Tinymce>
          </el-form-item>
        </div>
        <el-form-item style="margin-top:30px;">
          <el-button
            v-show="(!$route.params.id && currentTab > 0) || ($route.params.id && currentTab===2)"
            type="primary"
            class="submission"
            size="small"
            @click="handleSubmitUp"
          >上一步</el-button>
          <el-button
            v-show="currentTab == 0"
            type="primary"
            class="submission"
            size="small"
            @click="handleSubmitNest1('formValidate')"
          >下一步</el-button>
          <el-button
            v-show="currentTab == 1"
            type="primary"
            class="submission"
            size="small"
            @click="handleSubmitNest2('formValidate')"
          >下一步</el-button>
          <el-button
            v-show="currentTab===2"
            :loading="loading"
            type="primary"
            class="submission"
            size="small"
            @click="handleSubmit('formValidate')"
            v-hasPermi="['admin:combination:update']"
          >提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getShippingList" />
  </div>
</template>

<script>
  import Tinymce from '@/components/Tinymce/index'
  import {  productDetailApi, categoryApi } from '@/api/store'
  import { shippingTemplatesList } from '@/api/logistics'
  import { getSeckillList } from '@/libs/public'
  import {  combinationSaveApi, combinationUpdateApi, combinationInfoApi } from '@/api/marketing'
  import CreatTemplates from '@/views/systemSetting/logistics/shippingTemplates/creatTemplates'
  import {formatDates} from "@/utils";
  import {Debounce} from '@/utils/validate'
  const defaultObj = {
    image: '',
    images: '',
    imagelist: [],
    title: '',
    info: '',
    num: 1,
    unitName: '',
    sort: 0,
    giveIntegral: 0,
    ficti: 0,
    isShow: false,
    tempId: '',
    attrValue: [{
      image: '',
      price: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      quota: 1,
      weight: 0,
      volume: 0,
      barCode: ''
    }],
    attr: [],
    selectRule: '',
    content: '',
    specType: false,
    id: 0,
    // productId: 0,
    startTime: '',
    stopTime: '',
    timeVal: [],
    effectiveTime: 0,
    people: 2,
    virtualRation: 0
  }
  const objTitle = {
    price: {
      title: '拼团价'
    },
    cost: {
      title: '成本价'
    },
    otPrice: {
      title: '原价'
    },
    stock: {
      title: '库存'
    },
    quota: {
      title: "限量",
    },
    barCode: {
      title: '商品编号'
    },
    weight: {
      title: '重量（KG）'
    },
    volume: {
      title: '体积(m³)'
    }
  }
  export default {
    name: "creatSeckill",
    components: { CreatTemplates,Tinymce },
    data() {
      return {
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() < new Date().setTime(new Date().getTime() - 3600 * 1000 * 24);
          }
        },
        props2: {
          children: 'child',
          label: 'name',
          value: 'id',
          multiple: true,
          emitPath: false
        },
        grid2: {
          xl: 8,
          lg: 10,
          md: 12,
          sm: 24,
          xs: 24
        },
        currentTab: 0,
        formThead: Object.assign({}, objTitle),
        formValidate: Object.assign({}, defaultObj),
        loading: false,
        fullscreenLoading: false,
        merCateList: [], // 商户分类筛选
        shippingList: [], // 运费模板
        seckillTime: [],
        ruleValidate: {
          productId: [
            { required: true, message: '请选择商品', trigger: 'change' }
          ],
          title: [
            { required: true, message: '请输入商品标题', trigger: 'blur' }
          ],
          attrValue: [
            { required: true, message: '请选择商品属相', trigger: 'change', type: 'array', min: '1' }
          ],
          num: [
            { required: true, message: '请输入购买数量限制', trigger: 'blur' }
          ],
          unitName: [
            { required: true, message: '请输入单位', trigger: 'blur' }
          ],
          info: [
            { required: true, message: '请输入拼团商品简介', trigger: 'blur' }
          ],
          tempId: [
            { required: true, message: '请选择运费模板', trigger: 'change' }
          ],
          image: [
            { required: true, message: '请上传商品图', trigger: 'change' }
          ],
          imagelist: [
            { required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }
          ],
          specType: [
            { required: true, message: '请选择商品规格', trigger: 'change' }
          ],
          timeVal:[
            { required: true, message: '请选择活动日期', trigger: 'change', type: 'array'}
          ],
          virtualRation:[
            { required: true, message: '请输入补齐人数', trigger: 'blur'}
          ],
          onceNum: [
            { required: true, message: '请输入单次购买数量限制', trigger: 'blur'}
          ],
          people: [
            { required: true, message: '请输入拼团人数', trigger: 'blur'}
          ],
          effectiveTime: [
            { required: true, message: '请输入拼团时效', trigger: 'blur'}
          ]
        },
        manyTabDate: {},
        manyTabTit: {},
        attrInfo: {},
        tempRoute: {},
        multipleSelection: [],
        productId: 0,
        radio: '',
        ManyAttrValue: [Object.assign({}, defaultObj.attrValue[0])], // 多规格
      }
    },
    computed: {
      attrValue() {
        const obj = Object.assign({}, defaultObj.attrValue[0])
        delete obj.image
        return obj
      }
    },
    created() {
      this.$watch('formValidate.attr', this.watCh)
      this.tempRoute = Object.assign({}, this.$route)
    },
    mounted() {
      getSeckillList(1).then((res) => {
        this.seckillTime = res.list
      })
     this.formValidate.imagelist = []
      if ( this.$route.params.id ) {
        this.setTagsViewTitle()
        this.getInfo()
        this.currentTab = 1
      }
      this.getShippingList()
      this.getCategorySelect()
    },
    methods: {
      handleSelectionChange(val) {
        val.map(item => {
          item.checked = true
        })
        this.multipleSelection = val;
      },
      watCh(val) {
        const tmp = {}
        const tmpTab = {}
        this.formValidate.attr.forEach((o, i) => {
          // tmp['value' + i] = { title: o.attrName }
          // tmpTab['value' + i] = ''
          tmp[o.attrName] = { title: o.attrName };
          tmpTab[o.attrName] = '';
        })
        this.manyTabTit = tmp
        this.manyTabDate = tmpTab
        this.formThead = Object.assign({}, this.formThead, tmp)
      },
      handleRemove (i) {
        this.formValidate.imagelist.splice(i, 1)
      },
      // 点击商品图
      modalPicTap (tit, num, i) {
        const _this = this
        this.$modalUpload(function(img) {
          if(tit==='1'&& !num){
            _this.formValidate.image = img[0].sattDir
            _this.ManyAttrValue[0].image = img[0].sattDir
          }
          if(tit==='2'&& !num){
            if(img.length>10) return this.$message.warning("最多选择10张图片！");
            if(img.length + _this.formValidate.imagelist.length > 10) return this.$message.warning("最多选择10张图片！");
            img.map((item) => {
              _this.formValidate.imagelist.push(item.sattDir)
            });
          }
          if(tit==='1'&& num === 'duo' ){
            _this.ManyAttrValue[i].image = img[0].sattDir
          }
        },tit, 'content')

      },
      // 具体日期
      onchangeTime(e) {
        this.formValidate.timeVal = e;
        this.formValidate.startTime = e ? e[0] : "";
        this.formValidate.stopTime = e ? e[1] : "";
      },
      changeGood(){
        const _this = this
        this.$modalGoodList(function(row) {
          _this.formValidate.image = row.image
          _this.productId = row.id
        })
      },
      handleSubmitNest1() {
        if (!this.formValidate.image) {
          return this.$message.warning("请选择商品！");
        } else {
          this.currentTab++;
          if (!this.$route.params.id) this.getProdect(this.productId);
        }
      },
      // 商品分类；
      getCategorySelect() {
        categoryApi({ status: -1, type: 1 }).then(res => {
          this.merCateList = this.filerMerCateList(res)
        })
      },
      filerMerCateList(treeData) {
        return treeData.map((item) => {
          if(!item.child){
            item.disabled = true
          }
          item.label = item.name
          return item
        })
      },
      // 运费模板；
      getShippingList() {
        shippingTemplatesList(this.tempData).then(res => {
          this.shippingList = res.list
        })
      },
      // 运费模板
      addTem() {
        this.$refs.addTemplates.dialogVisible = true
        this.$refs.addTemplates.getCityList()
      },
      // 商品详情
      getInfo () {
        if(!this.$route.params.id){
          this.getProdect(this.productId)
        }else{
          this.getSekllProdect(this.$route.params.id)
        }
      },
      getProdect(id) {
        this.fullscreenLoading = true
        productDetailApi(id).then(async res => {
          this.formValidate = {
            image: this.$selfUtil.setDomain(res.image),
            imagelist: JSON.parse(res.sliderImage),
            title: res.storeName,
            info: res.storeInfo,
            quota: '',
            unitName: res.unitName,
            sort: res.sort,
            tempId: res.tempId,
            attr: res.attr,
            selectRule: res.selectRule,
            content: res.content,
            specType: res.specType,
            productId: res.id,
            giveIntegral: res.giveIntegral,
            ficti: res.ficti,
            startTime: res.startTime || '',
            stopTime: res.stopTime || '',
            timeVal: [],
            status: 0,
              isShow: false,
            num : 1,
            isHost : false,
            people : 2,
            onceNum : 1,
            virtualRation : '',
            effectiveTime : 0,
            isPostage: false
          }
          if(res.specType){
            res.attrValue.forEach((row) => {
              row.quota = row.stock;
              row.attrValue = JSON.parse(row.attrValue);
              for (let attrValueKey in row.attrValue) {
                row[attrValueKey] = row.attrValue[attrValueKey];
              }
            });
            this.$nextTick(() => {
              res.attrValue.forEach((row) => {
                row.image = this.$selfUtil.setDomain(row.image)
                this.$refs.multipleTable.toggleRowSelection(row, true);
                this.$set(row, 'checked', true)
              });
            });
            this.ManyAttrValue = res.attrValue
            this.multipleSelection = res.attrValue
          }else{
            res.attrValue.forEach((row) => {
              row.quota = row.stock;
            });
            this.ManyAttrValue = res.attrValue
            this.formValidate.attr = res.attr
          }
          this.fullscreenLoading = false
        }).catch(res => {
          this.fullscreenLoading = false
        })
      },
      getSekllProdect(id) {
        this.fullscreenLoading = true
        combinationInfoApi({id:id}).then(async res => {
          this.formValidate = {
            image: this.$selfUtil.setDomain(res.image),
            imagelist: JSON.parse(res.sliderImage),
            title: res.title,
            info: res.info,
            unitName: res.unitName,
            sort: res.sort,
            tempId: res.tempId,
            attr: res.attr,
            selectRule: res.selectRule,
            content: res.content,
            specType: res.specType,
            productId: res.productId,
            giveIntegral: res.giveIntegral,
            ficti: res.ficti,
            // timeVal: res.startTimeStr && res.stopTimeStr ? [res.startTimeStr, res.stopTimeStr] : [],
            timeVal: res.startTime && res.stopTime ? [formatDates(new Date(res.startTime), 'yyyy-MM-dd'), formatDates(new Date(res.stopTime), 'yyyy-MM-dd')] : [],
            status: res.status,
            isShow: res.isShow,
            num : res.num,
            isHost : res.isHost,
            people : res.people,
            onceNum : res.onceNum,
            virtualRation : res.virtualRation,
            effectiveTime : res.effectiveTime,
            isPostage: false,
            startTime: res.startTime || '',
            stopTime: res.stopTime || '',
            id: res.id
          }
          if(res.specType){
            this.ManyAttrValue = res.attrValue;
            this.$nextTick(() => {
              this.ManyAttrValue.forEach((item, index) => {
                item.image = this.$selfUtil.setDomain(item.image)
                item.attrValue = JSON.parse(item.attrValue);
                for (let attrValueKey in item.attrValue) {
                  item[attrValueKey] = item.attrValue[attrValueKey];
                }
                if (item.id) {
                  this.$set(item, 'price', item.price)
                  this.$set(item, 'quota', item.quota)
                  this.$nextTick(() => {
                    this.$refs.multipleTable.toggleRowSelection(item, true)
                  })
                }
              })
            });
          }else{
            this.ManyAttrValue = res.attrValue
            // this.formValidate.attr = []
          }
          this.fullscreenLoading = false
        }).catch(res => {
          this.fullscreenLoading = false
        })
      },
      handleSubmitNest2(name) {
        this.$refs[name].validate((valid) => {
          if (valid) {
            if(this.formValidate.specType && this.multipleSelection.length ===0 ) return this.$message.warning("请选择至少一个商品属性！");
            this.currentTab++;
          } else {
            return false;
          }
        });
      },
      // 提交
      handleSubmit:Debounce(function(name) {
        if(!this.formValidate.specType){
          // this.formValidate.attr = []
          this.formValidate.attrValue = this.ManyAttrValue
        }else{
          this.formValidate.attrValue = this.multipleSelection;
        }
        this.formValidate.attrValue.forEach(item=>{
          item.attrValue = JSON.stringify(item.attrValue);
        });
        this.formValidate.images = JSON.stringify(this.formValidate.imagelist);
        this.formValidate.startTime = this.formValidate.timeVal[0];
        this.formValidate.stopTime = this.formValidate.timeVal[1];
        // this.formValidate.virtualRation = Math.floor((this.formValidate.people - this.formValidate.peopleNum) / this.formValidate.people * 100)
        this.$refs[name].validate((valid) => {
          if (valid) {
            this.fullscreenLoading = true;
            this.loading = true;
            this.$route.params.id
              ? combinationUpdateApi({id: this.$route.params.id}, this.formValidate)
                .then(async () => {
                  this.fullscreenLoading = false;
                  this.$message.success('编辑成功');
                  this.$router.push({
                    path: "/marketing/groupBuy/groupGoods",
                  });
                  this.$refs[name].resetFields();
                  this.formValidate.imagelist = [];
                  this.loading = false;
                })
                .catch(() => {
                  this.fullscreenLoading = false;
                  this.loading = false;
                })
              : combinationSaveApi(this.formValidate)
                .then(async (res) => {
                  this.fullscreenLoading = false;
                  this.$message.success('新增成功');
                  this.$router.push({
                    path: "/marketing/groupBuy/groupGoods",
                  });
                  this.$refs[name].resetFields();
                  this.formValidate.imagelist = [];
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
              this.$message.warning("请填写完整商品信息！");
            }
          }
        });

      }),
      handleSubmitUp() {
        if (this.currentTab-- < 0) this.currentTab = 0;
      },
      setTagsViewTitle() {
        const title = '编辑拼团商品'
        const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.$route.params.id}` })
        this.$store.dispatch('tagsView/updateVisitedView', route)
      },
      // 移动
      handleDragStart (e, item) {
        this.dragging = item;
      },
      handleDragEnd (e, item) {
        this.dragging = null
      },
      handleDragOver (e) {
        e.dataTransfer.dropEffect = 'move'
      },
      handleDragEnter (e, item) {
        e.dataTransfer.effectAllowed = 'move'
        if (item === this.dragging) {
          return
        }
        const newItems = [...this.formValidate.imagelist]
        const src = newItems.indexOf(this.dragging)
        const dst = newItems.indexOf(item)
        newItems.splice(dst, 0, ...newItems.splice(src, 1))
        this.formValidate.imagelist = newItems;
      }
    }
  }
</script>

<style scoped lang="scss">
  .inpBox{
    ::v-deep.el-form-item__error {
      color: #ff4949;
      font-size: 12px;
      line-height: 20px;
      padding-top: 4px;
      position: static !important;
      /* position: absolute; */
      /* top: 100%; */
      /* left: 0; */
    }
  }
  .labeltop{
    ::v-deep.el-input-number--small{
      /*width: 172px !important;*/
      min-width: 132px !important;
    }
  }

  .proCoupon{
    ::v-deep.el-form-item__content{
      margin-top: 5px;
    }
  }
  .tabPic{
    width: 40px !important;
    height: 40px !important;
    img{
      width: 100%;
      height: 100%;
    }
  }
  .noLeft{
    ::v-deep.el-form-item__content{
      margin-left: 0 !important;
    }
  }
  .tabNumWidth{
    ::v-deep.el-input-number--medium{
      width: 121px !important;
    }
    ::v-deep.el-input-number__increase{
      width: 20px !important;
      font-size: 12px !important;
    }
    ::v-deep.el-input-number__decrease{
      width: 20px !important;
      font-size: 12px !important;
    }
    ::v-deep.el-input-number--medium .el-input__inner {
      padding-left: 25px !important;
      padding-right: 25px !important;
    }
    ::v-deep thead{
      line-height: normal !important;
    }
    ::v-deep .el-table .cell{
      line-height: normal !important;
    }
  }
  .selWidth{
    width: 80%;
  }
  .selWidthd{
    width: 350px;
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
  .pictrue{
    width: 60px;
    height: 60px;
    border: 1px dotted rgba(0,0,0,0.1);
    margin-right: 10px;
    position: relative;
    cursor: pointer;
    img{
      width: 100%;
      height: 100%;
    }
  }
  .btndel{
    position: absolute;
    z-index: 1;
    width :20px !important;
    height: 20px !important;
    left: 46px;
    top: -4px;
  }
  .labeltop{
    ::v-deep.el-form-item__label{
      /*float: none !important;*/
      /*display: inline-block !important;*/
      /*margin-left: 120px !important;*/
      /*width: auto !important;*/
    }
  }
</style>
