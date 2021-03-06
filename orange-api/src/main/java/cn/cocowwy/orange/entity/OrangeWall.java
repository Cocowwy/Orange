package cn.cocowwy.orange.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.time.LocalDateTime;


/**
 * @author Cocowwy
 * @since 2020-12-03 14:38:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Builder
@TableName(value = "t_orange_wall")
public class OrangeWall extends Model {
    private static final long serialVersionUID = 692784333772271610L;

    /**
     * 上墙唯一标识id
     */
    @TableField("wall_id")
    private Long wallId;

    /**
     * 创建者userid
     */
    @TableField("create_user")
    private Long createUser;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 到期时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 状态 0正常 1到期  2举报 3封禁
     */
    @TableField("status_tag")
    private String statusTag;

    /**
     * 类型  0招新  1表白   2公告   3广告  4其它
     */
    @TableField("wall_tag")
    private String wallTag;

    /**
     * 图片地址

     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 图片
     */
    @TableField("image")
    private InputStream image;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 所花费的橙币数量
     */
    @TableField("consume")
    private Integer consume;

    /**
     * 记录点赞的用户id 数组
     */
    @TableField("like")
    private Integer like;

    /**
     * 记录评论的用户信息  json  用户昵称  评论时间
     */
    @TableField("comment")
    private String comment;

    /**
     * 预留字段1
     */
    @TableField("rsrv_str1")
    private String rsrvStr1;

    /**
     * 预留字段2
     */
    @TableField("rsrv_str2")
    private String rsrvStr2;

    /**
     * 预留字段3
     */
    @TableField("rsrv_str3")
    private String rsrvStr3;

    /**
     * 预留字段4
     */
    @TableField("rsrv_str4")
    private String rsrvStr4;

}