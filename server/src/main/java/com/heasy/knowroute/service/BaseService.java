package com.heasy.knowroute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseService {
	@Autowired
    protected JdbcTemplate jdbcTemplate;
}
