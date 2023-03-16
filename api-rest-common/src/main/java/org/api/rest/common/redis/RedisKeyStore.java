package org.api.rest.common.redis;

public enum RedisKeyStore {

    API("API:MASTER", "LIST"),
    API_TOKEN("API:TOKEN:MASTER", "HASH"),
    ADDRESS("ADDRESS:MASTER", "ZSET"),
    VERTX("VERTX:MASTER", "ZSET"),
    ACCOUNT("ACCOUNT:MASTER", "HASH"),
    ARTICLE_MASTER("ARTICLE:MASTER", "LIST"),
    ARTICLE_COMMENT("ARTICLE:COMMENT", "LIST"),
    CLIENT("CLIENT:MASTER", "LIST"),
    DIC("DIC:MASTER", "LIST"),
    DAEMON("DAEMON:MASTER", "LIST"),
    EDM("EDM:MASTER", "LIST"),
    EDM_DOWN("EDM:DOWN:MASTER", "LIST"),
    EDIT_MULTI("EDIT:MULTI", "HASH"),
    GATE("GATE:MASTER", "LIST"),
    LOG("LOG:MASTER", "LIST") ,
    MSG_MON("MESSAGE:MON", "LIST"),
    MSG_TASK("MESSAGE:TASK", "LIST"),
    MSG_WSDL("MESSAGE:WSDL", "LIST"),
    MSG_PUB("MESSAGE:PUB", "LIST"),
    MSG_SUB("MESSAGE:SUB", "LIST"),
    MSG_MIN("MESSAGE:MIN", "LIST"),
    REGIST("REGIST:MASTER", "LIST"),
    PUBLISH("REQUEST:PUBLISH:MASTER", "LIST"),
    SERVICE("SERVICE:MASTER", "HASH"),
    SESSION("SESSION:MASTER", "HASH"),
    PERSONAL("PERSONAL:MASTER", "HASH"),
    PERSONAL_SUB("PERSONAL:SUB", "KEY"),
    PERSONAL_PUB("PERSONAL:PUB", "KEY"),
    TEMPLATE_MASTER("TEMPLATE:MASTER", "HASH"),
    HMAP_MASTER("ARTICLE:HMAP:MASTER", "HASH"),
    HMAP_PERSONAL("ARTICLE:HMAP:PERSONAL", "HASH"),
    HMAP_FILE("ARTICLE:HMAP:FILE", "HASH"),
    ARTICLE_REPLY("ARTICLE:REPLY:MASTER", "LIST"),
    ARTICLE_ALARM("ARTICLE:ALARM:MASTER", "LIST"),
    ARTICLE_DETAIL("ARTICLE:DETAIL:MASTER", "LIST"),
    ARTICLE_FUNCTION("ARTICLE:FUNCTION:MASTER", "LIST"),
    PERSONAL_MASTER("PERSONAL:MASTER", "HASH"),
    EVENT_SLAVE("EVENT:NOTIFY:SLAVE", "LIST"),
    EVENT_MASTER("EVENT:NOTIFY:MASTER", "LIST"),
    GROUP_CATEGORIZATION_NEW("GROUP:CATEGORIZATION:NEW", "LIST"),
    GROUP_CATEGORIZATION_POPULAR("GROUP:CATEGORIZATION:POPULAR", "LIST"),
    GROUP_CATEGORIZATION_FAVORITE("GROUP:CATEGORIZATION:FAVORITE", "LIST"),
    GROUP_CATEGORIZATION_VIEW("GROUP:CATEGORIZATION:VIEW", "LIST"),
    GROUP_CATEGORIZATION_VISIT("GROUP:CATEGORIZATION:VISIT", "LIST"),
    GROUP_UPDATE("GROUP:UPDATE", "LIST"),
    GROUP_MASTER("GROUP:MASTER", "HASH"),
    LOGOUT("LOGOUT:MASTER", "ZSET"),
    MIGRATION("MIGRATION:MASTER", "LIST"),
    MSG_PUSH("PUSH:MASTER", "LIST"),
    FCM_CLIENT("FCM:CLIENT", "HASH"),
    NOTIFY_FAVORITE_RECEIVER("NOTIFY:FAVORITE:MASTER", "ZSET"),
    NOTIFY_FAVORITE("NOTIFY:FAVORITE", "LIST"),
    NOTIFY_COMMENT_RECEIVER("NOTIFY:COMMENT:MASTER", "ZSET"),
    NOTIFY_COMMENT("NOTIFY:COMMENT", "LIST"),
    NOTIFY_GROUP_ARTICLE_RECEIVER("NOTIFY:GROUPARTICLE:MASTER", "ZSET"),
    NOTIFY_GROUP_ARTICLE("NOTIFY:GROUPARTICLE", "LIST"),
    NOTIFY_SENDLOG("NOTIFY:SENDLOG", "LIST"),
    NOTIFY_SENDLOG_MASTER("NOTIFY:SENDLOG:MASTER", "HASH"),
    GROUP_HISTORY("GROUP:HISTORY", "HASH"),
    TASK_LOG("TASK:LOG", "LIST"),
    TASK_RESTAPI("TASK:RESTAPI", "LIST");
    
    private String master;
    private String type;
    
    RedisKeyStore(String master, String type){
        this.master = master;
        this.type = type;
    }

    public String getMaster(){
        return this.master;
    }

    public String getType(){
        return this.type;
    }
}