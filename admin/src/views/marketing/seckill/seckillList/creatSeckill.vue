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
        <!-- 秒杀商品-->
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
              <el-form-item label="商品轮播图：" prop="imagess">
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
              <el-form-item label="商品标题：" prop="title">
                <el-input v-model="formValidate.title" maxlength="249" placeholder="请输入商品名称" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="秒杀活动简介：">
                <el-input v-model="formValidate.info" type="textarea" maxlength="250"  :rows="3" placeholder="请输入商品简介" />
              </el-form-item>
            </el-col>
            <!--<el-col v-bind="grid2">-->
              <!--<el-form-item label="商品分类：" prop="cateIds">-->
                <!--<el-cascader v-model="formValidate.cateIds" :options="merCateList" :props="props2" clearable class="selWidth" :show-all-levels="false" />-->
              <!--</el-form-item>-->
            <!--</el-col>-->
            <el-col v-bind="grid2">
              <el-form-item label="单位：" prop="unitName">
                <el-input v-model="formValidate.unitName" placeholder="请输入单位" class="selWidth"/>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="运费模板：" prop="tempId">
                <div class="acea-row">
                  <el-select v-model="formValidate.tempId" placeholder="请选择"  class="selWidth">
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
            <el-col v-bind="grid2">
              <el-form-item label="当天参与活动次数：" prop="num">
                <el-input-number v-model="formValidate.num" :step="1" step-strictly :min="1" placeholder="请输入活动次数" class="selWidth"/>
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="活动日期：" prop="timeVal">
                <el-date-picker
                  class="selWidth"
                  v-model="formValidate.timeVal"
                  value-format="yyyy-MM-dd"
                  format="yyyy-MM-dd"
                  size="small"
                  type="daterange"
                  placement="bottom-end"
                  :picker-options="pickerOptions"
                  placeholder="请选择活动日期"
                  @change="onchangeTime"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid2">
              <el-form-item label="活动时间：" prop="timeId">
                <el-select v-model="formValidate.timeId" placeholder="请选择" class="selWidth">
                  <el-option
                    v-for="item in seckillTime"
                    :key="item.id"
                    :label="item.time"
                    :value="item.id">
                  </el-option>
                </el-select>
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
                        v-if="formThead[iii].title === '秒杀价'"
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
                        @change="inpChange(scope.row[iii], scope.row.id)"
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
        <div v-show="currentTab === 2">
          <el-form-item label="商品详情：">
            <ueditor-from v-model="formValidate.content" :content="formValidate.content" />
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
  import {  seckillStoreSaveApi, seckillStoreUpdateApi, seckillStoreInfoApi } from '@/api/marketing'
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
      cost: 0,
      otPrice: 0,
      stock: 0,
      quota: 1,
      barCode: '',
      weight: 0,
      volume: 0,
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
    status: 0
  }
  const objTitle = {
    price: {
      title: '秒杀价'
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
    components: { CreatTemplates },
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
            { required: true, message: '请输入活动次数', trigger: 'blur' }
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
          ]
        },
        manyTabDate: {},
        manyTabTit: {},
        attrInfo: {},
        tempRoute: {},
        multipleSelection: [],
        productId: 0,
        OneattrValue: [Object.assign({}, defaultObj.attrValue[0])], // 单规格
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
      handleSelectionChange(val) {
        this.multipleSelection = val;
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
            if(img.length + _this.formValidate.imagess.length > 10) return this.$message.warning("最多选择10张图片！");
            img.map((item) => {
              _this.formValidate.imagess.push(item.sattDir)
            });
          }
          if(tit==='1'&& num === 'duo' ){
            _this.specType ? _this.ManyAttrValue[i].image = img[0].sattDir : _this.ManyAttrValue[0].image = img[0].sattDir
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
          this.formValidate = {
            image: info.image,
            imagess: JSON.parse(info.sliderImage),
            title: info.storeName,
            info: info.storeInfo,
            quota: '',
            unitName: info.unitName,
            sort: info.sort,
            isShow: info.isShow,
            tempId: info.tempId,
            attr: info.attr,
            selectRule: info.selectRule,
            content: info.content,
            specType: info.specType,
            productId: info.id,
            giveIntegral: info.giveIntegral,
            ficti: info.ficti,
            timeId: this.$route.params.id ? Number(info.timeId) : this.$route.params.timeId ? Number(this.$route.params.timeId) : '',
            startTime: info.startTime || '',
            stopTime: info.stopTime || '',
            timeVal: [],
            status: 0,
            num: 1
          }
          if(info.specType){
            info.attrValues.forEach((row) => {
              row.quota = row.stock;
            });
            this.$nextTick(() => {
              info.attrValues.forEach((row) => {
                this.$refs.multipleTable.toggleRowSelection(row, true);
                this.$set(row, 'checked', true)
              });
            });
            this.ManyAttrValue = info.attrValues
            this.multipleSelection = info.attrValues
          }else{
            info.attrValue.forEach((row) => {
              row.quota = row.stock;
            });
           this.ManyAttrValue = info.attrValue
           this.formValidate.attr = []
          }
          this.fullscreenLoading = false
        }).catch(res => {
          this.fullscreenLoading = false
        })
      },
      getSekllProdect(id) {
        this.fullscreenLoading = true
        seckillStoreInfoApi({id:id}).then(async res => {
          let info = res
          this.formValidate = {
            image: info.image,
            imagess: JSON.parse(info.sliderImage) || [],
            title: info.title,
            info: info.info,
            quota: info.quota,
            unitName: info.unitName,
            sort: info.sort,
            isShow: info.isShow,
            tempId: info.tempId,
            attr: info.attr,
            selectRule: info.selectRule,
            content: info.content,
            specType: info.specType,
            productId: info.productId,
            giveIntegral: info.giveIntegral,
            ficti: info.ficti,
            timeId: Number(info.timeId),
            startTime: info.startTime || '',
            stopTime: info.stopTime || '',
            status: info.status,
            num: info.num,
            timeVal: info.startTime && info.stopTime ? [info.startTime, info.stopTime] : []
          }
          if(info.specType){
            this.ManyAttrValue = info.attrValues;
            this.$nextTick(() => {
              this.ManyAttrValue.forEach((item, index) => {
                if (item.checked) {
                  this.$set(item, 'price', item.price)
                  this.$set(item, 'quota', item.quota)
                  this.$nextTick(() => {
                    this.$refs.multipleTable.toggleRowSelection(item, true)
                  })
                }
              })
            });
          }else{
            this.ManyAttrValue = info.attrValue
            this.formValidate.attr = []
          }
          this.fullscreenLoading = false
        }).catch(res => {
          this.fullscreenLoading = false
        })
      },
      handleSubmitNest2(name) {
        this.$refs[name].validate((valid) => {
          if (valid) {
            if(this.formValidate.specType && this.multipleSelection.length ===0 ) return this.$message.warning("请填选择至少一个商品属性！");
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
          this.formValidate.attrValue = this.ManyAttrValue
        }else{
          this.multipleSelection.forEach((row) => {
            this.$set(row, 'checked', true)
          });
          this.formValidate.attrValue = this.multipleSelection
        }
        this.formValidate.images = JSON.stringify(this.formValidate.imagess)
        this.$refs[name].validate((valid) => {
            if (valid) {
              this.fullscreenLoading = true;
              this.loading = true;
              this.$route.params.id
                ? seckillStoreUpdateApi({id: this.$route.params.id}, this.formValidate)
                  .then(async () => {
                    this.fullscreenLoading = false;
                    this.$message.success('编辑成功');
                    this.$router.push({
                      path: "/marketing/seckill/list",
                    });
                    this.$refs[name].resetFields();
                    this.formValidate.images = [];
                    this.loading = false;
                  })
                  .catch(() => {
                    this.fullscreenLoading = false;
                    this.loading = false;
                  })
                : seckillStoreSaveApi(this.formValidate)
                  .then(async (res) => {
                    this.fullscreenLoading = false;
                    this.$message.success('新增成功');
                    this.$router.push({
                      path: "/marketing/seckill/list",
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
        const newItems = [...this.formValidate.imagess]
        const src = newItems.indexOf(this.dragging)
        const dst = newItems.indexOf(item)
        newItems.splice(dst, 0, ...newItems.splice(src, 1))
        this.formValidate.imagess = newItems;
      }
    }
  }
</script>

<style scoped lang="scss">
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
