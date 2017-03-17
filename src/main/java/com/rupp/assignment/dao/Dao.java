package com.rupp.assignment.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;


public interface Dao <T extends Object>{
    
    T findById(@Param("id") int id);
    
    void add(T domain);
        
    void update(T domain);
    
   //Select("select * from category")
    List<T> getAll();
    
    void remove(@Param("id") int id);
    
    List<T> getPage(@Param("limit") int limit, @Param("offset") int offset);
    
    List<T> getAllWithOffset(@Param("limit") int limit, @Param("offset") int offset);
    
    Integer count();
}
