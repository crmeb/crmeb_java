<template>
  <div>
    <el-form-item label="文本标题">
      <el-input v-model="curComponent.propValue.text" placeholder="请填写文本" @change="onChange"></el-input>
    </el-form-item>
    <ConfigLink
      :curComponent="curComponent"
      :type="type"
      :currentFieldList="currentFieldList"
      :showLinkType="true"
      @change="onChange"
      @get-link="getLink"
    />
  </div>
</template>

<script>
import ConfigLink from './ConfigLink';

export default {
  name: 'ConfigTextContent',
  components: {
    ConfigLink,
  },
  props: {
    curComponent: {
      type: Object,
      required: true,
    },
    type: {
      type: String,
      default: 'user',
    },
    currentFieldList: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    getLink() {
      this.$emit('get-link');
    },
    onChange() {
      this.$emit('change');
    },
  },
};
</script>
