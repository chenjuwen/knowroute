package com.heasy.knowroute.service;

import java.util.List;

import com.heasy.knowroute.bean.ContactBean;

public interface ContactService {
	public boolean save(ContactBean bean);
	public boolean update(ContactBean bean);
	public void delete(int userId, int id);
	public ContactBean get(int id);
	public List<ContactBean> list(int userId);
	public boolean existsContact(ContactBean bean);
}
