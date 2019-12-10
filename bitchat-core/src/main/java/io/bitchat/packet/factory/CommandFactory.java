package io.bitchat.packet.factory;

import io.bitchat.packet.Command;

import java.util.Map;

/**
 * @author houyi
 */
public class CommandFactory {

    public static Command newCommand(String commandName) {
        return CommandFactory.newCommand(commandName, null);
    }

    public static Command newCommand(String commandName, Map<String, Object> content) {
        Command command = new Command();
        command.setCommandName(commandName);
        command.setContent(content);
        return command;
    }

}
