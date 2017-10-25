package com.madao.verticles;

import com.madao.Constants;
import com.madao.entity.Todo;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.util.HashSet;
import java.util.Set;

public class SingleApplicationVerticle extends AbstractVerticle {
    private static final String HTTP_HOST = "0.0.0.0";
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int HTTP_PORT = 8082;
    private static final int REDIS_PORT = 6379;

    private RedisClient redis;

    private void initData() {
        RedisOptions config = new RedisOptions()
                .setHost(config().getString("redis.host", REDIS_HOST))
                .setPort(config().getInteger("redis.port", REDIS_PORT));

        this.redis = RedisClient.create(vertx, config);

        redis.hset(
                Constants.REDIS_TODO_KEY,
                "24",
                Json.encodePrettily(new Todo(24, "Something to do...", false, 1, "todo/ex")),
                res -> {
                    if (res.failed()) {
                        System.err.println("[Error] Redis service is not running!");
                        res.cause().printStackTrace();
                    }
                }
        );

    }

    @Override
    public void start(Future<Void> future) throws Exception {
        initData();

        Router router = Router.router(vertx);

        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");

        HashSet<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(
                CorsHandler.create("*")
                        .allowedHeaders(allowHeaders)
                        .allowedMethods(allowMethods)
        );

        router.route().handler(
                BodyHandler.create()
        );

        router.get(Constants.API_GET).handler(this::handleGetTodo);
        router.get(Constants.API_LIST_ALL).handler(this::handleGetAll);
        router.post(Constants.API_CREATE).handler(this::handleCreateTodo);
        router.patch(Constants.API_UPDATE).handler(this::handleUpdateTodo);
        router.delete(Constants.API_DELETE).handler(this::handleDeleteOne);
        router.delete(Constants.API_DELETE_ALL).handler(this::handleDeleteAll);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        HTTP_PORT,
                        HTTP_HOST,
                        result -> {
                            if (result.succeeded()) {
                                future.complete();
                            } else {
                                future.fail(result.cause());
                            }
                        });
    }

    private void handleGetTodo(RoutingContext context) {
        @Nullable String todoId = context.request().getParam("todoId");
        if (todoId == null) {
            sendError(400, context.response());
        } else {
            redis.hget(
                    Constants.REDIS_TODO_KEY,
                    todoId,
                    x -> {
                        if (x.succeeded()) {
                            String result = x.result();
                            if (result == null) {
                                sendError(400, context.response());
                            } else {
                                context.response()
                                        .putHeader("content-type", "application/json;charset=utf8")
                                        .end(result);
                            }
                        } else {
                            sendError(503, context.response());
                        }
                    }
            );
        }
    }

    private void handleGetAll(RoutingContext context) {

    }

    private void handleCreateTodo(RoutingContext context) {

    }

    private void handleUpdateTodo(RoutingContext context) {

    }

    private void handleDeleteOne(RoutingContext context) {

    }

    private void handleDeleteAll(RoutingContext context) {

    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
