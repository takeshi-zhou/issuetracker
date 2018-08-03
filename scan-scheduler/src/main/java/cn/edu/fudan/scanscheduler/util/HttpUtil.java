package cn.edu.fudan.scanscheduler.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class HttpUtil {

	
	public static JSONObject doGet(String url) {
		
		CloseableHttpClient httpClient= HttpClients.createDefault();
		JSONObject jsonResult=null;
		
		HttpGet getRequest=new HttpGet(url);
		//设置请求和传输超时时间
		RequestConfig requestConfig= RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
		getRequest.setConfig(requestConfig);
		
		//发送请求
		try {
			CloseableHttpResponse response=httpClient.execute(getRequest);
			//请求发送成功并得到相应
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				//读取服务器返回的json字符串数据
				HttpEntity entity=response.getEntity();
				String jsonStr=EntityUtils.toString(entity, "utf-8");
				//把json字符串转换成json对象
				jsonResult=JSONObject.parseObject(jsonStr);
			}else {
				System.out.println("get请求提交失败："+url);
			}
		}catch(IOException e) {
			System.out.println("get请求提交失败："+url+" : "+e);
		}finally {
			getRequest.releaseConnection();
		}
		
		return jsonResult;
		
	}
	public static JSONObject doPost(String url,String strParam) {
		CloseableHttpClient httpClient= HttpClients.createDefault();
		JSONObject jsonResult=null;
		HttpPost postRequest=new HttpPost(url);
		//设置请求和传输超时时间
		RequestConfig requestConfig= RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
		postRequest.setConfig(requestConfig);
		try {
			if(strParam!=null) {
				//解决中文乱码问题
				StringEntity entity =new StringEntity(strParam,"utf-8");
				entity.setContentEncoding("UTF-8");
				entity.setContentType("application/x-www-form-urlencoded");
				postRequest.setEntity(entity);
			}
			CloseableHttpResponse response=httpClient.execute(postRequest);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				String str="";
				try {
					str=EntityUtils.toString(response.getEntity(),"utf-8");
					jsonResult=JSONObject.parseObject(str);
				}catch(Exception e) {
					System.out.println("post请求提交失败："+url+" : "+e);
				}
			}
		}catch(IOException e) {
			System.out.println("post请求提交失败："+url+" : "+e);
		}finally {
			postRequest.releaseConnection();
		}
		return jsonResult;
	}
	
	
	public  static JSONArray doPost(String url,JSONObject jsonParam) {
		CloseableHttpClient httpClient= HttpClients.createDefault();
		JSONArray jsonResult=null;
		HttpPost postRequest=new HttpPost(url);
		//设置请求和传输超时时间
		RequestConfig requestConfig= RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		postRequest.setConfig(requestConfig);
		try {
			if(jsonParam!=null) {
				//解决中文乱码问题
				StringEntity entity =new StringEntity(jsonParam.toString(),"utf-8");
				entity.setContentEncoding("UTF-8");
				entity.setContentType("application/json");
				postRequest.setEntity(entity);
			}
			CloseableHttpResponse response=httpClient.execute(postRequest);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				String str="";
				try {
					str=EntityUtils.toString(response.getEntity(),"utf-8");
					jsonResult=JSONObject.parseArray(str);
				}catch(Exception e) {
					System.out.println("post请求提交失败："+url+" : "+e);
				}
			}
		}catch(IOException e) {
			System.out.println("post请求提交失败："+url+" : "+e);
		}finally {
			postRequest.releaseConnection();
		}
		return jsonResult;
	}
	
	
	public static void main(String[]args){
		String url="http://10.141.221.80:8000/get_commit/admin/6285c858-7f83-11e8-8e6b-d067e5ea858d";
		JSONObject res=null;
		res=doGet(url);
		JSONArray jsonArray=res.getJSONArray("data");
		System.out.println(jsonArray);

	}
}
