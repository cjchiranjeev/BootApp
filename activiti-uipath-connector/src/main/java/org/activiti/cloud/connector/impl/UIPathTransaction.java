package org.activiti.cloud.connector.impl;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@Component
public class UIPathTransaction {
	
	@Value("${uipath.orchestrator.waitingTime}")
    private String uipathOrchWaitingTime;
	
	Logger logger = Logger.getLogger(UIPathTransaction.class.getName());

	@Autowired
	UIPathConnection uipathConnection;
	private String jobState = null;
	private String outputArguments = null;
	
	public String getJobState() {
		return jobState;
	}

	public void setJobstate(String jobState) {
		this.jobState = jobState;
	}

	public String getOutputArguments() {
		return outputArguments;
	}

	public void setOutputArguments(String outputArguments) {
		this.outputArguments = outputArguments;
	}

	public JsonObject executeRPAProcess(String processName, String inputArguments) throws Exception {
		
		int robotId = 0;
		String releaseKey = null;
		String jobStatus = "Pending";
		JsonObject jobResponse = null;
		String authKey = null;

		authKey = uipathConnection.getAuthenticationToken();
		releaseKey = uipathConnection.getReleaseKey(processName, authKey);
		robotId = uipathConnection.getRobotId(processName);

		if (releaseKey != null && robotId != 0) {

			JsonObject jObj = new JsonObject();
			jObj.addProperty("ReleaseKey", releaseKey);

			// Prepare request body
			JsonArray arr = new JsonArray();
			arr.add(robotId);
			jObj.add("RobotIds", arr);
			jObj.addProperty("NoOfRobots", "0");
			jObj.addProperty("Strategy", "Specific");

			// Set input arguments as per the process
			if (inputArguments != null) {
				jObj.addProperty("InputArguments", inputArguments);
			}

			JsonObject parentObj = new JsonObject();
			parentObj.add("startInfo", jObj);
			int jobId = uipathConnection.triggerRPAProcess(parentObj.toString());

			// check job status
			while (jobStatus.equalsIgnoreCase("Pending") || jobStatus.equalsIgnoreCase("Running")) {
				TimeUnit.SECONDS.sleep(Long.parseLong(uipathOrchWaitingTime));
				jobResponse = uipathConnection.jobStatus(jobId);
				jobStatus = jobResponse.get("State").getAsString();
				logger.info(jobStatus);
			}

			setJobstate(jobStatus);
			//setOutputArguments(jobResponse));
		} 
		return jobResponse;

	}
}
