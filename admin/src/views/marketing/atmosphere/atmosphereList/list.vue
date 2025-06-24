<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form ref="tableFrom" :model="tableFrom" size="small" :inline="true" @submit.native.prevent>
          <el-form-item label="创建时间：" prop="date">
            <el-date-picker
              style="width: 260px"
              v-model="timeVal"
              type="datetimerange"
              range-separator="-"
              value-format="yyyy-MM-dd HH:mm:ss"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="onchangeTime"
              :default-time="['00:00:00', '23:59:59']"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="活动状态：" prop="status">
            <el-select
              v-model="tableFrom.runningStatus"
              placeholder="请选择"
              class="selWidth"
              clearable
              @change="getList(1)"
            >
              <el-option label="未开始" :value="0" />
              <el-option label="进行中" :value="1" />
              <el-option label="已结束" :value="-1" />
            </el-select>
          </el-form-item>
          <el-form-item label="活动名称：">
            <el-input
              v-model="name"
              placeholder="请输入活动名称"
              class="selWidth"
              clearable
              @keyup.enter.native="getList(1)"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button @click="reset('tableFrom')">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14" :body-style="{ padding: '20px' }" :bordered="false" shadow="never">
      <router-link :to="{ path: activityTpye === 1 ? '/marketing/atmosphere/add' : '/marketing/border/add' }">
        <el-button type="primary">{{ activityTpye === 1 ? '添加氛围图' : '添加活动边框' }}</el-button>
      </router-link>
      <el-table :data="tableData.data" style="width: 100%" class="mt20" highlight-current-row>
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="name" label="活动名称" min-width="150" :show-overflow-tooltip="true" />
        <el-table-column min-width="100" label="氛围图">
          <template slot-scope="scope">
            <div class="demo-image__preview acea-row row-middle">
              <el-image
                :preview-src-list="[scope.row.style]"
                style="width: 36px; height: 36px"
                :src="scope.row.style"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="使用范围" min-width="110">
          <template slot-scope="scope">
            <span>{{ scope.row.method | activityMethodFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column min-width="260" label="活动日期">
          <template slot-scope="{ row }">
            <div>{{ row.starttime }} - {{ row.endtime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="活动状态" min-width="90">
          <template slot-scope="{ row }">
            <el-tag class="notStartTag tag-background" v-if="row.runningStatus == 0">未开始</el-tag>
            <el-tag class="doingTag tag-background" v-if="row.runningStatus == 1">进行中</el-tag>
            <el-tag class="endTag tag-background" v-if="row.runningStatus == -1">已结束</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createtime" label="创建时间" min-width="150" />
        <el-table-column label="是否开启" fixed="right" min-width="90">
          <template slot-scope="scope">
            <el-switch
              v-if="checkPermi(['admin:activitystyle:updatestatus'])"
              v-model="scope.row.status"
              :active-value="true"
              :inactive-value="false"
              active-text="开启"
              inactive-text="关闭"
              @click.native="onchangeIsShow(scope.row)"
            />
            <div v-else>{{ scope.row.status ? '开启' : '关闭' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <a @click="onEdit(scope.row)">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)">删除</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          background
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { atuosphereList, atmosphereDelete, atmosphereStatusApi } from '@/api/marketing';
import { checkPermi } from '@/utils/permission';
import { handleDeleteTable } from '@/libs/public'; // 权限判断函数
export default {
  name: 'HoneyList',
  components: {},
  data() {
    return {
      props: { multiple: false, emitPath: false },
      Loading: false,
      dialogVisible: false,
      listLoading: false,
      receiveType: 0,
      id: '',
      categoryList: [],
      labelList: [],
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        name: '',
        runningStatus: '',
        starttime: '',
        endtime: '',
        type: null,
      },
      name: '',
      type: 0,
      isShow: false,
      timeVal: [],
    };
  },
  computed: {
    activityTpye() {
      return this.$route.path.indexOf('border') !== -1 ? 0 : 1;
    },
  },
  mounted() {
    this.getList(1);
  },
  methods: {
    checkPermi,
    onEdit(item) {
      localStorage.setItem('activitystyle', JSON.stringify(item));
      this.$router.push({
        path: this.activityTpye === 1 ? `/marketing/atmosphere/add/${item.id}` : `/marketing/border/add/${item.id}`,
      });
    },
    onchangeTime(e) {
      this.timeVal = e;
      this.tableFrom.starttime = e ? e[0] : '';
      this.tableFrom.endtime = e ? e[1] : '';
    },
    reset(formName) {
      this.timeVal = [];
      this.name = '';
      this.tableFrom = {
        page: 1,
        limit: 20,
        name: '',
        status: '',
        starttime: '',
        endtime: '',
        type: null,
      };
      this.getList('');
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure(`删除活动后将无法恢复，请谨慎操作!`).then(() => {
        atmosphereDelete({ id: id }).then(() => {
          this.$message.success('删除成功');
          handleDeleteTable(this.tableData.data.length, this.tableFrom);
          this.getList(null);
        });
      });
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.type = this.activityTpye;
      this.tableFrom.page = num || this.tableFrom.page;
      this.tableFrom.name = encodeURIComponent(this.name);
      atuosphereList(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList('');
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList('');
    },
    // 修改状态
    onchangeIsShow(row) {
      atmosphereStatusApi({ id: row.id, status: row.status })
        .then(() => {
          this.$message.success('修改成功');
          this.getList('');
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
  },
};
</script>

<style scoped lang="scss">
.modalbox {
  :v-deep.el-dialog {
    min-width: 550px;
  }
}

.container {
  min-width: 821px;
}

.dialogBox {
  box-sizing: border-box;

  .el-pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 25px;
  }
}

::v-deep .el-card__header {
  padding: 18px 20px 0;
}
</style>
