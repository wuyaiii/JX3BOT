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
    /**
     * 屏蔽词
     */
    static List<String> MaskedWords = (List<String>) settings.get("team_masked_words");
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

    static String ticket = settings.get("jx3api_ticket").toString();
    static String token = settings.get("jx3api_token").toString();

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
                    Result<Daily> rd   = gson.fromJson(HttpTool.getData("https://www.jx3api.com/data/active/current?server="+server), tempType);
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
                    // @TODO 还没做其他服的！！
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/trade/demon?server="+server+"&robot="+robot),newType);
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
                    Result<ReturnImageEntity> results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/home/flower?scale=2&server="+server+"&robot="+robot),newType);
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
                    results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/member/recruit?server="+server+"&token="+token+"&robot="+robot),newType);
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

                    ChivalrousRequest cr = gson.fromJson(HttpTool.getData("https://www.jx3api.com/data/active/chivalrous?token="+token), ChivalrousRequest.class);
                    card.add(new CardBuilder()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("楚天行侠数据它来辣！")))
                            .build());
                    Chivalrous ce = cr.getData().get("now");

                    card.add(new CardBuilder()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("现在正在"+ce.getMap()+"行侠仗义捏！")))
                            .addModule(new SectionModule(new PlainTextElement("事件："+ce.getDesc()),null,null))
                            .addModule(new SectionModule(new PlainTextElement("地点："+ce.getMap()+"·"+ce.getSite()),null,null))
                            .addModule(new SectionModule(new PlainTextElement("开始时间："+ce.getTime()),null,null))
//                            .addModule(new CountdownModule(CountdownModule.Type.DAY, LocalDateTime.of(LocalDate.now(), LocalTime.parse(ce.getTime()).plusMinutes(4)).toInstant(ZoneOffset.ofHours(8)).toEpochMilli()))
                            .build());
//                    System.out.println( LocalDateTime.of(LocalDate.now(),LocalTime.parse(ce.getTime())).toEpochSecond(ZoneOffset.UTC));
                    ce = cr.getData().get("next");
                    card.add(new CardBuilder()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("即将到"+ce.getMap()+"行侠仗义捏！")))
                            .addModule(new SectionModule(new PlainTextElement("事件："+ce.getDesc()),null,null))
                            .addModule(new SectionModule(new PlainTextElement("地点："+ce.getMap()+"·"+ce.getSite()),null,null))
                            .addModule(new SectionModule(new PlainTextElement("开始时间："+ce.getTime()),null,null))
//                            .addModule(new CountdownModule(CountdownModule.Type.DAY, LocalDateTime.of(LocalDate.now(), LocalTime.parse(ce.getTime()).plusMinutes(4)).toInstant(ZoneOffset.ofHours(8)).toEpochMilli()))
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
                            .addModule(new HeaderModule(new PlainTextElement("剑三鸽鸽 ver.Remake 1.0.1 （开发版本号：1033）")))
                            .addModule(new SectionModule(new PlainTextElement("1. 部分功能惨招删除！"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("2. 修复了部分指令会发不出的问题！！"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("3. 加入消息推送服务辣，现在可以订阅开服监控之类的东西辣！"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("4. 增加了JJC战绩查询"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("5. 即将增加赞助辣！（只收取订阅服务所需+它的1%的费用，不使用诸恶、攻防类订阅服务就完全不要钱！）"), null, null))
                            .newCard()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(context)
                            .build());
                    break;
                //endregion
                //region 帮助相关实现
                case "帮助":
                case "功能":
                case "指令":
                    initSaohua();
                    card.add(new CardBuilder()
                            .setTheme(Theme.INFO)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("通用类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("查询日常：日常"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("绑定服务器：绑定 [服务器(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("物品价格：物价 [物品名称|物品别名(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("角色装备：查询 [服务器(可选)] [玩家名字(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查询金价：金价 [服务器(可选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查询奇遇：奇遇 [服务器(可选)] [玩家名字(必选)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("订阅监控：全部订阅[首次订阅将会立即生效]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("团队招募：招募|团队招募 [副本名(可选，例：西津渡)]"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("查询日志：[日志|更新日志|Version]"), null, null))
                            .newCard()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVE类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("查宏指令：宏 [心法前两字(必选)] 该功能目前是测试状态！"), null, null))
                            .newCard()
                            .setTheme(Theme.DANGER)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVP类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("JJC战绩：JJC｜战绩 [玩家名(必选)] [模式，例如22｜33｜55(如果需要查其他服则必选)] [服务器(可选)]"), null, null))
                            .newCard()
                            .setTheme(Theme.SUCCESS)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement("PVX类", false)))
                            .addModule(DividerModule.INSTANCE)
                            .addModule(new SectionModule(new PlainTextElement("查询花价：花价 "), null, null))
                            .addModule(new SectionModule(new PlainTextElement("楚天行侠：行侠|楚天社|楚天行侠"), null, null))
                            .addModule(new SectionModule(new PlainTextElement("宠物游历：游历 [地图(必选)]"), null, null))
                            .newCard()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(new ContextModule.Builder().add(new PlainTextElement("剑三鸽鸽 ver.Remake 1.0.1", false)).build())
                            .addModule(DividerModule.INSTANCE)
                            .addModule(context)
                            .build());
                    break;
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
                        case "梦江南":
                            tempServer = "梦江南";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
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
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "斗转星移":
                            tempServer = "斗转星移";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "乾坤一掷":
                            tempServer = "乾坤一掷";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
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
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "剑胆琴心":
                            tempServer = "剑胆琴心";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "蝶恋花":
                            tempServer = "蝶恋花";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "龙争虎斗":
                            tempServer = "龙争虎斗";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
                                            .build()
                            );
                            break;
                        case "长安城":
                            tempServer = "长安城";
                            card.add(
                                    new CardBuilder()
                                            .setSize(Size.LG)
                                            .setTheme(Theme.SUCCESS)
                                            .addModule(new SectionModule(new PlainTextElement("绑定服务器："+command[1]+"成功，欢迎大佬使用+"+robot),null,null))
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
                            break;
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
                //region 物价相关实现
                case "物价":
                    initSaohua();
                    results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/trade/record?token="+token+"&name="+command[1]+"&robot="+robot),newType);
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
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/luck/adventure?token="+token+"&ticket="+ticket+"&server="+command[1]+"&name="+command[2]+"&robot="+robot),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/luck/adventure?token="+token+"&ticket="+ticket+"&server="+server+"&name="+command[1]+"&robot="+robot),newType);
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
                    initSaohua();
                    if (command.length==3){
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/match/recent?token="+token+"&ticket="+ticket+"&server="+server+"&name="+command[1]+"&robot="+robot+"&mode="+command[2]),newType);
                    }else if (command.length == 4){
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/match/recent?token="+token+"&ticket="+ticket+"&server="+command[3]+"&name="+command[1]+"&robot="+robot+"&mode="+command[2]),newType);
                    }else if (command.length <3){
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/match/recent?token="+token+"&ticket="+ticket+"&server="+server+"&name="+command[1]+"&robot="+robot),newType);
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
                    results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/trade/demon?server="+command[1]+"&robot="+robot),newType);
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
                // @TODO 团队招募未更新！！！
                case "团队招募":
                case "招募":
                    initSaohua();
                    Type tempType = new TypeToken<Result<ServerTeam>>(){}.getType();
                    Result<ServerTeam> rst = gson.fromJson(HttpTool.getData("https://www.jx3api.com/data/member/recruit?server="+server+"&keyword="+command[1]+"&token="+token), tempType);
                    ServerTeam st = rst.getData();
                    card.add(new CardBuilder()
                            .setTheme(Theme.PRIMARY)
                            .setSize(Size.LG)
                            .addModule(new HeaderModule(new PlainTextElement(server+"目前的团队招募来辣！已经帮你排除掉金团辣！",false)))
                            .addModule(new HeaderModule(new PlainTextElement("以下是有关"+command[1]+"的团队招募信息辣！",false)))
                            .newCard()
                            .setTheme(Theme.NONE)
                            .setSize(Size.LG)
                            .addModule(context)
                            .build());
                    for (TeamActivity ta:
                            st.getData()) {
                        boolean mask = false;
                        for (String result :
                                MaskedWords) {
                            if (ta.getContent().contains(result)) {
                                mask = true;
                            }
                        }
                        if (mask){
                        }else{
                            card.add(new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("活动名称："+ta.getActivity(),false),null,null))
                                    .addModule(new SectionModule(new PlainTextElement("最低要求等级："+ta.getLevel(),false),null,null))
                                    .addModule(new SectionModule(new PlainTextElement("队长："+ta.getLeader(),false),null,null))
                                    .addModule(new SectionModule(new PlainTextElement("人数："+ta.getNumber()+"/"+ta.getMaxNumber(),false),null,null))
                                    .addModule(new SectionModule(new PlainTextElement("招募信息："+ta.getContent(),false),null,null))
                                    .build());
                        }
                    }
                    break;
                //endregion
                //region 装备查询
                case "查询":
                    initSaohua();

                    if (command.length>=3){
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/role/attribute?token="+token+"&ticket="+ticket+"&server="+command[1]+"&name="+command[2]+"&robot="+robot),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/role/attribute?token="+token+"&ticket="+ticket+"&server="+server+"&name="+command[1]+"&robot="+robot),newType);
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
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/role/teamCdList?token="+token+"&ticket="+ticket+"&server="+command[1]+"&name="+command[2]+"&robot="+robot),newType);
                    }else{
                        results = gson.fromJson(HttpTool.getData("https://www.jx3api.com/view/role/teamCdList?token="+token+"&ticket="+ticket+"&server="+server+"&name="+command[1]+"&robot="+robot),newType);
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
                saohuaResult = gson.fromJson(HttpTool.getData("https://www.jx3api.com/data/saohua/content"),contextType);
            }else{
                saohuaResult = gson.fromJson(HttpTool.getData("https://www.jx3api.com/data/saohua/random"),contextType);
            }
            //装填数据
            context = new ContextModule.Builder().add(new PlainTextElement(saohuaResult.getData().getText())).build();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
