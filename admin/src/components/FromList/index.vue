<template>
  <!--  isShowAddBtn添加按钮  sShowEdit删除按钮 isShowStatus开启状态  isShowLinkUrl链接地址 isShowImageUrl图片地址-->
  <!--  tips提示语，list列表数据， title文本输入框标题-->
  <div class="hot_imgs">
    <div class="from-tips" v-if="configData.tips">
      {{ configData.tips }}
    </div>
    <div class="list-box mt20">
      <draggable class="dragArea list-group" :list="configData.list" group="peoples" handle=".move-icon">
        <div class="item" v-for="(item, index) in configData.list" :key="index">
          <div class="move-icon">
            <span class="iconfont icontuozhuaitubiao"></span>
          </div>
          <div v-if="configData.isShowImageUrl" class="img-box" @click="modalPicTap(item, index)">
            <img :src="item.imageUrl" alt="" v-if="item.imageUrl" />
            <div class="upload-box" v-else><i class="el-icon-camera-solid" style="font-size: 30px" /></div>
          </div>
          <div class="info">
            <div v-if="configData.title" class="info-item">
              <span class="text-14px">{{ configData.title }}</span>
              <div class="input-box">
                <el-input
                  size="small"
                  v-model="item.name"
                  :placeholder="configData.placeWords"
                  :maxlength="configData.maxLength"
                ></el-input>
              </div>
            </div>
            <div v-if="configData.isShowLinkUrl" class="info-item">
              <span class="text-14px">链接</span>
              <div v-if="configData.isShowLinkUrlChose" class="input-box" @click="getLink(index, item.linkUrl)">
                {{ configData.linkPlaceWords }}
                <el-input size="small" v-model="item.linkUrl" :placeholder="configData.linkPlaceWords || '请输入链接'">
                  <el-button slot="append" icon="el-icon-arrow-right"></el-button>
                </el-input>
              </div>
              <div v-else class="input-box">
                <el-input size="small" v-model="item.linkUrl" placeholder="请输入链接"></el-input>
              </div>
            </div>
            <div v-if="configData.isShowStatus" class="info-item">
              <span class="text-14px">状态</span>
              <div class="input-box">
                <el-switch
                  v-model="item.status"
                  :active-value="true"
                  :inactive-value="false"
                  active-text="显示"
                  inactive-text="隐藏"
                  @change="onchangeIsShow(item.status, index)"
                />
              </div>
            </div>
            <!-- 添加链接-->
            <div v-if="item.linkList && item.linkList.length">
              <div class="acea-row">
                <div
                  class="input-box mb15"
                  v-for="(items, indexs) in item.linkList"
                  :key="indexs"
                  style="position: relative"
                >
                  {{ item.maxLength }}
                  <el-input
                    style="width: 155px"
                    size="small"
                    v-model="items.name"
                    placeholder="链接名称，最多8个字"
                    :maxlength="configData.linkNameMaxLength"
                  ></el-input>
                  <el-input
                    size="small"
                    style="width: 479px"
                    v-model="items.linkUrl"
                    placeholder="请输入链接地址"
                  ></el-input>
                  <div class="delect-btn-link" @click.stop="handleLinkDelete(items, indexs, index)">
                    <i class="el-icon-error" style="font-size: 20px" />
                  </div>
                </div>
              </div>
              <div class="add-btn" v-if="item.linkList.length < configData.modelLinkMaxLength">
                <el-button class="button" icon="el-icon-plus" plain @click="handleAddLink(index)">添加链接</el-button>
              </div>
            </div>
            <div v-if="configData.isShowEdit" class="delect-btn" @click.stop="handleDelete(item, index)">
              <i class="el-icon-error" style="font-size: 26px" />
            </div>
          </div>
        </div>
      </draggable>
    </div>
    <template v-if="configData.isShowAddBtn">
      <div class="add-btn mt20" v-if="configData.list.length < configData.modelMaxLength || !configData.modelMaxLength">
        <el-button class="button" icon="el-icon-plus" plain @click="handleAddBox">添加板块</el-button>
      </div>
    </template>
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import vuedraggable from 'vuedraggable';
import linkaddress from '@/components/linkaddress';
export default {
  name: 'c_menu_list',
  props: {
    configObj: {
      type: Object,
    },
    configNme: {
      type: String,
    },
    index: {
      type: null,
    },
    isRub: {
      type: Boolean,
      default: false,
    },
  },
  components: {
    draggable: vuedraggable,
    linkaddress,
  },
  data() {
    return {
      defaults: {},
      configData: {},
      gridBtn: {
        xl: 4,
        lg: 8,
        md: 8,
        sm: 8,
        xs: 8,
      },
      gridPic: {
        xl: 6,
        lg: 8,
        md: 12,
        sm: 12,
        xs: 12,
      },
      activeIndex: 0, //索引
      indexLast: 0,
      lastObj: {},
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.configData = this.configObj;
    });
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.configData = nVal;
      },
      deep: true,
    },
  },
  methods: {
    //状态切换
    onchangeIsShow(e, index) {
      this.activeIndex = index;
      this.configData.list[this.activeIndex].status = e;
    },
    linkUrl(e) {
      this.configData.list[this.activeIndex].linkUrl = e;
    },
    getLink(index, item) {
      this.activeIndex = index;
      this.$refs.linkaddres.dialogVisible = true;
    },
    handleAddLink(index) {
      let data = {
        name: '',
        linkUrl: '',
        id: 0,
        sort: 0,
        modelMaxLength: 8,
      };
      let datas = Object.assign({}, data);
      this.configData.list[index].linkList.push(datas);
    },
    //删除链接
    handleLinkDelete(item, indexs, index) {
      this.configData.list[index].linkList.splice(indexs, 1);
    },
    //添加板块
    handleAddBox() {
      let data = JSON.parse(JSON.stringify(this.configData.defaultList));
      this.configData.list.push(data);
    },
    // 点击图文封面
    modalPicTap(item, index) {
      this.activeIndex = index;
      let _this = this;
      _this.$modalUpload(function (img) {
        if (!img) return;
        item.img = img[0].sattDir;
        _this.getPic(img[0].sattDir);
      }, '1','diy');
    },
    // 获取图片信息
    getPic(pc) {
      this.$nextTick(() => {
        this.configData.list[this.activeIndex].imageUrl = pc;
        this.$emit('getPicList', this.configData.list)
      });
    },
    onBlur() {
      let data = this.defaults.menuConfig;
      this.defaults.picStyle.picList[this.defaults.picStyle.tabVal].link = data.list[0].info[0].value;
    },
    // 删除整个模块
    handleDelete(item, index) {
      if (this.configData.list.length == 1) {
        this.lastObj = this.configData.list[0];
      }
      this.configData.list.splice(index, 1);
    },
  },
};
</script>

<style scoped lang="scss">
.delect-btn-link {
  position: absolute;
  right: -9px;
  top: -9px;
  cursor: pointer;
}

.hot_imgs {
  max-width: 800px;
  // width: 800px;
  margin-bottom: 20px;

  .list-box {
    border: 1px dashed #cccccc;
    padding: 20px;

    .item {
      width: 100%;
      background: #f9f9f9;
      border-radius: 3px 3px 3px 3px;
      opacity: 1;
      position: relative;
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 20px;
      margin-bottom: 14px;

      .move-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 64px;
        cursor: move;
      }

      .img-box {
        position: relative;
        width: 64px;
        height: 64px;
        margin-left: 20px;
        border-radius: 3px;
        cursor: pointer;

        img {
          width: 100%;
          height: 100%;
          border-radius: 3px;
        }
      }
    }

    .info {
      flex: 1;
      margin-left: 22px;

      .info-item {
        display: flex;
        align-items: center;
        margin-bottom: 10px;

        span {
          width: 60px;
          font-size: 12px;
          color: #999999;
        }

        .input-box {
          flex: 1;

          ::v-deep.ivu-input {
            font-size: 13px !important;
          }
        }
      }
    }

    .delect-btn {
      position: absolute;
      right: -7px;
      top: -12px;

      .iconfont-diy,
      .iconfont {
        font-size: 25px;
        color: #999;
      }
    }
  }

  .add-btn .button {
    width: 100%;
    height: 36px;
  }
}

.upload-box {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background: #fff;
  font-size: 12px;
  color: #cccccc;

  .iconfont {
    font-size: 16px;
  }
}

.iconfont-diy,
.iconfont {
  color: #dddddd;
  font-size: 28px;
}

.el-select {
  display: block;
}
.el-icon-error {
  cursor: pointer;
  transition-property: all;
  transition-duration: 0.2s; 
  &:hover {
    color: #303133;
  }
}
</style>
