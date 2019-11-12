package io.bitchat.im.client;

import cn.hutool.core.util.NumberUtil;
import io.bitchat.client.Client;
import io.bitchat.im.BaseResult;
import io.bitchat.im.ListResult;
import io.bitchat.im.client.func.*;
import io.bitchat.im.message.MessageType;
import io.bitchat.im.user.User;

import java.util.List;
import java.util.Scanner;

/**
 * @author houyi
 */
public class ClientService {

    private RegisterFunc registerFunc;
    private LoginFunc loginFunc;
    private MsgFunc msgFunc;
    private UserFunc userFunc;

    public ClientService(Client client) {
        BaseFunc baseFunc = new BaseFunc(client);
        this.registerFunc = new RegisterFunc(baseFunc);
        this.loginFunc = new LoginFunc(baseFunc);
        this.msgFunc = new MsgFunc(baseFunc);
        this.userFunc = new UserFunc(baseFunc);
    }

    public void doCli() {
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
                case Cli.REGISTER: {
                    register(msg);
                }
                break;
                case Cli.LOGIN: {
                    login(msg);
                }
                break;
                case Cli.LIST_USER: {
                    listOnlineUser(msg);
                }
                break;
                case Cli.P2P_CHAT: {
                    p2pChat(msg);
                }
                break;
                case Cli.GROUP_CHAT: {
                    groupChat(msg);
                }
                break;
                default: {
                    showUsage();
                }
                break;
            }
        }
    }

    public void register(String msg) {
        String[] cmd = msg.split(" ");
        int cmdLen = 3;
        if (cmd.length == cmdLen) {
            String userName = cmd[1];
            String password = cmd[2];
            BaseResult baseResult = registerFunc.register(userName, password);
            if (baseResult.isSuccess()) {
                System.out.println("Register success");
            } else {
                System.out.println("Register failed cause: " + baseResult.getErrorMsg());
            }
            prompt();
        } else {
            showUsage();
        }
    }

    public void login(String msg) {
        String[] cmd = msg.split(" ");
        int cmdLen = 3;
        if (cmd.length == cmdLen) {
            String userName = cmd[1];
            String password = cmd[2];
            BaseResult baseResult = loginFunc.login(userName, password);
            if (baseResult.isSuccess()) {
                System.out.println("Login success");
            } else {
                System.out.println("Login failed cause: " + baseResult.getErrorMsg());
            }
            prompt();
        } else {
            showUsage();
        }
    }

    public void listOnlineUser(String msg) {
        String[] cmd = msg.split(" ");
        int cmdLen = 1;
        if (cmd.length == cmdLen) {
            ListResult<User> listResult = userFunc.getOnlineFriends(1);
            if (listResult.isSuccess()) {
                List<User> userList = listResult.getContent();
                System.out.println("userName(userId)");
                System.out.println("----------------");
                for (User user : userList) {
                    System.out.println(user.getUserName() + "(" + user.getUserId() + ")");
                }
            } else {
                System.out.println("List online user failed cause: " + listResult.getErrorMsg());
            }
            prompt();
        } else {
            showUsage();
        }
    }

    public void p2pChat(String msg) {
        String[] cmd = msg.split(" ");
        int cmdLen = 3;
        if (cmd.length == cmdLen) {
            String partnerId = cmd[1];
            String message = cmd[2];
            if (!NumberUtil.isLong(partnerId)) {
                showUsage();
                return;
            }
            BaseResult baseResult = msgFunc.sendP2pMsg(Long.parseLong(partnerId), MessageType.TEXT, message);
            if (baseResult.isSuccess()) {
                System.out.println("Send p2p message success");
            } else {
                System.out.println("Send p2p message failed cause: " + baseResult.getErrorMsg());
            }
            prompt();
        } else {
            showUsage();
        }
    }

    public void groupChat(String msg) {
        String[] cmd = msg.split(" ");
        int cmdLen = 3;
        if (cmd.length == cmdLen) {
            String groupId = cmd[1];
            String message = cmd[2];
            if (!NumberUtil.isLong(groupId)) {
                showUsage();
                return;
            }
            BaseResult baseResult = msgFunc.sendGroupMsg(Long.parseLong(groupId), MessageType.TEXT, message);
            if (baseResult.isSuccess()) {
                System.out.println("Send group message success");
            } else {
                System.out.println("Send group message failed cause: " + baseResult.getErrorMsg());
            }
            prompt();
        } else {
            showUsage();
        }
    }

    private void showUsage() {
        usage();
        prompt();
    }

    private void usage() {
        System.out.println("Usage: <cmd> [options]");
        System.out.println("\t<-rg>\t[userName ]\t[password]\t\t(register)");
        System.out.println("\t<-lo>\t[userName ]\t[password]\t\t(login)");
        System.out.println("\t<-lu>\t\t\t\t\t\t\t\t(list online user)");
        System.out.println("\t<-pc>\t[partnerId]\t[message ]\t\t(p2p chat)");
//        System.out.println("\t<-gc>\t[groupId  ]\t[message ]\t\t(group chat)");
    }

    private void prompt() {
        System.out.println(Cli.PROMPT);
    }


    private interface Cli {
        String PROMPT = "bitchat> ";
        String BYE = "bye";
        /**
         * register command
         * -rg <userName> <password>
         */
        String REGISTER = "-rg";
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
