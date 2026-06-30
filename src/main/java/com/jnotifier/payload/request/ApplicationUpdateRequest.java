package com.jnotifier.payload.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ApplicationUpdateRequest {

	@NotBlank
	@NotNull
	private Long applicationId;

	private String tags;

	@Size(max = 700)
	private String shortDescription;

	private String viewPageDescription;

	@Size(max = 100)
	private String applyLink;

	private Boolean status;

	private String advFileName;

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getViewPageDescription() {
		return viewPageDescription;
	}

	public void setViewPageDescription(String viewPageDescription) {
		this.viewPageDescription = viewPageDescription;
	}

	public String getApplyLink() {
		return applyLink;
	}

	public void setApplyLink(String applyLink) {
		this.applyLink = applyLink;
	}

	public String getAdvFileName() {
		return advFileName;
	}

	public void setAdvFileName(String advFileName) {
		this.advFileName = advFileName;
	}
}
