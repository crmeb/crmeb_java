<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-row :gutter="30">
        <el-col v-bind="grid" class="left mb15 ml40">
          <div>
            <img class="top" src="@/assets/imgs/mobilehead.png" />
            <img class="bottom" src="@/assets/imgs/mobilefoot.png" />
            <div style="background: #f4f5f9; min-height: 438px; position: absolute; top: 63px; width: 320px" />
            <div class="textbot">
              <div v-for="(item, indx) in list" :key="indx" class="li" :class="{ active: item === formValidate }">
                <div>
                  <div class="add" @click="add(item, indx)">
                    <i class="el-icon-plus" />
                    <div class="arrow" />
                  </div>
                  <div class="add-btn">
                    <div
                      v-for="(j, index) in item.sub_button"
                      :key="index"
                      class="addadd menuBox"
                      :class="{ active: j === formValidate }"
                      @click="gettem(j, index, indx)"
                    >
                      <el-tooltip class="item" effect="dark" :content="j.name" placement="top-start">
                        <el-button>{{ j.name || '二级菜单' }}</el-button>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
                <div class="text menuBox" @click="gettem(item, indx, null)">
                  <el-tooltip class="item" effect="dark" :content="item.name" placement="top-start">
                    <el-button>{{ item.name || '一级菜单' }}</el-button>
                  </el-tooltip>
                </div>
              </div>
              <div v-show="list.length < 3" class="li">
                <div class="text" @click="addtext"><i class="el-icon-plus" /></div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xl="11" :lg="12" :md="22" :sm="22" :xs="22">
          <div v-if="checkedMenuId !== null">
            <div class="dividerTitle acea-row row-between row-bottom">
              <span class="title">菜单信息</span>
              <el-button

                type="danger"
                @click="deltMenus"
                v-hasPermi="['admin:wechat:menu:public:delete']"
                >删除</el-button
              >
              <el-divider />
            </div>
            <el-col :span="24" class="userAlert">
              <div class="box-card right">
                <el-alert class="mb15" title="已添加子菜单，仅可设置菜单名称" type="success" show-icon />
                <el-form
                  ref="formValidateRef"
                  :model="formValidate"
                  :rules="ruleValidate"
                  label-width="100px"
                  class="mt20"
                >
                  <el-form-item label="菜单名称" prop="name">
                    <el-input v-model="formValidate.name" placeholder="请填写菜单名称" class="spwidth" />
                  </el-form-item>
                  <el-form-item label="规则状态" prop="type">
                    <el-select v-model="formValidate.type" placeholder="请选择规则状态" class="spwidth">
                      <el-option value="click" label="关键字">关键字</el-option>
                      <el-option value="view" label="跳转网页">跳转网页</el-option>
                      <el-option value="miniprogram" label="小程序">小程序</el-option>
                    </el-select>
                  </el-form-item>
                  <div v-if="formValidate.type === 'click'">
                    <el-form-item label="关键字" prop="key">
                      <el-input v-model="formValidate.key" placeholder="请填写关键字" class="spwidth" />
                    </el-form-item>
                  </div>
                  <div v-if="formValidate.type === 'miniprogram'">
                    <el-form-item label="appid" prop="appid">
                      <el-input v-model="formValidate.appid" placeholder="请填写appid" class="spwidth" />
                    </el-form-item>
                    <el-form-item label="备用网页" prop="url">
                      <el-input v-model="formValidate.url" placeholder="请填写备用网页" class="spwidth" />
                    </el-form-item>
                    <el-form-item label="小程序路径" prop="pagepath">
                      <el-input v-model="formValidate.pagepath" placeholder="请填写小程序路径" class="spwidth" />
                    </el-form-item>
                  </div>
                  <div v-if="formValidate.type === 'view'">
                    <el-form-item label="跳转地址" prop="url">
                      <el-input v-model="formValidate.url" placeholder="请填写跳转地址" class="spwidth" />
                    </el-form-item>
                  </div>
                </el-form>
              </div>
            </el-col>
          </div>
          <el-col v-if="isTrue" :span="24">
            <el-button
              type="primary"
              style="display: block; margin: 10px auto"
              @click="submenus('formValidate')"
              v-hasPermi="['admin:wechat:menu:public:create']"
              >保存并发布</el-button
            >
          </el-col>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage } from '@/utils/elementPlusFeedback';
import { wechatMenuApi, wechatMenuAddApi } from '@/api/wxApi';
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'WechatMenus' });

const { proxy } = getCurrentInstance();

const grid = reactive({
  xl: 8,
  lg: 8,
  md: 8,
  sm: 8,
  xs: 24,
});
const grid2 = reactive({
  xl: 16,
  lg: 16,
  md: 16,
  sm: 16,
  xs: 24,
});
const modal2 = ref(false);
const formValidate = ref({
  name: '',
  type: 'click',
  appid: '',
  url: '',
  key: '',
  pagepath: '',
  id: 0,
});
const ruleValidate = {
  name: [{ required: true, message: '请填写菜单名称', trigger: 'blur' }],
  key: [{ required: true, message: '请填写关键字', trigger: 'blur' }],
  appid: [{ required: true, message: '请填写appid', trigger: 'blur' }],
  pagepath: [{ required: true, message: '请填写小程序路径', trigger: 'blur' }],
  url: [{ required: true, message: '请填写跳转地址', trigger: 'blur' }],
  type: [{ required: true, message: '请选择规则状态', trigger: 'change' }],
};
const parentMenuId = ref(null);
const list = ref([]);
const checkedMenuId = ref(null);
const isTrue = ref(false);
const formValidateRef = ref(null);

// 添加一级字段函数
const defaultMenusData = () => {
  return {
    type: 'click',
    name: '',
    sub_button: [],
  };
};
// 添加二级字段函数
const defaultChildData = () => {
  return {
    type: 'click',
    name: '',
  };
};
// 获取 菜单
const getMenus = () => {
  wechatMenuApi().then(async (res) => {
    const data = res.menu;
    list.value = data.button;
  });
};
// 点击保存提交
const submenus = Debounce(function (name) {
  if (isTrue.value && !checkedMenuId.value && checkedMenuId.value !== 0) {
    putData();
  } else {
    formValidateRef.value.validate((valid) => {
      if (valid) {
        putData();
      } else {
        if (!check()) return false;
      }
    });
  }
});
// 新增data
const putData = () => {
  const data = {
    button: list.value,
  };
  wechatMenuAddApi(data).then(async (res) => {
    ElMessage.success('提交成功');
    checkedMenuId.value = null;
    formValidate.value = {};
    isTrue.value = false;
  });
};
// 点击元素
const gettem = (item, index, pid) => {
  checkedMenuId.value = index;
  formValidate.value = item;
  parentMenuId.value = pid;
  isTrue.value = true;
};
// 增加二级
const add = (item, index) => {
  if (!check()) return false;
  if (item.sub_button.length < 5) {
    const data = defaultChildData();
    const id = item.sub_button.length;
    item.sub_button.push(data);
    formValidate.value = data;
    checkedMenuId.value = id;
    parentMenuId.value = index;
    isTrue.value = true;
  }
};
// 增加一级
const addtext = () => {
  if (!check()) return false;
  const data = defaultMenusData();
  const id = list.value.length;
  list.value.push(data);
  formValidate.value = data;
  checkedMenuId.value = id;
  parentMenuId.value = null;
  isTrue.value = true;
};
// 判断函数
const check = () => {
  const reg = /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?/;
  if (checkedMenuId.value === null) return true;
  if (!isTrue.value) return true;
  if (!formValidate.value.name) {
    ElMessage.warning('请输入按钮名称!');
    return false;
  }
  if (formValidate.value.type === 'click' && !formValidate.value.key) {
    ElMessage.warning('请输入关键字!');
    return false;
  }
  if (formValidate.value.type === 'view' && !reg.test(formValidate.value.url)) {
    ElMessage.warning('请输入正确的跳转地址!');
    return false;
  }
  if (
    formValidate.value.type === 'miniprogram' &&
    (!formValidate.value.appid || !formValidate.value.pagepath || !formValidate.value.url)
  ) {
    ElMessage.warning('请填写完整小程序配置!');
    return false;
  }
  return true;
};
// 删除
const deltMenus = () => {
  if (isTrue.value) {
    proxy.$modalSure().then(() => {
      del();
    });
  } else {
    ElMessage.warning('请选择菜单!');
  }
};
// 确认删除
const del = () => {
  parentMenuId.value === null
    ? list.value.splice(checkedMenuId.value, 1)
    : list.value[parentMenuId.value].sub_button.splice(checkedMenuId.value, 1);
  parentMenuId.value = null;
  formValidate.value = {
    name: '',
    type: 'click',
    appid: '',
    url: '',
    key: '',
    pagepath: '',
    id: 0,
  };
  isTrue.value = false;
  modal2.value = false;
  checkedMenuId.value = null;
  formValidateRef.value.resetFields();
  submenus('formValidate');
};

onMounted(() => {
  getMenus();
  if (list.value.length) {
    formValidate.value = list.value[activeClass];
  } else {
    return formValidate.value;
  }
});
</script>

<style scoped lang="scss">
.menuBox {
  :deep(.el-button ){
    border: none;
    background: bottom;
    padding: 0 !important;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 100%;
  }
}
* {
  -moz-user-select: none; /*火狐*/
  -webkit-user-select: none; /*webkit浏览器*/
  -ms-user-select: none; /*IE10*/
  -khtml-user-select: none; /*早期浏览器*/
  user-select: none;
}
.title {
  margin-bottom: -19px !important;
  padding-bottom: 17px !important;
}
.left {
  min-width: 390px;
  min-height: 550px;
  position: relative;
  padding-left: 40px;
}

.top {
  position: absolute;
  top: 0px;
}

.bottom {
  position: absolute;
  bottom: 0px;
}

.textbot {
  position: absolute;
  bottom: 0px;
  left: 59px;
  width: 100%;
}
.active {
  border: 1px solid #44b549 !important;
  color: #44b549 !important;
}
.li {
  float: left;
  width: 93px;
  line-height: 48px;
  border: 1px solid #e7e7eb;
  background: #fafafa;
  text-align: center;
  cursor: pointer;
  color: #999;
  position: relative;
}
.text {
  height: 50px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.text:hover {
  color: #000;
}

.add {
  position: absolute;
  bottom: 65px;
  width: 100%;
  line-height: 48px;
  border: 1px solid #e7e7eb;
  background: #fafafa;
}
.arrow {
  position: absolute;
  bottom: -16px;
  left: 36px;
  /* 圆角的位置需要细心调试哦 */
  width: 0;
  height: 0;
  font-size: 0;
  border: solid 8px;
  border-color: #fff #f4f5f9 #f4f5f9 #f4f5f9;
}
.add-btn {
  position: absolute;
  bottom: 115px;
  width: 100%;
  line-height: 48px;
  background: #fafafa;
}
.addadd {
  width: 100%;
  line-height: 48px;
  border: 1px solid #e7e7eb;
  background: #fafafa;
  height: 48px;
  padding: 0 5px;
  box-sizing: border-box;
}
.right {
  background: #fff;
  min-height: 400px;
}
.spwidth {
  width: 100%;
}
</style>
