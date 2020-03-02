package com.tz.esdemo;

import com.tz.esdemo.entity.Book;
import com.tz.esdemo.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsDemoApplicationTests {

    @Autowired
    private BookService bookService;

    @Test
    public void getOne() {
        System.out.println(bookService.getById(100000).toString());
    }

    @Test
    public void getAll() {
        List<Book> res = bookService.getAll();
        System.out.println("=====>"+res.size());
        //res.forEach(System.out::println);
    }

    @Test
    public void addOneTest() {
        bookService.putOne(new Book(1, 1, "格林童话"));
        bookService.putOne(new Book(2, 1, "美人鱼"));
    }

    @Test
    public void addBatchTest() {
        List<Book> list = new ArrayList<>();
//        list.add(new Book(3, 1, "第一本书"));
//        list.add(new Book(4, 1, "第二本书"));

        for (int i=0;i<1000;i++){
            list.add(new Book(i, i, "第"+i+"本书"));
        }
        bookService.putList(list);
    }

    @Test
    public void deleteBatch() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            list.add(i);
        }
        bookService.deleteBatch(list);
    }

    @Test
    public void deleteByQuery() {
        bookService.deleteByUserId(1);
    }


}
