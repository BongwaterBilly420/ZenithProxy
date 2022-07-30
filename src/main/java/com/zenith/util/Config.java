/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.zenith.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * @author DaPorkchop_
 */
public final class Config {
    public Authentication authentication = new Authentication();
    public Client client = new Client();
    public Debug debug = new Debug();
    public Gui gui = new Gui();
    public Log log = new Log();
    public Server server = new Server();
    public Websocket websocket = new Websocket();
    public Discord discord = new Discord();
    public boolean autoUpdate = true;
    public int autoUpdateCheckIntervalSeconds = 60;
    public boolean shouldReconnectAfterAutoUpdate = false;

    public static final class Authentication {
        public boolean doAuthentication = false;
        public String accountType = "msa";
        public String email = "john.doe@example.com";
        public String password = "my_secure_password";
        public String username = "Steve";
        public boolean prio = false;
    }

    public static final class Client {
        public Extra extra = new Extra();
        public Server server = new Server();
        // auto-connect proxy on process start
        // todo: might make discord command to configure this
        public boolean autoConnect = false;

        public static final class Extra {
            public AntiAFK antiafk = new AntiAFK();
            public Utility utility = new Utility();
            public AutoReconnect autoReconnect = new AutoReconnect();
            public AutoRespawn autoRespawn = new AutoRespawn();
            public Spammer spammer = new Spammer();
            public Stalk stalk = new Stalk();
            public boolean visualRangeAlert = true;
            public boolean visualRangeAlertMention = false;
            public List<String> friendList = new ArrayList<>();
            public boolean clientConnectionMessages = true;
            public boolean autoConnectOnLogin = true;
            public boolean sixHourReconnect = false;

            public static final class Stalk {
                public boolean enabled = false;
                public List<String> stalkList = new ArrayList<>();
            }

            public static final class AntiAFK {
                public Actions actions = new Actions();
                public boolean enabled = true;
                public static final class Actions {
                    public boolean walk = true;
                    public boolean swingHand = true;
                    public boolean rotate = true;
                }
            }

            public static final class Utility {
                public Actions actions = new Actions();

                public static final class Actions {
                    public AutoDisconnect autoDisconnect = new AutoDisconnect();
                    public ActiveHours activeHours = new ActiveHours();
                }

                public static final class AutoDisconnect {
                    public boolean enabled = false;
                    public boolean autoClientDisconnect = false;
                    public int health = 5;
                }

                public static final class ActiveHours {
                    public boolean enabled = false;
                    public boolean forceReconnect = false;
                    public String timeZoneId = "Universal";
                    public List<ActiveTime> activeTimes = new ArrayList<>();

                    public static class ActiveTime {
                        public int hour;
                        public int minute;

                        public static ActiveTime fromString(final String arg) {
                            final String[] split = arg.split(":");
                            final int hour = Integer.parseInt(split[0]);
                            final int minute = Integer.parseInt(split[1]);
                            ActiveTime activeTime = new ActiveTime();
                            activeTime.hour = hour;
                            activeTime.minute = minute;
                            return activeTime;
                        }

                        @Override
                        public String toString() {
                            return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
                        }

                        @Override
                        public boolean equals(Object o) {
                            if (this == o) return true;
                            if (o == null || getClass() != o.getClass()) return false;
                            ActiveTime that = (ActiveTime) o;
                            return hour == that.hour && minute == that.minute;
                        }

                        @Override
                        public int hashCode() {
                            return Objects.hash(hour, minute);
                        }

                    }
                }
            }

            public static final class AutoReconnect {
                public boolean enabled = true;
                // todo: idk delete this seems useless
                public int delaySecondsOffline = 120;
                public int delaySeconds = 120;
                // todo: delete?
                public int linearIncrease = 0;
            }

            public static final class AutoRespawn {
                public boolean enabled = true;
                public int delayMillis = 100;
            }

            public static final class Spammer {
                public int delaySeconds = 30;
                public boolean enabled = false;
                public List<String> messages = asList(
                        "/stats",
                        "/stats",
                        "/stats"
                );
            }
        }

        public static final class Server {
            public String address = "2b2t.org";
            public int port = 25565;
        }
    }

    public static final class Debug {
        public Packet packet = new Packet();
        public boolean printDataFields = false;
        public Server server = new Server();

        public static final class Packet {
            public boolean received = false;
            public boolean receivedBody = false;
            public boolean preSent = false;
            public boolean preSentBody = false;
            public boolean postSent = false;
            public boolean postSentBody = false;
        }

        public static final class Server {
            public Cache cache = new Cache();

            public static final class Cache {
                public boolean sendingmessages = true;
                public boolean unknownplayers = false;
            }
        }
    }

    public static final class Gui {
        public boolean enabled = false;
        public int messageCount = 512;
    }

    public static final class Log {
        public boolean printDebug = false;
        public boolean storeDebug = true;
    }

    public static final class Server {
        public Bind bind = new Bind();
        public int compressionThreshold = 256;
        public boolean enabled = true;
        public Extra extra = new Extra();
        public Ping ping = new Ping();
        public boolean verifyUsers = true;
        public boolean kickPrevious = false;
        public int queueWarning = 10; // Queue position to send warning message at
        public String proxyIP = "localhost";
        public int queueStatusRefreshMinutes = 5; // how often to refresh queue lengths

        public static final class Bind {
            public String address = "0.0.0.0";
            public int port = 25565;
        }

        public static final class Extra {
            public Timeout timeout = new Timeout();
            public Whitelist whitelist = new Whitelist();

            public static final class Whitelist {
                public boolean enable = true;
                public List<String> allowedUsers = asList();
                public String kickmsg = "get out of here you HECKING scrub";
            }

            public static final class Timeout {
                public boolean enable = true;
                public long millis = 30000L;
                public long interval = 100L;
            }
        }

        public static final class Ping {
            public boolean favicon = true;
            public int maxPlayers = Integer.MAX_VALUE;
        }

        public String getProxyAddress() {
            return this.proxyIP;
        }
    }

    public static final class Websocket {
        public Bind bind = new Bind();
        public Client client = new Client();
        public boolean enable = false;

        public static final class Bind {
            public String address = "0.0.0.0";
            public int port = 8080;
        }

        public static final class Client {
            public int maxChatCount = 512;
        }
    }

    public static final class Discord {
        public String token = "";
        public String channelId = "";
        public String accountOwnerRoleId = "";
        public String visualRangeMentionRoleId = "";
        public boolean enable = false;
        public String prefix = ".";
        public boolean reportCoords = false;
        // internal use for update command state persistence
        public boolean isUpdating = false;
        public ChatRelay chatRelay = new ChatRelay();

        public static class ChatRelay {
            public boolean enable = false;
            public boolean ignoreQueue = true;
            public boolean mentionRoleOnWhisper = false;
            public boolean connectionMessages = false;
            public String channelId = "";
        }
    }

    private transient boolean donePostLoad = false;

    public synchronized Config doPostLoad() {
        if (this.donePostLoad) {
            throw new IllegalStateException("Config post-load already done!");
        }
        this.donePostLoad = true;

        return this;
    }
}
