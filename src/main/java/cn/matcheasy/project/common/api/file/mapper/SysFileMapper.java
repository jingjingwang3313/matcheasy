package cn.matcheasy.project.common.api.file.mapper;

import cn.matcheasy.project.common.api.file.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * @class: SysFileMapper
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: TODO
 */
@Mapper
public interface SysFileMapper {

    SysFile selectByPrimaryKey(Long fileId);

    int deleteByPrimaryKey(Long fileId);

    int insert(SysFile record);

    int insertSelective(SysFile record);

    int updateByPrimaryKeySelective(SysFile record);

    int updateByPrimaryKey(SysFile record);

}
