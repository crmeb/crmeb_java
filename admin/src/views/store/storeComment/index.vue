<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form :inline="true">
          <el-form-item label="时间选择：">
            <el-date-picker
              @change="onchangeTime"
              v-model="timeVal"
              value-format="yyyy-MM-dd"
              format="yyyy-MM-dd"
              size="small"
              type="daterange"
              placement="bottom-end"
              placeholder="自定义时间"
              class="selWidth"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="评价状态：" class="mr10">
            <el-select
              v-model="tableFrom.isReply"
              placeholder="请选择评价状态"
              @change="seachList"
              size="small"
              class="selWidth"
              clearable
            >
              <el-option label="已回复" value="1"></el-option>
              <el-option label="未回复" value="0"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="商品搜索：" class="mr10">
            <el-input
              v-model="tableFrom.productSearch"
              placeholder="请输入商品名称"
              class="selWidth"
              size="small"
              clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="用户名称：">
            <el-input v-model="tableFrom.nickname" placeholder="请输入用户名称" class="selWidth" size="small" clearable>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="seachList" size="small">搜索</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <el-button type="primary" @click="handleAdd" v-hasPermi="['admin:product:reply:save']">添加虚拟评论</el-button>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%">
        <el-table-column prop="id" label="ID" width="50" />
        <el-table-column label="商品信息" min-width="400" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div class="demo-image__preview acea-row row-middle" v-if="scope.row.storeProduct">
              <el-image
                style="width: 30px; height: 30px"
                :src="scope.row.storeProduct.image"
                :preview-src-list="[scope.row.storeProduct.image]"
                class="mr10"
              />
              <div class="info line1">{{ scope.row.storeProduct.storeName }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户名称" min-width="100" />
        <el-table-column prop="productScore" label="商品评分" min-width="90" />
        <el-table-column prop="serviceScore" label="服务评分" min-width="90" />
        <el-table-column label="评价内容" min-width="210">
          <template slot-scope="scope">
            <div class="mb5 content_font">{{ scope.row.comment || '-' }}</div>
            <template v-if="scope.row.pics.length && scope.row.pics[0]">
              <div class="demo-image__preview">
                <el-image
                  :src="item"
                  class="mr5"
                  :preview-src-list="[item]"
                  v-for="(item, index) in scope.row.pics"
                  :key="index"
                />
              </div>
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="merchantReplyContent" label="回复内容" min-width="250">
          <template slot-scope="scope">
            <div>{{ scope.row.merchantReplyContent || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="评价时间" min-width="120">
          <template slot-scope="scope">
            <span> {{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <a @click="reply(scope.row.id)" v-hasPermi="['admin:product:reply:comment']">回复</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['admin:product:reply:delete']">删除</a>
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
      <el-dialog title="添加评论" :visible.sync="dialogVisible" width="700px" :before-close="handleClose">
        <creat-comment :key="timer" @getList="seachList"></creat-comment>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import creatComment from './creatComment.vue';
import { categoryApi, replyListApi, replyDeleteApi, replyCommentApi } from '@/api/store';
import { formatDates } from '@/utils/index';
import { userListApi } from '@/api/user';
export default {
  name: 'StoreComment',
  filters: {
    formatDate(time) {
      if (time !== 0) {
        const date = new Date(time * 1000);
        return formatDates(date, 'yyyy-MM-dd hh:mm');
      }
    },
  },
  components: { creatComment },
  data() {
    return {
      merCateList: [],
      props: {
        children: 'child',
        label: 'name',
        value: 'id',
        emitPath: false,
      },
      fromList: this.$constants.fromList,
      tableData: {
        data: [],
        total: 0,
      },
      listLoading: true,
      tableFrom: {
        page: 1,
        limit: 20,
        isReply: '',
        dateLimit: '',
        // uid: '',
        nickname: '',
        productSearch: '',
        isDel: false,
      },
      timeVal: [],
      loading: false,
      uids: [],
      options: [],
      dialogVisible: false,
      timer: '',
    };
  },
  watch: {
    $route(to, from) {
      this.getList();
      this.getCategorySelect();
    },
  },
  mounted() {
    // this.getLstFilterApi()
    this.getList();
    this.getCategorySelect();
  },
  methods: {
    //重置
    handleReset() {
      this.tableFrom.isReply = '';
      this.tableFrom.nickname = '';
      this.tableFrom.productSearch = '';
      this.tableFrom.dateLimit = '';
      this.timeVal = [];
      this.getList();
    },
    remoteMethod(query) {
      if (query !== '') {
        this.loading = true;
        setTimeout(() => {
          this.loading = false;
          userListApi({ keywords: query, page: 1, limit: 10 }).then((res) => {
            this.options = res.list;
          });
        }, 200);
      } else {
        this.options = [];
      }
    },
    seachList() {
      this.dialogVisible = false;
      this.tableFrom.page = 1;
      this.getList();
    },
    // 回复
    reply(id) {
      this.$modalPrompt('textarea', '回复', null, '回复内容').then((V) => {
        replyCommentApi({
          ids: id,
          merchantReplyContent: V,
        }).then((res) => {
          this.$message({
            type: 'success',
            message: '回复成功',
          });
          this.getList();
        });
      });
    },
    // 选择时间
    selectChange(tab) {
      this.timeVal = [];
      this.tableFrom.page = 1;
      this.getList();
    },
    // 商户分类；
    getCategorySelect() {
      categoryApi({ status: -1, type: 1 })
        .then((res) => {
          this.merCateList = res;
        })
        .catch((res) => {
          this.$message.error(res.message);
        });
    },
    handleAdd() {
      this.dialogVisible = true;
      this.timer = new Date().getTime();
    },
    handleClose() {
      this.dialogVisible = false;
    },
    // 具体日期
    onchangeTime(e) {
      this.timeVal = e;
      this.tableFrom.dateLimit = e ? this.timeVal.join(',') : '';
      this.tableFrom.page = 1;
      this.getList();
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        replyDeleteApi(id).then(() => {
          this.$message.success('删除成功');
          if (this.tableData.data.length === 1 && this.tableFrom.page > 1)
            this.tableFrom.page = this.tableFrom.page - 1;
          this.getList();
        });
      });
    },
    // 列表
    getList() {
      this.listLoading = true;
      this.tableFrom.uid = this.uids.join(',');
      replyListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.listLoading = false;
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
  },
};
</script>

<style scoped lang="scss">
.table {
  .el-image {
    width: auto !important;
    height: 30px !important;
  }

  ::v-deepel-image__inner,
  .el-image__placeholder,
  .el-image__error {
    width: auto !important;
  }
}

.info {
  width: 63%;
}
</style>
