<template>
  <div class="slider-box">
    <div class="c_row-item" v-if="configData.title">
      <el-col class="label" :span="4">
        {{ configData.title }}
      </el-col>
      <el-col :span="18">
        <el-cascader
          @change="sliderChange"
          placeholder="请选择分类"
          size="mini"
          v-model="modelValue"
          :options="treeSelect"
          :props="props"
          filterable
          clearable
        >
        </el-cascader>
      </el-col>
    </div>
  </div>
</template>

<script>
import { themeArticleCategory, themeProductCategory } from '@/api/theme';
export default {
  name: 'c_classify',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
    number: {
      type: null,
    },
  },
  data() {
    return {
      defaults: {},
      configData: {},
      props: { multiple: true, checkStrictly: true, emitPath: false },
      treeSelect: [],
    };
  },
  computed: {
    modelValue: {
      get() {
        if (this.configData.activeValue !== undefined) {
          return this.configData.activeValue;
        }
        return this.configData.classVal;
      },
      set(val) {
        if (this.configData.activeValue !== undefined) {
          this.configData.activeValue = val;
        } else {
          this.configData.classVal = val;
        }
      },
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
      if (this.configNme === 'articleClass') {
        this.articleCategory();
      } else {
        this.goodsCategory();
      }
    });
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      deep: true,
    },
  },
  methods: {
    sliderChange() {
      this.$emit('getConfig', { name: 'classlfy' });
    },
    articleCategory() {
      themeArticleCategory()
        .then((res) => {
          this.treeSelect = this.formatCategory(res.data);
        })
        .catch((res) => {
          this.$message.error((res && (res.msg || res.message)) || '文章分类获取失败');
        });
    },
    formatCategory(list) {
      return (Array.isArray(list) ? list : []).map((item) => {
        return {
          value: item.id,
          label: item.title,
          children: item.children ? this.formatCategory(item.children) : null,
        };
      });
    },
    goodsCategory() {
      themeProductCategory({ status: 1 })
        .then((res) => {
          this.treeSelect = res.data;
        })
        .catch((res) => {
          this.$message.error((res && (res.msg || res.message)) || '商品分类获取失败');
        });
    },
  },
};
</script>

<style scoped lang="scss">
.slider-box {
  margin-top: 20px;
  padding: 0 15px;
  .label {
    color: #999999;
    font-size: 12px;
    width: 75px;
    margin-right: 16px;
  }
}
.c_row-item {
  margin-bottom: 20px;
}
::v-deep .el-cascader__search-input {
  margin-left: 8px;
  font-size: 12px;
}
::v-deep.el-cascader {
  width: 100%;
  .el-tag {
    margin: 4px 0 2px 6px;
  }
}
</style>
