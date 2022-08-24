package com.zbkj.common.annotation;

import com.zbkj.common.validation.StringContainsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 校验一个字符串是否包含在指定字符数组内
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Documented
@Constraint(validatedBy = StringContainsValidator.class)
@Target({ ElementType.PARAMETER,ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface StringContains {

    String message() default "字符串不符合规则";

    String[] limitValues() default {};

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
