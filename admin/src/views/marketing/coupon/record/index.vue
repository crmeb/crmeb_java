<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form :inline="true">
          <el-form-item label="使用状态：" class="mr10">
            <el-select
              v-model="tableFromIssue.status"
              placeholder="请选择使用状态"
              clearable
              class="selWidth"
              @change="seachList"
            >
              <el-option label="已使用" value="1" />
              <el-option label="未使用" value="0" />
              <el-option label="已过期" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="用户搜索：">
            <UserSearchInput ref="userSearchInput" v-model="tableFromIssue" />
          </el-form-item>
          <!-- <el-form-item label="领取人：">
            <el-select
              v-model="tableFromIssue.uid"
              class="selWidth"
              reserve-keyword
              remote
              filterable
              :remote-method="remoteMethod"
              :loading="loading"
              placeholder="请输入领取人"
              clearable
              @change="seachList"
            >
              <el-option v-for="item in options" :key="item.uid" :label="item.nickname" :value="item.uid"> </el-option>
            </el-select>
          </el-form-item> -->
          <el-form-item label="优惠劵：" class="mr10">
            <el-input v-model="tableFromIssue.name" placeholder="请输入优惠劵" class="selWidth" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="seachList">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <el-table v-loading="Loading" :data="issueData.data" style="width: 100%">
        <el-table-column prop="couponId" label="优惠券ID" min-width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150" />
        <el-table-column prop="nickname" label="领取人" min-width="130" />
        <el-table-column prop="money" label="面值" min-width="100" />
        <el-table-column prop="minPrice" label="最低消费额" min-width="120" />
        <el-table-column prop="startTime" label="开始使用时间" min-width="150" />
        <el-table-column prop="endTime" label="结束使用时间" min-width="150" />
        <el-table-column label="获取方式" min-width="150">
          <template #default="scope">
            <span>{{ failFilter(scope.row.type) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="is_fail" label="是否可用" min-width="100">
          <template #default="scope">
            <i v-if="scope.row.status === 0" class="el-icon-check" style="font-size: 14px; color: #0092dc" />
            <i v-else class="el-icon-close" style="font-size: 14px; color: #ed5565" />
          </template>
        </el-table-column>
        <el-table-column label="使用状态" min-width="100">
          <template #default="scope">
            <span>{{ statusFilter(scope.row.status) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFromIssue.limit"
          :current-page="tableFromIssue.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="issueData.total"
          @size-change="handleSizeChangeIssue"
          @current-change="pageChangeIssue"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { couponUserListApi } from '@/api/marketing';
import { roterPre } from '@/settings';
import { userListApi } from '@/api/user';

defineOptions({ name: 'CouponUser' });

const { proxy } = getCurrentInstance();

const failFilter = (status) => {
  const statusMap = {
    receive: '自己领取',
    send: '后台发送',
    give: '满赠',
    new: '新人',
    buy: '买赠送',
  };
  return statusMap[status];
};
const statusFilter = (status) => {
  const statusMap = {
    0: '未使用',
    1: '已使用',
    2: '已过期',
  };
  return statusMap[status];
};

const Loading = ref(false);
const roterPreRef = roterPre;
const imgList = ref([]);
const tableFromIssue = reactive({
  page: 1,
  limit: 20,
  uid: '',
  name: '',
  status: '',
  content: '',
  searchType: 'all',
});
const issueData = reactive({
  data: [],
  total: 0,
});
const loading = ref(false);
const options = ref([]);
const userSearchInput = ref(null);

//重置
const handleReset = () => {
  tableFromIssue.status = '';
  tableFromIssue.name = '';
  tableFromIssue.uid = '';
  tableFromIssue.content = '';
  tableFromIssue.searchType = 'all';
  getIssueList();
};
const remoteMethod = (query) => {
  if (query !== '') {
    loading.value = true;
    setTimeout(() => {
      loading.value = false;
      userListApi({ keywords: query, page: 1, limit: 10 }).then((res) => {
        options.value = res.list;
      });
    }, 200);
  } else {
    options.value = [];
  }
};
const seachList = () => {
  tableFromIssue.page = 1;
  getIssueList();
};
// 列表
const getIssueList = () => {
  Loading.value = true;
  couponUserListApi(tableFromIssue)
    .then((res) => {
      issueData.data = res.list;
      issueData.total = res.total;
      // this.issueData.data.map((item) => {
      //   this.imgList.push(item.user.avatar)
      // })
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

onMounted(() => {
  getIssueList();
});
</script>

<style scoped lang="scss">
.seachTiele {
  line-height: 35px;
}
.fa {
  color: #0a6aa1;
  display: block;
}
.sheng {
  color: #ff0000;
  display: block;
}
</style>
