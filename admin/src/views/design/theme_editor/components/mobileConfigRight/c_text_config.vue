<template>
  <div class="config-item">
    <div class="item-header">
      <span class="item-title">{{ configData.title }}</span>
      <el-switch
        class="defineSwitch"
        v-model="configData.enable"
        active-text="开启"
        inactive-text="关闭"
        @change="handleChange"
      ></el-switch>
    </div>
    <div class="item-content" v-if="configData.enable">
      <el-input
        v-model="configData.text"
        placeholder="请输入文字"
        maxlength="20"
        show-word-limit
        @change="handleChange"
      ></el-input>
      <div class="link-box" v-if="configData.link !== undefined">
        <el-input v-model="configData.link" placeholder="请选择链接">
          <i slot="suffix" class="el-icon-link" @click="getLink"></i>
        </el-input>
      </div>
    </div>
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script>
import linkaddress from '@/components/linkaddress';

export default {
  name: 'c_text_config',
  components: {
    linkaddress,
  },
  props: {
    configNme: {
      type: String,
    },
    configObj: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      configData: {
        title: '',
        enable: false,
        text: '',
      },
    };
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.configData = nVal[this.configNme] || { title: '', enable: false, text: '' };
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    handleChange() {
      this.$emit('getConfig', this.configData);
    },
    getLink() {
      this.$refs.linkaddres.modals = true;
    },
    linkUrl(e) {
      this.configData.link = e;
      this.handleChange();
    },
  },
};
</script>

<style lang="scss" scoped>
.config-item {
  padding: 12px 16px;

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .item-title {
      font-size: 12px;
      color: #999;
    }
  }

  .item-content {
    margin-top: 12px;

    .link-box {
      margin-top: 10px;

      .el-icon-link {
        cursor: pointer;
        line-height: 32px;
        font-size: 14px;
        color: #1890ff;
        margin-right: 5px;
      }
    }
  }
}
</style>
