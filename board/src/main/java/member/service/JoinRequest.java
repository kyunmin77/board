package member.service;

import java.util.Map;

public class JoinRequest {
	
	private String id;
	private String name;
	private String password;
	private String confirmPassword;
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public boolean isPasswordEqualToConfirm() {
		return password != null && password.equals(confirmPassword);
	}
	
	public void validate(Map<String, Boolean> errors) {
		// 아이디,이름등의 정보가 입력되었는지, password 와 confirmPassword가 일치하지 확인하여
		// 문제가 있을시 errors 맵에 키<String>과 함께 True를 저장하는 매서드
		checkEmpty(errors, id, "id");
		checkEmpty(errors, name, "name");
		checkEmpty(errors, password, "password");
		checkEmpty(errors, confirmPassword, "confirmPassword");
		if(!errors.containsKey("confirmPassword")) {
			if(!isPasswordEqualToConfirm()) {
				errors.put("notMatch", Boolean.TRUE);
			}
		}
	}
	
	private void checkEmpty(Map<String, Boolean> errors, String value, String fieldName) {
		if(value == null || value.isEmpty())
			errors.put(fieldName, Boolean.TRUE);
	}
}
