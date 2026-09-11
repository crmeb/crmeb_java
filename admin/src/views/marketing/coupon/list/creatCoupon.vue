<template>
  <div class="divBox">
    <pages-header ref="pageHeaderRef" title="添加优惠券" back-url="/marketing/coupon/list" />
    <el-card class="box-card mt14">
      <el-form :model="ruleForm" :rules="rules" ref="ruleFormRef" label-width="150px" class="demo-ruleForm">
        <el-form-item label="优惠劵名称：" prop="name">
          <el-input v-model="ruleForm.name" style="width: 350px" placeholder="请输入优惠券名称"></el-input>
        </el-form-item>
        <el-form-item label="优惠劵类型：">
          <el-radio-group v-model="ruleForm.useType">
            <el-radio :label="1" :value="1">通用券</el-radio>
            <el-radio :label="2" :value="2">商品券</el-radio>
            <el-radio :label="3" :value="3">品类券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择品类：" prop="primaryKey" v-if="ruleForm.useType === 3">
          <el-cascader
            v-model="ruleForm.primaryKey"
            :options="merCateList"
            :props="props2"
            clearable
            class="selWidth"
            :show-all-levels="false"
          />
        </el-form-item>
        <el-form-item label="商品：" v-if="ruleForm.useType === 2" prop="checked">
          <div class="acea-row">
            <template v-if="ruleForm.checked.length">
              <div class="pictrue" v-for="(item, index) in ruleForm.checked" :key="index">
                <img :src="item.image" />
                <i class="el-icon-error btndel" @click="handleRemove(index)" />
              </div>
            </template>
            <div class="upLoadPicBox" @click="changeGood">
              <div class="upLoad">
                <i class="el-icon-camera cameraIconfont" />
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="优惠券面值：" prop="money">
          <el-input-number
            controls-position="right"
            :max="99999.99"
            v-model="ruleForm.money"
            :min="1"
            label="描述文字"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="使用门槛：">
          <el-radio-group v-model="threshold">
            <el-radio :label="false" :value="false">无门槛</el-radio>
            <el-radio :label="true" :value="true">有门槛</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优惠券最低消费：" prop="minPrice" v-if="threshold">
          <el-input-number
            controls-position="right"
            v-model="ruleForm.minPrice"
            :min="1"
            label="描述文字"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="使用有效期：">
          <el-radio-group v-model="ruleForm.isFixedTime">
            <el-radio :label="false" :value="false">天数</el-radio>
            <el-radio :label="true" :value="true">时间段</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="使用有效期限（天）：" prop="day" v-if="!ruleForm.isFixedTime">
          <el-input-number
            controls-position="right"
            v-model="ruleForm.day"
            :min="0"
            :max="999"
            label="描述文字"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="使用有效期限：" prop="resource" v-if="ruleForm.isFixedTime">
          <el-date-picker
            style="width: 550px"
            v-model="termTime"
            type="datetimerange"
            range-separator="至"
            value-format="YYYY-MM-DD HH:mm:ss"
            start-placeholder="开始日期"
            :disabled-date="pickerOptions.disabledDate"
            end-placeholder="结束日期"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="领取是否限时：" prop="isForever">
          <el-radio-group v-model="ruleForm.isForever">
            <el-radio :label="true" :value="true">限时</el-radio>
            <el-radio :label="false" :value="false">不限时</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="领取时间：" v-if="ruleForm.isForever">
          <el-date-picker
            style="width: 550px"
            v-model="isForeverTime"
            type="datetimerange"
            range-separator="至"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="pickerOptions.disabledDate"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @blur="handleTimestamp"
          >
          </el-date-picker>
        </el-form-item>
        <el-form-item label="领取方式：" prop="resource">
          <el-radio-group v-model="ruleForm.type">
            <el-radio :label="1" :value="1">手动领取</el-radio>
            <el-radio :label="2" :value="2">新人券</el-radio>
            <el-radio :label="3" :value="3">赠送券</el-radio>
            <!--<el-radio :label="4" :value="4">付费会员卷</el-radio>-->
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否限量：" prop="isLimited">
          <el-radio-group v-model="ruleForm.isLimited">
            <el-radio :label="true" :value="true">限量</el-radio>
            <el-radio :label="false" :value="false">不限量</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发布数量：" prop="total" v-if="ruleForm.isLimited">
          <el-input-number controls-position="right" v-model="ruleForm.total" :min="1" label="排序"></el-input-number>
        </el-form-item>
        <el-form-item label="排序：" prop="sort">
          <el-input-number controls-position="right" v-model="ruleForm.sort" :min="0" label="排序"></el-input-number>
        </el-form-item>
        <el-form-item label="状态：" prop="status">
          <el-radio-group v-model="ruleForm.status">
            <el-radio :label="true" :value="true">开启</el-radio>
            <el-radio :label="false" :value="false">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="submitForm('ruleFormRef')"
            :loading="loading"
            v-hasPermi="['admin:coupon:save']"
            >立即创建</el-button
          >
          <!--<el-button @click="resetForm('ruleForm')">重置</el-button>-->
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { couponSaveApi, couponInfoApi } from '@/api/marketing';
import { categoryApi } from '@/api/store';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'creatCoupon' });

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const pickerOptions = {
  disabledDate(time) {
    // return time.getTime() < new Date().setTime(new Date().getTime() - 3600 * 1000 * 24); //不限制未来时间
    return time.getTime() < Date.now() - 8.64e7 || time.getTime() > Date.now() + 600 * 8.64e7; //限制未来时间
  },
};
const loading = ref(false);
const threshold = ref(false);
const termTime = ref([]);
const props2 = reactive({
  children: 'child',
  label: 'name',
  value: 'id',
  checkStrictly: true,
  emitPath: false,
});
const couponType = ref(0);
const term = ref('termday');
const merCateList = ref([]); // 商户分类筛选
const ruleForm = ref({
  useType: 1,
  isFixedTime: false,
  name: '',
  money: 1,
  minPrice: 1,
  day: null,
  isForever: false,
  primaryKey: '',
  type: 2,
  isLimited: false,
  useStartTime: '', // 使用
  useEndTime: '', // 结束
  receiveStartTime: '', //领取
  receiveEndTime: '',
  sort: 0,
  total: 1,
  status: false,
  checked: [],
});
const isForeverTime = ref([]);
const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  day: [{ required: true, message: '请输入使用有效期限（天）', trigger: 'blur' }],
  money: [{ required: true, message: '请输入优惠券面值', trigger: 'blur' }],
  primaryKey: [{ required: true, message: '请选择品类', trigger: 'change' }],
  checked: [{ required: true, message: '请至少选择一个商品', trigger: 'change', type: 'array' }],
  isForeverTime: [{ required: true, message: '请选择领取时间', trigger: 'change', type: 'array' }],
  total: [{ required: true, message: '请输入发布数量', trigger: 'blur' }],
  minPrice: [{ required: true, message: '请输入最低消费', trigger: 'blur' }],
};
const ruleFormRef = ref(null);
const pageHeaderRef = ref(null);

const handleTimestamp = () => {};
// 商品分类；
const getCategorySelect = () => {
  categoryApi({ status: -1, type: 1 }).then((res) => {
    merCateList.value = res;
    merCateList.value.map((item) => {
      item.disabled = true;
    });
  });
};
const getInfo = () => {
  loading.value = true;
  couponInfoApi({ id: route.query.copyId || route.params.id })
    .then((res) => {
      const info = res.coupon;
      ruleForm.value = {
        useType: info.useType,
        isFixedTime: info.isFixedTime,
        isForever: info.isForever,
        name: info.name,
        money: info.money,
        minPrice: info.minPrice,
        day: info.day,
        type: info.type,
        isLimited: info.isLimited,
        sort: info.sort,
        total: info.total,
        status: info.status,
        primaryKey: Number(info.primaryKey),
        checked: res.product || [],
      };
      info.minPrice == 0 ? (threshold.value = false) : (threshold.value = true);
      info.isForever
        ? (isForeverTime.value = [info.receiveStartTime, info.receiveEndTime])
        : (isForeverTime.value = []);
      info.isFixedTime && info.useStartTime && info.useEndTime
        ? (termTime.value = [info.useStartTime, info.useEndTime])
        : (termTime.value = []);
      loading.value = false;
    })
    .catch((res) => {
      loading.value = false;
      ElMessage.error(res.message);
    });
};
const handleRemove = (i) => {
  ruleForm.value.checked.splice(i, 1);
};
const changeGood = () => {
  proxy.$modalGoodList(
    function (row) {
      ruleForm.value.checked = row;
    },
    'many',
    ruleForm.value.checked,
  );
};
const submitForm = Debounce(function (formName) {
  if ((ruleForm.value.isFixedTime && !termTime.value) || (ruleForm.value.isFixedTime && !termTime.value.length))
    return ElMessage.warning('请选择使用有效期限');
  if ((ruleForm.value.isForever && !isForeverTime.value) || (ruleForm.value.isForever && !isForeverTime.value.length))
    return ElMessage.warning('请选择请选择领取时间');
  if (ruleForm.value.useType === 2)
    ruleForm.value.primaryKey = ruleForm.value.checked
      .map((item) => {
        return item.id;
      })
      .join(',');
  if (ruleForm.value.useType === 1) ruleForm.value.primaryKey = '';
  if (!threshold.value) ruleForm.value.minPrice = 0;
  if (!ruleForm.value.isLimited) ruleForm.value.total = 0;
  ruleForm.value.isFixedTime && termTime.value.length
    ? ((ruleForm.value.useStartTime = termTime.value[0]), (ruleForm.value.day = null))
    : (ruleForm.value.useStartTime = '');
  ruleForm.value.isFixedTime && termTime.value.length
    ? ((ruleForm.value.useEndTime = termTime.value[1]), (ruleForm.value.day = null))
    : (ruleForm.value.useEndTime = '');
  ruleForm.value.isForever && isForeverTime.value.length
    ? (ruleForm.value.receiveStartTime = isForeverTime.value[0])
    : (ruleForm.value.receiveStartTime = '');
  ruleForm.value.isForever && isForeverTime.value.length
    ? (ruleForm.value.receiveEndTime = isForeverTime.value[1])
    : (ruleForm.value.receiveEndTime = '');
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      couponSaveApi(ruleForm.value)
        .then(() => {
          ElMessage.success('新增成功');
          loading.value = false;
          setTimeout(() => {
            router.push({ path: `/marketing/coupon/list` });
          }, 200);
        })
        .catch(() => {
          loading.value = false;
        });
    } else {
      loading.value = false;
      return false;
    }
  });
});

onMounted(() => {
  getCategorySelect();
  if (route.query.copyId || route.params.id) getInfo();
});
</script>

<style scoped lang="scss">
.pictrue {
  width: 60px;
  height: 60px;
  border: 1px dotted rgba(0, 0, 0, 0.1);
  margin-right: 10px;
  position: relative;
  cursor: pointer;
  img {
    width: 100%;
    height: 100%;
  }
}
.btndel {
  position: absolute;
  z-index: 1;
  width: 20px !important;
  height: 20px !important;
  left: 43px;
  top: 1px;
}
</style>
