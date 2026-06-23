<template>
  <div>
    <el-row :gutter="24">
      <el-col :span="24">
        <el-form-item label="佣金设置：" props="isSub">
          <el-radio-group
            v-model="formValidate.isSub"
            @change="onChangetype(formValidate.isSub)"
            :disabled="isDisabled"
          >
            <el-radio :label="true" class="radio">单独设置</el-radio>
            <el-radio :label="false">默认设置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="规格类型：" props="specType">
          <el-radio-group
            v-model="formValidate.specType"
            @change="onChangeSpec(formValidate.specType)"
            :disabled="isDisabled"
          >
            <el-radio :label="false" class="radio">单规格</el-radio>
            <el-radio :label="true">多规格</el-radio>
          </el-radio-group>
          <el-dropdown
            :disabled="isDisabled"
            v-if="formValidate.specType && ruleList.length"
            class="ml20 dropdown-menu-box"
            trigger="hover"
            @command="confirmAttrTemp"
          >
            <span class="el-dropdown-link"> 选择规格模板<i class="el-icon-arrow-down el-icon--right"></i> </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-for="(item, index) in ruleList" :key="index" :command="item.ruleName">
                {{ item.ruleName }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-form-item>
      </el-col>
      <!-- 多规格设置 -->
      <el-col :span="24" v-if="formValidate.specType && !isDisabled">
        <el-form-item label="商品规格：">
          <div v-if="formValidate.attr.length" class="attr-tips from-tips mb20">
            勾选添加规格图，当前规格图片必须上传
          </div>
          <div class="specifications">
            <draggable
              group="specifications"
              :disabled="formValidate.attr.length < 2"
              :list="formValidate.attr"
              handle=".attr-move-icon"
              @end="onMoveSpec($event, 'attr')"
              animation="300"
            >
              <div
                class="specifications-item active"
                v-for="(item, index) in formValidate.attr"
                :key="index"
                @click="changeCurrentIndex(index)"
              >
                <div class="attr-move-icon">
                  <span class="iconfont icondrag2"></span>
                </div>
                <i class="del el-icon-error" @click="handleRemoveAttr(index)"></i>
                <div class="specifications-item-box">
                  <div class="lineBox"></div>
                  <div class="specifications-item-name mb18">
                    <el-input
                      size="small"
                      v-model="item.attrName"
                      placeholder="规格名称"
                      @input="changeAttr(index, item.attrName)"
                      @focus="handleFocus(item.attrName)"
                      class="specifications-item-name-input attr-input w-240"
                      maxlength="30"
                      show-word-limit
                    ></el-input>
                    <el-checkbox
                      class="ml20"
                      v-model="item.isShowImage"
                      :disabled="!item.isShowImage && !canSel"
                      :true-label="1"
                      :false-label="0"
                      @change="(e) => addPic(e, index)"
                      >添加规格图</el-checkbox
                    >
                    <el-tooltip
                      class="item"
                      effect="dark"
                      content="添加规格图片, 仅支持打开一个(建议尺寸:800*800),勾选后,当前规格图片必须上传"
                      placement="right"
                    >
                      <i class="el-icon-info"></i>
                    </el-tooltip>
                  </div>
                  <div class="rulesBox ml30">
                    <draggable
                      class="item"
                      :list="item.optionList"
                      :disabled="item.optionList.length < 2"
                      handle=".value-move-icon"
                      @end="onMoveSpec($event, index, 'value')"
                    >
                      <div v-for="(det, indexn) in item.optionList" :key="indexn" class="mr10 spec">
                        <i class="el-icon-error" @click="handleRemoveValue(item, indexn, det.value)"></i>
                        <el-input
                          class="attr-input w-240"
                          size="small"
                          v-model="det.value"
                          placeholder="规格值"
                          @input="changeValue(det.value, index, indexn)"
                          @focus="handleFocus(det.value)"
                          maxlength="30"
                          show-word-limit
                          @blur="handleBlur()"
                        >
                          <template slot="prefix">
                            <span class="iconfont icondrag2 value-move-icon"></span>
                          </template>
                        </el-input>
                        <div class="img-popover" v-if="item.isShowImage">
                          <div class="popper-arrow"></div>
                          <div class="popper" @click="handleSelImg(det, indexn)">
                            <el-image class="image" v-if="det.image" :src="det.image" fit="cover"></el-image>
                            <i v-else class="el-icon-plus"></i>
                          </div>
                          <i v-if="det.image" class="img-del el-icon-error" @click="handleRemoveImg(det)"></i>
                        </div>
                      </div>
                      <div>
                        <el-popover
                          :ref="'popoverRef_' + index"
                          placement=""
                          width="240"
                          trigger="click"
                          @after-enter="handleShowPop(index)"
                        >
                          <el-input
                            :ref="'inputRef_' + index"
                            size="small"
                            class="attr-input w-240"
                            placeholder="请输入规格值"
                            v-model="formDynamic.attrsVal"
                            @keyup.enter.native="createAttr(formDynamic.attrsVal, index)"
                            @blur="createAttr(formDynamic.attrsVal, index)"
                            maxlength="30"
                            show-word-limit
                          >
                          </el-input>
                          <div class="addfont" slot="reference">
                            <span slot="reference">添加规格值</span>
                          </div>
                          <div class="empty-box" slot="reference"></div>
                        </el-popover>
                      </div>
                    </draggable>
                  </div>
                </div>
              </div>
            </draggable>
          </div>
          <div class="flex">
            <el-button @click="handleAddRole">添加新规格</el-button>
            <el-button v-if="formValidate.attr.length >= 1" @click="handleSaveAsTemplate()">另存为模板</el-button>
          </div>
        </el-form-item>
      </el-col>
      <el-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
        <!-- 单规格表格-->
        <el-form-item v-if="formValidate.specType === false">
          <el-table :data="OneattrValue" border class="tabNumWidth" size="mini">
            <el-table-column label="图片" min-width="60">
              <template slot-scope="scope">
                <div class="upLoadPicBox" @click="modalPicTap('1', 'dan')">
                  <div v-if="formValidate.image" class="pictrue tabPic">
                    <el-image :preview-src-list="isDisabled ? [scope.row.image] : []" :src="OneattrValue[0].image" />
                  </div>
                  <div v-else class="upLoad tabPic">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </template>
            </el-table-column>
            <template v-if="formValidate.isSub">
              <el-table-column label="一级返佣(%)" min-width="100">
                <el-input v-model="OneattrValue[0].brokerage" :disabled="isDisabled"></el-input>
              </el-table-column>
              <el-table-column label="二级返佣(%)" min-width="100">
                <el-input v-model="OneattrValue[0].brokerageTwo"  :disabled="isDisabled"></el-input>
              </el-table-column>
            </template>
            <el-table-column
              v-for="(item, iii) in oneAttrTableTitle"
              :key="iii"
              :label="item.title"
              :min-width="item.minWidth || '200'"
            >
              <template slot-scope="scope" v-if="item.slot != 'isDefault' && item.slot != 'action'">
                <el-input
                  :disabled="isDisabled"
                  v-model="OneattrValue[0][item.slot]"
                  :type="item.title === '商品编码' ? 'text' : 'number'"
                  :min="0"
                  class="priceBox"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <!-- 多规格表格-->
        <el-form-item
          v-if="formValidate.attr.length > 0 && formValidate.specType"
          label="商品属性："
          class="labeltop"
          :class="isDisabled ? 'disLabel' : 'disLabelmoren'"
        >
          <el-table
            :data="ManyAttrValue"
            border
            class="tabNumWidth"
            size="small"
            :span-method="objectSpanMethod"
            :cell-class-name="tableCellClassName"
            :key="tableKey"
          >
            <template v-if="formValidate.header">
              <el-table-column
                v-for="(item, iii) in formValidate.header"
                :key="iii"
                :label="item.title"
                :min-width="item.minWidth || '200'"
              >
                <template slot-scope="scope">
                  <!-- 批量设置 -->
                  <template v-if="scope.$index == 0">
                    <!-- 自定义规格属性 -->
                    <template v-if="item.key">
                      <div v-if="formValidate.attr.length && formValidate.attr[iii] && ManyAttrValue.length">
                        <el-select v-model="oneFormBatch[0][item.title]" :placeholder="`请选择${item.title}`" clearable>
                          <el-option
                            v-for="(val, valIdx) in formValidate.attr[iii].optionList"
                            :key="valIdx"
                            :label="val.value"
                            :value="val.value"
                          >
                          </el-option>
                        </el-select>
                      </div>
                    </template>
                    <!-- 图片 -->
                    <template v-else-if="item.slot === 'image'">
                      <div class="upLoadPicBox" @click="modalPicTap('1', 'pi', scope.$index)">
                        <div v-if="oneFormBatch[0].image" class="pictrue tabPic">
                          <el-image
                            class="preview-src"
                            :preview-src-list="isDisabled ? [scope.row.image] : []"
                            :src="oneFormBatch[0].image"
                          />
                        </div>
                        <div v-else class="upLoad tabPic">
                          <i class="el-icon-camera cameraIconfont" />
                        </div>
                      </div>
                    </template>
                    <!-- 售价 -->
                    <template v-else-if="item.slot === 'price'">
                      <el-input-number
                        :controls="false"
                        v-model="oneFormBatch[0].price"
                        :min="0"
                        :max="999999.99"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 成本价 -->
                    <template v-else-if="item.slot === 'cost'">
                      <el-input-number
                        :controls="false"
                        v-model="oneFormBatch[0].cost"
                        :min="0"
                        :max="999999.99"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 原价 -->
                    <template v-else-if="item.slot === 'otPrice'">
                      <el-input-number
                        :controls="false"
                        v-model="oneFormBatch[0].otPrice"
                        :min="0"
                        :max="999999.99"
                        :disabled="isDisabled"
                        class="priceBox"
                      ></el-input-number>
                    </template>
                    <!-- 库存 -->
                    <template v-else-if="item.slot === 'stock'">
                      <el-input-number
                        :controls="false"
                        v-model="oneFormBatch[0].stock"
                        :min="0"
                        :max="999999"
                        :precision="0"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 商品编号 -->
                    <template v-else-if="item.slot === 'barCode'">
                      <el-input v-model="oneFormBatch[0].barCode" maxlength="40" class="priceBox" :disabled="isDisabled"></el-input>
                    </template>
                    <!-- 一级佣金 -->
                    <template v-else-if="item.slot === 'brokerage' && formValidate.isSub">
                      <el-input v-model="oneFormBatch[0].brokerage" :disabled="isDisabled"></el-input>
                    </template>
                    <!-- 二级佣金 -->
                    <template v-else-if="item.slot === 'brokerageTwo' && formValidate.isSub">
                      <el-input v-model="oneFormBatch[0].brokerageTwo" :disabled="isDisabled"></el-input>
                    </template>
                    <!-- 重量(KG) -->
                    <template v-else-if="item.slot === 'weight'">
                      <el-input-number
                        :controls="false"
                        v-model="oneFormBatch[0].weight"
                        :step="0.1"
                        :max="999999"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 体积(m³) -->
                    <template v-else-if="item.slot === 'volume'">
                      <el-input-number
                        :controls="false"
                        v-model="oneFormBatch[0].volume"
                        :step="0.1"
                        :min="0"
                        :max="999999"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 是否默认 -->
                    <template v-else-if="item.slot === 'isDefault'"> -- </template>
                    <!-- 操作 -->
                    <template v-else-if="item.slot === 'action'">
                      <a type="text" size="mini" @click="batchAdd">批量修改</a>
                      <a type="text" size="mini" @click="batchDel" class="ml10">清空</a>
                    </template>
                  </template>
                  <!-- 规格数据 -->
                  <template v-else>
                    <!-- 自定义规格属性 -->
                    <template v-if="item.key">
                      <div class="text-center">
                        <span>{{ scope.row[item.key] }}</span>
                      </div>
                    </template>
                    <!-- 图片 -->
                    <template v-else-if="item.slot === 'image'">
                      <div class="upLoadPicBox" @click="modalPicTap('1', 'duo', scope.$index)">
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
                    <!-- 售价 -->
                    <template v-else-if="item.slot === 'price'">
                      <el-input-number
                        :controls="false"
                        v-model="ManyAttrValue[scope.$index].price"
                        :min="0"
                        :max="999999.99"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 成本价 -->
                    <template v-else-if="item.slot === 'cost'">
                      <el-input-number
                        :controls="false"
                        v-model="ManyAttrValue[scope.$index].cost"
                        :min="0"
                        :max="999999.99"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 原价 -->
                    <template v-else-if="item.slot === 'otPrice'">
                      <el-input-number
                        :controls="false"
                        v-model="ManyAttrValue[scope.$index].otPrice"
                        :min="0"
                        :max="999999.99"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 库存 -->
                    <template v-else-if="item.slot === 'stock'">
                      <el-input-number
                        :controls="false"
                        v-model="ManyAttrValue[scope.$index].stock"
                        :min="0"
                        :max="999999"
                        :precision="0"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 商品编号 -->
                    <template v-else-if="item.slot === 'barCode'">
                      <el-input
                        v-model="ManyAttrValue[scope.$index].barCode"
                        maxlength="40"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input>
                    </template>
                    <!-- 一级佣金 -->
                    <template v-else-if="item.slot === 'brokerage' && formValidate.isSub">
                      <el-input v-model="ManyAttrValue[scope.$index].brokerage" :disabled="isDisabled"></el-input>
                    </template>
                    <!-- 二级佣金 -->
                    <template v-else-if="item.slot === 'brokerageTwo' && formValidate.isSub">
                      <el-input v-model="ManyAttrValue[scope.$index].brokerageTwo" :disabled="isDisabled"></el-input>
                    </template>
                    <!-- 重量(KG) -->
                    <template v-else-if="item.slot === 'weight'">
                      <el-input-number
                        :controls="false"
                        v-model="ManyAttrValue[scope.$index].weight"
                        :step="0.1"
                        :max="999999"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 体积(m³) -->
                    <template v-else-if="item.slot === 'volume'">
                      <el-input-number
                        :controls="false"
                        v-model="ManyAttrValue[scope.$index].volume"
                        :step="0.1"
                        :min="0"
                        :max="999999"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input-number>
                    </template>
                    <!-- 是否默认 -->
                    <template v-else-if="item.slot === 'isDefault'">
                      <el-switch
                        v-model="ManyAttrValue[scope.$index].isDefault"
                        :active-value="true"
                        :inactive-value="false"
                        active-text="默认"
                        :disabled="isDisabled"
                        @change="(e) => changeDefaultSelect(e, scope.$index)"
                      />
                    </template>
                    <!-- 操作 -->
                    <template v-else-if="item.slot === 'action'">
                      <el-switch
                        class="defineSwitch"
                        v-model="ManyAttrValue[scope.$index].isShow"
                        active-text="显示"
                        inactive-text="隐藏"
                        :active-value="true"
                        :inactive-value="false"
                        :disabled="isDisabled"
                        @change="changeDefaultShow(scope.$index)"
                      />
                    </template>
                  </template>
                </template>
              </el-table-column>
            </template>
          </el-table>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { templateListApi, attrCreatApi } from '@/api/store';
import { GoodsTableHead, imageTableHead, commissionTableHead } from '../creatStore/TableHeadList';
import { defaultObj } from '../creatStore/default';
import { arraysEqual } from '@/utils';
import vuedraggable from 'vuedraggable';
export default {
  name: 'creatAttr',
  components: {
    draggable: vuedraggable,
  },
  props: {
    // 商品数据
    value: {
      type: Object,
      default: function () {
        return {};
      },
    },
    // 单规格
    OneattrValue: {
      type: Array,
      default: function () {
        return [];
      },
    },
    // 多规格
    ManyAttrValue: {
      type: Array,
      default: function () {
        return [];
      },
    },
    //批量添加规格
    oneFormBatch: {
      type: Array,
      default: function () {
        return [];
      },
    },
    isDisabled: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      tableKey: 0,
      formValidate: this.value, // 商品数据
      isAttr: false, // 是否需要显示sku
      ruleList: [], // 商品属性模板列表
      currentIndex: 0, // 当前规格索引
      changeAttrValue: '', // 当前要修改的规格值
      canSel: true, // 规格图片添加判断
      // 规格数据
      formDynamic: {
        attrsName: '',
        attrsVal: '',
      },
      attrs: [], // 规格列表
    };
  },
  computed: {
    // 单规格表头
    oneAttrTableTitle() {
      const arr = GoodsTableHead.slice(0, -2);
      return arr;
    },
    attrValue() {
      const obj = Object.assign({}, defaultObj.attrValue[0]);
      delete obj.image;
      return obj;
    },
  },
  watch: {
    formValidate: {
      handler(newVal) {
        this.formValidate = newVal;
        // this.$emit('input', newVal);
      },
      // deep: true,
    },
  },
  mounted() {
    if (this.formValidate.specType) {
      this.productGetRule(); //加载商品规格选项
      // 添加 optionList 字段
      this.createOptionList(this.formValidate.attr);
      // 生成规格属性数据
      this.generateAttr(this.formValidate.attr);
    }
  },
  methods: {
    // 合并单元格
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0 && rowIndex > 0) {
        let lable = column.label;
        //这里判断第几列需要合并
        const tagFamily = this.ManyAttrValue[rowIndex].attrValue[lable];
        const index = this.ManyAttrValue.findIndex((item, index) => {
          if (index > 0) return item.attrValue[lable] == tagFamily;
        });
        if (rowIndex == index) {
          let len = 1;
          for (let i = index + 1; i < this.ManyAttrValue.length; i++) {
            if (this.ManyAttrValue[i].attrValue[lable] !== tagFamily) {
              break;
            }
            len++;
          }
          return {
            rowspan: len,
            colspan: 1,
          };
        } else {
          return {
            rowspan: 0,
            colspan: 0,
          };
        }
      }
    },
    // 生成列表 行 列 数据
    tableCellClassName({ row, column, rowIndex, columnIndex }) {
      //注意这里是解构
      //利用单元格的 className 的回调方法，给行列索引赋值
      row.index = rowIndex || '';
      column.index = columnIndex;
    },
    // 获取商品属性模板
    productGetRule() {
      templateListApi(this.tableFrom).then((res) => {
        const list = res.list;
        for (var i = 0; i < list.length; i++) {
          list[i].ruleValue = JSON.parse(list[i].ruleValue);
        }
        this.ruleList = list;
      });
    },
    // 修改佣金是否为默认设置
    onChangetype(item) {
      if (item === 1) {
        this.OneattrValue.map((item) => {
          this.$set(item, 'brokerage', null);
          this.$set(item, 'brokerageTwo', null);
        });
        this.ManyAttrValue.map((item) => {
          this.$set(item, 'brokerage', null);
          this.$set(item, 'brokerageTwo', null);
        });
      } else {
        this.OneattrValue.map((item) => {
          // delete item.brokerage;
          // delete item.brokerageTwo;
          this.$set(item, 'brokerage', 0);
          this.$set(item, 'brokerageTwo', 0);
        });
        this.ManyAttrValue.map((item) => {
          // delete item.brokerage;
          // delete item.brokerageTwo;
          this.$set(item, 'brokerage', 0);
          this.$set(item, 'brokerageTwo', 0);
        });
      }
      this.generateHeader(this.formValidate.attr);
    },
    // 修改规格类型
    onChangeSpec(num) {
      this.isAttr = true;
      if (num) this.productGetRule();
    },
    // 选择规格模板
    confirmAttrTemp(name) {
      this.canSel = true;
      this.formValidate.selectRule = name;
      if (!this.formValidate.selectRule) {
        return this.$message.warning('请选择属性');
      }
      const dataAttrs = [];
      this.ruleList.forEach((item) => {
        if (item.ruleName === this.formValidate.selectRule) {
          item.ruleValue.forEach((i) => {
            dataAttrs.push({
              attrName: i.value,
              optionList: i.detail.map((val) => ({ value: val, image: '', sort: 0  })),
              id: 0,
              isShowImage: 0,
              sort: i + 1,
            });
          });
        }
        this.formValidate.attr = dataAttrs;
      });
      this.generateAttr(this.formValidate.attr);
    },
    // 添加 optionList 字段
    createOptionList(attr) {
      // 如果 optionList 字段无内容
      if (attr[0].optionList.length == 0) {
        // 添加 optionList 内容
        this.attrs = attr.map((item) => {
          const attrValueList = item.attrValues.split(',');
          item.optionList = attrValueList.map((val) => {
            return {
              value: val,
              image: '',
            };
          });
        });
      }
    },
    // 生成商品规格表头
    generateHeader(attr) {
      let specificationsColumns = attr.map((item) => ({
        title: item.attrName,
        key: item.attrName,
        minWidth: 200,
        fixed: 'left',
      }));
      let arr;
      // 开启佣金设置
      if (this.formValidate.isSub) {
        arr = [...specificationsColumns, ...imageTableHead, ...commissionTableHead, ...GoodsTableHead];
      } else {
        arr = [...specificationsColumns, ...imageTableHead, ...GoodsTableHead];
      }
      this.$set(this.formValidate, 'header', arr);
      this.tableKey += 1;
    },
    // 生成规格组合
    generateCombinations(attr, prefix = []) {
      if (attr.length === 0) {
        return [prefix];
      }
      const [first, ...rest] = attr;
      return first.optionList.flatMap((detail) => this.generateCombinations(rest, [...prefix, detail.value]));
    },
    // 生成规格属性数据
    generateAttr(attr, val) {
      // 生成商品规格表头
      this.generateHeader(attr);
      // 生成规格值组合
      const combinations = this.generateCombinations(attr);
      let rows = combinations.map((combination) => {
        const row = {
          attrArr: combination,
          attrValueShow: {},
          attrValue: {},
          image: '',
          price: 0,
          cost: 0,
          otPrice: 0,
          stock: 0,
          barCode: '',
          weight: 0,
          volume: 0,
          brokerage: 0,
          isShow: true,
          brokerageTwo: 0,
        };
        for (let i = 0; i < combination.length; i++) {
          const value = combination[i];
          this.$set(row, attr[i].attrName, value);
          this.$set(row, 'title', attr[i].attrName);
          this.$set(row, 'key', attr[i].attrName);
          this.$set(row.attrValueShow, attr[i].attrName, value);
          row.attrValue = row.attrValueShow;
          // 如果ManyAttrValue中存在该属性值，则赋值
          for (let k = 0; k < this.ManyAttrValue.length; k++) {
            const manyItem = this.ManyAttrValue[k];
            // 对比两个数组是否完全相等
            if (k > 0 && manyItem.attrArr.length && arraysEqual(manyItem.attrArr, combination)) {
              Object.assign(row, {
                // attrArr: manyItem.attrArr,
                // attrValue: manyItem.attrValue,
                price: manyItem.price || 0,
                cost: manyItem.cost || 0,
                otPrice: manyItem.otPrice || 0,
                stock: manyItem.stock || 0,
                image: manyItem.image || '',
                sku: manyItem.sku || '',
                weight: manyItem.weight || 0,
                isDefault: manyItem.isDefault || 0,
                volume: manyItem.volume || 0,
                barCode: manyItem.barCode || '',
                brokerage: manyItem.brokerage || 0,
                brokerageTwo: manyItem.brokerageTwo || 0,
                isShow: manyItem.isShow,
              });
            } else if (k > 0 && manyItem.attrArr.length && attr[i].isShowImage && combination.includes(val)) {
              // data[i].detail中的value是规格值 存在与 manyItem.attr_arr 中的某一项
              attr[i].optionList.map((e, ii) => {
                combination.includes(e.value) && this.$set(row, 'image', e.image);
              });
            }
          }
        }
        return row;
      });
      this.$nextTick(() => {
        // rows数组第一项 新增默认数据 oneFormBatch
        this.$emit('changeManyAttrValue', [...this.oneFormBatch, ...rows]);
      });
    },
    // 新增规格
    handleAddRole() {
      let data = {
        attrValues: this.formDynamic.attrsName,
        attrValue: [],
        isShowImage: 0,
        optionList: [],
      };
      //  let attr = []
      //  attr.push(data);
      // this.$set(this.formValidate, 'attrs',attr)

      this.$nextTick(() => {
        this.formValidate.attr.push(data);
        this.$emit('changeManyAttrValue', [...this.ManyAttrValue]);
        this.$emit('input', this.formValidate);
      });
    },
    // 另存为模板
    handleSaveAsTemplate() {
      this.$prompt('', '请输入模板名称', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: (value) => {
          if (value === null) {
            return '输入不能为空';
          }
          if (value.length > 30) return '输入限制30字以内';
        },
      })
        .then(({ value }) => {
          let spec = this.formValidate.attr.map((item) => {
            return {
              value: item.attrName,
              detail: item.optionList.map((e) => e.value),
            };
          });
          const data = {
            id: 0,
            ruleName: value,
            ruleValue: JSON.stringify(spec),
          };
          attrCreatApi(data)
            .then((res) => {
              this.$message.success('提交成功');
              this.productGetRule();
              this.clear();
              this.loading = false;
              this.loadingBtn = false;
            })
            .catch(() => {
              this.loading = false;
              this.loadingBtn = false;
            });
        })
        .catch(() => {});
    },
    // 规格拖拽排序后
    onMoveSpec(event, index = -1, type) {
      const oldIndex = event.oldIndex,
        newIndex = event.newIndex;
      // if (type == 'value') {
      //   const [movedItem] = this.formValidate.attr[index].attrValue.splice(oldIndex, 1);
      //   this.formValidate.attr[index].attrValue.splice(newIndex, 0, movedItem);
      // }
      this.generateAttr(this.formValidate.attr);
    },
    // 修改当前选中的规格索引
    changeCurrentIndex(i) {
      this.currentIndex = i;
    },
    // 聚焦后更新当前要修改的的规格值
    handleFocus(val) {
      this.changeAttrValue = val;
    },
    // 失去焦点后重置当前要修改的规格值
    handleBlur() {
      this.changeAttrValue = '';
    },
    // 规格图片添加开关
    addPic(e, i) {
      if (e) {
        this.formValidate.attr.map((item, ii) => {
          if (ii !== i) {
            this.$set(item, 'isShowImage', 0);
          }
        });
        this.canSel = false;
      } else {
        this.canSel = true;
      }
    },
    // 删除属性
    handleRemoveValue(item, index, val) {
      item.optionList.splice(index, 1);
      this.generateAttr(this.formValidate.attr);
    },
    // 删除表格中的属性
    delAttrTable(val) {
      for (let i = 0; i < this.ManyAttrValue.length; i++) {
        let item = this.ManyAttrValue[i];
        if (item.attrArr && item.attrArr.includes(val)) {
          this.ManyAttrValue.splice(i, 1);
          i--;
        }
      }
    },
    // 在规格中选择规格图片
    handleSelImg(item) {
      const _this = this;
      this.$modalUpload(
        function (img) {
          if (!img) return;
          item.image = img[0].sattDir;
          _this.changeSpecImg([item.value], img[0].sattDir);
        },
        '1',
        'content',
      );
    },
    // 修改规格图片
    changeSpecImg(arr, img) {
      // 判断是否存在规格图
      let isHas = false;
      for (let i = 1; i < this.ManyAttrValue.length; i++) {
        let item = this.ManyAttrValue[i];
        if (item.image && this.isSubset(item.attrArr, arr)) {
          isHas = true;
          break;
        }
      }
      if (isHas) {
        this.$confirm('可以同步修改下方该规格图片，确定要替换吗？', '提示', {
          confirmButtonText: '替换',
          cancelButtonText: '暂不',
          type: 'warning',
        })
          .then(() => {
            for (let val of this.ManyAttrValue) {
              if (this.isSubset(val.attrArr, arr)) {
                this.$set(val, 'image', img);
              }
            }
          })
          .catch(() => {});
      } else {
        for (let val of this.ManyAttrValue) {
          if (this.isSubset(val.attrArr, arr)) {
            this.$set(val, 'image', img);
          }
        }
      }
    },
    // 检查元素是否存在
    isSubset(arr1, arr2) {
      // 将数组转换为 Set，以便进行高效的包含检查
      const set1 = new Set(arr1);
      const set2 = new Set(arr2);

      // 检查 set2 中的每个元素是否都在 set1 中
      for (let elem of set2) {
        if (!set1.has(elem)) {
          return false;
        }
      }
      return true;
    },
    // 删除规格图片
    handleRemoveImg(item) {
      item.image = '';
    },
    // 点击添加规格值按钮聚焦输入框
    handleShowPop(index) {
      this.$refs['inputRef_' + index][0].focus();
    },
    // 添加属性
    createAttr(value, idx) {
      if (value) {
        // 判断是否存在同样数据
        var isExist = this.formValidate.attr[idx].optionList.some((item) => item.value === value);
        if (isExist) {
          this.$message.error('规格值已存在');
          return;
        }
        this.formValidate.attr[idx].optionList.push({
          value: value,
          image: '',
        });
        if (this.ManyAttrValue.length) {
          this.generateAttr(this.formValidate.attr, value);
          // this.addOneAttr(this.formValidate.attr[idx].attrName, value);
        } else {
          this.generateAttr(this.formValidate.attr);
        }
        this.$refs['popoverRef_' + idx][0].doClose(); //关闭的
        this.clearAttr();
        setTimeout(() => {
          if (this.$refs['popoverRef_' + idx]) {
            //重点是以下两句
            this.$refs['popoverRef_' + idx][0].doShow(); //打开的
            //重点是以上两句
          }
        }, 20);
      } else {
        this.$refs['popoverRef_' + idx][0].doClose(); //关闭的
      }
    },
    // 新增一条属性
    addOneAttr(val, val2) {
      this.generateAttr(this.formValidate.attr, val2);
    },
    clearAttr() {
      this.formDynamic.attrsName = '';
      this.formDynamic.attrsVal = '';
    },
    // 删除规格
    handleRemoveAttr(index) {
      this.formValidate.attr.splice(index, 1);
      if (!this.formValidate.attr.length) {
        this.formValidate.header = [];
        this.ManyAttrValue = [];
      } else {
        this.generateAttr(this.formValidate.attr);
      }
      this.canSel = true;
    },
    // 修改规格名称
    changeAttr(index, val) {
      if (val.trim().length && this.formValidate.attr[index].optionList.length) {
        this.generateHeader(this.formValidate.attr);
        if (this.ManyAttrValue.length) {
          this.formValidate.attr[index].attrName = val;
          this.generateAttr(this.formValidate.attr);
          // this.ManyAttrValue.map((item, i) => {
          //   if (i > 0) {
          //     if (Object.keys(item.attrValueShow).includes(this.changeAttrValue)) {
          //       item.attrValueShow[val] = item.attrValueShow[this.changeAttrValue];
          //       item[val] = item[this.changeAttrValue];
          //       delete item.attrValueShow[this.changeAttrValue];
          //       delete item[this.changeAttrValue];
          //     }
          //   }
          // });
          this.changeAttrValue = val;
        }
      } else {
        this.generateAttr(this.formValidate.attr);
      }
      // 触发父组件数据同步
      this.$emit('input', this.formValidate);
    },
    // 规格值改变
    changeValue(val, index, indexn) {
      if (this.ManyAttrValue.length) {
        let key = this.formValidate.attr[index].attrName;
        this.ManyAttrValue.map((item, i) => {
          if (i > 0) {
            if (Object.keys(item).includes(key) && item[key] === this.changeAttrValue) {
              item[key] = val;
              item.attrValue[key] = val;
              let idx = item.attrArr.findIndex((item) => item === this.changeAttrValue);
              item.attrArr[idx] = val;
            }
          }
        });
        this.changeAttrValue = val;
      } else {
        this.generateAttr(this.formValidate.attr, 1);
      }
      this.$nextTick(() => {
        this.$emit('changeManyAttrValue', [...this.ManyAttrValue]);
        this.$emit('input', this.formValidate);
      });
    },
    keyupEvent(key, val, index, num) {
      var re = /([0-9]+.[0-9]{2})[0-9]*/;
      switch (num) {
        case 1:
          this.oneFormBatch[index][key] =
            key === 'stock' ? parseInt(val) : this.$set(this.oneFormBatch[index], key, String(val).replace(re, '$1'));
          break;
        case 2:
          this.OneattrValue[index][key] =
            key === 'stock' ? parseInt(val) : this.$set(this.OneattrValue[index], key, String(val).replace(re, '$1'));
          break;
        default:
          this.ManyAttrValue[index][key] =
            key === 'stock' ? parseInt(val) : this.$set(this.ManyAttrValue[index], key, String(val).replace(re, '$1'));
          break;
      }
    },
    //返佣输入
    keyupEventBrokerage(val, index, num) {
      switch (num) {
        case 1:
          this.oneFormBatch[index][val] =
            this.oneFormBatch[index][val] > 0 ? parseInt(this.oneFormBatch[index][val]) : 0;
          break;
        case 2:
          this.OneattrValue[index][val] =
            this.OneattrValue[index][val] > 0 ? parseInt(this.OneattrValue[index][val]) : 0;
          break;
        default:
          this.ManyAttrValue[index][val] =
            this.ManyAttrValue[index][val] > 0 ? parseInt(this.ManyAttrValue[index][val]) : 0;
      }
    },
    // 批量添加
    batchAdd() {
      let arr = [];
      if (this.isDisabled) return;
      for (let val of this.formValidate.attr) {
        if (this.oneFormBatch[0][val.attrName]) {
          arr.push(this.oneFormBatch[0][val.attrName]);
        }
      }
      this.ManyAttrValue.forEach((val) => {
        if (arr.length && val.attrArr) {
          let attrVal = val.attrArr;
          if (this.isSubset(Object.values(attrVal), arr)) {
            this.batchData(val);
          }
        } else {
          this.batchData(val);
        }
      });
    },
    // 批量数据
    batchData(val) {
      if (this.oneFormBatch[0].image) this.$set(val, 'image', this.oneFormBatch[0].image);
      if (this.oneFormBatch[0].price > 0) this.$set(val, 'price', this.oneFormBatch[0].price);
      if (this.oneFormBatch[0].cost > 0) this.$set(val, 'cost', this.oneFormBatch[0].cost);
      if (this.oneFormBatch[0].otPrice > 0) this.$set(val, 'otPrice', this.oneFormBatch[0].otPrice);
      if (this.oneFormBatch[0].barCode) this.$set(val, 'barCode', this.oneFormBatch[0].barCode);
      if (this.oneFormBatch[0].stock >= 0) this.$set(val, 'stock', this.oneFormBatch[0].stock);
      if (this.oneFormBatch[0].weight >= 0) this.$set(val, 'weight', this.oneFormBatch[0].weight);
      if (this.oneFormBatch[0].volume >= 0) this.$set(val, 'volume', this.oneFormBatch[0].volume);
      if (this.oneFormBatch[0].brokerage > 0) this.$set(val, 'brokerage', this.oneFormBatch[0].brokerage);
      if (this.oneFormBatch[0].brokerageTwo > 0) this.$set(val, 'brokerageTwo', this.oneFormBatch[0].brokerageTwo);
    },
    // 清空批量规格信息
    batchDel() {
      this.$emit('handleBatchDel');
    },
    // 点击商品图
    modalPicTap(tit, num, i, status) {
      const _this = this;
      if (_this.isDisabled) return;
      this.$modalUpload(
        function (img) {
          if (tit === '1' && !num) {
            _this.formValidate.image = img[0].sattDir;
            _this.OneattrValue[0].image = img[0].sattDir;
          }
          if (tit === '2' && !num) {
            if (img.length > 10) return this.$message.warning('最多选择10张图片！');
            if (img.length + _this.formValidate.sliderImages.length > 10)
              return this.$message.warning('最多选择10张图片！');
            img.map((item) => {
              _this.formValidate.sliderImages.push(item.sattDir);
            });
          }
          if (tit === '3' && status === 'video') {
            let videoInfo = img[0];
            if (videoInfo.attType !== 'video/mp4') {
              this.$message.warning('请重新选择视频！');
            } else {
              _this.$set(_this.formValidate, 'videoLink', videoInfo.sattDir);
            }
          }
          if (tit === '1' && num === 'dan') {
            _this.OneattrValue[0].image = img[0].sattDir;
          }
          if (tit === '1' && num === 'duo') {
            _this.ManyAttrValue[i].image = img[0].sattDir;
          }
          if (tit === '1' && num === 'pi') {
            _this.oneFormBatch[0].image = img[0].sattDir;
          }
        },
        tit,
        'content',
      );
    },
    // 切换默认选中规格
    changeDefaultSelect(e, index) {
      const ManyAttrValues = [...this.ManyAttrValue]
      ManyAttrValues.map((item, i) => {
        if (i !== index) {
          item.isDefault = false;
        }
      });
      if (e) ManyAttrValues[index].isShow = true;
      this.$emit('changeManyAttrValue', [...ManyAttrValues]);
    },
    // 改变是否显示
    changeDefaultShow(index) {
      // 如果默认选中开启 则不可隐藏
      if (this.ManyAttrValue[index].isDefault === true) {
        this.ManyAttrValue[index].isShow = true;
        return this.$message.error('默认规格不可隐藏');
      }
      // 至少显示一个规格
      if (!this.ManyAttrValue.some((item) => item.isShow)) {
        this.ManyAttrValue[index].isShow = true;
        this.$message.error('至少显示一个规格');
      }
    },
  },
};
</script>

<style scoped lang="scss">
@keyframes blink {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

.flash {
  border-color: #fbcac3;
  background-color: #feedeb;
  color: var(--prev-color-prompt-color);
  animation: blink 1s infinite;
}
.customize-time {
  &:hover .el-icon-error {
    visibility: visible;
  }
  .el-icon-error {
    visibility: hidden;
    cursor: pointer;
    font-size: 15px;
    color: #999999;
    position: absolute;
    top: -5px;
    right: 6px;
  }
}

.add-time {
  color: var(--prev-color-primary);
  cursor: pointer;
}
.reservation-times-box {
  margin-top: 10px;
  padding: 10px 20px;
  min-width: 700px;
  background-color: #fafafa;
  border-radius: 10px;

  ::v-deep(.el-checkbox__label) {
    font-size: 12px;
  }
}
.el-dropdown-menu {
  border-color: #ebeef5;
  max-height: 650px !important;
  overflow-y: scroll !important;
}

.priceBox {
  width: 100%;
  ::v-deep.el-input__inner {
    text-align: center;
  }
}
// 多规格设置
.addfont {
  position: relative;
  display: inline-block;
  font-size: 12px;
  font-weight: 400;
  color: var(--prev-color-primary);
  margin-left: 14px;
  cursor: pointer;
}
.specifications {
  .specifications-item:hover {
    background-color: var(--prev-color-primary-light-9);
  }
  .specifications-item:hover .del {
    display: block;
  }
  .specifications-item:last-child {
    margin-bottom: 14px;
  }
  .specifications-item {
    position: relative;
    display: flex;
    align-items: center;
    padding: 20px 15px;
    transition: all 0.1s;
    background-color: #fafafa;
    margin-bottom: 10px;
    border-radius: 4px;

    .del {
      display: none;
      position: absolute;
      right: 15px;
      top: 15px;
      font-size: 22px;
      color: var(--prev-color-primary);
      cursor: pointer;
      z-index: 9;
    }
    .specifications-item-box {
      position: relative;
      .lineBox {
        position: absolute;
        left: 13px;
        top: 30px;
        width: 30px;
        height: 45px;
        border-radius: 6px;
        border-left: 1px solid #dcdfe6;
        border-bottom: 1px solid #dcdfe6;
      }
      .specifications-item-name {
        .el-icon-info {
          color: var(--prev-color-primary);
          font-size: 12px;
          margin-left: 5px;
        }
      }
      .specifications-item-name-input {
        width: 300px;
      }
    }
  }
}

.rulesBox {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  .item {
    display: flex;
    flex-wrap: wrap;
  }
  .addfont {
    margin-top: 5px;
    margin-left: 0px;
  }
  ::v-deep .el-popover {
    border: none;
    box-shadow: none;
    padding: 0;
    margin-top: 5px;
    line-height: 1.5;
  }
}
.spec {
  display: block;
  margin: 5px 0;
  position: relative;

  .el-icon-error {
    position: absolute;
    display: none;
    right: -3px;
    top: -3px;
    z-index: 9;
    color: var(--prev-color-primary);
  }
}
.img-popover {
  cursor: pointer;
  width: 76px;
  height: 76px;
  padding: 6px;
  margin-top: 12px;
  background-color: #fff;
  position: relative;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  &:hover .img-del {
    display: block;
  }
  .img-del {
    display: none;
    position: absolute;
    right: -4px;
    top: -5px;
    font-size: 16px;
    color: var(--prev-color-primary);
    cursor: pointer;
    z-index: 9;
  }
  .popper {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
  }
  .popper-arrow,
  .popper-arrow:after {
    position: absolute;
    display: block;
    width: 0;
    height: 0;
    border-color: transparent;
    border-style: solid;
  }
  .popper-arrow {
    top: -13px;
    border-top-width: 0;
    border-bottom-color: #dcdfe6;
    border-width: 6px;
    filter: drop-shadow(0 2px 12px rgba(0, 0, 0, 0.03));
    &::after {
      top: -5px;
      margin-left: -6px;
      border-top-width: 0;
      border-bottom-color: #fff;
      content: ' ';
      border-width: 6px;
    }
  }
}
.spec:hover {
  .el-icon-error {
    display: block;
    z-index: 999;
    cursor: pointer;
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
// 多规格设置
.specifications {
  .specifications-item:hover {
    background: var(--prev-color-primary-light-9) !important;
    & .empty-box {
      background: var(--prev-color-primary-light-9) !important;
    }
  }
  .specifications-item:hover .del {
    display: block;
  }
  .specifications-item:last-child {
    margin-bottom: 14px;
  }
}
.attr-tips {
  margin-top: 0;
  line-height: 32px;
}
.attr-move-icon,
.value-move-icon {
  cursor: move;
}
.w-240 {
  width: 240px!important;
}
.empty-box {
  width: 240px;
  height: 1px;
  background-color: #fafafa;
}
.attr-input {
  ::v-deep .el-input__inner {
    padding-right: 45px;
  }
}
</style>
