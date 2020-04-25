package cn.edu.fudan.cloneservice.util;

import cn.edu.fudan.cloneservice.bean.CloneInstanceInfo;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    private Logger logger = LoggerFactory.getLogger(JGitUtil.class);

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private static Repository repository;
    private RevWalk revWalk;
    private Git git;

    /**
     *
     * repoPath 加上了 .git 目录
     *
     */
    public JGitUtil(String repoPath) {
        String gitDir =  IS_WINDOWS ? repoPath + "\\.git" : repoPath + "/.git";
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            repository = builder.setGitDir(new File(gitDir))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
            git = new Git(repository);
            revWalk = new RevWalk(repository);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static Integer getCloneLineByDeveloper(String repoPath, String commitId, List<CloneInstanceInfo> lci, String developerName){
        Integer cloneLine = 0;
        try {

            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(repoPath + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();
            // find the current commit id

            ObjectId curCommitId = repository.resolve(commitId);
            for(CloneInstanceInfo ci: lci){
                BlameCommand blamer = new BlameCommand(repository);
                blamer.setStartCommit(curCommitId);
                blamer.setFilePath(ci.getFile_path());
                try {
                    BlameResult blame = blamer.call();
                    for (int i = ci.getStart_line(); i <= ci.getEnd_line(); i ++){
                        PersonIdent personIdent =  blame.getSourceAuthor(i);
                        if(personIdent.getName().equals(developerName)){
                            cloneLine ++;
                        }
                    }
                } catch (GitAPIException e) {
                    e.printStackTrace();
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return cloneLine;
    }

    public static Set<String> getDeveloperList(String repoPath){
        Set<String> spi = new HashSet<>();
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            Repository repository = repositoryBuilder.setGitDir(new File(repoPath + "/.git"))
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

    public static Map<Integer, String> getNewlyIncreasedLinesNum(List<DiffEntry> diffEntryList) throws IOException{

        Map<Integer, String> map = new HashMap<>();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);

        //如果加上这句，就是在比较的时候不计算空格，WS的意思是White Space
        df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        df.setRepository(repository);

        //以下循环是针对每一个有变动的文件
        for (DiffEntry entry : diffEntryList) {
            df.format(entry);
            String diffText = out.toString("UTF-8");
            int startLine = 0;

            String fileName = entry.toString().split(" ")[1];
            fileName = fileName.substring(0, fileName.length() - 1);
            int addWhiteLines = 0;
            int delWhiteLines = 0;
            int addCommentLines = 0;
            int delCommentLines = 0;


            String[] diffLines = diffText.split("\n");
            for (String line : diffLines) {
                //若是增加的行，则执行以下筛选语句
                if (line.startsWith("+") && !line.startsWith("+++")) {
                    //去掉开头的"+"
                    line = line.substring(1);
                    //去掉头尾的空白符
                    line = line.trim();
                    if (line.matches("^[\\s]*$")) {
                        //匹配空白行
                        addWhiteLines++;
                    } else if (line.startsWith("//") || line.startsWith("/*") || line.startsWith("*") || line.endsWith("*/")) {
                        //匹配注释行
                        addCommentLines++;
                    }else {
//                        newlyIncreasedLineNum = ASTUtil.getIncreasedLineNum(fileName, line, startLine);
//                        if(newlyIncreasedLineNum != 0){
//                            map.put(newlyIncreasedLineNum, fileName);
//                            startLine = newlyIncreasedLineNum;
//                        }
                    }
                }
                //若是删除的行，则执行以下筛选语句
//                if (line.startsWith("-") && !line.startsWith("---")) {
//                    //去掉开头的"-"
//                    line = line.substring(1);
//                    //去掉头尾的空白符
//                    line = line.trim();
//                    if (line.matches("^[\\s]*$")) {//匹配空白行
//                        delWhiteLines++;
//                    } else if (line.startsWith("//") || line.startsWith("/*") || line.startsWith("*") || line.endsWith("*/")) {//匹配注释行
//                        delCommentLines++;
//                    }
//                }
            }
            FileHeader fileHeader = df.toFileHeader(entry);
            List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();

            for(HunkHeader hunkHeader:hunks){
                EditList editList = hunkHeader.toEditList();
                for(Edit edit : editList){
                    for(int i = edit.getBeginB()+1; i <= edit.getEndB(); i++){
                        map.put(i, fileName);
                    }

                }
            }


        }


        return map;
    }

    /**
     * 获取某次commit的修改文件列表(只统计java文件，并且去除test文件)
     * @param revCommit
     * @param repo
     * @return
     */
    public static List<DiffEntry> getChangedFileList(RevCommit revCommit, Repository repo) {
        List<DiffEntry> returnDiffs = null;

        try {
            //默认比较时间轴上最近的那个commit
            RevCommit previousCommit=getPrevHash(revCommit,repo);

            String currentName = revCommit.getAuthorIdent().getName();
            RevCommit[] parents = revCommit.getParents();
            //merge的情况，获取与当前commit开发者相同的那个commit
            if (parents.length == 2){
                if (parents[0].getAuthorIdent().getName().equals(currentName) && !parents[1].getAuthorIdent().getName().equals(currentName)){
                    previousCommit=parents[0];
                }else if (!parents[0].getAuthorIdent().getName().equals(currentName) && parents[1].getAuthorIdent().getName().equals(currentName)){
                    previousCommit=parents[1];
                }
            }
            if(previousCommit==null){
                return null;
            }

            ObjectId head=revCommit.getTree().getId();

            ObjectId oldHead=previousCommit.getTree().getId();

            // prepare the two iterators to compute the diff between
            try (ObjectReader reader = repo.newObjectReader()) {
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldHead);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, head);

                // finally get the list of changed files
                try (Git git = new Git(repo)) {
                    List<DiffEntry> diffs= git.diff()
                            .setNewTree(newTreeIter)
                            .setOldTree(oldTreeIter)
                            .call();
                    for(int i = diffs.size() - 1; i >= 0; i--){
                        String fullName = diffs.get(i).getNewPath().toLowerCase();
                        String shortName = fullName.substring(fullName.lastIndexOf('/') + 1);
                        //只统计java文件
                        if (fullName.endsWith(".java")){
                            //并且去除其中的test文件
                            if (fullName.contains("/test/") ||
                                    shortName.endsWith("test.java") ||
                                    shortName.endsWith("tests.java") ||
                                    shortName.startsWith("test")){
                                //去除其中的test文件
                                diffs.remove(i);
                            }
                        }else {
                            //去除非java文件
                            diffs.remove(i);
                        }
                    }
                    returnDiffs=diffs;
                } catch (GitAPIException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnDiffs;
    }

    /**
     * 获取上一次commit（时间轴上最近的那次commit）
     * @param commit
     * @param repo
     * @return
     * @throws IOException
     */
    private static RevCommit getPrevHash(RevCommit commit, Repository repo)  throws  IOException {
        try (RevWalk walk = new RevWalk(repo)) {
            // Starting point
            walk.markStart(commit);
            int count = 0;
            for (RevCommit rev : walk) {
                // got the previous commit.
                if (count == 1) {
                    return rev;
                }
                count++;
            }
            walk.dispose();
        }
        //Reached end and no previous commits.
        return null;
    }

    private RevCommit getCurrentRevCommit(String repoPath, String commitId) throws IOException{
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repoPath));
        builder.findGitDir(new File(repoPath));
        repository=builder.build();
        RevWalk walk = new RevWalk(repository);
        ObjectId versionId=repository.resolve(commitId);
        RevCommit verCommit=walk.parseCommit(versionId);

        return verCommit;

    }

    private static void test(JGitUtil jGitHeUtil, String repo_path, String commit_id){

        Map<Integer, String> map = new HashMap<>();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try {
            Repository repository = builder.build();
            RevCommit revCommit = jGitHeUtil.getCurrentRevCommit(repo_path,commit_id);
            //获取变更的文件列表
            List<DiffEntry> diffFix = getChangedFileList(revCommit,repository);
            if(diffFix != null){
                map = getNewlyIncreasedLinesNum(diffFix);
            }

            for(int s : map.keySet()){
                System.out.println(s+":"+map.get(s));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        test(new JGitUtil("C:\\Users\\Thinkpad\\Desktop\\config\\IssueTracker-Master"),
                "C:\\Users\\Thinkpad\\Desktop\\config\\IssueTracker-Master",
                "673c8264a7c972c7bba47e14b446baeca4846c6e");


    }
}
