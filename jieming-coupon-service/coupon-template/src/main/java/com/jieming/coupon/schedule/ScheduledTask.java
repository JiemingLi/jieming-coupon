package com.jieming.coupon.schedule;

import com.jieming.coupon.dao.CouponTemplateDao;
import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h1>定时清理已过期的优惠券模板</h1>
 * Created by Jieming.
 */
@Slf4j
@Component
public class ScheduledTask {

    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    @Autowired
    public ScheduledTask(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }

    /**
     * <h2>下线已过期的优惠券模板</h2>
     * 1000 = 1S
     * */
    @Scheduled(fixedRate = 60 * 1 * 1000)
    public void offlineCouponTemplate() {

        log.info("Start To Expire CouponTemplate");

        // 找到有效的模板
        List<CouponTemplate> templates =
                templateDao.findAllByExpired(false);

        if (CollectionUtils.isEmpty(templates)) {
            log.info("没有优惠券模板过期.");
        }

        Date cur = new Date();
        List<CouponTemplate> expiredTemplates =
                new ArrayList<>(templates.size());

        templates.forEach(t -> {

            // 根据优惠券模板规则中的 "过期规则" 校验模板是否过期
            TemplateRule rule = t.getRule();
            if (rule.getExpiration().getDeadline() < cur.getTime()) {
                t.setExpired(true);
                t.setAvailable(false);
                expiredTemplates.add(t);
            }
        });

        if (CollectionUtils.isNotEmpty(expiredTemplates)) {
            log.info("Expired CouponTemplate Num: {}",
                    templateDao.saveAll(expiredTemplates));
        }
        log.info("有优惠券模板过期，已经处理");
    }
}
