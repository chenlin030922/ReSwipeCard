package com.lin.reswipecard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchen on 2018/1/26.
 * mail: linchen@sogou-inc.com
 */

public class CardMaker {
    public static final String U1 = "https://afremov.com/images/product/image_1675.jpeg";
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
        cardBean.setTitle("first card");
        CardBean cardBean1 = new CardBean();
        cardBean1.setUrl(U2);
        cardBean1.setTitle("second card");
        CardBean cardBean2 = new CardBean();
        cardBean2.setUrl(U3);
        cardBean2.setTitle("third card");
        CardBean cardBean3 = new CardBean();
        cardBean3.setUrl(U4);
        cardBean3.setTitle("fourth card");
        CardBean cardBean4 = new CardBean();
        cardBean4.setUrl(U5);
        cardBean4.setTitle("fifth card");
        CardBean cardBean5 = new CardBean();
        cardBean5.setUrl(U6);
        cardBean5.setTitle("sixth card");
        CardBean cardBean6 = new CardBean();
        cardBean6.setUrl(U7);
        cardBean6.setTitle("seventh card");
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
