package io.github.pigeonmuyz.jx3bot;

import io.github.pigeonmuyz.jx3bot.event.MessageEvent;
import io.github.pigeonmuyz.jx3bot.utils.WebSocketUtils;
import org.yaml.snakeyaml.Yaml;
import snw.jkook.plugin.BasePlugin;

import java.io.*;
import java.net.URI;
import java.util.Map;

public class Main extends BasePlugin {

    public static String dataFolder = "";

    public static Map<String, Object> settings;

    public static WebSocketUtils client = new WebSocketUtils(URI.create("wss://socket.nicemoe.cn"));


    @Override
    public void onLoad() {

        saveDefaultConfig();
        dataFolder = String.valueOf(getDataFolder());
        System.out.println("剑三咕咕数据加载中");
        if (onLoadConfig(dataFolder+"/config.yml") == null){
            System.out.println("配置文件加载失败");
            getCore().shutdown();
        }else{
            settings = onLoadConfig(dataFolder+"/config.yml");
        }
        super.onLoad();
    }

    @Override
    public void onEnable() {
        System.out.println("注册消息监听器");
        getCore().getEventManager().registerHandlers(this,new MessageEvent());
    }

    @Override
    public void onDisable() {
        System.out.println("配置文件正在报错");
        Yaml yaml = new Yaml();
        try {
            String charset = "UTF-8";
            FileOutputStream fos = new FileOutputStream(new File(dataFolder+"/config.yml"));
            OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
            yaml.dump(settings, osw);
            osw.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("配置文件保存成功");
    }

    Map<String, Object> onLoadConfig(String fileRoute){
        Yaml yaml = new Yaml();
        try {
            return yaml.load(new FileInputStream(fileRoute));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}