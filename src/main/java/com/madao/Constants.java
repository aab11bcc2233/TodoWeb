package com.madao;

public final class Constants {

    public static final String REDIS_TODO_KEY = "VERT_TODO";

    public static final String KEY_CONTENT_TYPE = "content-type";
    public static final String VALUE_CONTENT_TYPE = "application/json;charset=utf8e";

    private Constants() {}

    public static final String API_GET = "/todos/:todoId";
    public static final String API_LIST_ALL = "/todos";
    public static final String API_CREATE = "/todos";
    public static final String API_UPDATE ="/todos/:todoId" ;
    public static final String API_DELETE = "/todos/:todoId";
    public static final String API_DELETE_ALL = "/todos";
}
