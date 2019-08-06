package entity;

import java.io.Serializable;

public class Result implements Serializable{
	public Result() {}
	public Result(boolean success,String message) {
		
		this.success = success;
		this.message = message;
	}



	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	private boolean success;
	private String message;
}
