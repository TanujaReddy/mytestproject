package org.strut.amway.core.services;

public interface ArticleStatisticsService {
	
	long getAggregatedStatisticsLong(String statisticsName, String contentPath);

	long incrementStatistic(String statisticsName, String contentPath);
	
}
