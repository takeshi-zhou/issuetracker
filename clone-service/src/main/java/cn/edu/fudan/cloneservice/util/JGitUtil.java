package cn.edu.fudan.cloneservice.util;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

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
    public static PersonIdent getPersonIdentByRepoIdAndCommitId(String repo_path, String commit_id, int line_id) {
        PersonIdent personIdent = null;
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(repo_path + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();
            // find the current commit id

            ObjectId curCommitId = repository.resolve(commit_id);
            BlameCommand blamer = new BlameCommand(repository);
            blamer.setStartCommit(curCommitId);
            blamer.setFilePath("pom.xml");
            BlameResult blame = blamer.call();
            blame.computeAll();

            //TODO line id check
            //Todo performance up
            personIdent = blame.getSourceAuthor(line_id);
            return personIdent;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return null;

    }

}
