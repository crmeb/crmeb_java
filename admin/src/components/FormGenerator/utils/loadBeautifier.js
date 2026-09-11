import { ElLoading } from 'element-plus';

// js-beautify 单例
// 原从 cdn.bootcss.com 动态加载，现改为本地 npm 包。
// 保留单例 + 回调签名，Home/JsonDrawer/FormDrawer 调用点无需改动。
// js-beautify 导出 { html, js, css }，与原 beautifier.html()/js()/css() 调用方式兼容。
let beautifierObj;

export default function loadBeautifier(cb) {
  if (beautifierObj) {
    cb(beautifierObj);
    return;
  }

  const loading = ElLoading.service({
    fullscreen: true,
    lock: true,
    text: '格式化资源加载中...',
    spinner: 'el-icon-loading',
    background: 'rgba(255, 255, 255, 0.5)',
  });

  // 动态 import 触发独立 chunk 加载（beautify-vendor），避免进入口
  import('js-beautify')
    .then((Module) => {
      beautifierObj = Module.default || Module;
      loading.close();
      cb(beautifierObj);
    })
    .catch((err) => {
      loading.close();
      console.error('js-beautify 加载失败', err);
    });
}
