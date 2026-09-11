<template>
  <div>
    <el-form
      ref="formDynamicRef"

      :model="formDynamic"
      v-loading="loading"
      :rules="rules"
      class="attrFrom mb20"
      label-width="75px"
      @submit.prevent
    >
      <el-row :gutter="24">
        <el-col :span="8">
          <el-form-item label="规格名称：" prop="ruleName">
            <el-input class="selWidth" maxlength="20" v-model="formDynamic.ruleName" placeholder="请输入标题名称" />
          </el-form-item>
        </el-col>
        <el-col v-for="(item, index) in formDynamic.ruleValue" :key="index" :span="24" class="noForm">
          <el-form-item>
            <div class="acea-row row-middle">
              <span class="mr5">{{ item.value }}</span
              ><i class="el-icon-circle-close" @click="handleRemove(index)" />
            </div>
            <div class="rulesBox">
              <el-tag
                v-for="(j, indexn) in item.detail"
                :key="indexn"
                closable
                size="default"
                :disable-transitions="false"
                class="mb5 mr10"
                @close="handleClose(item.detail, indexn)"
              >
                {{ j }}
              </el-tag>
              <el-input
                v-if="item.inputVisible"
                ref="saveTagInput"
                v-model="item.detail.attrsVal"
                class="input-new-tag"

                @keyup.enter="createAttr(item.detail.attrsVal, index)"
                @blur="createAttr(item.detail.attrsVal, index)"
              />
              <el-button v-else class="button-new-tag" @click="showInput(item)">+ 添加</el-button>
            </div>
          </el-form-item>
        </el-col>
        <el-col v-if="isBtn" :span="24" style="padding-left: 0; padding-right: 0">
          <el-col :span="9">
            <el-form-item label="规格：">
              <el-input class="selWidth" v-model="attrsName" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
          <el-col :span="9">
            <el-form-item label="规格值：">
              <el-input class="selWidth" v-model="attrsVal" placeholder="请输入规格值" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-button @click="offAttrName">取消</el-button>
            <el-button type="primary" class="mr10" @click="createAttrName">确定</el-button>
          </el-col>
        </el-col>
        <Spin v-if="spinShow" size="large" fix />
      </el-row>
      <el-button v-if="!isBtn" type="primary" icon="md-add" class="ml75" @click="addBtn">添加新规格</el-button>
    </el-form>
    <span class="footer acea-row">
      <el-button @click="resetForm('formDynamic')">取消</el-button>
      <el-button type="primary" :loading="loadingBtn" @click="handleSubmit('formDynamic')">确定</el-button>
    </span>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import { attrCreatApi, attrEditApi } from '@/api/store';

defineOptions({ name: 'CreatAttr' });

const props = defineProps({
  currentRow: {
    type: Object,
    default: null,
  },
  keyNum: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(['getList']);

const formDynamicRef = ref(null);

const loadingBtn = ref(false);
const loading = ref(false);
const dialogVisible = ref(false);
const inputVisible = ref(false);
const inputValue = ref('');
const spinShow = ref(false);
const grid = ref({
  xl: 3,
  lg: 3,
  md: 12,
  sm: 24,
  xs: 24,
});
const modal = ref(false);
const index = ref(1);
const rules = ref({
  ruleName: [{ required: true, message: '请输入规格名称', trigger: 'blur' }],
});
const formDynamic = ref({
  ruleName: '',
  ruleValue: [],
});
const attrsName = ref('');
const attrsVal = ref('');
const isBtn = ref(false);
const results = ref([]);
const result = ref([]);
const ids = ref(0);

watch(
  () => props.currentRow,
  (val, oldVal) => {
    formDynamic.value = val;
  },
  { immediate: true },
);

watch(
  () => props.keyNum,
  (val) => {
    if (val > 0) clear();
  },
  { deep: true },
);

onMounted(() => {
  formDynamic.value.ruleValue.map((item) => {
    item.inputVisible = false;
  });
});

function resetForm(formName) {
  ElMessageBox.close();
  clear();
  formDynamicRef.value.resetFields();
}

// 添加按钮
function addBtn() {
  isBtn.value = true;
}

function handleClose(item, index) {
  item.splice(index, 1);
}

// 取消
function offAttrName() {
  isBtn.value = false;
}

// 删除
function handleRemove(index) {
  formDynamic.value.ruleValue.splice(index, 1);
}

// 添加规则名称
function createAttrName() {
  if (attrsName.value && attrsVal.value) {
    const data = {
      value: attrsName.value,
      detail: [attrsVal.value],
    };
    formDynamic.value.ruleValue.push(data);
    var hash = {};
    formDynamic.value.ruleValue = formDynamic.value.ruleValue.reduce(function (item, next) {
      /* eslint-disable */
      hash[next.value] ? '' : (hash[next.value] = true && item.push(next));
      return item;
    }, []);
    attrsName.value = '';
    attrsVal.value = '';
    isBtn.value = false;
  } else {
    ElMessage.warning('请添加规格名称');
  }
}

// 添加属性
function createAttr(num, idx) {
  if (num) {
    formDynamic.value.ruleValue[idx].detail.push(num);
    var hash = {};
    formDynamic.value.ruleValue[idx].detail = formDynamic.value.ruleValue[idx].detail.reduce(function (item, next) {
      /* eslint-disable */
      hash[next] ? '' : (hash[next] = true && item.push(next));
      return item;
    }, []);
    formDynamic.value.ruleValue[idx].inputVisible = false;
  }
}

function showInput(item) {
  item.inputVisible = true;
}

// 提交
function handleSubmit(name) {
  const data = {
    id: props.currentRow.id || 0,
    ruleName: formDynamic.value.ruleName,
    ruleValue: JSON.stringify(formDynamic.value.ruleValue),
  };
  formDynamicRef.value.validate((valid) => {
    if (valid) {
      if (formDynamic.value.ruleValue.length === 0) {
        return ElMessage.warning('请至少添加一条属性规格！');
      }
      loadingBtn.value = true;
      loading.value = true;
      setTimeout(() => {
        props.currentRow.id
          ? attrEditApi(data)
              .then((res) => {
                ElMessage.success('提交成功');
                ElMessageBox.close();
                clear();
                emit('getList');
                loading.value = false;
                loadingBtn.value = false;
              })
              .catch(() => {
                loading.value = false;
                loadingBtn.value = false;
              })
          : attrCreatApi(data)
              .then((res) => {
                ElMessage.success('提交成功');
                ElMessageBox.close();
                emit('getList');
                clear();
                loading.value = false;
                loadingBtn.value = false;
              })
              .catch(() => {
                loading.value = false;
                loadingBtn.value = false;
              });
      }, 1200);
    } else {
      loading.value = false;
      loadingBtn.value = false;
      return false;
    }
  });
}

function clear() {
  formDynamicRef.value.resetFields();
  formDynamic.value.ruleValue = [];
  formDynamic.value.ruleName = '';
  isBtn.value = false;
  attrsName.value = '';
  attrsVal.value = '';
}

function handleInputConfirm() {
  const inputValueVal = inputValue.value;
  if (inputValueVal) {
    dynamicTags.value.push(inputValueVal);
  }
  inputVisible.value = false;
  inputValue.value = '';
}
</script>

<style scoped lang="scss">
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}

.footer {
  justify-content: flex-end;
}
.el-message-box {
  width: 900px;
}
.noForm {
  text-align: left;
}
[role='dialog'] .el-col-24:last-of-type {
  padding-top: 0 !important;
}
.ml75 {
  margin-left: 75px;
}
:deep([role='dialog'] .el-message-box) {
  padding-bottom: 10px !important;
}
</style>
