package io.renren.modules.crm.entity;

import io.renren.modules.sys.entity.SysDeptEntity;
import org.hibernate.validator.constraints.NotEmpty;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

/***
 * 只作为pojo类使用
 */
public class NewMerchInfoEntity {

    private Long id;

    private Long merchId;


    private String merchno;

    @NotEmpty(message="商铺名不能为空")
    private String name;

    private int merchType;
    private String legalName;
    private String mobile;
    private String address;
    private int status;
    private int quickSettle;
    private String industry;
    private String merchnoSub;
    private String path;

//
//    //部门ID
//    private Long deptId;

    //上级部门ID，一级部门为0
    private Long parentId;

    //上级部门名称
    private String parentName;
    //排序
    private Integer orderNum;



    private Date createTime;

    private Date updateTime;

    //@TODO
    private int deptType;

    private String userName;
    private String password;

    private List<RateConfig> rateConfigs;

    @Transient
    public MerchInfoEntity getMerchInfo(){
        MerchInfoEntity entity = new MerchInfoEntity();
        if(this.merchId!=null)
            entity.setId((int)this.merchId.longValue());
        entity.setMerchno(this.merchno);
        entity.setMerchName(this.name);
        entity.setMerchType(this.merchType);
        entity.setLegalName(this.legalName);
        entity.setMobile(this.mobile);
        entity.setAddress(this.address);
        entity.setStatus(this.status);
        entity.setQuickSettle(this.quickSettle);
        entity.setIndustry(this.industry);
        entity.setMerchnoSub(this.merchnoSub);
        entity.setCreateTime(this.createTime);
        if(this.id!=null)
            entity.setMcId(this.id);

        return entity;
    }

    @Transient
    public SysDeptEntity getDept(){
        SysDeptEntity deptEntity = new SysDeptEntity();
        deptEntity.setMcId(this.id);
        deptEntity.setDeptType(this.deptType);
        deptEntity.setName(this.name);
        deptEntity.setParentId(this.parentId);
        deptEntity.setParentName(this.parentName);
        deptEntity.setLegalName(this.getLegalName());
        deptEntity.setMobile(this.mobile);
        deptEntity.setAddress(this.address);
        deptEntity.setIndustry(this.industry);
        deptEntity.setOrderNum(this.orderNum);
        deptEntity.setOrderNum(0);
        return deptEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMerchno() {
        return merchno;
    }

    public void setMerchno(String merchno) {
        this.merchno = merchno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMerchType() {
        return merchType;
    }

    public void setMerchType(int merchType) {
        this.merchType = merchType;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getQuickSettle() {
        return quickSettle;
    }

    public void setQuickSettle(int quickSettle) {
        this.quickSettle = quickSettle;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getMerchnoSub() {
        return merchnoSub;
    }

    public void setMerchnoSub(String merchnoSub) {
        this.merchnoSub = merchnoSub;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getDeptType() {
        return deptType;
    }

    public void setDeptType(int deptType) {
        this.deptType = deptType;
    }

    public List<RateConfig> getRateConfigs() {
        return rateConfigs;
    }

    public void setRateConfigs(List<RateConfig> rateConfigs) {
        this.rateConfigs = rateConfigs;
    }

    public Long getMerchId() {
        return merchId;
    }

    public void setMerchId(Long merchId) {
        this.merchId = merchId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
