package com.lin.reswipecard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 2018/1/26.
 * mail: linchen@sogou-inc.com
 */

public class CardMaker {
    public static final String U1 = "https://gss1.bdstatic.com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D400/sign=e8cdeac05182b2b7a79f38c401accb0a/f11f3a292df5e0fed0acfbc5546034a85fdf72b9.jpg";
    public static final String U2 = "http://img02.tooopen.com/images/20151122/tooopen_sy_149199661189.jpg";
    public static final String U3 = "http://gmimg.geimian.com/pic/2015/04/20150419_213113_920.jpg";
    public static final String U4 = "http://pic.qiantucdn.com/58pic/11/84/20/04s58PICiYA.jpg";
    public static final String U5 = "http://img02.tooopen.com/images/20160122/tooopen_sy_155234647714.jpg";
    public static final String U6 = "http://seopic.699pic.com/photo/50007/5448.jpg_wh1200.jpg";
    public static final String U7 = "https://thumbs.dreamstime.com/b/%E6%8A%BD%E8%B1%A1%E6%B2%B9%E7%94%BB-15920804.jpg";

    public static List<CardBean> initCards() {
        List<CardBean> list = new ArrayList<>();
        CardBean cardBean = new CardBean();
        cardBean.setUrl(U1);
        CardBean cardBean1 = new CardBean();
        cardBean.setUrl(U2);
        CardBean cardBean2 = new CardBean();
        cardBean.setUrl(U3);
        CardBean cardBean3 = new CardBean();
        cardBean.setUrl(U4);
        CardBean cardBean4 = new CardBean();
        cardBean.setUrl(U5);
        CardBean cardBean5 = new CardBean();
        cardBean.setUrl(U6);
        CardBean cardBean6 = new CardBean();
        cardBean.setUrl(U7);
        list.add(cardBean);
        list.add(cardBean1);
        list.add(cardBean2);
        list.add(cardBean3);
        list.add(cardBean4);
        list.add(cardBean5);
        list.add(cardBean6);


        return list;
    }
}
