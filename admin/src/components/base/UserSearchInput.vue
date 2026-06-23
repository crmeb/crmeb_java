<template>
  <div>
    <el-input
      v-model="tableFromNew.content"
      placeholder="请输入用户信息"
      :type="tableFromNew.searchType === 'phone' || tableFromNew.searchType === 'uid' ? 'number' : 'text'"
      @input="handleChangeContent"
      @blur="handleBlur" 
      @clear="handleClear"
      clearable
      class="selWidth"
    >
      <el-select @change="handleChangeType" v-model="tableFromNew.searchType" slot="prepend" style="width: 100px">
        <el-option value="all" label="全部"></el-option>
        <el-option value="uid" label="用户ID"></el-option>
        <el-option value="phone" label="手机号"></el-option>
        <el-option value="nickname" label="用户昵称"></el-option>
      </el-select>
    </el-input>
  </div>
</template>

<script>
export default {
  name: 'UserSearchInput',
  data() {
    return {
      tableFromNew: this.value,
    };
  },
  props: {
    value: {
      type: Object,
      default: {},
    },
  },
  watch: {
    tableFromNew: {
      handler(newVal) {
        this.$emit('input', newVal);
      },
      deep: true,
    },
  },
  methods: {
    handleChangeType() {
      this.tableFromNew.content = '';
    },
    handleChangeContent(event) {
      if (this.tableFromNew.searchType === 'phone' || this.tableFromNew.searchType === 'uid')
        this.tableFromNew.content = event.replace(/[^0-9]/g, '');
      if (this.tableFromNew.content > 99999999999) {
        this.tableFromNew.content = ''; // 设置最大值为100
      }
    },
    // 清空搜索框
    clearInput() {
      this.tableFromNew.content = '';
      this.tableFromNew.searchType = 'all';
    },
    // 失去焦点
    handleBlur() {
      this.$emit('searchList')
    },
    // 清除搜索框内容
    handleClear() {
      this.$emit('searchList')
    }
  },
};
</script>
