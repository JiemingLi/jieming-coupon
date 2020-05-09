package com.jieming.coupon.thymeleaf;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.constant.CouponCategory;
import com.jieming.coupon.constant.DistributeTarget;
import com.jieming.coupon.constant.GoodsType;
import com.jieming.coupon.constant.PeriodType;
import com.jieming.coupon.constant.ProductLine;
import com.jieming.coupon.dao.CouponTemplateDao;
import com.jieming.coupon.dao.UserDao;
import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.entity.User;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.service.IBuildTemplateService;
import com.jieming.coupon.service.ITemplateBaseService;
import com.jieming.coupon.vo.GoodsInfo;
import com.jieming.coupon.vo.TemplateNumber;
import com.jieming.coupon.vo.TemplateRequest;
import com.jieming.coupon.vo.TemplateRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>优惠券模板 Controller</h1>
 * Created by Qinyi.
 */
@Slf4j
@Controller
@RequestMapping("/template/thy")
public class ThyTemplateController {

    @Autowired
    private UserDao userDao;

    /** CouponTemplate Dao */
    private final CouponTemplateDao templateDao;

    /** 构造优惠券模板服务 */
    private final IBuildTemplateService templateService;

    @Autowired
    private ITemplateBaseService baseService;

    @Autowired
    public ThyTemplateController(CouponTemplateDao templateDao, IBuildTemplateService templateService) {
        this.templateDao = templateDao;
        this.templateService = templateService;
    }

    // 查找所有用户
    @GetMapping("/users")
    public String getAllUsers(ModelMap map){
        List<User> users = userDao.findAll();
        map.addAttribute("allUsers",users);
        return "user_list";
    }

    /**
     * <h2>优惠券系统入口</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/index
     * 成功
     * 不需要登录
     * */
    @GetMapping("/index")
    public String home() {
        log.info("view home.");
        return "home";
    }

    /**
     * <h2>查看优惠券模板详情</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/info/{id}
     * 成功
     * */
    @GetMapping("/info/{id}")
    public String info(@PathVariable Integer id, ModelMap map) {

        log.info("view template info.");
        Optional<CouponTemplate> templateO = templateDao.findById(id);
        if (templateO.isPresent()) {
            CouponTemplate template = templateO.get();
            map.addAttribute("template", ThyTemplateInfo.to(template));
        }
        return "template_detail";
    }


    // 增加优惠券
    @SuppressWarnings("all")
    @GetMapping("templateSub/{id}")
    public String subCouponNums(@PathVariable Integer id, ModelMap map,HttpSession session){
        log.info("sub coupon ...");
        Optional<CouponTemplate> templateOptional = templateDao.findById(id);
        if(templateOptional.isPresent()){
            CouponTemplate template = templateOptional.get();
            session.setAttribute("category", CouponCategory.values());
            session.setAttribute("productLine", ProductLine.values());
            session.setAttribute("target", DistributeTarget.values());
            session.setAttribute("period", PeriodType.values());
            session.setAttribute("goodsType", GoodsType.values());
            map.addAttribute("template",ThyCreateTemplate.to(template));
        }
        return "template_sub";
    }

    @PostMapping("/sub")
    public String subCoupons(@ModelAttribute ThyCreateTemplate template) throws Exception{
        log.info("增加优惠券数量");
        TemplateRequest request = transToRequests(template);
        templateService.subCouponTemplateCount(request);
        return "redirect:/template/thy/list";
    }

    // 增加优惠券
    @SuppressWarnings("all")
    @GetMapping("templateAdd/{id}")
    public String addCouponNums(@PathVariable Integer id, ModelMap map,HttpSession session){
        log.info("add coupon ...");
        Optional<CouponTemplate> templateOptional = templateDao.findById(id);
        if(templateOptional.isPresent()){
            CouponTemplate template = templateOptional.get();
            session.setAttribute("category", CouponCategory.values());
            session.setAttribute("productLine", ProductLine.values());
            session.setAttribute("target", DistributeTarget.values());
            session.setAttribute("period", PeriodType.values());
            session.setAttribute("goodsType", GoodsType.values());
            map.addAttribute("template",ThyCreateTemplate.to(template));
        }
        return "template_add";
    }

    @PostMapping("/add")
    public String addCoupons(@ModelAttribute ThyCreateTemplate template) throws Exception{
        log.info("增加优惠券数量");
        TemplateRequest request = transToRequests(template);
        templateService.addCouponTemplateCount(request);
        return "redirect:/template/thy/list";
    }

    // 更新优惠券模板信息
    @SuppressWarnings("all")
    @GetMapping("templateUpdate/{id}")
    public String updateCouponTemplate(@PathVariable Integer id, ModelMap map,HttpSession session){
        log.info("update coupon template");
        Optional<CouponTemplate> templateOptional = templateDao.findById(id);
        if(templateOptional.isPresent()){
            CouponTemplate template = templateOptional.get();
            session.setAttribute("category", CouponCategory.values());
            session.setAttribute("productLine", ProductLine.values());
            session.setAttribute("target", DistributeTarget.values());
            session.setAttribute("period", PeriodType.values());
            session.setAttribute("goodsType", GoodsType.values());
            map.addAttribute("template",ThyCreateTemplate.to(template));
        }
        return "template_update";
    }

    // 更新优惠券模板
    @PostMapping("/update")
    public String update(@ModelAttribute ThyCreateTemplate template) throws Exception {
        log.info("更新优惠券模板");
        TemplateRequest request = transToRequests(template);
        templateService.updateTemplate(request);
        return "redirect:/template/thy/list";
    }

    @SuppressWarnings("all")
    public TemplateRequest transToRequests(ThyCreateTemplate createTemplate) throws Exception{
        TemplateRequest request = new TemplateRequest();
        request.setName(createTemplate.getName());
        request.setUserId(createTemplate.getUserId());
        request.setCount(createTemplate.getCount());
        request.setTarget(createTemplate.getTarget());
        request.setProductLine(createTemplate.getProductLine());
        request.setCategory(createTemplate.getCategory());
        request.setDesc(createTemplate.getDesc());
        request.setLogo(createTemplate.getLogo());
        request.setCount(createTemplate.getCount());
        TemplateRule rule = new TemplateRule();
        // 1 过期规则定义
        rule.setExpiration(new TemplateRule.Expiration(
                createTemplate.getPeriod(),
                createTemplate.getGap(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createTemplate.getDeadline()).getTime())
        );
        // 1.1 折扣定义
        rule.setDiscount(new TemplateRule.Discount(createTemplate.getQuota(), createTemplate.getBase()));
        // 1.2 运行领取多少
        rule.setLimitation(createTemplate.getLimitation());
        // 1.3 使用限制
        rule.setUsage(new TemplateRule.Usage(createTemplate.getProvince(), createTemplate.getCity(),
                JSON.toJSONString(createTemplate.getGoodsType())));
        rule.setWeight(
                JSON.toJSONString(Stream.of(createTemplate.getWeight().split(",")).collect(Collectors.toList()))
        );
        request.setRule(rule);
        return request;
    }

    /**
     * <h2>查看优惠券模板列表</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/list
     * 成功
     * */
    @GetMapping("/list")
    public String list(ModelMap map) {

        log.info("view template list.");
        List<CouponTemplate> couponTemplates = templateDao.findAll();
        List<ThyTemplateInfo> templates =
                couponTemplates.stream().map(ThyTemplateInfo::to).collect(Collectors.toList());
        map.addAttribute("templates", templates);
        return "template_list";
    }

    /**
     * <h2>创建优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/create
     * 成功
     * */
    @GetMapping("/create")
    public String create(ModelMap map, HttpSession session) {

        log.info("view create form.");
        session.setAttribute("category", CouponCategory.values());
        session.setAttribute("productLine", ProductLine.values());
        session.setAttribute("target", DistributeTarget.values());
        session.setAttribute("period", PeriodType.values());
        session.setAttribute("goodsType", GoodsType.values());
        map.addAttribute("template", new ThyCreateTemplate());
        map.addAttribute("action", "create");
        return "template_form";
    }

    /**
     * <h2>创建优惠券模板</h2>
     * 127.0.0.1:7001/coupon-template/template/thy/create
     * */
    @SuppressWarnings("all")
    @PostMapping("/create")
    public String create(@ModelAttribute ThyCreateTemplate template) throws Exception {

        log.info("create form.");
        log.info("{}", JSON.toJSONString(template));

        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(
                template.getPeriod(), template.getGap(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(template.getDeadline()).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(template.getQuota(), template.getBase()));
        rule.setLimitation(template.getLimitation());
        rule.setUsage(new TemplateRule.Usage(template.getProvince(), template.getCity(),
                JSON.toJSONString(template.getGoodsType())));
        rule.setWeight(
                JSON.toJSONString(Stream.of(template.getWeight().split(",")).collect(Collectors.toList()))
        );

        TemplateRequest request = new TemplateRequest(
                template.getName(), template.getLogo(), template.getDesc(),
                template.getCategory(), template.getProductLine(), template.getCount(),
                template.getUserId(), template.getTarget(), rule
        );
        log.info("create coupon template: {}", JSON.toJSONString(templateService.buildTemplate(request)));
        return "redirect:/template/thy/list";
    }

    @ResponseBody
    @GetMapping("/find/template/nums")
    public TemplateNumber findNumByCategory(@RequestParam("category") String category){
        if("满减券".equals(category)){
            category = "001";
        }else if("折扣券".equals(category)){
            category = "002";
        }else if("立减券".equals(category)){
            category = "003";
        }
        return baseService.findLeftNumByCategory(category);
    }
}
