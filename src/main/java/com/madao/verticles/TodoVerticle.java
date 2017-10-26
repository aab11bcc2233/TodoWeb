package com.madao.verticles;

import com.madao.Constants;
import com.madao.entity.Todo;
import com.madao.service.TodoService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisOptions;

import java.util.HashSet;
import java.util.Set;

public class TodoVerticle extends AbstractVerticle {
    private static final String HOST = "0.0.0.0";
    private static final int PORT = 8082;

    private TodoService service;

    private void initData() {
        final String serviceType = config().getString("service.type", "redis");
        switch (serviceType) {
            case "jdbc":
                service = new JdbcTodoService(vertx, config());
                break;
            case "redis":
                RedisOptions config = new RedisOptions()
                        .setHost(config().getString("redis.host", "127.0.0.1"))
                        .setPort(config().getInteger("redis.port", 6379));

                service = new ReidsTodoService(vertx, config);
                break;
        }

        service.initData().setHandler(
                res -> {
                    if (res.failed()) {
                        System.err.println("[Error] Persistence service is not running!");
                        res.cause().printStackTrace();
                    }
                }
        );
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);

        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");

        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(BodyHandler.create());
        router.route().handler(
                CorsHandler.create("*")
                        .allowedHeaders(allowHeaders)
                        .allowedMethods(allowMethods)
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
                        PORT,
                        HOST,
                        result -> {
                            if (result.succeeded()) {
                                future.complete();
                            } else {
                                future.fail(future.cause());
                            }
                        });
    }

    private void handleGetTodo(RoutingContext context) {

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

    private void badRequest(RoutingContext context) {
        context.response().setStatusCode(400).end();
    }

    private void notFound(RoutingContext context) {
        context.response().setStatusCode(404).end();
    }

    private Todo writeObject(Todo todo, RoutingContext context) {
        int id = todo.getId();

        if (id > Todo.getIncId()) {
            Todo.setIncIdWith(id);
        } else if (id == 0) {
            todo.setIncId();
        }

        todo.setUrl(context.request().absoluteURI() + "/" + todo.getId());
        return todo;
    }
}
