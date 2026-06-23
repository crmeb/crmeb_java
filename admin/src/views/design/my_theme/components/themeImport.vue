<template>
  <div class="theme-import">
    <div class="goods-upload">
      <el-upload
        v-show="!fileUrl && !importStatus"
        ref="upload"
        class="upload-demo"
        :drag="!fileUrl"
        :show-file-list="false"
        :action="uploadUrl"
        :headers="header"
        :data="uploadData"
        name="multipart"
        :before-upload="fileChange"
        :on-success="handleSuccess"
        :on-error="handleError"
        accept=".zip"
      >
        <div v-if="uploadLoading" class="flex-column">
          <i class="el-icon-loading" style="font-size: 40px; color: #ccc"></i>
          <div class="el-upload__text">正在上传中...</div>
        </div>
        <template v-else>
          <img class="el-upload-dragger__icon mb20" src="@/assets/images/upload-theme-icon.png" alt="" />
          <div class="el-upload__text">将文件拖到此处，或<em>点击添加</em></div>
          <div class="el-upload__trip">支持 .zip，限50M以内</div>
        </template>
      </el-upload>
      <div v-show="fileUrl && !importStatus" class="file-info">
        <img class="el-upload-dragger__icon mb20" src="@/assets/images/upload-theme-icon.png" alt="" />
        <div class="el-upload__text">{{ fileName }}</div>
        <div class="flex mt12" v-if="fileUrl && !importLoading">
          <div class="active-btn" @click="selectFile">重新上传</div>
          <div class="active-btn" @click="fileUrl = ''">删除</div>
        </div>
        <div class="el-upload__trip" v-if="importLoading">
          正在导入，您可关闭当前弹窗，稍候可在列表查看导入结果
          <i class="el-icon-loading"></i>
        </div>
        <el-button v-else class="btn-import" type="primary" size="small" @click="importThemePkg">立即导入</el-button>
      </div>
      <div v-show="fileUrl && importStatus" class="file-info">
        <img class="el-upload-dragger__icon mb20" :src="statusImage" alt="" />
        <div class="el-upload__text">导入成功</div>
        <div>
          <el-button class="btn-import" size="small" @click="selectFile">再次导入</el-button>
          <el-button type="primary" class="btn-import" @click="close">完成</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { importTheme } from '@/api/theme';
import SettingMer from '@/utils/settingMer';
import { getToken } from '@/utils/auth';

export default {
  name: 'themeImport',
  data() {
    return {
      uploadUrl: SettingMer.apiBaseURL + 'admin/upload/file',
      uploadData: {
        model: 'theme',
        pid: 0,
      },
      header: {
        'Authori-zation': getToken(),
      },
      fileName: '',
      fileUrl: '',
      importStatus: false,
      importLoading: false,
      uploadLoading: false,
      statusImage: require('@/assets/images/file-success.png'),
    };
  },
  methods: {
    fileChange(file, fileList) {
      const isZip = file.name.endsWith('.zip');
      if (!isZip) {
        this.$message.error('请上传 .zip 格式的文件');
        return false;
      }
      // 限制50M
      if (file.size >= 52428800) {
        this.$message.error('文件大小不能超过50MB');
        return false;
      } else {
        this.uploadLoading = true;
        this.fileName = file.name;
        return true;
      }
    },
    selectFile() {
      this.importStatus = false;
      this.importLoading = false;
      this.uploadLoading = false;
      // 调起选择文件
      this.$refs['upload'].$refs['upload-inner'].handleClick();
    },
    handleSuccess(res, file, fileList) {
      this.uploadLoading = false;
      if (res.code === 200) {
        const data = res.data || {};
        this.fileUrl = data.url || data.src || data.fileUrl || '';
      } else {
        this.$message.error(res.message || '上传失败');
      }
    },
    handleError(err, file, fileList) {
      this.uploadLoading = false;
      this.$message.error('上传失败');
    },
    importThemePkg() {
      this.importLoading = true;
      this.importStatus = false;
      importTheme({
        url: this.fileUrl,
      })
        .then((res) => {
          // 返回导入结果
          this.importStatus = true;
          this.importLoading = false;
          this.statusImage = require('@/assets/images/file-success.png');
          this.$message.success('导入成功');
          this.$emit('success');
        })
        .catch((err) => {
          this.importLoading = false;
          this.importStatus = false;
          this.$message.error((err && err.message) || '导入失败');
        });
    },
    close() {
      this.fileUrl = '';
      this.fileName = '';
      this.importStatus = false;
      this.$emit('close');
    },
  },
};
</script>

<style lang="scss" scoped>
.goods-upload {
  width: 100%;
  height: 680px;
  .upload-demo {
    height: 100%;
  }
  ::v-deep .el-upload {
    width: 100%;
    height: 100%;
    .el-upload-dragger {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      padding: 100px 0; // Adjusted padding since there is no top part
      .el-upload-dragger__icon {
        width: 54px;
        // height: 57px;
      }
      .el-upload__trip {
        font-weight: 400;
        font-size: 12px;
        color: #999999;
        margin-top: 6px;
      }
    }
  }
  .file-info {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    border: 1px dashed #d9d9d9;
    border-radius: 4px;
    .active-btn {
      color: var(--prev-color-primary);
      font-size: 12px;
      font-weight: 400;
      margin: 0 6px;
      cursor: pointer;
    }
    .btn-import {
      margin-top: 26px;
    }
    .el-upload-dragger__icon {
      height: 57px;
    }
    .el-upload__trip {
      display: flex;
      align-items: center;
      font-weight: 400;
      font-size: 12px;
      color: #999;
      margin-top: 6px;
    }
  }
}
</style>
