package org.strut.amway.core.model;

import java.util.List;

public class ABOProfile {
	
	private String aboId;
	private String aboClass;
	private String currentAwardLevel;
	private String highestAwardLevel;
	private String currentBonusLevel;
	private String highestBonusLevel;
	
	List<Partner> partners;
	public String getAboId() {
		return aboId;
	}
	public void setAboId(String aboId) {
		this.aboId = aboId;
	}
	public String getAboClass() {
		return aboClass;
	}
	public void setAboClass(String aboClass) {
		this.aboClass = aboClass;
	}
	public String getCurrentAwardLevel() {
		return currentAwardLevel;
	}
	public void setCurrentAwardLevel(String currentAwardLevel) {
		this.currentAwardLevel = currentAwardLevel;
	}
	public String getHighestAwardLevel() {
		return highestAwardLevel;
	}
	public void setHighestAwardLevel(String highestAwardLevel) {
		this.highestAwardLevel = highestAwardLevel;
	}
	public String getCurrentBonusLevel() {
		return currentBonusLevel;
	}
	public void setCurrentBonusLevel(String currentBonusLevel) {
		this.currentBonusLevel = currentBonusLevel;
	}
	public String getHighestBonusLevel() {
		return highestBonusLevel;
	}
	public void setHighestBonusLevel(String highestBonusLevel) {
		this.highestBonusLevel = highestBonusLevel;
	}
	
	public List<Partner> getPartners() {
		return partners;
	}
	public void setPartners(List<Partner> partners) {
		this.partners = partners;
	}
	
	
}
