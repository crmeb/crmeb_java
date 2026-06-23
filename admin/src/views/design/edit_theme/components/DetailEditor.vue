<template>
  <div class="detail-editor">
    <diy-index ref="diy" />
    
  </div>
</template>

<script>
import DiyIndex from '@/views/design/theme_editor/devise/diyIndex.vue';

export default {
  name: 'DetailEditor',
  components: { DiyIndex },
  methods: {
    preview() {
      this.$refs.diy && this.$refs.diy.preview && this.$refs.diy.preview();
    },
    saveOnly() {
      this.$refs.diy && this.$refs.diy.saveConfig && this.$refs.diy.saveConfig(1);
    },
    saveAndClose() {
      this.$refs.diy && this.$refs.diy.saveConfig && this.$refs.diy.saveConfig(2);
    },
    saveTemplate() {
      this.$refs.diy && (this.$refs.diy.nameModal = true);
    },
  },
};
</script>

<style lang="scss" scoped>
.detail-editor {
  height: 100%;
}
</style>
