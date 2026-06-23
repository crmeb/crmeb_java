<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form inline>
          <el-form-item label="商品ID：">
            <el-input v-model="tableFrom.proId" placeholder="请输入商品ID" class="selWidth"></el-input>
          </el-form-item>
          <el-form-item label="商品名称：">
            <el-input v-model="tableFrom.search" placeholder="请输入商品名称" class="selWidth" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getList(1)">搜索</el-button>
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <router-link :to="{ path: '/marketing/videoChannel/creatVideoChannel' }">
          <el-button size="small" type="primary" class="mr10" v-hasPermi="['admin:pay:component:product:add']"
            >添加视频号商品</el-button
          >
        </router-link>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="mini" ref="multipleTable">
        <el-table-column prop="productId" label="商品ID" min-width="80" />
        <el-table-column label="名称" prop="title" min-width="300">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="right" :open-delay="800">
              <div class="text_overflow" slot="reference">{{ scope.row.title }}</div>
              <div class="pup_card">{{ scope.row.title }}</div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="商品图片" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="JSON.parse(scope.row.headImg)[0]"
                :preview-src-list="JSON.parse(scope.row.headImg)"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="componentProductId" label="视频号商品ID" min-width="150" />
        <!--<el-table-column-->
        <!--label="品牌ID"-->
        <!--min-width="150"-->
        <!--prop="brandId"-->
        <!--/>-->
        <el-table-column label="类目" min-width="150" prop="thirdCatName" />
        <el-table-column label="获得积分" prop="giveIntegral" min-width="100" />
        <el-table-column prop="sales" label="销量" min-width="90" />
        <el-table-column prop="stock" label="库存" min-width="90" />
        <el-table-column prop="sales" label="状态" min-width="90">
          <template slot-scope="scope">
            <span>{{ scope.row.editStatus | editStatusFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="addTime" min-width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <router-link
              v-if="scope.row.editStatus !== 2"
              :to="{ path: '/marketing/videoChannel/creatVideoChannel/' + scope.row.id }"
            >
              <a  v-hasPermi="['admin:pay:component:product:draft:info']"
                >编辑</a
              >
            </router-link>
            <a v-else  style="color: #99a9bf" :disabled="true">编辑</a>
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

<script>
import { draftListApi, seckillStoreUpdateApi, seckillStoreStatusApi } from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'videoList',
  data() {
    return {
      listLoading: true,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 20,
        proId: '',
        search: '',
      },
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    checkPermi,
    //重置
    handleReset() {
      this.tableFrom.proId = '';
      this.tableFrom.search = '';
      this.getList();
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      draftListApi(this.tableFrom)
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
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
  },
};
</script>

<style scoped>
.el-table__body {
  width: 100%;
  table-layout: fixed !important;
}
.text_overflow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 400px;
}
.pup_card {
  width: 200px;
  border-radius: 5px;
  padding: 5px;
  box-sizing: border-box;
  font-size: 12px;
  line-height: 16px;
}
</style>
