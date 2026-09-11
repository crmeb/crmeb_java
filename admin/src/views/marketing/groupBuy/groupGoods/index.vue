<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="拼团状态：">
            <el-select
              v-model="tableFrom.isShow"
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
            <el-input
              v-model="tableFrom.keywords"
              placeholder="请输入商品名称、ID"
              class="selWidth"
              clearable
            ></el-input>
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
          <router-link :to="{ path: '/marketing/groupBuy/creatGroup' }">
            <el-button type="primary" class="mr14" v-hasPermi="['admin:combination:save']">添加拼团商品</el-button>
          </router-link>
          <el-button class="mr10" @click="exportList" v-hasPermi="['admin:export:excel:combiantion']">导出</el-button>
        </div>
      </template>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" ref="multipleTableRef" class="table">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="拼团图片" min-width="80">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]"
                preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="拼团名称" prop="title" min-width="300" :show-overflow-tooltip="true"> </el-table-column>
        <el-table-column label="原价" prop="otPrice" min-width="100" />
        <el-table-column label="拼团价" prop="price" min-width="100" />
        <el-table-column label="拼团人数" prop="countPeople" min-width="100" />
        <el-table-column label="参与人数" prop="countPeopleAll" min-width="100" />
        <el-table-column label="成团数量" prop="countPeoplePink" min-width="100" />
        <el-table-column label="限量" min-width="100" prop="quotaShow" />
        <el-table-column label="限量剩余" prop="remainingQuota" min-width="100" />
        <el-table-column prop="stopTime" label="结束时间" min-width="130">
          <template #default="scope">
            <span>{{ scope.row.stopTimeStr }}</span>
          </template>
        </el-table-column>
        <el-table-column label="拼团状态" min-width="80" fixed="right">
          <template #default="scope">
            <el-switch
              v-if="checkPermi(['admin:combination:update:status'])"
              v-model="scope.row.isShow"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
            <span v-else>{{ scope.row.isShow ? '开启' : '关闭' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="scope">
            <router-link
              :to="{
                path: scope.row.isShow
                  ? '/marketing/groupBuy/creatGroup/' + scope.row.id + '/info'
                  : '/marketing/groupBuy/creatGroup/' + scope.row.id
              }"
            >
              <a v-hasPermi="['admin:combination:info']">{{ scope.row.isShow ? '详情' : '编辑' }}</a>
            </router-link>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:combination:delete']">删除</a>
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
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import { ElMessage } from '@/utils/elementPlusFeedback'
import { combinationListApi, combinationDeleteApi, combinationStatusApi, exportcombiantionApi } from '@/api/marketing'
import { formatDates } from '@/utils/index'
import { checkPermi } from '@/utils/permission' // 权限判断函数

defineOptions({ name: 'index' })

const { proxy } = getCurrentInstance()

const formatDate = (time) => {
  if (time !== 0) {
    const date = new Date(time)
    return formatDates(date, 'YYYY-MM-DD hh:mm')
  }
}

const tableFrom = reactive({
  page: 1,
  limit: 20,
  keywords: '',
  isShow: ''
})
const listLoading = ref(true)
const tableData = reactive({
  data: [],
  total: 0
})
const multipleTableRef = ref(null)

//重置
const handleReset = () => {
  tableFrom.isShow = ''
  tableFrom.keywords = ''
  getList()
}
//导出
const exportList = () => {
  exportcombiantionApi({ keywords: tableFrom.keywords, isShow: tableFrom.isShow }).then((res) => {
    window.open(res.fileName)
  })
}
// 删除
const handleDelete = (id, idx) => {
  proxy.$modalSure('永久删除该商品').then(() => {
    combinationDeleteApi({ id: id }).then(() => {
      ElMessage.success('删除成功')
      if (tableData.data.length === 1 && tableFrom.page > 1) tableFrom.page = tableFrom.page - 1
      getList()
    })
  })
}
const onchangeIsShow = (row) => {
  combinationStatusApi({ id: row.id, isShow: row.isShow })
    .then(async () => {
      ElMessage.success('修改成功')
      getList()
    })
    .catch(() => {
      row.isShow = !row.isShow
    })
}
// 列表
const getList = (num) => {
  listLoading.value = true
  tableFrom.page = num ? num : tableFrom.page
  combinationListApi(tableFrom)
    .then((res) => {
      tableData.data = res.list
      tableData.total = res.total
      listLoading.value = false
    })
    .catch((res) => {
      listLoading.value = false
    })
}
const pageChange = (page) => {
  tableFrom.page = page
  getList()
}
const handleSizeChange = (val) => {
  tableFrom.limit = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}

</style>
