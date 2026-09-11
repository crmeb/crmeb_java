<template>
  <div class="goodList">
    <el-form ref="formValidateRef" :model="formValidate" label-width="80px" label-position="right" inline class="tabform">
      <el-form-item label="商品分类：" v-if="!liveStatus">
        <el-cascader
          v-model="formValidate.cate_id"

          :options="treeSelect"
          :props="{ checkStrictly: true, emitPath: false, multiple: true }"
          clearable
          class="form_content_width"
        ></el-cascader>
      </el-form-item>
      <el-form-item label="商品类型：" v-if="!type && diy">
        <el-select v-model="goodType" clearable @change="userSearchs" class="form_content_width">
          <el-option v-for="item in goodList" :value="item.activeValue" :key="item.activeValue" :label="item.title">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品搜索：">
        <el-input
          clearable
          placeholder="请输入商品名称/关键字/ID"
          v-model="formValidate.store_name"
          class="form_content_width"
        />
        <el-button type="primary" v-db-click @click="userSearchs" class="ml15">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
      ref="tableRef"
      empty-text="暂无数据"
      max-height="400"
      :highlight-current-row="many !== 'many'"
      :data="tableList"
      v-loading="loading"
      @select="changeCheckbox"
      @select-all="changeCheckbox"
    >
      <el-table-column v-if="many == 'many'" type="selection" width="55"> </el-table-column>
      <el-table-column v-else width="50">
        <template #default="scope">
          <el-radio v-model="templateRadio" :label="scope.row.id" :value="scope.row.id" @change="getTemplateRow(scope.row)"
            >&nbsp;</el-radio
          >
        </template>
      </el-table-column>

      <el-table-column label="商品ID" width="80">
        <template #default="scope">
          <span>{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图片" width="80">
        <template #default="scope">
          <div class="tabBox_img" v-viewer>
            <img v-lazy="scope.row.image" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" min-width="250">
        <template #default="scope">
          <span>{{ scope.row.store_name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="商品分类" min-width="150" v-if="liveStatus">
        <template #default="scope">
          <span>{{ scope.row.cate_name }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div class="goods-footer">
      <div class="goods-footer__pagination">
        <pagination
          v-if="total"
          :total="total"
          v-model:page="formValidate.page"
          v-model:limit="formValidate.limit"
          @pagination="pageChange"
        />
      </div>
      <div class="goods-footer__actions" v-if="many === 'many' && !diy">
        <el-button type="primary" v-db-click @click="ok">提交</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { cascaderListApi, changeListApi } from '@/api/product';
import { liveGoods } from '@/api/live';
import { getProductList } from '@/api/diy';

defineOptions({ name: 'index' });

const props = defineProps({
  is_new: {
    type: String,
    default: '',
  },
  type: {
    type: Number,
    default: 0,
  },
  diy: {
    type: Boolean,
    default: false,
  },
  ischeckbox: {
    type: Boolean,
    default: false,
  },
  liveStatus: {
    type: Boolean,
    default: false,
  },
  isLive: {
    type: Boolean,
    default: false,
  },
  isdiy: {
    type: Boolean,
    default: false,
  },
  selectIds: {
    type: Array,
    default: () => {
      return [];
    },
  },
  datas: {
    type: Object,
    default: function () {
      return {};
    },
  },
});

const emit = defineEmits(['getProductId', 'getProductDiy']);

const route = useRoute();

const formValidateRef = ref(null);
const tableRef = ref(null);
const templateRadio = ref(0);
const modal_loading = ref(false);
const treeSelect = ref([]);
const formValidate = ref({
  page: 1,
  limit: 15,
  cate_id: '',
  store_name: '',
  is_new: props.is_new,
});
const total = ref(0);
const modals = ref(false);
const loading = ref(false);
const grid = ref({
  xl: 10,
  lg: 10,
  md: 12,
  sm: 24,
  xs: 24,
});
const tableList = ref([]);
const currentid = ref(0);
const productRow = ref({});
const images = ref([]);
const many = ref('');
const goodType = ref('');
const goodList = ref([
  {
    activeValue: 0,
    title: '商品列表',
  },
  // {
  //   activeValue: '4',
  //   title: '热门榜单',
  // },
  // {
  //   activeValue: '5',
  //   title: '首发新品',
  // },
  // {
  //   activeValue: '6',
  //   title: '促销单品',
  // },
  {
    activeValue: '7',
    title: '优品推荐',
  },
]);
const diyVal = ref({});

watch(
  () => props.ischeckbox,
  (newVal, oldVal) => {
    if (newVal) {
      many.value = 'many';
    }
  },
  { immediate: true },
);

// created
{
  let manyVal = '';
  if (props.ischeckbox) {
    manyVal = 'many';
  } else {
    manyVal = route.query.type;
  }
  many.value = manyVal;
}

function productList() {
  let data = {
    page: formValidate.value.page,
    limit: formValidate.value.limit,
    cate_id: formValidate.value.cate_id,
    store_name: formValidate.value.store_name,
    type: props.type ? props.type : goodType.value,
  };
  loading.value = true;
  getProductList(data)
    .then((res) => {
      let data = res.data;
      tableList.value = data.list;
      total.value = res.data.count;
      loading.value = false;
    })
    .catch((res) => {
      loading.value = false;
      showError(res, '商品列表加载失败');
    });
}

function getTemplateRow(row) {
  let imagesArr = [];
  let imageObject = {
    image: row.image,
    product_id: row.id,
    store_name: row.store_name,
    temp_id: row.temp_id,
  };
  imagesArr.push(imageObject);
  images.value = imagesArr;
  diyVal.value = row;
  emit('getProductId', row);
}

function changeCheckbox(selection) {
  let imagesArr = [];
  selection.forEach(function (item) {
    let imageObject = {
      image: item.image,
      product_id: item.id,
      store_name: item.store_name,
      temp_id: item.temp_id,
    };
    imagesArr.push(imageObject);
  });

  images.value = imagesArr;
  diyVal.value = selection;
  emit('getProductDiy', selection);
}

// 商品分类；
function goodsCategory() {
  cascaderListApi(1)
    .then((res) => {
      treeSelect.value = res.data;
    })
    .catch((res) => {
      showError(res, '商品分类加载失败');
    });
}

function pageChange() {
  if (props.diy) {
    productList();
  } else {
    getList();
  }
}

// 列表
function getList() {
  loading.value = true;
  if (!props.liveStatus) {
    if (props.isLive) {
      formValidate.value.is_live = 1;
    }
    changeListApi(formValidate.value)
      .then(async (res) => {
        let data = res.data;
        tableList.value = data.list;
        total.value = res.data.count;
        loading.value = false;
        nextTick(() => {
          if (props.selectIds.length) {
            let arr = [];
            props.selectIds.map((item) => {
              data.list.map((i) => {
                if (i.id == item) {
                  tableRef.value.toggleRowSelection(i, true);
                  arr.push(i);
                }
              });
            });
            changeCheckbox(arr);
          }
        });
      })
      .catch((res) => {
        loading.value = false;
        showError(res, '商品列表加载失败');
      });
  } else {
    liveGoods({
      is_show: '1',
      status: '1',
      live_id: props.datas.id,
      kerword: formValidate.value.store_name,
      page: formValidate.value.page,
      limit: formValidate.value.limit,
    })
      .then(async (res) => {
        let data = res.data;
        data.list.forEach((el) => {
          el.image = el.cover_img;
        });
        if (props.selectIds.length) {
          props.selectIds.map((item) => {
            data.list.map((i) => {
              if (i.id == item) {
                tableRef.value.toggleRowSelection(i);
              }
            });
          });
        }
        nextTick((e) => {
          tableList.value = data.list;
          total.value = res.data.count;
          loading.value = false;
        });
      })
      .catch((res) => {
        loading.value = false;
        showError(res, '商品列表加载失败');
      });
  }
}

function ok() {
  if (images.value.length > 0) {
    if (route.query.fodder === 'image') {
      let imageValue = form_create_helper.get('image');
      form_create_helper.set('image', imageValue.concat(images.value));
      form_create_helper.close('image');
    } else {
      tableRef.value.clearSelection();
      if (props.isdiy) {
        emit('getProductId', diyVal.value);
      } else {
        emit('getProductId', images.value);
      }
    }
  } else {
    ElMessage.warning('请先选择商品');
  }
}

// 表格搜索
function userSearchs() {
  currentid.value = 0;
  productRow.value = {};
  formValidate.value.page = 1;
  if (props.diy) {
    productList();
  } else {
    getList();
  }
}

function clear() {
  productRow.value.id = '';
  currentid.value = '';
}

function showError(error, fallback) {
  ElMessage.error((error && (error.message || error.msg)) || fallback);
}

onMounted(() => {
  goodsCategory();
  if (props.diy) {
    productList();
  } else {
    getList();
  }
});
</script>

<style lang="scss" scoped>
:deep(.el-checkbox){
  margin-bottom: 0 !important;
}
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
.tabform {
  :deep(.ivu-form-item) {
    margin-bottom: 16px !important;
  }
}
.btn {
  margin-top: 20px;
  float: right;
}
.goodList {
  :deep(.table) {
    width: 100% !important;
  }
}
.goods-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  width: 100%;
  box-sizing: border-box;
  padding: 10px 24px 20px;
  margin-top: 10px;

  &__pagination {
    flex: 0 0 auto;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  &__actions {
    flex-shrink: 0;
    display: flex;
    align-items: center;
  }

  :deep(.pagination-container) {
    background: transparent;
    padding: 0;
    margin-top: 0;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  :deep(.pagination-container .el-pagination) {
    margin: 0;
  }
}
</style>
