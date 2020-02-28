package io.bitchat.packet.factory;

import com.alibaba.fastjson.JSON;
import io.bitchat.packet.Command;

/**
 * @author houyi
 */
public class CommandFactory {

    public static Command newCommand(String commandName) {
        return CommandFactory.newCommand(commandName, null);
    }

    public static Command newCommand(String commandName, Object content) {
        Command command = new Command();
        command.setCommandName(commandName);
        command.setContentJson(JSON.toJSONString(content));
        return command;
    }

}
