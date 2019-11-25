package org.activiti.cloud.connector.impl;

import org.activiti.cloud.api.process.model.IntegrationRequest;
import org.activiti.cloud.api.process.model.IntegrationResult;
import org.activiti.cloud.connectors.starter.channels.IntegrationResultSender;
import org.activiti.cloud.connectors.starter.configuration.ConnectorProperties;
import org.activiti.cloud.connectors.starter.model.IntegrationResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;


@Component
@EnableBinding(UIPathConnectorChannels.class)
public class UIPathConnector {

    private final Logger logger = LoggerFactory.getLogger(UIPathConnector.class);

    @Value("${spring.application.name}")
    private String appName;
    
    @Autowired
    UIPathTransaction uipathObj;
    
    @Autowired
    private ConnectorProperties connectorProperties;

    private final IntegrationResultSender integrationResultSender;

    public UIPathConnector(IntegrationResultSender integrationResultSender) {

        this.integrationResultSender = integrationResultSender;
    }

    @StreamListener(value = UIPathConnectorChannels.UIPATH_CONNECTOR_CONSUMER1)
    public void performTask1(IntegrationRequest event) throws InterruptedException {

    	logger.info(">> UIPath Cloud Connector: " + UIPathConnectorChannels.UIPATH_CONNECTOR_CONSUMER1);
    	Map<String, Object> variables=event.getIntegrationContext().getInBoundVariables();
    	//String processName =variables.get("uipathProcessName").toString();
    	String processName ="BPATestProcess";
		String inputArguments = variables.get("uipathProcessInput").toString();
		//String processStatus = variables.get("uipathProcessStatus").toString();
		logger.info(">> ProcessName - "+processName + " : ProcessInstanceId - "+event.getIntegrationContext().getProcessInstanceId());
		logger.info(">> Input Variables - "+inputArguments);
		
		try {
				
			JsonObject response=uipathObj.executeRPAProcess(processName, inputArguments).getAsJsonObject();
			String status=uipathObj.getJobState();
			String  output = response.toString();
			JsonParser jp=new JsonParser();
			JsonElement je=response.get("OutputArguments");
			String outputArguments=je.getAsString();
			
			JsonObject jo=(JsonObject) jp.parse(outputArguments);
			String uipathException=jo.get("uipathProcessException").getAsString();
			Map<String, Object> results = new HashMap<>();
			results.put("uipathProcessOutput1", output);
			//if("Faulted".equalsIgnoreCase(processStatus))
			results.put("uipathProcessStatus1", status);
			results.put("uipathProcessException1", uipathException);
			logger.info(">> uipathProcessException1: "+uipathException);
			logger.info(">> Status - "+status+" : Output Variables - "+output+" Exception - "+uipathException);
			Message<IntegrationResult> message = IntegrationResultBuilder.resultFor(event, connectorProperties)
			            .withOutboundVariables(results)
			            .buildMessage();
			
			integrationResultSender.send(message);
		
		} catch (Exception e) {
			logger.error(">> Exception in UIPath Cloud Connector performTask1 method: " + UIPathConnectorChannels.UIPATH_CONNECTOR_CONSUMER1+": "+ e);
		}
    }

    @StreamListener(value = UIPathConnectorChannels.UIPATH_CONNECTOR_CONSUMER2)
    public void performTask2(IntegrationRequest event2) throws InterruptedException {

    	logger.info(">> UIPath Cloud Connector: " + UIPathConnectorChannels.UIPATH_CONNECTOR_CONSUMER2);
    	Map<String, Object> variables=event2.getIntegrationContext().getInBoundVariables();
    	//String processName =variables.get("uipathProcessName").toString();
    	String processName ="BPAUITestProcess";
		String inputArguments = variables.get("uipathProcessInput").toString();
		//String processStatus = variables.get("uipathProcessStatus").toString();
		logger.info(">> ProcessName - "+processName + " : ProcessInstanceId - "+event2.getIntegrationContext().getProcessInstanceId());
		logger.info(">> Input Variables - "+inputArguments);
		
		try {
				
			JsonObject response=uipathObj.executeRPAProcess(processName, inputArguments).getAsJsonObject();
			String status=uipathObj.getJobState();
			String  output = response.toString();
			JsonParser jp=new JsonParser();
			JsonElement je=response.get("OutputArguments");
			String outputArguments=je.getAsString();
			
			JsonObject jo=(JsonObject) jp.parse(outputArguments);
			String uipathException=jo.get("uipathProcessException").getAsString();
			Map<String, Object> results = new HashMap<>();
			results.put("uipathProcessOutput2", output);
			//if("Faulted".equalsIgnoreCase(processStatus))
			results.put("uipathProcessStatus2", status);
			results.put("uipathProcessException2", uipathException);
			logger.info(">> uipathProcessException2: "+uipathException);
			logger.info(">> Status - "+status+" : Output Variables - "+output+" Exception - "+uipathException);
			Message<IntegrationResult> message = IntegrationResultBuilder.resultFor(event2, connectorProperties)
			            .withOutboundVariables(results)
			            .buildMessage();
			
			integrationResultSender.send(message);
		
		} catch (Exception e) {
			logger.error(">> Exception in UIPath Cloud Connector performTask2 method: " + UIPathConnectorChannels.UIPATH_CONNECTOR_CONSUMER2+": "+ e);
		}
    }
}
