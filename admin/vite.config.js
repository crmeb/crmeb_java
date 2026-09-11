// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons';
import path from 'path';
import { fileURLToPath } from 'url';

// ESM 下无 __dirname，通过 import.meta.url 构造
const __dirname = path.dirname(fileURLToPath(import.meta.url));
// 注意：不要在 vite.config.js 中 import src/ 下业务模块（如 settings.js），
// 因为它们可能使用 import.meta.env，而 config 加载阶段 import.meta.env 尚未填充。

function resolve(dir) {
  return path.join(__dirname, dir);
}

function kebabCase(name) {
  return name
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .replace(/([A-Z])([A-Z][a-z])/g, '$1-$2')
    .toLowerCase();
}

const elementPlusComponentDirMap = {
  AnchorLink: 'anchor',
  Aside: 'container',
  AutoResizer: 'table-v2',
  AvatarGroup: 'avatar',
  BreadcrumbItem: 'breadcrumb',
  ButtonGroup: 'button',
  CarouselItem: 'carousel',
  CheckboxButton: 'checkbox',
  CheckboxGroup: 'checkbox',
  CollapseItem: 'collapse',
  DescriptionsItem: 'descriptions',
  DropdownItem: 'dropdown',
  DropdownMenu: 'dropdown',
  Footer: 'container',
  FormItem: 'form',
  Header: 'container',
  Main: 'container',
  MenuItem: 'menu',
  MenuItemGroup: 'menu',
  Option: 'select',
  OptionGroup: 'select',
  RadioButton: 'radio',
  RadioGroup: 'radio',
  SkeletonItem: 'skeleton',
  SplitterPanel: 'splitter',
  Step: 'steps',
  SubMenu: 'menu',
  TabPane: 'tabs',
  TableColumn: 'table',
  TimelineItem: 'timeline',
  TourStep: 'tour',
};

function elementPlusOnDemandResolver(name) {
  if (name === 'ElSwitch') {
    return {
      name: 'default',
      from: resolve('src/components/ElementPlusCompat/Switch.vue'),
    };
  }
  if (/^ElIcon[A-Z]/.test(name)) {
    return {
      name: name.replace(/^ElIcon/, ''),
      from: '@element-plus/icons-vue',
    };
  }
  if (/^El[A-Z]/.test(name)) {
    const componentName = name.slice(2);
    const dirName = elementPlusComponentDirMap[componentName] || kebabCase(componentName);
    return {
      name,
      from: `element-plus/es/components/${dirName}/index.mjs`,
    };
  }
}

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const port = env.port || env.npm_config_port || 9527; // dev port

  return {
    base: '/',
    // 路径别名
    resolve: {
      alias: {
        '@': resolve('src'),
        qrcodejs2: resolve('src/utils/qrcodejs2.js'),
        // 启用 Vue 运行时模板编译器（FormGenerator 预览依赖运行时编译 template 字符串）
        vue: 'vue/dist/vue.esm-bundler.js',
      },
      // 兼容 Vue CLI 时代 import 目录（解析到 index.vue）及无扩展名 .vue 的写法
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    },
    // 插件
    plugins: [
      vue(),
      // JSX 支持（部分业务页如 views/store/index.vue、FormGenerator/Parser.vue 使用 JSX 渲染函数）
      vueJsx(),
      // Element Plus 按需自动导入：
      // - 官方 ElementPlusResolver({ importStyle: 'css' }) 负责组件 JS + 按需 CSS 样式注入（含 submenu 等子路径组件）
      // - 自定义 elementPlusOnDemandResolver 负责 ElSwitch->本地兼容组件、ElIcon->@element-plus/icons-vue 两个特殊重定向
      //   （仅返回 component，不返回 sideEffects，不会与官方 resolver 重复注入样式）
      // 顺序：官方在前，自定义在后（前者命中 ElXxx 注入样式，后者覆盖 ElSwitch/ElIcon 的特殊来源）
      AutoImport({
        resolvers: [ElementPlusResolver({ importStyle: 'css' }), elementPlusOnDemandResolver],
        imports: ['vue', 'vue-router', 'pinia'],
        dts: false,
      }),
      Components({
        dirs: ['src/components'],
        resolvers: [ElementPlusResolver({ importStyle: 'css' }), elementPlusOnDemandResolver],
        dts: false,
        globs: ['src/components/**/*.vue', '!src/components/FormGenerator/**', '!src/components/base/**'],
      }),
      // SVG 雪碧图
      createSvgIconsPlugin({
        iconDirs: [resolve('src/icons/svg')],
        symbolId: 'icon-[name]',
      }),
    ],
    // SCSS 配置：不使用 additionalData 全局注入（会导致被注入文件自身循环加载）。
    // 各 scss 文件按需 @use/@import 所需变量即可。
    css: {
      preprocessorOptions: {
        scss: {
          // 静默依赖包中的 scss 警告
          quietDeps: true,
          // 静默 @import 和 legacy-js-api 废弃警告（已迁移到 @use，但部分第三方依赖仍用 @import）
          silenceDeprecations: ['legacy-js-api', 'import'],
        },
      },
    },
    // 静态资源目录
    publicDir: 'public',
    // 服务配置
    server: {
      host: '0.0.0.0',
      port: port,
      open: false,
      // 代理（如需本地接口代理可在此配置）
      proxy: {
        // 示例：
        // '/api': {
        //   target: env.VITE_APP_BASE_API,
        //   changeOrigin: true,
        // },
      },
    },
    // 构建配置
    build: {
      outDir: 'dist',
      assetsDir: 'static',
      sourcemap: false,
      // 分包
      rollupOptions: {
        onwarn(warning, warn) {
          const msg = warning.message || '';
          const file = warning.loc?.file || '';
          if (
            warning.code === 'EVAL' ||
            (warning.code === 'SOURCEMAP_ERROR' && file.includes('/src/views/mobile/auth-send/index.vue')) ||
            /@vueuse\/core/.test(msg) ||
            /vconsole/.test(msg)
          ) {
            return;
          }
          warn(warning);
        },
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
          manualChunks: {
            // 框架核心
            'vue-vendor': ['vue', 'vue-router', 'pinia'],
            // 富文本编辑器（已 defineAsyncComponent 懒加载，独立 chunk 避免重复进多路由）
            'editor-vendor': ['@wangeditor/editor', '@wangeditor/editor-for-vue'],
            // Excel/压缩导出
            'excel-vendor': ['xlsx', 'jszip', 'file-saver'],
            // 轮播
            'swiper-vendor': ['swiper'],
            // 截图
            'canvas-vendor': ['html2canvas'],
            // 代码格式化（FormGenerator 用）
            'beautify-vendor': ['js-beautify'],
          },
        },
      },
      // chunk 大小警告阈值
      chunkSizeWarningLimit: 1500,
    },
    // define 全局变量（兼容旧代码中的 process.env.NODE_ENV）
    define: {
      'process.env.NODE_ENV': JSON.stringify(mode === 'staging' ? 'production' : mode),
    },
    // optimizeDeps 预构建
    optimizeDeps: {
      include: ['vue', 'vue-router', 'pinia', 'js-cookie', 'nprogress', 'axios', 'crypto-js'],
    },
  };
});
