package cn.jantd.modules.system.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author xiagf
 * @since 2019-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDataLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    /**
     * id
     */
    private String id;
    /**
     * 创建人登录名称
      */
    private String createBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /**
     * 创建日期
     */
    private Date createTime;
    /**
     * 更新人登录名称
     */
    private String updateBy;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /**
     * 更新日期
     */
    private Date updateTime;
    /**
     * 表名
     */
    private String dataTable;
    /**
     * 数据ID
     */
    private String dataId;
    /**
     * 数据内容
     */
    private String dataContent;
    /**
     * 版本号
     */
    private String dataVersion;
}
