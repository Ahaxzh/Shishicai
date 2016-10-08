import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*******************************************
 * 项目名称：重庆时时彩抓去程序
 * 程序功能：抓去时时彩今日数据
 * 编码日期：2016-09-28
 * 程序作者：ahaxzh@Gmail.com
 * ******************************************/

public class Main {

    public static void main(String[] args) {
        query();
    }

    /**
     * 启动程序入口s
     */
    public static void query() {
        Document doc = getHtmlDoc();
        List<Map<String, Object>> list = makeDate(doc);
        assert list != null;
        list.forEach(System.out::println);


    }

    /**
     * 处理HTML
     * @param doc 抓取的Documet档案
     * @return  处理好的List
     */
    private static List<Map<String, Object>> makeDate(Document doc) {
        //System.out.print(doc.html());
        if (doc == null) {
            throw new AssertionError();
        }
        Element results = doc.getElementById("mainArea");
        if (results == null) return null;
        Elements trs = results.getElementsByTag("tr");
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i < trs.size(); i++) {
            Element tr = trs.get(i);
            Elements tds = tr.getElementsByTag("td");

            String no;              //期号
            String winNum;          //开奖号码
            String shiWei;          //十位
            String gewWei;          //个位
            String houSanWei;       //后三形态

            if (!"<td>- -</td>".equals(tds.get(1).toString())) {
                winNum = tds.get(1).toString().substring(25, 34);           //期号
                no = tds.get(0).toString().substring(59, 68);               //开奖号码
                shiWei = tds.get(2).toString().substring(4, 6);             //十位
                gewWei = tds.get(3).toString().substring(4, 6);             //个位
                houSanWei = tds.get(4).toString().indexOf("color-state-2") > 0 ?
                        tds.get(4).toString().substring(26, 28) :
                        tds.get(4).toString().substring(25, 27);            //后三形态
                Map<String, Object> map = new HashMap<>();
                map.put("winNum", winNum);
                map.put("shiWei", shiWei);
                map.put("gewWei", gewWei);
                map.put("houSanWei", houSanWei);
                map.put("no", no);
                list.add(map);
            }

            if (!"<td>- -</td>".equals(tds.get(8).toString())) {
                winNum = tds.get(8).toString().substring(25, 34);           //期号
                no = tds.get(7).toString().substring(59, 68);               //开奖号码
                shiWei = tds.get(9).toString().substring(4, 6);             //十位
                gewWei = tds.get(10).toString().substring(4, 6);            //个位
                houSanWei = tds.get(11).toString().indexOf("color-state-2") > 0 ?
                        tds.get(11).toString().substring(26, 28) :
                        tds.get(11).toString().substring(25, 27);           //后三形态

                Map<String, Object> map = new HashMap<>();
                map.put("winNum", winNum);
                map.put("shiWei", shiWei);
                map.put("gewWei", gewWei);
                map.put("houSanWei", houSanWei);
                map.put("no", no);
                list.add(map);
            }

            if (!"<td>- -</td>".equals(tds.get(15).toString())) {
                winNum = tds.get(15).toString().substring(25, 34);          //期号
                no = tds.get(14).toString().substring(59, 68);              //开奖号码
                shiWei = tds.get(16).toString().substring(4, 6);            //十位
                gewWei = tds.get(17).toString().substring(4, 6);            //个位
                houSanWei = tds.get(18).toString().indexOf("color-state-2") > 0 ?
                        tds.get(18).toString().substring(26, 28) :
                        tds.get(18).toString().substring(25, 27);           //后三形态

                Map<String, Object> map = new HashMap<>();
                map.put("winNum", winNum);
                map.put("shiWei", shiWei);
                map.put("gewWei", gewWei);
                map.put("houSanWei", houSanWei);
                map.put("no", no);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 抓取数据模块
     * @return 抓取的Document档案
     */
    protected static Document getHtmlDoc() {
        CloseableHttpClient httpclient;
        httpclient = null;
        CloseableHttpResponse httpResponse;
        Document doc = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpClientContext context = HttpClientContext.create();

            RequestConfig requestCfg = RequestConfig.custom().setSocketTimeout(15000).setConnectionRequestTimeout(15000)
                    .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();          //设置延时
            HttpGet httpGetIndex = new HttpGet("http://caipiao.163.com/award/cqssc/");
            httpGetIndex.setConfig(requestCfg);
            httpGetIndex.addHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
            httpclient.execute(httpGetIndex, context);      //包装头第一访问目的获取passport

            HttpPost httpPost = new HttpPost("http://caipiao.163.com/award/cqssc/");
            httpPost.addHeader("Referer", "http://caipiao.163.com/");
            httpPost.setConfig(requestCfg);
            httpPost.addHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");
            httpResponse = httpclient.execute(httpPost, context);   //模拟浏览器访问 第二次真正访问目标.

            try {
                doc = Jsoup.parse(httpResponse.getEntity().getContent(), "UTF-8", "");
            } catch (IOException x) {
                System.out.printf("getHTML ERROR!");
            }
        } catch (IOException e) {
            System.out.printf(e.toString());
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    System.out.printf(e.toString());
                }
            }
        }
        return doc;
    }
}




