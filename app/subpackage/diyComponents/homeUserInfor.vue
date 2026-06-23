<template>
  <view class="member-wrap">
    <common-wrapper :config="configData">
      <view class="member-card" :class="'style' + (styleConfig + 1)">
        <template v-if="styleConfig == 0">
          <view
            class="card-header acea-row row-between-wrapper"
            @click="goUserInfo"
          >
            <view class="user-info acea-row row-middle">
              <view class="avatar">
                <image v-if="avatarUrl" :src="avatarUrl" mode="aspectFill" />
                <view class="empty-box" v-else>
                  <image src="/static/images/shan.png" mode="aspectFill" />
                </view>
              </view>
              <view class="text">
                <view class="name line1" :style="[nameTextStyle]">{{
                  userName
                }}</view>
                <view class="level line1" :style="[numTextStyle]">{{
                  userSubText
                }}</view>
              </view>
            </view>
            <view class="menu-entry" v-if="menuList.length">
              <view
                class="item"
                v-for="(item, index) in menuList"
                :key="index"
                @click.stop="goLink(getInfo(item, 1))"
              >
                <template v-if="menuStyle == 0">
                  <image
                    v-if="item.img"
                    :src="item.img"
                    mode="aspectFill"
                    class="menu-img"
                  />
                </template>
                <template v-else>
                  <text
                    v-if="item.icon"
                    class="iconfont"
                    :class="item.icon"
                    :style="[menuIconStyle]"
                  ></text>
                </template>
              </view>
            </view>
          </view>
        </template>

        <template v-if="styleConfig == 1">
          <view
            class="card-header acea-row row-between-wrapper"
            @click="goUserInfo"
          >
            <view class="user-info style-2 acea-row row-between-wrapper">
              <view class="text">
                <view class="name line1" :style="[nameTextStyle]">{{
                  userName
                }}</view>
                <view class="level line1" :style="[numTextStyle]">{{
                  userSubTextPlain
                }}</view>
              </view>
              <view class="avatar">
                <image v-if="avatarUrl" :src="avatarUrl" mode="aspectFill" />
                <view class="empty-box" v-else>
                  <image src="/static/images/shan.png" mode="aspectFill" />
                </view>
              </view>
            </view>
          </view>
        </template>

        <template v-if="styleConfig == 2">
          <view
            class="card-header acea-row row-center-wrapper"
            @click="goUserInfo"
          >
            <view class="user-info style-3">
              <view class="avatar">
                <image v-if="avatarUrl" :src="avatarUrl" mode="aspectFill" />
                <view class="empty-box" v-else>
                  <image src="/static/images/shan.png" mode="aspectFill" />
                </view>
              </view>
              <view class="text">
                <view class="name line1" :style="[nameTextStyle]">{{
                  userName
                }}</view>
                <view class="level line1" :style="[numTextStyle]">{{
                  userSubText
                }}</view>
              </view>
            </view>
            <view class="menu-entry" v-if="menuList.length">
              <view
                class="item"
                v-for="(item, index) in menuList"
                :key="index"
                @click.stop="goLink(getInfo(item, 1))"
              >
                <template v-if="menuStyle == 0">
                  <image
                    v-if="item.img"
                    :src="item.img"
                    mode="aspectFill"
                    class="menu-img"
                  />
                </template>
                <template v-else>
                  <text
                    v-if="item.icon"
                    class="iconfont"
                    :class="item.icon"
                    :style="[menuIconStyle]"
                  ></text>
                </template>
              </view>
            </view>
          </view>
        </template>

        <template v-if="styleConfig == 3">
          <view class="style-4-container acea-row row-between-wrapper">
            <view class="left-card" :style="[moduleCardStyle]">
              <view class="user-info acea-row row-middle" @click="goUserInfo">
                <view class="avatar">
                  <image v-if="avatarUrl" :src="avatarUrl" mode="aspectFill" />
                  <view class="empty-box" v-else>
                    <image src="/static/images/shan.png" mode="aspectFill" />
                  </view>
                </view>
                <view class="text">
                  <view class="name line1" :style="[nameTextStyle]">{{
                    userName
                  }}</view>
                  <view class="level line1" :style="[numTextStyle]">{{
                    userSubTextPlain
                  }}</view>
                </view>
              </view>
              <view
                class="stats-row card-data acea-row row-around"
                v-if="
                  (assetMode == 0 && checkType.length) ||
                  (assetMode == 1 && assetList.length)
                "
              >
                <template v-if="assetMode == 0">
                  <view
                    class="item"
                    v-for="(item, index) in dataList"
                    :key="index"
                    @click.stop="handleDataNavigation(item)"
                    :class="{
                      'style-vert': dataStyle == 0,
                      'style-horiz': dataStyle == 1,
                      'style-vert-2': dataStyle == 2,
                    }"
                  >
                    <template v-if="dataStyle == 0">
                      <view
                        class="num line1"
                        :style="{ color: dataNumColor }"
                        >{{ item.val }}</view
                      >
                      <view
                        class="label line1"
                        :style="{ color: dataTitleColor }"
                        >{{ item.name }}</view
                      >
                    </template>
                    <template v-else>
                      <view
                        class="label line1"
                        :style="{ color: dataTitleColor }"
                        >{{ item.name }}</view
                      >
                      <view
                        class="num line1"
                        :style="{ color: dataNumColor }"
                        >{{ item.val }}</view
                      >
                    </template>
                  </view>
                </template>
                <template v-if="assetMode == 1">
                  <view
                    class="item"
                    v-for="(item, index) in assetList"
                    :key="index"
                    @click.stop="goLink(getInfo(item, 1))"
                  >
                    <view class="icon-box">
                      <image
                        v-if="assetStyle == 0 && item.img"
                        :src="item.img"
                        mode="aspectFill"
                        class="asset-img"
                      />
                      <text
                        v-else-if="assetStyle == 1 && item.icon"
                        class="iconfont"
                        :class="item.icon"
                        :style="[assetIconStyle]"
                      ></text>
                      <text
                        v-else
                        class="iconfont icon-tuiguang"
                        :style="[assetIconStyle]"
                      ></text>
                    </view>
                    <view
                      class="label line1"
                      :style="{ color: moduleTitleColor }"
                      >{{ getInfo(item, 0) }}</view
                    >
                  </view>
                </template>
              </view>
            </view>
            <view
              class="right-card"
              :style="[moduleCardStyle]"
              v-if="rightEntryList.length"
              @click="goLink(getInfo(rightEntryList[0], 2))"
            >
              <view class="entry-content">
                <view
                  class="title line1"
                  :style="{ color: moduleTitleColor }"
                  >{{ getInfo(rightEntryList[0], 0) }}</view
                >
                <view class="subtitle line1">
                  <text class="sub-text">{{
                    getInfo(rightEntryList[0], 1)
                  }}</text>
                  <text class="iconfont icon-you2"></text>
                </view>
                <view class="img-box">
                  <image
                    v-if="rightEntryList[0].img"
                    :src="rightEntryList[0].img"
                    mode="aspectFill"
                  />
                </view>
              </view>
            </view>
          </view>
        </template>

        <template v-if="styleConfig == 4">
          <view class="style-5-container" :style="[cardStyle]">
            <view
              class="header acea-row row-between-wrapper"
              @click="goUserInfo"
            >
              <view class="left">
                <view class="avatar">
                  <image v-if="avatarUrl" :src="avatarUrl" mode="aspectFill" />
                  <view class="empty-box" v-else>
                    <image src="/static/images/shan.png" mode="aspectFill" />
                  </view>
                </view>
                <view class="text">
                  <view class="name line1" :style="[nameTextStyle]">{{
                    userName
                  }}</view>
                  <view class="level line1" :style="[numTextStyle]">{{
                    userSubTextPlain
                  }}</view>
                </view>
              </view>
              <view class="menu-entry" v-if="menuList.length">
                <view
                  class="item"
                  v-for="(item, index) in menuList"
                  :key="index"
                  @click.stop="goLink(getInfo(item, 1))"
                >
                  <template v-if="menuStyle == 0">
                    <image
                      v-if="item.img"
                      :src="item.img"
                      mode="aspectFill"
                      class="menu-img"
                    />
                  </template>
                  <template v-else>
                    <text
                      v-if="item.icon"
                      class="iconfont"
                      :class="item.icon"
                      :style="[menuIconStyle]"
                    ></text>
                  </template>
                </view>
              </view>
            </view>
            <view
              class="card-data acea-row row-around"
              v-if="
                (assetMode == 0 && checkType.length) ||
                (assetMode == 1 && assetList.length)
              "
            >
              <template v-if="assetMode == 0">
                <view
                  class="item"
                  v-for="(item, index) in dataList"
                  :key="index"
                  @click.stop="handleDataNavigation(item)"
                  :class="{
                    'style-vert': dataStyle == 0,
                    'style-horiz': dataStyle == 1,
                    'style-vert-2': dataStyle == 2,
                  }"
                >
                  <template v-if="dataStyle == 0">
                    <view class="num line1" :style="{ color: dataNumColor }">{{
                      item.val
                    }}</view>
                    <view
                      class="label line1"
                      :style="{ color: dataTitleColor }"
                      >{{ item.name }}</view
                    >
                  </template>
                  <template v-else>
                    <view
                      class="label line1"
                      :style="{ color: dataTitleColor }"
                      >{{ item.name }}</view
                    >
                    <view class="num line1" :style="{ color: dataNumColor }">{{
                      item.val
                    }}</view>
                  </template>
                </view>
              </template>
              <template v-else>
                <view
                  class="item style-icon"
                  v-for="(item, index) in assetList"
                  :key="index"
                  @click.stop="goLink(getInfo(item, 1))"
                >
                  <view class="icon-box">
                    <image
                      v-if="assetStyle == 0 && item.img"
                      :src="item.img"
                      mode="aspectFill"
                      class="img-icon"
                    />
                    <text
                      v-else-if="assetStyle == 1 && item.icon"
                      class="iconfont"
                      :class="item.icon"
                      :style="assetIconStyle"
                    ></text>
                  </view>
                  <view class="label line1" :style="assetTextStyle">{{
                    getInfo(item, 0)
                  }}</view>
                </view>
              </template>
            </view>
          </view>
        </template>

        <template
          v-if="
            ((assetMode == 0 && checkType.length) ||
              (assetMode == 1 && assetList.length)) &&
            styleConfig != 3 &&
            styleConfig != 4
          "
        >
          <view class="card-data acea-row row-around">
            <template v-if="assetMode == 0">
              <view
                class="item"
                v-for="(item, index) in dataList"
                :key="index"
                @click.stop="handleDataNavigation(item)"
                :class="{
                  'style-vert': dataStyle == 0,
                  'style-horiz': dataStyle == 1,
                  'style-vert-2': dataStyle == 2,
                }"
              >
                <template v-if="dataStyle == 0">
                  <view class="num line1" :style="{ color: dataNumColor }">{{
                    item.val
                  }}</view>
                  <view
                    class="label line1"
                    :style="{ color: dataTitleColor }"
                    >{{ item.name }}</view
                  >
                </template>
                <template v-else>
                  <view
                    class="label line1"
                    :style="{ color: dataTitleColor }"
                    >{{ item.name }}</view
                  >
                  <view class="num line1" :style="{ color: dataNumColor }">{{
                    item.val
                  }}</view>
                </template>
              </view>
            </template>
            <template v-else>
              <view
                class="item style-icon"
                v-for="(item, index) in assetList"
                :key="index"
                @click.stop="goLink(getInfo(item, 1))"
              >
                <view class="icon-box">
                  <image
                    v-if="assetStyle == 0 && item.img"
                    :src="item.img"
                    mode="aspectFill"
                    class="img-icon"
                  />
                  <text
                    v-else-if="assetStyle == 1 && item.icon"
                    class="iconfont"
                    :class="item.icon"
                    :style="assetIconStyle"
                  ></text>
                </view>
                <view class="label line1" :style="assetTextStyle">{{
                  getInfo(item, 0)
                }}</view>
              </view>
            </template>
          </view>
        </template>

        <template v-if="memberStyleConfig == 0 && memberList.length">
          <view class="member-style-1" :style="[cardStyle, memberTopStyle]">
            <view
              class="item"
              v-for="(item, index) in memberList"
              :key="index"
              @click="goLink(getInfo(item, 2))"
            >
              <view class="text-box">
                <view class="title line1">{{ getInfo(item, 0) }}</view>
                <view class="subtitle line1"
                  >{{ getInfo(item, 1) }}
                  <text class="iconfont icon-you2"></text
                ></view>
              </view>
              <view class="img-box">
                <image v-if="item.img" :src="item.img" mode="aspectFill" />
              </view>
              <view class="line" v-if="index < memberList.length - 1"></view>
            </view>
          </view>
        </template>

        <template v-if="memberStyleConfig == 1">
          <view
            class="member-style-2"
            :style="[cardStyle, memberTopStyle]"
            @click="goLink(ms2ButtonLink)"
          >
            <view class="top-row">
              <view class="left-info">
                <view class="title-area">
                  <image
                    v-if="ms2TitleType == 1 && ms2TitleImage"
                    :src="ms2TitleImage"
                    mode="aspectFill"
                    class="title-img"
                  />
                  <view
                    v-else
                    class="title-text line1"
                    :style="{ color: ms2TitleColor }"
                    >{{ ms2TitleText }}</view
                  >
                </view>
                <view
                  class="intro-text line1"
                  :style="{ color: ms2IntroColor }"
                  >{{ ms2IntroText }}</view
                >
              </view>
              <view class="right-rights">
                <view
                  class="right-item"
                  v-for="(item, index) in ms2RightsList"
                  :key="index"
                >
                  <view class="icon-wrap">
                    <image v-if="item.img" :src="item.img" mode="aspectFill" />
                    <text
                      v-else-if="item.icon"
                      class="iconfont"
                      :class="item.icon"
                      :style="{ color: ms2RightsColor }"
                    ></text>
                  </view>
                  <view class="text line1" :style="{ color: ms2RightsColor }">{{
                    getInfo(item, 0)
                  }}</view>
                </view>
                <text
                  class="iconfont icon-you2"
                  :style="{ color: ms2RightsColor }"
                ></text>
              </view>
            </view>
            <view class="divider"></view>
            <view class="bottom-row">
              <view class="explain-list">
                <view class="icons" v-if="ms2ExplainIcons">
                  <image :src="ms2ExplainIcons" mode="aspectFill" />
                </view>
                <view
                  class="explain-text line1"
                  :style="{ color: ms2ExplainColor }"
                  >{{ ms2ExplainText }}</view
                >
              </view>
              <view
                class="action-btn line1"
                :style="{ color: ms2ButtonColor, background: ms2ButtonBgColor }"
                >{{ ms2ButtonText }}</view
              >
            </view>
          </view>
        </template>

        <template v-if="memberStyleConfig == 2">
          <view class="member-style-3" :style="[cardStyle, memberTopStyle]">
            <view class="content-wrapper" :style="[ms3ContainerStyle]">
              <view class="desc line1" :style="{ color: ms3TitleColor }">{{
                ms3TitleText
              }}</view>
              <view
                class="btn line1"
                :style="{ color: ms3ButtonColor, borderColor: ms3ButtonColor }"
                @click="goLink('/pages/annex/vip_paid/index')"
                >{{ ms3ButtonText }}</view
              >
            </view>
          </view>
        </template>

        <template v-if="memberStyleConfig == 3">
          <view class="member-style-4" :style="[cardStyle, memberTopStyle]">
            <view class="left-box">
              <view class="label">可提现(元)</view>
              <view class="value">{{ withdrawAmount }}</view>
            </view>
            <view
              class="right-box"
              @click="goLink('/pages/users/user_cash/index')"
              >立即提现</view
            >
          </view>
        </template>
      </view>
    </common-wrapper>
  </view>
</template>

<script>
import commonWrapper from "./commonWrapper.vue";
import { mapGetters } from "vuex";

export default {
  components: { commonWrapper },
  name: "homeMember",
  props: {
    dataConfig: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    ...mapGetters(["userInfo", "isLogin"]),
    configData() {
      return {
        ...this.dataConfig,
        paddingConfig: this.dataConfig.paddingConfig || {
          isAll: false,
          valList: [
            {
              val: this.dataConfig.topConfig
                ? this.dataConfig.topConfig.val
                : 0,
            },
            {
              val: this.dataConfig.prConfig ? this.dataConfig.prConfig.val : 0,
            },
            {
              val: this.dataConfig.bottomConfig
                ? this.dataConfig.bottomConfig.val
                : 0,
            },
            {
              val: this.dataConfig.prConfig ? this.dataConfig.prConfig.val : 0,
            },
          ],
        },
        marginConfig: this.dataConfig.marginConfig || {
          isAll: false,
          valList: [
            {
              val: this.dataConfig.mbConfig ? this.dataConfig.mbConfig.val : 0,
            },
            { val: 0 },
            { val: 0 },
            { val: 0 },
          ],
        },
      };
    },
    styleConfig() {
      return (
        Number(
          this.dataConfig.styleConfig && this.dataConfig.styleConfig.tabVal,
        ) || 0
      );
    },
    memberStyleConfig() {
      return (
        Number(
          this.dataConfig.memberStyleConfig &&
            this.dataConfig.memberStyleConfig.tabVal,
        ) || 0
      );
    },
    menuStyle() {
      return (
        Number(
          this.dataConfig.menuConfig && this.dataConfig.menuConfig.listStyle,
        ) || 0
      );
    },
    userInfoConfig() {
      return (
        Number(
          this.dataConfig.userInfoConfig &&
            this.dataConfig.userInfoConfig.tabVal,
        ) || 0
      );
    },
    assetMode() {
      return (
        Number(this.dataConfig.assetMode && this.dataConfig.assetMode.tabVal) ||
        0
      );
    },
    dataStyle() {
      return (
        Number(this.dataConfig.dataStyle && this.dataConfig.dataStyle.tabVal) ||
        0
      );
    },
    checkType() {
      return (
        (this.dataConfig.checkboxInfo && this.dataConfig.checkboxInfo.type) ||
        []
      );
    },
    avatarUrl() {
      return this.userInfo.avatar || "";
    },
    menuList() {
      return (
        (this.dataConfig.menuConfig && this.dataConfig.menuConfig.list) || []
      );
    },
    assetStyle() {
      return this.dataConfig.assetConfig
        ? this.dataConfig.assetConfig.listStyle
        : 2;
    },
    assetList() {
      return (
        (this.dataConfig.assetConfig && this.dataConfig.assetConfig.list) || []
      );
    },
    memberList() {
      return (
        (this.dataConfig.memberConfig && this.dataConfig.memberConfig.list) ||
        []
      );
    },
    rightEntryList() {
      return (
        (this.dataConfig.rightEntryConfig &&
          this.dataConfig.rightEntryConfig.list) ||
        []
      );
    },
    moduleTitleColor() {
      return this.dataConfig.moduleTextColor &&
        this.dataConfig.moduleTextColor.color &&
        this.dataConfig.moduleTextColor.color[0]
        ? this.dataConfig.moduleTextColor.color[0].item
        : "#333";
    },
    moduleCardStyle() {
      if (this.styleConfig != 3) return {};
      let color1 =
        this.dataConfig.moduleBgColor &&
        this.dataConfig.moduleBgColor.color &&
        this.dataConfig.moduleBgColor.color[0]
          ? this.dataConfig.moduleBgColor.color[0].item
          : "#fff";
      let color2 =
        this.dataConfig.moduleBgColor &&
        this.dataConfig.moduleBgColor.color &&
        this.dataConfig.moduleBgColor.color[1]
          ? this.dataConfig.moduleBgColor.color[1].item
          : "#fff";
      return {
        background: `linear-gradient(90deg, ${color1} 0%, ${color2} 100%)`,
        borderRadius: this.radiusFromConfig(this.dataConfig.moduleRadius),
      };
    },
    memberTopStyle() {
      if (this.styleConfig == 3 || this.styleConfig == 4) {
        return { marginTop: "20rpx" };
      }
      return {};
    },
    cardStyle() {
      if (this.memberStyleConfig == 2) {
        let style = {
          borderRadius: this.radiusFromConfig(this.dataConfig.cardBgRadius),
        };
        const mode = this.dataConfig.ms3BgMode
          ? this.dataConfig.ms3BgMode.tabVal
          : 0;
        if (
          mode === 1 &&
          this.dataConfig.ms3BackgroundImage &&
          this.dataConfig.ms3BackgroundImage.url
        ) {
          style.backgroundImage = `url(${this.dataConfig.ms3BackgroundImage.url})`;
          style.backgroundRepeat = "no-repeat";
          style.backgroundSize = "100% 100%";
        } else {
          const c1 =
            this.dataConfig.cardBgColor &&
            this.dataConfig.cardBgColor.color &&
            this.dataConfig.cardBgColor.color[0]
              ? this.dataConfig.cardBgColor.color[0].item
              : "#fff";
          const c2 =
            this.dataConfig.cardBgColor &&
            this.dataConfig.cardBgColor.color &&
            this.dataConfig.cardBgColor.color[1]
              ? this.dataConfig.cardBgColor.color[1].item
              : c1;
          style.background = `linear-gradient(90deg, ${c1} 0%, ${c2} 100%)`;
        }
        return style;
      }
      if (this.memberStyleConfig == 3) {
        let style = {
          borderRadius: this.radiusFromConfig(this.dataConfig.cardBgRadius),
        };
        const mode = this.dataConfig.ms4BgMode
          ? this.dataConfig.ms4BgMode.tabVal
          : 0;
        if (
          mode === 1 &&
          this.dataConfig.ms4BackgroundImage &&
          this.dataConfig.ms4BackgroundImage.url
        ) {
          style.backgroundImage = `url(${this.dataConfig.ms4BackgroundImage.url})`;
          style.backgroundRepeat = "no-repeat";
          style.backgroundSize = "100% 100%";
        } else {
          const c1 =
            this.dataConfig.cardBgColor &&
            this.dataConfig.cardBgColor.color &&
            this.dataConfig.cardBgColor.color[0]
              ? this.dataConfig.cardBgColor.color[0].item
              : "#fff";
          const c2 =
            this.dataConfig.cardBgColor &&
            this.dataConfig.cardBgColor.color &&
            this.dataConfig.cardBgColor.color[1]
              ? this.dataConfig.cardBgColor.color[1].item
              : c1;
          style.background = `linear-gradient(90deg, ${c1} 0%, ${c2} 100%)`;
        }
        return style;
      }
      let color1 =
        this.dataConfig.cardBgColor &&
        this.dataConfig.cardBgColor.color &&
        this.dataConfig.cardBgColor.color[0]
          ? this.dataConfig.cardBgColor.color[0].item
          : "#fff";
      let color2 =
        this.dataConfig.cardBgColor &&
        this.dataConfig.cardBgColor.color &&
        this.dataConfig.cardBgColor.color[1]
          ? this.dataConfig.cardBgColor.color[1].item
          : "#fff";
      return {
        background: `linear-gradient(90deg, ${color1} 0%, ${color2} 100%)`,
        borderRadius: this.radiusFromConfig(this.dataConfig.cardBgRadius),
      };
    },
    nameTextStyle() {
      let color =
        this.dataConfig.nameColor &&
        this.dataConfig.nameColor.color &&
        this.dataConfig.nameColor.color[0]
          ? this.dataConfig.nameColor.color[0].item
          : "#333333";
      let size =
        this.dataConfig.nameSize && this.dataConfig.nameSize.val
          ? this.dataConfig.nameSize.val
          : 16;
      return { color: color, fontSize: size * 2 + "rpx" };
    },
    numTextStyle() {
      let color =
        this.dataConfig.numColor &&
        this.dataConfig.numColor.color &&
        this.dataConfig.numColor.color[0]
          ? this.dataConfig.numColor.color[0].item
          : "#333333";
      let size =
        this.dataConfig.numSize && this.dataConfig.numSize.val
          ? this.dataConfig.numSize.val
          : 14;
      return { color: color, fontSize: size * 2 + "rpx" };
    },
    dataTitleColor() {
      return this.dataConfig.dataTitleColor &&
        this.dataConfig.dataTitleColor.color &&
        this.dataConfig.dataTitleColor.color[0]
        ? this.dataConfig.dataTitleColor.color[0].item
        : "#333333";
    },
    dataNumColor() {
      return this.dataConfig.dataNumColor &&
        this.dataConfig.dataNumColor.color &&
        this.dataConfig.dataNumColor.color[0]
        ? this.dataConfig.dataNumColor.color[0].item
        : "#333333";
    },
    assetIconStyle() {
      let color =
        this.dataConfig.assetIconColor &&
        this.dataConfig.assetIconColor.color &&
        this.dataConfig.assetIconColor.color[0]
          ? this.dataConfig.assetIconColor.color[0].item
          : "#ff9900";
      let size =
        this.dataConfig.assetIconSize && this.dataConfig.assetIconSize.val
          ? this.dataConfig.assetIconSize.val
          : 20;
      return { color: color, fontSize: size * 2 + "rpx" };
    },
    assetTextStyle() {
      let color =
        this.dataConfig.assetTextColor &&
        this.dataConfig.assetTextColor.color &&
        this.dataConfig.assetTextColor.color[0]
          ? this.dataConfig.assetTextColor.color[0].item
          : "#333";
      let size =
        this.dataConfig.assetTextSize && this.dataConfig.assetTextSize.val
          ? this.dataConfig.assetTextSize.val
          : 12;
      return { color: color, fontSize: size * 2 + "rpx" };
    },
    menuIconStyle() {
      let iconStyle =
        this.dataConfig.iconStyleConfig || this.dataConfig.iconStyle || {};
      let color =
        iconStyle.color && iconStyle.color.color && iconStyle.color.color[0]
          ? iconStyle.color.color[0].item
          : "#333";
      let size = iconStyle.size && iconStyle.size.val ? iconStyle.size.val : 20;
      let padding =
        iconStyle.padding && iconStyle.padding.val ? iconStyle.padding.val : 0;
      let rotate =
        iconStyle.rotate && iconStyle.rotate.val ? iconStyle.rotate.val : 0;
      return {
        color: color,
        fontSize: size * 2 + "rpx",
        padding: padding * 2 + "rpx",
        transform: `rotate(${rotate}deg)`,
        display: "inline-block",
      };
    },
    assetIconStyle() {
      return { fontSize: "48rpx", color: "#ff9900" };
    },
    userName() {
      if (!this.isLogin) return "请点击登录";
      return (this.userInfo && this.userInfo.nickname) || "";
    },
    userSubText() {
      if (!this.isLogin) return "";
      if (this.userInfoConfig == 0)
        return (this.userInfo && this.userInfo.phone) || "";
      let uid =
        (this.userInfo && (this.userInfo.uid || this.userInfo.id)) || "";
      return uid ? `ID: ${uid}` : "";
    },
    userSubTextPlain() {
      if (!this.isLogin) return "";
      if (this.userInfoConfig == 0)
        return (this.userInfo && this.userInfo.phone) || "";
      return (this.userInfo && (this.userInfo.uid || this.userInfo.id)) || "";
    },
    ms2TitleType() {
      return (
        Number(
          this.dataConfig.ms2TitleType && this.dataConfig.ms2TitleType.tabVal,
        ) || 0
      );
    },
    ms2TitleText() {
      return (
        (this.dataConfig.ms2TitleText && this.dataConfig.ms2TitleText.value) ||
        ""
      );
    },
    ms2TitleColor() {
      return this.dataConfig.ms2TitleColor &&
        this.dataConfig.ms2TitleColor.color &&
        this.dataConfig.ms2TitleColor.color[0]
        ? this.dataConfig.ms2TitleColor.color[0].item
        : "";
    },
    ms2TitleImage() {
      return (
        (this.dataConfig.ms2TitleImage && this.dataConfig.ms2TitleImage.url) ||
        ""
      );
    },
    ms2IntroText() {
      return (
        (this.dataConfig.ms2IntroText && this.dataConfig.ms2IntroText.value) ||
        ""
      );
    },
    ms2IntroColor() {
      return this.dataConfig.ms2IntroColor &&
        this.dataConfig.ms2IntroColor.color &&
        this.dataConfig.ms2IntroColor.color[0]
        ? this.dataConfig.ms2IntroColor.color[0].item
        : "";
    },
    ms2RightsList() {
      return (
        (this.dataConfig.ms2RightsList && this.dataConfig.ms2RightsList.list) ||
        []
      );
    },
    ms2RightsColor() {
      return this.dataConfig.ms2RightsColor &&
        this.dataConfig.ms2RightsColor.color &&
        this.dataConfig.ms2RightsColor.color[0]
        ? this.dataConfig.ms2RightsColor.color[0].item
        : "";
    },
    ms2ExplainIcons() {
      return (
        (this.dataConfig.ms2ExplainIcons &&
          this.dataConfig.ms2ExplainIcons.url) ||
        ""
      );
    },
    ms2ExplainText() {
      return (
        (this.dataConfig.ms2ExplainText &&
          this.dataConfig.ms2ExplainText.value) ||
        ""
      );
    },
    ms2ExplainColor() {
      return this.dataConfig.ms2ExplainColor &&
        this.dataConfig.ms2ExplainColor.color &&
        this.dataConfig.ms2ExplainColor.color[0]
        ? this.dataConfig.ms2ExplainColor.color[0].item
        : "";
    },
    ms2ButtonText() {
      return (
        (this.dataConfig.ms2ButtonText &&
          this.dataConfig.ms2ButtonText.value) ||
        ""
      );
    },
    ms2ButtonLink() {
      return (
        (this.dataConfig.ms2ButtonLink &&
          this.dataConfig.ms2ButtonLink.value) ||
        ""
      );
    },
    ms2ButtonColor() {
      return this.dataConfig.ms2ButtonColor &&
        this.dataConfig.ms2ButtonColor.color &&
        this.dataConfig.ms2ButtonColor.color[0]
        ? this.dataConfig.ms2ButtonColor.color[0].item
        : "";
    },
    ms2ButtonBgColor() {
      return this.dataConfig.ms2ButtonBgColor &&
        this.dataConfig.ms2ButtonBgColor.color &&
        this.dataConfig.ms2ButtonBgColor.color[0]
        ? this.dataConfig.ms2ButtonBgColor.color[0].item
        : "";
    },
    ms3TitleText() {
      return (
        (this.dataConfig.ms3TitleText && this.dataConfig.ms3TitleText.value) ||
        ""
      );
    },
    ms3TitleColor() {
      return this.dataConfig.ms3TitleColor &&
        this.dataConfig.ms3TitleColor.color &&
        this.dataConfig.ms3TitleColor.color[0]
        ? this.dataConfig.ms3TitleColor.color[0].item
        : "";
    },
    ms3ButtonText() {
      return (
        (this.dataConfig.ms3ButtonText &&
          this.dataConfig.ms3ButtonText.value) ||
        ""
      );
    },
    ms3ButtonColor() {
      return this.dataConfig.ms3ButtonColor &&
        this.dataConfig.ms3ButtonColor.color &&
        this.dataConfig.ms3ButtonColor.color[0]
        ? this.dataConfig.ms3ButtonColor.color[0].item
        : "";
    },
    ms3ContainerStyle() {
      let pad =
        this.dataConfig.ms3PaddingConfig &&
        this.dataConfig.ms3PaddingConfig.valList
          ? this.dataConfig.ms3PaddingConfig.valList
          : [{ val: 0 }, { val: 0 }, { val: 0 }, { val: 0 }];
      return {
        padding: `${pad[0].val * 2}rpx ${pad[1].val * 2}rpx ${
          pad[2].val * 2
        }rpx ${pad[3].val * 2}rpx`,
      };
    },
    withdrawAmount() {
      if (!this.isLogin) return "0.00";
      let val =
        (this.userInfo &&
          (this.userInfo.brokerage_price ||
            this.userInfo.brokeragePrice ||
            this.userInfo.brokerage)) ||
        0;
      return val;
    },
    dataList() {
      let list = [
        { id: 1, name: "余额", key: "money" },
        { id: 3, name: "优惠券", key: "coupon" },
        { id: 2, name: "积分", key: "integral" },
        { id: 5, name: "收藏商品", key: "collection" },
        { id: 6, name: "浏览记录", key: "visit" },
        { id: 8, name: "推广佣金", key: "brokerage" },
        { id: 9, name: "推广人", key: "spreadCount" },
        { id: 10, name: "推广订单", key: "spreadOrderCount" },
      ];
      return list
        .filter((item) => this.checkType.indexOf(item.id) != -1)
        .map((item) => ({ ...item, val: this.getDataValue(item.key) }));
    },
  },
  methods: {
    radiusFromConfig(cfg) {
      if (!cfg) return "0rpx";
      let type = Number(cfg.type) || 0;
      let val = Number(cfg.val) || 0;
      let valList = cfg.valList || [];
      if (type && valList.length >= 4) {
        return `${valList[0].val * 2}rpx ${valList[1].val * 2}rpx ${
          valList[3].val * 2
        }rpx ${valList[2].val * 2}rpx`;
      }
      return val * 2 + "rpx";
    },
    getInfo(item, index) {
      if (!item || !item.info || !item.info[index]) return "";
      return item.info[index].value || "";
    },
    getDataValue(key) {
      if (!this.isLogin) return 0;
      let u = this.userInfo || {};
      if (key === "money") return u.now_money || u.nowMoney || 0;
      if (key === "coupon")
        return u.couponCount || u.coupon_num || u.couponNum || 0;
      if (key === "integral") return u.integral || 0;
      if (key === "collection") return u.collectCount || u.collect_count || 0;
      if (key === "visit") return u.visitCount || u.visit_num || 0;
      if (key === "brokerage")
        return u.brokerage_price || u.brokeragePrice || u.brokerage || 0;
      if (key === "spreadCount") return u.spread_count || u.spreadCount || 0;
      if (key === "spreadOrderCount")
        return u.order_count || u.spread_order_count || u.spreadOrderCount || 0;
      return 0;
    },
    goUserInfo() {
      if (!this.isLogin) {
        this.$emit("changeLogin");
        return;
      }
      uni.navigateTo({ url: "/pages/users/user_info/index" });
    },
    handleDataNavigation(item) {
      let url = "";
      switch (item.id) {
        case 1:
          url = "/pages/users/user_money/index";
          break;
        case 3:
          url = "/pages/users/user_coupon/index";
          break;
        case 2:
          url = "/pages/users/user_integral/index";
          break;
        case 5:
          url = "/pages/users/user_goods_collection/index";
          break;
        case 6:
          url = "/pages/users/visit_list/index";
          break;
        case 8:
          url = "/pages/users/user_spread_money/index?type=2";
          break;
        case 9:
          url = "/pages/users/promoter-list/index";
          break;
        case 10:
          url = "/pages/users/promoter-order/index";
          break;
      }
      if (url) {
        this.goLink(url);
      }
    },
    goLink(url) {
      if (!url) return;
      if (!this.isLogin) {
        this.$emit("changeLogin");
        return;
      }
      if (this.$util && this.$util.JumpPath) {
        this.$util.JumpPath(url);
        return;
      }
      uni.navigateTo({ url });
    },
  },
};
</script>

<style lang="scss" scoped>
.member-card {
  position: relative;
  overflow: hidden;
  .left-card {
    .label {
      font-size: 22rpx;
      color: rgba(255, 255, 255, 0.8);
      margin-top: 0;
    }
  }
}
.card-header {
  position: relative;
  padding: 28rpx 20rpx;
}
.user-info {
  .avatar {
    width: 90rpx;
    height: 90rpx;
    border-radius: 50%;
    margin-right: 20rpx;
    overflow: hidden;
    image {
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }
    .empty-box {
      width: 100%;
      height: 100%;
      background: #f3f9ff;
      border: 2rpx solid #eeeeee;
      display: flex;
      align-items: center;
      justify-content: center;
      image {
        width: 52rpx;
        height: 40rpx;
        border-radius: 0;
      }
    }
  }
  .text {
    flex: 1;
    .name {
      font-weight: 600;
    }
    .level {
      margin-top: 8rpx;
      opacity: 0.9;
    }
  }
  &.style-2 {
    width: 100%;
    padding: 0 20rpx;
    .avatar {
      margin-right: 0;
      margin-left: 20rpx;
    }
  }
  &.style-3 {
    display: flex;
    flex-direction: column;
    align-items: center;
    .avatar {
      margin-right: 0;
      margin-bottom: 16rpx;
    }
    .text {
      text-align: center;
    }
  }
}
.menu-entry {
  display: flex;
  align-items: center;
  position: absolute;
  right: 20rpx;
  top: 28rpx;
  z-index: 10;
  .item {
    margin-left: 20rpx;
  }
  .menu-img {
    width: 40rpx;
    height: 40rpx;
    display: block;
  }
}
.card-data {
  padding: 24rpx 0;
  flex-wrap: nowrap;
  .item {
    width: 100%;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .num {
    font-size: 32rpx;
    font-weight: 600;
  }
  .label {
    font-size: 24rpx;
    margin-top: 0;
  }
  .style-vert,
  .style-vert-2 {
    flex-direction: column;
    .label {
      margin-top: 6rpx;
    }
  }
  .style-horiz {
    flex-direction: row;
    .label {
      margin-top: 0;
      margin-right: 0;
    }
    .num {
      margin-left: 10rpx;
    }
  }
  .style-icon {
    flex-direction: column;
    .icon-box {
      width: 56rpx;
      height: 56rpx;
      margin: 0 auto;
      margin-bottom: 10rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      .img-icon {
        width: 56rpx;
        height: 56rpx;
      }
      .iconfont {
        font-size: 48rpx;
        line-height: 1;
      }
    }
    .label {
      margin-top: 0;
    }
  }
}
.style-4-container {
  align-items: stretch;

  .left-card,
  .right-card {
    flex: 1;
    padding: 20rpx;
    box-sizing: border-box;
  }
  .entry-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .left-card {
    flex: 2;
    margin-right: 20rpx;

    .stats-row {
      margin-top: 20rpx;
      .icon-box {
        width: 48rpx;
        height: 48rpx;
        margin: 0 auto;
        display: flex;
        align-items: center;
        justify-content: center;
        .asset-img {
          width: 48rpx;
          height: 48rpx;
        }
      }
      .label {
        font-size: 24rpx;
        margin-top: 10rpx;
        text-align: center;
      }
    }
  }
  .right-card {
    .title {
      font-size: 28rpx;
      font-weight: 600;
      margin-bottom: 10rpx;
    }
    .subtitle {
      font-size: 24rpx;
      color: #ff9900;
      display: flex;
      align-items: center;
      margin-bottom: 16rpx;
      .icon-you2 {
        font-size: 24rpx;
        margin-left: 6rpx;
      }
    }
    .img-box {
      width: 120rpx;
      height: 120rpx;
      image {
        width: 100%;
        height: 100%;
      }
    }
  }
  .item {
    flex-direction: column;
  }
}
.style-5-container {
  padding: 40rpx;
  background: #fff;
  border-radius: 24rpx;
  margin-top: 90rpx;

  .header {
    margin-bottom: 90rpx;
    position: relative;
    .left {
      position: absolute;
      top: -92rpx;
      left: 0;
      display: flex;
      align-items: flex-end;

      .avatar {
        width: 148rpx;
        height: 148rpx;
        margin-right: 24rpx;
        border: 4rpx solid #fff;
        border-radius: 50%;
        box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
        image {
          width: 100%;
          height: 100%;
          border-radius: 50%;
        }
        .empty-box {
          width: 100%;
          height: 100%;
          border-radius: 50%;
          background: #eee;
          display: flex;
          align-items: center;
          justify-content: center;
          image {
            width: 60rpx;
            height: 60rpx;
            border-radius: 0;
          }
        }
      }
      .text {
        .name {
          font-weight: bold;
          margin-bottom: 8rpx;
          font-size: 32rpx;
        }
        .level {
          font-size: 24rpx;
          opacity: 0.8;
        }
      }
    }
    .menu-entry {
      position: absolute;
      top: 0rpx;
      right: 0;
      display: flex;
      align-items: center;
      .item {
        margin-left: 20rpx;
        .menu-img {
          width: 60rpx;
          height: 60rpx;
        }
        .iconfont {
          font-size: 60rpx;
          color: #333;
        }
      }
    }
  }
}
.member-style-1 {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30rpx 0;
  margin-top: 20rpx;
  .item {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    padding: 0 40rpx;
    .text-box {
      .title {
        font-size: 28rpx;
        color: #333;
        font-weight: bold;
        margin-bottom: 10rpx;
      }
      .subtitle {
        font-size: 22rpx;
        color: #ff7d00;
        display: flex;
        align-items: center;
        .iconfont {
          font-size: 20rpx;
          margin-top: 2rpx;
        }
      }
    }
    .img-box {
      width: 88rpx;
      height: 88rpx;
      image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 50%;
      }
    }
    .line {
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 2rpx;
      height: 40rpx;
      background: #eee;
    }
  }
}
.member-style-2 {
  margin-top: 36rpx;
  padding: 30rpx;
  box-sizing: border-box;
  .top-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .left-info {
      flex: 1;
      .title-area {
        display: flex;
        align-items: center;
        .title-img {
          max-width: 324rpx;
          height: 72rpx;
        }
        .title-text {
          font-size: 32rpx;
          font-weight: 600;
        }
      }
      .intro-text {
        margin-top: 8rpx;
        font-size: 24rpx;
      }
    }
    .right-rights {
      display: flex;
      align-items: center;
      .right-item {
        margin-left: 16rpx;
        text-align: center;
        .icon-wrap {
          width: 56rpx;
          height: 56rpx;
          margin: 0 auto;
          display: flex;
          align-items: center;
          justify-content: center;
          image {
            width: 56rpx;
            height: 56rpx;
          }
        }
        .text {
          margin-top: 6rpx;
          font-size: 20rpx;
        }
      }
      .icon-you2 {
        font-size: 24rpx;
        margin-left: 10rpx;
      }
    }
  }
  .divider {
    height: 1rpx;
    background: rgba(0, 0, 0, 0.08);
    margin: 16rpx 0;
  }
  .bottom-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .explain-list {
      display: flex;
      align-items: center;
      flex: 1;
      .icons {
        width: 140rpx;
        height: 36rpx;
        margin-right: 10rpx;
        image {
          width: 100%;
          height: 100%;
        }
      }
      .explain-text {
        font-size: 22rpx;
      }
    }
    .action-btn {
      padding: 10rpx 18rpx;
      border-radius: 30rpx;
      font-size: 22rpx;
      font-weight: 600;
      max-width: 240rpx;
      text-align: center;
    }
  }
}
.member-style-3 {
  margin-top: 36rpx;
  .content-wrapper {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .desc {
    font-size: 24rpx;
  }
  .btn {
    border: 2rpx solid;
    padding: 8rpx 24rpx;
    border-radius: 28rpx;
    font-size: 24rpx;
  }
}
.member-style-4 {
  margin-top: 36rpx;
  padding: 40rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  .left-box {
    .label {
      color: #999;
      font-size: 24rpx;
      margin-bottom: 10rpx;
    }
    .value {
      color: #f6d99d;
      font-size: 48rpx;
      font-weight: 700;
    }
  }
  .right-box {
    background: #f6d99d;
    color: #5a350c;
    padding: 10rpx 30rpx;
    border-radius: 40rpx;
    font-size: 24rpx;
    font-weight: 700;
  }
}
</style>
