package org.strut.amway.core.services;

public interface IframeStatisticsService {
	
	long getAggregatedStatisticsLong(String statisticsName, String contentPath);

	long incrementStatistic(String statisticsName, String contentPath);
	
}
