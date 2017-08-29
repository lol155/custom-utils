package cn.emay.fileFlow.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

/**
 * 
 * @author wxb
 * @date 2017年8月4日  下午4:35:38
 */
public class TimerLog {
	private Long startTime ;
	private LinkedHashMap<String,Long> cuts = Maps.newLinkedHashMap();
	private String eventStr = ""; 
	private TimerStatus status = TimerStatus.CREATED;
	
	private enum TimerStatus{
		CREATED("ready"),
		TIMMING("timing"),
		STOPED("stoped");
		
		private String desc ;

		private TimerStatus(String desc) {
			this.desc = desc;
		}
		
		protected String getDesc(){
			return this.desc;
		}
	}
	
	private TimerLog() {
		
	}
	
	public static TimerLog newInstance(){
		return new TimerLog();
	}
	
	public static TimerLog newInstance(String eventStr){
		return new TimerLog().setEventStr(eventStr);
	}
	
	/**
	 * 开始计时
	 * @return
	 */
	public TimerLog start(){
		Assert.isTrue(this.status == TimerStatus.CREATED,
				"timerLog status is not ready,status is " + this.status.getDesc());
		
		this.startTime = System.currentTimeMillis();
		this.status = TimerStatus.TIMMING;
		return this;
	}
	
	/**
	 * 计时
	 * @param cutName
	 * @return
	 */
	public TimerLog cut(String cutName){
		Assert.isTrue(this.status == TimerStatus.TIMMING,
				"timerLog status is not timing,status is " + this.status.getDesc());
		
		this.cuts.put(cutName, System.currentTimeMillis());
		return this;
	}
	
	/**
	 * 计时
	 * @param cutName
	 * @return
	 */
	public TimerLog cut(){
		
		this.cuts.put("c" + (this.cuts.size() + 1), System.currentTimeMillis());
		return this;
	}
	
	/**
	 * 停止
	 * @return
	 */
	public TimerLog stop(){
		Assert.isTrue(this.status == TimerStatus.TIMMING,
				"timerLog status is not timing,status is " + this.status.getDesc());
		
		cuts.put("end", System.currentTimeMillis());
		this.status = TimerStatus.STOPED;
		return this;
	}

	public TimerLog setEventStr(String eventStr){
		this.eventStr = eventStr ;
		return this;
	}
	
	@Override
	public String toString() {
		Assert.notNull(this.startTime,"timerLog is not started");
		
		StringBuilder sb = new StringBuilder();
		if( StringUtils.isNotBlank(this.eventStr) ){
			sb.append(this.eventStr).append(" ");
		}
		
		final Set<Entry<String, Long>> cutsEntrySet = this.cuts.entrySet();
		
		Long lastTime = this.startTime; 
		for (Entry<String, Long> cut : cutsEntrySet) {
			if(cutsEntrySet.size() > 1)
				sb.append(" | ").append(cut.getKey()).append("：").append(cut.getValue().longValue() - lastTime.longValue()).append(" ms");
			
			lastTime = cut.getValue();
		}
		
		if(cutsEntrySet.size() > 0){
			sb.append(" 总耗时：").append(lastTime.longValue() - this.startTime.longValue()).append(" ms");
		}
		
		return sb.toString();
	}
}
