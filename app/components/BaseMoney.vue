<template>
  <view
    :class="{ line: line, weight: weight }"
    :style="{ color: color, fontFamily: SemiBold ? 'SemiBold' : 'Regular' }"
    class="base-money"
  >
    <text
      v-show="preFix"
      class="preFix"
      :style="{ 'font-size': preFixSize + 'rpx', color: textColor }"
    >{{ preFix }}</text>
    <text v-if="symbol" class="symbol" :style="{ 'font-size': symbolSize + 'rpx' }">￥</text>
    <text class="integer" :style="{ 'font-size': integerSize + 'rpx' }">{{ integer }}</text>
    <text
      v-if="digits && ((isCoupon && decimal != '00') || !isCoupon)"
      class="decimal"
      :style="{ 'font-size': decimalSize + 'rpx' }"
    >.{{ decimal }}</text>
  </view>
</template>

<script>
export default {
  name: 'BaseMoney',
  props: {
    digits: {
      type: Number,
      default: 2,
    },
    money: {
      type: [String, Number],
      default: '',
    },
    line: {
      type: Boolean,
      default: false,
    },
    weight: {
      type: Boolean,
      default: false,
    },
    symbol: {
      type: Boolean,
      default: true,
    },
    color: {
      type: String,
      default: 'var(--view-theme)',
    },
    textColor: {
      type: String,
      default: '#999',
    },
    symbolSize: {
      type: String,
      default: '20',
    },
    integerSize: {
      type: String,
      default: '26',
    },
    decimalSize: {
      type: String,
      default: '24',
    },
    preFix: {
      type: String,
      default: '',
    },
    preFixSize: {
      type: String,
      default: '24',
    },
    SemiBold: {
      type: Boolean,
      default: true,
    },
    isCoupon: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      integer: '0',
      decimal: '00',
    };
  },
  watch: {
    money: {
      handler(newValue) {
        const number = Number(newValue);
        let value = Number.isFinite(number) ? number : 0;
        value = value.toFixed(this.digits).split('.');
        this.integer = value[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        this.decimal = value[1] || '00';
      },
      immediate: true,
    },
  },
};
</script>

<style lang="scss" scoped>
.base-money {
  display: inline-block;

  &.line {
    text-decoration: line-through;
  }

  &.weight {
    font-weight: 500;
  }
}

.preFix {
  font-weight: 500 !important;
  font-family: PingFang SC-Medium, PingFang SC !important;
}

.SemiBold {
  font-family: 'SemiBold';
}

.Regular {
  font-family: 'Regular';
}
</style>
