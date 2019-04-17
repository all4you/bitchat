package io.bitchat.client;

import cn.hutool.core.util.NumberUtil;
import io.bitchat.core.Listener;
import io.bitchat.core.Carrier;
import io.bitchat.core.client.Client;
import io.bitchat.core.lang.enums.MessageType;
import io.bitchat.core.server.ServerAttr;

import java.util.Scanner;

/**
 * @author houyi
 */
public class DirectConnectServerClientApplication {

    public static void main(String[] args) {
        Client client = SimpleClientFactory.getInstance().newClient(ServerAttr.getLocalServer(8864));
        client.connect();
        usage();
        prompt();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String msg = scanner.nextLine();
            if (Cli.BYE.equals(msg)) {
                System.out.println("bye bye!");
                System.exit(1);
            }
            handle(msg, client);
        }
    }


    private static void handle(String msg, Client client) {
        String[] cmd = msg.split(" ");
        if (cmd.length == 3) {
            switch (cmd[0]) {
                case Cli.LOGIN: {
                    client.login(cmd[1], cmd[2], new Listener<Carrier<String>>() {
                        @Override
                        public void onEvent(Carrier<String> event) {
                            if (event.isSuccess()) {
                                System.out.println("Login success");
                            } else {
                                System.out.println("Login failed cause: " + event.getErrorMsg());
                            }
                            prompt();
                        }
                    });
                }
                break;
                case Cli.P2P_CHAT: {
                    if (!NumberUtil.isLong(cmd[1])) {
                        usage();
                        prompt();
                        return;
                    }
                    client.sendP2pMsg(Long.parseLong(cmd[1]), MessageType.TEXT, cmd[2], new Listener<Carrier<String>>() {
                        @Override
                        public void onEvent(Carrier<String> event) {
                            if (event.isSuccess()) {
                                System.out.println("Send p2p message success");
                            } else {
                                System.out.println("Send p2p message failed cause: " + event.getErrorMsg());
                            }
                            prompt();
                        }
                    });
                }
                break;
                case Cli.GROUP_CHAT: {
                    if (!NumberUtil.isLong(cmd[1])) {
                        usage();
                        prompt();
                        return;
                    }
                    client.sendGroupMsg(Long.parseLong(cmd[1]), MessageType.TEXT, cmd[2], new Listener<Carrier<String>>() {
                        @Override
                        public void onEvent(Carrier<String> event) {
                            if (event.isSuccess()) {
                                System.out.println("Send group message success");
                            } else {
                                System.out.println("Send group message failed cause: " + event.getErrorMsg());
                            }
                            prompt();
                        }
                    });
                }
                break;
                default:
                    break;
            }
        } else {
            usage();
            prompt();
        }
    }


    private static void prompt() {
        System.out.println(Cli.PROMPT);
    }

    private static void usage() {
        System.out.println("Usage: <cmd> [options]");
        System.out.println("\t<-lo>\t[userName ]\t[password]\t\t(login)");
        System.out.println("\t<-pc>\t[partnerId]\t[message ]\t\t(p2p chat)");
//        System.out.println("\t<-gc>\t[groupId  ]\t[message ]\t\t(group chat)");
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
