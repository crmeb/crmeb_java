<template>
  <div class="divBox">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <div class="container">
          <el-form inline>
            <el-form-item label="砍价状态：">
              <el-select v-model="tableFrom.status" placeholder="请选择" class="filter-item selWidth mr20" @change="getList(1)" clearable>
                <el-option label="关闭" :value="0" />
                <el-option label="开启" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="商品搜索：">
              <el-input v-model="tableFrom.keywords" placeholder="请输入商品名称、ID" class="selWidth" clearable>
                <el-button slot="append" icon="el-icon-search" @click="getList(1)"/>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <router-link :to=" { path:'/marketing/bargain/creatBargain' }">
          <el-button size="mini" type="primary" class="mr10" v-hasPermi="['admin:bargain:save']">添加砍价商品</el-button>
        </router-link>
        <el-button size="mini" class="mr10" @click="exportList" v-hasPermi="['admin:export:excel:bargain']">导出</el-button>
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
                <div class="text_overflow" slot="reference">{{scope.row.title}}</div>
                <div class="pup_card">{{scope.row.title}}</div>
              </el-popover>
            </template>
        </el-table-column>
        <el-table-column
          label="砍价价格"
          prop="price"
          min-width="100"
          align="center"
        />
        <el-table-column
          label="最低价"
          prop="minPrice"
          min-width="100"
          align="center"
        />
        <el-table-column
          label="参与人数"
          prop="countPeopleAll"
          min-width="100"
          align="center"
        />
        <el-table-column
          label="帮忙砍价人数"
          prop="countPeopleHelp"
          min-width="100"
          align="center"
        />
        <el-table-column
          label="砍价成功人数"
          prop="countPeopleSuccess"
          min-width="100"
          align="center"
        />
        <el-table-column
          label="限量"
          min-width="100"
          prop="quotaShow"
          align="center"
        />
        <el-table-column
          label="限量剩余"
          prop="surplusQuota"
          min-width="100"
          align="center"
        />
        <el-table-column
          prop="stopTime"
          label="活动时间"
          min-width="160"
        >
          <template slot-scope="scope">
            {{scope.row.startTime + ' ~ ' + scope.row.stopTime}}
          </template>
        </el-table-column>
        <el-table-column
          label="砍价状态"
          min-width="150"
        >
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
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="130" fixed="right" align="center">
          <template slot-scope="scope">
            <router-link :to="{ path:'/marketing/bargain/creatBargain/' + scope.row.id}">
              <el-button type="text" size="small" v-hasPermi="['admin:bargain:info']">编辑</el-button>
            </router-link>
            <el-button type="text" size="small" @click="handleDelete(scope.row.id, scope.$index)" class="mr10" v-hasPermi="['admin:bargain:delete']">删除</el-button>
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
        />
      </div>
    </el-card>
  </div>
</template>

<script>
  import { bargainListApi,bargainDeleteApi,bargainStatusApi,exportBargainApi } from '@/api/marketing'
  import { checkPermi, checkRole } from "@/utils/permission";
  export default {
    name: "index",
    data() {
      return {
        tableFrom: {
          page: 1,
          limit: 20,
          keywords: '',
          status: null
        },
        listLoading: true,
        tableData: {
          data: [],
          total: 0
        },
        afterData: []
      }
    },
    mounted() {
      // 源数据
      var oldData = [
        {
          city_id: 1,
          city_name: '北京',
          city_img: "http://dfknbdjknvkjsfnvlkjdn.png",
          city_country: "中国"
        },
        {
          city_id: 2,
          city_name: '上海',
          city_img: "http://wergerbe.png",
          city_country: "中国"
        },
        {
          city_id: 3,
          city_name: '广州',
          city_img: "http://hrthhr.png",
          city_country: "中国"
        },
        {
          city_id: 4,
          city_name: '西雅图',
          city_img: "http://frevfd.png",
          city_country: "美国"
        },
        {
          city_id: 5,
          city_name: '纽约',
          city_img: "http://反而个.png",
          city_country: "美国"
        }
      ]
     // 把源数据先变成目标数据的规则
      var oldDataRule = []
      oldData.forEach(el => {
        var oldObj = {
          name: el.city_country,
          citys:[]
        }
        var cityObj = {
          city_name: el.city_name,
          city_img: el.city_img,
          city_id: el.city_id
        }
        oldObj.citys.push(cityObj)
        oldDataRule.push(oldObj)
      })
      var newData = []
      var newObj = {}
      oldDataRule.forEach((el, i) => {
        if (!newObj[el.name]) {
          newData.push(el);
          newObj[el.name] = true;
        } else {
          newData.forEach(el => {
            if (el.name === oldDataRule[i].name) {
              el.citys = el.citys.concat(oldDataRule[i].citys);
              // el.citys = [...el.citys, ...oldDataRule[i].citys]; // es6语法
            }
          })
        }
      })
      this.getList()
    },
    methods: {
      checkPermi, //权限控制
      //导出
      exportList(){
        exportBargainApi({keywords: this.tableFrom.keywords, status:this.tableFrom.status}).then((res) => {
          window.open(res.fileName)
        })
      },
      // 删除
      handleDelete(id, idx) {
        let that = this;
        this.$modal.confirm('确认删除该商品吗').then(function() {
          bargainDeleteApi({id: id}).then(() => {
            that.$message.success('删除成功')
            that.getList();
          })
        }).catch(() => {});
      },
      onchangeIsShow(row) {
        bargainStatusApi({id:row.id, status: row.status})
          .then(async () => {
            this.$message.success('修改成功');
            this.getList()
          }).catch(()=>{
          row.status = !row.status
        })
      },
      // 列表
      getList(num) {
        this.listLoading = true
        this.tableFrom.page = num ? num : this.tableFrom.page;
        bargainListApi(this.tableFrom).then(res => {
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
      add() {
        this.isCreate = 0
        this.dialogVisible = true
      },
      handleClose() {
        this.dialogVisible = false
        this.editData = {}
      }
    }
  }
</script>

<style scoped>
   .el-table__body {
    width: 100%;
    table-layout: fixed !important;
  }
  .text_overflow{
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 400px;
  }
  .pup_card{
    width: 200px;
    border-radius: 5px;
    padding: 5px;
    box-sizing: border-box;
    font-size: 12px;
    line-height: 16px;
  }
</style>
