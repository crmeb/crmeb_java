<template>
  <view class="goodList" :style="[bgRadius]">
    <common-wrapper :config="configData">
      <!-- Header Section -->
      <view
        class="header-box"
        :style="[headerBoxStyle]"
        v-if="headerText || headerImg"
      >
        <view
          class="title-text"
          v-if="headerType == 0"
          :style="[titleTextStyle]"
        >
          {{ headerText }}
        </view>
        <view class="title-img" v-else :style="[titleImgBoxStyle]">
          <image
            :src="headerImg"
            mode="widthFix"
            :style="[titleImgStyle]"
            v-if="headerImg"
          ></image>
        </view>
      </view>

      <view v-if="tempArr.length > 0" class="list">
        <!-- 单列 -->
        <view v-if="styleConfig == 0">
          <view
            class="w-full flex justify-between item bg--w111-fff p-20"
            :style="[bgColor, bgRadius]"
            v-for="(item, index) in tempArr"
            :key="index"
            @tap="goDetail(item)"
          >
            <easy-loadimage
              :image-src="item.image"
              width="224rpx"
              height="224rpx"
              :borderRadius="imgStyle"
            ></easy-loadimage>
            <view class="flex-1 flex-col justify-between pl-20">
              <view
                class="w-full fs-28 h-80 lh-40rpx line2"
                v-if="checkboxInfo.includes(0)"
                :style="[productStyle]"
              >
                <text v-if="item.brand_name" class="brand-tag">{{
                  item.brand_name
                }}</text>
                {{ item.store_name }}
              </view>
              <view
                class="flex items-end flex-wrap mt-8 w-full"
                v-if="
                  checkboxInfo.includes(1) &&
                  item.label_list &&
                  item.label_list.length
                "
              >
                <BaseTag
                  :text="label.name"
                  :color="label.font_color"
                  :background="label.bg_color"
                  :borderColor="label.border_color"
                  :circle="label.border_color ? true : false"
                  :imgSrc="label.image"
                  v-for="(label, idx) in item.label_list"
                  :key="idx"
                ></BaseTag>
              </view>
              <view class="flex-between-center" v-if="onlyShowPrice">
                <baseMoney
                  :money="item.price"
                  symbolSize="24"
                  integerSize="40"
                  decimalSize="24"
                  weight
                  :color="priceColor"
                ></baseMoney>
                <view @tap.stop="addCartChange(item, index)" v-if="!showBtn">
                  <view
                    class="w-96 h-56 rd-28rpx flex-center fs-24 text--w111-fff"
                    v-if="btnStyle == 0"
                    :style="[btnBgColor]"
                    >{{ '购买' }}</view
                  >
                  <view
                    class="rd-24rpx w-44 h-44"
                    :style="[btnTextColor]"
                    v-else-if="btnStyle == 1"
                  >
                    <view class="flex-center cart-btn">
                      <text class="iconfont icon-ic_increase-2 fs-26"></text>
                    </view>
                  </view>
                  <view
                    class="rd-24rpx w-44 h-44"
                    :style="[btnTextColor]"
                    v-else
                  >
                    <view class="flex-center cart-btn">
                      <text class="iconfont icon-ic_ShoppingCart1 fs-26"></text>
                    </view>
                  </view>
                </view>
              </view>
              <view v-else>
                <view class="flex-y-center mt-4 pb-10">
                  <baseMoney
                    :money="item.price"
                    symbolSize="24"
                    integerSize="40"
                    decimalSize="24"
                    weight
                    :color="priceColor"
                    v-if="checkboxInfo.includes(2)"
                  ></baseMoney>
                </view>
                <view class="flex justify-between items-end relative">
                  <view class="flex-y-center">
                    <text
                      class="fs-22"
                      v-if="checkboxInfo.includes(3)"
                      :style="[uniStyle]"
                      >{{ '已售' }}{{ item.sales
                      }}{{ item.unit_name }}</text
                    >
                    <text
                      class="fs-22 text--w111-999 pl-16"
                      v-if="checkboxInfo.includes(4)"
                      >评分{{ item.star || 0 }}</text
                    >
                  </view>
                  <view
                    class="absolute right-0 bottom-0"
                    @tap.stop="addCartChange(item, index)"
                    v-if="!showBtn"
                  >
                    <view
                      class="w-96 h-56 rd-28rpx flex-center fs-24 text--w111-fff"
                      v-if="btnStyle == 0"
                      :style="[btnBgColor]"
                      >{{ '购买' }}</view
                    >
                    <view
                      class="rd-24rpx w-44 h-44"
                      :style="[btnTextColor]"
                      v-else-if="btnStyle == 1"
                    >
                      <view class="flex-center cart-btn">
                        <text class="iconfont icon-ic_increase-2 fs-26"></text>
                      </view>
                    </view>
                    <view
                      class="rd-24rpx w-44 h-44"
                      :style="[btnTextColor]"
                      v-else
                    >
                      <view class="flex-center cart-btn">
                        <text
                          class="iconfont icon-ic_ShoppingCart1 fs-26"
                        ></text>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
        <!-- 两列瀑布流 -->
        <view class="wf-page" v-if="goodStyleConfig == 1">
          <!-- left -->
          <view class="item-box">
            <view id="left" v-if="leftList.length">
              <view
                v-for="(item, index) in leftList"
                :key="index"
                class="wf-item"
                @tap="goDetail(item)"
                :style="[bgColor, bgRadius]"
              >
                <view class="pictrue">
                  <easy-loadimage
                    mode="widthFix"
                    :image-src="item.image"
                    width="100%"
                    height="346rpx"
                    :borderRadius="imgStyle"
                  ></easy-loadimage>
                </view>
                <view class="info_box" :style="[bgRadius2]">
                  <view
                    class="w-full line2 fs-28 text--w111-333 lh-40rpx"
                    v-if="checkboxInfo.includes(0)"
                    :style="[productStyle]"
                  >
                    <text v-if="item.brand_name" class="brand-tag">{{
                      item.brand_name
                    }}</text>
                    {{ item.store_name }}
                  </view>
                  <view
                    class="flex items-end flex-wrap mt-8 w-full"
                    v-if="
                      checkboxInfo.includes(1) &&
                      item.label_list &&
                      item.label_list.length
                    "
                  >
                    <BaseTag
                      :text="label.name"
                      :color="label.font_color"
                      :background="label.bg_color"
                      :borderColor="label.border_color"
                      :circle="label.border_color ? true : false"
                      :imgSrc="label.image"
                      v-for="(label, idx) in item.label_list"
                      :key="idx"
                    ></BaseTag>
                  </view>
                  <view class="flex-between-center mt-20" v-if="onlyShowPrice">
                    <baseMoney
                      :money="item.price"
                      symbolSize="24"
                      integerSize="40"
                      decimalSize="24"
                      weight
                      :color="priceColor"
                    ></baseMoney>
                    <view
                      @tap.stop="addCartChange(item, index)"
                      v-if="!showBtn"
                    >
                      <view
                        class="rd-24rpx w-44 h-44"
                        :style="[btnTextColor]"
                        v-if="btnStyle == 0"
                      >
                        <view class="flex-center cart-btn">
                          <text
                            class="iconfont icon-ic_increase-2 fs-26"
                          ></text>
                        </view>
                      </view>
                      <view
                        class="rd-24rpx w-44 h-44"
                        :style="[btnTextColor]"
                        v-else
                      >
                        <view class="flex-center cart-btn">
                          <text
                            class="iconfont icon-ic_ShoppingCart1 fs-26"
                          ></text>
                        </view>
                      </view>
                    </view>
                  </view>
                  <view v-else >
                    <view class="flex-y-center flex-no-wrap mt-8">
                      <baseMoney
                        :money="item.price"
                        symbolSize="24"
                        integerSize="40"
                        decimalSize="24"
                        weight
                        :color="priceColor"
                        v-if="checkboxInfo.includes(2)"
                      ></baseMoney>
                    </view>
                    <view class="flex-between-center mt-20">
                      <text
                        class="fs-22"
                        v-if="checkboxInfo.includes(3)"
                        :style="[uniStyle]"
                        >{{ '已售' }}{{ item.sales
                        }}{{ item.unit_name }}</text
                      >
                      <text v-else></text>
                      <view
                        @tap.stop="addCartChange(item, index)"
                        v-if="!showBtn"
                      >
                        <view
                          class="rd-24rpx w-44 h-44"
                          :style="[btnTextColor]"
                          v-if="btnStyle == 0"
                        >
                          <view class="flex-center cart-btn">
                            <text
                              class="iconfont icon-ic_increase-2 fs-26"
                            ></text>
                          </view>
                        </view>
                        <view
                          class="rd-24rpx w-44 h-44"
                          :style="[btnTextColor]"
                          v-else
                        >
                          <view class="flex-center cart-btn">
                            <text
                              class="iconfont icon-ic_ShoppingCart1 fs-26"
                            ></text>
                          </view>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
          <!-- right -->
          <view class="item-box">
            <view id="right" v-if="rightList.length">
              <view
                v-for="(item, index) in rightList"
                :key="index"
                class="wf-item"
                @tap="goDetail(item)"
                :style="[bgColor, bgRadius]"
              >
                <view class="pictrue">
                  <easy-loadimage
                    mode="widthFix"
                    :image-src="item.image"
                    width="100%"
                    height="346rpx"
                    :borderRadius="imgStyle"
                  ></easy-loadimage>
                </view>
                <view class="info_box" :style="[bgRadius2]">
                  <view
                    class="w-full line2 fs-28 text--w111-333 lh-40rpx"
                    v-if="checkboxInfo.includes(0)"
                    :style="[productStyle]"
                  >
                    <text v-if="item.brand_name" class="brand-tag">{{
                      item.brand_name
                    }}</text>
                    {{ item.store_name }}
                  </view>
                  <view
                    class="flex items-end flex-wrap mt-8 w-full"
                    v-if="
                      checkboxInfo.includes(1) &&
                      item.label_list &&
                      item.label_list.length
                    "
                  >
                    <BaseTag
                      :text="label.name"
                      :color="label.font_color"
                      :background="label.bg_color"
                      :borderColor="label.border_color"
                      :circle="label.border_color ? true : false"
                      :imgSrc="label.image"
                      v-for="(label, idx) in item.label_list"
                      :key="idx"
                    ></BaseTag>
                  </view>
                  <view class="flex-between-center mt-20" v-if="onlyShowPrice">
                    <baseMoney
                      :money="item.price"
                      symbolSize="24"
                      integerSize="40"
                      decimalSize="24"
                      weight
                      :color="priceColor"
                    ></baseMoney>
                    <view
                      @tap.stop="addCartChange(item, index)"
                      v-if="!showBtn"
                    >
                      <view
                        class="rd-24rpx w-44 h-44"
                        :style="[btnTextColor]"
                        v-if="btnStyle == 0"
                      >
                        <view class="flex-center cart-btn">
                          <text
                            class="iconfont icon-ic_increase-2 fs-26"
                          ></text>
                        </view>
                      </view>
                      <view
                        class="rd-24rpx w-44 h-44"
                        :style="[btnTextColor]"
                        v-else
                      >
                        <view class="flex-center cart-btn">
                          <text
                            class="iconfont icon-ic_ShoppingCart1 fs-26"
                          ></text>
                        </view>
                      </view>
                    </view>
                  </view>
                  <view v-else>
                    <view class="flex-y-center mt-8">
                      <baseMoney
                        :money="item.price"
                        symbolSize="24"
                        integerSize="40"
                        decimalSize="24"
                        weight
                        :color="priceColor"
                        v-if="checkboxInfo.includes(2)"
                      ></baseMoney>
                    </view>
                    <view class="flex-between-center mt-20">
                      <text
                        class="fs-22"
                        v-if="checkboxInfo.includes(3)"
                        :style="[uniStyle]"
                        >{{ '已售' }}{{ item.sales
                        }}{{ item.unit_name }}</text
                      >
                      <text v-else></text>
                      <view
                        @tap.stop="addCartChange(item, index)"
                        v-if="!showBtn"
                      >
                        <view
                          class="rd-24rpx w-44 h-44"
                          :style="[btnTextColor]"
                          v-if="btnStyle == 0"
                        >
                          <view class="flex-center cart-btn">
                            <text
                              class="iconfont icon-ic_increase-2 fs-26"
                            ></text>
                          </view>
                        </view>
                        <view
                          class="rd-24rpx w-44 h-44"
                          :style="[btnTextColor]"
                          v-else
                        >
                          <view class="flex-center cart-btn">
                            <text
                              class="iconfont icon-ic_ShoppingCart1 fs-26"
                            ></text>
                          </view>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
        <!-- 两列展示(横向) -->
        <view
          class="pt-32 pr-24 pb-32 pl-24 bg--w111-fff"
          :style="[bgRadius, bgColor]"
          v-if="goodStyleConfig == 3 && dataConfig.name !== 'goodRecommend'"
        >
          <view class="grid-column-2 grid-gap-20rpx">
            <view
              class="flex"
              v-for="(item, index) in tempArr"
              :key="index"
              @tap="goDetail(item)"
              :style="[bgRadius]"
            >
              <easy-loadimage
                mode="widthFix"
                :image-src="item.image"
                width="144rpx"
                height="144rpx"
                :borderRadius="imgStyle"
              ></easy-loadimage>
              <view class="flex-1 pl-20">
                <view
                  class="w-full fs-26 h-72 lh-36rpx line2 mb-20"
                  v-if="checkboxInfo.includes(0)"
                  :style="[productStyle]"
                >
                  <text v-if="item.brand_name" class="brand-tag">{{
                    item.brand_name
                  }}</text>
                  {{ item.store_name }}
                </view>
                <baseMoney
                  :money="item.price"
                  symbolSize="24"
                  integerSize="40"
                  decimalSize="24"
                  weight
                  :color="priceColor"
                  v-if="checkboxInfo.includes(2)"
                ></baseMoney>
              </view>
            </view>
          </view>
        </view>
        <!-- 三列 -->
        <view
          class="pt-32 pr-24 pb-32 pl-24 bg--w111-fff"
          :style="[bgColor, bgRadius]"
          v-if="goodStyleConfig == 2"
        >
          <view class="grid-column-3 grid-gap-20rpx">
            <view
              v-for="(item, index) in tempArr"
              :key="index"
              @tap="goDetail(item)"
              :style="[bgRadius]"
            >
              <easy-loadimage
                mode="widthFix"
                :image-src="item.image"
                width="100%"
                height="210rpx"
                :borderRadius="imgStyle"
              ></easy-loadimage>
              <view
                class="w-full fs-28 h-80 lh-40rpx line2 mt-20"
                v-if="checkboxInfo.includes(0)"
                :style="[productStyle]"
              >
                <text v-if="item.brand_name" class="brand-tag">{{
                  item.brand_name
                }}</text>
                {{ item.store_name }}
              </view>
              <view class="flex-between-center mt-14">
                <baseMoney
                  :money="item.price"
                  symbolSize="24"
                  integerSize="40"
                  decimalSize="24"
                  weight
                  :color="priceColor"
                  v-if="checkboxInfo.includes(2)"
                ></baseMoney>
                <view @tap.stop="addCartChange(item, index)" v-if="!showBtn">
                  <view
                    class="rd-24rpx w-44 h-44"
                    :style="[btnTextColor]"
                    v-if="btnStyle == 0"
                  >
                    <view class="flex-center cart-btn">
                      <text class="iconfont icon-ic_increase-2 fs-26"></text>
                    </view>
                  </view>
                  <view
                    class="rd-24rpx w-44 h-44"
                    :style="[btnTextColor]"
                    v-else
                  >
                    <view class="flex-center cart-btn">
                      <text class="iconfont icon-ic_ShoppingCart1 fs-26"></text>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
        <!-- 大图展示 -->
        <view v-if="goodStyleConfig == 4">
          <view
            class="w-full bg--w111-fff item"
            :style="[bgColor, bgRadius]"
            v-for="(item, index) in tempArr"
            :key="index"
            @tap="goDetail(item)"
          >
            <easy-loadimage
              mode="widthFix"
              :image-src="item.image"
              width="100%"
              height="360rpx"
              :borderRadius="imgStyle"
            ></easy-loadimage>
            <view class="p-24">
              <view
                class="w-full line1 fs-28 text--w111-333 lh-40rpx"
                v-if="checkboxInfo.includes(0)"
                :style="[productStyle]"
              >
                <text v-if="item.brand_name" class="brand-tag">{{
                  item.brand_name
                }}</text>
                {{ item.store_name }}
              </view>
              <view
                class="flex items-end flex-wrap mt-8 w-full"
                v-if="
                  checkboxInfo.includes(1) &&
                  item.label_list &&
                  item.label_list.length
                "
              >
                <BaseTag
                  :text="label.name"
                  :color="label.font_color"
                  :background="label.bg_color"
                  :borderColor="label.border_color"
                  :circle="label.border_color ? true : false"
                  :imgSrc="label.image"
                  v-for="(label, idx) in item.label_list"
                  :key="idx"
                ></BaseTag>
              </view>
              <view class="flex-between-center" v-if="onlyShowPrice">
                <baseMoney
                  :money="item.price"
                  symbolSize="24"
                  integerSize="40"
                  decimalSize="24"
                  weight
                  :color="priceColor"
                ></baseMoney>
                <view @tap.stop="addCartChange(item, index)" v-if="!showBtn">
                  <view
                    class="w-96 h-56 rd-28rpx flex-center fs-24 text--w111-fff"
                    v-if="btnStyle == 0"
                    :style="[btnBgColor]"
                    >{{ '购买' }}</view
                  >
                  <view
                    class="rd-24rpx w-44 h-44"
                    :style="[btnTextColor]"
                    v-else-if="btnStyle == 1"
                  >
                    <view class="flex-center cart-btn">
                      <text class="iconfont icon-ic_increase-2 fs-26"></text>
                    </view>
                  </view>
                  <view
                    class="rd-24rpx w-44 h-44"
                    :style="[btnTextColor]"
                    v-else
                  >
                    <view class="flex-center cart-btn">
                      <text class="iconfont icon-ic_ShoppingCart1 fs-26"></text>
                    </view>
                  </view>
                </view>
              </view>
              <view v-else>
                <view class="flex-y-center mt-8">
                  <baseMoney
                    :money="item.price"
                    symbolSize="24"
                    integerSize="40"
                    decimalSize="24"
                    weight
                    :color="priceColor"
                    v-if="checkboxInfo.includes(2)"
                  ></baseMoney>
                </view>
                <view class="flex justify-between items-end">
                  <view class="flex-y-center">
                    <text
                      class="fs-22 text--w111-999"
                      v-if="checkboxInfo.includes(3)"
                      :style="[uniStyle]"
                      >{{ '已售' }}{{ item.sales
                      }}{{ item.unit_name }}</text
                    >
                    <text
                      class="fs-22 text--w111-999 pl-16"
                      v-if="checkboxInfo.includes(4)"
                      >{{ '评分' }}{{ item.star || 0 }}</text
                    >
                  </view>
                  <view @tap.stop="addCartChange(item, index)" v-if="!showBtn">
                    <view
                      class="w-96 h-56 rd-28rpx flex-center fs-24 text--w111-fff"
                      v-if="btnStyle == 0"
                      :style="[btnBgColor]"
                      >{{ '购买' }}</view
                    >
                    <view
                      class="rd-24rpx w-44 h-44"
                      :style="[btnTextColor]"
                      v-else-if="btnStyle == 1"
                    >
                      <view class="flex-center cart-btn">
                        <text class="iconfont icon-ic_increase-2 fs-26"></text>
                      </view>
                    </view>
                    <view
                      class="rd-24rpx w-44 h-44"
                      :style="[btnTextColor]"
                      v-else
                    >
                      <view class="flex-center cart-btn">
                        <text
                          class="iconfont icon-ic_ShoppingCart1 fs-26"
                        ></text>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>
        <!-- 横向滑动 -->
        <view
          class="pt-32 pb-32 pl-24 bg--w111-fff"
          :style="[bgRadius, bgColor]"
          v-if="
            goodStyleConfig == 5 ||
            (goodStyleConfig == 3 && dataConfig.name === 'goodRecommend')
          "
        >
          <scroll-view
            scroll-x="true"
            show-scrollbar="false"
            class="goods-scroll-view w-full"
          >
            <view class="goods-scroll-row">
              <view
                class="goods-scroll-item"
                v-for="(item, index) in tempArr"
                :key="index"
                @tap="goDetail(item)"
                :style="[bgRadius]"
              >
                <easy-loadimage
                  mode="widthFix"
                  :image-src="item.image"
                  width="200rpx"
                  height="200rpx"
                  :borderRadius="imgStyle"
                ></easy-loadimage>
                <view
                  class="w-200 fs-28 h-80 lh-40rpx line2 break_word mt-20"
                  v-if="checkboxInfo.includes(0)"
                  :style="[productStyle]"
                >
                  <text v-if="item.brand_name" class="brand-tag">{{
                    item.brand_name
                  }}</text>
                  {{ item.store_name }}
                </view>
                <view class="flex-between-center mt-8">
                  <baseMoney
                    :money="item.price"
                    symbolSize="24"
                    integerSize="40"
                    decimalSize="24"
                    weight
                    :color="priceColor"
                    v-if="checkboxInfo.includes(2)"
                  ></baseMoney>
                  <view @tap.stop="addCartChange(item, index)" v-if="!showBtn">
                    <view
                      class="rd-24rpx w-44 h-44"
                      :style="[btnTextColor]"
                      v-if="btnStyle == 0"
                    >
                      <view class="flex-center cart-btn">
                        <text class="iconfont icon-ic_increase-2 fs-26"></text>
                      </view>
                    </view>
                    <view
                      class="rd-24rpx w-44 h-44"
                      :style="[btnTextColor]"
                      v-else
                    >
                      <view class="flex-center cart-btn">
                        <text class="iconfont icon-ic_ShoppingCart1 fs-26"></text>
                      </view>
                    </view>
                  </view>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>
    </common-wrapper>
    <productWindow
      :attr="attr"
      :isShow="1"
      :iSplus="1"
      :iScart="1"
      :fangda="false"
      type="2"
      :limitNum="storeInfo.limit_num"
      :minQty="storeInfo.min_qty"
      :unitName="storeInfo.unit_name"
      @myevent="onMyEvent"
      @ChangeAttr="ChangeAttr"
      @ChangeCartNum="ChangeCartNumDuo"
      @attrVal="attrVal"
      @iptCartNum="iptCartNum"
      @goCat="goCatNum"
      id="product-window"
    ></productWindow>
  </view>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, nextTick, getCurrentInstance } from "vue";
import { getAttr, postCartAdd } from "@/api/store.js";
import { getThemeProduct } from "@/api/api.js";
import { toLogin } from "@/libs/login.js";
import { getCartCounts, changeCartNum, cartDel } from "@/api/order.js";
import { goShopDetail } from "@/libs/order.js";
import util from "@/utils/util.js";
import productWindow from "@/components/productWindow/index.vue";
import BaseTag from "@/components/BaseTag.vue";
import commonWrapper from "./commonWrapper.vue";
import { useAppStore } from "@/store/app.js";
import { useIndexDataStore } from "@/store/indexData.js";
import { storeToRefs } from "pinia";

const { proxy } = getCurrentInstance();
const appStore = useAppStore();
const indexDataStore = useIndexDataStore();
const { isLogin, uid } = storeToRefs(appStore);
const { cartNum } = storeToRefs(indexDataStore);

const props = defineProps({
  dataConfig: {
    type: Object,
    default: () => {},
  },
  list: {
    type: [Array, Object],
    default: () => [],
  },
  isSortType: {
    type: [String, Number],
    default: 0,
  },
});

const tempArr = ref([]);
const type = ref(0);
const attr = reactive({
  cartAttr: false,
  productAttr: [],
  productSelect: {},
});
const id = ref(0);
const productValue = ref([]);
const attrValue = ref(""); //已选属性
const storeName = ref(""); //多属性产品名称
const storeInfo = ref({});
const currentCartItem = ref(null);
const allList = ref([]); // 全部列表
const leftList = ref([]); // 左边列表
const rightList = ref([]); // 右边列表
const mark = ref(0); // 列表标记
const boxHeight = ref([]); // 下标0和1分别为左列和右列高度

// computed
const showHeader = computed(() => true);
const headerType = computed(() => props.dataConfig.headerType ? props.dataConfig.headerType.tabVal : 0);
const headerText = computed(() => props.dataConfig.headerText ? props.dataConfig.headerText.value : "");
const headerImg = computed(() => props.dataConfig.headerImg ? props.dataConfig.headerImg.url : "");
const headerBoxStyle = computed(() => {
  const alignMap = ["left", "center", "right"];
  const align = props.dataConfig.headerAlign ? alignMap[props.dataConfig.headerAlign.tabVal] : "left";
  return { "text-align": align, "margin-bottom": "20rpx", padding: "0 20rpx" };
});
const titleTextStyle = computed(() => {
  const alignMap = ["left", "center", "right"];
  const align = props.dataConfig.headerAlign ? alignMap[props.dataConfig.headerAlign.tabVal] : "left";
  const color = props.dataConfig.headerColor && props.dataConfig.headerColor.color && props.dataConfig.headerColor.color[0] ? props.dataConfig.headerColor.color[0].item : "#333";
  const fontSize = props.dataConfig.headerFontSize ? props.dataConfig.headerFontSize.val * 2 + "rpx" : "32rpx";
  const fontWeight = props.dataConfig.headerTextConfig && props.dataConfig.headerTextConfig.tabVal == 0 ? "bold" : "normal";
  const fontStyle = props.dataConfig.headerTextConfig && props.dataConfig.headerTextConfig.tabVal == 2 ? "italic" : "normal";
  return { color, "font-size": fontSize, "font-weight": fontWeight, "font-style": fontStyle, "text-align": align };
});
const titleImgBoxStyle = computed(() => {
  const alignMap = ["left", "center", "right"];
  return { "text-align": props.dataConfig.headerAlign ? alignMap[props.dataConfig.headerAlign.tabVal] : "left" };
});
const titleImgStyle = computed(() => ({ height: "auto", display: "inline-block" }));
const configData = computed(() => ({ ...props.dataConfig }));
const bgRadius = computed(() => {
  let borderRadius = `${props.dataConfig.fillet.val * 2}rpx`;
  if (props.dataConfig.fillet.type) borderRadius = `${props.dataConfig.fillet.valList[0].val * 2}rpx ${props.dataConfig.fillet.valList[1].val * 2}rpx ${props.dataConfig.fillet.valList[3].val * 2}rpx ${props.dataConfig.fillet.valList[2].val * 2}rpx`;
  return { borderRadius };
});
const bgRadius2 = computed(() => {
  let borderRadius = `0 0 ${props.dataConfig.fillet.val * 2}rpx ${props.dataConfig.fillet.val * 2}rpx`;
  if (props.dataConfig.fillet.type) borderRadius = `0 0 ${props.dataConfig.fillet.valList[3].val * 2}rpx ${props.dataConfig.fillet.valList[2].val * 2}rpx`;
  return { borderRadius };
});
const bgColor = computed(() => {
  const moduleColor = props.dataConfig.moduleColor;
  const colors = moduleColor && moduleColor.color ? moduleColor.color : [];
  const left = colors[0] && colors[0].item ? colors[0].item : "#fff";
  const right = colors[1] && colors[1].item ? colors[1].item : left;
  return { background: `linear-gradient(90deg, ${left} 0%, ${right} 100%)` };
});
const styleConfig = computed(() => props.dataConfig.styleConfig.tabVal);
const imgStyle = computed(() => {
  let borderRadius = `${props.dataConfig.filletImg.val * 2}rpx`;
  if (props.dataConfig.styleConfig.tabVal == 1) borderRadius = `${props.dataConfig.filletImg.val * 2}rpx ${props.dataConfig.filletImg.val * 2}rpx 0 0`;
  if (props.dataConfig.filletImg.type) {
    borderRadius = `${props.dataConfig.filletImg.valList[0].val * 2}rpx ${props.dataConfig.filletImg.valList[1].val * 2}rpx ${props.dataConfig.filletImg.valList[3].val * 2}rpx ${props.dataConfig.filletImg.valList[2].val * 2}rpx`;
    if (props.dataConfig.styleConfig.tabVal == 1) borderRadius = `${props.dataConfig.filletImg.valList[0].val * 2}rpx ${props.dataConfig.filletImg.valList[1].val * 2}rpx 0 0`;
  }
  let imgRadius = `${props.dataConfig.fillet.val * 2}rpx ${props.dataConfig.fillet.val * 2}rpx 0 0`;
  if (props.dataConfig.fillet.type) imgRadius = `${props.dataConfig.fillet.valList[0].val * 2}rpx ${props.dataConfig.fillet.valList[1].val * 2}rpx 0 0`;
  return props.dataConfig.name == "promotionList" ? imgRadius : borderRadius;
});
const productStyle = computed(() => ({ color: props.dataConfig.goodsNameColor.color[0].item, fontWeight: props.dataConfig.goodsName.tabVal ? "normal" : "bold" }));
const checkboxInfo = computed(() => props.dataConfig.checkboxInfo.type);
const priceColor = computed(() => props.dataConfig.toneCartConfig.tabVal ? props.dataConfig.goodsPriceColor.color[0].item : "var(--view-theme)");
const otPriceColor = computed(() => props.dataConfig.goodsPriceColor.color[0].item);
const btnStyle = computed(() => Number(props.dataConfig.bntStyleConfig ? props.dataConfig.bntStyleConfig.tabVal : 0));
const showBtn = computed(() => Number(props.dataConfig.cartConfig ? props.dataConfig.cartConfig.tabVal : 0));
const cartButtonBackground = computed(() => {
  if (props.dataConfig.toneCartConfig && props.dataConfig.toneCartConfig.tabVal) {
    return `linear-gradient(90deg,${props.dataConfig.bntBgColor.color[0].item} 0%,${props.dataConfig.bntBgColor.color[1].item} 100%)`;
  }
  return "linear-gradient(90deg, var(--view-theme) 0%, var(--view-gradient) 100%)";
});
const btnBgColor = computed(() => ({ background: cartButtonBackground.value }));
const btnTextColor = computed(() => ({ color: "#FFFFFF", background: cartButtonBackground.value }));
const uniStyle = computed(() => ({
  color: props.dataConfig.toneConfig.tabVal ? props.dataConfig.soldNumColor.color[0].item || "#999" : "#999",
}));
const numberConfig = computed(() => props.dataConfig.numberConfig.val);
const goodStyleConfig = computed(() => props.dataConfig.styleConfig.tabVal);
const goodsSort = computed(() => props.dataConfig.goodsSort.tabVal);
const typeConfig = computed(() => props.dataConfig.typeConfig.activeValue);
const bntConfig = computed(() => Number(props.dataConfig.bntConfig ? props.dataConfig.bntConfig.tabVal : 0));
const onlyShowPrice = computed(() => {
  const info = checkboxInfo.value.toString();
  return info == "0,2" || info == "2,0" || info == "2";
});

// watch
watch(() => props.list, (val) => {
  if (Array.isArray(val) && val.length) tempArr.value = val;
}, { deep: true });

watch(tempArr, (nVal) => {
  if (!tempArr.value.length || (tempArr.value.length === numberConfig.value && tempArr.value.length <= allList.value.length)) {
    allList.value = [];
    leftList.value = [];
    rightList.value = [];
    boxHeight.value = [];
    mark.value = 0;
  }
  if (tempArr.value.length) {
    allList.value = tempArr.value;
    leftList.value = [];
    rightList.value = [];
    boxHeight.value = [];
    allList.value.forEach((v, i) => {
      if (allList.value.length < 3 || (allList.value.length <= 7 && allList.value.length - i > 1) || (allList.value.length > 7 && allList.value.length - i > 2)) {
        if (i % 2) rightList.value.push(v);
        else leftList.value.push(v);
      }
    });
    if (allList.value.length < 3) mark.value = allList.value.length + 1;
    else if (allList.value.length <= 7) mark.value = allList.value.length - 1;
    else mark.value = allList.value.length - 2;
    if (mark.value < allList.value.length) waterFall();
  }
}, { immediate: true, deep: true });

watch(mark, () => {
  const len = allList.value.length;
  if (mark.value < len && mark.value !== 0 && boxHeight.value.length) waterFall();
});

watch(() => props.dataConfig, () => { productslist(); });

onMounted(() => { productslist(); });

// methods
function observeVideo() {
  let observer = uni.createIntersectionObserver(proxy, { observeAll: true });
  observer.relativeToViewport().observe(".video", (res) => {
    if (res.intersectionRatio) uni.createVideoContext(res.id, proxy).play();
    else uni.createVideoContext(res.id, proxy).pause();
  });
}

function productslist() {
  if (Array.isArray(props.list) && props.list.length) {
    tempArr.value = props.list;
    return;
  }
  let data = { limit: numberConfig.value, order: goodsSort.value, sort: goodsSort.value };
  if (typeConfig.value == 1) {
    const goodsList = props.dataConfig.goodsList.list || [];
    const ids = goodsList.map((item) => item.id).filter(Boolean).join(",");
    if (ids) data.ids = ids;
    else { tempArr.value = []; return; }
  } else if (typeConfig.value == 3) {
    data.cate_ids = props.dataConfig.classList.classVal ? props.dataConfig.classList.classVal.join(",") : "";
  }
  getThemeProduct(data).then((res) => {
    tempArr.value = Array.isArray(res.data) ? res.data : res.data && res.data.list ? res.data.list : [];
  });
}

function goDetail(item) {
  goShopDetail(item, uid.value).then(() => {
    uni.navigateTo({ url: `/pages/goods/goods_details/index?id=${item.id}` });
  });
}

function getAttrs(id2) {
  getAttr(id2).then((res) => {
    uni.hideLoading();
    const data = res.data || {};
    const productAttr = (data.productAttr || []).map((item) => ({
      attrName: item.attrName,
      attrValues: Array.isArray(item.attrValues) ? item.attrValues : String(item.attrValues || "").split(","),
      id: item.id, isDel: item.isDel, productId: item.productId, type: item.type,
      optionList: item.optionList || [], isShowImage: item.isShowImage,
    }));
    attr.productAttr = productAttr;
    productValue.value = data.productValue || {};
    storeInfo.value = normalizeStoreInfo(data.storeInfo || data.productInfo || currentCartItem.value || {});
    attr.cartAttr = true;
    DefaultSelect();
  }).catch((err) => {
    uni.hideLoading();
    util.Tips({ title: getErrorMessage(err) });
  });
}

function addCartChange(item, index) {
  if (props.dataConfig.name === "goodList" || props.dataConfig.name === "goodRecommend") {
    goDetail(item);
    return;
  }
  if (bntConfig.value == 1) {
    if (isMultiSpec(item)) goCartDuo(item);
    else goCartDan(item, index);
  } else goDetail(item);
}

function getCartNum() {
  getCartCounts(true, "total").then((res) => {
    indexDataStore.setCartNum(res.data.count);
  });
}

function getErrorMessage(err) {
  if (!err) return "操作失败";
  if (typeof err === "string") return err;
  return err.msg || err.message || "操作失败";
}

function normalizeStoreInfo(item) {
  const minQty = item.min_qty || item.minQty || 1;
  return {
    ...item, id: item.id, image: item.image || "", price: item.price || 0, stock: item.stock || 0,
    unique: item.unique || item.attrValueId || item.id || "", storeName: item.storeName || item.store_name || "",
    store_name: item.store_name || item.storeName || "", unit_name: item.unit_name || item.unitName || "",
    min_qty: minQty, limit_num: item.limit_num || item.limitNum || 0,
  };
}

function isMultiSpec(item) {
  const specType = item.spec_type !== undefined ? item.spec_type : item.specType;
  return specType === true || Number(specType) === 1;
}

function getDefaultProductValue(pv) {
  const values = Object.values(pv || {});
  if (!values.length) return null;
  return values.find((item) => item && item.stock > 0 && item.isShow !== false && item.isDefault) ||
    values.find((item) => item && item.stock > 0 && item.isShow !== false) || values[0];
}

function getProductAttrUnique(item, pv) {
  const sku = getDefaultProductValue(pv);
  return (sku && (sku.id || sku.unique)) || item.unique || item.attrValueId || "";
}

function DefaultSelect() {
  let productAttr = attr.productAttr || [];
  let value = [];
  const sortedArray = Object.entries(productValue.value || {})
    .sort(([, a], [, b]) => (Number(a.id) || 0) - (Number(b.id) || 0))
    .map(([key, val]) => ({ key, ...val }));
  for (let i = 0; i < sortedArray.length; i++) {
    const attrItem = sortedArray[i];
    if (attrItem.stock > 0 && attrItem.isShow !== false) {
      if (!value.length) value = productAttr.length ? attrItem.key.split(",") : [];
      if (attrItem.isDefault) { value = productAttr.length ? attrItem.key.split(",") : []; break; }
    }
  }
  for (let i = 0; i < productAttr.length; i++) productAttr[i].index = value[i];
  let productSelect = productValue.value[value.join(",")];
  attr.productSelect.storeName = storeName.value;
  if (productSelect && productAttr.length) {
    attr.productSelect.image = productSelect.image;
    attr.productSelect.price = productSelect.price;
    attr.productSelect.stock = productSelect.stock;
    attr.productSelect.unique = productSelect.id || productSelect.unique;
    attr.productSelect.cart_num = 1;
    attr.productSelect.isShow = productSelect.isShow !== false;
    attrValue.value = value.join(",");
  } else if (!productSelect && productAttr.length) {
    attr.productSelect.image = storeInfo.value.image;
    attr.productSelect.price = storeInfo.value.price;
    attr.productSelect.stock = 0;
    attr.productSelect.unique = "";
    attr.productSelect.cart_num = 0;
    attrValue.value = "";
  } else {
    attr.productSelect.image = storeInfo.value.image;
    attr.productSelect.price = storeInfo.value.price;
    attr.productSelect.stock = storeInfo.value.stock;
    attr.productSelect.unique = storeInfo.value.unique || storeInfo.value.id || "";
    attr.productSelect.cart_num = 1;
    attrValue.value = "";
  }
}

// SKU 弹窗关闭回调
function onMyEvent() {
  attr.cartAttr = false;
}

function ChangeAttr(res) {
  let productSelect = productValue.value[res];
  if (productSelect) {
    attr.productSelect.image = productSelect.image;
    attr.productSelect.price = productSelect.price;
    attr.productSelect.stock = productSelect.stock;
    attr.productSelect.unique = productSelect.id || productSelect.unique;
    attr.productSelect.cart_num = 1;
    attr.productSelect.isShow = productSelect.isShow !== false;
    if (!attr.productSelect.isShow) {
      attr.productSelect.stock = 0;
      util.Tips({ title: "请重新选择其它规格" });
    }
    attrValue.value = res;
  } else {
    attr.productSelect.price = "暂无报价";
    attr.productSelect.stock = 0;
    attr.productSelect.unique = 0;
    attr.productSelect.cart_num = 0;
    attrValue.value = "";
  }
}

function attrVal(val) {
  attr.productAttr[val.indexw].index = attr.productAttr[val.indexw].attrValues[val.indexn];
}

function iptCartNum(e) {
  attr.productSelect.cart_num = e || 1;
}

function ChangeCartNumDuo(changeValue) {
  let productSelect = productValue.value[attrValue.value];
  if (productSelect === undefined && !attr.productAttr.length) productSelect = attr.productSelect;
  if (productSelect === undefined) return;
  let stock = productSelect.stock || 0;
  let num = attr.productSelect;
  if (changeValue) {
    num.cart_num++;
    if (num.cart_num > stock) {
      attr.productSelect.cart_num = stock;
    }
  } else {
    num.cart_num--;
    if (num.cart_num < 1) {
      attr.productSelect.cart_num = 1;
    }
  }
}

function goCatNum() { goCat(1); }

function goCat(num) {
  let productSelect = productValue.value[attrValue.value];
  if (attr.productAttr.length && productSelect === undefined) {
    return util.Tips({ title: "产品库存不足，请选择其它" });
  }
  if (attr.productSelect.stock <= 0) {
    return util.Tips({ title: "请选择其它规格" });
  }
  if (num === 1) {
    let q = {
      productId: parseFloat(id.value),
      cartNum: parseFloat(attr.productSelect.cart_num || 1),
      isNew: false,
      productAttrUnique: attr.productSelect.unique || id.value,
    };
    postCartAdd(q)
      .then((res) => {
        attr.cartAttr = false;
        util.Tips({
          title: res.msg || res.message || "添加购物车成功",
          success: () => { getCartNum(); },
        });
      })
      .catch((err) => {
        return util.Tips({ title: getErrorMessage(err) });
      });
  }
}

// 内联自原 skuSelect mixin 的方法
function updateFun(e, num) {
  if (e.cartNum) {
    tempArr.value.forEach((item) => {
      if (item.id == e.id) item.cart_num = e.cartNum;
    });
    if (num) getCartNum();
  }
}

function goCartDuo(item) {
  if (!isLogin.value) { toLogin(); return; }
  if (item.cart_button == 0) { goDetail(item); return; }
  uni.showLoading({ title: "加载中" });
  currentCartItem.value = item;
  storeName.value = item.storeName || item.store_name || "";
  id.value = item.id;
  getAttrs(item.id);
}

function goCartDan(item, index) {
  if (!isLogin.value) { toLogin(); return; }
  if (item.cart_button == 0) { goDetail(item); return; }
  if (Number(item.stock) <= 0) return util.Tips({ title: "产品库存不足" });
  uni.showLoading({ title: "加载中" });
  getAttr(item.id)
    .then((attrRes) => {
      uni.hideLoading();
      const productAttrUnique = getProductAttrUnique(item, attrRes.data && attrRes.data.productValue);
      if (!productAttrUnique) return Promise.reject("未获取到商品规格信息");
      return postCartAdd({
        productId: parseFloat(item.id), cartNum: 1, isNew: false, productAttrUnique,
      });
    })
    .then((res) => {
      tempArr.value[index].cart_num = Number(tempArr.value[index].cart_num || 0) + 1;
      util.Tips({
        title: res.msg || res.message || "添加购物车成功",
        success: () => { getCartNum(); },
      });
    })
    .catch((err) => {
      uni.hideLoading();
      return util.Tips({ title: getErrorMessage(err) });
    });
}

function ChangeCartList(changeValue, index) {
  let list = tempArr.value;
  let num = list[index];
  let stock = list[index].stock || list[index].trueStock || 999;
  if (changeValue) {
    num.cart_num++;
    if (num.cart_num > stock) num.cart_num = stock;
  } else {
    num.cart_num--;
    if (num.cart_num <= 0) {
      list.splice(index, 1);
    }
  }
  getCartNum();
}

// 瀑布流排序
function waterFall() {
  const i = mark.value;
  if (i == 0) {
    leftList.value.push(allList.value[i]);
    getViewHeight(0);
  } else if (i == 1) {
    rightList.value.push(allList.value[i]);
    getViewHeight(1);
  } else {
    if (!boxHeight.value.length) {
      rightList.value.length < leftList.value.length
        ? rightList.value.push(allList.value[i])
        : leftList.value.push(allList.value[i]);
    } else {
      const leftOrRight = boxHeight.value[0] > boxHeight.value[1] ? 1 : 0;
      if (leftOrRight) rightList.value.push(allList.value[i]);
      else leftList.value.push(allList.value[i]);
    }
    getViewHeight();
  }
}

function getViewHeight() {
  nextTick(() => {
    setTimeout(() => {
      uni.createSelectorQuery().in(proxy).select("#right").boundingClientRect((res) => {
        if (res) boxHeight.value[1] = res.height;
        uni.createSelectorQuery().in(proxy).select("#left").boundingClientRect((res2) => {
          if (res2) boxHeight.value[0] = res2.height;
          mark.value = mark.value + 1;
        }).exec();
      }).exec();
    }, 100);
  });
}
</script>

<style lang="scss">
$page-padding: 10px;
$grid-gap: 10px;
.wf-page {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  .item-box {
    width: 48.5%;
    margin-bottom: 20rpx;
  }
}
.wf-item {
  width: 100%;
  margin-bottom: 20rpx;
}
.wf-page1 .wf-item {
  margin-top: 20rpx;
  border-radius: 20rpx;
  padding-bottom: 0;
}
.item ~ .item {
  margin-top: 20rpx;
}
.info_box {
  padding: 16rpx 20rpx;
  border-radius: 0 0 20rpx 20rpx;
}
.cart-btn {
  // background:rgba(255,255,255,0.9);
  width: 100%;
  height: 100%;
}
.goods-scroll-view {
  white-space: nowrap;
}
.goods-scroll-row {
  display: inline-flex;
  flex-wrap: nowrap;
  align-items: flex-start;
  min-width: 100%;
}
.goods-scroll-item {
  flex: 0 0 200rpx;
  width: 200rpx;
  margin-right: 20rpx;
  white-space: normal;
}
</style>
