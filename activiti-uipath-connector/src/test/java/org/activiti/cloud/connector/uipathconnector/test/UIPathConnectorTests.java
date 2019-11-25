package org.activiti.cloud.connector.uipathconnector.test;

import java.util.HashMap;

import org.activiti.api.runtime.model.impl.IntegrationContextImpl;
import org.activiti.cloud.api.process.model.IntegrationRequest;
import org.activiti.cloud.api.process.model.impl.IntegrationRequestImpl;
import org.activiti.cloud.connector.impl.UIPathConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UIPathConnectorTests {

	@Autowired
	UIPathConnector con;
	
	@Test
	public void testUIPathConnectorSuccess() {
		try {
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("uipathProcessName", "BPAUITestProcess");
			map.put("uipathProcessInput","{\"in_certificateNo\":\"NHE-OSCAO6-OO13-52\",\"in_vehicleNo\": \"TS08 AG 1458ds\"," + 
					"\"in_LicenseNo\": \"AP5675QY\",\"inWebUrl\":\"http://172.16.0.237:8080/policy/policyform.html\"}");
			map.put("uipathProcessStatus","");
			map.put("uipathProcessException1","");
			map.put("uipathProcessException2","");
			IntegrationContextImpl ic=new IntegrationContextImpl();
			ic.setInBoundVariables(map);
			IntegrationContextImpl ic2=new IntegrationContextImpl();
			ic2.setInBoundVariables(map);
			
			IntegrationRequest ir=new IntegrationRequestImpl(ic);
			con.performTask1(ir);
			IntegrationRequest ir2=new IntegrationRequestImpl(ic2);
			con.performTask2(ir2);
			System.out.println(ir2.getIntegrationContext().getOutBoundVariables().get("uipathProcessException2"));
			System.out.println(ir.getIntegrationContext().getOutBoundVariables().get("uipathProcessException1"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testUIPathConnectorPDFSuccess() {
		try {
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("uipathProcessName", "BPATestProcess");
			map.put("uipathProcessInput","{\"in_certificateNo\":\"NHE-OSCAO6-OO13-52\"}");
			map.put("uipathProcessStatus","");
			IntegrationContextImpl ic=new IntegrationContextImpl();
			ic.setInBoundVariables(map);
			
			IntegrationRequest ir=new IntegrationRequestImpl(ic);
			con.performTask1(ir);
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
