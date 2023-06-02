package io.github.pigeonmuyz.jx3bot.event;

import com.google.gson.Gson;
import io.github.pigeonmuyz.jx3bot.Main;
import io.github.pigeonmuyz.jx3bot.tools.CardTool;
import io.github.pigeonmuyz.jx3bot.tools.HttpTool;
import snw.jkook.Core;
import snw.jkook.JKook;
import snw.jkook.entity.User;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.event.pm.PrivateMessageEvent;
import snw.jkook.event.pm.PrivateMessageReceivedEvent;
import snw.jkook.message.component.card.MultipleCardComponent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        validBindServer(cme.getChannel().getGuild().getId());
        String[] commands = cme.getMessage().getComponent().toString().split(" ");
            /*
              判断指令
             */
        if (cme.getMessage().getComponent().toString().split(" ").length >=2){
                /*
                  如果用户发送指令，例：花价 xxxx
                  则代表当前指令为多层
                  则进入该方法体
                 */
            if (cardMessage.isEmpty()){
                return;
            }
            if (commands[1].equals("绑定")){
                cme.getMessage().reply("如果没有看到报错的消息就是绑定成功了！！");
                cme.getMessage().sendToSource("绑定的提示消息被飞龙的臭鸽子吃了！");
            }
            cardMessage = CardTool.multiCommand(cme.getMessage().getComponent().toString().split(" "),cme.getMessage().getSender().getId(),cme.getChannel().getGuild().getId(),server);
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
        if (commands.length >=2 && commands[0].equalsIgnoreCase(".send" )&& cme.getMessage().getSender().getId().equals("1787060816")){
            User user = JKook.getCore().getHttpAPI().getUser(commands[1]);
            user.sendPrivateMessage(commands[2]);
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

    void validBindServer(String guildId){
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
                }
            }
        }catch(Exception e){
            System.out.println("绑定服务器为空捏");
        }
    }

}
