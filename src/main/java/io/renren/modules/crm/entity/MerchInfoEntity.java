package io.renren.modules.crm.entity;

import java.util.Date;

/**
 * 商户信息
 */
public class MerchInfoEntity {

    private Integer id;
    private String merchno;
    private String merchName;
    private int merchType;
    private String legalName;
    private String mobile;
    private String address;
    private int status;
    private int quickSettle;
    private String industry;
    private String merchnoSub;
    private Date createTime;
    private Date updateTime;
    private Long userId;
    private String username;

    public Integer getId() {
        return id;
    }

    public MerchInfoEntity setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getMerchno() {
        return merchno;
    }

    public MerchInfoEntity setMerchno(String merchno) {
        this.merchno = merchno;
        return this;
    }

    public String getMerchName() {
        return merchName;
    }

    public MerchInfoEntity setMerchName(String merchName) {
        this.merchName = merchName;
        return this;
    }

    public int getMerchType() {
        return merchType;
    }

    public MerchInfoEntity setMerchType(int merchType) {
        this.merchType = merchType;
        return this;
    }

    public String getLegalName() {
        return legalName;
    }

    public MerchInfoEntity setLegalName(String legalName) {
        this.legalName = legalName;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public MerchInfoEntity setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public MerchInfoEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public MerchInfoEntity setStatus(int status) {
        this.status = status;
        return this;
    }

    public int getQuickSettle() {
        return quickSettle;
    }

    public MerchInfoEntity setQuickSettle(int quickSettle) {
        this.quickSettle = quickSettle;
        return this;
    }

    public String getIndustry() {
        return industry;
    }

    public MerchInfoEntity setIndustry(String industry) {
        this.industry = industry;
        return this;
    }

    public String getMerchnoSub() {
        return merchnoSub;
    }

    public MerchInfoEntity setMerchnoSub(String merchnoSub) {
        this.merchnoSub = merchnoSub;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public MerchInfoEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public MerchInfoEntity setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
