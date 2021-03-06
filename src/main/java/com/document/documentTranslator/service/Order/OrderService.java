package com.document.documentTranslator.service.Order;

import com.document.documentTranslator.dto.OrderDto;
import com.document.documentTranslator.entity.Order;
import com.document.documentTranslator.entity.User;
import com.document.documentTranslator.enums.ErrorMessage;
import com.document.documentTranslator.enums.OrderStatus;
import com.document.documentTranslator.exception.DomainException;
import com.document.documentTranslator.repository.Order.OrderRepository;
import com.document.documentTranslator.service.User.UserService;
import com.document.documentTranslator.util.DomainUtil;
import com.document.documentTranslator.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Order createOrUpdate(OrderDto orderDto) throws DomainException {

        if (Validator.isNull(orderDto))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);

        orderDto.validate();
        this.userService.findByUserName(orderDto.getUsername());

        Order order;
        if (Validator.isNull(orderDto.getId()))
            order = new Order();
        else order = findById(orderDto.getId());

        if (Validator.notNull(orderDto.getUsername()))
            order.setUsername(orderDto.getUsername());

        if (Validator.notNull(orderDto.getType()))
            order.setType(orderDto.getType());

        if (Validator.notNull(orderDto.getStatus()))
            order.setStatus(OrderStatus.lookupByName(orderDto.getStatus()));

        if (Validator.notNull(orderDto.getDetails()))
            order.setDetails(DomainUtil.mapToString((HashMap<String, Object>) orderDto.getDetails()));

        if (Validator.notNull(orderDto.getMode()) && orderDto.getMode().equals("SUBMIT"))
            order.setSubmitDate(new Date());

        orderRepository.save(order);
        return order;
    }

    public Order findById(Long id) throws DomainException {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (!orderOptional.isPresent())
            throw new DomainException(String.format(ErrorMessage.NOT_FOUND.getFarsiMessage(), "سفارش"), ErrorMessage.NOT_FOUND);
        return orderOptional.get();
    }

    public Map<String, Object> getById(OrderDto orderDto) throws DomainException {
        if (Validator.isNull(orderDto) || Validator.isNull(orderDto.getId()))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);
        return findById(orderDto.getId()).map();
    }

    public List<Map<String, Object>> getOrders(OrderDto orderDto) throws DomainException {

        if (Validator.isNull(orderDto))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);
        return DomainUtil.toMapList(orderRepository.getAll(orderDto, DomainUtil.getBegin(orderDto), DomainUtil.getLength(orderDto)), DomainUtil.getBegin(orderDto));
    }

    public Long getAllCount(OrderDto orderDto) {
        return orderRepository.getAllCount(orderDto, DomainUtil.getBegin(orderDto), DomainUtil.getLength(orderDto));
    }

    public Order assignOrderToAdmin(OrderDto orderDto) throws DomainException {
        if (Validator.isNull(orderDto))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);
        orderDto.assignValidate();
        Order order = findById(orderDto.getOrderId());
        User user = userService.validateAdminByName(orderDto.getAdminName());

        order.setAdminName(user.getUsername());
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setAcceptanceDate(new Date());
        orderRepository.save(order);
        return order;
    }

    public Order unAssignOrder(OrderDto orderDto) throws DomainException {
        if (Validator.isNull(orderDto))
            throw new DomainException(ErrorMessage.INVALID_PARAMETER);
        orderDto.unAssignValidate();
        Order order = findById(orderDto.getOrderId());

        order.setAdminName(null);
        order.setStatus(OrderStatus.PENDING);
        order.setAcceptanceDate(null);
        orderRepository.save(order);
        return order;
    }
}
