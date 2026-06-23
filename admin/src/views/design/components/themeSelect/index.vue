<template>
  <el-dialog
    :visible.sync="visible"
    width="1188px"
    top="5vh"
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
          <img class="theme-in" src="https://www.crmeb.com/static/images/zhutishichang.png" alt="" @click="toTheme" />
        </div>
      </div>
      <i class="el-icon-close" @click="handleClose"></i>
    </div>
    <div class="dialog-content" v-if="!showDetail">
      <!-- 顶部Tab切换 -->

      <div class="content-wrapper">
        <!-- 右侧内容 -->
        <div class="main-content">
          <div class="filters-header">
            <div v-if="type != 'mall'" class="filter-left">
              <el-select v-model="currentFilter" size="small" placeholder="首页" style="width: 204px">
                <el-option
                  v-for="item in filterOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
              </el-select>
            </div>
            <div class="search-box">
              <el-input
                v-model="searchKeyword"
                placeholder="请输入主题名称"
                suffix-icon="el-icon-search"
                size="small"
                @change="searchTheme"
              ></el-input>
            </div>
          </div>

          <div class="theme-grid">
            <!-- 新建空白主题 -->
            <div class="theme-item create-new" v-if="activeTab === 'my'" @click="createNewTheme">
              <div class="create-icon">
                <div class="iconfont iconic_brush"></div>
              </div>
              <div class="create-text">新建空白主题</div>
            </div>

            <!-- 主题列表 -->
            <div class="theme-item" v-for="(theme, index) in themes" :key="index">
              <div class="theme-cover">
                <!-- 背景模糊层 -->
                <div class="blur-bg" :style="{ backgroundImage: `url(${theme[currentFilter]})` }"></div>
                <div class="phone-preview">
                  <img
                    v-if="theme[currentFilter] || theme.home_image"
                    :src="theme[currentFilter] || theme.home_image"
                    alt="theme"
                  />
                  <div class="no-poster" v-else>
                    <img :src="require('@/assets/images/no-theme-poster.png')" class="preview-image" alt="no poster" />
                    <div>暂无封面</div>
                  </div>
                </div>
              </div>
              <div class="theme-info">
                <div class="theme-title">
                  <div class="line1">
                    {{ theme.title || '暂无主题名称' }}
                  </div>
                  <div class="tag" v-if="theme.page_type == 'micro'">专题页</div>
                </div>
                <div class="theme-time">修改时间：{{ theme.up_time }}</div>

                <div class="theme-hover-actions">
                  <div class="button default" @click="viewThemeDetail(theme)">查看详情</div>
                  <div class="button primary" @click="selectTheme(theme)">使用主题</div>
                </div>
              </div>
            </div>
          </div>
          <div class="acea-row row-right page" v-if="total > limit">
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
      </div>
    </div>

    <!-- 详情页视图 -->
    <div class="detail-content" v-else>
      <div class="detail-header">
        <div class="flex">
          <div class="back-btn" @click="showDetail = false"><i class="el-icon-arrow-left"></i> 返回</div>
          <div class="detail-title">主题详情</div>
        </div>
        <i class="el-icon-close" @click="handleClose"></i>
      </div>
      <div class="detail-body">
        <div class="detail-top">
          <div class="detail-preview-main">
            <img
              v-if="currentTheme[currentFilter] || currentTheme.home_image"
              :src="currentTheme[currentFilter] || currentTheme.home_image"
              alt="preview"
            />
            <div class="no-poster" v-else>
              <img :src="require('@/assets/images/no-theme-poster.png')" class="preview-image" alt="no poster" />
            </div>
          </div>
          <div class="detail-info-box">
            <div>
              <div class="info-title">{{ currentTheme.title || '暂无主题名称' }}</div>
              <div class="info-desc">
                {{ currentTheme.info || '暂无描述' }}
              </div>
            </div>
            <div class="info-btn">
              <el-button type="primary" @click="selectTheme(currentTheme)">使用主题</el-button>
            </div>
          </div>
        </div>

        <div class="detail-images-section">
          <div class="section-title">主题图片</div>
          <div class="images-list">
            <div class="image-item" v-for="(opt, index) in filterOptions" :key="index">
              <div class="phone-mockup">
                <img
                  v-if="currentTheme[opt.value]"
                  v-lazy="currentTheme[opt.value]"
                  :src="currentTheme[opt.value]"
                  class="preview-image"
                />
                <div class="no-poster" v-else>
                  <img :src="require('@/assets/images/no-theme-poster.png')" class="preview-image" alt="no poster" />
                  <div>暂无封面</div>
                </div>
              </div>
            </div>
            <div class="detail-qrcode">
              <div class="qrcode-box" id="qrcodeDetail"></div>
              <div class="qrcode-text">扫码查看完整演示</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { getThemeList, useThemeData } from '@/api/theme';
import QRCode from 'qrcodejs2';
import SettingMer from '@/utils/settingMer';

export default {
  name: 'ThemeSelectDialog',
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    type: {
      type: String,
      default: 'mall', // mall: 商城装修, my: 我的主题
    },
    themeId: {
      type: [Number, String],
      default: 0,
    },
    currentType: {
      type: String,
      default: 'home',
    },
    activeTab: {
      type: String,
      default: 'my',
    },
  },
  data() {
    return {
      showDetail: false,
      currentTheme: {},
      currentCategory: 'all',
      currentFilter: 'home_image',
      searchKeyword: '',
      filterOptions: [
        { label: '首页', value: 'home_image' },
        { label: '分类页', value: 'category_image' },
        { label: '详情页', value: 'detail_image' },
        { label: '个人中心', value: 'user_image' },
      ],
      themes: [],
      page: 1,
      limit: 9,
      total: 0,
      BaseURL: SettingMer.httpUrl + '/',
    };
  },
  watch: {
    currentType: {
      handler(newVal) {
        this.currentFilter = `${newVal}_image`;
      },
      immediate: true,
    },
    activeTab: {
      handler(newVal) {
        if (newVal === 'mall') {
          this.limit = 10;
        } else {
          this.limit = 9;
        }
      },
      immediate: true,
    },
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      const reqData = { page: this.page, limit: this.limit, title: this.searchKeyword };
      if (this.type == 'mall' && this.currentType == 'home') {
        reqData.pageType = 'all';
      } else {
        reqData.pageType = 'theme';
      }
      getThemeList(reqData).then((res) => {
        this.themes = res.list;
        this.total = res.count;
      });
    },
    handlePageChange(val) {
      this.page = val;
      this.getList();
    },
    toTheme() {
      window.open('https://www.crmeb.com/theme?from=crmebkytheme', '_blank');
    },
    handleClose() {
      this.showDetail = false;
      this.$emit('update:visible', false);
    },
    selectTheme(theme) {
      if (this.themeId && this.themeId != 0) {
        this.$confirm('确定要使用该主题数据吗？这将覆盖当前页面配置', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            const type = this.currentFilter.replace('_image', '');
            useThemeData(this.themeId, {
              theme_id: theme.id,
              type: type,
            })
              .then(() => {
                this.$message.success('设置成功');
                this.$emit('success');
                this.handleClose();
                this.showDetail = false;
              })
              .catch((err) => {
                this.$message.error((err && (err.message || err.msg)) || '设置失败');
              });
          })
          .catch(() => {});
      } else {
        this.$emit('select', theme);
        this.handleClose();
        this.showDetail = false;
      }
    },
    viewThemeDetail(theme) {
      this.currentTheme = theme;
      this.showDetail = true;
      this.$nextTick(() => {
        this.creatQrCode();
      });
    },
    creatQrCode() {
      if (document.getElementById('qrcodeDetail')) {
        document.getElementById('qrcodeDetail').innerHTML = '';
      }
      let url = `${this.BaseURL}pages/index/index?theme_id=${this.currentTheme.id}`;
      var qrcode = new QRCode(document.getElementById('qrcodeDetail'), {
        text: url,
        width: 120,
        height: 120,
        colorDark: '#000000',
        colorLight: '#ffffff',
        correctLevel: QRCode.CorrectLevel.H,
      });
    },
    createNewTheme() {
      this.$router.push({
        path: '/design/edit_theme',
        query: {
          id: 0,
          type: 'home',
        },
      });
    },
    searchTheme() {
      this.page = 1;
      this.getList();
    },
  },
};
</script>

<style lang="scss" scoped>
// 弹窗样式
::v-deep .theme-dialog {
  border-radius: 8px;
  overflow: hidden;

  .el-dialog__header {
    border-bottom: 1px solid #f0f0f0;
    padding: 0px !important;
  }

  .el-dialog__body {
    padding: 0 !important;
  }
  .el-dialog__body::-webkit-scrollbar {
    width: 0%;
  }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 15px 24px;
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
    }
  }
}
.el-icon-close {
  font-size: 16px;
  color: #909399;
  cursor: pointer;
}

.dialog-content {
  display: flex;
  flex-direction: column;
  height: calc(100% - 100px);
  padding: 1px 24px 1px 24px !important;

  .content-wrapper {
    flex: 1;
    display: flex;
    overflow: hidden;
    .main-content {
      flex: 1;
      min-height: 756px;
      padding: 20px 0;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background: #fff;
      .filters-header {
        display: flex;
        align-items: center;
        margin-bottom: 20px;
        .filter-left {
          margin-right: 10px;
        }
        .search-box {
          width: 250px;
        }
      }

      .theme-grid {
        display: flex;
        flex-wrap: wrap;
        gap: 30px;
        overflow-y: auto;
        flex: 1;

        .theme-item {
          width: 204px;
          height: 295px;
          background: #fff;
          border-radius: 8px;
          overflow: hidden;
          border: 1px solid #f0f0f0;
          display: flex;
          flex-direction: column;

          &.create-new {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            background: #fff;
            cursor: pointer;
            border: 1px solid #eee;
            background: #fafafa;
            height: 295px; // Fixed height to match theme items
            transition: all 0.3s;

            .iconic_brush {
              font-size: 40px;
              color: #ccc;
              padding-bottom: 15px;
            }

            .create-text {
              color: #333;
              font-size: 14px;
            }

            &:hover {
              border-color: var(--prev-color-primary);
              .iconic_brush,
              .create-text {
                color: var(--prev-color-primary);
              }
            }
          }

          .theme-cover {
            position: relative;
            height: 215px;
            overflow: hidden;
            background: #f0f2f5;
            transition: height 0.3s;

            .blur-bg {
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              bottom: 0;
              background-size: cover;
              background-position: top;
              filter: blur(20px);
              z-index: 1;
            }

            .phone-preview {
              position: relative;
              z-index: 2;
              height: 95%;
              margin: 25px 24px 0 24px;
              background: #fff;
              border-radius: 10px 10px 0 0;
              overflow: hidden;
              border: 2px solid #fff;

              img {
                width: 100%;
              }
              .no-poster {
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                height: 100%;
                font-size: 12px;
                color: #999;
              }
            }
          }

          .theme-info {
            background: #fff;
            flex: 1;
            display: flex;
            flex-direction: column;
            position: relative;
            overflow: hidden;

            .theme-title {
              padding: 12px 0px 0 12px;
              font-size: 14px;
              color: #333;
              margin-bottom: 12px;
              font-weight: 500;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
              display: flex;
              align-items: center;
              justify-content: space-between;
              .tag {
                font-weight: 400;
                color: rgba(2, 86, 255, 1);
                font-size: 12px;
                background: rgba(2, 86, 255, 0.06);
                padding: 4px 6px 4px 8px;
                border-radius: 10px 0 0 10px;
              }
            }

            .theme-time {
              padding: 0 12px 0 12px;

              font-size: 12px;
              color: #999;
              margin-bottom: 10px;
            }

            .theme-hover-actions {
              display: flex;
              justify-content: space-between;
              align-items: center;
              opacity: 0;
              transform: translateY(20px);
              transition: all 0.3s;
              height: 0;
              padding: 0 12px;
              overflow: hidden;

              .button {
                width: 84px;
                height: 28px;
                line-height: 28px;
                text-align: center;
                border-radius: 4px;
                font-size: 12px;
                cursor: pointer;
                transition: all 0.3s;
                cursor: pointer;
              }
              .primary {
                background: var(--prev-color-primary);
                color: #fff;
                &:hover {
                  background: var(--prev-color-primary-light-1);
                }
              }
              .default {
                background: #f5f5f5;
                color: #606266;
                &:hover {
                  background: #e4e7ed;
                }
              }
            }
          }

          &:hover {
            .theme-cover {
              height: 170px;
            }

            .theme-info {
              .theme-hover-actions {
                opacity: 1;
                transform: translateY(0);
                height: 32px;
                margin-top: 10px;
              }
            }
          }
        }
      }
    }
  }
}
::v-deep .el-dialog__body {
  max-height: calc(100vh - 100px) !important;
}
.detail-content {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  background: #f5f7fa;

  .detail-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15px 20px 16px;
    background: #fff;
    border-bottom: 1px solid #eee;

    .back-btn {
      cursor: pointer;
      color: #666;
      font-size: 14px;
      margin-right: 10px;
      display: flex;
      align-items: center;

      &:hover {
        color: var(--prev-color-primary-light-1);
      }

      i {
        margin-right: 4px;
      }
    }

    .detail-title {
      font-size: 14px;
      font-weight: 500;
      color: #333;
      border-left: 1px solid #eee;
      padding-left: 10px;
    }
  }

  .detail-body {
    height: calc(100vh - 100px);
    overflow-y: auto;
    background: #fff;
    padding: 0 20px 0 24px !important;

    .detail-top {
      display: flex;
      background: #fff;
      padding: 24px 0 20px 0;
      margin-bottom: 20px;
      border-bottom: 1px solid #eee;

      .detail-preview-main {
        width: 180px;
        height: 210px;
        border-radius: 8px;
        overflow: hidden;
        margin-right: 24px;
        border: 1px solid #eeeeee;
        img {
          width: 100%;
          object-fit: cover;
        }
      }

      .detail-info-box {
        flex: 1;
        margin-right: 24px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        .info-title {
          font-size: 16px;
          font-weight: 500;
          color: #333;
          margin-bottom: 20px;
        }

        .info-desc {
          font-size: 14px;
          color: #666;
          line-height: 1.6;
          margin-bottom: 30px;
          color: #888;
        }
      }
    }

    .detail-images-section {
      .section-title {
        font-size: 16px;
        font-weight: 500;
        color: #333;
        margin-bottom: 15px;
      }

      .images-list {
        display: flex;
        overflow-x: auto;
        padding-bottom: 10px;

        .image-item {
          margin-right: 29px;
          flex-shrink: 0;

          .phone-mockup {
            width: 214px;
            height: 380px;
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
            position: relative;
            border: 1px solid #eeeeee;
            .preview-image {
              width: 100%;
              object-fit: cover;
            }
            .no-poster {
              display: flex;
              flex-direction: column;
              justify-content: center;
              align-items: center;
              padding: 10px;
              margin-top: 70px;
              color: rgba(153, 153, 153, 1);
              font-size: 13px;
            }
          }
        }
      }
      .detail-qrcode {
        width: max-content;
        height: max-content;
        text-align: center;
        background: #f9f9f9;
        border-radius: 8px 8px 8px 8px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 16px;

        .qrcode-box {
          width: 120px;
          height: 120px;
          margin-bottom: 10px;

          img {
            width: 100%;
            height: 100%;
          }
        }

        .qrcode-text {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}
</style>
