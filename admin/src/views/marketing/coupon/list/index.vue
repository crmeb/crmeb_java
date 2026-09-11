<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <div class="demo-input-suffix acea-row">
          <span class="seachTiele">状态：</span>
          <el-select
            v-model="tableFrom.status"
            placeholder="请选择"
            class="filter-item selWidth mr30"
            @change="seachList"
            clearable
          >
            <el-option label="未开启" :value="0" />
            <el-option label="开启" :value="1" />
          </el-select>
          <span class="seachTiele">优惠券名称：</span>
          <el-input v-model="tableFrom.name" placeholder="请输入优惠券名称" class="selWidth" clearable></el-input>
          <el-button class="ml30" type="primary" @click="seachList">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <router-link :to="{ path: '/marketing/coupon/list/save' }">
            <el-button type="primary" v-hasPermi="['admin:coupon:save']">添加优惠劵</el-button>
          </router-link>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" label="名称" min-width="180" />
        <el-table-column label="类型" min-width="80">
          <template #default="{ row }">
            <span>{{ $filters.couponUserTypeFilter(row.useType) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="money" label="面值" min-width="100" />
        <el-table-column prop="name" label="领取方式" min-width="100">
          <template #default="{ row }">
            <span>{{ $filters.couponTypeFilter(row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="260" label="领取日期">
          <template #default="{ row }">
            <div v-if="row.receiveEndTime">{{ row.receiveStartTime }} - {{ row.receiveEndTime }}</div>
            <span v-else>不限时</span>
          </template>
        </el-table-column>
        <el-table-column min-width="260" label="使用时间">
          <template #default="{ row }">
            <div v-if="row.day">{{ row.day }}天</div>
            <span v-else> {{ row.useStartTime }} - {{ row.useEndTime }} </span>
          </template>
        </el-table-column>
        <el-table-column min-width="100" label="发布数量">
          <template #default="{ row }">
            <span v-if="!row.isLimited">不限量</span>
            <div v-else>
              <span class="fa">发布：{{ row.total }}</span>
              <span class="sheng">剩余：{{ row.lastTotal }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="是否开启" min-width="100">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:coupon:update:status'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @click="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.status ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <a @click="receive(scope.row)" v-hasPermi="['admin:coupon:user:list']">领取记录</a>
            <el-divider direction="vertical"></el-divider>
            <template v-if="scope.row.status">
              <router-link :to="{ path: '/marketing/coupon/list/save', query: { copyId: scope.row.id } }">
                <a v-hasPermi="['admin:coupon:info']">复制</a>
              </router-link>
              <el-divider direction="vertical"></el-divider>
            </template>
            <a @click="handleDelMenu(scope.row)" v-hasPermi="['admin:coupon:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          background
        />
      </div>
    </el-card>
    <!--领取记录-->
    <el-dialog title="领取记录" v-model="dialogVisible" width="700px" :before-close="handleClose">
      <el-table v-loading="Loading" :data="issueData.data" style="width: 100%">
        <el-table-column prop="nickname" label="用户名" min-width="120" />
        <el-table-column label="用户头像" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]" preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="领取时间" min-width="180" />
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFromIssue.limit"
          :current-page="tableFromIssue.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="issueData.total"
          @size-change="handleSizeChangeIssue"
          @current-change="pageChangeIssue"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { marketingListApi, couponIssueStatusApi, couponUserListApi, couponDeleteApi } from '@/api/marketing';
import { roterPre } from '@/settings';
import { checkPermi } from '@/utils/permission'; // 权限判断函数

defineOptions({ name: 'CouponList' });

const { proxy } = getCurrentInstance();

const Loading = ref(false);
const dialogVisible = ref(false);
const roterPreRef = roterPre;
const listLoading = ref(true);
const tableData = reactive({
  data: [],
  total: 0,
});
const tableFrom = reactive({
  page: 1,
  limit: 20,
  status: '',
  name: '',
});
const tableFromIssue = reactive({
  page: 1,
  limit: 10,
  couponId: '',
});
const issueData = reactive({
  data: [],
  total: 0,
});

//重置
const handleReset = () => {
  tableFrom.status = '';
  tableFrom.name = '';
  getList();
};
const seachList = () => {
  tableFrom.page = 1;
  getList();
};
const handleClose = () => {
  dialogVisible.value = false;
};
// 领取记录
const receive = (row) => {
  dialogVisible.value = true;
  tableFromIssue.couponId = row.id;
  getIssueList();
};
// 列表
const getIssueList = () => {
  Loading.value = true;
  couponUserListApi(tableFromIssue)
    .then((res) => {
      issueData.data = res.list;
      issueData.total = res.total;
      Loading.value = false;
    })
    .catch((res) => {
      Loading.value = false;
      ElMessage.error(res.message);
    });
};
const pageChangeIssue = (page) => {
  tableFromIssue.page = page;
  getIssueList();
};
const handleSizeChangeIssue = (val) => {
  tableFromIssue.limit = val;
  getIssueList();
};
// 列表
const getList = () => {
  listLoading.value = true;
  marketingListApi(tableFrom)
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
// 修改状态
const onchangeIsShow = (row) => {
  couponIssueStatusApi({ id: row.id, status: row.status })
    .then(() => {
      ElMessage.success('修改成功');
      getList();
    })
    .catch(() => {
      row.status = !row.status;
    });
};
const handleDelMenu = (rowData) => {
  proxy.$modalSure('删除当前数据?').then(() => {
    couponDeleteApi({ id: rowData.id }).then((data) => {
      ElMessage.success('删除成功');
      tableFrom.page = 1;
      getList();
    });
  });
};

onMounted(() => {
  getList();
});
</script>

<style scoped lang="scss">
:deep(.el-input--medium .el-input__inner) {
  height: 40px;
  line-height: 40px;
}

.fa {
  color: #0a6aa1;
  display: block;
}

.sheng {
  color: #ff0000;
  display: block;
}
.padding-add {
  padding-bottom: 20px;
}
.block {
  margin-bottom: 20px;
}
.seachTiele {
  line-height: 31px !important;
  margin-right: 2px;
}
</style>
