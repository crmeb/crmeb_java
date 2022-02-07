<template>
  <el-dialog
    v-if="dialogVisible"
    title="运费模板"
    :visible.sync="dialogVisible"
    width="1000px"
    :before-close="handleClose"
  >
    <el-form ref="ruleForm" :model="ruleForm" label-width="120px" size="mini" v-if="dialogVisible" :rules="rules">
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="ruleForm.name" class="withs" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="计费方式" prop="type">
        <el-radio-group v-model="ruleForm.type" @change="changeRadio(ruleForm.type)">
          <el-radio :label="1">按件数</el-radio>
          <el-radio :label="2">按重量</el-radio>
          <el-radio :label="3">按体积</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="配送区域及运费" prop="region">
        <el-table v-loading="listLoading" :data="ruleForm.region" border fit highlight-current-row style="width: 100%" size="mini" class="tempBox">
          <el-table-column align="center" label="可配送区域" min-width="260">
            <template slot-scope="scope">
              <span v-if="scope.$index === 0">默认全国</span>
              <el-cascader
                v-else
                v-model="scope.row.city_ids"
                style="width: 98%"
                :options="cityList"
                :props="props"
                collapse-tags
                clearable
                filterable
                @change="changeRegion"
              />
            </template>
          </el-table-column>
          <el-table-column min-width="130px" align="center" :label="columns.title" prop="first">
            <template slot-scope="scope">
              <el-form-item :rules="rules.first" :prop="'region.'+scope.$index+'.first'">
                <el-input-number v-model="scope.row.first" controls-position="right" :step-strictly="ruleForm.type===1?true:false" :min="ruleForm.type===1?1:0.1"/>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column min-width="120px" align="center" label="运费（元）" prop="firstPrice">
            <template slot-scope="scope">
              <el-form-item :rules="rules.firstPrice" :prop="'region.'+scope.$index+'.firstPrice'">
                <el-input-number v-model="scope.row.firstPrice" controls-position="right" />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column min-width="120px" align="center" :label="columns.title2" prop="renewal">
            <template slot-scope="scope">
              <el-form-item :rules="rules.renewal" :prop="'region.'+scope.$index+'.renewal'">
                <el-input-number v-model="scope.row.renewal" controls-position="right" :step-strictly="ruleForm.type===1?true:false" :min="ruleForm.type===1?1:0.1"/>
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column class-name="status-col" align="center" label="续费（元）" min-width="120" prop="renewalPrice">
            <template slot-scope="scope">
              <el-form-item :rules="rules.renewalPrice" :prop="'region.'+scope.$index+'.renewalPrice'">
                <el-input-number v-model="scope.row.renewalPrice" controls-position="right" />
              </el-form-item>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" min-width="80">
            <template slot-scope="scope">
              <el-button
                v-if="scope.$index > 0"
                type="text"
                size="small"
                @click="confirmEdit(ruleForm.region,scope.$index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" icon="el-icon-edit" @click="addRegion(ruleForm.region)">
          添加配送区域
        </el-button>
      </el-form-item>
      <el-form-item label="指定包邮" prop="appoint">
        <el-radio-group v-model="ruleForm.appoint">
          <el-radio :label="true">开启</el-radio>
          <el-radio :label="false">关闭</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="ruleForm.appoint === true" prop="free">
        <el-table v-loading="listLoading" :data="ruleForm.free" border fit highlight-current-row style="width: 100%" size="mini">
          <el-table-column align="center" label="选择地区" min-width="220">
            <template slot-scope="{row}">
              <el-cascader
                v-model="row.city_ids"
                style="width: 95%"
                :options="cityList"
                :props="props"
                collapse-tags
                clearable
              />
            </template>
          </el-table-column>
          <el-table-column min-width="180px" align="center" :label="columns.title3">
            <template slot-scope="{row}">
              <el-input-number v-model="row.number" controls-position="right" :step-strictly="ruleForm.type===1?true:false" :min="ruleForm.type===1?1:0.1"/>
            </template>
          </el-table-column>
          <el-table-column min-width="120px" align="center" label="包邮金额（元）">
            <template slot-scope="{row}">
              <el-input-number v-model="row.price" controls-position="right" />
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作" min-width="120">
            <template slot-scope="scope">
              <el-button
                type="text"
                size="small"
                @click="confirmEdit(ruleForm.free,scope.$index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item v-if="ruleForm.appoint === true">
        <el-button type="primary" size="mini" icon="el-icon-edit" @click="addFree(ruleForm.free)">
          添加指定包邮区域
        </el-button>
      </el-form-item>
      <!--<el-row :gutter="20">-->
        <!--<el-col :span="7">-->
          <!--<el-form-item label="指定区域不配送" prop="undelivery">-->
            <!--<el-radio-group v-model="ruleForm.undelivery">-->
              <!--<el-radio :label="1">开启</el-radio>-->
              <!--<el-radio :label="0">关闭</el-radio>-->
            <!--</el-radio-group>-->
          <!--</el-form-item>-->
        <!--</el-col>-->
        <!--<el-col :span="14">-->
          <!--<el-form-item v-if="ruleForm.undelivery === 1" class="noBox" prop="city_id3">-->
            <!--<el-cascader-->
              <!--v-model="ruleForm.city_id3"-->
              <!--placeholder="请选择不配送区域"-->
              <!--:options="cityList"-->
              <!--:props="props"-->
              <!--collapse-tags-->
              <!--clearable-->
              <!--style="width: 46%"-->
            <!--/>-->
          <!--</el-form-item>-->
        <!--</el-col>-->
      <!--</el-row>-->
      <el-form-item label="排序">
        <el-input v-model="ruleForm.sort" class="withs" placeholder="请输入排序" />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="onClose('ruleForm')">取 消</el-button>
      <el-button type="primary"  :loading="loading" @click="onsubmit('ruleForm')">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import * as logistics from '@/api/logistics'
import { Loading } from 'element-ui'
const defaultRole = {
  name: '',
  type: 1,
  appoint: false,
  sort: 0,
  region: [{
    first: 0,
    firstPrice: 0,
    renewal: 0,
    renewalPrice: 0,
    city_ids: []
  }],
  undelivery: 0,
  free: [],
  undelives: {},
  city_id3: []
}
const kg = '重量（kg）'
const m = '体积（m³）'
const statusMap = [
  {
    title: '首件',
    title2: '续件',
    title3: '包邮件数'
  },
  {
    title: `首件${kg}`,
    title2: `续件${kg}`,
    title3: `包邮${kg}`
  },
  {
    title: `首件${m}`,
    title2: `续件${m}`,
    title3: `包邮${m}`
  }
]
export default {
  name: 'CreatTemplates',
  components: {
  },
  data() {
    return {
      loading : false,
      rules: {
        name: [
          { required: true, message: '请输入模板名称', trigger: 'blur' }
        ],
        free: [
          { type: 'array', required: true, message: '请至少添加一个地区', trigger: 'change' }
        ],
        appoint: [
          { required: true, message: '请选择是否指定包邮', trigger: 'change' }
        ],
        undelivery: [
          { required: true, message: '请选择是否指定区域不配送', trigger: 'change' }
        ],
        type: [
          { required: true, message: '请选择计费方式', trigger: 'change' }
        ],
        region: [
          { required: true, message: '请选择活动区域', trigger: 'change' }
        ],
        city_id3: [
          { type: 'array', required: true, message: '请至少选择一个地区', trigger: 'change' }
        ],
        first: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        renewal: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        firstPrice: [
          { required: true, message: '请输入运费', trigger: 'blur' }
        ],
        renewalPrice: [
          { required: true, message: '请输入续费', trigger: 'blur' }
        ]
      },
      nodeKey: 'city_id',
      props: {
        children: 'child',
        label: 'name',
        value: 'cityId',
        multiple: true
      },
      dialogVisible: false,
      ruleForm: Object.assign({}, defaultRole),
      listLoading: false,
      cityList: [],
      columns: {
        title: '首件',
        title2: '续件',
        title3: '包邮件数'
      },
      tempId: 0,
      type: 0 // 0添加 1编辑
    }
  },
  mounted() {
    setTimeout(()=>{
      let cityList = JSON.parse(sessionStorage.getItem('cityList'));
      this.cityList = cityList;
    },1000);
  },
  methods: {
    changType(type) {
      this.type = type
    },
    onClose(formName) {
      this.dialogVisible = false
      this.$refs[formName].resetFields()
    },
    confirmEdit(row, index) {
      row.splice(index, 1)
    },
    popoverHide() {},
    handleClose() {
     // this.$refs['ruleForm'].resetFields()
      this.dialogVisible = false
      this.ruleForm={
          name: '',
          type: 1,
          appoint: false,
          sort: 0,
          region: [{
          first: 0,
          firstPrice: 0,
          renewal: 0,
          renewalPrice: 0,
          city_ids: []
        }],
          undelivery: 0,
          free: [],
          undelives: {},
          city_id3: []
      }
    },
    changeRegion(value) {
      console.log(value)
    },
    changeRadio(num) {
      this.columns = Object.assign({}, statusMap[num - 1])
    },
    // 添加配送区域
    addRegion(region) {
      region.push(Object.assign({}, {
        first: 0,
        firstPrice: 0,
        renewal: 0,
        renewalPrice: 0,
        city_ids: []
      }))
    },
    addFree(Free) {
      Free.push(Object.assign({}, {
        city_id: [],
        number: 1,
        price: 1,
        city_ids: []
      }))
    },

    /**
     * 详情
     * id 模板id
     * appoint true包邮 false不包邮
    **/
    getInfo(id, appoint) {
      this.tempId = id
      const loadingInstance = Loading.service({ fullscreen: true })
      logistics.templateDetailApi({ id }).then(res => {
        this.dialogVisible = true
        const info = res
        this.ruleForm = Object.assign(this.ruleForm, {
          name: info.name,
          type: info.type,
          appoint: info.appoint,
          sort: info.sort
        })
        this.columns = Object.assign({}, statusMap[this.ruleForm.type - 1])
        this.$nextTick(() => {
          loadingInstance.close()
        })
        // 不包邮地区
        this.shippingRegion()
        // 包邮地区
        if (info.appoint) {
          this.shippingFree()
        }
      }).catch(res => {
        // console.integralLog(res)
        this.$message.error(res.message)
        this.$nextTick(() => {
          loadingInstance.close()
        })
      })
    },
    // 不包邮
    shippingRegion() {
      logistics.shippingRegion({ tempId: this.tempId }).then(res => {
        res.forEach((item, index) => {
          item.title = JSON.parse(item.title)
          item.city_ids = item.title
        })
        this.ruleForm.region = res
      })
    },
    // 包邮
    shippingFree() {
      logistics.shippingFree({ tempId: this.tempId }).then(res => {
        res.forEach((item, index) => {
          item.title = JSON.parse(item.title)
          item.city_ids = item.title
        })
        this.ruleForm.free = res
      })
    },
    // 列表
    getCityList() {
      logistics.cityListTree().then(res => {
        sessionStorage.setItem('cityList',JSON.stringify(res));
        let cityList = JSON.parse(sessionStorage.getItem('cityList'));
        this.cityList = cityList;
      }).catch(res => {
        this.$message.error(res.message)
      })
    },
    change(idBox) {
      idBox.map(item => {
        const ids = []
        item.city_ids.map(j => {
          j.splice(0, 1)
          ids.push(j[0])
        })
        item.city_id = ids
      })
      return idBox
    },
    changeOne(idBox) {
      const city_ids = []
      idBox.map(item => {
        item.splice(0, 1)
        city_ids.push(item[0])
      })
      return city_ids
    },
    onsubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          const param = {
            appoint: this.ruleForm.appoint,
            name: this.ruleForm.name,
            sort: this.ruleForm.sort,
            type: this.ruleForm.type,
            // 配送区域及运费
            // shippingTemplatesRegionRequestList: [],
            // // 指定包邮设置
            // shippingTemplatesFreeRequestList: []
          }
          this.ruleForm.region.forEach((el, index) => {
            el.title = el.city_ids.length > 0 ? JSON.stringify(el.city_ids) : JSON.stringify([[0, 0]])
            for (var i = 0; i < el.city_ids.length; i++) {
              el.city_ids[i].shift()
            }
            el.cityId = el.city_ids.length > 0 ? el.city_ids.join(',') : 'all'
          })
          param.shippingTemplatesRegionRequestList = this.ruleForm.region
          param.shippingTemplatesRegionRequestList.forEach((el, index) => {
            // delete el.city_ids
            // delete el.city_id
          })
          if (this.ruleForm.appoint) {
            this.ruleForm.free.forEach((el, index) => {
              el.title = el.city_ids.length > 0 ? JSON.stringify(el.city_ids) : JSON.stringify([[0, 0]])
              for (var i = 0; i < el.city_ids.length; i++) {
                el.city_ids[i].shift()
              }
              el.cityId = el.city_ids.length > 0 ? el.city_ids.join(',') : 'all'
            })
            param.shippingTemplatesFreeRequestList = this.ruleForm.free
            param.shippingTemplatesFreeRequestList.forEach((el, index) => {
              // delete el.city_ids
              // delete el.city_id
            })
          }
          if (this.type === 0) {
            logistics.shippingSave(param).then(res => {
              this.$message.success('操作成功')
              this.handleClose()
              this.$nextTick(() => {
                this.dialogVisible = false
              })
              setTimeout(() => {
                this.$emit('getList')
              }, 600)
              this.loading = false;
            })
          } else {
            logistics.shippingUpdate(param, { id: this.tempId }).then(res => {
              this.$message.success('操作成功')
              setTimeout(() => {
                this.$emit('getList')
                this.handleClose()
              }, 600)
              this.$nextTick(() => {
                this.dialogVisible = false
              })
              this.loading = false;
            })
          }
        } else {
          return false
        }
      })
    },
    clear() {
      this.ruleForm.name = ''
      this.ruleForm.sort = 0
    }
  }
}
</script>

<style scoped lang="scss">
  .withs{
    width: 50%;
  }
  .noBox{
    ::v-deep.el-form-item__content{
      margin-left: 0 !important;
    }
  }
  .tempBox{
    ::v-deep.el-input-number--mini{
     width: 100px !important;
    }
  }
</style>