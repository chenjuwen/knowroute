package com.heasy.knowroute.bean;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="定点属性JavaBean类")
public class FixedPointInfoBean implements Serializable {
	private static final long serialVersionUID = 5825597504308144333L;

	@ApiModelProperty(value="id")
	private int id;
	
	@ApiModelProperty(value="用户ID")
	private int userId;
	
	@ApiModelProperty(value="类别ID")
    private int categoryId;
	
	@ApiModelProperty(value="经度")
    private double longitude;
	
	@ApiModelProperty(value="纬度")
    private double latitude;
	
	@ApiModelProperty(value="地址")
    private String address;
	
	@ApiModelProperty(value="备注")
    private String comments;
	
	@ApiModelProperty(value="标签")
    private String label;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
