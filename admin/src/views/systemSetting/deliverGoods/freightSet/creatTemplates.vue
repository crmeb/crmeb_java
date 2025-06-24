<template>
  <el-drawer
    v-if="dialogVisible"
    title="运费模板"
    :visible.sync="dialogVisible"
    size="1000px"
    direction="rtl"
    :before-close="handleClose"
    :append-to-body="true"
    :modal-append-to-body="false"
    :wrapperClosable="false"
    :show-close="true"
  >
    <div class="demo-drawer__content">
      <el-form ref="ruleForm" :model="ruleForm" label-width="100px" size="mini" v-if="dialogVisible" :rules="rules">
        <el-form-item label="模板名称：" prop="name">
          <el-input v-model="ruleForm.name" class="withs" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="包邮方式：" prop="appoint">
          <el-radio-group v-model="ruleForm.appoint" @change="changeAppoint">
            <el-radio :label="0">全国包邮</el-radio>
            <el-radio :label="1">部分包邮</el-radio>
            <el-radio :label="2">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="Number(ruleForm.appoint) > 0">
          <el-form-item label="计费方式：" prop="type">
            <el-radio-group v-model="ruleForm.type" @change="changeRadio(ruleForm.type)">
              <el-radio :label="1">按件数</el-radio>
              <el-radio :label="2">按重量</el-radio>
              <el-radio :label="3">按体积</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="运费：" prop="region">
            <el-table
              v-loading="listLoading"
              :data="ruleForm.region"
              border
              fit
              highlight-current-row
              style="width: 100%"
              size="mini"
              class="tempBox"
            >
              <el-table-column label="送达到" min-width="260" prop="city_ids">
                <template slot-scope="scope">
                  <span v-if="scope.$index === 0 && ruleForm.appoint === 2">默认运费</span>
                  <el-cascader
                    v-else
                    v-model="scope.row.city_ids"
                    style="width: 98%"
                    :rules="rules.city_ids"
                    :options="cityList"
                    :props="props"
                    collapse-tags
                    filterable
                    @change="changeRegion"
                    class="topCascader"
                  />
                </template>
              </el-table-column>
              <el-table-column min-width="120px" :label="columns.title" prop="first">
                <template slot-scope="scope">
                  <el-form-item :rules="rules.first" :prop="'region.' + scope.$index + '.first'">
                    <el-input-number
                      v-model="scope.row.first"
                      controls-position="right"
                      :step-strictly="ruleForm.type === 1 ? true : false"
                      :min="ruleForm.type === 1 ? 1 : 0.1"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column min-width="120px" label="运费（元）" prop="firstPrice">
                <template slot-scope="scope">
                  <el-form-item :rules="rules.firstPrice" :prop="'region.' + scope.$index + '.firstPrice'">
                    <el-input-number v-model="scope.row.firstPrice" controls-position="right" :min="0" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column min-width="120px" :label="columns.title2" prop="renewal">
                <template slot-scope="scope">
                  <el-form-item :rules="rules.renewal" :prop="'region.' + scope.$index + '.renewal'">
                    <el-input-number
                      v-model="scope.row.renewal"
                      controls-position="right"
                      :step-strictly="ruleForm.type === 1 ? true : false"
                      :min="ruleForm.type === 1 ? 1 : 0.1"
                    />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column class-name="status-col" label="续费（元）" min-width="120" prop="renewalPrice">
                <template slot-scope="scope">
                  <el-form-item :rules="rules.renewalPrice" :prop="'region.' + scope.$index + '.renewalPrice'">
                    <el-input-number v-model="scope.row.renewalPrice" controls-position="right" :min="0" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template slot-scope="scope">
                  <el-button
                    v-if="ruleForm.appoint === 1 || (ruleForm.appoint !== 1 && scope.$index > 0)"
                    type="text"
                    size="small"
                    @click="confirmEdit(ruleForm.region, scope.$index)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item>
            <el-button class="mt20 mb20" type="primary" icon="el-icon-edit" @click="addRegion(ruleForm.region)">
              添加区域
            </el-button>
          </el-form-item>
          <el-form-item v-if="ruleForm.appoint === 2" label="包邮区域：">
            <el-table
              v-loading="listLoading"
              :data="ruleForm.free"
              border
              fit
              highlight-current-row
              style="width: 100%"
              size="mini"
            >
              <el-table-column label="选择区域" min-width="220">
                <template slot-scope="{ row }">
                  <el-cascader
                    v-model="row.city_ids"
                    style="width: 95%"
                    :options="cityList"
                    :props="props"
                    collapse-tags
                    clearable
                    class="botCascader"
                  />
                </template>
              </el-table-column>
              <el-table-column min-width="180px" :label="columns.title3">
                <template slot-scope="{ row }">
                  <el-input-number
                    v-model="row.number"
                    controls-position="right"
                    :step-strictly="ruleForm.type === 1 ? true : false"
                    :min="ruleForm.type === 1 ? 1 : 0.1"
                  />
                </template>
              </el-table-column>
              <el-table-column min-width="120px" label="包邮金额（元）">
                <template slot-scope="{ row }">
                  <el-input-number v-model="row.price" controls-position="right" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template slot-scope="scope">
                  <el-button type="text" size="small" @click="confirmEdit(ruleForm.free, scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item v-if="ruleForm.appoint === 2">
            <el-button class="mt20 mb20" type="primary" icon="el-icon-edit" @click="addFree(ruleForm.free)">
              添加指定包邮区域
            </el-button>
          </el-form-item>
        </template>
        <el-form-item label="排序：">
          <el-input v-model="ruleForm.sort" class="withs" placeholder="请输入排序" />
        </el-form-item>
      </el-form>
    </div>
    <div class="demo-drawer__footer from-foot-btn btn-shadow drawer_fix">
      <el-button @click="handleClose('ruleForm')">取 消</el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="onsubmit('ruleForm')"
        v-hasPermi="['admin:shipping:templates:update']"
        >确定</el-button
      >
    </div>
    <span slot="footer" class="dialog-footer"> </span>
  </el-drawer>
</template>

<script>
import * as logistics from '@/api/logistics';
import { Loading } from 'element-ui';
import { Debounce } from '@/utils/validate';
const defaultRole = {
  name: '',
  type: 1,
  appoint: 0,
  sort: 0,
  region: [
    {
      first: 1,
      firstPrice: 0,
      renewal: 0,
      renewalPrice: 0,
      city_ids: [],
      cityId: 0,
    },
  ],
  undelivery: 0,
  free: [],
  undelives: {},
};
const kg = '重量（kg）';
const m = '体积（m³）';
const statusMap = [
  {
    title: '首件',
    title2: '续件',
    title3: '包邮件数',
  },
  {
    title: `首件${kg}`,
    title2: `续件${kg}`,
    title3: `包邮${kg}`,
  },
  {
    title: `首件${m}`,
    title2: `续件${m}`,
    title3: `包邮${m}`,
  },
];
export default {
  name: 'CreatTemplates',
  components: {},
  data() {
    return {
      loading: false,
      rules: {
        name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
        free: [{ type: 'array', required: true, message: '请至少添加一个区域', trigger: 'change' }],
        appoint: [{ required: true, message: '请选择包邮方式', trigger: 'change' }],
        undelivery: [{ required: true, message: '请选择是否指定区域不配送', trigger: 'change' }],
        type: [{ required: true, message: '请选择计费方式', trigger: 'change' }],
        region: [{ required: true, message: '请选择区域运费', trigger: 'change' }],
        city_ids: [{ type: 'array', required: true, message: '请至少选择一个区域', trigger: 'change' }],
        first: [{ required: true, message: '请输入', trigger: 'blur' }],
        renewal: [{ required: true, message: '请输入', trigger: 'blur' }],
        firstPrice: [{ required: true, message: '请输入运费', trigger: 'blur' }],
        renewalPrice: [{ required: true, message: '请输入续费', trigger: 'blur' }],
      },
      nodeKey: 'city_ids',
      props: {
        children: 'child',
        label: 'name',
        value: 'cityId',
        multiple: true,
      },
      dialogVisible: false,
      ruleForm: Object.assign({}, defaultRole),
      listLoading: false,
      cityList: [],
      columns: {
        title: '首件',
        title2: '续件',
        title3: '包邮件数',
      },
      tempId: 0,
      regionNew: [
        {
          first: 1,
          firstPrice: 0,
          renewal: 0,
          renewalPrice: 0,
          city_ids: [],
          cityId: 0,
        },
      ],
      type: 0, // 0添加 1编辑
    };
  },
  mounted() {
    this.listLoading = false;
    let cityList = JSON.parse(sessionStorage.getItem('cityList'));
    this.cityList = cityList;
  },
  methods: {
    changeAppoint() {
      this.listLoading = false;
      let region = [...this.ruleForm.region];
    },
    changType(type) {
      this.type = type;
    },
    confirmEdit(row, index) {
      row.splice(index, 1);
    },
    popoverHide() {},
    handleClose() {
      this.dialogVisible = false;
      this.ruleForm = {
        name: '',
        type: 1,
        appoint: 0,
        sort: 0,
        region: [
          {
            first: 0,
            firstPrice: 0,
            renewal: 0,
            renewalPrice: 0,
            city_ids: [],
            cityId: 0,
          },
        ],
        undelivery: 0,
        free: [],
        undelives: {},
      };
      this.columns = Object.assign({}, statusMap[0]);
    },
    changeRegion(value) {},
    changeRadio(num) {
      this.columns = Object.assign({}, statusMap[num - 1]);
    },
    // 添加配送区域
    addRegion(region) {
      region.push(
        Object.assign(
          {},
          {
            first: 0,
            firstPrice: 0,
            renewal: 0,
            renewalPrice: 0,
            city_ids: [],
            cityId: '',
          },
        ),
      );
    },
    addFree(Free) {
      Free.push(
        Object.assign(
          {},
          {
            number: 1,
            price: 1,
            city_ids: [],
            cityId: '',
          },
        ),
      );
    },

    /**
     * 详情
     * id 模板id
     * appoint true包邮 false不包邮
     **/
    getInfo(id, appoint) {
      this.tempId = id;
      const loadingInstance = Loading.service({ fullscreen: true });
      logistics
        .templateDetailApi({ id })
        .then((res) => {
          this.dialogVisible = true;
          const info = res;
          if (info.appoint === 0) info.type = 1;
          if (Number(info.appoint) > 0) {
            if (info.regionList && info.regionList.length > 0) {
              info.regionList.forEach((item, index) => {
                item.title = JSON.parse(item.title);
                item.city_ids = item.title;
              });
            }
            if (info.freeList && info.freeList.length > 0) {
              info.freeList.forEach((item, index) => {
                item.title = JSON.parse(item.title);
                item.city_ids = item.title;
              });
            }
          }
          this.ruleForm = Object.assign(this.ruleForm, {
            name: info.name,
            type: info.type,
            appoint: info.appoint,
            sort: info.sort,
            region: info.regionList || [], // 运费区域
            free: info.freeList || [], // 包邮区域
          });
          this.regionNew = [...this.ruleForm.region];
          this.columns = Object.assign({}, statusMap[this.ruleForm.type - 1]);
          this.$nextTick(() => {
            loadingInstance.close();
          });
        })
        .catch((res) => {
          // console.integralLog(res)
          this.$message.error(res.message);
          this.$nextTick(() => {
            loadingInstance.close();
          });
        });
    },
    // 列表
    getCityList() {
      logistics
        .cityListTree()
        .then((res) => {
          sessionStorage.setItem('cityList', JSON.stringify(res));
          let cityList = JSON.parse(sessionStorage.getItem('cityList'));
          this.cityList = cityList;
        })
        .catch((res) => {
          this.$message.error(res.message);
        });
    },
    change(idBox) {
      idBox.map((item) => {
        const ids = [];
        item.city_ids.map((j) => {
          j.splice(0, 1);
          ids.push(j[0]);
        });
        item.city_ids = ids;
      });
      return idBox;
    },
    changeOne(idBox) {
      const city_ids = [];
      idBox.map((item) => {
        item.splice(0, 1);
        city_ids.push(item[0]);
      });
      return city_ids;
    },
    onsubmit: Debounce(function (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true;
          this.listLoading = true;
          const param = {
            appoint: this.ruleForm.appoint,
            name: this.ruleForm.name,
            sort: this.ruleForm.sort,
            type: this.ruleForm.type,
            // 配送区域及运费
            shippingTemplatesRegionRequestList: [],
            // 指定包邮设置
            shippingTemplatesFreeRequestList: [],
          };
          this.ruleForm.region.forEach((el, index) => {
            el.title = el.city_ids.length > 0 ? JSON.stringify(el.city_ids) : JSON.stringify([[0, 0]]);
            if (el.title === '[[0,0]]') {
              el.cityId = '0';
            } else {
              for (var i = 0; i < el.city_ids.length; i++) {
                el.city_ids[i].splice(0, 2);
              }
              el.cityId = el.city_ids.length > 0 ? el.city_ids.join(',') : '0';
            }
          });
          param.shippingTemplatesRegionRequestList = this.ruleForm.region;
          param.shippingTemplatesRegionRequestList.forEach((el, index) => {
            this.$delete(el, 'city_ids');
          });
          this.ruleForm.free.forEach((el, index) => {
            el.title = el.city_ids.length > 0 ? JSON.stringify(el.city_ids) : JSON.stringify([[0, 0]]);
            if (el.title === '[[0,0]]') {
              el.cityId = '0';
            } else {
              for (var i = 0; i < el.city_ids.length; i++) {
                el.city_ids[i].splice(0, 2);
              }
              el.cityId = el.city_ids.length > 0 ? el.city_ids.join(',') : '0';
            }
          });
          param.shippingTemplatesFreeRequestList = this.ruleForm.free;
          param.shippingTemplatesFreeRequestList.forEach((el, index) => {
            this.$delete(el, 'city_ids');
          });
          if (this.ruleForm.appoint === 2) {
            this.ruleForm.region.map((item, index) => {
              this.ruleForm.region[0].title = '[[0,0]]';
              this.ruleForm.region[0].cityId = '0';
            });
          }
          if (this.ruleForm.appoint === 0) {
            this.$delete(param, 'shippingTemplatesRegionRequestList');
            this.$delete(param, 'shippingTemplatesFreeRequestList');
            this.ruleForm.type = 0;
          }
          if (this.type === 0) {
            logistics
              .shippingSave(param)
              .then((res) => {
                this.$message.success('操作成功');
                this.$emit('getList');
                setTimeout(() => {
                  this.handleClose();
                }, 800);
                this.loading = false;
              })
              .catch(() => {
                this.loading = false;
              });
          } else {
            logistics
              .shippingUpdate(param, { id: this.tempId })
              .then((res) => {
                this.$message.success('操作成功');
                this.$emit('getList');
                setTimeout(() => {
                  this.handleClose();
                  this.loading = false;
                  this.listLoading = false;
                }, 500);
              })
              .catch(() => {
                this.loading = false;
                this.listLoading = false;
              });
          }
        } else {
          return false;
        }
      });
    }),
    clear() {
      this.ruleForm.name = '';
      this.ruleForm.sort = 0;
    },
  },
};
</script>

<style scoped lang="scss">
.withs {
  width: 460px;
}
::v-deep .el-input__inner {
  height: 32px !important;
}
.noBox {
  ::v-deep .el-form-item__content {
    /*margin-left: 0 !important;*/
  }
}
.tempBox {
  ::v-deep .el-input-number--mini {
    width: 100px !important;
  }
}
::v-deep .drawer_fix {
  text-align: center;
  bottom: 10px;
}
::v-deep .el-drawer__header {
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  margin-bottom: 0;
}
.topCascader ::v-deep .el-cascader__tags {
  top: 95% !important;
}
.botCascader ::v-deep .el-cascader__tags {
  top: 50% !important;
}
::v-deep .el-drawer__body {
  padding: 30px 0 80px 0 !important;
}
</style>
