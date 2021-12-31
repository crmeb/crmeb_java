<template>
  <div class="divBox">
    <div class="header clearfix">
      <div class="container">
        <el-form inline size="small">
          <el-form-item label="优惠卷名称：">
            <el-input v-model="tableFrom.keywords" placeholder="请输入优惠券名称" class="selWidth" size="small">
              <el-button slot="append" icon="el-icon-search" size="small" @click="getList(1)" />
            </el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-table
      ref="table"
      v-loading="listLoading"
      :data="tableData.data"
      style="width: 100%"
      size="mini"
      max-height="400"
      tooltip-effect="dark"
      highlight-current-row
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        v-if="handle==='wu'"
        type="selection"
        width="55"
      />
      <el-table-column
        prop="id"
        label="ID"
        min-width="50"
      />
      <el-table-column
        prop="name"
        label="优惠券名称"
        min-width="90"
      />
      <el-table-column
        prop="money"
        label="优惠券面值"
        min-width="90"
      />
      <el-table-column
        prop="minPrice"
        label="最低消费额"
        min-width="90"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.minPrice===0?'不限制':scope.row.minPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="有效期限"
        min-width="190"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.isFixedTime===1?scope.row.useStartTime+' 一 '+scope.row.useEndTime:'不限制' }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="剩余数量"
        min-width="90"
      >
        <template slot-scope="scope">
          <span>{{ !scope.row.isLimited ? '不限量' : scope.row.lastTotal }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="handle==='send'" label="操作" min-width="120" fixed="right" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="small" class="mr10" @click="sendGrant(scope.row.id)" v-hasPermi="['admin:coupon:user:receive']">发送</el-button>
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
    <div  v-if="handle==='wu'">
      <el-button size="small" type="primary" class="fr" @click="ok">确定</el-button>
    </div>
  </div>
</template>

<script>
import { marketingListApi, couponUserApi, marketingSendApi } from '@/api/marketing'
export default {
  name: 'CouponList',
  props: {
    handle: {
      type: String,
      default: ''
    },
    couponData: {
      type: Array,
      default: () => []
    },
    keyNum: {
      type: Number,
      default: 0
    },
    userIds: {
      type: String,
      default: ''
    },
    userType: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      listLoading: true,
      tableData: {
        data: [],
        total: 0
      },
      tableFrom: {
        page: 1,
        limit: 10,
        keywords: '',
        type: ''
      },
      multipleSelection: [],
      multipleSelectionAll: [],
      idKey: 'id',
      nextPageFlag: false,
      attr: []
    }
  },
  watch: {
    keyNum: {
      deep: true,
      handler(val) {
        this.getList()
      }
    }
  },
  mounted() {
    this.tableFrom.page = 1
    this.getList()
    if(!this.couponData) return;
    this.couponData.forEach(row => {
      this.$refs.table.toggleRowSelection(row);
    });
  },
  methods: {
    close() {
      this.multipleSelection = []
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
      setTimeout(() => {
        this.changePageCoreRecordData()
      }, 50)
    },
    // 设置选中的方法
    setSelectRow() {
      if (!this.multipleSelectionAll || this.multipleSelectionAll.length <= 0) {
        return
      }
      // 标识当前行的唯一键的名称
      const idKey = this.idKey
      const selectAllIds = []
      this.multipleSelectionAll.forEach(row => {
        selectAllIds.push(row[idKey])
      })
      this.$refs.table.clearSelection()
      for (var i = 0; i < this.tableData.data.length; i++) {
        if (selectAllIds.indexOf(this.tableData.data[i][idKey]) >= 0) {
          // 设置选中，记住table组件需要使用ref="table"
          this.$refs.table.toggleRowSelection(this.tableData.data[i], true)
        }
      }
    },
    // 记忆选择核心方法
    changePageCoreRecordData() {
      // 标识当前行的唯一键的名称
      const idKey = this.idKey
      const that = this
      // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
      if (this.multipleSelectionAll.length <= 0) {
        this.multipleSelectionAll = this.multipleSelection
        return
      }
      // 总选择里面的key集合
      const selectAllIds = []
      this.multipleSelectionAll.forEach(row => {
        selectAllIds.push(row[idKey])
      })
      const selectIds = []
      // 获取当前页选中的id
      this.multipleSelection.forEach(row => {
        selectIds.push(row[idKey])
        // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
        if (selectAllIds.indexOf(row[idKey]) < 0) {
          that.multipleSelectionAll.push(row)
        }
      })
      const noSelectIds = []
      // 得到当前页没有选中的id
      this.tableData.data.forEach(row => {
        if (selectIds.indexOf(row[idKey]) < 0) {
          noSelectIds.push(row[idKey])
        }
      })
      noSelectIds.forEach(id => {
        if (selectAllIds.indexOf(id) >= 0) {
          for (let i = 0; i < that.multipleSelectionAll.length; i++) {
            if (that.multipleSelectionAll[i][idKey] == id) {
              // 如果总选择中有未被选中的，那么就删除这条
              that.multipleSelectionAll.splice(i, 1)
              break
            }
          }
        }
      })
    },
    ok() {
      if (this.multipleSelection.length > 0) {
        this.$emit('getCouponId', this.multipleSelectionAll)
         this.close()
      } else {
        this.$message.warning('请先选择优惠劵')
      }
    },
    // 列表
    getList(num) {
      this.listLoading = true
      this.tableFrom.page = num ? num : this.tableFrom.page
      this.userType ? this.tableFrom.type = 1 : this.tableFrom.type = 3;
      marketingSendApi(this.tableFrom).then(res => {
        this.tableData.data = res.list
        this.tableData.total = res.total
        this.listLoading = false
        this.$nextTick(function() {
          this.setSelectRow()// 调用跨页选中方法
        })
      }).catch(res => {
        this.listLoading = false
      })
    },
    pageChange(page) {
      this.changePageCoreRecordData()
      this.tableFrom.page = page
      this.getList()
    },
    handleSizeChange(val) {
      this.changePageCoreRecordData()
      this.tableFrom.limit = val
      this.getList()
    },
    // 发送
    sendGrant(id){
      this.$modalSure('发送优惠劵吗').then(() => {
        couponUserApi({ couponId:id, uid:this.userIds }).then(() => {
          this.$message.success('发送成功')
          this.getList()
        })
      })
    }
  }
}
</script>

<style scoped lang="scss">
  .selWidth{
    width: 219px !important;
  }
  .seachTiele{
    line-height: 35px;
  }
  .fr{
    float: right;
  }
</style>
