package org.activiti.cloud.connector.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class UIPathConnection {
	
	@Value("${uipath.orchestrator.url}")
    private String uipathOrchURL;
    
    @Value("${uipath.orchestrator.username}")
    private String uipathUserName;
    
    @Value("${uipath.orchestrator.password}")
    private String uipathOrchPassword;
    
    @Value("${uipath.orchestrator.tenant}")
    private String uipathOrchTenant;
    
    @Value("${uipath.orchestrator.robotsConfig}")
    private String uipathOrchRobotConfig;
    
    //@Value("${uipath.orchestrator.robotEnvironments}")
    //private String uipathOrchRobotEnvironments;
    
    @Value("${uipath.orchestrator.waitingTime}")
    private String uipathOrchWaitingTime;
    
    
	String authToken;
	String releaseKey;
	int robotId;
	JsonParser parser;

	Logger logger = Logger.getLogger(UIPathConnection.class.getName());

	private long tokenTime=0;
	
	public int triggerRPAProcess(String reqBody) throws Exception {

		logger.info(">> Start RPA Job  - "+reqBody);
		String result="";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + authToken);

		HttpEntity<String> entity = new HttpEntity<String>(reqBody, headers);
		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		ResponseEntity<String> response;
		
		response = restTemplate.exchange(uipathOrchURL+"/odata/Jobs/UiPath.Server.Configuration.OData.StartJobs", HttpMethod.POST, entity, String.class);
		
		logger.info("Response Code : " + response.getStatusCode()+ ": triggerRPAProcess Response :  " + result);
		result = response.getBody();
		logger.info("start job " + result);
		httpClient.close();
		
		JsonObject obj = (JsonObject) this.parser.parse(result.toString());
		int JobId = obj.get("value").getAsJsonArray().get(0).getAsJsonObject().get("Id").getAsInt();
		logger.info("JOB ID is " + JobId);

		return JobId;
	}

	public JsonObject jobStatus(int jobId) throws Exception {

		JsonObject jObj = null;

		logger.info(">> Get Job Status");
		
		String result="";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + authToken);

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		ResponseEntity<String> response;
		
		response = restTemplate.exchange(uipathOrchURL+"/odata/Jobs(" + jobId + ")", HttpMethod.GET, entity, String.class);
		
		result = response.getBody();
		logger.info("Response Code : " + response.getStatusCode()+ ": Job Status Response :  " + result);
		httpClient.close();
		
		parser = new JsonParser();
		jObj = (JsonObject) parser.parse(result.toString());
		logger.info("Please wait for a moment ...");
		return jObj;
	}

	public String getAuthenticationToken() throws Exception {
		logger.info(">> Get Authentication Token");
		
		long diffMinutes =0;
		if(tokenTime!=0)
			diffMinutes = (new Date().getTime()-tokenTime) / (60 * 1000);
		if(authToken==null || diffMinutes>5) 
		{
			String result="";
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + authToken);

			JsonObject obj = new JsonObject();
			obj.addProperty("tenancyName", uipathOrchTenant);
			obj.addProperty("usernameOrEmailAddress", uipathUserName);
			obj.addProperty("password", uipathOrchPassword);
			
			HttpEntity<String> entity = new HttpEntity<String>(obj.toString(), headers);
			CloseableHttpClient httpClient = HttpClients.custom()
	                .setSSLHostnameVerifier(new NoopHostnameVerifier())
	                .build();
			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(httpClient);
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			ResponseEntity<String> response;
			
			response = restTemplate.exchange(uipathOrchURL+"/api/account/authenticate", HttpMethod.POST, entity, String.class);
			
			logger.info("Response Code : " + response.getStatusCode()+ ": getAuthentication Response :  " + result);
			result = response.getBody();
			httpClient.close();
	
			parser = new JsonParser();
			JsonObject jObj = (JsonObject) parser.parse(result.toString());
			authToken = jObj.get("result").getAsString();
			tokenTime=new Date().getTime();
		}
		logger.info("Get Authentication Token -- "+authToken);
		return authToken;

	}

	public String getReleaseKey(String processName, String authKey) throws Exception {

		logger.info(">> Get Release Key");
	
		String result="";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + authToken);
		logger.info("authToken : " + authToken);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		ResponseEntity<String> response;
		response = restTemplate.exchange(uipathOrchURL+"/odata/Releases", HttpMethod.GET, entity, String.class);
		
		result = response.getBody();
		logger.info("Response Code : " + response.getStatusCode()+ ": getReleaseKey Response :  " + result);
		httpClient.close();
		
		parser = new JsonParser();
		JsonObject jObj = (JsonObject) parser.parse(result.toString());
		JsonArray values = jObj.getAsJsonArray("value");
		for (int i = 0; i < values.size(); i++) {
			JsonObject value = values.get(i).getAsJsonObject();
			if (value.get("ProcessKey").getAsString().equals(processName))
				releaseKey = value.get("Key").getAsString();
		}
		logger.info("Release Key : " + releaseKey);
		return releaseKey;
	}

	public int getRobotId(String processName) throws Exception {

		logger.info(">> Get Robot ID");
		
		String result="";
		String uipathOrchRobotName="";
		String[] robotsConfig=uipathOrchRobotConfig.split(",");
		for (int i = 0; i < robotsConfig.length; i++) {
			String[] robotConfig=robotsConfig[i].split(":");
			if(processName.equalsIgnoreCase(robotConfig[0]))
			{
				uipathOrchRobotName=robotConfig[1];
				break;
			}
		}
		logger.info(">> Identified Robot Name - "+uipathOrchRobotName);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + authToken);

		HttpEntity<String> entity = new HttpEntity<String>(headers);
		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		ResponseEntity<String> response;
		
		response = restTemplate.exchange(uipathOrchURL+"/odata/Robots", HttpMethod.GET, entity, String.class);
		
		result = response.getBody();
		logger.info("Response Code : " + response.getStatusCode()+ ": getRobotId Response :  " + result);
		httpClient.close();
		
		JsonObject obj = (JsonObject) this.parser.parse(result.toString());
		JsonArray values = obj.getAsJsonArray("value");
		System.out.println(values.size());
		for (int i = 0; i < values.size(); i++) {
			JsonObject value = values.get(i).getAsJsonObject();
			if (value.get("Name").getAsString().equals(uipathOrchRobotName))
				robotId = value.get("Id").getAsInt();
		}
		logger.info("Robot Name : " + uipathOrchRobotName + "\n" + "Robot Id : " + robotId + "\n");

		return robotId;
	}

}
