<template>
  <div class="Box">
    <el-card>
      <div class="line-ht">生成的商品默认是没有上架的，请手动上架商品！
        <span v-if="copyConfig.copyType && copyConfig.copyType==1">您当前剩余{{copyConfig.copyNum}}条采集次数，
          <router-link :to="{path:'/operation/systemSms/pay?type=copy'}">
            <span style="color:#1890ff;">增加采集次数</span>
          </router-link>
        </span>
        <el-link v-if="copyConfig.copyType && copyConfig.copyType!=1" type="primary" :underline="false"
                 href="https://help.crmeb.net/crmeb_java/2103903" target="_blank">如何配置密钥
        </el-link>
        <br>
        商品采集设置：设置 > 系统设置 > 第三方接口设置 > 采集商品配置（如配置一号通采集，请先登录一号通账号，无一号通，请选择99Api设置）
      </div>
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
              <el-button slot="append" icon="el-icon-search" @click="add" size="small" v-hasPermi="['admin:product:copy:product','admin:product:import:product']" />
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
              <el-input v-model="formValidate.storeInfo" maxlength="250" type="textarea" :rows="3"
                        placeholder="请输入商品简介"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品分类：" prop="cateIds">
              <el-cascader v-model="formValidate.cateIds" :options="merCateList" :props="props2" clearable
                           class="selWidth" :show-all-levels="false"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品关键字：" prop="keyword">
              <el-input v-model="formValidate.keyword" placeholder="请输入商品关键字"></el-input>
            </el-form-item>
          </el-col>
          <el-col v-bind="grid">
            <el-form-item label="单位：" prop="unitName">
              <el-input v-model="formValidate.unitName" placeholder="请输入单位" class="selWidth"></el-input>
            </el-form-item>
          </el-col>
          <el-col v-bind="grid">
            <el-form-item label="运费模板：" prop="tempId">
              <el-select v-model="formValidate.tempId" placeholder="请选择" class="selWidth">
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
              <el-table :data="formValidate.attrValue" border class="tabNumWidth" size="mini">
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
                    <el-form-item :rules="[{required: true, message: '请上传图片', trigger: 'change'}]" :prop="'attrValue.'+scope.$index+'.image'">
                      <div class="upLoadPicBox" @click="modalPicTap('1','duo',scope.$index)">
                        <div v-if="scope.row.image" class="pictrue pictrueTab"><img :src="scope.row.image"></div>
                        <div v-else class="upLoad pictrueTab">
                          <i class="el-icon-camera cameraIconfont"/>
                        </div>
                      </div>
                    </el-form-item>
                  </template>
                </el-table-column>
                <el-table-column v-for="(item,iii) in attrValue" :key="iii" :label="formThead[iii].title"
                                 align="center" min-width="120">
                  <template slot-scope="scope">
                    <el-form-item :rules="[{required: true, message: '请输入'+formThead[iii].title, trigger: 'blur'}]" :prop="formThead[iii].title!=='商品编号'?'attrValue.'+scope.$index+'.'+iii:''">
                      <el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'" class="priceBox" />
                    </el-form-item>
                    <!--<el-input v-model="scope.row[iii]" :type="formThead[iii].title==='商品编号'?'text':'number'"-->
                              <!--class="priceBox"/>-->
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
              <Tinymce v-model="formValidate.content"></Tinymce>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <template>
              <el-row :gutter="24">
                <el-col :span="24">
                  <template>
                    <el-row :gutter="24">
                      <el-col :span="8">
                      <el-form-item label="排序：">
                        <el-input-number v-model="formValidate.sort" :max="9999" placeholder="请输入排序" :disabled="isDisabled" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="积分：">
                        <el-input-number v-model="formValidate.giveIntegral" placeholder="请输入排序" :disabled="isDisabled" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="虚拟销量：">
                        <el-input-number v-model="formValidate.ficti" placeholder="请输入排序" :disabled="isDisabled" />
                      </el-form-item>
                    </el-col>
                    </el-row>
                  </template>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="商品推荐：">
                    <el-checkbox-group v-model="checkboxGroup" size="small" @change="onChangeGroup" :disabled="isDisabled">
                      <el-checkbox v-for="(item, index) in recommend" :key="index" :label="item.value">{{ item.name }}</el-checkbox>
                    </el-checkbox-group>
                  </el-form-item>
                </el-col>
              </el-row>
            </template>
          </el-col>

          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" :loading="modal_loading" class="submission" @click="handleSubmit('formValidate')">提交
              </el-button>
            </el-form-item>
          </el-col>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
  import {
    copyProductApi,
    crawlFromApi,
    treeListApi,
    crawlSaveApi,
    categoryApi,
    importProductApi,
    productCreateApi,
    copyConfigApi
  } from '@/api/store';
  import { goodDesignList } from "@/api/systemGroup";
  import Tinymce from '@/components/Tinymce/index'
  import {shippingTemplatesList} from '@/api/logistics'
  import {Debounce} from '@/utils/validate'
  const defaultObj = [{
    image: '',
    price: null,
    cost: null,
    otPrice: null,
    stock: null,
    barCode: '',
    weight: 0,
    volume: 0,
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
    components: {Tinymce },
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
        checkboxGroup: [], //
        recommend: [],
        modal_loading: false,
        ManyAttrValue: [Object.assign({}, defaultObj[0])], // 多规格
        imgList: [],
        tempData: {
          page: 1,
          limit: 9999
        },
        shippingList: [],
        merCateList: [],
        images: '',
        url: '',
        modalPic: false,
        isChoice: '',
        isDisabled:false,
        ruleInline: {
          storeName: [
            {required: true, message: '请输入商品名称', trigger: 'blur'}
          ],
          cateIds: [
            {required: true, message: '请选择商品分类', trigger: 'change', type: 'array', min: '1'}
          ],
          unitName: [
            {required: true, message: '请输入单位', trigger: 'blur'}
          ],
          tempId: [
            {required: true, message: '请选择运费模板', trigger: 'change', type: 'number'}
          ],
          keyword: [
            {required: true, message: '请输入商品关键字', trigger: 'blur'}
          ],
          attrValue: [
            { required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }
          ]
        },
        grid: {
          xl: 12,
          lg: 12,
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
        handler: function (val) {
          this.watCh(val)
        },
        immediate: false,
        deep: true
      }
    },
    mounted() {
      this.productGetTemplate();
      this.getCopyConfig();
      this.getGoodsType()
    },
    methods: {
      // 删除表格中的属性
      delAttrTable(index) {
        this.formValidate.attrValue.splice(index, 1)
      },
      getCopyConfig() {
        copyConfigApi().then(res => {
          this.copyConfig = res
        })
      },
       onChangeGroup() {
        this.checkboxGroup.includes('isGood') ? this.formValidate.isGood = true : this.formValidate.isGood = false
        this.checkboxGroup.includes('isBenefit') ? this.formValidate.isBenefit = true : this.formValidate.isBenefit = false
        this.checkboxGroup.includes('isBest') ? this.formValidate.isBest = true : this.formValidate.isBest = false
        this.checkboxGroup.includes('isNew') ? this.formValidate.isNew = true : this.formValidate.isNew = false
        this.checkboxGroup.includes('isHot') ? this.formValidate.isHot = true : this.formValidate.isHot = false
      },
      // 批量添加
      batchAdd() {
        // if (!this.oneFormBatch[0].pic || !this.oneFormBatch[0].price || !this.oneFormBatch[0].cost || !this.oneFormBatch[0].ot_price ||
        //     !this.oneFormBatch[0].stock || !this.oneFormBatch[0].bar_code) return this.$Message.warning('请填写完整的批量设置内容！');
        for (const val of this.formValidate.attrValue) {
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
          // tmp['value' + i] = {title: o.attrName}
          // tmpTab['value' + i] = ''
          tmp[o.attrName] = { title: o.attrName };
          tmpTab[o.attrName] = '';
        })
        this.formValidate.attrValue = this.attrFormat(val)
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
              data.forEach(function (vv) {
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
                    // Object.values(rep4.attrValue).forEach((v, i) => {
                    //   rep4['value' + i] = v
                    // })
                    for (let attrValueKey in rep4.attrValue) {
                      rep4[attrValueKey] = rep4.attrValue[attrValueKey];
                    }
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
                  attrValue: {[v['attrName']]: vv}
                }
                for (let attrValueKey in res[kk].attrValue) {
                  res[kk][attrValueKey] = res[kk].attrValue[attrValueKey];
                }
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
        this.$forceUpdate();
      },
      // 选择主图
      checked(item, index) {
        this.formValidate.image = item;
      },
      // 商品分类；
      goodsCategory() {
        categoryApi({status: -1, type: 1}).then(res => {
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
          this.copyConfig.copyType == 1 ? copyProductApi({url: this.url}).then(res => {
            let info = res.info;
            this.formValidate = {
              image: this.$selfUtil.setDomain(info.image),
              sliderImage: info.sliderImage,
              storeName: info.storeName,
              storeInfo: info.storeInfo,
              keyword: info.keyword,
              cateIds: info.cateId ? info.cateId.split(',') : [], // 商品分类id
              cateId: info.cateId,// 商品分类id传值
              unitName: info.unitName,
              sort: 0,
              isShow: 0,
              isBenefit: 0,
              isNew: 0,
              isGood: 0,
              isHot: 0,
              isBest: 0,
              tempId: info.tempId,
              attrValue: info.attrValue,
              attr: info.attr || [],
              selectRule: info.selectRule,
              isSub: false,
              content: this.$selfUtil.replaceImgSrcHttps(info.content),
              specType: info.attr.length ? true : false,
              id: info.id,
              giveIntegral: info.giveIntegral,
              ficti: info.ficti
            }
            if(info.isHot) this.checkboxGroup.push('isHot')
            if(info.isGood) this.checkboxGroup.push('isGood')
            if(info.isBenefit) this.checkboxGroup.push('isBenefit')
            if(info.isBest) this.checkboxGroup.push('isBest')
            if(info.isNew) this.checkboxGroup.push('isNew')
              let imgs = JSON.parse(info.sliderImage)
            let imgss = []
            Object.keys(imgs).map(i => {
              imgss.push(this.$selfUtil.setDomain(imgs[i]))
            })
            this.formValidate.sliderImages = imgss
            if (this.formValidate.attr.length) {
              this.oneFormBatch[0].image = this.$selfUtil.setDomain(info.image)
              for (var i = 0; i < this.formValidate.attr.length; i++) {
                this.formValidate.attr[i].attrValue = JSON.parse(this.formValidate.attr[i].attrValues)
              }
            }
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          }) : importProductApi({url: this.url, form: this.form}).then(res => {
            this.formValidate = {
              image: this.$selfUtil.setDomain(res.image),
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
            let imgs = JSON.parse(res.sliderImage)
            let imgss = []
            Object.keys(imgs).map(i => {
              imgss.push(this.$selfUtil.setDomain(imgs[i]))
            })
            this.formValidate.sliderImages = imgss
            if (this.formValidate.attr.length) {
              this.oneFormBatch[0].image = this.$selfUtil.setDomain(res.image)
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
      handleSubmit:Debounce(function(name) {
        let pram = JSON.parse(JSON.stringify(this.formValidate));
       // this.formValidate.attr.length ? this.formValidate.attrValue = this.ManyAttrValue : this.formValidate.attrValue = []
        pram.attr.forEach(item => {
          item.attrValues = item.attrValue.join(",");
        });
        pram.cateId = pram.cateIds.join(',')
        pram.sliderImage = JSON.stringify(pram.sliderImages);
        pram.attrValue.forEach(itemData => {
          itemData.attrValue = JSON.stringify(itemData.attrValue);
        });
        this.$refs[name].validate((valid) => {
          if (valid) {
            this.modal_loading = true
            productCreateApi(pram).then(async res => {
              this.$message.success('新增成功');
              this.$emit('handleCloseMod', false)
              this.modal_loading = false
            }).catch(() => {
              this.modal_loading = false
            })
          } else {
            if (!pram.storeName || !pram.cateId || !pram.keyword
              || !pram.unitName || !pram.image) {
              this.$message.warning("请填写完整商品信息！");
            }
          }
        })
      }),
      // 点击商品图
      modalPicTap(tit, num, i) {
        const _this = this
        this.$modalUpload(function (img) {
          if (tit === '1' && !num) {
            _this.formValidate.image = img[0].sattDir
            _this.OneattrValue[0].image = img[0].sattDir
          }
          if (tit === '2' && !num) {
            if (img.length > 10) return this.$message.warning("最多选择10张图片！");
            if (img.length + _this.formValidate.sliderImages.length > 10) return this.$message.warning("最多选择10张图片！");
            img.map((item) => {
              _this.formValidate.sliderImages.push(item.sattDir)
            });
          }
          if (tit === '1' && num === 'dan') {
            _this.OneattrValue[0].image = img[0].sattDir
          }
          if (tit === '1' && num === 'duo') {
            _this.formValidate.attrValue[i].image = img[0].sattDir
          }
          if (tit === '1' && num === 'pi') {
            _this.oneFormBatch[0].image = img[0].sattDir
          }
        }, tit, 'store')
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
      },
      getGoodsType(){
        /** 让商品推荐列表的name属性与页面设置tab的name匹配**/
        goodDesignList({gid:70}).then((response)=>{
          let list = response.list;
          let arr = [];
          let arr1 = [];
          const listArr = [{ name: '是否热卖', value: 'isHot' }];
          let typeLists = [ 
            { name: '', value: 'isGood',type:'2' },  //精品推荐1  热门榜单2 首发新品3 促销单品4 
            { name: '', value: 'isBenefit' ,type:'4'}, 
            { name: '', value: 'isBest',type:'1' }, 
            { name: '', value: 'isNew',type:'3' }];
          list.forEach((item)=>{
            let obj = {};
            obj.value = JSON.parse(item.value);
            obj.id = item.id;
            obj.gid = item.gid;
            obj.status = item.status;
            arr.push(obj);
          })
          arr.forEach((item1)=>{
            let obj1 = {};
            obj1.name = item1.value.fields[1].value;
            obj1.status = item1.status;
            obj1.type = item1.value.fields[3].value;
            arr1.push(obj1);
          })
          typeLists.forEach((item)=>{
            arr1.forEach((item1)=>{
              if(item.type == item1.type){
                listArr.push({
                  name:item1.name,
                  value:item.value,
                  type:item.type
                })
              }
            })
          })
          this.recommend = listArr
        })
      },
    }
  }
</script>

<style scoped lang="scss">
  .formValidate{
    ::v-deep .el-form-item__error{
      position: static !important;
    }
  }
  .selWidth {
    width: 100%;
  }

  .line-ht{
    line-height: 28px;
  }

  .lunBox {
    display: flex;
    flex-direction: column;
    border: 1px solid #0bb20c;
  }

  .pictrueBox {
    display: inline-block;
  }

  .pictrue {
    width: 111px;
    height: 111px;
    border: 1px dotted rgba(0, 0, 0, 0.1);
    display: inline-block;
    position: relative;
    cursor: pointer;
    img {
      width: 100%;
      height: 100%;
    }
  }

  .pictrueTab {
    width: 40px !important;
    height: 40px !important;
  }

  .upLoad {
    width: 86px;
    height: 86px;
    border: 1px dotted rgba(0, 0, 0, 0.1);
    border-radius: 4px;
    background: rgba(0, 0, 0, 0.02);
    cursor: pointer;
  }

  .ft {
    color: red;
  }
</style>
