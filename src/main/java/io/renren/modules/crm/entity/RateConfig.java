package io.renren.modules.crm.entity;

public class RateConfig {
    private Long id;
    private Long deptId;
    private String payType;
    private float rate;
    private float max;
    private float shareBenefit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getShareBenefit() {
        return shareBenefit;
    }

    public void setShareBenefit(float shareBenefit) {
        this.shareBenefit = shareBenefit;
    }

    @Override
    public String toString() {
        return "RateConfig{" +
                "id=" + id +
                ", deptId=" + deptId +
                ", payType='" + payType + '\'' +
                ", rate=" + rate +
                ", max=" + max +
                ", shareBenefit=" + shareBenefit +
                '}';
    }
}
