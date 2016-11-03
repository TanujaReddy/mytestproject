package org.strut.amway.core.events;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.event.jobs.JobProcessor;
import org.apache.sling.event.jobs.JobUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;

public abstract class BaseEventListener implements EventHandler, JobProcessor {

    private static boolean JOB_COMPLETE = true;
    private static int ATTEMPT = 5;

    private Logger logger;

    protected abstract boolean isEligibleEvent(Event event);

    protected abstract void processEvent(Event event) throws Exception;

    @Override
    public void handleEvent(final Event event) {
        if (isEligibleEvent(event)) {
            logger.debug("---> Event has fired on path {}", event.getProperty(SlingConstants.PROPERTY_PATH));
            JobUtil.processJob(event, this);
        }
    }

    @Override
    public boolean process(final Event event) {
        logger.debug("---> Job Processor received event from {}", event.getTopic());

        if (isEligibleEvent(event)) {
            for (int i = 1; i <= ATTEMPT; i++) {
                try {
                    processEvent(event);
                    break;
                } catch (Exception ex) {
                    logger.error("Error while process event. Try to attempt to process event with time {}", i);
                }
            }
        }
        return JOB_COMPLETE;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
