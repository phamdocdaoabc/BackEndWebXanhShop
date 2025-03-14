package org.example.shippingservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shippingservice.constant.AppConstant;
import org.example.shippingservice.domain.dtos.*;
import org.example.shippingservice.domain.dtos.adminStatistics.CategoryStatisticDTO;
import org.example.shippingservice.domain.dtos.adminStatistics.ProductStatisticDTO;
import org.example.shippingservice.domain.entity.OrderItem;
import org.example.shippingservice.domain.entity.OrderItemId;
import org.example.shippingservice.exception.OrderItemNotFoundException;
import org.example.shippingservice.mapper.OrderItemMapping;
import org.example.shippingservice.repository.OrderItemRepository;
import org.example.shippingservice.service.OrderItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;


    public List<OrderItemsDTO> findAll() {
        log.info("*** OrderItemDto List, service; fetch all orderItems *");
        return this.orderItemRepository.findAll()
                .stream()
                .map(OrderItemMapping::map)
                .map(o -> {
                    o.setProductDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .PRODUCT_SERVICE_API_URL + "/" + o.getProductDto().getProductId(), ProductDTO.class));
                    o.setOrderDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .ORDER_SERVICE_API_URL + "/" + o.getOrderDto().getOrderId(), OrderDTO.class));
                    return o;
                })
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }


    public OrderItemsDTO findById(final OrderItemId orderItemId) {
        log.info("*** OrderItemDto, service; fetch orderItem by id *");
        return this.orderItemRepository.findById(null)
                .map(OrderItemMapping::map)
                .map(o -> {
                    o.setProductDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .PRODUCT_SERVICE_API_URL + "/" + o.getProductDto().getProductId(), ProductDTO.class));
                    o.setOrderDto(this.restTemplate.getForObject(AppConstant.DiscoveredDomainsApi
                            .ORDER_SERVICE_API_URL + "/" + o.getOrderDto().getOrderId(), OrderDTO.class));
                    return o;
                })
                .orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem with id: %s not found", orderItemId)));
    }


    public OrderItemsDTO save(final OrderItemsDTO orderItemDto) {
        log.info("*** OrderItemDto, service; save orderItem *");
        return OrderItemMapping.map(this.orderItemRepository
                .save(OrderItemMapping.map(orderItemDto)));
    }


    public OrderItemsDTO update(final OrderItemsDTO orderItemDto) {
        log.info("*** OrderItemDto, service; update orderItem *");
        return OrderItemMapping.map(this.orderItemRepository
                .save(OrderItemMapping.map(orderItemDto)));
    }


    public void deleteById(final OrderItemId orderItemId) {
        log.info("*** Void, service; delete orderItem by id *");
        this.orderItemRepository.deleteById(orderItemId);
    }

    // Thêm sản phẩm dựa vào oderId đã được tạo
    @Override
    @Transactional
    public void addOrderItems(Integer orderId, List<OrderItemsRequest.OrderItems> orderItems) {
        for (OrderItemsRequest.OrderItems orderItems1 : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(orderItems1.getProductId());
            orderItem.setOrderedQuantity(orderItems1.getQuantity());
            orderItem.setPrice(orderItems1.getPrice());
            orderItem.setCreatedAt(LocalDate.now());
            orderItemRepository.save(orderItem);
        }
    }

    // Call ProductService to get productName
    private String fetchProductName(Integer productId){
        String url = "http://ProductService/product-service/api/products/" + productId;
        try{
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> productData = response.getBody();
            // get productName from Map(if any)
            return (productData != null && productData.containsKey("productName")) ? productData.get("productName").toString() : "Không xác định";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    // Admin Product statistics
    @Override
    public Page<ProductStatisticDTO> getProductStatistics(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // Query list OrderItems by date range
        Page<OrderItem> orderItemPage = orderItemRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        List<OrderItem> orderItems = orderItemPage.getContent();

        // Group data by product_Id
        Map<Integer, ProductStatisticDTO> statisticDTOMap = new HashMap<>();

        for(OrderItem orderItem : orderItems){
            Integer productId = orderItem.getProductId();
            ProductStatisticDTO stat = statisticDTOMap.getOrDefault(productId, new ProductStatisticDTO(productId));

            stat.setSoldQuantity(stat.getSoldQuantity() + orderItem.getOrderedQuantity());
            stat.setRevenue(stat.getRevenue() + (orderItem.getOrderedQuantity() * orderItem.getPrice()));

            stat.setMinPrice(Math.min(stat.getMinPrice(), orderItem.getPrice()));
            stat.setMaxPrice(Math.max(stat.getMaxPrice(), orderItem.getPrice()));

            statisticDTOMap.put(productId, stat);
        }
        // Call api ProductService to get ProductName
        for(ProductStatisticDTO stat : statisticDTOMap.values()){
            String productName = fetchProductName(stat.getProductId());
            stat.setProductName(productName);
            stat.setAveragePrice(stat.getRevenue() / stat.getSoldQuantity()); // Tính giá trung bình
        }
        return new PageImpl<>(new ArrayList<>(statisticDTOMap.values()), pageable, orderItemPage.getTotalElements());
    }

    // Call ProductService to get categoryName
    private String fetchCategoryName(Integer productId){
        String url = "http://ProductService/product-service/api/products/" + productId;
        try{
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> productData = response.getBody();
            // get productName from Map(if any)
            return (productData != null && productData.containsKey("categoryName")) ? productData.get("categoryName").toString() : "Không xác định";
        }catch (Exception e){
            return e.getMessage();
        }
    }
    // Admin Category statistics
    @Override
    public Page<CategoryStatisticDTO> getCategoryStatistics(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // Query list OrderItems by date range
        Page<OrderItem> orderItemPage = orderItemRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        List<OrderItem> orderItems = orderItemPage.getContent();

        // Group data by category_Name
        Map<String, CategoryStatisticDTO> statisticDTOMap = new HashMap<>();

        for(OrderItem orderItem : orderItems){
            Integer productId = orderItem.getProductId();
            String categoryName = fetchCategoryName(productId);

            CategoryStatisticDTO stat = statisticDTOMap.getOrDefault(categoryName, new CategoryStatisticDTO(categoryName));

            stat.setSoldQuantity(stat.getSoldQuantity() + orderItem.getOrderedQuantity());
            stat.setRevenue(stat.getRevenue() + (orderItem.getOrderedQuantity() * orderItem.getPrice()));

            stat.setMinPrice(Math.min(stat.getMinPrice(), orderItem.getPrice()));
            stat.setMaxPrice(Math.max(stat.getMaxPrice(), orderItem.getPrice()));

            statisticDTOMap.put(categoryName, stat);
        }
        // calculate the average value for each category
        for(CategoryStatisticDTO stat : statisticDTOMap.values()){
            if(stat.getSoldQuantity() > 0){
                stat.setAveragePrice(stat.getRevenue() / stat.getSoldQuantity()); // Calculate the average value
            }
        }
       // convert list to `Page<CategoryStatisticDTO>`
        List<CategoryStatisticDTO> categoryStatisticDTOS = new ArrayList<>(statisticDTOMap.values());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), categoryStatisticDTOS.size());

        return new PageImpl<>(categoryStatisticDTOS.subList(start, end), pageable, categoryStatisticDTOS.size());
    }
}
