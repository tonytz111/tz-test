package com.tz.esdemo.util;

import com.alibaba.fastjson.JSON;
import com.tz.esdemo.entity.EsEntity;
import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author Tianzheng
 * @date 2019/7/29 11:24
 */
@Component
public class EsUtil {

    private final Logger logger = Logger.getLogger(getClass());

    @Value("${es.host}")
    public String host;
    @Value("${es.port}")
    public int port;
    @Value("${es.scheme}")
    public String scheme;
    @Value("${es.clusterName}")
    public String clusterName;

    public static final String INDEX_NAME = "book-index";
    public static final String NEW_INDEX_NAME = "index_entity";

    public static final String CREATE_INDEX = "{\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"userId\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"url\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"index\": true,\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      }\n" +
            "    }\n" +
            "  }";

    public static RestHighLevelClient restHighLevelClientClient = null;
    public static TransportClient transportClient = null;
    public static SearchRequest searchRequest = null;


    @PostConstruct
    public void initRestHighLevelClientClient() {
        logger.info("===========222222222222=============");

        try {
            if (restHighLevelClientClient != null) {
                restHighLevelClientClient.close();
            }
            restHighLevelClientClient = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, scheme)));
            if (this.indexExist(INDEX_NAME)) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
            request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
            request.mapping(CREATE_INDEX, XContentType.JSON);

            if (transportClient != null) {
                transportClient.close();
            }
            //因为下载困难所以暂时取消分词器
//            CreateIndexResponse res = client.indices().create(request, RequestOptions.DEFAULT);
//            if (!res.isAcknowledged()) {
//                throw new RuntimeException("初始化失败");
//            }


            searchRequest = new SearchRequest(String.valueOf(port));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Description: 判断某个index是否存在
     *
     * @param index index名
     * @return boolean
     * @author Tianzheng
     * @date 2019/7/24 14:57
     */
    public boolean indexExist(String index) throws Exception {
        GetIndexRequest request = new GetIndexRequest(index);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        return restHighLevelClientClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * Description: 插入/更新一条记录
     *
     * @param index  index
     * @param entity 对象
     * @author Tianzheng
     * @date 2019/7/24 15:02
     */
    public void insertOrUpdateOne(String index, EsEntity entity) {
        IndexRequest request = new IndexRequest(index);
        request.id(entity.getId());
        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
        try {
            restHighLevelClientClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 批量插入数据
     *
     * @param index index
     * @param list  带插入列表
     * @author Tianzheng
     * @date 2019/7/24 17:38
     */
    public void insertBatch(String index, List<EsEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(index).id(item.getId())
                .source(JSON.toJSONString(item.getData()), XContentType.JSON)));
        try {
            restHighLevelClientClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 批量删除
     *
     * @param index  index
     * @param idList 待删除列表
     * @author Tianzheng
     * @date 2019/7/25 14:24
     */
    public <T> void deleteBatch(String index, Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(index, item.toString())));
        try {
            restHighLevelClientClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 搜索
     *
     * @param index   index
     * @param builder 查询参数
     * @param c       结果类对象
     * @return java.util.ArrayList
     * @author Tianzheng
     * @date 2019/7/25 13:46
     */
    public <T> List<T> search(String index, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            long beginTime = System.currentTimeMillis();
            SearchResponse response = restHighLevelClientClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            long endTime = System.currentTimeMillis();

            //大数据量查询数据量限制，可以向elasticsearch发送PUT消息
            //消息地址:http://localhost:9200/book-index/_settings
            //消息内容:"index.max_result_window":999999999
            logger.info("=======此次查询命中记录数：" + res.size() + "======此次查询耗时毫秒数：" + (endTime - beginTime));
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> searchScroll(String index, SearchSourceBuilder builder, Class<T> c) {

        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest(String.valueOf(index));
        searchRequest.scroll(scroll);
        searchRequest.source(builder);
        List<T> res = new ArrayList<>();

        try {
            SearchResponse searchResponse = restHighLevelClientClient.search(searchRequest, RequestOptions.DEFAULT); //通过发送初始搜索请求来初始化搜索上下文
//            restHighLevelClientClient

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    /**
     * Description: 删除index
     *
     * @param index index
     * @return void
     * @author Tianzheng
     * @date 2019/7/26 11:30
     */
    public void deleteIndex(String index) {
        try {
            restHighLevelClientClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: delete by query
     *
     * @param index   index
     * @param builder builder
     * @author Tianzheng
     * @date 2019/7/26 15:16
     */
    public void deleteByQuery(String index, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            restHighLevelClientClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
