package io.renren.modules.crm.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易记录
 */
public class TransDataEntity {
    private Integer id;
    private String cur;
    private String batchNo;
    private String orderId;
    private String traceNo;
    private String amt;
    private String txnRef;
    private String txnType;
    private String terminalId;
    private String shortPan;
    private String merchantId;
    private String respCode;
    private Date txnDatetime;
    private String cardType;
    private String hashPan;
    private String issuerCode;

    private String merchName;
    private BigDecimal sharePoint = BigDecimal.ZERO;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getShortPan() {
        return shortPan;
    }

    public void setShortPan(String shortPan) {
        this.shortPan = shortPan;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public Date getTxnDatetime() {
        return txnDatetime;
    }

    public void setTxnDatetime(Date txnDatetime) {
        this.txnDatetime = txnDatetime;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getHashPan() {
        return hashPan;
    }

    public void setHashPan(String hashPan) {
        this.hashPan = hashPan;
    }

    public String getIssuerCode() {
        return issuerCode;
    }

    public void setIssuerCode(String issuerCode) {
        this.issuerCode = issuerCode;
    }

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public BigDecimal getSharePoint() {
        return sharePoint;
    }

    public void setSharePoint(BigDecimal sharePoint) {
        this.sharePoint = sharePoint;
    }
}
