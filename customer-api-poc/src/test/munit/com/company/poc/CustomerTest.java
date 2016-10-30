package com.company.poc;

import org.junit.Assert;
import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;

public class CustomerTest extends FunctionalMunitSuite {

	  @Test
	  public void test() throws Exception {
	    String myMockPayload = "myPayload"; 

	    MuleMessage messageToBeReturned =
	      muleMessageWithPayload(myMockPayload); 
	    MessageProcessorMocker mocker =
	      whenMessageProcessor("set-payload"); 
	    mocker.thenReturn(messageToBeReturned); 

	    MuleEvent resultMuleEvent =
	      runFlow("myFlow", testEvent("example")); 
	    Assert.assertEquals(myMockPayload,
	      resultMuleEvent.getMessage().getPayload()); 
	  }
	}