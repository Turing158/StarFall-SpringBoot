package com.starfall.service;

import com.starfall.dao.SearchDao;
import com.starfall.entity.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ScriptType;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private SearchDao searchDao;
    @Autowired
    private ElasticsearchOperations elasticsearch;

    @Async
    public void saveTopic(Search search){
        searchDao.save(search);
    }

    @Async
    public void saveUserName(String user,String name){
        try {
            Criteria criteria = new Criteria("user").is(user);
            CriteriaQuery query = new CriteriaQuery(criteria);

            Map<String, Object> params = new HashMap<>();
            params.put("newName", name);

            UpdateQuery updateQuery = UpdateQuery.builder(query)
                    .withScriptType(ScriptType.INLINE)
                    .withScript("ctx._source.name = params.newName")
                    .withParams(params)
                    .withMaxRetries(3)
                    .withLang("painless")
                    .build();

            ByQueryResponse response = elasticsearch.updateByQuery(
                    updateQuery,
                    IndexCoordinates.of("topic_search")
            );
        } catch (Exception e) {
            System.err.println("Update by query 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Async
    public void deleteById(String id){
        searchDao.deleteById(id);
    }

    @Async
    public void deleteAllByUser(String user){
        searchDao.deleteAll(searchDao.findAllByUser(user));
    }
}
