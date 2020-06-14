package cn.edu.fudan.measureservice.mapper;

import cn.edu.fudan.measureservice.domain.core.FileMeasure;
import org.springframework.stereotype.Repository;

/**
 * description:
 *
 * @author fancying
 * create: 2020-06-10 23:43
 **/
@Repository
public interface FileMeasureMapper {

    void insertOneFileMeasure(FileMeasure fileMeasure);

}