package com.tz.esdemo.service;

import com.tz.esdemo.entity.Book;
import com.tz.esdemo.entity.EsEntity;
import com.tz.esdemo.util.EsUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author Tianzheng
 * @date 2019/7/29 14:31
 */
@RestController
@RequestMapping("/book")
@Service
public class BookService {

    @Autowired
    private EsUtil esUtil;

    /**
     * @param id 获取某一个
     */
    public Book getById(@PathVariable("id") int id) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(new TermQueryBuilder("id", id));
        List<Book> res = esUtil.search(EsUtil.INDEX_NAME, builder, Book.class);
        if (res.size() > 0) {
            return res.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取全部
     */
    public List<Book> getAll() {
        return esUtil.search(EsUtil.INDEX_NAME, new SearchSourceBuilder(), Book.class);
    }

    /**
     * 根据关键词搜索某用户下的书
     *
     * @param content 关键词
     */
    public List<Book> searchByUserIdAndName(int userId, String content) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(!StringUtils.isEmpty(content)){
            boolQueryBuilder.should(QueryBuilders.matchQuery("name", content));
        }

        boolQueryBuilder.must(QueryBuilders.termQuery("userId", userId));
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(10).query(boolQueryBuilder);
        return esUtil.search(EsUtil.INDEX_NAME, builder, Book.class);

    }

    /**
     * from-size分页查询
     *
     * @param from
     * @param size
     * @return
     */
    public List<Book> searchPageFromSize(int from, int size) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(from).size(size);
        return esUtil.search(EsUtil.INDEX_NAME, builder, Book.class);
    }

    /**
     * Scroll深度分页
     *
     * @param size
     * @return
     */
    public List<Book> searchPageScroll(int size){
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(size);
        return esUtil.searchScroll(EsUtil.INDEX_NAME, builder, Book.class);
    }

    /**
     * 单个插入
     *
     * @param book book
     */
    public void putOne(@RequestBody Book book) {
        EsEntity<Book> entity = new EsEntity<>(book.getId().toString(), book);
        esUtil.insertOrUpdateOne(EsUtil.INDEX_NAME, entity);
    }

    /**
     * 批量插入
     *
     * @param books books
     */
    public void putList(@RequestBody List<Book> books) {
        List<EsEntity> list = new ArrayList<>();
        books.forEach(item -> list.add(new EsEntity<>(item.getId().toString(), item)));
        esUtil.insertBatch(EsUtil.INDEX_NAME, list);
    }

    /**
     * 批量删除
     *
     * @param list list
     */
    public void deleteBatch(List<Integer> list) {
        esUtil.deleteBatch(EsUtil.INDEX_NAME, list);
    }

    /**
     * delete by query 根据用户id删除数据
     *
     * @param userId userId
     */
    public void deleteByUserId(@PathVariable("userId") int userId) {
        esUtil.deleteByQuery(EsUtil.INDEX_NAME, new TermQueryBuilder("userId", userId));
    }


}
