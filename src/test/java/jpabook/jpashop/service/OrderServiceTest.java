package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Test
    public void 상품주문() throws Exception {
        Member member = getMember();

        Book book = getBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long order = orderService.order(member.getId(), book.getId(), orderCount);

        Order one = orderRepository.findOne(order);

        assertEquals("상품 주문 시 상태는 ORDER", OrderStatus.ORDER, one.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, one.getOrderItems().size());
        assertEquals("주문 가격은 수량*상품 가격이다.", 20000, one.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member getMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "마포구", "123-123"));
        em.persist(member);
        return member;
    }


    @Test
    public void 주문취소() throws Exception {
        Member member = getMember();
        Book book = getBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancelOrder(orderId);

        Order one = orderRepository.findOne(orderId);
        assertEquals("주문 취소시 상태는 CANCEL이다.", OrderStatus.CANCEL, one.getStatus());
        assertEquals("주문 취소시 상품 재고가 원상복귀 되어야한다.", 10, book.getStockQuantity());
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        Member member = getMember();
        Book book = getBook("시골 JPA", 10000, 10);

        int orderCount = 11;
        try {
            orderService.order(member.getId(), book.getId(), orderCount);
        } catch (NotEnoughStockException e) {
            return;
        }

        fail("재고 수량 부족 예외가 발생해야 한다.");
    }
}