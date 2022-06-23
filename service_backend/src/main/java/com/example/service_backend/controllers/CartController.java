package com.example.service_backend.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.service_backend.exception.implementations.CartNotFoundException;
import com.example.service_backend.exception.implementations.ForbiddenOperationException;
import com.example.service_backend.exception.implementations.OrderNotFoundException;
import com.example.service_backend.exception.implementations.ProductNotFoundException;
import com.example.service_backend.model.BusinessUser;
import com.example.service_backend.model.Cart;
import com.example.service_backend.model.CartInfo;
import com.example.service_backend.model.Order;
import com.example.service_backend.model.OrderRequest;
import com.example.service_backend.model.OrderStatus;
import com.example.service_backend.model.Product;
import com.example.service_backend.model.webhooks.Hook;
import com.example.service_backend.model.webhooks.WebHook;
import com.example.service_backend.model.Costumer;
import com.example.service_backend.services.CartInfoService;
import com.example.service_backend.security.auth.AuthHandler;
import com.example.service_backend.services.CartService;
import com.example.service_backend.services.OrderRequestService;
import com.example.service_backend.services.ProductsService;
import com.example.service_backend.utils.MessageResponse;
import com.example.service_backend.utils.WebHookEvent;
import com.example.service_backend.services.CostumerService;

@RestController
@RequestMapping("/api")
public class CartController {

    private final AuthHandler authHandler;
    private final CostumerService userService;
    private final ProductsService productsService;
    private final CartService cartService;
    private final CartInfoService cartInfoService;
    private final OrderRequestService orderRequestService;

    @Autowired
    public CartController(CostumerService userService, ProductsService productsService, OrderRequestService orderRequestService, CartService cartService, CartInfoService cartInfoService, AuthHandler authHandler) {
        this.userService = userService;
        this.productsService = productsService;
        this.orderRequestService = orderRequestService;
        this.cartService = cartService;
        this.cartInfoService = cartInfoService;
        this.authHandler = authHandler;
    }

    @PatchMapping("/cart/{productId}/add")
    public MessageResponse addProductToCart(@PathVariable Long productId) {

        Optional<Product> productOptional = productsService.findById(productId);

        if (productOptional.isEmpty())
            throw new ProductNotFoundException(String.format("The product %s could not be found.", productId));

        Product product = productOptional.get();
        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());

        Optional<Cart> cartOptional = cartService.findByCostume(res);

        if (cartOptional.isEmpty())
            throw new CartNotFoundException(String.format("The cart could not be found."));
    
        Cart cart = cartOptional.get();

        boolean bol = true;
        for (CartInfo info : cartInfoService.getAllCarts()){
            if (info.getProduct().equals(product) && info.getCart().equals(res.getC())){
                info.setQuantity(info.getQuantity()+1);
                cartInfoService.save(info);
                bol = false;
                break;
            }
        }
        if(bol)
            cartInfoService.save(new CartInfo(cart, product, 1));

        /* 
        boolean bol = true;
        for (CartInfo info : set){
            if (info.getProduct().equals(product)){
                info.setQuantity(info.getQuantity()+1);
                bol = false;
            }
        }
        if(bol)
            set.add(new CartInfo(product, 1));
        */

        //cart.setCartInfo(set);

        //res.getCart().add(new CartInfo(product, 1));

        //userService.updateUser(res);
        //cartService.updateCart(cart);

        return new MessageResponse("The product was successfully added to cart!");
    }
    
    @PatchMapping("/cart/{productId}/remove")
    public MessageResponse removeProductFromCart(@PathVariable Long productId) {

        Optional<Product> productOptional = productsService.findById(productId);

        if (productOptional.isEmpty())
            throw new ProductNotFoundException(String.format("The product %s could not be found.", productId));

        Product product = productOptional.get();
        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());

        Optional<Cart> cartOptional = cartService.findByCostume(res);

        if (cartOptional.isEmpty())
            throw new CartNotFoundException(String.format("The cart could not be found."));

        boolean bol = true;
        for (CartInfo info : cartInfoService.getAllCarts()){
            if (info.getProduct().equals(product) && info.getCart().equals(res.getC())){
                info.setQuantity(info.getQuantity()-1);
                if(info.getQuantity()==0)
                    cartInfoService.deleteCart(info);
                else
                    cartInfoService.save(info);
                bol = false;
                break;
            }
        }
        if(bol)
            throw new ProductNotFoundException(String.format("The product %s could not be found on cart.", productId));

        return new MessageResponse("The product was successfully removed from cart!");
    }

    @PutMapping("/cart/{productId}/clear")
    public MessageResponse clearProductFromCart(@PathVariable Long productId){

        Optional<Product> productOptional = productsService.findById(productId);

        if (productOptional.isEmpty())
            throw new ProductNotFoundException(String.format("The product %s could not be found.", productId));

        Product product = productOptional.get();
        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());

        Optional<Cart> cartOptional = cartService.findByCostume(res);

        if (cartOptional.isEmpty())
            throw new CartNotFoundException(String.format("The cart could not be found."));

        boolean bol = true;
        for (CartInfo info : cartInfoService.getAllCarts()){
            if (info.getProduct().equals(product) && info.getCart().equals(res.getC())){
                cartInfoService.deleteCart(info);
                bol = false;
                break;
            }
        }
        if(bol)
            throw new ProductNotFoundException(String.format("The product %s could not be found on cart.", productId));

        return new MessageResponse("The product was successfully removed from cart!");
    }
    
    @PostMapping("/cart/order")
    public MessageResponse createOrder() throws ParseException{

        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());
        Optional<Cart> cartOptional = cartService.findByCostume(res);
        Cart cart = cartOptional.get();
        double totalPrice = 0.0;
        Order order = new Order();

        for (CartInfo info : cartInfoService.getAllCarts()){
            if (info.getCart().equals(cart)){
                totalPrice = totalPrice + (info.getProduct().getPrice() * info.getQuantity());
                order.getProducts().add(info.getProduct());
            }
        }

        if(order.getProducts().isEmpty())
            throw new ForbiddenOperationException("Can't order because there are no products on your cart.");

        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        order.setDate(date);
        order.setTotalPrice(totalPrice);
        order.setCostumer(res.getUsername());

        List<Order> temp = res.getOrders();
        temp.add(order);
        res.setOrders(temp);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBusinessUsername("SendASnack");
        orderRequest.setOrderStatus(null);
        orderRequest.setOrder(order);
        orderRequest.setCostumer(res);

        JSONObject json = new JSONObject();
        JSONObject costumer = new JSONObject();
        JSONArray prods = new JSONArray();
        JSONObject orderr = new JSONObject();

        costumer.put("address", new JSONObject().put("city", orderRequest.getCostumer().getAddress().getCity())
                .put("street", orderRequest.getCostumer().getAddress().getStreet())
                .put("postalCode", orderRequest.getCostumer().getAddress().getPostalCode()))
                .put("email", orderRequest.getCostumer().getEmail())
                .put("name", orderRequest.getCostumer().getName());

        List<Product> products = orderRequest.getOrder().getProducts();

        for (Product product : products){
            JSONObject jsonTemp = new JSONObject();
            jsonTemp.put("name", product.getName())
                    .put("description", product.getDescription())
                    .put("price", product.getPrice())
                    .put("ingredients", product.getIngredients());
            prods.put(jsonTemp);
        }

        orderr.put("products", prods)
                .put("totalPrice", orderRequest.getOrder().getTotalPrice())
                .put("date", dt.format(orderRequest.getOrder().getDate()));
        
        json.put("orderStatus", orderRequest.getOrderStatus()).put("deliveryTime", 0).put("order", orderr).put("costumer", costumer).put("businessUsername", "SendASnack");

        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(BusinessUser.businessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(json.toString(),headers);

        ResponseEntity<MessageResponse> response = template.exchange("http://20.77.90.223:8080/api/business/orders", HttpMethod.POST, entity, MessageResponse.class);

        if(response.getBody().getMessage().equals("Your order was successfully placed.")){
            String stringToConvert = String.valueOf(response.getBody().getArgs());
            Long orderId = Long.parseLong(stringToConvert);
            orderRequest.setOrderRequestId(orderId);
            orderRequestService.updateOrderRequestId(orderRequest);

            WebHook webHook = new WebHook();
            webHook.setId(null);
            webHook.setBusinessUsername("SendASnack");
            webHook.setHook(new Hook(null, "http://20.77.90.223:8080/api/webhooks/order/status", HttpMethod.POST, "VALUE"));
            webHook.setWhen(WebHookEvent.DELIVERY_STATUS);

            HttpEntity<WebHook> entity2 = new HttpEntity<WebHook>(webHook,headers);

            ResponseEntity<MessageResponse> responseWebHook = template.exchange("http://20.77.90.223:8080/api/business/webhook", HttpMethod.POST, entity2, MessageResponse.class);

            for (CartInfo info:cartInfoService.getAllCarts()){
                if (info.getCart().equals(res.getC())){
                    cartInfoService.deleteCart(info);
                }
            }

        }

        return response.getBody();
    }

    @DeleteMapping("/cart/{orderId}/cancel")
    public MessageResponse cancelOrder(@PathVariable Long orderId){

        for(OrderRequest orderRequest : orderRequestService.getAllOrders()){
            if (orderRequest.getOrderRequestId()==orderId){
                RestTemplate template = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(BusinessUser.businessToken);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<String>(headers);
        
                ResponseEntity<MessageResponse> response = template.exchange("http://20.77.90.223:8080/api/business/orders/" + orderId, HttpMethod.DELETE, entity, MessageResponse.class);
                
                if (response.getBody().getMessage().equals(String.format("The order %s was cancelled.", orderId)))
                    orderRequestService.delete(orderRequest);

                return response.getBody();
            }
        }

        return new MessageResponse("There is no order with that ID!");

    }

    @GetMapping("/orders/pending")
    public List<OrderRequest> getOrdersRequest(){
        
        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());

        List<Order> orders = res.getOrders();

        List<OrderRequest> ordersPending = new ArrayList<>();

        for(OrderRequest orderRequest : orderRequestService.getAllOrders()){
            for (Order ord : orders){
                if (orderRequest.getOrder().equals(ord)){
                    orderRequest.setCostumer(null);
                    ordersPending.add(orderRequest);
                }
            }
        }

        if (ordersPending.isEmpty())
            throw new OrderNotFoundException("There is no pending orders for the current user");

        return ordersPending;
    }

    @GetMapping("/history")
    public List<Order> getPreviousOrders(){

        Costumer res = userService.findByUsername(authHandler.getCurrentUsername());

        List<Order> orders = new ArrayList<>();
        List<Order> iterate = res.getOrders();

        if (res.getOrders().isEmpty())
            throw new OrderNotFoundException("No available orders for the current user.");

        for(OrderRequest orderRequest : orderRequestService.getAllOrders()){
            for (Order ord : iterate){
                if (orderRequest.getOrder().equals(ord) && !(orderRequest.getOrderStatus()==null || orderRequest.getOrderStatus().equals(OrderStatus.DELIVERED))){
                    orders.add(ord);
                }
            }
        }

        if (orders.isEmpty())
            throw new OrderNotFoundException("No available previous orders for the current user.");

        return res.getOrders();
    }

}
