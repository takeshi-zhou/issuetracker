package cn.edu.fudan.scanservice.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExcuteShellUtil {

	public static boolean executeCheckout(String repoPath,String commit_id){
		try{
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("/home/fdse/issueTracker/checkout.sh "+repoPath+" "+commit_id);
			int exitValue = process.waitFor();
			if (exitValue == 0)
				return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static boolean excuteAnalyse(String repoPath, String projectName) {
		try {
			Runtime rt = Runtime.getRuntime();
			//String findbugs = "findbugs -xml -output  /home/fdse/issueTracker/resultfile/" + projectName
			//		+ ".xml " + repoPath;
			//脚本实现 用来解耦 还需要与tool解耦合
			Process process = rt.exec("/home/fdse/issueTracker/excuteTools.sh " + projectName + " " + repoPath);
			//Process process = rt.exec(findbugs, null, null);
			int exitValue = process.waitFor();
			if (exitValue == 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean excuteMvn(String repoPath) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("/home/fdse/issueTracker/excuteMvn.sh " + repoPath);
			int exitValue = process.waitFor();
			if (exitValue == 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//do not include/consider the situation that there are at least files holding the same name in project-home dir
	public static String getFileLocation(String repoPath,String fileName) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("find /home/fdse/issueTracker/repo/"+repoPath + " -name " + fileName);
			process.waitFor();
			BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			StringBuffer sBuffer = new StringBuffer();
//			String line =   bReader.readLine();
//			while ((line = bReader.readLine())!= null) {
//				sBuffer.append(line).append("\n");
//			}
			return bReader.readLine().replace("/home/fdse/issueTracker/repo/", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static boolean removeProject(String userName, String projectName) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("rm -rf  /home/fdse/issueTracker/repo/"+userName+projectName);
			int exitValue = process.waitFor();
			if (exitValue == 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
