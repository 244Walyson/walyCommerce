package com.walyCommerce.walycommerce.dto;

import com.walyCommerce.walycommerce.entities.Order;
import com.walyCommerce.walycommerce.entities.OrderItem;
import com.walyCommerce.walycommerce.entities.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private Long id;
    private Instant moment;
    private OrderStatus status;

    private ClientDTO client;
    private PaymentDTO paymentDTO;

    @NotEmpty(message = "O pedido deve ter pelo menos um item")
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO(Long id, Instant moment, OrderStatus status, ClientDTO client, PaymentDTO paymentDTO) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.client = client;
        this.paymentDTO = paymentDTO;
    }

    public OrderDTO(Order entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.status = entity.getStatus();
        this.client = new ClientDTO(entity.getClient());
        this.paymentDTO = (entity.getPayment()==null) ? null: new PaymentDTO(entity.getPayment());
        for (OrderItem item : entity.getItems()){
            items.add(new OrderItemDTO(item));
        }
    }

    public Long getId() {
        return id;
    }

    public Instant getMoment() {
        return moment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public ClientDTO getClient() {
        return client;
    }

    public PaymentDTO getPaymentDTO() {
        return paymentDTO;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public double getTotal(){
        double sum = 0;
        for(OrderItemDTO item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }
}

