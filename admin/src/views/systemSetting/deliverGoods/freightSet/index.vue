<template>
  <div class="divBox">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form ref="form" inline :model="form">
          <el-form-item label="模板名称：">
            <el-input v-model="form.keywords" placeholder="请输入模板名称" class="selWidth" size="small" clearable>
            </el-input>
          </el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <el-button type="primary" @click="handleSubmit()" v-hasPermi="['admin:shipping:templates:save']"
          >添加运费模板</el-button
        >
      </div>
      <el-table v-loading="loading" :data="tableData.list" style="width: 100%" size="mini">
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column label="模板名称" min-width="180" prop="name" />
        <el-table-column min-width="100" label="计费方式" prop="type">
          <template slot-scope="{ row }">
            <p>{{ row.type | typeFilter }}</p>
          </template>
        </el-table-column>
        <el-table-column min-width="100" label="包邮方式" prop="appoint">
          <template slot-scope="{ row }">
            <p>{{ row.appoint | statusFilter }}</p>
          </template>
        </el-table-column>
        <el-table-column label="排序" min-width="100" prop="sort" />
        <el-table-column label="添加时间" min-width="150" prop="createTime" />
        <el-table-column prop="address" fixed="right" width="120" label="操作">
          <template slot-scope="scope">
            <a @click="bindEdit(scope.row)" v-hasPermi="['admin:shipping:templates:info']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="bindDelete(scope.row)" v-hasPermi="['admin:shipping:templates:delete']">删除</a>
          </template>
        </el-table-column>
      </el-table>
      <div class="block-pagination">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="tableData.limit"
          :current-page="tableData.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @current-change="pageChange"
          @size-change="handleSizeChange"
          background
        />
      </div>
    </el-card>
    <CreatTemplates ref="addTemplates" @getList="getList" />
  </div>
</template>

<script>
import CreatTemplates from './creatTemplates';
import * as logistics from '@/api/logistics.js';
export default {
  name: 'ShippingTemplates',
  filters: {
    statusFilter(status) {
      const statusMap = {
        0: '全国包邮',
        1: '部分包邮',
        2: '自定义',
      };
      return statusMap[status];
    },
    typeFilter(status) {
      const statusMap = {
        0: '无',
        1: '按件数',
        2: '按重量',
        3: '按体积',
      };
      return statusMap[status];
    },
  },
  components: { CreatTemplates },
  data() {
    return {
      isShow: false,
      dialogVisible: false,
      form: {
        keywords: '',
      },
      tableData: '',
      page: 1,
      limit: 20,
      loading: false,
    };
  },
  created() {
    this.getDataList();
  },
  methods: {
    // 添加
    handleSubmit() {
      this.$refs.addTemplates.dialogVisible = true;
      if (!sessionStorage.getItem('cityList')) this.$refs.addTemplates.getCityList();
      this.$refs.addTemplates.changType(0);
    },
    handleSearch() {
      this.page = 1;
      this.getDataList();
    },
    // 分页
    pageChange(e) {
      this.page = e;
      this.getDataList();
    },
    handleSizeChange(e) {
      this.limit = e;
      this.getDataList();
    },
    // 数据列表
    getDataList() {
      this.loading = true;
      logistics
        .shippingTemplatesList({
          keywords: this.form.keywords,
          page: this.page,
          limit: this.limit,
        })
        .then((res) => {
          this.loading = false;
          this.tableData = res;
        });
    },
    // 编辑
    bindEdit(item) {
      if (!sessionStorage.getItem('cityList')) this.$refs.addTemplates.getCityList();
      this.$refs.addTemplates.getInfo(item.id, item.appoint);
      this.$refs.addTemplates.changType(1);
    },
    // 删除
    bindDelete(item) {
      this.$modalSure().then(() => {
        logistics.shippingDetete({ id: item.id }).then((res) => {
          this.$message.success('删除成功');
          this.getDataList();
        });
      });
      // logistics.shippingDetete()
    },
    getList() {
      this.getDataList();
    },
  },
};
</script>

<style scoped lang="scss">
.selWidth {
  width: 350px;
}
</style>
