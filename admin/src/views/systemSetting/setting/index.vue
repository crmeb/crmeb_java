<template>
  <div class="divBox">
    <el-card class="box-card">
      <el-tabs
        v-model="activeNamel1"
        @tab-click="handleTabClick"
        v-loading="loading"
        v-if="checkPermi(['admin:system:config:info'])"
      >
        <el-tab-pane v-for="(tab, index) in treeList" :key="index" :label="tab.name" :name="tab.id.toString()">
          <template>
            <el-tabs
              v-if="tab.child && tab.child.length > 0"
              v-model="activeNamel2"
              type="border-card"
              @tab-click="handleItemTabClick"
              class="tab-content"
            >
              <el-tab-pane
                v-for="(tabItem, itemIndex) in tab.child"
                :key="itemIndex"
                :label="tabItem.name"
                :name="tabItem.extra"
              >
                <parser
                  v-if="formConfChild.render"
                  :is-edit="formConfChild.isEdit"
                  :form-conf="formConfChild.content"
                  :form-edit-data="currentEditData"
                  @submit="handlerSubmit"
                />
              </el-tab-pane>
            </el-tabs>
            <span v-else>
              <parser
                v-if="formConf.render"
                :is-edit="formConf.isEdit"
                :form-conf="formConf.content"
                :form-edit-data="currentEditData"
                @submit="handlerSubmit"
              />
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import parser from '@/components/FormGenerator/components/parser/Parser';
import * as categoryApi from '@/api/categoryApi.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import * as systemFormConfigApi from '@/api/systemFormConfig.js';
import * as systemSettingApi from '@/api/systemSetting.js';
import * as systemConfigApi from '@/api/systemConfig.js';
import Template from '@/views/appSetting/wxAccount/wxTemplate/index';
import { beautifierConf } from '@/components/FormGenerator/utils';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
export default {
  // name: "index",
  components: { Template, parser },
  data() {
    return {
      loading: false,
      formConf: { content: { fields: [] }, id: null, render: false, isEdit: false },
      formConfChild: { content: { fields: [] }, id: null, render: false, isEdit: false },
      activeNamel1: '0',
      activeNamel2: '0', //针对文件特殊处理
      treeList: [],
      editDataChild: {},
      isCreate: 0,
      currentEditId: null,
      currentEditData: null,
      currentSelectedUploadFlag: null,
    };
  },
  mounted() {
    this.handlerGetTreeList();
    this.getCurrentUploadSelectedFlag();
  },
  methods: {
    checkPermi,
    handleTabClick(tab) {
      this.activeNamel2 = tab.$children[0].panes[0].name;
      this.handlerGetLevel2FormConfig(this.activeNamel2);
    },
    handlerGetLevel1FormConfig(id) {
      const formPram = { id: id };
      this.currentEditId = id;
      this.formConf.content = { fields: [] };
      this.formConf.render = false;
      this.loading = true;
      systemFormConfigApi
        .getFormConfigInfo(formPram)
        .then((data) => {
          const { id, name, info, content } = data;
          this.formConf.content = JSON.parse(content);
          this.formConf.id = id;
          this.handlerGetSettingInfo(id, 1);
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    handleItemTabClick(tab, event) {
      //这里对tabs=tab.name和radio=id做了兼容
      let _id = tab.name ? tab.name : tab;
      if (!_id) return this.$message.error('表单配置不正确，请关联正确表单后使用');
      this.handlerGetLevel2FormConfig(_id);
    },
    handlerGetLevel2FormConfig(id) {
      const formPram = { id: id };
      this.currentEditId = id;
      this.formConfChild.content = { fields: [] };
      this.formConfChild.render = false;
      this.loading = true;
      systemFormConfigApi
        .getFormConfigInfo(formPram)
        .then((data) => {
          const { id, name, info, content } = data;
          this.formConfChild.content = JSON.parse(content);
          this.formConfChild.id = id;
          this.handlerGetSettingInfo(id, 2);
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    // 表单详情
    handlerGetSettingInfo(id, level) {
      systemSettingApi.systemConfigInfo({ id: id }).then((data) => {
        // 小程序登录配置，此处返回字符串，表单组件是数组，需要字符串转为数组
        if (data.routine_phone_verification)
          data.routine_phone_verification = data.routine_phone_verification.split(',').map(Number);
        this.currentEditData = data;
        if (level === 1) {
          this.formConf.isEdit = this.currentEditData !== null;
          this.formConf.render = true;
        } else {
          this.formConfChild.isEdit = this.currentEditData !== null;
          this.formConfChild.render = true;
        }
      });
    },
    handlerSubmit: Debounce(function (formValue) {
      this.handlerSave(formValue);
    }),
    handlerSave(formValue) {
      const _pram = this.buildFormPram(formValue);
      let _formId = 0;
      systemSettingApi.systemConfigSave(_pram).then((data) => {
        this.$message.success('添加数据成功');
      });
    },
    handlerGetTreeList() {
      const _pram = { type: this.$constants.categoryType[5].value, status: 1 };
      this.loading = true;
      categoryApi
        .treeCategroy(_pram)
        .then((data) => {
          this.treeList = this.handleAddArrt(data);
          if (this.treeList.length > 0) this.activeNamel1 = this.treeList[0].id.toString();
          if (this.treeList.length > 0 && this.treeList[0].child.length > 0) {
            this.activeNamel2 = this.treeList[0].child[0].extra;
          }
          if (this.activeNamel2) {
            this.handlerGetLevel2FormConfig(this.treeList[0].child[0].extra);
          }
          // else {
          //  this.handlerGetLevel1FormConfig(this.treeList[0].extra)
          //}
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    handleAddArrt(treeData) {
      // let _result = this.addTreeListLabel(treeData)
      const _result = selfUtil.addTreeListLabel(treeData);
      return _result;
    },
    buildFormPram(formValue) {
      const _pram = {
        fields: [],
        id: this.currentEditId,
        sort: 0, // 参数暂时无用
        status: true, // 参数暂时无用
      };
      const _fields = [];
      Object.keys(formValue).forEach((key) => {
        _fields.push({
          name: key,
          title: key,
          // 提交数据要求是字符串，如果是数组，转为字符串
          value: formValue[key] instanceof Array ? formValue[key].join(',') : formValue[key],
        });
      });
      _pram.fields = _fields;
      return _pram;
    },
    //获取文件存储类型
    getCurrentUploadSelectedFlag() {
      systemConfigApi.getUploadTypeApi().then((data) => {
        this.currentSelectedUploadFlag = parseInt(data);
      });
    },
  },
};
</script>

<style scoped lang="scss">
::v-deep .tab-content .el-tabs__header {
  margin-bottom: 20px !important;
}
::v-deep .dialog-footer-inner {
  float: left !important;
}
::v-deep .closeBtn {
  display: none;
}
::v-deep .el-tabs__nav .el-tabs__item:nth-of-type(1) {
  padding-left: 20px !important;
}
</style>
