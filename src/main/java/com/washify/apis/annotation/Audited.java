package com.washify.apis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation để đánh dấu các method cần được audit log tự động
 * 
 * Cách sử dụng:
 * @Audited(action = "UPDATE_ORDER_STATUS", entityType = "Order")
 * public Order updateOrderStatus(Long orderId, OrderStatus newStatus) { ... }
 * 
 * @author Washify Team
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    
    /**
     * Hành động được thực hiện (CREATE, UPDATE, DELETE, etc.)
     * @return Action name
     */
    String action();
    
    /**
     * Loại entity đang được thao tác (Order, User, Payment, etc.)
     * @return Entity type
     */
    String entityType();
    
    /**
     * Mô tả chi tiết về hành động (optional)
     * @return Description
     */
    String description() default "";
}
