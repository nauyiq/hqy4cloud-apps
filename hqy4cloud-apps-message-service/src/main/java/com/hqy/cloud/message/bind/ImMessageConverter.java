package com.hqy.cloud.message.bind;

import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.common.base.converter.CommonConverter;
import com.hqy.cloud.message.bind.dto.ImUserSettingInfoDTO;
import com.hqy.cloud.message.bind.vo.UserImSettingVO;
import com.hqy.cloud.message.bind.vo.UserInfoVO;
import com.hqy.cloud.message.db.entity.UserSetting;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/8/24 14:21
 */
@SuppressWarnings("all")
@Mapper(uses = CommonConverter.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImMessageConverter {

    ImMessageConverter CONVERTER = Mappers.getMapper(ImMessageConverter.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    UserInfoVO convert(AccountProfileStruct struct);

    @Mappings({
            @Mapping(source = "inviteGroup", target = "isInviteGroup"),
            @Mapping(source = "syncSetting", target = "isSyncSetting"),
            @Mapping(source = "queryAccount", target = "isQueryAccount")
    })
    UserImSettingVO convert(UserSetting userSetting);


    AccountProfileStruct convertProfileStruct(UserSetting userSetting);

    @Mappings({
            @Mapping(source = "isInviteGroup", target = "inviteGroup"),
            @Mapping(source = "isSyncSetting", target = "syncSetting"),
            @Mapping(source = "isQueryAccount", target = "queryAccount")
    })
    void update(UserImSettingVO setting, @MappingTarget UserSetting userSetting);

    void update(ImUserSettingInfoDTO setting, @MappingTarget UserSetting userSetting);
}
