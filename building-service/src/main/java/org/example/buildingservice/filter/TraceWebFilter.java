//package org.example.buildingservice.filter;
//
//import org.slf4j.MDC;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@Component
//public class TraceWebFilter implements WebFilter {
//
//    private static final String TRACE_ID_HEADER = "X-B3-SpanId";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        String traceId = UUID.randomUUID().toString();
//        MDC.put(TRACE_ID_HEADER, traceId);
//        return chain.filter(exchange)
//                .doOnSuccess(aVoid -> MDC.remove(TRACE_ID_HEADER))
//                .doOnError(throwable -> MDC.remove(TRACE_ID_HEADER));
//    }
//}