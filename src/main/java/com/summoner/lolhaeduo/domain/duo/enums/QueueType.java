package com.summoner.lolhaeduo.domain.duo.enums;

public enum QueueType {
	// QUICK 은 어떤 값으로 넘겨주는 찾아야함
	QUICK("QUICK"),
	SOLO("RANKED_SOLO_5x5"),
	FLEX("RANKED_FLEX_SR");

	private final String riotQueueType;

	QueueType(String riotQueueType) {
		this.riotQueueType = riotQueueType;
	}

	public String getRiotQueueType() {
		return riotQueueType;
	}

	public static QueueType fromRiotQueueType(String riotQueueType) {
		for (QueueType type : values()) {
			if (type.riotQueueType.equalsIgnoreCase(riotQueueType)) {
				return type;
			}
		}
			throw new IllegalArgumentException("Unknown QueueType: " + riotQueueType);
		}
	}

