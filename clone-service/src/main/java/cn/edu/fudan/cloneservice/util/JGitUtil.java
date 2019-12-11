package cn.edu.fudan.cloneservice.util;

import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by njzhan
 * <p>
 * Date :2019-08-22
 * <p>
 * Description :
 * <p>
 * Version :1.0
 */
public class JGitUtil {

    public static Integer getCloneLineByDeveloper(String repo_path, String commit_id, List<CloneInstanceInfo> lci, String developer_name){
        Integer clone_line = 0;
        try {

            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(repo_path + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();
            // find the current commit id

            ObjectId curCommitId = repository.resolve(commit_id);


            for(CloneInstanceInfo ci: lci){
                BlameCommand blamer = new BlameCommand(repository);
                blamer.setStartCommit(curCommitId);
                blamer.setFilePath(ci.getFile_path());
                try {
                    BlameResult blame = blamer.call();
                    for (int i = ci.getStart_line(); i <= ci.getEnd_line(); i ++){
                        PersonIdent personIdent =  blame.getSourceAuthor(i);
                        if(personIdent.getName().equals(developer_name)){
                            clone_line ++;
                        }
                    }
                } catch (GitAPIException e) {
                    e.printStackTrace();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return clone_line;
    }

    public static Set<String> getDeveloperList(String repo_path){
        Set<String> spi = new HashSet<>();
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(repo_path + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();
            // find the current commit id
            Git git = new Git(repository);
            try {
                Iterable<RevCommit> log = git.log().call();
                for(RevCommit revCommit:log){
                    spi.add(revCommit.getAuthorIdent().getName()) ;
                }
            } catch (GitAPIException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return spi;
    }
}
