<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline>
            <el-form-item label="是否显示：">
              <el-select v-model="tableFrom.status" placeholder="请选择" class="filter-item selWidth" @change="getList(1)" clearable>
                <el-option label="关闭" :value="0" />
                <el-option label="开启" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="配置名称：">
              <el-select v-model="tableFrom.timeId" placeholder="请选择" class="selWidth" @change="getList(1)" clearable>
                <el-option
                  v-for="item in seckillTime"
                  :key="item.id"
                  :label="item.name +' - ' + item.time"
                  :value="item.id">
                </el-option>
              </el-select>
              <!--<el-input v-model="tableFrom.name" placeholder="请输入秒杀名称" class="selWidth">-->
                <!--<el-button slot="append" icon="el-icon-search" @click="getList(1)"/>-->
              <!--</el-input>-->
            </el-form-item>
            <el-form-item label="商品搜索：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入商品ID/名称" class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search" @click="getList(1)"/>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <router-link :to=" { path:'/marketing/seckill/creatSeckill/creat' }">
          <el-button size="small" type="primary" class="mr10" v-hasPermi="['admin:seckill:save']">添加秒杀商品</el-button>
        </router-link>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"
        size="mini"
        ref="multipleTable"
        :header-cell-style=" {fontWeight:'bold'}"
      >
        <el-table-column
          prop="id"
          label="ID"
          min-width="50"
        />
        <el-table-column
          label="配置"
          min-width="160"
        >
          <template slot-scope="scope">
            <div>{{scope.row.storeSeckillManagerResponse ? scope.row.storeSeckillManagerResponse.name : '-'}}</div>
            <div>{{scope.row.startTime + ' - ' + scope.row.stopTime}}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="秒杀时段"
          min-width="130"
        >
          <template slot-scope="scope">
            <div>{{ scope.row.storeSeckillManagerResponse ? scope.row.storeSeckillManagerResponse.time.split(',').join(' - ') : '-'}}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品图片" min-width="80">
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
        <el-table-column label="商品标题" prop="title" min-width="300" :show-overflow-tooltip="true">
        </el-table-column>
        <el-table-column label="活动简介" min-width="300" prop="info" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column
          label="原价"
          prop="otPrice"
          min-width="100"
        />
        <el-table-column
          label="秒杀价"
          min-width="100"
          prop="price"
        />
        <el-table-column
          label="限量"
          prop="quotaShow"
          min-width="80"
        />
        <el-table-column
          label="限量剩余"
          min-width="80"
          prop="quota"
        />
        <el-table-column
          label="秒杀状态"
          min-width="100"
          prop="statusName"
        />
        <el-table-column
          label="创建时间"
          prop="createTime"
          min-width="150"
        />
        <el-table-column
          label="状态"
          min-width="80"
          fixed="right"
        >
          <template slot-scope="scope" v-if="checkPermi(['admin:seckill:update:status'])">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="开启"
              inactive-text="关闭"
              @change="onchangeIsShow(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="120" fixed="right" align="center">
          <template slot-scope="scope">
            <router-link :to="{ path:'/marketing/seckill/creatSeckill/updeta/' + scope.row.productId + '/' + scope.row.id}">
              <el-button type="text" size="small" v-hasPermi="['admin:seckill:info']">编辑</el-button>
            </router-link>
            <el-button v-if="scope.row.killStatus !== 2 " 
            type="text" size="small" 
            @click="handleDelete(scope.row.id, scope.$index)"  
            class="mr10"
            v-hasPermi="['admin:seckill:delete']"
            >删除</el-button>
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
        />
      </div>
    </el-card>
  </div>
</template>

<script>
  import { seckillStoreListApi, seckillStoreDeleteApi, seckillStoreUpdateApi, seckillStoreStatusApi } from '@/api/marketing'
  import { getSeckillList } from '@/libs/public'
  import { checkPermi } from "@/utils/permission"; // 权限判断函数
  export default {
    name: "SeckillList",
    data() {
      return {
        listLoading: true,
        tableData: {
          data: [],
          total: 0
        },
        tableFrom: {
          page: 1,
          limit: 20,
          timeId: '',
          status: '',
          keywords: ''
        },
        seckillTime: []
      }
    },
    mounted() {
      getSeckillList().then((res) => {
        this.seckillTime = res.list
      })
      this.tableFrom.timeId = Number(this.$route.params.timeId) || ''
      this.getList()
    },
    methods: {
      checkPermi,
      // 订单删除
      handleDelete(id, idx) {
        this.$modalSure().then(() => {
          seckillStoreDeleteApi({ id: id }).then(() => {
            this.$message.success('删除成功')
            this.tableData.data.splice(idx, 1)
          })
        })
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.tableFrom.page = num ? num : this.tableFrom.page;
        seckillStoreListApi(this.tableFrom).then(res => {
          this.tableData.data = res.list
          this.tableData.total = res.total
          this.listLoading = false
        }).catch((res) => {
          this.listLoading = false
        })
      },
      pageChange(page) {
        this.tableFrom.page = page
        this.getList()
      },
      handleSizeChange(val) {
        this.tableFrom.limit = val
        this.getList()
      },
      onchangeIsShow(row) {
        seckillStoreStatusApi({id: row.id, status: row.status})
          .then(async () => {
            this.$message.success('修改成功');
            this.getList()
          }).catch(()=>{
          row.status = !row.status
        })
      }
    }
  }
</script>

<style scoped>
   .el-table__body {
    width: 100%;
    table-layout: fixed !important;
  }
</style>
