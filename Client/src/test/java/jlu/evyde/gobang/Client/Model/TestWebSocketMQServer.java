package jlu.evyde.gobang.Client.Model;

import jlu.evyde.gobang.Client.Controller.Utils;
import org.java_websocket.client.WebSocketClient;
import org.junit.Test;
import oshi.util.Util;

import java.util.*;

import static java.lang.Thread.sleep;
import static jlu.evyde.gobang.Client.Controller.Utils.*;
import static org.junit.Assert.*;

public class TestWebSocketMQServer {
    @Test
    public void normalTest() {
        // construct 2 ui client, 1 logic server
        int port = Utils.generateRandomInt(8000, 65535);

        MQBrokerServer server = startTestServer(port);
        List<MQMessage> ui1List = new ArrayList<>();
        List<MQMessage> ui2List = new ArrayList<>();
        List<MQMessage> logicList = new ArrayList<>();

        try {
            sleep(50);
        } catch (Exception e) {

        }

        TestWebSocketClient ui1 = createTestClient(port, ui1List, MQProtocol.Group.GAMER);
        TestWebSocketClient ui2 = createTestClient(port, ui2List, MQProtocol.Group.GAMER);
        LogicServer logic = new LogicServer(logicList, port);

        try {
            sleep(50);
        } catch (Exception e) {

        }
        ui1.connect();
        ui2.connect();
        try {
            sleep(1000);
        } catch (Exception e) {

        }
        ui1.send("");
        ui2.send("");
        try {
            sleep(1000);
        } catch (Exception e) {

        }
        assertEquals(ui1List.get(1), ui2List.get(1));
        if (ui1List.get(0).chess.getColor().equals(SystemConfiguration.getFIRST())) {
            // ui1 send, all will receive.
            ui1.send(MQMessage.generateRandomMQMessage(MQProtocol.Group.GAMER, ui1List.get(0).chess.getColor()), MQProtocol.Head.PRODUCE);
            try {
                sleep(500);
            } catch (Exception e) {

            }
            assertEquals(ui1List.get(2), ui2List.get(2));
            assertEquals(ui1List.get(2).chess, logicList.get(1).chess);
            assertEquals(ui1List.get(2).code, logicList.get(1).code);
            assertEquals(ui1List.get(2).status, logicList.get(1).status);
            assertNotEquals(ui1List.get(2), logicList.get(1));
        } else {
            // ui2 send, all will receive.
            ui2.send(MQMessage.generateRandomMQMessage(MQProtocol.Group.GAMER, ui2List.get(0).chess.getColor()),
                    MQProtocol.Head.PRODUCE);
            try {
                sleep(500);
            } catch (Exception e) {

            }
            assertEquals(ui1List.get(2), ui2List.get(2));
            assertEquals(ui2List.get(2).chess, logicList.get(1).chess);
            assertEquals(ui2List.get(2).code, logicList.get(1).code);
            assertEquals(ui2List.get(2).status, logicList.get(1).status);
            assertNotEquals(ui2List.get(2), logicList.get(1));
        }

        // logic send, except for logic, all will receive
        logic.send(MQMessage.generateRandomMQMessage(MQProtocol.Group.LOGIC_SERVER), MQProtocol.Head.PRODUCE);
        try {
            sleep(50);
        } catch (Exception e) {

        }
        assertEquals(ui1List.get(1), ui2List.get(1));
        assertEquals(2, logicList.size());
    }

//    @Test
//    public void randomlyTest() {
//        int port = Utils.generateRandomInt(8000, 65535);
//        MQBrokerServer mbs = startTestServer(port);
//        List<MQMessage> actualLogicMessageList = new LinkedList<>();
//        List<MQMessage> expectedLogicMessageList = new LinkedList<>();
//
//        TestWebSocketClient logicServer = createTestClient(port, actualLogicMessageList, MQProtocol.Group.LOGIC_SERVER);
//
//        HashSet<TestWebSocketClient> uis = new HashSet<>();
//        List<MQMessage> expectedUIMessageList = new LinkedList<>();
//        HashMap<TestWebSocketClient, List<MQMessage>> actualUIMessageList = new HashMap<>();
//
//        assertNotNull(logicServer);
//
//        try {
//            Util.sleep(500);
//        } catch (Exception e) {
//
//        }
//
//        logicServer.connect();
//
//        try {
//            Util.sleep(50);
//        } catch (Exception e) {
//
//        }
//
//
//        int times = 500;
//
//        while (times-- > 0) {
//            int choice = Utils.generateRandomInt(0, 6);
//            if (choice == 0) {
//                System.err.println("Register new UI.");
//                // Register a new UI
//                List<MQMessage> uil = new LinkedList<>();
//                TestWebSocketClient ui = createTestClient(port, uil, MQProtocol.Group.GAMER);
//                ui.connect();
//                int i = 0;
//                while (uil.size() != expectedUIMessageList.size()) {
//                    try {
//                        Util.sleep(500);
//                    } catch (Exception e) {
//
//                    }
//                    if (i++ > 20) {
//                        System.err.println("????????????: " + expectedUIMessageList.size());
//                        System.err.println("????????????: " + uil.size());
//                        throw new AssertionError();
//                    }
//                }
//                uis.add(ui);
//                actualUIMessageList.put(ui, uil);
//                try {
//                    Util.sleep(50);
//                } catch (Exception e) {
//
//                }
//            } else if (choice == 1) {
//                System.err.println("Produce from UI.");
//                // Produce from UI
//                if (uis.isEmpty()) {
//                    continue;
//                }
//                MQMessage m = MQMessage.generateRandomMQMessage(MQProtocol.Group.GAMER);
//                expectedUIMessageList.add(m);
//                expectedLogicMessageList.add(m);
//                for (TestWebSocketClient c: uis) {
//                    if (c != null && !c.isClosed()) {
//                        c.send(m, MQProtocol.Head.PRODUCE);
//                        break;
//                    }
//                }
//                try {
//                    Util.sleep(50);
//                } catch (Exception e) {
//
//                }
//                for (TestWebSocketClient w: uis) {
//                    if (w != null && !w.isClosed()) {
//                        Utils.assertListEquals(expectedUIMessageList, actualUIMessageList.get(w));
//                    }
//                }
//                Utils.assertListEquals(expectedLogicMessageList, actualLogicMessageList);
//            } else if (choice == 2) {
//                // Produce from Logic
//                System.err.println("Produce from logic.");
//                MQMessage m = MQMessage.generateRandomMQMessage(MQProtocol.Group.LOGIC_SERVER);
//                expectedUIMessageList.add(m);
//                if (!logicServer.isClosed()) {
//                    logicServer.send(m, MQProtocol.Head.PRODUCE);
//                } else {
//                    continue;
//                }
//                try {
//                    Util.sleep(50);
//                } catch (Exception e) {
//
//                }
//                for (List<MQMessage> i: actualUIMessageList.values()) {
//                    Utils.assertListEquals(expectedUIMessageList, i);
//                }
//                Utils.assertListEquals(expectedLogicMessageList, actualLogicMessageList);
//            } else if (choice == 3) {
//                // UI reconnect
//                // In this case, reconnect is same as establish a new connection, so skip this test.
//            } else if (choice == 4) {
//                System.err.println("Logic server reconnect.");
//                // Logic Server reconnect
//                if (logicServer != null && !logicServer.isClosed()) {
//                    logicServer.close();
//                    actualLogicMessageList.clear();
//                    logicServer = createTestClient(port, actualLogicMessageList, MQProtocol.Group.LOGIC_SERVER);
//                    try {
//                        Util.sleep(50);
//                        logicServer.connect();
//                        Util.sleep(100);
//                        //logicServer.send(MQMessage.constructRegisterMessage(MQProtocol.MQSource.LOGIC));
//                        Util.sleep(100);
//                    } catch (Exception e) {
//                        continue;
//                    }
//                    assertListEquals(expectedLogicMessageList, actualLogicMessageList);
//                }
//            } else if (choice == 5) {
//                System.err.println("Remove random UI.");
//                // UI disconnect
//                if (uis.isEmpty()) {
//                    continue;
//                }
//                int which = Utils.generateRandomInt(0, uis.size());
//                TestWebSocketClient remove = null;
//                for (TestWebSocketClient w: uis) {
//                    if (which-- <= 2) {
//                        remove = w;
//                    }
//                }
//                uis.remove(remove);
//                actualUIMessageList.remove(remove);
//                remove.close();
//            }
//        }
//
//        closeServer(mbs);
//    }
}
