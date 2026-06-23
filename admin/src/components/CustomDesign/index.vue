<template>
  <div class="custom-design">
    <!-- Toolbar -->
    <div class="header">
      <div class="left">
        <span class="close-btn" @click="$emit('close')"><i class="icon el-icon-arrow-left"></i>返回</span>
        <span class="title">设计组件</span>
      </div>
      <div class="center"></div>
      <div class="right">
        <el-button size="small" @click="save(false)">保存</el-button>
        <el-button type="primary" size="small" @click="save(true)">保存关闭</el-button>
      </div>
    </div>

    <div class="body">
      <!-- Left Panel -->
      <div class="left-panel">
        <ComponentList />

        <div class="height-setting">
          <div class="label">内容高度</div>
          <div class="control">
            <el-slider
              v-model="canvasHeight"
              :min="10"
              :max="1000"
              input-size="mini"
              style="flex: 1; margin-right: 10px"
            ></el-slider>
            <el-input-number
              v-model="canvasHeight"
              :min="10"
              :max="1000"
              size="mini"
              controls-position="right"
              style="width: 100px"
            ></el-input-number>
          </div>
        </div>

        <div class="layer-list">
          <div class="title">已选组件 ({{ componentData.length }})</div>
          <el-scrollbar style="height: calc(100% - 40px)">
            <draggable v-model="reversedComponentData" handle=".handle" animation="200">
              <div
                v-for="item in reversedComponentData"
                :key="item.id"
                class="layer-item"
                :class="{ active: curComponent && curComponent.id === item.id }"
                @click="setCurComponent(item, componentData.indexOf(item))"
              >
                <span class="handle iconfont iconxingzhuangjiehe"></span>
                <span class="name">{{ item.label || item.component }}</span>
                <div class="actions">
                  <span class="iconfont iconic_edit2" @click.stop="editName(item)"></span>
                  <span class="iconfont iconic_Eyes" v-if="!item.isHidden" @click.stop="toggleHide(item)"></span>
                  <span class="iconfont iconic_eye" v-else style="color: #ccc" @click.stop="toggleHide(item)"></span>
                  <span class="iconfont iconshanchu3" @click.stop="deleteComponent(componentData.indexOf(item))"></span>
                </div>
              </div>
            </draggable>
          </el-scrollbar>
        </div>
      </div>

      <!-- Center Canvas -->
      <div class="center-panel" @contextmenu="handleContextMenu" @mousedown="handleBackgroundClick">
        <ContextMenu
          v-if="menuVisible"
          :visible="menuVisible"
          :top="menuTop"
          :left="menuLeft"
          :curComponent="curComponent"
          @action="handleMenuAction"
          @close="closeMenu"
        />
        <div class="history-controls">
          <div class="operation-bar">
            <div class="operation-item" :class="{ disabled: snapshotIndex <= 0 }" @click="snapshotIndex > 0 && undo()">
              <span class="iconfont iconshangyibu1"></span>
            </div>
            <div
              class="operation-item"
              :class="{ disabled: snapshotIndex === snapshotData.length - 1 }"
              @click="snapshotIndex < snapshotData.length - 1 && redo()"
            >
              <span class="iconfont iconshangyibu"></span>
            </div>
            <div class="divider"></div>
            <div class="operation-item text" @click="historyVisible = !historyVisible">历史记录</div>
          </div>
        </div>
        <div class="history-list" v-show="historyVisible">
          <div class="history-header">
            <span>历史记录</span>
            <i class="el-icon-close close-icon" @click.stop="historyVisible = false"></i>
          </div>
          <div class="history-content">
            <div
              class="history-item"
              v-for="(item, index) in snapshotData"
              :key="index"
              :class="{ active: index === snapshotIndex }"
              @click="handleHistorySelect(index)"
            >
              <span class="type">{{ item.type || '操作' }}</span>
              <div class="right-info">
                <span class="time">{{ item.time }}</span>
                <span class="iconfont icon-check" v-if="index === snapshotIndex">
                  <i class="el-icon-check"></i>
                </span>
                <span v-else class="iconfont"></span>
              </div>
            </div>
          </div>
        </div>
        <div class="component-actions" v-if="curComponent" :style="actionBarStyle">
          <div class="action-item" title="删除" @click="deleteComponent(curComponentIndex)">
            <span class="iconfont iconshanchu3"></span>
          </div>
          <div class="action-item" title="复制" @click="copyComponent(curComponent)">
            <i class="el-icon-document-copy"></i>
          </div>
          <div
            class="action-item"
            :class="{ disabled: isTop }"
            title="上移一层"
            @click="moveComponent({ index: curComponentIndex, type: 'up' })"
          >
            <i class="el-icon-arrow-up"></i>
          </div>
          <div
            class="action-item"
            :class="{ disabled: isBottom }"
            title="下移一层"
            @click="moveComponent({ index: curComponentIndex, type: 'down' })"
          >
            <i class="el-icon-arrow-down"></i>
          </div>
          <div
            class="action-item"
            :class="{ disabled: isTop }"
            title="置顶"
            @click="moveComponent({ index: curComponentIndex, type: 'top' })"
          >
            <span class="iconfont iconzhiding"></span>
          </div>
          <div
            class="action-item"
            :class="{ disabled: isBottom }"
            title="置底"
            @click="moveComponent({ index: curComponentIndex, type: 'bottom' })"
          >
            <span class="iconfont iconzhidi"></span>
          </div>
        </div>
        <div class="canvas-container">
          <Editor
            ref="editor"
            :componentData="componentData"
            :canvasStyle="{ width: canvasWidth + 'px', height: canvasHeight + 'px' }"
            :curComponent="curComponent"
            :type="type"
            :fieldList="fieldList"
            @update:componentData="(val) => (componentData = val)"
            @select="setCurComponent"
            @record="recordSnapshot"
            @delete="deleteComponent"
            @copy="copyComponent"
            @move="moveComponent"
            @multi-select="handleMultiSelect"
          />
        </div>
      </div>

      <!-- Right Panel -->
      <div class="right-panel">
        <AttrList
          v-if="curComponent || activeComponentIds.length > 1"
          :curComponent="curComponent"
          :activeComponentIds="activeComponentIds"
          :componentData="componentData"
          :canvasWidth="canvasWidth"
          :canvasHeight="canvasHeight"
          :type="type"
          :fieldList="fieldList"
          @change="handleAttrChange"
          @update-group="handleUpdateGroup"
        />
        <div v-else class="empty-tip">
          <img src="@/assets/images/no-sel-cum.png" alt="" />
          <p>暂未选中任何组件～</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ComponentList from './ComponentList.vue';
import Editor from './Editor.vue';
import AttrList from './AttrList.vue';
import ContextMenu from './ContextMenu.vue';
import draggable from 'vuedraggable';
import { deepCopy } from './utils/utils';
import generateID from './utils/generateID';

import { getDiyField } from '@/api/diy';

export default {
  name: 'CustomDesign',
  components: { ComponentList, Editor, AttrList, draggable, ContextMenu },
  props: {
    initialData: {
      type: Object,
      default: () => ({}),
    },
    columnNum: {
      type: Number,
      default: 1,
    },
    type: {
      type: String,
      default: 'user',
    },
  },
  data() {
    return {
      componentData: [],
      canvasHeight: 375,
      curComponent: null,
      curComponentIndex: -1,
      snapshotData: [],
      snapshotIndex: -1,
      fieldList: {},
      historyVisible: false,
      activeComponentIds: [],
      menuVisible: false,
      menuTop: 0,
      menuLeft: 0,
    };
  },
  computed: {
    canvasWidth() {
      // Default 375, divided by columns if > 1
      const base = 375;
      return this.columnNum > 1 ? base / this.columnNum : base;
    },
    actionBarStyle() {
      return {
        left: `calc(50% + ${this.canvasWidth / 2}px + 10px)`,
        top: '50%',
        transform: 'translateY(-50%)',
      };
    },
    isTop() {
      return this.curComponentIndex === this.componentData.length - 1;
    },
    isBottom() {
      return this.curComponentIndex === 0;
    },
    reversedComponentData: {
      get() {
        return this.componentData.slice().reverse();
      },
      set(val) {
        this.componentData = val.slice().reverse();
        this.recordSnapshot('调整层级');
      },
    },
  },
  created() {
    if (this.initialData) {
      if (this.initialData.list) {
        this.componentData = deepCopy(this.initialData.list);
      }
      if (this.initialData.canvasHeight) {
        this.canvasHeight = this.initialData.canvasHeight;
      }
    }
    this.recordSnapshot();
    this.getFieldList();
  },
  mounted() {
    window.addEventListener('keydown', this.handleKeydown);
    window.addEventListener('click', this.closeMenu);
  },
  beforeDestroy() {
    window.removeEventListener('keydown', this.handleKeydown);
    window.removeEventListener('click', this.closeMenu);
  },
  methods: {
    handleBackgroundClick(e) {
      // Check if the click target is center-panel or canvas-container
      // to avoid triggering when clicking on controls
      if (e.target.classList.contains('center-panel') || e.target.classList.contains('canvas-container')) {
        this.curComponent = null;
        this.curComponentIndex = -1;
        this.activeComponentIds = [];
      }
    },
    getFieldList() {
      getDiyField().then((res) => {
        this.fieldList = res.data || {};
      });
    },
    setCurComponent(component, index) {
      this.curComponent = component;
      this.curComponentIndex = index;
    },
    deleteComponent(index) {
      this.componentData.splice(index, 1);
      this.curComponent = null;
      this.curComponentIndex = -1;
      this.recordSnapshot('删除组件');
    },
    toggleHide(item) {
      this.$set(item, 'isHidden', !item.isHidden);
    },
    editName(item) {
      this.$prompt('请输入组件名称', '修改名称', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: item.label,
      })
        .then(({ value }) => {
          this.$set(item, 'label', value);
        })
        .catch(() => {});
    },
    formatTime(date) {
      const h = date.getHours().toString().padStart(2, '0');
      const m = date.getMinutes().toString().padStart(2, '0');
      const s = date.getSeconds().toString().padStart(2, '0');
      return `${h}:${m}:${s}`;
    },
    recordSnapshot(type = '操作') {
      // 简单的撤销重做实现
      this.snapshotIndex++;
      this.snapshotData[this.snapshotIndex] = {
        componentData: deepCopy(this.componentData),
        canvasHeight: this.canvasHeight,
        type,
        time: this.formatTime(new Date()),
      };
      // 删除后续的历史记录
      if (this.snapshotIndex < this.snapshotData.length - 1) {
        this.snapshotData = this.snapshotData.slice(0, this.snapshotIndex + 1);
      }

      // Limit to 15 records
      const limit = 15;
      if (this.snapshotData.length > limit) {
        const diff = this.snapshotData.length - limit;
        this.snapshotData.splice(0, diff);
        this.snapshotIndex -= diff;
      }
    },
    undo() {
      if (this.snapshotIndex > 0) {
        this.snapshotIndex--;
        const data = deepCopy(this.snapshotData[this.snapshotIndex]);
        this.componentData = data.componentData;
        this.canvasHeight = data.canvasHeight;
        this.curComponent = null;
      }
    },
    redo() {
      if (this.snapshotIndex < this.snapshotData.length - 1) {
        this.snapshotIndex++;
        const data = deepCopy(this.snapshotData[this.snapshotIndex]);
        this.componentData = data.componentData;
        this.canvasHeight = data.canvasHeight;
        this.curComponent = null;
      }
    },
    copyComponent(item) {
      const component = deepCopy(item);
      component.id = generateID();
      component.style.top += 20;
      component.style.left += 20;
      this.componentData.push(component);
      this.recordSnapshot('复制组件');
    },
    moveComponent({ index, type }) {
      const component = this.componentData[index];
      if (type === 'up') {
        if (index < this.componentData.length - 1) {
          this.componentData.splice(index, 1);
          this.componentData.splice(index + 1, 0, component);
          this.curComponentIndex = index + 1;
          this.recordSnapshot('上移一层');
        } else {
          this.$message.warning('已经到顶了');
        }
      } else if (type === 'down') {
        if (index > 0) {
          this.componentData.splice(index, 1);
          this.componentData.splice(index - 1, 0, component);
          this.curComponentIndex = index - 1;
          this.recordSnapshot('下移一层');
        } else {
          this.$message.warning('已经到底了');
        }
      } else if (type === 'top') {
        if (index < this.componentData.length - 1) {
          this.componentData.splice(index, 1);
          this.componentData.push(component);
          this.curComponentIndex = this.componentData.length - 1;
          this.recordSnapshot('置顶');
        } else {
          this.$message.warning('已经到顶了');
        }
      } else if (type === 'bottom') {
        if (index > 0) {
          this.componentData.splice(index, 1);
          this.componentData.unshift(component);
          this.curComponentIndex = 0;
          this.recordSnapshot('置底');
        } else {
          this.$message.warning('已经到底了');
        }
      }
    },
    handleHistorySelect(index) {
      this.snapshotIndex = index;
      const data = deepCopy(this.snapshotData[this.snapshotIndex]);
      this.componentData = data.componentData;
      this.canvasHeight = data.canvasHeight;
      this.curComponent = null;
      this.historyVisible = false;
    },
    handleAttrChange() {
      this.recordSnapshot('修改属性');
    },
    handleUpdateGroup() {
      this.recordSnapshot('调整对齐/位置');
    },
    save(close = false) {
      const data = {
        list: this.componentData,
        canvasHeight: this.canvasHeight,
      };
      this.$emit('save', data);
      if (close) {
        this.$emit('close');
      } else {
        this.$message.success('保存成功');
      }
    },
    handleMultiSelect(ids) {
      this.activeComponentIds = ids;
    },
    handleKeydown(e) {
      // Avoid triggering when editing text
      if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return;

      if (e.ctrlKey || e.metaKey) {
        if (e.shiftKey && (e.key === 'z' || e.key === 'Z')) {
          e.preventDefault();
          this.redo();
        } else if (e.key === 'z' || e.key === 'Z') {
          e.preventDefault();
          this.undo();
        }
      } else if (e.key === 'Delete' || e.key === 'Backspace') {
        e.preventDefault();
        this.handleDeleteKey();
      }
    },
    handleDeleteKey() {
      if (this.activeComponentIds && this.activeComponentIds.length > 0) {
        this.componentData = this.componentData.filter((item) => !this.activeComponentIds.includes(item.id));
        this.curComponent = null;
        this.curComponentIndex = -1;
        this.activeComponentIds = [];
        this.recordSnapshot('删除组件');
      } else if (this.curComponent) {
        this.deleteComponent(this.curComponentIndex);
      }
    },
    closeMenu() {
      this.menuVisible = false;
    },
    handleContextMenu(e) {
      e.preventDefault();
      e.stopPropagation();

      const panel = this.$el.querySelector('.center-panel');
      const rect = panel.getBoundingClientRect();

      this.menuLeft = e.clientX - rect.left;
      this.menuTop = e.clientY - rect.top;
      this.menuVisible = true;
    },
    handleMenuAction(action) {
      if (!this.curComponent) return;

      switch (action) {
        case 'copy':
          this.copyComponent(this.curComponent);
          break;
        case 'delete':
          this.deleteComponent(this.curComponentIndex);
          break;
        case 'top':
          this.moveComponent({ index: this.curComponentIndex, type: 'top' });
          break;
        case 'bottom':
          this.moveComponent({ index: this.curComponentIndex, type: 'bottom' });
          break;
        case 'up':
          this.moveComponent({ index: this.curComponentIndex, type: 'up' });
          break;
        case 'down':
          this.moveComponent({ index: this.curComponentIndex, type: 'down' });
          break;
        case 'lock':
          this.$set(this.curComponent, 'isLock', true);
          this.recordSnapshot('锁定组件');
          break;
        case 'unlock':
          this.$set(this.curComponent, 'isLock', false);
          this.recordSnapshot('解锁组件');
          break;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.custom-design {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;

  .header {
    height: 60px;
    background: #fff;
    border-bottom: 1px solid #ddd;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px 0 10px;
    .left {
      display: flex;
      align-items: center;
      .title {
        font-size: 16px;
        font-weight: 500;
        margin-left: 10px;
      }
    }
    .close-btn {
      display: flex;
      align-items: center;
      font-size: 14px;
      color: #333;
      cursor: pointer;
      position: relative;
      padding-right: 10px;
      border-right: 1px solid #eee;
    }
  }

  .body {
    flex: 1;
    display: flex;
    overflow: hidden;

    .left-panel {
      width: 300px;
      background: #fff;
      display: flex;
      flex-direction: column;

      .height-setting {
        padding: 15px;
        border-top: 6px solid #f0f2f5;
        border-bottom: 6px solid #f0f2f5;
        .label {
          margin-bottom: 10px;
          font-size: 14px;
          color: #333;
        }
        .control {
          display: flex;
          align-items: center;
        }
        .tip {
          font-size: 12px;
          color: #999;
          margin-top: 5px;
        }
      }

      .layer-list {
        flex: 1;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        .title {
          padding: 10px 15px;
          color: #333;
          font-size: 14px;
          background: #f9f9f9;
        }
        .layer-item {
          display: flex;
          align-items: center;
          padding: 10px 15px;
          cursor: pointer;
          &:hover {
            background: var(--prev-color-primary-light-9);
          }
          &.active {
            background: var(--prev-color-primary-light-9);
            border-left: 3px solid var(--prev-color-primary);
          }
          .handle {
            cursor: move;
            margin-right: 10px;
            color: rgba(192, 196, 204, 1);
            font-size: 14px;
            line-height: 18px;
          }
          .name {
            flex: 1;
            font-size: 13px;
          }
          .actions {
            .iconfont {
              margin-left: 5px;
              color: rgba(187, 187, 187, 1);
              &:hover {
                color: var(--prev-color-primary);
              }
            }
            .iconic_edit2 {
              margin-right: 12px;
              padding-right: 12px;
              border-right: 1px solid rgba(51, 51, 51, 0.05);
            }
          }
        }
      }
    }

    .center-panel {
      flex: 1;
      background: #f0f2f5;
      display: flex;
      justify-content: center;
      align-items: center;
      overflow: hidden;
      position: relative;
      padding-top: 70px;

      .canvas-container {
        width: 100%;
        height: 100%;
        overflow: auto;
        display: flex;
        justify-content: center;
        align-items: center;
      }

      .history-controls {
        position: absolute;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        z-index: 100;
        background: #fff;
        padding: 5px;
        border-radius: 4px;
        // box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

        .operation-bar {
          display: flex;
          align-items: center;
          height: 32px;

          .operation-item {
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            color: #333;
            margin: 0 10px;
            transition: all 0.3s;

            &.disabled {
              color: #ccc;
              cursor: not-allowed;
            }

            &:hover:not(.disabled) {
              color: var(--prev-color-primary);
            }

            .iconfont {
              font-size: 16px;
            }

            &.text {
              font-size: 14px;
              color: #606266;
              &:hover {
                color: var(--prev-color-primary);
              }
              &.active {
                color: var(--prev-color-primary);
              }
            }
          }

          .divider {
            width: 1px;
            height: 16px;
            background: #e6e6e6;
            margin: 0 5px;
          }
        }
      }

      .history-list {
        position: absolute;
        top: 20px;
        right: 0;
        width: 187px;
        background: #fff;
        border: 1px solid #e4e7ed;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        border-radius: 4px;
        z-index: 100;
        display: flex;
        flex-direction: column;

        .history-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 15px;
          border-bottom: 1px solid #f0f0f0;
          font-size: 12px;
          font-weight: 400;
          color: #333;

          .close-icon {
            cursor: pointer;
            color: #333;
          }
        }

        .history-content {
          max-height: 300px;
          overflow-y: auto;
          padding: 5px 0;
        }

        .history-item {
          padding: 8px 15px;
          cursor: pointer;
          font-size: 12px;
          color: rgba(51, 51, 51, 1);
          display: flex;
          justify-content: space-between;
          align-items: center;

          &:hover {
            background-color: #f5f7fa;
          }
          &.active {
            color: #606266;
            background-color: #f5f7fa;
            // font-weight: bold;
          }

          .type {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .right-info {
            display: flex;
            align-items: center;
            margin-left: 10px;

            .time {
              color: rgba(51, 51, 51, 1);
              font-size: 13px;
              margin-right: 8px;
            }

            .iconfont {
              color: var(--prev-color-primary);
              font-size: 12px;
              width: 12px;
            }
          }
        }
      }

      .component-actions {
        position: absolute;
        width: 32px;
        background: var(--prev-color-primary);
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        border-radius: 4px;
        display: flex;
        flex-direction: column;
        padding: 10px 0;
        z-index: 100;

        .action-item {
          width: 100%;
          height: 30px;
          display: flex;
          justify-content: center;
          align-items: center;
          cursor: pointer;
          color: #fff;
          transition: all 0.3s;
          position: relative;

          &.disabled {
            opacity: 0.3;
            cursor: not-allowed;
            pointer-events: none;
          }

          &:hover {
            background-color: rgba(255, 255, 255, 0.1);
          }
          i {
            font-size: 16px;
          }
        }
      }
    }

    .right-panel {
      width: 400px;
      background: #fff;
      overflow-y: auto;
      ::v-deep .el-form-item__label {
        color: #999;
      }
      .empty-tip {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: #999;
        img {
          width: 150px;
          margin-bottom: 20px;
        }
        p {
          font-size: 14px;
        }
      }
    }
  }
}
</style>
