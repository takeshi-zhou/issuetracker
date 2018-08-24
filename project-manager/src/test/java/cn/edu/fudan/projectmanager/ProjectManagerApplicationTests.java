package cn.edu.fudan.projectmanager;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProjectManagerApplicationTests {

	@Test
	public void test(){
		Pattern pattern=Pattern.compile("https://github.com(/[\\w-]{1,}/[\\w-]{1,})");
		Matcher matcher=pattern.matcher("https://github.com/ccran/WebMagicForBlog");
		while(matcher.find()){
			System.out.println(matcher.group(1));
		}

	}

}
