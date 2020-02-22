package cn.edu.fudan.measureservice.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class JGitUtil {

    static Repository Repo;

    static void printTime(int commitTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestampString=String.valueOf(commitTime);
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = formatter.format(new Date(timestamp));
        System.out.println("It's commit time: "+date);
    }

    static List<DiffEntry> getChangedFileList(RevCommit revCommit, Repository repo) {
        List<DiffEntry> returnDiffs = null;

        try {
            RevCommit previsouCommit=getPrevHash(revCommit,repo);
            if(previsouCommit==null)
                return null;
            ObjectId head=revCommit.getTree().getId();

            ObjectId oldHead=previsouCommit.getTree().getId();

            System.out.println("Printing diff between the Revisions: " + revCommit.getName() + " and " + previsouCommit.getName());

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
                    for (DiffEntry entry : diffs) {
//                        System.out.println("Entry: " + entry);
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

    public static RevCommit getPrevHash(RevCommit commit, Repository repo)  throws  IOException {
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

    public RevCommit getCurrentRevCommit(String repo_path, String commit_id){
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try{
            Repo=builder.build();

            RevWalk walk = new RevWalk(Repo);
            ObjectId versionId=Repo.resolve(commit_id);
            RevCommit verCommit=walk.parseCommit(versionId);
            return verCommit;

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public boolean isMerge(RevCommit revCommit){
        RevCommit[] parents = revCommit.getParents();
        if (parents.length == 1){
            return false;
        }else{
            return true;
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
//		String versionTag="v2.6.19";//定位到某一次Commi，既可以使用Tag，也可以使用其hash
        String versionTag="7fca49f4";
        String path="E:\\Project\\FDSELab\\IssueTracker-Master";
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(path));
        builder.findGitDir(new File(path));
        try {
            Repo=builder.build();

            RevWalk walk = new RevWalk(Repo);
            ObjectId versionId=Repo.resolve(versionTag);
            RevCommit verCommit=walk.parseCommit(versionId);
            RevCommit[] parents = verCommit.getParents();
            System.out.println("父节点的数量为：" + parents.length);

            String commitName=verCommit.getName();
            System.out.println("This version is: "+versionTag+", It hash: "+commitName);//如果通过Tag定位，可以获得其SHA-1 Hash Value
            int verCommitTime=verCommit.getCommitTime();
            printTime(verCommitTime);//打印出本次Commit的时间

            System.out.println("The author is: "+verCommit.getAuthorIdent().getEmailAddress());//获得本次Commit Author的邮箱

            List<DiffEntry> diffFix=getChangedFileList(verCommit,Repo);//获取变更的文件列表

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DiffFormatter df = new DiffFormatter(out);
//			df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);//如果加上这句，就是在比较的时候不计算空格，WS的意思是White Space
            df.setRepository(Repo);

            for (DiffEntry entry : diffFix) {

                df.format(entry);
                String diffText = out.toString("UTF-8");
//				System.out.println(diffText);

                System.out.println(entry.getNewPath());//变更文件的路径

                FileHeader fileHeader = df.toFileHeader(entry);
                List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();
                int addSize = 0;
                int subSize = 0;
                for(HunkHeader hunkHeader:hunks){
                    EditList editList = hunkHeader.toEditList();
                    for(Edit edit : editList){
                        subSize += edit.getEndA()-edit.getBeginA();
                        addSize += edit.getEndB()-edit.getBeginB();
                    }
                }
                System.out.println("addSize="+addSize);//增加和减少的代码行数统计，我和Git Log核对了一下，还挺准确的。
                System.out.println("subSize="+subSize);
                out.reset();
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}