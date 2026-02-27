package com.starfall.dao;

import com.starfall.entity.Search;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SearchDao extends ElasticsearchRepository<Search,String> {
    List<Search> findAllByUser(String user);
    int deleteAllByUser(String user);
}
