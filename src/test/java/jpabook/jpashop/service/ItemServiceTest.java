package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;

    @Test
    public void 상품등록() throws Exception {
        // given
        Book book = new Book();
        book.setAuthor("김영하");

        // when
        Long saveItemId = itemService.saveItem(book);

        // then
        Assertions.assertThat(saveItemId).isEqualTo(book.getId());
        Assertions.assertThat(book).isEqualTo(itemService.findOne(saveItemId));
    }

    @Test
    public void 상품조회() throws Exception {
        // given

        // when

        // then


    }

    @Test
    public void 상품전체조회() throws Exception {
        // given

        // when

        // then


    }
}