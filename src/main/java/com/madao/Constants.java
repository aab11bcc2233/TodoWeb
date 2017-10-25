package com.madao;

public final class Constants {

    public static final String REDIS_TODO_KEY = "VERT_TODO";

    private Constants() {}

    public static final String API_GET = "/todos/:todoId";
    public static final String API_LIST_ALL = "/todos";
    public static final String API_CREATE = "/todos";
    public static final String API_UPDATE ="/todos/:todoId" ;
    public static final String API_DELETE = "/todos/:todoId";
    public static final String API_DELETE_ALL = "/todos";
}
