/**
 * Created by Netuser on 2016-4-8.
 */

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author 阁楼麻雀
 * @Date 2017/4/26
 * @Desc webmagic爬虫新浪博客
 */
public class SinaBlogProcessor implements PageProcessor {

    public static final String URL_LIST = "http://blog\\.sina\\.com\\.cn/s/articlelist_1229071681_0_\\d+\\.html";
    public static final String URL_POST = "http://blog\\.sina\\.com\\.cn/s/blog_\\w+\\.html";

    private Site site = Site
            .me()
            .addCookie("","","")
            .setDomain("blog.sina.com.cn")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(URL_LIST).match()) {//列表页
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"articleList\"]").links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
        } else {//文章页
            page.putField("title", page.getHtml().xpath("//div[@class='articalTitle']/h2/text()").toString());
            page.putField("date", page.getHtml().xpath("//div[@id='articlebody']//span[@class='time SG_txtc']").regex("\\((.*)\\)"));
            page.putField("content", stripHtml(page.getHtml().xpath("//div[@id='articlebody']//div[@class='articalContent']").toString()));

        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static String removeHtmlTag(String content) {
        Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>");
        Matcher m = p.matcher(content);
        if (m.find()) {
            content = content.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>", "$2");
            content = removeHtmlTag(content);
        }
        return content;
    }
    //清除HTML标签
    public static String stripHtml(String content) {
        // <p>段落替换为换行
        //content = content.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        content = content.replaceAll("<br\\s*/?>", "\r\n");
        // 去掉其它的<>之间的东西
        content = content.replaceAll("\\<.*?>", "");
        //去掉&nbsp
        content = content.replaceAll("&nbsp;","");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        return content;
    }

    public static void main(String[] args) {
        Spider.create(new SinaBlogProcessor())
                .addUrl("http://blog.sina.com.cn/s/articlelist_1229071681_0_1.html")
                .addPipeline(new JsonFilePipeline("D:\\webmagic\\"))
                .run();
    }
}