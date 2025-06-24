<template>
  <div class="storeEdit">
    <el-form>
      <!-- 单规格表格-->
      <el-form-item v-if="formValidate.specType === false">
        <el-table :data="OneattrValue" border class="tabNumWidth" size="mini">
          <el-table-column label="图片" min-width="80">
            <template slot-scope="scope">
              <div class="upLoadPicBox">
                <div v-if="formValidate.image" class="pictrue tabPic">
                  <el-image :preview-src-list="isDisabled ? [scope.row.image] : []" :src="scope.row.image" />
                </div>
                <div v-else class="upLoad tabPic">
                  <i class="el-icon-camera cameraIconfont" />
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column v-for="(item, iii) in attrValue" :key="iii" :label="formThead[iii].title" min-width="120">
            <template slot-scope="scope">
              <!--                    <span>scope.row:{{scope.row}}</span>-->
              <el-input-number
                v-if="formThead[iii].title === '增加库存'"
                controls-position="right"
                v-model="scope.row[iii]"
                :min="0"
                :step="1"
                step-strictly
              />
              <el-input
                v-else
                :disabled="isDisabled"
                v-model="scope.row[iii]"
                :type="formThead[iii].title === '商品编号' ? 'text' : 'number'"
                class="priceBox"
              />
            </template>
          </el-table-column>
          <template v-if="formValidate.isSub">
            <el-table-column label="一级返佣(元)" min-width="120">
              <template slot-scope="scope">
                <el-input
                  :disabled="isDisabled"
                  v-model="scope.row.brokerage"
                  type="number"
                  :min="0"
                  class="priceBox"
                />
              </template>
            </el-table-column>
            <el-table-column label="二级返佣(元)" min-width="120">
              <template slot-scope="scope">
                <el-input
                  :disabled="isDisabled"
                  v-model="scope.row.brokerageTwo"
                  type="number"
                  :min="0"
                  class="priceBox"
                />
              </template>
            </el-table-column>
          </template>
        </el-table>
      </el-form-item>
      <!-- 多规格-- -->
      <el-form-item
        v-if="formValidate.attr.length > 0 && formValidate.specType"
        label="商品属性："
        class="labeltop"
        :class="isDisabled ? 'disLabel' : 'disLabelmoren'"
      >
        <el-table :data="ManyAttrValue" border class="tabNumWidth" size="mini">
          <template v-if="manyTabDate">
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
              <div class="upLoadPicBox">
                <div v-if="scope.row.image" class="pictrue tabPic">
                  <el-image
                    class="preview-src"
                    :preview-src-list="isDisabled ? [scope.row.image] : []"
                    :src="scope.row.image"
                  />
                </div>
                <div v-else class="upLoad tabPic">
                  <i class="el-icon-camera cameraIconfont" />
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column v-for="(item, iii) in attrValue" :key="iii" :label="formThead[iii].title" min-width="120">
            <template slot-scope="scope">
              <!--                    <span>scope.row:{{scope.row}}</span>-->
              <el-input-number
                v-if="formThead[iii].title === '增加库存'"
                controls-position="right"
                v-model="scope.row[iii]"
                :min="0"
                :step="1"
                step-strictly
              />
              <el-input
                v-else
                :disabled="isDisabled"
                v-model="scope.row[iii]"
                :type="formThead[iii].title === '商品编号' ? 'text' : 'number'"
                class="priceBox"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="success">确认</el-button>
  </div>
</template>
<script>
import { productDetailApi, templateListApi, stockAddApi } from '@/api/store';
import { marketingSendApi } from '@/api/marketing';
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
      addStock: 0,
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
  addStock: {
    title: '增加库存',
  },
};
export default {
  name: 'storeEdit',
  props: ['productId'],
  data() {
    return {
      OneattrValue: [Object.assign({}, defaultObj.attrValue[0])], // 单规格
      ManyAttrValue: [Object.assign({}, defaultObj.attrValue[0])], // 多规格
      formValidate: Object.assign({}, defaultObj),
      formThead: Object.assign({}, objTitle),
      manyTabTit: {},
      manyTabDate: {},
      attrInfo: {},
      isDisabled: true,
      tempRoute: {},
    };
  },
  computed: {
    attrValue() {
      const obj = Object.assign({}, defaultObj.attrValue[0]);
      delete obj.image;
      return obj;
    },
    oneFormBatch() {
      const obj = [Object.assign({}, defaultObj.attrValue[0])];
      delete obj[0].barCode;
      return obj;
    },
  },
  watch: {
    'formValidate.attr': {
      handler: function (val) {
        this.watCh(val); //重要！！！
      },
      immediate: false,
      deep: true,
    },
  },
  created() {
    this.tempRoute = Object.assign({}, this.$route);
    if (this.formValidate.specType) {
      this.$watch('formValidate.attr', this.watCh);
    }
  },
  mounted() {
    this.getInfo(this.productId);
  },
  methods: {
    success() {
      let arr = [];
      if (this.formValidate.specType) {
        this.ManyAttrValue.forEach((val) => {
          arr.push({ addStock: val.addStock, id: val.id, version: 0 });
        });
        stockAddApi({ attrValueList: arr, id: this.ManyAttrValue[0].productId }).then((res) => {
          this.$emit('sucess');
        });
      } else {
        this.OneattrValue.forEach((val) => {
          arr.push({ addStock: val.addStock, id: val.id, version: 0 });
        });
        stockAddApi({ attrValueList: arr, id: this.OneattrValue[0].productId }).then((res) => {
          this.$emit('sucess');
        });
      }
    },
    getInit() {
      if (this.formValidate.specType) {
        this.ManyAttrValue.forEach((val) => {
          this.$set(val, 'addStock', 0);
        });
      } else {
        this.OneattrValue.forEach((val) => {
          this.$set(val, 'addStock', 0);
        });
      }
    },
    // 详情
    getInfo(id) {
      this.fullscreenLoading = true;
      productDetailApi(id)
        .then(async (res) => {
          // this.isAttr = true;
          let info = res;
          this.formValidate = {
            image: this.$selfUtil.setDomain(info.image),
            sliderImage: info.sliderImage,
            sliderImages: JSON.parse(info.sliderImage),
            storeName: info.storeName,
            keyword: info.keyword,
            cateIds: info.cateId.split(','), // 商品分类id
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
            content: info.content ? this.$selfUtil.replaceImgSrcHttps(info.content) : '',
            specType: info.specType,
            id: info.id,
            giveIntegral: info.giveIntegral,
            ficti: info.ficti,
            coupons: info.coupons,
            couponIds: info.couponIds,
            activity: info.activity ? info.activity : ['默认', '秒杀', '砍价', '拼团'],
          };
          marketingSendApi({ type: 3 }).then((res) => {
            if (this.formValidate.couponIds !== null) {
              let ids = this.formValidate.couponIds.toString();
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
              this.$set(this.formValidate, 'coupons', newArr); //在编辑回显时，让返回数据中的优惠券id，通过接口匹配显示,
            }
          });
          let imgs = JSON.parse(info.sliderImage);
          let imgss = [];
          Object.keys(imgs).map((i) => {
            imgss.push(this.$selfUtil.setDomain(imgs[i]));
          });
          this.formValidate.sliderImages = [...imgss];
          if (this.getFileType(this.formValidate.sliderImages[0]) == 'video') {
            //如果返回数据轮播图的第一张是视频，就将其赋值给videoLink做渲染，同时将其在轮播图中删除
            this.$set(this.formValidate, 'videoLink', this.formValidate.sliderImages[0]);
            this.formValidate.sliderImages.splice(0, 1);
          }
          this.productGetRule();
          if (info.specType) {
            this.formValidate.attr = info.attr.map((item) => {
              return {
                attrName: item.attrName,
                attrValue: item.attrValues.split(','),
              };
            });
            this.ManyAttrValue = info.attrValue;
            this.ManyAttrValue.forEach((val) => {
              val.image = this.$selfUtil.setDomain(val.image);
              val.attrValue = JSON.parse(val.attrValue);
              this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val;
            });
            /***多规格商品如果被删除过sku，优先展示api返回的数据,否则会有没有删除的错觉***/
            let manyAttr = this.attrFormat(this.formValidate.attr);
            if (manyAttr.length !== this.ManyAttrValue.length) {
              this.$set(this, 'showAll', true);
              this.isAttr = false;
            } else {
              this.isAttr = true;
            }
            /*******/
            const tmp = {};
            const tmpTab = {};
            this.formValidate.attr.forEach((o, i) => {
              // tmp['value' + i] = { title: o.attrName }
              // tmpTab['value' + i] = ''
              tmp[o.attrName] = { title: o.attrName };
              tmpTab[o.attrName] = '';
            });

            // 此处手动实现后台原本value0 value1的逻辑
            this.formValidate.attrValue.forEach((item) => {
              for (let attrValueKey in item.attrValue) {
                item[attrValueKey] = item.attrValue[attrValueKey];
              }
            });

            this.manyTabTit = tmp;
            this.manyTabDate = tmpTab;
            this.formThead = Object.assign({}, this.formThead, tmp);
          } else {
            this.OneattrValue = info.attrValue;
            // this.formValidate.attr = [] //单规格商品规格设置为空
          }
          this.getInit();
          console.log(this.formValidate);
          this.fullscreenLoading = false;
        })
        .catch((res) => {
          this.fullscreenLoading = false;
          this.$message.error(res.message);
        });
    },
    // 获取商品属性模板；
    productGetRule() {
      templateListApi(this.tableFrom).then((res) => {
        const list = res.list;
        for (var i = 0; i < list.length; i++) {
          list[i].ruleValue = JSON.parse(list[i].ruleValue);
        }
        this.ruleList = list;
      });
    },
    getFileType(fileName) {
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
    },
    watCh(val) {
      const tmp = {};
      const tmpTab = {};
      this.formValidate.attr.forEach((o, i) => {
        tmp[o.attrName] = { title: o.attrName };
        tmpTab[o.attrName] = '';
      });
      this.ManyAttrValue = this.attrFormat(val);
      this.ManyAttrValue.forEach((val, index) => {
        const key = Object.values(val.attrValue).sort().join('/');
        if (this.attrInfo[key]) this.ManyAttrValue[index] = this.attrInfo[key];
      });
      this.attrInfo = [];
      this.ManyAttrValue.forEach((val) => {
        this.attrInfo[Object.values(val.attrValue).sort().join('/')] = val;
      });
      this.manyTabTit = tmp;
      this.manyTabDate = tmpTab;
      this.formThead = Object.assign({}, this.formThead, tmp);
    },
    attrFormat(arr) {
      let data = [];
      const res = [];
      return format(arr);
      function format(arr) {
        if (arr.length > 1) {
          arr.forEach((v, i) => {
            if (i === 0) data = arr[i]['attrValue'];
            const tmp = [];
            if (!data) return;
            data.forEach(function (vv) {
              arr[i + 1] &&
                arr[i + 1]['attrValue'] &&
                arr[i + 1]['attrValue'].forEach((g) => {
                  const rep2 = (i !== 0 ? '' : arr[i]['attrName'] + '_') + vv + '$&' + arr[i + 1]['attrName'] + '_' + g;
                  tmp.push(rep2);
                  if (i === arr.length - 2) {
                    const rep4 = {
                      image: '',
                      price: 0,
                      cost: 0,
                      otPrice: 0,
                      stock: 0,
                      barCode: '',
                      weight: 0,
                      volume: 0,
                      addStock: 0,
                      brokerage: 0,
                      brokerage_two: 0,
                    };
                    rep2.split('$&').forEach((h, k) => {
                      const rep3 = h.split('_');
                      if (!rep4['attrValue']) rep4['attrValue'] = {};
                      rep4['attrValue'][rep3[0]] = rep3.length > 1 ? rep3[1] : '';
                    });
                    for (let attrValueKey in rep4.attrValue) {
                      rep4[attrValueKey] = rep4.attrValue[attrValueKey];
                    }
                    res.push(rep4);
                  }
                });
            });
            data = tmp.length ? tmp : [];
          });
        } else {
          const dataArr = [];
          arr.forEach((v, k) => {
            v['attrValue'].forEach((vv, kk) => {
              dataArr[kk] = v['attrName'] + '_' + vv;
              res[kk] = {
                image: '',
                price: 0,
                cost: 0,
                otPrice: 0,
                stock: 0,
                barCode: '',
                weight: 0,
                volume: 0,
                addStock: 0,
                brokerage: 0,
                brokerage_two: 0,
                attrValue: { [v['attrName']]: vv },
              };
              for (let attrValueKey in res[kk].attrValue) {
                res[kk][attrValueKey] = res[kk].attrValue[attrValueKey];
              }
            });
          });
          data.push(dataArr.join('$&'));
        }
        return res;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.storeEdit {
  padding: 0 35px;
}
.pictrue {
  width: 36px !important;
  height: 36px !important;
}
</style>
