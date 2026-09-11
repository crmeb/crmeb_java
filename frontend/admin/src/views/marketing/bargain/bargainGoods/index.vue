<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="砍价状态：">
            <el-select
              v-model="tableFrom.status"
              placeholder="请选择"
              class="filter-item selWidth"
              @change="getList(1)"
              clearable
            >
              <el-option label="关闭" :value="0" />
              <el-option label="开启" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品搜索：">
            <el-input v-model="tableFrom.keywords" placeholder="请输入商品名称、ID" class="selWidth" clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <router-link :to="{ path: '/marketing/bargain/creatBargain' }">
            <el-button type="primary" class="mr14" v-hasPermi="['admin:bargain:save']">添加砍价商品</el-button>
          </router-link>
          <el-button class="mr10" @click="exportList" v-hasPermi="['admin:export:excel:bargain']">导出</el-button>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" ref="multipleTableRef">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="砍价图片" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]" preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="砍价名称" prop="title" min-width="300" :show-overflow-tooltip="true"> </el-table-column>
        <el-table-column label="砍价价格" prop="price" min-width="100" />
        <el-table-column label="最低价" prop="minPrice" min-width="100" />
        <el-table-column label="参与人数" prop="countPeopleAll" min-width="100" />
        <el-table-column label="帮忙砍价人数" prop="countPeopleHelp" min-width="100" />
        <el-table-column label="砍价成功人数" prop="countPeopleSuccess" min-width="100" />
        <el-table-column label="限量" min-width="100" prop="quotaShow" />
        <el-table-column label="限量剩余" prop="surplusQuota" min-width="100" />
        <el-table-column prop="stopTime" label="活动时间" min-width="160">
          <template #default="scope">
            {{ scope.row.startTime + ' ~ ' + scope.row.stopTime }}
          </template>
        </el-table-column>
        <el-table-column label="砍价状态" min-width="150">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
              v-if="checkPermi(['admin:bargain:update:status'])"
            />
            <span v-else>{{ scope.row.status ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <router-link :to="{
              path: scope.row.status
                  ? '/marketing/bargain/creatBargain/' + scope.row.id + '/info'
                  : '/marketing/bargain/creatBargain/' + scope.row.id,
               }">
              <a type="text" v-hasPermi="['admin:bargain:info']">
                {{ scope.row.status ? '详情' : '编辑' }}
              </a>
            </router-link>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:bargain:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block mb20">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          v-if="checkPermi(['admin:bargain:list'])"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { bargainListApi, bargainDeleteApi, bargainStatusApi, exportBargainApi } from '@/api/marketing';
import { checkPermi, checkRole } from '@/utils/permission';

defineOptions({ name: 'index' });

const { proxy } = getCurrentInstance();

const tableFrom = reactive({
  page: 1,
  limit: 20,
  keywords: '',
  status: null,
});
const listLoading = ref(true);
const tableData = reactive({
  data: [],
  total: 0,
});
const afterData = ref([]);
const multipleTableRef = ref(null);
const isCreate = ref(0);
const dialogVisible = ref(false);
const editData = ref({});

//重置
const handleReset = () => {
  tableFrom.status = null;
  tableFrom.keywords = '';
  getList();
};
//导出
const exportList = () => {
  exportBargainApi({ keywords: tableFrom.keywords, status: tableFrom.status }).then((res) => {
    window.open(res.fileName);
  });
};
// 删除
const handleDelete = (id, idx) => {
  proxy.$modalSure('删除该商品吗')
    .then(function () {
      bargainDeleteApi({ id: id }).then(() => {
        ElMessage.success('删除成功');
        getList();
      });
    })
    .catch(() => {});
};
const onchangeIsShow = (row) => {
  bargainStatusApi({ id: row.id, status: row.status })
    .then(async () => {
      ElMessage.success('修改成功');
      getList();
    })
    .catch(() => {
      row.status = !row.status;
    });
};
// 列表
const getList = (num) => {
  listLoading.value = true;
  tableFrom.page = num ? num : tableFrom.page;
  bargainListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      listLoading.value = false;
    })
    .catch((res) => {
      listLoading.value = false;
    });
};
const pageChange = (page) => {
  tableFrom.page = page;
  getList();
};
const handleSizeChange = (val) => {
  tableFrom.limit = val;
  getList();
};
const add = () => {
  isCreate.value = 0;
  dialogVisible.value = true;
};
const handleClose = () => {
  dialogVisible.value = false;
  editData.value = {};
};

onMounted(() => {
  // 源数据
  var oldData = [
    {
      city_id: 1,
      city_name: '北京',
      city_img: 'http://dfknbdjknvkjsfnvlkjdn.png',
      city_country: '中国',
    },
    {
      city_id: 2,
      city_name: '上海',
      city_img: 'http://wergerbe.png',
      city_country: '中国',
    },
    {
      city_id: 3,
      city_name: '广州',
      city_img: 'http://hrthhr.png',
      city_country: '中国',
    },
    {
      city_id: 4,
      city_name: '西雅图',
      city_img: 'http://frevfd.png',
      city_country: '美国',
    },
    {
      city_id: 5,
      city_name: '纽约',
      city_img: 'http://反而个.png',
      city_country: '美国',
    },
  ];
  // 把源数据先变成目标数据的规则
  var oldDataRule = [];
  oldData.forEach((el) => {
    var oldObj = {
      name: el.city_country,
      citys: [],
    };
    var cityObj = {
      city_name: el.city_name,
      city_img: el.city_img,
      city_id: el.city_id,
    };
    oldObj.citys.push(cityObj);
    oldDataRule.push(oldObj);
  });
  var newData = [];
  var newObj = {};
  oldDataRule.forEach((el, i) => {
    if (!newObj[el.name]) {
      newData.push(el);
      newObj[el.name] = true;
    } else {
      newData.forEach((el) => {
        if (el.name === oldDataRule[i].name) {
          el.citys = el.citys.concat(oldDataRule[i].citys);
          // el.citys = [...el.citys, ...oldDataRule[i].citys]; // es6语法
        }
      });
    }
  });
  getList();
});
</script>

<style scoped>
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}
:deep(.deleteConfirm .el-message-box__btns) {
  padding: 20px 24px 0px !important;
}
</style>
