package top.lzzly.sync.kafka.config;

/**
 * @Author : yangwc
 * @Description: config 配置文件
 * @Date : 2020/5/30
 * @Modified By :
 */
public interface Config {

    String ES_HOST = "http://localhost:9200";

    String ES_INDICES = "temmoliu";
    String ES_USER_TYPE = "user";
    String ES_ROLE_TYPE = "role";

    String KAFKA_JSON_TOPICS = "binlog";
    String KAFKA_JSON_ID = "consumer2";

}