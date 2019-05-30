package cn.edu.fudan.cloneservice.dao;

import cn.edu.fudan.cloneservice.domain.Commit;
import cn.edu.fudan.cloneservice.mapper.RepoCommitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CommitDao {

    RepoCommitMapper repoCommitMapper;

    public CommitDao(RepoCommitMapper repoCommitMapper) {
        this.repoCommitMapper = repoCommitMapper;
    }

    public List<String> getCommitList(String repo_id){
        List<Commit> lc= new ArrayList<>();
        lc=         repoCommitMapper.selectCommitByRepoId(repo_id);
        List<String> ls = new ArrayList<>();
        for(Commit commit:lc){
            ls.add(commit.getCommit_id());
        }
        return ls;
    }

}
