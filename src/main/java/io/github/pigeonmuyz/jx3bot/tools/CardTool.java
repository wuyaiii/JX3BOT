package io.github.pigeonmuyz.jx3bot.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.pigeonmuyz.jx3bot.entity.*;
import io.github.pigeonmuyz.jx3bot.entity.request.ChivalrousRequest;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.pigeonmuyz.jx3bot.Main.settings;

public class CardTool {
    /**
     * Gson对象
     */
    static Gson gson = new Gson();
    static String robot = settings.get("bot_name").toString();
    /**
     * 时间格式
     */
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
//    /**
//     * 屏蔽词
//     */
//    static List<String> MaskedWords = (List<String>) settings.get("team_masked_words");
    /**
     * 卡片集合
     */
    static List<MultipleCardComponent> card = new ArrayList<>();
    static Type newType = new TypeToken<Result<ReturnImageEntity>>(){}.getType();
    static Result<ReturnImageEntity> results;
    /**
     * 图片数组
     */
    static List<ImageElement> imagesList = new ArrayList<>();
    /**
     * 备注模块
     */
    static ContextModule context;

    /**
     * @Author 清晨
     * @param command 用户输入
     * @param userID 用户ID
     * @param channelID 频道ID
     * @param server 服务器
     * @return 处理好的卡片信息
     * 此方法为单指令（即单独输入：日常｜金价 之类的）
     */
    public static List<MultipleCardComponent> singleCommand(String command,String userID,String channelID,String server) {

        try {
            switch(command){
                //region 订阅WSS
                case "全部订阅":
                case "订阅":
                case "订阅全部":
                    initSaohua();
                    List<String> newChannelList = (List<String>)settings.get("news");
                    newChannelList.add(channelID);
                    settings.put("news",newChannelList);
                    newChannelList = (List<String>)settings.get("version_fix");
                    newChannelList.add(channelID);
                    settings.put("version_fix",newChannelList);
                    newChannelList = (List<String>)settings.get("server_status");
                    newChannelList.add(channelID);
                    settings.put("server_status",newChannelList);
                    if (channelID != null){
                        card.add(new CardBuilder()
                                .setSize(Size.LG)
                                .setTheme(Theme.WARNING)
                                .addModule(new SectionModule(new PlainTextElement("当前频道订阅成功"), null, null))
                                .newCard()
                                .setTheme(Theme.NONE)
                                .setSize(Size.LG)
                                .addModule(context)
                                .build());
                    }
                    break;
                //endregion
                //region 日常相关实现
                case "日常":
                    initSaohua();
                    Type tempType = new TypeToken<Result<Daily>>(){}.getType();
                    Result<Daily> rd   = gson.fromJson(HttpTool.getData("https://v7.jx3api.com/data/active/current?server="+server), tempType);
                    Daily de = rd.getData();
                    card.add(new CardBuilder()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVE日常", false)))
                            .addModule(new SectionModule(new PlainTextElement("秘境日常："+de.getWar()), null, null))
                            .addModule(new SectionModule(new PlainTextElement("公共日常："+de.getTeam().get(0)), null, null))
                            .newCard()
                            .setTheme(Theme.DANGER)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVP日常", false)))
                            .addModule(new SectionModule(new PlainTextElement("矿车：跨服•烂柯山"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("战场："+de.getBattle()), null, null))
                            .newCard()
                            .setTheme(Theme.INFO)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVX日常", false)))
                            .addModule(new SectionModule(new PlainTextElement("美人图："+de.getHuatu()), null, null))
                            .addModule(new SectionModule(new PlainTextElement("门派事件："+de.getSchool()), null, null))
                            .addModule(new SectionModule(new PlainTextElement("预测高概率摸宠："+de.getLuck()), null, null))
                            .newCard()
                            .setTheme(Theme.SUCCESS)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVE周常", false)))
                            .addModule(new SectionModule(new PlainTextElement("五人秘境："+de.getTeam().get(1)), null, null))
                            .addModule(new SectionModule(new PlainTextElement("十人秘境："+de.getTeam().get(2)), null, null))
                            .newCard()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(new ContextModule.Builder().add(new PlainTextElement("今天是"+de.getDate()+" 星期"+de.getWeek(), false)).build())
                            .addModule(DividerModule.INSTANCE)
                            .addModule(context)
                            .build());
                    break;
                //endregion
                //region 金价相关实现
                case "金价":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/trade/demon?server="+server),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new HeaderModule(new PlainTextElement(server+" 目前金价！",false)))
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 花价相关实现
                case "花价":
                    // @TODO 还没做其他服的！！
                    initSaohua();
                    Result<ReturnImageEntity> results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/home/flower?server="+server),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 团队招募相关实现
                case "团队招募":
                case "招募":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/member/recruit?server="+server),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new HeaderModule(new PlainTextElement(server+"目前的团队招募来辣！",false)))
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 楚天行侠相关实现
                // 迭代太麻烦了！开摆！！
                case "楚天行侠":
                case "楚天社":
                case "行侠":
                    initSaohua();
                    ChivalrousRequest cr = gson.fromJson(HttpTool.getData("http://localhost:8080/api/celebrities"), ChivalrousRequest.class);
                    card.add(new CardBuilder()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("楚天社事件来咯！", false)))
                            .newCard()
                            .setTheme(Theme.DANGER)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("现在正在进行："+cr.getData().get(0).getDesc(), false)))
                            .addModule(new SectionModule(new PlainTextElement(String.format("地点：%s · %s",cr.getData().get(0).getMap_name(),cr.getData().get(0).getSite())), null, null))
                            .addModule(new SectionModule(new PlainTextElement(String.format("开始时间：%s",cr.getData().get(0).getTime())), null, null))
                            .newCard()
                            .setTheme(Theme.INFO)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("即将进行：", false)))
                            .addModule(new SectionModule(new PlainTextElement(String.format("事件1：%s",cr.getData().get(1).getDesc())), null, null))
                            .addModule(new SectionModule(new PlainTextElement(String.format("地点：%s · %s",cr.getData().get(1).getMap_name(),cr.getData().get(1).getSite())), null, null))
                            .addModule(new SectionModule(new PlainTextElement(String.format("开始时间：%s",cr.getData().get(1).getTime())), null, null))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement(String.format("事件2：%s",cr.getData().get(2).getDesc())), null, null))
                            .addModule(new SectionModule(new PlainTextElement(String.format("地点：%s · %s",cr.getData().get(2).getMap_name(),cr.getData().get(2).getSite())), null, null))
                            .addModule(new SectionModule(new PlainTextElement(String.format("开始时间：%s",cr.getData().get(2).getTime())), null, null))
                            .build());
                    break;
                //endregion
                //region 更新日志相关实现
                case "日志":
                case "更新日志":
                case "Version":
                    initSaohua();
                    card.add(new CardBuilder()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("剑三鸽鸽 ver.Remake 2.0.1 （开发版本号：2030）")))
                            .addModule(new SectionModule(new PlainTextElement("1. 修复了部分指令反馈错误的问题！！"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("2. 加入赞助辣！！！（你当然可以选择白嫖！我可以为爱发电！）"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("3. 加入了一些新功能（宏查询可以了！）"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("4. 重构了服务后端，已经开始着手准备安卓、iOS、Windows及Mac的查询小工具"), null, null))
                            .newCard()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(context)
                            .build());
                    break;
                //endregion
                //region 帮助相关实现
                //TODO: 还没做完！！
                case "帮助":
                case "功能":
                case "指令":
                    initSaohua();
                    card.add(new CardBuilder()
                            .setTheme(Theme.INFO)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("通用类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("赞助指令：赞助"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("最新公告：公告"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查询日常：日常"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("绑定服务器：绑定 [服务器(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("外观价格：外观 [物品名称|物品别名(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("角色装备：查询 [服务器(可选)] [玩家名字(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查询金价：金价 [服务器(可选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查询奇遇：奇遇 [服务器(可选)] [玩家名字(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("订阅监控：全部订阅[首次订阅将会立即生效]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("团队招募：招募|团队招募 [服务器名（可选）] [副本名(可选，例：西津渡)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查询日志：[日志|更新日志|Version]"), null, null))
                            .newCard()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVE类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("百战指令：百战"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查宏指令：宏 [心法名（可以使用别称！！！）]"), null, null))
                            .newCard()
                            .setTheme(Theme.DANGER)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVP类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("JJC战绩：JJC｜战绩 [玩家名(必选)] [模式，例如22｜33｜55(必选)] [服务器(可选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("战争沙盘：沙盘 [服务器(可选)]"), null, null))
                            .newCard()
                            .setTheme(Theme.SUCCESS)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVX类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("查询花价：花价 "), null, null))
                            .addModule(new SectionModule(new PlainTextElement("成就进度：成就 [服务器(机器人绑定过服务器的不需要输入)] [玩家名] [成就名或成就系列(例：沈剑心)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("楚天行侠：行侠|楚天社|楚天行侠"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("宠物游历：游历 [地图(必选)]"), null, null))
                            .newCard()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(new ContextModule.Builder().add(new PlainTextElement("剑三鸽鸽 ver.Remake 2.0", false)).build())
                            .addModule(DividerModule.INSTANCE)
                            .addModule(context)
                            .build());
                    break;
                //endregion
                //region 开服状态查询
                case "开服":
                    initSaohua();
                    tempType = new TypeToken<Result<ServerSwitch>>(){}.getType();
                    Result<ServerSwitch> resultServer = gson.fromJson(HttpTool.getData("http://localhost:8080/api/serverCheck?server="+server), tempType);
                    if (resultServer.getCode() == 200){
                        ServerSwitch ss = resultServer.getData();
                        card.add(new CardBuilder()
                                .setTheme(Theme.NONE)
                                .setSize(Size.LG)
                                .addModule(new HeaderModule(new PlainTextElement(server+" 当前状态："+ ss.getStatus(), false)))
                                .newCard()
                                .setTheme(Theme.NONE)
                                .setSize(Size.LG)
                                .addModule(new ContextModule.Builder().add(new PlainTextElement("剑三鸽鸽 ver.Remake 2.0", false)).build())
                                .addModule(DividerModule.INSTANCE)
                                .addModule(context)
                                .build());
                    }else{
                        card.add(new CardBuilder()
                                .setSize(Size.LG)
                                .setTheme(Theme.DANGER)
                                .addModule(new HeaderModule(new PlainTextElement("服务器未响应")))
                                .build());
                    }
                    break;
                //endregion
                //region 百战相关实现
                case "百战":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/active/monster"),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 沙盘相关实现
                case "沙盘":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/server/sand?server="+server+"&desc=我只是个平凡的鸽鸽罢了"),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 公告相关实现
                case "公告":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/web/news/announce"),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 赞助相关实现
                case "捐赠":
                case "赞助":
                    initSaohua();
                    card.add(new CardBuilder()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule("赞助我继续持久运营！！！"))
                            .addModule(new SectionModule(new MarkdownElement("`请不要赞助超过市面上机器人价格的数额哦<br/>我写的东西不如各位大佬的<br/>如果需要诸恶推送，请大佬们使用（建议 1）指令，如果人多我将会用赞助费用去整这个`")))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new MarkdownElement("为了安全这里使用的超链接！如果你需要二维码可以私聊我拿！")))
                            .addModule(new SectionModule(new MarkdownElement("[手机支付宝请点我](https://qr.alipay.com/fkx11820uxa8jopmhra2t3a)")))
                            .addModule(new SectionModule(new MarkdownElement("[手机微信点我](wxp://f2f0oCtITFnGlOz8ob32HoRT55B4sM26PQhrmaTAHhZqAi4)")))
                            .newCard()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(new SectionModule(new PlainTextElement("记得备注写好自己的KOOK或者游戏内昵称哦"),null,null))
                            .addModule(new SectionModule(new PlainTextElement("后面会加一个赞助名单感谢各位支持的大佬们！"),null,null))
                            .build());
                //endregion
            }
            imagesList.clear();
        } catch (Exception e) {
            e.printStackTrace();
            card.add(new CardBuilder()
                    .setSize(Size.LG)
                    .setTheme(Theme.DANGER)
                    .addModule(new HeaderModule(new PlainTextElement("坏起来了！你要用的这个查询结果出不来了！机器人内部出毛病了！快叫鸽鸽来修！")))
                    .build());
        }
        return card;
    }

    public static List<MultipleCardComponent> multiCommand(String[] command,String userID,String guildID,String server) {
        try {
            initSaohua();
            switch (command[0]) {
                // region 绑定服务器
                case "绑定":
                    String tempServer = "";
                    switch (command[1]){
                        case "双二":
                        case "飞龙":
                        case "飞龙在天":
                            tempServer = "飞龙在天";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "青梅":
                        case "青梅煮酒":
                            tempServer = "青梅煮酒";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "天鹅坪":
                            tempServer = "天鹅坪";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "破阵子":
                            tempServer = "破阵子";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "绝代天骄":
                            tempServer = "绝代天骄";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "横刀断浪":
                            tempServer = "横刀断浪";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "双梦":
                        case "双梦镇":
                        case "梦江南":
                            tempServer = "梦江南";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "唯满侠":
                        case "唯我独尊":
                            tempServer = "唯我独尊";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "斗转星移":
                            tempServer = "斗转星移";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "乾坤一掷":
                            tempServer = "乾坤一掷";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "圣墓山":
                        case "电五":
                        case "幽月轮":
                            tempServer = "幽月轮";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "剑胆琴心":
                            tempServer = "剑胆琴心";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "蝶恋花":
                            tempServer = "蝶恋花";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "龙争虎斗":
                            tempServer = "龙争虎斗";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "长安城":
                            tempServer = "长安城";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用"+robot),null,null))
                                            .build()
                            );
                            break;
                        default:
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.WARNING)
                                            .addModule(new SectionModule(new PlainTextElement("服务器绑定失误，请尝试输入服务器官方全称，"),null,null))
                                            .build()
                            );
                            System.out.println("又触发-1！！！");
                            System.out.println("用户ID: "+userID+" 尝试的绑定的服务器: "+command[1]);
                            return card;
                    }
                    List<String> tempGuild = new ArrayList<>();
                    List<Map<String, List<String>>> blindServerList = (List<Map<String, List<String>>>) settings.get("blind_server");
                    int blindServerListIndex = -1;
                    for (int i = 0; i < blindServerList.size(); i++) {
                        Map.Entry<String, List<String>> entry = blindServerList.get(i).entrySet().iterator().next();
                        String key = entry.getKey();
                        if (key.equals(tempServer)){
                            tempGuild = blindServerList.get(i).get(tempServer);
                            blindServerListIndex = i;
                        }
                    }
                    tempGuild.add(guildID);
                    blindServerList.get(blindServerListIndex).put(tempServer,tempGuild);
                    settings.put("blind_server", blindServerList);
                break;
                //endregion
                //region 建议
                case "建议":
                    card.add(new CardBuilder()
                            .setSize(Size.LG)
                            .setTheme(Theme.SUCCESS)
                            .addModule(new SectionModule(new PlainTextElement( "意见反馈成功！"),null,null))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("作者将会通过机器人私聊回复当前建议是否可用！！！"),null,null))
                            .build());
                    System.out.println("用户【"+userID+"】建议："+command[1]);
                    break;
                //endregion
                //region 屏蔽广告！！
                case "屏蔽":
                    for (String adminAcc :
                            (List<String>)settings.get("admin")) {
                        if (userID.equals(adminAcc)) {
                            List<String> maskWord = (List<String>)settings.get("MaskedWords");
                            maskWord.add(command[1]);
                            settings.put("MaskedWords",maskWord);
                            card.add(new CardBuilder()
                                    .setSize(Size.LG)
                                    .setTheme(Theme.SUCCESS)
                                    .addModule(new SectionModule(new PlainTextElement("欢迎【"+command[1]+"】加入屏蔽豪华大礼包！"),null,null))
                                    .addModule(DividerModule.INSTANCE)
                                    .addModule(new SectionModule(new PlainTextElement("将在下一次机器人重启中固定屏蔽词！"),null,null))
                                    .build());
                        }
                    }
                    break;
                //endregion
                //region 添加管理员
                case "添加管理":
                    Boolean isAdmin = false;
                    List<String> adminID = (List<String>)settings.get("admin");
                    if(command[1].contains("(met)")){

                    }else{
                        card.add(new CardBuilder()
                                .setSize(Size.LG)
                                .setTheme(Theme.DANGER)
                                .addModule(new SectionModule(new PlainTextElement("暂不支持以此种方式添加管理"),null,null))
                                .build());
                        return card;
                    }
                    String newAdmin = command[1].replaceAll("\\(met\\)", "");
                    for (String adminAcc :
                            (List<String>)settings.get("admin")) {
                        if (userID.equals(adminAcc)) {
                            isAdmin = true;
                            card.add(new CardBuilder()
                                    .setSize(Size.LG)
                                    .setTheme(Theme.SUCCESS)
                                    .addModule(new SectionModule(new PlainTextElement("欢迎用户ID："+newAdmin+"成为咕咕管理员！！！"),null,null))
                                    .addModule(DividerModule.INSTANCE)
                                    .addModule(new SectionModule(new PlainTextElement("将在下一次机器人正常重启中固定管理员资格！"),null,null))
                                    .build());
                        }
                    }
                    if (isAdmin){
                        adminID.add(newAdmin);
                        settings.put("admin",adminID);
                    }else{
                        card.add(new CardBuilder()
                                .setSize(Size.LG)
                                .setTheme(Theme.DANGER)
                                .addModule(new SectionModule(new PlainTextElement("？你要造反？"),null,null))
                                .build());
                    }
                    break;
                //endregion
                //region 外观相关实现
                case "外观":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/trade/record?name="+command[1]),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 奇遇查询相关实现
                case "奇遇":
                    initSaohua();
                    if (command.length>=3){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/luck/adventure?server="+command[1]+"&name="+command[2]+"&filter=1"),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/luck/adventure?server="+server+"&name="+command[1]+"&filter=1"),newType);
                    }
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region JJC战绩
                case "竞技场":
                case "名剑":
                case "战绩":
                case "JJC":
                    // JJC XXXX XXXX 22
                    initSaohua();
                    if (command.length >= 4){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/match/recent?server="+command[1]+"&name="+command[2]+"&robot="+robot+"&mode="+command[3]),newType);
                    }else if (command.length <4){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/match/recent?server="+server+"&name="+command[1]+"&mode="+command[2]),newType);
                    }
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 金价相关实现
                case "金价":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/trade/demon?server="+command[1]),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new HeaderModule(new PlainTextElement(server+" 目前金价！",false)))
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 阵眼相关实现（暂未写）
                case "阵眼":
                    initSaohua();

                    break;
                //endregion
                //region 团队招募相关实现
                case "团队招募":
                case "招募":
                    initSaohua();
                    if (command.length>=3){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/member/recruit?server="+command[1]+"&keyword="+command[2]),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/member/recruit?server="+command[1]),newType);
                        if (results.getCode() != 200){
                            results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/member/recruit?server="+server+"&keyword="+command[1]),newType);
                        }
                    }
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new HeaderModule(new PlainTextElement("目前的团队招募来辣！",false)))
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                    //region 过时的团队招募样式
//                    Type tempType = new TypeToken<Result<ServerTeam>>(){}.getType();
//                    Result<ServerTeam> rst = null;
//                    if (command.length>=3){
//                        rst = gson.fromJson(HttpTool.getData("https://v7.jx3api.com/data/member/recruit?server="+command[1]+"&keyword="+command[2]+"&token="+token), tempType);
//                    }else{
//                        rst = gson.fromJson(HttpTool.getData("https://v7.jx3api.com/data/member/recruit?server="+server+"&keyword="+command[1]+"&token="+token), tempType);
//                    }
//                    ServerTeam st = rst.getData();
//                    card.add(new CardBuilder()
//                            .setTheme(Theme.PRIMARY)
//                            .setSize(Size.LG)
//                            .addModule(new HeaderModule(new PlainTextElement(server+"目前的团队招募来辣！已经帮你排除掉金团辣！",false)))
//                            .addModule(new HeaderModule(new PlainTextElement("以下是有关"+command[1]+"的团队招募信息辣！",false)))
//                            .newCard()
//                            .setTheme(Theme.NONE)
//                            .setSize(Size.LG)
//                            .addModule(context)
//                            .build());
//                    for (TeamActivity ta:
//                            st.getData()) {
//                        boolean mask = false;
//                        for (String result :
//                                MaskedWords) {
//                            if (ta.getContent().contains(result)) {
//                                mask = true;
//                            }
//                        }
//                        if (mask){
//                        }else{
//                            card.add(new CardBuilder()
//                                    .setTheme(Theme.PRIMARY)
//                                    .setSize(Size.LG)
//                                    .addModule(new SectionModule(new PlainTextElement("活动名称："+ta.getActivity(),false),null,null))
//                                    .addModule(new SectionModule(new PlainTextElement("最低要求等级："+ta.getLevel(),false),null,null))
//                                    .addModule(new SectionModule(new PlainTextElement("队长："+ta.getLeader(),false),null,null))
//                                    .addModule(new SectionModule(new PlainTextElement("人数："+ta.getNumber()+"/"+ta.getMaxNumber(),false),null,null))
//                                    .addModule(new SectionModule(new PlainTextElement("招募信息："+ta.getContent(),false),null,null))
//                                    .build());
//                        }
//                    }
//                    break;
                    //endregion
                //endregion
                //region 装备查询
                case "查询":
                    initSaohua();
                    if (command.length>=3){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/role/attribute?server="+command[1]+"&name="+command[2]),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/role/attribute?server="+server+"&name="+command[1]),newType);
                    }
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 副本击杀记录
                case "副本击杀":
                case "副本进度":
                case "击杀记录":
                case "进度":
                    initSaohua();
                    if (command.length>=3){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/role/teamCdList?server="+command[1]+"&name="+command[2]),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/teamCdList?server="+server+"&name="+command[1]),newType);
                    }
                    switch (results.getCode()) {
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(), "剑三咕咕", false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"), null, null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 开服状态查询
                case "开服":
                    initSaohua();
                    Type tempType = new TypeToken<Result<ServerSwitch>>(){}.getType();
                    Result<ServerSwitch> resultServer = gson.fromJson(HttpTool.getData("http://localhost:8080/api/serverCheck?server="+command[1]), tempType);
                    if (resultServer.getCode() == 200){
                        ServerSwitch ss = resultServer.getData();
                        card.add(new CardBuilder()
                                .setTheme(Theme.NONE)
                                .setSize(Size.LG)
                                .addModule(new HeaderModule(new PlainTextElement(command[1]+" 当前状态："+ ss.getStatus(), false)))
                                .newCard()
                                .setTheme(Theme.NONE)
                                .setSize(Size.LG)
                                .addModule(new ContextModule.Builder().add(new PlainTextElement("剑三鸽鸽 ver.Remake 2.0", false)).build())
                                .addModule(DividerModule.INSTANCE)
                                .addModule(context)
                                .build());
                    }else{
                        card.add(new CardBuilder()
                                .setSize(Size.LG)
                                .setTheme(Theme.DANGER)
                                .addModule(new HeaderModule(new PlainTextElement("服务器未响应")))
                                .build());
                    }
                    break;
                //endregion
                //region 烟花相关实现
                case "烟花":
                    initSaohua();
                    if (command.length>=3){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/watch/record?server="+command[1]+"&name="+command[2]),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/watch/record?server="+server+"&name="+command[1]),newType);
                    }
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 沙盘相关实现
                case "沙盘":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/server/sand?server="+command[1]+"&desc=我只是个平凡的鸽鸽罢了"),newType);
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 成就相关实现
                case "成就":
                    initSaohua();
                    if(command.length >=4){
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/role/achievement?server="+command[1]+"&role="+command[2]+"&name="+command[3]),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("http://localhost:8080/image/api/role/achievement?server="+server+"&role="+command[1]+"&name="+command[2]),newType);
                    }
                    switch (results.getCode()){
                        case 200:
                            imagesList.add(new ImageElement(results.getData().getUrl(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 宏相关实现
                case "宏":
                    Type context = new TypeToken<Result<MacroJx3api>>(){}.getType();
                    Result<MacroJx3api> macroJx3apiResult = gson.fromJson(HttpTool.getData("http://localhost:8080/api/macros/jx3api?kungfu="+command[1]),context);
                    if (macroJx3apiResult.getCode() == 200){
                        MacroJx3api macroJx3api = macroJx3apiResult.getData();
                        card.add(new CardBuilder()
                                .setTheme(Theme.SUCCESS)
                                .setSize(Size.LG)
                                .addModule(new HeaderModule(new PlainTextElement(command[1]+"宏来了！！！")))
                                .addModule(new SectionModule(new PlainTextElement(macroJx3api.getContext()),null,null))
                                .build());
                    }else{
                        card.add(new CardBuilder()
                                .setTheme(Theme.DANGER)
                                .setSize(Size.LG)
                                .addModule(new SectionModule(new PlainTextElement("参数错误！或者服务器未响应！"),null,null))
                                .build());
                    }
                    break;
                //endregion
            }
            imagesList.clear();
        }catch(Exception e){
            card.add(new CardBuilder()
                    .setSize(Size.LG)
                    .setTheme(Theme.DANGER)
                    .addModule(new HeaderModule(new PlainTextElement("坏起来了！你要用的这个查询结果出不来了！试着换一个说法或者找鸽子来看看机器人是不是出毛病了！")))
                    .build());
            e.printStackTrace();
        }
        return card;
    }

    //不管成功与否，都去请求一次骚话或者舔狗日记！！！
    static void initSaohua(){
        try{
            Type contextType = new TypeToken<Result<Saohua>>(){}.getType();
            Result<Saohua> saohuaResult = null;
            //简单的随机判断
            if (Math.random()>0.5){
                context = new ContextModule.Builder().add(new PlainTextElement("如果你觉得好用的话，可以输入：捐赠  来支持我继续运营哦")).build();
            }else{
                saohuaResult = gson.fromJson(HttpTool.getData("https://www.jx3api.com/data/saohua/random"),contextType);
                context = new ContextModule.Builder().add(new PlainTextElement(saohuaResult.getData().getText())).build();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
