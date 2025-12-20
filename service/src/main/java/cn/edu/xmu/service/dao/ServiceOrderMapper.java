package cn.edu.xmu.service.dao;

import cn.edu.xmu.service.dao.po.ServiceOrderPo;
import org.apache.ibatis.annotations.*;

/**
 * 服务单Mapper - 对应表 service_order
 */
@Mapper
public interface ServiceOrderMapper {

    /**
     * 插入服务单
     * 注意：数据库字段是 created_at/updated_at，不是 gmt_create/gmt_modified
     */
    @Insert("INSERT INTO service_order (shop_id, aftersales_id, service_provider_id, type, status, consignee, address, tracking_number, created_at, updated_at) " +
            "VALUES (#{shopId}, #{aftersalesId}, #{serviceProviderId}, #{type}, #{status}, #{consignee}, #{address}, #{trackingNumber}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceOrderPo po);

    /**
     * 根据ID查询服务单
     */
    @Select("SELECT * FROM service_order WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "shopId", column = "shop_id"),
            @Result(property = "aftersalesId", column = "aftersales_id"),
            @Result(property = "serviceProviderId", column = "service_provider_id"),
            @Result(property = "type", column = "type"),
            @Result(property = "status", column = "status"),
            @Result(property = "consignee", column = "consignee"),
            @Result(property = "address", column = "address"),
            @Result(property = "trackingNumber", column = "tracking_number"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    ServiceOrderPo findById(@Param("id") Long id);

    /**
     * 根据售后单ID查询服务单
     */
    @Select("SELECT * FROM service_order WHERE aftersales_id = #{aftersalesId}")
    ServiceOrderPo findByAftersalesId(@Param("aftersalesId") Long aftersalesId);

    /**
     * 更新服务单状态
     * 注意：数据库字段是 updated_at，不是 gmt_modified
     */
    @Update("UPDATE service_order SET service_provider_id = #{serviceProviderId}, status = #{status}, " +
            "address = #{address}, tracking_number = #{trackingNumber}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateStatus(ServiceOrderPo po);
}

