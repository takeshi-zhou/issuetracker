package cn.edu.fudan.cloneservice.service;

import cn.edu.fudan.cloneservice.domain.*;
import org.eclipse.jgit.lib.PersonIdent;

import java.util.List;
import java.util.Set;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-19
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
public interface CloneMeasureService {

    CloneMeasure insertCloneMeasure(String repoId, String commitId);

    CloneMessage getCloneMeasure(String repoId, String developer, String start, String end);

    void deleteCloneMeasureByRepoId(String repoId);

    void scanCloneMeasure(String repoId, String startCommitId);

}
