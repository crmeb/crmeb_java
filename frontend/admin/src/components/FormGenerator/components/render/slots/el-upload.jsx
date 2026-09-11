import { ElButton } from 'element-plus/es/components/button/index.mjs';
import { Upload } from '@element-plus/icons-vue';

export default {
  'list-type': (h, conf, key) => {
    const list = [];
    const config = conf.__config__;
    if (conf['list-type'] === 'picture-card') {
      list.push(<i class="el-icon-plus"></i>);
    } else {
      list.push(
        <ElButton type="primary" icon={Upload}>
          {config.buttonText}
        </ElButton>,
      );
    }
    if (config.showTip) {
      list.push(
        <div class="el-upload__tip">
          只能上传不超过 {config.fileSize}
          {config.sizeUnit} 的{conf.accept}文件
        </div>,
      );
    }
    return list;
  },
};
