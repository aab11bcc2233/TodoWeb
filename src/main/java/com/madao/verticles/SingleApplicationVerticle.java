package com.madao.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.redis.RedisClient;

public class SingleApplicationVerticle extends AbstractVerticle {
    private static final String HTTP_HOST = "0.0.0.0";
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int HTTP_PORT = 8082;
    private static final int REDIS_PORT = 6379;

    private RedisClient redis;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
    }
}
