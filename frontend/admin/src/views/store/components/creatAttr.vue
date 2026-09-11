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
            <el-radio :label="true" :value="true" class="radio">单独设置</el-radio>
            <el-radio :label="false" :value="false">默认设置</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="规格类型：" props="specType">
          <el-radio-group
            v-model="formValidate.specType"
            @change="onChangeSpec(formValidate.specType)"
            :disabled="isDisabled"
          >
            <el-radio :label="false" :value="false" class="radio">单规格</el-radio>
            <el-radio :label="true" :value="true">多规格</el-radio>
          </el-radio-group>
          <el-dropdown
            :disabled="isDisabled"
            v-if="formValidate.specType && ruleList.length"
            class="ml20 dropdown-menu-box"
            trigger="hover"
            @command="confirmAttrTemp"
          >
            <span class="el-dropdown-link"> 选择规格模板<i class="el-icon-arrow-down el-icon--right"></i> </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="(item, index) in ruleList" :key="index" :command="item.ruleName">
                  {{ item.ruleName }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
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
              :item-key="getDraggableItemKey"
              handle=".attr-move-icon"
              @end="onMoveSpec($event, 'attr')"
              animation="300"
            >
              <template #item="{ element: item, index }">
                <div class="specifications-item active" @click="changeCurrentIndex(index)">
                  <div class="attr-move-icon">
                    <span class="iconfont icondrag2"></span>
                  </div>
                  <i class="del el-icon-error" @click="handleRemoveAttr(index)"></i>
                  <div class="specifications-item-box">
                    <div class="lineBox"></div>
                    <div class="specifications-item-name mb18">
                      <el-input

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
                        :true-value="1"
                        :false-value="0"
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
                        :item-key="getDraggableItemKey"
                        :disabled="item.optionList.length < 2"
                        handle=".value-move-icon"
                        @end="onMoveSpec($event, index, 'value')"
                      >
                        <template #item="{ element: det, index: indexn }">
                          <div class="mr10 spec">
                            <i class="el-icon-error" @click="handleRemoveValue(item, indexn, det.value)"></i>
                            <el-input
                              class="attr-input w-240"

                              v-model="det.value"
                              placeholder="规格值"
                              @input="changeValue(det.value, index, indexn)"
                              @focus="handleFocus(det.value)"
                              maxlength="30"
                              show-word-limit
                              @blur="handleBlur()"
                            >
                              <template #prefix>
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
                        </template>
                        <template #footer>
                          <div>
                            <el-popover
                              :ref="
                                (el) => {
                                  if (el) popoverRefs[index] = el
                                }
                              "
                              placement="bottom"
                              width="240"
                              trigger="click"
                              @after-enter="handleShowPop(index)"
                            >
                              <el-input
                                :ref="
                                  (el) => {
                                    if (el) inputRefs[index] = el
                                  }
                                "

                                class="attr-input w-240"
                                placeholder="请输入规格值"
                                v-model="formDynamic.attrsVal"
                                @keyup.enter="createAttr(formDynamic.attrsVal, index)"
                                @blur="createAttr(formDynamic.attrsVal, index)"
                                maxlength="30"
                                show-word-limit
                              >
                              </el-input>
                              <template #reference>
                                <div class="addfont">
                                  <span>添加规格值</span>
                                </div>
                                <div class="empty-box"></div>
                              </template>
                            </el-popover>
                          </div>
                        </template>
                      </draggable>
                    </div>
                  </div>
                </div>
              </template>
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
          <el-table :data="OneattrValue" border class="tabNumWidth">
            <el-table-column label="图片" min-width="60">
              <template #default="scope">
                <div class="upLoadPicBox" @click="modalPicTap('1', 'dan')">
                  <div v-if="formValidate.image" class="pictrue tabPic">
                    <el-image :preview-src-list="isDisabled ? [scope.row.image] : []" preview-teleported :src="OneattrValue[0].image" />
                  </div>
                  <div v-else class="upLoad tabPic">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </template>
            </el-table-column>
            <template v-if="formValidate.isSub">
              <el-table-column label="一级返佣" min-width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.brokerage" :disabled="isDisabled" class="priceBox"></el-input>
                </template>
              </el-table-column>
              <el-table-column label="二级返佣" min-width="100">
                <template #default="scope">
                  <el-input v-model="scope.row.brokerageTwo" :disabled="isDisabled" class="priceBox"></el-input>
                </template>
              </el-table-column>
            </template>
            <el-table-column
              v-for="(item, iii) in oneAttrTableTitle"
              :key="iii"
              :label="item.title"
              :min-width="item.minWidth || '200'"
            >
              <template #default="scope" v-if="item.slot != 'isDefault' && item.slot != 'action'">
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
                <template #default="scope">
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
                            :preview-src-list="isDisabled ? [scope.row.image] : []" preview-teleported
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
                      <el-input
                        v-model="oneFormBatch[0].barCode"
                        maxlength="40"
                        class="priceBox"
                        :disabled="isDisabled"
                      ></el-input>
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
                      <a type="text" @click="batchAdd">批量修改</a>
                      <a type="text" @click="batchDel" class="ml10">清空</a>
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
                            :preview-src-list="isDisabled ? [scope.row.image] : []" preview-teleported
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

<script setup lang="jsx">
import { ref, reactive, computed, watch, onMounted, nextTick, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback'
import { templateListApi, attrCreatApi } from '@/api/store'
import { GoodsTableHead, imageTableHead, commissionTableHead } from '../creatStore/TableHeadList'
import { defaultObj } from '../creatStore/default'
import { arraysEqual } from '@/utils'
import { getDraggableItemKey } from '@/utils/draggableKey'
import vuedraggable from 'vuedraggable'
const draggable = vuedraggable // 模板中使用 <draggable>

defineOptions({ name: 'creatAttr' })

const props = defineProps({
  // 商品数据
  modelValue: {
    type: Object,
    default: function () {
      return {}
    }
  },
  // 单规格
  OneattrValue: {
    type: Array,
    default: function () {
      return []
    }
  },
  // 多规格
  ManyAttrValue: {
    type: Array,
    default: function () {
      return []
    }
  },
  //批量添加规格
  oneFormBatch: {
    type: Array,
    default: function () {
      return []
    }
  },
  isDisabled: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['update:modelValue', 'changeManyAttrValue', 'handleBatchDel'])

const { proxy } = getCurrentInstance()

const tableKey = ref(0)
// formValidate 直接引用父组件传入的 modelValue 对象（保留原 this.value 行为）
const formValidate = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})
const isAttr = ref(false) // 是否需要显示sku
const ruleList = ref([]) // 商品属性模板列表
const currentIndex = ref(0) // 当前规格索引
const changeAttrValue = ref('') // 当前要修改的规格值
const canSel = ref(true) // 规格图片添加判断
// 规格数据
const formDynamic = reactive({
  attrsName: '',
  attrsVal: ''
})
const attrs = ref([]) // 规格列表
// 动态 ref 映射（原 this.$refs['popoverRef_' + index] / ['inputRef_' + index]）
const popoverRefs = reactive({})
const inputRefs = reactive({})
// 原方法中引用但未在 data 中声明（保留原逻辑）
const tableFrom = reactive({ page: 1, limit: 9999 })
const loading = ref(false)
const loadingBtn = ref(false)

// 单规格表头
const oneAttrTableTitle = computed(() => {
  const arr = GoodsTableHead.slice(0, -2)
  return arr
})
const attrValue = computed(() => {
  const obj = Object.assign({}, defaultObj.attrValue[0])
  delete obj.image
  return obj
})

// 合并单元格
function objectSpanMethod({ row, column, rowIndex, columnIndex }) {
  if (columnIndex === 0 && rowIndex > 0) {
    let lable = column.label
    //这里判断第几列需要合并
    const tagFamily = props.ManyAttrValue[rowIndex].attrValue[lable]
    const index = props.ManyAttrValue.findIndex((item, index) => {
      if (index > 0) return item.attrValue[lable] == tagFamily
    })
    if (rowIndex == index) {
      let len = 1
      for (let i = index + 1; i < props.ManyAttrValue.length; i++) {
        if (props.ManyAttrValue[i].attrValue[lable] !== tagFamily) {
          break
        }
        len++
      }
      return {
        rowspan: len,
        colspan: 1
      }
    } else {
      return {
        rowspan: 0,
        colspan: 0
      }
    }
  }
}
// 生成列表 行 列 数据
function tableCellClassName({ row, column, rowIndex, columnIndex }) {
  //注意这里是解构
  //利用单元格的 className 的回调方法，给行列索引赋值
  row.index = rowIndex || ''
  column.index = columnIndex
}
// 获取商品属性模板
function productGetRule() {
  templateListApi(tableFrom).then((res) => {
    const list = res.list
    for (var i = 0; i < list.length; i++) {
      list[i].ruleValue = JSON.parse(list[i].ruleValue)
    }
    ruleList.value = list
  })
}
// 修改佣金是否为默认设置
function onChangetype(item) {
  if (item === 1) {
    props.OneattrValue.map((item) => {
      item.brokerage = null
      item.brokerageTwo = null
    })
    props.ManyAttrValue.map((item) => {
      item.brokerage = null
      item.brokerageTwo = null
    })
  } else {
    props.OneattrValue.map((item) => {
      // delete item.brokerage;
      // delete item.brokerageTwo;
      item.brokerage = 0
      item.brokerageTwo = 0
    })
    props.ManyAttrValue.map((item) => {
      // delete item.brokerage;
      // delete item.brokerageTwo;
      item.brokerage = 0
      item.brokerageTwo = 0
    })
  }
  generateHeader(formValidate.value.attr)
}
// 修改规格类型
function onChangeSpec(num) {
  isAttr.value = true
  if (num) productGetRule()
}
// 选择规格模板
function confirmAttrTemp(name) {
  canSel.value = true
  formValidate.value.selectRule = name
  if (!formValidate.value.selectRule) {
    return ElMessage.warning('请选择属性')
  }
  const rule = ruleList.value.find((item) => item.ruleName === formValidate.value.selectRule)
  if (!rule) {
    return ElMessage.warning('规格模板不存在')
  }
  const dataAttrs = (rule.ruleValue || []).map((item, index) => {
    const detail = Array.isArray(item.detail) ? item.detail : []
    return {
      attrName: item.value,
      optionList: detail.map((val, sort) => ({ value: val, image: '', sort })),
      id: 0,
      isShowImage: 0,
      sort: index + 1
    }
  })
  formValidate.value.attr = dataAttrs
  generateAttr(formValidate.value.attr)
}
// 添加 optionList 字段
function createOptionList(attr) {
  if (!attr.length) return
  // 如果 optionList 字段无内容
  if (!Array.isArray(attr[0].optionList) || attr[0].optionList.length == 0) {
    // 添加 optionList 内容
    attr.forEach((item) => {
      const attrValueList = typeof item.attrValues === 'string' ? item.attrValues.split(',') : []
      item.optionList = attrValueList.map((val) => {
        return {
          value: val,
          image: ''
        }
      })
    })
  }
}
// 生成商品规格表头
function generateHeader(attr) {
  let specificationsColumns = attr.map((item) => ({
    title: item.attrName,
    key: item.attrName,
    minWidth: 200,
    fixed: 'left'
  }))
  let arr
  // 开启佣金设置
  if (formValidate.value.isSub) {
    arr = [...specificationsColumns, ...imageTableHead, ...commissionTableHead, ...GoodsTableHead]
  } else {
    arr = [...specificationsColumns, ...imageTableHead, ...GoodsTableHead]
  }
  formValidate.value.header = arr
  tableKey.value += 1
}
// 生成规格组合
function generateCombinations(attr, prefix = []) {
  if (attr.length === 0) {
    return [prefix]
  }
  const [first, ...rest] = attr
  return first.optionList.flatMap((detail) => generateCombinations(rest, [...prefix, detail.value]))
}
// 生成规格属性数据
function generateAttr(attr, val) {
  // 生成商品规格表头
  generateHeader(attr)
  // 生成规格值组合
  const combinations = generateCombinations(attr)
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
      brokerageTwo: 0
    }
    for (let i = 0; i < combination.length; i++) {
      const value = combination[i]
      row[attr[i].attrName] = value
      row.title = attr[i].attrName
      row.key = attr[i].attrName
      row.attrValueShow[attr[i].attrName] = value
      row.attrValue = row.attrValueShow
      // 如果ManyAttrValue中存在该属性值，则赋值
      for (let k = 0; k < props.ManyAttrValue.length; k++) {
        const manyItem = props.ManyAttrValue[k]
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
            isShow: manyItem.isShow
          })
        } else if (k > 0 && manyItem.attrArr.length && attr[i].isShowImage && combination.includes(val)) {
          // data[i].detail中的value是规格值 存在与 manyItem.attr_arr 中的某一项
          attr[i].optionList.map((e, ii) => {
            combination.includes(e.value) && (row.image = e.image)
          })
        }
      }
    }
    return row
  })
  nextTick(() => {
    // rows数组第一项 新增默认数据 oneFormBatch
    emit('changeManyAttrValue', [...props.oneFormBatch, ...rows])
  })
}
// 新增规格
function handleAddRole() {
  let data = {
    attrValues: formDynamic.attrsName,
    attrValue: [],
    isShowImage: 0,
    optionList: []
  }
  //  let attr = []
  //  attr.push(data);
  // this.$set(this.formValidate, 'attrs',attr)

  nextTick(() => {
    formValidate.value.attr.push(data)
    emit('changeManyAttrValue', [...props.ManyAttrValue])
    emit('update:modelValue', formValidate.value)
  })
}
// 另存为模板
function handleSaveAsTemplate() {
  ElMessageBox.prompt('', '请输入模板名称', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValidator: (value) => {
      if (value === null) {
        return '输入不能为空'
      }
      if (value.length > 30) return '输入限制30字以内'
    }
  })
    .then(({ value }) => {
      let spec = formValidate.value.attr.map((item) => {
        return {
          value: item.attrName,
          detail: item.optionList.map((e) => e.value)
        }
      })
      const data = {
        id: 0,
        ruleName: value,
        ruleValue: JSON.stringify(spec)
      }
      attrCreatApi(data)
        .then((res) => {
          ElMessage.success('提交成功')
          productGetRule()
          clear()
          loading.value = false
          loadingBtn.value = false
        })
        .catch(() => {
          loading.value = false
          loadingBtn.value = false
        })
    })
    .catch(() => {})
}
// 规格拖拽排序后
function onMoveSpec(event, index = -1, type) {
  const oldIndex = event.oldIndex,
    newIndex = event.newIndex
  // if (type == 'value') {
  //   const [movedItem] = this.formValidate.attr[index].attrValue.splice(oldIndex, 1);
  //   this.formValidate.attr[index].attrValue.splice(newIndex, 0, movedItem);
  // }
  generateAttr(formValidate.value.attr)
}
// 修改当前选中的规格索引
function changeCurrentIndex(i) {
  currentIndex.value = i
}
// 聚焦后更新当前要修改的的规格值
function handleFocus(val) {
  changeAttrValue.value = val
}
// 失去焦点后重置当前要修改的规格值
function handleBlur() {
  changeAttrValue.value = ''
}
// 规格图片添加开关
function addPic(e, i) {
  if (e) {
    formValidate.value.attr.map((item, ii) => {
      if (ii !== i) {
        item.isShowImage = 0
      }
    })
    canSel.value = false
  } else {
    canSel.value = true
  }
}
// 删除属性
function handleRemoveValue(item, index, val) {
  item.optionList.splice(index, 1)
  generateAttr(formValidate.value.attr)
}
// 删除表格中的属性
function delAttrTable(val) {
  for (let i = 0; i < props.ManyAttrValue.length; i++) {
    let item = props.ManyAttrValue[i]
    if (item.attrArr && item.attrArr.includes(val)) {
      props.ManyAttrValue.splice(i, 1)
      i--
    }
  }
}
// 在规格中选择规格图片
function handleSelImg(item) {
  proxy.$modalUpload(
    function (img) {
      if (!img) return
      item.image = img[0].sattDir
      changeSpecImg([item.value], img[0].sattDir)
    },
    '1',
    'content'
  )
}
// 修改规格图片
function changeSpecImg(arr, img) {
  // 判断是否存在规格图
  let isHas = false
  for (let i = 1; i < props.ManyAttrValue.length; i++) {
    let item = props.ManyAttrValue[i]
    if (item.image && isSubset(item.attrArr, arr)) {
      isHas = true
      break
    }
  }
  if (isHas) {
    ElMessageBox.confirm('可以同步修改下方该规格图片，确定要替换吗？', '提示', {
      confirmButtonText: '替换',
      cancelButtonText: '暂不',
      type: 'warning'
    })
      .then(() => {
        for (let val of props.ManyAttrValue) {
          if (isSubset(val.attrArr, arr)) {
            val.image = img
          }
        }
      })
      .catch(() => {})
  } else {
    for (let val of props.ManyAttrValue) {
      if (isSubset(val.attrArr, arr)) {
        val.image = img
      }
    }
  }
}
// 检查元素是否存在
function isSubset(arr1, arr2) {
  // 将数组转换为 Set，以便进行高效的包含检查
  const set1 = new Set(arr1)
  const set2 = new Set(arr2)

  // 检查 set2 中的每个元素是否都在 set1 中
  for (let elem of set2) {
    if (!set1.has(elem)) {
      return false
    }
  }
  return true
}
// 删除规格图片
function handleRemoveImg(item) {
  item.image = ''
}
// 点击添加规格值按钮聚焦输入框
function handleShowPop(index) {
  inputRefs[index]?.focus?.()
}
// 添加属性
function createAttr(value, idx) {
  if (value) {
    // 判断是否存在同样数据
    var isExist = formValidate.value.attr[idx].optionList.some((item) => item.value === value)
    if (isExist) {
      ElMessage.error('规格值已存在')
      return
    }
    formValidate.value.attr[idx].optionList.push({
      value: value,
      image: ''
    })
    if (props.ManyAttrValue.length) {
      generateAttr(formValidate.value.attr, value)
      // this.addOneAttr(this.formValidate.attr[idx].attrName, value);
    } else {
      generateAttr(formValidate.value.attr)
    }
    popoverRefs[idx]?.doClose?.() //关闭的
    clearAttr()
    setTimeout(() => {
      if (popoverRefs[idx]) {
        //重点是以下两句
        popoverRefs[idx]?.doShow?.() //打开的
        //重点是以上两句
      }
    }, 20)
  } else {
    popoverRefs[idx]?.doClose?.() //关闭的
  }
}
// 新增一条属性
function addOneAttr(val, val2) {
  generateAttr(formValidate.value.attr, val2)
}
function clearAttr() {
  formDynamic.attrsName = ''
  formDynamic.attrsVal = ''
}
// 删除规格
function handleRemoveAttr(index) {
  formValidate.value.attr.splice(index, 1)
  if (!formValidate.value.attr.length) {
    formValidate.value.header = []
    props.ManyAttrValue.length = 0
  } else {
    generateAttr(formValidate.value.attr)
  }
  canSel.value = true
}
// 修改规格名称
function changeAttr(index, val) {
  if (val.trim().length && formValidate.value.attr[index].optionList.length) {
    generateHeader(formValidate.value.attr)
    if (props.ManyAttrValue.length) {
      formValidate.value.attr[index].attrName = val
      generateAttr(formValidate.value.attr)
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
      changeAttrValue.value = val
    }
  } else {
    generateAttr(formValidate.value.attr)
  }
  // 触发父组件数据同步
  emit('update:modelValue', formValidate.value)
}
// 规格值改变
function changeValue(val, index, indexn) {
  if (props.ManyAttrValue.length) {
    let key = formValidate.value.attr[index].attrName
    props.ManyAttrValue.map((item, i) => {
      if (i > 0) {
        if (Object.keys(item).includes(key) && item[key] === changeAttrValue.value) {
          item[key] = val
          item.attrValue[key] = val
          let idx = item.attrArr.findIndex((item) => item === changeAttrValue.value)
          item.attrArr[idx] = val
        }
      }
    })
    changeAttrValue.value = val
  } else {
    generateAttr(formValidate.value.attr, 1)
  }
  nextTick(() => {
    emit('changeManyAttrValue', [...props.ManyAttrValue])
    emit('update:modelValue', formValidate.value)
  })
}
function keyupEvent(key, val, index, num) {
  var re = /([0-9]+.[0-9]{2})[0-9]*/
  switch (num) {
    case 1:
      props.oneFormBatch[index][key] =
        key === 'stock' ? parseInt(val) : (props.oneFormBatch[index][key] = String(val).replace(re, '$1'))
      break
    case 2:
      props.OneattrValue[index][key] =
        key === 'stock' ? parseInt(val) : (props.OneattrValue[index][key] = String(val).replace(re, '$1'))
      break
    default:
      props.ManyAttrValue[index][key] =
        key === 'stock' ? parseInt(val) : (props.ManyAttrValue[index][key] = String(val).replace(re, '$1'))
      break
  }
}
//返佣输入
function keyupEventBrokerage(val, index, num) {
  switch (num) {
    case 1:
      props.oneFormBatch[index][val] = props.oneFormBatch[index][val] > 0 ? parseInt(props.oneFormBatch[index][val]) : 0
      break
    case 2:
      props.OneattrValue[index][val] = props.OneattrValue[index][val] > 0 ? parseInt(props.OneattrValue[index][val]) : 0
      break
    default:
      props.ManyAttrValue[index][val] =
        props.ManyAttrValue[index][val] > 0 ? parseInt(props.ManyAttrValue[index][val]) : 0
  }
}
// 批量添加
function batchAdd() {
  let arr = []
  if (props.isDisabled) return
  for (let val of formValidate.value.attr) {
    if (props.oneFormBatch[0][val.attrName]) {
      arr.push(props.oneFormBatch[0][val.attrName])
    }
  }
  props.ManyAttrValue.forEach((val) => {
    if (arr.length && val.attrArr) {
      let attrVal = val.attrArr
      if (isSubset(Object.values(attrVal), arr)) {
        batchData(val)
      }
    } else {
      batchData(val)
    }
  })
}
// 批量数据
function batchData(val) {
  if (props.oneFormBatch[0].image) val.image = props.oneFormBatch[0].image
  if (props.oneFormBatch[0].price > 0) val.price = props.oneFormBatch[0].price
  if (props.oneFormBatch[0].cost > 0) val.cost = props.oneFormBatch[0].cost
  if (props.oneFormBatch[0].otPrice > 0) val.otPrice = props.oneFormBatch[0].otPrice
  if (props.oneFormBatch[0].barCode) val.barCode = props.oneFormBatch[0].barCode
  if (props.oneFormBatch[0].stock >= 0) val.stock = props.oneFormBatch[0].stock
  if (props.oneFormBatch[0].weight >= 0) val.weight = props.oneFormBatch[0].weight
  if (props.oneFormBatch[0].volume >= 0) val.volume = props.oneFormBatch[0].volume
  if (props.oneFormBatch[0].brokerage > 0) val.brokerage = props.oneFormBatch[0].brokerage
  if (props.oneFormBatch[0].brokerageTwo > 0) val.brokerageTwo = props.oneFormBatch[0].brokerageTwo
}
// 清空批量规格信息
function batchDel() {
  emit('handleBatchDel')
}
// 点击商品图
function modalPicTap(tit, num, i, status) {
  if (props.isDisabled) return
  proxy.$modalUpload(
    function (img) {
      if (tit === '1' && !num) {
        formValidate.value.image = img[0].sattDir
        props.OneattrValue[0].image = img[0].sattDir
      }
      if (tit === '2' && !num) {
        if (img.length > 10) return ElMessage.warning('最多选择10张图片！')
        if (img.length + formValidate.value.sliderImages.length > 10) return ElMessage.warning('最多选择10张图片！')
        img.map((item) => {
          formValidate.value.sliderImages.push(item.sattDir)
        })
      }
      if (tit === '3' && status === 'video') {
        let videoInfo = img[0]
        if (videoInfo.attType !== 'video/mp4') {
          ElMessage.warning('请重新选择视频！')
        } else {
          formValidate.value.videoLink = videoInfo.sattDir
        }
      }
      if (tit === '1' && num === 'dan') {
        props.OneattrValue[0].image = img[0].sattDir
      }
      if (tit === '1' && num === 'duo') {
        props.ManyAttrValue[i].image = img[0].sattDir
      }
      if (tit === '1' && num === 'pi') {
        props.oneFormBatch[0].image = img[0].sattDir
      }
    },
    tit,
    'content'
  )
}
// 切换默认选中规格
function changeDefaultSelect(e, index) {
  const ManyAttrValues = [...props.ManyAttrValue]
  ManyAttrValues.map((item, i) => {
    if (i !== index) {
      item.isDefault = false
    }
  })
  if (e) ManyAttrValues[index].isShow = true
  emit('changeManyAttrValue', [...ManyAttrValues])
}
// 改变是否显示
function changeDefaultShow(index) {
  // 如果默认选中开启 则不可隐藏
  if (props.ManyAttrValue[index].isDefault === true) {
    props.ManyAttrValue[index].isShow = true
    return ElMessage.error('默认规格不可隐藏')
  }
  // 至少显示一个规格
  if (!props.ManyAttrValue.some((item) => item.isShow)) {
    props.ManyAttrValue[index].isShow = true
    ElMessage.error('至少显示一个规格')
  }
}

// 原 watch formValidate：仅同步赋值（保留）
watch(
  () => props.modelValue,
  (newVal) => {
    // this.formValidate = newVal; (computed 已处理)
  }
)

onMounted(() => {
  if (formValidate.value.specType) {
    productGetRule() //加载商品规格选项
    // 添加 optionList 字段
    createOptionList(formValidate.value.attr)
    // 生成规格属性数据
    generateAttr(formValidate.value.attr)
  }
})
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

  :deep(.el-checkbox__label) {
    font-size: 12px;
  }
}
.el-dropdown-menu {
  border-color: #ebeef5;
  max-height: 650px;
  overflow-y: auto;
}
.dropdown-menu-box {
  height: 34px;
  align-items: center;
}

.priceBox {
  width: 100%;
  :deep(.el-input__inner) {
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
  :deep(.el-popover) {
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
  width: 240px !important;
}
.empty-box {
  width: 240px;
  height: 1px;
  background-color: #fafafa;
}
.attr-input {
  :deep(.el-input__inner) {
    padding-right: 45px;
  }
}
:deep(.el-form-item__content)  {
  display: block;
}
</style>
