<template>
  <div class="Box">
    <el-card>
      <div>生成的商品默认是没有上架的，请手动上架商品！
        <span v-if="copyConfig.copyType && copyConfig.copyType==1">您当前剩余{{copyConfig.copyNum}}条采集次数，
          <router-link :to="{path:'/operation/onePass'}">
            <el-link type="primary" :underline="false">增加采集次数</el-link>
          </router-link>
        </span>
        <el-link  v-if="copyConfig.copyType && copyConfig.copyType!=1" type="primary" :underline="false" href="https://www.kancloud.cn/crmeb/crmeb_java/1909022" target="_blank">如何配置密钥</el-link>
        <br>
        商品采集设置：设置 > 系统设置 > 第三方接口设置 > 采集商品配置（如配置一号通采集，请先登录一号通账号，无一号通，请选择99Api设置）</div>
    </el-card>
    <el-form class="formValidate mt20" ref="formValidate" :model="formValidate" :rules="ruleInline" label-width="120px"
             @submit.native.prevent v-loading="loading">
      <el-form-item v-if="copyConfig.copyType && copyConfig.copyType!=1">
        <el-radio-group v-model="form">
          <el-radio :label="1">淘宝</el-radio>
          <el-radio :label="2">京东</el-radio>
          <!--<el-radio :label="3">苏宁</el-radio>-->
          <!--<el-radio :label="4">拼多多</el-radio>-->
          <el-radio :label="5">天猫</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-row :gutter="24">
        <el-col :span="24" v-if="copyConfig.copyType">
          <el-form-item label="链接地址：">
            <el-input v-model="url" placeholder="请输入链接地址" class="selWidth" size="small">
              <el-button slot="append" icon="el-icon-search" @click="add" size="small"/>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col v-if="formValidate">
          <el-col :span="24">
            <el-form-item label="商品名称：" prop="storeName">
              <el-input v-model="formValidate.storeName" maxlength="249" placeholder="请输入商品名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品简介：">
              <el-input v-model="formValidate.storeInfo"  maxlength="250" type="textarea" :rows="3" placeholder="请输入商品简介"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品分类：" prop="cateIds">
              <el-cascader v-model="formValidate.cateIds" :options="merCateList" :props="props2" clearable
                           class="selWidth" :show-all-levels="false"/>
            </el-form-item>
          </el-col>
          <el-col v-bind="grid">
            <el-form-item label="商品关键字：" prop="keyword">
              <el-input v-model="formValidate.keyword" placeholder="请输入商品关键字"></el-input>
            </el-form-item>
          </el-col>
          <el-col v-bind="grid">
            <el-form-item label="单位：" prop="unitName">
              <el-input v-model="formValidate.unitName" placeholder="请输入单位"></el-input>
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
          <el-col v-bind="grid">
            <el-form-item label="运费模板：" prop="tempId">
              <el-select v-model="formValidate.tempId" placeholder="请选择" class="selWidthd mr20">
                <el-option
                  v-for="item in shippingList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品封面图：" prop="image">
              <div class="upLoadPicBox" @click="modalPicTap('1')">
                <div v-if="formValidate.image" class="pictrue"><img :src="formValidate.image"></div>
                <div v-else class="upLoad">
                  <i class="el-icon-camera cameraIconfont"/>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品轮播图：">
              <div class="acea-row">
                <div
                  v-for="(item,index) in formValidate.sliderImages"
                  :key="index"
                  class="lunBox mr5"
                  draggable="false"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnter($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >
                  <div class="pictrue"><img :src="item"></div>
                  <el-button-group>
                    <el-button size="mini" @click.native="checked(item,index)">主图</el-button>
                    <el-button size="mini" @click.native="handleRemove(index)">移除</el-button>
                  </el-button-group>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col v-if="formValidate.specType || formValidate.attr.length" :span="24" class="noForm">
            <el-form-item label="批量设置：" class="labeltop">
              <el-table :data="oneFormBatch" border class="tabNumWidth" size="mini">
                <el-table-column align="center" label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1','pi')">
                      <div v-if="scope.row.image" class="pictrue pictrueTab"><img :src="scope.row.image"></div>
                      <div v-else class="upLoad pictrueTab">
                        <i class="el-icon-camera cameraIconfont"/>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column v-for="(item,iii) in attrValue" :key="iii" :label="formThead[iii].title"
                                 align="center" min-width="120">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'" :min="0"
                              class="priceBox"/>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="操作" min-width="80">
                  <template>
                    <el-button type="text" class="submission" @click="batchAdd">批量添加</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品规格：" props="spec_type" label-for="spec_type">
              <el-table :data="ManyAttrValue" border class="tabNumWidth" size="mini">
                <template v-if="manyTabDate">
                  <el-table-column v-for="(item,iii) in manyTabDate" :key="iii" align="center"
                                   :label="manyTabTit[iii].title" min-width="80">
                    <template slot-scope="scope">
                      <span class="priceBox" v-text="scope.row[iii]"/>
                    </template>
                  </el-table-column>
                </template>
                <el-table-column align="center" label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1','duo',scope.$index)">
                      <div v-if="scope.row.image" class="pictrue pictrueTab"><img :src="scope.row.image"></div>
                      <div v-else class="upLoad pictrueTab">
                        <i class="el-icon-camera cameraIconfont"/>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column v-for="(item,iii) in attrValue" :key="iii" :label="formThead[iii].title"
                                 align="center" min-width="120">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'"
                              class="priceBox"/>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="操作" min-width="80">
                  <template slot-scope="scope">
                    <el-button type="text" class="submission" @click="delAttrTable(scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <ueditor-from v-model="formValidate.content" :content="formValidate.content"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" :loading="modal_loading" class="submission"
                         @click="handleSubmit('formValidate')">提交
              </el-button>
            </el-form-item>
          </el-col>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
  import {crawlFromApi, treeListApi, crawlSaveApi, categoryApi, importProductApi, productCreateApi, copyConfigApi} from '@/api/store';
  import { shippingTemplatesList } from '@/api/logistics'
  const defaultObj = [{
    image: '',
    price: null,
    cost: null,
    otPrice: null,
    stock: null,
    barCode: '',
    weight: null,
    volume: null
  }]
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
  export default {
    name: 'taoBao',
    data() {
      return {
        loading: false,
        formThead: Object.assign({}, objTitle),
        manyTabTit: {},
        manyTabDate: {},
        formValidate: null,
        form: 1,
        props2: {
          children: 'child',
          label: 'name',
          value: 'id',
          multiple: true,
          emitPath: false
        },
        modal_loading: false,
        ManyAttrValue: [Object.assign({}, defaultObj[0])], // 多规格
        imgList: [],
        tempData: {
          page: 1,
          limit: 9999
        },
        shippingList:[],
        merCateList:[],
        images: '',
        url: '',
        modalPic: false,
        isChoice: '',
        ruleInline: {
          storeName: [
            { required: true, message: '请输入商品名称', trigger: 'blur' }
          ],
          cateIds: [
            { required: true, message: '请选择商品分类', trigger: 'change', type: 'array', min: '1' }
          ],
          unitName: [
            { required: true, message: '请输入单位', trigger: 'blur' }
          ],
          tempId: [
            {required: true, message: '请选择运费模板', trigger: 'change', type: 'number'}
          ],
          keyword: [
            { required: true, message: '请输入商品关键字', trigger: 'blur' }
          ]
        },
        grid: {
          xl: 8,
          lg: 8,
          md: 12,
          sm: 24,
          xs: 24
        },
        copyConfig: {}
      }
    },
    created() {
      this.goodsCategory();
    },
    computed: {
      attrValue() {
        const obj = Object.assign({}, defaultObj[0])
        delete obj.image
        return obj
      },
      oneFormBatch() {
        const obj = [Object.assign({}, defaultObj[0])]
        delete obj[0].barCode
        return obj
      }
    },
    watch: {
      'formValidate.attr': {
        handler: function(val) {
          this.watCh(val)
        },
        immediate: false,
        deep: true
      }
    },
    mounted() {
      this.productGetTemplate();
      this.getCopyConfig();
    },
    methods: {
      // 删除表格中的属性
      delAttrTable(index) {
        this.ManyAttrValue.splice(index, 1)
      },
      getCopyConfig(){
        copyConfigApi().then(res => {
           this.copyConfig = res
        })
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
        }
      },
      watCh(val) {
        const tmp = {}
        const tmpTab = {}
        this.formValidate.attr.forEach((o, i) => {
          tmp['value' + i] = { title: o.attrName }
          tmpTab['value' + i] = ''
        })
        this.ManyAttrValue = this.attrFormat(val)
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
      // 获取运费模板；
      productGetTemplate() {
        shippingTemplatesList(this.tempData).then(res => {
          this.shippingList = res.list
        })
      },
      // 删除图片
      handleRemove(i) {
        this.formValidate.sliderImages.splice(i, 1);
      },
      // 选择主图
      checked(item, index) {
        this.formValidate.image = item;
      },
      // 商品分类；
      goodsCategory() {
        categoryApi({ status: -1, type: 1 }).then(res => {
          this.merCateList = res
        })
      },
      // 生成表单
      add() {
        if (this.url) {
          // var reg = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
          // if (!reg.test(this.soure_link)) {
          //   return this.$message.warning('请输入以http开头的地址！');
          // }
          this.loading = true;
          importProductApi({ url: this.url, form: this.form}).then(res => {
            this.formValidate = {
              image: res.image,
              sliderImages: JSON.parse(res.sliderImage),
              sliderImage: res.sliderImage,
              storeName: res.storeName,
              storeInfo: res.storeInfo,
              keyword: res.keyword,
              cateIds: res.cateId ? res.cateId.split(',') : [], // 商品分类id
              cateId: res.cateId,// 商品分类id传值
              unitName: res.unitName,
              sort: 0,
              isShow: 0,
              isBenefit: 0,
              isNew: 0,
              isGood: 0,
              isHot: 0,
              isBest: 0,
              tempId: res.tempId,
              attrValue: res.attrValue,
              attr: res.attr || [],
              selectRule: res.selectRule,
              isSub: false,
              content: res.content,
              specType: res.attr.length ? true : false,
              id: res.id,
              giveIntegral: res.giveIntegral,
              ficti: res.ficti
            }
            if(this.formValidate.attr.length){
              for (var i = 0; i < this.formValidate.attr.length; i++) {
                this.formValidate.attr[i].attrValue = JSON.parse(this.formValidate.attr[i].attrValues)
              }
            }
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          })
        } else {
          this.$message.warning('请输入链接地址！');
        }
      },
      // 提交
      handleSubmit (name) {
        this.formValidate.attr.length ? this.formValidate.attrValue=this.ManyAttrValue:this.formValidate.attrValue=[]
        this.formValidate.cateId = this.formValidate.cateIds.join(',')
        this.formValidate.sliderImage = JSON.stringify(this.formValidate.sliderImages)
        for (var i = 0; i < this.formValidate.attr.length; i++) {
          this.formValidate.attr[i].attrValues = JSON.stringify(this.formValidate.attr[i].attrValue)
        }
        this.$refs[name].validate((valid) => {
          if (valid) {
            this.modal_loading = true
            productCreateApi(this.formValidate).then(async res => {
              this.$message.success('新增成功');
              this.$emit('handleCloseMod', false)
              this.modal_loading = false
            }).catch(() => {
              this.modal_loading = false
            })
          } else {
            if(!this.formValidate.storeName || !this.formValidate.cateId  || !this.formValidate.keyword
              || !this.formValidate.unitName || !this.formValidate.image){
              this.$message.warning("请填写完整商品信息！");
            }
          }
        })
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
        },tit, 'store')
      },
      handleDragStart(e, item) {
        this.dragging = item;
      },
      handleDragEnd(e, item) {
        this.dragging = null
      },
      // 首先把div变成可以放置的元素，即重写dragenter/dragover
      handleDragOver(e) {
        // e.dataTransfer.dropEffect="move";//在dragenter中针对放置目标来设置!
        e.dataTransfer.dropEffect = 'move'
      },
      handleDragEnter(e, item) {
        // 为需要移动的元素设置dragstart事件
        e.dataTransfer.effectAllowed = 'move'
        if (item === this.dragging) {
          return
        }
        const newItems = [...this.formValidate.slider_image]
        const src = newItems.indexOf(this.dragging)
        const dst = newItems.indexOf(item)
        newItems.splice(dst, 0, ...newItems.splice(src, 1))
        this.formValidate.slider_image = newItems
      }
    }
  }
</script>

<style scoped lang="scss">
    .selWidth{
      width: 100%;
    }
    .lunBox{
      display: flex;
      flex-direction: column;
      border: 1px solid #0bb20c;
    }
    .pictrueBox{
      display: inline-block;
    }

    .pictrue{
      width:111px;
      height:111px;
      border:1px dotted rgba(0,0,0,0.1);
      display: inline-block;
      position: relative;
      cursor: pointer;
      img{
        width: 100%;
        height: 100%;
      }
    }
    .pictrueTab{
      width:40px !important;
      height:40px !important;
    }
    .upLoad {
        width: 86px;
        height: 86px;
        border: 1px dotted rgba(0, 0, 0, 0.1);
        border-radius: 4px;
        background: rgba(0, 0, 0, 0.02);
        cursor: pointer;
    }
    .ft{
      color: red;
    }
</style>
