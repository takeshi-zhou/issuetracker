/**
 * @description: 代码度量--Issue数量相关controller
 * @author: fancying
 * @create: 2019-04-08 16:55
 **/
package cn.edu.fudan.issueservice.controller;

import cn.edu.fudan.issueservice.service.IssueMeasureInfoService;
import cn.edu.fudan.issueservice.service.IssueRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueMeasurementController {

    private IssueMeasureInfoService issueMeasureInfoService;
    private IssueRankService issueRankService;

    @Autowired
    public void setIssueMeasureInfoService(IssueMeasureInfoService issueMeasureInfoService) {
        this.issueMeasureInfoService = issueMeasureInfoService;
    }

    @Autowired
    public void setIssueRankService(IssueRankService issueRankService) {
        this.issueRankService = issueRankService;
    }



}