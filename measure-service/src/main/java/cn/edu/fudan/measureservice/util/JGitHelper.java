/**
 * @description:
 * @author: fancying
 * @create: 2019-06-05 17:16
 **/
package cn.edu.fudan.measureservice.util;

import ch.qos.logback.classic.pattern.SyslogStartConverter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static cn.edu.fudan.measureservice.util.DateTimeUtil.timeTotimeStamp;

@SuppressWarnings("Duplicates")
@Slf4j
public class JGitHelper {
    private Logger logger = LoggerFactory.getLogger(JGitHelper.class);

    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    private static final int MERGE_WITH_CONFLICT = -1;
    private static final int MERGE_WITHOUT_CONFLICT = 2;
    private static final int NOT_MERGE = 1;
    private static Repository repository;
    private RevWalk revWalk;
    private Git git;

    /**
     * repoPath 加上了 .git 目录
     */
    public JGitHelper(String repoPath) {
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
            log.error(e.getMessage());
        }
    }

    public void checkout(String commit) {
        try {
            git.reset().setMode(ResetCommand.ResetType.HARD).call();
            CheckoutCommand checkoutCommand = git.checkout();
            checkoutCommand.setName(commit).call();
        } catch (Exception e) {
            logger.error("JGitHelper checkout error:{} ", e.getMessage());
        }
    }

    public String getAuthorName(String commit) {
        String authorName = null;
        try {
            RevCommit revCommit = revWalk.parseCommit(ObjectId.fromString(commit));
            authorName = revCommit.getAuthorIdent().getName();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return authorName;
    }

    public String getCommitTime(String commit) {
        String time = null;
        final String format = "yyyy-MM-dd HH:mm:ss";
        try {
            RevCommit revCommit = revWalk.parseCommit(ObjectId.fromString(commit));
            int t = revCommit.getCommitTime() ;
            long timestamp = Long.parseLong(String.valueOf(t)) * 1000;
            time = new SimpleDateFormat(format).format(new Date(timestamp));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    private Long getLongCommitTime(String version) {
        try {
            RevCommit revCommit = revWalk.parseCommit(ObjectId.fromString(version));
            return revCommit.getCommitTime() * 1000L;
        }catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


    public String getMess(String commit) {
        String message = null;
        try {
            RevCommit revCommit = revWalk.parseCommit(ObjectId.fromString(commit));
            message = revCommit.getFullMessage();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


    public void close() {
        if (repository != null) {
            repository.close();
        }
    }


    public List<String> getCommitListByBranchAndBeginCommit(String branchName, String beginCommit) {
        checkout(branchName);
        Map<String, Long> commitMap = new HashMap<>(512);
        Long start = getLongCommitTime(beginCommit);
        if (start == 0) {
            throw new RuntimeException("beginCommit Error!");
        }
        try {
            Iterable<RevCommit> commits = git.log().call();
            for (RevCommit commit : commits) {
                Long commitTime = commit.getCommitTime() * 1000L;
                if (commitTime >= start) {
                    commitMap.put(commit.getName(), commitTime);
                }
            }
        } catch (GitAPIException e) {
            e.getMessage();
        }
        return new ArrayList<>(sortByValue(commitMap).keySet());
    }


    /**
     *
     *  getCommitTime return second not millisecond
     */
    public List<String> getCommitListByBranchAndDuration(String branchName, String duration) {
        checkout(branchName);
        final int durationLength = 21;
        Map<String, Long> commitMap = new HashMap<>(512);
        if (duration.length() < durationLength) {
            throw new RuntimeException("duration error!");
        }
        long start =  getTime(duration.substring(0,10));
        long end = getTime(duration.substring(11,21));
        try {
            Iterable<RevCommit> commits = git.log().call();
            for (RevCommit commit : commits) {
                long commitTime = commit.getCommitTime() * 1000L;
                if (commitTime <= end && commitTime >= start) {
                    commitMap.put(commit.getName(), commitTime);
                }
            }
        } catch (GitAPIException e) {
            e.getMessage();
        }
        return new ArrayList<>(sortByValue(commitMap).keySet());
    }

    /**
     * 由小到大排序
     * st.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
     * 默认由大到小排序
     * 类型 V 必须实现 Comparable 接口，并且这个接口的类型是 V 或 V 的任一父类。这样声明后，V 的实例之间，V 的实例和它的父类的实例之间，可以相互比较大小。
     */
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();
        st.sorted(Comparator.comparing(Map.Entry::getValue)).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * s : 2018.01.01
     */
    private long getTime(String s) {
        s = s.replace(".","-");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(s);
            return  date.getTime();
        }catch (ParseException e) {
            e.getMessage();
        }
        return 0;
    }

    public String[] getCommitParents(String commit) {
        try {
            RevCommit revCommit = revWalk.parseCommit(ObjectId.fromString(commit));
            RevCommit[] parentCommits = revCommit.getParents();
            String[] result = new String[parentCommits.length];
            for (int i = 0; i < parentCommits.length; i++) {
                result[i] = parentCommits[i].getName();
            }
            return result;
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public Map<String, List<DiffEntry>> getMappedFileList(String commit) {
        Map<String, List<DiffEntry>> result = new HashMap<>(8);
        try {
            RevCommit currCommit = revWalk.parseCommit(ObjectId.fromString(commit));
            RevCommit[] parentCommits = currCommit.getParents();
            for (RevCommit p : parentCommits) {
                RevCommit parentCommit = revWalk.parseCommit(ObjectId.fromString(p.getName()));
                ObjectReader reader = git.getRepository().newObjectReader();
                CanonicalTreeParser currTreeIter = new CanonicalTreeParser();
                currTreeIter.reset(reader, currCommit.getTree().getId());

                CanonicalTreeParser parentTreeIter = new CanonicalTreeParser();
                parentTreeIter.reset(reader, parentCommit.getTree().getId());
                DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
                diffFormatter.setRepository(git.getRepository());
                List<DiffEntry> entries = diffFormatter.scan(currTreeIter, parentTreeIter);
                result.put(parentCommit.getName(), entries);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int mergeJudgment(String commit) {
        Map<String, List<DiffEntry>> diffList = getMappedFileList(commit);
        if (diffList.keySet().size() == NOT_MERGE) {
            return NOT_MERGE;
        }
        Set<String> stringSet = new HashSet<>();
        boolean isFirst = true;
        for (List<DiffEntry> diffEntryList : diffList.values()) {
            for (DiffEntry diffEntry : diffEntryList) {
                if (isFirst) {
                    stringSet.add(diffEntry.getOldPath());
                } else if (stringSet.contains(diffEntry.getOldPath())){
                    return MERGE_WITH_CONFLICT;
                }
            }
            isFirst = false;
        }
        return MERGE_WITHOUT_CONFLICT;
    }

    public List<String> getAggregationCommit(String startTime){
        List<String> aggregationCommits = new ArrayList<>();
        try {
            int startTimeStamp = Integer.valueOf(timeTotimeStamp(startTime));
            int branch = 0;
            Iterable<RevCommit> commits = git.log().call();
            List<RevCommit> commitList = new ArrayList<>();
            Map<String,Integer> sonCommitsMap = new HashMap<>();
            for (RevCommit revCommit: commits) {
                commitList.add(revCommit);
                RevCommit[] parents = revCommit.getParents();
                for (RevCommit parentCommit : parents) {
                    int sonCount = Optional.ofNullable(sonCommitsMap.get(parentCommit.getName())).orElse(0);
                    sonCommitsMap.put(parentCommit.getName(),++sonCount);
                }
            }
            commitList.sort(Comparator.comparingInt(RevCommit::getCommitTime));

            for (RevCommit revCommit : commitList) {
                branch -= revCommit.getParentCount()-1;
                if (startTimeStamp<revCommit.getCommitTime()&&branch==1) {aggregationCommits.add(revCommit.getName());}
                branch += Optional.ofNullable(sonCommitsMap.get(revCommit.getName())).orElse(0)-1;
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return aggregationCommits;
    }

    //打印commit时间
    static void printTime(int commitTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestampString=String.valueOf(commitTime);
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = formatter.format(new Date(timestamp));
        System.out.println("It's commit time: "+date);
    }

    //获取某次commit修改的文件数量
    public static int getChangedFilesCount(List<DiffEntry> diffEntryList) throws IOException {
        int result = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (DiffEntry entry : diffEntryList) {
//            String diffText = out.toString("UTF-8");
//                System.out.println(diffText);
            String fullName = entry.getNewPath().toLowerCase();
            String shortName = fullName.substring(fullName.lastIndexOf('/') + 1);
//            System.out.println(fullName);//变更文件的路径
//            System.out.println(shortName);
            //只统计java文件
            if (fullName.endsWith(".java")){
                //并且去除其中的test文件
                if (fullName.contains("/test/") ||
                        shortName.endsWith("test.java") ||
                        shortName.endsWith("tests.java") ||
                        shortName.startsWith("test")){
                    continue;
                }else {
                    result += 1;
                }
            }else {
                continue;
            }
            out.reset();
        }
        return result;
    }

    //    获取某次commit的修改文件列表(只统计java文件，并且去除test文件)
    public static List<DiffEntry> getChangedFileList(RevCommit revCommit, Repository repo) {
        List<DiffEntry> returnDiffs = null;

        try {
            //默认比较时间轴上最近的那个commit
            RevCommit previsouCommit=getPrevHash(revCommit,repo);

            String currentName = revCommit.getAuthorIdent().getName();
            RevCommit[] parents = revCommit.getParents();
            //merge的情况，获取与当前commit开发者相同的那个commit
            if (parents.length == 2){
                if (parents[0].getAuthorIdent().getName().equals(currentName) && !parents[1].getAuthorIdent().getName().equals(currentName)){
                    previsouCommit=parents[0];
                }else if (!parents[0].getAuthorIdent().getName().equals(currentName) && parents[1].getAuthorIdent().getName().equals(currentName)){
                    previsouCommit=parents[1];
                }
            }
            if(previsouCommit==null)
                return null;
            ObjectId head=revCommit.getTree().getId();

            ObjectId oldHead=previsouCommit.getTree().getId();

//            System.out.println("Printing diff between the Revisions: " + revCommit.getName() + " and " + previsouCommit.getName());

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
                                diffs.remove(i);//去除其中的test文件
                            }else {
//                                System.out.println("Entry: " + diffs.get(i));
                                continue;
                            }
                        }else {
                            diffs.remove(i);//去除非java文件
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

    //获取上一次commit（时间轴上最近的那次commit）
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

    //根据repopath 和commitid获取当前commit
    public RevCommit getCurrentRevCommit(String repo_path, String commit_id){
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setMustExist(true);
        builder.addCeilingDirectory(new File(repo_path));
        builder.findGitDir(new File(repo_path));
        try{
            repository=builder.build();

            RevWalk walk = new RevWalk(repository);
            ObjectId versionId=repository.resolve(commit_id);
            RevCommit verCommit=walk.parseCommit(versionId);
            return verCommit;

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    //判断当前commit是否是最初始的那个commit
    public boolean isInitCommit(RevCommit revCommit){
        RevCommit[] parents = revCommit.getParents();
        if (parents.length == 0){
            return true;
        }else{
            return false;
        }
    }

    //判断该次commit是否是merge
    public boolean isMerge(RevCommit revCommit){
        RevCommit[] parents = revCommit.getParents();
        if (parents.length == 2){
            return true;
        }else{
            return false;
        }
    }

    //判断该次commit的提交信息message
    public String getCommitMessage(RevCommit revCommit){
        return revCommit.getShortMessage();
    }

    //获取本次commit工作量行数数据（包括新增行数、删除行数、新增注释行、删除注释行、新增空白行、删除空白行）
    public static Map<String, Integer> getLinesData(List<DiffEntry> diffEntryList) throws IOException{
        Map<String,Integer> map = new HashMap<>();
        int sumAddLines = 0;
        int sumDelLines = 0;
        int sumAddCommentLines = 0;
        int sumDelCommentLines = 0;
        int sumAddWhiteLines = 0;
        int sumDelWhiteLines = 0;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);

        //如果加上这句，就是在比较的时候不计算空格，WS的意思是White Space
        df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        df.setRepository(repository);

        //以下循环是针对每一个有变动的文件
        for (DiffEntry entry : diffEntryList) {
            df.format(entry);
            String diffText = out.toString("UTF-8");
//            String fullName = entry.getNewPath();
//            System.out.println("正在匹配文件：" + fullName);//变更文件的路径
//            System.out.println(diffText);
            int addWhiteLines = 0;
            int delWhiteLines = 0;
            int addCommentLines = 0;
            int delCommentLines = 0;
            String[] diffLines = diffText.split("\n");
            for (String line : diffLines){
                //若是增加的行，则执行以下筛选语句
                if (line.startsWith("+") && ! line.startsWith("+++")){
                    //去掉开头的"+"
                    line = line.substring(1);
                    //去掉头尾的空白符
                    line = line.trim();
                    if (line.matches("^[\\s]*$")){//匹配空白行
                        addWhiteLines++;
                    }else if(line.startsWith("//") || line.startsWith("/*")  || line.startsWith("*") || line.endsWith("*/")){//匹配注释行
                        addCommentLines++;
                    }
                }
                //若是删除的行，则执行以下筛选语句
                if (line.startsWith("-") && ! line.startsWith("---")){
                    //去掉开头的"-"
                    line = line.substring(1);
                    //去掉头尾的空白符
                    line = line.trim();
                    if (line.matches("^[\\s]*$")){//匹配空白行
                        delWhiteLines++;
                    }else if(line.startsWith("//") || line.startsWith("/*")  || line.startsWith("*") || line.endsWith("*/")){//匹配注释行
                        delCommentLines++;
                    }
                }
            }

            //对单个文件中的注释行数进行累加，计算到总的注释行当中去
            sumAddCommentLines = sumAddCommentLines + addCommentLines;
            sumDelCommentLines = sumDelCommentLines + delCommentLines;
            sumAddWhiteLines += addWhiteLines;
            sumDelWhiteLines += delWhiteLines;

            // 获取文件差异位置，从而统计差异的行数，如增加行数，减少行数
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
            sumAddLines = sumAddLines + addSize - addCommentLines - addWhiteLines;
            sumDelLines = sumDelLines + subSize - delCommentLines - delWhiteLines;
            out.reset();
        }
        map.put("addLines", sumAddLines);
        map.put("delLines", sumDelLines);
        map.put("addCommentLines", sumAddCommentLines);
        map.put("delCommentLines", sumDelCommentLines);
        map.put("addWhiteLines", sumAddWhiteLines);
        map.put("delWhiteLines", sumDelWhiteLines);
        return map;
    }

    //获取本次commit每个文件的工作量行数数据（包括新增行数、删除行数、新增注释行、删除注释行、新增空白行、删除空白行）
    public static List<Map<String,Object>> getFileLinesData(List<DiffEntry> diffEntryList) throws IOException{
        List<Map<String,Object>> result = new ArrayList<>();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(out);

        //如果加上这句，就是在比较的时候不计算空格，WS的意思是White Space
        df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        df.setRepository(repository);

        //以下循环是针对每一个有变动的文件
        for (DiffEntry entry : diffEntryList) {
            Map<String,Object> map = new HashMap<>();
            df.format(entry);
            String diffText = out.toString("UTF-8");
            String fullName = entry.getNewPath();
            map.put("filePath",fullName);
//            System.out.println("正在匹配文件：" + fullName);//变更文件的路径
//            System.out.println(diffText);
            int addWhiteLines = 0;
            int delWhiteLines = 0;
            int addCommentLines = 0;
            int delCommentLines = 0;
            String[] diffLines = diffText.split("\n");
            for (String line : diffLines){
                //若是增加的行，则执行以下筛选语句
                if (line.startsWith("+") && ! line.startsWith("+++")){
                    //去掉开头的"+"
                    line = line.substring(1);
                    //去掉头尾的空白符
                    line = line.trim();
                    if (line.matches("^[\\s]*$")){//匹配空白行
                        addWhiteLines++;
                    }else if(line.startsWith("//") || line.startsWith("/*")  || line.startsWith("*") || line.endsWith("*/")){//匹配注释行
                        addCommentLines++;
                    }
                }
                //若是删除的行，则执行以下筛选语句
                if (line.startsWith("-") && ! line.startsWith("---")){
                    //去掉开头的"-"
                    line = line.substring(1);
                    //去掉头尾的空白符
                    line = line.trim();
                    if (line.matches("^[\\s]*$")){//匹配空白行
                        delWhiteLines++;
                    }else if(line.startsWith("//") || line.startsWith("/*")  || line.startsWith("*") || line.endsWith("*/")){//匹配注释行
                        delCommentLines++;
                    }
                }
            }

            // 获取文件差异位置，从而统计差异的行数，如增加行数，减少行数
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
            int addLines = addSize - addCommentLines - addWhiteLines;
            int delLines = subSize - delCommentLines - delWhiteLines;
            map.put("addLines",addLines);
            map.put("delLines",delLines);
            result.add(map);
            out.reset();
        }

        return result;
    }



    /**
     * 根据diffEntryList获取更改的文件路径List
     * @param diffEntryList
     * @return
     * @throws IOException
     */
    public List<String> getChangedFilePathList(List<DiffEntry> diffEntryList) throws IOException {
        if (diffEntryList == null){
            return null;
        } else {
            List<String> result = new ArrayList<>();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DiffFormatter df = new DiffFormatter(out);
            df.setRepository(repository);
            //以下循环是针对每一个有变动的文件
            for (DiffEntry entry : diffEntryList) {
                df.format(entry);
                String diffText = out.toString("UTF-8");
                String fullName = entry.getNewPath();
//                System.out.println("正在匹配文件：" + fullName);//变更文件的路径
                result.add(fullName);
            }
            return result;
        }
    }

    @SneakyThrows
    public List<DiffEntry> getConflictDiffEntryList (String commit) {
        RevCommit currCommit = revWalk.parseCommit(ObjectId.fromString(commit));
        RevCommit[] parentCommits = currCommit.getParents();
        if (parentCommits.length != 2) {
            return null;
        }

        List<DiffEntry> parent1 = getDiffEntry(parentCommits[0], currCommit);
        List<DiffEntry> parent2 = getDiffEntry(parentCommits[1], currCommit);
        List<DiffEntry> result = new ArrayList<>();
        if (isParent2(parentCommits[0], parentCommits[1], currCommit)) {
            List<DiffEntry> tmp = parent1;
            parent1 = parent2;
            parent2 = tmp;
        }

        // oldPath 相同
        for (DiffEntry diffEntry1 : parent1) {
            for (DiffEntry diffEntry2 :parent2) {
                // todo 暂未考虑重命名的情况 或者无需考虑重命名的情况
                //  如 p1 a=a1  p2 a=>a2 是否冲突待验证
                boolean isSame = diffEntry1.getOldPath().equals(diffEntry2.getOldPath()) &&
                        diffEntry1.getNewPath().equals(diffEntry2.getNewPath());

                if (isSame) {
                    result.add(diffEntry1);
                }
            }
        }
        return result;
    }

    @SneakyThrows
    private List<DiffEntry> getDiffEntry(RevCommit parentCommit, RevCommit currCommit) {
        parentCommit = revWalk.parseCommit(ObjectId.fromString(parentCommit.getName()));
        TreeWalk tw = new TreeWalk(repository);
        tw.addTree(parentCommit.getTree());
        tw.addTree(currCommit.getTree());
        tw.setRecursive(true);
        RenameDetector rd = new RenameDetector(repository);
        rd.addAll(DiffEntry.scan(tw));
        rd.setRenameScore(100);
        return rd.compute();
    }

    private boolean isParent2(RevCommit parent1, RevCommit parent2, RevCommit currCommit) {
        String author1 = parent1.getAuthorIdent().getName();
        String author2 = parent2.getAuthorIdent().getName();
        String author = currCommit.getAuthorIdent().getName();
        if (author.equals(author2) && !author.equals(author1)) {
            return true;
        }

        if (!author.equals(author2) && author.equals(author1)) {
            return false;
        }

        return parent2.getCommitTime() > parent1.getCommitTime();
    }

    @SneakyThrows
    public String getSingleParent(String commit) {
        RevCommit currCommit = revWalk.parseCommit(ObjectId.fromString(commit));
        RevCommit[] parentCommits = currCommit.getParents();
        if (parentCommits.length == 0) {
            return null;
        }
        if (parentCommits.length == 1 || isParent2(parentCommits[1], parentCommits[0], currCommit)) {
            return parentCommits[0].getName();
        }

        return parentCommits[1].getName();
    }


    public static void main(String[] args) throws ParseException {
        String repoPath = "E:\\Lab\\scanProject\\IssueTracker-Master";
        JGitHelper jGitHelper = new JGitHelper(repoPath);
        System.out.println("t");
    }


}