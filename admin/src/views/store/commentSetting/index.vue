<template>
  <div class="divBox">
    <el-card shadow="never" :bordered="false">
      <el-form
        ref="formValidate"
        :rules="ruleValidate"
        class="formValidate mt20"
        :model="formValidate"
        label-width="140px"
        @submit.native.prevent
      >
        <el-form-item label="默认好评开关：" prop="autoReplySwitch">
          <el-switch
            v-model="formValidate.autoReplySwitch"
            :active-value="1"
            :inactive-value="0"
            active-text="开启"
            inactive-text="关闭"
          >
          </el-switch>
        </el-form-item>
        <el-form-item label="自动默认好评时间(天)：" prop="autoReplyTime">
          <el-input-number
            controls-position="right"
            step-strictly
            :step="1"
            v-model="formValidate.autoReplyTime"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="默认好评内容：" prop="autoReplyContent">
          <el-input
            class="from-ipt-width"
            v-model="formValidate.autoReplyContent"
            placeholder="请输入默认好评内容"
            type="textarea"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script>
import { getCommentSettingApi, setCommentSettingApi } from '@/api/store';
export default {
  name: '',
  components: {},
  mixins: [],
  props: {},
  data() {
    return {
      formValidate: {
        autoReplySwitch: 0, // 自动好评开关
        autoReplyTime: 5, // 自动好评时间
        autoReplyContent: '好评', // 好评内容
      },
      ruleValidate: {
        autoReplyContent: [{ required: true, message: '', trigger: 'none' }],
      },
    };
  },
  mounted() {
    // 获取评论配置
    this.getCommentSetting();
  },
  methods: {
    // 获取评论配置
    getCommentSetting() {
      getCommentSettingApi()
        .then((res) => {
          this.formValidate = res;
        })
        .catch((err) => {
          this.$message.error(err.message);
        });
    },
    // 提交
    submit() {
      setCommentSettingApi(this.formValidate)
        .then((res) => {
          this.$message.success('保存成功');
        })
        .catch((err) => {});
    },
  },
};
</script>
<style lang="less" scoped></style>
