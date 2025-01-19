package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        // your code here
        orderMap.put(order.getId(),order);


    }

    public void savePartner(String partnerId){
        // your code here
        // create a new partner with given partnerId and save it
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId,partner);

    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            // your code here
            //add order to given partner's order list
            //increase order count of partner
            //assign partner to thi
                // Add order to the partner's order list
                partnerToOrderMap.putIfAbsent(partnerId, new HashSet<>());
                partnerToOrderMap.get(partnerId).add(orderId);

                // Increment the partner's order count
                DeliveryPartner partner = partnerMap.get(partnerId);
                partner.incrementOrderCount();

                // Assign the partner to the order
                orderToPartnerMap.put(orderId, partnerId);
            }

    }

    public Order findOrderById(String orderId){
        // your code here
        return orderMap.get(orderId);

    }

    public DeliveryPartner findPartnerById(String partnerId){
        // your code here
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
        if (partnerMap.containsKey(partnerId)) {
            return partnerMap.get(partnerId).getNumberOfOrders();
        }
        return 0; // Return 0 if the partner doesn't exist

    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        return partnerToOrderMap.containsKey(partnerId) ? new ArrayList<>(partnerToOrderMap.get(partnerId)) : new ArrayList<>();

    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId){
        // your code here
        // delete partner by ID
        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                orderToPartnerMap.remove(orderId);
            }
            partnerToOrderMap.remove(partnerId);
        }
        partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        // your code here
        // delete order by ID
        if (orderToPartnerMap.containsKey(orderId)) {
            String partnerId = orderToPartnerMap.get(orderId);
            partnerToOrderMap.get(partnerId).remove(orderId);
            orderToPartnerMap.remove(orderId);
        }
        orderMap.remove(orderId);
    }

    public Integer findCountOfUnassignedOrders(){
        // your code here
        return (int) orderMap.keySet().stream()
                .filter(orderId -> !orderToPartnerMap.containsKey(orderId))
                .count();
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        int time = convertTimeToMinutes(timeString);
        if (partnerToOrderMap.containsKey(partnerId)) {
            return (int) partnerToOrderMap.get(partnerId).stream()
                    .map(orderMap::get)
                    .filter(order -> order.getDeliveryTime() > time)
                    .count();
        }
        return 0;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        if (partnerToOrderMap.containsKey(partnerId)) {
            int maxTime = partnerToOrderMap.get(partnerId).stream()
                    .map(orderMap::get)
                    .mapToInt(Order::getDeliveryTime)
                    .max()
                    .orElse(0);
            return convertMinutesToTime(maxTime);
        }
        return null;
    }

    private int convertTimeToMinutes(String timeString) {
        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private String convertMinutesToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}