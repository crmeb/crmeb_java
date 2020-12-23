<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-steps :active="currentTab" align-center finish-status="success">
          <el-step title="商品信息" />
          <el-step title="商品详情" />
          <el-step title="其他设置" />
          <el-step title="规格设置" />
        </el-steps>
      </div>
      <el-form ref="formValidate" v-loading="fullscreenLoading" class="formValidate mt20" :rules="ruleValidate" :model="formValidate" label-width="120px" @submit.native.prevent>
        <el-row v-show="currentTab === 0" :gutter="24">
          <!-- 商品信息-->
          <el-col v-bind="grid2">
            <el-form-item label="商品名称：" prop="storeName">
              <el-input v-model="formValidate.storeName" maxlength="249" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品分类：" prop="cateIds">
              <el-cascader v-model="formValidate.cateIds" :options="merCateList" :props="props2" clearable class="selWidth" :show-all-levels="false" />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品关键字：">
              <el-input v-model="formValidate.keyword" placeholder="请输入商品关键字" />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="单位：" prop="unitName">
              <el-input v-model="formValidate.unitName" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品简介：">
              <el-input v-model="formValidate.storeInfo" type="textarea"  maxlength="250" :rows="3" placeholder="请输入商品简介" />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品封面图：" prop="image">
              <div class="upLoadPicBox" @click="modalPicTap('1')">
                <div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image"></div>
                <div v-else class="upLoad">
                  <i class="el-icon-camera cameraIconfont" />
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品轮播图：" prop="sliderImages">
              <div class="acea-row">
                <div
                  v-for="(item,index) in formValidate.sliderImages"
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
                <div v-if="formValidate.sliderImages.length<10" class="upLoadPicBox" @click="modalPicTap('2')">
                  <div class="upLoad">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="运费模板：" prop="tempId">
              <div class="acea-row">
                <el-select v-model="formValidate.tempId" placeholder="请选择" class="selWidthd mr20">
                  <el-option
                    v-for="item in shippingList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
                <el-button class="mr15" @click="addTem">添加运费模板</el-button>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠券：" class="proCoupon">
              <div class="acea-row">
                <el-tag
                  v-for="(tag, index) in formValidate.coupons"
                  :key="index"
                  class="mr10"
                  closable
                  :disable-transitions="false"
                  @close="handleCloseCoupon(tag)"
                >
                  {{ tag.name }}
                </el-tag>
                <el-button class="mr15" size="mini" @click="addCoupon">选择优惠券</el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 商品详情-->
        <el-row v-show="currentTab === 1">
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <ueditor-from v-model="formValidate.content" :content="formValidate.content" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 其他设置-->
        <el-row v-show="currentTab === 2">
          <el-col :span="24">
            <el-col v-bind="grid">
              <el-form-item label="排序：">
                <el-input-number v-model="formValidate.sort" :max="9999" placeholder="请输入排序" />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid">
              <el-form-item label="积分：">
                <el-input-number v-model="formValidate.giveIntegral" placeholder="请输入排序" />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid">
              <el-form-item label="虚拟销量：">
                <el-input-number v-model="formValidate.ficti" placeholder="请输入排序" />
              </el-form-item>
            </el-col>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品推荐：">
              <el-checkbox-group v-model="checkboxGroup" size="small" @change="onChangeGroup">
                <el-checkbox v-for="(item, index) in recommend" :key="index" :label="item.value">{{ item.name }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动优先级：">
              <div class="color-list acea-row row-middle">
                <div
                  class="color-item" :class="activity[item]"
                  v-for="item in formValidate.activity"
                  :key="item"
                  draggable="true"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnterFont($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >{{item}}</div>
                <div class="tip">可拖动按钮调整活动的优先展示顺序</div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-show="currentTab === 3">
          <el-col :span="24">
            <el-form-item label="商品规格：" props="specType">
              <el-radio-group v-model="formValidate.specType" @change="onChangeSpec(formValidate.specType)">
                <el-radio :label="false" class="radio">单规格</el-radio>
                <el-radio :label="true">多规格</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="佣金设置：" props="isSub">
              <el-radio-group v-model="formValidate.isSub" @change="onChangetype(formValidate.isSub)">
                <el-radio :label="true" class="radio">单独设置</el-radio>
                <el-radio :label="false">默认设置</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <!-- 多规格添加-->
          <el-col v-if="formValidate.specType" :span="24" class="noForm">
            <el-form-item label="选择规格：" prop="">
              <div class="acea-row">
                <el-select v-model="formValidate.selectRule">
                  <el-option v-for="item in ruleList" :key="item.id" :label="item.ruleName" :value="item.id" />
                </el-select>
                <el-button type="primary" class="mr20" @click="confirm">确认</el-button>
                <el-button class="mr15" @click="addRule">添加规格</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <div v-for="(item, index) in formValidate.attr" :key="index">
                <div class="acea-row row-middle"><span class="mr5">{{ item.attrName }}</span><i class="el-icon-circle-close" @click="handleRemoveAttr(index)" /></div>
                <div class="rulesBox">
                  <el-tag
                    v-for="(j, indexn) in item.attrValue"
                    :key="indexn"
                    closable
                    size="medium"
                    :disable-transitions="false"
                    class="mb5 mr10"
                    @close="handleClose(item.attrValue,indexn)"
                  >
                    {{ j }}
                  </el-tag>
                  <el-input
                    v-if="item.inputVisible"
                    ref="saveTagInput"
                    v-model="item.attrValue.attrsVal"
                    class="input-new-tag"
                    size="small"
                    @keyup.enter.native="createAttr(item.attrValue.attrsVal,index)"
                    @blur="createAttr(item.attrValue.attrsVal,index)"
                  />
                  <el-button v-else class="button-new-tag" size="small" @click="showInput(item)">+ 添加</el-button>
                </div>
              </div>
            </el-form-item>
            <el-col v-if="isBtn">
              <el-col :xl="6" :lg="9" :md="9" :sm="24" :xs="24">
                <el-form-item label="规格：">
                  <el-input v-model="formDynamic.attrsName" placeholder="请输入规格" />
                </el-form-item>
              </el-col>
              <el-col :xl="6" :lg="9" :md="9" :sm="24" :xs="24">
                <el-form-item label="规格值：">
                  <el-input v-model="formDynamic.attrsVal" placeholder="请输入规格值" />
                </el-form-item>
              </el-col>
              <el-col :xl="12" :lg="6" :md="6" :sm="24" :xs="24">
                <el-form-item class="noLeft">
                  <el-button type="primary" class="mr15" @click="createAttrName">确定</el-button>
                  <el-button @click="offAttrName">取消</el-button>
                </el-form-item>
              </el-col>
            </el-col>
            <el-form-item v-if="!isBtn">
              <el-button type="primary" icon="md-add" class="mr15" @click="addBtn">添加新规格</el-button>
            </el-form-item>
          </el-col>
          <!-- 批量设置-->
          <el-col v-if="formValidate.attr.length>0 && formValidate.specType" :span="24" class="noForm">
            <el-form-item label="批量设置：" class="labeltop">
              <el-table :data="oneFormBatch" border class="tabNumWidth" size="mini">
                <el-table-column align="center" label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1','pi')">
                      <div v-if="scope.row.image" class="pictrue tabPic"><img :src="scope.row.image"></div>
                      <div v-else class="upLoad tabPic">
                        <i class="el-icon-camera cameraIconfont" />
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column v-for="(item,iii) in attrValue" :key="iii" :label="formThead[iii].title" align="center" min-width="120">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'" :min="0" class="priceBox" />
                  </template>
                </el-table-column>
                <template v-if="formValidate.isSub">
                  <el-table-column align="center" label="一级返佣(元)" min-width="120">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.brokerage" type="number" :min="0" class="priceBox" />
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="二级返佣(元)" min-width="120">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.brokerageTwo" type="number" :min="0" class="priceBox" />
                    </template>
                  </el-table-column>
                </template>
                <el-table-column align="center" label="操作" min-width="80">
                  <template>
                    <el-button type="text" class="submission" @click="batchAdd">批量添加</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
          <el-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
            <!-- 单规格表格-->
            <el-form-item v-if="formValidate.specType === false">
              <el-table :data="OneattrValue" border class="tabNumWidth" size="mini">
                <el-table-column align="center" label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1', 'dan', 'pi')">
                      <div v-if="formValidate.image" class="pictrue tabPic"><img :src="scope.row.image"></div>
                      <div v-else class="upLoad tabPic">
                        <i class="el-icon-camera cameraIconfont" />
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column v-for="(item,iii) in attrValue" :key="iii" :label="formThead[iii].title" align="center" min-width="120">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'" :min="0" class="priceBox" />
                  </template>
                </el-table-column>
                <template v-if="formValidate.isSub">
                  <el-table-column align="center" label="一级返佣(元)" min-width="120">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.brokerage" type="number" :min="0" class="priceBox" />
                    </template>
                  </el-table-column>
                  <el-table-column align="center" label="二级返佣(元)" min-width="120">
                    <template slot-scope="scope">
                      <el-input v-model="scope.row.brokerageTwo" type="number" :min="0" class="priceBox" />
                    </template>
                  </el-table-column>
                </template>
              </el-table>
            </el-form-item>
            <!-- 多规格表格-->
            <el-form-item v-if="formValidate.attr.length>0 && formValidate.specType" label="商品属性：" class="labeltop">
              <el-table :data="ManyAttrValue" border class="tabNumWidth" size="mini">
                <template v-if="manyTabDate">
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
                <el-table-column v-for="(item,iii) in attrValue" :key="iii" :label="formThead[iii].title" align="center" min-width="120">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'" class="priceBox" />
                  </template>
                </el-table-column>
                <el-table-column align="center" label="一级返佣(元)" min-width="120" v-if="formValidate.isSub">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.brokerage" type="number" :min="0" class="priceBox" />
                  </template>
                </el-table-column>
                <el-table-column align="center" label="二级返佣(元)" min-width="120" v-if="formValidate.isSub">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.brokerageTwo" type="number" :min="0" class="priceBox" />
                  </template>
                </el-table-column>
                <el-table-column key="3" align="center" label="操作" min-width="80">
                  <template slot-scope="scope">
                    <el-button type="text" class="submission" @click="delAttrTable(scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button v-show="currentTab>0" type="primary" class="submission" @click="handleSubmitUp">上一步</el-button>
          <el-button v-show="currentTab<3" type="primary" class="submission" @click="handleSubmitNest('formValidate')">下一步</el-button>
          <el-button v-show="currentTab===3 || $route.params.id" type="primary" class="submission" @click="handleSubmit('formValidate')">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getShippingList" />
  </div>
</template>

<script>
  import { templateListApi, productCreateApi, categoryApi, productDetailApi, productUpdateApi } from '@/api/store'
  import { shippingTemplatesList } from '@/api/logistics'
  import { clearTreeData } from '@/utils/ZBKJIutil'
  import CreatTemplates from '@/views/systemSetting/logistics/shippingTemplates/creatTemplates'
  import Templates from "../../appSetting/wxAccount/wxTemplate/index";
  const defaultObj = {
    image: '',
    sliderImages: [],
    sliderImage: '',
    storeName: '',
    storeInfo: '',
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
    attrValue: [{
      image: '',
      price: 0,
      cost: 0,
      otPrice: 0,
      stock: 0,
      barCode: '',
      weight: 0,
      volume: 0
    }],
    attr: [],
    selectRule: '',
    isSub: false,
    content: '',
    specType: false,
    id: 0,
    couponIds: [],
    coupons: [],
    activity: ['默认','秒杀','砍价','拼团']
  }
  const objTitle = {
    price: {
      title: '售价'
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
  const proOptions = [{ name: '是否热卖', value: 'isHot' }, { name: '优品推荐', value: 'isGood' }, { name: '促销单品', value: 'isBenefit' }, { name: '是否精品', value: 'isBest' }, { name: '是否新品', value: 'isNew' }]
  export default {
    name: 'ProductProductAdd',
    components: {Templates, CreatTemplates },
    data() {
      return {
        activity: { '默认': 'red', '秒杀': 'blue', '砍价': 'green', '拼团': 'yellow' },
        props2: {
          children: 'child',
          label: 'name',
          value: 'id',
          multiple: true,
          emitPath: false
        },
        checkboxGroup: [],
        recommend: proOptions,
        tabs: [],
        fullscreenLoading: false,
        props: { multiple: true },
        active: 0,
        OneattrValue: [Object.assign({}, defaultObj.attrValue[0])], // 单规格
        ManyAttrValue: [Object.assign({}, defaultObj.attrValue[0])], // 多规格
        ruleList: [],
        merCateList: [], // 商户分类筛选
        shippingList: [], // 运费模板
        formThead: Object.assign({}, objTitle),
        formValidate: Object.assign({}, defaultObj),
        formDynamics: {
          ruleName: '',
          ruleValue: []
        },
        tempData: {
          page: 1,
          limit: 9999
        },
        manyTabTit: {},
        manyTabDate: {},
        grid2: {
          xl: 10,
          lg: 12,
          md: 12,
          sm: 24,
          xs: 24
        },
        // 规格数据
        formDynamic: {
          attrsName: '',
          attrsVal: ''
        },
        isBtn: false,
        manyFormValidate: [],
        currentTab: 0,
        isChoice: '',
        grid: {
          xl: 8,
          lg: 8,
          md: 12,
          sm: 24,
          xs: 24
        },
        ruleValidate: {
          storeName: [
            { required: true, message: '请输入商品名称', trigger: 'blur' }
          ],
          cateIds: [
            { required: true, message: '请选择商品分类', trigger: 'change', type: 'array', min: '1' }
          ],
          keyword: [
            { required: true, message: '请输入商品关键字', trigger: 'blur' }
          ],
          unitName: [
            { required: true, message: '请输入单位', trigger: 'blur' }
          ],
          storeInfo: [
            { required: true, message: '请输入商品简介', trigger: 'blur' }
          ],
          tempId: [
            { required: true, message: '请选择运费模板', trigger: 'change' }
          ],
          image: [
            { required: true, message: '请上传商品图', trigger: 'change' }
          ],
          sliderImages: [
            { required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }
          ],
          specType: [
            { required: true, message: '请选择商品规格', trigger: 'change' }
          ]
        },
        attrInfo: {},
        tableFrom: {
          page: 1,
          limit: 9999,
          keywords: ''
        },
        tempRoute: {},
        keyNum: 0,
        isAttr: false
      }
    },
    computed: {
      attrValue() {
        const obj = Object.assign({}, defaultObj.attrValue[0])
        delete obj.image
        return obj
      },
      oneFormBatch() {
        const obj = [Object.assign({}, defaultObj.attrValue[0])]
        delete obj[0].barCode
        return obj
      }
    },
    watch: {
      'formValidate.attr': {
        handler: function(val) {
          if (this.formValidate.specType && this.isAttr) this.watCh(val)
        },
        immediate: false,
        deep: true
      }
    },
    created() {
      this.tempRoute = Object.assign({}, this.$route)
      if (this.$route.params.id && this.formValidate.specType) {
        this.$watch('formValidate.attr', this.watCh)
      }
    },
    mounted() {
      this.formValidate.sliderImages = []
      if ( this.$route.params.id ) {
        this.setTagsViewTitle()
        this.getInfo()
      }
      // if( this.formValidate.attr.length ){
      //   this.formValidate.attr.map(item => {
      //     this.$set(item, 'inputVisible', false)
      //   })
      // }
      this.getCategorySelect()
      this.getShippingList()
    },
    methods: {
      handleCloseCoupon(tag) {
        this.isAttr = true
        this.formValidate.coupons.splice(this.formValidate.coupons.indexOf(tag), 1)
      },
      addCoupon() {
        const _this = this
        this.$modalCoupon('wu', this.keyNum += 1, this.formValidate.coupons, function(row) {
          _this.formValidate.couponIds = []
          _this.formValidate.coupons = row
          row.map((item) => {
            _this.formValidate.couponIds.push(item.id)
          })
        }, '')
      },
      setTagsViewTitle() {
        const title = '编辑商品'
        const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.$route.params.id}` })
        this.$store.dispatch('tagsView/updateVisitedView', route)
      },
      onChangeGroup() {
        this.checkboxGroup.includes('isGood') ? this.formValidate.isGood = true : this.formValidate.isGood = false
        this.checkboxGroup.includes('isBenefit') ? this.formValidate.isBenefit = true : this.formValidate.isBenefit = false
        this.checkboxGroup.includes('isBest') ? this.formValidate.isBest = true : this.formValidate.isBest = false
        this.checkboxGroup.includes('isNew') ? this.formValidate.isNew = true : this.formValidate.isNew = false
        this.checkboxGroup.includes('isHot') ? this.formValidate.isHot = true : this.formValidate.isHot = false
      },
      watCh(val) {
        const tmp = {}
        const tmpTab = {}
        this.formValidate.attr.forEach((o, i) => {
          tmp['value' + i] = { title: o.attrName }
          tmpTab['value' + i] = ''
        })
        this.ManyAttrValue = this.attrFormat(val)
        this.ManyAttrValue.forEach((val, index) => {
          const key = Object.values(val.attrValue).sort().join('/')
          if (this.attrInfo[key]) this.ManyAttrValue[index] = this.attrInfo[key]
        })
        this.attrInfo = {}
        this.ManyAttrValue.forEach((val) => {
          this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val
        })
        this.manyTabTit = tmp
        this.manyTabDate = tmpTab
        this.formThead = Object.assign({}, this.formThead, tmp)
      },
      attrFormat(arr) {
        let data = []
        const res = []
        return format(arr)
        function format(arr) {
          if (arr.length > 1) {
            arr.forEach((v, i) => {
              if (i === 0) data = arr[i]['attrValue']
              const tmp = []
              data.forEach(function(vv) {
                arr[i + 1] && arr[i + 1]['attrValue'] && arr[i + 1]['attrValue'].forEach(g => {
                  const rep2 = (i !== 0 ? '' : arr[i]['attrName'] + '_') + vv + '$&' + arr[i + 1]['attrName'] + '_' + g
                  tmp.push(rep2)
                  if (i === (arr.length - 2)) {
                    const rep4 = {
                      image: '',
                      price: 0,
                      cost: 0,
                      otPrice: 0,
                      stock: 0,
                      barCode: '',
                      weight: 0,
                      volume: 0,
                      brokerage: 0,
                      brokerage_two: 0
                    }
                    rep2.split('$&').forEach((h, k) => {
                      const rep3 = h.split('_')
                      if (!rep4['attrValue']) rep4['attrValue'] = {}
                      rep4['attrValue'][rep3[0]] = rep3.length > 1 ? rep3[1] : ''
                    })
                    Object.values(rep4.attrValue).forEach((v, i) => {
                      rep4['value' + i] = v
                    })
                    res.push(rep4)
                  }
                })
              })
              data = tmp.length ? tmp : []
            })
          } else {
            const dataArr = []
            arr.forEach((v, k) => {
              v['attrValue'].forEach((vv, kk) => {
                dataArr[kk] = v['attrName'] + '_' + vv
                res[kk] = {
                  image: '',
                  price: 0,
                  cost: 0,
                  otPrice: 0,
                  stock: 0,
                  barCode: '',
                  weight: 0,
                  volume: 0,
                  brokerage: 0,
                  brokerage_two: 0,
                  attrValue: { [v['attrName']]: vv }
                }
                Object.values(res[kk].attrValue).forEach((v, i) => {
                  res[kk]['value' + i] = v
                })
              })
            })
            data.push(dataArr.join('$&'))
          }
          return res
        }
      },
      // 运费模板
      addTem() {
        this.$refs.addTemplates.dialogVisible = true
        this.$refs.addTemplates.getCityList()
      },
      // 添加规则；
      addRule() {
        const _this = this
        this.$modalAttr(this.formDynamics, function() {
          _this.productGetRule()
        })
      },
      // 选择规格
      onChangeSpec(num) {
        this.isAttr = true;
        if (num) this.productGetRule()
      },
      // 选择属性确认
      confirm() {
        this.isAttr = true
        if (!this.formValidate.selectRule) {
          return this.$message.warning('请选择属性')
        }
        const data = []
        this.ruleList.forEach(item => {
          if (item.id === this.formValidate.selectRule) {
            item.ruleValue.forEach(i => {
              data.push({
                attrName: i.value,
                attrValue: i.detail
              })
            })
          }
          this.formValidate.attr = data
        })
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
      // 获取商品属性模板；
      productGetRule() {
        templateListApi(this.tableFrom).then(res => {
          const list = res.list
          for (var i = 0; i < list.length; i++) {
            list[i].ruleValue = JSON.parse(list[i].ruleValue)
          }
          this.ruleList = list
        })
      },
      // 运费模板；
      getShippingList() {
        shippingTemplatesList(this.tempData).then(res => {
          this.shippingList = res.list
        })
      },
      showInput(item) {
        this.$set(item, 'inputVisible', true)
      },
      onChangetype(item) {
        if (item === 1) {
          this.OneattrValue.map(item => {
            this.$set(item, 'brokerage', null)
            this.$set(item, 'brokerageTwo', null)
          })
          this.ManyAttrValue.map(item => {
            this.$set(item, 'brokerage', null)
            this.$set(item, 'brokerageTwo', null)
          })
        } else {
          this.OneattrValue.map(item => {
            delete item.brokerage
            delete item.brokerageTwo
            this.$set(item, 'brokerage', null)
            this.$set(item, 'brokerageTwo', null)
          })
          this.ManyAttrValue.map(item => {
            delete item.brokerage
            delete item.brokerageTwo
          })
        }
      },
      // 删除表格中的属性
      delAttrTable(index) {
        this.ManyAttrValue.splice(index, 1)
      },
      // 批量添加
      batchAdd() {
        // if (!this.oneFormBatch[0].pic || !this.oneFormBatch[0].price || !this.oneFormBatch[0].cost || !this.oneFormBatch[0].ot_price ||
        //     !this.oneFormBatch[0].stock || !this.oneFormBatch[0].bar_code) return this.$Message.warning('请填写完整的批量设置内容！');
        for (const val of this.ManyAttrValue) {
          this.$set(val, 'image', this.oneFormBatch[0].image)
          this.$set(val, 'price', this.oneFormBatch[0].price)
          this.$set(val, 'cost', this.oneFormBatch[0].cost)
          this.$set(val, 'otPrice', this.oneFormBatch[0].otPrice)
          this.$set(val, 'stock', this.oneFormBatch[0].stock)
          this.$set(val, 'barCode', this.oneFormBatch[0].barCode)
          this.$set(val, 'weight', this.oneFormBatch[0].weight)
          this.$set(val, 'volume', this.oneFormBatch[0].volume)
          this.$set(val, 'brokerage', this.oneFormBatch[0].brokerage)
          this.$set(val, 'brokerageTwo', this.oneFormBatch[0].brokerageTwo)
        }
      },
      // 添加按钮
      addBtn() {
        this.clearAttr()
        this.isBtn = true
      },
      // 取消
      offAttrName() {
        this.isBtn = false
      },
      clearAttr() {
        this.formDynamic.attrsName = ''
        this.formDynamic.attrsVal = ''
      },
      // 删除规格
      handleRemoveAttr(index) {
        this.isAttr = true
        this.formValidate.attr.splice(index, 1)
        this.manyFormValidate.splice(index, 1)
      },
      // 删除属性
      handleClose(item, index) {
        item.splice(index, 1)
      },
      // 添加规则名称
      createAttrName() {
        this.isAttr = true
        if (this.formDynamic.attrsName && this.formDynamic.attrsVal) {
          const data = {
            attrName: this.formDynamic.attrsName,
            attrValue: [
              this.formDynamic.attrsVal
            ]
          }
          this.formValidate.attr.push(data)
          var hash = {}
          this.formValidate.attr = this.formValidate.attr.reduce(function(item, next) {
            /* eslint-disable */
            hash[next.attrName] ? '' : hash[next.attrName] = true && item.push(next)
            return item
          }, [])
          this.clearAttr()
          this.isBtn = false
        } else {
          this.$Message.warning('请添加完整的规格！');
        }
      },
      // 添加属性
      createAttr (num, idx) {
        this.isAttr = true
        if (num) {
          this.formValidate.attr[idx].attrValue.push(num);
          var hash = {};
          this.formValidate.attr[idx].attrValue = this.formValidate.attr[idx].attrValue.reduce(function (item, next) {
            /* eslint-disable */
            hash[next] ? '' : hash[next] = true && item.push(next);
            return item
          }, [])
          this.formValidate.attr[idx].inputVisible = false
        } else {
          this.$message.warning('请添加属性');
        }
      },
      // 详情
      getInfo () {
        this.fullscreenLoading = true
        productDetailApi(this.$route.params.id).then(async res => {
          this.isAttr = false;
          let info = res
          this.formValidate = {
            image: info.image,
            sliderImages: JSON.parse(info.sliderImage),
            sliderImage: info.sliderImage,
            storeName: info.storeName,
            storeInfo: info.storeInfo,
            keyword: info.keyword,
            cateIds: info.cateId.split(','), // 商品分类id
            cateId: info.cateId,// 商品分类id传值
            unitName: info.unitName,
            sort: info.sort,
            isShow: info.isShow,
            isBenefit: info.isBenefit,
            isNew: info.isNew,
            isGood: info.isGood,
            isHot: info.isHot,
            isBest: info.isBest,
            tempId: info.tempId,
            // attrValue: info.attrValue,
            attr: info.attr,
            selectRule: info.selectRule,
            isSub: info.isSub,
            content: info.content,
            specType: info.specType,
            id: info.id,
            giveIntegral: info.giveIntegral,
            ficti: info.ficti,
            coupons: info.coupons,
            activity: info.activityStr ? info.activityStr.split(',') : ['默认','秒杀','砍价','拼团']
          }
          if(info.isHot) this.checkboxGroup.push('isHot')
          if(info.isGood) this.checkboxGroup.push('isGood')
          if(info.isBenefit) this.checkboxGroup.push('isBenefit')
          if(info.isBest) this.checkboxGroup.push('isBest')
          if(info.isNew) this.checkboxGroup.push('isNew')
          this.productGetRule()
          if(info.specType){
            this.formValidate.attr = info.attr.map(item => {
              return {
                attrName : item.attrName,
                attrValue: item.attrValues.split(','),
                // inputVisible: false
              }
            })
            this.ManyAttrValue = info.attrValues;
            this.ManyAttrValue.forEach((val) => {
              this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val
            })

            const tmp = {}
            const tmpTab = {}
            this.formValidate.attr.forEach((o, i) => {
              tmp['value' + i] = { title: o.attrName }
              tmpTab['value' + i] = ''
            })
            this.manyTabTit = tmp
            this.manyTabDate = tmpTab
            this.formThead = Object.assign({}, this.formThead, tmp)
          }else{
            this.OneattrValue = info.attrValue
            this.formValidate.attr = []
          }
          this.fullscreenLoading = false
        }).catch(res => {
          this.fullscreenLoading = false
          this.$message.error(res.message);
        })
      },
      handleRemove (i) {
        this.formValidate.sliderImages.splice(i, 1)
      },
      // 点击商品图
      modalPicTap (tit, num, i) {
        const _this = this
        this.$modalUpload(function(img) {
          if(tit==='1'&& !num){
            _this.formValidate.image = img[0].sattDir
            _this.OneattrValue[0].image = img[0].sattDir
          }
          if(tit==='2'&& !num){
            if(img.length>10) return this.$message.warning("最多选择10张图片！");
            if(img.length + _this.formValidate.sliderImages.length > 10) return this.$message.warning("最多选择10张图片！");
            img.map((item) => {
              _this.formValidate.sliderImages.push(item.sattDir)
            });
          }
          if(tit==='1'&& num === 'dan' ){
            _this.OneattrValue[0].image = img[0].sattDir
          }
          if(tit==='1'&& num === 'duo' ){
            _this.ManyAttrValue[i].image = img[0].sattDir
          }
          if(tit==='1'&& num === 'pi' ){
            _this.oneFormBatch[0].image = img[0].sattDir
          }
        },tit, 'content')
      },
      handleSubmitUp(){
        // this.currentTab --
        if (this.currentTab-- <0) this.currentTab = 0;
      },
      handleSubmitNest(name){
        this.$refs[name].validate((valid) => {
          if (valid) {
            if (this.currentTab++ > 2) this.currentTab = 0;
          } else {
            if(!this.formValidate.store_name || !this.formValidate.cate_id || !this.formValidate.keyword
              || !this.formValidate.unit_name || !this.formValidate.store_info
              || !this.formValidate.image || !this.formValidate.slider_image){
              this.$message.warning("请填写完整商品信息！");
            }
          }
        })
      },
      // 提交
      handleSubmit (name) {
        this.onChangeGroup()
        if( this.formValidate.specType && this.formValidate.attr.length < 1 ) return this.$message.warning("请填写多规格属性！");
        this.formValidate.cateId = this.formValidate.cateIds.join(',')
        this.formValidate.sliderImage = JSON.stringify(this.formValidate.sliderImages)
        if(this.formValidate.specType){
          this.formValidate.attrValue=this.ManyAttrValue
          for (var i = 0; i < this.formValidate.attr.length; i++) {
            this.formValidate.attr[i].attrValues = this.formValidate.attr[i].attrValue.join(',')
          }
        }else{
          this.formValidate.attr = []
          this.formValidate.attrValue=this.OneattrValue
        }
        this.$refs[name].validate((valid) => {
          if (valid) {
            this.fullscreenLoading = true
            this.$route.params.id?productUpdateApi(this.formValidate).then(async res => {
              this.$message.success('编辑成功');
              setTimeout(() => {
                this.$router.push({ path: '/store/index' });
              }, 500);
              this.fullscreenLoading = false
            }).catch(res => {
              this.fullscreenLoading = false
            }):productCreateApi(this.formValidate).then(async res => {
              this.$message.success('新增成功');
              setTimeout(() => {
                this.$router.push({ path: '/store/index' });
              }, 500);
              this.fullscreenLoading = false
            }).catch(res => {
              this.fullscreenLoading = false
            })
          } else {
            if(!this.formValidate.storeName || !this.formValidate.cateId  || !this.formValidate.keyword
              || !this.formValidate.unitName || !this.formValidate.storeInfo || !this.formValidate.image || !this.formValidate.sliderImages){
              this.$message.warning("请填写完整商品信息！");
            }
          }
        })
      },
      // 表单验证
      validate (prop, status, error) {
        if (status === false) {
          this.$message.warning(error);
        }
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
        const newItems = [...this.formValidate.sliderImages]
        const src = newItems.indexOf(this.dragging)
        const dst = newItems.indexOf(item)
        newItems.splice(dst, 0, ...newItems.splice(src, 1))
        this.formValidate.sliderImages = newItems;
      },
      handleDragEnterFont(e, item) {
        e.dataTransfer.effectAllowed = 'move'
        if (item === this.dragging) {
          return
        }
        const newItems = [...this.formValidate.activity]
        const src = newItems.indexOf(this.dragging)
        const dst = newItems.indexOf(item)
        newItems.splice(dst, 0, ...newItems.splice(src, 1))
        this.formValidate.activity = newItems;
      }
    }
  }
</script>
<style scoped lang="scss">
  .color-item{
    height: 30px;
    line-height: 30px;
    padding: 0 10px;
    color:#fff;
    margin-right :10px;
  }
  .color-list .color-item.blue{
    background-color: #1E9FFF;
  }
  .color-list .color-item.yellow{
    background-color: rgb(254, 185, 0);
  }
  .color-list .color-item.green{
    background-color: #009688;
  }
  .color-list .color-item.red{
    background-color: #ed4014;
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
    width: 100%;
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
      float: none !important;
      display: inline-block !important;
      margin-left: 120px !important;
      width: auto !important;
    }
  }
</style>
