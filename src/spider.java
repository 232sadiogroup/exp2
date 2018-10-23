import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class spider {

    private String words;
    int page;
    private String[][] map = new String[100][8];
    int num = 0;
    private Elements cards;

    // 发送Html请求
    private void getHtml() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://s.weibo.com/weibo?q="+words+"&page="+page)
                    //                .data("query", "Java")   // 请求参数
                    //                .userAgent("spider") // 设置 User-Agent
                    //                .cookie("auth", "token") // 设置 cookie
                    //                .timeout(3000)           // 设置连接超时时间
                    .post();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cards = doc.select(".card-wrap[action-type=feed_list_item][mid]");
    }

    // 获取用户Id
    private void getId() {
        Elements ids = cards.select(".avator>a");
        num = 0;//计数器置零
        for (Element id : ids) {
            Pattern pattern = Pattern.compile("\\d+");//正则表达式提取出用户Id
            Matcher matcher = pattern.matcher(id.attr("href"));
            //System.out.println(id.text());
            if (matcher.find()) {
                //System.out.println(matcher.group(0));
                map[num][0] = matcher.group(0);//有则填入数组中
            } else {
                map[num][0] = null;//无则为空
            }
            num++;//计数器加一，开始填写下一条微博数据
        }
    }

    //获取用户名
    private void getNickName() {
        num = 0;
        for (Element card : cards.select("p.txt")) {
            //System.out.println(card.attr("nick-name"));
            map[num][1] = card.attr("nick-name");
            num++;
        }
    }

    //获取微博内容
    private void getText() {
        num = 0;
        Elements txts = cards.select("[node-type=feed_list_content]");
        for (Element txt : txts) {
            //System.out.println(txt.text());
            map[num][2] = txt.text();
            num++;
        }
    }

    //获取微博发送时间
    private void getDate() {
        Elements dates = cards.select(".from>[target]");
        num = 0;
        for (Element date : dates) {
            map[num][3] = date.text();
            num++;
        }
    }

    //获取发送设备
    private void getDevice() {
        Elements devs = cards.select(".from>[rel]");
        num = 0;
        for (Element dev : devs) {
            map[num][4] = dev.text();
            num++;
        }
    }

    //获取互动数量（转发、评论、点赞）
    private void getInteractNum() {
        Elements a = cards.select(".card-act");
        //System.out.println(a.toString());
        num = 0;
        for (Element b : a) {
            map[num][5] = b.select("[action-data^=allowForward]").text().replaceAll("\\D+", "");
            map[num][6] = b.select("[action-data^=pageid]").text().replaceAll("\\D+", "");
            map[num][7] = b.select("[action-data^=mid]").text();
            num++;
        }
    }

    //输出所有数据
    private void print() {

        for (int i = 0; i < num; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(map[i][j] + '|');
            }
            System.out.println();
        }
    }

    //接收字符串类型的搜索关键字和整数类型的页数
    spider(String words,int page) {
        this.words = words;
        this.page = page;

        getHtml();

        getId();
        getNickName();
        getText();
        getDate();
        getDevice();
        getInteractNum();

        print();
    }
}
