/**
    增量更新sql
    适用于1.3.1版本升级1.3.2
    运行此脚本必须保证已经导入了1.3.1 的sql脚本
 */
alter table eb_system_admin add phone varchar(15) DEFAULT NULL COMMENT '手机号码';
alter table eb_system_admin add is_sms tinyint(1) unsigned DEFAULT '0' COMMENT '是否接收短信';

select * from `eb_system_attachment` s where s.`satt_dir` like 'image/%';
update `eb_system_attachment` s set s.`satt_dir` = REPLACE(`satt_dir`,'image/','crmebimage/') where s.`satt_dir` like 'image/%';


select * from `eb_system_config` s where s.`value` like 'image/%';
update `eb_system_config` c set c.`value` = REPLACE(`value`,'image/','crmebimage/') where c.`value` like 'image/%';



select * from `eb_system_group_data` d where d.`value` like '%image/%';
update `eb_system_group_data` d set d.`value` = REPLACE(`value`, 'image/', 'crmebimage/')where d.`value` like '%image/%';


select * from `eb_store_product` p where p.`image` like'image/%';
update `eb_store_product` p set p.`image` = REPLACE(`image`,'image/','crmebimage/') where p.`image` like 'image/%';

select * from `eb_store_product` p where p.`slider_image` like'%image/%';
update `eb_store_product` p set p.`slider_image` = REPLACE(`slider_image`,'image/','crmebimage/') where p.`image` like '%image/%';



select * from `eb_store_order_info` i where i.`info` like '%image/%';
update `eb_store_order_info` p set p.`info` = REPLACE(`info`,'image/','crmebimage/') where p.`info` like '%image/%';


select * from `eb_system_user_level` s where s.`image` like '%image/%' or s.`icon` like '%image/%';
update `eb_system_user_level` s set s.`image` = REPLACE(`image`,'image/','crmebimage/') where s.`image` like '%image/%';
update `eb_system_user_level` s set s.`icon` = REPLACE(`icon`,'image/','crmebimage/')  where s.`icon` like '%image/%';



select * from `eb_user` u where u.`avatar` like 'image/%';
update `eb_user` u set u.`avatar` = REPLACE(`avatar`,'image/','crmebimage/') where u.`avatar` like 'image/%';