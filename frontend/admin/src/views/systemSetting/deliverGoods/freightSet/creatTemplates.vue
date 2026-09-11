<template>
  <el-drawer
    v-if="dialogVisible"
    title="运费模板"
    v-model="dialogVisible"
    size="1200px"
    direction="rtl"
    :before-close="handleClose"
    :append-to-body="true"
    :modal-append-to-body="false"
    :wrapperClosable="false"
    :show-close="true"
  >
    <div class="demo-drawer__content">
      <el-form ref="ruleFormRef" :model="ruleForm" label-width="100px" v-if="dialogVisible" :rules="rules">
        <el-form-item label="模板名称：" prop="name">
          <el-input v-model="ruleForm.name" class="withs" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="包邮方式：" prop="appoint">
          <el-radio-group v-model="ruleForm.appoint" @change="changeAppoint">
            <el-radio :label="0" :value="0">全国包邮</el-radio>
            <el-radio :label="1" :value="1">部分包邮</el-radio>
            <el-radio :label="2" :value="2">自定义</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="Number(ruleForm.appoint) > 0">
          <el-form-item label="计费方式：" prop="type">
            <el-radio-group v-model="ruleForm.type" @change="changeRadio(ruleForm.type)">
              <el-radio :label="1" :value="1">按件数</el-radio>
              <el-radio :label="2" :value="2">按重量</el-radio>
              <el-radio :label="3" :value="3">按体积</el-radio>
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

              class="tempBox"
            >
              <el-table-column label="送达到" min-width="260" prop="city_ids">
                <template #default="scope">
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
                <template #default="scope">
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
                <template #default="scope">
                  <el-form-item :rules="rules.firstPrice" :prop="'region.' + scope.$index + '.firstPrice'">
                    <el-input-number v-model="scope.row.firstPrice" controls-position="right" :min="0" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column min-width="120px" :label="columns.title2" prop="renewal">
                <template #default="scope">
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
                <template #default="scope">
                  <el-form-item :rules="rules.renewalPrice" :prop="'region.' + scope.$index + '.renewalPrice'">
                    <el-input-number v-model="scope.row.renewalPrice" controls-position="right" :min="0" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button
                    v-if="ruleForm.appoint === 1 || (ruleForm.appoint !== 1 && scope.$index > 0)"
                    type="text"

                    @click="confirmEdit(ruleForm.region, scope.$index)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item>
            <el-button class="mt20 mb20" type="primary" :icon="Edit" @click="addRegion(ruleForm.region)">
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

            >
              <el-table-column label="选择区域" min-width="220">
                <template #default="{ row }">
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
                <template #default="{ row }">
                  <el-input-number
                    v-model="row.number"
                    controls-position="right"
                    :step-strictly="ruleForm.type === 1 ? true : false"
                    :min="ruleForm.type === 1 ? 1 : 0.1"
                  />
                </template>
              </el-table-column>
              <el-table-column min-width="120px" label="包邮金额（元）">
                <template #default="{ row }">
                  <el-input-number v-model="row.price" controls-position="right" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button link @click="confirmEdit(ruleForm.free, scope.$index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item v-if="ruleForm.appoint === 2">
            <el-button class="mt20 mb20" type="primary" :icon="Edit" @click="addFree(ruleForm.free)">
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
    <template #footer>
      <span class="dialog-footer"> </span>
    </template>
  </el-drawer>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick } from 'vue';
import { ElMessage, ElLoading } from '@/utils/elementPlusFeedback';
import { Edit } from '@element-plus/icons-vue';
import * as logistics from '@/api/logistics';
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

defineOptions({ name: 'CreatTemplates' });

const emit = defineEmits(['getList']);

const loading = ref(false);
const rules = reactive({
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
});
const nodeKey = ref('city_ids');
const props = reactive({
  children: 'child',
  label: 'name',
  value: 'cityId',
  multiple: true,
});
const dialogVisible = ref(false);
const ruleForm = reactive(Object.assign({}, defaultRole));
const listLoading = ref(false);
const cityList = ref([]);
const columns = reactive({
  title: '首件',
  title2: '续件',
  title3: '包邮件数',
});
const tempId = ref(0);
const regionNew = ref([
  {
    first: 1,
    firstPrice: 0,
    renewal: 0,
    renewalPrice: 0,
    city_ids: [],
    cityId: 0,
  },
]);
const type = ref(0); // 0添加 1编辑

const ruleFormRef = ref(null);

function changeAppoint() {
  listLoading.value = false;
  let region = [...ruleForm.region];
}
function changType(_type) {
  type.value = _type;
}
function confirmEdit(row, index) {
  row.splice(index, 1);
}
function popoverHide() {}
function handleClose() {
  dialogVisible.value = false;
  Object.assign(ruleForm, {
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
  });
  Object.assign(columns, statusMap[0]);
}
function changeRegion(value) {}
function changeRadio(num) {
  Object.assign(columns, statusMap[num - 1]);
}
// 添加配送区域
function addRegion(region) {
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
}
function addFree(Free) {
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
}

/**
 * 详情
 * id 模板id
 * appoint true包邮 false不包邮
 **/
function getInfo(id, appoint) {
  tempId.value = id;
  const loadingInstance = ElLoading.service({ fullscreen: true });
  logistics
    .templateDetailApi({ id })
    .then((res) => {
      dialogVisible.value = true;
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
      Object.assign(ruleForm, {
        name: info.name,
        type: info.type,
        appoint: info.appoint,
        sort: info.sort,
        region: info.regionList || [], // 运费区域
        free: info.freeList || [], // 包邮区域
      });
      regionNew.value = [...ruleForm.region];
      Object.assign(columns, statusMap[ruleForm.type - 1]);
      nextTick(() => {
        loadingInstance.close();
      });
    })
    .catch((res) => {
      // console.integralLog(res)
      ElMessage.error(res.message);
      nextTick(() => {
        loadingInstance.close();
      });
    });
}
// 列表
function getCityList() {
  logistics
    .cityListTree()
    .then((res) => {
      sessionStorage.setItem('cityList', JSON.stringify(res));
      let _cityList = JSON.parse(sessionStorage.getItem('cityList'));
      cityList.value = _cityList;
    })
    .catch((res) => {
      ElMessage.error(res.message);
    });
}
function change(idBox) {
  idBox.map((item) => {
    const ids = [];
    item.city_ids.map((j) => {
      j.splice(0, 1);
      ids.push(j[0]);
    });
    item.city_ids = ids;
  });
  return idBox;
}
function changeOne(idBox) {
  const city_ids = [];
  idBox.map((item) => {
    item.splice(0, 1);
    city_ids.push(item[0]);
  });
  return city_ids;
}
const onsubmit = Debounce(function (formName) {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      listLoading.value = true;
      const param = {
        appoint: ruleForm.appoint,
        name: ruleForm.name,
        sort: ruleForm.sort,
        type: ruleForm.type,
        // 配送区域及运费
        shippingTemplatesRegionRequestList: [],
        // 指定包邮设置
        shippingTemplatesFreeRequestList: [],
      };
      ruleForm.region.forEach((el, index) => {
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
      param.shippingTemplatesRegionRequestList = ruleForm.region;
      param.shippingTemplatesRegionRequestList.forEach((el, index) => {
        delete el.city_ids;
      });
      ruleForm.free.forEach((el, index) => {
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
      param.shippingTemplatesFreeRequestList = ruleForm.free;
      param.shippingTemplatesFreeRequestList.forEach((el, index) => {
        delete el.city_ids;
      });
      if (ruleForm.appoint === 2) {
        ruleForm.region.map((item, index) => {
          ruleForm.region[0].title = '[[0,0]]';
          ruleForm.region[0].cityId = '0';
        });
      }
      if (ruleForm.appoint === 0) {
        delete param.shippingTemplatesRegionRequestList;
        delete param.shippingTemplatesFreeRequestList;
        ruleForm.type = 0;
      }
      if (type.value === 0) {
        logistics
          .shippingSave(param)
          .then((res) => {
            ElMessage.success('操作成功');
            emit('getList');
            setTimeout(() => {
              handleClose();
            }, 800);
            loading.value = false;
          })
          .catch(() => {
            loading.value = false;
          });
      } else {
        logistics
          .shippingUpdate(param, { id: tempId.value })
          .then((res) => {
            ElMessage.success('操作成功');
            emit('getList');
            setTimeout(() => {
              handleClose();
              loading.value = false;
              listLoading.value = false;
            }, 500);
          })
          .catch(() => {
            loading.value = false;
            listLoading.value = false;
          });
      }
    } else {
      return false;
    }
  });
});
function clear() {
  ruleForm.name = '';
  ruleForm.sort = 0;
}

onMounted(() => {
  listLoading.value = false;
  let _cityList = JSON.parse(sessionStorage.getItem('cityList'));
  cityList.value = _cityList;
});

defineExpose({ dialogVisible, getCityList, changType, getInfo });
</script>

<style scoped lang="scss">
.withs {
  width: 460px;
}
.noBox {
  :deep(.el-form-item__content) {
    /*margin-left: 0 !important;*/
  }
}
// 运费表格内表单对齐：各列内容高度一致（上下 20px 与全局 el-form-item 边距对应），行内垂直居中
.tempBox {
  :deep(.el-input-number) {
    width: 100%;
  }
  :deep(.el-form-item) {
    margin-top: 20px;
  }
  :deep(.cell .el-button) {
    margin: 20px 0;
  }
  :deep(.cell > span) {
    display: inline-block;
    line-height: 32px;
    margin: 20px 0;
  }
}
// 送达到 cascader：块级布局行内居中；固定高度避免多选标签撑高，标签单行折叠显示
:deep(.topCascader) {
  display: block;
  margin: 20px 0;
  .el-input__inner {
    height: 32px !important;
  }
  .el-cascader__tags {
    flex-wrap: nowrap;
    .el-tag {
      max-width: calc(100% - 46px);
    }
  }
}
// 包邮区域 cascader：同样固定高度、单行折叠标签
:deep(.botCascader) {
  .el-input__inner {
    height: 32px !important;
  }
  .el-cascader__tags {
    flex-wrap: nowrap;
  }
}
:deep(.drawer_fix) {
  text-align: center;
  bottom: 10px;
}
:deep(.el-drawer__header) {
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  margin-bottom: 0;
}
:deep(.el-drawer__body) {
  padding: 30px 0 80px 0 !important;
}
</style>
