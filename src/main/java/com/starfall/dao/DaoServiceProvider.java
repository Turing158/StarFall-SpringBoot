package com.starfall.dao;

import com.starfall.entity.UserNotice;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public class DaoServiceProvider {

    public String batchUpdateStatus(List<UserNotice> userNotices){
        StringBuilder caseBuilder = new StringBuilder("CASE id");
        for (UserNotice notice : userNotices) {
            caseBuilder.append(" WHEN ")
                    .append(notice.getId())
                    .append(" THEN ")
                    .append(1);
        }
        caseBuilder.append(" END");
        return new SQL(){
            {
                UPDATE("starfall.user_notice");
                SET("status = "+ caseBuilder);
                WHERE("id IN ("+String.join(",",String.join(", ", userNotices.stream().map(UserNotice::getId).toArray(String[]::new)))+")");
            }
        }.toString();
    }
}
