//package org.example.buildingservice.filter;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.logging.log4j.ThreadContext;
//import org.slf4j.MDC;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@Component
//@Order(-100)
//@RequiredArgsConstructor
//public class RequestIdFilter implements WebFilter {
//    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String requestId = UUID.randomUUID().toString();
//        exchange.getAttributes().put(REQUEST_ID_ATTRIBUTE, requestId);
//        MDC.put("requestId", requestId);
//
//        return chain.filter(exchange)
//                .doFinally(signalType -> {
//                    MDC.remove("requestId");
//                    exchange.getAttributes().remove(REQUEST_ID_ATTRIBUTE);
//                });
//    }
//}
