<template>
  <div>
    <vue-ueditor-wrap v-model="contents" :config="myConfig" style="width: 90%;" @beforeInit="addCustomDialog" />
  </div>
</template>

<script>
export default {
  name: 'Index',
  // components: { VueUeditorWrap },
  scrollerHeight: {
    content: String,
    default: ''
  },
  props: {
    content: {
      type: String,
      default: ''
    },
    value: {}
  },
  beforeMount(){
    // 接收 v-model 数据
    if(this.value){
      this.contents = this.value
    }
  },
  data() {
    return {
      contents: this.content,
      myConfig: {
        autoHeightEnabled: false, // 编辑器不自动被内容撑高
        initialFrameHeight: 500, // 初始容器高度
        initialFrameWidth: '100%', // 初始容器宽度
        UEDITOR_HOME_URL: '/UEditor/',
        serverUrl: ''
      }
    }
  },
  watch: {
    content: function(val) {
      this.contents = this.content
    },
    contents: function(val) {
      this.$emit('input', val)
    }
  },
  methods: {
    // 添加自定义弹窗
    addCustomDialog(editorId) {
      window.UE.registerUI('test-dialog', function(editor, uiName) {
        // 创建 dialog
        const dialog = new window.UE.ui.Dialog({
          // 指定弹出层中页面的路径，这里只能支持页面，路径参考常见问题 2
          iframeUrl: '/setting/uploadPicture?field=dialog&type=2',
          // 需要指定当前的编辑器实例
          editor: editor,
          // 指定 dialog 的名字
          name: uiName,
          // dialog 的标题
          title: '上传图片',
          // 指定 dialog 的外围样式
          cssRules: 'width:1000px;height:620px;padding:20px;'
        })
        this.dialog = dialog
        var btn = new window.UE.ui.Button({
          name: 'dialog-button',
          title: '上传图片',
          cssRules: `background-image: url(@/assets/images/icons.png);background-position: -726px -77px;`,
          onclick: function() {
            // 渲染dialog
            dialog.render()
            dialog.open()
          }
        })
        return btn
      }, 37)
    }
  }
}
</script>

<style scoped>

</style>
