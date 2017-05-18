import Dao.MongoDBDao;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;

/**
 * @Author 阁楼麻雀
 * @Email netuser.orz@icloud.com
 * @Date 2017/5/16
 * @Desc
 */
public class BangTx3Processor implements PageProcessor {

    public static final String URL_GET = "http://bang.tx3.163.com/bang/role/";
    public static ArrayList<String> list = new ArrayList<String>();
    private Site site = Site
            .me()
            .setDomain(".163.com")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    @Override
    public void process(Page page) {
        String name = page.getHtml().xpath("//div[@class='dMain clear']/div/div/span[@class='sTitle']/text()").toString();
        if(name == null){
            page.setSkip(true);
        }
        String id = page.getUrl().toString().split("role/")[1].toString();
        String p = page.getHtml().toString();
        System.out.println("=============================================================");
        new MongoDBDao().add(page);
        //page.addTargetRequests(list);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        for (int i=2;i<3;i++){
            for (int j=2;j<3;j++){
                list.add(URL_GET+i+"_"+j);
            }
        }


        Spider.create(new BangTx3Processor())
                .addUrl("http://bang.tx3.163.com/bang/role/1_1")
                .thread(3)
                .run();
    }
}
