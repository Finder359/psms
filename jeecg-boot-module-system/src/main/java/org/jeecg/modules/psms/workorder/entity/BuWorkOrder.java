package org.jeecg.modules.psms.workorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("bu_work_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bu_work_order对象", description = "运维工单表")
public class BuWorkOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;

    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;

    @ApiModelProperty(value = "工单编号")
    private String orderNo;

    @ApiModelProperty(value = "企业编码")
    private String companyCode;

    @ApiModelProperty(value = "监测点ID")
    private String pointId;

    @ApiModelProperty(value = "故障描述")
    private String faultDesc;

    @ApiModelProperty(value = "报修联系人")
    private String repairMan;

    @ApiModelProperty(value = "报修电话")
    private String repairPhone;

    @ApiModelProperty(value = "工单状态")
    private String orderStatus;

    @ApiModelProperty(value = "维修人员")
    private String assignTo;

    @ApiModelProperty(value = "维修处理结果")
    private String handleResult;

    @ApiModelProperty(value = "验收评价")
    private Integer evaluateScore;

    @ApiModelProperty(value = "验收意见")
    private String evaluateOpinion;
}
