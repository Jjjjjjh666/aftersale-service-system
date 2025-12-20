package cn.edu.xmu.service.dao;

import cn.edu.xmu.service.dao.po.ServiceProviderDraftPo;
import org.apache.ibatis.annotations.*;

/**
 * 服务商草稿Mapper
 */
@Mapper
public interface ServiceProviderDraftMapper {

    /**
     * 根据ID查询草稿
     */
    @Select("SELECT * FROM service_provider_draft WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "serviceProviderId", column = "service_provider_id"),
            @Result(property = "providerName", column = "provider_name"),
            @Result(property = "contactPerson", column = "contact_person"),
            @Result(property = "contactPhone", column = "contact_phone"),
            @Result(property = "address", column = "address"),
            @Result(property = "status", column = "status"),
            @Result(property = "opinion", column = "opinion"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    ServiceProviderDraftPo findById(@Param("id") Long id);

    /**
     * 更新草稿状态
     */
    @Update("UPDATE service_provider_draft SET status = #{status}, opinion = #{opinion}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(ServiceProviderDraftPo po);

    /**
     * 插入草稿（用于测试）
     */
    @Insert("INSERT INTO service_provider_draft (service_provider_id, provider_name, contact_person, " +
            "contact_phone, address, status, created_at, updated_at) " +
            "VALUES (#{serviceProviderId}, #{providerName}, #{contactPerson}, #{contactPhone}, " +
            "#{address}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceProviderDraftPo po);
}

