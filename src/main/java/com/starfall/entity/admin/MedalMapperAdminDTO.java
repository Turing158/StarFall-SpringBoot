package com.starfall.entity.admin;

import com.starfall.entity.MedalMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedalMapperAdminDTO extends MedalMapper {
    String userName;
}
