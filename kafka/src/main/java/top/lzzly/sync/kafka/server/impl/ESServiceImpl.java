package top.lzzly.sync.kafka.server.impl;

import com.alibaba.fastjson.JSON;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lzzly.sync.kafka.config.Config;
import top.lzzly.sync.kafka.config.EsJestClient;
import top.lzzly.sync.kafka.entity.User;
import top.lzzly.sync.kafka.server.ESService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author : yangwc
 * @Description: ES 通用逻辑
 * @Date : 2018/9/14  09:24
 * @Modified By :
 */
@Service
public class ESServiceImpl implements ESService {

    private Logger logger= LoggerFactory.getLogger(ESServiceImpl.class);

    JestClient client = EsJestClient.getClient();


    /**
     * 创建索引
     * @param index
     * @Author yangwc
     */
    @Override
    public void createIndex(String index){

        JestResult execute=null;
        try {
            execute = client.execute(new CreateIndex.Builder(index).build());
        } catch (IOException e) {
            logger.error("创建索引ERROR:"+e);
            e.printStackTrace();
        }
        logger.warn("创建索引:"+execute.isSucceeded()+"信息:"+execute.getJsonString());
    }

    /**
     * 删除索引
     * @param index
     */
    @Override
    public void delteIndex(String index) {

        JestResult execute=null;
        try {
            execute = client.execute(new DeleteIndex.Builder(index).build());
        } catch (IOException e) {
            logger.error("删除索引ERROR:"+e);
            e.printStackTrace();
        }
        logger.warn("删除索引:"+execute.isSucceeded()+"信息:"+execute.getJsonString());
    }

    /**
     * 检查索引是否存在
     * @param index
     */
    @Override
    public void indicatesExists(String index) {

        JestResult execute=null;
        try {
            execute = client.execute(new IndicesExists.Builder(index).build());
        } catch (IOException e) {
            logger.error("检查索引是否存在异常:"+e);
            e.printStackTrace();
        }
        logger.warn("检查索引是否存在:"+execute.isSucceeded()+",信息:"+execute.getJsonString());
    }

    /**
     * 创建文档
     * @param index 索引 (库名)
     * @param type (表名)
     * @param object 文档(数据)
     */
    @Override
    public void createDocument(String index,String type, Object object) {

        DocumentResult execute=null;
        try {
            execute = client.execute(new Index.Builder(object).index(index).type(type).build());
        } catch (IOException e) {
            logger.error("创建文档异常:"+e);
            e.printStackTrace();
        }
        logger.warn("创建文档:"+execute.isSucceeded()+",信息:"+execute.getJsonString());
    }

    /**
     * 读取文档
     * @param search 条件
     */
    @Override
    public void readDocument(String search) {

        List<SearchResult.Hit<User, Void>> execute=null;
        try {
            execute = client.execute(new Search.Builder(search).build()).getHits(User.class);
        } catch (IOException e) {
            logger.error("读取文档异常:"+e);
            e.printStackTrace();
        }
        logger.warn("读取文档:"+execute.get(0).id+",信息:"+execute.get(0).source);
    }

    /**
     * 更新文档
     * @param index 索引 (库名)
     * @param id 文档id
     * @param object 文档(数据)
     *               todo 异常
     */
    @Override
    public void updateDocument(String index, String id, Object object) {

        try {
            DocumentResult execute = client.execute(new Update.Builder(object).index(index).type("user").id(id).build());
            System.out.println(execute);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteDocument(String index, String id, Object object) {

        try {
            DocumentResult employees = client.execute(new Delete.Builder("32").index("temmoliu").build());
            System.out.println(employees);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置数据类型和分词方式
     * @param index
     */
    @Override
    public void createIndexMapping(String index, String type, String mappingString) {

        JestResult execute=null;
        try {
            execute = client.execute(new PutMapping.Builder(index, type, mappingString).build());
        } catch (IOException e) {
            logger.error("设置数据类型和分词方式ERROR:"+e);
            e.printStackTrace();
        }
        logger.warn("设置数据类型和分词方式:"+execute.isSucceeded()+",信息:"+execute.getJsonString());
    }




    public static void main(String[] args) {
//        String search = "{" +
//                "  \"query\": {" +
//                "    \"bool\": {" +
//                "      \"must\": [" +
//                "        { \"match\": { \"name\": \"gg33\" }}" +
//                "      ]" +
//                "    }" +
//                "  }" +
//                "}";
//        new ESServiceImpl().readDocument(search);

        User user = new User();
        user.setId("65");
        user.setName("杨文超");
        new ESServiceImpl().deleteDocument("temmoliu","32",user);
    }

    public boolean update(String id, String esType, Object object) {
        Index index = new Index.Builder(object).index(Config.ES_INDICES).type(esType).id(id).refresh(true).build();
        try {
            JestResult result = client.execute(index);
            return result != null && result.isSucceeded();
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public Index getUpdateIndex(String id, String esType, Object object) {
        return new Index.Builder(object).index(Config.ES_INDICES).type(esType).id(id).refresh(true).build();
    }

    @Override
    public Delete getDeleteIndex(String id, String esType) {
        return new Delete.Builder(id).index(Config.ES_INDICES).type(esType).build();
    }

    @Override
    public boolean executeESClientRequest(List indexList, String esType) {
        Bulk bulk = new Bulk.Builder()
                .defaultIndex(Config.ES_INDICES)
                .defaultType(esType)
                .addAction(indexList)
                .build();
        indexList.clear();
        try {
            JestResult result = client.execute(bulk);
            return result != null && result.isSucceeded();
        } catch (Exception ignore) {
        }
        return false;
    }

    public boolean delete(String id, String esType) {
        try {
            DocumentResult result = client.execute(new Delete.Builder(id)
                    .index(Config.ES_INDICES)
                    .type(esType)
                    .build());
            return result.isSucceeded();
        } catch (Exception e) {
            throw new RuntimeException("delete exception", e);
        }
    }
}
