<template>
  <div
    class="editor"
    id="editor"
    :style="canvasStyle"
    @drop="handleDrop"
    @dragover="handleDragOver"
    @mousedown="handleMouseDown"
  >
    <!-- Grid Background -->
    <div class="grid-bg"></div>

    <!-- Components -->
    <template v-for="(item, index) in componentData">
      <Shape
        v-if="!item.isHidden"
        :key="item.id"
        :defaultStyle="item.style"
        :style="getShapeStyle(item)"
        :active="activeComponentIds.includes(item.id)"
        :element="item"
        :index="index"
        @select="handleSelect"
        @resize="handleResize"
        @end="handleEnd"
        @delete="handleDelete(index)"
        @copy="handleCopy(item)"
        @move="(type) => handleMove(index, type)"
      >
        <div :style="{ width: '100%', height: '100%' }">
          <!-- Picture -->
          <img
            v-if="item.component === 'Picture'"
            :src="item.propValue.url || require('@/assets/images/pic-decode.png')"
            :style="getPictureStyle(item.propValue)"
          />

          <!-- Text -->
          <div v-if="item.component === 'Text'" :style="getTextBg(item.propValue)">
            <div :style="getTextStyle(item.propValue)">
              {{ getDisplayText(item) || '请填写或选择信息' }}
            </div>
          </div>

          <!-- Icon -->
          <div v-else-if="item.component === 'Icon'" :style="getIconStyle(item.propValue)">
            <span
              class="mb-iconfont"
              :class="item.propValue.class"
              :style="{ fontSize: item.propValue.size + 'px', color: item.propValue.color }"
            ></span>
          </div>

          <!-- Line -->
          <div
            v-else-if="item.component === 'Line'"
            style="
              display: flex;
              align-items: center;
              justify-content: center;
              width: 100%;
              height: 100%;
              padding: 5px 0;
              box-sizing: border-box;
            "
          >
            <div
              :style="{
                width: item.propValue.direction === 'vertical' ? '0px' : '100%',
                height: item.propValue.direction === 'vertical' ? '100%' : '0px',
                borderTop:
                  item.propValue.direction === 'vertical'
                    ? 'none'
                    : item.propValue.height + 'px ' + item.propValue.style + ' ' + item.propValue.color,
                borderLeft:
                  item.propValue.direction === 'vertical'
                    ? item.propValue.height + 'px ' + item.propValue.style + ' ' + item.propValue.color
                    : 'none',
              }"
            ></div>
          </div>

          <!-- Panel -->
          <div v-else-if="item.component === 'Panel'" :style="getPanelStyle(item.propValue)"></div>
        </div>
      </Shape>
    </template>

    <!-- Guidelines -->
    <div v-show="guideline.showV" class="guideline-v" :style="{ left: guideline.v + 'px' }"></div>
    <div v-show="guideline.showH" class="guideline-h" :style="{ top: guideline.h + 'px' }"></div>

    <!-- Area Selector -->
    <div
      v-show="isShowArea"
      class="area-selector"
      :style="{
        left: areaStart.x + 'px',
        top: areaStart.y + 'px',
        width: areaWidth + 'px',
        height: areaHeight + 'px',
      }"
    ></div>
  </div>
</template>

<script>
import { h } from 'vue';
import Shape from './Shape.vue';
import generateID from './utils/generateID';
import { deepCopy } from './utils/utils';

export default {
  components: { Shape },
  props: {
    componentData: { type: Array, default: () => [] },
    canvasStyle: { type: Object, default: () => ({}) },
    curComponent: { type: Object, default: null },
    type: { type: String, default: 'user' },
    fieldList: { type: Object, default: () => ({}) },
  },
  data() {
    return {
      guideline: { v: 0, h: 0, showV: false, showH: false },
      isShowArea: false,
      areaStart: { x: 0, y: 0 },
      areaWidth: 0,
      areaHeight: 0,
      activeComponentIds: [],
    };
  },
  watch: {
    activeComponentIds(val) {
      this.$emit('multi-select', val);
    },
    curComponent: {
      handler(val) {
        if (val) {
          if (!this.activeComponentIds.includes(val.id)) {
            this.activeComponentIds = [val.id];
          }
        } else {
          this.activeComponentIds = [];
        }
      },
      immediate: true,
    },
  },
  computed: {
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
    getPictureStyle(propValue) {
      const style = {
        width: '100%',
        height: '100%',
        objectFit: 'cover',
        pointerEvents: 'none',
        boxSizing: 'border-box',
        borderWidth: (propValue.showBorder ? propValue.borderWidth : 0) + 'px',
        borderColor: propValue.borderColor || 'transparent',
        borderStyle: propValue.borderStyle || 'solid',
      };

      // Border Radius
      if (propValue.isRadiusAll) {
        style.borderRadius = (propValue.borderRadius || 0) + 'px';
      } else {
        style.borderRadius = `${propValue.borderRadiusTopLeft || 0}px ${propValue.borderRadiusTopRight || 0}px ${
          propValue.borderRadiusBottomRight || 0
        }px ${propValue.borderRadiusBottomLeft || 0}px`;
      }

      // Box Shadow
      if (propValue.showShadow) {
        style.boxShadow = `${propValue.shadowX || 0}px ${propValue.shadowY || 0}px ${propValue.shadowBlur || 0}px ${
          propValue.shadowSpread || 0
        }px ${propValue.shadowColor || 'rgba(0,0,0,0.5)'}`;
      }
      return style;
    },
    getIconStyle(propValue) {
      const style = {
        display: 'flex',
        justifyContent: propValue.iconAlign || 'center',
        alignItems: 'center',
        width: '100%',
        height: '100%',
        boxSizing: 'border-box',
        borderWidth: (propValue.showBorder ? propValue.borderWidth : 0) + 'px',
        borderColor: propValue.borderColor || 'transparent',
        borderStyle: propValue.borderStyle || 'solid',
        padding: `${propValue.paddingTop || 0}px ${propValue.paddingRight || 0}px ${propValue.paddingBottom || 0}px ${
          propValue.paddingLeft || 0
        }px`,
      };

      // Background
      if (propValue.bgColor2) {
        const directionMap = {
          horizontal: '90deg',
          vertical: '180deg',
          'left-diagonal': '135deg',
          'right-diagonal': '45deg',
        };
        const deg = directionMap[propValue.bgDirection] || '180deg';
        style.backgroundImage = `linear-gradient(${deg}, ${propValue.backgroundColor || 'transparent'}, ${
          propValue.bgColor2
        })`;
      } else {
        style.backgroundColor = propValue.backgroundColor || 'transparent';
      }

      // Border Radius
      if (propValue.isRadiusAll) {
        style.borderRadius = (propValue.borderRadius || 0) + 'px';
      } else {
        style.borderRadius = `${propValue.borderRadiusTopLeft || 0}px ${propValue.borderRadiusTopRight || 0}px ${
          propValue.borderRadiusBottomRight || 0
        }px ${propValue.borderRadiusBottomLeft || 0}px`;
      }

      return style;
    },
    getTextStyle(propValue) {
      const style = {
        fontSize: propValue.fontSize + 'px',
        color: propValue.color,
        lineHeight: propValue.lineHeight,
        letterSpacing: (propValue.letterSpacing || 0) + 'px',
        fontWeight: propValue.fontWeight || 'normal',
        fontStyle: propValue.fontStyle || 'normal',
        textDecoration: propValue.textDecoration || 'none',
        textAlign: propValue.textAlign || 'left',

        boxSizing: 'border-box',
        wordBreak: 'break-all',
      };

      // Ellipsis
      if (propValue.ellipsis > 0) {
        style.display = '-webkit-box';
        style.WebkitBoxOrient = 'vertical';
        style.WebkitLineClamp = propValue.ellipsis;
        style.overflow = 'hidden';
        style.whiteSpace = 'normal';
      } else {
        style.whiteSpace = 'normal';
      }

      if (propValue.showTextShadow) {
        style.textShadow = `${propValue.shadowX || 0}px ${propValue.shadowY || 0}px ${propValue.shadowBlur || 0}px ${
          propValue.shadowColor || 'rgba(0,0,0,0.5)'
        }`;
      }

      return style;
    },
    getTextBg(propValue) {
      const style = {
        height: '100%',
        borderWidth: (propValue.showBorder ? propValue.borderWidth : 0) + 'px',
        borderColor: propValue.borderColor || 'transparent',
        borderStyle: propValue.borderStyle || 'solid',
        padding: `${propValue.paddingTop || 0}px ${propValue.paddingRight || 0}px ${propValue.paddingBottom || 0}px ${
          propValue.paddingLeft || 0
        }px`,
      };
      // Background
      if (propValue.bgColor2) {
        const directionMap = {
          horizontal: '90deg',
          vertical: '180deg',
          'left-diagonal': '135deg',
          'right-diagonal': '45deg',
        };
        const deg = directionMap[propValue.bgDirection] || '180deg';
        style.backgroundImage = `linear-gradient(${deg}, ${propValue.backgroundColor || 'transparent'}, ${
          propValue.bgColor2
        })`;
      } else {
        style.backgroundColor = propValue.backgroundColor || 'transparent';
      }
      if (propValue.showBorder && propValue.borderWidth) {
        style.backgroundSize = `100% calc(100% + ${propValue.borderWidth * 2 || 0}px`;
        style.backgroundPosition = `0px -${propValue.borderWidth || 0}px`;
      }

      // Border Radius
      if (propValue.isRadiusAll) {
        style.borderRadius = (propValue.borderRadius || 0) + 'px';
      } else {
        style.borderRadius = `${propValue.borderRadiusTopLeft || 0}px ${propValue.borderRadiusTopRight || 0}px ${
          propValue.borderRadiusBottomRight || 0
        }px ${propValue.borderRadiusBottomLeft || 0}px`;
      }
      return style;
    },
    getPanelStyle(propValue) {
      const style = {
        width: '100%',
        height: '100%',
        boxSizing: 'border-box',
        borderWidth: (propValue.showBorder ? propValue.borderWidth : 0) + 'px',
        borderColor: propValue.borderColor || 'transparent',
        borderStyle: propValue.borderStyle || 'solid',
        padding: `${propValue.paddingTop || 0}px ${propValue.paddingRight || 0}px ${propValue.paddingBottom || 0}px ${
          propValue.paddingLeft || 0
        }px`,
      };

      // Background
      if (propValue.bgColor2) {
        const directionMap = {
          horizontal: '90deg',
          vertical: '180deg',
          'left-diagonal': '135deg',
          'right-diagonal': '45deg',
        };
        const deg = directionMap[propValue.bgDirection] || '180deg';
        style.backgroundImage = `linear-gradient(${deg}, ${propValue.backgroundColor || 'transparent'}, ${
          propValue.bgColor2
        })`;
      } else {
        style.backgroundColor = propValue.backgroundColor || 'transparent';
      }
      // Border Radius
      if (propValue.isRadiusAll) {
        style.borderRadius = (propValue.borderRadius || 0) + 'px';
      } else {
        style.borderRadius = `${propValue.borderRadiusTopLeft || 0}px ${propValue.borderRadiusTopRight || 0}px ${
          propValue.borderRadiusBottomRight || 0
        }px ${propValue.borderRadiusBottomLeft || 0}px`;
      }

      if (propValue.showBorder && propValue.borderWidth) {
        style.backgroundSize = `100% calc(100% + ${propValue.borderWidth * 2 || 0}px`;
        style.backgroundPosition = `0px -${propValue.borderWidth || 0}px`;
      }

      // Box Shadow
      if (propValue.showShadow) {
        style.boxShadow = `${propValue.shadowX || 0}px ${propValue.shadowY || 0}px ${propValue.shadowBlur || 0}px ${
          propValue.shadowSpread || 0
        }px ${propValue.shadowColor || '#000000'}`;
      }

      return style;
    },
    getDisplayText(item) {
      if (item.propValue.fieldType) {
        const field = this.currentFieldList.find((f) => f.value === item.propValue.fieldType);
        const label = field ? field.label : '';
        const title = item.propValue.text || '';
        return title ? `${title}${label}` : label;
      }
      return item.propValue.text || '';
    },
    handleDrop(e) {
      e.preventDefault();
      e.stopPropagation();

      const componentStr = e.dataTransfer.getData('component');
      if (!componentStr) return;

      const component = JSON.parse(componentStr);
      const rect = this.$el.getBoundingClientRect();

      // Calculate drop position relative to canvas
      let left = e.clientX - rect.left;
      let top = e.clientY - rect.top;

      const canvasWidth = parseInt(this.canvasStyle.width);
      const canvasHeight = parseInt(this.canvasStyle.height);
      const compWidth = component.propValue.width;
      const compHeight = component.propValue.height;

      // Boundary checks for drop
      if (left < 0) left = 0;
      if (top < 0) top = 0;
      if (left + compWidth > canvasWidth) left = canvasWidth - compWidth;
      if (top + compHeight > canvasHeight) top = canvasHeight - compHeight;

      // Boundary checks can be added here

      const newComponent = {
        id: generateID(),
        component: component.type,
        label: component.label,
        style: {
          top: top,
          left: left,
          width: component.propValue.width,
          height: component.propValue.height,
          zIndex: 1,
        },
        propValue: deepCopy(component.propValue),
      };

      this.$emit('update:componentData', [...this.componentData, newComponent]);
      this.$emit('record');
      this.$emit('select', newComponent, this.componentData.length);
    },
    handleDragOver(e) {
      e.preventDefault();
      e.dataTransfer.dropEffect = 'copy';
    },
    handleMouseDown(e) {
      e.stopPropagation();
      this.$emit('select', null, -1);
      this.activeComponentIds = [];

      // Start area selection
      const rect = this.$el.getBoundingClientRect();
      const startX = e.clientX - rect.left;
      const startY = e.clientY - rect.top;
      this.areaStart = { x: startX, y: startY };
      this.isShowArea = true;
      this.areaWidth = 0;
      this.areaHeight = 0;

      const move = (moveEvent) => {
        const currX = moveEvent.clientX - rect.left;
        const currY = moveEvent.clientY - rect.top;
        this.areaWidth = Math.abs(currX - startX);
        this.areaHeight = Math.abs(currY - startY);
        this.areaStart.x = Math.min(currX, startX);
        this.areaStart.y = Math.min(currY, startY);
      };

      const up = () => {
        document.removeEventListener('mousemove', move);
        document.removeEventListener('mouseup', up);
        this.isShowArea = false;

        // Calculate intersection
        const selectedIds = [];
        this.componentData.forEach((component) => {
          if (component.isHidden) return;
          const { left, top, width, height } = component.style;
          // Check overlap
          if (
            left < this.areaStart.x + this.areaWidth &&
            left + width > this.areaStart.x &&
            top < this.areaStart.y + this.areaHeight &&
            top + height > this.areaStart.y
          ) {
            selectedIds.push(component.id);
          }
        });

        if (selectedIds.length > 0) {
          this.activeComponentIds = selectedIds;
          // Select the last one as curComponent
          const lastId = selectedIds[selectedIds.length - 1];
          const component = this.componentData.find((c) => c.id === lastId);
          const index = this.componentData.findIndex((c) => c.id === lastId);
          this.$emit('select', component, index);
        }
      };

      document.addEventListener('mousemove', move);
      document.addEventListener('mouseup', up);
    },
    handleSelect(component, index) {
      if (component) {
        // If clicking on an item that is NOT in the current selection, clear and select it.
        // If clicking on an item that IS in the selection, keep the selection (to allow moving group).
        // TODO: Add Ctrl/Command key support for toggling selection.
        if (!this.activeComponentIds.includes(component.id)) {
          this.activeComponentIds = [component.id];
        }
      } else {
        this.activeComponentIds = [];
      }
      this.$emit('select', component, index);
    },
    handleResize(pos) {
      if (this.curComponent) {
        const canvasWidth = parseInt(this.canvasStyle.width);
        const canvasHeight = parseInt(this.canvasStyle.height);

        // Calculate delta for group move before modifying pos (which might be clamped)
        // Wait, pos IS the new absolute position of curComponent.
        // We need to know the *intended* change.
        // But handleResize in Shape.vue calculates absolute pos based on mouse delta.
        // So pos.left - curComponent.style.left IS the delta.
        // However, we should apply this delta to other components *if* it's a move operation (not resize).
        // Resize usually changes width/height too.
        // Shape.vue emits resize event for both move and resize.
        // For move, width/height don't change.
        const isResize = pos.width !== this.curComponent.style.width || pos.height !== this.curComponent.style.height;

        let dx = 0;
        let dy = 0;
        if (!isResize) {
          dx = pos.left - this.curComponent.style.left;
          dy = pos.top - this.curComponent.style.top;
        }

        // Boundary checks
        if (isResize) {
          if (pos.left < 0) {
            pos.width += pos.left;
            pos.left = 0;
          }
          if (pos.top < 0) {
            pos.height += pos.top;
            pos.top = 0;
          }
          if (pos.left + pos.width > canvasWidth) {
            const leftChanged = pos.left !== this.curComponent.style.left;
            if (leftChanged) {
              pos.left = canvasWidth - pos.width;
            } else {
              pos.width = canvasWidth - pos.left;
            }
          }
          if (pos.top + pos.height > canvasHeight) {
            const topChanged = pos.top !== this.curComponent.style.top;
            if (topChanged) {
              pos.top = canvasHeight - pos.height;
            } else {
              pos.height = canvasHeight - pos.top;
            }
          }
          // Clear guidelines during resize
          this.guideline.showV = false;
          this.guideline.showH = false;
        } else {
          // Move
          const rotate = pos.rotate || 0;
          if (rotate === 0) {
            if (pos.left < 0) pos.left = 0;
            if (pos.top < 0) pos.top = 0;
            if (pos.left + pos.width > canvasWidth) pos.left = canvasWidth - pos.width;
            if (pos.top + pos.height > canvasHeight) pos.top = canvasHeight - pos.height;
          } else {
            // Rotated boundary check
            const r = (rotate * Math.PI) / 180;
            const w = pos.width;
            const h = pos.height;

            // AABB dimensions
            const boundingWidth = w * Math.abs(Math.cos(r)) + h * Math.abs(Math.sin(r));
            const boundingHeight = w * Math.abs(Math.sin(r)) + h * Math.abs(Math.cos(r));

            // Constraints
            const minLeft = (boundingWidth - w) / 2;
            const maxLeft = canvasWidth - (w + boundingWidth) / 2;
            const minTop = (boundingHeight - h) / 2;
            const maxTop = canvasHeight - (h + boundingHeight) / 2;

            if (pos.left < minLeft) pos.left = minLeft;
            if (pos.left > maxLeft) pos.left = maxLeft;
            if (pos.top < minTop) pos.top = minTop;
            if (pos.top > maxTop) pos.top = maxTop;
          }

          // Recalculate dx/dy after boundary checks
          dx = pos.left - this.curComponent.style.left;
          dy = pos.top - this.curComponent.style.top;

          // Snapping and Guidelines (Center Alignment)
          const threshold = 5;
          const curCenterX = pos.left + pos.width / 2;
          const curCenterY = pos.top + pos.height / 2;

          let closestX = null;
          let minDiffX = threshold;
          let closestY = null;
          let minDiffY = threshold;

          // 1. Check Canvas Center
          const canvasCenterX = canvasWidth / 2;
          const canvasCenterY = canvasHeight / 2;

          if (Math.abs(curCenterX - canvasCenterX) < threshold) {
            minDiffX = Math.abs(curCenterX - canvasCenterX);
            closestX = canvasCenterX;
          }
          if (Math.abs(curCenterY - canvasCenterY) < threshold) {
            minDiffY = Math.abs(curCenterY - canvasCenterY);
            closestY = canvasCenterY;
          }

          // 2. Check Other Components
          this.componentData.forEach((comp) => {
            if (comp.id === this.curComponent.id) return;
            if (comp.isHidden) return;
            // Skip components in the current selection group to avoid snapping to moving peers
            if (this.activeComponentIds.includes(comp.id)) return;

            const compCenterX = comp.style.left + comp.style.width / 2;
            const compCenterY = comp.style.top + comp.style.height / 2;

            const diffX = Math.abs(curCenterX - compCenterX);
            if (diffX < minDiffX) {
              minDiffX = diffX;
              closestX = compCenterX;
            }

            const diffY = Math.abs(curCenterY - compCenterY);
            if (diffY < minDiffY) {
              minDiffY = diffY;
              closestY = compCenterY;
            }
          });

          // Apply Snap X
          this.guideline.showV = false;
          if (closestX !== null) {
            const newLeft = closestX - pos.width / 2;
            // Re-calculate dx if snapped
            dx = newLeft - this.curComponent.style.left;
            pos.left = newLeft;
            this.guideline.showV = true;
            this.guideline.v = closestX;
          }

          // Apply Snap Y
          this.guideline.showH = false;
          if (closestY !== null) {
            const newTop = closestY - pos.height / 2;
            // Re-calculate dy if snapped
            dy = newTop - this.curComponent.style.top;
            pos.top = newTop;
            this.guideline.showH = true;
            this.guideline.h = closestY;
          }
        }

        // Apply changes to curComponent
        this.curComponent.style = { ...this.curComponent.style, ...pos };

        // Apply changes to other selected components
        if (!isResize && this.activeComponentIds.length > 1) {
          this.activeComponentIds.forEach((id) => {
            if (id === this.curComponent.id) return;
            const comp = this.componentData.find((c) => c.id === id);
            if (comp) {
              comp.style.left += dx;
              comp.style.top += dy;
            }
          });
        }

        // Sync Line thickness with container size
        if (this.curComponent.component === 'Line') {
          if (this.curComponent.propValue.direction === 'horizontal') {
            this.curComponent.propValue.height = pos.height;
          } else {
            this.curComponent.propValue.height = pos.width;
          }
        }
      }
    },
    handleEnd() {
      this.guideline.showV = false;
      this.guideline.showH = false;
      this.$emit('record', '调整组件');
    },
    handleDelete(index) {
      this.$emit('delete', index);
    },
    handleCopy(item) {
      this.$emit('copy', item);
    },
    handleMove(index, type) {
      this.$emit('move', { index, type });
    },
    getShapeStyle(item) {
      const style = item.style;
      const isLine = item.component === 'Line';
      return {
        top: (isLine ? Number(style.top) - 5 : style.top) + 'px',
        left: style.left + 'px',
        width: style.width + 'px',
        height: (isLine ? Number(style.height) + 10 : style.height) + 'px',
        zIndex: style.zIndex,
        transform: `rotate(${style.rotate || 0}deg)`,
      };
    },
  },
};
</script>

<style scoped lang="scss">
.editor {
  position: relative;
  background: #fff;
  margin: 20px auto;
  // box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);

  .grid-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: linear-gradient(45deg, #f5f5f5 25%, transparent 25%, transparent 75%, #f5f5f5 75%, #f5f5f5),
      linear-gradient(45deg, #f5f5f5 25%, transparent 25%, transparent 75%, #f5f5f5 75%, #f5f5f5);
    background-size: 20px 20px;
    background-position: 0 0, 10px 10px;
    pointer-events: none;
  }

  .guideline-v {
    position: absolute;
    top: 0;
    bottom: 0;
    border-left: 1px dashed #1890ff;
    z-index: 9999;
    pointer-events: none;
  }

  .guideline-h {
    position: absolute;
    left: 0;
    right: 0;
    border-top: 1px dashed #1890ff;
    z-index: 9999;
    pointer-events: none;
  }

  .area-selector {
    position: absolute;
    border: 1px solid #1890ff;
    background-color: rgba(24, 144, 255, 0.3);
    z-index: 9999;
    pointer-events: none;
  }
}
</style>
