package com.rd.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.math.BigInteger;


/**
 * The persistent class for the audit_log database table.
 * 
 */
@Entity
@Table(name="audit_log")
public class AuditLog implements Serializable {
	private static final long serialVersionUID = 1L;
	

	public AuditLog(String action, Date timestamp, long timetaken, String user) {
		super();
		this.action = action;
		this.timestamp = timestamp;
		this.timetaken = timetaken;
		this.user = user;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private String action;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	private long timetaken;

	private String user;

	public AuditLog() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimetaken() {
		return this.timetaken;
	}

	public void setTimetaken(long timetaken) {
		this.timetaken = timetaken;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}