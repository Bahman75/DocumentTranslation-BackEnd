package com.document.documentTranslator.service.Order;

import com.document.documentTranslator.dto.OrderDto;
import com.document.documentTranslator.entity.Order;
import com.document.documentTranslator.entity.User;
import com.document.documentTranslator.enums.ErrorMessage;
import com.document.documentTranslator.enums.OrderStatus;
import com.document.documentTranslator.enums.OrderType;
import com.document.documentTranslator.exception.DomainException;
import com.document.documentTranslator.repository.Order.OrderRepository;
import com.document.documentTranslator.service.User.UserService;
import com.document.documentTranslator.util.DomainUtil;
import com.document.documentTranslator.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    public Order createOrder(OrderDto orderDto) throws DomainException {

        if (Validator.isNull(orderDto))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);

        orderDto.validate();
        this.userService.findByUserName(orderDto.getUsername());

        Order order = new Order();
        order.setUsername(orderDto.getUsername());
        order.setType(OrderType.lookupByName(orderDto.getType()));
        order.setStatus(OrderStatus.PENDING);
        order.setDetails(DomainUtil.objectToString(orderDto.getDetails()));

        orderRepository.save(order);
        return order;
    }

    public Order findById(Long id) throws DomainException {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent())
            throw new DomainException(String.format(ErrorMessage.NOT_FOUND.getFarsiMessage(), "سفارش"), ErrorMessage.NOT_FOUND);
        return orderOptional.get();
    }

    public List<Order> getOrders(OrderDto orderDto) throws DomainException {

        if (Validator.isNull(orderDto))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);
        return orderRepository.getAll(orderDto, DomainUtil.getBegin(orderDto), DomainUtil.getLength(orderDto));
    }

    public Order assignOrderToAdmin(OrderDto orderDto) throws DomainException {
        if (Validator.isNull(orderDto))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);
        orderDto.assignValidate();
        Order order = findById(orderDto.getOrderId());
        User user = userService.validateAdminByName(orderDto.getAdminName());

        order.setAdminName(user.getUsername());
        order.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(order);
        return order;
    }
}
