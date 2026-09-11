<template>
  <div class="divBox relative">
    <el-card :bordered="false" shadow="never" class="ivu-mt" :body-style="{ padding: 0 }">
      <div class="padding-add">
        <el-form
          inline

          :model="userFrom"
          ref="userFromRef"
          :label-position="labelPosition"
          label-width="75px"
        >
          <div class="search-box flex flex-between">
            <div class="acea-row search-form">
              <div class="search-form-box">
                <el-form-item label="用户搜索：">
                  <UserSearchInput ref="userSearchInputRef" v-model="userFrom" @searchList="userSearchs" />
                </el-form-item>
                <el-form-item label="用户标签：">
                  <el-select
                    v-model="labelData"
                    @visible-change="userSearchs"
                    @remove-tag="userSearchs"
                    @clear="userSearchs"
                    placeholder="请选择"
                    class="selWidth"
                    clearable
                    filterable
                    multiple
                  >
                    <el-option
                      :value="item.id"
                      v-for="(item, index) in labelLists"
                      :key="index"
                      :label="item.name"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="消费情况：">
                  <el-select
                    v-model="userFrom.payCount"
                    @visible-change="userSearchs"
                    @clear="userSearchs"
                    placeholder="请选择"
                    class="selWidth"
                    clearable
                  >
                    <!-- <el-option value="" label="全部"></el-option> -->
                    <el-option value="0" label="0"></el-option>
                    <el-option value="1" label="1+"></el-option>
                    <el-option value="2" label="2+"></el-option>
                    <el-option value="3" label="3+"></el-option>
                    <el-option value="4" label="4+"></el-option>
                    <el-option value="5" label="5+"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="用户分组：" v-if="collapse">
                  <el-select
                    v-model="groupData"
                    @visible-change="userSearchs"
                    @remove-tag="userSearchs"
                    @clear="userSearchs"
                    placeholder="请选择"
                    class="selWidth"
                    clearable
                    filterable
                    multiple
                  >
                    <el-option
                      :value="item.id"
                      v-for="(item, index) in groupList"
                      :key="index"
                      :label="item.groupName"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="国家：" v-if="collapse">
                  <el-select
                    v-model="userFrom.country"
                    @visible-change="userSearchs"
                    @clear="userSearchs"
                    placeholder="请选择"
                    class="selWidth"
                    clearable
                    @on-change="changeCountry"
                  >
                    <!-- <el-option value="" ></el-option> -->
                    <el-option value="CN" label="中国"></el-option>
                    <el-option value="OTHER" label="国外"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="省份：" v-if="collapse">
                  <el-cascader
                    :options="addresData"
                    :props="propsCity"
                    filterable
                    v-model="address"
                    @clear="userSearchs"
                    @change="handleChange"
                    clearable
                    class="selWidth"
                  ></el-cascader>
                </el-form-item>
                <el-form-item label="访问情况：" v-if="collapse">
                  <el-select
                    v-model="userFrom.accessType"
                    @visible-change="userSearchs"
                    @clear="userSearchs"
                    placeholder="请选择"
                    class="selWidth"
                    clearable
                  >
                    <!-- <el-option :value="0" label="全部"></el-option> -->
                    <el-option :value="1" label="首次访问"></el-option>
                    <el-option :value="2" label="时间段访问过"></el-option>
                    <el-option :value="3" label="时间段未访问"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="时间选择：" v-if="collapse && userFrom.accessType">
                  <optionDatePicker v-model="timeVal" @changeOptTime="onchangeTime"></optionDatePicker>
                  <!-- <el-date-picker
                  v-model="timeVal"
                  align="right"
                  unlink-panels
                  value-format="YYYY-MM-DD"
                  format="YYYY-MM-DD"

                  type="daterange"
                  placement="bottom-end"
                  placeholder="自定义时间"
                  class="selWidth"
                  @change="onchangeTime"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                /> -->
                </el-form-item>
                <el-form-item label="性别：" v-if="collapse">
                  <el-select
                    v-model="userFrom.sex"
                    @visible-change="userSearchs"
                    @clear="userSearchs"
                    placeholder="请选择"
                    class="selWidth"
                  >
                    <el-option :value="0" label="未知"></el-option>
                    <el-option :value="1" label="男"></el-option>
                    <el-option :value="2" label="女"></el-option>
                    <el-option :value="3" label="保密"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="身份：" v-if="collapse">
                  <el-select
                    v-model="userFrom.isPromoter"
                    @visible-change="userSearchs"
                    @clear="userSearchs"
                    placeholder="请选择"
                    class="selWidth"
                  >
                    <el-option :value="1" label="推广员"></el-option>
                    <el-option :value="0" label="普通用户"></el-option>
                  </el-select>
                </el-form-item>
              </div>
            </div>
            <div class="search-btn-group-box">
              <el-form-item class="search-form-sub">
                <el-button type="primary" :icon="Search" label="default" @click="userSearchs">搜索</el-button>
                <el-button class="ResetSearch mr14" @click="reset('userFrom')">重置</el-button>
                <a class="ivu-ml-8" @click="collapse = !collapse">
                  <template v-if="!collapse"> 展开 <i class="el-icon-arrow-down"></i> </template>
                  <template v-else> 收起 <i class="el-icon-arrow-up"></i> </template>
                </a>
              </el-form-item>
            </div>
          </div>
        </el-form>
      </div>
    </el-card>
    <el-card class="box-card mt14">
      <template #header>
        <div class="clearfix">
          <el-tabs v-model="loginType" @tab-change="getList(1)">
            <el-tab-pane :label="item.name" :name="item.type.toString()" v-for="(item, index) in headeNum" :key="index" />
          </el-tabs>
          <div>
            <el-button @click="showCreateUser" type="primary" v-hasPermi="['admin:user:save']">新增用户</el-button>
            <el-button @click="onSend" type="primary" v-hasPermi="['admin:coupon:user:receive']">发送优惠券</el-button>
            <el-button :disabled="!selectionList.length" @click="setBatch('group')" v-hasPermi="['admin:user:group']"
              >批量设置分组</el-button
            >
            <el-button :disabled="!selectionList.length" @click="setBatch('label')" v-hasPermi="['admin:user:tag']"
              >批量设置标签</el-button
            >
          </div>
        </div>
      </template>
      <el-table
        ref="tableRef"
        v-loading="listLoading"
        :data="tableData.data"
        style="width: 100%"

        @selection-change="onSelectTab"
        highlight-current-row
      >
        <el-table-column type="expand">
          <template #default="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="身份：">
                <span>{{ $filters.filterIsPromoter(props.row.isPromoter) }}</span>
              </el-form-item>
              <el-form-item label="首次访问：">
                <span>{{ $filters.filterEmpty(props.row.createTime) }}</span>
              </el-form-item>
              <el-form-item label="近次访问：">
                <span>{{ $filters.filterEmpty(props.row.lastLoginTime) }}</span>
              </el-form-item>
              <el-form-item label="手机号：">
                <span>{{ $filters.filterEmpty(props.row.phone) }}</span>
              </el-form-item>
              <el-form-item label="标签：">
                <span>{{ $filters.filterEmpty(props.row.tagName) }}</span>
              </el-form-item>
              <el-form-item label="地址：">
                <span>{{ $filters.filterEmpty(props.row.addres) }}</span>
              </el-form-item>
              <el-form-item label="备注：" style="width: 100%; display: flex; margin-right: 10px">
                <span>{{ $filters.filterEmpty(props.row.mark) }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column prop="uid" label="ID" min-width="80" v-if="checkedCities.includes('ID')" />
        <el-table-column label="头像" min-width="80" v-if="checkedCities.includes('头像')">
          <template #default="scope">
            <div class="demo-image__preview">
              <el-image
                style="width: 36px; height: 36px"
                :src="scope.row.avatar"
                :preview-src-list="[scope.row.avatar]" preview-teleported
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="姓名" min-width="160" v-if="checkedCities.includes('姓名')">
          <template #default="scope">
            <span :class="isRedFont(scope.row)"
              >{{ $filters.filterEmpty(scope.row.nickname) }} | {{ sexFilter(scope.row.sex) }}
              {{ scope.row.isLogoff ? '| (已注销)' : '' }}</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="groupName" label="分组" min-width="100" v-if="checkedCities.includes('分组')">
          <template #default="scope">
            <span>{{ scope.row.groupName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="spreadNickname" label="推荐人" min-width="130" v-if="checkedCities.includes('推荐人')" />
        <el-table-column label="手机号" min-width="100" v-if="checkedCities.includes('手机号')">
          <template #default="scope">
            <span>{{ $filters.filterEmpty(scope.row.phone) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nowMoney" label="余额" min-width="100" v-if="checkedCities.includes('余额')" />
        <el-table-column prop="integral" label="积分" min-width="100" v-if="checkedCities.includes('积分')" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #header>
            <div class="col-setting-header">
              <span style="padding-right: 5px">操作</span>
              <el-popover
                placement="bottom-end"
                :width="200"
                trigger="click"
                v-model:visible="card_select_show"
                popper-class="col-setting-popover"
              >
                <template #reference>
                  <i class="el-icon-setting" style="cursor: pointer"></i>
                </template>
                <div class="cell_ht">
                  <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
                  <el-button link @click="checkSave()">保存</el-button>
                </div>
                <el-checkbox-group
                  v-model="checkedCities"
                  class="column-checkbox-group"
                  @change="handleCheckedCitiesChange"
                >
                  <el-checkbox v-for="item in columnData" :label="item" :value="item" :key="item" class="check_cell">{{ item }}</el-checkbox>
                </el-checkbox-group>
              </el-popover>
            </div>
          </template>
          <template #default="scope">
            <a @click="onDetails(scope.row.uid)" v-hasPermi="['admin:user:topdetail']">详情</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="editUser(scope.row.uid)" v-hasPermi="['admin:user:infobycondition']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <el-dropdown trigger="click">
              <span class="el-dropdown-link"> 更多<i class="el-icon-arrow-down el-icon--right" /> </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    @click="editPoint(scope.row.uid)"
                    v-if="checkPermi(['admin:user:operate:founds'])"
                    >积分余额</el-dropdown-item
                  >
                  <el-dropdown-item @click="setBatch('group', scope.row)" v-if="checkPermi(['admin:user:group'])"
                    >设置分组</el-dropdown-item
                  >
                  <el-dropdown-item @click="setBatch('label', scope.row)" v-if="checkPermi(['admin:user:tag'])"
                    >设置标签</el-dropdown-item
                  >
                  <el-dropdown-item
                    @click="setPassword(scope.row)"
                    v-if="checkPermi(['admin:user:update:password'])"
                    >修改密码</el-dropdown-item
                  >
                  <el-dropdown-item @click="setPhone(scope.row)" v-if="checkPermi(['admin:user:update:phone'])"
                    >修改手机号</el-dropdown-item
                  >
                  <el-dropdown-item
                    @click="setExtension(scope.row)"
                    v-if="checkPermi(['admin:user:update:spread'])"
                    >修改上级推广人</el-dropdown-item
                  >
                  <el-dropdown-item
                    @click="clearSpread(scope.row)"
                    v-if="scope.row.spreadUid && scope.row.spreadUid > 0 && checkPermi(['admin:retail:spread:clean'])"
                    >清除上级推广人</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[20, 40, 60, 80]"
          :page-size="userFrom.limit"
          :current-page="userFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
          background
        />
      </div>
    </el-card>
    <!--修改推广人-->
    <el-dialog title="修改推广人" v-model="extensionVisible" width="540px" :before-close="handleCloseExtension">
      <el-form
        class="formExtension"
        ref="formExtensionRef"
        :model="formExtension"
        :rules="ruleInline"
        label-width="75px"
        @submit.prevent
        v-loading="loading"
      >
        <el-form-item label="用户头像：" prop="image">
          <div class="upLoadPicBox" @click="modalPicTap">
            <div v-if="formExtension.image" class="pictrue"><img :src="formExtension.image" /></div>
            <div v-else class="upLoad">
              <i class="el-icon-camera cameraIconfont" />
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="extensionVisible = false">取消</el-button>
          <el-button type="primary" @click="onSubExtension('formExtension')">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!--用户列表-->
    <el-dialog class="user-dialog" title="用户列表" v-model="userVisible" width="900px">
      <user-list @closeDialog="userVisible = false" v-if="userVisible" @getTemplateRow="getTemplateRow"></user-list>
    </el-dialog>
    <!--批量设置-->
    <el-dialog title="设置" v-model="dialogVisible" width="540px" :before-close="handleClose">
      <el-form
        :model="dynamicValidateForm"
        ref="dynamicValidateFormRef"
        label-width="75px"
        class="demo-dynamic"
        v-loading="loading"
      >
        <el-form-item
          prop="groupId"
          label="用户分组："
          :rules="[{ required: true, message: '请选择用户分组', trigger: 'change' }]"
          v-if="batchName === 'group'"
          key="1"
        >
          <el-select v-model="dynamicValidateForm.groupId" placeholder="请选择分组" style="width: 100%" filterable>
            <el-option
              :value="item.id"
              v-for="(item, index) in groupList"
              :key="index"
              :label="item.groupName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="groupId"
          label="用户标签："
          :rules="[{ required: true, message: '请选择用户标签', trigger: 'change' }]"
          v-else
        >
          <el-select
            v-model="dynamicValidateForm.groupId"
            placeholder="请选择标签"
            style="width: 100%"
            multiple
            filterable
          >
            <el-option :value="item.id" v-for="(item, index) in labelLists" :key="index" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="submitForm('dynamicValidateForm')">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!--编辑-->
    <el-dialog title="编辑" v-model="visible" width="900px">
      <edit-from v-if="visible" :uid="uid" @resetForm="resetForm" @success="handleEditSuccess"></edit-from>
    </el-dialog>
    <!--积分余额-->
    <el-dialog
      title="积分余额"
      v-model="VisiblePoint"
      width="540px"
      :close-on-click-modal="false"
      :before-close="handlePointClose"
    >
      <el-form
        :model="PointValidateForm"
        ref="PointValidateFormRef"
        label-width="80px"
        class="demo-dynamic"
        v-loading="loadingPoint"
      >
        <el-form-item label="修改余额：" required>
          <el-radio-group v-model="PointValidateForm.moneyType">
            <el-radio :label="1" :value="1">增加</el-radio>
            <el-radio :label="2" :value="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="余额：" required>
          <el-input-number
            controls-position="right"
            type="text"
            v-model="PointValidateForm.moneyValue"
            :precision="2"
            :step="0.1"
            :min="0"
            :max="999999"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="修改积分：" required>
          <el-radio-group v-model="PointValidateForm.integralType">
            <el-radio :label="1" :value="1">增加</el-radio>
            <el-radio :label="2" :value="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="积分：" required>
          <el-input-number
            controls-position="right"
            type="text"
            step-strictly
            v-model="PointValidateForm.integralValue"
            :min="0"
            :max="999999"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handlePointClose">取消</el-button>
          <el-button type="primary" :loading="loadingBtn" @click="submitPointForm('PointValidateForm')">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!--账户详情-->
    <user-details ref="userDetailFromRef" :userNo="uid"></user-details>
    <!--修改密码-->
    <el-dialog
      title="修改密码"
      v-model="passwordVisible"
      width="540px"
      :close-on-click-modal="false"
      :before-close="handlePasswordClose"
    >
      <el-form
        :model="passwordForm"
        ref="passwordFormRef"
        :rules="passwordRules"
        label-width="100px"
        v-loading="passwordLoading"
      >
        <el-form-item label="新密码：" prop="password">
          <el-input
            v-model="passwordForm.password"
            placeholder="请输入6~18位新密码"
            maxlength="18"
            show-password
            autocomplete="new-password"
          />
        </el-form-item>
        <el-form-item label="确认密码：" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            placeholder="请再次输入新密码"
            maxlength="18"
            show-password
            autocomplete="new-password"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handlePasswordClose">取消</el-button>
          <el-button type="primary" :loading="passwordLoading" @click="submitPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>
    <!--新增用户-->
    <el-dialog title="新增H5用户" v-model="createUserVisible" width="540px" :before-close="handleCreateUserClose">
      <el-form
        :model="createForm"
        ref="createFormRef"
        :rules="createRules"
        label-width="100px"
        v-loading="createLoading"
      >
        <el-form-item label="手机号：" prop="phone">
          <el-input v-model="createForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="用户昵称：" prop="nickname">
          <el-input v-model="createForm.nickname" placeholder="不填则自动生成" />
        </el-form-item>
        <el-form-item label="真实姓名：" prop="realName">
          <el-input v-model="createForm.realName" placeholder="请输入真实姓名（选填）" />
        </el-form-item>
        <el-form-item label="密码：" prop="pwd">
          <el-input v-model="createForm.pwd" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码：" prop="confirmPwd">
          <el-input v-model="createForm.confirmPwd" placeholder="请再次输入密码" show-password />
        </el-form-item>
        <el-form-item label="是否推广员：" prop="isPromoter">
          <el-radio-group v-model="createForm.isPromoter">
            <el-radio :label="true" :value="true">是</el-radio>
            <el-radio :label="false" :value="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态：" prop="status">
          <el-radio-group v-model="createForm.status">
            <el-radio :label="true" :value="true">开启</el-radio>
            <el-radio :label="false" :value="false">禁止</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCreateUserClose">取消</el-button>
          <el-button type="primary" :loading="createLoading" @click="submitCreateUser">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="jsx">
import { ref, reactive, computed, nextTick, onMounted, onActivated, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox } from '@/utils/elementPlusFeedback';
import { Search } from '@element-plus/icons-vue'
import {
  userListApi,
  groupListApi,
  tagListApi,
  groupPiApi,
  tagPiApi,
  foundsApi,
  updateSpreadApi,
  updatePhoneApi,
  userUpdatePasswordApi,
  userCreateApi,
} from '@/api/user';
import { spreadClearApi } from '@/api/distribution';
import editFrom from './edit';
import userDetails from './userDetails';
import userList from '@/components/userList';
import * as logistics from '@/api/logistics.js';
import Cookies from 'js-cookie';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';

defineOptions({ name: 'UserIndex' });

const { proxy } = getCurrentInstance();

const formExtension = reactive({
  image: '',
  spreadUid: '',
  userId: '',
});
const ruleInline = ref({});
const extensionVisible = ref(false);
const userVisible = ref(false);
const pickerOptions = proxy.$timeOptions;
const loadingBtn = ref(false);
const PointValidateForm = reactive({
  integralType: 1,
  integralValue: 0,
  moneyType: 1,
  moneyValue: 0,
  uid: '',
});
const loadingPoint = ref(false);
const VisiblePoint = ref(false);
const visible = ref(false);
const userIds = ref('');
const dialogVisible = ref(false);
const groupData = ref([]);
const labelData = ref([]);
const selData = ref([]);
const labelPosition = ref('right');
const collapse = ref(false);
const props = {
  children: 'child',
  label: 'name',
  value: 'name',
  emitPath: false,
};
const propsCity = {
  children: 'child',
  label: 'name',
  value: 'name',
};
const headeNum = [
  { type: '', name: '全部用户' },
  { type: 'wechat', name: '微信公众号用户' },
  { type: 'routine', name: '微信小程序用户' },
  { type: 'h5', name: 'H5用户' },
];
const listLoading = ref(true);
const tableData = reactive({
  data: [],
  total: 0,
});
const loginType = ref('');
const userFrom = reactive({
  searchType: 'all',
  content: '',
  labelId: '',
  userType: '',
  sex: '',
  isPromoter: '',
  country: '',
  payCount: '',
  accessType: '',
  dateLimit: '',
  keywords: '',
  province: '',
  city: '',
  page: 1,
  limit: 20,
  groupId: '',
});
const grid = {
  xl: 8,
  lg: 12,
  md: 12,
  sm: 24,
  xs: 24,
};
const labelLists = ref([]);
const groupList = ref([]);
const selectedData = ref([]);
const timeVal = ref([]);
const addresData = ref([]);
const dynamicValidateForm = reactive({
  groupId: [],
});
const loading = ref(false);
const groupIdFrom = ref([]);
const selectionList = ref([]);
const batchName = ref('');
const uid = ref(0);
const keyNum = ref(0);
const address = ref([]);
const multipleSelectionAll = ref([]);
const idKey = 'uid';
const card_select_show = ref(false);
const checkAll = ref(false);
const checkedCities = ref(['ID', '头像', '姓名', '分组', '推荐人', '手机号', '余额', '积分']);
const columnData = ['ID', '头像', '姓名', '分组', '推荐人', '手机号', '余额', '积分'];
const isIndeterminate = ref(true);
const createUserVisible = ref(false);
const createLoading = ref(false);
const passwordVisible = ref(false);
const passwordLoading = ref(false);
const passwordForm = reactive({
  id: null,
  password: '',
  confirmPassword: '',
});
const validatePasswordConfirm = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'));
  } else if (value !== passwordForm.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};
const passwordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 18, message: '密码长度必须为6~18位', trigger: 'blur' },
  ],
  confirmPassword: [{ validator: validatePasswordConfirm, trigger: 'blur' }],
};
const createForm = reactive({
  phone: '',
  nickname: '',
  realName: '',
  pwd: '',
  confirmPwd: '',
  isPromoter: false,
  status: true,
});
const createRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  pwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  confirmPwd: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
  ],
  isPromoter: [
    { required: true, message: '请选择是否推广员', trigger: 'change' },
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' },
  ],
};

const tableRef = ref(null);
const userFromRef = ref(null);
const userSearchInputRef = ref(null);
const formExtensionRef = ref(null);
const dynamicValidateFormRef = ref(null);
const PointValidateFormRef = ref(null);
const createFormRef = ref(null);
const passwordFormRef = ref(null);
const userDetailFromRef = ref(null);

// 局部过滤器
function sexFilter(status) {
  const statusMap = {
    0: '未知',
    1: '男',
    2: '女',
    3: '保密',
  };
  return statusMap[status];
}

// 是否注销
const isRedFont = computed(() => {
  return (info) => {
    if (info.isLogoff) {
      return 'red-fonts';
    } else {
      return '';
    }
  };
});

function setPhone(row) {
  ElMessageBox.prompt('', '修改手机号', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputErrorMessage: '请输入修改手机号',
    inputType: 'text',
    inputValue: row.phone,
    inputPlaceholder: '请输入手机号',
    closeOnClickModal: false,
    inputValidator: (value) => {
      if (!value) return '请填写手机号';
    },
  })
    .then(({ value }) => {
      updatePhoneApi({ id: row.uid, phone: value }).then(() => {
        ElMessage.success('编辑成功');
        getList();
      });
    })
    .catch(() => {
      ElMessage.info('取消输入');
    });
}
function setPassword(row) {
  Object.assign(passwordForm, {
    id: row.uid,
    password: '',
    confirmPassword: '',
  });
  passwordVisible.value = true;
  nextTick(() => {
    passwordFormRef.value?.clearValidate();
  });
}
function handlePasswordClose(done) {
  passwordLoading.value = false;
  Object.assign(passwordForm, {
    id: null,
    password: '',
    confirmPassword: '',
  });
  passwordFormRef.value?.clearValidate();
  if (typeof done === 'function') {
    done();
  } else {
    passwordVisible.value = false;
  }
}
function submitPassword() {
  passwordFormRef.value.validate((valid) => {
    if (!valid) return;

    passwordLoading.value = true;
    userUpdatePasswordApi({
      id: passwordForm.id,
      password: passwordForm.password,
    })
      .then(() => {
        ElMessage.success('密码修改成功');
        handlePasswordClose();
      })
      .catch(() => {
        passwordLoading.value = false;
      });
  });
}
// 清除
function clearSpread(row) {
  proxy.$modalSure('解除【' + row.nickname + '】的上级推广人吗').then(() => {
    spreadClearApi(row.uid).then((res) => {
      ElMessage.success('清除成功');
      getList();
    });
  });
}
function onSubExtension(formName) {
  formExtensionRef.value.validate((valid) => {
    if (valid) {
      updateSpreadApi(formExtension).then((res) => {
        ElMessage.success('设置成功');
        extensionVisible.value = false;
        getList();
      });
    } else {
      return false;
    }
  });
}
function getTemplateRow(row) {
  formExtension.image = row.avatar;
  formExtension.spreadUid = row.uid;
}
function setExtension(row) {
  Object.assign(formExtension, {
    image: '',
    spreadUid: '',
    userId: row.uid,
  });
  extensionVisible.value = true;
}
function handleCloseExtension() {
  extensionVisible.value = false;
}
function modalPicTap() {
  userVisible.value = true;
}
function resetForm() {
  visible.value = false;
}
// 编辑完成：关闭弹窗并刷新列表
function handleEditSuccess() {
  visible.value = false;
  getList();
}
function reset(formName) {
  Object.assign(userFrom, {
    searchType: 'all',
    content: '',
    labelId: '',
    userType: '',
    sex: '',
    isPromoter: '',
    country: '',
    payCount: '',
    accessType: '',
    dateLimit: '',
    keywords: '',
    province: '',
    city: '',
    page: 1,
    limit: 20,
    groupId: '',
  });
  address.value = [];
  groupData.value = [];
  labelData.value = [];
  timeVal.value = [];
  userSearchInputRef.value.clearInput(); // 清空用户搜索输入框
  getList();
}
// 列表
async function getCityList() {
  let res = await logistics.cityListTree();
  //res.forEach((el, index) => {
  //     el.child.forEach((cel, j) => {
  //       delete cel.child
  //     })
  //   })
  addresData.value = res;
  // })
}
// 发送文章
function sendNews() {
  if (selectionList.value.length === 0) return ElMessage.warning('请先选择用户');
  proxy.$modalArticle(function (row) {}, 'send');
}
// 发送优惠劵
function onSend() {
  if (selectionList.value.length === 0) return ElMessage.warning('请选择要设置的用户');
  // 注意：以下回调中引用的 formValidate / couponData 在原组件中即未定义，属历史遗留逻辑，保持原样
  const _this = { formValidate: {}, couponData: [], selectionList: [] };
  proxy.$modalCoupon(
    'send',
    (keyNum.value += 1),
    [],
    function (row) {
      _this.formValidate.give_coupon_ids = [];
      _this.couponData = [];
      row.map((item) => {
        _this.formValidate.give_coupon_ids.push(item.coupon_id);
        _this.couponData.push(item.title);
      });
      _this.selectionList = [];
    },
    userIds.value,
    'user',
  );
}
// 账户详情
function onDetails(id) {
  uid.value = id;
  userDetailFromRef.value.getUserDetail(id);
  userDetailFromRef.value.dialogUserDetail = true;
}
// 积分余额
function editPoint(id) {
  uid.value = id;
  VisiblePoint.value = true;
}
// 积分余额
const submitPointForm = Debounce(function (formName) {
  PointValidateFormRef.value.validate((valid) => {
    if (valid) {
      PointValidateForm.uid = uid.value;
      loadingBtn.value = true;
      foundsApi(PointValidateForm)
        .then((res) => {
          ElMessage.success('设置成功');
          loadingBtn.value = false;
          handlePointClose();
          getList();
        })
        .catch(() => {
          loadingBtn.value = false;
        });
    } else {
      return false;
    }
  });
});
// 积分余额
function handlePointClose() {
  VisiblePoint.value = false;
  Object.assign(PointValidateForm, {
    integralType: 1,
    integralValue: 0,
    moneyType: 1,
    moneyValue: 0,
    uid: '',
  });
}
function editUser(id) {
  uid.value = id;
  visible.value = true;
}
function submitForm(formName) {
  dynamicValidateFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      batchName.value === 'group'
        ? groupPiApi({ groupId: dynamicValidateForm.groupId, id: userIds.value })
            .then((res) => {
              ElMessage.success('设置成功');
              loading.value = false;
              handleClose();
              getList();
            })
            .catch(() => {
              loading.value = false;
            })
        : tagPiApi({ tagId: dynamicValidateForm.groupId.join(','), id: userIds.value })
            .then((res) => {
              ElMessage.success('设置成功');
              loading.value = false;
              handleClose();
              getList();
            })
            .catch(() => {
              loading.value = false;
            });
    } else {
      return false;
    }
  });
}
function setBatch(name, row) {
  batchName.value = name;
  if (row) {
    userIds.value = row.uid;
    if (batchName.value === 'group') {
      dynamicValidateForm.groupId = row.groupId ? Number(row.groupId) : '';
    } else {
      dynamicValidateForm.groupId = row.tagId ? row.tagId.split(',').map(Number) : [];
    }
  } else {
    dynamicValidateForm.groupId = '';
  }
  if (multipleSelectionAll.value.length === 0 && !row) return ElMessage.warning('请选择要设置的用户');
  dialogVisible.value = true;
}
function handleClose() {
  dialogVisible.value = false;
  dynamicValidateFormRef.value.resetFields();
}
// 全选
function onSelectTab(selection) {
  selectionList.value = selection;
  setTimeout(() => {
    changePageCoreRecordData();
    let data = [];
    if (multipleSelectionAll.value.length) {
      multipleSelectionAll.value.map((item) => {
        data.push(item.uid);
      });
      userIds.value = data.join(',');
    }
  }, 50);
}
// 搜索
function userSearchs() {
  userFrom.page = 1;
  getList();
}
// 选择国家
function changeCountry() {
  if (userFrom.country === 'OTHER' || !userFrom.country) {
    selectedData.value = [];
    userFrom.province = '';
    userFrom.city = '';
    address.value = [];
  }
}
// 选择地址
function handleChange(value) {
  userFrom.province = value[0];
  userFrom.city = value[1];
  userSearchs();
}
// 具体日期
function onchangeTime(e) {
  timeVal.value = e;
  userFrom.dateLimit = e ? timeVal.value.join(',') : '';
  userSearchs();
}
// 分组列表
function groupLists() {
  groupListApi({ page: 1, limit: 9999 }).then(async (res) => {
    groupList.value = res.list;
  });
}
//标签列表
function getTagList() {
  tagListApi({ page: 1, limit: 9999 }).then((res) => {
    labelLists.value = res.list;
  });
}
// 列表
function getList(num) {
  listLoading.value = true;
  userFrom.page = num ? num : userFrom.page;
  userFrom.userType = loginType.value;
  if (loginType.value == 0) userFrom.userType = '';
  userFrom.groupId = groupData.value.join(',');
  userFrom.labelId = labelData.value.join(',');
  userListApi(userFrom)
    .then((res) => {
      tableData.data = res.list;
      tableData.total = res.total;
      nextTick(function () {
        setSelectRow(); // 调用跨页选中方法
      });
      listLoading.value = false;
    })
    .catch(() => {
      listLoading.value = false;
    });
  checkedCities.value = proxy.$cache.local.has('user_stroge')
    ? proxy.$cache.local.getJSON('user_stroge')
    : checkedCities.value;
  card_select_show.value = false;
}
// 设置选中的方法
function setSelectRow() {
  if (!multipleSelectionAll.value || multipleSelectionAll.value.length <= 0) {
    return;
  }
  // 标识当前行的唯一键的名称
  const idKey_ = idKey;
  const selectAllIds = [];
  multipleSelectionAll.value.forEach((row) => {
    selectAllIds.push(row[idKey_]);
  });
  tableRef.value.clearSelection();
  for (var i = 0; i < tableData.data.length; i++) {
    if (selectAllIds.indexOf(tableData.data[i][idKey_]) >= 0) {
      // 设置选中，记住table组件需要使用ref="table"
      tableRef.value.toggleRowSelection(tableData.data[i], true);
    }
  }
}
// 记忆选择核心方法
function changePageCoreRecordData() {
  // 标识当前行的唯一键的名称
  const idKey_ = idKey;
  // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
  if (multipleSelectionAll.value.length <= 0) {
    multipleSelectionAll.value = selectionList.value;
    return;
  }
  // 总选择里面的key集合
  const selectAllIds = [];
  multipleSelectionAll.value.forEach((row) => {
    selectAllIds.push(row[idKey_]);
  });
  const selectIds = [];
  // 获取当前页选中的id
  selectionList.value.forEach((row) => {
    selectIds.push(row[idKey_]);
    // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
    if (selectAllIds.indexOf(row[idKey_]) < 0) {
      multipleSelectionAll.value.push(row);
    }
  });
  const noSelectIds = [];
  // 得到当前页没有选中的id
  tableData.data.forEach((row) => {
    if (selectIds.indexOf(row[idKey_]) < 0) {
      noSelectIds.push(row[idKey_]);
    }
  });
  noSelectIds.forEach((uid_) => {
    if (selectAllIds.indexOf(uid_) >= 0) {
      for (let i = 0; i < multipleSelectionAll.value.length; i++) {
        if (multipleSelectionAll.value[i][idKey_] == uid_) {
          // 如果总选择中有未被选中的，那么就删除这条
          multipleSelectionAll.value.splice(i, 1);
          break;
        }
      }
    }
  });
}
function pageChange(page) {
  changePageCoreRecordData();
  userFrom.page = page;
  getList();
}
function handleSizeChange(val) {
  changePageCoreRecordData();
  userFrom.limit = val;
  getList();
}
// 删除
function handleDelete(id, idx) {
  proxy.$modalSure().then(() => {
    productDeleteApi(id).then(() => {
      ElMessage.success('删除成功');
      getList();
    });
  });
}
function onchangeIsShow(row) {
  row.isShow
    ? putOnShellApi(row.id)
        .then(() => {
          ElMessage.success('上架成功');
          getList();
        })
        .catch(() => {
          row.isShow = !row.isShow;
        })
    : offShellApi(row.id)
        .then(() => {
          ElMessage.success('下架成功');
          getList();
        })
        .catch(() => {
          row.isShow = !row.isShow;
        });
}
function handleCheckAllChange(val) {
  checkedCities.value = val ? columnData : [];
  isIndeterminate.value = false;
}
function handleCheckedCitiesChange(value) {
  let checkedCount = value.length;
  checkAll.value = checkedCount === columnData.length;
  isIndeterminate.value = checkedCount > 0 && checkedCount < columnData.length;
}
function checkSave() {
  card_select_show.value = false;
  proxy.$modal.loading('正在保存到本地，请稍候...');
  proxy.$cache.local.setJSON('user_stroge', checkedCities.value);
  setTimeout(proxy.$modal.closeLoading(), 1000);
}
// 新增用户
function showCreateUser() {
  createUserVisible.value = true;
}
function handleCreateUserClose() {
  createUserVisible.value = false;
  Object.assign(createForm, { phone: '', nickname: '', realName: '', pwd: '', confirmPwd: '', isPromoter: false, status: true });
  if (createFormRef.value) {
    createFormRef.value.resetFields();
  }
}
function submitCreateUser() {
  createFormRef.value.validate((valid) => {
    if (valid) {
      if (createForm.pwd !== createForm.confirmPwd) {
        ElMessage.warning('两次输入的密码不一致');
        return;
      }
      createLoading.value = true;
      const { confirmPwd, ...params } = createForm;
      userCreateApi(params)
        .then((res) => {
          ElMessage.success('新增成功');
          createLoading.value = false;
          handleCreateUserClose();
          getList(1);
        })
        .catch(() => {
          createLoading.value = false;
        });
    }
  });
}

onMounted(() => {
  getList();
  groupLists();
  getTagList();
  if (checkPermi(['admin:system:city:list:tree'])) getCityList();
});

onActivated(() => {
  userFrom.keywords = '';
  loginType.value = '';
  getList(1);
});
</script>

<style scoped lang="scss">
/*.timeBox{*/
/*width: 100%;*/
/*:deep(.el-form-item__content){*/
/*width: 87% !important;*/
/*}*/
/*}*/
.el-dropdown-link {
  cursor: pointer;
  color: #409eff;
  font-size: 12px;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.text-right {
  text-align: right;
}

.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}


.seachTiele {
  line-height: 30px;
}

.container {
  min-width: 821px;

  :deep(.el-form-item ){
    width: 100%;
  }

  :deep(.el-form-item__content ){
    width: 72%;
  }
}

.ivu-ml-8 {
  font-size: 12px;
  color: var(--prev-color-primary) !important;
}

.relative {
  position: relative;
}

/* 操作列表头：让“操作”文字与设置图标垂直居中对齐 */
.col-setting-header {
  display: inline-flex;
  align-items: center;
}

.cell_ht {
  height: 50px;
  padding: 15px 20px;
  box-sizing: border-box;
  border-bottom: 1px solid #eeeeee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.check_cell {
  width: 100%;
  padding: 15px 20px 0;
}

.column-checkbox-group {
  padding-bottom: 15px;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #606266;
}
:deep(.user-dialog .el-dialog__body) {
  padding: 0;
  height: 600px;
}
.search-box {
  // position: absolute;
  // top: 20px;
  // right: 0;
  .search-btn-group-box {
    flex-shrink: 0;
  }
}
:deep(.search-form) {
  position: relative;
}
.search-form-sub-bottom {
  position: absolute;
  right: 0;
  bottom: 0;
}
.red-fonts {
  color: #ed4014;
}
.flex-between {
  justify-content: space-between;
}
</style>

<style lang="scss">
/* el-popover 渲染在 body 下，需用全局样式覆盖内边距 */
.col-setting-popover.el-popover.el-popper {
  padding: 0 !important;
}
</style>
