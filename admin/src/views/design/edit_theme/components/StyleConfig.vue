<template>
  <div class="style-config-container">
    <!-- 左侧配置 -->
    <div class="config-panel">
      <div class="panel-header">
        <span class="title">当前风格：商城通用主题</span>
        <el-button type="text" @click="openStyleDialog">更换风格</el-button>
        <span class="restore-btn" @click="initData()"><i class="el-icon-refresh-left"></i> 还原主题</span>
      </div>

      <div class="config-body">
        <div class="">
          <div class="config-item">
            <div class="label">主题颜色</div>
            <div class="color-picker-row">
              <el-color-picker v-model="themeColor"></el-color-picker>
              <span class="color-value">{{ themeColor }}</span>
            </div>
          </div>

          <div class="config-item">
            <div class="label">渐变颜色</div>
            <div class="color-picker-row">
              <el-color-picker v-model="gradientColor"></el-color-picker>
              <span class="color-value">{{ gradientColor }}</span>
            </div>
          </div>

          <div class="config-item">
            <div class="label">辅助颜色</div>
            <div class="color-picker-row">
              <el-color-picker v-model="subColor"></el-color-picker>
              <span class="color-value">{{ subColor }}</span>
            </div>
          </div>
        </div>
        <div class="qrcode-section">
          <div class="qrcode-list">
            <div class="qrcode-item">
              <div class="qrcode-box" ref="qrCodeUrl"></div>
            </div>
          </div>
          <div class="demo-tip">扫码查看完整演示</div>
        </div>
      </div>
    </div>

    <!-- 右侧预览 -->
    <div class="preview-panel">
      <div class="preview-title">预览效果</div>

      <div class="preview-list">
        <!-- 模拟预览图 0 1 2 -->
        <div class="preview-item" v-for="i in [0, 1, 2]" :key="i">
          <div class="phone-mockup">
            <img
              :style="{ background: themeColor }"
              :src="require('@/assets/images/theme-bg-' + i + '.png')"
              alt="preview"
            />
            <div v-if="i == 0" class="buy-btn">
              <div class="btn btn-outline" :style="{ background: subColor }">加入购物车</div>
              <div class="btn btn-primary" :style="{ background: themeColor }">立即购买</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 风格选择弹窗 -->
    <el-dialog
      :visible.sync="styleDialogVisible"
      width="1188px"
      top="9vh"
      custom-class="theme-dialog"
      :show-close="false"
      append-to-body
      :modal="true"
      :modal-append-to-body="true"
      :lock-scroll="true"
      @close="handleClose"
    >
      <div slot="title" v-if="!showDetail" class="dialog-header">
        <div class="dialog-header-tabs">
          <div class="tabs">
            <span class="title">我的主题</span>
            <img
              class="title-icon"
              src="https://www.crmeb.com/static/images/zhutishichang.png"
              alt=""
              @click="toTheme"
            />
          </div>
        </div>
        <i class="el-icon-close" @click="handleClose"></i>
      </div>
      <div v-else class="detail-header" slot="title">
        <div class="left-action">
          <div class="back-btn" @click="backToStyleList"><i class="el-icon-arrow-left"></i> 返回</div>
          <div class="vertical-line"></div>
          <span class="detail-title">风格详情</span>
        </div>
      </div>

      <transition name="fade-transform" mode="out-in">
        <div class="dialog-content" v-if="!showDetail" key="list">
          <div class="content-wrapper">
            <div class="main-content">
              <div class="filters-header">
                <div class="search-box">
                  <el-input
                    v-model="searchKeyword"
                    placeholder="请输入主题名称"
                    prefix-icon="el-icon-search"
                    size="small"
                  ></el-input>
                </div>
              </div>

              <div class="theme-grid" v-loading="loading">
                <div class="theme-item" v-for="(item, index) in filteredStyleList" :key="index">
                  <div class="theme-cover" :style="{ background: hexToRgba(item.themeColor, 0.1) }">
                    <div class="phone-preview">
                      <img
                        :style="{ background: item.themeColor }"
                        :src="require('@/assets/images/theme-bg-1.png')"
                        alt="style"
                      />
                    </div>
                  </div>
                  <div class="theme-info">
                    <div class="default-info">
                      <div class="theme-title line1">{{ item.name || '无名称' }}</div>
                      <div class="color-dots">
                        <span class="dot" :style="{ background: item.themeColor }"></span>
                        <span class="dot" :style="{ background: item.gradientColor }"></span>
                        <span class="dot" :style="{ background: item.subColor }"></span>
                      </div>
                    </div>
                    <div class="hover-actions">
                      <el-button plain size="small" @click="viewStyleDetail(item)">查看详情</el-button>
                      <el-button type="primary" size="small" @click="handleStyleSelect(item)">使用风格</el-button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="pagination-wrapper">
                <el-pagination
                  background
                  layout="prev, pager, next"
                  :total="total"
                  :page-size="limit"
                  :current-page.sync="page"
                  @current-change="handlePageChange"
                >
                </el-pagination>
              </div>
            </div>
          </div>
        </div>

        <!-- 详情视图 -->
        <div class="detail-content" v-else key="detail">
          <div class="detail-body">
            <div class="detail-images-section">
              <div class="section-title">页面预览</div>
              <div class="images-list">
                <div class="image-item" v-for="i in [0, 1, 2]" :key="i">
                  <div class="phone-mockup">
                    <img
                      :style="{ background: selectedStyle.themeColor }"
                      :src="require('@/assets/images/theme-bg-' + i + '.png')"
                      alt="preview"
                    />
                    <div v-if="i == 0" class="buy-btn">
                      <div class="btn btn-outline" :style="{ background: selectedStyle.subColor }">加入购物车</div>
                      <div class="btn btn-primary" :style="{ background: selectedStyle.themeColor }">立即购买</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </el-dialog>
  </div>
</template>

<script>
import { themeSave, themeInfo, getThemeList } from '@/api/theme';
import QRCode from 'qrcodejs2';
import Setting from '@/utils/settingMer';

export default {
  name: 'StyleConfig',
  data() {
    return {
      themeColor: '#E93323',
      gradientColor: '#FF7F00',
      subColor: '#FFC300',
      BaseURL: Setting.httpUrl + '/',
      styleDialogVisible: false,
      activeTab: 'my',
      searchKeyword: '',
      styleList: [],
      showDetail: false,
      selectedStyle: {},
      page: 1,
      limit: 10,
      total: 0,
      loading: false,
    };
  },
  computed: {
    filteredStyleList() {
      return this.styleList.filter((item) => {
        const matchSearch = item.name.includes(this.searchKeyword);
        return matchSearch;
      });
    },
  },
  mounted() {
    if (this.$route.query.id != 0) this.initData();
  },
  methods: {
    openStyleDialog() {
      this.styleDialogVisible = true;
      this.page = 1;
      this.getStyleList();
    },
    getStyleList() {
      this.loading = true;
      getThemeList({ page: this.page, limit: this.limit })
        .then((res) => {
          this.loading = false;
          this.total = res.data.count;
          this.styleList = res.data.list.map((item) => {
            return {
              ...item,
              name: item.title,
              image: item.image,
              themeColor: (item.theme_data && item.theme_data.theme_color) || '#E93323',
              gradientColor: (item.theme_data && item.theme_data.gradient_color) || '#FF7F00',
              subColor: (item.theme_data && item.theme_data.sub_color) || '#FFC300',
            };
          });
        })
        .catch(() => {
          this.loading = false;
        });
    },
    handlePageChange(val) {
      this.page = val;
      this.getStyleList();
    },
    handleStyleSelect(item) {
      this.themeColor = (item.theme_data && item.theme_data.theme_color) || '#E93323';
      this.gradientColor = (item.theme_data && item.theme_data.gradient_color) || '#FF7F00';
      this.subColor = (item.theme_data && item.theme_data.sub_color) || '#FFC300';
      this.$message.success('已应用风格颜色');
      this.styleDialogVisible = false;
      this.showDetail = false;
    },
    toTheme() {
      window.open('https://www.crmeb.com/theme?from=crmebkytheme', '_blank');
    },
    viewStyleDetail(item) {
      this.selectedStyle = item;
      this.showDetail = true;
    },
    backToStyleList() {
      this.showDetail = false;
    },
    handleClose() {
      this.styleDialogVisible = false;
      this.showDetail = false;
    },
    // 生成二维码
    creatQrCode() {
      if (this.$refs.qrCodeUrl) {
        this.$refs.qrCodeUrl.innerHTML = '';
        let url = `${this.BaseURL}pages/index/index?theme_id=${this.$route.query.id}`;
        new QRCode(this.$refs.qrCodeUrl, {
          text: url,
          width: 110,
          height: 110,
          colorDark: '#000000',
          colorLight: '#ffffff',
          correctLevel: QRCode.CorrectLevel.H,
        });
      }
    },
    hexToRgba(hex, opacity) {
      if (!hex) hex = '#E93323';
      hex = hex.replace('#', '');
      const r = parseInt(hex.substring(0, 2), 16);
      const g = parseInt(hex.substring(2, 4), 16);
      const b = parseInt(hex.substring(4, 6), 16);
      return `rgba(${r}, ${g}, ${b}, ${opacity})`;
    },
    bgLight() {
      //这里根据this.themeColor计算出一个该色值透明度为0.1的颜色
      const hex = this.themeColor.replace('#', '');
      const r = parseInt(hex.substring(0, 2), 16);
      const g = parseInt(hex.substring(2, 4), 16);
      const b = parseInt(hex.substring(4, 6), 16);
      return `rgba(${r}, ${g}, ${b}, 0.1)`;
    },
    // 初始化数据
    initData() {
      this.$nextTick(() => {
        this.creatQrCode();
      });
      themeInfo(this.$route.query.id, 'theme').then((res) => {
        if (res.data) {
          this.themeColor = res.data.theme_color;
          this.gradientColor = res.data.gradient_color;
          this.subColor = res.data.sub_color;
        }
      });
    },
    saveOnly() {
      this.$confirm('确认仅保存风格吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(() => {
        themeSave(this.$route.query.id, {
          type: 'theme',
          value: {
            theme_color: this.themeColor,
            gradient_color: this.gradientColor,
            sub_color: this.subColor,
            light_color: this.bgLight(),
          },
        }).then((res) => {
          if (this.$route.query.id == 0) {
            this.$router.replace({ query: { ...this.$route.query, id: res.data.id } });
          }
          this.$message({
            type: 'success',
            message: res.msg,
          });
        });
      });
    },
    saveAndClose() {
      // 保存主题配置数据
      themeSave(this.$route.query.id, {
        type: 'theme',
        value: {
          theme_color: this.themeColor,
          gradient_color: this.gradientColor,
          sub_color: this.subColor,
          light_color: this.bgLight(),
        },
      })
        .then((res) => {
          // 如果是新建（id为0），更新路由参数
          if (this.$route.query.id == 0) {
            this.$router.replace({ query: { ...this.$route.query, id: res.data.id } });
          }

          // 显示成功消息
          this.$message({
            type: 'success',
            message: res.msg,
          });

          // 保存成功后跳转回主题列表页面
          this.$router.push(`${''}/setting/my_theme`);
        })
        .catch((err) => {
          // 保存失败时的处理
          this.$message.error(err.msg || '保存失败');
        });
    },
  },
};
</script>

<style lang="scss" scoped>
/* fade-transform */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.5s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.style-config-container {
  display: flex;
  height: 100%;

  .config-panel {
    background: #fff;
    padding: 35px 40px;
    border-right: 1px solid #eee;
    ::v-deep .el-color-picker__trigger {
      border: none;
    }
    .panel-header {
      display: flex;
      align-items: center;
      margin-bottom: 30px;
      ::v-deep .el-button {
        font-size: 14px;
        padding-left: 0 !important;
      }
      .title {
        font-size: 16px;
        font-weight: 500;
        margin-right: 10px;
      }

      .restore-btn {
        margin-left: auto;
        cursor: pointer;
        color: #666;
        font-size: 14px;

        &:hover {
          color: #1890ff;
        }
      }
    }

    .config-body {
      display: flex;
      background: #fafafa;
      border-radius: 8px 8px 8px 8px;
      padding: 20px 50px 20px 20px;
      .config-item {
        margin-bottom: 25px;
        display: flex;
        align-items: center;

        .label {
          width: 80px;
          color: #666;
        }

        .color-picker-row {
          display: flex;
          align-items: center;

          .color-value {
            margin-left: 10px;
            color: #333;
          }
        }
      }
      .config-item:last-child {
        margin-bottom: 0;
      }

      .qrcode-section {
        margin-left: 50px;
        border-left: 1px dashed #eee;
        padding-left: 50px;

        .qrcode-list {
          display: flex;
          justify-content: space-around;
          margin-bottom: 10px;

          .qrcode-item {
            display: flex;
            flex-direction: column;
            align-items: center;

            .qrcode-box {
              width: 110px;
              height: 110px;
              border: 1px solid #eee;
              padding: 5px;
              background: #fff;
              display: flex;
              align-items: center;
              justify-content: center;

              img,
              canvas {
                width: 100% !important;
                height: 100% !important;
              }
            }

            .qrcode-label {
              margin-top: 10px;
              font-size: 12px;
              color: #666;
            }
          }
        }
        .demo-tip {
          text-align: center;
          font-size: 12px;
          color: #999;
        }
      }
    }
  }

  .preview-panel {
    flex: 1;
    background: #fff;
    padding: 40px;
    display: flex;
    flex-direction: column;

    .preview-title {
      font-size: 14px;
      margin-bottom: 20px;
    }

    .preview-list {
      display: flex;
      gap: 30px;
      overflow-y: auto;
      flex: 1;

      .preview-item {
        .phone-mockup {
          width: 202px;
          height: max-content;
          overflow: hidden;
          position: relative;

          .buy-btn {
            position: absolute;
            bottom: 24px;
            right: 7px;
            display: flex;

            .btn {
              padding: 4px 11px;
              border-radius: 5px;
              color: #fff;
              font-size: 8px;
            }

            .btn-outline {
              border-radius: 20px 0 0 20px;
            }

            .btn-primary {
              border-radius: 0 20px 20px 0;
            }
          }

          img {
            width: 100%;
            height: 100%;
            border-radius: 20px;
            object-fit: cover;
          }
        }
      }
    }
  }

  // 弹窗样式
  ::v-deep .theme-dialog {
    border-radius: 8px;
    overflow: hidden;
    margin-top: 50px !important;
    margin-bottom: 50px !important;
    width: calc(100vw - 140px) !important;
    height: calc(100vh - 100px) !important;
    margin-left: 70px !important;
    margin-right: 70px !important;

    .el-dialog__header {
      border-bottom: 1px solid #f0f0f0;
      padding: 0px 20px !important;
    }
  }
}
::v-deep .el-dialog__body {
  max-height: max-content;
  padding: 0px !important;
  overflow-y: auto;
}
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  .dialog-header-tabs {
    background: #fff;

    .tabs {
      display: flex;
      align-items: center;
      .title {
        font-size: 14px;
        line-height: 22px;
        font-weight: 500;
        color: #333;
        cursor: pointer;
        position: relative;
      }
      .theme-in {
        height: 12px;
        margin-left: 20px;
        cursor: pointer;
      }
      .title-icon {
        height: 14px;
        margin-left: 10px;
      }
    }
  }
}

.dialog-content {
  display: flex;
  flex-direction: column;
  height: calc(100% - 20px);
  background-color: #f5f7fa;

  .content-wrapper {
    flex: 1;
    display: flex;
    overflow: hidden;

    .main-content {
      flex: 1;
      padding: 20px;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background: #fff;
      .filters-header {
        display: flex;
        align-items: center;
        margin-bottom: 20px;

        .search-box {
          width: 300px;
        }
      }

      .theme-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 20px;
        overflow-y: auto;
        padding-right: 5px;
        flex: 1;

        .theme-item {
          background: #fff;
          border-radius: 8px;
          overflow: hidden;
          border: 1px solid #f0f0f0;
          transition: all 0.3s;
          display: flex;
          flex-direction: column;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);

            .theme-info {
              .default-info {
                // opacity: 0;
                transform: translateY(-10px);
              }
              .hover-actions {
                opacity: 1;
                visibility: visible;
                transform: translateY(0);
              }
            }
          }

          .theme-cover {
            position: relative;
            height: 240px;
            overflow: hidden;
            display: flex;
            justify-content: center;
            align-items: center;

            .phone-preview {
              width: 140px;
              height: 100%;
              overflow: hidden;
              padding: 20px 0;

              img {
                width: 100%;
                border-radius: 10px;
              }
            }
          }

          .theme-info {
            padding: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            background: #fff;

            .default-info {
              display: flex;
              align-items: center;
              justify-content: space-between;
              width: 100%;
              transition: all 0.3s;
            }

            .theme-title {
              font-size: 14px;
              color: #333;
              font-weight: 500;
            }

            .color-dots {
              display: flex;
              gap: 4px;

              .dot {
                width: 16px;
                height: 16px;
                border-radius: 50%;
                display: inline-block;
              }
            }

            .hover-actions {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              display: flex;
              align-items: center;
              justify-content: center;
              gap: 0px;
              background: #fff;
              opacity: 0;
              visibility: hidden;
              transform: translateY(10px);
              transition: all 0.3s;
              z-index: 2;
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
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0px;
  background: #fff;

  .left-action {
    display: flex;
    align-items: center;
    gap: 15px;

    .back-btn {
      cursor: pointer;
      color: #666;
      font-size: 14px;
      display: flex;
      align-items: center;

      &:hover {
        color: #1890ff;
      }

      i {
        margin-right: 4px;
      }
    }

    .vertical-line {
      width: 1px;
      height: 16px;
      background: #e8e8e8;
    }

    .detail-title {
      font-size: 16px;
      font-weight: bold;
      color: #333;
    }
  }
}
.detail-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f7fa;

  .detail-body {
    flex: 1;
    overflow-y: auto;
    padding: 20px;

    .detail-top {
      display: flex;
      background: #fff;
      border-radius: 8px;
      padding: 30px;
      margin-bottom: 20px;

      .detail-preview-main {
        width: 220px;
        margin-right: 24px;
        flex-shrink: 0;

        .phone-mockup-main {
          width: 100%;
          height: 380px;
          border-radius: 20px;
          overflow: hidden;
          border: 1px solid #eeeeee;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
      }

      .detail-info-box {
        flex: 1;
        padding-top: 20px;

        .info-title {
          font-size: 24px;
          font-weight: bold;
          color: #333;
          margin-bottom: 20px;
        }

        .info-desc {
          font-size: 14px;
          color: #666;
          line-height: 1.8;
          margin-bottom: 40px;
          max-width: 600px;
        }

        .info-colors {
          margin-bottom: 40px;

          .color-row {
            display: flex;
            align-items: center;
            margin-bottom: 15px;

            .label {
              width: 70px;
              color: #999;
              font-size: 14px;
            }

            .color-block {
              display: flex;
              align-items: center;
              background: #f5f7fa;
              padding: 5px 15px 5px 5px;
              border-radius: 4px;

              .dot {
                width: 24px;
                height: 24px;
                border-radius: 4px;
                margin-right: 10px;
              }

              .value {
                font-size: 14px;
                color: #333;
                font-family: Monaco, monospace;
              }
            }
          }
        }

        .info-btn {
          .el-button {
            width: 120px;
            height: 40px;
          }
        }
      }

      .detail-qrcode {
        width: 200px;
        border-left: 1px solid #eee;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding-left: 40px;

        .qrcode-box {
          width: 140px;
          height: 140px;
          padding: 10px;
          border: 1px solid #eee;
          border-radius: 4px;
          margin-bottom: 15px;
          display: flex;
          align-items: center;
          justify-content: center;

          img,
          canvas {
            width: 100% !important;
            height: 100% !important;
          }
        }

        .qrcode-text {
          font-size: 13px;
          color: #666;
        }
      }
    }

    .detail-images-section {
      background: #fff;
      border-radius: 8px;
      padding: 30px;

      .section-title {
        font-size: 18px;
        font-weight: bold;
        color: #333;
        margin-bottom: 25px;
        padding-left: 10px;
        border-left: 4px solid #1890ff;
        line-height: 1;
      }

      .images-list {
        display: flex;
        gap: 40px;
        overflow-x: auto;
        padding-bottom: 20px;

        .image-item {
          flex-shrink: 0;
          display: flex;
          flex-direction: column;
          align-items: center;

          .phone-mockup {
            width: 240px;
            height: max-content;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
            position: relative;
            transition: transform 0.3s;
            border: 1px solid #eee;

            &:hover {
              transform: translateY(-5px);
            }

            .buy-btn {
              position: absolute;
              bottom: 28px;
              right: 9px;
              display: flex;

              .btn {
                padding: 5px 10px;
                border-radius: 5px;
                color: #fff;
                font-size: 10px;
              }

              .btn-outline {
                border-radius: 20px 0 0 20px;
              }

              .btn-primary {
                border-radius: 0 20px 20px 0;
              }
            }

            img {
              width: 100%;
              display: block;
            }
          }

          .page-name {
            margin-top: 15px;
            font-size: 14px;
            color: #666;
          }
        }
      }
    }
  }
}
</style>
