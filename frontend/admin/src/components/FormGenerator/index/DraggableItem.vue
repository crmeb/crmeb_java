<script lang="jsx">
import { defineComponent } from 'vue';
import draggable from 'vuedraggable';
import { CopyDocument } from '@element-plus/icons-vue';
import render from '@/components/FormGenerator/components/render/render';
import { getDraggableItemKey } from '@/utils/draggableKey';

function layoutIsNotFound(vm) {
  throw new Error(`没有与${vm.element.__config__.layout}匹配的layout`);
}

function renderChildren(h, element, vm) {
  const config = element.__config__;
  if (!Array.isArray(config.children)) return null;
  return config.children.map((el, i) => renderChild(h, el, i, config.children, vm));
}

function renderChild(h, element, index, parent, vm) {
  const layout = layouts[element.__config__.layout];
  if (layout) {
    return layout(h, element, index, parent, vm);
  }
  return layoutIsNotFound(vm);
}

const components = {
  itemBtns(h, element, index, parent, vm) {
    return [
      <span
        class="drawing-item-copy"
        title="复制"
        onClick={(event) => {
          vm.copyItem(element, parent);
          event.stopPropagation();
        }}
      >
        <el-icon><CopyDocument /></el-icon>
      </span>,
      <span
        class="drawing-item-delete"
        title="删除"
        onClick={(event) => {
          vm.deleteItem(index, parent);
          event.stopPropagation();
        }}
      >
        <i class="el-icon-delete" />
      </span>,
    ];
  },
};

const layouts = {
  colFormItem(h, element, index, parent, vm) {
    const config = element.__config__;
    let className = vm.activeId === config.formId ? 'drawing-item active-from-item' : 'drawing-item';
    if (vm.formConf.unFocusedComponentBorder) className += ' unfocus-bordered';
    let labelWidth = config.labelWidth ? `${config.labelWidth}px` : null;
    if (config.showLabel === false) labelWidth = '0';
    if (config.tips == undefined) {
      config.tips = false; //如果以前的表单没有tooltip配置，就赋值一个默认值用来读取
    }
    if (config.tips) {
      return (
        <el-col
          span={config.span}
          class={className}
          nativeOnClick={(event) => {
            vm.activeItem(element);
            event.stopPropagation();
          }}
        >
          <el-form-item
            label-width={labelWidth}
            label={config.showLabel ? config.label : ''}
            required={config.required}
          >
            <render
              key={config.renderKey}
              conf={element}
              onInput={(event) => {
                config.defaultValue = event;
              }}
            />
            <div>
              <span class="tips-info">{config.tipsDesc}</span>
            </div>
          </el-form-item>
          {components.itemBtns(h, element, index, parent, vm)}
        </el-col>
      );
    } else {
      return (
        <el-col
          span={config.span}
          class={className}
          nativeOnClick={(event) => {
            vm.activeItem(element);
            event.stopPropagation();
          }}
        >
          <el-form-item
            label-width={labelWidth}
            label={config.showLabel ? config.label : ''}
            required={config.required}
          >
            <render
              key={config.renderKey}
              conf={element}
              onInput={(event) => {
                config.defaultValue = event;
              }}
            />
          </el-form-item>
          {components.itemBtns(h, element, index, parent, vm)}
        </el-col>
      );
    }
  },
  rowFormItem(h, element, index, parent, vm) {
    const className =
      vm.activeId === element.__config__.formId ? 'drawing-row-item active-from-item' : 'drawing-row-item';
    let child = renderChildren(h, element, vm);
    if (element.type === 'flex') {
      child = (
        <el-row type={element.type} justify={element.justify} align={element.align}>
          {child}
        </el-row>
      );
    }
    return (
      <el-col span={element.__config__.span}>
        <el-row
          gutter={element.__config__.gutter}
          class={className}
          nativeOnClick={(event) => {
            vm.activeItem(element);
            event.stopPropagation();
          }}
        >
          <span class="component-name">{element.__config__.componentName}</span>
          <draggable
            list={element.__config__.children}
            itemKey={getDraggableItemKey}
            animation={340}
            group="componentsGroup"
            class="drag-wrapper"
            v-slots={{
              item: ({ element: childElement, index: childIndex }) =>
                renderChild(h, childElement, childIndex, element.__config__.children, vm),
            }}
          />
          {components.itemBtns(h, element, index, parent, vm)}
        </el-row>
      </el-col>
    );
  },
};

export default defineComponent({
  components: {
    render,
    draggable,
    CopyDocument,
  },
  props: ['element', 'index', 'drawingList', 'activeId', 'formConf'],
  emits: ['activeItem', 'copyItem', 'deleteItem'],
  setup(props, { emit }) {
    const vm = {
      get activeId() {
        return props.activeId;
      },
      get formConf() {
        return props.formConf;
      },
      get element() {
        return props.element;
      },
      activeItem: (element) => emit('activeItem', element),
      copyItem: (element, parent) => emit('copyItem', element, parent),
      deleteItem: (index, parent) => emit('deleteItem', index, parent),
    };
    return () => {
      const layout = layouts[props.element.__config__.layout];
      if (layout) {
        return layout(null, props.element, props.index, props.drawingList, vm);
      }
      return layoutIsNotFound(vm);
    };
  },
});
</script>
