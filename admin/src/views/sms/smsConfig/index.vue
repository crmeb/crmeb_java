<template>
  <div>
    <iframe
      ref="iframes"
      src="https://api.crmeb.com/"
      width="100%"
      :height="iframeHeight"
      style="border: none"
    ></iframe>
  </div>
</template>

<script>
export default {
  name: 'SmsConfig',
  data() {
    return {
      iframeHeight: 0,
    };
  },
  created() {
    window.addEventListener('resize', this.handleResize);
  },
  mounted() {
    this.$nextTick(() => {
      this.iframeHeight = this.$selfUtil.getTableHeight(0);
    });
  },
  methods: {
    handleResize(event) {
      this.iframeHeight = this.$selfUtil.getTableHeight(0);
    },
  },
};
</script>
