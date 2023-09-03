package io.github.pigeonmuyz.jx3bot.event;

import com.google.gson.Gson;
import io.github.pigeonmuyz.jx3bot.Main;
import io.github.pigeonmuyz.jx3bot.tools.CardTool;

import snw.jkook.JKook;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.event.pm.PrivateMessageReceivedEvent;
import snw.jkook.message.component.card.MultipleCardComponent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static io.github.pigeonmuyz.jx3bot.Main.settings;

public class MessageEvent implements Listener {
    Gson gson = new Gson();

    /**
     * 时间格式
     */
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    List<MultipleCardComponent> cardMessage;

    String server = "飞龙在天";

    @EventHandler
    public void channelMessage(ChannelMessageEvent cme){
        //判断当前服务器是否绑定
        boolean validBind = validBindServer(cme.getChannel().getGuild().getId());
        String[] commands = cme.getMessage().getComponent().toString().split(" ");
        if (commands.length>=2 && commands[0].equals("绑定") && validBind){
            cme.getMessage().reply("请勿重复绑定，重复绑定将会造成服务器资源浪费哦！");
            return;
        }
        if (commands.length >= 2 && commands[0].equalsIgnoreCase(".send")&& cme.getMessage().getSender().getId().equals("1787060816")){
            switch (commands[1]){
                case "频道":
                    TextChannel tc = (TextChannel) JKook.getHttpAPI().getChannel(commands[2]);
                    tc.sendComponent(commands[3]);
                    break;
                case "用户":
                    User user = JKook.getHttpAPI().getUser(commands[2]);
                    user.sendPrivateMessage(commands[3]);
                    break;
                case "全部订阅者":
                    Iterator<String> iterator = ((List<String>) Main.settings.get("news")).iterator();
                    List<String> elementsToRemove = new ArrayList<>(); // 用于暂存需要删除的元素
                    while (iterator.hasNext()) {
                        String channelID = iterator.next();
                        try {
                            TextChannel tempTc = (TextChannel) JKook.getCore().getHttpAPI().getChannel(channelID);
                            tempTc.sendComponent(commands[2]);
                        } catch (Exception e) {
                            System.out.println("发送失败，当前恶心人的频道ID："+channelID);
                            elementsToRemove.add(channelID); // 将需要删除的元素添加到列表中
                        }
                    }
                    // 循环结束后删除需要删除的元素
                    ((List<String>) Main.settings.get("news")).removeAll(elementsToRemove);
            }
        }
            /*
              判断指令
             */
        if (cme.getMessage().getComponent().toString().split(" ").length >=2){
                /*
                  如果用户发送指令，例：花价 xxxx
                  则代表当前指令为多层
                  则进入该方法体
                 */

//            if (commands[1].equals("绑定")){
//                cme.getMessage().reply("如果没有看到报错的消息就是绑定成功了！！");
//                cme.getMessage().sendToSource("绑定的提示消息被飞龙的臭鸽子吃了！");
//            }
            cardMessage = CardTool.multiCommand(cme.getMessage().getComponent().toString().split(" "),cme.getMessage().getSender().getId(),cme.getChannel().getGuild().getId(),server);
            if (cardMessage.isEmpty()){
                return;
            }
            for (MultipleCardComponent card:
                    cardMessage) {
                cme.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }else{
                /*
                  如果用户发送指令，例：花价
                  则代表当前指令为单层
                  则进入该方法体
                 */
            cardMessage = CardTool.singleCommand(cme.getMessage().getComponent().toString(),cme.getMessage().getSender().getId(),cme.getChannel().getId(),server);
            for (MultipleCardComponent card:
                    cardMessage) {
                cme.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }

    }

    @EventHandler
    public void privateMessage(PrivateMessageReceivedEvent pmre){
        if (!pmre.getMessage().getSender().getId().equals("3107210249")){
            System.out.println("["+sdf.format(new Date(pmre.getTimeStamp()))+"]"+pmre.getMessage().getSender().getName()+"："+pmre.getMessage().getComponent().toString());
        }

            /*
              判断指令
             */
        if (pmre.getMessage().getComponent().toString().split(" ").length >=2){
                /*
                  如果用户发送指令，例：花价 xxxx
                  则代表当前指令为多层
                  则进入该方法体
                 */
            cardMessage = CardTool.multiCommand(pmre.getMessage().getComponent().toString().split(" "),pmre.getMessage().getSender().getId(),null,server);
            for (MultipleCardComponent card:
                    cardMessage) {
                pmre.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }else{
                /*
                  如果用户发送指令，例：花价
                  则代表当前指令为单层
                  则进入该方法体
                 */
            cardMessage = CardTool.singleCommand(pmre.getMessage().getComponent().toString(),pmre.getMessage().getSender().getId(),null,server);
            for (MultipleCardComponent card:
                    cardMessage) {
                pmre.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }
    }

    Boolean validBindServer(String guildId){
        List<Map<String, List<String>>> blindServerList = (List<Map<String, List<String>>>) settings.get("blind_server");
        try{
            for (Map<String,List<String>> serverBlink:
                    blindServerList) {
                Map.Entry<String, List<String>> entry = serverBlink.entrySet().iterator().next();
                String key = entry.getKey();
                List<String> values = entry.getValue();

                // 进行条件判断
                if (values.contains(guildId)) {
                    // 执行操作
                    server = key;
                    return true;
                }else{

                }
            }
        }catch(Exception e) {
            System.out.println("绑定服务器为空捏");
        }
        return false;
    }

}
