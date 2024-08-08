package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문 생성
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //주문 주소 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문 내역, 주문 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장 (delivery와 orderitem 도 원래는 따로 리포지토리를 만들고 persist를 해주어야 하지만 cascade옵션에 의해 같이 persist가 됨)
        // cascade 옵션은 이 서비스에서는 Order만 delivery와 orderitem을 참조하고 있기 때문에 사용해도 괜찮지만 다른 엔티티도 이들을 참조하고 있다면 주의해야 함
        orderRepository.save(order);
        return order.getId();
    }

    //주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel(); //JPA 장점! 원래는 주문 삭제와 동시에 orderstatus와 item의 stockquantity가 변경되면서 각각
                        // sql로 update쿼리를 작성해야 하지만 JPA를 쓰면 더티체킹을 통해서 값이 변경된 지점을 자동 감지 해서 알아서 UPDATE쿼리를 날려줌
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        List<Order> orders = orderRepository.findAllByString(orderSearch);
        return orders;
    }
}
