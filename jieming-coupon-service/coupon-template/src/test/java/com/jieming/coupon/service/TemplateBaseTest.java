package com.jieming.coupon.service;

import com.alibaba.fastjson.JSON;
import com.jieming.coupon.constant.CouponCategory;
import com.jieming.coupon.dao.CouponTemplateDao;
import com.jieming.coupon.dao.UserDao;
import com.jieming.coupon.entity.CouponTemplate;
import com.jieming.coupon.entity.User;
import com.jieming.coupon.exception.CouponException;
import com.jieming.coupon.vo.CouponTemplateSDK;
import com.jieming.coupon.vo.TemplateNumber;
import com.jieming.coupon.vo.TemplateRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>优惠券模板基础服务的测试</h1>
 * Created by Jieming.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseTest {

    @Autowired
    private ITemplateBaseService baseService;

    @Autowired
    private CouponTemplateDao couponTemplateDao;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserDao userDao;

    @Test
    public void getAllUsers(){
        List<User> all = userDao.findAll();
        System.err.println(all);
    }


    @Test
    public void testUpdateNums() throws CouponException{
        List<CouponTemplate> all = couponTemplateDao.findAll();
        System.out.println();
    }



    @Test
    public void testCategory() throws CouponException{
        TemplateNumber leftNumber3 = baseService.findLeftNumByCategory("003");
        TemplateNumber leftNumber2 = baseService.findLeftNumByCategory("002");
        TemplateNumber leftNumber1 = baseService.findLeftNumByCategory("001");
//        List<CouponTemplate> category = couponTemplateDao.findByCategory(CouponCategory.of("003"));
        System.err.println(JSON.toJSONString(leftNumber1));
        System.err.println(JSON.toJSONString(leftNumber2));
        System.err.println(JSON.toJSONString(leftNumber3));
    }

    // register
    @Test
    public void testRegisterUser() throws  CouponException{
        User user = new User();
        user.setAccount("1600300218");
        user.setPassword("123456");
        user.setName("jiemingli");
        User res = userService.register(user);
        System.out.println(JSON.toJSONString(res));
    }

    // login
    @Test
    public void testLoginUser() throws CouponException{
        User user = new User();
        user.setAccount("1600300218");
        user.setPassword("123456");
        user.setName("jiemingli");
        String r = userService.login(user);
        System.out.println(r);
    }


    @Test
    public void testBuildTemplateInfo() throws CouponException {

        System.out.println(JSON.toJSONString(
                baseService.buildTemplateInfo( 10)));
//        System.out.println(JSON.toJSONString(
//                baseService.buildTemplateInfo( 2)));
    }

    @Test
    public void testFindAllUsableTemplate() {

        System.out.println(JSON.toJSONString(
                baseService.findAllUsableTemplate()
        ));
    }

    @Test
    public void testFindAll(){
        List<CouponTemplateSDK> allTemplate = baseService.findAll();
        System.out.println(JSON.toJSONString(allTemplate));
    }

    @Test
    public void testFindById() throws CouponException{
        CouponTemplate template = baseService.buildTemplateInfo(1);
        System.out.println(JSON.toJSONString(template));
    }

    @Test
    public void testFindId2TemplateSDK() {

        System.out.println(JSON.toJSONString(
                baseService.findIds2TemplateSDK(Arrays.asList(10, 2, 3))
        ));
    }
}
