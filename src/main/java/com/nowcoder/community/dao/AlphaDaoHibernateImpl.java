package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author xi_wang
 * @create 2021-11-2021/11/29-22:27
 */
@Repository("alphaDaoHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String get() {
        return "Hibernate";
    }
}
