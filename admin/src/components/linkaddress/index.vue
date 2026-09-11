<template>
  <div>
    <el-dialog
      lock-scroll
      :append-to-body="true"
      show-close
      title="选择链接"
      :close-on-click-modal="false"
      :data="categoryData"
      width="860"
      v-model="dialogVisible"
    >
      <div class="table_box">
        <div class="left_box">
          <el-tree
            :data="categoryData"
            :props="defaultProps"
            :default-expanded-keys="[1, 2, 3, 4]"
            :current-node-key="5"
            :highlight-current="true"
            node-key="id"
            @node-click="handleNodeClick"
          ></el-tree>
        </div>
        <div class="right_box" v-if="currenType == 'link'">
          <div v-if="basicsList.length">
            <div class="cont">基础链接</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in basicsList"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
          <div v-if="userList.length">
            <div class="cont">个人中心</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in userList"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
          <div v-if="distributionList.length">
            <div class="cont">分销</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in distributionList"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
        </div>
        <div class="right_box" v-if="currenType == 'marketing_link'">
          <div v-if="coupon.length">
            <div class="cont">优惠券</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in coupon"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
          <div v-if="basicsList.length">
            <div class="cont">秒杀</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in basicsList"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
          <div v-if="distributionList.length">
            <div class="cont">砍价</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in distributionList"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
          <div v-if="userList.length">
            <div class="cont">拼团</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in userList"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
          <div v-if="integral.length">
            <div class="cont">积分</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in integral"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
          <div v-if="luckDraw.length">
            <div class="cont">抽奖</div>
            <div class="Box">
              <div
                class="cont_box"
                :class="currenId == item.id ? 'on' : ''"
                v-for="(item, index) in luckDraw"
                :key="index"
                @click="getUrl(item)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>
        </div>
        <div
          class="right_box"
          v-if="
            currenType == 'special' ||
            currenType == 'product_category' ||
            currenType == 'product' ||
            currenType == 'seckill' ||
            currenType == 'bargain' ||
            currenType == 'combination' ||
            currenType == 'news' ||
            currenType == 'integral' ||
            currenType === 'micro'
          "
        >
          <el-form ref="formValidateRef" class="tabform" v-if="currenType == 'product'">
            <el-row>
              <el-col :span="24">
                <el-form-item>
                  <el-input
                    placeholder="请输入商品名称,关键字,编号"
                    v-model="params.keywords"
                    style="width: 250px; margin-right: 20px"
                  />
                  <el-button type="primary" @click="getProductList">搜索</el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <el-table
            row-key="id"
            ref="tableRef"

            v-loading="lodingList"
            :data="tableList.list"
            @row-click="singleElection"
            :tree-props="{ children: 'child', hasChildren: 'hasChildren' }"
            :max-height="
              currenType == 'product_category'
                ? '410'
                : currenType == 'product' ||
                  currenType == 'seckill' ||
                  currenType == 'bargain' ||
                  currenType == 'combination' ||
                  currenType == 'news' ||
                  currenType == 'integral' ||
                  currenType === 'micro'
                ? '400'
                : ''
            "
          >
            <el-table-column label="" width="80">
              <template #default="scope">
                <el-radio class="radio" v-model="radioData" :label="scope.$index" :value="scope.$index">&nbsp;</el-radio>
              </template>
            </el-table-column>
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column v-if="currenType !== 'micro'" label="图片" width="80">
              <template #default="scope">
                <el-image
                  style="width: 50px; height: 50px"
                  lazy
                  :src="scope.row.imageInput"
                  fit="fill"
                  v-if="currenType == 'news'"
                ></el-image>
                <img
                  width="30px"
                  height="30px"
                  v-else-if="currenType == 'product_category'"
                  :src="scope.row.extra"
                  alt=""
                />
                <el-image style="width: 50px; height: 50px" lazy :src="scope.row.image" fit="fill" v-else></el-image>
              </template>
            </el-table-column>
            <el-table-column
              :prop="currenType === 'product' ? 'storeName' : 'title'"
              :label="currenType === 'product' ? '商品名称' : '标题名称'"
              :show-overflow-tooltip="true"
              max-width="250"
              v-if="currenType === 'product' || currenType === 'micro'"
            ></el-table-column>
            <el-table-column
              prop="name"
              label="分类名称"
              :show-overflow-tooltip="true"
              max-width="250"
              v-else-if="currenType == 'product_category'"
            ></el-table-column>
            <el-table-column
              prop="title"
              :label="currenType == 'news' ? '文章名称' : '商品名称'"
              :show-overflow-tooltip="true"
              max-width="250"
              v-else
            ></el-table-column>
          </el-table>
          <el-pagination
            class="link-pagination"
            :current-page="params.page"
            :page-sizes="constants.page.limit"
            :layout="constants.page.layout"
            :total="tableList.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            v-if="currenType !== 'product_category'"
          />
        </div>
        <div class="right_box" v-if="currenType == 'custom'">
          <div style="width: 440px; margin: 50px 100px 0 30px">
            <el-form ref="customDateRef" :model="customDate" label-width="100px">
              <el-form-item label="跳转路径：" prop="url">
                <el-input v-model="customDate.url" placeholder="请输入跳转路径" />
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
      <template #footer>
        <div>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit()">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from 'vue';
import { getMicroPageList } from '@/api/theme';
import { productLstApi, categoryApi } from '@/api/store';
import { seckillStoreListApi, combinationListApi, bargainListApi } from '@/api/marketing';
import { ListArticle } from '@/api/article';
import listData from './list.json';
import linkData from './linkData.json';
import marketing from './marketing.json';

defineOptions({ name: 'linkaddress' });

const emit = defineEmits(['linkUrl']);

const { proxy } = getCurrentInstance();

const constants = proxy.$constants;
const dialogVisible = ref(false);
const defaultProps = ref({
  children: 'children',
  label: 'title',
});
const basicsList = ref([]);
const userList = ref([]);
const distributionList = ref([]);
const coupon = ref([]);
const luckDraw = ref([]);
const integral = ref([]);
const lodingList = ref(false);
const categoryData = ref(listData.data);
const currenType = ref('link');
const currenId = ref('');
const currenUrl = ref('');
const formValidate = ref({
  keywords: '',
});
const customDate = ref({
  url: '',
});
const radioData = ref('');
const linkId = ref(0);
const tableList = ref({
  list: [],
  total: 0,
});
const params = ref({
  page: 1,
  limit: 20,
  keywords: '',
  name: '',
});
const columns = ref([
  { title: 'ID', key: 'id', width: 60 },
  { title: '页面名称', key: 'name', width: 150 },
  { title: '页面链接', key: 'url' },
]);
const columns7 = ref([
  { title: 'ID', key: 'id', width: 60 },
  { title: '分类名称', key: 'cate_name', tree: true },
  { title: '分类图标', slot: 'pic' },
]);
const columns8 = ref([
  { title: 'ID', key: 'id', width: 60 },
  { title: '商品图片', slot: 'image', width: 90 },
  { title: '商品名称', key: 'store_name' },
]);
const bargain = ref([
  { title: 'ID', key: 'id', width: 60 },
  { title: '商品图片', slot: 'image', width: 90 },
  { title: '商品名称', key: 'title' },
]);
const news = ref([
  { title: 'ID', key: 'id', width: 60 },
  { title: '文章图片', slot: 'image_input', width: 90 },
  { title: '文章名称', key: 'title' },
]);
const categorytitle = ref('');
const formValidateRef = ref(null);
const tableRef = ref(null);
const customDateRef = ref(null);

const modals = computed({
  get() {
    return dialogVisible.value;
  },
  set(value) {
    dialogVisible.value = value;
  },
});

// 暴露给父组件：在 <script setup> 中，内部绑定默认不挂到实例代理上，
// 父组件通过 ref 调用 `linkaddresRef.value.modals = true` 或 `handleCheckChange()`
// 必须显式 expose 才能生效，否则弹窗无法打开。
defineExpose({
  modals,
  dialogVisible,
  handleCheckChange,
});

onMounted(() => {
  mockData('link');
});

function getProductList() {
  lodingList.value = true;
  productLstApi(params.value).then((res) => {
    tableList.value = normalizeTableList(res);
    lodingList.value = false;
  });
}

function handleCheckChange(data = {}) {
  params.value.keywords = '';
  currenId.value = '';
  currenUrl.value = '';
  linkId.value = 0;
  radioData.value = '';
  categorytitle.value = '';
  currenType.value = data.type || 'link';
  mockData(currenType.value);
}

function handleNodeClick(data) {
  params.value.keywords = '';
  currenId.value = '';
  currenUrl.value = '';
  linkId.value = 0;
  radioData.value = '';
  categorytitle.value = '';
  currenType.value = data.type;
  mockData(data.type);
}

function mockData(type) {
  let data = [];
  if (type == 'marketing_link') {
    data = marketing.data.list;
  } else if (type == 'link') {
    data = linkData.data.list;
  } else if (type == 'product_category') {
    lodingList.value = true;
    categoryApi({ type: 1, status: -1 }).then((res) => {
      tableList.value = normalizeTableList(res);
      lodingList.value = false;
    });
  } else if (type == 'product') {
    getProductList();
  } else if (type == 'seckill') {
    lodingList.value = true;
    seckillStoreListApi(params.value).then((res) => {
      tableList.value = normalizeTableList(res);
      lodingList.value = false;
    });
  } else if (type == 'bargain') {
    lodingList.value = true;
    bargainListApi(params.value).then((res) => {
      tableList.value = normalizeTableList(res);
      lodingList.value = false;
    });
  } else if (type == 'combination') {
    lodingList.value = true;
    combinationListApi(params.value).then((res) => {
      tableList.value = normalizeTableList(res);
      lodingList.value = false;
    });
  } else if (type == 'news') {
    lodingList.value = true;
    ListArticle(params.value).then((res) => {
      tableList.value = normalizeTableList(res);
      lodingList.value = false;
    });
  } else if (type == 'micro') {
    lodingList.value = true;
    getMicroPageList(params.value).then((res) => {
      tableList.value = normalizeTableList(res);
      lodingList.value = false;
    });
  }
  let basicsListArr = [];
  let distributionListArr = [];
  let userListArr = [];
  let integralArr = [];
  let luckDrawArr = [];
  let couponArr = [];
  data.forEach((e) => {
    if (e.type == 1) {
      basicsListArr.push(e);
    } else if (e.type == 2) {
      distributionListArr.push(e);
    } else if (e.type == 3) {
      userListArr.push(e);
    } else if (e.type == 4) {
      integralArr.push(e);
    } else if (e.type == 5) {
      luckDrawArr.push(e);
    } else {
      couponArr.push(e);
    }
  });
  basicsList.value = basicsListArr;
  distributionList.value = distributionListArr;
  userList.value = userListArr;
  coupon.value = couponArr;
  luckDraw.value = luckDrawArr;
  integral.value = integralArr;
}

function getUrl(item) {
  currenId.value = item.id;
  currenUrl.value = item.url;
}

function singleElection(row) {
  linkId.value = row.id || row.value;
  categorytitle.value = row.name || row.title || row.label || row.storeName || '';
}

function normalizeTableList(res) {
  const list = Array.isArray(res) ? res : res.list || (res.data && res.data.list) || res.data || [];
  return {
    ...res,
    list: Array.isArray(list) ? list : [],
    total: res.total || res.count || (res.data && (res.data.total || res.data.count)) || (Array.isArray(list) ? list.length : 0),
  };
}

function handleSubmit(name) {
  switch (currenType.value) {
    case 'product':
      emit('linkUrl', '/pages/goods/goods_details/index?id=' + linkId.value);
      break;
    case 'seckill':
      emit('linkUrl', '/pages/activity/goods_seckill_details/index?id=' + linkId.value);
      break;
    case 'bargain':
      emit('linkUrl', '/pages/activity/goods_bargain_details/index?id=' + linkId.value);
      break;
    case 'combination':
      emit('linkUrl', '/pages/activity/goods_combination_details/index?id=' + linkId.value);
      break;
    case 'news':
      emit('linkUrl', '/pages/news/news_details/index?id=' + linkId.value);
      break;
    case 'product_category':
      emit(
        'linkUrl',
        '/pages/goods/goods_list/index?cid=' + linkId.value + '&title=' + encodeURIComponent(categorytitle.value),
      );
      break;
    case 'custom':
      emit('linkUrl', customDate.value.url);
      break;
    case 'micro':
      emit('linkUrl', `/pages/activity/small_page/index?micro_id=${linkId.value}`);
      break;
    default:
      emit('linkUrl', currenUrl.value);
      break;
  }
  dialogVisible.value = false;
}

function handleSizeChange(val) {
  params.value.limit = val;
  mockData(currenType.value);
}

function handleCurrentChange(val) {
  params.value.page = val;
  mockData(currenType.value);
}
</script>

<style scoped lang="scss">
.tabBox_img {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
  }
}

/*定义滑块 内阴影+圆角*/
::-webkit-scrollbar-thumb {
  -webkit-box-shadow: inset 0 0 6px #ddd;
}

::-webkit-scrollbar {
  width: 4px !important;
  /*对垂直流动条有效*/
}

.on {
  background-color: #2d8cf0 !important;
  color: #fff !important;
}

.menu-item {
  position: relative;

  display flex justify-content space-between word-break break-all .icon-box {
    z-index: 3 position absolute right 20px top 50% transform translateY(-50%) display none;
  }

  &:hover .icon-box {
    display: block;
  }

  .right-menu {
    z-index: 10;
    position: absolute;
    right: -106px;
    top: -11px;
    width: auto;
    min-width: 121px;
  }
}

.table_box {
  margin-top: 14px;
  display: flex;
  position: relative;

  .left_box {
    width: 171px;
    height: 470px;
    border-right: 1px solid #eeeeee;
    overflow-x: hidden;
    overflow-y: auto;

    .left_cont {
      margin-bottom: 12px;
      cursor: pointer;
    }
  }

  .right_box {
    margin-left: 23px;
    font-size: 13px;
    font-family: PingFang SC;
    width: 645px;
    height: 470px;
    overflow-x: hidden;
    overflow-y: auto;

    .cont {
      font-weight: 500;
      color: #000000;
      font-weight: bold;
    }

    .Box {
      margin-top: 19px;
      display: flex;
      flex-wrap: wrap;

      .cont_box {
        font-weight: 400;
        color: rgba(0, 0, 0, 0.85);
        background: #fafafa;
        border-radius: 3px;
        text-align: center;
        padding: 7px 30px;
        margin-right: 10px;
        margin-bottom: 18px;
        cursor: pointer;

        &:hover {
          background-color: #eee;
          color: #333;
        }
      }

      .item {
        position: relative;

        .iconfont {
          display: none;
        }

        &:hover {
          .iconfont {
            display: block;
          }
        }
      }

      .iconfont {
        position: absolute;
        right: 9px;
        top: -8px;
        font-size: 18px;
        color: #333;
      }
    }
  }

  .Button {
    position: absolute;
    bottom: 15px;
    right: 15px;
    font-family: PingFangSC-Regular;

    text-align center .cancel {
      width: 70px;
      height: 32px;
      background: #ffffff;
      border: 1px solid rgba(0, 0, 0, 0.14901960784313725);
      border-radius: 2px;
      font-size: 14px;
      color: #000000;
      line-height: 32px;
      float: left;
      margin-right: 10px;
      cursor: pointer;
    }

    .ok {
      width: 70px;
      height: 32px;
      background: #1890ff;
      border-radius: 2px;
      font-size: 14px;
      color: #ffffff;
      line-height: 32px;
      float: left;
      cursor: pointer;
    }
  }
}

:deep(.el-tree-node:focus > .el-tree-node__content) {
  color: #409eff; //节点的字体颜色
}
:deep(.link-pagination) {
  display: flex;
  justify-content: flex-end;
  width: 100%;
  margin-top: 20px;
  padding: 0;
}
:deep(.el-table__indent) {
  padding-left: 0 !important;
}
:deep(.el-radio__label) {
  display: none;
}
</style>
