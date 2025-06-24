<template>
  <div class="divBox">
    <pages-header
      ref="pageHeader"
      :title="$route.params.id ? (isDisabled ? '商品详情' : '编辑商品') : '添加商品'"
      backUrl="/store/index"
    ></pages-header>
    <el-card v-if="$route.params.isCopy" class="mt14" shadow="never" :bordered="false">
      <div class="line-ht mb15">
        生成的商品默认是没有上架的，请手动上架商品！
        <span v-if="copyConfig.copyType && copyConfig.copyType == 1"
          >您当前剩余{{ copyConfig.copyNum }}条采集次数。
        </span>
        <div class="tips-bottom">
          商品采集设置：设置 > 系统设置 > 第三方接口设置 >
          采集商品配置（如配置一号通采集，请先登录一号通账号，无一号通，请选择99Api设置）
        </div>
      </div>
      <div :span="24" v-if="copyConfig.copyType">
        <el-input v-model.trim="url" placeholder="请输入链接地址" class="selWidth100" size="small">
          <el-button slot="append" icon="el-icon-search" @click="addProduct" size="small" />
        </el-input>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-tabs class="list-tabs" v-model="currentTab" @tab-click="tabsHandleClick">
        <el-tab-pane label="商品信息" name="0"></el-tab-pane>
        <el-tab-pane label="规格库存" name="1"></el-tab-pane>
        <el-tab-pane label="商品详情" name="2"></el-tab-pane>
        <el-tab-pane label="其他设置" name="3"></el-tab-pane>
      </el-tabs>
      <el-form
        ref="formValidate"
        v-loading="fullscreenLoading"
        class="formValidate mt20"
        :rules="ruleValidate"
        :model="formValidate"
        label-width="90px"
        @submit.native.prevent
      >
        <el-row v-show="currentTab == 0" :gutter="24">
          <!-- 商品信息-->
          <el-col v-bind="grid2">
            <el-form-item label="商品名称：" prop="storeName">
              <el-input
                class="from-ipt-width"
                v-model="formValidate.storeName"
                maxlength="249"
                placeholder="请输入商品名称"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品分类：" prop="cateIds">
              <el-cascader
                class="from-ipt-width"
                v-model="formValidate.cateIds"
                :options="merCateList"
                :props="props2"
                clearable
                :show-all-levels="false"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="商品关键字：" prop="keyword">
              <el-input
                class="from-ipt-width"
                v-model="formValidate.keyword"
                placeholder="请输入商品关键字"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col v-bind="grid2">
            <el-form-item label="单位：" prop="unitName">
              <el-input
                class="from-ipt-width"
                v-model="formValidate.unitName"
                placeholder="请输入单位"
                :disabled="isDisabled"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品封面图：" prop="image">
              <div class="acea-row upLoadPicBox row-middle">
                <div v-if="formValidate.image" class="pictrue" @click="modalPicTap('1')">
                  <el-image
                    class="image"
                    :src="formValidate.image"
                    :preview-src-list="isDisabled ? [formValidate.image] : []"
                  >
                  </el-image>
                </div>
                <div v-else class="upLoad" @click="modalPicTap('1')">
                  <i class="el-icon-camera cameraIconfont" />
                </div>
              </div>
              <div class="from-tips" v-show="!isDisabled">建议尺寸：800*800px，上传小于500kb的图片</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品轮播图：" prop="sliderImages">
              <div class="acea-row">
                <div
                  v-for="(item, index) in formValidate.sliderImages"
                  :key="index"
                  class="pictrue"
                  draggable="true"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnter($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >
                  <el-image class="image" :src="item" :preview-src-list="formValidate.sliderImages"> </el-image>
                  <i v-if="!isDisabled" class="el-icon-error btndel" @click="handleRemove(index)" />
                </div>
                <div
                  v-if="formValidate.sliderImages.length < 10 && !isDisabled"
                  class="upLoadPicBox"
                  @click="modalPicTap('2')"
                >
                  <div class="upLoad">
                    <i class="el-icon-camera cameraIconfont" />
                  </div>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col>
            <el-form-item label="主图视频：">
              <el-input
                class="from-ipt-width mr15"
                maxlength="250"
                v-model="videoLink"
                :disabled="isDisabled"
                placeholder="请输入视频链接"
              />
              <el-button v-if="videoLink" @click="zh_uploadFile" :disabled="isDisabled">确认添加</el-button>
              <el-button v-if="!videoLink" :disabled="isDisabled" @click="modalPicTap('3', '', '', 'video')"
                >选择视频</el-button
              >
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <div class="iview-video-style" v-if="formValidate.videoLink">
              <video
                style="width: 100%; height: 100% !important; border-radius: 10px"
                :src="formValidate.videoLink"
                controls
                autoplay
                muted
              >
                您的浏览器不支持 video 标签。
              </video>
              <div class="mark"></div>
              <span class="iconv iconfont iconmd-trash" @click="delVideo()"></span>
            </div>
          </el-col>
          <el-col>
            <el-form-item label="运费模板：" prop="tempId">
              <el-select
                class="from-ipt-width mr15"
                v-model="formValidate.tempId"
                placeholder="请选择"
                :disabled="isDisabled"
                style="width: 100%"
              >
                <el-option v-for="item in shippingList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
              <el-button v-show="!isDisabled" class="mr15" @click="addTem">运费模板</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-show="currentTab == 1">
          <el-col :span="24">
            <el-form-item label="商品规格：" props="specType">
              <el-radio-group
                v-model="formValidate.specType"
                @change="onChangeSpec(formValidate.specType)"
                :disabled="isDisabled"
              >
                <el-radio :label="false" class="radio">单规格</el-radio>
                <el-radio :label="true">多规格</el-radio>
              </el-radio-group>
            </el-form-item>
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
          </el-col>
          <!-- 多规格添加-->
          <el-col v-if="formValidate.specType && !isDisabled" :span="24" class="noForm">
            <el-form-item label="选择规格：" prop="">
              <div class="acea-row">
                <el-select v-model="formValidate.selectRule" @change="confirm">
                  <el-option v-for="item in ruleList" :key="item.id" :label="item.ruleName" :value="item.id" />
                </el-select>
                <el-button class="ml10" @click="addRule">添加规格</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <div v-for="(item, index) in formValidate.attr" :key="index">
                <div class="acea-row row-middle">
                  <span class="mr5">{{ item.attrName }}</span
                  ><i class="el-icon-circle-close" @click="handleRemoveAttr(index)" />
                </div>
                <div class="rulesBox">
                  <el-tag
                    v-for="(j, indexn) in item.attrValue"
                    :key="indexn"
                    closable
                    size="medium"
                    :disable-transitions="false"
                    class="mb5 mr10"
                    @close="handleClose(item.attrValue, indexn, index)"
                  >
                    {{ j }}
                  </el-tag>
                  <template v-if="!$route.params.isCopy">
                    <el-input
                      v-if="item.inputVisible"
                      ref="saveTagInput"
                      v-model="item.attrValue.attrsVal"
                      class="input-new-tag"
                      size="small"
                      @keyup.enter.native="createAttr(item.attrValue.attrsVal, index)"
                      @blur="createAttr(item.attrValue.attrsVal, index)"
                    />
                    <el-button v-else class="button-new-tag" size="small" @click="showInput(item)">+ 添加</el-button>
                  </template>
                </div>
              </div>
            </el-form-item>
            <el-col v-if="isBtn">
              <div class="acea-row">
                <el-form-item label="规格：" class="inputWid">
                  <el-input v-model="formDynamic.attrsName" placeholder="请输入规格" />
                </el-form-item>
                <el-form-item label="规格值：" class="inputWid">
                  <el-input v-model="formDynamic.attrsVal" placeholder="请输入规格值" />
                </el-form-item>
                <el-form-item class="noLeft">
                  <el-button type="primary" @click="createAttrName">确定</el-button>
                  <el-button @click="offAttrName">取消</el-button>
                </el-form-item>
              </div>
            </el-col>
            <el-form-item v-if="!isBtn && !$route.params.isCopy">
              <el-button type="primary" icon="md-add" class="mr15" @click="addBtn">添加新规格</el-button>
            </el-form-item>
          </el-col>
          <!-- 批量设置-->
          <el-col v-if="formValidate.attr.length > 0 && formValidate.specType && !isDisabled" :span="24" class="noForm">
            <el-form-item label="批量设置：">
              <el-table :data="oneFormBatch" border class="tabNumWidth" size="mini">
                <el-table-column label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1', 'pi')">
                      <div v-if="scope.row.image" class="pictrue tabPic">
                        <el-image :preview-src-list="isDisabled ? [scope.row.image] : []" :src="scope.row.image" />
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
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row[iii]"
                      :type="formThead[iii].title === '商品编号' ? 'text' : 'number'"
                      :min="0"
                      class="priceBox"
                      @keyup.native="keyupEvent(iii, scope.row[iii], scope.$index, 1)"
                    />
                  </template>
                </el-table-column>
                <template v-if="formValidate.isSub">
                  <el-table-column label="一级返佣(元)" min-width="120">
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.brokerage"
                        type="number"
                        :min="0"
                        :max="scope.row.price"
                        class="priceBox"
                      />
                    </template>
                  </el-table-column>
                  <el-table-column label="二级返佣(元)" min-width="120">
                    <template slot-scope="scope">
                      <el-input
                        v-model="scope.row.brokerageTwo"
                        type="number"
                        :min="0"
                        :max="scope.row.price"
                        class="priceBox"
                      />
                    </template>
                  </el-table-column>
                </template>
                <el-table-column label="操作" width="80">
                  <template>
                    <a class="submission" @click="batchAdd">批量添加</a>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
          <el-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
            <!-- 单规格表格-->
            <el-form-item v-if="formValidate.specType === false">
              <el-table :data="OneattrValue" border class="tabNumWidth" size="mini">
                <el-table-column label="图片" min-width="80">
                  <template slot-scope="scope">
                    <div class="upLoadPicBox" @click="modalPicTap('1', 'dan', 'pi')">
                      <div v-if="formValidate.image" class="pictrue tabPic">
                        <el-image :preview-src-list="isDisabled ? [scope.row.image] : []" :src="scope.row.image" />
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
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <el-input
                      :disabled="isDisabled"
                      v-model="scope.row[iii]"
                      :type="formThead[iii].title === '商品编号' ? 'text' : 'number'"
                      :min="0"
                      class="priceBox"
                      @keyup.native="keyupEvent(iii, scope.row[iii], scope.$index, 2)"
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
            <!-- <div>manyTabDate:{{manyTabDate}}</div> -->
            <el-form-item label="全部sku：" v-if="$route.params.id && showAll">
              <el-button type="default" @click="showAllSku()" :disabled="isDisabled">展示</el-button>
            </el-form-item>
            <!-- 多规格表格-->
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
                </el-table-column>
                <el-table-column
                  v-for="(item, iii) in attrValue"
                  :key="iii"
                  :label="formThead[iii].title"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <!--                    <span>scope.row:{{scope.row}}</span>-->
                    <el-input
                      :disabled="isDisabled"
                      v-model="scope.row[iii]"
                      :type="formThead[iii].title === '商品编号' ? 'text' : 'number'"
                      class="priceBox"
                      @keyup.native="keyupEvent(iii, scope.row[iii], scope.$index, 3)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="一级返佣(元)" min-width="120" v-if="formValidate.isSub">
                  <template slot-scope="scope">
                    <el-input
                      :disabled="isDisabled"
                      v-model="scope.row.brokerage"
                      type="number"
                      :min="0"
                      :max="scope.row.price"
                      class="priceBox"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="二级返佣(元)" min-width="120" v-if="formValidate.isSub">
                  <template slot-scope="scope">
                    <el-input
                      :disabled="isDisabled"
                      v-model="scope.row.brokerageTwo"
                      type="number"
                      :min="0"
                      :max="scope.row.price"
                      class="priceBox"
                    />
                  </template>
                </el-table-column>
                <el-table-column v-if="!isDisabled" key="3" label="操作" min-width="80">
                  <template slot-scope="scope">
                    <a class="submission" @click="delAttrTable(scope.$index)">删除</a>
                  </template>
                </el-table-column>
              </el-table>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 商品详情-->
        <el-row v-show="currentTab == 2 && !isDisabled">
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <Tinymce v-model.trim="formValidate.content" :key="htmlKey"></Tinymce>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-show="currentTab == 2 && isDisabled">
          <el-col :span="24">
            <el-form-item label="商品详情：">
              <span v-html="formValidate.content || '无'"></span>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 其他设置-->
        <el-row v-show="currentTab == 3">
          <el-col :span="24">
            <el-col v-bind="grid">
              <el-form-item label="排序：">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.sort"
                  :min="0"
                  placeholder="请输入排序"
                  :disabled="isDisabled"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid">
              <el-form-item label="积分：">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.giveIntegral"
                  :min="0"
                  placeholder="请输入排序"
                  :disabled="isDisabled"
                />
              </el-form-item>
            </el-col>
            <el-col v-bind="grid">
              <el-form-item label="虚拟销量：">
                <el-input-number
                  controls-position="right"
                  v-model="formValidate.ficti"
                  :min="0"
                  placeholder="请输入排序"
                  :disabled="isDisabled"
                />
              </el-form-item>
            </el-col>
          </el-col>
          <el-col :span="24">
            <el-form-item label="商品推荐：">
              <el-checkbox-group v-model="checkboxGroup" size="small" @change="onChangeGroup" :disabled="isDisabled">
                <el-checkbox v-for="(item, index) in recommend" :key="index" :label="item.value">{{
                  item.name
                }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="活动优先级：">
              <div class="color-list acea-row row-middle">
                <div
                  :disabled="isDisabled"
                  class="color-item"
                  :class="activity[item]"
                  v-for="item in formValidate.activity"
                  :key="item"
                  draggable="true"
                  @dragstart="handleDragStart($event, item)"
                  @dragover.prevent="handleDragOver($event, item)"
                  @dragenter="handleDragEnterFont($event, item)"
                  @dragend="handleDragEnd($event, item)"
                >
                  {{ item }}
                </div>
                <div class="tip">可拖动按钮调整活动的优先展示顺序</div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="优惠券：" class="proCoupon">
              <div>
                <el-tag
                  v-for="(tag, index) in formValidate.coupons"
                  :key="index"
                  class="mr10"
                  :closable="!isDisabled"
                  :disable-transitions="false"
                  @close="handleCloseCoupon(tag)"
                >
                  {{ tag.name }}
                </el-tag>
                <span class="mr10" v-if="formValidate.couponIds == null">暂无优惠券</span>
                <el-button v-if="!isDisabled" class="mr15" @click="addCoupon">选择优惠券</el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button v-show="Number(currentTab) > 0" class="submission" @click="handleSubmitUp">上一步</el-button>
          <el-button
            v-show="Number(currentTab) < 3"
            class="submission"
            :class="Number(currentTab) == 0 ? 'onePrimary' : ''"
            @click="handleSubmitNest('formValidate')"
            >下一步</el-button
          >
          <el-button v-show="!isDisabled" type="primary" class="submission" @click="handleSubmit('formValidate')"
            >提交</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getShippingList" />
  </div>
</template>

<script>
import Tinymce from '@/components/Tinymce/index';
import { templateListApi, productCreateApi, categoryApi, productDetailApi, productUpdateApi } from '@/api/store';
import { marketingSendApi } from '@/api/marketing';
import { shippingTemplatesList } from '@/api/logistics';
import { goodDesignList } from '@/api/systemGroup';
import { clearTreeData } from '@/utils/ZBKJIutil';
import CreatTemplates from '@/views/systemSetting/deliverGoods/freightSet/creatTemplates';
import Templates from '../../appSetting/wxAccount/wxTemplate/index';
import { Debounce } from '@/utils/validate';
import { copyConfigApi, copyProductApi } from '@/api/store';
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
};
export default {
  name: 'SortCreat',
  components: { Templates, CreatTemplates, Tinymce },
  data() {
    return {
      htmlKey: 0,
      isDisabled: this.$route.params.isDisabled === '1' ? true : false,
      activity: { 默认: 'red', 秒杀: 'blue', 砍价: 'green', 拼团: 'yellow' },
      props2: {
        children: 'child',
        label: 'name',
        value: 'id',
        multiple: true,
        emitPath: false,
      },
      checkboxGroup: [],
      recommend: [],
      tabs: [],
      fullscreenLoading: false,
      props: { multiple: true },
      active: 0,
      OneattrValue: [Object.assign({}, defaultObj.attrValue[0])], // 单规格
      ManyAttrValue: [Object.assign({}, defaultObj.attrValue[0])], // 多规格
      ruleList: [],
      merCateList: [], // 商品分类筛选
      shippingList: [], // 运费模板
      formThead: Object.assign({}, objTitle),
      formValidate: Object.assign({}, defaultObj),
      formDynamics: {
        ruleName: '',
        ruleValue: [],
      },
      tempData: {
        page: 1,
        limit: 9999,
      },
      manyTabTit: {},
      manyTabDate: {},
      grid2: {
        xl: 24,
        lg: 24,
        md: 24,
        sm: 24,
        xs: 24,
      },
      // 规格数据
      formDynamic: {
        attrsName: '',
        attrsVal: '',
      },
      isBtn: false,
      manyFormValidate: [],
      currentTab: 0,
      isChoice: '',
      grid: {
        xl: 24,
        lg: 24,
        md: 24,
        sm: 24,
        xs: 24,
      },
      ruleValidate: {
        storeName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        cateIds: [{ required: true, message: '请选择商品分类', trigger: 'change', type: 'array', min: '1' }],
        keyword: [{ required: true, message: '请输入商品关键字', trigger: 'blur' }],
        unitName: [{ required: true, message: '请输入单位', trigger: 'blur' }],
        tempId: [{ required: true, message: '请选择运费模板', trigger: 'change' }],
        image: [{ required: true, message: '请上传商品图', trigger: 'change' }],
        sliderImages: [{ required: true, message: '请上传商品轮播图', type: 'array', trigger: 'change' }],
        specType: [{ required: true, message: '请选择商品规格', trigger: 'change' }],
      },
      attrInfo: {},
      tableFrom: {
        page: 1,
        limit: 9999,
        keywords: '',
      },
      tempRoute: {},
      keyNum: 0,
      isAttr: false,
      showAll: false,
      videoLink: '',
      copyConfig: {},
      url: '',
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
    if (this.$route.params.id && this.formValidate.specType) {
      this.$watch('formValidate.attr', this.watCh);
    }
  },
  mounted() {
    this.getCopyConfig();
    this.formValidate.sliderImages = [];
    if (this.$route.params.id) {
      this.setTagsViewTitle();
      this.getInfo();
    }
    this.getCategorySelect();
    this.getShippingList();
    this.getGoodsType();
  },
  methods: {
    addProduct() {
      if (this.url) {
        this.loading = true;
        this.copyConfig.copyType == 1
          ? copyProductApi({ url: this.url })
              .then((res) => {
                let info = res.info;
                this.formValidate = {
                  image: this.$selfUtil.setDomain(info.image),
                  sliderImage: info.sliderImage,
                  storeName: info.storeName,
                  keyword: info.keyword,
                  cateIds: info.cateId ? info.cateId.split(',') : [], // 商品分类id
                  cateId: info.cateId, // 商品分类id传值
                  unitName: info.unitName,
                  sort: 0,
                  isShow: 0,
                  isBenefit: false,
                  isNew: false,
                  isGood: false,
                  isHot: false,
                  isBest: false,
                  tempId: info.tempId,
                  attrValue: info.attrValue,
                  attr: info.attr || [],
                  selectRule: info.selectRule,
                  isSub: false,
                  content: this.$selfUtil.replaceImgSrcHttps(info.content),
                  specType: info.attr.length ? true : false,
                  id: info.id,
                  giveIntegral: info.giveIntegral,
                  ficti: info.ficti,
                };
                if (info.isHot) this.checkboxGroup.push('isHot');
                if (info.isGood) this.checkboxGroup.push('isGood');
                if (info.isBenefit) this.checkboxGroup.push('isBenefit');
                if (info.isBest) this.checkboxGroup.push('isBest');
                if (info.isNew) this.checkboxGroup.push('isNew');
                let imgs = JSON.parse(info.sliderImage);
                let imgss = [];
                Object.keys(imgs).map((i) => {
                  imgss.push(this.$selfUtil.setDomain(imgs[i]));
                });
                this.formValidate.sliderImages = imgss;
                if (this.formValidate.attr.length) {
                  this.oneFormBatch[0].image = this.$selfUtil.setDomain(info.image);
                  for (var i = 0; i < this.formValidate.attr.length; i++) {
                    this.formValidate.attr[i].attrValue = JSON.parse(this.formValidate.attr[i].attrValues);
                  }
                }
                this.loading = false;
              })
              .catch(() => {
                this.loading = false;
              })
          : importProductApi({ url: this.url, form: this.form })
              .then((res) => {
                this.formValidate = {
                  image: this.$selfUtil.setDomain(res.image),
                  sliderImage: res.sliderImage,
                  storeName: res.storeName,
                  keyword: res.keyword,
                  cateIds: res.cateId ? res.cateId.split(',') : [], // 商品分类id
                  cateId: res.cateId, // 商品分类id传值
                  unitName: res.unitName,
                  sort: 0,
                  isShow: 0,
                  isBenefit: false,
                  isNew: false,
                  isGood: false,
                  isHot: false,
                  isBest: false,
                  tempId: res.tempId,
                  attrValue: res.attrValue,
                  attr: res.attr || [],
                  selectRule: res.selectRule,
                  isSub: false,
                  content: res.content,
                  specType: res.attr.length ? true : false,
                  id: res.id,
                  giveIntegral: res.giveIntegral,
                  ficti: res.ficti,
                };
                let imgs = JSON.parse(res.sliderImage);
                let imgss = [];
                Object.keys(imgs).map((i) => {
                  imgss.push(this.$selfUtil.setDomain(imgs[i]));
                });
                this.formValidate.sliderImages = imgss;
                if (this.formValidate.attr.length) {
                  this.oneFormBatch[0].image = this.$selfUtil.setDomain(res.image);
                  for (var i = 0; i < this.formValidate.attr.length; i++) {
                    this.formValidate.attr[i].attrValue = JSON.parse(this.formValidate.attr[i].attrValues);
                  }
                }
                this.loading = false;
              })
              .catch(() => {
                this.loading = false;
              });
      } else {
        this.$message.warning('请输入链接地址！');
      }
    },
    getCopyConfig() {
      copyConfigApi().then((res) => {
        this.copyConfig = res;
      });
    },
    tabsHandleClick(tab, event) {
      this.currentTab = tab.name;
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
    handleCloseCoupon(tag) {
      this.isAttr = true;
      this.formValidate.coupons.splice(this.formValidate.coupons.indexOf(tag), 1);
      this.formValidate.couponIds.splice(this.formValidate.couponIds.indexOf(tag.id), 1);
    },
    addCoupon() {
      const _this = this;
      this.$modalCoupon(
        'wu',
        (this.keyNum += 1),
        this.formValidate.coupons,
        function (row) {
          _this.formValidate.couponIds = [];
          _this.formValidate.coupons = row;
          row.map((item) => {
            _this.formValidate.couponIds.push(item.id);
          });
        },
        '',
      );
    },
    setTagsViewTitle() {
      const title = this.isDisabled ? '商品详情' : '编辑商品';
      const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.$route.params.id}` });
      this.$store.dispatch('tagsView/updateVisitedView', route);
    },
    onChangeGroup() {
      this.checkboxGroup.includes('isGood') ? (this.formValidate.isGood = true) : (this.formValidate.isGood = false);
      this.checkboxGroup.includes('isBenefit')
        ? (this.formValidate.isBenefit = true)
        : (this.formValidate.isBenefit = false);
      this.checkboxGroup.includes('isBest') ? (this.formValidate.isBest = true) : (this.formValidate.isBest = false);
      this.checkboxGroup.includes('isNew') ? (this.formValidate.isNew = true) : (this.formValidate.isNew = false);
      this.checkboxGroup.includes('isHot') ? (this.formValidate.isHot = true) : (this.formValidate.isHot = false);
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
    // 运费模板
    addTem() {
      this.$refs.addTemplates.dialogVisible = true;
      this.$refs.addTemplates.getCityList();
    },
    // 添加规则；
    addRule() {
      const _this = this;
      this.$modalAttr(this.formDynamics, function () {
        _this.productGetRule();
      });
    },
    // 选择规格
    onChangeSpec(num) {
      this.isAttr = true;
      if (num) this.productGetRule();
    },
    // 选择属性确认
    confirm() {
      this.isAttr = true;
      if (!this.formValidate.selectRule) {
        return this.$message.warning('请选择属性');
      }
      const data = [];
      this.ruleList.forEach((item) => {
        if (item.id === this.formValidate.selectRule) {
          item.ruleValue.forEach((i) => {
            data.push({
              attrName: i.value,
              attrValue: i.detail,
            });
          });
        }
        this.formValidate.attr = data;
      });
    },
    //采集
    getCollect() {},
    // 商品分类；
    getCategorySelect() {
      categoryApi({ status: -1, type: 1 }).then((res) => {
        this.merCateList = this.addDisabled(res);
      });
    },
    //限制商品分类只能选择开启的
    addDisabled(dropdownList) {
      const list = [];
      try {
        dropdownList.forEach((e, index) => {
          let e_new = {
            id: e.id,
            name: e.name,
            level: e.level,
            pid: e.pid,
            sort: e.sort,
            status: e.status,
          };
          if (!e.status) {
            e_new = { ...e_new, disabled: true };
          }
          if (e.child) {
            const childList = this.addDisabled(e.child);
            e_new = { ...e_new, child: childList };
          }
          list.push(e_new);
        });
      } catch (error) {
        console.log(error);
        return [];
      }
      return list;
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
    // 运费模板；
    getShippingList() {
      shippingTemplatesList(this.tempData).then((res) => {
        this.shippingList = res.list;
      });
    },
    showInput(item) {
      this.$set(item, 'inputVisible', true);
    },
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
          delete item.brokerage;
          delete item.brokerageTwo;
          this.$set(item, 'brokerage', null);
          this.$set(item, 'brokerageTwo', null);
        });
        this.ManyAttrValue.map((item) => {
          delete item.brokerage;
          delete item.brokerageTwo;
        });
      }
    },
    // 删除表格中的属性
    delAttrTable(index) {
      this.ManyAttrValue.splice(index, 1);
    },
    // 批量添加
    batchAdd() {
      // if (!this.oneFormBatch[0].pic || !this.oneFormBatch[0].price || !this.oneFormBatch[0].cost || !this.oneFormBatch[0].ot_price ||
      //     !this.oneFormBatch[0].stock || !this.oneFormBatch[0].bar_code) return this.$Message.warning('请填写完整的批量设置内容！');
      for (const val of this.ManyAttrValue) {
        this.$set(val, 'image', this.oneFormBatch[0].image);
        this.$set(val, 'price', this.oneFormBatch[0].price);
        this.$set(val, 'cost', this.oneFormBatch[0].cost);
        this.$set(val, 'otPrice', this.oneFormBatch[0].otPrice);
        this.$set(val, 'stock', this.oneFormBatch[0].stock);
        this.$set(val, 'barCode', this.oneFormBatch[0].barCode);
        this.$set(val, 'weight', this.oneFormBatch[0].weight);
        this.$set(val, 'volume', this.oneFormBatch[0].volume);
        this.$set(val, 'brokerage', this.oneFormBatch[0].brokerage);
        this.$set(val, 'brokerageTwo', this.oneFormBatch[0].brokerageTwo);
      }
    },
    // 添加按钮
    addBtn() {
      this.clearAttr();
      this.isBtn = true;
    },
    // 取消
    offAttrName() {
      this.isBtn = false;
    },
    clearAttr() {
      this.isAttr = true;
      this.formDynamic.attrsName = '';
      this.formDynamic.attrsVal = '';
    },
    // 删除规格
    handleRemoveAttr(index) {
      this.isAttr = true;
      this.formValidate.attr.splice(index, 1);
      this.manyFormValidate.splice(index, 1);
    },
    // 删除属性
    handleClose(item, index, attrIndex) {
      if (index === 0 && item.length === 1) this.handleRemoveAttr(attrIndex);
      item.splice(index, 1);
    },
    // 添加规则名称
    createAttrName() {
      this.isAttr = true;
      if (this.formDynamic.attrsName && this.formDynamic.attrsVal) {
        const data = {
          attrName: this.formDynamic.attrsName,
          attrValue: [this.formDynamic.attrsVal],
        };
        this.formValidate.attr.push(data);
        var hash = {};
        this.formValidate.attr = this.formValidate.attr.reduce(function (item, next) {
          /* eslint-disable */
          hash[next.attrName] ? '' : (hash[next.attrName] = true && item.push(next));
          return item;
        }, []);
        this.clearAttr();
        this.isBtn = false;
      } else {
        this.$Message.warning('请添加完整的规格！');
      }
    },
    // 添加属性
    createAttr(num, idx) {
      this.isAttr = true;
      if (num) {
        this.formValidate.attr[idx].attrValue.push(num);
        var hash = {};
        this.formValidate.attr[idx].attrValue = this.formValidate.attr[idx].attrValue.reduce(function (item, next) {
          /* eslint-disable */
          hash[next] ? '' : (hash[next] = true && item.push(next));
          return item;
        }, []);
        this.formValidate.attr[idx].inputVisible = false;
      } else {
        // this.$message.warning('请添加属性');
      }
    },
    //点击展示所有多规格属性
    showAllSku() {
      if (this.isAttr == false) {
        this.isAttr = true;
        if (this.formValidate.specType && this.isAttr) this.watCh(this.formValidate.attr); //重要！！！
      } else if (this.isAttr == true) {
        this.isAttr = false;
        this.getInfo();
      }
    },
    // 详情
    getInfo() {
      this.fullscreenLoading = true;
      productDetailApi(this.$route.params.id)
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
          if (info.isHot) this.checkboxGroup.push('isHot');
          if (info.isGood) this.checkboxGroup.push('isGood');
          if (info.isBenefit) this.checkboxGroup.push('isBenefit');
          if (info.isBest) this.checkboxGroup.push('isBest');
          if (info.isNew) this.checkboxGroup.push('isNew');
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
          this.fullscreenLoading = false;
        })
        .catch((res) => {
          this.fullscreenLoading = false;
          this.$message.error(res.message);
        });
    },
    handleRemove(i) {
      this.formValidate.sliderImages.splice(i, 1);
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
    handleSubmitUp() {
      if (this.currentTab-- < 0) this.currentTab = 0;
      this.currentTab = this.currentTab.toString();
    },
    handleSubmitNest(name) {
      this.$refs[name].validate((valid) => {
        if (valid) {
          if (this.currentTab++ > 3) this.currentTab = 0;
          this.currentTab = this.currentTab.toString();
        } else {
          if (
            !this.formValidate.store_name ||
            !this.formValidate.cate_id ||
            !this.formValidate.keyword ||
            !this.formValidate.unit_name ||
            !this.formValidate.store_info ||
            !this.formValidate.image ||
            !this.formValidate.slider_image
          ) {
            this.$message.warning('请填写完整商品信息！');
          }
        }
      });
    },
    //提交接口数据更新
    getFromData() {
      if (this.formValidate.specType && this.formValidate.attr.length < 1)
        return this.$message.warning('请填写多规格属性！');
      this.formValidate.cateId = this.formValidate.cateIds.join(',');
      if (this.formValidate.videoLink) {
        //如果有视频主图，将视频链接插入到轮播图第一的位置
        this.formValidate.sliderImages.unshift(this.formValidate.videoLink);
      }
      this.formValidate.sliderImage = JSON.stringify(this.formValidate.sliderImages);
      if (this.formValidate.specType) {
        this.formValidate.attrValue = this.ManyAttrValue;
        this.formValidate.attr = this.formValidate.attr.map((item) => {
          return {
            attrName: item.attrName,
            id: item.id,
            attrValue: item.attrValue,
            attrValues: item.attrValue.join(','),
          };
        });
        for (var i = 0; i < this.formValidate.attrValue.length; i++) {
          this.$set(this.formValidate.attrValue[i], 'id', 0);
          this.$set(this.formValidate.attrValue[i], 'productId', 0);
          let attrValues = this.formValidate.attrValue[i].attrValue;
          this.$set(this.formValidate.attrValue[i], 'attrValue', JSON.stringify(attrValues));
          delete this.formValidate.attrValue[i].value0;
        }
      } else {
        this.formValidate.attr = [
          { attrName: '规格', attrValues: '默认', id: this.$route.params.id ? this.formValidate.attr[0].id : 0 },
        ];
        this.OneattrValue.map((item) => {
          this.$set(item, 'attrValue', JSON.stringify({ 规格: '默认' }));
          //this.$set(item, 'productId', 0);
        });
        this.formValidate.attrValue = this.OneattrValue;
      }
    },
    // 提交
    handleSubmit: Debounce(function (name) {
      this.onChangeGroup();
      this.getFromData();
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.fullscreenLoading = true;
          this.$route.params.id
            ? productUpdateApi(this.formValidate)
                .then(async (res) => {
                  this.$message.success('编辑成功');
                  setTimeout(() => {
                    this.$router.push({ path: '/store/index' });
                  }, 500);
                  this.fullscreenLoading = false;
                })
                .catch((res) => {
                  this.fullscreenLoading = false;
                  this.restoreData();
                  if (this.formValidate.specType) this.ManyAttrValue = this.formValidate.attrValue;
                })
            : productCreateApi(this.formValidate)
                .then(async (res) => {
                  this.$message.success('新增成功');
                  setTimeout(() => {
                    this.$router.push({ path: '/store/index' });
                  }, 500);
                  this.fullscreenLoading = false;
                })
                .catch((res) => {
                  this.fullscreenLoading = false;
                  this.restoreData();
                });
        } else {
          if (
            !this.formValidate.storeName ||
            !this.formValidate.cateId ||
            !this.formValidate.keyword ||
            !this.formValidate.unitName ||
            !this.formValidate.image ||
            !this.formValidate.sliderImages
          ) {
            this.$message.warning('请填写完整商品信息！');
          }
        }
      });
    }),
    // 提交失败之后恢复数据
    restoreData() {
      for (var i = 0; i < this.formValidate.attrValue.length; i++) {
        let attrValues = this.formValidate.attrValue[i].attrValue;
        this.$set(this.formValidate.attrValue[i], 'attrValue', JSON.parse(attrValues));
      }
    },
    // 表单验证
    validate(prop, status, error) {
      if (status === false) {
        this.$message.warning(error);
      }
    },
    // 移动
    handleDragStart(e, item) {
      if (!this.isDisabled) this.dragging = item;
    },
    handleDragEnd(e, item) {
      if (!this.isDisabled) this.dragging = null;
    },
    handleDragOver(e) {
      if (!this.isDisabled) e.dataTransfer.dropEffect = 'move';
    },
    handleDragEnter(e, item) {
      if (!this.isDisabled) {
        e.dataTransfer.effectAllowed = 'move';
        if (item === this.dragging) {
          return;
        }
        const newItems = [...this.formValidate.sliderImages];
        const src = newItems.indexOf(this.dragging);
        const dst = newItems.indexOf(item);
        newItems.splice(dst, 0, ...newItems.splice(src, 1));
        this.formValidate.sliderImages = newItems;
      }
    },
    handleDragEnterFont(e, item) {
      if (!this.isDisabled) {
        e.dataTransfer.effectAllowed = 'move';
        if (item === this.dragging) {
          return;
        }
        const newItems = [...this.formValidate.activity];
        const src = newItems.indexOf(this.dragging);
        const dst = newItems.indexOf(item);
        newItems.splice(dst, 0, ...newItems.splice(src, 1));
        this.formValidate.activity = newItems;
      }
    },
    getGoodsType() {
      /** 让商品推荐列表的name属性与页面设置tab的name匹配**/
      goodDesignList({ gid: 70 }).then((response) => {
        let list = response.list;
        let arr = [],
          arr1 = [];
        const listArr = [{ name: '是否热卖', value: 'isGood', type: '5' }];
        let typeLists = [
          { name: '', value: 'isHot', type: '2' }, //热门榜单
          { name: '', value: 'isBenefit', type: '4' }, //促销单品
          { name: '', value: 'isBest', type: '1' }, //精品推荐
          { name: '', value: 'isNew', type: '3' },
        ]; //首发新品
        list.forEach((item) => {
          let obj = {};
          obj.value = JSON.parse(item.value);
          obj.id = item.id;
          obj.gid = item.gid;
          obj.status = item.status;
          arr.push(obj);
        });
        arr.forEach((item1) => {
          let obj1 = {};
          obj1.name = item1.value.fields[1].value;
          obj1.status = item1.status;
          obj1.type = item1.value.fields[3].value;
          arr1.push(obj1);
        });
        typeLists.forEach((item) => {
          arr1.forEach((item1) => {
            if (item.type == item1.type) {
              listArr.push({
                name: item1.name,
                value: item.value,
                type: item.type,
              });
            }
          });
        });
        this.recommend = listArr;
      });
    },
    // 删除视频；
    delVideo() {
      let that = this;
      that.$set(that.formValidate, 'videoLink', '');
    },
    zh_uploadFile() {
      if (this.videoLink) {
        this.$set(this.formValidate, 'videoLink', this.videoLink);
      }
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
  },
};
</script>
<style scoped lang="scss">
.upLoadPicBox {
  ::v-deep.el-alert {
    padding: 0 !important;
  }
}

.disLabel {
  ::v-deepel-form-item__label {
    margin-left: 36px !important;
  }
}

.disLabelmoren {
  ::v-deepel-form-item__label {
    margin-left: 120px !important;
  }
}

.priamry_border {
  border: 1px solid #1890ff;
  color: #1890ff;
}

.color-item {
  height: 30px;
  line-height: 30px;
  padding: 0 10px;
  color: #fff;
  margin-right: 10px;
}

.color-list .color-item.blue {
  background-color: #1e9fff;
}

.color-list .color-item.yellow {
  background-color: rgb(254, 185, 0);
}

.color-list .color-item.green {
  background-color: #009688;
}

.color-list .color-item.red {
  background-color: #ed4014;
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
  width: 100%;
}

.selWidthd {
  width: 300px;
}

.button-new-tag {
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

  img,
  .image {
    width: 100%;
    height: 100%;
  }

  video {
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

.labeltop {
  ::v-deepel-form-item__label {
    float: none !important;
    display: inline-block !important;
    width: auto !important;
  }
}

.iview-video-style {
  width: 300px;
  height: 180px;
  border-radius: 10px;
  background-color: #707070;
  margin: 0 120px 20px;
  position: relative;
  overflow: hidden;
}

.iview-video-style .iconv {
  color: #fff;
  line-height: 180px;
  width: 50px;
  height: 50px;
  display: inherit;
  font-size: 26px;
  position: absolute;
  top: -74px;
  left: 50%;
  margin-left: -25px;
}

.iview-video-style .mark {
  position: absolute;
  width: 100%;
  height: 30px;
  top: 0;
  background-color: rgba(0, 0, 0, 0.5);
  text-align: center;
}

::v-deep .el-tabs__nav-scroll {
  margin-top: -20px;
}
.selWidth100 {
  width: 100%;
}
.tips-bottom {
  margin-top: 10px;
}
.onePrimary {
  margin-left: 0 !important;
}
.formValidate.mt20 {
  padding: 0 30px;
}
.mr16 {
  margin-right: 16px;
}
::v-deep .el-radio__label {
  font-size: 12px !important;
}
::v-deep .el-radio__input {
  font-size: 12px !important;
}
.inputWid {
  width: 500px;
}
.noLeft {
  margin-left: -60px;
}
</style>
