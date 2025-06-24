<template>
  <!--公告设置添加内容-->
  <div class="c_product borderPadding" v-if="configData">
    <div class="title">{{ configData.title }}</div>
    <div class="list-box mt20">
      <draggable class="dragArea list-group" :list="configData.list" group="peoples" handle=".move-icon">
        <div
          class="item"
          v-for="(item, index) in configData.list"
          :key="index"
          @click="activeBtn(index)"
          v-model="configData.tabCur"
        >
          <div class="move-icon">
            <span class="iconfont icontuozhuaitubiao"></span>
          </div>
          <div class="content ml20">
            <div class="con-item" v-for="(list, key) in item.chiild" :key="key">
              <span>{{ list.title }}</span>
              <div style="width: 100%" @click="getLink(index, key, item)">
                <el-input
                  size="small"
                  :readonly="key && !item.link ? true : false"
                  v-model="list.val"
                  :placeholder="list.pla"
                  :maxlength="list.max"
                >
                  <el-button v-if="key && !item.link" slot="append" icon="el-icon-arrow-right"></el-button>
                </el-input>
              </div>
            </div>
            <div class="con-item" v-if="item.link">
              <span>{{ item.link.title }}</span>
              <el-select v-model="item.link.activeVal" style="" @change="sliderChange(index)">
                <el-option v-for="(item, j) in item.link.optiops" :value="item.value" :key="j"
                  >{{ item.label }}
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="delete" @click.stop="bindDelete(index)">
            <i class="el-icon-error" style="font-size: 26px" />
          </div>
        </div>
      </draggable>
    </div>
    <div v-if="configData.list">
      <div class="add-btn" @click="addHotTxt" v-if="configData.list.length < configData.max">
        <el-button icon="el-icon-plus" plain style="width: 100%; height: 40px; font-size: 12px">添加模块</el-button>
      </div>
    </div>
    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import vuedraggable from 'vuedraggable';
import linkaddress from '@/components/linkaddress';

export default {
  name: 'c_product',
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
  },
  components: {
    linkaddress,
    draggable: vuedraggable,
  },
  data() {
    return {
      defaults: {},
      configData: {},
      itemObj: {},
      activeIndex: 0,
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.defaults = this.configObj;
      this.configData = this.configObj[this.configNme];
    });
  },
  watch: {
    configObj: {
      handler(nVal, oVal) {
        this.defaults = nVal;
        this.configData = nVal[this.configNme];
      },
      deep: true,
    },
  },
  methods: {
    linkUrl(e) {
      this.configData.list[this.activeIndex].chiild[1].val = e;
    },
    getLink(index, key, item) {
      if (!key || item.link) {
        return;
      }
      this.activeIndex = index;
      this.$refs.linkaddres.dialogVisible = true;
    },
    addHotTxt() {
      if (this.configData.list.length == 0) {
        let storage = window.localStorage;
        this.itemObj = JSON.parse(storage.getItem('itemObj'));
        if (this.itemObj.link) {
          this.itemObj.link.activeVal = 0;
        }
        this.itemObj.chiild[0].val = '';
        this.itemObj.chiild[1].val = '';
        this.configData.list.push(this.itemObj);
      } else {
        let obj = JSON.parse(JSON.stringify(this.configData.list[this.configData.list.length - 1]));
        if (obj.chiild[0].empty) {
          obj.chiild[0].val = '';
          obj.chiild[1].val = '';
        }
        this.configData.list.push(obj);
      }
    },
    // 删除数组
    bindDelete(index) {
      if (this.configData.list.length == 1) {
        let itemObj = this.configData.list[0];
        this.itemObj = itemObj;
        let storage = window.localStorage;
        storage.setItem('itemObj', JSON.stringify(itemObj));
      }
      this.configData.list.splice(index, 1);
      this.configData.tabCur = 0;
      this.$emit('getConfig', { name: 'delete', indexs: 0 });
    },
    sliderChange(index) {
      this.configData.tabCur = index;
      this.$emit('getConfig', { name: 'product', indexs: index });
    },
    activeBtn(index) {
      this.configData.tabCur = index;
      this.$emit('getConfig', { name: 'product', indexs: index });
    },
  },
};
</script>

<style scoped lang="scss">
.delete {
  position: absolute;
  right: -7px;
  top: -12px;
}
::v-deep.el-input__inner {
  font-size: 13px;
}
.c_product {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding-bottom: 20px;
  margin-bottom: 20px;
  .list-box {
    .item {
      width: 100%;
      height: 140px;
      background: #f9f9f9;
      border-radius: 3px 3px 3px 3px;
      opacity: 1;
      position: relative;
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 16px 20px;
      margin-bottom: 14px;
      .move-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 80px;
        cursor: move;
      }

      .img-box {
        position: relative;
        width: 64px;
        height: 64px;
        margin-left: 20px;
        img {
          width: 100%;
          height: 100%;
        }
      }
    }
    .move-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: move;
    }
    .content {
      flex: 1;
      .con-item {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
        &:last-child {
          margin-bottom: 0;
        }
        span {
          width: 45px;
          font-size: 13px;
        }
      }
    }
  }
  .add-btn {
    margin-top: 18px;
  }
}
.title {
  font-size: 12px;
  color: #bbbbbb;
}

.iconfont-diy,
.iconfont {
  color: #dddddd;
  font-size: 24px;
}
</style>
