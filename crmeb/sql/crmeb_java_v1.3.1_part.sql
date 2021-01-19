/**
    增量更新sql
    适用于1.3版本升级1.3.1
    运行此脚本必须保证已经导入了1.3 的sql脚本
 */
-- 修改用户积分字段类型
alter table eb_user modify column integral int(11) NULL DEFAULT 0 COMMENT '用户剩余积分';
-- 修改订单表积分字段类型
alter table eb_store_order modify column gain_integral int(11) NULL DEFAULT 0 COMMENT '消费赚取积分';
alter table eb_store_order modify column use_integral int(11) NULL DEFAULT 0 COMMENT '使用积分';
alter table eb_store_order modify column back_integral int(11) NULL DEFAULT 0 COMMENT '给用户退了多少积分';

-- 修改商品表积分字段类型
alter table eb_store_product modify column give_integral int(11) NULL DEFAULT 0 COMMENT '获得积分';
-- 修改砍价商品积分字段类型
alter table eb_store_bargain modify column give_integral int(11) NULL DEFAULT 0 COMMENT '反多少积分';
-- 修改秒杀商品积分字段类型
alter table eb_store_seckill modify column give_integral int(11) NULL DEFAULT 0 COMMENT '反多少积分';

-- 评价表添加sku字段

alter table eb_store_product_reply add sku varchar(128) NOT NULL DEFAULT '' COMMENT '商品规格属性值,多个,号隔开';
