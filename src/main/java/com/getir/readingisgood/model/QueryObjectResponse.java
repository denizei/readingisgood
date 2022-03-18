package com.getir.readingisgood.model;

import com.getir.readingisgood.model.exception.ExceptionObject;
import com.getir.readingisgood.model.exception.GeneralException;

import java.util.Map;

public class QueryObjectResponse<T> extends ObjectResponse<T>  {

    private Map<String,Object> query;

    public QueryObjectResponse(T data,Map<String,Object> query){
       super(data);
       this.query=query;
    }

    public QueryObjectResponse(GeneralException exception,Map<String,Object> query){
        this(400, exception,query);
    }

    public QueryObjectResponse(Integer status, GeneralException exception,Map<String,Object> query){
        super(status,exception);
        this.query=query;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }
}
