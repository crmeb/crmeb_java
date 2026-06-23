<template>
  <div class="divBox">
    <el-card class="box-card">
      <div class="flex justify-between mb20" v-if="checkPermi(['admin:statistics:product:ranking'])">
        <div class="header_title">商品排行</div>
        <div class="acea-row">
          <div class="mr-20">
            <el-select v-model="value" placeholder="请选择" size="small" class="mr-20">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
            <el-date-picker
              v-model="timeVal"
              size="small"
              type="daterange"
              placement="bottom-end"
              :picker-options="pickerOptions"
              value-format="yyyy-MM-dd"
              format="yyyy-MM-dd"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="onchangeTime"
              style="width: 220px"
              align="right"
            >
            </el-date-picker>
          </div>
          <el-button type="primary" size="small" @click="search">搜索</el-button>
        </div>
      </div>
      <el-table
        v-loading="LogLoading"
        :data="tableData.list"
        stripe
        :default-sort="{ prop: 'pageviews', order: 'descending' }"
        style="width: 100%"
      >
        <el-table-column label="商品图" width="80">
          <template slot-scope="scope">
            <div>
              <el-image
                style="width: 40px; height: 40px"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="商品名称" prop="proName" min-width="200">
          <template slot-scope="scope">
            <div class="text_overflow">{{ scope.row.proName }}</div>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(item, key) in columns1"
          :key="key"
          :label="item.title"
          :prop="item.key"
          :sortable="item.sortable"
          width="150"
        >
        </el-table-column>
      </el-table>
      <div class="flex dialog-footer-inner padd-bottom">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="limit"
          :current-page="currentPage"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChangeLog"
          @current-change="pageChangeLog"
          background
        >
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>
<script>
import { productRankApi } from '@/api/statistic';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  data() {
    return {
      timeVal: [],
      dateLimit: '',
      options: [
        { value: 'pageviews', label: '浏览量' },
        { value: 'collectNum', label: '收藏数' },
        { value: 'addCartNum', label: '加购件数' },
        { value: 'salesNum', label: '销量' },
        { value: 'salesAmount', label: '销售额' },
      ],
      value: 'pageviews',
      columns1: [
        { title: '浏览量', key: 'pageView', sortable: true },
        { title: '收藏数量', key: 'collectNum', sortable: true },
        { title: '加购件数', key: 'addCartNum', sortable: true },
        { title: '销量', key: 'salesNum', sortable: true },
        { title: '销售额', key: 'salesAmount', sortable: true },
      ],
      pickerOptions: this.$timeOptions,
      LogLoading: false,
      tableData: [],
      limit: 20,
      currentPage: 1,
    };
  },
  created() {
    const end = new Date();
    const start = new Date();
    start.setTime(start.setTime(new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate() - 29)));
    this.timeVal = [start, end];
    // this.dataTime = formatDate(start, "yyyy/MM/dd") + "-" + formatDate(end, "yyyy/MM/dd");
  },
  mounted() {
    this.dateLimit = 'lately30';
    this.getRankData();
  },
  methods: {
    checkPermi,
    search() {
      this.getRankData();
    },
    onchangeTime(e) {
      this.timeVal = e;
      this.dateLimit = e ? this.timeVal.join(',') : '';
    },
    getRankData() {
      this.LogLoading = true;
      let data = {
        dateLimit: this.dateLimit,
        limit: this.limit,
        page: this.currentPage,
        sortKey: this.value,
      };
      productRankApi(data)
        .then((res) => {
          this.tableData = res;
          this.LogLoading = false;
        })
        .catch(() => {
          this.LogLoading = false;
        });
    },
    handleSizeChangeLog(val) {
      this.limit = val;
      this.getRankData();
    },
    pageChangeLog(val) {
      this.currentPage = val;
      this.getRankData();
    },
  },
};
</script>

<style scoped lang="scss">
.divBox {
  padding-top: 14px;
}
.flex {
  display: flex;
}
.flex-wrap {
  flex-wrap: wrap;
}
.mb20 {
  margin-bottom: 20px;
}
.mb30 {
  margin-bottom: 30px;
}
.mr-20 {
  margin-right: 20px;
}
.justify-between {
  justify-content: space-between;
}
.justify-center {
  justify-content: center;
}

.header_title {
  font-size: 16px;
  font-family: PingFangSC-Semibold, PingFang SC;
  font-weight: 600;
  color: #000000;
  padding-left: 8px;
  position: relative;
  &::before {
    position: absolute;
    content: '';
    width: 2px;
    height: 18px;
    background: #1890ff;
    top: 0;
    left: 0;
  }
}

.iconfont {
  font-size: 16px;
  color: #fff;
}
.text_overflow {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.padd-bottom {
  padding-bottom: 20px;
}
</style>
