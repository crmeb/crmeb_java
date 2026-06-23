<template>
  <div class="my-theme-page">

    <!-- 顶部操作栏 -->
    <div class="content-area">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <div class="left-actions">
          <el-button type="primary" @click="handleAdd">新建主题</el-button>
          <el-button @click="handleImport">导入主题</el-button>
          <img class="theme-in" src="https://www.crmeb.com/static/images/zhutishichang.png" alt="" @click="toTheme" />
        </div>
        <div class="right-actions flex">
          <el-input v-model="searchKeyword" placeholder="请输入主题名称" class="search-input m-r-10"> </el-input>
          <el-button type="primary" @click="getList">搜索</el-button>
        </div>
      </div>

      <!-- 主题列表 -->
      <div class="theme-grid" ref="gridContainer">
        <div class="theme-card" v-for="(item, index) in themeList" :key="index">
          <!-- 悬浮遮罩层 (移到最外层以覆盖整个卡片) -->
          <div class="hover-overlay">
            <div class="overlay-content">
              <!-- 二维码区域 -->
              <div class="qrcode-box">
                <div :id="'qrcode' + item.id"></div>
                <div class="scan-text">扫码预览</div>
              </div>

              <!-- 中间操作栏 -->
              <div class="middle-actions">
                <div class="tag-row">
                  <span class="theme-tag">{{ item.type }}</span>
                  <span class="action-text" @click="handleExport(item)">
                    <span class="iconfont iconic_output"></span> 导出
                  </span>
                  <div v-if="!item.is_use" class="line"></div>
                  <span v-if="!item.is_use" class="action-text" @click="handleDelete(item)">
                    <span class="iconfont iconshanchu3"></span> 删除
                  </span>
                </div>
                <div class="theme-name-overlay line2">{{ item.title || '未命名主题' }}</div>
                <div class="update-time">修改时间：{{ item.up_time }}</div>
              </div>

              <!-- 底部按钮 -->
              <div class="bottom-buttons">
                <el-button size="small" @click="handleEdit(item)">编辑主题</el-button>
                <el-button type="primary" size="small" @click="handleUse(item)">使用主题</el-button>
              </div>
            </div>
          </div>

          <!-- 卡片封面区 -->
          <div class="card-cover">
            <!-- 模糊背景 -->
            <div class="blur-bg" :style="{ backgroundImage: 'url(' + item.home_image + ')' }"></div>
            <div class="phone-preview">
              <img v-if="item.home_image" :src="item.home_image" alt="cover" />
              <div class="no-poster" v-else>
                <img :src="require('@/assets/images/no-theme-poster.png')" class="preview-image" alt="no poster" />
                <div>暂无封面</div>
              </div>
            </div>

            <!-- 正在使用标记 (仅当不悬浮时可见，或者在蒙层下方) -->
          </div>

          <!-- 底部信息区 (默认展示) -->
          <div class="card-info">
            <div class="info-top">
              <span class="tag">{{ item.type }}</span>
              <div class="using-tag" v-if="item.is_use">正在使用</div>
            </div>
            <div class="theme-name line1">{{ item.title || '未命名主题' }}</div>
          </div>
        </div>
      </div>
      <div class="acea-row row-right page" v-if="total / limit > 1">
        <pagination
          v-if="total"
          :total="total"
          :page.sync="page"
          :limit.sync="limit"
          layout="total, prev, pager, next, jumper"
          @pagination="handlePageChange"
        />
      </div>
    </div>

    <!-- 导入主题弹窗 -->
    <el-dialog
      title="导入主题"
      :visible.sync="importVisible"
      width="1188px"
      top="10vh"
      destroy-on-close
      :close-on-click-modal="false"
    >
      <theme-import @close="closeImport" @success="handleImportSuccess"></theme-import>
    </el-dialog>

    <!-- 选择主题弹窗 -->
    <theme-select-dialog :visible.sync="selectVisible" type="my" @select="handleThemeSelect"></theme-select-dialog>
  </div>
</template>

<script>
import ThemeImport from './components/themeImport.vue';
import ThemeSelectDialog from '../components/themeSelect/index.vue';
import { getThemeList, exportTheme, getExportRecord, useTheme, deleteTheme } from '@/api/theme';
import QRCode from 'qrcodejs2';
import SettingMer from '@/utils/settingMer';

export default {
  name: 'MyTheme',
  components: {
    ThemeImport,
    ThemeSelectDialog,
  },
  data() {
    return {
      BaseURL: SettingMer.httpUrl + '/',
      searchKeyword: '',
      importVisible: false,
      selectVisible: false,
      themeList: [],
      page: 1,
      limit: 10,
      total: 0,
    };
  },
  mounted() {
    this.calcLimit();
    window.addEventListener('resize', this.calcLimit);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.calcLimit);
  },
  methods: {
    calcLimit() {
      if (!this.$refs.gridContainer) return;
      const width = this.$refs.gridContainer.clientWidth;
      const cardWidth = 206;
      const gap = 20;
      const n = Math.floor((width + gap) / (cardWidth + gap));
      const newLimit = n * 2 > 0 ? n * 2 : 2;
      if (this.limit !== newLimit) {
        this.limit = newLimit;
        this.page = 1;
        this.getList();
      } else if (this.themeList.length === 0) {
        this.getList();
      }
    },
    handlePageChange(val) {
      this.page = val;
      this.getList();
    },
    getList() {
      getThemeList({ page: this.page, limit: this.limit, title: this.searchKeyword, pageType: 'theme' }).then((res) => {
        this.themeList = res.list;
        this.total = res.count;
        this.$nextTick(() => {
          this.themeList.forEach((item) => {
            if (document.getElementById('qrcode' + item.id)) {
              document.getElementById('qrcode' + item.id).innerHTML = '';
            }
            this.creatQrCode(item.id);
          });
        });
      });
    },
    //生成二维码
    creatQrCode(id) {
      let url = `${this.BaseURL}pages/index/index?theme_id=${id}`;
      var qrcode = new QRCode(document.getElementById('qrcode' + id), {
        text: url, // 需要转换为二维码的内容
        width: 100,
        height: 100,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H,
      });
    },
    handleExport(item) {
      exportTheme(item.id)
        .then((res) => {
          const recordId = res.record_id;
          // duration: 0 表示不自动关闭，轮询完成后手动关闭
          const loadingMsg = this.$message({
            type: 'success',
            message: res.message || '导出中，请稍候',
            duration: 0,
          });
          // 开始轮询，最多查 60 次（每 3s 一次，共 3 分钟）
          let attempts = 0;
          const maxAttempts = 60;
          const timer = setInterval(() => {
            attempts++;
            console.log(recordId)
            getExportRecord(recordId)
              .then((r) => {
                const url = r.downloadUrl || r.download_url;
                if (url) {
                  clearInterval(timer);
                  loadingMsg.close();
                  this.$message.success('导出成功，正在下载…');
                  window.location.href = url;
                } else if (attempts >= maxAttempts) {
                  clearInterval(timer);
                  loadingMsg.close();
                  this.$message.warning('打包超时，请稍后到下载记录中查看');
                }
              })
              .catch(() => {
                clearInterval(timer);
                loadingMsg.close();
              });
          }, 3000);
        })
        .catch((err) => {
          this.$message.error((err && (err.message || err.msg)) || '导出失败');
        });
    },
    handleDelete(item) {
      this.$confirm('确认删除该主题吗？', '提示', {
        type: 'warning',
      })
        .then(() => {
          deleteTheme(item.id)
            .then((res) => {
              this.$message.success('删除成功');
              let index = this.themeList.findIndex((e) => e.id === item.id);
              if (index !== -1) {
                this.themeList.splice(index, 1);
              }
              this.total = this.total - 1;
              if (this.themeList.length === 0 && this.page > 1) {
                this.page = this.page - 1;
              }
              this.getList();
            })
            .catch((err) => {
              this.$message.error((err && (err.message || err.msg)) || '删除失败');
            });
        })
        .catch(() => {});
    },
    handleEdit(item) {
      // 跳转编辑页
      this.$router.push({
        path: '/design/edit_theme',
        query: { id: item.id, type: 'home' },
      });
    },
    handleUse(item) {
      this.$confirm('确认使用该主题吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        useTheme(item.id)
          .then((res) => {
            this.$message.success('已切换主题');
            this.getList();
          })
          .catch((err) => {
            this.$message.error((err && (err.message || err.msg)) || '切换失败');
          });
      });
    },
    handleAdd() {
      this.selectVisible = true;
    },
    toTheme() {
      window.open('https://www.crmeb.com/theme?from=crmebkytheme', '_blank');
    },
    handleThemeSelect(theme) {
      // 跳转新建页，使用选中主题作为模板
      this.$router.push({
        path: '/design/edit_theme',
        query: { type: 'home', id: 0, tid: theme.id },
      });
    },
    handleImport() {
      this.importVisible = true;
    },
    closeImport() {
      this.importVisible = false;
    },
    handleImportSuccess() {
      this.getList();
    },
  },
};
</script>

<style lang="scss" scoped>
.my-theme-page {
  min-height: 100%;
  .theme-in {
    height: 14px;
    margin-left: 15px;
    cursor: pointer;
  }
  .page-header {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    margin-bottom: 20px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

    .header-title {
      font-size: 18px;
      font-weight: bold;
      color: #333;
    }
  }

  .content-area {
    background: #fff;
    padding: 20px;
    border-radius: 4px;
    min-height: calc(100vh - 140px);

    .action-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      .left-actions {
        display: flex;
        align-items: center;
      }
      .right-actions {
        width: 300px;
      }
    }

    .theme-grid {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;

      .theme-card {
        width: 208px;
        height: 360px;
        border: 1px solid #eee;
        border-radius: 8px;
        overflow: hidden;
        background: #fff;
        display: flex;
        flex-direction: column;
        transition: all 0.3s;

        position: relative; // Ensure card is relative for overlay

        &:hover {
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
          transform: translateY(-5px);

          .hover-overlay {
            opacity: 1;
            visibility: visible;
            transform: translateY(0);
            backdrop-filter: blur(2px);
          }
        }

        .hover-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.65);
          z-index: 100; // Higher z-index to cover everything
          opacity: 0;
          visibility: hidden;
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          transform: translateY(10px);
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 30px 12px 12px;
          color: #fff;
          border-radius: 8px; // Match card border radius

          .overlay-content {
            width: 100%;
            height: 100%;
            display: flex;
            flex-direction: column;
            align-items: center;
          }

          .qrcode-box {
            text-align: center;
            margin-top: 10px;
            background: #fff;
            padding: 10px 10px 5px 10px;
            border-radius: 8px;

            img {
              width: 100px;
              height: 100px;
              display: block;
            }
            .scan-text {
              margin-top: 5px;
              color: #333;
              font-size: 12px;
            }
          }

          .middle-actions {
            width: 100%;
            text-align: left;

            .tag-row {
              display: flex;
              align-items: center;
              justify-content: space-between;
              margin: 35px 0 10px 0;
              font-size: 12px;
              .line {
                width: 1px;
                height: 14px;
                background: rgba(255, 255, 255, 0.2);
              }
              .theme-tag {
                background: rgba(255, 255, 255, 0.1);
                padding: 2px 7px;
                border-radius: 2px;
                margin-right: 20px;
              }

              .action-text {
                cursor: pointer;
                margin-right: 3px;
                color: #ccc;

                &:hover {
                  color: #fff;
                }
                .iconfont {
                  font-size: 13px;
                }
              }
            }

            .theme-name-overlay {
              height: 40px;
              font-size: 14px;
              font-weight: 500;
              margin-bottom: 5px;
              line-height: 1.4;
            }

            .update-time {
              margin-bottom: 10px;
              font-size: 12px;
              color: rgba(255, 255, 255, 0.6);
            }
          }

          .bottom-buttons {
            display: flex;
            width: 100%;
            justify-content: space-between;

            .el-button {
              width: 48%;
            }
          }
        }

        .card-cover {
          position: relative;
          flex: 1;
          background: #f5f7fa;
          overflow: hidden;

          .blur-bg {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-repeat: no-repeat;
            background-size: 100% 100%;
            background-position: center;
            filter: blur(20px);
            opacity: 0.6;
            z-index: 1;
            transform: scale(1.2);
          }

          .phone-preview {
            width: 162px;
            height: 100%;
            margin: 23px;
            background: #fff;
            border-radius: 12px 12px 0 0;
            overflow: hidden;
            position: relative;
            border: 2px solid #fff;
            z-index: 2;

            img {
              width: 100%;
              object-fit: cover;
            }

            .no-poster {
              display: flex;
              flex-direction: column;
              margin-top: 64px;
              align-items: center;
              height: 100%;
              font-size: 12px;
              color: #999;
              img {
                width: 100px;
                object-fit: cover;
              }
            }
          }
        }
        .card-info {
          border-top: 1px solid #eee;

          .info-top {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 12px 0 0 12px;

            .tag {
              background: #f7f7f7;
              color: #333;
              font-size: 12px;
              padding: 2px 6px;
              height: max-content;
              border-radius: 2px;
            }

            .status {
              color: #1890ff;
              font-size: 12px;
            }
            .using-tag {
              color: rgba(2, 86, 255, 1);
              font-size: 12px;
              background: rgba(2, 86, 255, 0.06);
              padding: 4px 10px;
              border-radius: 10px 0 0 10px;
              z-index: 5;
            }
          }

          .theme-name {
            padding: 12px;

            font-size: 14px;
            color: #333;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-weight: 500;
          }
        }
      }
    }

    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
