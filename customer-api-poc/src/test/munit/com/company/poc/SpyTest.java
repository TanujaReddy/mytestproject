package com.company.poc;

import org.junit.Assert;
import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.munit.common.mocking.SpyProcess;
import org.mule.munit.runner.functional.FunctionalMunitSuite;

@Test
public void SpyTest() throws Exception {
  SpyProcess beforeSpy = new SpyProcess() { 

    @Override
    public void spy(MuleEvent event) throws MuleException {
      Assert.assertEquals(1, event.getMessage().getPayload());
    }
  };
  SpyProcess afterSpy = new SpyProcess() { 

    @Override
    public void spy(MuleEvent event) throws MuleException {
      Assert.assertEquals(2, event.getMessage().getPayload());
    }
  };
  spyMessageProcessor("set-payload") 
    .ofNamespace("mule")
    .before(beforeSpy)
    .after(afterSpy);

  runFlow("myFlow", testEvent(1)); 

}
}
