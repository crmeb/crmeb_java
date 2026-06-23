<template>
  <div class="attr-list">
    <!-- Multi-select Position/Alignment Panel -->
    <ConfigMultiSelect
      v-if="activeComponentIds.length > 1"
      :groupX="groupX"
      :groupY="groupY"
      :canvasWidth="canvasWidth"
      :canvasHeight="canvasHeight"
      @align="alignComponents"
      @update:groupX="(val) => (groupX = val)"
      @update:groupY="(val) => (groupY = val)"
    />

    <template v-else-if="curComponent">
      <div class="custom-tabs" v-if="curComponent.component !== 'Line'">
        <div class="tab-item" :class="{ active: activeName === 'attr' }" @click="activeName = 'attr'">内容设置</div>
        <div class="tab-item" :class="{ active: activeName === 'style' }" @click="activeName = 'style'">样式设置</div>
      </div>

      <div class="tab-content" v-show="activeName === 'attr' && curComponent.component !== 'Line'">
        <el-form size="small" label-width="70px" label-position="left">
          <template v-if="curComponent.component === 'Text'">
            <ConfigTextContent
              :curComponent="curComponent"
              :type="type"
              :currentFieldList="currentFieldList"
              @change="onChange"
              @get-link="getLink"
            />
          </template>

          <template v-if="curComponent.component === 'Picture'">
            <ConfigPictureContent
              :curComponent="curComponent"
              :type="type"
              :currentFieldList="currentFieldList"
              @change="onChange"
              @get-link="getLink"
            />
          </template>

          <template v-if="curComponent.component === 'Icon'">
            <ConfigIconContent
              :curComponent="curComponent"
              :type="type"
              :currentFieldList="currentFieldList"
              @change="onChange"
              @get-link="getLink"
            />
          </template>

          <template v-if="curComponent.component === 'Panel'">
            <ConfigPanelContent
              :curComponent="curComponent"
              :type="type"
              :currentFieldList="currentFieldList"
              @change="onChange"
              @get-link="getLink"
            />
          </template>
        </el-form>
      </div>

      <div class="tab-content" v-show="activeName === 'style' && curComponent.component !== 'Line'">
        <ConfigPosition
          :curComponent="curComponent"
          :canvasWidth="canvasWidth"
          :canvasHeight="canvasHeight"
          @change="onChange"
        />
        <div v-if="curComponent.component === 'Picture'" class="section-title">图片设置</div>
        <div v-if="curComponent.component === 'Icon'" class="section-title">图标设置</div>
        <el-form size="small" label-width="70px" label-position="left">
          <template v-if="curComponent.component === 'Picture'">
            <ConfigRadius :curComponent="curComponent" labelPrefix="图片" @change="onChange" />
            <ConfigSize
              :curComponent="curComponent"
              :canvasWidth="canvasWidth"
              :canvasHeight="canvasHeight"
              labelPrefix="图片"
              @change="onChange"
            />
            <ConfigShadow :curComponent="curComponent" @change="onChange" />
            <ConfigBorder :curComponent="curComponent" @change="onChange" />
          </template>

          <template v-if="curComponent.component === 'Text'">
            <ConfigText :curComponent="curComponent" @change="onChange" />
            <div class="section-title">容器设置</div>
            <ConfigSize
              :curComponent="curComponent"
              :canvasWidth="canvasWidth"
              :canvasHeight="canvasHeight"
              @change="onChange"
            />
            <ConfigBackground :curComponent="curComponent" @change="onChange" />
            <div class="section-title">圆角样式</div>
            <ConfigRadius :curComponent="curComponent" @change="onChange" />
            <ConfigPadding :curComponent="curComponent" @change="onChange" />
            <ConfigBorder :curComponent="curComponent" @change="onChange" />
          </template>

          <template v-if="curComponent.component === 'Icon'">
            <ConfigIconStyle :curComponent="curComponent" @change="onChange" />

            <div class="section-title">容器设置</div>
            <ConfigSize
              :curComponent="curComponent"
              :canvasWidth="canvasWidth"
              :canvasHeight="canvasHeight"
              @change="onChange"
            />
            <ConfigBackground :curComponent="curComponent" @change="onChange" />
            <div class="section-title">圆角样式</div>
            <ConfigRadius :curComponent="curComponent" @change="onChange" />
            <ConfigPadding :curComponent="curComponent" @change="onChange" />
            <ConfigBorder :curComponent="curComponent" @change="onChange" />
          </template>

          <template v-if="curComponent.component === 'Panel'">
            <ConfigSize
              :curComponent="curComponent"
              :canvasWidth="canvasWidth"
              :canvasHeight="canvasHeight"
              @change="onChange"
            />
            <ConfigBackground :curComponent="curComponent" @change="onChange" />
            <ConfigRadius :curComponent="curComponent" @change="onChange" />
            <ConfigBorder :curComponent="curComponent" @change="onChange" />
            <ConfigShadow :curComponent="curComponent" @change="onChange" />
          </template>
        </el-form>
      </div>

      <!-- Special Line Component Settings -->
      <div class="line-settings" v-if="curComponent.component === 'Line'">
        <ConfigPosition
          :curComponent="curComponent"
          :canvasWidth="canvasWidth"
          :canvasHeight="canvasHeight"
          @change="onChange"
        />
        <ConfigLine :curComponent="curComponent" :canvasWidth="canvasWidth" @change="onChange" />
      </div>
    </template>

    <linkaddress ref="linkaddres" @linkUrl="linkUrl"></linkaddress>
  </div>
</template>

<script>
import linkaddress from '@/components/linkaddress';
import ConfigPosition from './components/ConfigPosition';
import ConfigSize from './components/ConfigSize';
import ConfigBorder from './components/ConfigBorder';
import ConfigRadius from './components/ConfigRadius';
import ConfigBackground from './components/ConfigBackground';
import ConfigPadding from './components/ConfigPadding';
import ConfigShadow from './components/ConfigShadow';
import ConfigText from './components/ConfigText';
import ConfigMultiSelect from './components/ConfigMultiSelect';
import ConfigLine from './components/ConfigLine';
import ConfigIconStyle from './components/ConfigIconStyle';
import ConfigTextContent from './components/ConfigTextContent';
import ConfigPictureContent from './components/ConfigPictureContent';
import ConfigIconContent from './components/ConfigIconContent';
import ConfigPanelContent from './components/ConfigPanelContent';

export default {
  components: {
    linkaddress,
    ConfigPosition,
    ConfigSize,
    ConfigBorder,
    ConfigRadius,
    ConfigBackground,
    ConfigPadding,
    ConfigShadow,
    ConfigText,
    ConfigMultiSelect,
    ConfigLine,
    ConfigIconStyle,
    ConfigTextContent,
    ConfigPictureContent,
    ConfigIconContent,
    ConfigPanelContent,
  },
  props: {
    curComponent: {
      type: Object,
      default: () => ({}),
    },
    activeComponentIds: {
      type: Array,
      default: () => [],
    },
    componentData: {
      type: Array,
      default: () => [],
    },
    canvasWidth: {
      type: Number,
      default: 375,
    },
    canvasHeight: {
      type: Number,
      default: 380,
    },
    type: {
      type: String,
      default: 'user',
    },
    fieldList: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      activeName: 'attr',
    };
  },
  watch: {
    'curComponent.id': {
      handler(val) {
        if (val && this.curComponent && this.curComponent.style) {
          // Ensure padding properties are reactive (for ConfigPadding)
          if (this.curComponent.propValue.paddingTop === undefined)
            this.$set(this.curComponent.propValue, 'paddingTop', 0);
          if (this.curComponent.propValue.paddingRight === undefined)
            this.$set(this.curComponent.propValue, 'paddingRight', 0);
          if (this.curComponent.propValue.paddingBottom === undefined)
            this.$set(this.curComponent.propValue, 'paddingBottom', 0);
          if (this.curComponent.propValue.paddingLeft === undefined)
            this.$set(this.curComponent.propValue, 'paddingLeft', 0);

          // Ensure rotate is reactive in style (for drag rotate)
          if (this.curComponent.style.rotate === undefined) {
            this.$set(this.curComponent.style, 'rotate', 0);
          }

          if (this.curComponent.component === 'Text' && this.curComponent.propValue.linkType === undefined) {
            this.$set(this.curComponent.propValue, 'linkType', 'url');
          }
          // Ensure typeLabel is synced if missing
          if (this.curComponent.propValue.fieldType && !this.curComponent.propValue.typeLabel) {
            const field = this.currentFieldList.find((item) => item.value === this.curComponent.propValue.fieldType);
            if (field) {
              this.$set(this.curComponent.propValue, 'typeLabel', field.label);
            }
          }
        }
      },
      immediate: true,
    },
  },
  computed: {
    selectedComponents() {
      return this.componentData.filter((item) => this.activeComponentIds.includes(item.id));
    },
    groupRect() {
      if (this.selectedComponents.length === 0) return { left: 0, top: 0, width: 0, height: 0 };
      const lefts = this.selectedComponents.map((c) => c.style.left);
      const tops = this.selectedComponents.map((c) => c.style.top);
      const rights = this.selectedComponents.map((c) => c.style.left + c.style.width);
      const bottoms = this.selectedComponents.map((c) => c.style.top + c.style.height);

      const minLeft = Math.min(...lefts);
      const minTop = Math.min(...tops);
      const maxRight = Math.max(...rights);
      const maxBottom = Math.max(...bottoms);

      return {
        left: minLeft,
        top: minTop,
        width: maxRight - minLeft,
        height: maxBottom - minTop,
      };
    },
    groupX: {
      get() {
        return this.groupRect.left;
      },
      set(val) {
        const delta = val - this.groupRect.left;
        this.selectedComponents.forEach((c) => {
          c.style.left += delta;
        });
        this.$emit('update-group');
      },
    },
    groupY: {
      get() {
        return this.groupRect.top;
      },
      set(val) {
        const delta = val - this.groupRect.top;
        this.selectedComponents.forEach((c) => {
          c.style.top += delta;
        });
        this.$emit('update-group');
      },
    },
    currentFieldList() {
      const map = {
        user: 'user',
        article: 'article',
        coupon: 'coupon',
        goods: 'product',
      };
      const key = map[this.type] || 'user';
      const list = this.fieldList[key] || [];
      if (Array.isArray(list)) {
        return list.map((item) => {
          if (typeof item === 'object') {
            return {
              label: item.label || item.title || item.name,
              value: item.value || item.field || item.key || item.name,
            };
          }
          return { label: item, value: item };
        });
      }
      if (typeof list === 'object') {
        return Object.keys(list).map((k) => ({
          label: list[k],
          value: k,
        }));
      }
      return [];
    },
  },
  methods: {
    alignComponents(type) {
      if (this.selectedComponents.length < 2) return;

      const rect = this.groupRect;
      this.selectedComponents.forEach((c) => {
        switch (type) {
          case 'left':
            c.style.left = rect.left;
            break;
          case 'center':
            c.style.left = rect.left + (rect.width - c.style.width) / 2;
            break;
          case 'right':
            c.style.left = rect.left + rect.width - c.style.width;
            break;
          case 'top':
            c.style.top = rect.top;
            break;
          case 'middle':
            c.style.top = rect.top + (rect.height - c.style.height) / 2;
            break;
          case 'bottom':
            c.style.top = rect.top + rect.height - c.style.height;
            break;
          case 'all-center':
            c.style.left = rect.left + (rect.width - c.style.width) / 2;
            c.style.top = rect.top + (rect.height - c.style.height) / 2;
            break;
        }
      });
      this.$emit('update-group');
    },
    onChange() {
      this.$emit('change');
    },
    getLink() {
      const typeMap = {
        goods: {
          id: 9,
          pid: 2,
          type: 'product',
        },
        article: {
          id: 14,
          pid: 3,
          type: 'news',
        },
      };
      const linkType = this.curComponent && this.curComponent.propValue && this.curComponent.propValue.linkType;
      const target = linkType === 'detail' ? typeMap[this.type] : null;
      if (target) this.$refs.linkaddres.handleCheckChange(target);
      this.$refs.linkaddres.modals = true;
    },
    linkUrl(e) {
      this.$set(this.curComponent.propValue, 'link', e);
      this.onChange();
    },
  },
};
</script>

<style scoped lang="scss">
.flex {
  display: flex;
  align-items: center;
}
.justify-between {
  justify-content: space-between;
}

.attr-list {
  padding: 15px;
  .tip {
    font-size: 12px;
    color: #f56c6c;
    line-height: 1.5;
  }
  .section-title {
    font-size: 14px;
    color: #333;
    margin-bottom: 18px;
    margin-top: 10px;
    width: calc(100% + 30px);
    margin-left: -15px;
    padding-left: 15px;
    padding-top: 20px;
    border-top: 6px solid #f0f2f5;
  }
  .custom-tabs {
    display: flex;
    border-bottom: 1px solid #e4e7ed;
    margin-bottom: 20px;
    .tab-item {
      flex: 1;
      text-align: center;
      padding: 10px 0;
      cursor: pointer;
      font-size: 14px;
      color: #303133;
      position: relative;
      &.active {
        color: var(--prev-color-primary);
        font-weight: 500;
        &:after {
          content: '';
          position: absolute;
          left: 50%;
          bottom: 0;
          transform: translateX(-50%);
          width: 40px;
          height: 2px;
          background: var(--prev-color-primary);
        }
      }
    }
  }
  ::v-deep .el-radio,
  ::v-deep .el-checkbox {
    margin-bottom: 0;
  }
  ::v-deep .el-form-item__content {
    margin-left: 80px !important;
    line-height: 36px !important;
  }
  ::v-deep .el-form-item--small .el-form-item__label {
    line-height: 38px !important;
    word-break: break-word;
  }
}
::v-deep .reset-color {
  font-size: 13px;
  white-space: nowrap;
  color: var(--prev-color-primary);
  cursor: pointer;
  margin-left: 15px;
  &:hover {
    color: var(--prev-color-primary-light-1);
  }
}
</style>
