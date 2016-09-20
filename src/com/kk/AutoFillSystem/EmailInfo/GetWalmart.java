/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kk.AutoFillSystem.EmailInfo;

import com.kk.AutoFillSystem.Database.Entities.Orders;
import static com.kk.AutoFillSystem.Database.Operations.OrderOp.createNewOrder;
import static com.kk.AutoFillSystem.Database.Operations.TrackOp.createUsTrk;
import com.kk.AutoFillSystem.utility.Order;
import com.kk.AutoFillSystem.utility.Product;
import com.kk.AutoFillSystem.utility.Shipment;
import static com.kk.AutoFillSystem.utility.Tools.getWarehouse;
import static com.kk.AutoFillSystem.utility.Tools.readFileLines;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Yi
 */
public class GetWalmart extends GetStore {
    
    
    public GetWalmart(String email, String pwd) { 
        super();
        this.email = email;
        this.pwd = pwd;
        emailSender = "help@walmart.com";
        orderSubject = "Order received";
        shipSubject = "Shipped";
        storeName = "Walmart";
    }
    
    
    
   
    
    public static void main(String[] args) throws MessagingException, AddressException, ParseException {
        //get entitymanager
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AutoFillSystemPU");
//        EntityManager em = emf.createEntityManager();
        
        //query returned info of orders and shipments
        //using the orderInfo and shipmentInfo, to create new orders and trackings to db
        
//        GetKmart query = new GetKmart("fatblackcat2016@gmail.com","bnmrc123");
//        query.connectGmail();
//        query.searchInfoSince("09/15/16");
        
       
        
//        ArrayList<Order> newOrders = query.getOrders();
//        for(Order orderInfo : newOrders) {
//            createNewOrder(em, orderInfo);
//        }
//        
//        
//        ArrayList<Shipment> newShips = query.getShipments();
//        
//        for(Shipment shipInfo : newShips) {
//            createUsTrk(em, shipInfo);
//        }
//       
//
//        em.close();
//        emf.close();
//    
//    
        //test zz info
        String html ="<html><head><meta charset=\"utf-8\" content=\"text/html\" http-equiv=\"Content-Type\"><title>查看出库单 - 转运中国，美国购物轻松送到国内</title><meta content=\"转运中国,是为中国个人买家在海外购物中提供仓储,清关,转运服务的互联网国际物流平台\" name=\"Description\"><meta content=\"转运,转运中国,转运服务,海外购物,海外物流,美国,国际运费,国际物流,国际购物,第三方物流,美国快递\" name=\"Keywords\"><meta content=\"width=1024, initial-scale=1.0, maximum-scale=1.0;\" name=\"viewport\"><meta content=\"yes\" name=\"apple-mobile-web-app-capable\"><meta content=\"none\" name=\"msapplication-config\"><meta content=\"authenticity_token\" name=\"csrf-param\">\n" +
"<meta content=\"6WwdaEucHEDm1u4yJkos3uf2v5UJIbdjMxveG5kvOaY=\" name=\"csrf-token\"><link href=\"/favicon.ico?v=20131112\" rel=\"shortcut icon\" type=\"image/vnd.microsoft.icon\"><link href=\"/assets/www/product/content-1f81ac0cf7ef0812104d2a4ef20b42c8.css\" media=\"screen\" rel=\"stylesheet\"><script type=\"text/javascript\" async=\"\" src=\"https://ssl.google-analytics.com/ga.js\"></script><script src=\"/assets/lib/jquery-f0ef41811f718bf2e1a6723f5b45a310.js\"></script><script src=\"/assets/lib/jquery.validate.zh.min-c2f0cb409b2c7b472523b1966fcff4de.js\"></script><script src=\"/assets/www/ui/common-a136b5c597e8bccf6d9b8b861d344dc1.js\"></script></head><body><div id=\"header\"><div class=\"items\"><div class=\"logo\"><a href=\"/user\"><span>转运中国</span></a></div><div class=\"menu\"><ul><li><a href=\"/user\">我的货物</a></li><li><a href=\"/user/addresses\">我的地址</a></li><li><a href=\"/user/handovers\">我的交接</a></li><li><a href=\"/user/invoices\">交易记录</a></li><li><a href=\"/user/account\">我的账号</a></li><li><a href=\"/price\">价格与服务</a></li><li><a href=\"/user/logout\">退出登录</a></li></ul></div></div></div><div id=\"content\"><div class=\"content_title\"><span>查看出库单</span></div><div class=\"content_detail\"><div class=\"header_intro_panel\"><table><tbody><tr><td><div class=\"label\">出库仓库：</div><div class=\"value\"><img alt=\"Us 32 20\" height=\"15\" src=\"/assets/country/us_32_20-a33f9a40c9049733a3eaf8cf4b307536.png\" width=\"24\">&nbsp;OR免税州</div></td><td><div class=\"label\">出库单号：</div><div class=\"value\">0090922894943166</div></td><td><div class=\"label\">国内快递号：</div><div class=\"value\">暂无</div></td><td class=\"status\"><div class=\"label\">当前状态：</div><div class=\"value\">到达中国</div></td></tr></tbody></table></div><div class=\"detail_info_panel\"><dl class=\"list_info\"><dd><div class=\"label\">收件人</div><div class=\"value\">薛奕，北京，北京市，北京市海淀区北京大学中关新园80115，100871，18511380706，</div></dd><dd><div class=\"label\">出库单重量</div><div class=\"value\">4.04 公斤</div></dd><dd><div class=\"label\">出库单费用</div><div class=\"value\">267.66 元</div></dd><dd><div class=\"label\">优惠券减免</div><div class=\"value coupon_price\">0.0 元</div></dd><dd><div class=\"label\">实际称重</div><div class=\"value\">3.72 公斤</div></dd><dd><div class=\"label\">实际付费</div><div class=\"value\">249.03 元</div></dd><dd><div class=\"label\">备注</div><div class=\"value\"></div></dd><dd><div class=\"label\">管理员备注</div><div class=\"value\"></div></dd></dl><dl class=\"chart_info\"><dd class=\"first\">完成缴费&nbsp;<em>(2016-09-09 08:03:19)</em></dd><dd>等待库房发货&nbsp;<em>(2016-09-09 16:15:08)</em></dd><dd>飞往中国&nbsp;<em>(2016-09-10 12:30:31)</em></dd><dd class=\"last\">到达中国，等待清关&nbsp;<em>(2016-09-16 14:07:11)</em></dd></dl></div><div class=\"common_panel\"><div class=\"panel_header\">出库单包含快递包裹</div><div class=\"panel_content\"><table class=\"common_list\"><thead><tr><th>快递号码</th><th>货物概述</th><th>重量</th><th>入库时间</th><th class=\"last\">异常情况</th></tr></thead><tbody><tr><td>UPS 1Z022A8FYW00631930</td><td>儿童塑料无电机拼图玩具</td><td>1.68 公斤</td><td>2016-09-07 03:42:23</td><td class=\"last\">&nbsp;</td></tr><tr><td>UPS 1Z022VW60300053656</td><td>儿童塑料无电机拼图玩具</td><td>1.66 公斤</td><td>2016-09-09 03:19:50</td><td class=\"last\">&nbsp;</td></tr><tr class=\"last\"><td>UPS 1Z022VW60300053647</td><td>儿童塑料无电机拼图玩具</td><td>0.7 公斤</td><td>2016-09-09 01:09:01</td><td class=\"last\">&nbsp;</td></tr></tbody></table></div></div><div class=\"common_panel\"><div class=\"panel_header\">出库单包含快递物品</div><div class=\"panel_content\"><table class=\"common_list\"><thead><tr><th>快递号码</th><th>商品名称</th><th>商品类别</th><th width=\"8%\">商品品牌</th><th width=\"8%\">商品型号</th><th width=\"8%\">商品单价</th><th width=\"8%\" class=\"last\">商品数量</th></tr></thead><tbody><tr><td>UPS 1Z022A8FYW00631930</td><td>儿童塑料无电机拼图玩具</td><td>母婴用品玩具 - 儿童玩具</td><td>lego</td><td>30厘米</td><td>USD 33.0</td><td class=\"last\">2</td></tr><tr><td>UPS 1Z022VW60300053656</td><td>儿童塑料无电机拼图玩具</td><td>母婴用品玩具 - 儿童玩具</td><td>lego</td><td>30厘米</td><td>USD 33.0</td><td class=\"last\">2</td></tr><tr class=\"last\"><td>UPS 1Z022VW60300053647</td><td>儿童塑料无电机拼图玩具</td><td>母婴用品玩具 - 儿童玩具</td><td>lego</td><td>30厘米</td><td>USD 33.0</td><td class=\"last\">1</td></tr></tbody></table></div></div><div class=\"common_panel\"><div class=\"panel_header\">增值服务及收费明细</div><div class=\"panel_content\"><table class=\"common_list\"><thead><tr><th>快递号码</th><th>服务名称</th><th>收费</th><th class=\"last\">完成情况及收费标准收费描述</th></tr></thead><tbody><tr><td>UPS 1Z022A8FYW00631930</td><td>海外仓储</td><td width=\"7%\">0.0</td><td style=\"text-align:left;\" width=\"60%\" class=\"last\">入库时间2016-09-07 03:42:23，结算时间2016-09-09 08:03:19，共2天，您结算时为VIP用户，免费仓储30.0天</td></tr><tr><td>UPS 1Z022VW60300053656</td><td>海外仓储</td><td width=\"7%\">0.0</td><td style=\"text-align:left;\" width=\"60%\" class=\"last\">入库时间2016-09-09 03:19:50，结算时间2016-09-09 08:03:19，共0天，您结算时为VIP用户，免费仓储30.0天</td></tr><tr><td>UPS 1Z022VW60300053647</td><td>海外仓储</td><td width=\"7%\">0.0</td><td style=\"text-align:left;\" width=\"60%\" class=\"last\">入库时间2016-09-09 01:09:01，结算时间2016-09-09 08:03:19，共0天，您结算时为VIP用户，免费仓储30.0天</td></tr><tr><td>&nbsp;</td><td>合并发货</td><td width=\"7%\">0.0</td><td style=\"text-align:left;\" width=\"60%\" class=\"last\">两个及以上入库单一起出库，即为合并发货，免费提供合并发货服务</td></tr><tr class=\"last\"><td>&nbsp;</td><td>运费</td><td width=\"7%\">267.66</td><td style=\"text-align:left;\" width=\"60%\" class=\"last\">出库单称重4.04公斤，计费重量4.04公斤，最终计费重量以飞往中国时实际重量为准，您结算时为VIP用户，首重500克44.1元，续重100克6.21元</td></tr></tbody></table></div></div><div class=\"common_panel\"><div class=\"panel_header\">相关交易明细</div><div class=\"panel_content\"><table class=\"common_list\"><thead><tr><th>交易发起时间</th><th>交易金额</th><th class=\"last\">交易内容</th></tr></thead><tbody><tr><td class=\"nowrap\">2016-09-10 12:33:26</td><td class=\"nowrap\"><span style=\"color:#5d5;;\">+18.63</span></td><td class=\"last\">核算实际重量3.72KG，原出库单重量4.04KG，退回运费差</td></tr><tr><td class=\"nowrap\">2016-09-09 08:03:19</td><td class=\"nowrap\"><span style=\"color:#d55;;\">-267.66</span></td><td class=\"last\">出库单扣费</td></tr><tr class=\"last\"><td class=\"nowrap\">2016-09-09 08:02:56</td><td class=\"nowrap\"><span style=\"color:#5d5;;\">+267.66</span></td><td class=\"last\">支付宝充值</td></tr></tbody></table></div></div></div></div><div id=\"footer\"><div class=\"sponsor\"></div><div class=\"items\"><div class=\"links\"><ul><li><a href=\"/misc/membership-plan\"> 会员计划</a></li><li><a href=\"/misc/refund-insurance\"> 理赔保险</a></li><li><a href=\"/misc/intro\"> 公司简介</a></li><li><a href=\"/misc/contact-us\"> 联系我们</a></li><li><a href=\"/articles\"> 网站动态</a></li><li><a href=\"/price\"> 价格与服务</a></li><li><a href=\"/misc/service\">服务条款</a></li><li><a href=\"/misc/helps\"> 帮助信息</a></li></ul></div><div class=\"copyright\">© 2011-2016 北京西边信息科技有限公司<a href=\"http://www.miibeian.gov.cn/\" target=\"_blank\"> 京ICP备12000641号</a></div><div class=\"contact\"><ul><li class=\"tel\"><h4>客服热线</h4><div class=\"info\">400-080-5877 (工作日9-12时、14-18时)</div></li><script src=\"/assets/www/product/kf-33ff85c5a5945b020310defdc6db31c9.js\"></script><script type=\"text/javascript\">hz6d_fixed_conf[\"1\"] = {};\n" +
"hz6d_fixed_conf[\"1\"].text = \"进入在线客服 (工作日9-12时、14-18时)\";\n" +
"hz6d_fixed_conf[\"1\"].width = \"\";\n" +
"hz6d_fixed_conf[\"1\"].height = \"\";\n" +
"hz6d_fixed_conf[\"1\"].style = \"1\";\n" +
"hz6d_fixed_conf[\"1\"].kf = \"\";\n" +
"hz6d_FixedKF(\"text\",\"1\");</script><li><h4>在线客服</h4><div class=\"info\"><a href=\"http://tb.53kf.com/webCompany.php?arg=9002976&amp;style=1&amp;keyword=https%3A//www.uszcn.com/&amp;kf=\" target=\"_blank\">进入在线客服 (工作日9-12时、14-18时)</a></div></li><li class=\"group\"><h4>合作公司</h4><div class=\"info\"><a href=\"http://www.xiji.com/\" target=\"_blank\">西集</a>&nbsp;&nbsp;<a href=\"http://www.shopshow.com/\" target=\"_blank\">店秀</a>&nbsp;&nbsp;<a href=\"https://www.tmall.com/\" target=\"_blank\">天猫</a>&nbsp;&nbsp;<a href=\"http://www.jd.com/\" target=\"_blank\">京东</a>&nbsp;&nbsp;<a href=\"http://www.z.cn/\" target=\"_blank\">亚马逊</a></div></li></ul></div></div></div><script type=\"text/javascript\">var _gaq = _gaq || [];\n" +
"_gaq.push(['_setAccount', 'UA-23768598-1']);\n" +
"_gaq.push(['_setDomainName', '.uszcn.com']);\n" +
"_gaq.push(['_trackPageview']);\n" +
"(function () {\n" +
"  var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" +
"  ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" +
"  var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" +
"})();</script></body></html>" ;
        
        Document doc = Jsoup.parse(html);
        
        
//        Elements values = doc.select(".value");
//        
//        
//        
//        System.out.println(values.get(1).text());
//        System.out.println(values.get(5).text());
//        System.out.println(values.get(6).text());
//        
//        Element ustrkTable = doc.select("tbody").get(1);
//        
//        Elements ustrks = ustrkTable.select("tr");
//        
//        for(Element ustrk : ustrks){
//            System.out.println(ustrk.select("td").first().text());
//        }
   }
    
    
    @Override
    public Order extractOrder(String text) {
        
        Order order = new Order();
        //get order number
        Pattern orderNum = Pattern.compile("Order number: ([0-9-]+)");
        Matcher m = orderNum.matcher(text);
        if(m.find()) {
            order.orderNum = m.group(1);
        }
        
        
        
        
        //get items
        //put item in products
        //also, accumulate same item
        ArrayList<String> items = getItems(text, "Items may arrive");
        ArrayList<Product> products = new ArrayList();
        for(String item : items) {
            
            Pattern orderLine = Pattern.compile("([^$]*) ([1-9][0-9]?) \\$[0-9.]+\\s\\$[0-9.]+\\s");
            Matcher m2 = orderLine.matcher(item);
            boolean found = false;
            while(m2.find()) {
                String name = m2.group(1);
                int count = Integer.parseInt(m2.group(2));
                
                for (Product prod : products) {
                    if (name.equals(prod.name)) {
                        prod.count += count;
                        found = true;
                        break;
                    }
                }
                
                if(!found) {
                    products.add(new Product(name, count));
                }
            }
        }
        
        order.products = products;
        return order;
    
    }
    
    
    //get the text string for items
    public static ArrayList<String> getItems(String text, String endText) {
        ArrayList<String> items = new ArrayList();
        String str1 = "Qty Price Total ";
        String str2 = endText;
        int index = 0;
        while(index < text.length()) {
            String searchText = text.substring(index);
            int startIndex = searchText.indexOf(str1);
            int stopIndex = searchText.indexOf(str2);
            
            if (startIndex != -1 && stopIndex != -1){
                items.add(searchText.substring(startIndex + str1.length(), stopIndex));
                index += stopIndex + str2.length();
            }
            
            else break;
                
            
        }
        return items;    
    }
    
    
    
    
    
  //get information for delivery confirm email : We just delivered a shipment on your order
  
    @Override
    public Shipment extractShipment(String text) {
        
        
        
        
        Shipment shipment = new Shipment();
        
        //tracking
        Pattern track = Pattern.compile("[0-9]+ ([a-zA-Z ]*) tracking number:\\s.{1}?([a-zA-Z0-9]*)");
        Matcher m = track.matcher(text);
        if(m.find()) {
            shipment.carrier = m.group(1);
            shipment.trackingNum = m.group(2);
            //System.out.println(m.group(2));
        }
        
        //order number
        Pattern order = Pattern.compile("Order #: ([0-9-]+)");
        Matcher m1 = order.matcher(text);
        if(m1.find()) {
            shipment.orderNum = m1.group(1);
        }
        
        //ship to 
        shipment.warehouse = getWarehouse(text);
        
        
        //get items
        //put item in products
        
        ArrayList<String> items = getItems(text, "Return Code:");
        ArrayList<Product> products = new ArrayList();
        for(String item : items) {
            
            Pattern orderLine = Pattern.compile("([^$]*) ([1-9][0-9]?) \\$[0-9.]+\\s\\$[0-9.]+\\s");
            Matcher m2 = orderLine.matcher(item);
            boolean found = false;
            while(m2.find()) {
                String name = m2.group(1);
                int count = Integer.parseInt(m2.group(2));
                
                for (Product prod : products) {
                    if (name.equals(prod.name)) {
                        prod.count += count;
                        found = true;
                        break;
                    }
                }
                
                if(!found) {
                    products.add(new Product(name, count));
                }
            }
        }
        
        shipment.products = products;
        
        return shipment;
    }
    
    public static void splitLego(String items) {
        
        String[] itemSet = items.split("LEGO®");
        Pattern itemP = Pattern.compile("(.*)([0-9]+)$");
        for(String item : itemSet) {
            
            item = item.trim();
            //System.out.println(item);
            Matcher m = itemP.matcher(item);
            if(m.find()) {
                System.out.println("Lego " + m.group(1));
                System.out.println(m.group(2));
            }
        }
        
       
    }
    
    
    
    
    
    
}
