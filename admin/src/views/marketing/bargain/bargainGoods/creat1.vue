<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-steps :active="currentTab" align-center finish-status="success">
          <el-step title="选择秒杀商品" />
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
        label-width="150px"
        @submit.native.prevent
      >
        <!-- 砍价商品-->
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
              <el-form-item label="商品轮播图：" prop="images">
                <div class="acea-row">
                  <div
                    v-for="(item,index) in formValidate.imagess"
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
                  <div v-if="formValidate.imagess.length<10" class="upLoadPicBox" @click="modalPicTap('2')">
                    <div class="upLoad">
                      <i class="el-icon-camera cameraIconfont" />
                    </div>
                  </div>
                </div>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="砍价活动名称：" prop="title">
                <el-input v-model="formValidate.title" placeholder="请输入商品名称" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="砍价活动简介：">
                <el-input v-model="formValidate.info"  maxlength="250" type="textarea" :rows="3" placeholder="请输入商品简介" />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="单位：" prop="unitName">
                <el-input v-model="formValidate.unitName" placeholder="请输入单位" class="selWidthd"/>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="排序：" prop="sort">
                <el-input v-model="formValidate.sort" placeholder="请输入排序" class="selWidthd"/>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="活动时间：">
                <el-date-picker
                  class="mr20"
                  v-model="timeVal"
                  type="daterange"
                  value-format="yyyy-MM-dd"
                  format="yyyy-MM-dd"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  @change="onchangeTime">
                </el-date-picker>
                <span>设置活动开启结束时间，用户可以在设置时间内发起参与砍价</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="砍价人数：" prop="peopleNum">
                <el-input-number v-model="formValidate.peopleNum" :min="1"  :step="1" step-strictly step-strictly placeholder="请输入砍价人数" class="selWidthd mr20"/>
                <span>需邀请多少人砍价成功</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="帮砍次数：" prop="bargainNum">
                <el-input-number v-model="formValidate.bargainNum" :min="1"  :step="1" step-strictly placeholder="请输入帮砍次数" class="selWidthd mr20"/>
                <span>单个商品用户可以帮砍的次数，例：次数设置为1，甲和乙同时将商品A的砍价链接发给丙，丙只能帮甲或乙其中一个人砍价</span>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="购买数量限制：" prop="num">
                <el-input-number v-model="formValidate.num" :min="1"  :step="1" step-strictly placeholder="请输入购买数量限制" class="selWidthd mr20"/>
                <span>单个活动每个用户发起砍价次数限制</span>
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
            <el-col :span="24">
              <el-form-item label="活动状态：" required>
                <el-radio-group v-model="formValidate.status">
                  <el-radio :label="0" class="radio">关闭</el-radio>
                  <el-radio :label="1">开启</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <!-- 规格表格-->
            <el-col :span="24">
              <el-form-item label="商品属性：" class="labeltop" required>
                <el-table
                  ref="multipleTable"
                  :data="formValidate.ManyAttrValue"
                  tooltip-effect="dark"
                  style="width: 100%">
                  <el-table-column label="选择" min-width="60">
                    <template slot-scope="scope">
                      <el-radio v-model="radio" :label="scope.row"
                                @change.native="changeType(scope.row)"
                      >&nbsp;
                      </el-radio>
                    </template>
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
                    <template slot-scope="{row,$index}">
                      <template v-if="formThead[iii].title === '砍价起始金额'">
                        <el-form-item
                          :prop="`multipleSelection.${$index}.price`"
                          :rules="ruleValidate.price"
                          class="inpBox"
                        >
                          <el-input-number
                            size="small"
                            v-model="row[iii]"
                            :min="0"
                            :precision="2" :step="0.1"
                            class="priceBox"
                          />
                        </el-form-item>
                      </template>
                      <template  v-else-if="formThead[iii].title === '砍价最低价'">
                        <el-form-item
                          :prop="`ManyAttrValue.${$index}.minPrice`"
                          :rules="ruleValidate.minPrice"
                          class="inpBox"
                        >
                          <el-input-number
                            size="small"
                            v-model="row[iii]"
                            :min="0"
                            :max="parseFloat(row.price)"
                            :precision="2" :step="0.1"
                            class="priceBox"
                          />
                        </el-form-item>
                      </template>
                      <template  v-else-if="formThead[iii].title === '限量'">
                        <el-form-item
                          :prop="`ManyAttrValue.${$index}.quota`"
                          :rules="ruleValidate.quota"
                          class="inpBox"
                        >
                          <el-input-number
                            size="small"
                            v-model="row[iii]"
                            type="number"
                            :min="1"
                            :max="row.stock"
                            :step="1" step-strictly
                            class="priceBox"
                            @change="inpChange(row[iii], row.id)"
                          />
                        </el-form-item>
                      </template>
                      <span v-else v-text="row[iii]" class="priceBox" />
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
            <ueditor-from v-model="formValidate.content" :content="formValidate.content" />
          </el-form-item>
          <el-form-item label="商品规则：">
            <ueditor-from v-model="formValidate.rule" :content="formValidate.rule" />
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
          >提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getShippingList" />
  </div>
</template>

<script>
  import {  productDetailApi, categoryApi } from '@/api/store'
  import { shippingTemplatesList } from '@/api/logistics'
  import { getSeckillList } from '@/libs/public'
  import {  bargainSaveApi, bargainUpdateApi, bargainInfoApi } from '@/api/marketing'
  import CreatTemplates from '@/views/systemSetting/logistics/shippingTemplates/creatTemplates'
  const defaultObj = {
    image: '',
    images: '',
    imagess: [],
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
      minPrice: 0,
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
    timeId: '',
    startTime: '',
    stopTime: '',
    timeVal: [],
    status: 0,
    rule: '',
    ManyAttrValue: [{
      image: '',
      price: 0,
      minPrice: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      quota: 1,
      weight: 0,
      volume: 0,
      barCode: ''
    }], // 多规格
    multipleSelection: [{
      image: '',
      price: 0,
      minPrice: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      quota: 1,
      weight: 0,
      volume: 0,
      barCode: ''
    }]
  }
  const objTitle = {
    price: {
      title: '砍价起始金额'
    },
    minPrice: {
      title: '砍价最低价'
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
    weight: {
      title: '重量（KG）'
    },
    volume: {
      title: '体积(m³)'
    },
    barCode: {
      title: '商品编号'
    }
  }
  export default {
    name: "creatSeckill",
    components: { CreatTemplates },
    data() {
      return {
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
        formValidate: {},
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
            { required: true, message: '请输入秒杀商品简介', trigger: 'blur' }
          ],
          tempId: [
            { required: true, message: '请选择运费模板', trigger: 'change' }
          ],
          timeId: [
            { required: true, message: '请选择活动时间', trigger: 'change' }
          ],
          image: [
            { required: true, message: '请上传商品图', trigger: 'change' }
          ],
          imagess: [
            { required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }
          ],
          specType: [
            { required: true, message: '请选择商品规格', trigger: 'change' }
          ],
          timeVal:[
            { required: true, message: '请选择活动日期', trigger: 'change', type: 'array'}
          ],
          bargainNum:[
            { required: true, message: '请输入帮砍次数', trigger: 'blur'}
          ],
          peopleNum: [
            { required: true, message: '请输入砍价人数', trigger: 'blur'}
          ],
          price: [
            { required: true, message: '请输入砍价起始金额', trigger: 'blur'}
          ],
          minPrice: [
            { required: true, message: '请输入砍价最低金额', trigger: 'blur'}
          ],
          quota: [
            { required: true, message: '请输入限量', trigger: 'blur'}
          ]
        },
        manyTabDate: {},
        manyTabTit: {},
        attrInfo: {},
        tempRoute: {},
        multipleSelection: [Object.assign({}, defaultObj.attrValue[0])],
        productId: 0,
        timeVal: [],
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
      this.formValidate.imagess = []
      if ( this.$route.params.id ) {
        this.setTagsViewTitle()
        this.getInfo()
        this.currentTab = 1
      }
      this.getShippingList()
      this.getCategorySelect()
    },
    methods: {
      changeType(row, index){
        row.checked = true;
        this.formValidate.multipleSelection = [row];
      },
      inpChange(currentValue, id){
        // this.ManyAttrValue.map(item => {
        //    if(!currentValue && item.id ===id){
        //      item.quota = 1
        //      this.$set(item, 'quota', 1)
        //      this.ManyAttrValue = Object.assign([], this.ManyAttrValue)
        //    }
        // })

        console.log(this.ManyAttrValue)
        // if(!currentValue) item.quota = 1
      },
      watCh(val) {
        const tmp = {}
        const tmpTab = {}
        this.formValidate.attr.forEach((o, i) => {
          tmp['value' + i] = { title: o.attrName }
          tmpTab['value' + i] = ''
        })
        this.manyTabTit = tmp
        this.manyTabDate = tmpTab
        this.formThead = Object.assign({}, this.formThead, tmp)
      },
      handleRemove (i) {
        this.formValidate.imagess.splice(i, 1)
      },
      // 点击商品图
      modalPicTap (tit, num, i) {
        const _this = this
        this.$modalUpload(function(img) {
          if(tit==='1'&& !num){
            _this.formValidate.image = img[0].sattDir
            _this.formValidate.ManyAttrValue[0].image = img[0].sattDir
          }
          if(tit==='2'&& !num){
            if(img.length>10) return this.$message.warning("最多选择10张图片！");
            if(img.length + _this.formValidate.imagess.length > 10) return this.$message.warning("最多选择10张图片！");
            img.map((item) => {
              _this.formValidate.imagess.push(item.sattDir)
            });
          }
          if(tit==='1'&& num === 'duo' ){
            _this.specType ? _this.formValidate.ManyAttrValue[i].image = img[0].sattDir : _this.formValidate.ManyAttrValue[0].image = img[0].sattDir
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
          // _this.formValidate.productId = row.id
        })
      },
      handleSubmitNest1() {
        if (!this.formValidate.image) {
          this.$message.warning("请选择商品！");
          return;
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
          let info = res
          this.formValidate = info;
          this.formValidate.imagess = JSON.parse(info.sliderImage);
          this.formValidate.title = info.storeName;
          this.formValidate.proName = info.storeName;
          this.formValidate.info = info.storeInfo;
          this.formValidate.productId = info.id;
          this.formValidate.timeId = this.$route.params.id ? Number(info.timeId) : this.$route.params.timeId ? Number(this.$route.params.timeId) : '';
          this.formValidate.startTime = info.startTime || '';
          this.formValidate.stopTime = info.stopTime || '';
          this.formValidate.timeVal = [];
          this.formValidate.status = 0;
          this.formValidate.num = 1;
          this.formValidate.rule = '';
          this.formValidate.peopleNum = 1;
          this.formValidate.bargainNum = 1;
          if(info.specType){
            info.attrValues.forEach((row) => {
              row.quota = row.stock;
            });
            this.formValidate.ManyAttrValue = info.attrValues
            this.multipleSelection = info.attrValues
          }else{
            info.attrValue.forEach((row) => {
              row.quota = row.stock;
            });
            this.formValidate.ManyAttrValue = info.attrValue
            this.radio = info.attrValue[0]
            this.formValidate.attr = []
          }
          this.fullscreenLoading = false
        }).catch(res => {
          this.fullscreenLoading = false
        })
      },
      getSekllProdect(id) {
        this.fullscreenLoading = true
        bargainInfoApi({id:id}).then(async res => {
          let info = res;
          this.formValidate = info;
          this.formValidate.proName = info.title;
          this.formValidate.imagess = JSON.parse(info.sliderImage);
          this.formValidate.timeId = Number(info.timeId);
          this.timeVal = info.startTime && info.stopTime ? [info.startTime, info.stopTime] : []
          if(info.specType){
            this.formValidate.ManyAttrValue = info.attrValues;
            this.$nextTick(() => {
              this.formValidate.ManyAttrValue.forEach((item, index) => {
                if (item.checked) {
                  this.$set(item, 'price', item.price)
                  this.$set(item, 'quota', item.quota)
                  this.radio = item
                }
              })
            });
          }else{
            this.formValidate.ManyAttrValue = info.attrValue;
            this.formValidate.attr = [];
            this.radio = info.attrValue[0];
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
            return this.$message.warning("请填写完整信息！");
            this.currentTab++;
            // this.multipleSelection.every((value, index) => {
            //   if(!value.quota){
            //     this.$message.warning("请填选择至少一个商品属性！");
            //     return false;
            //   }else{
            //    this.currentTab++;
            //    return true;
            //   }
            // });
            // for(i = 0; i < this.multipleSelection.length; i++){
            //   if(!this.multipleSelection[i].quota){
            //     break;
            //   }else{
            //     this.currentTab++;
            //   }
            // }

          } else {
            return false;
          }
        });
      },
      // 提交
      handleSubmit(name) {
        if(!this.formValidate.specType){
          this.formValidate.attr = []
          this.formValidate.attrValue = this.formValidate.ManyAttrValue
        }else{
          this.formValidate.attrValue = this.multipleSelection
        }
        this.formValidate.images = JSON.stringify(this.formValidate.imagess)
        this.$refs[name].validate((valid) => {
          if (valid) {
            this.fullscreenLoading = true;
            this.loading = true;
            this.$route.params.id
              ? bargainUpdateApi({id: this.$route.params.id}, this.formValidate)
                .then(async () => {
                  this.fullscreenLoading = false;
                  this.$message.success('编辑成功');
                  this.$router.push({
                    path: "/marketing/bargain/bargainGoods",
                  });
                  this.$refs[name].resetFields();
                  this.formValidate.images = [];
                  this.loading = false;
                })
                .catch(() => {
                  this.fullscreenLoading = false;
                  this.loading = false;
                })
              : bargainSaveApi(this.formValidate)
                .then(async (res) => {
                  this.fullscreenLoading = false;
                  this.$message.success('新增成功');
                  this.$router.push({
                    path: "/marketing/bargain/bargainGoods",
                  });
                  this.$refs[name].resetFields();
                  this.formValidate.images = [];
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

      },
      handleSubmitUp() {
        if (this.currentTab-- < 0) this.currentTab = 0;
      },
      setTagsViewTitle() {
        const title = '编辑秒杀商品'
        const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.$route.params.id}` })
        this.$store.dispatch('tagsView/updateVisitedView', route)
      },
    }
  }
</script>

<style scoped lang="scss">
  .inpBox{
    /deep/.el-form-item__error {
      line-height: 20px !important;
      position: static !important;
    }
  }
  .labeltop{
    /deep/.el-input-number--small{
      /*width: 172px !important;*/
      min-width: 132px !important;
    }
  }

  .proCoupon{
    /deep/.el-form-item__content{
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
    /deep/.el-form-item__content{
      margin-left: 0 !important;
    }
  }
  .tabNumWidth{
    /deep/.el-input-number--medium{
      width: 121px !important;
    }
    /deep/.el-input-number__increase{
      width: 20px !important;
      font-size: 12px !important;
    }
    /deep/.el-input-number__decrease{
      width: 20px !important;
      font-size: 12px !important;
    }
    /deep/.el-input-number--medium .el-input__inner {
      padding-left: 25px !important;
      padding-right: 25px !important;
    }
    /deep/ thead{
      line-height: normal !important;
    }
    /deep/ .el-table .cell{
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
    /deep/.el-form-item__label{
      /*float: none !important;*/
      /*display: inline-block !important;*/
      /*margin-left: 120px !important;*/
      /*width: auto !important;*/
    }
  }
</style>
