package io.bitchat.client;

import cn.hutool.core.util.NumberUtil;
import io.bitchat.client.func.*;
import io.bitchat.core.Carrier;
import io.bitchat.core.Listener;
import io.bitchat.core.ServerAttr;
import io.bitchat.user.User;
import io.bitchat.message.MessageType;

import java.util.List;
import java.util.Scanner;

/**
 * @author houyi
 */
public class ClientApplication {

    public static void main(String[] args) {
        Client client = SimpleClientFactory.getInstance().newClient(ServerAttr.getLocalServer(8864));
        client.connect();

        doClientBiz(client);
    }

    private static void doClientBiz(Client client) {
        LoginFunc loginFunc = new DefaultLoginFunc(client);
        MsgFunc msgFunc = new DefaultMsgFunc(client);
        UserFunc userFunc = new DefaultUserFunc(client);
        showUsage();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();
            if (Cli.BYE.equals(msg)) {
                System.out.println("bye bye!");
                System.exit(1);
            }
            String[] cmd = msg.split(" ");
            if (cmd.length == 0) {
                showUsage();
                continue;
            }
            String cli = cmd[0];
            switch (cli) {
                case Cli.LOGIN: {
                    login(msg, loginFunc);
                }
                break;
                case Cli.LIST_USER: {
                    listOnlineUser(msg, userFunc);
                }
                break;
                case Cli.P2P_CHAT: {
                    p2pChat(msg, msgFunc);
                }
                break;
                case Cli.GROUP_CHAT: {
                    groupChat(msg, msgFunc);
                }
                break;
                default: {
                    showUsage();
                }
                break;
            }
        }
    }

    private static void login(String msg, LoginFunc loginFunc) {
        String[] cmd = msg.split(" ");
        int cmdLen = 3;
        if (cmd.length == cmdLen) {
            String userName = cmd[1];
            String password = cmd[2];
            loginFunc.login(userName, password, new Listener<Carrier<String>>() {
                @Override
                public void onEvent(Carrier<String> event) {
                    if (event.isSuccess()) {
                        System.out.println("Login success");
                    } else {
                        System.out.println("Login failed cause: " + event.getMsg());
                    }
                    prompt();
                }
            });
        } else {
            showUsage();
        }
    }

    private static void listOnlineUser(String msg, UserFunc userFunc) {
        String[] cmd = msg.split(" ");
        int cmdLen = 1;
        if (cmd.length == cmdLen) {
            userFunc.onlineUser(new Listener<Carrier<List<User>>>() {
                @Override
                public void onEvent(Carrier<List<User>> event) {
                    if (event.isSuccess()) {
                        List<User> userList = event.getData();
                        System.out.println("userName(userId)");
                        System.out.println("----------------");
                        for (User user : userList) {
                            System.out.println(user.getUserName() + "(" + user.getUserId() + ")");
                        }
                    } else {
                        System.out.println("List online user failed cause: " + event.getMsg());
                    }
                    prompt();
                }
            });
        } else {
            showUsage();
        }
    }

    private static void p2pChat(String msg, MsgFunc msgFunc) {
        String[] cmd = msg.split(" ");
        int cmdLen = 3;
        if (cmd.length == cmdLen) {
            String partnerId = cmd[1];
            String message = cmd[2];
            if (!NumberUtil.isLong(partnerId)) {
                showUsage();
                return;
            }
            msgFunc.sendP2pMsg(Long.parseLong(partnerId), MessageType.TEXT, message, new Listener<Carrier<String>>() {
                @Override
                public void onEvent(Carrier<String> event) {
                    if (event.isSuccess()) {
                        System.out.println("Send p2p message success");
                    } else {
                        System.out.println("Send p2p message failed cause: " + event.getMsg());
                    }
                    prompt();
                }
            });
        } else {
            showUsage();
        }
    }

    private static void groupChat(String msg, MsgFunc msgFunc) {
        String[] cmd = msg.split(" ");
        int cmdLen = 3;
        if (cmd.length == cmdLen) {
            String groupId = cmd[1];
            String message = cmd[2];
            if (!NumberUtil.isLong(groupId)) {
                showUsage();
                return;
            }
            msgFunc.sendGroupMsg(Long.parseLong(groupId), MessageType.TEXT, message, new Listener<Carrier<String>>() {
                @Override
                public void onEvent(Carrier<String> event) {
                    if (event.isSuccess()) {
                        System.out.println("Send group message success");
                    } else {
                        System.out.println("Send group message failed cause: " + event.getMsg());
                    }
                    prompt();
                }
            });
        } else {
            showUsage();
        }
    }

    private static void showUsage() {
        usage();
        prompt();
    }

    private static void usage() {
        System.out.println("Usage: <cmd> [options]");
        System.out.println("\t<-lo>\t[userName ]\t[password]\t\t(login)");
        System.out.println("\t<-lu>\t\t\t\t\t\t\t\t(list online user)");
        System.out.println("\t<-pc>\t[partnerId]\t[message ]\t\t(p2p chat)");
//        System.out.println("\t<-gc>\t[groupId  ]\t[message ]\t\t(group chat)");
    }

    private static void prompt() {
        System.out.println(Cli.PROMPT);
    }


    private interface Cli {
        String PROMPT = "bitchat> ";
        String BYE = "bye";
        /**
         * login command
         * -lo <userName> <password>
         */
        String LOGIN = "-lo";
        /**
         * list user
         * -lu
         */
        String LIST_USER = "-lu";
        /**
         * p2p chat
         * -pc <partnerId> <msg>
         */
        String P2P_CHAT = "-pc";
        /**
         * group chat
         * -gc <groupId> <msg>
         */
        String GROUP_CHAT = "-gc";
    }

}
