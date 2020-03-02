package com.tz.esdemo.controller;
/**
 * 类功能简述： 插入es的数据controller
 * 类功能详述：
 *
 * @author Tianzheng
 * @date 2019/7/29 11:33
 */

import com.tz.esdemo.entity.Book;
import com.tz.esdemo.service.BookService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.log4j.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@Api(value = "tztest")
@RequestMapping("/estest")
public class TZTestEsController {
    private final Logger logger = Logger.getLogger(getClass());

    @PostConstruct
    public void init() {
        logger.info("===========111111111111111111=============");
    }


    @Autowired
    private BookService bookService;

    @ApiOperation(value = "获取某一个", notes = "获取某一个")
    @RequestMapping(value = "getOneById", method = RequestMethod.GET)
    public Book getOneById(@RequestParam int id) {
        return this.bookService.getById(id);
    }

    @ApiOperation(value = "获取全部", notes = "获取全部")
    @RequestMapping(value = "getAll", method = RequestMethod.GET)
    public List<Book> getAll() {
        return this.bookService.getAll();
    }

    @ApiOperation(value = "根据关键词搜索某用户下的书", notes = "根据关键词搜索某用户下的书")
    @GetMapping(value = "searchByUserIdAndName")
    public List<Book> searchByUserIdAndName(@RequestParam(value="id",required = true) int userId, @RequestParam(value = "content",required = false) String content) {
        return this.bookService.searchByUserIdAndName(userId, content);
    }

    @ApiOperation(value = "From-Size方式浅分页查询", notes = "From-Size方式浅分页查询")
    @RequestMapping(value = "pageSearchByFromSize", method = RequestMethod.GET)
    public List<Book> pageSearchByFromSize(@RequestParam int from, @RequestParam int size) {
        return this.bookService.searchPageFromSize(from, size);
    }

    @ApiOperation(value = "Scroll方式深度分页查询", notes = "Scroll方式深度分页查询")
    @RequestMapping(value = "pageSearchByScroll", method = RequestMethod.GET)
    public List<Book> pageSearchByScroll(@RequestParam int size) {
        return this.bookService.searchPageScroll(size);
    }

    @ApiOperation(value = "单个插入", notes = "单个插入")
    @RequestMapping(value = "putOne", method = RequestMethod.PUT)
    public void putOne(@RequestBody Book book) {
        this.bookService.putOne(book);
    }

    @ApiOperation(value = "批量插入", notes = "批量插入")
    @RequestMapping(value = "putList", method = RequestMethod.PUT)
    public void putList(@RequestBody List<Book> books) {
        this.bookService.putList(books);
    }

    @ApiOperation(value = "批量删除", notes = "批量删除")
    @RequestMapping(value = "deleteBatch", method = RequestMethod.DELETE)
    public void deleteBatch(@RequestBody List<Integer> list) {
        this.bookService.deleteBatch(list);
    }

    @ApiOperation(value = "根据用户id删除数据", notes = "根据用户id删除数据")
    @RequestMapping(value = "deleteByQuery", method = RequestMethod.DELETE)
    public void deleteByQuery(@RequestBody int userId) {
        this.bookService.deleteByUserId(userId);
    }
}
