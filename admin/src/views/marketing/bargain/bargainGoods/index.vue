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
            <el-button size="small" @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <div slot="header" class="clearfix">
        <router-link :to="{ path: '/marketing/bargain/creatBargain' }">
          <el-button type="primary" class="mr14" v-hasPermi="['admin:bargain:save']">添加砍价商品</el-button>
        </router-link>
        <el-button class="mr10" @click="exportList" v-hasPermi="['admin:export:excel:bargain']">导出</el-button>
      </div>
      <el-table v-loading="listLoading" :data="tableData.data" style="width: 100%" size="mini" ref="multipleTable">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column label="砍价图片" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="砍价名称" prop="title" min-width="300">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="right" :open-delay="800">
              <div class="text_overflow" slot="reference">{{ scope.row.title }}</div>
              <div class="pup_card">{{ scope.row.title }}</div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="砍价价格" prop="price" min-width="100" />
        <el-table-column label="最低价" prop="minPrice" min-width="100" />
        <el-table-column label="参与人数" prop="countPeopleAll" min-width="100" />
        <el-table-column label="帮忙砍价人数" prop="countPeopleHelp" min-width="100" />
        <el-table-column label="砍价成功人数" prop="countPeopleSuccess" min-width="100" />
        <el-table-column label="限量" min-width="100" prop="quotaShow" />
        <el-table-column label="限量剩余" prop="surplusQuota" min-width="100" />
        <el-table-column prop="stopTime" label="活动时间" min-width="160">
          <template slot-scope="scope">
            {{ scope.row.startTime + ' ~ ' + scope.row.stopTime }}
          </template>
        </el-table-column>
        <el-table-column label="砍价状态" min-width="150">
          <template slot-scope="scope">
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
          <template slot-scope="scope">
            <router-link :to="{ path: '/marketing/bargain/creatBargain/' + scope.row.id }">
              <a type="text" size="small" v-hasPermi="['admin:bargain:info']">编辑</a>
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

<script>
import { bargainListApi, bargainDeleteApi, bargainStatusApi, exportBargainApi } from '@/api/marketing';
import { checkPermi, checkRole } from '@/utils/permission';
export default {
  name: 'index',
  data() {
    return {
      tableFrom: {
        page: 1,
        limit: 20,
        keywords: '',
        status: null,
      },
      listLoading: true,
      tableData: {
        data: [],
        total: 0,
      },
      afterData: [],
    };
  },
  mounted() {
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
    this.getList();
  },
  methods: {
    checkPermi, //权限控制
    //重置
    handleReset() {
      this.tableFrom.status = null;
      this.tableFrom.keywords = '';
      this.getList();
    },
    //导出
    exportList() {
      exportBargainApi({ keywords: this.tableFrom.keywords, status: this.tableFrom.status }).then((res) => {
        window.open(res.fileName);
      });
    },
    // 删除
    handleDelete(id, idx) {
      let that = this;
      this.$modalSure('删除该商品吗')
        .then(function () {
          bargainDeleteApi({ id: id }).then(() => {
            that.$message.success('删除成功');
            that.getList();
          });
        })
        .catch(() => {});
    },
    onchangeIsShow(row) {
      bargainStatusApi({ id: row.id, status: row.status })
        .then(async () => {
          this.$message.success('修改成功');
          this.getList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    // 列表
    getList(num) {
      this.listLoading = true;
      this.tableFrom.page = num ? num : this.tableFrom.page;
      bargainListApi(this.tableFrom)
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
    add() {
      this.isCreate = 0;
      this.dialogVisible = true;
    },
    handleClose() {
      this.dialogVisible = false;
      this.editData = {};
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
::v-deep .deleteConfirm .el-message-box__btns {
  padding: 20px 24px 0px !important;
}
</style>
