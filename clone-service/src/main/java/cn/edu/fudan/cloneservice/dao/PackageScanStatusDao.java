package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.mapper.PackageScanStatusMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class PackageScanStatusDao {


    PackageScanStatusMapper packageScanStatusMapper;

}
