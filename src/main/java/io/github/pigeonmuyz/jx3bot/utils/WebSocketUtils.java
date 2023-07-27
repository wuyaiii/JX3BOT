package io.github.pigeonmuyz.jx3bot.utils;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.github.pigeonmuyz.jx3bot.Main;
import io.github.pigeonmuyz.jx3bot.entity.*;
import org.glassfish.tyrus.client.ClientManager;
import snw.jkook.JKook;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;

import javax.websocket.*;
import java.net.URI;
import java.net.http.WebSocket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ClientEndpoint
public class WebSocketUtils implements WebSocket.Listener {
    private Session session;
    private Gson gson = new Gson();
    private URI uri;

    private boolean wssStatus = false;

    public WebSocketUtils(URI uri){
        this.uri = uri;
        connect(); // 建立初始连接
    }

    private void connect() {
        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(this, uri);
            wssStatus = true;
        } catch (Exception e) {
            System.out.println("WebSocket连接失败: " + e.getMessage());
            wssStatus = false;
            scheduleReconnect(); // 连接失败后，开始定时重连
        }
    }

    private void scheduleReconnect() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connect(); // 定时器触发时，进行重连
                if (wssStatus){
                    timer.cancel();
                }
            }
        }, 5000); // 重连间隔为5秒，你可以根据需要调整这个时间
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WSS服务已经开启");
        System.out.println("当前SessionID："+session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WSS服务已经关闭");
        this.session = null;
        wssStatus = false;
        scheduleReconnect(); // 连接关闭后，开始定时重连
    }

    @OnMessage
    public void onMessage(String message) {

        // 处理接收到的消息
//        System.out.println("-----------------");
//        System.out.println(message);
//        System.out.println("-----------------");

        JsonElement jsonElement = JsonParser.parseString(message);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int code = jsonObject.get("action").getAsInt();

        String messageType = "";

        News news;
        ServerStatus ss;
        VersionFix vf;
        ForumPost fp;

        MultipleCardComponent mcc = null;

        switch (code){
            case 2001:
                messageType = "server_status";
                Wss<ServerStatus> ssResult = gson.fromJson(message, new TypeToken<Wss<ServerStatus>>(){}.getType());
                ss = ssResult.getData();
                if (ss.getStatus() == 1 && ss.getServer().equals("飞龙在天")){
                    mcc = new CardBuilder()
                            .setTheme(Theme.SUCCESS)
                            .setSize(Size.LG)
                            .addModule(new SectionModule(new PlainTextElement("游戏现在开服了！！！"),null,null))
                            .build();
                }else if (ss.getStatus() == 0 && ss.getServer().equals("飞龙在天")){
                    mcc = new CardBuilder()
                            .setTheme(Theme.WARNING)
                            .setSize(Size.LG)
                            .addModule(new SectionModule(new PlainTextElement("游戏现在开始维护了捏"),null,null))
                            .build();
                }
                break;
            case 2002:
                messageType = "news";
                Wss<News> newsResult = gson.fromJson(message, new TypeToken<Wss<News>>(){}.getType());
                news = newsResult.getData();
                mcc = new CardBuilder()
                        .setTheme(Theme.WARNING)
                        .setSize(Size.LG)
                        .addModule(new SectionModule(new PlainTextElement(news.getType()+"："+news.getTitle()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("时间："+news.getDate()),null,null))
                        .addModule(new SectionModule(new MarkdownElement("[原文传送门🚪]("+news.getUrl()+")")))
                        .build();
                break;
            case 2003:
                messageType = "version_fix";
                Wss<VersionFix> vfResult = gson.fromJson(message, new TypeToken<Wss<VersionFix>>(){}.getType());
                vf = vfResult.getData();
                mcc = new CardBuilder()
                        .setTheme(Theme.WARNING)
                        .setSize(Size.LG)
                        .addModule(new HeaderModule(new PlainTextElement("客户端更新辣！！！")))
                        .addModule(new SectionModule(new PlainTextElement("原版本："+vf.getOld_version()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("新版本："+vf.getNew_version()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("更新补丁大小："+vf.getPackage_size()),null,null))
                        .build();
                break;
            case 2004:
                messageType = "forum_post";
                Wss<ForumPost> fpResult = gson.fromJson(message, new TypeToken<Wss<ForumPost>>(){}.getType());
                fp = fpResult.getData();
                mcc = new CardBuilder()
                        .setTheme(Theme.WARNING)
                        .setSize(Size.LG)
                        .addModule(new HeaderModule(new PlainTextElement("贴吧更新咯！")))
                        .addModule(new SectionModule(new PlainTextElement(fp.getTitle()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("来自 "+fp.getName()+"吧"),null,null))
                        .addModule(new SectionModule(new PlainTextElement("时间："+fp.getDate()),null,null))
                        .addModule(new SectionModule(new MarkdownElement("[原文传送门🚪]("+fp.getUri()+")")))
                        .build();
                break;
            default:
                mcc = new CardBuilder()
                        .setTheme(Theme.INFO)
                        .setSize(Size.LG)
                        .addModule(new HeaderModule(new PlainTextElement("该死的，又有新推送要改！！")))
                        .addModule(new SectionModule(new PlainTextElement(message),null,null))
                        .build();
                TextChannel tc = (TextChannel) JKook.getCore().getHttpAPI().getChannel("3715090900510071");
                tc.sendComponent(mcc);
                mcc = null;
                break;
        }

        if (mcc != null){
            for (String channelID :
                    (List<String>) Main.settings.get(messageType)) {
                TextChannel tc = (TextChannel) JKook.getCore().getHttpAPI().getChannel(channelID);
                tc.sendComponent(mcc);
            }
        }else {

        }
    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    public void close() {
        try {
            this.session.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
